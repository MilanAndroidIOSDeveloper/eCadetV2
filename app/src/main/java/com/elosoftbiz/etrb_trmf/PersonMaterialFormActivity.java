package com.elosoftbiz.etrb_trmf;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

public class PersonMaterialFormActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title;
    ProgressDialog pd;
    DatabaseHelper db;

    String person_id, person_material_id = "", dept;
    TextInputLayout et_date_material, et_material;

    Button btn_cancel, btn_delete, btn_save;
    String str = "", err_message, photo_file;
    HttpResponse response;
    JSONObject json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_material_form);

        /*** LOGO AND HEADER NAME HERE ***/
        mToolbar = findViewById( R.id.nav_action );
        setSupportActionBar( mToolbar );
        mDrawerLayout = findViewById( R.id.drawerLayout );
        mToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.open, R.string.close );
        mDrawerLayout.addDrawerListener( mToggle );
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        /*** END OF LOGO AND HEADER NAME ***/

        db = new DatabaseHelper(this);
        person_id = db.getCadetId();
        dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");

        /****** START MENU *******/
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        //CHANGE MENU HERE
        if(dept.equals("DECK")){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.main_menu_deck);
            new MenuDeck(navigationView, this, mDrawerLayout);
        }else{
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.main_menu_engine);
            new MenuEngine(navigationView, this, mDrawerLayout);
        }
        /****** END MENU *******/

        Intent intent = getIntent();
        if (intent.hasExtra("person_material_id")) {
            person_material_id = intent.getStringExtra("person_material_id");
        }

        et_date_material = findViewById(R.id.et_date_material);
        et_material = findViewById(R.id.et_material);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_delete = findViewById(R.id.btn_delete);
        btn_save = findViewById(R.id.btn_save);

        final MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker mdp = materialDateBuilder.build();
        et_date_material.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        et_date_material.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        mdp.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        String dateSelected = "" + mdp.getHeaderText();
                        if(dateSelected.equals("Selected Date")){
                            et_date_material.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");
                            if(day.length() == 1){
                                day = "0"+ day;
                            }
                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_date_material.getEditText().setText(completed);
                        }
                    }
                });

        if(person_material_id.equals("")){ //ADD
            btn_delete.setVisibility(View.GONE);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String date_material = et_date_material.getEditText().getText().toString();
                    String material = et_material.getEditText().getText().toString();

                    if(material.equals("")){
                        Toast.makeText(PersonMaterialFormActivity.this, "Subject / Title is required..", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Integer id = db.newIntegerId("person_material");
                    String person_material_id = db.newId();

                    db.query("INSERT INTO person_material (id, person_material_id, person_id, material, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, date_material) VALUES ("+id+", '"+person_material_id+"', '"+person_id+"', '"+material+"', '', '', '', '', '', '', '"+date_material+"')");

                    pd = new ProgressDialog(PersonMaterialFormActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();
                    int conn = getConnection.getConnectionType(PersonMaterialFormActivity.this);
                    if(conn != 0){
                        new SyncOnline(context, person_material_id, "ADD").execute();
                    }else{
                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_material', '" + person_material_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                        pd.dismiss();
                        Intent intent = new Intent(PersonMaterialFormActivity.this, PersonMaterialActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(PersonMaterialFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }else{ //UPDATE
            String saved_date_material = db.getFieldString("date_material", "person_material_id = '"+person_material_id+"'", "person_material");
            String saved_material = db.getFieldString("material", "person_material_id = '"+person_material_id+"'", "person_material");

            et_date_material.getEditText().setText(saved_date_material);
            et_material.getEditText().setText(saved_material);

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String date_material = et_date_material.getEditText().getText().toString();
                    String material = et_material.getEditText().getText().toString();

                    if(material.equals("")){
                        Toast.makeText(PersonMaterialFormActivity.this, "Subject / Title is required..", Toast.LENGTH_LONG).show();
                        return;
                    }

                    db.query("UPDATE person_material SET date_material = '"+date_material+"', material = '"+material+"' WHERE person_material_id = '"+person_material_id+"'");

                    pd = new ProgressDialog(PersonMaterialFormActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();
                    int conn = getConnection.getConnectionType(PersonMaterialFormActivity.this);
                    if(conn != 0){
                        new SyncOnline(context, person_material_id, "UPDATE").execute();
                    }else{
                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_material', '" + person_material_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");
                        pd.dismiss();
                        Intent intent = new Intent(PersonMaterialFormActivity.this, PersonMaterialActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(PersonMaterialFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonMaterialFormActivity.this);

                alertDialogBuilder.setMessage("Are you sure you want to delete this record?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        delete(person_material_id);
                    }
                });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(PersonMaterialFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(PersonMaterialFormActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(PersonMaterialFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(PersonMaterialFormActivity.this, R.color.black));

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonMaterialFormActivity.this);

                alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved.");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(PersonMaterialFormActivity.this, PersonMaterialFormActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(PersonMaterialFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(PersonMaterialFormActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(PersonMaterialFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(PersonMaterialFormActivity.this, R.color.black));
            }
        });
    }

    public void delete(String person_material_id){
        pd = new ProgressDialog(PersonMaterialFormActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Processing. Please wait .... ");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        db.query("DELETE FROM person_material WHERE person_material_id = '"+person_material_id+"'");
        int conn = getConnection.getConnectionType(PersonMaterialFormActivity.this);
        if(conn != 0){
            new SyncOnlineDelete(context,  "person_material", person_material_id).execute();
        }else{
            Integer backup_item_id = db.newIntegerId("backup_item");
            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_material', '" + person_material_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'DELETE', 'N')");
            pd.dismiss();
            Intent intent = new Intent(PersonMaterialFormActivity.this, PersonMaterialActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(PersonMaterialFormActivity.this, "Record successfully deleted.", Toast.LENGTH_LONG).show();
        }

    }

    private class SyncOnline extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public String id, event;
        public SyncOnline(Context context, String id, String event)
        {
            this.context = context;
            this.id = id;
            this.event = event;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            String url = getString(R.string.url);
            //GET FROM TBL

            String saved_person_id = db.getFieldString("person_id", "person_material_id = '" + id + "'", "person_material");
            String saved_material = db.getFieldString("material", "person_material_id = '" + id + "'", "person_material");
            saved_material = URLEncoder.encode(saved_material);
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_material_id = '" + id + "'", "person_material");
            String saved_app_by_id = db.getFieldString("app_by_id", "person_material_id = '" + id + "'", "person_material");
            String saved_date_checked = db.getFieldString("date_checked", "person_material_id = '" + id + "'", "person_material");
            String saved_date_app = db.getFieldString("date_app", "person_material_id = '" + id + "'", "person_material");
            String saved_checked_remarks = db.getFieldString("checked_remarks", "person_material_id = '" + id + "'", "person_material");
            saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
            String saved_app_remarks = db.getFieldString("app_remarks", "person_material_id = '" + id + "'", "person_material");
            saved_app_remarks = URLEncoder.encode(saved_app_remarks);
            String saved_date_material = db.getFieldString("date_material", "person_material_id = '" + id + "'", "person_material");

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_material&id="+id+"&person_id="+saved_person_id+"&material="+saved_material+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&date_material="+saved_date_material+"&event="+event);
            Log.d("CONNECT", url +"sync.php?table=person_material&id="+id+"&person_id="+saved_person_id+"&material="+saved_material+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&date_material="+saved_date_material+"&event="+event);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                Log.d("CONNECT", str);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response + str);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response + str);
            }

            return null;
        }
        protected void onPostExecute(Void result){
            pd.dismiss();
            Intent intent = new Intent(PersonMaterialFormActivity.this, PersonMaterialActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(PersonMaterialFormActivity.this, "Record saved successfully.", Toast.LENGTH_LONG).show();

        }
    }

    private class SyncOnlineDelete extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public String table;
        public String tbl_id;
        public SyncOnlineDelete(Context context, String table, String id)
        {
            this.context = context;
            this.table  = table;
            this.tbl_id = id;

        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            String url = getString(R.string.url);
            //GET FROM TBL
            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table="+table+"&id="+tbl_id+"&event=DELETE");
            Log.d("CONNECT", url +"sync.php?table="+table+"&id="+tbl_id+"&event=DELETE");

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                Log.d("CONNECT", str);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response + str);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response + str);
            }

            return null;
        }
        protected void onPostExecute(Void result){
            pd.dismiss();
            Intent intent = new Intent(PersonMaterialFormActivity.this, PersonMaterialActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(PersonMaterialFormActivity.this, "Record deleted successfully.", Toast.LENGTH_LONG).show();
        }
    }

    public String getMonth(String month){
        String month_no = "";
        if(month.equals("Jan")){
            month_no = "01";
        }else if(month.equals("Feb")){
            month_no = "02";
        }else if(month.equals("Mar")){
            month_no = "03";
        }else if(month.equals("Apr")){
            month_no = "04";
        }else if(month.equals("May")){
            month_no = "05";
        }else if(month.equals("Jun")){
            month_no = "06";
        }else if(month.equals("Jul")){
            month_no = "07";
        }else if(month.equals("Aug")){
            month_no = "08";
        }else if(month.equals("Sep")){
            month_no = "09";
        }else if(month.equals("Oct")){
            month_no = "10";
        }else if(month.equals("Nov")){
            month_no = "11";
        }else if(month.equals("Dec")){
            month_no = "12";
        }

        return month_no;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved.");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(PersonMaterialFormActivity.this, PersonMaterialActivity.class);
                startActivity(intent);
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.black));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( pd!=null && pd.isShowing() ){
            pd.cancel();
        }
    }
}
