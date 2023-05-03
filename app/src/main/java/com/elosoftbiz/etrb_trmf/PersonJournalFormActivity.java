package com.elosoftbiz.etrb_trmf;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
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
import java.net.URLEncoder;
import java.util.Calendar;

public class PersonJournalFormActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;

    TextView tv_title;
    ProgressDialog pd;
    DatabaseHelper db;
    String person_id, person_journal_id = "", dept = "";
    LinearLayout approval_container;
    Button btn_cancel, btn_delete, btn_save;
    String str = "", err_message, photo_file;
    HttpResponse response;
    JSONObject json = null;

    TextInputLayout et_journal_date, et_journal_time, et_ship_position, et_fixing_method, et_course_speed;
    TextInputLayout et_fo_do, et_average_rpm, et_average_speed, et_activities;
    TimePickerDialog timePickerDialog;
    int Hour, Minute;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_journal_form);

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
        dept = db.getFieldString("dept", " person_id ='"+person_id+"'", "person");
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

        //GET ELEMENTS
        et_journal_date = findViewById(R.id.et_journal_date);
        et_journal_time = findViewById(R.id.et_journal_time);
        et_ship_position = findViewById(R.id.et_ship_position);
        et_fixing_method = findViewById(R.id.et_fixing_method);
        et_course_speed = findViewById(R.id.et_course_speed);
        et_fo_do = findViewById(R.id.et_fo_do);
        et_average_rpm = findViewById(R.id.et_average_rpm);
        et_average_speed = findViewById(R.id.et_average_speed);
        et_activities = findViewById(R.id.et_activities);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_delete = findViewById(R.id.btn_delete);
        btn_save = findViewById(R.id.btn_save);
        approval_container = findViewById(R.id.approval_container);

        if(dept.equals("DECK")){
            et_fo_do.setVisibility(View.GONE);
            et_average_rpm.setVisibility(View.GONE);
            et_average_speed.setVisibility(View.GONE);
        }else{
            et_ship_position.setVisibility(View.GONE);
            et_fixing_method.setVisibility(View.GONE);
            et_course_speed.setVisibility(View.GONE);
        }

        final MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker mdp = materialDateBuilder.build();
        et_journal_date.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        et_journal_date.getEditText().setOnClickListener(new View.OnClickListener() {
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
                            et_journal_date.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");
                            if(day.length() == 1){
                                day = "0"+ day;
                            }
                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_journal_date.getEditText().setText(completed);
                        }
                    }
                });

        calendar = Calendar.getInstance();
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minute = calendar.get(Calendar.MINUTE);

        et_journal_time.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(PersonJournalFormActivity.this, null,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
               // timePickerDialog.setThemeDark(false);
                //timePickerDialog.showYearPickerFirst(false);
                timePickerDialog.setTitle("Select Time");

                timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                    }
                });
                //timePickerDialog.setAccentColor(Color.parseColor("#86b9cf"));
                timePickerDialog.show();
            }
        });
        et_journal_time.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(PersonJournalFormActivity.this, null,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                //timePickerDialog.setThemeDark(false);
                //timePickerDialog.showYearPickerFirst(false);
                timePickerDialog.setTitle("Select Time");

                timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                    }
                });
                //timePickerDialog.setAccentColor(Color.parseColor("#86b9cf"));
                timePickerDialog.show();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("person_journal_id")) {
            person_journal_id = intent.getStringExtra("person_journal_id");
        }

       // Toast.makeText(PersonJournalFormActivity.this, "" + person_journal_id, Toast.LENGTH_LONG).show();

        if(person_journal_id.equals("")){ //ADD
            approval_container.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String vessel_id = db.getFieldString("vessel_id", " person_id ='"+person_id+"' ORDER BY sign_on DESC LIMIT 1", "shipboard");
                    String journal_date = et_journal_date.getEditText().getText().toString();
                    String journal_time = et_journal_time.getEditText().getText().toString();
                    String ship_position = "";
                    String fixing_method = "";
                    String course_speed = "";
                    String activities = et_activities.getEditText().getText().toString();
                    String fo_do = "";
                    String average_rpm = "";
                    String average_speed = "";

                    if(dept.equals("DECK")){
                        ship_position = et_ship_position.getEditText().getText().toString();
                        fixing_method = et_fixing_method.getEditText().getText().toString();
                        course_speed = et_course_speed.getEditText().getText().toString();
                        fo_do = "";
                        average_rpm = "";
                        average_speed = "";
                    }else{
                        ship_position = "";
                        fixing_method = "";
                        course_speed = "";
                        fo_do = et_fo_do.getEditText().getText().toString();
                        average_rpm = et_average_rpm.getEditText().getText().toString();
                        average_speed = et_average_speed.getEditText().getText().toString();
                    }

                    if(journal_date.equals("")){
                        Toast.makeText(PersonJournalFormActivity.this, "Date is required", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(journal_time.equals("")){
                        Toast.makeText(PersonJournalFormActivity.this, "Time is required", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(dept.equals("DECK")){
                        if(ship_position.equals("")){
                            Toast.makeText(PersonJournalFormActivity.this, "Ship's position is required", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(fixing_method.equals("")){
                            Toast.makeText(PersonJournalFormActivity.this, "Position-Fixing Method is required", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(course_speed.equals("")){
                            Toast.makeText(PersonJournalFormActivity.this, "Course and Speed is required", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }else{
                        if(fo_do.equals("")){
                            Toast.makeText(PersonJournalFormActivity.this, "F.O./D.O. Consumption is required", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(average_rpm.equals("")){
                            Toast.makeText(PersonJournalFormActivity.this, "Average RPM is required", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(average_speed.equals("")){
                            Toast.makeText(PersonJournalFormActivity.this, "Average Engine Speed is required", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }



                    pd = new ProgressDialog(PersonJournalFormActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();


                    Integer id = db.newIntegerId("person_journal");
                    String person_journal_id = db.newId();
                    db.execQuery("INSERT INTO person_journal (person_journal_id, id, journal_date, journal_time, person_id, vessel_id, ship_position, fixing_method, course_speed, activities, fo_do, average_rpm, average_speed, checked_by_id, date_checked, login_id, last_update) VALUES ('" + person_journal_id + "', '" + id + "', '" + journal_date + "', '" + journal_time + "', '" + person_id + "', '" + vessel_id + "', '" + ship_position + "', '" + fixing_method + "', '" + course_speed + "', '" + activities + "', '" + fo_do + "', '"+average_rpm+"', '"+average_speed+"', '', '', '', '')");
                    int conn = getConnection.getConnectionType(PersonJournalFormActivity.this);
                    if(conn != 0){ //WITH NET
                        new SyncOnline(context, person_journal_id, "ADD").execute();
                    }else{

                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_journal', '" + person_journal_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                        pd.dismiss();

                        Intent intent1 = new Intent(PersonJournalFormActivity.this, PortWatchesActivity.class);
                        startActivity(intent1);
                        finish();
                        Toast.makeText(PersonJournalFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();

                    }
                }
            });
        } else{ //UPDATE
            String saved_journal_date = db.getFieldString("journal_date", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_journal_time = db.getFieldString("journal_time", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_ship_position = db.getFieldString("ship_position", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_fixing_method = db.getFieldString("fixing_method", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_course_speed = db.getFieldString("course_speed", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_activities = db.getFieldString("activities", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_fo_do = db.getFieldString("fo_do", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_average_rpm = db.getFieldString("average_rpm", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_average_speed = db.getFieldString("average_speed", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_date_checked = db.getFieldString("date_checked", "person_journal_id = '" + person_journal_id + "'", "person_journal");

            et_journal_date.getEditText().setText(saved_journal_date);
            et_journal_time.getEditText().setText(saved_journal_time);
            et_ship_position.getEditText().setText(saved_ship_position);
            et_fixing_method.getEditText().setText(saved_fixing_method);
            et_course_speed.getEditText().setText(saved_course_speed);
            et_activities.getEditText().setText(saved_activities);
            et_fo_do.getEditText().setText(saved_fo_do);
            et_average_rpm.getEditText().setText(saved_average_rpm);
            et_average_speed.getEditText().setText(saved_average_speed);

            if(saved_checked_by_id.equals("")){//not yet signed
                approval_container.setVisibility(View.GONE);
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String journal_date = et_journal_date.getEditText().getText().toString();
                        String journal_time = et_journal_time.getEditText().getText().toString();
                        String ship_position = "";
                        String fixing_method = "";
                        String course_speed = "";
                        String activities = et_activities.getEditText().getText().toString();
                        String fo_do = "";
                        String average_rpm = "";
                        String average_speed = "";

                        if(dept.equals("DECK")){
                            ship_position = et_ship_position.getEditText().getText().toString();
                            fixing_method = et_fixing_method.getEditText().getText().toString();
                            course_speed = et_course_speed.getEditText().getText().toString();
                            fo_do = "";
                            average_rpm = "";
                            average_speed = "";
                        }else{
                            ship_position = "";
                            fixing_method = "";
                            course_speed = "";
                            fo_do = et_fo_do.getEditText().getText().toString();
                            average_rpm = et_average_rpm.getEditText().getText().toString();
                            average_speed = et_average_speed.getEditText().getText().toString();
                        }

                        if(journal_date.equals("")){
                            Toast.makeText(PersonJournalFormActivity.this, "Date is required", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(journal_time.equals("")){
                            Toast.makeText(PersonJournalFormActivity.this, "Time is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(dept.equals("DECK")){
                            if(ship_position.equals("")){
                                Toast.makeText(PersonJournalFormActivity.this, "Ship's position is required", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(fixing_method.equals("")){
                                Toast.makeText(PersonJournalFormActivity.this, "Position-Fixing Method is required", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(course_speed.equals("")){
                                Toast.makeText(PersonJournalFormActivity.this, "Course and Speed is required", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }else{
                            if(fo_do.equals("")){
                                Toast.makeText(PersonJournalFormActivity.this, "F.O./D.O. Consumption is required", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(average_rpm.equals("")){
                                Toast.makeText(PersonJournalFormActivity.this, "Average RPM is required", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(average_speed.equals("")){
                                Toast.makeText(PersonJournalFormActivity.this, "Average Engine Speed is required", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        pd = new ProgressDialog(PersonJournalFormActivity.this);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage("Processing. Please wait .... ");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();

                        db.execQuery("UPDATE person_journal SET journal_date = '" + journal_date + "', journal_time = '" + journal_time + "', ship_position = '" + ship_position + "', fixing_method = '" + fixing_method + "', course_speed = '" + course_speed + "', activities = '" + activities + "', fo_do = '" + fo_do + "', average_rpm = '" + average_rpm + "', average_speed = '" + average_speed + "' WHERE person_journal_id = '" + person_journal_id + "'");
                        int conn = getConnection.getConnectionType(PersonJournalFormActivity.this);
                        if(conn != 0){ //WITH NET
                            new SyncOnline(context, person_journal_id, "UPDATE").execute();
                        }else {
                            Integer backup_item_id = db.newIntegerId("backup_item");
                            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_journal', '" + person_journal_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");
                            pd.dismiss();

                            Intent intent1 = new Intent(PersonJournalFormActivity.this, PersonJournalActivity.class);
                            startActivity(intent1);
                            finish();
                            Toast.makeText(PersonJournalFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();

                        }

                    }
                });

                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonJournalFormActivity.this);

                        alertDialogBuilder.setMessage("Are you sure you want to delete this record?");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                delete(person_journal_id);
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
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(PersonJournalFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(PersonJournalFormActivity.this, R.color.black));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(PersonJournalFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(PersonJournalFormActivity.this, R.color.black));

                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonJournalFormActivity.this);

                        alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved.");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(PersonJournalFormActivity.this, SteeringActivity.class);
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
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(PersonJournalFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(PersonJournalFormActivity.this, R.color.black));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(PersonJournalFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(PersonJournalFormActivity.this, R.color.black));
                    }
                });
            }else{
                btn_delete.setVisibility(View.GONE);
                btn_save.setVisibility(View.GONE);
                btn_cancel.setText("Back");
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PersonJournalFormActivity.this, SteeringActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                TextView tv_checked_by_id = findViewById(R.id.tv_checked_by_id);
                TextView tv_date_checked = findViewById(R.id.tv_date_checked);
                String officer = db.getFieldString("full_name", "person_id = '"+saved_checked_by_id+"'", "person");

                tv_checked_by_id.setText("Officer : " + officer);
                tv_date_checked.setText("Date : " + saved_date_checked);
            }

        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String min_ = ""+minute;
        int min = min_.length();
        String time = "";
        if(min > 1){
            time = hourOfDay+":"+minute;
        }else{
            time = hourOfDay+":0"+minute;
        }

        et_journal_time.getEditText().setText(time);
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
            String saved_journal_date = db.getFieldString("journal_date", "person_journal_id = '" + id + "'", "person_journal");
            String saved_journal_time = db.getFieldString("journal_time", "person_journal_id = '" + id + "'", "person_journal");
            String saved_person_id = db.getFieldString("person_id", "person_journal_id = '" + id + "'", "person_journal");
            String saved_vessel_id = db.getFieldString("vessel_id", "person_journal_id = '" + id + "'", "person_journal");
            String saved_ship_position = db.getFieldString("ship_position", "person_journal_id = '" + id + "'", "person_journal");
            saved_ship_position = URLEncoder.encode(saved_ship_position);
            String saved_fixing_method = db.getFieldString("fixing_method", "person_journal_id = '" + id + "'", "person_journal");
            saved_fixing_method = URLEncoder.encode(saved_fixing_method);
            String saved_course_speed = db.getFieldString("course_speed", "person_journal_id = '" + id + "'", "person_journal");
            saved_course_speed = URLEncoder.encode(saved_course_speed);
            String saved_activities = db.getFieldString("activities", "person_journal_id = '" + id + "'", "person_journal");
            saved_activities = URLEncoder.encode(saved_activities);
            String saved_fo_do = db.getFieldString("fo_do", "person_journal_id = '" + id + "'", "person_journal");
            saved_fo_do = URLEncoder.encode(saved_fo_do);
            String saved_average_rpm = db.getFieldString("average_rpm", "person_journal_id = '" + id + "'", "person_journal");
            saved_average_rpm = URLEncoder.encode(saved_average_rpm);
            String saved_average_speed = db.getFieldString("average_speed", "person_journal_id = '" + id + "'", "person_journal");
            saved_average_speed = URLEncoder.encode(saved_average_speed);
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_journal_id = '" + id + "'", "person_journal");
            String saved_date_checked = db.getFieldString("date_checked", "person_journal_id = '" + id + "'", "person_journal");
            String saved_login_id = db.getFieldString("login_id", "person_journal_id = '" + id + "'", "person_journal");
            String saved_last_update = db.getFieldString("last_update", "person_journal_id = '" + id + "'", "person_journal");
            saved_last_update = URLEncoder.encode(saved_last_update);

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_journal&id="+id+"&journal_date="+saved_journal_date+"&journal_time="+saved_journal_time+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&ship_position="+saved_ship_position+"&fixing_method="+saved_fixing_method+"&course_speed="+saved_course_speed+"&activities="+saved_activities+"&fo_do="+saved_fo_do+"&average_rpm="+saved_average_rpm+"&average_speed="+saved_average_speed+"&checked_by_id="+saved_checked_by_id+"&date_checked="+saved_date_checked+"&login_id="+saved_login_id+"&last_update="+saved_last_update+"&event="+event);
            Log.d("CONNECT", url +"sync.php?table=person_journal&id="+id+"&journal_date="+saved_journal_date+"&journal_time="+saved_journal_time+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&ship_position="+saved_ship_position+"&fixing_method="+saved_fixing_method+"&course_speed="+saved_course_speed+"&activities="+saved_activities+"&fo_do="+saved_fo_do+"&average_rpm="+saved_average_rpm+"&average_speed="+saved_average_speed+"&checked_by_id="+saved_checked_by_id+"&date_checked="+saved_date_checked+"&login_id="+saved_login_id+"&last_update="+saved_last_update+"&event="+event);

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
            Intent intent = new Intent(PersonJournalFormActivity.this, PersonJournalActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(PersonJournalFormActivity.this, "Record saved successfully.", Toast.LENGTH_LONG).show();
        }
    }

    public void delete(String person_journal_id){
        pd = new ProgressDialog(PersonJournalFormActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Processing. Please wait .... ");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        db.query("DELETE FROM person_journal WHERE person_journal_id = '"+person_journal_id+"'");

        int conn = getConnection.getConnectionType(PersonJournalFormActivity.this);
        if(conn != 0){
            new SyncOnlineDelete(context,  "person_journal", person_journal_id).execute();
        }else{
            Integer backup_item_id = db.newIntegerId("backup_item");
            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_journal', '" + person_journal_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'DELETE', 'N')");
            pd.dismiss();

            Intent intent = new Intent(PersonJournalFormActivity.this, PersonJournalActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(PersonJournalFormActivity.this, "Record successfully deleted.", Toast.LENGTH_LONG).show();
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
            Intent intent = new Intent(PersonJournalFormActivity.this, PersonJournalActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(PersonJournalFormActivity.this, "Record deleted successfully.", Toast.LENGTH_LONG).show();

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
        Intent intent = new Intent(PersonJournalFormActivity.this, PersonJournalActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ( pd!=null && pd.isShowing() ){
            pd.cancel();
        }

    }

}
