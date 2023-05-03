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

public class PersonUpdateActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;

    TextInputLayout et_fname, et_mname, et_lname, et_birth_date, et_birth_place, et_nationality, et_passport_no;
    TextInputLayout et_passport_no_issue_date, et_cdc_no,  et_cdc_no_issue_date, et_cdc_no_issue_place, et_indos_no;
    TextInputLayout et_st_address, et_mobile, et_email, et_enrtry_date, et_height, et_weight, et_blood_group;
    TextInputLayout et_marks, et_emergency_contact_name, et_emergency_contact_address, et_emergency_relationship, et_emergency_contact_no;
    TextInputLayout et_srn, et_sid_no, et_srb_no, et_srb_date;
    String person_id;
    DatabaseHelper db;
    Button btn_save, btn_cancel;

    String str = "", err_message, photo_file;
    HttpResponse response;
    ProgressDialog pd;
    JSONObject json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_update);

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

        /****** START MENU *******/
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        new MenuDeck(navigationView, this, mDrawerLayout);
        /****** END MENU *******/

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        final MaterialDatePicker materialDatePicker_pass = materialDateBuilder.build();
        final MaterialDatePicker materialDatePicker_cdc = materialDateBuilder.build();
        final MaterialDatePicker materialDatePicker_entry = materialDateBuilder.build();
        final MaterialDatePicker materialDatePicker_srb_date = materialDateBuilder.build();


        et_birth_date = findViewById(R.id.et_birth_date);
        et_birth_date.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        et_birth_date.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        et_passport_no_issue_date = findViewById(R.id.et_passport_no_issue_date);
        et_passport_no_issue_date.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker_pass.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        et_passport_no_issue_date.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker_pass.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        et_srb_date = findViewById(R.id.et_srb_date);
        et_srb_date.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker_srb_date.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        et_srb_date.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker_srb_date.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });


        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        String dateSelected = "" + materialDatePicker.getHeaderText();
                        if(dateSelected.equals("Selected Date")){
                            et_birth_date.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");

                            if(day.length() == 1){
                                day = "0"+ day;
                            }

                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_birth_date.getEditText().setText(completed);
                        }
                    }
                });

        materialDatePicker_pass.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        String dateSelected = "" + materialDatePicker_pass.getHeaderText();
                        if(dateSelected.equals("Selected Date")){
                            et_passport_no_issue_date.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");

                            if(day.length() == 1){
                                day = "0"+ day;
                            }

                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_passport_no_issue_date.getEditText().setText(completed);
                        }
                    }
                });

        materialDatePicker_cdc.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        String dateSelected = "" + materialDatePicker_cdc.getHeaderText();

                        if(dateSelected.equals("Selected Date")){
                            et_cdc_no_issue_date.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");

                            if(day.length() == 1){
                                day = "0"+ day;
                            }

                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_cdc_no_issue_date.getEditText().setText(completed);
                        }
                    }
                });

        materialDatePicker_entry.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        String dateSelected = "" + materialDatePicker_entry.getHeaderText();
                        if(dateSelected.equals("Selected Date")){
                            et_enrtry_date.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");
                            if(day.length() == 1){
                                day = "0"+ day;
                            }
                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_enrtry_date.getEditText().setText(completed);
                        }
                    }
                });

        materialDatePicker_srb_date.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        String dateSelected = "" + materialDatePicker_srb_date.getHeaderText();
                        if(dateSelected.equals("Selected Date")){
                            et_srb_date.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");
                            if(day.length() == 1){
                                day = "0"+ day;
                            }
                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_srb_date.getEditText().setText(completed);
                        }
                    }
                });

        db = new DatabaseHelper(this);
        person_id = db.getCadetId();

        String fname = db.getFieldString("fname", " person_id = '"+person_id+"'", "person");
        String mname = db.getFieldString("mname", " person_id = '"+person_id+"'", "person");
        String lname = db.getFieldString("lname", " person_id = '"+person_id+"'", "person");
        String dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");
        String birth_date = db.getFieldString("birth_date", " person_id = '"+person_id+"'", "person");
        String birth_place = db.getFieldString("birth_place", " person_id = '"+person_id+"'", "person");
        if(birth_place.equals("null")){
            birth_place = "";
        }
        final String nationality = db.getFieldString("nationality", " person_id = '"+person_id+"'", "person");

        String passport_no = db.getFieldString("passport_no", " person_id = '"+person_id+"'", "person");
        String passport_no_issue_date = db.getFieldString("passport_no_issue_date", " person_id = '"+person_id+"'", "person");
        String cdc_no = db.getFieldString("cdc_no", " person_id = '"+person_id+"'", "person");
        String cdc_no_issue_date = db.getFieldString("cdc_no_issue_date", " person_id = '"+person_id+"'", "person");
        String cdc_no_issue_place = db.getFieldString("cdc_no_issue_place", " person_id = '"+person_id+"'", "person");
        final String indos_no = db.getFieldString("indos_no", " person_id = '"+person_id+"'", "person");
        String st_address = db.getFieldString("st_address", " person_id = '"+person_id+"'", "person");
        String mobile = db.getFieldString("mobile", " person_id = '"+person_id+"'", "person");
        String email = db.getFieldString("email", " person_id = '"+person_id+"'", "person");
        String entry_date = db.getFieldString("enrtry_date", " person_id = '"+person_id+"'", "person");
        String height = db.getFieldString("height", " person_id = '"+person_id+"'", "person");
        String weight = db.getFieldString("weight", " person_id = '"+person_id+"'", "person");
        String blood_group = db.getFieldString("blood_group", " person_id = '"+person_id+"'", "person");
        String marks = db.getFieldString("marks", " person_id = '"+person_id+"'", "person");
        String company_id = db.getFieldString("company_id", " person_id = '"+person_id+"'", "person");
        String school_id = db.getFieldString("school_id", " person_id = '"+person_id+"'", "person");
        String emergency_contact_name = db.getFieldString("emergency_contact_name", " person_id = '"+person_id+"'", "person");
        String emergency_contact_address = db.getFieldString("emergency_contact_address", " person_id = '"+person_id+"'", "person");
        String emergency_relationship = db.getFieldString("emergency_relationship", " person_id = '"+person_id+"'", "person");
        String emergency_contact_no = db.getFieldString("emergency_contact_no", " person_id = '"+person_id+"'", "person");
        String srn = db.getFieldString("srn", " person_id = '"+person_id+"'", "person");
        String sid_no = db.getFieldString("sid_no", " person_id = '"+person_id+"'", "person");
        String srb_no = db.getFieldString("srb_no", " person_id = '"+person_id+"'", "person");
        String srb_date = db.getFieldString("srb_date", " person_id = '"+person_id+"'", "person");

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

        et_fname = findViewById(R.id.et_fname);
        et_fname.getEditText().setText(fname);
        et_fname.getEditText().setEnabled(false);

        et_mname = findViewById(R.id.et_mname);
        et_mname.getEditText().setText(mname);
        et_mname.getEditText().setEnabled(false);

        et_lname = findViewById(R.id.et_lname);
        et_lname.getEditText().setText(lname);
        et_lname.getEditText().setEnabled(false);

        et_birth_date = findViewById(R.id.et_birth_date);
        et_birth_date.getEditText().setText(birth_date);

        et_birth_place = findViewById(R.id.et_birth_place);
        et_birth_place.getEditText().setText(birth_place);

        et_nationality = findViewById(R.id.et_nationality);
        et_nationality.getEditText().setText(nationality);

        et_passport_no = findViewById(R.id.et_passport_no);
        et_passport_no.getEditText().setText(passport_no);

        et_passport_no_issue_date = findViewById(R.id.et_passport_no_issue_date);
        et_passport_no_issue_date.getEditText().setText(passport_no_issue_date);

        et_srn = findViewById(R.id.et_srn);
        et_srn.getEditText().setText(srn);

        et_sid_no = findViewById(R.id.et_sid_no);
        et_sid_no.getEditText().setText(sid_no);

        et_srb_no = findViewById(R.id.et_srb_no);
        et_srb_no.getEditText().setText(srb_date);

        et_st_address = findViewById(R.id.et_st_address);
        et_st_address.getEditText().setText(st_address);

        et_mobile = findViewById(R.id.et_mobile);
        et_mobile.getEditText().setText(mobile);

        et_email = findViewById(R.id.et_email);
        et_email.getEditText().setText(email);

        et_height = findViewById(R.id.et_height);
        et_height.getEditText().setText(height);

        et_weight = findViewById(R.id.et_weight);
        et_weight.getEditText().setText(weight);

        et_blood_group = findViewById(R.id.et_blood_group);
        et_blood_group.getEditText().setText(blood_group);

        et_marks = findViewById(R.id.et_marks);
        et_marks.getEditText().setText(marks);

        //et_company_id.getEditText().setEnabled(false);

        et_emergency_contact_name = findViewById(R.id.et_emergency_contact_name);
        et_emergency_contact_name.getEditText().setText(emergency_contact_name);

        et_emergency_contact_address = findViewById(R.id.et_emergency_contact_address);
        et_emergency_contact_address.getEditText().setText(emergency_contact_address);

        et_emergency_contact_no = findViewById(R.id.et_emergency_contact_no);
        et_emergency_contact_no.getEditText().setText(emergency_contact_no);

        et_emergency_relationship = findViewById(R.id.et_emergency_relationship);
        et_emergency_relationship.getEditText().setText(emergency_relationship);

        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonUpdateActivity.this);

                alertDialogBuilder.setMessage("Are you sure you want to save changes?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String new_birth_date = et_birth_date.getEditText().getText().toString();
                        String new_birth_place = et_birth_place.getEditText().getText().toString();
                        String new_nationality = et_nationality.getEditText().getText().toString();
                        String new_passport_no = et_passport_no.getEditText().getText().toString();
                        String new_passport_no_issue_date = et_passport_no_issue_date.getEditText().getText().toString();
                        String new_cdc_no = "";
                        String new_cdc_no_issue_date = "";
                        String new_cdc_no_issue_place = "";
                        String new_indos_no = "";
                        String new_st_address = et_st_address.getEditText().getText().toString();
                        String new_mobile = et_mobile.getEditText().getText().toString();
                        String new_email = et_email.getEditText().getText().toString();
                        String new_enrtry_date = "";
                        String new_height = et_height.getEditText().getText().toString();
                        String new_weight = et_weight.getEditText().getText().toString();
                        String new_blood_group = et_blood_group.getEditText().getText().toString();
                        String new_marks = et_marks.getEditText().getText().toString();
                        String new_emergency_contact_name = et_emergency_contact_name.getEditText().getText().toString();
                        String new_emergency_contact_address = et_emergency_contact_address.getEditText().getText().toString();
                        String new_emergency_relationship = et_emergency_relationship.getEditText().getText().toString();
                        String new_emergency_contact_no = et_emergency_contact_no.getEditText().getText().toString();
                        String new_srn = et_srn.getEditText().getText().toString();
                        String new_sid_no = et_sid_no.getEditText().getText().toString();
                        String new_srb_no = et_srb_no.getEditText().getText().toString();
                        String new_srb_date = et_srb_date.getEditText().getText().toString();
                        Log.d("QUERY", "UPDATE person SET birth_date = '"+new_birth_date+"', birth_place = '"+new_birth_place+"', nationality = '"+new_nationality+"', passport_no = '"+new_passport_no+"', passport_no_issue_date = '"+new_passport_no_issue_date+"', cdc_no = '"+new_cdc_no+"', cdc_no_issue_date = '"+new_cdc_no_issue_date+"', cdc_no_issue_place = '"+new_cdc_no_issue_place+"', indos_no = '"+new_indos_no+"', st_address = '"+new_st_address+"', mobile = '"+new_mobile+"', email = '"+new_email+"', enrtry_date = '"+new_enrtry_date+"', height = '"+new_height+"', weight = '"+new_weight+"', blood_group = '"+new_blood_group+"', marks = '"+new_marks+"', emergency_contact_name = '"+new_emergency_contact_name+"', emergency_contact_address = '"+new_emergency_contact_address+"', emergency_relationship = '"+new_emergency_relationship+"', emergency_contact_no = '"+new_emergency_contact_no+"', srn = '"+new_srn+"', sid_no = '"+ new_sid_no+"', srb_no = '"+ new_sid_no+"', srb_no = '"+ new_srb_no+"', srb_date = '"+ new_srb_date+"' WHERE person_id  = '"+person_id+"'");
                        db.query("UPDATE person SET birth_date = '"+new_birth_date+"', birth_place = '"+new_birth_place+"', nationality = '"+new_nationality+"', passport_no = '"+new_passport_no+"', passport_no_issue_date = '"+new_passport_no_issue_date+"', cdc_no = '"+new_cdc_no+"', cdc_no_issue_date = '"+new_cdc_no_issue_date+"', cdc_no_issue_place = '"+new_cdc_no_issue_place+"', indos_no = '"+new_indos_no+"', st_address = '"+new_st_address+"', mobile = '"+new_mobile+"', email = '"+new_email+"', enrtry_date = '"+new_enrtry_date+"', height = '"+new_height+"', weight = '"+new_weight+"', blood_group = '"+new_blood_group+"', marks = '"+new_marks+"', emergency_contact_name = '"+new_emergency_contact_name+"', emergency_contact_address = '"+new_emergency_contact_address+"', emergency_relationship = '"+new_emergency_relationship+"', emergency_contact_no = '"+new_emergency_contact_no+"', srn = '"+new_srn+"', sid_no = '"+ new_sid_no+"', srb_no = '"+ new_sid_no+"', srb_no = '"+ new_srb_no+"', srb_date = '"+ new_srb_date+"' WHERE person_id  = '"+person_id+"'");

                        pd = new ProgressDialog(PersonUpdateActivity.this);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage("Processing. Please wait .... ");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();

                        int conn = getConnection.getConnectionType(context);
                        if(conn != 0){ //WITH CONN
                            new UpdateOnline(context).execute();
                        }else{ //WOUT CONN
                            Integer backup_item_id = db.newIntegerId("backup_item");
                            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person', '" + person_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");
                            pd.dismiss();
                            Intent intent = new Intent(PersonUpdateActivity.this, CadetParticularActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(PersonUpdateActivity.this, "Record successfully updated", Toast.LENGTH_LONG).show();
                        }

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
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(PersonUpdateActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(PersonUpdateActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(PersonUpdateActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(PersonUpdateActivity.this, R.color.black));

            }
        });

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonUpdateActivity.this, CadetParticularActivity.class);
                startActivity(intent);
                finish();
            }
        });
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

    private class UpdateOnline extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public UpdateOnline(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            String url = getString(R.string.url);
            //GET FROM TBL
            String saved_st_address = db.getFieldString("st_address", "person_id = '" + person_id + "'", "person");
            saved_st_address = URLEncoder.encode(saved_st_address);
            String saved_phone = db.getFieldString("phone", "person_id = '" + person_id + "'", "person");
            saved_phone = URLEncoder.encode(saved_phone);
            String saved_mobile = db.getFieldString("mobile", "person_id = '" + person_id + "'", "person");
            saved_mobile = URLEncoder.encode(saved_mobile);
            String saved_email = db.getFieldString("email", "person_id = '" + person_id + "'", "person");
            saved_email = URLEncoder.encode(saved_email);
            String saved_birth_date = db.getFieldString("birth_date", "person_id = '" + person_id + "'", "person");
            saved_birth_date = URLEncoder.encode(saved_birth_date);
            String saved_birth_place = db.getFieldString("birth_place", "person_id = '" + person_id + "'", "person");
            saved_birth_place = URLEncoder.encode(saved_birth_place);
            String saved_father_name = db.getFieldString("father_name", "person_id = '" + person_id + "'", "person");
            saved_father_name = URLEncoder.encode(saved_father_name);
            String saved_mother_name = db.getFieldString("mother_name", "person_id = '" + person_id + "'", "person");
            saved_mother_name = URLEncoder.encode(saved_mother_name);
            String saved_photo_file = db.getFieldString("photo_file", "person_id = '" + person_id + "'", "person");
            String saved_passport_no = db.getFieldString("passport_no", "person_id = '" + person_id + "'", "person");
            saved_passport_no = URLEncoder.encode(saved_passport_no);
            String saved_cdc_no = db.getFieldString("cdc_no", "person_id = '" + person_id + "'", "person");
            saved_cdc_no = URLEncoder.encode(saved_cdc_no);
            String saved_indos_no = db.getFieldString("indos_no", "person_id = '" + person_id + "'", "person");
            saved_indos_no = URLEncoder.encode(saved_indos_no);
            String saved_height = db.getFieldString("height", "person_id = '" + person_id + "'", "person");
            saved_height = URLEncoder.encode(saved_height);
            String saved_weight = db.getFieldString("weight", "person_id = '" + person_id + "'", "person");
            saved_weight = URLEncoder.encode(saved_weight);
            String saved_entry_date = db.getFieldString("enrtry_date", "person_id = '" + person_id + "'", "person");
            saved_entry_date = URLEncoder.encode(saved_entry_date);
            String saved_marks = db.getFieldString("marks", "person_id = '" + person_id + "'", "person");
            saved_marks = URLEncoder.encode(saved_marks);
            String saved_blood_group = db.getFieldString("blood_group", "person_id = '" + person_id + "'", "person");
            saved_blood_group = URLEncoder.encode(saved_blood_group);
            String saved_emergency_contact_name = db.getFieldString("emergency_contact_name", "person_id = '" + person_id + "'", "person");
            saved_emergency_contact_name = URLEncoder.encode(saved_emergency_contact_name);
            String saved_emergency_contact_address = db.getFieldString("emergency_contact_address", "person_id = '" + person_id + "'", "person");
            saved_emergency_contact_address = URLEncoder.encode(saved_emergency_contact_address);
            String saved_emergency_contact_no = db.getFieldString("emergency_contact_no", "person_id = '" + person_id + "'", "person");
            saved_emergency_contact_no = URLEncoder.encode(saved_emergency_contact_no);
            String saved_emergency_relationship = db.getFieldString("emergency_relationship", "person_id = '" + person_id + "'", "person");
            saved_emergency_relationship = URLEncoder.encode(saved_emergency_relationship);
            String saved_nationality = db.getFieldString("nationality", "person_id = '" + person_id + "'", "person");
            saved_nationality = URLEncoder.encode(saved_nationality);
            String saved_passport_no_issue_date = db.getFieldString("passport_no_issue_date", "person_id = '" + person_id + "'", "person");
            saved_passport_no_issue_date = URLEncoder.encode(saved_passport_no_issue_date);
            String saved_cdc_no_issue_date = db.getFieldString("cdc_no_issue_date", "person_id = '" + person_id + "'", "person");
            saved_cdc_no_issue_date = URLEncoder.encode(saved_cdc_no_issue_date);
            String saved_cdc_no_issue_place = db.getFieldString("cdc_no_issue_place", "person_id = '" + person_id + "'", "person");
            saved_cdc_no_issue_place = URLEncoder.encode(saved_cdc_no_issue_place);
            String saved_srn = db.getFieldString("srn", "person_id = '" + person_id + "'", "person");
            saved_srn = URLEncoder.encode(saved_srn);
            String saved_sid_no = db.getFieldString("sid_no", "person_id = '" + person_id + "'", "person");
            saved_sid_no = URLEncoder.encode(saved_sid_no);
            String saved_srb_no = db.getFieldString("srb_no", "person_id = '" + person_id + "'", "person");
            saved_srb_no = URLEncoder.encode(saved_srb_no);
            String saved_srb_date = db.getFieldString("srb_date", "person_id = '" + person_id + "'", "person");
            saved_srb_date = URLEncoder.encode(saved_srb_date);

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person&id="+person_id+"&st_address="+saved_st_address+"&email="+saved_email+"&phone="+saved_phone+"&mobile="+saved_mobile+"&birth_date="+saved_birth_date+"&birth_place="+saved_birth_place+"&father_name="+saved_father_name+"&mother_name="+saved_mother_name+"&photo_file="+saved_photo_file+"&passport_no="+saved_passport_no+"&cdc_no="+saved_cdc_no+"&indos_no="+saved_indos_no+"&height="+saved_height+"&weight="+saved_weight+"&entry_date="+saved_entry_date+"&marks="+saved_marks+"&blood_group="+saved_blood_group+"&emergency_contact_name="+saved_emergency_contact_name+"&emergency_contact_address="+saved_emergency_contact_address+"&emergency_contact_no="+saved_emergency_contact_no+"&emergency_relationship="+saved_emergency_relationship+"&nationality="+saved_nationality+"&passport_no_issue_date="+saved_passport_no_issue_date+"&cdc_no_issue_date="+saved_cdc_no_issue_date+"&cdc_no_issue_place="+saved_cdc_no_issue_place+ "&srn="+saved_srn+ "&sid_no="+saved_sid_no + "&srb_no="+saved_srb_no+ "&srb_date="+saved_srb_date);
            Log.d("CONNECT", url +"sync.php?table=person&id="+person_id+"&st_address="+saved_st_address+"&email="+saved_email+"&phone="+saved_phone+"&mobile="+saved_mobile+"&birth_date="+saved_birth_date+"&birth_place="+saved_birth_place+"&father_name="+saved_father_name+"&mother_name="+saved_mother_name+"&photo_file="+saved_photo_file+"&passport_no="+saved_passport_no+"&cdc_no="+saved_cdc_no+"&indos_no="+saved_indos_no+"&height="+saved_height+"&weight="+saved_weight+"&entry_date="+saved_entry_date+"&marks="+saved_marks+"&blood_group="+saved_blood_group+"&emergency_contact_name="+saved_emergency_contact_name+"&emergency_contact_address="+saved_emergency_contact_address+"&emergency_contact_no="+saved_emergency_contact_no+"&emergency_relationship="+saved_emergency_relationship+"&nationality="+saved_nationality+"&passport_no_issue_date="+saved_passport_no_issue_date+"&cdc_no_issue_date="+saved_cdc_no_issue_date+"&cdc_no_issue_place="+saved_cdc_no_issue_place);

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
            Intent intent = new Intent(PersonUpdateActivity.this, CadetParticularActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(PersonUpdateActivity.this, "Record saved successfully.", Toast.LENGTH_LONG).show();

        }
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
                    Intent intent = new Intent(PersonUpdateActivity.this, CadetParticularActivity.class);
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
