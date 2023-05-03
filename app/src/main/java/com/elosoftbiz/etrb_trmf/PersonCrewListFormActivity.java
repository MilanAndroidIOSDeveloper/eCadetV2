package com.elosoftbiz.etrb_trmf;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.Date;

public class PersonCrewListFormActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_filename;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;
    TextInputLayout et_date_uploaded;
    String person_id, vessel_id;
    Button btn_save,btn_cancel, btn_delete, btn_filename;
    String person_crew_list_id ="", currentPhotoPath, imageFileName, filename;

    String str = "", err_message, upLoadServerUri;
    HttpResponse response;
    int serverResponseCode = 0;
    JSONObject json = null;

    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_crew_list_form);

        /*** LOGO AND HEADER NAME HERE ***/
        mToolbar = findViewById( R.id.nav_action );
        setSupportActionBar( mToolbar );
        mDrawerLayout = findViewById( R.id.drawerLayout );
        mToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.open, R.string.close );
        mDrawerLayout.addDrawerListener( mToggle );
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        /*** END OF LOGO AND HEADER NAME ***/
        context = this;
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
        if (intent.hasExtra("person_crew_list_id")) {
            person_crew_list_id = intent.getStringExtra("person_crew_list_id");
        }

        et_date_uploaded = findViewById(R.id.et_date_uploaded);
        tv_filename = findViewById(R.id.tv_filename);
        btn_filename = findViewById(R.id.btn_filename);
        btn_save = findViewById(R.id.btn_save);
        btn_delete = findViewById(R.id.btn_delete);
        btn_cancel = findViewById(R.id.btn_cancel);

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker mdp = materialDateBuilder.build();

        et_date_uploaded.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        et_date_uploaded.getEditText().setOnClickListener(new View.OnClickListener() {
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
                            et_date_uploaded.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");
                            if(day.length() == 1){
                                day = "0"+ day;
                            }
                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_date_uploaded.getEditText().setText(completed);
                        }
                    }
                });


        if(!person_crew_list_id.equals("")){ //UPDATE
            String saved_date_uploaded = db.getFieldString("date_uploaded", " person_crew_list_id = '"+person_crew_list_id+"'", "person_crew_list");
            String saved_filename = db.getFieldString("filename", " person_crew_list_id = '"+person_crew_list_id+"'", "person_crew_list");
            filename = saved_filename;
            et_date_uploaded.getEditText().setText(saved_date_uploaded);
            tv_filename.setText(saved_filename);

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String date_uploaded = et_date_uploaded.getEditText().getText().toString();

                    if(date_uploaded.equals("")){
                        Toast.makeText(context, "Date is rewuired.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(filename.equals("")){
                        Toast.makeText(context, "Crew List File is required", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Integer cnt = db.GetCount("person_crew_list", " WHERE date_uploaded = '"+date_uploaded+"' AND person_crew_list_id != '"+person_crew_list_id+"'");
                    if(cnt > 0){
                        Toast.makeText(context, "Date already existed. Please review your details", Toast.LENGTH_LONG).show();
                        return;
                    }

                    pd = new ProgressDialog(context);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();

                    db.query("UPDATE person_crew_list SET date_uploaded = '"+date_uploaded+"', filename = '"+filename+"' WHERE person_crew_list_id = '"+person_crew_list_id+"' ");
                    Log.d("RESULT", "UPDATE person_crew_list SET date_uploaded = '"+date_uploaded+"', filename = '"+filename+"' WHERE person_crew_list_id = '"+person_crew_list_id+"' ");
                    int conn = getConnection.getConnectionType(context);
                    if(conn != 0){
                        new SyncOnline(context, person_crew_list_id, "UPDATE").execute();
                    }else{
                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_crew_list', '" + person_crew_list_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");
                        pd.dismiss();
                        Intent intent = new Intent(context, PersonCrewListActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(context, "Record successfully saved.", Toast.LENGTH_LONG).show();

                    }


                }
            });

            btn_delete.setVisibility(View.VISIBLE);
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setMessage("Are you sure you want to delete this record?");
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            delete(person_crew_list_id);
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
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black));

                }
            });


        }else{ //ADD
            btn_delete.setVisibility(View.GONE);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String date_uploaded = et_date_uploaded.getEditText().getText().toString();

                    if(date_uploaded.equals("")){
                        Toast.makeText(context, "Date is required.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(filename.equals("")){
                        Toast.makeText(context, "Crew List File is required.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Integer cnt = db.GetCount("person_crew_list", " WHERE date_uploaded = '"+date_uploaded+"'");
                    if(cnt > 0){
                        Toast.makeText(context, "Date already existed. Please review your details", Toast.LENGTH_LONG).show();
                        return;
                    }

                    pd = new ProgressDialog(context);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Processing. Please wait .... ");
                    pd.setIndeterminate(true);
                    pd.setCancelable(false);
                    pd.show();

                    String person_crew_list_id = db.newId();
                    Integer id = db.newIntegerId("person_crew_list");

                    String vessel_id = db.getCadetVessel(person_id);

                    db.query("INSERT INTO person_crew_list (id, person_crew_list_id, person_id, vessel_id, date_uploaded, filename, checked_by_id, date_checked, checked_remarks, login_id, last_update) VALUES ("+id+", '"+person_crew_list_id+"', '"+person_id+"', '"+vessel_id+"', '"+date_uploaded+"', '"+filename+"', '', '', '', '"+person_id+"', DATE())");
                    Log.d("RESULT", "INSERT INTO person_crew_list (id, person_crew_list_id, person_id, vessel_id, date_uploaded, filename, checked_by_id, date_checked, checked_remarks, login_id, last_update) VALUES ("+id+", '"+person_crew_list_id+"', '"+person_id+"', '"+vessel_id+"', '"+date_uploaded+"', '"+filename+"', '', '', '', '"+person_id+"', DATE())");
                    int conn = getConnection.getConnectionType(context);
                    if(conn != 0){
                        new SyncOnline(context, person_crew_list_id, "ADD").execute();
                    }else{
                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_crew_list', '" + person_crew_list_id + "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                        pd.dismiss();
                        Intent intent = new Intent(context, PersonCrewListActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(context, "Record successfully added.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setMessage("Are you sure you want to leave without saving?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(context, PersonCrewListActivity.class);
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
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
            }
        });

        btn_filename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        imageFileName = "crewlist_" + timeStamp + ".jpg";


        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File image = File.createTempFile("eRTB_" + person_task_id, ".jpg", storageDir);
        File image = new File(storageDir, imageFileName);

        //Toast.makeText(PersonTaskActivity.this, "" + imageFileName, Toast.LENGTH_SHORT).show();
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        tv_filename.setText(imageFileName);
        filename = imageFileName;

    }

    public void delete(String shipboard_id){
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Processing. Please wait .... ");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        db.query("DELETE FROM person_crew_list WHERE person_crew_list_id = '"+person_crew_list_id+"'");
        int conn = getConnection.getConnectionType(context);
        if(conn != 0){
            new SyncOnlineDelete(context, "person_crew_list", person_crew_list_id).execute();
        }else{
            Integer backup_item_id = db.newIntegerId("backup_item");
            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_crew_list', '" + person_crew_list_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'DELETE', 'N')");
            pd.dismiss();

            Intent intent = new Intent(context, PersonCrewListActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(context, "Record successfully deleted.", Toast.LENGTH_LONG).show();
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
            String saved_vessel_id = db.getFieldString("vessel_id", "person_crew_list_id = '" + id + "'", "person_crew_list");
            String saved_date_uploaded = db.getFieldString("date_uploaded", "person_crew_list_id = '" + id + "'", "person_crew_list");
            String saved_filename = db.getFieldString("filename", "person_crew_list_id = '" + id + "'", "person_crew_list");
            saved_filename = URLEncoder.encode(saved_filename);
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_crew_list_id = '" + id + "'", "person_crew_list");
            String saved_date_checked = db.getFieldString("date_checked", "person_crew_list_id = '" + id + "'", "person_crew_list");
            String saved_checked_remarks = db.getFieldString("checked_remarks", "person_crew_list_id = '" + id + "'", "person_crew_list");
            saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
            String saved_login_id = db.getFieldString("login_id", "person_crew_list_id = '" + id + "'", "person_crew_list");
            String saved_last_update = db.getFieldString("last_update", "person_crew_list_id = '" + id + "'", "person_crew_list");
            saved_last_update = URLEncoder.encode(saved_last_update);

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_crew_list&id="+id+"&person_id="+person_id+"&vessel_id="+saved_vessel_id+"&date_uploaded="+saved_date_uploaded+"&filename="+saved_filename+"&checked_by_id="+saved_checked_by_id+"&date_checked="+saved_date_checked+"&checked_remarks="+saved_checked_remarks+"&login_id="+saved_login_id+"&last_update="+saved_last_update + "&event="+event);
            Log.d("CONNECT", url +"sync.php?table=person_crew_list&id="+id+"&person_id="+person_id+"&vessel_id="+saved_vessel_id+"&date_uploaded="+saved_date_uploaded+"&filename="+saved_filename+"&checked_by_id="+saved_checked_by_id+"&date_checked="+saved_date_checked+"&checked_remarks="+saved_checked_remarks+"&login_id="+saved_login_id+"&last_update="+saved_last_update + "&event="+event);

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

            if(!saved_filename.equals("")){
                saved_filename = URLDecoder.decode(saved_filename);
                uploadImage(saved_filename);
            }


            return null;
        }
        protected void onPostExecute(Void result){
            pd.dismiss();
            Intent intent = new Intent(context, PersonCrewListActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(context, "Record saved successfully.", Toast.LENGTH_LONG).show();
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
            Intent intent = new Intent(context, PersonCrewListActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(context, "Record deleted successfully.", Toast.LENGTH_LONG).show();


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
                Intent intent = new Intent(context, PersonCrewListActivity.class);
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