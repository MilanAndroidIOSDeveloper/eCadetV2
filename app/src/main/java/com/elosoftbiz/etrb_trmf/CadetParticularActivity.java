package com.elosoftbiz.etrb_trmf;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

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

public class CadetParticularActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;

    DatabaseHelper db;
    Button btn_update;
    TextView tv_name;
    String person_id, imageFileName, currentPhotoPath, upLoadServerUri;
    ImageView edit;
    ImageView imageView, profile;
    ProgressDialog progressDialog;
    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_TAKE_PHOTO = 1;
    int serverResponseCode = 0;

    String str = "", err_message;
    HttpResponse response;
    ProgressDialog pd;
    JSONObject json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadet_particular);

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

        db = new DatabaseHelper(this);
        int cnt = db.GetCount("person", "");
        upLoadServerUri = getString(R.string.url) + "upload_stud_photo.php";

        tv_name = findViewById(R.id.tv_name);
        if(cnt > 0){ //WITH DATA

            btn_update = findViewById(R.id.btn_update);
            btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CadetParticularActivity.this, PersonUpdateActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            person_id = db.getCadetId();
            String fname = db.getFieldString("fname", " person_id = '"+person_id+"'", "person");
            String mname = db.getFieldString("mname", " person_id = '"+person_id+"'", "person");
            String lname = db.getFieldString("lname", " person_id = '"+person_id+"'", "person");
            tv_name.setText(fname + " " + mname + " " + lname);

            String dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");
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

            TextView tv_dept = findViewById(R.id.tv_dept);
            tv_dept.setText("Department: " + dept);

            String birth_date = db.getFieldString("birth_date", " person_id = '"+person_id+"'", "person");
            TextView tv_birth_date = findViewById(R.id.tv_birth_date);
            tv_birth_date.setText("Date of Birth : " + birth_date);

            String birth_place = db.getFieldString("birth_place", " person_id = '"+person_id+"'", "person");
            TextView tv_birth_place = findViewById(R.id.tv_birth_place);
            tv_birth_place.setText("Place of Birth : " + birth_place);

            String nationality = db.getFieldString("nationality", " person_id = '"+person_id+"'", "person");
            TextView tv_nationality = findViewById(R.id.tv_nationality);
            tv_nationality.setText("Nationality : " + nationality);

            String passport_no = db.getFieldString("passport_no", " person_id = '"+person_id+"'", "person");
            TextView tv_passport_no = findViewById(R.id.tv_passport_no);
            tv_passport_no.setText("Passport No. : " + passport_no);

            String passport_no_issue_date = db.getFieldString("passport_no_issue_date", " person_id = '"+person_id+"'", "person");
            TextView tv_passport_no_issue_date = findViewById(R.id.tv_passport_no_issue_date);
            tv_passport_no_issue_date.setText("Passport Issue Date : " + passport_no_issue_date);

            String st_address = db.getFieldString("st_address", " person_id = '"+person_id+"'", "person");
            TextView tv_st_address = findViewById(R.id.tv_st_address);
            tv_st_address.setText("Home Address : " + st_address);

            String mobile = db.getFieldString("mobile", " person_id = '"+person_id+"'", "person");
            TextView tv_mobile = findViewById(R.id.tv_mobile);
            tv_mobile.setText("Mobile : " + mobile);

            String email = db.getFieldString("email", " person_id = '"+person_id+"'", "person");
             TextView tv_email = findViewById(R.id.tv_email);
            tv_email.setText("E-mail : " + email);

            String height = db.getFieldString("height", " person_id = '"+person_id+"'", "person");
            TextView tv_height = findViewById(R.id.tv_height);
            tv_height.setText("Height (cm) : " + height);

            String weight = db.getFieldString("weight", " person_id = '"+person_id+"'", "person");
            TextView tv_weight = findViewById(R.id.tv_weight);
            tv_weight.setText("Weight (Kg) : " + weight);

            String blood_group = db.getFieldString("blood_group", " person_id = '"+person_id+"'", "person");
            TextView tv_blood_group = findViewById(R.id.tv_blood_group);
            tv_blood_group.setText("Blood Type : " + blood_group);

            String marks = db.getFieldString("marks", " person_id = '"+person_id+"'", "person");
            TextView tv_marks = findViewById(R.id.tv_marks);
            tv_marks.setText("Distinguishing Marks : " + marks);

            String company_id = db.getFieldString("company_id", " person_id = '"+person_id+"'", "person");
            TextView tv_company_id = findViewById(R.id.tv_company_id);
            tv_company_id.setText("Company : " + company_id);

            String school_id = db.getFieldString("school_id", " person_id = '"+person_id+"'", "person");
            TextView tv_school_id = findViewById(R.id.tv_school_id);
            tv_school_id.setText("School : " + school_id);

            String emergency_contact_name = db.getFieldString("emergency_contact_name", " person_id = '"+person_id+"'", "person");
            TextView tv_emergency_contact_name = findViewById(R.id.tv_emergency_contact_name);
            tv_emergency_contact_name.setText("Next of Kin Name : " + emergency_contact_name);

            String emergency_contact_address = db.getFieldString("emergency_contact_address", " person_id = '"+person_id+"'", "person");
            TextView tv_emergency_contact_address = findViewById(R.id.tv_emergency_contact_address);
            tv_emergency_contact_address.setText("Next of Kin Address : " + emergency_contact_address);

            String emergency_relationship = db.getFieldString("emergency_relationship", " person_id = '"+person_id+"'", "person");
            TextView tv_emergency_relationship = findViewById(R.id.tv_emergency_relationship);
            tv_emergency_relationship.setText("Next of Kin Relationship : " + emergency_relationship);

            String emergency_contact_no = db.getFieldString("emergency_contact_no", " person_id = '"+person_id+"'", "person");
            TextView tv_emergency_contact_no = findViewById(R.id.tv_emergency_contact_no);
            tv_emergency_contact_no.setText("Next of Kin Contact No. : " + emergency_contact_no);

            String srn = db.getFieldString("srn", " person_id = '"+person_id+"'", "person");
            TextView tv_srn = findViewById(R.id.tv_srn);
            tv_srn.setText("SRN : " + srn);

            String sid_no = db.getFieldString("sid_no", " person_id = '"+person_id+"'", "person");
            TextView tv_sid_no = findViewById(R.id.tv_sid);
            tv_sid_no.setText("SID No. : " + sid_no);

            String srb_no = db.getFieldString("srb_no", " person_id = '"+person_id+"'", "person");
            TextView tv_srb_no = findViewById(R.id.tv_srb);
            tv_srb_no.setText("SRB No. : " + srb_no);

            String srb_date = db.getFieldString("srb_date", " person_id = '"+person_id+"'", "person");
            TextView tv_srb_date = findViewById(R.id.tv_srb_date);
            tv_srb_date.setText("SRB Date of Issue : " + srb_date);

            profile = findViewById(R.id.profile);

            final String photo_file = db.getFieldString("photo_file", " person_id = '"+person_id+"'", "person");
            File file = new File("/storage/emulated/0/Android/data/com.elosoftbiz.etrb_trmf/files/" + Environment.DIRECTORY_PICTURES + "/" + photo_file);
            currentPhotoPath = file.getAbsolutePath();
            if(!photo_file.equals("") && !photo_file.equals("null")){
               if(file.exists() ){
                    Bitmap bm = BitmapFactory.decodeFile(currentPhotoPath);
                    profile.setImageBitmap(bm);
                    Log.d("RESULT",  "" + file);

               }else{
                   Log.d("RESULT",  "NO IMAGE" + file);
                   profile.setImageResource(R.drawable.profile);
               }
            }else{
                Log.d("RESULT",  "NO IMAGE" + file);
                profile.setImageResource(R.drawable.profile);
            }

            edit = findViewById(R.id.edit);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CapturePhoto();
                }
            });
        }else{
            btn_update = findViewById(R.id.btn_update);
            btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CadetParticularActivity.this, "You are currently using a demo version. Acquire a license now to activate this feature.", Toast.LENGTH_LONG).show();
                }
            });
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

    public void CapturePhoto(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CadetParticularActivity.this);
        alertDialogBuilder.setTitle("");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("CLOSE",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                    }
                });
        alertDialogBuilder.setPositiveButton("UPDATE", null);
        alertDialogBuilder.setNegativeButton("CLOSE", null);


        ScrollView main_linearLayout = new ScrollView( CadetParticularActivity.this );

        LinearLayout linearlayout = new LinearLayout( CadetParticularActivity.this );
        linearlayout.setPadding( 30, 30, 30, 30 );
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
        linearlayout.setOrientation(LinearLayout.VERTICAL);
        linearlayout.setLayoutParams( llParams );

        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(250, 250);
        ivParams.setMargins(10, 20, 10, 10);
        ivParams.gravity = Gravity.CENTER_HORIZONTAL;

        imageView = new ImageView(CadetParticularActivity.this);
        imageView.setBackgroundResource(R.drawable.border1dp);
        imageView.setImageResource(R.drawable.ic_photo_size_select_actual_black_24dp);
        imageView.setLayoutParams(ivParams);
        imageView.setAdjustViewBounds(true);
        linearlayout.addView(imageView);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(10, 5, 10, 10);

        Button btn_takephoto = new Button(CadetParticularActivity.this);
        btn_takephoto.setText("CAPTURE PHOTO");
        btn_takephoto.setBackgroundColor(Color.parseColor("#115181"));
        btn_takephoto.setTextColor(Color.parseColor("#ffffff"));
        btn_takephoto.setGravity(Gravity.CENTER_HORIZONTAL);
        btn_takephoto.setLayoutParams(btnParams);
        linearlayout.addView(btn_takephoto);

        btn_takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(shouldAskPermissions()){
                    askPermissions();
                }
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
                if(imageFileName.equals("")){
                    Toast.makeText(CadetParticularActivity.this, "Capture a photo to update your profile.", Toast.LENGTH_LONG).show();
                    return;
                }

                progressDialog = new ProgressDialog(CadetParticularActivity.this);
                progressDialog.setMessage("Processing. Please wait .... ");
                progressDialog.setTitle(""); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);

                db.query("UPDATE person SET photo_file = '"+imageFileName+"' WHERE person_id= '"+person_id+"'");
                Log.d("QUERY", "UPDATE person SET photo_file = '"+imageFileName+"' WHERE person_id= '"+person_id+"'");
                int conn = getConnection.getConnectionType(CadetParticularActivity.this);
                if(conn != 0){
                    new UploadPhotoLive(CadetParticularActivity.this).execute();
                }else{
                    Integer backup_item_id = db.newIntegerId("backup_item");
                    db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_photo', '" + person_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");

                    alertDialog.dismiss();
                    progressDialog.dismiss();
                    Intent intent = new Intent(CadetParticularActivity.this, CadetParticularActivity.class);
                    startActivity(intent);
                    finish();
                }

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

    private class UploadPhotoLive extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public UploadPhotoLive(Context context)
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

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_photo&id="+person_id+"&photo_file="+imageFileName);
            Log.d("CONNECT", url +"sync.php?table=person&id="+person_id+"&photo_file="+imageFileName);

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
            progressDialog.dismiss();
            Intent intent = new Intent(CadetParticularActivity.this, CadetParticularActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(CadetParticularActivity.this, "Profile photo updated successfully.", Toast.LENGTH_LONG).show();

        }
    }


    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT);
        //return true;
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
        String new_id = db.newId();
        imageFileName = "eTRB_" + new_id + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/" + imageFileName);
        currentPhotoPath = storageDir.getAbsolutePath();
        Bitmap bm = BitmapFactory.decodeFile(currentPhotoPath);

        imageView.setImageBitmap(bm);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CadetParticularActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
