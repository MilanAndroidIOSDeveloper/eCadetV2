package com.elosoftbiz.etrb_trmf;

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
import java.util.ArrayList;
import java.util.List;

public class MusterFormActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title;
    LinearLayout approval_container;
    ProgressDialog pd;
    DatabaseHelper db;

    String person_id, person_muster_id = "", vessel_id, safety_officer_id, security_officer_id;
    TextInputLayout et_lifeboat_station, et_lifeboat_duties, et_emergency_station, et_emergency_duties, et_oil_spill_duties;
    TextView tv_master_id, tv_date_checked;
    Spinner spinner_safety, spinner_security;
    List<String> safetyArr =  new ArrayList<>();
    List<String> securityArr =  new ArrayList<>();
    Button btn_cancel, btn_delete, btn_save;

    String str = "", err_message, photo_file;
    HttpResponse response;
    JSONObject json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muster_form);

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
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        new MenuDeck(navigationView, this, mDrawerLayout);
        /****** END MENU *******/

        db = new DatabaseHelper(this);
        person_id = db.getCadetId();


        Intent intent = getIntent();
        if (intent.hasExtra("person_muster_id")) {
            person_muster_id = intent.getStringExtra("person_muster_id");
        }

        et_lifeboat_station = findViewById(R.id.et_lifeboat_station);
        et_lifeboat_duties = findViewById(R.id.et_lifeboat_duties);
        et_emergency_station = findViewById(R.id.et_emergency_station);
        et_emergency_duties = findViewById(R.id.et_emergency_duties);
        et_oil_spill_duties = findViewById(R.id.et_oil_spill_duties);
        spinner_safety = findViewById(R.id.spinner_safety);
        spinner_security = findViewById(R.id.spinner_security);
        btn_save = findViewById(R.id.btn_save);
        btn_delete = findViewById(R.id.btn_delete);
        btn_cancel = findViewById(R.id.btn_cancel);
        approval_container = findViewById(R.id.approval_container);


        safetyArr.add("Select Safety Officer");
        List<Person> safetyOfficer = db.getPersons("vessel_officer = 'Y'");
        for(Person Safety : safetyOfficer){
            safetyArr.add(Safety.getFull_name());
        }

        securityArr.add("Select Security Officer");
        List<Person> securityOfficer = db.getPersons("vessel_officer = 'Y'");
        for(Person Security : securityOfficer){
            securityArr.add(Security.getFull_name());
        }

        ArrayAdapter arrayAdapterSafe = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, safetyArr);
        arrayAdapterSafe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_safety.setAdapter(arrayAdapterSafe);
        spinner_safety.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                safety_officer_id  = parent.getItemAtPosition(position).toString();
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(15);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter arrayAdapterSecurity = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, securityArr);
        arrayAdapterSecurity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_security.setAdapter(arrayAdapterSecurity);
        spinner_security.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                security_officer_id  = parent.getItemAtPosition(position).toString();
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(15);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(person_muster_id.equals("")){ //ADD
            btn_delete.setVisibility(View.GONE);
            approval_container.setVisibility(View.GONE);

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vessel_id = db.getFieldString("vessel_id", " person_id = '"+person_id+"' ORDER BY sign_on DESC LIMIT 1", "shipboard");
                    int cnt = db.GetCount("person_muster", " WHERE person_id = '"+person_id+"' AND vessel_id = '"+vessel_id+"'");
                    if(cnt > 0){
                        Toast.makeText(MusterFormActivity.this, "An existing record with your current vessel has been detected. Only one record per vessel is allowed", Toast.LENGTH_LONG).show();
                        return;
                    }else{
                        String lifeboat_station = et_lifeboat_station.getEditText().getText().toString();
                        String lifeboat_duties = et_lifeboat_duties.getEditText().getText().toString();
                        String emergency_station = et_emergency_station.getEditText().getText().toString();
                        String emergency_duties = et_emergency_duties.getEditText().getText().toString();
                        String oil_spill_duties = et_oil_spill_duties.getEditText().getText().toString();

                        if(lifeboat_station.equals("")){
                            Toast.makeText(MusterFormActivity.this, "Lifeboat Muster Station is required.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(emergency_station.equals("")){
                            Toast.makeText(MusterFormActivity.this, "Emergency Muster Station is required.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        pd = new ProgressDialog(MusterFormActivity.this);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage("Processing. Please wait .... ");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();

                        String safety_officer_id_ = db.getFieldString("person_id", " full_name = '"+safety_officer_id+"'", "person");
                        String security_officer_id_ = db.getFieldString("person_id", " full_name = '"+security_officer_id+"'", "person");
                        Integer id = db.newIntegerId("person_muster");
                        String person_muster_id = db.newId();
                        db.execQuery("INSERT INTO person_muster (id, person_muster_id, person_id, vessel_id, lifeboat_station, lifeboat_duties, emergency_station, emergency_duties, oil_spill_duties, safety_officer_id, security_officer_id, master_id, date_checked) VALUES ('"+id+"', '"+person_muster_id+"', '"+person_id+"', '"+vessel_id+"', '"+lifeboat_station+"', '"+lifeboat_duties+"', '"+emergency_station+"', '"+emergency_duties+"', '"+oil_spill_duties+"', '"+safety_officer_id_+"', '"+security_officer_id_+"', '', '')");
                        Log.d("QUERY", "INSERT INTO person_muster (id, person_muster_id, person_id, vessel_id, lifeboat_station, lifeboat_duties, emergency_station, emergency_duties, oil_spill_duties, safety_officer_id, security_officer_id, master_id, date_checked) VALUES ('"+id+"', '"+person_muster_id+"', '"+person_id+"', '"+vessel_id+"', '"+lifeboat_station+"', '"+lifeboat_duties+"', '"+emergency_station+"', '"+emergency_duties+"', '"+oil_spill_duties+"', '"+safety_officer_id_+"', '"+security_officer_id_+"', '', '')");

                        int conn = getConnection.getConnectionType(MusterFormActivity.this);
                        if(conn != 0){ //WITH NET
                            new SyncOnline(context, person_muster_id, "ADD").execute();
                        }else {
                            Integer backup_item_id = db.newIntegerId("backup_item");
                            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_muster', '" + person_muster_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                            pd.dismiss();

                            Intent intent1 = new Intent(MusterFormActivity.this, MusterActivity.class);
                            startActivity(intent1);
                            finish();
                            Toast.makeText(MusterFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();

                        }

                    }
                }
            });
        }else{ //UPDATE


            String saved_lifeboat_station = db.getFieldString("lifeboat_station", "person_muster_id ='"+person_muster_id+"'", "person_muster");
            String saved_lifeboat_duties = db.getFieldString("lifeboat_duties", "person_muster_id ='"+person_muster_id+"'", "person_muster");
            String saved_emergency_station = db.getFieldString("emergency_station", "person_muster_id ='"+person_muster_id+"'", "person_muster");
            String saved_emergency_duties = db.getFieldString("emergency_duties", "person_muster_id ='"+person_muster_id+"'", "person_muster");
            String saved_oil_spill_duties = db.getFieldString("oil_spill_duties", "person_muster_id ='"+person_muster_id+"'", "person_muster");
            String saved_safety_officer_id = db.getFieldString("safety_officer_id", "person_muster_id ='"+person_muster_id+"'", "person_muster");
            String saved_safety_officer_id_ = db.getFieldString("full_name", "person_id ='"+saved_safety_officer_id+"'", "person");
            String saved_security_officer_id = db.getFieldString("security_officer_id", "person_muster_id ='"+person_muster_id+"'", "person_muster");
            String saved_security_officer_id_ = db.getFieldString("full_name", "person_id ='"+saved_security_officer_id+"'", "person");
            String saved_master_id = db.getFieldString("master_id", "person_muster_id ='"+person_muster_id+"'", "person_muster");
            String saved_date_checked = db.getFieldString("date_checked", "person_muster_id ='"+person_muster_id+"'", "person_muster");

            et_lifeboat_station.getEditText().setText(saved_lifeboat_station);
            et_lifeboat_duties.getEditText().setText(saved_lifeboat_duties);
            et_emergency_station.getEditText().setText(saved_emergency_station);
            et_emergency_duties.getEditText().setText(saved_emergency_duties);
            et_oil_spill_duties.getEditText().setText(saved_oil_spill_duties);
            spinner_safety.setSelection(arrayAdapterSafe.getPosition(saved_safety_officer_id_));
            spinner_security.setSelection(arrayAdapterSecurity.getPosition(saved_security_officer_id_));

            if(saved_master_id.equals("")){
                btn_delete.setVisibility(View.VISIBLE);
                btn_save.setVisibility(View.VISIBLE);
                approval_container.setVisibility(View.GONE);

                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String lifeboat_station = et_lifeboat_station.getEditText().getText().toString();
                        String lifeboat_duties = et_lifeboat_duties.getEditText().getText().toString();
                        String emergency_station = et_emergency_station.getEditText().getText().toString();
                        String emergency_duties = et_emergency_duties.getEditText().getText().toString();
                        String oil_spill_duties = et_oil_spill_duties.getEditText().getText().toString();

                        if(lifeboat_station.equals("")){
                            Toast.makeText(MusterFormActivity.this, "Lifeboat Muster Station is required.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(emergency_station.equals("")){
                            Toast.makeText(MusterFormActivity.this, "Emergency Muster Station is required.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        pd = new ProgressDialog(MusterFormActivity.this);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage("Processing. Please wait .... ");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();

                        String safety_officer_id_ = db.getFieldString("person_id", " full_name = '"+safety_officer_id+"'", "person");
                        String security_officer_id_ = db.getFieldString("person_id", " full_name = '"+security_officer_id+"'", "person");

                        db.execQuery("UPDATE person_muster SET lifeboat_station = '"+lifeboat_station+"', lifeboat_duties = '"+lifeboat_duties+"', emergency_station = '"+emergency_station+"', emergency_duties = '"+emergency_duties+"', oil_spill_duties = '"+oil_spill_duties+"', safety_officer_id= '"+safety_officer_id_+"', security_officer_id = '"+security_officer_id_+"' WHERE person_muster_id = '"+person_muster_id+"'");
                        Log.d("QUERY", "UPDATE person_muster SET lifeboat_station = '"+lifeboat_station+"', lifeboat_duties = '"+lifeboat_duties+"', emergency_station = '"+emergency_station+"', emergency_duties = '"+emergency_duties+"', oil_spill_duties = '"+oil_spill_duties+"', safety_officer_id= '"+safety_officer_id_+"', security_officer_id = '"+security_officer_id_+"' WHERE person_muster_id = '"+person_muster_id+"'");
                        int conn = getConnection.getConnectionType(MusterFormActivity.this);
                        if(conn != 0 ){
                            new SyncOnline(context, person_muster_id, "UPDATE").execute();
                        }else{
                            Integer backup_item_id = db.newIntegerId("backup_item");
                            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_muster', '" + person_muster_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");
                            pd.dismiss();

                            Intent intent1 = new Intent(MusterFormActivity.this, MusterActivity.class);
                            startActivity(intent1);
                            finish();
                            Toast.makeText(MusterFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();
                        }

                    }
                });

                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MusterFormActivity.this);

                        alertDialogBuilder.setMessage("Are you sure you want to delete this record?");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                delete(person_muster_id);
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
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(MusterFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MusterFormActivity.this, R.color.black));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(MusterFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MusterFormActivity.this, R.color.black));

                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MusterFormActivity.this);

                        alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved.");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(MusterFormActivity.this, EducationalRecordActivity.class);
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
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(MusterFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MusterFormActivity.this, R.color.black));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(MusterFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MusterFormActivity.this, R.color.black));
                    }
                });

            }else{
                btn_delete.setVisibility(View.GONE);
                btn_save.setVisibility(View.GONE);
                btn_cancel.setText("Back");
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MusterFormActivity.this, EducationalRecordActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                tv_master_id = findViewById(R.id.tv_master_id);
                tv_date_checked = findViewById(R.id.tv_date_checked);

                String name_master = db.getFieldString("full_name"," person_id = '"+saved_master_id+"'","person");
                tv_master_id.setText("Master's Name : " + name_master);
                tv_date_checked.setText("Date Checked : " + saved_date_checked);
                approval_container.setVisibility(View.VISIBLE);

            }


        }
    }

    public void delete(String person_muster_id){
        pd = new ProgressDialog(MusterFormActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Processing. Please wait .... ");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        db.query("DELETE FROM person_muster WHERE person_muster_id = '"+person_muster_id+"'");
        int conn = getConnection.getConnectionType(MusterFormActivity.this);
        if(conn != 0){
            new SyncOnlineDelete(context, "person_muster", person_muster_id).execute();
        }else{
            Integer backup_item_id = db.newIntegerId("backup_item");
            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_muster', '" + person_muster_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'DELETE', 'N')");
            pd.dismiss();

            Intent intent = new Intent(MusterFormActivity.this, MusterActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(MusterFormActivity.this, "Record successfully deleted.", Toast.LENGTH_LONG).show();
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
            String saved_person_id = db.getFieldString("person_id", "person_muster_id = '" + id + "'", "person_muster");
            String saved_vessel_id = db.getFieldString("vessel_id", "person_muster_id = '" + id + "'", "person_muster");
            String saved_lifeboat_station = db.getFieldString("lifeboat_station", "person_muster_id = '" + id + "'", "person_muster");
            saved_lifeboat_station = URLEncoder.encode(saved_lifeboat_station);
            String saved_lifeboat_duties = db.getFieldString("lifeboat_duties", "person_muster_id = '" + id + "'", "person_muster");
            saved_lifeboat_duties = URLEncoder.encode(saved_lifeboat_duties);
            String saved_emergency_station = db.getFieldString("emergency_station", "person_muster_id = '" + id + "'", "person_muster");
            saved_emergency_station = URLEncoder.encode(saved_emergency_station);
            String saved_emergency_duties = db.getFieldString("emergency_duties", "person_muster_id = '" + id + "'", "person_muster");
            saved_emergency_duties = URLEncoder.encode(saved_emergency_duties);
            String saved_oil_spill_duties = db.getFieldString("oil_spill_duties", "person_muster_id = '" + id + "'", "person_muster");
            saved_oil_spill_duties = URLEncoder.encode(saved_oil_spill_duties);
            String saved_safety_officer_id = db.getFieldString("safety_officer_id", "person_muster_id = '" + id + "'", "person_muster");
            String saved_security_officer_id = db.getFieldString("security_officer_id", "person_muster_id = '" + id + "'", "person_muster");
            String saved_master_id = db.getFieldString("master_id", "person_muster_id = '" + id + "'", "person_muster");
            String saved_date_checked = db.getFieldString("date_checked", "person_muster_id = '" + id + "'", "person_muster");

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_muster&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&lifeboat_station="+saved_lifeboat_station+"&lifeboat_duties="+saved_lifeboat_duties+"&emergency_station="+saved_emergency_station+"&emergency_duties="+saved_emergency_duties+"&oil_spill_duties="+saved_oil_spill_duties+"&safety_officer_id="+saved_safety_officer_id+"&security_officer_id="+saved_security_officer_id+"&master_id="+saved_master_id+"&date_checked="+saved_date_checked+"&event="+event);
            Log.d("CONNECT", url + "sync.php?table=person_muster&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&lifeboat_station="+saved_lifeboat_station+"&lifeboat_duties="+saved_lifeboat_duties+"&emergency_station="+saved_emergency_station+"&emergency_duties="+saved_emergency_duties+"&oil_spill_duties="+saved_oil_spill_duties+"&safety_officer_id="+saved_safety_officer_id+"&security_officer_id="+saved_security_officer_id+"&master_id="+saved_master_id+"&date_checked="+saved_date_checked+"&event="+event);

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
            Intent intent = new Intent(MusterFormActivity.this, MusterActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(MusterFormActivity.this, "Record saved successfully.", Toast.LENGTH_LONG).show();

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
           Intent intent = new Intent(MusterFormActivity.this, MusterActivity.class);
           startActivity(intent);
           finish();
           Toast.makeText(MusterFormActivity.this, "Record deleted successfully.", Toast.LENGTH_LONG).show();
        }
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
                Intent intent = new Intent(MusterFormActivity.this, MusterActivity.class);
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
