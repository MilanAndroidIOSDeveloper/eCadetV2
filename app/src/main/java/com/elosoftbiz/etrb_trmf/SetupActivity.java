package com.elosoftbiz.etrb_trmf;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SetupActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    DatabaseHelper db;
    isNetworkAvailable isNet;
    Boolean wNet = false, success = false;
    TextInputLayout et_login_name, et_login_pass;
    Button btn_download;
    TextView tv_message;
    ProgressBar progressBar;
    String login_name, login_pass, person_id, url = "", str = "", err_message, photo_file;
    String company_id, dwnload_file_path, dl_photo_file;
    JSONObject json = null;
    HttpResponse response;
    int totalSize, downloadedSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        context = this;
        db = new DatabaseHelper(context);
        url = getString(R.string.url);

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

        et_login_name = findViewById(R.id.et_login_name);
        et_login_pass = findViewById(R.id.et_login_pass);
        et_login_pass.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btn_download = findViewById(R.id.btn_download);
        tv_message = findViewById(R.id.tv_message);
        progressBar = findViewById(R.id.progressBar);

        tv_message.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_message.setText("");
                login_name = et_login_name.getEditText().getText().toString();
                login_pass = et_login_pass.getEditText().getText().toString();
                Log.d("RESULT", login_name + "");

                if(login_name.equals("")){
                    Toast.makeText(context, "Username is required.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(login_pass.equals("")){
                    Toast.makeText(context, "Password is required.", Toast.LENGTH_LONG).show();
                    return;
                }
                login_name = URLEncoder.encode(login_name);
                login_pass = URLEncoder.encode(login_pass);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("By clicking the confirmation button below, your e-Cadet records will be saved here.");
                alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        isNet = new isNetworkAvailable(context);
                        wNet = isNet.networkStatus();
                        if(wNet){
                            new Download(context).execute();
                        }else{
                            tv_message.setText("No Internet Access! Make sure you are connected to a stable internet.");
                            tv_message.setTextColor(getColor(R.color.red));
                            tv_message.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));

            }
        });
    }

    private class Download extends AsyncTask<Void, Void, Void> {
        public Context context;

        public Download(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tv_message.setText("Connecting to server .....");
            tv_message.setTextColor(getColor(R.color.black));
            tv_message.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            db.query("DELETE FROM backup_history");
            db.query("DELETE FROM backup_item");

            db.query("DELETE FROM abbreviation");
            db.query("DELETE FROM basic_training");
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
            db.query("DELETE FROM person_trb_sub_competence");
            db.query("DELETE FROM person_to");
            db.query("DELETE FROM person_vessel");
            db.query("DELETE FROM program");
            db.query("DELETE FROM safety");
            db.query("DELETE FROM shipboard");
            db.query("DELETE FROM vessel");
            db.query("DELETE FROM vessel_type");
            db.query("DELETE FROM person_journal");
            db.query("DELETE FROM client");

            url = getString(R.string.url);
            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url + "connection.php?login_name=" + login_name + "&login_pass=" + login_pass + "&table=person");
            Log.d("CONNECT", url + "connection.php?login_name=" + login_name + "&login_pass=" + login_pass + "&table=person");

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "";
                Log.d("CONNECT", "" + response + "=" + str);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str.equals("No registered cadet found.")){
                err_message = str;
                success = false;
            }else if(!str.equals("[]") && !str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        json = jArray.getJSONObject(i);

                        person_id = json.getString("id");

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

                        Log.d("RESULT", "ID : "+ person_id + " " + lname);
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

                        Integer int_id = db.newIntegerId("person");
                        //db.addPerson(int_id, person_id, code_person, lname, fname, mname, st_address, city_id, province_id, phone, mobile, st_address_province, phone_province, email, gender, civ_status, birth_date, birth_place, spouse_name, children, father_name, mother_name, notes, active, date_reg, compman_id, company_id, amt_paid, rank_id, vessel_officer, dept, vessel_id, course_id, school_id, school_admin, type, batch_no, pct_done, days_on_board, photo_file, passport_no, cdc_no, indos_no, height, weight, enrtry_date, marks, blood_group, emergency_contact_name, emergency_contact_address, emergency_contact_no, emergency_relationship, nationality, passport_no_issue_date, cdc_no_issue_date, cdc_no_issue_place, full_name, "N", "N");
                        Log.d("RESULT", "INSERT INTO(id, person_id, code_person, lname, fname, mname, st_address, city_id, province_id, phone, mobile, st_address_province, phone_province, email, gender, civ_status, birth_date, birth_place, spouse_name, children, father_name, mother_name, notes, active, date_reg, compman_id, company_id, amt_paid, rank_id, vessel_officer, dept, vessel_id, course_id, school_id, school_admin, type, batch_no, pct_done, days_on_board, logged_in, last_login, full_name, photo_file, w_fr) " +
                                "VALUES ("+int_id+", "+person_id+", "+ code_person+", "+lname+", "+ fname+", "+mname+", "+ st_address+", "+city_id+", "+province_id+", "+phone+", "+mobile+", "+st_address_province+", "+phone_province+", "+email+", "+gender+", "+civ_status+", "+birth_date+", "+birth_place+", "+spouse_name+", 0, "+father_name+", "+mother_name+", "+notes+", "+active+", "+date_reg+", "+compman_id+", "+company_id+", 0, "+rank_id+", "+vessel_officer+", "+dept+", "+vessel_id+", "+course_id+", "+school_id+", "+school_admin+", "+type+", "+batch_no+", "+pct_done+", "+days_on_board+", '', "+last_login+", "+full_name+", "+photo_file+", 'Y')");
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
            }else{
                success = false;
                err_message = getString(R.string.probleConn);
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if(success){
                tv_message.setTextColor(getColor(R.color.black));
                tv_message.setText("Downloading from server. Please wait .....");
                new DownloadOfficers(context).execute();
            }else{
                tv_message.setTextColor(getColor(R.color.red));
                tv_message.setText(err_message + " Make sure you provided a valid username and password.");
                progressBar.setVisibility(View.GONE);
            }
            tv_message.setVisibility(View.VISIBLE);

        }
    }

    //******** DOWNLOAD OFFICERS ********
    private class DownloadOfficers extends AsyncTask<Void, Void, Void> {
        public Context context;
        public DownloadOfficers(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=officer&company_id=" + URLEncoder.encode(company_id));
            Log.d("CONNECT", url +"connection.php?login_name="+login_name+"&login_pass=" + login_pass+ "&table=officer&company_id=" + URLEncoder.encode(company_id));

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

            if(!str.equals("[]") && !str.equals("")){
                success = true;
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
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

                        Integer int_id = db.newIntegerId("person");
                        int cnt = db.GetCount("person", " WHERE person_id = '"+id+"'");
                        if(cnt == 0){
                            db.addPerson(int_id, id, code_person, lname, fname, mname, st_address, city_id, province_id, phone, mobile, st_address_province, phone_province, email, gender, civ_status, birth_date, birth_place, spouse_name, children, father_name, mother_name, notes, active, date_reg, compman_id, company_id, amt_paid, rank_id, vessel_officer, dept, vessel_id, course_id, school_id, school_admin, type, batch_no, pct_done, days_on_board, photo_file, passport_no, cdc_no, indos_no, height, weight, enrtry_date, marks, blood_group, emergency_contact_name, emergency_contact_address, emergency_contact_no, emergency_relationship, nationality, passport_no_issue_date, cdc_no_issue_date, cdc_no_issue_place, full_name, "N", "N", "", "", "", "", "","","", "", "", "");
                            Log.d("RESULT OFFICER", "INSERT INTO(id, person_id, code_person, lname, fname, mname, st_address, city_id, province_id, phone, mobile, st_address_province, phone_province, email, gender, civ_status, birth_date, birth_place, spouse_name, children, father_name, mother_name, notes, active, date_reg, compman_id, company_id, amt_paid, rank_id, vessel_officer, dept, vessel_id, course_id, school_id, school_admin, type, batch_no, pct_done, days_on_board, logged_in, last_login, full_name, photo_file, w_fr) " +
                                    "VALUES ("+int_id+", "+id+", "+ code_person+", "+lname+", "+ fname+", "+mname+", "+ st_address+", "+city_id+", "+province_id+", "+phone+", "+mobile+", "+st_address_province+", "+phone_province+", "+email+", "+gender+", "+civ_status+", "+birth_date+", "+birth_place+", "+spouse_name+", 0, "+father_name+", "+mother_name+", "+notes+", "+active+", "+date_reg+", "+compman_id+", "+company_id+", 0, "+rank_id+", "+vessel_officer+", "+dept+", "+vessel_id+", "+course_id+", "+school_id+", "+school_admin+", "+type+", "+batch_no+", "+pct_done+", "+days_on_board+", '', "+last_login+", "+full_name+", "+photo_file+", 'Y')");
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
                //new DownloadFromServerActivity.DownloadOtherLogin(context).execute();
                tv_message.setText("Success");
                progressBar.setVisibility(View.GONE);
            }else{
                new DownloadOfficers(context).execute();
            }
        }
    }

    void downloadFile(String filename, String table){
        try {
            URL url = null;
            if(table.equals("person_task_file")){
                url = new URL(dwnload_file_path + filename);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }
}