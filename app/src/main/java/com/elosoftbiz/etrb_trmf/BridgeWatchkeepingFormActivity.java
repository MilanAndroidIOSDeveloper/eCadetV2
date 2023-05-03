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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BridgeWatchkeepingFormActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;

    DatabaseHelper db;
    String person_id, time_type = "from", dept;
    TextInputLayout et_journal_date, et_journal_time, et_journal_time_to, et_ship_position_lat, et_fixing_method, et_activities, et_port_depart, et_port_dest;
    TextInputLayout et_course_speed, et_ship_position_long, et_ship_position_vicinity, et_fo_rob, et_fo_dob, et_fo_lob;
    Button btn_cancel, btn_delete, btn_save;
    String person_journal_id = "", vessel_id, watch_type, remarks;
    LinearLayout approval_container;
    int Hour, Minute;
    TimePickerDialog timePickerDialog, timePickerDialog1 ;
    Calendar calendar ;
    Spinner spinner;
    List<String> typeArray =  new ArrayList<>();

    String str = "", err_message, photo_file;
    HttpResponse response;
    JSONObject json = null;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_watchkeeping_form);

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


        db = new DatabaseHelper(BridgeWatchkeepingFormActivity.this);
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

        et_journal_date = findViewById(R.id.et_journal_date);
        et_journal_time = findViewById(R.id.et_journal_time);
        et_journal_time_to = findViewById(R.id.et_journal_time_to);
        et_ship_position_lat = findViewById(R.id.et_ship_position_lat);
        et_fixing_method = findViewById(R.id.et_fixing_method);
        et_course_speed = findViewById(R.id.et_course_speed);
        et_activities = findViewById(R.id.et_activities);
        et_port_depart = findViewById(R.id.et_port_depart);
        et_port_dest = findViewById(R.id.et_port_dest);
        et_ship_position_long = findViewById(R.id.et_ship_position_long);
        et_ship_position_vicinity = findViewById(R.id.et_ship_position_vicinity);
        et_fo_rob = findViewById(R.id.et_fo_rob);
        et_fo_dob = findViewById(R.id.et_fo_dob);
        et_fo_lob = findViewById(R.id.et_fo_lob);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_delete = findViewById(R.id.btn_delete);
        btn_save = findViewById(R.id.btn_save);
        approval_container = findViewById(R.id.approval_container);



        calendar = Calendar.getInstance();
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minute = calendar.get(Calendar.MINUTE);

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

        et_journal_time.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_type = "from";
                timePickerDialog = new TimePickerDialog(BridgeWatchkeepingFormActivity.this, null,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                //timePickerDialog.setThemeDark(false);
                //timePickerDialog.showYearPickerFirst(false);
                timePickerDialog.setTitle("Select From Time");

                timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                    }
                });
               // timePickerDialog.setAccentColor(Color.parseColor("#86b9cf"));
                timePickerDialog.show();
            }
        });

        et_journal_time.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_type = "from";
                timePickerDialog = new TimePickerDialog(BridgeWatchkeepingFormActivity.this, null,
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


        et_journal_time_to.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_type = "to";
                timePickerDialog1 = new TimePickerDialog(BridgeWatchkeepingFormActivity.this, null,
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
        et_journal_time_to.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_type = "to";
                timePickerDialog1 = new TimePickerDialog(BridgeWatchkeepingFormActivity.this, null,
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
        if (intent.hasExtra("person_journal_id")) {
            person_journal_id = intent.getStringExtra("person_journal_id");
        }

        if(person_journal_id.equals("")){ //ADD
            approval_container.setVisibility(View.GONE);
            btn_delete.setVisibility(View.GONE);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String vessel_id = db.getFieldString("vessel_id", " person_id ='"+person_id+"' ORDER BY sign_on DESC LIMIT 1", "shipboard");
                    String journal_date = et_journal_date.getEditText().getText().toString();
                    String journal_time = et_journal_time.getEditText().getText().toString();
                    String journal_time_to = et_journal_time_to.getEditText().getText().toString();
                    String ship_position_lat = et_ship_position_lat.getEditText().getText().toString();
                    String fixing_method = et_fixing_method.getEditText().getText().toString();
                    String course_speed = et_course_speed.getEditText().getText().toString();
                    String activities = et_activities.getEditText().getText().toString();
                    String port_depart = et_port_depart.getEditText().getText().toString();
                    String port_dest = et_port_dest.getEditText().getText().toString();
                    String ship_position_long = et_ship_position_long.getEditText().getText().toString();
                    String ship_position_vicinity = et_ship_position_vicinity.getEditText().getText().toString();
                    String fo_rob = et_fo_rob.getEditText().getText().toString();
                    String fo_dob = et_fo_dob.getEditText().getText().toString();
                    String fo_lob = et_fo_lob.getEditText().getText().toString();

                    if(journal_date.equals("")){
                        Toast.makeText(BridgeWatchkeepingFormActivity.this, "Date is required", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(journal_time.equals("")){
                        Toast.makeText(BridgeWatchkeepingFormActivity.this, "From Time is required", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(journal_time_to.equals("")){
                        Toast.makeText(BridgeWatchkeepingFormActivity.this, "To Time is required", Toast.LENGTH_LONG).show();
                        return;
                    }

                    double total_hrs = get_time_diff(journal_time, journal_time_to, journal_date);

                    pd = new ProgressDialog(BridgeWatchkeepingFormActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();

                    Integer id = db.newIntegerId("person_journal");
                    String person_journal_id = db.newId();
                    db.execQuery("INSERT INTO person_journal (id, person_journal_id, journal_date, journal_time, person_id, vessel_id, ship_position_lat, fixing_method, course_speed, activities, journal_time_to, port_depart, port_dest, ship_position_long, ship_position_vicinity, fo_rob, fo_dob, fo_lob, hrs) VALUES ('" + id + "', '" + person_journal_id + "', '" + journal_date + "', '" + journal_time + "', '" + person_id + "', '" + vessel_id + "', '" + ship_position_lat + "', '" + fixing_method + "', '" + course_speed + "', '" + activities + "', '" + journal_time_to + "', '"+port_depart+"', '"+port_dest+"', '"+ship_position_long+"', '"+ship_position_vicinity+"', '"+fo_rob+"', '"+fo_dob+"', '"+fo_lob+"', '"+total_hrs+"')");

                    int conn = getConnection.getConnectionType(BridgeWatchkeepingFormActivity.this);
                    if(conn != 0){ //WITH NET
                        new SyncOnline(context, person_journal_id, "ADD").execute();
                    }else{
                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_journal', '" + person_journal_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                        pd.dismiss();

                        Intent intent1 = new Intent(BridgeWatchkeepingFormActivity.this, BridgeWatchkeepingActivity.class);
                        startActivity(intent1);
                        finish();
                        Toast.makeText(BridgeWatchkeepingFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();
                    }



                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BridgeWatchkeepingFormActivity.this);

                    alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved.");
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(BridgeWatchkeepingFormActivity.this, BridgeWatchkeepingActivity.class);
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
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(BridgeWatchkeepingFormActivity.this, R.color.white));
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(BridgeWatchkeepingFormActivity.this, R.color.black));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(BridgeWatchkeepingFormActivity.this, R.color.white));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(BridgeWatchkeepingFormActivity.this, R.color.black));
                }
            });
        }else{ //SAVE
            String saved_journal_date = db.getFieldString("journal_date", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_journal_time = db.getFieldString("journal_time", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_ship_position_lat = db.getFieldString("ship_position_lat", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_fixing_method = db.getFieldString("fixing_method", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_course_speed = db.getFieldString("course_speed", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_activities = db.getFieldString("activities", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_fo_do = db.getFieldString("fo_do", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_date_checked = db.getFieldString("date_checked", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_journal_time_to = db.getFieldString("journal_time_to", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_port_depart = db.getFieldString("port_depart", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_port_dest = db.getFieldString("port_dest", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_ship_position_long = db.getFieldString("ship_position_long", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_ship_position_vicinity = db.getFieldString("ship_position_vicinity", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_fo_rob = db.getFieldString("fo_rob", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_fo_dob = db.getFieldString("fo_dob", "person_journal_id = '" + person_journal_id + "'", "person_journal");
            String saved_fo_lob = db.getFieldString("fo_lob", "person_journal_id = '" + person_journal_id + "'", "person_journal");

            if(saved_checked_by_id.equals("")){ //not yet signed
                approval_container.setVisibility(View.GONE);
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String journal_date = et_journal_date.getEditText().getText().toString();
                        String journal_time = et_journal_time.getEditText().getText().toString();
                        String journal_time_to = et_journal_time_to.getEditText().getText().toString();
                        String ship_position_lat = et_ship_position_lat.getEditText().getText().toString();
                        String fixing_method = et_fixing_method.getEditText().getText().toString();
                        String course_speed = et_course_speed.getEditText().getText().toString();
                        String activities = et_activities.getEditText().getText().toString();
                        String port_depart = et_port_depart.getEditText().getText().toString();
                        String port_dest = et_port_dest.getEditText().getText().toString();
                        String ship_position_long = et_ship_position_long.getEditText().getText().toString();
                        String ship_position_vicinity = et_ship_position_vicinity.getEditText().getText().toString();
                        String fo_rob = et_fo_rob.getEditText().getText().toString();
                        String fo_dob = et_fo_dob.getEditText().getText().toString();
                        String fo_lob = et_fo_lob.getEditText().getText().toString();

                        if(journal_date.equals("")){
                            Toast.makeText(BridgeWatchkeepingFormActivity.this, "Date is required", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(journal_time.equals("")){
                            Toast.makeText(BridgeWatchkeepingFormActivity.this, "From Time is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(journal_time_to.equals("")){
                            Toast.makeText(BridgeWatchkeepingFormActivity.this, "To Time is required", Toast.LENGTH_LONG).show();
                            return;
                        }


                        double total_hrs = get_time_diff(journal_time, journal_time_to, journal_date);

                        //db.execQuery("UPDATE person_bridge_watch SET date_watchkeeping = '" + date_watchkeeping + "', from_time = '" + from_time + "', to_time = '" + to_time + "', voyage_number = '" + voyage_number + "', voyage_desc = '" + voyage_desc + "', watch_type = '" + watch_type + "', remarks = '" + remarks + "', total_hrs = "+total_hrs+" WHERE person_bridge_watch_id = '" + person_bridge_watch_id + "'");
                        pd = new ProgressDialog(BridgeWatchkeepingFormActivity.this);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage("Processing. Please wait .... ");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();
                        int conn = getConnection.getConnectionType(BridgeWatchkeepingFormActivity.this);
                        if(conn != 0){ //WITH NET
                            new SyncOnline(context, person_journal_id, "UPDATE").execute();
                        }else{
                            Integer backup_item_id = db.newIntegerId("backup_item");
                            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_journal', '" + person_journal_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");
                            pd.dismiss();

                            Intent intent1 = new Intent(BridgeWatchkeepingFormActivity.this, BridgeWatchkeepingActivity.class);
                            startActivity(intent1);
                            finish();
                            Toast.makeText(BridgeWatchkeepingFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();

                        }


                    }
                });

                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BridgeWatchkeepingFormActivity.this);

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
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(BridgeWatchkeepingFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(BridgeWatchkeepingFormActivity.this, R.color.black));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(BridgeWatchkeepingFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(BridgeWatchkeepingFormActivity.this, R.color.black));

                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BridgeWatchkeepingFormActivity.this);

                        alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved.");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(BridgeWatchkeepingFormActivity.this, BridgeWatchkeepingActivity.class);
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
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(BridgeWatchkeepingFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(BridgeWatchkeepingFormActivity.this, R.color.black));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(BridgeWatchkeepingFormActivity.this, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(BridgeWatchkeepingFormActivity.this, R.color.black));
                    }
                });
            }else{
                btn_delete.setVisibility(View.GONE);
                btn_save.setVisibility(View.GONE);
                btn_cancel.setText("Back");
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(BridgeWatchkeepingFormActivity.this, BridgeWatchkeepingActivity.class);
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

    public void delete(String person_bridge_watch_id){
        pd = new ProgressDialog(BridgeWatchkeepingFormActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Processing. Please wait .... ");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        db.query("DELETE FROM person_bridge_watch WHERE person_bridge_watch_id = '"+person_bridge_watch_id+"'");

        int conn = getConnection.getConnectionType(BridgeWatchkeepingFormActivity.this);
        if(conn != 0){
            new SyncOnlineDelete(context,  "person_bridge_watch", person_bridge_watch_id).execute();
        }else{
            Integer backup_item_id = db.newIntegerId("backup_item");
            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_bridge_watch', '" + person_bridge_watch_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'DELETE', 'N')");
            pd.dismiss();

            Intent intent = new Intent(BridgeWatchkeepingFormActivity.this, BridgeWatchkeepingActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(BridgeWatchkeepingFormActivity.this, "Record successfully deleted.", Toast.LENGTH_LONG).show();
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
            et_journal_time.getEditText().setText(time);
        }else{
            et_journal_time_to.getEditText().setText(time);
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
            String saved_person_id = db.getFieldString("person_id", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            String saved_vessel_id = db.getFieldString("vessel_id", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            String saved_date_watchkeeping = db.getFieldString("date_watchkeeping", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            String saved_from_time = db.getFieldString("from_time", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            saved_from_time = URLEncoder.encode(saved_from_time);
            String saved_to_time = db.getFieldString("to_time", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            saved_to_time = URLEncoder.encode(saved_to_time);
            String saved_voyage_number = db.getFieldString("voyage_number", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            String saved_voyage_desc = db.getFieldString("voyage_desc", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            saved_voyage_desc = URLEncoder.encode(saved_voyage_desc);
            String saved_watch_type = db.getFieldString("watch_type", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            saved_watch_type = URLEncoder.encode(saved_watch_type);
            String saved_remarks = db.getFieldString("remarks", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            saved_remarks = URLEncoder.encode(saved_remarks);
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            String saved_app_by_id = db.getFieldString("app_by_id", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            String saved_date_checked = db.getFieldString("date_checked", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            String saved_date_app = db.getFieldString("date_app", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            String saved_checked_remarks = db.getFieldString("checked_remarks", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
            String saved_app_remarks = db.getFieldString("app_remarks", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
            saved_app_remarks = URLEncoder.encode(saved_app_remarks);
            String saved_total_hrs = db.getFieldString("total_hrs", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_bridge_watch&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&date_watchkeeping="+saved_date_watchkeeping+"&from_time="+saved_from_time+"&to_time="+saved_to_time+"&voyage_number="+saved_voyage_number+"&voyage_desc="+saved_voyage_desc+"&watch_type="+saved_watch_type+"&remarks="+saved_remarks+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&total_hrs="+saved_total_hrs+"&event="+event);
            Log.d("CONNECT", url +"sync.php?table=person_bridge_watch&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&date_watchkeeping="+saved_date_watchkeeping+"&from_time="+saved_from_time+"&to_time="+saved_to_time+"&voyage_number="+saved_voyage_number+"&voyage_desc="+saved_voyage_desc+"&watch_type="+saved_watch_type+"&remarks="+saved_remarks+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&total_hrs="+saved_total_hrs+"&event="+event);

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
            Intent intent = new Intent(BridgeWatchkeepingFormActivity.this, BridgeWatchkeepingActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(BridgeWatchkeepingFormActivity.this, "Record saved successfully.", Toast.LENGTH_LONG).show();

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
            Intent intent = new Intent(BridgeWatchkeepingFormActivity.this, BridgeWatchkeepingActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(BridgeWatchkeepingFormActivity.this, "Record deleted successfully.", Toast.LENGTH_LONG).show();

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
        Intent intent = new Intent(BridgeWatchkeepingFormActivity.this, BridgeWatchkeepingActivity.class);
        startActivity(intent);
        finish();
    }

    //@Override
    //public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

    //}

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
