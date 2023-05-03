package com.elosoftbiz.etrb_trmf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 300;
    Context context;
    ProgressDialog pd;
    DatabaseHelper db;
    HttpResponse response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        db = new DatabaseHelper(this);

        String person_id = db.getCadetId();
        final String w_fr = db.getFieldString("w_fr", " person_id = '"+person_id+"'", "person");
        context = this;
        int cnt = db.GetCount("task"," WHERE 1=1");
        if(cnt == 0){
            new Generate(context).execute();
        }else{
            final int cnt2 = db.GetCount("person", "");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    if(cnt2 > 0){
                        if(w_fr.equals("Y")){
                            intent = new Intent(SplashScreen.this, LoginActivity.class); //Main here
                        }else{
                            intent = new Intent(SplashScreen.this, CaptureActivity.class); //Main here
                        }
                    }else{
                        intent = new Intent(SplashScreen.this, DownloadFromServerActivity.class); //Main here
                    }
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    private class Generate extends AsyncTask<Void, Void, Void>{
        public Context context;
        public Generate(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pd = new ProgressDialog(SplashScreen.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("Loading. This may take a few minutes. Please wait .... ");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            String filename = "etrb_trmf_v2.txt"; //ETRB_V2 IS USED IN VHSIP

            BufferedReader reader = null;
            try {
                reader = new BufferedReader(
                        new InputStreamReader(getAssets().open(filename), "UTF-8"));

                // do reading, usually loop until end of file reading
                String mLine;
                while ((mLine = reader.readLine()) != null) {
                    Log.d("QUERY", mLine);
                    db.execQuery(mLine);
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
            return null;
        }
        protected void onPostExecute(Void result){
            pd.dismiss();
            Intent i = new Intent(SplashScreen.this, DownloadFromServerActivity.class);
            startActivity(i);

            finish();

        }
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
