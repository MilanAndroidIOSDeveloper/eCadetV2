package com.elosoftbiz.etrb_trmf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.net.URLDecoder;
import java.util.List;

public class TaskSummaryActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    WebView wv_content;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;
    String dept, person_id;
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_summary);

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
        /****** START MENU *******/
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        new MenuDeck(navigationView, this, mDrawerLayout);
        /****** END MENU *******/

        main_container = findViewById(R.id.main_container);
        wv_content = findViewById(R.id.wv_content);

        tv_title = findViewById(R.id.tv_title);

        int cnt = db.GetCount("person", "");
        if(cnt > 0){ //WITH DATA
            pd = ProgressDialog.show(TaskSummaryActivity.this, "Loading.", "Please wait...", true);
            pd.setCancelable(false);

            person_id = db.getCadetId();
            dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");

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

            new generate(context).execute();
        }else{ //DEMO
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,10,5,5);

            TextView textView =  new TextView(TaskSummaryActivity.this);
            textView.setLayoutParams(layoutParams);
            textView.setText("NOT YET ACTIVATED");
            textView.setTextSize(20);
            textView.setTextColor(Color.RED);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(5,5,5,5);
            main_container.addView(textView);

        }



    }

    public void startProgress() {

        runOnUiThread(new Runnable() {

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

        }

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
        Log.d("RESULT", "DEPT " + dept);
        TextView textView = null;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        List<TrbFunction> trbfunctions = db.getFunctions(dept, "vessel_type_id='' AND dp='' AND ice_class=''");
        for (TrbFunction function : trbfunctions) {
            textView = new TextView(TaskSummaryActivity.this);
            textView.setLayoutParams(layoutParams);
            textView.setText(function.getDesc_trb_function());
            textView.setTextColor(getColor(R.color.white));
            textView.setBackgroundColor(getColor(R.color.black));
            textView.setTextSize(15);
            textView.setPadding(10,5,10,5);

            main_container.addView(textView);

            //GET COMPETENCES
            List<TrbCompetence> trbCompetences = db.getCompetences(function.getTrb_function_id());
            for (TrbCompetence trbCompetence : trbCompetences) {
                textView = new TextView(TaskSummaryActivity.this);
                textView.setLayoutParams(layoutParams);
                textView.setText(trbCompetence.getRef_no() + " - " + URLDecoder.decode(trbCompetence.getDesc_competence()));
                textView.setTextColor(getColor(R.color.white));
                textView.setBackgroundColor(getColor(R.color.black));
                textView.setTextSize(15);
                textView.setPadding(10,5,10,5);

                main_container.addView(textView);
                int cnt = 0;
                int div = 6;
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    div = 10;
                }


                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                LinearLayout linearLayout = null;
                int cnt_task = db.GetCount("task", " WHERE trb_competence_id = '"+trbCompetence.getTrb_competence_id()+"'");
                if(cnt_task > 0){
                    List<Task> tasks = db.getTasks(trbCompetence.getTrb_competence_id(), "");
                    for (final Task task : tasks) {
                        int phase_no = task.getPhase_no();
                        final String task_id = task.getTask_id();
                        if(cnt == 0){ //FIRST TASK
                            linearLayout = new LinearLayout(TaskSummaryActivity.this);
                            linearLayout.setLayoutParams(layoutParams1);

                            TextView textView1 = new TextView(TaskSummaryActivity.this);
                            textView1.setLayoutParams(layoutParams2);
                            textView1.setText(task.getRef_no_task());
                            textView1.setTextSize(13);
                            textView1.setTextColor(Color.parseColor("#FFFFFF"));
                            textView1.setPadding(10, 12, 10, 10);
                            textView1.setBackgroundResource(R.drawable.row_border);
                            //if(phase_no == 1){
                                textView1.setBackgroundResource(R.drawable.border_darkblue);
                            //}else if(phase_no == 2){
                            //    textView1.setBackgroundResource(R.drawable.border_green);
                            //}else if(phase_no == 3){
                            //    textView1.setBackgroundResource(R.drawable.border_blu);
                            //}
                            textView1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    show_task(task_id);
                                }
                            });
                            linearLayout.addView(textView1);
                            cnt++;

                            //CHECK IF ALREADY HAS ANSWER
                            String completed = db.getFieldString("completed", " task_id = '"+task.getTask_id()+"'", "person_task");
                            if(completed.equals("") || completed.equals("0000-00-00")){
                                textView1 = new TextView(TaskSummaryActivity.this);
                                textView1.setLayoutParams(layoutParams2);
                                textView1.setText("");
                                textView1.setTextSize(13);
                                textView1.setTextColor(Color.parseColor("#000000"));
                                textView1.setPadding(10, 12, 10, 10);
                                textView1.setBackgroundResource(R.drawable.row_border);
                                linearLayout.addView(textView1);
                            }else{
                                LinearLayout linearLayout1 = new LinearLayout(TaskSummaryActivity.this);
                                linearLayout1.setLayoutParams(layoutParams2);
                                linearLayout1.setBackgroundResource(R.drawable.row_border);
                                linearLayout1.setGravity(Gravity.CENTER_HORIZONTAL);
                                linearLayout1.setPadding(5,5,5,10);

                                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(40, ViewGroup.LayoutParams.MATCH_PARENT);
                                ImageView imageView = new ImageView(TaskSummaryActivity.this);
                                imageView.setLayoutParams(layoutParams3);
                                imageView.setBackgroundResource(R.drawable.ic_check_black_24dp);
                                //imageView.setPadding(50,50,50,50);

                                linearLayout1.addView(imageView);
                                linearLayout.addView(linearLayout1);
                            }


                            cnt++;

                        }else if(cnt == div){ //LAST TASK
                            main_container.addView(linearLayout);
                            cnt = 0;

                            linearLayout = new LinearLayout(TaskSummaryActivity.this);
                            linearLayout.setLayoutParams(layoutParams1);

                            TextView textView1 = new TextView(TaskSummaryActivity.this);
                            textView1.setLayoutParams(layoutParams2);
                            textView1.setText(task.getRef_no_task());
                            textView1.setTextSize(13);
                            textView1.setTextColor(Color.parseColor("#FFFFFF"));
                            textView1.setPadding(10, 12, 10, 10);
                            textView1.setBackgroundResource(R.drawable.row_border);
                           // if(phase_no == 1){
                                textView1.setBackgroundResource(R.drawable.border_darkblue);
                            //}else if(phase_no == 2){
                            //    textView1.setBackgroundResource(R.drawable.border_green);
                            //}else if(phase_no == 3){
                            //    textView1.setBackgroundResource(R.drawable.border_blu);
                            //}
                            textView1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    show_task(task_id);
                                }
                            });
                            linearLayout.addView(textView1);
                            cnt++;
                            //CHECK IF ALREADY HAS ANSWER
                            String completed = db.getFieldString("completed", " task_id = '"+task.getTask_id()+"'", "person_task");
                            if(completed.equals("") || completed.equals("0000-00-00")){
                                textView1 = new TextView(TaskSummaryActivity.this);
                                textView1.setLayoutParams(layoutParams2);
                                textView1.setText("");
                                textView1.setTextSize(13);
                                textView1.setTextColor(Color.parseColor("#000000"));
                                textView1.setPadding(10, 12, 10, 10);
                                textView1.setBackgroundResource(R.drawable.row_border);
                                linearLayout.addView(textView1);
                            }else{
                                LinearLayout linearLayout1 = new LinearLayout(TaskSummaryActivity.this);
                                linearLayout1.setLayoutParams(layoutParams2);
                                linearLayout1.setBackgroundResource(R.drawable.row_border);
                                linearLayout1.setGravity(Gravity.CENTER_HORIZONTAL);
                                linearLayout1.setPadding(5,5,5,10);

                                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(40, ViewGroup.LayoutParams.MATCH_PARENT);
                                ImageView imageView = new ImageView(TaskSummaryActivity.this);
                                imageView.setLayoutParams(layoutParams3);
                                imageView.setBackgroundResource(R.drawable.ic_check_black_24dp);
                                //imageView.setPadding(50,50,50,50);

                                linearLayout1.addView(imageView);
                                linearLayout.addView(linearLayout1);
                            }

                            cnt++;
                        }else{ //WITHIN DIV
                            TextView textView1 = new TextView(TaskSummaryActivity.this);
                            textView1.setLayoutParams(layoutParams2);
                            textView1.setText(task.getRef_no_task());
                            textView1.setTextSize(13);
                            textView1.setTextColor(Color.parseColor("#FFFFFF"));
                            textView1.setPadding(10, 12, 10, 10);
                            textView1.setBackgroundResource(R.drawable.row_border);
                            //if(phase_no == 1){
                                textView1.setBackgroundResource(R.drawable.border_darkblue);
                            //}else if(phase_no == 2){
                            //    textView1.setBackgroundResource(R.drawable.border_green);
                            //}else if(phase_no == 3){
                             //   textView1.setBackgroundResource(R.drawable.border_blu);
                            //}
                            textView1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    show_task(task_id);
                                }
                            });
                            linearLayout.addView(textView1);
                            cnt++;
                            //CHECK IF ALREADY HAS ANSWER
                            String completed = db.getFieldString("completed", " task_id = '"+task.getTask_id()+"'", "person_task");
                            if(completed.equals("") || completed.equals("0000-00-00")){
                                textView1 = new TextView(TaskSummaryActivity.this);
                                textView1.setLayoutParams(layoutParams2);
                                textView1.setText("");
                                textView1.setTextSize(13);
                                textView1.setTextColor(Color.parseColor("#000000"));
                                textView1.setPadding(10, 12, 10, 10);
                                textView1.setBackgroundResource(R.drawable.row_border);
                                linearLayout.addView(textView1);
                            }else{
                                LinearLayout linearLayout1 = new LinearLayout(TaskSummaryActivity.this);
                                linearLayout1.setLayoutParams(layoutParams2);
                                linearLayout1.setBackgroundResource(R.drawable.row_border);
                                linearLayout1.setGravity(Gravity.CENTER_HORIZONTAL);
                                linearLayout1.setPadding(5,5,5,10);

                                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(40, ViewGroup.LayoutParams.MATCH_PARENT);
                                ImageView imageView = new ImageView(TaskSummaryActivity.this);
                                imageView.setLayoutParams(layoutParams3);
                                imageView.setBackgroundResource(R.drawable.ic_check_black_24dp);
                                //imageView.setPadding(50,50,50,50);

                                linearLayout1.addView(imageView);
                                linearLayout.addView(linearLayout1);
                            }

                            cnt++;
                        }
                        //cnt++;
                    }

                    Log.d("RES", "" + cnt);
                    if(cnt < div){
                        //linearLayout = new LinearLayout(TaskSummaryActivity.this);
                        //linearLayout.setLayoutParams(layoutParams1);

                        while(cnt < div){
                            Log.d("RES", "" + cnt);
                            TextView textView1 = new TextView(TaskSummaryActivity.this);
                            textView1.setLayoutParams(layoutParams2);
                            textView1.setText("");
                            textView1.setTextSize(13);
                            textView1.setTextColor(Color.parseColor("#000000"));
                            textView1.setPadding(10, 12, 10, 10);
                            textView1.setBackgroundResource(R.drawable.row_border);
                            linearLayout.addView(textView1);
                            cnt++;
                        }
                        main_container.addView(linearLayout);
                    }else{
                        main_container.addView(linearLayout);
                    }
                }

            }
        }

        //GET FUNCTIONS BASED ON VESSEL TYPE
        String person_id = db.getCadetId();
        String vessel_id = db.getFieldString("vessel_id","person_id ='"+person_id+"' ORDER BY sign_on DESC LIMIT 1","shipboard");
        String vessel_type_id = db.getFieldString("vessel_type_id", "vessel_id = '"+vessel_id+"'", "vessel");
        if(!vessel_id.equals("")){
            trbfunctions = db.getFunctions(dept, "vessel_type_id='"+vessel_type_id+"' AND dp='' AND ice_class=''");
            for (TrbFunction function : trbfunctions) {
                textView = new TextView(TaskSummaryActivity.this);
                textView.setLayoutParams(layoutParams);
                textView.setText(function.getDesc_trb_function());
                textView.setTextColor(getColor(R.color.white));
                textView.setBackgroundColor(getColor(R.color.black));
                textView.setTextSize(15);
                textView.setPadding(10,5,10,5);

                main_container.addView(textView);

                //GET COMPETENCES
                List<TrbCompetence> trbCompetences = db.getCompetences(function.getTrb_function_id());
                for (TrbCompetence trbCompetence : trbCompetences) {
                    textView = new TextView(TaskSummaryActivity.this);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(trbCompetence.getRef_no() + " - " + URLDecoder.decode(trbCompetence.getDesc_competence()));
                    textView.setTextColor(getColor(R.color.white));
                    textView.setBackgroundColor(getColor(R.color.black));
                    textView.setTextSize(15);
                    textView.setPadding(10, 5, 10, 5);

                    main_container.addView(textView);
                    int cnt = 0;
                    int div = 6;
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        div = 10;
                    }


                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                    LinearLayout linearLayout = null;
                    int cnt_task = db.GetCount("task", " WHERE trb_competence_id = '" + trbCompetence.getTrb_competence_id() + "'");
                    if (cnt_task > 0) {
                        List<Task> tasks = db.getTasks(trbCompetence.getTrb_competence_id(), "");
                        for (Task task : tasks) {
                            int phase_no = task.getPhase_no();
                            final String task_id = task.getTask_id();
                            if (cnt == 0) {
                                linearLayout = new LinearLayout(TaskSummaryActivity.this);
                                linearLayout.setLayoutParams(layoutParams1);

                                TextView textView1 = new TextView(TaskSummaryActivity.this);
                                textView1.setLayoutParams(layoutParams2);
                                textView1.setText(task.getRef_no_task());
                                textView1.setTextSize(13);
                                textView1.setTextColor(Color.parseColor("#FFFFFF"));
                                textView1.setPadding(10, 12, 10, 10);
                                textView1.setBackgroundResource(R.drawable.row_border);
                                //if (phase_no == 1) {
                                    textView1.setBackgroundResource(R.drawable.border_darkblue);
                                //} else if (phase_no == 2) {
                                //    textView1.setBackgroundResource(R.drawable.border_green);
                                //} else if (phase_no == 3) {
                                //    textView1.setBackgroundResource(R.drawable.border_blu);
                                //}
                                linearLayout.addView(textView1);

                                textView1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("RES", "" + task_id);
                                        Toast.makeText(TaskSummaryActivity.this, "" + task_id, Toast.LENGTH_LONG).show();
                                        show_task(task_id);
                                    }
                                });
                                cnt++;
                                TextView textView2 = new TextView(TaskSummaryActivity.this);
                                textView2.setLayoutParams(layoutParams2);
                                textView2.setText("");
                                textView2.setTextSize(13);
                                textView2.setTextColor(Color.parseColor("#000000"));
                                textView2.setPadding(10, 12, 10, 10);
                                textView2.setBackgroundResource(R.drawable.row_border);
                                linearLayout.addView(textView2);

                                cnt++;

                            } else if (cnt == div) {
                                main_container.addView(linearLayout);
                                cnt = 0;

                                linearLayout = new LinearLayout(TaskSummaryActivity.this);
                                linearLayout.setLayoutParams(layoutParams1);

                                TextView textView1 = new TextView(TaskSummaryActivity.this);
                                textView1.setLayoutParams(layoutParams2);
                                textView1.setText(task.getRef_no_task());
                                textView1.setTextSize(13);
                                textView1.setTextColor(Color.parseColor("#FFFFFF"));
                                textView1.setPadding(10, 12, 10, 10);
                                textView1.setBackgroundResource(R.drawable.row_border);
                                //if (phase_no == 1) {
                                    textView1.setBackgroundResource(R.drawable.border_darkblue);
                                //} else if (phase_no == 2) {
                                 //   textView1.setBackgroundResource(R.drawable.border_green);
                               // } else if (phase_no == 3) {
                                //    textView1.setBackgroundResource(R.drawable.border_blu);
                               // }
                                textView1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        show_task(task_id);
                                    }
                                });
                                linearLayout.addView(textView1);
                                cnt++;
                                TextView textView2 = new TextView(TaskSummaryActivity.this);
                                textView2.setLayoutParams(layoutParams2);
                                textView2.setText("");
                                textView2.setTextSize(13);
                                textView2.setTextColor(Color.parseColor("#000000"));
                                textView2.setPadding(10, 12, 10, 10);
                                textView2.setBackgroundResource(R.drawable.row_border);
                                linearLayout.addView(textView2);

                                cnt++;
                            } else {
                                TextView textView1 = new TextView(TaskSummaryActivity.this);
                                textView1.setLayoutParams(layoutParams2);
                                textView1.setText(task.getRef_no_task());
                                textView1.setTextSize(13);
                                textView1.setTextColor(Color.parseColor("#FFFFFF"));
                                textView1.setPadding(10, 12, 10, 10);
                                textView1.setBackgroundResource(R.drawable.border_darkblue);
                                //if (phase_no == 1) {
                                 //   textView1.setBackgroundResource(R.drawable.border_yellow);
                                //} else if (phase_no == 2) {
                                //    textView1.setBackgroundResource(R.drawable.border_green);
                                //} else if (phase_no == 3) {
                                 //   textView1.setBackgroundResource(R.drawable.border_blu);
                                //}
                                textView1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        show_task(task_id);
                                    }
                                });
                                linearLayout.addView(textView1);
                                cnt++;
                                TextView textView2 = new TextView(TaskSummaryActivity.this);
                                textView2.setLayoutParams(layoutParams2);
                                textView2.setText("");
                                textView2.setTextSize(13);
                                textView2.setTextColor(Color.parseColor("#000000"));
                                textView2.setPadding(10, 12, 10, 10);
                                textView2.setBackgroundResource(R.drawable.row_border);
                                linearLayout.addView(textView2);

                                cnt++;
                            }
                            //cnt++;
                        }
                        Log.d("RES", "" + cnt);
                        if (cnt < div) {
                            while (cnt < div) {
                                TextView textView1 = new TextView(TaskSummaryActivity.this);
                                textView1.setLayoutParams(layoutParams2);
                                textView1.setText("");
                                textView1.setTextSize(13);
                                textView1.setTextColor(Color.parseColor("#000000"));
                                textView1.setPadding(10, 12, 10, 10);
                                textView1.setBackgroundResource(R.drawable.row_border);
                                linearLayout.addView(textView1);
                                cnt++;
                            }
                            main_container.addView(linearLayout);
                        } else {
                            main_container.addView(linearLayout);
                        }
                    }
                }
            }
            String dp = db.getFieldString("dp","vessel_id = '"+vessel_id+"'","vessel");
            if(dp.equals("Y")){
                trbfunctions = db.getFunctions(dept, "dp='Y'");
                for (TrbFunction function : trbfunctions) {
                    textView = new TextView(TaskSummaryActivity.this);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(function.getDesc_trb_function());
                    textView.setTextColor(getColor(R.color.white));
                    textView.setBackgroundColor(getColor(R.color.black));
                    textView.setTextSize(15);
                    textView.setPadding(10,5,10,5);

                    main_container.addView(textView);

                    //GET COMPETENCES
                    List<TrbCompetence> trbCompetences = db.getCompetences(function.getTrb_function_id());
                    for (TrbCompetence trbCompetence : trbCompetences) {
                        textView = new TextView(TaskSummaryActivity.this);
                        textView.setLayoutParams(layoutParams);
                        textView.setText(trbCompetence.getRef_no() + " - " + URLDecoder.decode(trbCompetence.getDesc_competence()));
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setTextSize(15);
                        textView.setPadding(10,5,10,5);

                        main_container.addView(textView);
                        int cnt = 0;
                        int div = 6;
                        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                            div = 10;
                        }

                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                        LinearLayout linearLayout = null;

                        List<Task> tasks = db.getTasks(trbCompetence.getTrb_competence_id(), "");
                        int cnt_task = db.GetCount("task", " WHERE trb_competence_id = '" + trbCompetence.getTrb_competence_id() + "'");
                        if (cnt_task > 0) {
                            for (Task task : tasks) {
                                final String task_id = task.getTask_id();
                                int phase_no = task.getPhase_no();
                                if(cnt == 0){
                                    linearLayout = new LinearLayout(TaskSummaryActivity.this);
                                    linearLayout.setLayoutParams(layoutParams1);

                                    TextView textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText(task.getRef_no_task());
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#FFFFFF"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.border_dark_gray);
                                    //if(phase_no == 1){
                                    //    textView1.setBackgroundResource(R.drawable.border_yellow);
                                    //}else if(phase_no == 2){
                                    //    textView1.setBackgroundResource(R.drawable.border_green);
                                   // }else if(phase_no == 3){
                                    //    textView1.setBackgroundResource(R.drawable.border_blu);
                                   // }
                                    textView1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show_task(task_id);
                                        }
                                    });
                                    linearLayout.addView(textView1);
                                    cnt++;
                                    textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText("");
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#000000"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.row_border);
                                    linearLayout.addView(textView1);

                                    cnt++;

                                }else if(cnt == div){
                                    main_container.addView(linearLayout);
                                    cnt = 0;

                                    linearLayout = new LinearLayout(TaskSummaryActivity.this);
                                    linearLayout.setLayoutParams(layoutParams1);

                                    TextView textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText(task.getRef_no_task());
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#FFFFFF"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.row_border);
                                    ///if(phase_no == 1){
                                        textView1.setBackgroundResource(R.drawable.border_dark_gray);
                                    //}else if(phase_no == 2){
                                    //    textView1.setBackgroundResource(R.drawable.border_green);
                                    //}else if(phase_no == 3){
                                    //    textView1.setBackgroundResource(R.drawable.border_blu);
                                    //}
                                    textView1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show_task(task_id);
                                        }
                                    });
                                    linearLayout.addView(textView1);
                                    cnt++;
                                    textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText("");
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#000000"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.row_border);
                                    linearLayout.addView(textView1);

                                    cnt++;
                                }else{
                                    TextView textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText(task.getRef_no_task());
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#FFFFFF"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.row_border);
                                    //if(phase_no == 1){
                                        textView1.setBackgroundResource(R.drawable.border_dark_gray);
                                    //}else if(phase_no == 2){
                                    //    textView1.setBackgroundResource(R.drawable.border_green);
                                    //}else if(phase_no == 3){
                                    //    textView1.setBackgroundResource(R.drawable.border_blu);
                                    //}
                                    textView1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show_task(task_id);
                                        }
                                    });
                                    linearLayout.addView(textView1);
                                    cnt++;
                                    textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText("");
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#000000"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.row_border);
                                    linearLayout.addView(textView1);

                                    cnt++;
                                }
                                //cnt++;
                            }
                            Log.d("RES", "" + cnt);
                            if(cnt < div){
                                while(cnt < div){
                                    TextView textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText("");
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#000000"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.row_border);
                                    linearLayout.addView(textView1);
                                    cnt++;
                                }
                                main_container.addView(linearLayout);
                            }else{
                                main_container.addView(linearLayout);
                            }
                        }
                    }
                }
            }
            String ice_class = db.getFieldString("ice_class","vessel_id = '"+vessel_id+"'","vessel");
            if(ice_class.equals("Y")){
                trbfunctions = db.getFunctions(dept, "ice_class='Y'");
                for (TrbFunction function : trbfunctions) {
                    textView = new TextView(TaskSummaryActivity.this);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(function.getDesc_trb_function());
                    textView.setTextColor(Color.parseColor("#000000"));
                    textView.setTextSize(15);
                    textView.setPadding(10,5,10,5);

                    main_container.addView(textView);

                    //GET COMPETENCES
                    List<TrbCompetence> trbCompetences = db.getCompetences(function.getTrb_function_id());
                    for (TrbCompetence trbCompetence : trbCompetences) {
                        textView = new TextView(TaskSummaryActivity.this);
                        textView.setLayoutParams(layoutParams);
                        textView.setText(trbCompetence.getRef_no() + " - " + trbCompetence.getDesc_competence());
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setTextSize(15);
                        textView.setPadding(10,5,10,5);

                        main_container.addView(textView);
                        int cnt = 0;
                        int div = 6;
                        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                            div = 10;
                        }


                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                        LinearLayout linearLayout = null;

                        List<Task> tasks = db.getTasks(trbCompetence.getTrb_competence_id(), "");
                        int cnt_task = db.GetCount("task", " WHERE trb_competence_id = '" + trbCompetence.getTrb_competence_id() + "'");
                        if(cnt_task > 0){
                            for (Task task : tasks) {
                                final  String task_id = task.getTask_id();
                                int phase_no = task.getPhase_no();
                                if(cnt == 0){
                                    linearLayout = new LinearLayout(TaskSummaryActivity.this);
                                    linearLayout.setLayoutParams(layoutParams1);

                                    TextView textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText(task.getRef_no_task());
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#FFFFFF"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.row_border);
                                    //if(phase_no == 1){
                                        textView1.setBackgroundResource(R.drawable.border_dark_gray);
                                    //}else if(phase_no == 2){
                                    //    textView1.setBackgroundResource(R.drawable.border_green);
                                    //}else if(phase_no == 3){
                                     //   textView1.setBackgroundResource(R.drawable.border_blu);
                                    //}
                                    textView1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show_task(task_id);
                                        }
                                    });
                                    linearLayout.addView(textView1);
                                    cnt++;
                                    textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText("");
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#000000"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.row_border);
                                    linearLayout.addView(textView1);

                                    cnt++;

                                }else if(cnt == div){
                                    main_container.addView(linearLayout);
                                    cnt = 0;

                                    linearLayout = new LinearLayout(TaskSummaryActivity.this);
                                    linearLayout.setLayoutParams(layoutParams1);

                                    TextView textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText(task.getRef_no_task());
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#FFFFFF"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.border_dark_gray);
                                    //if(phase_no == 1){
                                   //     textView1.setBackgroundResource(R.drawable.border_dark_gray);
                                    //}else if(phase_no == 2){
                                     //   textView1.setBackgroundResource(R.drawable.border_green);
                                    //}else if(phase_no == 3){
                                     //   textView1.setBackgroundResource(R.drawable.border_blu);
                                    //}
                                    textView1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show_task(task_id);
                                        }
                                    });
                                    linearLayout.addView(textView1);
                                    cnt++;
                                    textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText("");
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#000000"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.row_border);
                                    linearLayout.addView(textView1);

                                    cnt++;
                                }else{
                                    TextView textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText(task.getRef_no_task());
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#FFFFFF"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.border_dark_gray);
                                    //if(phase_no == 1){
                                    //    textView1.setBackgroundResource(R.drawable.border_yellow);
                                    //}else if(phase_no == 2){
                                     //   textView1.setBackgroundResource(R.drawable.border_green);
                                    //}else if(phase_no == 3){
                                     //   textView1.setBackgroundResource(R.drawable.border_blu);
                                    //}
                                    textView1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            show_task(task_id);
                                        }
                                    });
                                    linearLayout.addView(textView1);
                                    cnt++;
                                    textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText("");
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#000000"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.row_border);
                                    linearLayout.addView(textView1);

                                    cnt++;
                                }
                                //cnt++;
                            }
                            Log.d("RES", "" + cnt);
                            if(cnt < div){
                                while(cnt < div){
                                    TextView textView1 = new TextView(TaskSummaryActivity.this);
                                    textView1.setLayoutParams(layoutParams2);
                                    textView1.setText("");
                                    textView1.setTextSize(13);
                                    textView1.setTextColor(Color.parseColor("#000000"));
                                    textView1.setPadding(10, 12, 10, 10);
                                    textView1.setBackgroundResource(R.drawable.row_border);
                                    linearLayout.addView(textView1);
                                    cnt++;
                                }
                                main_container.addView(linearLayout);
                            }else{
                                main_container.addView(linearLayout);
                            }
                        }
                    }
                }
            }
        }
        /*String text = "";
        text += "<html><body>";
        text += "<p align=\"justify\">The purpose of this summary record is to provide a continuous check on the status of the tasks. The color code for the task is: <br/>" +
                "Phase 1: Yellow <br/>" +
                "Phase 2: Green <br/>" +
                "Phase 3: Blue <br/>" +
                "There is no colour coding for specialized ships as the tasks on such ships are not segregated into phases.</p>";
        text += "</body></html>";
        wv_content.loadData(text, "text/html", "utf-8");

*/
        return;

    }

    public void show_task(String task_id){

        String desc_task = db.getFieldString("desc_task","task_id='"+task_id+"'","task");
        String ref_no = db.getFieldString("ref_no_task","task_id='"+task_id+"'","task");
        String criteria = db.getFieldString("criteria","task_id='"+task_id+"'","task");
        criteria = URLDecoder.decode(criteria);

        String person_id = db.getCadetId();
        String completed = db.getFieldString("completed", " task_id = '"+task_id+"' AND person_id = '"+person_id+"'", "person_task");
        String checked_by_id = db.getFieldString("checked_by_id", " task_id = '"+task_id+"' AND person_id = '"+person_id+"'", "person_task");
        String sto = db.getFieldString("full_name", "person_id = '"+checked_by_id+"'","person");
        String date_checked = db.getFieldString("date_checked", " task_id = '"+task_id+"' AND person_id = '"+person_id+"'", "person_task");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage(ref_no + " " + URLDecoder.decode(URLDecoder.decode(desc_task)) + "\n\n" +
                "Criteria : \n" + criteria + "\n\n" +
                "Date Completed : " + completed + "\n" +
                "STO : " + sto + "\n" +
                "Date Signed : " + date_checked);
        alertDialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.black));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( pd!=null && pd.isShowing() ){
            pd.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TaskSummaryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
