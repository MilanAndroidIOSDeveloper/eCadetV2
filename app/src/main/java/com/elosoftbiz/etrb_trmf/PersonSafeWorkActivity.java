package com.elosoftbiz.etrb_trmf;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PersonSafeWorkActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    DatabaseHelper db;
    String person_id, dept;
    ProgressDialog pd;
    LinearLayout main_container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_safe_work);
        context = this;
        db = new DatabaseHelper(context);

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

        main_container = findViewById(R.id.main_container);
        person_id = db.getCadetId();
        dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");
        int cnt = db.GetCount("person", "");
        if(cnt > 0){
            new generate(context).execute();
        }else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,5,5,5);

            TextView textView = new TextView(context);
            textView.setText("Your app is not yet activated. Please download your data to use the e-cadet's full features.");
            textView.setTextColor(Color.RED);
            textView.setTextSize(20);
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            main_container.addView(textView);
        }
    }

    private class generate extends AsyncTask<Void, Void, Void>{
        public Context context;
        public generate(Context context)
        {
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

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(context);
        textView.setText("Tasks the Cadet should be familiar with:");
        textView.setBackgroundResource(R.drawable.border_darkblue);
        textView.setTextSize(18);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(5,5,5,5);
        textView.setLayoutParams(layoutParams);
        linearLayout.addView(textView);
        main_container.addView(linearLayout);
        List<WorkPractice> list= db.getWorkPractice("");
        int cnt = 1;
        for (WorkPractice list1 : list){
            linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackgroundResource(R.drawable.border1dp);

            textView = new TextView(context);
            textView.setText(cnt + ". " + list1.getDesc_work_practice());
            textView.setTextSize(18);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(15,5,15,5);
            textView.setLayoutParams(layoutParams);
            linearLayout.addView(textView);

            String curr_vessel_id = db.getCadetVessel(person_id);
            String vessel_id = db.getFieldString("vessel_id", "person_id=  '"+person_id+"' AND vessel_id = '"+curr_vessel_id+"'", "person_work_practice");
            String name_vessel = db.getFieldString("name_vessel", " vessel_id = '"+vessel_id+"'", "vessel");

            textView = new TextView(context);
            textView.setText("Ship Name : " + name_vessel);
            textView.setTextSize(18);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(15,5,15,5);
            textView.setLayoutParams(layoutParams);
            linearLayout.addView(textView);

            String checked_by_id = db.getFieldString("checked_by_id", "person_id=  '"+person_id+"' AND vessel_id = '"+curr_vessel_id+"'", "person_work_practice");
            String full_name = db.getFieldString("full_name", "person_id = '"+checked_by_id+"'", "person");
            textView = new TextView(context);
            textView.setText("STO : " + full_name);
            textView.setTextSize(18);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(15,5,15,5);
            textView.setLayoutParams(layoutParams);
            linearLayout.addView(textView);

            String date_checked = db.getFieldString("date_checked", "person_id=  '"+person_id+"' AND vessel_id = '"+curr_vessel_id+"'", "person_work_practice");
            if(!date_checked.equals("")){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
                try {
                    Date date = format.parse(date_checked);
                    date_checked = format2.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            textView = new TextView(context);
            textView.setText("Date : " + date_checked);
            textView.setTextSize(18);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(15,5,15,5);
            textView.setLayoutParams(layoutParams);
            linearLayout.addView(textView);

            main_container.addView(linearLayout);
            cnt++;
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( pd!=null && pd.isShowing() ){
            pd.cancel();
        }
    }
}