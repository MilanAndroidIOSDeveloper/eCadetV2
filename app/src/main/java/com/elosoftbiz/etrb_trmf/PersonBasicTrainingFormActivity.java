package com.elosoftbiz.etrb_trmf;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.URLDecoder;
import java.net.URLEncoder;

public class PersonBasicTrainingFormActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;
    TextInputLayout et_date_completed, et_institution;
    TextView tvBasicTraining;
    String person_id = "", person_basic_training_id;
    HttpResponse response;
    int serverResponseCode = 0;
    JSONObject json = null;
    Button btn_save,btn_cancel, btn_delete;
    String str = "", err_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_basic_training_form);
        context = this;

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
        person_id = db.getFieldString("person_id"," vessel_officer ='N'","person");
        String dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");
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

        tvBasicTraining = findViewById(R.id.tvBasicTraining);
        et_date_completed = findViewById(R.id.et_date_completed);
        et_institution = findViewById(R.id.et_institution);
        btn_delete = findViewById(R.id.btn_delete);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker mdp = materialDateBuilder.build();

        et_date_completed.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        et_date_completed.getEditText().setOnClickListener(new View.OnClickListener() {
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
                            et_date_completed.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");
                            if(day.length() == 1){
                                day = "0"+ day;
                            }
                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_date_completed.getEditText().setText(completed);
                        }
                    }
                });

        Intent intent = getIntent();
        if (intent.hasExtra("person_basic_training_id")) {
            person_basic_training_id = intent.getStringExtra("person_basic_training_id");
        }
        String saved_basic_training_id = db.getFieldString("basic_training_id", " person_basic_training_id = '"+person_basic_training_id+"'", "person_basic_training");;
        String desc_basic_training = db.getFieldString("desc_basic_training", " basic_training_id = '"+saved_basic_training_id+"'", "basic_training");
        String saved_date_completed = db.getFieldString("date_completed", " person_basic_training_id = '"+person_basic_training_id+"'", "person_basic_training");
        String saved_institution = db.getFieldString("date_completed", " person_basic_training_id = '"+person_basic_training_id+"'", "person_basic_training");
        et_date_completed.getEditText().setText(saved_date_completed);
        et_institution.getEditText().setText(saved_institution);
        tvBasicTraining.setText(desc_basic_training);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date_completed = et_date_completed.getEditText().getText().toString();
                String institution = et_institution.getEditText().getText().toString();

                if(date_completed.equals("")){
                    Toast.makeText(context, "Date completed is required.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(institution.equals("")){
                    Toast.makeText(context, "Name of MTI is required.", Toast.LENGTH_LONG).show();
                    return;
                }

                pd = new ProgressDialog(context);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("Processing. Please wait .... ");
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.show();
                db.query("UPDATE person_basic_training SET date_completed = '"+date_completed+"', institution = '"+institution+"' WHERE person_basic_training_id = '"+person_basic_training_id+"'");
                Log.d("RESULT", "UPDATE person_basic_training SET date_completed = '"+date_completed+"', institution = '"+institution+"' WHERE person_basic_training_id = '"+person_basic_training_id+"'");
                int conn = getConnection.getConnectionType(context);
                if(conn != 0){
                    new SyncOnline(context, person_basic_training_id, "UPDATE").execute();
                }else{
                    Integer backup_item_id = db.newIntegerId("backup_item");
                    db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_basic_training', '" + person_basic_training_id + "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");
                    pd.dismiss();
                    Intent intent = new Intent(context, ProfessionalTrainingActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(context, "Record successfully added.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setMessage("Are you sure you want to leave without saving?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(context, ProfessionalTrainingActivity.class);
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
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
            }
        });
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

            String person_id = db.getFieldString("person_id", "person_basic_training_id = '" + id + "'", "person_basic_training");
            String basic_training_id = db.getFieldString("basic_training_id", "person_basic_training_id = '" + id + "'", "person_basic_training");
            String location_name = db.getFieldString("location_name", "person_basic_training_id = '" + id + "'", "person_basic_training");
            String date_completed = db.getFieldString("date_completed", "person_basic_training_id = '" + id + "'", "person_basic_training");
            String doc_ref_no = db.getFieldString("doc_ref_no", "person_basic_training_id = '" + id + "'", "person_basic_training");
            String checked_by_id = db.getFieldString("checked_by_id", "person_basic_training_id = '" + id + "'", "person_basic_training");
            String app_by_id = db.getFieldString("app_by_id", "person_basic_training_id = '" + id + "'", "person_basic_training");
            String date_checked = db.getFieldString("date_checked", "person_basic_training_id = '" + id + "'", "person_basic_training");
            String date_app = db.getFieldString("date_app", "person_basic_training_id = '" + id + "'", "person_basic_training");
            String checked_remarks = db.getFieldString("checked_remarks", "person_basic_training_id = '" + id + "'", "person_basic_training");
            String app_remarks = db.getFieldString("app_remarks", "person_basic_training_id = '" + id + "'", "person_basic_training");
            String institution = db.getFieldString("institution", "person_basic_training_id = '" + id + "'", "person_basic_training");
            institution = URLEncoder.encode(institution);

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_basic_training&id="+person_basic_training_id+"&person_id="+person_id+"&basic_training_id="+basic_training_id+"&location_name="+location_name+"&date_completed="+date_completed+"&doc_ref_no="+doc_ref_no+"&checked_by_id="+checked_by_id+"&app_by_id="+app_by_id+"&date_checked="+date_checked+"&date_app="+date_app + "&checked_remarks="+ checked_remarks + "&app_remarks="+ app_remarks + "&institution=" +institution +  "&event="+event);
            Log.d("CONNECT", url +"sync.php?table=person_basic_training&id="+person_basic_training_id+"&person_id="+person_id+"&basic_training_id="+basic_training_id+"&location_name="+location_name+"&date_completed="+date_completed+"&doc_ref_no="+doc_ref_no+"&checked_by_id="+checked_by_id+"&app_by_id="+app_by_id+"&date_checked="+date_checked+"&date_app="+date_app + "&checked_remarks="+ checked_remarks + "&app_remarks="+ app_remarks + "&institution=" +institution + "&event="+event);

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
            Intent intent = new Intent(context, ProfessionalTrainingActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(context, "Record saved successfully.", Toast.LENGTH_LONG).show();
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

        alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(context, ProfessionalTrainingActivity.class);
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
}