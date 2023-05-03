package com.elosoftbiz.etrb_trmf;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.core.content.FileProvider;
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeaServiceFormActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;

    List<String> vesselArray =  new ArrayList<>();
    List<Vessel> vessels;
    TextInputLayout et_sign_on, et_sign_off, et_voyage_mos, et_voyage_days, et_engine_watch_mos;
    TextInputLayout et_engine_watch_days, et_imo_number;
    String person_id, vessel_id;
    Spinner spinner;
    Button btn_save,btn_cancel, btn_delete, btn_srb_file, btn_cert_file, btn_contract_file;
    String shipboard_id ="", currentPhotoPath, imageFileName;
    String srb_file = "", cert_file = "", contract_file = "";
    TextView tv_srb_file, tv_cert_file, tv_contract_file;
    String str = "", err_message, upLoadServerUri;
    HttpResponse response;
    int serverResponseCode = 0;
    JSONObject json = null;

    int REQUEST_IMAGE_CAPTURE = 1, IMAGE_TYPE;
    int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sea_service_form);

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
        String dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");
        upLoadServerUri = getString(R.string.url) + "upload_files.php";
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
        if (intent.hasExtra("shipboard_id")) {
            shipboard_id = intent.getStringExtra("shipboard_id");
        }

        vessels = db.getVessels(); //POPULATE VESSEL SPINNER
        vesselArray.add("Select Vessel *");
        for (Vessel row : vessels) {
            vesselArray.add(row.getName_vessel());
        }

        ArrayAdapter arrayAdapter_Vessel = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, vesselArray);
        arrayAdapter_Vessel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter_Vessel);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vessel_id  = parent.getItemAtPosition(position).toString();
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(15);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_sign_on = findViewById(R.id.et_sign_on);
        et_sign_off = findViewById(R.id.et_sign_off);
        et_voyage_mos = findViewById(R.id.et_voyage_mos);
        et_voyage_days = findViewById(R.id.et_voyage_days);
        et_engine_watch_mos = findViewById(R.id.et_engine_watch_mos);
        et_engine_watch_days = findViewById(R.id.et_engine_watch_days);
        et_imo_number = findViewById(R.id.et_imo_number);
        tv_srb_file = findViewById(R.id.tv_srb_file);
        tv_cert_file = findViewById(R.id.tv_cert_file);
        tv_contract_file = findViewById(R.id.tv_contract_file);
        btn_srb_file = findViewById(R.id.btn_srb_file);
        btn_cert_file = findViewById(R.id.btn_cert_file);
        btn_contract_file = findViewById(R.id.btn_contract_file);

        if(!shipboard_id.equals("")){
            String saved_sign_on = db.getFieldString("sign_on", " shipboard_id = '"+shipboard_id+"'", "shipboard");
            String saved_sign_off = db.getFieldString("sign_off", " shipboard_id = '"+shipboard_id+"'", "shipboard");
            String saved_vessel_id = db.getFieldString("vessel_id", " shipboard_id = '"+shipboard_id+"'", "shipboard");
            String saved_vessel_id_ = db.getFieldString("name_vessel", " vessel_id = '"+saved_vessel_id+"'", "vessel");
            Integer saved_voyage_mos = db.getFieldInt("voyage_mos", " shipboard_id = '"+shipboard_id+"'", "shipboard");
            Integer saved_voyage_days = db.getFieldInt("voyage_days", " shipboard_id = '"+shipboard_id+"'", "shipboard");
            String saved_imo_number = db.getFieldString("imo_number", " shipboard_id = '"+shipboard_id+"'", "shipboard");
            String saved_srb_file = db.getFieldString("srb_file", " shipboard_id = '"+shipboard_id+"'", "shipboard");
            String saved_cert_file = db.getFieldString("cert_file", " shipboard_id = '"+shipboard_id+"'", "shipboard");
            String saved_contract_file = db.getFieldString("contract_file", " shipboard_id = '"+shipboard_id+"'", "shipboard");
            srb_file = saved_srb_file;
            cert_file = saved_cert_file;
            contract_file = saved_contract_file;
            spinner.setSelection(arrayAdapter_Vessel.getPosition(saved_vessel_id_));
            et_sign_on.getEditText().setText(saved_sign_on);
            et_sign_off.getEditText().setText(saved_sign_off);
            et_voyage_mos.getEditText().setText("" + saved_voyage_mos);
            et_voyage_days.getEditText().setText("" + saved_voyage_days);
            et_imo_number.getEditText().setText(saved_imo_number);
            tv_srb_file.setText(saved_srb_file);
            tv_cert_file.setText(saved_cert_file);
            tv_contract_file.setText(saved_contract_file);
        }

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker mdp_on = materialDateBuilder.build();
        final MaterialDatePicker mdp_off = materialDateBuilder.build();

        et_sign_on.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp_on.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        et_sign_on.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp_on.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        et_sign_off.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp_off.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        et_sign_off.getEditText().setOnClickListener(new View.OnClickListener() {
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
                            et_sign_on.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");
                            if(day.length() == 1){
                                day = "0"+ day;
                            }
                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_sign_on.getEditText().setText(completed);
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
                            et_sign_off.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");
                            if(day.length() == 1){
                                day = "0"+ day;
                            }
                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_sign_off.getEditText().setText(completed);
                        }
                    }
                });

        btn_save = findViewById(R.id.btn_save);
        btn_delete = findViewById(R.id.btn_delete);
        if(shipboard_id.equals("")){
            btn_delete.setVisibility(View.GONE);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //ADD
                    String sign_on = et_sign_on.getEditText().getText().toString();
                    String sign_off = et_sign_off.getEditText().getText().toString();
                    String voyage_mos = et_voyage_mos.getEditText().getText().toString();
                    String voyage_days = et_voyage_days.getEditText().getText().toString();
                    String engine_watch_mos = et_engine_watch_mos.getEditText().getText().toString();
                    String engine_watch_days = et_engine_watch_days.getEditText().getText().toString();
                    String imo_number = et_imo_number.getEditText().getText().toString();

                    if(vessel_id.equals("Select Vessel *")){
                        Toast.makeText(SeaServiceFormActivity.this, "Please select a vessel.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(sign_on.equals("")){
                        Toast.makeText(SeaServiceFormActivity.this, "Please select a sign on date.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(imo_number.equals("")){
                        Toast.makeText(SeaServiceFormActivity.this, "IMO Number is required", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Integer cnt = db.GetCount("shipboard", " WHERE sign_on = '"+sign_on+"'");
                    if(cnt > 0){
                        Toast.makeText(SeaServiceFormActivity.this, "Sign on date already existed. Please review your details", Toast.LENGTH_LONG).show();
                        return;
                    }

                    pd = new ProgressDialog(SeaServiceFormActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();

                    String shipboard_id = db.newId();
                    Integer id = db.newIntegerId("shipboard");

                    String vessel_id_ = db.getFieldString("vessel_id", " name_vessel = '"+vessel_id+"'", "vessel");

                    db.query("INSERT INTO shipboard (id, shipboard_id, person_id, sign_on, sign_off, engine_watch_mos, engine_watch_yrs, voyage_mos, voyage_days, vessel_id, imo_number, srb_file, cert_file, contract_file) VALUES ("+id+", '"+shipboard_id+"', '"+person_id+"', '"+sign_on+"', '"+sign_off+"', '"+engine_watch_mos+"', '"+engine_watch_days+"', '"+voyage_mos+"', '"+voyage_days+"', '"+vessel_id_+"', '"+imo_number+"', '"+srb_file+"', '"+cert_file+"', '"+contract_file+"')");

                    int conn = getConnection.getConnectionType(SeaServiceFormActivity.this);
                    if(conn != 0){
                        new SyncOnline(context, shipboard_id, "ADD").execute();
                    }else{
                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'shipboard', '" + shipboard_id + "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                        pd.dismiss();
                        Intent intent = new Intent(SeaServiceFormActivity.this, SeaServiceActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(SeaServiceFormActivity.this, "Record successfully added.", Toast.LENGTH_LONG).show();
                    }

                }
            });

        }else{ //update
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sign_on = et_sign_on.getEditText().getText().toString();
                    String sign_off = et_sign_off.getEditText().getText().toString();
                    String voyage_mos = et_voyage_mos.getEditText().getText().toString();
                    String voyage_days = et_voyage_days.getEditText().getText().toString();
                    String engine_watch_mos = et_engine_watch_mos.getEditText().getText().toString();
                    String engine_watch_days = et_engine_watch_days.getEditText().getText().toString();
                    String imo_number = et_imo_number.getEditText().getText().toString();

                    if(vessel_id.equals("Select Vessel *")){
                        Toast.makeText(SeaServiceFormActivity.this, "Please select a vessel.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(sign_on.equals("")){
                        Toast.makeText(SeaServiceFormActivity.this, "Please select a sign on date.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(imo_number.equals("")){
                        Toast.makeText(SeaServiceFormActivity.this, "IMO Number is required", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Integer cnt = db.GetCount("shipboard", " WHERE sign_on = '"+sign_on+"' AND shipboard_id != '"+shipboard_id+"'");
                    if(cnt > 0){
                        Toast.makeText(SeaServiceFormActivity.this, "Sign on date already existed. Please review your details", Toast.LENGTH_LONG).show();
                        return;
                    }

                    pd = new ProgressDialog(SeaServiceFormActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();

                    String vessel_id_ = db.getFieldString("vessel_id", " name_vessel = '"+vessel_id+"'", "vessel");

                    db.query("UPDATE shipboard SET sign_on = '"+sign_on+"', sign_off = '"+sign_off+"', imo_number = '"
                            +imo_number+"', vessel_id = '"+vessel_id_+"', voyage_mos = '"+voyage_mos+"', voyage_days = '"+voyage_days+"', " +
                            "engine_watch_mos = '"+engine_watch_mos+"', engine_watch_yrs = '"+engine_watch_days+"', " +
                            "srb_file = '"+srb_file+"', cert_file = '"+cert_file+"', contract_file = '"+contract_file+"' " +
                            "WHERE shipboard_id = '"+shipboard_id+"'");
                    Log.d("RESULT", "UPDATE shipboard SET sign_on = '"+sign_on+"', sign_off = '"+sign_off+"', imo_number = '"+imo_number+"', vessel_id = '"+vessel_id_+"', voyage_mos = '"+voyage_mos+"', voyage_days = '"+voyage_days+"', engine_watch_mos = '"+engine_watch_mos+"', engine_watch_yrs = '"+engine_watch_days+"' WHERE shipboard_id = '"+shipboard_id+"'");
                    int conn = getConnection.getConnectionType(SeaServiceFormActivity.this);
                    if(conn != 0){
                        new SyncOnline(context, shipboard_id, "UPDATE").execute();
                    }else{
                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'shipboard', '" + shipboard_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");
                        pd.dismiss();
                        Intent intent = new Intent(SeaServiceFormActivity.this, SeaServiceActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(SeaServiceFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();

                    }


                }
            });

            btn_delete.setVisibility(View.VISIBLE);
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SeaServiceFormActivity.this);

                    alertDialogBuilder.setMessage("Are you sure you want to delete this record?");
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            delete(shipboard_id);
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
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(SeaServiceFormActivity.this, R.color.white));
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(SeaServiceFormActivity.this, R.color.black));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(SeaServiceFormActivity.this, R.color.white));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(SeaServiceFormActivity.this, R.color.black));

                }
            });

        }


        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SeaServiceFormActivity.this);

                alertDialogBuilder.setMessage("Are you sure you want to leave without saving?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(SeaServiceFormActivity.this, SeaServiceActivity.class);
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
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(SeaServiceFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(SeaServiceFormActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(SeaServiceFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(SeaServiceFormActivity.this, R.color.black));
            }
        });

        btn_srb_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE_TYPE = 1;
                dispatchTakePictureIntent();
            }
        });
        btn_cert_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE_TYPE = 2;
                dispatchTakePictureIntent();
            }
        });
        btn_contract_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE_TYPE = 3;
                dispatchTakePictureIntent();
            }
        });
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if(IMAGE_TYPE == 1){
            imageFileName = "srb_" + timeStamp + ".jpg";
        }else if(IMAGE_TYPE == 2){
            imageFileName = "cert_" + timeStamp + ".jpg";
        }else if(IMAGE_TYPE == 3){
            imageFileName = "contract_" + timeStamp + ".jpg";
        }

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File image = File.createTempFile("eRTB_" + person_task_id, ".jpg", storageDir);
        File image = new File(storageDir, imageFileName);

        //Toast.makeText(PersonTaskActivity.this, "" + imageFileName, Toast.LENGTH_SHORT).show();
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        if(IMAGE_TYPE == 1){
            tv_srb_file.setText(imageFileName);
            srb_file = imageFileName;
        }else if(IMAGE_TYPE == 2){
            tv_cert_file.setText(imageFileName);
            cert_file = imageFileName;
        }else if(IMAGE_TYPE == 3){
            tv_contract_file.setText(imageFileName);
            contract_file = imageFileName;
        }

    }

    public void delete(String shipboard_id){
        pd = new ProgressDialog(SeaServiceFormActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Processing. Please wait .... ");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        db.query("DELETE FROM shipboard WHERE shipboard_id = '"+shipboard_id+"'");
        int conn = getConnection.getConnectionType(SeaServiceFormActivity.this);
        if(conn != 0){
            new SyncOnlineDelete(context, "shipboard", shipboard_id).execute();
        }else{
            Integer backup_item_id = db.newIntegerId("backup_item");
            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'shipboard', '" + person_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'DELETE', 'N')");
            pd.dismiss();

            Intent intent = new Intent(SeaServiceFormActivity.this, SeaServiceActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(SeaServiceFormActivity.this, "Record successfully deleted.", Toast.LENGTH_LONG).show();
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

            String saved_sign_on = db.getFieldString("sign_on", "shipboard_id = '" + id + "'", "shipboard");
            String saved_sign_off = db.getFieldString("sign_off", "shipboard_id = '" + id + "'", "shipboard");
            String saved_engine_watch_mos = db.getFieldString("engine_watch_mos", "shipboard_id = '" + id + "'", "shipboard");
            String saved_engine_watch_yrs = db.getFieldString("engine_watch_yrs", "shipboard_id = '" + id + "'", "shipboard");
            String saved_voyage_mos = db.getFieldString("voyage_mos", "shipboard_id = '" + id + "'", "shipboard");
            String saved_person_id = db.getFieldString("person_id", "shipboard_id = '" + id + "'", "shipboard");
            String saved_vessel_id = db.getFieldString("vessel_id", "shipboard_id = '" + id + "'", "shipboard");
            String saved_voyage_days = db.getFieldString("voyage_days", "shipboard_id = '" + id + "'", "shipboard");
            String saved_imo_number = db.getFieldString("imo_number", "shipboard_id = '" + id + "'", "shipboard");
            saved_imo_number = URLEncoder.encode(saved_imo_number);
            String saved_srb_file = db.getFieldString("srb_file", "shipboard_id = '" + id + "'", "shipboard");
            saved_srb_file = URLEncoder.encode(saved_srb_file);
            String saved_cert_file = db.getFieldString("cert_file", "shipboard_id = '" + id + "'", "shipboard");
            saved_cert_file = URLEncoder.encode(saved_cert_file);
            String saved_contract_file = db.getFieldString("contract_file", "shipboard_id = '" + id + "'", "shipboard");
            saved_contract_file = URLEncoder.encode(saved_contract_file);

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=shipboard&id="+id+"&sign_on="+saved_sign_on+"&sign_off="+saved_sign_off+"&engine_watch_mos="+saved_engine_watch_mos+"&engine_watch_yrs="+saved_engine_watch_yrs+"&voyage_mos="+saved_voyage_mos+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&voyage_days="+saved_voyage_days+"&imo_number="+saved_imo_number + "&srb_file="+ saved_srb_file + "&cert_file="+ saved_cert_file + "&contract_file=" +saved_contract_file + "&event="+event);
            Log.d("CONNECT", url +"sync.php?table=shipboard&id="+id+"&sign_on="+saved_sign_on+"&sign_off="+saved_sign_off+"&engine_watch_mos="+saved_engine_watch_mos+"&engine_watch_yrs="+saved_engine_watch_yrs+"&voyage_mos="+saved_voyage_mos+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&voyage_days="+saved_voyage_days+"&imo_number="+saved_imo_number + "&srb_file="+ saved_srb_file + "&cert_file="+ saved_cert_file + "&contract_file=" +saved_contract_file + "&event="+event);

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

            if(!saved_srb_file.equals("")){
                saved_srb_file = URLDecoder.decode(saved_srb_file);
                uploadImage(saved_srb_file);
            }
            if(!saved_cert_file.equals("")){
                saved_cert_file = URLDecoder.decode(saved_cert_file);
                uploadImage(saved_cert_file);
            }
            if(!saved_contract_file.equals("")){
                saved_contract_file = URLDecoder.decode(saved_contract_file);
                uploadImage(saved_contract_file);
            }

            return null;
        }
        protected void onPostExecute(Void result){
            pd.dismiss();
            Intent intent = new Intent(SeaServiceFormActivity.this, SeaServiceActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(SeaServiceFormActivity.this, "Record saved successfully.", Toast.LENGTH_LONG).show();
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
           Intent intent = new Intent(SeaServiceFormActivity.this, SeaServiceActivity.class);
           startActivity(intent);
           finish();
           Toast.makeText(SeaServiceFormActivity.this, "Record deleted successfully.", Toast.LENGTH_LONG).show();


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

        alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(SeaServiceFormActivity.this, SeaServiceActivity.class);
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
                            Log.d("RESULT: ", sourceFileUri + "File Upload Complete.");
                        }
                    });
                }else{
                    Log.d("RESULT: ", "REUPLOAD " + sourceFileUri);
                    uploadImage(sourceFileUri);
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( pd!=null && pd.isShowing() ){
            pd.cancel();
        }
    }
}
