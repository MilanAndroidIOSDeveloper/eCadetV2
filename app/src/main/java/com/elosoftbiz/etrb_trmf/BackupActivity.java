package com.elosoftbiz.etrb_trmf;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

public class BackupActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;

    DatabaseHelper db;
    TextView tv_message, tv_success;
    ProgressBar progressBar1;
    Button btn_sync, btn_back, btn_download, btn_restore, btn_backup;
    ProgressDialog pd, progressDialog;

    String person_id, h, dept;
    int REQUEST_TEXT_FILE = 1;
    String currentFilePath;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

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

        db = new DatabaseHelper(BackupActivity.this);
        person_id = db.getCadetId();
        dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");

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

        tv_message = findViewById(R.id.tv_message);
        tv_success = findViewById(R.id.tv_success);
        progressBar1 = findViewById(R.id.progressBar1);
        btn_download = findViewById(R.id.btn_download);
        btn_back = findViewById(R.id.btn_back);
        btn_restore = findViewById(R.id.btn_restore);
        btn_backup = findViewById(R.id.btn_backup);

        tv_message.setVisibility(View.GONE);
        tv_success.setVisibility(View.GONE);
        progressBar1.setVisibility(View.GONE);
        btn_back.setVisibility(View.GONE);

        btn_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BackupActivity.this).setCancelable(false);
                alertDialogBuilder.setMessage("Are you sure you want to execute this function?");
                alertDialogBuilder.setPositiveButton("CONFIRM",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                progressDialog = new ProgressDialog(BackupActivity.this);
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("Loading..."); // Setting Message
                                progressDialog.setTitle(""); // Setting Title
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                                progressDialog.show();

                                new BackupToDevice(context).execute();

                            }
                        });

                alertDialogBuilder.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(BackupActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(BackupActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(BackupActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(BackupActivity.this, R.color.white));
            }
        });

        btn_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BackupActivity.this).setCancelable(false);
                alertDialogBuilder.setMessage("Are you sure you want to execute this function? it will overwrite your app's database and cannot be undone.");
                alertDialogBuilder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
                            Toast.makeText(BackupActivity.this, "Sorry, Your android version does not support this feature. We recommend to use other device with atleast 4.0 android version.", Toast.LENGTH_LONG).show();
                        }else {


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                // start runtime permission
                                Boolean hasPermission = (ContextCompat.checkSelfPermission(BackupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED);
                                if (!hasPermission) {
                                    Log.e("RESULT", "get permision   ");
                                    ActivityCompat.requestPermissions(BackupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_TEXT_FILE);
                                } else {
                                    Log.e("RESULT", "get permision-- already granted ");
                                    showFileChooser();
                                }
                            } else {
                                //readfile();
                                showFileChooser();
                            }
                        }

                    }
                });

                alertDialogBuilder.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(BackupActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(BackupActivity.this, R.color.black));

            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(  Intent.createChooser(intent, "Select a File to Upload"),  REQUEST_TEXT_FILE);
        } catch (Exception e) {
            Log.e("RESULT", " choose file error "+e.toString());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("RESULT", requestCode + " result is "+ data + "  uri  "+ data.getData()+ " auth "+ data.getData().getAuthority()+ " path "+ data.getData().getPath());
        String fullerror ="";
        if (requestCode == REQUEST_TEXT_FILE){
            if (resultCode == RESULT_OK){
                progressDialog = new ProgressDialog(BackupActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading..."); // Setting Message
                progressDialog.setTitle(""); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show();

                try {
                    Uri imageuri = data.getData();
                    InputStream stream = null;
                    String tempID= "", id ="";

                    Uri uri = data.getData();
                    Log.e("RESULT", "file auth is "+uri.getAuthority());
                    fullerror = fullerror +"file auth is "+uri.getAuthority();
                    if (imageuri.getAuthority().equals("media")){
                        tempID = imageuri.toString();
                        tempID = tempID.substring(tempID.lastIndexOf("/")+1);
                        id = tempID;
                        Uri contenturi = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        String selector = MediaStore.Images.Media._ID+"=?";
                        currentFilePath = getColunmData( contenturi, selector, new String[]{id}  );


                    }else if (imageuri.getAuthority().equals("com.android.providers.media.documents")){
                        tempID = DocumentsContract.getDocumentId(imageuri);
                        String[] split = tempID.split(":");
                        String type = split[0];
                        id = split[1];
                        Uri contenturi = null;
                        if (type.equals("image")){
                            contenturi = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        }else if (type.equals("video")){
                            contenturi = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        }else if (type.equals("audio")){
                            contenturi = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }
                        String selector = "_id=?";
                        currentFilePath = getColunmData( contenturi, selector, new String[]{id}  );

                    } else if (imageuri.getAuthority().equals("com.android.providers.downloads.documents")){

                        tempID =   imageuri.toString();
                        tempID = tempID.substring(tempID.lastIndexOf("/")+1);
                        id = tempID;
                        Uri contenturi = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        // String selector = MediaStore.Images.Media._ID+"=?";
                        currentFilePath = getColunmData( contenturi, null, null  );


                    }else if (imageuri.getAuthority().equals("com.android.externalstorage.documents")){

                        tempID = DocumentsContract.getDocumentId(imageuri);
                        String[] split = tempID.split(":");
                        String type = split[0];
                        id = split[1];
                        Uri contenturi = null;
                        if (type.equals("primary")){
                            currentFilePath=  Environment.getExternalStorageDirectory()+"/"+id;
                        }


                    }

                    File myFile = new File(currentFilePath);
                    // MessageDialog dialog = new MessageDialog(Home.this, " file details --"+actualfilepath+"\n---"+ uri.getPath() );
                    // dialog.displayMessageShow();
                    String temppath =  uri.getPath();
                    if (temppath.contains("//")){
                        temppath = temppath.substring(temppath.indexOf("//")+1);
                    }
                    Log.d("RESULT", " temppath is "+ temppath);
                    fullerror = fullerror +"\n"+" file details -  "+currentFilePath+"\n --"+ uri.getPath()+"\n--"+temppath;

                    if ( currentFilePath.equals("") || currentFilePath.equals(" ")) {
                        myFile = new File(temppath);

                    }else {
                        myFile = new File(currentFilePath);
                    }

                    Log.d("RESULT", " myfile is "+ myFile.getAbsolutePath());

                    file = myFile;
                    new readFile(context).execute();
                    // lyf path  - /storage/emulated/0/kolektap/04-06-2018_Admin_1528088466207_file.xls

                } catch (Exception e) {
                    Log.e("RESULT", " read errro "+ e.toString());
                }

            }
        }
    }

    private class readFile extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public readFile(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

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
            db.query("DELETE FROM person_trb_topic");
            db.query("DELETE FROM person_trb_sub_competence");
            db.query("DELETE FROM person_to");
            db.query("DELETE FROM person_vessel");
            db.query("DELETE FROM program");
            db.query("DELETE FROM safety");
            db.query("DELETE FROM shipboard");
            db.query("DELETE FROM vessel");
            db.query("DELETE FROM vessel_type");
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            StringBuilder builder = new StringBuilder();
            Log.d("RESULT Main", "read start");
            try {

                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine())!=null){
                    builder.append(line);
                    db.query(line);
                    //builder.append("\n");
                    Log.d("RESULT", line);
                }

                br.close();

            }catch (Exception e){
                Log.d("RESULT", " error is "+e.toString());
            }

            //GET ALL PERSON TASK FILE FROM DB
            List<PersonTaskFile> personTaskFiles = db.getPersonTaskFiles("");
            for (PersonTaskFile cn : personTaskFiles) {

                String sourcePath = Environment.getExternalStorageDirectory() + "/eTRB/" + cn.getFilename();
                File source = new File(sourcePath);;
                Log.d("RESULT SRC ", sourcePath);

                File storageDir2 = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File destination = new File(storageDir2, cn.getFilename());
                Log.d("RESULT DES ", "" + destination);
                try{
                    FileUtils.copyFile(source, destination);
                }catch (IOException e){
                    e.printStackTrace();
                    Log.d("RESULT ERR", ""+ e);
                }

            }


            return null;
        }
        protected void onPostExecute(Void result){
            tv_success.setText("Successfully restored. Go back to Homescreen to see changes.");
            tv_success.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
        }
    }

    public String getColunmData( Uri uri, String selection, String[] selectarg){

        String filepath ="";
        Cursor cursor = null;
        String colunm = "_data";
        String[] projection = {colunm};
        cursor =  getContentResolver().query( uri, projection, selection, selectarg, null);
        if (cursor!= null){
            cursor.moveToFirst();
            Log.d("RESULT", " file path is "+  cursor.getString(cursor.getColumnIndex(colunm)));
            filepath = cursor.getString(cursor.getColumnIndex(colunm));

        }
        if (cursor!= null)
            cursor.close();

        return  filepath;
    }

    private class BackupToDevice extends AsyncTask<Void, Void, Void> {
        public Context context;
        public BackupToDevice(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            h = DateFormat.format("yyyy-MM-dd_h_mm_aa", System.currentTimeMillis()).toString();
            String str = "";
            // this will create a new name everytime and unique
            //File root = new File(Environment.getExternalStorageDirectory() + "/eCADET");
            File root = new File(getApplicationContext().getExternalFilesDir("eCADET").getAbsolutePath());
            // if external memory exists and folder with name Notes
            if (!root.exists()) {
                root.mkdirs(); // this will create folder.
                Log.d("RESULT", "" + root);
            }else{
                Log.d("RESULT", "" + root + " created");
            }
            File filepath = new File(root, h + ".txt"); // file path to save
            FileWriter writer = null;
            try {
                writer = new FileWriter(filepath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //GET ALL PERSON FROM DB
            List<Person> persons = db.getPersons("");
            int cnt = 0;
            for (Person cn : persons) {
                if(cnt != 0){
                    str += "\n";
                }
                str += "INSERT INTO person (person_id , id, code_person, lname, fname, mname, st_address, city_id, province_id, phone, mobile, st_address_province, phone_province, email, gender, civ_status, birth_date, birth_place, spouse_name, children, father_name, mother_name, notes, active, date_reg, compman_id, company_id, amt_paid, rank_id, vessel_officer, dept, vessel_id, course_id, school_id, school_admin, officer_type, batch_no, pct_done, days_on_board, photo_file, passport_no, cdc_no, indos_no, height, weight, enrtry_date, marks, blood_group, emergency_contact_name, emergency_contact_address, emergency_contact_no, emergency_relationship, nationality, passport_no_issue_date, cdc_no_issue_date, cdc_no_issue_place, full_name, logged_in, w_fr) VALUES ('"+ cn.getPerson_id() +"', '"+
                        cn.getId()+"', '"+ cn.getCode_person()+"', '"+ cn.getLname()+"', '"+cn.getFname()+"', '"+ cn.getMname()+"', '"+
                        cn.getSt_address()+"', '"+ cn.getCity_id()+"', '"+cn.getProvince_id()+"', '"+cn.getPhone()+"', '"+cn.getMobile()+"', '"+
                        cn.getSt_address_province()+"', '"+cn.getPhone_province()+"', '"+cn.getEmail()+"', '"+cn.getGender()+"', '"+
                        cn.getCiv_status()+"', '"+cn.getBirth_date()+"', '"+cn.getBirth_place()+"', '"+cn.getSpouse_name()+"', '"+
                        cn.getChildren()+"', '"+cn.getFather_name()+"', '"+cn.getMother_name()+"', '"+cn.getNotes()+"', '"+cn.getActive()+"', '"+
                        cn.getDate_reg()+"', '"+cn.getCompman_id()+"', '"+cn.getCompany_id()+"', '"+cn.getAmt_paid()+"', '"+cn.getRank_id()+"', '"+
                        cn.getVessel_officer()+"', '"+cn.getDept()+"', '"+cn.getVessel_id()+"', '"+cn.getCourse_id()+"', '"+
                        cn.getSchool_id()+"', '"+cn.getSchool_admin()+"', '"+cn.getType()+"', '"+cn.getBatch_no()+"', '"+
                        cn.getPct_done()+"', '"+cn.getDays_on_board()+"', '"+cn.getPhoto_file()+"', '"+cn.getPassport_no()+"', '"+
                        cn.getCdc_no()+"', '"+cn.getIndos_no()+"', '"+cn.getHeight()+"', '"+cn.getWeight()+"', '"+cn.getEnrtry_date()+"', '"+
                        cn.getMarks()+"', '"+cn.getBlood_group()+"', '"+cn.getEmergency_contact_name()+"', '"+cn.getEmergency_contact_address()+"', '"+
                        cn.getEmergency_contact_no()+"', '"+
                        cn.getEmergency_relationship()+"', '"+cn.getNationality()+"', '"+cn.getPassport_no_issue_date()+"', '"+
                        cn.getCdc_no_issue_date()+"', '"+cn.getCdc_no_issue_place()+"', '"+cn.getFull_name()+"', '"+cn.getLogged_in()+"', '"+
                        cn.getW_fr()+"');";
                cnt++;
            }
            //GET ALL ABBREVIATIONS
            List<Abbreviation> list = db.getAbbreviations("");
            for (Abbreviation cn : list) {
                str += "\nINSERT INTO abbreviation(abbreviation_id , id, code_abbreviation, desc_abbreviation, login_id, last_update) VALUES ('"+cn.getAbbreviation_id()+"', "+cn.getId()+", '"+cn.getCode_abbreviation()+"', '"+cn.getDesc_abbreviation()+"', '"+cn.getLogin_id()+"', '"+cn.getLast_update()+"');";
            }
            //GET ALL BACKUP ITEM FROM DB
            List<BackupItem> backupItems = db.getBackupItems("");
            for (BackupItem cn : backupItems) {
                str += "\nINSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ('" + cn.getId() + "', '" + cn.getTbl() + "', '" + cn.getTbl_id() + "', '" + cn.getBackup_date() + "', '" + cn.getBackup_time() + "', '" + cn.getBackup_event() + "', '" + cn.getBackuped() + "');";
            }
            //GET ALL BASIC TRAINING FROM DB
            List<BasicTraining> basicTrainings = db.getBasicTrainings("", "");
            for (BasicTraining cn : basicTrainings) {
                str += "\nINSERT INTO basic_training (id, basic_training_id, desc_basic_training, dept, prio, training_type) VALUES ('"+ cn.getId() +"', '"+ cn.getBasic_training_id()+"', "+ '"' + cn.getDesc_basic_training() + '"'+", '"+ cn.getDept()+"', '"+ cn.getPrio() +"', '"+cn.getTraining_type()+"');";
            }
            //GET ALL GLOBALSYS FROM DB
            List<GlobalSys> globalSys = db.getGlobalSys();
            for (GlobalSys cn : globalSys) {
                str += "\nINSERT INTO global_sys (id, global_sys_id, days_on_board, submission_alert_pct, submission_alert_days, assessment_alert_pct, assessment_alert_days, company_alert_days) VALUES ('" + cn.getId() + "', '" + cn.getGlobal_sys_id() + "', '" + cn.getDays_on_board() + "', '" + cn.getSubmission_alert_pct() + "', '" + cn.getSubmission_alert_days() + "', '" + cn.getAssessment_alert_pct() + "', '" + cn.getAssessment_alert_days() + "', '" + cn.getCompany_alert_days() + "');";
            }
            //GET ALL PERSON FROM DB
            List<Login> logins = db.getLogins("");
            for (Login cn : logins) {
                str += "\nINSERT INTO login (id, login_id, email, login_name, login_pass, session_id, login_type_id, initial, lname, fname, mname, company_id, designation) VALUES ('"+ cn.getId() +"', '"+ cn.getLogin_id()+"', '"+ cn.getEmail()+"', '"+ cn.getLogin_name()+"', '"+cn.getLogin_pass()+"', '"+ cn.getSession_id()+"', '"+cn.getLogin_type_id()+"', '"+cn.getInitial()+"', '"+cn.getLname()+"', '"+cn.getFname()+"', '"+cn.getMname()+"', '"+cn.getCompany_id()+"', '"+cn.getDesignation()+"');";
            }
            //GET ALL PERSON BASIC TRAINING FROM DB
            List<PersonBasicTraining> personBasicTrainings = db.getPersonBasicTrainings(person_id);
            for (PersonBasicTraining cn : personBasicTrainings) {
                str += "\nINSERT INTO person_basic_training (id, person_basic_training_id, person_id, basic_training_id, location_name, date_completed, doc_ref_no, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, institution) VALUES ('"+ cn.getId() +"', '"+ cn.getPerson_basic_training_id()+"', '"+ cn.getPerson_id()+"', '"+ cn.getBasic_training_id()+"', '"+ cn.getLocation_name() +"', '"+cn.getDate_completed()+"', '"+ cn.getDoc_ref_no()+"', '"+ cn.getChecked_by_id()+"', '"+cn.getApp_by_id()+"', '"+ cn.getDate_checked()+"', '"+cn.getDate_app()+"', '"+ cn.getChecked_remarks()+"', '"+cn.getApp_remarks()+"', '"+cn.getInstitution()+"');";
            }
            //GET ALL PERSON BASIC TRAINING FROM DB
            List<PersonBridgeWatch> personBridgeWatches = db.getPersonBridgeWatch(person_id);
            for (PersonBridgeWatch cn : personBridgeWatches) {
                str += "\nINSERT INTO person_bridge_watch (id, person_bridge_watch_id, person_id, vessel_id, date_watchkeeping, from_time, to_time, voyage_number, voyage_desc, watch_type, remarks, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, total_hrs) VALUES ('"+ cn.getId() +"', '"+ cn.getPerson_bridge_watch_id()+"', '"+ cn.getPerson_id()+"', '"+ cn.getVessel_id()+"', '"+ cn.getDate_watchkeeping() +"', '"+cn.getFrom_time()+"', '"+ cn.getTo_time()+"', '"+ cn.getVoyage_number()+"', '"+cn.getVoyage_desc()+"', '"+ cn.getWatch_type()+"', '"+cn.getRemarks()+"', '"+ cn.getChecked_by_id()+"', '"+cn.getApp_by_id()+"', '"+cn.getDate_checked()+"', '"+cn.getDate_app()+"', '"+cn.getChecked_remarks()+"', '"+cn.getApp_remarks()+"', '"+cn.getTotal_hrs()+"');";
            }
            //GET ALL PERSON CE FROM DB
            List<PersonCe> personCes = db.getPersonCe(person_id);
            for (PersonCe cn : personCes) {
                str += "\nINSERT INTO person_ce (id, person_ce_id, person_id, vessel_id, comments, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks) VALUES ('"+cn.getId()+"', '"+ cn.getPerson_ce_id()+"', '"+cn.getPerson_id() +"', '"+cn.getVessel_id()+"', '"+cn.getComments()+"', '"+cn.getChecked_by_id()+"', '"+ cn.getApp_by_id()+"', '"+ cn.getDate_checked()+"', '"+ cn.getDate_app()+"', '"+cn.getChecked_remarks()+"', '"+cn.getApp_remarks()+"');";
            }
            //GET ALL PERSON EDUCH
            List<PersonEducationH> personEducationHS = db.getPersonEducationH(person_id);
            for (PersonEducationH cn : personEducationHS) {
                str += "\nINSERT INTO person_education_h (id, person_education_h_id, person_id, gce_level, school_name, school_address, certificate_date, login_id, last_update) VALUES ('"+cn.getId()+"', '"+ cn.getPerson_education_h_id()+"', '"+cn.getPerson_id() +"', '"+cn.getGce_level()+"', '"+cn.getSchool_name()+"', '"+cn.getSchool_address()+"', '"+ cn.getCertificate_date()+"', '"+ cn.getLogin_id()+"', '"+ cn.getLast_update()+"');";
                //GET EDUCD
                List<PersonEducationD> personEducationDS = db.getPersonEducationD(cn.getPerson_education_h_id());
                for(PersonEducationD cn2 : personEducationDS){
                    str += "\nINSERT INTO person_education_d (id, person_education_d_id, person_education_h_id, subject, grade) VALUES ("+cn2.getId()+", '"+cn2.getPerson_education_d_id()+"', '"+cn2.getPerson_education_h_id()+"', '"+cn2.getSubject()+"', '"+cn2.getGrade()+"');";
                }
            }
            //GET ALL PERSON INSPECT FROM DB
            List<PersonInspect> personInspects = db.getPersonInspect(person_id);
            for (PersonInspect cn : personInspects) {
                str += "\nINSERT INTO person_inspect (id, person_inspect_id, person_id, comments, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, company_id) VALUES ('"+cn.getId()+"', '"+ cn.getPerson_inspect_id()+"', '"+cn.getPerson_id() +"', '"+cn.getComments()+"', '"+cn.getChecked_by_id()+"', '"+ cn.getApp_by_id()+"', '"+ cn.getDate_checked()+"', '"+ cn.getDate_app()+"', '"+cn.getChecked_remarks()+"', '"+cn.getApp_remarks()+"', '"+cn.getCompany_id()+"');";
            }
            //GET ALL PERSON MATERIAL FROM DB
            List<PersonMaterial> personMaterials = db.getPersonMaterials(person_id);
            for (PersonMaterial cn : personMaterials) {
                str += "\nINSERT INTO person_material (id, person_material_id, person_id, material, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, date_material) VALUES ('"+cn.getId()+"', '"+ cn.getPerson_material_id()+"', '"+cn.getPerson_id() +"', '"+ URLEncoder.encode(cn.getMaterial())+"', '"+cn.getChecked_by_id()+"', '"+ cn.getApp_by_id()+"', '"+ cn.getDate_checked()+"', '"+ cn.getDate_app()+"', '"+cn.getChecked_remarks()+"', '"+cn.getApp_remarks()+"', '"+cn.getDate_material()+"');";
            }
            //GET ALL PERSON MUSTER FROM DB
            List<PersonMuster> personMusters = db.getPersonMuster(person_id,"");
            for (PersonMuster cn : personMusters) {
                str += "\nINSERT INTO person_muster (id, person_muster_id, person_id, vessel_id, lifeboat_station, lifeboat_duties, emergency_station, emergency_duties, oil_spill_duties, safety_officer_id, security_officer_id, master_id, date_checked) VALUES ('"+cn.getId()+"', '"+ cn.getPerson_muster_id()+"', '"+cn.getPerson_id() +"', '"+cn.getVessel_id()+"', '"+cn.getLifeboat_station()+"', '"+ cn.getLifeboat_duties()+"', '"+ cn.getEmergency_station()+"', '"+ cn.getEmergency_duties()+"', '"+cn.getOil_spill_duties()+"', '"+cn.getSafety_officer_id()+"', '"+cn.getSecurity_officer_id()+"', '"+cn.getMaster_id()+"', '"+cn.getDate_checked()+"');";
            }
            //GET ALL PERSON OFFICER FROM DB
            List<PersonOfficer> personOfficers = db.getPersonOfficers(person_id);
            for (PersonOfficer cn : personOfficers) {
                str += "\nINSERT INTO person_officer (id, person_officer_id, person_id, officer_id, from_date, to_date, comp_officer_ok, vessel_id, last_update, assessor_id, master_id) VALUES ('"+cn.getId()+"', '"+ cn.getPerson_officer_id()+"', '"+cn.getPerson_id() +"', '"+cn.getOfficer_id()+"', '"+cn.getFrom_date()+"', '"+ cn.getTo_date()+"', '"+ cn.getComp_officer_ok()+"', '"+ cn.getVessel_id()+"', '"+cn.getLast_update()+"', '"+cn.getAssessor_id()+"', '"+cn.getMaster_id()+"');";
            }
            //GET ALL PERSON PORT WATCH FROM DB
            List<PersonPortWatch> personPortWatches = db.getPersonPortWatches(person_id);
            for (PersonPortWatch cn : personPortWatches) {
                str += "\nINSERT INTO person_port_watch (id, person_port_watch_id, person_id, vessel_id, date_watch, from_time, to_time, voyage_number, port_name, desc_cargo, remarks, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, total_hrs) VALUES ('"+cn.getId()+"', '"+ cn.getPerson_port_watch_id()+"', '"+cn.getPerson_id() +"', '"+cn.getVessel_id()+"', '"+cn.getDate_watch()+"', '"+ cn.getFrom_time()+"', '"+ cn.getTo_time()+"', '"+ cn.getVoyage_number()+"', '"+cn.getPort_name()+"', '"+cn.getDesc_cargo()+"', '"+cn.getRemarks()+"', '"+cn.getChecked_by_id()+"', '"+cn.getApp_by_id()+"', '"+cn.getDate_checked()+"', '"+cn.getDate_app()+"', '"+cn.getChecked_remarks()+"', '"+cn.getApp_remarks()+"', '"+cn.getTotal_hrs()+"');";
            }
            //GET ALL REGULATION H FROM DB
            List<RegulationH> regulationHS = db.getRegulationH();
            for (RegulationH cn : regulationHS) {
                //str += "\nINSERT INTO regulation_h (id, regulation_h_id, desc_regulation_h, prio, login_id, last_update) VALUES ('"+ cn.getId() +"', '"+ cn.getRegulation_h_id()+"', '"+ cn.getDesc_regulation_h()+"', '"+ cn.getPrio()+"', '"+cn.getLogin_id()+"', '"+cn.getLast_update()+"'); ";
                //GET REGULATION D
                List<RegulationD> regulationDS = db.getRegulationD(cn.getRegulation_h_id());
                for (RegulationD cn2 : regulationDS){
                    //str += "\nINSERT INTO regulation_d (id, regulation_d_id, regulation_h_id, prio) VALUES ('"+ cn2.getId() +"', '"+ cn2.getRegulation_d_id()+"', '"+ cn2.getRegulation_h_id()+"', '"+ cn2.getPrio()+"');";
                    //GET PERSON REGULATION
                    List<PersonRegulation> personRegulations = db.getPersonRegulation(person_id, cn2.getRegulation_d_id());
                    for(PersonRegulation cn3 : personRegulations){
                        str += "\nINSERT INTO person_regulation(id, person_regulation_id, person_id, regulation_d_id, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks) VALUES ("+cn3.getId()+", '"+cn3.getPerson_regulation_id()+"', '"+cn3.getPerson_id()+"', '"+cn3.getRegulation_d_id()+"', '"+cn3.getChecked_by_id()+"', '"+cn3.getApp_by_id()+"', '"+cn3.getDate_checked()+"', '"+cn3.getDate_app()+"', '"+cn3.getChecked_remarks()+"', '"+cn3.getApp_remarks()+"');";
                    }

                }

            }
            //GET ALL PERSON SAFETY FROM DB
            List<PersonSafety> personSafeties = db.getAllPersonSafety();
            for (PersonSafety cn : personSafeties) {
                str += "\nINSERT INTO person_safety (id, person_safety_id, person_id, safety_id, date_completed, ship_id, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks) VALUES ('"+cn.getId()+"', '"+ cn.getPerson_safety_id()+"', '"+cn.getPerson_id() +"', '"+cn.getSafety_id()+"', '"+cn.getDate_completed()+"', '"+ cn.getShip_id()+"', '"+cn.getChecked_by_id()+"', '"+cn.getApp_by_id()+"', '"+ cn.getDate_checked()+"', '"+cn.getDate_app()+"', '"+cn.getChecked_remarks()+"', '"+cn.getApp_remarks()+"');";
            }
            //GET ALL PERSON STEER TASK FROM DB
            List<PersonSteerTask> personSteerTasks = db.getPersonSteerTask("");
            for (PersonSteerTask cn : personSteerTasks) {
                str += "\nINSERT INTO person_steer_task (id, person_steer_task_id, steer_task_id, person_id, vessel_id, completed, answers, passed, not_app, lat_long, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks) VALUES ('"+cn.getId()+"', '"+ cn.getPerson_steer_task_id()+"', '"+cn.getSteer_task_id() +"', '"+cn.getPerson_id()+"', '"+cn.getVessel_id()+"', '"+ cn.getCompleted()+"', '"+cn.getAnswers()+"', '"+cn.getPassed()+"', '"+ cn.getNot_app()+"', '"+cn.getLat_long()+"', '"+cn.getChecked_by_id()+"', '"+cn.getApp_by_id()+"', '"+cn.getDate_checked()+"', '"+cn.getDate_app()+"', '"+cn.getChecked_remarks()+"', '"+cn.getApp_remarks()+"');";
            }
            //GET ALL PERSON STEER TOPIC FROM DB
            List<PersonSteerTopic> personSteerTopics = db.getPersonSteerTopic("");
            for (PersonSteerTopic cn : personSteerTopics) {
                str += "\nINSERT INTO person_steer_topic (id, person_steer_topic_id, steer_topic_id, person_id, checked_by_id, date_checked, checked_remarks) VALUES ('"+cn.getId()+"', '"+ cn.getPerson_steer_topic_id()+"', '"+cn.getSteer_topic_id() +"', '"+cn.getPerson_id()+"', '"+cn.getChecked_by_id()+"', '"+ cn.getDate_checked()+"', '"+cn.getChecked_remarks()+"'); ";
            }
            //GET ALL PERSON STEERING FROM DB
            List<PersonSteering> personSteerings = db.getPersonSteering(person_id);
            for (PersonSteering cn : personSteerings) {
                str += "\nINSERT INTO person_steering (id, person_steering_id, person_id, vessel_id, steering_type, voyage_from, voyage_to, date_steering, steering_from, steering_to, total_hrs, remarks, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks) VALUES ('"+cn.getId()+"', '"+ cn.getPerson_steering_id()+"', '"+cn.getPerson_id() +"', '"+cn.getVessel_id()+"', '"+cn.getSteering_type()+"', '"+ cn.getVoyage_from()+"', '"+cn.getVoyage_to()+"', '"+cn.getDate_steering()+"', '"+cn.getSteering_from()+"', '"+cn.getSteering_to()+"', '"+cn.getTotal_hrs()+"', '"+cn.getRemarks()+"', '"+cn.getChecked_by_id()+"', '"+cn.getApp_by_id()+"', '"+cn.getDate_checked()+"', '"+cn.getDate_app()+"', '"+cn.getChecked_remarks()+"', '"+cn.getApp_remarks()+"');";
            }
            //GET ALL PERSON TASK FROM DB
            List<PersonTask> personTasks = db.getAllPersonTask();
            for (PersonTask cn : personTasks) {
                str += "\nINSERT INTO person_task (id, person_task_id, task_id, person_id, completed, answers, passed, img_file, not_app, lat_long, vessel_type_id, checked_by_id, app_by_id, date_checked, date_app, officer_remarks, app_remarks, vessel_id) VALUES ('" + cn.getId() + "', '" + cn.getPerson_task_id() + "', '" + cn.getTask_id() + "', '" + cn.getPerson_id() + "', '" + cn.getCompleted() + "', '" + cn.getAnswers() + "', '" + cn.getPassed() + "', '" + cn.getImg_file() + "', '" + cn.getNot_app() + "', '" + cn.getLat_long() + "', '"+cn.getVessel_type_id()+"', '"+cn.getChecked_by_id()+"', '"+cn.getApp_by_id()+"', '"+cn.getDate_checked()+"', '"+cn.getDate_app()+"', '"+cn.getOfficer_remarks()+"', '"+cn.getApp_remarks()+"', '"+cn.getVessel_id()+"');";
                //GET PERSON TASK FILES
                List<PersonTaskFile> personTaskFiles = db.getPersonTaskFiles(cn.getPerson_task_id());
                for(PersonTaskFile cn2 : personTaskFiles){
                    str += "\nINSERT INTO person_task_file (id, person_task_file_id, filename, file_desc, uploaded, person_id, task_id, person_task_id, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks) VALUES ("+cn2.getId()+", '"+cn2.getPerson_task_file_id()+"', '"+cn2.getFilename()+"', '"+cn2.getFile_desc()+"', '"+cn2.getUploaded()+"', '"+cn2.getPerson_id()+"', '"+cn2.getTask_id()+"', '"+cn2.getPerson_task_id()+"', '"+cn2.getChecked_by_id()+"', '"+cn2.getApp_by_id()+"', '"+cn2.getDate_checked()+"', '"+cn2.getDate_app()+"', '"+cn2.getChecked_remarks()+"', '"+cn2.getApp_remarks()+"');";
                    //String sourcePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TongueTwister/tt_temp.3gp";
                    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/" + cn2.getFilename());
                    String sourcePath = storageDir.getAbsolutePath();
                    File source = new File(sourcePath);
                    Log.d("RESULT IMG ", sourcePath);

                    String destinationPath = Environment.getExternalStorageDirectory() + "/eTRB/" + cn2.getFilename();
                    File destination = new File(destinationPath);
                    Log.d("RESULT IMG ", destinationPath);
                    try{
                        FileUtils.copyFile(source, destination);
                    }catch (IOException e){
                        e.printStackTrace();
                        Log.d("RESULT ERR", ""+ e);
                    }
                }
            }
            //GET ALL PERSON TO FROM DB
            List<PersonTo> personTos = db.getPersonTo(person_id);
            for (PersonTo cn : personTos) {
                str += "\nINSERT INTO person_to (id, person_to_id, person_id, date_signed, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, vessel_id) VALUES ('"+ cn.getId() +"', '"+ cn.getPerson_to_id()+"', '"+ cn.getPerson_id()+"', '"+ cn.getDate_signed()+"', '"+ cn.getChecked_by_id() +"', '"+cn.getApp_by_id()+"', '"+ cn.getDate_checked()+"', '"+ cn.getDate_app()+"', '"+cn.getChecked_remarks()+"', '"+ cn.getApp_remarks()+"', '"+cn.getVessel_id()+"');";
            }
            //GET ALL PERSON TRB TOPIC FROM DB
            List<PersonTrbSubCompetence> personTrbTopic = db.getPersonTrbSubComp(person_id);
            for (PersonTrbSubCompetence cn : personTrbTopic) {
                str += "\nINSERT INTO person_trb_sub_competence (id, person_trb_sub_competence_id, trb_sub_competence_id, person_id, checked_by_id, date_checked, checked_remarks) VALUES ('"+ cn.getId() +"', '"+ cn.getPerson_trb_sub_competence_id()+"', '"+ cn.getTrb_sub_competenceid()+"', '"+ cn.getPerson_id()+"', '"+ cn.getChecked_by_id() +"', '"+ cn.getDate_checked()+"', '"+cn.getChecked_remarks()+"');";
            }
            //GET ALL SAFETY FROM DB
            List<Safety> safeties = db.getSafety("");
            for (Safety cn : safeties) {
                str += "\nINSERT INTO safety (id, safety_id, desc_task, prio, dept) VALUES ('"+cn.getId()+"', '"+ cn.getSafety_id()+"', "+ '"'+URLEncoder.encode(cn.getDesc_task()) + '"' +", '"+cn.getPrio()+"', '"+cn.getDept()+"');";
            }
            //GET ALL SHIPBOARD FROM DB
            List<Shipboard> shipboards = db.getShipboard(person_id);
            for (Shipboard cn : shipboards) {
                str += "\nINSERT INTO shipboard (id, shipboard_id, person_id, sign_on, sign_off, engine_watch_mos, engine_watch_yrs, voyage_mos, voyage_days, vessel_id, imo_number) VALUES ('"+ cn.getId() +"', '"+ cn.getShipboard_id()+"', '"+ cn.getPerson_id()+"', '"+ cn.getSign_on()+"', '"+cn.getSign_off()+"', '"+cn.getEngine_watch_mos()+"', '"+ cn.getEngine_watch_yrs()+"', '"+cn.getVoyage_mos()+"', '"+cn.getVoyage_days()+"', '"+cn.getVessel_id()+"', '"+cn.getImo_number()+"');";
            }

            //GET ALL VESSEL FROM DB
            List<Vessel> vessels = db.getVessels();
            for (Vessel cn : vessels) {
                str += "\nINSERT INTO vessel (id, vessel_id, name_vessel, owner_company_id, operator_company_id, year_built, flag_registry_id, hp, kw, grt, trade_type, imo_number, call_sign, vessel_type_id, dp, ice_class) VALUES ('"+ cn.getId() +"', '"+ cn.getVessel_id()+"', '"+ cn.getName_vessel()+"', '"+ cn.getOwner_company_id()+"', '"+cn.getOperator_company_id()+"', '"+cn.getYear_built()+"', '"+cn.getFlag_registry_id()+"', '"+cn.getHp()+"', '"+cn.getKw()+"', '"+ cn.getGrt()+"', '"+ cn.getTrade_type()+"', '"+ cn.getImo_number() +"', '"+cn.getCall_sign()+"', '"+cn.getVessel_type_id()+"', '"+cn.getDp()+"', '"+cn.getIce_class()+"');";
            }
            //GET ALL VESSEL TYPE FROM DB
            List<VesselType> vesselTypes = db.getVesselTypes();
            for (VesselType cn : vesselTypes) {
                str += "\nINSERT INTO vessel_type (id, vessel_type_id, desc_vessel_type) VALUES ('"+ cn.getId() +"', '"+ cn.getVessel_type_id()+"', '"+ cn.getDesc_vessel_type()+"');";
            }


            try {
                writer.append(str);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute(Void result){
            progressDialog.dismiss();
            tv_success.setText("Backup File was generated with name " + h + ".txt");
            tv_success.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BackupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( pd!=null && pd.isShowing() ){
            pd.cancel();
        }

        if ( progressDialog!=null && progressDialog.isShowing() ){
            progressDialog.cancel();
        }
    }
}
