package com.elosoftbiz.etrb_trmf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import org.apache.http.HttpResponse;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DownloadFromServerActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    DatabaseHelper db;
    ProgressDialog pd;
    TextInputLayout et_login_name, et_login_pass;
    Button btn_download;
    TextView tv_download;
    ProgressBar progressBar;
    String url, login_name, login_pass, person_id = "", company_id, login_id;
    String dwnload_file_path, dl_photo_file, url_file;
    Integer cnt = 0;
    int downloadedSize = 0;
    int totalSize = 0;
    Integer tcnt =0;

    JSONObject json = null;
    String str = "", err_message, photo_file;
    HttpResponse response;
    isNetworkAvailable isNet;
    Boolean wNet, success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_from_server);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        context = this;
        url = getString(R.string.url);
        dwnload_file_path = getString(R.string.url) + "task_files/";
        dl_photo_file = getString(R.string.url) + "photos/";
        url_file = getString(R.string.url)+"files/";

        /*** LOGO AND HEADER NAME HERE ***/
        mToolbar = findViewById( R.id.nav_action );
        setSupportActionBar( mToolbar );
        mDrawerLayout = findViewById( R.id.drawerLayout );
        mToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.open, R.string.close );
        mDrawerLayout.addDrawerListener( mToggle );
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        /*** END OF LOGO AND HEADER NAME ***/

        /****** START MENU *******/
        navigationView = findViewById(R.id.navigation_view);
        new MenuDeck(navigationView, this, mDrawerLayout);
        /****** END MENU *******/

        db = new DatabaseHelper(this);
        et_login_name = findViewById(R.id.et_login_name);
        et_login_pass = findViewById(R.id.et_login_pass);
        et_login_pass.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btn_download = findViewById(R.id.btn_download);
        progressBar = findViewById(R.id.progressBar);
        tv_download = findViewById(R.id.tv_download);

        progressBar.setVisibility(View.GONE);
        tv_download.setVisibility(View.GONE);

        Log.d("CONNECT", "" + getConnectionType(context));

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNet = new isNetworkAvailable(context);
                wNet = isNet.networkStatus();
                if(!wNet){
                    Toast.makeText(DownloadFromServerActivity.this, "NO INTERNET ACCESS! MAKE SURE YOU ARE CONNECTED TO A STABLE CONNECTION.", Toast.LENGTH_SHORT).show();
                }else{
                    String login_name_ = et_login_name.getEditText().getText().toString();
                    String login_pass_ = et_login_pass.getEditText().getText().toString();

                    if(login_name_.equals("")){
                        Toast.makeText(DownloadFromServerActivity.this, "Please provide your username.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(login_pass_.equals("")){
                        Toast.makeText(DownloadFromServerActivity.this, "Please provide your password.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    login_name = login_name_;
                    login_pass = login_pass_;

                    progressBar.setVisibility(View.VISIBLE);
                    tv_download.setVisibility(View.VISIBLE);
                    tv_download.setText("Downloading data, please wait....");
                    tv_download.setTextColor(Color.BLACK);
                    new Download(context).execute();
                    //new Setup(context).execute();

                    //tv_download.setText("DONE");
                    //progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private class Download extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public Download(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            db.query("DELETE FROM backup_history");
            db.query("DELETE FROM backup_item");

            db.query("DELETE FROM country");
            db.query("DELETE FROM global_sys");
            db.query("DELETE FROM login");
            db.query("DELETE FROM person");
            db.query("DELETE FROM person_basic_training");
            db.query("DELETE FROM person_bridge_watch");
            db.query("DELETE FROM person_ce");
            db.query("DELETE FROM person_education_d");
            db.query("DELETE FROM person_education_h");
            db.query("DELETE FROM person_inspect");
            db.query("DELETE FROM person_ce");
            db.query("DELETE FROM person_to");
            db.query("DELETE FROM person_material");
            db.query("DELETE FROM person_muster");
            db.query("DELETE FROM person_officer");
            db.query("DELETE FROM person_port_watch");
            db.query("DELETE FROM person_program");
            db.query("DELETE FROM person_regulation");
            db.query("DELETE FROM person_safety");
            db.query("DELETE FROM person_special_course");
            db.query("DELETE FROM person_steer_task");
            db.query("DELETE FROM person_steer_topic");
            db.query("DELETE FROM person_steering");
            db.query("DELETE FROM person_task");
            db.query("DELETE FROM person_task_file");
            db.query("DELETE FROM person_trb_topic");
            db.query("DELETE FROM person_work_practice");
            db.query("DELETE FROM person_vessel");
            db.query("DELETE FROM program");
            db.query("DELETE FROM safety");
            db.query("DELETE FROM shipboard");
            db.query("DELETE FROM vessel");
            db.query("DELETE FROM vessel_type");
            db.query("DELETE FROM person_journal");
            db.query("DELETE FROM person_journal_engine");
            db.query("DELETE FROM client");
            db.query("DELETE FROM person_crew_list");
            db.query("DELETE FROM person_emergency_alarm");
            db.query("DELETE FROM person_emergency_drill");
            db.query("DELETE FROM person_emergency_muster");
            db.query("DELETE FROM person_project_work");
            db.query("DELETE FROM person_project_work_file");

            //GET DOMAIN
            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person");
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person");

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "";
                Log.d("CONNECT", "" + response +"=" + str);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }


            if(!str.equals("") && !str.equals("No registered cadet found.")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        //id = json.getString("id");
                        String id = json.getString("id");

                        String code_person = json.getString("code_person");
                        String lname = json.getString("lname");
                        String fname = json.getString("fname");
                        String mname = json.getString("mname");
                        if(mname.equals("null")){
                            mname = "";
                        }
                        String st_address = json.getString("st_address");
                        String city_id = json.getString("city_id");
                        String province_id = json.getString("province_id");
                        final String phone = json.getString("phone");
                        String mobile = json.getString("mobile");
                        String st_address_province = json.getString("st_address_province");
                        String phone_province = json.getString("phone_province");
                        String email = json.getString("email");
                        String gender = json.getString("gender");
                        String civ_status = json.getString("civ_status");
                        String birth_date = json.getString("birth_date");
                        if(birth_date.equals("null")){
                            birth_date = "";
                        }
                        String birth_place = json.getString("birth_place");
                        String spouse_name = json.getString("spouse_name");
                        Integer children = json.getInt("children");
                        String father_name = json.getString("father_name");
                        String mother_name = json.getString("mother_name");
                        String notes = json.getString("notes");
                        String active = json.getString("active");
                        String date_reg = json.getString("date_reg");
                        String compman_id = json.getString("compman_id");
                        company_id = json.getString("company_id");
                        Double amt_paid = json.getDouble("amt_paid");
                        String rank_id = json.getString("rank_id");
                        String vessel_officer = "N";
                        String dept = json.getString("dept");
                        String vessel_id = json.getString("vessel_id");
                        String course_id = json.getString("course_id");
                        String school_id = json.getString("school_id");
                        String school_admin = json.getString("school_admin");
                        String type = json.getString("type");
                        Integer batch_no = json.getInt("batch_no");
                        Double pct_done = json.getDouble("pct_done");
                        Log.d("PCT DONE ", "" + pct_done);
                        if(pct_done.equals("")){
                            pct_done = 0.00;
                        }
                        if(pct_done == null){
                            pct_done = 0.00;
                        }
                        Integer days_on_board = json.getInt("days_on_board");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String last_login = sdf.format(new Date());
                        String full_name = lname + ", " + fname + " " + mname;

                        person_id = json.getString("id");

                        Log.d("RESULT", "ID : "+ id + " " + lname);
                        photo_file = json.getString("photo_file");
                        String passport_no = json.getString("passport_no");
                        String cdc_no = json.getString("cdc_no");
                        String indos_no = json.getString("indos_no");
                        String height = json.getString("height");
                        String weight = json.getString("weight");
                        String enrtry_date = json.getString("entry_date");
                        if(enrtry_date.equals("null")){
                            enrtry_date = "";
                        }
                        String marks = json.getString("marks");
                        String blood_group = json.getString("blood_group");
                        String emergency_contact_name = json.getString("emergency_contact_name");
                        String emergency_contact_address = json.getString("emergency_contact_address");
                        String emergency_contact_no = json.getString("emergency_contact_no");
                        String emergency_relationship = json.getString("emergency_relationship");
                        String nationality = json.getString("nationality");
                        String passport_no_issue_date = json.getString("passport_no_issue_date");
                        if(passport_no_issue_date.equals("null")){
                            passport_no_issue_date = "";
                        }
                        String cdc_no_issue_date = json.getString("cdc_no_issue_date");
                        if(cdc_no_issue_date.equals("null")){
                            cdc_no_issue_date = "";
                        }
                        String cdc_no_issue_place = json.getString("cdc_no_issue_place");
                        String emergency_email = json.getString("emergency_email");
                        String sponsor_id = json.getString("sponsor_id");
                        String login_name_ = json.getString("login_name");
                        String login_pass_ = json.getString("login_pass");
                        String login_type_id = json.getString("login_type_id");
                        String dig_signature = json.getString("dig_signature");
                        String srn = json.getString("srn");
                        String srb_no = json.getString("srb_no");
                        String srb_date = json.getString("srb_date");
                        if(srb_date.equals("null")){
                            srb_date = "";
                        }
                        String sid_no = json.getString("sid_no");
                        Integer int_id = db.newIntegerId("person");
                        db.addPerson(int_id, person_id, code_person, lname, fname, mname, st_address, city_id, province_id, phone, mobile, st_address_province, phone_province, email, gender, civ_status, birth_date, birth_place, spouse_name, children, father_name, mother_name, notes, active, date_reg, compman_id, company_id, amt_paid, rank_id, vessel_officer, dept, vessel_id, course_id, school_id, school_admin, type, batch_no, pct_done, days_on_board, photo_file, passport_no, cdc_no, indos_no, height, weight, enrtry_date, marks, blood_group, emergency_contact_name, emergency_contact_address, emergency_contact_no, emergency_relationship, nationality, passport_no_issue_date, cdc_no_issue_date, cdc_no_issue_place, full_name, "N", "N", emergency_email, sponsor_id, dig_signature, srn, srb_no, srb_date, sid_no, login_name_, login_pass_, login_type_id);
                        Log.d("RESULT", "INSERT INTO person(id, person_id, code_person, lname, fname, mname, st_address, city_id, province_id, phone, mobile, st_address_province, phone_province, email, gender, civ_status, birth_date, birth_place, spouse_name, children, father_name, mother_name, notes, active, date_reg, compman_id, company_id, amt_paid, rank_id, vessel_officer, dept, vessel_id, course_id, school_id, school_admin, type, batch_no, pct_done, days_on_board, logged_in, last_login, full_name, photo_file, w_fr) " +
                                "VALUES ("+int_id+", "+id+", "+ code_person+", "+lname+", "+ fname+", "+mname+", "+ st_address+", "+city_id+", "+province_id+", "+phone+", "+mobile+", "+st_address_province+", "+phone_province+", "+email+", "+gender+", "+civ_status+", "+birth_date+", "+birth_place+", "+spouse_name+", 0, "+father_name+", "+mother_name+", "+notes+", "+active+", "+date_reg+", "+compman_id+", "+company_id+", 0, "+rank_id+", "+vessel_officer+", "+dept+", "+vessel_id+", "+course_id+", "+school_id+", "+school_admin+", "+type+", "+batch_no+", "+pct_done+", "+days_on_board+", '', "+last_login+", "+full_name+", "+photo_file+", 'Y')");
                        //db.query("UPDATE person SET w_fr = 'Y'");
                        Log.d("RESULT : ", "" + photo_file);
                        if(photo_file != ""){
                            new Thread(new Runnable() {
                                public void run() {
                                    downloadFile(photo_file, "person");
                                }
                            }).start();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if(str.equals("No registered cadet found.")){
                success = false;
                err_message = "No registered cadet found.";
            }else{
                success = false;
            }
            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
               new DownloadOfficers(context).execute();
                tv_download.setText("Downloading Officers. Please wait....");
                tv_download.setTextColor(Color.BLACK);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }else{
                if(err_message.equals("No registered cadet found.")){
                    tv_download.setText("No registered cadet found. Ensure that your username or password is correct.");
                    tv_download.setTextColor(Color.RED);
                    progressBar.setVisibility(View.GONE);
                }else{
                    new Download(context).execute();
                }

            }
        }
    }

    //******** DOWNLOAD OFFICERS ********
    private class DownloadOfficers extends AsyncTask<Void, Void, Void>{
        public Context context;
        public DownloadOfficers(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            String school_id_ = db.getFieldString("school_id", " person_id = '"+person_id+"'", "person");
            school_id_ = URLEncoder.encode(school_id_);
            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=officer&company_id=" + URLEncoder.encode(company_id) + "&school_id="+school_id_);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=officer&company_id=" + URLEncoder.encode(company_id) + "&school_id="+school_id_);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String id = json.getString("id");
                        String code_person = json.getString("code_person");
                        String lname = json.getString("lname");
                        String fname = json.getString("fname");
                        String mname = json.getString("mname");
                        if(mname.equals("null")){
                            mname = "";
                        }
                        String st_address = json.getString("st_address");
                        String city_id = json.getString("city_id");
                        String province_id = json.getString("province_id");
                        final String phone = json.getString("phone");
                        String mobile = json.getString("mobile");
                        String st_address_province = json.getString("st_address_province");
                        String phone_province = json.getString("phone_province");
                        String email = json.getString("email");
                        String gender = json.getString("gender");
                        String civ_status = json.getString("civ_status");
                        String birth_date = json.getString("birth_date");
                        if(birth_date.equals("null")){
                            birth_date = "";
                        }
                        String birth_place = json.getString("birth_place");
                        String spouse_name = json.getString("spouse_name");
                        Integer children = json.getInt("children");
                        String father_name = json.getString("father_name");
                        String mother_name = json.getString("mother_name");
                        String notes = json.getString("notes");
                        String active = json.getString("active");
                        String date_reg = json.getString("date_reg");
                        String compman_id = json.getString("compman_id");
                        String company_id = json.getString("company_id");
                        Double amt_paid = json.getDouble("amt_paid");
                        String rank_id = json.getString("rank_id");
                        String vessel_officer = "Y";
                        String dept = json.getString("dept");
                        String vessel_id = json.getString("vessel_id");
                        String course_id = json.getString("course_id");
                        String school_id = json.getString("school_id");
                        String school_admin = json.getString("school_admin");
                        String type = json.getString("type");
                        Integer batch_no = json.getInt("batch_no");
                        Double pct_done = json.getDouble("pct_done");
                        Log.d("PCT DONE ", "" + pct_done);
                        if(pct_done.equals("")){
                            pct_done = 0.00;
                        }
                        if(pct_done == null){
                            pct_done = 0.00;
                        }
                        Integer days_on_board = json.getInt("days_on_board");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String last_login = sdf.format(new Date());
                        String full_name = lname + ", " + fname + " " + mname;

                        Log.d("RESULT", "ID : "+ id + " " + lname);
                        photo_file = json.getString("photo_file");
                        String passport_no = json.getString("passport_no");
                        String cdc_no = json.getString("cdc_no");
                        String indos_no = json.getString("indos_no");
                        String height = json.getString("height");
                        String weight = json.getString("weight");
                        String enrtry_date = json.getString("entry_date");
                        if(enrtry_date.equals("null")){
                            enrtry_date = "";
                        }
                        String marks = json.getString("marks");
                        String blood_group = json.getString("blood_group");
                        String emergency_contact_name = json.getString("emergency_contact_name");
                        String emergency_contact_address = json.getString("emergency_contact_address");
                        String emergency_contact_no = json.getString("emergency_contact_no");
                        String emergency_relationship = json.getString("emergency_relationship");
                        String nationality = json.getString("nationality");
                        String passport_no_issue_date = json.getString("passport_no_issue_date");
                        if(passport_no_issue_date.equals("null")){
                            passport_no_issue_date = "";
                        }
                        String cdc_no_issue_date = json.getString("cdc_no_issue_date");
                        if(cdc_no_issue_date.equals("null")){
                            cdc_no_issue_date = "";
                        }
                        String cdc_no_issue_place = json.getString("cdc_no_issue_place");
                        String login_type_id = json.getString("login_type_id");

                        Integer int_id = db.newIntegerId("person");
                        db.addPerson(int_id, id, code_person, lname, fname, mname, st_address, city_id, province_id, phone, mobile, st_address_province, phone_province, email, gender, civ_status, birth_date, birth_place, spouse_name, children, father_name, mother_name, notes, active, date_reg, compman_id, company_id, amt_paid, rank_id, vessel_officer, dept, vessel_id, course_id, school_id, school_admin, type, batch_no, pct_done, days_on_board, photo_file, passport_no, cdc_no, indos_no, height, weight, enrtry_date, marks, blood_group, emergency_contact_name, emergency_contact_address, emergency_contact_no, emergency_relationship, nationality, passport_no_issue_date, cdc_no_issue_date, cdc_no_issue_place, full_name, "N", "N", "", "", "", "", "","","", "", "", login_type_id);

                        Log.d("RESULT OFFICER", "INSERT INTO person(id, person_id, code_person, lname, fname, mname, st_address, city_id, province_id, phone, mobile, st_address_province, phone_province, email, gender, civ_status, birth_date, birth_place, spouse_name, children, father_name, mother_name, notes, active, date_reg, compman_id, company_id, amt_paid, rank_id, vessel_officer, dept, vessel_id, course_id, school_id, school_admin, type, batch_no, pct_done, days_on_board, logged_in, last_login, full_name, photo_file, w_fr) " +
                                "VALUES ("+int_id+", "+id+", "+ code_person+", "+lname+", "+ fname+", "+mname+", "+ st_address+", "+city_id+", "+province_id+", "+phone+", "+mobile+", "+st_address_province+", "+phone_province+", "+email+", "+gender+", "+civ_status+", "+birth_date+", "+birth_place+", "+spouse_name+", 0, "+father_name+", "+mother_name+", "+notes+", "+active+", "+date_reg+", "+compman_id+", "+company_id+", 0, "+rank_id+", "+vessel_officer+", "+dept+", "+vessel_id+", "+course_id+", "+school_id+", "+school_admin+", "+type+", "+batch_no+", "+pct_done+", "+days_on_board+", '', "+last_login+", "+full_name+", "+photo_file+", 'Y')");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            if(success){
                new DownlaodShipboard(context).execute();
            }else{
                new DownloadOfficers(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownlaodShipboard ********
    private class DownlaodShipboard extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownlaodShipboard(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Sea Service Records. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=shipboard&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=shipboard&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);


                        Integer id = db.newIntegerId("shipboard");
                        String shipboard_id = json.getString("id");
                        String sign_on = json.getString("sign_on");
                        String sign_off = json.getString("sign_off");
                        if(sign_off.equals("null")){
                            sign_off = "";
                        }
                        String engine_watch_mos = json.getString("engine_watch_mos");
                        String engine_watch_yrs = json.getString("engine_watch_yrs");
                        String voyage_mos = json.getString("voyage_mos");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String voyage_days = json.getString("voyage_days");
                        String imo_number = json.getString("imo_number");
                        imo_number = URLDecoder.decode(imo_number);
                        String srb_file = json.getString("srb_file");
                        String cert_file = json.getString("cert_file");
                        String checked_by_id = json.getString("checked_by_id");
                        String date_checked = json.getString("date_checked");
                        String contract_file = json.getString("contract_file");

                        db.execQuery("INSERT INTO shipboard(id, shipboard_id, sign_on, sign_off, engine_watch_mos, engine_watch_yrs, voyage_mos, person_id, vessel_id, voyage_days, imo_number, srb_file, cert_file, checked_by_id, date_checked, contract_file) VALUES ("+id+",'"+shipboard_id+"','"+sign_on+"','"+sign_off+"','"+engine_watch_mos+"','"+engine_watch_yrs+"','"+voyage_mos+"','"+person_id+"','"+vessel_id+"','"+voyage_days+"','"+imo_number+"','"+srb_file+"','"+cert_file+"','"+checked_by_id+"','"+date_checked+"','"+contract_file+"')");
                        Log.d("RESULT","INSERT INTO shipboard(id, shipboard_id, sign_on, sign_off, engine_watch_mos, engine_watch_yrs, voyage_mos, person_id, vessel_id, voyage_days, imo_number, srb_file, cert_file, checked_by_id, date_checked, contract_file) VALUES ("+id+",'"+shipboard_id+"','"+sign_on+"','"+sign_off+"','"+engine_watch_mos+"','"+engine_watch_yrs+"','"+voyage_mos+"','"+person_id+"','"+vessel_id+"','"+voyage_days+"','"+imo_number+"','"+srb_file+"','"+cert_file+"','"+checked_by_id+"','"+date_checked+"','"+contract_file+"')");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonVessel(context).execute();
            }else{
                new DownlaodShipboard(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadPersonVessel ********
    private class DownloadPersonVessel extends AsyncTask<Void, Void, Void> {
        public Context context;
        public DownloadPersonVessel(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Ship's Particulars. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_vessel&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_vessel&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_vessel_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String imo_number = json.getString("imo_number");
                        imo_number = URLDecoder.decode(imo_number);
                        String call_sign = json.getString("call_sign");
                        call_sign = URLDecoder.decode(call_sign);
                        String flag = json.getString("flag");
                        flag = URLDecoder.decode(flag);
                        String length_over_all = json.getString("length_over_all");
                        length_over_all = URLDecoder.decode(length_over_all);
                        String breadth = json.getString("breadth");
                        breadth = URLDecoder.decode(breadth);
                        String depth = json.getString("depth");
                        depth = URLDecoder.decode(depth);
                        String summer_draft = json.getString("summer_draft");
                        summer_draft = URLDecoder.decode(summer_draft);
                        String summer_freeboard = json.getString("summer_freeboard");
                        summer_freeboard = URLDecoder.decode(summer_freeboard);
                        String gross_tonnage = json.getString("gross_tonnage");
                        gross_tonnage = URLDecoder.decode(gross_tonnage);
                        String net_tonnage = json.getString("net_tonnage");
                        net_tonnage = URLDecoder.decode(net_tonnage);
                        String dead_weight = json.getString("dead_weight");
                        dead_weight = URLDecoder.decode(dead_weight);
                        String light_displacement = json.getString("light_displacement");
                        light_displacement = URLDecoder.decode(light_displacement);
                        String fresh_water = json.getString("fresh_water");
                        fresh_water = URLDecoder.decode(fresh_water);
                        String immersion_at_load = json.getString("immersion_at_load");
                        immersion_at_load = URLDecoder.decode(immersion_at_load);
                        String trimming_moment_d = json.getString("trimming_moment_d");
                        trimming_moment_d = URLDecoder.decode(trimming_moment_d);
                        String bale_capacity_d = json.getString("bale_capacity_d");
                        bale_capacity_d = URLDecoder.decode(bale_capacity_d);
                        String grain_capacity_d = json.getString("grain_capacity_d");
                        grain_capacity_d = URLDecoder.decode(grain_capacity_d);
                        String liquid_capacity_d = json.getString("liquid_capacity_d");
                        liquid_capacity_d = URLDecoder.decode(liquid_capacity_d);
                        String refrigerated_capacity_d = json.getString("refrigerated_capacity_d");
                        refrigerated_capacity_d = URLDecoder.decode(refrigerated_capacity_d);
                        String container_capacity_d = json.getString("container_capacity_d");
                        container_capacity_d = URLDecoder.decode(container_capacity_d);
                        String fresh_water_capacity_d = json.getString("fresh_water_capacity_d");
                        fresh_water_capacity_d = URLDecoder.decode(fresh_water_capacity_d);
                        String daily_fresh_water_gen_d = json.getString("daily_fresh_water_gen_d");
                        daily_fresh_water_gen_d = URLDecoder.decode(daily_fresh_water_gen_d);
                        String daily_fresh_water_con_d = json.getString("daily_fresh_water_con_d");
                        daily_fresh_water_con_d = URLDecoder.decode(daily_fresh_water_con_d);
                        String main_engine_make_e = json.getString("main_engine_make_e");
                        main_engine_make_e = URLDecoder.decode(main_engine_make_e);
                        String main_engine_type = json.getString("main_engine_type");
                        main_engine_type = URLDecoder.decode(main_engine_type);
                        String main_engine_stroke_e = json.getString("main_engine_stroke_e");
                        main_engine_stroke_e = URLDecoder.decode(main_engine_stroke_e);
                        String main_engine_bore_e = json.getString("main_engine_bore_e");
                        main_engine_bore_e = URLDecoder.decode(main_engine_bore_e);
                        String main_engine_output = json.getString("main_engine_output");
                        main_engine_output = URLDecoder.decode(main_engine_output);
                        String main_engine_reduction_gear_e = json.getString("main_engine_reduction_gear_e");
                        main_engine_reduction_gear_e = URLDecoder.decode(main_engine_reduction_gear_e);
                        String main_engine_turbo_charger_e = json.getString("main_engine_turbo_charger_e");
                        main_engine_turbo_charger_e = URLDecoder.decode(main_engine_turbo_charger_e);
                        String main_engine_service_speed = json.getString("main_engine_service_speed");
                        main_engine_service_speed = URLDecoder.decode(main_engine_service_speed);
                        String main_engine_boiler_d = json.getString("main_engine_boiler_d");
                        main_engine_boiler_d = URLDecoder.decode(main_engine_boiler_d);
                        String main_engine_bunker_capacity_d = json.getString("main_engine_bunker_capacity_d");
                        main_engine_bunker_capacity_d = URLDecoder.decode(main_engine_bunker_capacity_d);
                        String main_engine_daily_consumption_d = json.getString("main_engine_daily_consumption_d");
                        main_engine_daily_consumption_d = URLDecoder.decode(main_engine_daily_consumption_d);
                        String main_engine_steering_gear_d = json.getString("main_engine_steering_gear_d");
                        main_engine_steering_gear_d = URLDecoder.decode(main_engine_steering_gear_d);
                        String auxiliary_make_e = json.getString("auxiliary_make_e");
                        auxiliary_make_e = URLDecoder.decode(auxiliary_make_e);
                        String auxiliary_type_e = json.getString("auxiliary_type_e");
                        auxiliary_type_e = URLDecoder.decode(auxiliary_type_e);
                        String auxiliary_stroke_e = json.getString("auxiliary_stroke_e");
                        auxiliary_stroke_e = URLDecoder.decode(auxiliary_stroke_e);
                        String auxiliary_bore_e = json.getString("auxiliary_bore_e");
                        auxiliary_bore_e = URLDecoder.decode(auxiliary_bore_e);
                        String auxiliary_output_e = json.getString("auxiliary_output_e");
                        auxiliary_output_e = URLDecoder.decode(auxiliary_output_e);
                        String auxiliary_turbo_charger_e = json.getString("auxiliary_turbo_charger_e");
                        auxiliary_turbo_charger_e = URLDecoder.decode(auxiliary_turbo_charger_e);
                        String auxiliary_normal_electrical_e = json.getString("auxiliary_normal_electrical_e");
                        auxiliary_normal_electrical_e = URLDecoder.decode(auxiliary_normal_electrical_e);
                        String auxiliary_boiler_make_e = json.getString("auxiliary_boiler_make_e");
                        auxiliary_boiler_make_e = URLDecoder.decode(auxiliary_boiler_make_e);
                        String auxiliary_boiler_working_pressure_e = json.getString("auxiliary_boiler_working_pressure_e");
                        auxiliary_boiler_working_pressure_e = URLDecoder.decode(auxiliary_boiler_working_pressure_e);
                        String auxiliary_boiler_type_waste_e = json.getString("auxiliary_boiler_type_waste_e");
                        auxiliary_boiler_type_waste_e = URLDecoder.decode(auxiliary_boiler_type_waste_e);
                        String fuel_main_engine_fuel_type_e = json.getString("fuel_main_engine_fuel_type_e");
                        fuel_main_engine_fuel_type_e = URLDecoder.decode(fuel_main_engine_fuel_type_e);
                        String fuel_viscosity_e = json.getString("fuel_viscosity_e");
                        fuel_viscosity_e = URLDecoder.decode(fuel_viscosity_e);
                        String fuel_specific_fuel_con_e = json.getString("fuel_specific_fuel_con_e");
                        fuel_specific_fuel_con_e = URLDecoder.decode(fuel_specific_fuel_con_e);
                        String fuel_boiler_fuel_type_e = json.getString("fuel_boiler_fuel_type_e");
                        fuel_boiler_fuel_type_e = URLDecoder.decode(fuel_boiler_fuel_type_e);
                        String fuel_viscosity_range_e = json.getString("fuel_viscosity_range_e");
                        fuel_viscosity_range_e = URLDecoder.decode(fuel_viscosity_range_e);
                        String fuel_generator_fuel_type_e = json.getString("fuel_generator_fuel_type_e");
                        fuel_generator_fuel_type_e = URLDecoder.decode(fuel_generator_fuel_type_e);
                        String fuel_bunker_capacity_e = json.getString("fuel_bunker_capacity_e");
                        fuel_bunker_capacity_e = URLDecoder.decode(fuel_bunker_capacity_e);
                        String fuel_daily_con_e = json.getString("fuel_daily_con_e");
                        fuel_daily_con_e = URLDecoder.decode(fuel_daily_con_e);
                        String others_heavy_fuel_oil_e = json.getString("others_heavy_fuel_oil_e");
                        others_heavy_fuel_oil_e = URLDecoder.decode(others_heavy_fuel_oil_e);
                        String others_lub_oil_purifier_e = json.getString("others_lub_oil_purifier_e");
                        others_lub_oil_purifier_e = URLDecoder.decode(others_lub_oil_purifier_e);
                        String others_air_compressor_e = json.getString("others_air_compressor_e");
                        others_air_compressor_e = URLDecoder.decode(others_air_compressor_e);
                        String others_oily_water_separator_e = json.getString("others_oily_water_separator_e");
                        others_oily_water_separator_e = URLDecoder.decode(others_oily_water_separator_e);
                        String others_water_capacity_fw_e = json.getString("others_water_capacity_fw_e");
                        others_water_capacity_fw_e = URLDecoder.decode(others_water_capacity_fw_e);
                        String others_water_capacity_dw_e = json.getString("others_water_capacity_dw_e");
                        others_water_capacity_dw_e = URLDecoder.decode(others_water_capacity_dw_e);
                        String others_fw_generator_e = json.getString("others_fw_generator_e");
                        others_fw_generator_e = URLDecoder.decode(others_fw_generator_e);
                        String others_av_cons_e = json.getString("others_av_cons_e");
                        others_av_cons_e = URLDecoder.decode(others_av_cons_e);
                        String others_steering_type_e = json.getString("others_steering_type_e");
                        others_steering_type_e = URLDecoder.decode(others_steering_type_e);
                        String others_er_lifting_gear_e = json.getString("others_er_lifting_gear_e");
                        others_er_lifting_gear_e = URLDecoder.decode(others_er_lifting_gear_e);
                        String others_swl_e = json.getString("others_swl_e");
                        others_swl_e = URLDecoder.decode(others_swl_e);
                        String others_sewage_treatment_e = json.getString("others_sewage_treatment_e");
                        others_sewage_treatment_e = URLDecoder.decode(others_sewage_treatment_e);
                        String mooring_natural_fiber_d = json.getString("mooring_natural_fiber_d");
                        mooring_natural_fiber_d = URLDecoder.decode(mooring_natural_fiber_d);
                        String mooring_synthetic_fiber_d = json.getString("mooring_synthetic_fiber_d");
                        mooring_synthetic_fiber_d = URLDecoder.decode(mooring_synthetic_fiber_d);
                        String mooring_wires_d = json.getString("mooring_wires_d");
                        mooring_wires_d = URLDecoder.decode(mooring_wires_d);
                        String mooring_towing_spring_d = json.getString("mooring_towing_spring_d");
                        mooring_towing_spring_d = URLDecoder.decode(mooring_towing_spring_d);
                        String anchors_port = json.getString("anchors_port");
                        anchors_port = URLDecoder.decode(anchors_port);
                        String anchors_starboard = json.getString("anchors_starboard");
                        anchors_starboard = URLDecoder.decode(anchors_starboard);
                        String anchors_stern_d = json.getString("anchors_stern_d");
                        anchors_stern_d = URLDecoder.decode(anchors_stern_d);
                        String anchors_spare = json.getString("anchors_spare");
                        anchors_spare = URLDecoder.decode(anchors_spare);
                        String anchors_cable = json.getString("anchors_cable");
                        anchors_cable = URLDecoder.decode(anchors_cable);
                        String lifesaving_lifeboat_type_d = json.getString("lifesaving_lifeboat_type_d");
                        lifesaving_lifeboat_type_d = URLDecoder.decode(lifesaving_lifeboat_type_d);
                        String lifesaving_lifeboat_no = json.getString("lifesaving_lifeboat_no");
                        lifesaving_lifeboat_no = URLDecoder.decode(lifesaving_lifeboat_no);
                        String lifesaving_liferaft_no = json.getString("lifesaving_liferaft_no");
                        lifesaving_liferaft_no = URLDecoder.decode(lifesaving_liferaft_no);
                        String lifesaving_lifeboat_dimension_d = json.getString("lifesaving_lifeboat_dimension_d");
                        lifesaving_lifeboat_dimension_d = URLDecoder.decode(lifesaving_lifeboat_dimension_d);
                        String lifesaving_lifeboat_capacity = json.getString("lifesaving_lifeboat_capacity");
                        lifesaving_lifeboat_capacity = URLDecoder.decode(lifesaving_lifeboat_capacity);
                        String lifesaving_liferaft_capacity = json.getString("lifesaving_liferaft_capacity");
                        lifesaving_liferaft_capacity = URLDecoder.decode(lifesaving_liferaft_capacity);
                        String lifesaving_lifeboat_davits = json.getString("lifesaving_lifeboat_davits");
                        lifesaving_lifeboat_davits = URLDecoder.decode(lifesaving_lifeboat_davits);
                        String lifesaving_lifeboat_fall = json.getString("lifesaving_lifeboat_fall");
                        lifesaving_lifeboat_fall = URLDecoder.decode(lifesaving_lifeboat_fall);
                        String lifesaving_lifebuoys_no = json.getString("lifesaving_lifebuoys_no");
                        lifesaving_lifebuoys_no = URLDecoder.decode(lifesaving_lifebuoys_no);
                        String fire_extinguisher_no = json.getString("fire_extinguisher_no");
                        fire_extinguisher_no = URLDecoder.decode(fire_extinguisher_no);
                        String fire_water = json.getString("fire_water");
                        fire_water = URLDecoder.decode(fire_water);
                        String fire_foam = json.getString("fire_foam");
                        fire_foam = URLDecoder.decode(fire_foam);
                        String fire_dry_powder = json.getString("fire_dry_powder");
                        fire_dry_powder = URLDecoder.decode(fire_dry_powder);
                        String fire_co2 = json.getString("fire_co2");
                        fire_co2 = URLDecoder.decode(fire_co2);
                        String fire_firehoses = json.getString("fire_firehoses");
                        fire_firehoses = URLDecoder.decode(fire_firehoses);
                        String fire_breathing_e = json.getString("fire_breathing_e");
                        fire_breathing_e = URLDecoder.decode(fire_breathing_e);
                        String fire_breathing_no_e = json.getString("fire_breathing_no_e");
                        fire_breathing_no_e = URLDecoder.decode(fire_breathing_no_e);
                        String fire_fixed_fire_system_d = json.getString("fire_fixed_fire_system_d");
                        fire_fixed_fire_system_d = URLDecoder.decode(fire_fixed_fire_system_d);
                        String fire_scba_d = json.getString("fire_scba_d");
                        fire_scba_d = URLDecoder.decode(fire_scba_d);
                        String cargo_handling_derricks = json.getString("cargo_handling_derricks");
                        cargo_handling_derricks = URLDecoder.decode(cargo_handling_derricks);
                        String cargo_handling_cranes = json.getString("cargo_handling_cranes");
                        cargo_handling_cranes = URLDecoder.decode(cargo_handling_cranes);
                        String cargo_handling_winches = json.getString("cargo_handling_winches");
                        cargo_handling_winches = URLDecoder.decode(cargo_handling_winches);
                        String cargo_handling_other_d = json.getString("cargo_handling_other_d");
                        cargo_handling_other_d = URLDecoder.decode(cargo_handling_other_d);
                        String cargo_handling_ballast_d = json.getString("cargo_handling_ballast_d");
                        cargo_handling_ballast_d = URLDecoder.decode(cargo_handling_ballast_d);
                        String cargo_handling_tank_d = json.getString("cargo_handling_tank_d");
                        cargo_handling_tank_d = URLDecoder.decode(cargo_handling_tank_d);
                        String cargo_handling_pump_no = json.getString("cargo_handling_pump_no");
                        cargo_handling_pump_no = URLDecoder.decode(cargo_handling_pump_no);
                        String cargo_handling_pipelines = json.getString("cargo_handling_pipelines");
                        cargo_handling_pipelines = URLDecoder.decode(cargo_handling_pipelines);
                        String cargo_handling_type_rating_e = json.getString("cargo_handling_type_rating_e");
                        cargo_handling_type_rating_e = URLDecoder.decode(cargo_handling_type_rating_e);
                        String cargo_handling_ballast_pump_e = json.getString("cargo_handling_ballast_pump_e");
                        cargo_handling_ballast_pump_e = URLDecoder.decode(cargo_handling_ballast_pump_e);
                        String navigational_radar_d = json.getString("navigational_radar_d");
                        navigational_radar_d = URLDecoder.decode(navigational_radar_d);
                        String navigational_log_d = json.getString("navigational_log_d");
                        navigational_log_d = URLDecoder.decode(navigational_log_d);
                        String navigational_gps_d = json.getString("navigational_gps_d");
                        navigational_gps_d = URLDecoder.decode(navigational_gps_d);
                        String navigational_magnetic_d = json.getString("navigational_magnetic_d");
                        navigational_magnetic_d = URLDecoder.decode(navigational_magnetic_d);
                        String navigational_gyro_d = json.getString("navigational_gyro_d");
                        navigational_gyro_d = URLDecoder.decode(navigational_gyro_d);
                        String navigational_echo_d = json.getString("navigational_echo_d");
                        navigational_echo_d = URLDecoder.decode(navigational_echo_d);
                        String navigational_auto_d = json.getString("navigational_auto_d");
                        navigational_auto_d = URLDecoder.decode(navigational_auto_d);
                        String navigational_vhf_d = json.getString("navigational_vhf_d");
                        navigational_vhf_d = URLDecoder.decode(navigational_vhf_d);
                        String navigational_mf_hf_d = json.getString("navigational_mf_hf_d");
                        navigational_mf_hf_d = URLDecoder.decode(navigational_mf_hf_d);
                        String navigational_sat_d = json.getString("navigational_sat_d");
                        navigational_sat_d = URLDecoder.decode(navigational_sat_d);
                        String navigational_ecdis_d = json.getString("navigational_ecdis_d");
                        navigational_ecdis_d = URLDecoder.decode(navigational_ecdis_d);
                        String navigational_sart_d = json.getString("navigational_sart_d");
                        navigational_sart_d = URLDecoder.decode(navigational_sart_d);
                        String navigational_navtex_d = json.getString("navigational_navtex_d");
                        navigational_navtex_d = URLDecoder.decode(navigational_navtex_d);
                        String navigational_ais_d = json.getString("navigational_ais_d");
                        navigational_ais_d = URLDecoder.decode(navigational_ais_d);
                        String navigational_vdr_d = json.getString("navigational_vdr_d");
                        navigational_vdr_d = URLDecoder.decode(navigational_vdr_d);
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);

                        Integer int_id = db.newIntegerId("person_vessel");

                        db.addPersonVessel(person_vessel_id , int_id, person_id, vessel_id, imo_number, call_sign, flag, length_over_all, breadth, depth, summer_draft, summer_freeboard, gross_tonnage, net_tonnage, dead_weight, light_displacement, fresh_water, immersion_at_load, trimming_moment_d, bale_capacity_d, grain_capacity_d, liquid_capacity_d, refrigerated_capacity_d, container_capacity_d, fresh_water_capacity_d, daily_fresh_water_gen_d, daily_fresh_water_con_d, main_engine_make_e, main_engine_type, main_engine_stroke_e, main_engine_bore_e, main_engine_output, main_engine_reduction_gear_e, main_engine_turbo_charger_e, main_engine_service_speed, main_engine_boiler_d, main_engine_bunker_capacity_d, main_engine_daily_consumption_d, main_engine_steering_gear_d, auxiliary_make_e, auxiliary_type_e, auxiliary_stroke_e, auxiliary_bore_e, auxiliary_output_e, auxiliary_turbo_charger_e, auxiliary_normal_electrical_e, auxiliary_boiler_make_e, auxiliary_boiler_working_pressure_e, auxiliary_boiler_type_waste_e, fuel_main_engine_fuel_type_e, fuel_viscosity_e, fuel_specific_fuel_con_e, fuel_boiler_fuel_type_e, fuel_viscosity_range_e, fuel_generator_fuel_type_e, fuel_bunker_capacity_e, fuel_daily_con_e, others_heavy_fuel_oil_e, others_lub_oil_purifier_e, others_air_compressor_e, others_oily_water_separator_e, others_water_capacity_fw_e, others_water_capacity_dw_e, others_fw_generator_e, others_av_cons_e, others_steering_type_e, others_er_lifting_gear_e, others_swl_e, others_sewage_treatment_e, mooring_natural_fiber_d, mooring_synthetic_fiber_d, mooring_wires_d, mooring_towing_spring_d, anchors_port, anchors_starboard, anchors_stern_d, anchors_spare, anchors_cable, lifesaving_lifeboat_type_d, lifesaving_lifeboat_no, lifesaving_liferaft_no, lifesaving_lifeboat_dimension_d, lifesaving_lifeboat_capacity, lifesaving_liferaft_capacity, lifesaving_lifeboat_davits, lifesaving_lifeboat_fall, lifesaving_lifebuoys_no, fire_extinguisher_no, fire_water, fire_foam, fire_dry_powder, fire_co2, fire_firehoses, fire_breathing_e, fire_breathing_no_e, fire_fixed_fire_system_d, fire_scba_d, cargo_handling_derricks, cargo_handling_cranes, cargo_handling_winches, cargo_handling_other_d, cargo_handling_ballast_d, cargo_handling_tank_d, cargo_handling_pump_no, cargo_handling_pipelines, cargo_handling_type_rating_e, cargo_handling_ballast_pump_e, navigational_radar_d, navigational_log_d, navigational_gps_d, navigational_magnetic_d, navigational_gyro_d, navigational_echo_d, navigational_auto_d, navigational_vhf_d, navigational_mf_hf_d, navigational_sat_d, navigational_ecdis_d, navigational_sart_d, navigational_navtex_d, navigational_ais_d, navigational_vdr_d, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result) {
            if(success){
                new DownloadVessel(context).execute();
            }else{
                new DownloadPersonVessel(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadVessel********
    private class DownloadVessel extends AsyncTask<Void, Void, Void> {
        public Context context;
        public DownloadVessel(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Vessels. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=vessel&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=vessel&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String vessel_id = json.getString("id");
                        String name_vessel = json.getString("name_vessel");
                        name_vessel = URLDecoder.decode(name_vessel);
                        String owner_company_id = json.getString("owner_company_id");
                        String operator_company_id = json.getString("operator_company_id");
                        String year_built = json.getString("year_built");
                        String flag_registry_id = json.getString("flag_registry_id");
                        String hp = json.getString("hp");
                        String kw = json.getString("kw");
                        String grt = json.getString("grt");
                        String trade_type = json.getString("trade_type");
                        trade_type = URLDecoder.decode(trade_type);
                        String imo_number = json.getString("imo_number");
                        imo_number = URLDecoder.decode(imo_number);
                        String call_sign = json.getString("call_sign");
                        call_sign = URLDecoder.decode(call_sign);
                        String vessel_type_id = json.getString("vessel_type_id");
                        String dp = json.getString("dp");
                        String ice_class = json.getString("ice_class");
                        String motor = json.getString("motor");
                        String st = json.getString("st");
                        String gt = json.getString("gt");

                        Integer int_id = db.newIntegerId("vessel");
                        Integer cnt = db.GetCount("vessel" , " WHERE vessel_id = '"+vessel_id+"'");
                        if(cnt == 0){
                            db.addVessel(vessel_id , int_id, name_vessel, owner_company_id, operator_company_id, year_built, flag_registry_id, hp, kw, grt, trade_type, imo_number, call_sign, vessel_type_id, dp, ice_class, motor, st, gt);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadVesselType(context).execute();
            }else{
                new DownloadVessel(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadVesselType********
    private class DownloadVesselType extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadVesselType(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Vessel Types. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=vessel_type&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=vessel_type&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String vessel_type_id = json.getString("id");
                        String desc_vessel_type = json.getString("desc_vessel_type");
                        desc_vessel_type = URLDecoder.decode(desc_vessel_type);

                        Integer int_id = db.newIntegerId("vessel_type");

                        db.addVesselType(vessel_type_id , int_id, desc_vessel_type);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }
            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonCrewList(context).execute();
            }else{
                new DownloadVesselType(context).execute();
            }
        }
    }

    //******** DOWNLOAD OFFICERS ********
    private class DownloadPersonCrewList extends AsyncTask<Void, Void, Void>{
        public Context context;
        public DownloadPersonCrewList(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Crew List. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_crew_list&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_crew_list&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT off", err_message +" " + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        Integer id = db.newIntegerId("person_crew_list");
                        String person_crew_list_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String date_uploaded = json.getString("date_uploaded");
                        String filename = json.getString("filename");
                        String checked_by_id = json.getString("checked_by_id");
                        String date_checked = json.getString("date_checked");
                        String checked_remarks = json.getString("checked_remarks");
                        String login_id = json.getString("login_id");
                        String last_update = json.getString("last_update");

                        Integer cnt = db.GetCount("person_crew_list", " WHERE person_crew_list_id = '"+person_crew_list_id+"'");
                        if(cnt == 0){
                            downloadFile(filename,  "person_crew_list");
                            db.execQuery("INSERT INTO person_crew_list(id, person_crew_list_id, person_id, vessel_id, date_uploaded, filename, checked_by_id, date_checked, checked_remarks, login_id, last_update) VALUES ("+id+",'"+person_crew_list_id+"','"+person_id+"','"+vessel_id+"','"+date_uploaded+"','"+filename+"','"+checked_by_id+"','"+date_checked+"','"+checked_remarks+"','"+login_id+"','"+last_update+"')");
                            Log.d("RESULT", "INSERT INTO person_crew_list(id, person_crew_list_id, person_id, vessel_id, date_uploaded, filename, checked_by_id, date_checked, checked_remarks, login_id, last_update) VALUES ("+id+",'"+person_crew_list_id+"','"+person_id+"','"+vessel_id+"','"+date_uploaded+"','"+filename+"','"+checked_by_id+"','"+date_checked+"','"+checked_remarks+"','"+login_id+"','"+last_update+"')");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonInspect(context).execute();
            }else{
                new DownloadPersonCrewList(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadPersonInspect ********
    private class DownloadPersonInspect extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonInspect(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Master's Monthly Inspection. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_inspect&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_inspect&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "PERSON INSPECT " + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_inspect_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String comments = json.getString("comments");
                        comments = URLDecoder.decode(comments);
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);
                        String company_id = json.getString("company_id");
                        String vessel_id = json.getString("vessel_id");

                        Integer int_id = db.newIntegerId("person_inspect");

                        db.addPersonInspect(person_inspect_id , int_id, person_id, comments, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, company_id, vessel_id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(success){
                new DownloadPersonCe(context).execute();
            }else{
                new DownloadPersonInspect(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadPersonBridgeWatch ********
    private class DownloadPersonCe extends AsyncTask<Void, Void, Void>{
        public Context context;
        public DownloadPersonCe(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Chief Engineer's Monthly Inspection. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_ce&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_ce&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_ce_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String comments = json.getString("comments");
                        comments = URLDecoder.decode(comments);
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);

                        Integer int_id = db.newIntegerId("person_ce");

                        db.addPersonCe(person_ce_id , int_id, person_id, vessel_id, comments, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonTo(context).execute();
            }else{
                new DownloadPersonCe(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadPersonTo ********
    private class DownloadPersonTo extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonTo(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading OTS's Monthly Inspection. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_to&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_to&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_to_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String date_signed = json.getString("date_signed");
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);
                        String vessel_id = json.getString("vessel_id");

                        Integer int_id = db.newIntegerId("person_to");

                        db.addPersonTo(person_to_id , int_id, person_id, date_signed, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, vessel_id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }
            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonBasicTraining(context).execute();
            }else{
                new DownloadPersonTo(context).execute();
            }
        }
    }

    //******** DOWNLOAD OFFICERS ********
    private class DownloadPersonBasicTraining extends AsyncTask<Void, Void, Void>{
        public Context context;
        public DownloadPersonBasicTraining(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading List of Training Records. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_basic_training&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_basic_training&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT off", err_message +" " + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_basic_training_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String basic_training_id = json.getString("basic_training_id");
                        String location_name = json.getString("location_name");
                        location_name = URLDecoder.decode(location_name);
                        String date_completed = json.getString("date_completed");
                        if(date_completed.equals("null")){
                            date_completed = "";
                        }
                        String doc_ref_no = json.getString("doc_ref_no");
                        doc_ref_no = URLDecoder.decode(doc_ref_no);
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        if(date_checked.equals("null")){
                            date_checked = "";
                        }
                        String date_app = json.getString("date_app");
                        if(date_app.equals("null")){
                            date_app = "";
                        }
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);
                        String institution = json.getString("institution");
                        institution = URLDecoder.decode(institution);

                        Integer int_id = db.newIntegerId("person_basic_training");
                        db.addPersonBasicTraining(person_basic_training_id , int_id, person_id, basic_training_id, location_name, date_completed, doc_ref_no, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, institution);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonSafety(context).execute();
            }else{
                new DownloadPersonBasicTraining(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadPersonSafety********
    private class DownloadPersonSafety extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonSafety(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Cadet's Onboard Familairization. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_safety&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_safety&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_safety_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String safety_id = json.getString("safety_id");
                        String date_completed = json.getString("date_completed");
                        String ship_id = json.getString("ship_id");
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);
                        String na = json.getString("na");

                        Integer int_id = db.newIntegerId("person_safety");

                        db.addPersonSafety(person_safety_id , int_id, person_id, safety_id, date_completed, ship_id, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, na);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonEmergenyDrill(context).execute();
            }else{
                new DownloadPersonSafety(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadPersonSafety********
    private class DownloadPersonEmergenyDrill extends AsyncTask<Void, Void, Void> {
        public Context context;
        public DownloadPersonEmergenyDrill(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Onboard Drills and Training Program. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_emergency_drill&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_emergency_drill&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        Integer id = db.newIntegerId("person_emergency_drill");
                        String person_emergency_drill_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String date_familiarization = json.getString("date_familiarization");
                        String details = json.getString("details");
                        String filename = json.getString("filename");

                        downloadFile(filename, "person_crew_list");
                        String checked_by_id = json.getString("checked_by_id");
                        String date_checked = json.getString("date_checked");
                        String checked_remarks = json.getString("checked_remarks");
                        filename = URLDecoder.decode(filename);
                        db.execQuery("INSERT INTO person_emergency_drill(id, person_emergency_drill_id, person_id, vessel_id, date_familiarization, details, filename, checked_by_id, date_checked, checked_remarks) VALUES ("+id+",'"+person_emergency_drill_id+"','"+person_id+"','"+vessel_id+"','"+date_familiarization+"','"+details+"','"+filename+"','"+checked_by_id+"','"+date_checked+"','"+checked_remarks+"')");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonEmergenyAlarm(context).execute();
            }else{
                new DownloadPersonEmergenyDrill(context).execute();
            }
        }
    }

    private class DownloadPersonEmergenyAlarm extends AsyncTask<Void, Void, Void> {
        public Context context;
        public DownloadPersonEmergenyAlarm(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Onboard Emergency Alarm Signals. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_emergency_alarm&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_emergency_alarm&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        Integer id = db.newIntegerId("person_emergency_alarm");
                        String person_emergency_alarm_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String alarm_signal_id = json.getString("alarm_signal_id");
                        String checked_by_id = json.getString("checked_by_id");
                        String date_checked = json.getString("date_checked");
                        String checked_remarks = json.getString("checked_remarks");
                        String na = json.getString("na");

                        db.execQuery("INSERT INTO person_emergency_alarm(id, person_emergency_alarm_id, person_id, vessel_id, alarm_signal_id, checked_by_id, date_checked, checked_remarks, na) VALUES ("+id+",'"+person_emergency_alarm_id+"','"+person_id+"','"+vessel_id+"','"+alarm_signal_id+"','"+checked_by_id+"','"+date_checked+"','"+checked_remarks+"','"+na+"')");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonEmergencyMuster(context).execute();
            }else{
                new DownloadPersonEmergenyAlarm(context).execute();
            }
        }
    }

    private class DownloadPersonEmergencyMuster extends AsyncTask<Void, Void, Void> {
        public Context context;
        public DownloadPersonEmergencyMuster(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Duties and Responsibilities during Emergency. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_emergency_muster&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_emergency_muster&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);


                        Integer id = db.newIntegerId("person_emergency_muster");
                        String person_emergency_muster_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String date_familiarization = json.getString("date_familiarization");
                        String details = json.getString("details");
                        String filename = json.getString("filename");

                        String checked_by_id = json.getString("checked_by_id");
                        String date_checked = json.getString("date_checked");
                        String checked_remarks = json.getString("checked_remarks");

                        db.execQuery("INSERT INTO person_emergency_muster(id, person_emergency_muster_id, person_id, vessel_id, date_familiarization, details, filename, checked_by_id, date_checked, checked_remarks) VALUES ("+id+",'"+person_emergency_muster_id+"','"+person_id+"','"+vessel_id+"','"+date_familiarization+"','"+details+"','"+filename+"','"+checked_by_id+"','"+date_checked+"','"+checked_remarks+"')");
                        filename = filename.replace(" ", "%");
                        downloadFile(filename,  "person_task_file");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonWorkPractice(context).execute();
            }else{
                new DownloadPersonEmergencyMuster(context).execute();
            }
        }
    }

    private class DownloadPersonWorkPractice extends AsyncTask<Void, Void, Void> {
        public Context context;
        public DownloadPersonWorkPractice(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Safe Working Practices. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_work_practice&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_work_practice&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);


                        Integer id = db.newIntegerId("person_work_practice");
                        String person_work_practice_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String work_practice_id = json.getString("work_practice_id");
                        String checked_by_id = json.getString("checked_by_id");
                        String date_checked = json.getString("date_checked");
                        String checked_remarks = json.getString("checked_remarks");
                        db.execQuery("INSERT INTO person_work_practice(id, person_work_practice_id, person_id, vessel_id, work_practice_id, checked_by_id, date_checked, checked_remarks) VALUES ("+id+",'"+person_work_practice_id+"','"+person_id+"','"+vessel_id+"','"+work_practice_id+"','"+checked_by_id+"','"+date_checked+"','"+checked_remarks+"')");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonTask(context).execute();
            }else{
                new DownloadPersonWorkPractice(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadPersonTask ********
    private class DownloadPersonTask extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonTask(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Onboard Training Tasks and Practices. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_task&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_task&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        Integer id = db.newIntegerId("person_task");
                        String person_task_id = json.getString("id");
                        String task_id = json.getString("task_id");
                        String person_id = json.getString("person_id");
                        String completed = json.getString("completed");
                        String answers = json.getString("answers");
                        answers = URLDecoder.decode(answers);
                        String passed = json.getString("passed");
                        String img_file = json.getString("img_file");
                        img_file = URLDecoder.decode(img_file);
                        String not_app = json.getString("not_app");
                        String lat_long = json.getString("lat_long");
                        lat_long = URLDecoder.decode(lat_long);
                        String vessel_type_id = json.getString("vessel_type_id");
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String officer_remarks = json.getString("officer_remarks");
                        officer_remarks = URLDecoder.decode(officer_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);
                        String vessel_id = json.getString("vessel_id");
                        String assessed = json.getString("assessed");
                        String answers2 = json.getString("answers2");
                        String completed2 = json.getString("completed2");
                        String passed2 = json.getString("passed2");
                        String additional_comment = json.getString("additional_comment");
                        additional_comment = URLDecoder.decode(additional_comment);
                        String for_app = json.getString("for_app");
                        String activity_area = json.getString("activity_area");
                        activity_area = URLDecoder.decode(activity_area);
                        String intial_cond = json.getString("intial_cond");
                        intial_cond = URLDecoder.decode(intial_cond);
                        String feedback = json.getString("feedback");
                        feedback = URLDecoder.decode(feedback);

                        String equipments = json.getString("equipments");
                        equipments = URLDecoder.decode(equipments);
                        int cnt = db.GetCount("person_task", " WHERE person_task_id = '"+person_task_id+"'");
                        Log.d("RESULT", "PT CNT :" + cnt);
                        if(cnt == 0){
                            db.execQuery("INSERT INTO person_task(id, person_task_id, task_id, person_id, completed, answers, passed, img_file, not_app, lat_long, vessel_type_id, checked_by_id, app_by_id, date_checked, date_app, officer_remarks, app_remarks, vessel_id, assessed, answers2, completed2, passed2, additional_comment, for_app, activity_area, intial_cond, feedback, equipments) VALUES ("+id+",'"+person_task_id+"','"+task_id+"','"+person_id+"','"+completed+"','"+answers+"','"+passed+"','"+img_file+"','"+not_app+"','"+lat_long+"','"+vessel_type_id+"','"+checked_by_id+"','"+app_by_id+"','"+date_checked+"','"+date_app+"','"+officer_remarks+"','"+app_remarks+"','"+vessel_id+"','"+assessed+"','"+answers2+"','"+completed2+"','"+passed2+"','"+additional_comment+"','"+for_app+"','"+activity_area+"','"+intial_cond+"','"+feedback+"','"+equipments+"')");
                            Log.d("RESULT", "PERSON TASK INSERT INTO person_task(id, person_task_id, task_id, person_id, completed, answers, passed, img_file, not_app, lat_long, vessel_type_id, checked_by_id, app_by_id, date_checked, date_app, officer_remarks, app_remarks, vessel_id, assessed, answers2, completed2, passed2, additional_comment, for_app, activity_area, intial_cond, feedback, equipments) VALUES ("+id+",'"+person_task_id+"','"+task_id+"','"+person_id+"','"+completed+"','"+answers+"','"+passed+"','"+img_file+"','"+not_app+"','"+lat_long+"','"+vessel_type_id+"','"+checked_by_id+"','"+app_by_id+"','"+date_checked+"','"+date_app+"','"+officer_remarks+"','"+app_remarks+"','"+vessel_id+"','"+assessed+"','"+answers2+"','"+completed2+"','"+passed2+"','"+additional_comment+"','"+for_app+"','"+activity_area+"','"+intial_cond+"','"+feedback+"','"+equipments+"')");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            if(success){
                new DownloadPersonTaskFile(context).execute();
            }else{
                new DownloadPersonTask(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadPersonTaskFile ********
    private class DownloadPersonTaskFile extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonTaskFile(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_task_file&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_task_file&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_task_file_id = json.getString("id");
                        String filename = json.getString("filename");
                        filename = URLDecoder.decode(filename);

                        String file_desc = json.getString("file_desc");
                        file_desc = URLDecoder.decode(file_desc);
                        String uploaded = json.getString("uploaded");
                        String person_id = json.getString("person_id");
                        String task_id = json.getString("task_id");
                        String person_task_id = json.getString("person_task_id");
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);

                        Integer int_id = db.newIntegerId("person_task_file");

                        db.addPersonTaskFile(person_task_file_id , int_id, filename, file_desc, uploaded, person_id, task_id, person_task_id, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks);
                        filename = URLDecoder.decode(filename);
                        filename = filename.replace(" ", "%");
                        downloadFile(filename,  "person_task_file");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result) {
            if(success){
                new DownloadPersonSteering(context).execute();
            }else{
                new DownloadPersonTaskFile(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadPersonSteering********
    private class DownloadPersonSteering extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonSteering(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Records of Steering. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_steering&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_steering&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_steering_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String steering_type = json.getString("steering_type");
                        steering_type = URLDecoder.decode(steering_type);
                        String voyage_from = json.getString("voyage_from");
                        voyage_from = URLDecoder.decode(voyage_from);
                        String voyage_to = json.getString("voyage_to");
                        voyage_to = URLDecoder.decode(voyage_to);
                        String date_steering = json.getString("date_steering");
                        String steering_from = json.getString("steering_from");
                        String steering_to = json.getString("steering_to");
                        String total_hrs = json.getString("total_hrs");
                        String remarks = json.getString("remarks");
                        remarks = URLDecoder.decode(remarks);
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);

                        Integer int_id = db.newIntegerId("person_steering");

                        db.addPersonSteering(person_steering_id , int_id, person_id, vessel_id, steering_type, voyage_from, voyage_to, date_steering, steering_from, steering_to, total_hrs, remarks, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonRegulation(context).execute();
            }else{
                new DownloadPersonSteering(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadPersonMuster********
    private class DownloadPersonRegulation extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonRegulation(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading ColRegs. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_regulation&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_regulation&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_regulation_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String regulation_d_id = json.getString("regulation_d_id");
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);

                        Integer int_id = db.newIntegerId("person_regulation");

                        db.addPersonRegulation(person_regulation_id , int_id, person_id, regulation_d_id, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result) {
            if(success){
                new DownloadPersonJournal(context).execute();
            }else{
                new DownloadPersonRegulation(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadPersonJournal ********
    private class DownloadPersonJournal extends AsyncTask<Void, Void, Void> {
        public Context context;
        public DownloadPersonJournal(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Daily Journal. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_journal&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_journal&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        Integer id = db.newIntegerId("person_journal");
                        String person_journal_id = json.getString("id");
                        String journal_date = json.getString("journal_date");
                        String journal_time = json.getString("journal_time");
                        journal_time = URLDecoder.decode(journal_time);
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String ship_position_lat = json.getString("ship_position_lat");
                        ship_position_lat = URLDecoder.decode(ship_position_lat);
                        String fixing_method = json.getString("fixing_method");
                        fixing_method = URLDecoder.decode(fixing_method);
                        String course_speed = json.getString("course_speed");
                        course_speed = URLDecoder.decode(course_speed);
                        String activities = json.getString("activities");
                        activities = URLDecoder.decode(activities);
                        String fo_do = json.getString("fo_do");
                        fo_do = URLDecoder.decode(fo_do);
                        String average_rpm = json.getString("average_rpm");
                        average_rpm = URLDecoder.decode(average_rpm);
                        String average_speed = json.getString("average_speed");
                        average_speed = URLDecoder.decode(average_speed);
                        String checked_by_id = json.getString("checked_by_id");
                        String date_checked = json.getString("date_checked");
                        String login_id = json.getString("login_id");
                        String last_update = json.getString("last_update");
                        String journal_time_to = json.getString("journal_time_to");
                        journal_time_to = URLDecoder.decode(journal_time_to);
                        String hrs = json.getString("hrs");
                        String port_depart = json.getString("port_depart");
                        port_depart = URLDecoder.decode(port_depart);
                        String port_dest = json.getString("port_dest");
                        port_dest = URLDecoder.decode(port_dest);
                        String ship_position_long = json.getString("ship_position_long");
                        ship_position_long = URLDecoder.decode(ship_position_long);
                        String ship_position_vicinity = json.getString("ship_position_vicinity");
                        ship_position_vicinity = URLDecoder.decode(ship_position_vicinity);
                        String fo_rob = json.getString("fo_rob");
                        fo_rob = URLDecoder.decode(fo_rob);
                        String fo_dob = json.getString("fo_dob");
                        fo_dob = URLDecoder.decode(fo_dob);
                        String fo_lob = json.getString("fo_lob");
                        fo_lob = URLDecoder.decode(fo_lob);
                        int cnt= db.GetCount("person_journal", " WHERE person_journal_id = '"+person_journal_id+"'");
                        if(cnt == 0){
                            db.execQuery("INSERT INTO person_journal(id, person_journal_id, journal_date, journal_time, person_id, vessel_id, ship_position_lat, fixing_method, course_speed, activities, fo_do, average_rpm, average_speed, checked_by_id, date_checked, login_id, last_update, journal_time_to, hrs, port_depart, port_dest, ship_position_long, ship_position_vicinity, fo_rob, fo_dob, fo_lob) VALUES ("+id+",'"+person_journal_id+"','"+journal_date+"','"+journal_time+"','"+person_id+"','"+vessel_id+"','"+ship_position_lat+"','"+fixing_method+"','"+course_speed+"','"+activities+"','"+fo_do+"','"+average_rpm+"','"+average_speed+"','"+checked_by_id+"','"+date_checked+"','"+login_id+"','"+last_update+"','"+journal_time_to+"','"+hrs+"','"+port_depart+"','"+port_dest+"','"+ship_position_long+"','"+ship_position_vicinity+"','"+fo_rob+"','"+fo_dob+"','"+fo_lob+"')");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonJournalEngine(context).execute();
            }else{
                new DownloadPersonJournal(context).execute();
            }
        }
    }

    private class DownloadPersonJournalEngine extends AsyncTask<Void, Void, Void> {
        public Context context;
        public DownloadPersonJournalEngine(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Daily Journal. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_journal_engine&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_journal_engine&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        Integer id = db.newIntegerId("person_journal_engine");
                        String person_journal_engine_id = json.getString("id");
                        String journal_date = json.getString("journal_date");
                        String journal_time = json.getString("journal_time");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String ship_position_lat = json.getString("ship_position_lat");
                        ship_position_lat = URLDecoder.decode(ship_position_lat);
                        String fixing_method = json.getString("fixing_method");
                        fixing_method = URLDecoder.decode(fixing_method);
                        String course_speed = json.getString("course_speed");
                        course_speed = URLDecoder.decode(course_speed);
                        String activities = json.getString("activities");
                        activities = URLDecoder.decode(activities);
                        String fo_cons = json.getString("fo_cons");
                        fo_cons = URLDecoder.decode(fo_cons);
                        String average_rpm = json.getString("average_rpm");
                        average_rpm = URLDecoder.decode(average_rpm);
                        String average_speed = json.getString("average_speed");
                        average_speed = URLDecoder.decode(average_speed);
                        String checked_by_id = json.getString("checked_by_id");
                        String date_checked = json.getString("date_checked");
                        String login_id = json.getString("login_id");
                        String last_update = json.getString("last_update");
                        String journal_time_to = json.getString("journal_time_to");
                        journal_time_to = URLDecoder.decode(journal_time_to);
                        String hrs = json.getString("hrs");
                        String port_depart = json.getString("port_depart");
                        port_depart = URLDecoder.decode(port_depart);
                        String port_dest = json.getString("port_dest");
                        port_dest = URLDecoder.decode(port_dest);
                        String ship_position_long = json.getString("ship_position_long");
                        ship_position_long = URLDecoder.decode(ship_position_long);
                        String ship_position_vicinity = json.getString("ship_position_vicinity");
                        ship_position_vicinity = URLDecoder.decode(ship_position_vicinity);
                        String do_cons = json.getString("do_cons");
                        do_cons = URLDecoder.decode(do_cons);

                        int cnt= db.GetCount("person_journal_engine", " WHERE person_journal_engine_id = '"+person_journal_engine_id+"'");
                        if(cnt == 0){
                            db.execQuery("INSERT INTO person_journal_engine(id, person_journal_engine_id, journal_date, journal_time, person_id, vessel_id, ship_position_lat, fixing_method, course_speed, activities, fo_cons, average_rpm, average_speed, checked_by_id, date_checked, login_id, last_update, journal_time_to, hrs, port_depart, port_dest, ship_position_long, ship_position_vicinity, do_cons) VALUES ("+id+",'"+person_journal_engine_id+"','"+journal_date+"','"+journal_time+"','"+person_id+"','"+vessel_id+"','"+ship_position_lat+"','"+fixing_method+"','"+course_speed+"','"+activities+"','"+fo_cons+"','"+average_rpm+"','"+average_speed+"','"+checked_by_id+"','"+date_checked+"','"+login_id+"','"+last_update+"','"+journal_time_to+"','"+hrs+"','"+port_depart+"','"+port_dest+"','"+ship_position_long+"','"+ship_position_vicinity+"','"+do_cons+"')");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonProjectWork(context).execute();
            }else{
                new DownloadPersonJournalEngine(context).execute();
            }
        }
    }

    private class DownloadPersonProjectWork extends AsyncTask<Void, Void, Void> {
        public Context context;
        public DownloadPersonProjectWork(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Project Works. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_project_work&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_project_work&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);


                        Integer id = db.newIntegerId("person_project_work");
                        String person_project_work_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String project_work_d_id = json.getString("project_work_d_id");
                        String date_completed = json.getString("date_completed");
                        String vessel_id = json.getString("vessel_id");
                        String details = json.getString("details");
                        details = URLDecoder.decode(details);
                        String eval = json.getString("eval");
                        String checked_by_id = json.getString("checked_by_id");
                        String date_checked = json.getString("date_checked");
                        String checked_remarks = json.getString("checked_remarks");
                        String login_id = json.getString("login_id");
                        String last_update = json.getString("last_update");
                        String for_app = json.getString("for_app");

                        int cnt= db.GetCount("person_project_work", " WHERE person_project_work_id = '"+person_project_work_id+"'");
                        if(cnt == 0){
                            db.execQuery("INSERT INTO person_project_work(id, person_project_work_id, person_id, project_work_d_id, date_completed, vessel_id, details, eval, checked_by_id, date_checked, checked_remarks, login_id, last_update, for_app) VALUES ("+id+",'"+person_project_work_id+"','"+person_id+"','"+project_work_d_id+"','"+date_completed+"','"+vessel_id+"','"+details+"','"+eval+"','"+checked_by_id+"','"+date_checked+"','"+checked_remarks+"','"+login_id+"','"+last_update+"','"+for_app+"')");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonProjectWorkFile(context).execute();
            }else{
                new DownloadPersonProjectWork(context).execute();
            }
        }
    }

    private class DownloadPersonProjectWorkFile extends AsyncTask<Void, Void, Void> {
        public Context context;
        public DownloadPersonProjectWorkFile(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Project Works. Please wait...");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_project_work_file&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_project_work_file&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);


                        Integer id = db.newIntegerId("person_project_work_file");
                        String person_project_work_file_id = json.getString("id");
                        String person_project_work_id = json.getString("person_project_work_id");
                        String filename = json.getString("filename");
                        filename = URLDecoder.decode(filename);
                        String file_desc = json.getString("file_desc");
                        file_desc = URLDecoder.decode(file_desc);
                        String prio = json.getString("prio");


                        int cnt= db.GetCount("person_project_work_file", " WHERE person_project_work_file_id = '"+person_project_work_file_id+"'");
                        if(cnt == 0){
                            db.execQuery("INSERT INTO person_project_work_file(id, person_project_work_file_id, person_project_work_id, filename, file_desc, prio) VALUES ("+id+",'"+person_project_work_file_id+"','"+person_project_work_id+"','"+filename+"','"+file_desc+"','"+prio+"')");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new DownloadPersonOfficer(context).execute();
            }else{
                new DownloadPersonProjectWorkFile(context).execute();
            }
        }
    }

    //******** DOWNLOAD DownloadPersonOfficer********
    private class DownloadPersonOfficer extends AsyncTask<Void, Void, Void>{
        public Context context;
        public DownloadPersonOfficer(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            tv_download.setText("Downloading Responsible Officers. Please wait....");
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_officer&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_officer&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(!str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_officer_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String officer_id = json.getString("officer_id");
                        String last_update = json.getString("last_update");
                        String from_date = json.getString("from_date");
                        String to_date = json.getString("to_date");
                        String comp_officer_ok = json.getString("comp_officer_ok");
                        String vessel_id = json.getString("vessel_id");
                        String assessor_id = json.getString("assessor_id");
                        String master_id = json.getString("master_id");
                        String chief_eng_id = json.getString("chief_eng_id");
                        Integer int_id = db.newIntegerId("person_officer");

                        db.addPersonOfficer(person_officer_id , int_id, person_id, officer_id, last_update, from_date, to_date, comp_officer_ok, vessel_id, assessor_id, master_id, chief_eng_id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                success = false;
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(success){
                new Setup(context).execute();
            }else{
                new DownloadPersonOfficer(context).execute();
            }
        }
    }

    //******** DOWNLOAD BASIC TRAINING ********
    private class DownloadBasicTraining extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadBasicTraining(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=basic_training&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=basic_training&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String basic_training_id = json.getString("id");
                        String desc_basic_training = json.getString("desc_basic_training");
                        desc_basic_training = URLDecoder.decode(desc_basic_training);
                        String dept = json.getString("dept");
                        Integer prio = json.getInt("prio");
                        String training_type = json.getString("training_type");
                        training_type = URLDecoder.decode(training_type);

                        Integer int_id = db.newIntegerId("basic_training");

                        db.addBasicTraining(basic_training_id , int_id, desc_basic_training, dept, prio, training_type);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(err_message.equals("Connected")){
                new DownlaodShipboard(context).execute();
            }else{
                progressBar.setVisibility(View.GONE);
                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }



    //******** DOWNLOAD DownloadPersonBridgeWatch ********
    private class DownloadPersonBridgeWatch extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonBridgeWatch(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_bridge_watch&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_bridge_watch&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_bridge_watch_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String date_watchkeeping = json.getString("date_watchkeeping");
                        String from_time = json.getString("from_time");
                        String to_time = json.getString("to_time");
                        String voyage_number = json.getString("voyage_number");
                        String voyage_desc = json.getString("voyage_desc");
                        voyage_desc = URLDecoder.decode(voyage_desc);
                        String watch_type = json.getString("watch_type");
                        String remarks = json.getString("remarks");
                        remarks = URLDecoder.decode(remarks);
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        if(date_checked.equals("null")){
                            date_checked = "";
                        }
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);
                        Double total_hrs = json.getDouble("total_hrs");

                        Integer int_id = db.newIntegerId("person_bridge_watch");

                        db.addPersonBridgeWatch(person_bridge_watch_id , int_id, person_id, vessel_id, date_watchkeeping, from_time, to_time, voyage_number, voyage_desc, watch_type, remarks, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, total_hrs);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(err_message.equals("Connected")){
                new DownloadPersonCe(context).execute();
            }else{
                progressBar.setVisibility(View.GONE);
                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }





    //******** DOWNLOAD DownloadEducH ********
    private class DownloadEducH extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadEducH(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_education_h&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_education_h&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_education_h_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String gce_level = json.getString("gce_level");
                        gce_level = URLDecoder.decode(gce_level);
                        String school_name = json.getString("school_name");
                        school_name = URLDecoder.decode(school_name);
                        String school_address = json.getString("school_address");
                        school_address = URLDecoder.decode(school_address);
                        String certificate_date = json.getString("certificate_date");
                        String login_id = json.getString("login_id");
                        String last_update = json.getString("last_update");

                        Integer int_id = db.newIntegerId("person_education_h");

                        db.addPersonEducationH(person_education_h_id , int_id, person_id, gce_level, school_name, school_address, certificate_date, login_id, last_update);
                        DownloadEducationD(person_education_h_id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(err_message.equals("Connected")){
                new DownloadPersonMaterial(context).execute();
            }else{

                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    public void DownloadEducationD(String person_education_h_id){
        HttpClient myClient = new DefaultHttpClient();

        HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_education_d&person_education_h_id=" + person_education_h_id);
        Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_education_d&person_education_h_id=" + person_education_h_id);

        try {
            response = (HttpResponse) myClient.execute(myConnection);
            str = EntityUtils.toString(response.getEntity(), "UTF-8");
            err_message = "Connected";
            Log.d("CONNECT", "" + response);

        } catch (ClientProtocolException e) {
            err_message = "Cannot connect to server.";
            e.printStackTrace();
            Log.d("CONNECT", "" + response);
        } catch (IOException e) {
            e.printStackTrace();
            err_message = "Sorry! Something went wrong." + e;
            Log.d("CONNECT", "" + response);
        }

        if(str != null){
            JSONArray jArray = null;
            try {
                jArray = new JSONArray(str);
                for (int i = 0; i < jArray.length(); i++) {
                    cnt++;
                    json = jArray.getJSONObject(i);

                    String person_education_d_id = json.getString("id");
                    String subject = json.getString("subject");
                    subject = URLDecoder.decode(subject);
                    Double grade = json.getDouble("grade");

                    Integer int_id = db.newIntegerId("person_education_d");

                    db.addPersonEducationD(person_education_d_id , int_id, person_education_h_id, subject, grade);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return;
    }

    //******** DOWNLOAD DownloadPersonMaterial********
    private class DownloadPersonMaterial extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonMaterial(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_material&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_material&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_material_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String material = json.getString("material");
                        material = URLDecoder.decode(material);
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);
                        String date_material = json.getString("date_material");

                        Integer int_id = db.newIntegerId("person_material");

                        db.addPersonMaterial(person_material_id , int_id, person_id, material, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, date_material);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(err_message.equals("Connected")){
                new DownloadPersonOfficer(context).execute();
            }else{

                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }



    //******** DOWNLOAD DownloadPersonMuster********
    private class DownloadPersonMuster extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonMuster(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_muster&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_muster&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_muster_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String lifeboat_station = json.getString("lifeboat_station");
                        lifeboat_station = URLDecoder.decode(lifeboat_station);
                        String lifeboat_duties = json.getString("lifeboat_duties");
                        lifeboat_duties = URLDecoder.decode(lifeboat_duties);
                        String emergency_station = json.getString("emergency_station");
                        emergency_station = URLDecoder.decode(emergency_station);
                        String emergency_duties = json.getString("emergency_duties");
                        emergency_duties = URLDecoder.decode(emergency_duties);
                        String oil_spill_duties = json.getString("oil_spill_duties");
                        oil_spill_duties = URLDecoder.decode(oil_spill_duties);
                        String safety_officer_id = json.getString("safety_officer_id");
                        String security_officer_id = json.getString("security_officer_id");
                        String master_id = json.getString("master_id");
                        String date_checked = json.getString("date_checked");

                        Integer int_id = db.newIntegerId("person_muster");

                        db.addPersonMuster(person_muster_id , int_id, person_id, vessel_id, lifeboat_station, lifeboat_duties, emergency_station, emergency_duties, oil_spill_duties, safety_officer_id, security_officer_id, master_id, date_checked);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(err_message.equals("Connected")){
                new DownloadPersonPortWatch(context).execute();
            }else{

                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    //******** DOWNLOAD DownloadPersonPortWatch********
    private class DownloadPersonPortWatch extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonPortWatch(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_port_watch&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_port_watch&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_port_watch_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String date_watch = json.getString("date_watch");
                        String from_time = json.getString("from_time");
                        String to_time = json.getString("to_time");
                        String voyage_number = json.getString("voyage_number");
                        String port_name = json.getString("port_name");
                        port_name = URLDecoder.decode(port_name);
                        String desc_cargo = json.getString("desc_cargo");
                        desc_cargo = URLDecoder.decode(desc_cargo);
                        String remarks = json.getString("remarks");
                        remarks = URLDecoder.decode(remarks);
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);
                        Double total_hrs = json.getDouble("total_hrs");

                        Integer int_id = db.newIntegerId("person_port_watch");

                        db.addPersonPortWatch(person_port_watch_id , int_id, person_id, vessel_id, date_watch, from_time, to_time, voyage_number, port_name, desc_cargo, remarks, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, total_hrs);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(err_message.equals("Connected")){
                new DownloadPersonRegulation(context).execute();
            }else{

                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }



    //******** DOWNLOAD DownloadRegulationH********
    private class DownloadRegulationH extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadRegulationH(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=regulation_h&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=regulation_h&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String regulation_h_id = json.getString("id");
                        String desc_regulation_h = json.getString("desc_regulation_h");
                        desc_regulation_h = URLDecoder.decode(desc_regulation_h);
                        String prio = json.getString("prio");
                        String login_id = json.getString("login_id");
                        String last_update = json.getString("last_update");

                        Integer int_id = db.newIntegerId("regulation_h");

                        db.addRegulationH(regulation_h_id , int_id, desc_regulation_h, prio, login_id, last_update);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(err_message.equals("Connected")){
                new DownloadRegulationD(context).execute();
            }else{

                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    //******** DOWNLOAD DownloadRegulationD********
    private class DownloadRegulationD extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadRegulationD(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=regulation_d&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=regulation_d&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String regulation_d_id = json.getString("id");
                        String regulation_h_id = json.getString("regulation_h_id");
                        String desc_regulation_d = json.getString("desc_regulation_d");
                        desc_regulation_d = URLDecoder.decode(desc_regulation_d);
                        String prio = json.getString("prio");
                        String contents = json.getString("contents");

                        Integer int_id = db.newIntegerId("regulation_d");

                        db.addRegulationD(regulation_d_id , int_id, regulation_h_id, desc_regulation_d, prio, contents);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(err_message.equals("Connected")){
                new DownloadPersonSafety(context).execute();
            }else{

                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }



    //******** DOWNLOAD DownloadSafety********
    private class DownloadSafety extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadSafety(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=safety&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=safety&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String safety_id = json.getString("id");
                        String desc_task = json.getString("desc_task");
                        desc_task = URLDecoder.decode(desc_task);
                        String prio = json.getString("prio");
                        String dept = json.getString("dept");

                        Integer int_id = db.newIntegerId("safety");

                        db.addSafety(safety_id , int_id, desc_task, prio, dept);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(err_message.equals("Connected")){
                new DownloadVessel(context).execute();
            }else{

                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }





    //******** DOWNLOAD DownloadPersonSteerTopic********
    private class DownloadPersonSteerTopic extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonSteerTopic(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_steer_topic&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_steer_topic&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_steer_topic_id = json.getString("id");
                        String steer_topic_id = json.getString("steer_topic_id");
                        String person_id = json.getString("person_id");
                        String checked_by_id = json.getString("checked_by_id");
                        String date_checked = json.getString("date_checked");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);

                        Integer int_id = db.newIntegerId("person_steer_topic");

                        db.addPersonSteerTopic(person_steer_topic_id , int_id, steer_topic_id, person_id, checked_by_id, date_checked, checked_remarks);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(err_message.equals("Connected")){
                new DownloadPersonSteerTask(context).execute();
            }else{
                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    //******** DOWNLOAD DownloadPersonSteerTask ********
    private class DownloadPersonSteerTask extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonSteerTask(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_steer_task&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_steer_task&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_steer_task_id = json.getString("id");
                        String steer_task_id = json.getString("steer_task_id");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String completed = json.getString("completed");
                        String answers = json.getString("answers");
                        answers = URLDecoder.decode(answers);
                        String passed = json.getString("passed");
                        String not_app = json.getString("not_app");
                        String lat_long = json.getString("lat_long");
                        lat_long = URLDecoder.decode(lat_long);
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);

                        Integer int_id = db.newIntegerId("person_steer_task");

                        db.addPersonSteerTask(person_steer_task_id , int_id, steer_task_id, person_id, vessel_id, completed, answers, passed, not_app, lat_long, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(err_message.equals("Connected")){
                new DownloadPersonTrbSubCompetence(context).execute();
            }else{
                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    //******** DOWNLOAD DownloadSteerTask ********
    private class DownloadSteerTask extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadSteerTask(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=steer_task&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=steer_task&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String steer_task_id = json.getString("id");
                        String ref_no = json.getString("ref_no");
                        ref_no = URLDecoder.decode(ref_no);
                        String desc_steer_task = json.getString("desc_steer_task");
                        desc_steer_task = URLDecoder.decode(desc_steer_task);
                        String steer_competence_id = json.getString("steer_competence_id");
                        String steer_topic_id = json.getString("steer_topic_id");
                        String prio = json.getString("prio");
                        String phase_no = json.getString("phase_no");

                        Integer int_id = db.newIntegerId("steer_task");

                        db.addSteerTask(steer_task_id , int_id, ref_no, desc_steer_task, steer_competence_id, steer_topic_id, prio, phase_no);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(err_message.equals("Connected")){
                new DownloadSteerTopic(context).execute();
            }else{
                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    //******** DOWNLOAD DownloadSteerTopic ********
    private class DownloadSteerTopic extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadSteerTopic(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=steer_topic&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=steer_topic&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String steer_topic_id = json.getString("id");
                        String ref_no_topic = json.getString("ref_no_topic");
                        ref_no_topic = URLDecoder.decode(ref_no_topic);
                        String desc_steer_topic = json.getString("desc_steer_topic");
                        desc_steer_topic = URLDecoder.decode(desc_steer_topic);
                        String steer_competence_id = json.getString("steer_competence_id");
                        String criteria = json.getString("criteria");
                        criteria = URLDecoder.decode(criteria);
                        String prio = json.getString("prio");

                        Integer int_id = db.newIntegerId("steer_topic");

                        db.addSteerTopic(steer_topic_id , int_id, ref_no_topic, desc_steer_topic, steer_competence_id, criteria, prio);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(err_message.equals("Connected")){
                new DownloadSteerCompetence(context).execute();
            }else{
                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    //******** DOWNLOAD DownloadSteerCompetence ********
    private class DownloadSteerCompetence extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadSteerCompetence(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=steer_competence&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=steer_competence&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String steer_competence_id = json.getString("id");
                        String ref_no = json.getString("ref_no");
                        ref_no = URLDecoder.decode(ref_no);
                        String desc_competence = json.getString("desc_competence");
                        desc_competence = URLDecoder.decode(desc_competence);
                        Integer prio = json.getInt("prio");

                        Integer int_id = db.newIntegerId("steer_competence");

                        db.addSteerCompetence(steer_competence_id , int_id, ref_no, desc_competence, prio);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(err_message.equals("Connected")){
                new DownloadPersonTrbSubCompetence(context).execute();
            }else{
                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    //******** DOWNLOAD DownloadPersonTrbTopic ********
    private class DownloadPersonTrbSubCompetence extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonTrbSubCompetence(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_trb_sub_competence&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=person_trb_sub_competence&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String person_trb_sub_competence_id = json.getString("id");
                        String trb_sub_competence_id = json.getString("trb_sub_competence_id");
                        String person_id = json.getString("person_id");
                        String checked_by_id = json.getString("checked_by_id");
                        String date_checked = json.getString("date_checked");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);

                        Integer int_id = db.newIntegerId("person_trb_sub_competence");

                        db.addPersonTrbSubComp(person_trb_sub_competence_id , int_id, trb_sub_competence_id, person_id, checked_by_id, date_checked, checked_remarks);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(err_message.equals("Connected")){
                new DownloadPersonTask(context).execute();
            }else{
                tv_download.setText("An error downloading basic training records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }







    //******** DOWNLOAD DownloadAbbrev ********
    private class DownloadAbbrev extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadAbbrev(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=abbreviation&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=abbreviation&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        cnt++;
                        json = jArray.getJSONObject(i);

                        String abbreviation_id = json.getString("id");
                        String code_abbreviation = json.getString("code_abbreviation");
                        code_abbreviation = URLDecoder.decode(code_abbreviation);
                        String desc_abbreviation = json.getString("desc_abbreviation");
                        desc_abbreviation = URLDecoder.decode(desc_abbreviation);
                        String login_id = json.getString("login_id");
                        String last_update = json.getString("last_update");
                        last_update = URLDecoder.decode(last_update);

                        Integer int_id = db.newIntegerId("abbreviation");

                        db.addAbbreviation(abbreviation_id , int_id, code_abbreviation, desc_abbreviation, login_id, last_update);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result){
            if(err_message.equals("Connected")){
                new DownloadPersonJournal(context).execute();
            }else{
                tv_download.setText("An error downloading abbreviation records has been detected. Please ensure that you have provided your username and password correctly.");
                tv_download.setTextColor(Color.RED);
                tv_download.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }




    //******** Setup ********
    private class Setup extends AsyncTask<Void, Void, Void> {
        public Context context;
        public Setup(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            String dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");
            List<TrbFunction> trbFunctions = db.getFunctions(dept, "vessel_type_id='' AND dp='' AND ice_class = '' AND spec_to = ''");
            for(TrbFunction trbFunction : trbFunctions){
                String trb_function_id = trbFunction.getTrb_function_id();
                List<TrbCompetence> trbCompetences = db.getCompetences(trb_function_id);
                for(TrbCompetence trbCompetence : trbCompetences){
                    String trb_competence_id = trbCompetence.getTrb_competence_id();
                    List<TrbTopic> trbTopics = db.getTrbTopics(trb_competence_id);
                    for(TrbTopic trbTopic : trbTopics){
                        String trb_topic_id = trbTopic.getTrb_topic_id();
                        int cnt = db.GetCount("person_trb_topic", " WHERE trb_topic_id = '"+trb_topic_id+"' AND person_id = '"+person_id+"'");
                        Log.d("RESULT", "TRBF1" + cnt);
                        if(cnt == 0){ //ADD
                            Integer int_id = db.newIntegerId("person_trb_topic");
                            String person_trb_topic_id = db.newId();
                            db.execQuery("INSERT INTO person_trb_topic (id, person_trb_topic_id, trb_topic_id, person_id, checked_by_id, date_checked, checked_remarks) VALUES ("+int_id+", '"+person_trb_topic_id+"', '"+trb_topic_id+"', '"+person_id+"', '', '','')");
                            //GET TASKS
                            List<Task> tasks = db.getTasks(trb_competence_id, trb_topic_id);
                            for(Task task : tasks){
                                Integer int_id2 = db.newIntegerId("person_task");
                                String person_task_id = db.newId();
                                String task_id = task.getTask_id();
                                int cnt2 = db.GetCount("person_task", " WHERE task_id = '"+task_id+"' AND person_id = '"+person_id+"'");
                                if(cnt2 == 0){
                                    db.query("INSERT INTO person_task (id, person_task_id, task_id, person_id, completed, answers, passed, img_file, not_app, lat_long, vessel_type_id, checked_by_id, app_by_id, date_checked, date_app, officer_remarks, app_remarks, vessel_id) VALUES ("+int_id2+", '"+person_task_id+"', '"+task_id+"', '"+person_id+"', '', '', '', '', '', '', '', '', '', '', '', '', '', '')");
                                    Log.d("RESULT", "INSERT INTO person_task (id, person_task_id, task_id, person_id, completed, answers, passed, img_file, not_app, lat_long, vessel_type_id, checked_by_id, app_by_id, date_checked, date_app, officer_remarks, app_remarks, vessel_id) VALUES ("+int_id2+", '"+person_task_id+"', '"+task_id+"', '"+person_id+"', '', '', '', '', '', '', '', '', '', '', '', '', '', '')");
                                }

                                tcnt++;
                            }
                        }else { //CHECK PERSON_TASK
                            List<Task> tasks = db.getTasks(trb_competence_id, trb_topic_id);
                            for(Task task : tasks){
                                Integer int_id2 = db.newIntegerId("person_task");
                                String person_task_id = db.newId();
                                String task_id = task.getTask_id();
                                int cnt2 = db.GetCount("person_task", " WHERE task_id = '"+task_id+"' AND person_id = '"+person_id+"'");
                                if(cnt2 == 0){
                                    db.query("INSERT INTO person_task (id, person_task_id, task_id, person_id, completed, answers, passed, img_file, not_app, lat_long, vessel_type_id, checked_by_id, app_by_id, date_checked, date_app, officer_remarks, app_remarks, vessel_id) VALUES ("+int_id2+", '"+person_task_id+"', '"+task_id+"', '"+person_id+"', '', '', '', '', '', '', '', '', '', '', '', '', '', '')");
                                    Log.d("RESULT", "INSERT INTO person_task (id, person_task_id, task_id, person_id, completed, answers, passed, img_file, not_app, lat_long, vessel_type_id, checked_by_id, app_by_id, date_checked, date_app, officer_remarks, app_remarks, vessel_id) VALUES ("+int_id2+", '"+person_task_id+"', '"+task_id+"', '"+person_id+"', '', '', '', '', '', '', '', '', '', '', '', '', '', '')");
                                }
                                tcnt++;
                            }//END OF TASKS
                        }
                    }
                }
            }
            //GET FUNCTIONS BASED ON SEA SERVICE
            int cnt = db.GetCount("shipboard", "");
            if(cnt > 0){
                String vessel_id = db.getFieldString("vessel_id", " person_id = '"+person_id+"' ORDER BY sign_on DESC LIMIT 1", "shipboard");
                String vessel_type_id = db.getFieldString("vessel_type_id", " vessel_id = '"+vessel_id+"'", "vessel");

                //SELECT FUNCTION DEPENDS ON VESSEL TYPE
                String chk = db.getFieldString("id", " vessel_type_id = '"+vessel_type_id+"' AND dept = '"+dept+"'", "trb_function");
                if(!chk.equals("")){ //IF FUNCTIONS SPECS TO VESSEL TYPE EXISTS
                    trbFunctions = db.getFunctions(dept, "vessel_type_id='"+vessel_type_id+"' AND spec_to = ''");
                    for(TrbFunction trbFunction : trbFunctions){
                        String trb_function_id = trbFunction.getTrb_function_id();
                        List<TrbCompetence> trbCompetences = db.getCompetences(trb_function_id);
                        for(TrbCompetence trbCompetence : trbCompetences){
                            String trb_competence_id = trbCompetence.getTrb_competence_id();
                            List<TrbTopic> trbTopics = db.getTopics(trb_competence_id);
                            for(TrbTopic trbTopic : trbTopics){
                                String trb_topic_id = trbTopic.getTrb_topic_id();
                                int cnt2 = db.GetCount("person_trb_topic", " WHERE trb_topic_id = '"+trb_topic_id+"' AND person_id = '"+person_id+"'");
                                Log.d("RESULT", "TRBF2" + cnt2);
                                if(cnt2 == 0){ //ADD
                                    Integer int_id = db.newIntegerId("person_trb_topic");
                                    String person_trb_topic_id = db.newId();
                                    db.execQuery("INSERT INTO person_trb_topic (id, person_trb_topic_id, trb_topic_id, person_id, checked_by_id, date_checked, checked_remarks) VALUES ("+int_id+", '"+person_trb_topic_id+"', '"+trb_topic_id+"', '"+person_id+"', '', '','')");
                                    int conn = getConnection.getConnectionType(DownloadFromServerActivity.this);
                                    //if(conn != 0){
                                     //   SyncOnline(context, person_trb_topic_id);
                                    //}
                                    List<Task> tasks = db.getTasks(trb_competence_id, trb_topic_id);
                                    for(Task task : tasks){
                                        Integer int_id2 = db.newIntegerId("person_task");
                                        String person_task_id = db.newId();
                                        String task_id = task.getTask_id();
                                        db.query("INSERT INTO person_task (id, person_task_id, task_id, person_id, completed, answers, passed, img_file, not_app, lat_long, vessel_type_id, checked_by_id, app_by_id, date_checked, date_app, officer_remarks, app_remarks, vessel_id) VALUES ("+int_id2+", '"+person_task_id+"', '"+task_id+"', '"+person_id+"', '', '', '', '', '', '', '', '', '', '', '', '', '', '')");
                                        //SyncOnline2(person_task_id);
                                        tcnt++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Log.d("RESULT", "TOTAL T CNT : " + tcnt);
            return null;
        }
        protected void onPostExecute(Void result){
            progressBar.setVisibility(View.GONE);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DownloadFromServerActivity.this);

            alertDialogBuilder.setMessage("CONGRATULATIONS! You have successfully downloaded your record from cloud.");
            alertDialogBuilder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent intent = new Intent(DownloadFromServerActivity.this, CaptureActivity.class);
                    //Intent intent = new Intent(DownloadFromServerActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialogBuilder.setCancelable(false);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(DownloadFromServerActivity.this, R.color.white));
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(DownloadFromServerActivity.this, R.color.black));
        }
    }

    public void SyncOnline(Context context, String id){
        String url = getString(R.string.url);
        //GET FROM TBL
        String saved_trb_sub_competence_id = db.getFieldString("trb_sub_competence_id", "person_trb_sub_competence_id = '" + id + "'", "person_trb_sub_competence");
        String saved_person_id = db.getFieldString("person_id", "person_trb_sub_competence_id = '" + id + "'", "person_trb_sub_competence");
        String saved_checked_by_id = db.getFieldString("checked_by_id", "person_trb_sub_competence_id = '" + id + "'", "person_trb_sub_competence");
        String saved_date_checked = db.getFieldString("date_checked", "person_trb_sub_competence_id = '" + id + "'", "person_trb_sub_competence");
        String saved_checked_remarks = db.getFieldString("checked_remarks", "person_trb_sub_competence_id = '" + id + "'", "person_trb_sub_competence");
        saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
        HttpClient myClient = new DefaultHttpClient();

        HttpPost myConnection = new HttpPost(url +"sync.php?table=person_trb_sub_competence&id="+id+"&trb_sub_competence_id="+saved_trb_sub_competence_id+"&person_id="+saved_person_id+"&checked_by_id="+saved_checked_by_id+"&date_checked="+saved_date_checked+"&checked_remarks="+saved_checked_remarks+"&event=ADD");
        Log.d("CONNECT", url +"sync.php?table=person_trb_sub_competence&id="+id+"&trb_sub_competence_id="+saved_trb_sub_competence_id+"&person_id="+saved_person_id+"&checked_by_id="+saved_checked_by_id+"&date_checked="+saved_date_checked+"&checked_remarks="+saved_checked_remarks+"&event=ADD");

        try {
            response = (HttpResponse) myClient.execute(myConnection);
            str = EntityUtils.toString(response.getEntity(), "UTF-8");
            Log.d("CONNECT", str);
            err_message = "Connected";
        } catch (ClientProtocolException e) {
            err_message = "Cannot connect to server.";
            e.printStackTrace();
            Log.d("CONNECT", "" + response + str);
        } catch (IOException e) {
            e.printStackTrace();
            err_message = "Sorry! Something went wrong." + e;
            Log.d("CONNECT", "" + response + str);
        }

        return;
    }

    public void SyncOnline2(String id){
        String url = getString(R.string.url);
        //GET FROM TBL
        String saved_task_id = db.getFieldString("task_id", "person_task_id = '" + id + "'", "person_task");
        String saved_person_id = db.getFieldString("person_id", "person_task_id = '" + id + "'", "person_task");
        String saved_completed = db.getFieldString("completed", "person_task_id = '" + id + "'", "person_task");
        String saved_answers = db.getFieldString("answers", "person_task_id = '" + id + "'", "person_task");
        saved_answers = URLEncoder.encode(saved_answers);
        String saved_passed = db.getFieldString("passed", "person_task_id = '" + id + "'", "person_task");
        String saved_img_file = db.getFieldString("img_file", "person_task_id = '" + id + "'", "person_task");
        saved_img_file = URLEncoder.encode(saved_img_file);
        String saved_not_app = db.getFieldString("not_app", "person_task_id = '" + id + "'", "person_task");
        String saved_lat_long = db.getFieldString("lat_long", "person_task_id = '" + id + "'", "person_task");
        saved_lat_long = URLEncoder.encode(saved_lat_long);
        String saved_vessel_type_id = db.getFieldString("vessel_type_id", "person_task_id = '" + id + "'", "person_task");
        String saved_checked_by_id = db.getFieldString("checked_by_id", "person_task_id = '" + id + "'", "person_task");
        String saved_app_by_id = db.getFieldString("app_by_id", "person_task_id = '" + id + "'", "person_task");
        String saved_date_checked = db.getFieldString("date_checked", "person_task_id = '" + id + "'", "person_task");
        String saved_date_app = db.getFieldString("date_app", "person_task_id = '" + id + "'", "person_task");
        String saved_officer_remarks = db.getFieldString("officer_remarks", "person_task_id = '" + id + "'", "person_task");
        saved_officer_remarks = URLEncoder.encode(saved_officer_remarks);
        String saved_app_remarks = db.getFieldString("app_remarks", "person_task_id = '" + id + "'", "person_task");
        saved_app_remarks = URLEncoder.encode(saved_app_remarks);
        String saved_vessel_id = db.getFieldString("vessel_id", "person_task_id = '" + id + "'", "person_task");

        HttpClient myClient = new DefaultHttpClient();

        HttpPost myConnection = new HttpPost(url +"sync.php?table=person_task&id="+id+"&task_id="+saved_task_id+"&person_id="+saved_person_id+"&completed="+saved_completed+"&answers="+saved_answers+"&passed="+saved_passed+"&img_file="+saved_img_file+"&not_app="+saved_not_app+"&lat_long="+saved_lat_long+"&vessel_type_id="+saved_vessel_type_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&officer_remarks="+saved_officer_remarks+"&app_remarks="+saved_app_remarks+"&vessel_id="+saved_vessel_id+"&event=ADD");
        Log.d("CONNECT", url +"sync.php?person_task&id="+id+"&task_id="+saved_task_id+"&person_id="+saved_person_id+"&completed="+saved_completed+"&answers="+saved_answers+"&passed="+saved_passed+"&img_file="+saved_img_file+"&not_app="+saved_not_app+"&lat_long="+saved_lat_long+"&vessel_type_id="+saved_vessel_type_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&officer_remarks="+saved_officer_remarks+"&app_remarks="+saved_app_remarks+"&vessel_id="+saved_vessel_id+"&event=ADD");

        try {
            response = (HttpResponse) myClient.execute(myConnection);
            str = EntityUtils.toString(response.getEntity(), "UTF-8");
            Log.d("CONNECT", str);
            err_message = "Connected";
        } catch (ClientProtocolException e) {
            err_message = "Cannot connect to server.";
            e.printStackTrace();
            Log.d("CONNECT", "" + response + str);
        } catch (IOException e) {
            e.printStackTrace();
            err_message = "Sorry! Something went wrong." + e;
            Log.d("CONNECT", "" + response + str);
        }
    }

    void downloadFile(String filename, String table){
        try {
            URL url = null;
            if(table.equals("person_task_file")){
                url = new URL(dwnload_file_path + filename);
            }else if(table.equals("person_crew_list")){
                url = new URL(url_file + filename);
            }else{
                url = new URL(dl_photo_file + filename);
            }

            Log.d("RESULT - IMG", "" + url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect(); //connect

            File SDCardRoot = getExternalFilesDir(Environment.DIRECTORY_PICTURES); //set the path where we want to save the file
            File file = new File(SDCardRoot, filename); //create a new file, to save the downloaded file
            Log.d("RESULT", "" + SDCardRoot);

            FileOutputStream fileOutput = new FileOutputStream(file);

            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            //this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();
            downloadedSize = 0;

            runOnUiThread(new Runnable() {
                public void run() {
                    Log.d("RESULT ", "DL Total Size" + totalSize);
                }
            });

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                // update the progressbar //
                runOnUiThread(new Runnable() {
                    public void run() {
                        float per = ((float)downloadedSize/totalSize) * 100;
                        Log.d("RESULT ", "DL Size" + downloadedSize + "KB / " + totalSize + "KB (" + (int)per + "%)");
                    }
                });
            }
            //close the output stream when complete //
            fileOutput.close();
            runOnUiThread(new Runnable() {
                public void run() {
                    // pb.dismiss(); // if you want close it..
                }
            });

        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            showError("Error : IOException " + e);
            e.printStackTrace();
        }
        catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }
    }

    void showError(final String err){
        runOnUiThread(new Runnable() {
            public void run() {
                // Toast.makeText(DownloadFromServerActivity.this, err, Toast.LENGTH_LONG).show();
            }
        });
    }


    public static int getConnectionType(Context context) {
        int result = 0; // Returns connection type. 0: none; 1: mobile data; 2: wifi
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = 2;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = 1;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        result = 3;
                    }
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        result = 2;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        result = 1;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_VPN) {
                        result = 3;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DownloadFromServerActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( pd!=null && pd.isShowing() ){
            pd.cancel();
        }
    }


}
