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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

public class EducationalRecordFormActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title;
    LinearLayout subjects_container;
    ProgressDialog pd;
    DatabaseHelper db;

    String person_id, person_education_h_id = "", gce_level;
    TextInputLayout et_school_name, et_school_address, et_certificate_date;
    Spinner spinner;
    List<String> levelArray =  new ArrayList<>();
    Button btn_add_subj, btn_cancel, btn_delete, btn_save;

    String str = "", err_message, photo_file;
    HttpResponse response;
    JSONObject json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educational_record_form);

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
        if (intent.hasExtra("person_education_h_id")) {
            person_education_h_id = intent.getStringExtra("person_education_h_id");
        }

        levelArray.add("Select Level *");
        levelArray.add("Secondary");
        levelArray.add("Tertiary");
        levelArray.add("Vocational");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, levelArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gce_level  = parent.getItemAtPosition(position).toString();
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(15);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_school_name = findViewById(R.id.et_school_name);
        et_school_address = findViewById(R.id.et_school_address);
        et_certificate_date = findViewById(R.id.et_certificate_date);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_delete = findViewById(R.id.btn_delete);
        btn_save = findViewById(R.id.btn_save);

        final MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker mdp = materialDateBuilder.build();
        et_certificate_date.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });
        et_certificate_date.getEditText().setOnClickListener(new View.OnClickListener() {
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
                            et_certificate_date.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");
                            if(day.length() == 1){
                                day = "0"+ day;
                            }
                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_certificate_date.getEditText().setText(completed);
                        }
                    }
                });

        if(person_education_h_id.equals("")){ //ADD
            btn_delete.setVisibility(View.GONE);
            person_education_h_id = db.newId();
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String school_name = et_school_name.getEditText().getText().toString();
                    String school_address = et_school_address.getEditText().getText().toString();
                    String certificate_date = et_certificate_date.getEditText().getText().toString();

                    if(gce_level.equals("Select GCE Level *")){
                        Toast.makeText(EducationalRecordFormActivity.this, "Please select a GCE Level.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(school_name.equals("")){
                        Toast.makeText(EducationalRecordFormActivity.this, "School name is required.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    pd = new ProgressDialog(EducationalRecordFormActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();

                    Integer id = db.newIntegerId("person_education_h");

                    db.query("INSERT INTO person_education_h (id, person_education_h_id, person_id, gce_level, school_name, school_address, certificate_date, login_id, last_update) VALUES ("+id+", '"+person_education_h_id+"', '"+person_id+"', '"+gce_level+"', '"+school_name+"', '"+school_address+"', '"+certificate_date+"', '"+person_id+"', datetime('now', 'localtime'))");

                    int conn = getConnection.getConnectionType(EducationalRecordFormActivity.this);
                    if(conn != 0){ //WITH NET
                        new SyncOnline(context).execute();
                    }else{
                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_education_h', '" + person_education_h_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                        pd.dismiss();
                        Intent intent = new Intent(EducationalRecordFormActivity.this, EducationalRecordActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(EducationalRecordFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();
                    }


                }
            });
        }else{ //UPDATE
            btn_delete.setVisibility(View.VISIBLE);
            String saved_gce_level = db.getFieldString("gce_level", " person_education_h_id = '"+person_education_h_id+"'", "person_education_h");
            String saved_school_name = db.getFieldString("school_name", " person_education_h_id = '"+person_education_h_id+"'", "person_education_h");
            String saved_school_address = db.getFieldString("school_address", " person_education_h_id = '"+person_education_h_id+"'", "person_education_h");
            String saved_certificate_date = db.getFieldString("certificate_date", " person_education_h_id = '"+person_education_h_id+"'", "person_education_h");

            spinner.setSelection(arrayAdapter.getPosition(saved_gce_level));
            et_school_name.getEditText().setText(saved_school_name);
            et_school_address.getEditText().setText(saved_school_address);
            et_certificate_date.getEditText().setText(saved_certificate_date);

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String school_name = et_school_name.getEditText().getText().toString();
                    String school_address = et_school_address.getEditText().getText().toString();
                    String certificate_date = et_certificate_date.getEditText().getText().toString();

                    if(gce_level.equals("Select GCE Level *")){
                        Toast.makeText(EducationalRecordFormActivity.this, "Please select a GCE Level.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(school_name.equals("")){
                        Toast.makeText(EducationalRecordFormActivity.this, "School name is required.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    pd = new ProgressDialog(EducationalRecordFormActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();

                    db.query("UPDATE person_education_h SET school_name = '"+school_name+"', school_address ='"+school_address+"', gce_level = '"+gce_level+"', certificate_date ='"+certificate_date+"' WHERE person_education_h_id = '"+person_education_h_id+"'");
                    int conn = getConnection.getConnectionType(EducationalRecordFormActivity.this);
                    if(conn != 0){ //WITH NET
                        new SyncOnlineUpdate(context).execute();
                    }else{

                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_education_h', '" + person_education_h_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");
                        pd.dismiss();
                        Intent intent = new Intent(EducationalRecordFormActivity.this, EducationalRecordActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(EducationalRecordFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();
                    }


                }
            });

        }

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EducationalRecordFormActivity.this);

                alertDialogBuilder.setMessage("Are you sure you want to delete this record?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        delete(person_education_h_id);
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
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(EducationalRecordFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(EducationalRecordFormActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(EducationalRecordFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(EducationalRecordFormActivity.this, R.color.black));

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EducationalRecordFormActivity.this);

                alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved.");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(EducationalRecordFormActivity.this, EducationalRecordActivity.class);
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
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(EducationalRecordFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(EducationalRecordFormActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(EducationalRecordFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(EducationalRecordFormActivity.this, R.color.black));
            }
        });


        //get_subjects();
    }

    public void get_subjects(){
        subjects_container.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(EducationalRecordFormActivity.this);
        linearLayout.setLayoutParams(layoutParams1);

        TextView textView = new TextView(EducationalRecordFormActivity.this);
        textView.setLayoutParams(layoutParams);
        textView.setText("Subjects");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(15);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(textView);

        textView = new TextView(EducationalRecordFormActivity.this);
        textView.setLayoutParams(layoutParams2);
        textView.setText("Grade/Marks");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(15);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(textView);

        textView = new TextView(EducationalRecordFormActivity.this);
        textView.setLayoutParams(layoutParams2);
        textView.setText("");
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(textView);
        subjects_container.addView(linearLayout);

        int cnt = db.GetCount("person_education_d", " WHERE person_education_h_id = '"+person_education_h_id+"'");
        if(cnt > 0){
            List<PersonEducationD> personEducationDS = db.getPersonEducationD(person_education_h_id);
            for(final PersonEducationD personEducationD : personEducationDS){
                linearLayout = new LinearLayout(EducationalRecordFormActivity.this);
                linearLayout.setLayoutParams(layoutParams1);

                final TextView textView2 = new TextView(EducationalRecordFormActivity.this);
                textView2.setLayoutParams(layoutParams);
                textView2.setText(URLDecoder.decode(personEducationD.getSubject()));
                textView2.setTextSize(15);
                textView2.setTextColor(getResources().getColor(R.color.black));
                textView2.setGravity(Gravity.LEFT);
                textView2.setPadding(10,10,10,10);
                textView2.setBackgroundResource(R.drawable.border);
                linearLayout.addView(textView2);

                final TextView textView1 = new TextView(EducationalRecordFormActivity.this);
                textView1.setLayoutParams(layoutParams2);
                textView1.setText("" + personEducationD.getGrade());
                textView1.setTextSize(15);
                textView1.setTextColor(getResources().getColor(R.color.black));
                textView1.setGravity(Gravity.CENTER_HORIZONTAL);
                textView1.setPadding(10,10,10,10);
                textView1.setBackgroundResource(R.drawable.border);
                linearLayout.addView(textView1);

                final TextView textView3 = new TextView(EducationalRecordFormActivity.this);
                textView3.setLayoutParams(layoutParams2);
                textView3.setText("Delete");
                textView3.setTextSize(15);
                textView3.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                textView3.setGravity(Gravity.CENTER_HORIZONTAL);
                textView3.setPadding(10,10,10,10);
                textView3.setBackgroundResource(R.drawable.border);
                linearLayout.addView(textView3);

                textView3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delete_person_education_d(personEducationD.getPerson_education_d_id());
                        }
                });

                textView2.post(new Runnable() {
                        @Override
                        public void run() {
                            int height = textView2.getLineCount() * textView3.getMeasuredHeight();
                            Log.d("HI", "" + height + " - " + textView3.getMeasuredHeight());
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView2.getLayoutParams();
                            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) textView3.getLayoutParams();

                            params.height = height;
                            params2.height = height;

                            textView2.setLayoutParams(params);
                            textView3.setLayoutParams(params2);
                            textView1.setLayoutParams(params2);
                        }
                    });



                subjects_container.addView(linearLayout);
            }
        }else{
            linearLayout = new LinearLayout(EducationalRecordFormActivity.this);
            linearLayout.setLayoutParams(layoutParams1);

            TextView textView2 = new TextView(EducationalRecordFormActivity.this);
            textView2.setLayoutParams(layoutParams);
            textView2.setText("No subject to display");
            textView2.setTextSize(15);
            textView2.setTextColor(getResources().getColor(R.color.black));
            textView2.setGravity(Gravity.LEFT);
            textView2.setPadding(10,10,10,10);
            textView2.setBackgroundResource(R.drawable.border);
            linearLayout.addView(textView2);
            subjects_container.addView(linearLayout);

        }
    }

    public void new_subject(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EducationalRecordFormActivity.this);
        alertDialogBuilder.setTitle("");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("CLOSE",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                    }
                });
        alertDialogBuilder.setPositiveButton("Save", null);
        alertDialogBuilder.setNegativeButton("Close", null);


        ScrollView main_linearLayout = new ScrollView( EducationalRecordFormActivity.this );

        LinearLayout linearlayout = new LinearLayout( EducationalRecordFormActivity.this );
        linearlayout.setPadding( 30, 30, 30, 30 );
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
        linearlayout.setOrientation(LinearLayout.VERTICAL);
        linearlayout.setLayoutParams( llParams );

        TextView tv_subject = new TextView(EducationalRecordFormActivity.this);
        tv_subject.setText("Subject *");
        tv_subject.setTextColor(Color.BLACK);
        tv_subject.setTextSize(18);
        tv_subject.setPadding(10,10,10,10);
        linearlayout.addView(tv_subject);

        final EditText et_tv_subject = new EditText(this);
        et_tv_subject.setLayoutParams(llParams);
        et_tv_subject.setBackgroundResource(R.drawable.border_circular);
        linearlayout.addView(et_tv_subject);

        TextView tv_grade = new TextView(EducationalRecordFormActivity.this);
        tv_grade.setText("Grade/Mark");
        tv_grade.setTextColor(Color.BLACK);
        tv_grade.setTextSize(18);
        tv_grade.setPadding(10,10,10,10);
        linearlayout.addView(tv_grade);

        final EditText et_grade = new EditText(this);
        et_grade.setLayoutParams(llParams);
        et_grade.setBackgroundResource(R.drawable.border_circular);
        et_grade.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        linearlayout.addView(et_grade);




        main_linearLayout.addView( linearlayout );

        alertDialogBuilder.setView( main_linearLayout );
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#000000"));
        positiveButton.setBackgroundColor(Color.parseColor("#FFFFFF"));

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = et_tv_subject.getText().toString();
                String grade = et_grade.getText().toString();

                if(subject.equals("")){
                   Toast.makeText(EducationalRecordFormActivity.this, "Subject is required", Toast.LENGTH_LONG).show();
                   return;
                }

                if(grade.equals("")){
                    Toast.makeText(EducationalRecordFormActivity.this, "Grade/Mark is required", Toast.LENGTH_LONG).show();
                    return;
                }
                String person_education_d_id = db.newId();
                Integer id = db.newIntegerId("person_education_d");
                db.query("INSERT INTO person_education_d (id, person_education_d_id, person_education_h_id, subject, grade) VALUES ("+ id +", '"+ person_education_d_id+"', '"+person_education_h_id+"', '"+ subject+"', '"+ grade+"')");

                int conn = getConnection.getConnectionType(EducationalRecordFormActivity.this);
                if(conn != 0){
                    new SyncOnlineD(context, person_education_d_id).execute();

                }else{
                    Integer backup_item_id = db.newIntegerId("backup_item");
                    db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_education_d', '" + person_education_d_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                }
                alertDialog.dismiss();
                get_subjects();
            }
        });

        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#000000"));
        negativeButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public void delete_person_education_d(final String person_education_d_id){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Are you sure you to delete this record?");
        alertDialogBuilder.setPositiveButton("Yes", null);
        alertDialogBuilder.setNegativeButton("Cancel", null);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.black));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.black));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.query("DELETE FROM person_education_d WHERE person_education_d_id = '"+person_education_d_id+"'");

                int conn = getConnection.getConnectionType(EducationalRecordFormActivity.this);
                if(conn != 0){
                    new SyncOnlineDelete(context, "person_education_d", person_education_d_id).execute();
                }else{
                    Integer backup_item_id = db.newIntegerId("backup_item");
                    db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_education_d', '" + person_education_d_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'DELETE', 'N')");
                }

                alertDialog.dismiss();
                get_subjects();
            }
        });

        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public void delete(String person_education_h_id){
        pd = new ProgressDialog(EducationalRecordFormActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Processing. Please wait .... ");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        db.query("DELETE FROM person_education_h WHERE person_education_h_id = '"+person_education_h_id+"'");
        db.query("DELETE FROM person_education_d WHERE person_education_h_id = '"+person_education_h_id+"'");

        int conn = getConnection.getConnectionType(EducationalRecordFormActivity.this);
        if(conn != 0){
            new SyncOnlineDelete(context,  "person_education_h", person_education_h_id).execute();
        }else{
            Integer backup_item_id = db.newIntegerId("backup_item");
            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_education_h', '" + person_education_h_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'DELETE', 'N')");
            pd.dismiss();

            Intent intent = new Intent(EducationalRecordFormActivity.this, EducationalRecordActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(EducationalRecordFormActivity.this, "Record successfully deleted.", Toast.LENGTH_LONG).show();
        }

    }

    private class SyncOnline extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public SyncOnline(Context context)
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

            String saved_person_id = db.getFieldString("person_id", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            String saved_gce_level = db.getFieldString("gce_level", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            saved_gce_level = URLEncoder.encode(saved_gce_level);
            String saved_school_name = db.getFieldString("school_name", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            saved_school_name = URLEncoder.encode(saved_school_name);
            String saved_school_address = db.getFieldString("school_address", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            saved_school_address = URLEncoder.encode(saved_school_address);
            String saved_certificate_date = db.getFieldString("certificate_date", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            saved_certificate_date = URLEncoder.encode(saved_certificate_date);
            String saved_login_id = db.getFieldString("login_id", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            saved_login_id = URLEncoder.encode(saved_login_id);
            String saved_last_update = db.getFieldString("last_update", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            saved_last_update = URLEncoder.encode(saved_last_update);

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_education_h&id="+person_education_h_id+"&person_id="+saved_person_id+"&gce_level="+saved_gce_level+"&school_name="+saved_school_name+"&school_address="+saved_school_address+"&certificate_date="+saved_certificate_date+"&login_id="+saved_login_id+"&last_update="+saved_last_update + "&event=ADD");
            Log.d("CONNECT", url +"sync.php?table=person_education_h&id="+person_education_h_id+"&person_id="+saved_person_id+"&gce_level="+saved_gce_level+"&school_name="+saved_school_name+"&school_address="+saved_school_address+"&certificate_date="+saved_certificate_date+"&login_id="+saved_login_id+"&last_update="+saved_last_update + "&event=ADD");

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
            Intent intent = new Intent(EducationalRecordFormActivity.this, EducationalRecordActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(EducationalRecordFormActivity.this, "Record saved successfully.", Toast.LENGTH_LONG).show();

        }
    }

    private class SyncOnlineD extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public String person_education_d_id;
        public SyncOnlineD(Context context, String id)
        {
            this.context = context;
            this.person_education_d_id = id;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            String url = getString(R.string.url);
            //GET FROM TBL

            String saved_person_education_h_id = db.getFieldString("person_education_h_id", "person_education_d_id = '" + person_education_d_id + "'", "person_education_d");
            String saved_subject = db.getFieldString("subject", "person_education_d_id = '" + person_education_d_id + "'", "person_education_d");
            saved_subject = URLEncoder.encode(saved_subject);
            String saved_grade = db.getFieldString("grade", "person_education_d_id = '" + person_education_d_id + "'", "person_education_d");

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_education_d&id="+person_education_d_id+"&person_education_h_id="+saved_person_education_h_id+"&subject="+saved_subject+"&grade="+saved_grade+"&event=ADD");
            Log.d("CONNECT", url +"sync.php?table=person_education_d&id="+person_education_d_id+"&person_education_h_id="+saved_person_education_h_id+"&subject="+saved_subject+"&grade="+saved_grade+"&event=ADD");

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
            return;
        }
    }

    private class SyncOnlineUpdate extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public SyncOnlineUpdate(Context context)
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
            String saved_person_id = db.getFieldString("person_id", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            String saved_gce_level = db.getFieldString("gce_level", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            saved_gce_level = URLEncoder.encode(saved_gce_level);
            String saved_school_name = db.getFieldString("school_name", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            saved_school_name = URLEncoder.encode(saved_school_name);
            String saved_school_address = db.getFieldString("school_address", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            saved_school_address = URLEncoder.encode(saved_school_address);
            String saved_certificate_date = db.getFieldString("certificate_date", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            saved_certificate_date = URLEncoder.encode(saved_certificate_date);
            String saved_login_id = db.getFieldString("login_id", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            saved_login_id = URLEncoder.encode(saved_login_id);
            String saved_last_update = db.getFieldString("last_update", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
            saved_last_update = URLEncoder.encode(saved_last_update);

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_education_h&id="+person_education_h_id+"&person_id="+saved_person_id+"&gce_level="+saved_gce_level+"&school_name="+saved_school_name+"&school_address="+saved_school_address+"&certificate_date="+saved_certificate_date+"&login_id="+saved_login_id+"&last_update="+saved_last_update + "&event=UPDATE");
            Log.d("CONNECT", url +"sync.php?table=person_education_h&id="+person_education_h_id+"&person_id="+saved_person_id+"&gce_level="+saved_gce_level+"&school_name="+saved_school_name+"&school_address="+saved_school_address+"&certificate_date="+saved_certificate_date+"&login_id="+saved_login_id+"&last_update="+saved_last_update + "&event=UPDATE");

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
            Intent intent = new Intent(EducationalRecordFormActivity.this, EducationalRecordActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(EducationalRecordFormActivity.this, "Record saved successfully.", Toast.LENGTH_LONG).show();

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
            if(table.equals("person_education_h")){
                pd.dismiss();
                Intent intent = new Intent(EducationalRecordFormActivity.this, EducationalRecordActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(EducationalRecordFormActivity.this, "Record deleted successfully.", Toast.LENGTH_LONG).show();
            }else{
                return;
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
                Intent intent = new Intent(EducationalRecordFormActivity.this, EducationalRecordActivity.class);
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
