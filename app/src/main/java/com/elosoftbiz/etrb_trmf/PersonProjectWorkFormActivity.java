package com.elosoftbiz.etrb_trmf;

import androidx.annotation.RequiresApi;
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
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PersonProjectWorkFormActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;

    TextInputLayout et_date_completed, et_details;
    String person_id, vessel_id, person_project_work_id;
    Button btn_save,btn_cancel, btn_upload;
    LinearLayout linear_sub;
    String str = "", err_message, upLoadServerUri;
    HttpResponse response;
    int serverResponseCode = 0;
    JSONObject json = null;
    TextView tvDetails;
    String imageFileName, currentPhotoPath;
    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_project_work_form);
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
        if (intent.hasExtra("person_project_work_id")) {
            person_project_work_id = intent.getStringExtra("person_project_work_id");
        }

        et_date_completed = findViewById(R.id.et_date_completed);
        et_details = findViewById(R.id.et_details);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);
        btn_upload = findViewById(R.id.btn_upload);
        tvDetails = findViewById(R.id.tvDetails);
        linear_sub = findViewById(R.id.linear_sub);

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        final MaterialDatePicker mdp = materialDateBuilder.build();

        et_date_completed.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdp.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        et_date_completed.getEditText().setOnClickListener(new View.OnClickListener() {
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
                            et_date_completed.getEditText().setText("");
                        }else{
                            String[] separated = dateSelected.split(" ");
                            String day = separated[1].replace(",", "");
                            if(day.length() == 1){
                                day = "0"+ day;
                            }
                            String completed = separated[2] + "-" + getMonth(separated[0]) + "-" + day;
                            et_date_completed.getEditText().setText(completed);
                        }
                    }
                });

        String saved_date_completed = db.getFieldString("date_completed", " person_project_work_id = '"+person_project_work_id+"'", "person_project_work");
        String saved_details = db.getFieldString("details", " person_project_work_id = '"+person_project_work_id+"'", "person_project_work");
        saved_details = URLDecoder.decode(saved_details);
        String saved_project_work_d_id = db.getFieldString("project_work_d_id", " person_project_work_id = '"+person_project_work_id+"'", "person_project_work");
        String pw_details = db.getFieldString("details", "project_work_d_id = '"+saved_project_work_d_id+"'", "project_work_d");
        pw_details = URLDecoder.decode(pw_details);
        tvDetails.setText(pw_details);
        et_date_completed.getEditText().setText(saved_date_completed);
        et_details.getEditText().setText(saved_details);

        new generate(context).execute();

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date_completed = et_date_completed.getEditText().getText().toString();
                String details = et_details.getEditText().getText().toString();
                details = URLEncoder.encode(details);
                if(date_completed.equals("")){
                    Toast.makeText(context, "Date Completed is required", Toast.LENGTH_LONG).show();
                    return;
                }
                db.execQuery("UPDATE person_project_work SET date_completed = '"+date_completed+"', details = '"+details+"' WHERE person_project_work_id = '"+person_project_work_id+"'");
                Intent intent1 = new Intent(context, PersonProjectWorkActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }

    public void startProgress() {

        runOnUiThread(new Runnable() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                display();
            }
        });
    }

    private class generate extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public generate(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("Loading. Please wait .... ");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected Void doInBackground(Void... arg0){
            startProgress();
            return null;
        }
        protected void onPostExecute(Void result){
            pd.dismiss();

        }
    }

    public void display(){
        linear_sub.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(50, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);

        TextView textView = new TextView(context);
        textView.setLayoutParams(layoutParams);
        textView.setText("Images");
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(20);
        textView.setTypeface(null, Typeface.BOLD);
        linear_sub.addView(textView);

        List<PersonProjectWorkFile> list = db.getPersonProjectWorkFile(person_project_work_id);
        for(PersonProjectWorkFile list1 : list){
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView1 = new TextView(context);
            textView1.setLayoutParams(layoutParams2);
            textView1.setText(list1.getFilename());
            textView1.setTextColor(Color.BLACK);
            textView1.setTextSize(20);
            linearLayout.addView(textView1);

            linear_sub.addView(linearLayout);
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "pw_" + timeStamp + ".jpg";


        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File image = File.createTempFile("eRTB_" + person_task_id, ".jpg", storageDir);
        File image = new File(storageDir, imageFileName);

        //Toast.makeText(PersonTaskActivity.this, "" + imageFileName, Toast.LENGTH_SHORT).show();
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        int id = db.newIntegerId("person_project_work_file");
        String person_project_work_file_id = db.newId();
        db.execQuery("INSERT INTO person_project_work_file (id, person_project_work_file_id, person_project_work_id, filename, file_desc, prio) VALUES("+id+", '"+person_project_work_file_id+"', '"+person_project_work_id+"', '"+imageFileName+"', '', '')");
        new generate(context).execute();
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
                Intent intent = new Intent(context, PersonProjectWorkActivity.class);
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
}