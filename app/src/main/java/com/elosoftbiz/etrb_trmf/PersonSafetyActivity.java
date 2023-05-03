package com.elosoftbiz.etrb_trmf;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PersonSafetyActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;

    TextView tv_title, tv_instruct;
    DatabaseHelper db;

    String str = "", err_message, person_id, safety_h_id;
    ProgressDialog pd;
    HttpResponse response;
    JSONObject json = null;
    LinearLayout main_container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_safety);

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

        db = new DatabaseHelper(context);

        tv_title = findViewById(R.id.tv_title);
        tv_instruct = findViewById(R.id.tv_instruct);
        main_container = findViewById(R.id.main_container);

        int cnt = db.GetCount("person", "");
        if(cnt > 0){ //WITH DATA
            person_id = db.getCadetId();
            Intent intent = getIntent();
            if (intent.hasExtra("safety_h_id")) {
                safety_h_id = intent.getStringExtra("safety_h_id");
            }
            String desc_safety = db.getFieldString("desc_safety_h", "safety_h_id = '"+safety_h_id+"'", "safety_h");
            tv_title.setText(desc_safety);
            String remarks = db.getFieldString("remarks", "safety_h_id = '"+safety_h_id+"'", "safety_h");
            remarks = URLDecoder.decode(remarks);
            remarks = URLDecoder.decode(remarks);

            tv_instruct.setText(remarks);
            tv_instruct.setTextSize(20);
            new Generate(context).execute();
        }else{
            tv_title.setText("Cadet's Onboard Familiarization");
            tv_instruct.setText(getString(R.string.notLicensed));
            tv_instruct.setTextColor(getColor(R.color.red));
            tv_instruct.setTextSize(20);
            tv_instruct.setPadding(10,20,10,10);
            tv_instruct.setGravity(Gravity.CENTER_HORIZONTAL);

        }
    }

    private class Generate extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public Generate(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
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

    public void startProgress() {
        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                display();
            }
        });
    }

    public void display() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(10,20,10,0);
        layoutParams2.setMargins(10,0,10,0);
        String vessel_id = db.getCadetVessel(person_id);

        int cnt = db.GetCount("person_safety" , " WHERE safety_id IN (SELECT safety_d_id FROM safety_d WHERE safety_h_id = '"+safety_h_id+"') AND ship_id = '"+vessel_id+"' AND person_id = '"+person_id+"'");
        if(cnt == 0){ //AUTO
            List<SafetyD> safetyD = db.getSafetyD(safety_h_id);
            for (SafetyD cn : safetyD){
                int id = db.newIntegerId("person_safety");
                String person_safety_id = db.newId();
                db.execQuery("INSERT INTO person_safety (id, person_safety_id, person_id, safety_id, date_completed, ship_id) VALUES ("+id+", '"+person_safety_id+"', '"+person_id+"', '"+cn.getSafety_d_id()+"', '', '"+vessel_id+"')");
                Log.d("RESULT", "INSERT INTO person_safety (id, person_safety_id, person_id, safety_id, date_completed, ship_id) VALUES ("+id+", '"+person_safety_id+"', '"+person_id+"', '"+cn.getSafety_d_id()+"', '', '"+vessel_id+"')");
            }
        }

        String table_header = db.getFieldString("table_header", " safety_h_id = '"+safety_h_id+"'", "safety_h");

        //DISPLAY
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setBackgroundColor(getColor(R.color.colorPrimaryDark));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView tv_header = new TextView(context);
        tv_header.setLayoutParams(layoutParams);
        tv_header.setText(table_header);
        tv_header.setTextColor(getColor(R.color.white));
        tv_header.setTextSize(20);
        tv_header.setTypeface(null, Typeface.BOLD);
        tv_header.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(tv_header);
        main_container.addView(linearLayout);

        List<SafetyD> safetyDS = db.getSafetyD(safety_h_id);
        for (SafetyD cn : safetyDS){
            String desc_safety_d = cn.getDesc_safety_d();
            desc_safety_d = URLDecoder.decode(desc_safety_d);

            linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(layoutParams2);
            linearLayout.setBackgroundResource(R.drawable.border1dp);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            TextView textView = new TextView(context);
            textView.setText(desc_safety_d);
            textView.setTextColor(getColor(R.color.black));
            textView.setTextSize(20);
            textView.setPadding(10,10,10,10);
            textView.setLayoutParams(layoutParams2);
            linearLayout.addView(textView);

            String name_vessel = db.getFieldString("name_vessel", " vessel_id = '"+vessel_id+"'", "vessel");
            textView = new TextView(context);
            textView.setText("Vessel : " + name_vessel);
            textView.setTextColor(getColor(R.color.black));
            textView.setTextSize(20);
            textView.setPadding(10,0,10,0);
            textView.setLayoutParams(layoutParams2);
            linearLayout.addView(textView);

            String checked_by_id = db.getFieldString("checked_by_id", " ship_id = '"+vessel_id+"' AND person_id = '"+person_id+"' AND safety_id = '"+cn.getSafety_d_id()+"'", "person_safety");
            String checked_by = db.getFieldString("full_name", "person_id = '"+checked_by_id+"'", "person");
            textView = new TextView(context);
            textView.setText("Officer : " + checked_by);
            textView.setTextColor(getColor(R.color.black));
            textView.setTextSize(20);
            textView.setPadding(10,0,10,0);
            textView.setLayoutParams(layoutParams2);
            linearLayout.addView(textView);

            String date_checked = db.getFieldString("date_checked", " ship_id = '"+vessel_id+"' AND person_id = '"+person_id+"' AND safety_id = '"+cn.getSafety_d_id()+"'", "person_safety");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
            if(!date_checked.equals("")){
                try {
                    Date date = format.parse(date_checked);
                    date_checked = format2.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            textView = new TextView(context);
            textView.setText("Date Signed : " + date_checked);
            textView.setTextColor(getColor(R.color.black));
            textView.setTextSize(20);
            textView.setPadding(10,0,10,0);
            textView.setLayoutParams(layoutParams2);
            linearLayout.addView(textView);

            main_container.addView(linearLayout);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, ShipboardSafetyActivity.class);
        startActivity(intent);
        finish();
    }
}