package com.elosoftbiz.etrb_trmf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.luxand.FSDK;

public class LoginActivity extends AppCompatActivity {
    Button btn_setup;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 1;

    private boolean mIsFailed = false;
    private Preview mPreview;
    private ProcessImageAndDrawResults mDraw;
    private final String database = "Memory70.dat";
    private final String help_text = "Luxand Face Recognition\n\nJust tap any detected face and name it. The app will recognize this face further. For best results, hold the device at arm's length. You may slowly rotate the head for the app to memorize you at multiple views. The app can memorize several persons. If a face is not recognized, tap and name it again.\n\nThe SDK is available for mobile developers: www.luxand.com/facesdk";

    private boolean wasStopped = false;
    public static float sDensity = 1.0f;
    private int rotation;

    FrameLayout camera_preview;
    DatabaseHelper db;
    String fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(LoginActivity.this);
        String person_id = db.getCadetId();
        fname = db.getFieldString("fname", "person_id = '"+person_id+"'", "person");
        btn_setup = findViewById(R.id.btn_setup);


        String w_fr = db.getFieldString("w_fr", "person_id = '"+person_id+"'", "person");
        if(w_fr.equals("N")){
            btn_setup.setVisibility(View.VISIBLE);
        }else{
            btn_setup.setVisibility(View.GONE);
        }

        btn_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CaptureActivity.class);
                startActivity(intent);
                finish();
            }
        });

        int res = FSDK.ActivateLibrary("p02h8cIr8WjGRWoOecVavJpa8wq23q2XHxw8v8cw+fIzdGWW17JMoNMzdeaV9HIVWoEF4opL26KXSE2pa7ixowYM6QWoBZaiQZLGHJ6kbnekVCAdale1qMN1pnYAnLxhM4KhBNlY9rvv9GRdWcSPhmf40hUeEk5guwBhIZmnZcA=");
        if (res != FSDK.FSDKE_OK) {
            mIsFailed = true;
            //showErrorAndClose("FaceSDK activation failed", res);
        } else {
            FSDK.Initialize();

            camera_preview = findViewById(R.id.camera_preview);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            checkCameraPermissionsAndOpenCamera();
        }
    }

    private void checkCameraPermissionsAndOpenCamera() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                final Runnable onCloseAlert = new Runnable() {
                    @Override
                    public void run() {
                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[] {Manifest.permission.CAMERA},
                                CAMERA_PERMISSION_REQUEST_CODE);
                    }
                };

                //alert(this, onCloseAlert, "The application processes frames from camera.");
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        CAMERA_PERMISSION_REQUEST_CODE);
            }
        }else{
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        openCamera();

    }

    private void openCamera() {
        // Camera layer and drawing layer

        mDraw = new ProcessImageAndDrawResults(this, "N", "");
        mPreview = new Preview(this, mDraw);
        //mPreview.setBackgroundColor(Color.GREEN);
        //mDraw.setBackgroundColor(Color.RED);
        mDraw.mTracker = new FSDK.HTracker();
        String templatePath = this.getApplicationInfo().dataDir + "/" + database;
        if (FSDK.FSDKE_OK != FSDK.LoadTrackerMemoryFromFile(mDraw.mTracker, templatePath)) {
            int res = FSDK.CreateTracker(mDraw.mTracker);
            if (FSDK.FSDKE_OK != res) {
                //showErrorAndClose("Error creating tracker", res);
            }
        }

        resetTrackerParameters();

        camera_preview.addView(mPreview);
        camera_preview.addView(mDraw);

    }

    private void resetTrackerParameters() {
        int errpos[] = new int[1];
        FSDK.SetTrackerMultipleParameters(mDraw.mTracker, "ContinuousVideoFeed=true;FacialFeatureJitterSuppression=0;RecognitionPrecision=1;Threshold=0.996;Threshold2=0.9995;ThresholdFeed=0.97;MemoryLimit=2000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;", errpos);
        if (errpos[0] != 0) {
            //showErrorAndClose("Error setting tracker parameters, position", errpos[0]);
        }
    }

    @Override
    protected void onStop() {
        if (mDraw != null || mPreview != null) {
            mPreview.setVisibility(View.GONE); // to destroy surface
            mPreview.releaseCallbacks();
            mPreview = null;
            mDraw = null;
            wasStopped = true;
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (wasStopped && mDraw == null) {
            checkCameraPermissionsAndOpenCamera();
            //openCamera();
            wasStopped = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDraw != null) {
            pauseProcessingFrames();
            String templatePath = this.getApplicationInfo().dataDir + "/" + database;
            FSDK.SaveTrackerMemoryToFile(mDraw.mTracker, templatePath);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsFailed)
            return;
        resumeProcessingFrames();
    }

    private void pauseProcessingFrames() {
        if (mDraw != null) {
            mDraw.mStopping = 1;

            // It is essential to limit wait time, because mStopped will not be set to 0, if no frames are feeded to mDraw
            for (int i = 0; i < 100; ++i) {
                if (mDraw.mStopped != 0) break;
                try {
                    Thread.sleep(10);
                } catch (Exception ex) {
                }
            }
        }
    }

    private void resumeProcessingFrames() {
        if (mDraw != null) {
            mDraw.mStopped = 0;
            mDraw.mStopping = 0;
        }
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            DatabaseHelper db = new DatabaseHelper(LoginActivity.this);
            db.query("UPDATE person SET logged_in = 'N'");
            finish();
        }
    }
}
