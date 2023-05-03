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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ProfessionalTrainingFormActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;

    List<String> basicTrainingArray =  new ArrayList<>();
    List<BasicTraining> basicTrainings;
    String person_id, person_basic_training_id = "", basic_training_id, dept;
    Spinner spinner_basic_training_id;
    TextInputLayout et_date_completed, et_institution, et_location_name, et_doc_ref_no;
    Button btn_cancel, btn_delete, btn_save;

    String str = "", err_message, photo_file;
    HttpResponse response;
    JSONObject json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_training_form);

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
        String dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");

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

        basicTrainingArray.add("Select Course *");
        basicTrainings = db.getBasicTrainings(dept, ""); //POPULATE VESSEL SPINNER
        for (BasicTraining row : basicTrainings) {
            basicTrainingArray.add(URLDecoder.decode(row.getDesc_basic_training()));
        }

        ArrayAdapter arrayAdapter_bt = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, basicTrainingArray);
        arrayAdapter_bt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_basic_training_id = findViewById(R.id.spinner_basic_training_id);
        spinner_basic_training_id.setAdapter(arrayAdapter_bt);
        spinner_basic_training_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                basic_training_id  = parent.getItemAtPosition(position).toString();
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(15);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("person_basic_training_id")) {
            person_basic_training_id = intent.getStringExtra("person_basic_training_id");
        }

        et_date_completed = findViewById(R.id.et_date_completed);
        et_institution = findViewById(R.id.et_institution);
        //et_location_name = findViewById(R.id.et_location_name);
        //et_doc_ref_no = findViewById(R.id.et_doc_ref_no);
        btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setVisibility(View.GONE);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);

        final MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
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

        if(person_basic_training_id.equals("")){ //ADD
            btn_delete.setVisibility(View.GONE);

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String date_completed = et_date_completed.getEditText().getText().toString();
                    String institution = et_institution.getEditText().getText().toString();
                    //String location_name = et_location_name.getEditText().getText().toString();
                    //String doc_ref_no = et_doc_ref_no.getEditText().getText().toString();
                    String location_name = "";
                    String doc_ref_no = "";
                    if(basic_training_id.equals("Select Course *")){
                        Toast.makeText(ProfessionalTrainingFormActivity.this, "Please select a course.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(date_completed.equals("")){
                        Toast.makeText(ProfessionalTrainingFormActivity.this, "Date is required.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(institution.equals("")){
                        Toast.makeText(ProfessionalTrainingFormActivity.this, "Maritime Training Centre is required.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    pd = new ProgressDialog(ProfessionalTrainingFormActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();

                    String basic_training_id_ = db.getFieldString("basic_training_id", "desc_basic_training = '"+ basic_training_id +"'", "basic_training");
                    Integer id = db.newIntegerId("person_basic_training");
                    person_basic_training_id = db.newId();

                    db.query("INSERT INTO person_basic_training (id, person_basic_training_id, person_id, basic_training_id, location_name, date_completed, doc_ref_no, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, institution) VALUES ("+id+", '"+person_basic_training_id+"', '"+person_id+"', '"+basic_training_id_+"', '"+location_name+"', '"+date_completed+"', '"+doc_ref_no+"', '', '', '','','','', '"+institution+"')");
                    int conn = getConnection.getConnectionType(ProfessionalTrainingFormActivity.this);
                    if(conn != 0){
                        new SyncOnline(context, person_basic_training_id, "ADD").execute();
                    }else{
                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_basic_training', '" + person_basic_training_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");

                        pd.dismiss();
                        Intent intent = new Intent(ProfessionalTrainingFormActivity.this, ProfessionalTrainingActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(ProfessionalTrainingFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }else{
            String saved_basic_training_id = db.getFieldString("basic_training_id", " person_basic_training_id = '"+person_basic_training_id+"'", "person_basic_training");
            String saved_basic_training_id_ = db.getFieldString("desc_basic_training", " basic_training_id = '"+saved_basic_training_id+"'", "basic_training");
            saved_basic_training_id_ = URLDecoder.decode(saved_basic_training_id_);
            String saved_location_name = db.getFieldString("location_name", " person_basic_training_id = '"+person_basic_training_id+"'", "person_basic_training");
            String saved_date_completed = db.getFieldString("date_completed", " person_basic_training_id = '"+person_basic_training_id+"'", "person_basic_training");
            String saved_doc_ref_no = db.getFieldString("doc_ref_no", " person_basic_training_id = '"+person_basic_training_id+"'", "person_basic_training");
            String saved_institution = db.getFieldString("institution", " person_basic_training_id = '"+person_basic_training_id+"'", "person_basic_training");

            spinner_basic_training_id.setSelection(arrayAdapter_bt.getPosition(saved_basic_training_id_));
            et_date_completed.getEditText().setText(saved_date_completed);
            et_institution.getEditText().setText(saved_institution);
            //et_location_name.getEditText().setText(saved_location_name);
            //et_doc_ref_no.getEditText().setText(saved_doc_ref_no);

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String date_completed = et_date_completed.getEditText().getText().toString();
                    String institution = et_institution.getEditText().getText().toString();
                    //String location_name = et_location_name.getEditText().getText().toString();
                    //String doc_ref_no = et_doc_ref_no.getEditText().getText().toString();
                    String location_name = "";
                    String doc_ref_no = "";

                    if(basic_training_id.equals("Select Course *")){
                        Toast.makeText(ProfessionalTrainingFormActivity.this, "Please select a course.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(date_completed.equals("")){
                        Toast.makeText(ProfessionalTrainingFormActivity.this, "Date is required.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(institution.equals("")){
                        Toast.makeText(ProfessionalTrainingFormActivity.this, "Maritime Training Centre is required.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    String basic_training_id_ = db.getFieldString("basic_training_id", "desc_basic_training = '"+ basic_training_id +"'", "basic_training");

                    pd = new ProgressDialog(ProfessionalTrainingFormActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();

                    db.query("UPDATE person_basic_training SET basic_training_id = '"+basic_training_id_+"', location_name = '"+location_name+"', date_completed ='"+date_completed+"', doc_ref_no = '"+doc_ref_no+"', institution = '"+institution+"' WHERE person_basic_training_id = '"+person_basic_training_id+"'");

                    int conn = getConnection.getConnectionType(ProfessionalTrainingFormActivity.this);
                    if(conn != 0){ //WITH CONN
                        new SyncOnline(ProfessionalTrainingFormActivity.this, person_basic_training_id, "UPDATE").execute();
                    }else{
                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_basic_training', '" + person_basic_training_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");

                        pd.dismiss();

                        Intent intent = new Intent(ProfessionalTrainingFormActivity.this, ProfessionalTrainingActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(ProfessionalTrainingFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();
                    }


                }
            });
        }

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfessionalTrainingFormActivity.this);

                alertDialogBuilder.setMessage("Are you sure you want to delete this record?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        pd = new ProgressDialog(ProfessionalTrainingFormActivity.this);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage("Processing. Please wait .... ");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();

                        delete(person_basic_training_id);
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
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(ProfessionalTrainingFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ProfessionalTrainingFormActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(ProfessionalTrainingFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ProfessionalTrainingFormActivity.this, R.color.black));
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfessionalTrainingFormActivity.this);

                alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved.");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(ProfessionalTrainingFormActivity.this, ProfessionalTrainingActivity.class);
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
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(ProfessionalTrainingFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ProfessionalTrainingFormActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(ProfessionalTrainingFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ProfessionalTrainingFormActivity.this, R.color.black));
            }
        });
    }

    public void delete(String person_basic_training_id){
        db.query("DELETE FROM person_basic_training WHERE person_basic_training_id = '"+person_basic_training_id+"'");

        int conn = getConnection.getConnectionType(ProfessionalTrainingFormActivity.this);
        if(conn != 0){
            new SyncOnlineDelete(context, "person_basic_training", person_basic_training_id).execute();
        }else{
            Integer backup_item_id = db.newIntegerId("backup_item");
            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_basic_training', '" + person_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'DELETE', 'N')");

            pd.dismiss();
            Intent intent = new Intent(ProfessionalTrainingFormActivity.this, ProfessionalTrainingActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(ProfessionalTrainingFormActivity.this, "Record successfully deleted.", Toast.LENGTH_LONG).show();
        }

    }

    private class SyncOnline extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public String id;
        public String event;
        public SyncOnline(Context context, String id, String event){

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
            String saved_person_id = db.getFieldString("person_id", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
            String saved_basic_training_id = db.getFieldString("basic_training_id", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
            String saved_location_name = db.getFieldString("location_name", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
            saved_location_name = URLEncoder.encode(saved_location_name);
            String saved_date_completed = db.getFieldString("date_completed", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
            String saved_doc_ref_no = db.getFieldString("doc_ref_no", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
            saved_doc_ref_no = URLEncoder.encode(saved_doc_ref_no);
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
            String saved_app_by_id = db.getFieldString("app_by_id", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
            String saved_date_checked = db.getFieldString("date_checked", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
            String saved_date_app = db.getFieldString("date_app", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
            String saved_checked_remarks = db.getFieldString("checked_remarks", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
            saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
            String saved_app_remarks = db.getFieldString("app_remarks", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
            saved_app_remarks = URLEncoder.encode(saved_app_remarks);
            String saved_institution = db.getFieldString("institution", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
            saved_institution = URLEncoder.encode(saved_institution);

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_basic_training&id="+person_basic_training_id+"&person_id="+saved_person_id+"&basic_training_id="+saved_basic_training_id+"&location_name="+saved_location_name+"&date_completed="+saved_date_completed+"&doc_ref_no="+saved_doc_ref_no+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&institution="+saved_institution + "&event="+event);
            Log.d("CONNECT", url +"sync.php?table=person_basic_training&id="+person_basic_training_id+"&person_id="+saved_person_id+"&basic_training_id="+saved_basic_training_id+"&location_name="+saved_location_name+"&date_completed="+saved_date_completed+"&doc_ref_no="+saved_doc_ref_no+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&institution="+saved_institution + "&event=" + event);

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
            Intent intent = new Intent(ProfessionalTrainingFormActivity.this, ProfessionalTrainingActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(ProfessionalTrainingFormActivity.this, "Record saved successfully.", Toast.LENGTH_LONG).show();

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
            Intent intent = new Intent(ProfessionalTrainingFormActivity.this, ProfessionalTrainingActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(ProfessionalTrainingFormActivity.this, "Record deleted successfully.", Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(ProfessionalTrainingFormActivity.this, ProfessionalTrainingActivity.class);
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
    protected void onPause() {
        super.onPause();
        if(pd != null){
            pd.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pd != null){
            pd.dismiss();
        }

    }
}
