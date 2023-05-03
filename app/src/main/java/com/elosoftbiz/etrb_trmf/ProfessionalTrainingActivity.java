package com.elosoftbiz.etrb_trmf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProfessionalTrainingActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;
    String person_id = "", dept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_training);
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
        person_id = db.getCadetId();
        dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");

        tv_title = findViewById(R.id.tv_title);
        main_container = findViewById(R.id.main_container);

        int cnt = db.GetCount("person", "");

        if(cnt > 0){
            new generate(context).execute();
        }else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(context);
            textView.setText("Training Description");
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(layoutParams2);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView);

            textView = new TextView(context);
            textView.setText("Date Completed");
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(layoutParams2);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView);

            textView = new TextView(context);
            textView.setText("Name of Maritime Training Institution (MTI)");
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(layoutParams2);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView);

            main_container.addView(linearLayout);

            linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView1 = new TextView(context);
            textView1.setLayoutParams(layoutParams2);
            textView1.setBackgroundResource(R.drawable.border1dp);
            textView1.setTextSize(18);
            textView1.setTextColor(Color.RED);
            textView1.setText("NOT YET ACTIVATED");
            textView1.setGravity(Gravity.CENTER);
            textView1.setPadding(5,5,5,5);
            linearLayout.addView(textView1);

            main_container.addView(linearLayout);
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

    private class generate extends AsyncTask<Void, Void, Void>{
        public Context context;
        public generate(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pd = new ProgressDialog(ProfessionalTrainingActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("Loading. Please wait .... ");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected Void doInBackground(Void... arg0){

            int cnt = db.GetCount("person_basic_training", " WHERE person_id = '"+person_id+"'");
            if(cnt == 0){
                List<BasicTraining> list = db.getBasicTrainings("", "");
                for (BasicTraining list1 : list){
                    int id = db.newIntegerId("person_basic_training");
                    String person_basic_training_id = db.newId();
                    db.execQuery("INSERT INTO person_basic_training (id, person_basic_training_id, person_id, basic_training_id, location_name, date_completed, doc_ref_no, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, institution, from_date, to_date) " +
                            "VALUES ("+id+", '"+person_basic_training_id+"', '"+person_id+"', '"+list1.getBasic_training_id()+"', '', '', '', '', '', '', '', '', '', '', '', '')");
                    Log.d("RESULT", "INSERT INTO person_basic_training (id, person_basic_training_id, person_id, basic_training_id, location_name, date_completed, doc_ref_no, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, institution, from_date, to_date) VALUES ("+id+", '"+person_basic_training_id+"', '"+person_id+"', '"+list1.getBasic_training_id()+"', '', '', '', '', '', '', '', '', '', '', '', '')");
                }
            }
            startProgress();
            return null;
        }
        protected void onPostExecute(Void result){
            pd.dismiss();

        }
    }

    public void display(){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);

        TextView textView = new TextView(context);
        textView.setText("Records of Basic Safety Trainings");
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        textView.setGravity(Gravity.LEFT);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(5,15,5,5);
        textView.setTypeface(null, Typeface.BOLD);
        main_container.addView(textView);

        LinearLayout linearLayout =  new LinearLayout(this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        textView = new TextView(context);
        textView.setText("Training Description");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams2);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        textView = new TextView(context);
        textView.setText("Date Completed");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams2);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        textView = new TextView(context);
        textView.setText("Name of Maritime Training Institution (MTI)");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams2);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        textView = new TextView(context);
        textView.setText("");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams2);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);
        main_container.addView(linearLayout);

        List<BasicTraining> list = db.getBasicTrainings("", " training_type = 'Basic Training'");
        for (BasicTraining list1 : list){
            linearLayout =  new LinearLayout(this);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setBackgroundResource(R.drawable.border1dp);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            textView = new TextView(context);
            textView.setText(list1.getDesc_basic_training());
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(18);
            textView.setLayoutParams(layoutParams2);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            String date_completed = db.getFieldString("date_completed", " basic_training_id = '"+list1.getBasic_training_id()+"' AND person_id = '"+person_id+"'", "person_basic_training");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
            try {
                Date date = format.parse(date_completed);
                date_completed = format2.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            textView = new TextView(context);
            textView.setText(date_completed);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(18);
            textView.setLayoutParams(layoutParams2);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            String institution = db.getFieldString("institution", " basic_training_id = '"+list1.getBasic_training_id()+"' AND person_id = '"+person_id+"'", "person_basic_training");
            textView = new TextView(context);
            textView.setText(institution);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(18);
            textView.setLayoutParams(layoutParams2);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            Integer id = db.getFieldInt("id", " basic_training_id = '"+list1.getBasic_training_id()+"' AND person_id = '"+person_id+"'", "person_basic_training");
            textView = new TextView(context);
            textView.setId(id);
            textView.setText("Update");
            textView.setTextColor(getColor(R.color.link));
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(layoutParams2);
            textView.setPadding(5,5,5,5);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String person_basic_training_id = db.getFieldString("person_basic_training_id", " id = "+ v.getId(), "person_basic_training");
                    Intent intent = new Intent(context, PersonBasicTrainingFormActivity.class);
                    intent.putExtra("person_basic_training_id", person_basic_training_id);
                    startActivity(intent);
                    finish();
                }
            });
            linearLayout.addView(textView);
            main_container.addView(linearLayout);
        }

        textView = new TextView(context);
        textView.setText("Record of Other Trainings");
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        textView.setGravity(Gravity.LEFT);
        textView.setLayoutParams(layoutParams);
        textView.setPadding(5,25,5,5);
        textView.setTypeface(null, Typeface.BOLD);
        main_container.addView(textView);

        linearLayout =  new LinearLayout(this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        textView = new TextView(context);
        textView.setText("Training Description");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams2);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        textView = new TextView(context);
        textView.setText("Date Completed");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams2);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        textView = new TextView(context);
        textView.setText("Name of Maritime Training Institution (MTI)");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams2);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        textView = new TextView(context);
        textView.setText("");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams2);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);
        main_container.addView(linearLayout);

        list = db.getBasicTrainings("", " training_type = 'Other Training'");
        for (BasicTraining list1 : list){
            linearLayout =  new LinearLayout(this);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setBackgroundResource(R.drawable.border1dp);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            textView = new TextView(context);
            textView.setText(list1.getDesc_basic_training());
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(18);
            textView.setLayoutParams(layoutParams2);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);
            String date_completed = db.getFieldString("date_completed", " basic_training_id = '"+list1.getBasic_training_id()+"' AND person_id = '"+person_id+"'", "person_basic_training");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
            try {
                Date date = format.parse(date_completed);
                date_completed = format2.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            textView = new TextView(context);
            textView.setText(date_completed);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(18);
            textView.setLayoutParams(layoutParams2);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);
            String institution = db.getFieldString("institution", " basic_training_id = '"+list1.getBasic_training_id()+"' AND person_id = '"+person_id+"'", "person_basic_training");
            textView = new TextView(context);
            textView.setText(institution);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(18);
            textView.setLayoutParams(layoutParams2);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);
            Integer id = db.getFieldInt("id", " basic_training_id = '"+list1.getBasic_training_id()+"' AND person_id = '"+person_id+"'", "person_basic_training");
            textView = new TextView(context);
            textView.setText("Update");
            textView.setTextColor(getColor(R.color.link));
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(layoutParams2);
            textView.setPadding(5,5,5,5);
            textView.setId(id);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String person_basic_training_id = db.getFieldString("person_basic_training_id", " id = "+ v.getId(), "person_basic_training");
                    Intent intent = new Intent(context, PersonBasicTrainingFormActivity.class);
                    intent.putExtra("person_basic_training_id", person_basic_training_id);
                    startActivity(intent);
                    finish();
                }
            });
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
        Intent intent = new Intent(ProfessionalTrainingActivity.this, MainActivity.class);
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
