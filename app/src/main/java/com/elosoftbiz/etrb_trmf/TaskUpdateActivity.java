package com.elosoftbiz.etrb_trmf;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.gcacace.signaturepad.views.SignaturePad;
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskUpdateActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    DatabaseHelper db;

    LinearLayout files_container, approval_container;
    ProgressDialog pd;
    String dept, person_id, task_type;
    String trb_competence_id, person_task_id, task_id, person_task_file_id, passed, type="";
    String trb_function_id, trb_topic_id, trb_task_group_id;
    TextView tv_desc_task, tvTaskDetails, tv_fileName, tv_notice, createNR;
    TextView tv_checked_by_id, tv_officer_remark, tv_date_checked, tvSig;
    TextInputLayout et_completed, et_answers, et_lat_long, et_checked_remarks;
    CheckBox cb_not_app, cb_passed;
    Button btn_save, btn_back, btn_upload, btn_revise, btn_approve;
    String imageFileName = "", currentPhotoPath;

    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_TAKE_PHOTO = 1;

    String str = "", err_message, upLoadServerUri, path;
    HttpResponse response;
    int serverResponseCode = 0;
    SignaturePad signaturePad;
    private boolean isSignatured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_update);

        context = this;

        /* LOGO AND HEADER NAME HERE */
        mToolbar = findViewById( R.id.nav_action );
        setSupportActionBar( mToolbar );
        mDrawerLayout = findViewById( R.id.drawerLayout );
        mToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.open, R.string.close );
        mDrawerLayout.addDrawerListener( mToggle );
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        /* END OF LOGO AND HEADER NAME ***/

        Intent intent = getIntent();
        person_task_id = intent.getStringExtra("person_task_id");
        if (intent.hasExtra("type")) {
            type = intent.getStringExtra("type");
        }
        task_type = intent.getStringExtra("task_type");

        upLoadServerUri = getString(R.string.url) + "upload_task_files.php";

        db = new DatabaseHelper(this);
        person_id = db.getCadetId();
        dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");
        /* START MENU *******/
        navigationView = findViewById(R.id.navigation_view);

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
        /* END MENU *******/

        task_id = db.getFieldString("task_id", "person_task_id = '"+person_task_id+"'", "person_task");
        passed = db.getFieldString("passed", "person_task_id = '"+person_task_id+"'", "person_task");
        trb_competence_id = db.getFieldString("trb_competence_id", "task_id='"+task_id+"'", "task");
        trb_function_id = db.getFieldString("trb_function_id", "task_id='"+task_id+"'", "task");
        trb_topic_id = db.getFieldString("trb_topic_id", "task_id='"+task_id+"'", "task");
        trb_task_group_id = db.getFieldString("trb_task_group_id", "task_id='"+task_id+"'", "task");

        String desc_task = db.getFieldString("desc_task", "task_id='"+task_id+"'", "task");
        String criteria = db.getFieldString("criteria", "task_id='"+task_id+"'", "task");
        criteria = URLDecoder.decode(criteria);
        String ref_no =  db.getFieldString("ref_no_task", "task_id='"+task_id+"'", "task");
        //Integer phase_no = db.getFieldInt("phase_no", "task_id='"+task_id+"'", "task");

        String answers = db.getFieldString("answers", "person_task_id = '"+person_task_id+"'", "person_task");
        try {
            answers = URLDecoder.decode(answers, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String completed = db.getFieldString("completed", "person_task_id = '"+person_task_id+"'", "person_task");
        String not_app = db.getFieldString("not_app", "person_task_id = '"+person_task_id+"'", "person_task");
        String lat_long = db.getFieldString("lat_long", "person_task_id = '"+person_task_id+"'", "person_task");
        try {
            lat_long = URLDecoder.decode(lat_long, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);
        btn_upload = findViewById(R.id.btn_upload);
        btn_revise = findViewById(R.id.btn_revise);
        btn_approve = findViewById(R.id.btn_approve);
        signaturePad = findViewById(R.id.signaturePad);
        et_checked_remarks = findViewById(R.id.et_checked_remarks);
        tvSig = findViewById(R.id.tvSig);

        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                isSignatured = true;
            }

            @Override
            public void onClear() {
                isSignatured = false;
            }
        });

        int cnt = db.GetCount("shipboard", "WHERE person_id = '"+person_id+"'");
        if(cnt > 0){
            if(type.equals("approve")){
                btn_save.setVisibility(View.GONE);
                btn_upload.setVisibility(View.GONE);
                btn_back.setVisibility(View.GONE);
                btn_revise.setVisibility(View.VISIBLE);
                btn_approve.setVisibility(View.VISIBLE);
                signaturePad.setVisibility(View.VISIBLE);
                et_checked_remarks.setVisibility(View.VISIBLE);
            }else{
                btn_save.setVisibility(View.VISIBLE);
                btn_upload.setVisibility(View.VISIBLE);
                btn_back.setVisibility(View.VISIBLE);
                btn_revise.setVisibility(View.GONE);
                btn_approve.setVisibility(View.GONE);
                signaturePad.setVisibility(View.GONE);
                et_checked_remarks.setVisibility(View.GONE);
                tvSig.setVisibility(View.GONE);
            }

        }else {
            btn_save.setVisibility(View.GONE);
            btn_back.setVisibility(View.GONE);
            btn_upload.setVisibility(View.GONE);
        }
        files_container = findViewById(R.id.files_container);
        approval_container = findViewById(R.id.approval_container);

        tv_desc_task = findViewById(R.id.tv_desc_task);
        try {
            desc_task = URLDecoder.decode(desc_task, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(cnt > 0){
            tv_desc_task.setText(ref_no + " - " + desc_task + "\n\nSTANDARD CRITERIA FOR THIS TASK:\n"+ criteria);
        }else{
            tv_desc_task.setText(ref_no + " - " + desc_task + "\n\nSTANDARD CRITERIA FOR THIS TASK:\n"+ criteria + "\n\n *Please add a record to your Sea Service first*");
        }
        tvTaskDetails = findViewById(R.id.tvTaskDetails);
        String desc_function = db.getFieldString("desc_trb_function", "trb_function_id = '"+trb_function_id+"'", "trb_function");
        String desc_competence = db.getFieldString("desc_competence", "trb_competence_id = '"+trb_competence_id+"'", "trb_competence");
        desc_competence = URLDecoder.decode(desc_competence);
        String desc_topic = db.getFieldString("desc_topic", "trb_topic_id = '"+trb_topic_id+"'", "trb_topic");
        desc_topic = URLDecoder.decode(desc_topic);
        desc_topic = URLDecoder.decode(desc_topic);
        String desc_task_group = db.getFieldString("desc_task_group", "trb_task_group_id = '"+trb_task_group_id+"'", "trb_task_group");
        desc_task_group = URLDecoder.decode(desc_task_group);
        tvTaskDetails.setText("Function : " + desc_function + "\nCompetence : " + desc_competence + "\nKUP : "+desc_topic + "\nTask : "+desc_task_group  );

        tv_notice = findViewById(R.id.tv_notice);
        int cnt2 = db.GetCount("person_officer", "");
        if(cnt2 > 0){
            tv_notice.setVisibility(View.GONE);
        }else{
            tv_notice.setVisibility(View.VISIBLE);
            tv_notice.setTypeface(null, Typeface.BOLD);
        }
        tv_desc_task.setBackgroundResource(R.drawable.border_dark_gray);
        tv_desc_task.setTextColor(Color.WHITE);

        final MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        et_completed = findViewById(R.id.et_completed);
        et_completed.getEditText().setText(completed);
        et_completed.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

            }
        });
        et_completed.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        String dateSelected = "" + materialDatePicker.getHeaderText();
                        if(dateSelected.equals("Selected Date")){
                            et_completed.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");

                            if(day.length() == 1){
                                day = "0"+ day;
                            }

                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_completed.getEditText().setText(completed);
                        }
                    }
                });

        et_answers = findViewById(R.id.et_answers);
        et_answers.getEditText().setText(answers);
        et_lat_long = findViewById(R.id.et_lat_long);
        et_lat_long.getEditText().setText(lat_long);
        cb_not_app = findViewById(R.id.cb_not_app);
        if(not_app.equals("Y")){
            cb_not_app.setChecked(true);
        }else{
            cb_not_app.setChecked(false);
        }
        tv_officer_remark = findViewById(R.id.tv_officer_remark);
        String officer_remarks = db.getFieldString("officer_remarks", "person_task_id = '"+person_task_id+"'", "person_task");
        try {
            officer_remarks = URLDecoder.decode(officer_remarks, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(officer_remarks.equals("")){
            tv_officer_remark.setVisibility(View.GONE);
        }else{
            tv_officer_remark.setText("Onboard Training Supervisor Remarks : " + officer_remarks);
        }



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(TaskUpdateActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("Loading. Please wait .... ");
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.show();

                String new_answers = et_answers.getEditText().getText().toString();
                try {
                    new_answers = URLEncoder.encode(new_answers, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String new_completed = et_completed.getEditText().getText().toString();
                String new_lat_long = et_lat_long.getEditText().getText().toString();
                try {
                    new_lat_long = URLEncoder.encode(new_lat_long, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String new_not_app = "N";
                if(cb_not_app.isChecked()){
                    new_not_app = "Y";
                }
                String vessel_id = db.getFieldString("vessel_id", "person_id = '"+person_id+"' ORDER BY sign_on DESC LIMIT 1", "shipboard");
                String vessel_type_id = db.getFieldString("vessel_type_id", "vessel_id = '"+vessel_id+"'", "vessel");

                db.query("UPDATE person_task SET completed = '"+new_completed+"', answers = '"+new_answers+"', not_app = '"+new_not_app+"', lat_long = '"+new_lat_long+"', vessel_type_id = '"+vessel_type_id+"', vessel_id = '"+vessel_id+"', for_app = 'Y' WHERE person_task_id = '"+person_task_id+"'");

                int conn = getConnection.getConnectionType(TaskUpdateActivity.this);
                if(conn != 0){//WITH CONN
                    new SyncOnline(context).execute();
                }else{
                    Integer backup_item_id = db.newIntegerId("backup_item");
                    db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_task', '" + person_task_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");
                    pd.dismiss();
                    Intent intent1 = new Intent(TaskUpdateActivity.this, PersonTaskActivity.class);
                    intent1.putExtra("id", trb_competence_id);
                    intent1.putExtra("task_type", task_type);
                    startActivity(intent1);
                    finish();
                    Toast.makeText(TaskUpdateActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();
                    pd.dismiss();

                }
                //new generate(context).execute();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskUpdateActivity.this, PersonTaskActivity.class);
                intent.putExtra("id", trb_competence_id);
                startActivity(intent);
                finish();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                person_task_file_id = db.newId();
                uploadFile();
            }
        });

        createNR = findViewById(R.id.createNR);
        createNR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ScrollView scrollView = new ScrollView(context);
                scrollView.setLayoutParams(layoutParams);
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setOrientation(LinearLayout.VERTICAL);


                TextView textView = new TextView(context);
                textView.setText("Cadet's Guidance");
                textView.setPadding(20,20, 20,10);
                textView.setTextColor(getColor(R.color.black));
                textView.setTextSize(20);

                linearLayout.addView(textView);

                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.narrative_report1);
                imageView.setAdjustViewBounds(true);
                linearLayout.addView(imageView);

                imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.narrative_report2);
                imageView.setAdjustViewBounds(true);
                linearLayout.addView(imageView);

                imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.narrative_report3);
                imageView.setAdjustViewBounds(true);
                linearLayout.addView(imageView);

                scrollView.addView(linearLayout);
                alertDialogBuilder.setView(scrollView);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black));

            }
        });
        //CHECK IF ALREADY APPROVED
        passed = db.getFieldString("passed", "person_task_id = '"+person_task_id+"'", "person_task");
        if(passed.equals("Y")){
            approval_container.setVisibility(View.VISIBLE);
            cb_passed = findViewById(R.id.cb_passed);
            tv_checked_by_id = findViewById(R.id.tv_checked_by_id);
            tv_date_checked = findViewById(R.id.tv_date_checked);
            signaturePad.setVisibility(View.GONE);
            et_checked_remarks.setVisibility(View.GONE);
            btn_approve.setVisibility(View.GONE);
            btn_revise.setVisibility(View.GONE);
            tvSig.setVisibility(View.GONE);

            cb_passed.setChecked(true);
            String checked_by_id = db.getFieldString("checked_by_id", "person_task_id = '"+person_task_id+"'", "person_task");
            String checked_by_lname = db.getFieldString("lname"," person_id = '"+checked_by_id+"'","person");
            String checked_by_fname = db.getFieldString("fname"," person_id = '"+checked_by_id+"'","person");
            String date_checked = db.getFieldString("date_checked", "person_task_id = '"+person_task_id+"'", "person_task");


            tv_checked_by_id.setText("Checked By : " + checked_by_lname + ", " + checked_by_fname);
            tv_date_checked.setText("Date Checked : " + date_checked);


            btn_save.setVisibility(View.GONE);
            btn_upload.setVisibility(View.GONE);
        }else{
            approval_container.setVisibility(View.GONE);
        }
        get_files();

        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSignatured){
                    Bitmap bitmap = signaturePad.getSignatureBitmap();
                    path = saveImage(bitmap);

                    Intent intent = new Intent(context, TaskUpdateActivity.class);
                    intent.putExtra("person_task_id", person_task_id);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(context, "Your signature is required.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        btn_revise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSignatured){
                    Bitmap bitmap = signaturePad.getSignatureBitmap();
                    path = saveImage2(bitmap);

                    Intent intent = new Intent(context, TaskUpdateActivity.class);
                    intent.putExtra("person_task_id", person_task_id);
                    startActivity(intent);
                    finish();
                }else{
                    String vessel_id = db.getCadetVessel(person_id);
                    String checked_by_id = db.getFieldString("assessor_id", "person_id = '"+person_task_id+"' AND vessel_id = '"+vessel_id+"'", "person_officer");
                    String checked_remarks = et_checked_remarks.getEditText().getText().toString();

                    db.execQuery("UPDATE person_task SET esig_file = '"+ person_task_id + "_esig.jpg" +"', checked_by_id = '"+checked_by_id+"', date_checked = DATE(), officer_remarks = '"+checked_remarks+"', for_app ='N', passed = 'N', passed2 = 'N' WHERE person_task_id = '"+person_task_id+"'");
                    Intent intent = new Intent(context, TaskUpdateActivity.class);
                    intent.putExtra("person_task_id", person_task_id);
                    startActivity(intent);
                    finish();
                }
            }
        });
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
            String saved_task_id = db.getFieldString("task_id", "person_task_id = '" + person_task_id + "'", "person_task");
            String saved_person_id = db.getFieldString("person_id", "person_task_id = '" + person_task_id + "'", "person_task");
            String saved_completed = db.getFieldString("completed", "person_task_id = '" + person_task_id + "'", "person_task");
            String saved_answers = db.getFieldString("answers", "person_task_id = '" + person_task_id + "'", "person_task");
            try {
                saved_answers = URLEncoder.encode(saved_answers, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String saved_passed = db.getFieldString("passed", "person_task_id = '" + person_task_id + "'", "person_task");
            String saved_img_file = db.getFieldString("img_file", "person_task_id = '" + person_task_id + "'", "person_task");
            try {
                saved_img_file = URLEncoder.encode(saved_img_file, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String saved_not_app = db.getFieldString("not_app", "person_task_id = '" + person_task_id + "'", "person_task");
            String saved_lat_long = db.getFieldString("lat_long", "person_task_id = '" + person_task_id + "'", "person_task");
            try {
                saved_lat_long = URLEncoder.encode(saved_lat_long, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String saved_vessel_type_id = db.getFieldString("vessel_type_id", "person_task_id = '" + person_task_id + "'", "person_task");
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_task_id = '" + person_task_id + "'", "person_task");
            String saved_app_by_id = db.getFieldString("app_by_id", "person_task_id = '" + person_task_id + "'", "person_task");
            String saved_date_checked = db.getFieldString("date_checked", "person_task_id = '" + person_task_id + "'", "person_task");
            String saved_date_app = db.getFieldString("date_app", "person_task_id = '" + person_task_id + "'", "person_task");

            String saved_officer_remarks = db.getFieldString("officer_remarks", "person_task_id = '" + person_task_id + "'", "person_task");
            try {
                saved_officer_remarks = URLEncoder.encode(saved_officer_remarks, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String saved_app_remarks = db.getFieldString("app_remarks", "person_task_id = '" + person_task_id + "'", "person_task");
            try {
                saved_app_remarks = URLEncoder.encode(saved_app_remarks, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String saved_vessel_id = db.getFieldString("vessel_id", "person_task_id = '" + person_task_id + "'", "person_task");

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_task&id="+person_task_id+"&task_id="+saved_task_id+"&person_id="+saved_person_id+"&completed="+saved_completed+"&answers="+saved_answers+"&passed="+saved_passed+"&img_file="+saved_img_file+"&not_app="+saved_not_app+"&lat_long="+saved_lat_long+"&vessel_type_id="+saved_vessel_type_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&officer_remarks="+saved_officer_remarks+"&app_remarks="+saved_app_remarks+"&vessel_id="+saved_vessel_id+ "&event=UPDATE");
            Log.d("CONNECT", url +"sync.php?table=person_task&id="+person_task_id+"&task_id="+saved_task_id+"&person_id="+saved_person_id+"&completed="+saved_completed+"&answers="+saved_answers+"&passed="+saved_passed+"&img_file="+saved_img_file+"&not_app="+saved_not_app+"&lat_long="+saved_lat_long+"&vessel_type_id="+saved_vessel_type_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&officer_remarks="+saved_officer_remarks+"&app_remarks="+saved_app_remarks+"&vessel_id="+saved_vessel_id+"&event=UPDATE");

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
            Intent intent = new Intent(TaskUpdateActivity.this, PersonTaskActivity.class);
            intent.putExtra("id", trb_competence_id);
            intent.putExtra("task_type", task_type);
            startActivity(intent);
            finish();
            Toast.makeText(TaskUpdateActivity.this, "Record saved successfully.", Toast.LENGTH_LONG).show();

        }
    }

    private class SyncOnline2 extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public String id;
        public SyncOnline2(Context context, String id)
        {
            this.context = context;
            this.id = id;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0){
            String url = getString(R.string.url);
            //GET FROM TBL
            String saved_filename = db.getFieldString("filename", "person_task_file_id = '" + id + "'", "person_task_file");
            imageFileName = saved_filename;
            try {
                saved_filename = URLEncoder.encode(saved_filename, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String saved_file_desc = db.getFieldString("file_desc", "person_task_file_id = '" + id + "'", "person_task_file");
            try {
                saved_file_desc = URLEncoder.encode(saved_file_desc, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String saved_uploaded = db.getFieldString("uploaded", "person_task_file_id = '" + id + "'", "person_task_file");
            String saved_person_id = db.getFieldString("person_id", "person_task_file_id = '" + id + "'", "person_task_file");
            String saved_task_id = db.getFieldString("task_id", "person_task_file_id = '" + id + "'", "person_task_file");
            String saved_person_task_id = db.getFieldString("person_task_id", "person_task_file_id = '" + id + "'", "person_task_file");
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_task_file_id = '" + id + "'", "person_task_file");
            String saved_app_by_id = db.getFieldString("app_by_id", "person_task_file_id = '" + id + "'", "person_task_file");
            String saved_date_checked = db.getFieldString("date_checked", "person_task_file_id = '" + id + "'", "person_task_file");
            String saved_date_app = db.getFieldString("date_app", "person_task_file_id = '" + id + "'", "person_task_file");
            String saved_checked_remarks = db.getFieldString("checked_remarks", "person_task_file_id = '" + id + "'", "person_task_file");
            try {
                saved_checked_remarks = URLEncoder.encode(saved_checked_remarks, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String saved_app_remarks = db.getFieldString("app_remarks", "person_task_file_id = '" + id + "'", "person_task_file");
            try {
                saved_app_remarks = URLEncoder.encode(saved_app_remarks, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_task_file&id="+id+"&filename="+saved_filename+"&file_desc="+saved_file_desc+"&uploaded="+saved_uploaded+"&person_id="+saved_person_id+"&task_id="+saved_task_id+"&person_task_id="+saved_person_task_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+ "&event=ADD");
            Log.d("CONNECT", url +"sync.php?table=person_task_file&id="+id+"&filename="+saved_filename+"&file_desc="+saved_file_desc+"&uploaded="+saved_uploaded+"&person_id="+saved_person_id+"&task_id="+saved_task_id+"&person_task_id="+saved_person_task_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+ "&event=ADD");

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
            new Thread(new Runnable() {
                public void run() {
                    uploadImage(imageFileName);
                }
            }).start();


            return null;
        }
        protected void onPostExecute(Void result){
            return;

        }
    }


    public void uploadFile(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TaskUpdateActivity.this);
        alertDialogBuilder.setTitle("");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("CLOSE",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                    }
                });
        alertDialogBuilder.setPositiveButton("Save", null);
        alertDialogBuilder.setNegativeButton("Close", null);


        ScrollView main_linearLayout = new ScrollView( TaskUpdateActivity.this );

        LinearLayout linearlayout = new LinearLayout( TaskUpdateActivity.this );
        linearlayout.setPadding( 30, 30, 30, 30 );
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
        linearlayout.setOrientation(LinearLayout.VERTICAL);
        linearlayout.setLayoutParams( llParams );

        TextView tv_file_desc = new TextView(TaskUpdateActivity.this);
        tv_file_desc.setText("File Description");
        tv_file_desc.setTextColor(Color.parseColor("#000000"));
        tv_file_desc.setTextSize(18);
        tv_file_desc.setPadding(10,10,10,10);
        linearlayout.addView(tv_file_desc);

        final EditText et_file_desc = new EditText(this);
        et_file_desc.setLayoutParams(llParams);
        linearlayout.addView(et_file_desc);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(10, 5, 10, 10);

        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ivParams.setMargins(10, 10, 10, 10);


        tv_fileName = new TextView(TaskUpdateActivity.this);
        tv_fileName.setLayoutParams(ivParams);
        tv_fileName.setTextColor(Color.parseColor("#000000"));
        tv_fileName.setTextSize(15);
        linearlayout.addView(tv_fileName);

        AppCompatButton btn_takephoto = new AppCompatButton(TaskUpdateActivity.this);
        btn_takephoto.setText("CAPTURE PHOTO");
        btn_takephoto.setBackgroundColor(getColor(R.color.colorPrimaryDark));
        btn_takephoto.setTextColor(Color.parseColor("#ffffff"));
        btn_takephoto.setGravity(Gravity.CENTER_HORIZONTAL);
        btn_takephoto.setLayoutParams(btnParams);
        linearlayout.addView(btn_takephoto);

        btn_takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dispatchTakePictureIntent();
            }
        });


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
                Integer id = db.newIntegerId("person_task_file");
                String file_desc = et_file_desc.getText().toString();
                String uploaded = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                if(file_desc.equals("")){
                    file_desc = imageFileName;
                }

                db.query("INSERT INTO person_task_file (id, person_task_file_id, filename, file_desc, uploaded, person_id, task_id, person_task_id) VALUES ("+ id +", '"+ person_task_file_id+"', '"+imageFileName+"', '"+ file_desc+"', '"+ uploaded+"', '"+person_id+"', '"+ task_id+"', '"+ person_task_id +"')");

                pd = new ProgressDialog(TaskUpdateActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("Loading. Please wait .... ");
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.show();
                int conn = getConnection.getConnectionType(TaskUpdateActivity.this);
                if(conn != 0){
                    new SyncOnline2(context, person_task_file_id).execute();
                }else{
                    Integer backup_item_id = db.newIntegerId("backup_item");
                    db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_task_file', '" + person_task_file_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                    pd.dismiss();

                }
                alertDialog.dismiss();
                get_files();
                pd.dismiss();

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

    public void get_files(){
        files_container.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(TaskUpdateActivity.this);
        linearLayout.setLayoutParams(layoutParams1);

        TextView textView = new TextView(TaskUpdateActivity.this);
        textView.setLayoutParams(layoutParams);
        textView.setText("Filename");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(15);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(textView);

        textView = new TextView(TaskUpdateActivity.this);
        textView.setLayoutParams(layoutParams2);
        textView.setText("");
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(textView);
        files_container.addView(linearLayout);

        int cnt = db.GetCount("person_task_file", " WHERE person_task_id = '"+person_task_id+"'");
        if(cnt > 0){
            List<PersonTaskFile> personTaskFiles = db.getPersonTaskFiles(person_task_id);
            for(final PersonTaskFile personTaskFile : personTaskFiles){
                linearLayout = new LinearLayout(TaskUpdateActivity.this);
                linearLayout.setLayoutParams(layoutParams1);
                String file_desc = personTaskFile.getFile_desc();
                try {
                    file_desc = URLDecoder.decode(file_desc, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                final TextView textView2 = new TextView(TaskUpdateActivity.this);
                textView2.setLayoutParams(layoutParams);
                textView2.setText(file_desc);
                textView2.setTextSize(15);
                textView2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                textView2.setGravity(Gravity.START);
                textView2.setPadding(10,10,10,10);
                textView2.setBackgroundResource(R.drawable.border);
                linearLayout.addView(textView2);

                textView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show_image(personTaskFile.getFilename());
                    }
                });
                if(!passed.equals("Y")){
                    final TextView textView3 = new TextView(TaskUpdateActivity.this);
                    textView3.setLayoutParams(layoutParams2);
                    textView3.setText("Delete");
                    textView3.setTextSize(15);
                    textView3.setTextColor(getColor(R.color.colorPrimaryDark));
                    textView3.setGravity(Gravity.CENTER_HORIZONTAL);
                    textView3.setPadding(10,10,10,10);
                    textView3.setBackgroundResource(R.drawable.border);
                    linearLayout.addView(textView3);

                    textView3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delete_person_task_file(personTaskFile.getPerson_task_file_id());
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
                        }
                    });


                }
                files_container.addView(linearLayout);
            }
        }else{
            linearLayout = new LinearLayout(TaskUpdateActivity.this);
            linearLayout.setLayoutParams(layoutParams1);

            TextView textView2 = new TextView(TaskUpdateActivity.this);
            textView2.setLayoutParams(layoutParams);
            textView2.setText("No file to display");
            textView2.setTextSize(15);
            textView2.setTextColor(getColor(R.color.black));
            textView2.setGravity(Gravity.START);
            textView2.setPadding(10,10,10,10);
            textView2.setBackgroundResource(R.drawable.border);
            linearLayout.addView(textView2);
            files_container.addView(linearLayout);

        }
    }

    public void show_image(String filename){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TaskUpdateActivity.this);
        alertDialogBuilder.setTitle("PREVIEW");
        alertDialogBuilder.setIcon(R.drawable.ic_image_black_24dp);
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("CLOSE",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //DO NOTHING
                    }
                });
        alertDialogBuilder.setNegativeButton("CLOSE", null);

        LinearLayout linearlayout = new LinearLayout( TaskUpdateActivity.this );
        linearlayout.setPadding( 30, 20, 30, 5 );
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
        linearlayout.setOrientation(LinearLayout.VERTICAL);
        linearlayout.setLayoutParams( llParams );


        Log.d("QUEST IMG : ", filename);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/" + filename);
        currentPhotoPath = storageDir.getAbsolutePath();
        Bitmap bm = BitmapFactory.decodeFile(currentPhotoPath);

        ImageView iv_img = new ImageView(TaskUpdateActivity.this);
        iv_img.setImageBitmap(bm);
        iv_img.setLayoutParams(llParams);

        linearlayout.addView(iv_img);

        alertDialogBuilder.setView(linearlayout );

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


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

    public void delete_person_task_file(final String person_task_file_id){
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
                pd = new ProgressDialog(TaskUpdateActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("Loading. Please wait .... ");
                pd.setIndeterminate(true);
                pd.setCancelable(false);
                pd.show();

                db.query("DELETE FROM person_task_file WHERE person_task_file_id = '"+person_task_file_id+"'");

                int conn = getConnection.getConnectionType(TaskUpdateActivity.this);
                if(conn != 0){
                    new SyncOnlineDelete(context, "person_task_file", person_task_file_id).execute();
                }else{
                    Integer backup_item_id = db.newIntegerId("backup_item");
                    db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_task_file', '" + person_task_file_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'DELETE', 'N')");
                }
                pd.dismiss();
                alertDialog.dismiss();
                get_files();
            }
        });

        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
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
            return;
        }
    }

    public int uploadImage(String sourceFileUri) {
        final String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/"+ sourceFileUri);

        if (!sourceFile.isFile()) {
            Log.d("RESULT", "Source File not exist :" +upLoadServerUri + "" + sourceFileUri);

            return 0;

        }else{
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();


                Log.d("RESULT", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Log.d("RESULT: ", "File Upload Complete.");
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.d("RESULT", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();


                Log.d("RESULT", "Exception : " + e.getMessage(), e);
            }
            return serverResponseCode;

        } // End else block
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.Manifest.permission.CAMERA"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if(REQUEST_TAKE_PHOTO == 1){
                dispatchTakePictureIntent();
            }

        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.elosoftbiz.etrb_trmf.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                //setPic();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "eTRB_" + person_task_file_id + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File image = File.createTempFile("eRTB_" + person_task_id, ".jpg", storageDir);
        File image = new File(storageDir, imageFileName);

        //Toast.makeText(PersonTaskActivity.this, "" + imageFileName, Toast.LENGTH_SHORT).show();
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        //Toast.makeText(PersonTaskActivity.this, imageFileName + " was successfully saved!", Toast.LENGTH_SHORT).show();
        tv_fileName.setText("Filename : " + imageFileName);

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

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        //File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY /*iDyme folder*/);
        File wallpaperDirectory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/esig");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
            Log.d("hhhhh",wallpaperDirectory.toString());
        }

        try {
            File f = new File(wallpaperDirectory, person_task_id+"_esig.jpg");
            String vessel_id = db.getCadetVessel(person_id);
            String checked_by_id = db.getFieldString("assessor_id", "person_id = '"+person_id+"' AND vessel_id = '"+vessel_id+"'", "person_officer");
            String checked_remarks = et_checked_remarks.getEditText().getText().toString();

            db.execQuery("UPDATE person_task SET esig_file = '"+ person_task_id + "_esig.jpg" +"', checked_by_id = '"+checked_by_id+"', app_by_id='"+checked_by_id+"', date_app = DATE(),  date_checked = DATE(), officer_remarks = '"+checked_remarks+"', for_app ='N', passed = 'Y', passed2 = 'Y' WHERE person_task_id = '"+person_task_id+"'");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(context,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("RESULT", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
            Log.d("RESULT", " " + e1);
        }
        return "";

    }

    public String saveImage2(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        //File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY /*iDyme folder*/);
        File wallpaperDirectory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/esig");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
            Log.d("hhhhh",wallpaperDirectory.toString());
        }

        try {
            File f = new File(wallpaperDirectory, person_task_id+"_esig.jpg");
            String vessel_id = db.getCadetVessel(person_id);
            String checked_by_id = db.getFieldString("assessor_id", "person_id = '"+person_id+"' AND vessel_id = '"+vessel_id+"'", "person_officer");
            String checked_remarks = et_checked_remarks.getEditText().getText().toString();

            db.execQuery("UPDATE person_task SET esig_file = '"+ person_task_id + "_esig.jpg" +"', checked_by_id = '"+checked_by_id+"', date_checked = DATE(), officer_remarks = '"+checked_remarks+"', for_app ='N', passed = 'N', passed2 = 'N' WHERE person_task_id = '"+person_task_id+"'");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(context,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("RESULT", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
            Log.d("RESULT", " " + e1);
        }
        return "";

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TaskUpdateActivity.this, PersonTaskActivity.class);
        intent.putExtra("id", trb_competence_id);
        intent.putExtra("task_type", task_type);
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
