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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.net.URLDecoder;
import java.util.List;

public class SteeringActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;

    TextView tv_title;
    DatabaseHelper db;
    ImageButton IB_add;
    String person_id, dept;
    ProgressDialog pd;
    LinearLayout main_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steering);

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

        tv_title = findViewById(R.id.tv_title);
        tv_title.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        IB_add = findViewById(R.id.IB_add);
        IB_add.setVisibility(View.GONE);
        main_container = findViewById(R.id.main_container);

        db = new DatabaseHelper(this);
        person_id = db.getCadetId();
        dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");

        if(person_id.equals("")){ //NO DATA
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            viewParam.weight = 1;

            TextView textView = new TextView(SteeringActivity.this);
            textView.setLayoutParams(viewParam);
            textView.setText("Type");
            textView.setBackgroundResource(R.drawable.border_darkblue);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(SteeringActivity.this);
            textView1.setLayoutParams(viewParam);
            textView1.setText("Voyage From");
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(15);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setPadding(5,5,5,5);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(SteeringActivity.this);
            textView2.setLayoutParams(viewParam);
            textView2.setText("Voyage To");
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(15);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setPadding(5,5,5,5);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(SteeringActivity.this);
            textView3.setLayoutParams(viewParam);
            textView3.setText("Date");
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            textView3.setTextColor(Color.WHITE);
            textView3.setTextSize(15);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setPadding(5,5,5,5);
            linearLayout.addView(textView3);

            final TextView textView5 = new TextView(SteeringActivity.this);
            textView5.setLayoutParams(viewParam);
            textView5.setText("Total Hrs.");
            textView5.setBackgroundResource(R.drawable.border_darkblue);
            textView5.setTextColor(Color.WHITE);
            textView5.setTextSize(15);
            textView5.setGravity(Gravity.CENTER_HORIZONTAL);
            textView5.setPadding(5,5,5,5);

            main_container.addView(linearLayout);

            linearLayout.addView(textView5);
            linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            layoutParams.setMargins(5,0,5,5);

            textView = new TextView(this);
            textView.setLayoutParams(viewParam);
            textView.setText("NOT YET ACTIVATED");
            textView.setTextColor(Color.RED);
            textView.setTextSize(15);
            textView.setPadding(5,5,5,5);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setBackgroundResource(R.drawable.border1dp);
            linearLayout.addView(textView);

            main_container.addView(linearLayout);
        }else{
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

            int cnt = db.GetCount("shipboard", "");
            if(cnt > 0){
                IB_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SteeringActivity.this, SteeringFormActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                //GENERATE
                new generate(context).execute();
            }else{


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView textView = new TextView(SteeringActivity.this);
                textView.setText("No Sea Service Record detected. Please make sure you saved your current on board status.");
                textView.setTextColor(Color.RED);
                textView.setTextSize(20);
                textView.setLayoutParams(layoutParams);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                main_container.addView(textView);
            }
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
            pd = new ProgressDialog(SteeringActivity.this);
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

    public void display() {
        //get person steer task
        int cnt2 = db.GetCount("person_steer_task", "");

        if(cnt2 > 0){
            //GET STEER COMPETENCE
            List<SteerCompetence> steerCompetences = db.getSteerCompetence();
            for(SteerCompetence steerCompetence : steerCompetences){
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams2.setMargins(5,10,5,5);

                TextView textView = new TextView(SteeringActivity.this);
                textView.setText(URLDecoder.decode(steerCompetence.getDesc_competence()));
                textView.setBackgroundResource(R.drawable.border_comp);
                textView.setTextColor(Color.WHITE);
                textView.setLayoutParams(layoutParams2);
                textView.setPadding(5,5,5,5);

                main_container.addView(textView);
                //GET STEER TOPICS
                List<SteerTopic> steerTopics = db.getSteerTopic(" steer_competence_id = '"+steerCompetence.getSteer_competence_id()+"'");
                for(SteerTopic steerTopic : steerTopics){
                    LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
                    LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    layoutParams3.setMargins(5,-5,5,5);

                    LinearLayout linearLayout = new LinearLayout(SteeringActivity.this);
                    linearLayout.setLayoutParams(layoutParams3);
                    linearLayout.setBackgroundResource(R.drawable.border_orange);

                    textView = new TextView(SteeringActivity.this);
                    textView.setText(URLDecoder.decode(steerTopic.getDesc_steer_topic()));
                    textView.setTextColor(Color.BLACK);
                    textView.setLayoutParams(layoutParams4);
                    textView.setPadding(5,5,5,5);
                    linearLayout.addView(textView);

                    textView = new TextView(SteeringActivity.this);
                    textView.setText(URLDecoder.decode(steerTopic.getCriteria()));
                    textView.setTextColor(Color.BLACK);
                    textView.setLayoutParams(layoutParams4);
                    textView.setPadding(5,5,5,5);
                    linearLayout.addView(textView);

                    String officer = db.getFieldString("checked_by_id", " person_id = '"+person_id+"' AND steer_topic_id = '"+steerTopic.getSteer_topic_id()+"'", "person_steer_topic");
                    String officer_name = db.getFieldString("full_name", " person_id = '"+officer+"'", "person");
                    textView = new TextView(SteeringActivity.this);
                    textView.setText("STO\n" + officer_name);
                    textView.setTextColor(Color.BLACK);
                    textView.setLayoutParams(layoutParams5);
                    textView.setPadding(5,5,5,5);
                    linearLayout.addView(textView);

                    String date_checked = db.getFieldString("date_checked", " person_id = '"+person_id+"' AND steer_topic_id = '"+steerTopic.getSteer_topic_id()+"'", "person_steer_topic");
                    textView = new TextView(SteeringActivity.this);
                    textView.setText("Date\n" + date_checked);
                    textView.setTextColor(Color.BLACK);
                    textView.setLayoutParams(layoutParams5);
                    textView.setPadding(5,5,5,5);
                    linearLayout.addView(textView);

                    main_container.addView(linearLayout);

                    List<SteerTask> steerTasks = db.getSteerTask(" steer_topic_id = '"+steerTopic.getSteer_topic_id()+"'");
                    for(SteerTask steerTask : steerTasks){
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(5,-5,5,5);

                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4);
                        LinearLayout.LayoutParams layoutParams1_ = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                        LinearLayout linearLayout1 = new LinearLayout(SteeringActivity.this);
                        linearLayout1.setLayoutParams(layoutParams);
                        linearLayout1.setBackgroundResource(R.drawable.border1dp);

                        TextView textView1 = new TextView(SteeringActivity.this);
                        textView1.setText(URLDecoder.decode(URLDecoder.decode(steerTask.getDesc_steer_task())));
                        textView1.setTextSize(15);
                        textView1.setTextColor(Color.BLACK);
                        textView1.setLayoutParams(layoutParams1);
                        textView1.setPadding(15,5,5,5);
                        linearLayout1.addView(textView1);

                        String officer_ = db.getFieldString("checked_by_id", " person_id = '"+person_id+"' AND steer_task_id = '"+steerTask.getSteer_task_id()+"'", "person_steer_task");
                        String officer_name_ = db.getFieldString("full_name", " person_id = '"+officer_+"'", "person");

                        TextView textView2 = new TextView(SteeringActivity.this);
                        textView2.setText(officer_name_);
                        textView2.setTextSize(15);
                        textView2.setTextColor(Color.BLACK);
                        textView2.setLayoutParams(layoutParams1_);
                        linearLayout1.addView(textView2);

                        String date_checked_ = db.getFieldString("date_checked", " person_id = '"+person_id+"' AND steer_task_id = '"+steerTask.getSteer_task_id()+"'", "person_steer_task");
                        TextView textView3 = new TextView(SteeringActivity.this);
                        textView3.setText(date_checked_);
                        textView3.setTextSize(15);
                        textView3.setTextColor(Color.BLACK);
                        textView3.setLayoutParams(layoutParams1_);
                        linearLayout1.addView(textView3);

                        main_container.addView(linearLayout1);
                    }
                }
            }
        }else{
            List<SteerTopic> steerTopics = db.getSteerTopic("");
            for(SteerTopic steerTopic : steerTopics){
                Integer id = db.newIntegerId("person_steer_topic");
                String person_steer_topic_id = db.newId();
                db.execQuery("INSERT INTO person_steer_topic (person_steer_topic_id , id, steer_topic_id, person_id, checked_by_id, date_checked, checked_remarks) VALUES ('" + person_steer_topic_id + "', '" + id + "', '" + steerTopic.getSteer_topic_id() + "', '" + person_id + "', '', '', '')");
                Log.d("QUERY", "INSERT INTO person_steer_topic (person_steer_topic_id , id, steer_topic_id, person_id, checked_by_id, date_checked, checked_remarks) VALUES ('" + person_steer_topic_id + "', '" + id + "', '" + steerTopic.getSteer_topic_id() + "', '" + person_id + "', '', '', '')");
                List<SteerTask> steerTasks = db.getSteerTask(" steer_topic_id = '"+steerTopic.getSteer_topic_id()+"'");
                for(SteerTask steerTask : steerTasks){
                    Integer id_ = db.newIntegerId("person_steer_task");
                    String person_steer_task_id = db.newId();
                    String vessel_id = db.getFieldString("vessel_id", " person_id = '"+person_id+"' ORDER BY sign_on DESC LIMIT 1", "shipboard");
                    db.execQuery("INSERT INTO person_steer_task (person_steer_task_id , id, steer_task_id, person_id, vessel_id, completed, answers, passed, not_app, lat_long, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks) VALUES ('" + person_steer_task_id + "', '" + id_ + "', '" + steerTask.getSteer_task_id() + "', '" + person_id + "', '" + vessel_id + "', '', '', '', '', '', '', '', '', '', '', '')");
                    Log.d("QUERY", "INSERT INTO person_steer_task (person_steer_task_id , id, steer_task_id, person_id, vessel_id, completed, answers, passed, not_app, lat_long, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks) VALUES ('" + person_steer_task_id + "', '" + id_ + "', '" + steerTask.getSteer_task_id() + "', '" + person_id + "', '" + vessel_id + "', '', '', '', '', '', '', '', '', '', '', '')");
                }
            }

            //GET STEER COMPETENCE
            List<SteerCompetence> steerCompetences = db.getSteerCompetence();
            for(SteerCompetence steerCompetence : steerCompetences){
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams2.setMargins(5,10,5,5);

                TextView textView = new TextView(SteeringActivity.this);
                textView.setText(URLDecoder.decode(steerCompetence.getDesc_competence()));
                textView.setBackgroundResource(R.drawable.border_comp);
                textView.setTextColor(Color.WHITE);
                textView.setLayoutParams(layoutParams2);
                textView.setPadding(5,5,5,5);

                main_container.addView(textView);
                //GET STEER TOPICS
                List<SteerTopic> steerTopics_ = db.getSteerTopic(" steer_competence_id = '"+steerCompetence.getSteer_competence_id()+"'");
                for(SteerTopic steerTopic : steerTopics_){
                    LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
                    LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    layoutParams3.setMargins(5,-5,5,5);

                    LinearLayout linearLayout = new LinearLayout(SteeringActivity.this);
                    linearLayout.setLayoutParams(layoutParams3);
                    linearLayout.setBackgroundResource(R.drawable.border_orange);

                    textView = new TextView(SteeringActivity.this);
                    textView.setText(URLDecoder.decode(steerTopic.getDesc_steer_topic()));
                    textView.setTextColor(Color.BLACK);
                    textView.setLayoutParams(layoutParams4);
                    textView.setPadding(5,5,5,5);
                    linearLayout.addView(textView);

                    textView = new TextView(SteeringActivity.this);
                    textView.setText(URLDecoder.decode(steerTopic.getCriteria()));
                    textView.setTextColor(Color.BLACK);
                    textView.setLayoutParams(layoutParams4);
                    textView.setPadding(5,5,5,5);
                    linearLayout.addView(textView);

                    textView = new TextView(SteeringActivity.this);
                    textView.setText("STO");
                    textView.setTextColor(Color.BLACK);
                    textView.setLayoutParams(layoutParams5);
                    textView.setPadding(5,5,5,5);
                    linearLayout.addView(textView);

                    textView = new TextView(SteeringActivity.this);
                    textView.setText("Date");
                    textView.setTextColor(Color.BLACK);
                    textView.setLayoutParams(layoutParams5);
                    textView.setPadding(5,5,5,5);
                    linearLayout.addView(textView);

                    main_container.addView(linearLayout);

                    List<SteerTask> steerTasks = db.getSteerTask(" steer_topic_id = '"+steerTopic.getSteer_topic_id()+"'");
                    for(SteerTask steerTask : steerTasks){
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(5,-5,5,5);

                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4);
                        LinearLayout.LayoutParams layoutParams1_ = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                        LinearLayout linearLayout1 = new LinearLayout(SteeringActivity.this);
                        linearLayout1.setLayoutParams(layoutParams);
                        linearLayout1.setBackgroundResource(R.drawable.border1dp);

                        TextView textView1 = new TextView(SteeringActivity.this);
                        textView1.setText(URLDecoder.decode(URLDecoder.decode(steerTask.getDesc_steer_task())));
                        textView1.setTextSize(15);
                        textView1.setTextColor(Color.BLACK);
                        textView1.setLayoutParams(layoutParams1);
                        textView1.setPadding(15,5,5,5);
                        linearLayout1.addView(textView1);

                        TextView textView2 = new TextView(SteeringActivity.this);
                        textView2.setText("");
                        textView2.setTextSize(15);
                        textView2.setTextColor(Color.BLACK);
                        textView2.setLayoutParams(layoutParams1_);
                        linearLayout1.addView(textView2);

                        TextView textView3 = new TextView(SteeringActivity.this);
                        textView3.setText("");
                        textView3.setTextSize(15);
                        textView3.setTextColor(Color.BLACK);
                        textView3.setLayoutParams(layoutParams1_);
                        linearLayout1.addView(textView3);

                        main_container.addView(linearLayout1);

                    }
                }

            }
        }

        int cnt = db.GetCount("person_steering", "");
        if(cnt > 0){

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,25,5,5);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

            LinearLayout linearLayout = new LinearLayout(SteeringActivity.this);
            linearLayout.setLayoutParams(layoutParams);

            final TextView textView = new TextView(SteeringActivity.this);
            textView.setLayoutParams(layoutParams1);
            textView.setText("Type");
            textView.setBackgroundResource(R.drawable.border_darkblue);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(SteeringActivity.this);
            textView1.setLayoutParams(layoutParams1);
            textView1.setText("Voyage From");
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(15);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setPadding(5,5,5,5);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(SteeringActivity.this);
            textView2.setLayoutParams(layoutParams1);
            textView2.setText("Voyage To");
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(15);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setPadding(5,5,5,5);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(SteeringActivity.this);
            textView3.setLayoutParams(layoutParams1);
            textView3.setText("Date");
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            textView3.setTextColor(Color.WHITE);
            textView3.setTextSize(15);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setPadding(5,5,5,5);
            linearLayout.addView(textView3);

            final TextView textView5 = new TextView(SteeringActivity.this);
            textView5.setLayoutParams(layoutParams1);
            textView5.setText("Total Hrs.");
            textView5.setBackgroundResource(R.drawable.border_darkblue);
            textView5.setTextColor(Color.WHITE);
            textView5.setTextSize(15);
            textView5.setGravity(Gravity.CENTER_HORIZONTAL);
            textView5.setPadding(5,5,5,5);
            linearLayout.addView(textView5);

            final TextView textView4 = new TextView(SteeringActivity.this);
            textView4.setLayoutParams(layoutParams1);
            textView4.setText("");
            textView4.setBackgroundResource(R.drawable.border_darkblue);
            textView4.setTextColor(Color.WHITE);
            textView4.setTextSize(15);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL);
            textView4.setPadding(5,5,5,5);
            linearLayout.addView(textView4);

            textView1.post(new Runnable() {
                @Override
                public void run() {
                    int height = textView1.getLineCount() * textView.getMeasuredHeight();
                    Log.d("HI", "" + height + " - " + textView.getMeasuredHeight());
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();

                    params.height = height;

                    textView.setLayoutParams(params);
                    textView1.setLayoutParams(params);
                    textView2.setLayoutParams(params);
                    textView3.setLayoutParams(params);
                    textView4.setLayoutParams(params);
                    textView5.setLayoutParams(params);
                }
            });

            main_container.addView(linearLayout);

            List<PersonSteering> personSteerings = db.getPersonSteering(person_id);
            for(final PersonSteering personSteering : personSteerings){
                LinearLayout.LayoutParams layoutParams_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams_.setMargins(5,-5,5,5);
                LinearLayout.LayoutParams layoutParams1_ = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                LinearLayout linearLayout_ = new LinearLayout(SteeringActivity.this);
                linearLayout_.setLayoutParams(layoutParams_);
                linearLayout_.setBackgroundResource(R.drawable.border1dp);

                TextView textView_ = new TextView(SteeringActivity.this);
                textView_.setLayoutParams(layoutParams1_);
                textView_.setText(personSteering.getSteering_type());
                textView_.setTextColor(Color.BLACK);
                textView_.setTextSize(15);
                textView_.setGravity(Gravity.LEFT);
                textView_.setPadding(5,5,5,5);
                linearLayout_.addView(textView_);

                TextView textView1_ = new TextView(SteeringActivity.this);
                textView1_.setLayoutParams(layoutParams1_);
                textView1_.setText(personSteering.getVoyage_from());
                textView1_.setTextColor(Color.BLACK);
                textView1_.setTextSize(15);
                textView1_.setGravity(Gravity.LEFT);
                textView1_.setPadding(5,5,5,5);
                linearLayout_.addView(textView1_);

                TextView textView2_ = new TextView(SteeringActivity.this);
                textView2_.setLayoutParams(layoutParams1_);
                textView2_.setText(personSteering.getVoyage_to());
                textView2_.setTextColor(Color.BLACK);
                textView2_.setTextSize(15);
                textView2_.setGravity(Gravity.LEFT);
                textView2_.setPadding(5,5,5,5);
                linearLayout_.addView(textView2_);

                TextView textView3_ = new TextView(SteeringActivity.this);
                textView3_.setLayoutParams(layoutParams1_);
                textView3_.setText(personSteering.getDate_steering());
                textView3_.setTextColor(Color.BLACK);
                textView3_.setTextSize(15);
                textView3_.setGravity(Gravity.LEFT);
                textView3_.setPadding(5,5,5,5);
                linearLayout_.addView(textView3_);

                TextView textView5_ = new TextView(SteeringActivity.this);
                textView5_.setLayoutParams(layoutParams1_);
                textView5_.setText(""+personSteering.getTotal_hrs());
                textView5_.setTextColor(Color.BLACK);
                textView5_.setTextSize(15);
                textView5_.setGravity(Gravity.LEFT);
                textView5_.setPadding(5,5,5,5);
                linearLayout_.addView(textView5_);

                TextView textView4_ = new TextView(SteeringActivity.this);
                textView4_.setLayoutParams(layoutParams1_);
                textView4_.setText("Update");
                textView4_.setTextColor(Color.BLUE);
                textView4_.setTextSize(15);
                textView4_.setGravity(Gravity.CENTER_HORIZONTAL);
                textView4_.setPadding(5,5,5,5);
                linearLayout_.addView(textView4_);

                textView4_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SteeringActivity.this, SteeringFormActivity.class);
                        intent.putExtra("person_steering_id", personSteering.getPerson_steering_id());
                        startActivity(intent);
                        finish();
                    }
                });

                main_container.addView(linearLayout_);
            }
            LinearLayout.LayoutParams layoutParams_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams_.setMargins(5,-5,5,5);

            final TextView tvAdd = new TextView(SteeringActivity.this);
            tvAdd.setLayoutParams(layoutParams_);
            tvAdd.setText("+ ADD NEW RECORD");
            tvAdd.setTypeface(null, Typeface.BOLD);
            tvAdd.setTextColor(Color.BLUE);
            tvAdd.setTextSize(15);
            tvAdd.setGravity(Gravity.LEFT);
            tvAdd.setPadding(10,5,5,5);
            tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SteeringActivity.this, SteeringFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            main_container.addView(tvAdd);

        }else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,25,5,5);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

            LinearLayout linearLayout = new LinearLayout(SteeringActivity.this);
            linearLayout.setLayoutParams(layoutParams);

            final TextView textView = new TextView(SteeringActivity.this);
            textView.setLayoutParams(layoutParams1);
            textView.setText("Type");
            textView.setBackgroundResource(R.drawable.border_darkblue);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(SteeringActivity.this);
            textView1.setLayoutParams(layoutParams1);
            textView1.setText("Voyage From");
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(15);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setPadding(5,5,5,5);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(SteeringActivity.this);
            textView2.setLayoutParams(layoutParams1);
            textView2.setText("Voyage To");
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(15);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setPadding(5,5,5,5);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(SteeringActivity.this);
            textView3.setLayoutParams(layoutParams1);
            textView3.setText("Date");
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            textView3.setTextColor(Color.WHITE);
            textView3.setTextSize(15);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setPadding(5,5,5,5);
            linearLayout.addView(textView3);

            textView1.post(new Runnable() {
                @Override
                public void run() {
                    int height = textView1.getLineCount() * textView.getMeasuredHeight();
                    Log.d("HI", "" + height + " - " + textView.getMeasuredHeight());
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();

                    params.height = height;

                    textView.setLayoutParams(params);
                    textView1.setLayoutParams(params);
                    textView2.setLayoutParams(params);
                    textView3.setLayoutParams(params);
                }
            });

            main_container.addView(linearLayout);

            LinearLayout.LayoutParams layoutParams_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams_.setMargins(5,-5,5,5);

            final TextView textView4 = new TextView(SteeringActivity.this);
            textView4.setLayoutParams(layoutParams_);
            textView4.setText("+ ADD NEW RECORD");
            textView4.setBackgroundResource(R.drawable.border1dp);
            textView4.setTextColor(Color.BLUE);
            textView4.setTextSize(15);
            textView4.setGravity(Gravity.LEFT);
            textView4.setPadding(10,5,5,5);
            textView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SteeringActivity.this, SteeringFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            main_container.addView(textView4);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SteeringActivity.this, RecordWatchActivity.class);
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
