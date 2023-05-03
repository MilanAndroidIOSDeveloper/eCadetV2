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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class OfficerAssignmentFormActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;

    String person_id, person_officer_id = "", officer_id, assessor_id, master_id, chief_eng_id;
    String company_id, school_id;
    List<String> personArray =  new ArrayList<>();
    List<String> assessorArray =  new ArrayList<>();
    List<String> masterArray =  new ArrayList<>();
    List<String> ceArray =  new ArrayList<>();
    List<Person> persons;
    Spinner spinner, spinner_assesor, spinner_master, spinner_ce;
    TextInputLayout et_from_date, et_to_date;
    Button btn_cancel, btn_delete, btn_save;

    String str = "", err_message, dept;
    HttpResponse response;
    JSONObject json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_assignment_form);

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
        dept = db.getFieldString("dept", " person_id ='"+person_id+"'", "person");
        company_id = db.getFieldString("company_id", " person_id ='"+person_id+"'", "person");
        school_id = db.getFieldString("school_id", " person_id ='"+person_id+"'", "person");

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
        if (intent.hasExtra("person_officer_id")) {
            person_officer_id = intent.getStringExtra("person_officer_id");
        }

        persons = db.getPersons(" vessel_officer = 'Y' AND school_id = '"+school_id+"'"); //POPULATE VESSEL SPINNER
        personArray.add("Select Onboard Training Supervisor *");
        for (Person row : persons) {
            personArray.add(row.getFull_name());
        }
        persons = db.getPersons(" vessel_officer = 'Y' AND company_id = '"+company_id+"'"); //POPULATE VESSEL SPINNER
        assessorArray.add("Select Shipboard Training Officer *");
        for (Person row : persons) {
            assessorArray.add(row.getFull_name());
        }
        persons = db.getPersons(" vessel_officer = 'Y' AND rank_id = 'Master Mariner'"); //POPULATE VESSEL SPINNER
        masterArray.add("Select Captain/Master");
        for (Person row : persons) {
            masterArray.add(row.getFull_name());
        }
        persons = db.getPersons(" vessel_officer = 'Y' AND rank_id = 'Chief Engineer'"); //POPULATE VESSEL SPINNER
        ceArray.add("Select Chief Engineer");
        for (Person row : persons) {
            ceArray.add(row.getFull_name());
        }


        ArrayAdapter arrayAdapter_officer = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, personArray);
        arrayAdapter_officer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter_officer);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                officer_id  = parent.getItemAtPosition(position).toString();
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(18);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter arrayAdapter_assessor = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, assessorArray);
        arrayAdapter_assessor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_assesor = findViewById(R.id.spinner_assesor);
        spinner_assesor.setAdapter(arrayAdapter_assessor);
        spinner_assesor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                assessor_id  = parent.getItemAtPosition(position).toString();
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(15);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter arrayAdapter_master = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, masterArray);
        arrayAdapter_master.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_master = findViewById(R.id.spinner_master);
        spinner_master.setAdapter(arrayAdapter_master);
        spinner_master.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                master_id  = parent.getItemAtPosition(position).toString();
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(15);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter arrayAdapter_ce = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, ceArray);
        arrayAdapter_ce.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_ce = findViewById(R.id.spinner_ce);
        spinner_ce.setAdapter(arrayAdapter_ce);
        spinner_ce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chief_eng_id  = parent.getItemAtPosition(position).toString();
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(15);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_from_date = findViewById(R.id.et_from_date);
        et_to_date = findViewById(R.id.et_to_date);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_delete = findViewById(R.id.btn_delete);
        btn_save = findViewById(R.id.btn_save);

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker mdp_on = materialDateBuilder.build();
        final MaterialDatePicker mdp_off = materialDateBuilder.build();

        et_from_date.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp_on.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        et_from_date.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp_on.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        et_to_date.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp_off.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        et_to_date.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp_off.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        mdp_on.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        String dateSelected = "" + mdp_on.getHeaderText();
                        if(dateSelected.equals("Selected Date")){
                            et_from_date.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");
                            if(day.length() == 1){
                                day = "0"+ day;
                            }
                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_from_date.getEditText().setText(completed);
                        }
                    }
                });

        mdp_off.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        String dateSelected = "" + mdp_off.getHeaderText();
                        if(dateSelected.equals("Selected Date")){
                            et_to_date.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");
                            if(day.length() == 1){
                                day = "0"+ day;
                            }
                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_to_date.getEditText().setText(completed);
                        }
                    }
                });

        if(person_officer_id.equals("")){ //ADD
            btn_delete.setVisibility(View.GONE);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String from_date = et_from_date.getEditText().getText().toString();
                    String to_date = et_to_date.getEditText().getText().toString();

                    if(officer_id.equals("Select Onboard Training Supervisor *")){
                        Toast.makeText(OfficerAssignmentFormActivity.this, "Please select an on board training supervisor.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(assessor_id.equals("Select Shipboard Training Officer *")){
                        Toast.makeText(OfficerAssignmentFormActivity.this, "Please select an assessor.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(from_date.equals("")){
                        Toast.makeText(OfficerAssignmentFormActivity.this, "Please select from date.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String person_officer_id = db.newId();
                    Integer id = db.newIntegerId("person_officer");
                    String vessel_id = db.getFieldString("vessel_id", " person_id = '"+person_id+"' ORDER BY sign_on DESC LIMIT 1", "shipboard");
                    String officer_id_ = db.getFieldString("person_id", " full_name = '"+officer_id+"'", "person");
                    String assessor_id_ = db.getFieldString("person_id", " full_name = '"+assessor_id+"'", "person");
                    String master_id_ = db.getFieldString("person_id", " full_name = '"+master_id+"'", "person");
                    String chief_eng_id_ = db.getFieldString("person_id", " full_name = '"+chief_eng_id+"'", "person");

                    //check
                    int chk = db.GetCount("person_officer", " WHERE officer_id = '"+officer_id_+"' AND vessel_id = '"+vessel_id+"'");
                    if(chk > 0){
                        Toast.makeText(OfficerAssignmentFormActivity.this, "Record with the same onboard training supervisor and vessel already exists", Toast.LENGTH_LONG).show();
                        return;
                    }else{
                        pd = new ProgressDialog(OfficerAssignmentFormActivity.this);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage("Processing. Please wait .... ");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();

                        db.query("INSERT INTO person_officer (id, person_officer_id, person_id, officer_id, from_date, to_date, comp_officer_ok, vessel_id, last_update, assessor_id, master_id, chief_eng_id) VALUES ("+id+", '"+person_officer_id+"', '"+person_id+"', '"+officer_id_+"', '"+from_date+"', '"+to_date+"', '', '"+vessel_id+"', '', '"+assessor_id_+"', '"+master_id_+"', '"+chief_eng_id_+"')");
                        int conn = getConnection.getConnectionType(OfficerAssignmentFormActivity.this);
                        if(conn != 0){ //WITH NET
                            new SyncOnline(context, person_officer_id, "ADD").execute();
                        }else{
                            Integer backup_item_id = db.newIntegerId("backup_item");
                            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_officer', '" + person_officer_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                            pd.dismiss();

                            Intent intent = new Intent(OfficerAssignmentFormActivity.this, OfficerAssignmentActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(OfficerAssignmentFormActivity.this, "Record successfully added.", Toast.LENGTH_LONG).show();
                        }


                    }

                }
            });
        }else{ //UPDATE
            btn_delete.setVisibility(View.VISIBLE);

            String saved_officer_id = db.getFieldString("officer_id","person_officer_id = '"+person_officer_id+"'","person_officer");
            String saved_officer = db.getFieldString("full_name","person_id ='"+saved_officer_id+"'","person");
            String saved_assessor_id = db.getFieldString("assessor_id","person_officer_id = '"+person_officer_id+"'","person_officer");
            String saved_assessor = db.getFieldString("full_name","person_id ='"+saved_assessor_id+"'","person");
            String saved_from_date = db.getFieldString("from_date","person_officer_id = '"+person_officer_id+"'","person_officer");
            String saved_to_date = db.getFieldString("to_date","person_officer_id = '"+person_officer_id+"'","person_officer");
            String saved_master_id = db.getFieldString("master_id","person_officer_id = '"+person_officer_id+"'","person_officer");
            String saved_master = db.getFieldString("full_name","person_id ='"+saved_master_id+"'","person");
            String saved_chief_eng_id = db.getFieldString("person_id", " full_name = '"+chief_eng_id+"'", "person");

            spinner.setSelection(arrayAdapter_officer.getPosition(saved_officer));
            spinner_assesor.setSelection(arrayAdapter_assessor.getPosition(saved_assessor));
            spinner_master.setSelection(arrayAdapter_assessor.getPosition(saved_master));

            et_from_date.getEditText().setText(saved_from_date);
            et_to_date.getEditText().setText(saved_to_date);


            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String from_date = et_from_date.getEditText().getText().toString();
                    String to_date = et_to_date.getEditText().getText().toString();

                    if(officer_id.equals("Select Onboard Training Supervisor *")){
                        Toast.makeText(OfficerAssignmentFormActivity.this, "Please select an on board training supervisor.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(assessor_id.equals("Select Shipboard Training Officer *")){
                        Toast.makeText(OfficerAssignmentFormActivity.this, "Please select an assessor.", Toast.LENGTH_LONG).show();
                        return;
                    }


                    if(from_date.equals("")){
                        Toast.makeText(OfficerAssignmentFormActivity.this, "Please select from date.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    //String vessel_id = db.getFieldString("vessel_id", " person_id = '"+person_id+"' ORDER BY sign_on DESC LIMIT 1", "shipboard");
                    String officer_id_ = db.getFieldString("person_id", " full_name = '"+officer_id+"'", "person");
                    String assessor_id_ = db.getFieldString("person_id", " full_name = '"+assessor_id+"'", "person");
                    String master_id_ = db.getFieldString("person_id", " full_name = '"+master_id+"'", "person");
                    String chief_eng_id_ = db.getFieldString("person_id", " full_name = '"+chief_eng_id+"'", "person");

                    pd = new ProgressDialog(OfficerAssignmentFormActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();

                    db.query("UPDATE person_officer SET officer_id = '"+officer_id_+"', assessor_id = '"+assessor_id_+"', from_date ='"+from_date+"', to_date = '"+to_date+"', master_id = '"+master_id_+"', chief_eng_id= '"+chief_eng_id_+"' WHERE person_officer_id = '"+person_officer_id+"'");

                    int conn = getConnection.getConnectionType(OfficerAssignmentFormActivity.this);
                    if(conn != 0){ //WITH NET
                        new SyncOnline(context, person_officer_id, "UPDATE").execute();
                    }else{
                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_officer', '" + person_officer_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");
                        pd.dismiss();

                        Intent intent = new Intent(OfficerAssignmentFormActivity.this, OfficerAssignmentActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(OfficerAssignmentFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();
                    }


                }
            });

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OfficerAssignmentFormActivity.this);

                    alertDialogBuilder.setMessage("Are you sure you want to delete this record?");
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            delete(person_officer_id);
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
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(OfficerAssignmentFormActivity.this, R.color.white));
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(OfficerAssignmentFormActivity.this, R.color.black));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(OfficerAssignmentFormActivity.this, R.color.white));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(OfficerAssignmentFormActivity.this, R.color.black));

                }
            });
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OfficerAssignmentFormActivity.this);

                alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved.");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(OfficerAssignmentFormActivity.this, OfficerAssignmentActivity.class);
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
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(OfficerAssignmentFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(OfficerAssignmentFormActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(OfficerAssignmentFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(OfficerAssignmentFormActivity.this, R.color.black));
            }
        });

    }

    public void delete(String person_officer_id){
        pd = new ProgressDialog(OfficerAssignmentFormActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Processing. Please wait .... ");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        db.query("DELETE FROM person_officer WHERE person_officer_id = '"+person_officer_id+"'");

        int conn = getConnection.getConnectionType(OfficerAssignmentFormActivity.this);
        if(conn != 0){
            new SyncOnlineDelete(context,  "person_officer", person_officer_id).execute();
        }else {
            Integer backup_item_id = db.newIntegerId("backup_item");
            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_officer', '" + person_officer_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'DELETE', 'N')");
            pd.dismiss();

            Intent intent = new Intent(OfficerAssignmentFormActivity.this, OfficerAssignmentActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(OfficerAssignmentFormActivity.this, "Record successfully deleted.", Toast.LENGTH_LONG).show();

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
            String saved_person_id = db.getFieldString("person_id", "person_officer_id = '" + id + "'", "person_officer");
            String saved_officer_id = db.getFieldString("officer_id", "person_officer_id = '" + id + "'", "person_officer");
            String saved_last_update = db.getFieldString("last_update", "person_officer_id = '" + id + "'", "person_officer");
            saved_last_update = URLEncoder.encode(saved_last_update);
            String saved_from_date = db.getFieldString("from_date", "person_officer_id = '" + id + "'", "person_officer");
            String saved_to_date = db.getFieldString("to_date", "person_officer_id = '" + id + "'", "person_officer");
            String saved_comp_officer_ok = db.getFieldString("comp_officer_ok", "person_officer_id = '" + id + "'", "person_officer");
            String saved_vessel_id = db.getFieldString("vessel_id", "person_officer_id = '" + id + "'", "person_officer");
            String saved_assessor_id = db.getFieldString("assessor_id", "person_officer_id = '" + id + "'", "person_officer");
            String saved_master_id = db.getFieldString("master_id", "person_officer_id = '" + id + "'", "person_officer");

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_officer&id="+id+"&person_id="+saved_person_id+"&officer_id="+saved_officer_id+"&last_update="+saved_last_update+"&from_date="+saved_from_date+"&to_date="+saved_to_date+"&comp_officer_ok="+saved_comp_officer_ok+"&vessel_id="+saved_vessel_id+"&assessor_id="+saved_assessor_id+"&master_id="+saved_master_id+"&event="+event);
            Log.d("CONNECT", url +"sync.php?table=person_officer&id="+id+"&person_id="+saved_person_id+"&officer_id="+saved_officer_id+"&last_update="+saved_last_update+"&from_date="+saved_from_date+"&to_date="+saved_to_date+"&comp_officer_ok="+saved_comp_officer_ok+"&vessel_id="+saved_vessel_id+"&assessor_id="+saved_assessor_id+"&master_id="+saved_master_id+"&event="+event);

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
            Intent intent = new Intent(OfficerAssignmentFormActivity.this, OfficerAssignmentActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(OfficerAssignmentFormActivity.this, "Record saved successfully.", Toast.LENGTH_LONG).show();

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
            Intent intent = new Intent(OfficerAssignmentFormActivity.this, OfficerAssignmentActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(OfficerAssignmentFormActivity.this, "Record deleted successfully.", Toast.LENGTH_LONG).show();

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

        alertDialogBuilder.setMessage("Are you sure you want to leave without saving?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(OfficerAssignmentFormActivity.this, OfficerAssignmentActivity.class);
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
