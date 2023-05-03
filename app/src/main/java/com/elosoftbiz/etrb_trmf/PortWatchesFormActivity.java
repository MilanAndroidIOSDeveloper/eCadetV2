package com.elosoftbiz.etrb_trmf;

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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PortWatchesFormActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;

    DatabaseHelper db;
    String person_id, time_type= "from", dept;
    LinearLayout approval_container;
    Button btn_cancel, btn_delete, btn_save;
    TextInputLayout et_person_port_watch_id, et_id, et_person_id, et_vessel_id, et_date_watch, et_from_time, et_to_time, et_voyage_number, et_port_name, et_desc_cargo, et_remarks, et_checked_by_id, et_app_by_id, et_date_checked, et_date_app, et_checked_remarks, et_app_remarks;
    String person_port_watch_id = "", vessel_id, date_watch, from_time, to_time, voyage_number, port_name, desc_cargo, remarks, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks;

    int Hour, Minute;
    TimePickerDialog timePickerDialog, timePickerDialog1 ;
    Calendar calendar ;

    String str = "", err_message, photo_file;
    HttpResponse response;
    JSONObject json = null;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_watches_form);

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


        db = new DatabaseHelper(PortWatchesFormActivity.this);
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

        et_date_watch = findViewById(R.id.et_date_watch);
        et_from_time = findViewById(R.id.et_from_time);
        et_to_time = findViewById(R.id.et_to_time);
        et_voyage_number = findViewById(R.id.et_voyage_number);
        et_port_name = findViewById(R.id.et_port_name);
        et_desc_cargo = findViewById(R.id.et_desc_cargo);
        et_remarks = findViewById(R.id.et_remarks);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_delete = findViewById(R.id.btn_delete);
        btn_save = findViewById(R.id.btn_save);
        approval_container = findViewById(R.id.approval_container);

        final MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker mdp = materialDateBuilder.build();
        et_date_watch.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        et_date_watch.getEditText().setOnClickListener(new View.OnClickListener() {
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
                            et_date_watch.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");
                            if(day.length() == 1){
                                day = "0"+ day;
                            }
                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_date_watch.getEditText().setText(completed);
                        }
                    }
                });

        calendar = Calendar.getInstance();
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minute = calendar.get(Calendar.MINUTE);

        et_from_time.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_type = "from";
                timePickerDialog = new TimePickerDialog(PortWatchesFormActivity.this, null,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                //timePickerDialog.setThemeDark(false);
                //timePickerDialog.showYearPickerFirst(false);
                timePickerDialog.setTitle("Select From Time");

                timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                    }
                });
                //timePickerDialog.setAccentColor(Color.parseColor("#86b9cf"));
                timePickerDialog.show();
            }
        });
        et_from_time.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_type = "from";
                timePickerDialog = new TimePickerDialog(PortWatchesFormActivity.this, null,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                //timePickerDialog.setThemeDark(false);
                //timePickerDialog.showYearPickerFirst(false);
                timePickerDialog.setTitle("Select From Time");

                timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                    }
                });
                //timePickerDialog.setAccentColor(Color.parseColor("#86b9cf"));
                timePickerDialog.show();
            }
        });

        et_to_time.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_type = "to";
                timePickerDialog1 = new TimePickerDialog(PortWatchesFormActivity.this, null,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                //timePickerDialog1.setThemeDark(false);
                //timePickerDialog.showYearPickerFirst(false);
                timePickerDialog1.setTitle("Select To Time");

                timePickerDialog1.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                    }
                });
                //timePickerDialog1.setAccentColor(Color.parseColor("#86b9cf"));
                timePickerDialog1.show();
            }
        });
        et_to_time.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_type = "to";
                timePickerDialog1 = new TimePickerDialog(PortWatchesFormActivity.this, null,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                //timePickerDialog1.setThemeDark(false);
                //timePickerDialog.showYearPickerFirst(false);
                timePickerDialog1.setTitle("Select To Time");

                timePickerDialog1.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                    }
                });
                //timePickerDialog1.setAccentColor(Color.parseColor("#86b9cf"));
                timePickerDialog1.show();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("person_port_watch_id")) {
            person_port_watch_id = intent.getStringExtra("person_port_watch_id");
        }

        if(person_port_watch_id.equals("")){ //ADD
            approval_container.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String vessel_id = db.getFieldString("vessel_id", " person_id ='"+person_id+"' ORDER BY sign_on DESC LIMIT 1", "shipboard");
                    String date_watch = et_date_watch.getEditText().getText().toString();
                    String from_time = et_from_time.getEditText().getText().toString();
                    String to_time = et_to_time.getEditText().getText().toString();
                    String voyage_number = et_voyage_number.getEditText().getText().toString();
                    String port_name = et_port_name.getEditText().getText().toString();
                    String desc_cargo = et_desc_cargo.getEditText().getText().toString();
                    String remarks = et_remarks.getEditText().getText().toString();

                    if(date_watch.equals("")){
                        Toast.makeText(PortWatchesFormActivity.this, "Date is required", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(from_time.equals("")){
                        Toast.makeText(PortWatchesFormActivity.this, "From time is required", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(to_time.equals("")){
                        Toast.makeText(PortWatchesFormActivity.this, "To time is required", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(port_name.equals("")){
                        Toast.makeText(PortWatchesFormActivity.this, "Name of port/terminal is required", Toast.LENGTH_LONG).show();
                        return;
                    }

                    double total_hrs = get_time_diff(from_time, to_time, date_watch);

                    pd = new ProgressDialog(PortWatchesFormActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();


                    Integer id = db.newIntegerId("person_port_watch");
                    String person_port_watch_id = db.newId();
                    db.execQuery("INSERT INTO person_port_watch (person_port_watch_id , id, person_id, vessel_id, date_watch, from_time, to_time, voyage_number, port_name, desc_cargo, remarks, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, total_hrs) VALUES ('" + person_port_watch_id + "', '" + id + "', '" + person_id + "', '" + vessel_id + "', '" + date_watch + "', '" + from_time + "', '" + to_time + "', '" + voyage_number + "', '" + port_name + "', '" + desc_cargo + "', '" + remarks + "', '', '', '', '', '', '', "+total_hrs+")");
                    int conn = getConnection.getConnectionType(PortWatchesFormActivity.this);
                    if(conn != 0){ //WITH NET
                        new SyncOnline(context, person_port_watch_id, "ADD").execute();
                    }else{
                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_port_watch', '" + person_port_watch_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                        pd.dismiss();

                        Intent intent1 = new Intent(PortWatchesFormActivity.this, PortWatchesActivity.class);
                        startActivity(intent1);
                        finish();
                        Toast.makeText(PortWatchesFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();

                    }
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PortWatchesFormActivity.this);

                    alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved.");
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(PortWatchesFormActivity.this, PortWatchesActivity.class);
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
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(PortWatchesFormActivity.this, R.color.white));
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(PortWatchesFormActivity.this, R.color.black));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(PortWatchesFormActivity.this, R.color.white));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(PortWatchesFormActivity.this, R.color.black));
                }
            });

        }else{ //SAVE
            String saved_date_watch = db.getFieldString("date_watch", "person_port_watch_id = '" + person_port_watch_id + "'", "person_port_watch");
            String saved_from_time = db.getFieldString("from_time", "person_port_watch_id = '" + person_port_watch_id + "'", "person_port_watch");
            String saved_to_time = db.getFieldString("to_time", "person_port_watch_id = '" + person_port_watch_id + "'", "person_port_watch");
            String saved_voyage_number = db.getFieldString("voyage_number", "person_port_watch_id = '" + person_port_watch_id + "'", "person_port_watch");
            String saved_port_name = db.getFieldString("port_name", "person_port_watch_id = '" + person_port_watch_id + "'", "person_port_watch");
            String saved_desc_cargo = db.getFieldString("desc_cargo", "person_port_watch_id = '" + person_port_watch_id + "'", "person_port_watch");
            String saved_remarks = db.getFieldString("remarks", "person_port_watch_id = '" + person_port_watch_id + "'", "person_port_watch");
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_port_watch_id = '" + person_port_watch_id + "'", "person_port_watch");
            String saved_app_by_id = db.getFieldString("app_by_id", "person_port_watch_id = '" + person_port_watch_id + "'", "person_port_watch");
            String saved_date_checked = db.getFieldString("date_checked", "person_port_watch_id = '" + person_port_watch_id + "'", "person_port_watch");
            String saved_date_app = db.getFieldString("date_app", "person_port_watch_id = '" + person_port_watch_id + "'", "person_port_watch");
            String saved_checked_remarks = db.getFieldString("checked_remarks", "person_port_watch_id = '" + person_port_watch_id + "'", "person_port_watch");
            String saved_app_remarks = db.getFieldString("app_remarks", "person_port_watch_id = '" + person_port_watch_id + "'", "person_port_watch");

            et_date_watch.getEditText().setText(saved_date_watch);
            et_from_time.getEditText().setText(saved_from_time);
            et_to_time.getEditText().setText(saved_to_time);
            et_voyage_number.getEditText().setText(saved_voyage_number);
            et_port_name.getEditText().setText(saved_port_name);
            et_desc_cargo.getEditText().setText(saved_desc_cargo);
            et_remarks.getEditText().setText(saved_remarks);

            if(saved_checked_by_id.equals("")){ //not yet signed
                approval_container.setVisibility(View.GONE);
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String date_watch = et_date_watch.getEditText().getText().toString();
                        String from_time = et_from_time.getEditText().getText().toString();
                        String to_time = et_to_time.getEditText().getText().toString();
                        String voyage_number = et_voyage_number.getEditText().getText().toString();
                        String port_name = et_port_name.getEditText().getText().toString();
                        String desc_cargo = et_desc_cargo.getEditText().getText().toString();
                        String remarks = et_remarks.getEditText().getText().toString();

                        if(date_watch.equals("")){
                            Toast.makeText(PortWatchesFormActivity.this, "Date is required", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(from_time.equals("")){
                            Toast.makeText(PortWatchesFormActivity.this, "From time is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(to_time.equals("")){
                            Toast.makeText(PortWatchesFormActivity.this, "To time is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(port_name.equals("")){
                            Toast.makeText(PortWatchesFormActivity.this, "Name of port/terminal is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        double total_hrs = get_time_diff(from_time, to_time, date_watch);

                        pd = new ProgressDialog(PortWatchesFormActivity.this);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage("Processing. Please wait .... ");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();

                        db.execQuery("UPDATE person_port_watch SET date_watch = '" + date_watch + "', from_time = '" + from_time + "', to_time = '" + to_time + "', voyage_number = '" + voyage_number + "', port_name = '" + port_name + "', desc_cargo = '" + desc_cargo + "', remarks = '" + remarks + "', total_hrs = "+total_hrs+" WHERE person_port_watch_id = '" + person_port_watch_id + "'");
                        int conn = getConnection.getConnectionType(PortWatchesFormActivity.this);
                        if(conn != 0){ //WITH NET
                            new SyncOnline(context, person_port_watch_id, "UPDATE").execute();
                        }else{
                            Integer backup_item_id = db.newIntegerId("backup_item");
                            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_bridge_watch', '" + person_port_watch_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");
                            pd.dismiss();
                            Intent intent1 = new Intent(PortWatchesFormActivity.this, PortWatchesActivity.class);
                            startActivity(intent1);
                            finish();
                            Toast.makeText(PortWatchesFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();

                        }

                    }
                });

                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PortWatchesFormActivity.this);

                        alertDialogBuilder.setMessage("Are you sure you want to delete this record?");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                delete(person_port_watch_id);
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
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(PortWatchesFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(PortWatchesFormActivity.this, R.color.black));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(PortWatchesFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(PortWatchesFormActivity.this, R.color.black));

                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PortWatchesFormActivity.this);

                        alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved.");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(PortWatchesFormActivity.this, PortWatchesActivity.class);
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
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(PortWatchesFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(PortWatchesFormActivity.this, R.color.black));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(PortWatchesFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(PortWatchesFormActivity.this, R.color.black));
                    }
                });
            }else{
                btn_delete.setVisibility(View.GONE);
                btn_save.setVisibility(View.GONE);
                btn_cancel.setText("Back");
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PortWatchesFormActivity.this, PortWatchesActivity.class);
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

    public void delete(String person_port_watch_id){
        pd = new ProgressDialog(PortWatchesFormActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Processing. Please wait .... ");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        db.query("DELETE FROM person_port_watch WHERE person_port_watch_id = '"+person_port_watch_id+"'");
        int conn = getConnection.getConnectionType(PortWatchesFormActivity.this);
        if(conn != 0){
            new SyncOnlineDelete(context,  "person_port_watch", person_port_watch_id).execute();
        }else{
            Integer backup_item_id = db.newIntegerId("backup_item");
            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_port_watch', '" + person_port_watch_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'DELETE', 'N')");
            pd.dismiss();

            Intent intent = new Intent(PortWatchesFormActivity.this, PortWatchesActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(PortWatchesFormActivity.this, "Record successfully deleted.", Toast.LENGTH_LONG).show();
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

        if(time_type.equals("from")){
            et_from_time.getEditText().setText(time);
        }else{
            et_to_time.getEditText().setText(time);
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
            String saved_person_id = db.getFieldString("person_id", "person_port_watch_id = '" + id + "'", "person_port_watch");
            String saved_vessel_id = db.getFieldString("vessel_id", "person_port_watch_id = '" + id + "'", "person_port_watch");
            String saved_date_watch = db.getFieldString("date_watch", "person_port_watch_id = '" + id + "'", "person_port_watch");
            String saved_from_time = db.getFieldString("from_time", "person_port_watch_id = '" + id + "'", "person_port_watch");
            saved_from_time = URLEncoder.encode(saved_from_time);
            String saved_to_time = db.getFieldString("to_time", "person_port_watch_id = '" + id + "'", "person_port_watch");
            saved_to_time = URLEncoder.encode(saved_to_time);
            String saved_voyage_number = db.getFieldString("voyage_number", "person_port_watch_id = '" + id + "'", "person_port_watch");
            String saved_port_name = db.getFieldString("port_name", "person_port_watch_id = '" + id + "'", "person_port_watch");
            saved_port_name = URLEncoder.encode(saved_port_name);
            String saved_desc_cargo = db.getFieldString("desc_cargo", "person_port_watch_id = '" + id + "'", "person_port_watch");
            saved_desc_cargo = URLEncoder.encode(saved_desc_cargo);
            String saved_remarks = db.getFieldString("remarks", "person_port_watch_id = '" + id + "'", "person_port_watch");
            saved_remarks = URLEncoder.encode(saved_remarks);
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_port_watch_id = '" + id + "'", "person_port_watch");
            String saved_app_by_id = db.getFieldString("app_by_id", "person_port_watch_id = '" + id + "'", "person_port_watch");
            String saved_date_checked = db.getFieldString("date_checked", "person_port_watch_id = '" + id + "'", "person_port_watch");
            String saved_date_app = db.getFieldString("date_app", "person_port_watch_id = '" + id + "'", "person_port_watch");
            String saved_checked_remarks = db.getFieldString("checked_remarks", "person_port_watch_id = '" + id + "'", "person_port_watch");
            saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
            String saved_app_remarks = db.getFieldString("app_remarks", "person_port_watch_id = '" + id + "'", "person_port_watch");
            saved_app_remarks = URLEncoder.encode(saved_app_remarks);
            String saved_total_hrs = db.getFieldString("total_hrs", "person_port_watch_id = '" + id + "'", "person_port_watch");

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_port_watch&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&date_watch="+saved_date_watch+"&from_time="+saved_from_time+"&to_time="+saved_to_time+"&voyage_number="+saved_voyage_number+"&port_name="+saved_port_name+"&desc_cargo="+saved_desc_cargo+"&remarks="+saved_remarks+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&total_hrs="+saved_total_hrs+"&event="+event);
            Log.d("CONNECT", url +"sync.php?table=person_port_watch&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&date_watch="+saved_date_watch+"&from_time="+saved_from_time+"&to_time="+saved_to_time+"&voyage_number="+saved_voyage_number+"&port_name="+saved_port_name+"&desc_cargo="+saved_desc_cargo+"&remarks="+saved_remarks+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&total_hrs="+saved_total_hrs+"&event="+event);

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
            Intent intent = new Intent(PortWatchesFormActivity.this, PortWatchesActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(PortWatchesFormActivity.this, "Record saved successfully.", Toast.LENGTH_LONG).show();

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
            Intent intent = new Intent(PortWatchesFormActivity.this, PortWatchesActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(PortWatchesFormActivity.this, "Record deleted successfully.", Toast.LENGTH_LONG).show();

        }
    }

    public double get_time_diff(String from, String to_time, String date_watchkeeping){
        String pattern = "HH:mm";
        String pattern2 = "yyyy-MM-DD HH:mm";

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        SimpleDateFormat sdf2 = new SimpleDateFormat(pattern2);
        DecimalFormat df = new DecimalFormat("0.00");

        Double total_hrs = 0.00;
        try {
            Date date1 = sdf.parse(from);
            Date date2 = sdf.parse(to_time);

            if(date2.before(date1)) {
                String dt = date_watchkeeping;  // Start date
                SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.setTime(sdf_.parse(dt));
                c.add(Calendar.DATE, 1);  // number of days to add
                dt = sdf_.format(c.getTime());  // dt is now the new date

                Date date1_ = sdf2.parse(date_watchkeeping + " " + from);
                Date date2_ = sdf2.parse(dt + " " + to_time);

                long diff = date2_.getTime() - date1_.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                double diff_ = (double) diff / (60 * 60 * 1000);
                total_hrs = Double.valueOf(df.format(diff_));

                //Toast.makeText(BridgeWatchkeepingFormActivity.this, "Before : " + df.format(diff_), Toast.LENGTH_LONG).show();

            } else {
                long diff = date2.getTime() - date1.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                double diff_ = (double) diff / (60 * 60 * 1000);
                total_hrs = Double.valueOf(df.format(diff_));

                //Toast.makeText(BridgeWatchkeepingFormActivity.this, "After : " + df.format(diff_), Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e){
            e.printStackTrace();
        }

        return total_hrs;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PortWatchesFormActivity.this, PortWatchesActivity.class);
        startActivity(intent);
        finish();
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
