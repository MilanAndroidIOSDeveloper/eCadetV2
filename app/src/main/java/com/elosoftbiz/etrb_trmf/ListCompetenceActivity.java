package com.elosoftbiz.etrb_trmf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

public class ListCompetenceActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title, tv_guide;

    String demo = "Y", task_type;
    ProgressDialog pd;
    DatabaseHelper db;
    String dept, person_id;
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_competence);

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


        Intent intent = getIntent();
        task_type = intent.getStringExtra("task_type");

        db = new DatabaseHelper(this);
        int cnt = db.GetCount("person"," WHERE vessel_officer='N'");
        if(cnt > 0){
            demo = "N";
            person_id = db.getFieldString("person_id","vessel_officer='N'","person");
            dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");

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



            pd = new ProgressDialog(ListCompetenceActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("Loading. Please wait .... ");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();

            container = findViewById(R.id.container);

            new generate(context).execute();
        }else{
            tv_title = findViewById(R.id.tv_title);
            tv_title.setText("Your app is not yet activated. Please download your data to use the eTRB's full features.");
            tv_title.setTextSize(30);
            tv_title.setTextColor(Color.RED);
            tv_title.setGravity(Gravity.CENTER_HORIZONTAL);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void display(){
        int cnt = db.GetCount("person_task", "");

        if(cnt == 0){
            Log.d( "RESULT", cnt + " xxxx");
            int tcnt = 0;
            String dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");
            List<TrbFunction> trbFunctions = db.getFunctions(dept, "1=1");
            for(TrbFunction trbFunction : trbFunctions){
                String trb_function_id = trbFunction.getTrb_function_id();
                List<TrbCompetence> trbCompetences = db.getCompetences(trb_function_id);
                for(TrbCompetence trbCompetence : trbCompetences){
                    String trb_competence_id = trbCompetence.getTrb_competence_id();

                    List<TrbTopic> trbTopics = db.getTopics(trb_competence_id);
                    for(TrbTopic trbTopic : trbTopics){
                        String trb_topic_id = trbTopic.getTrb_topic_id();
                        int cnt4 = db.GetCount("person_trb_topic", " WHERE trb_topic_id = '"+trb_topic_id+"' AND person_id = '"+person_id+"'");
                        Log.d("RESULT", "TRBF1" + cnt4);
                        if(cnt4 == 0){ //ADD
                            Integer int_id = db.newIntegerId("person_trb_topic");
                            String person_trb_topic_id = db.newId();
                            db.execQuery("INSERT INTO person_trb_topic (id, person_trb_topic_id, trb_topic_id, person_id, checked_by_id, date_checked, checked_remarks) VALUES ("+int_id+", '"+person_trb_topic_id+"', '"+trb_topic_id+"', '"+person_id+"', '', '','')");
                            //SyncOnline(context, person_trb_sub_competence_id);
                            Integer backup_item_id = db.newIntegerId("backup_item");
                            //db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_trb_sub_competence', '" + person_trb_sub_competence_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                            Log.d("QUERY", "INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_trb_sub_competence', '" + person_trb_topic_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                            List<Task> tasks = db.getTasks(trb_competence_id, trb_topic_id);
                            for(Task task : tasks){
                                Integer int_id2 = db.newIntegerId("person_task");
                                String person_task_id = db.newId();
                                String task_id = task.getTask_id();
                                int cnt2 = db.GetCount("person_task", " WHERE task_id = '"+task_id+"' AND person_id = '"+person_id+"'");
                                if(cnt2 == 0){
                                    db.query("INSERT INTO person_task (id, person_task_id, task_id, person_id, completed, answers, passed, img_file, not_app, lat_long, vessel_type_id, checked_by_id, app_by_id, date_checked, date_app, officer_remarks, app_remarks, vessel_id) VALUES ("+int_id2+", '"+person_task_id+"', '"+task_id+"', '"+person_id+"', '', '', '', '', '', '', '', '', '', '', '', '', '', '')");
                                }

                                backup_item_id = db.newIntegerId("backup_item");
                                //db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_task', '" + person_task_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                                //Log.d("QUERY", "INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_task', '" + person_task_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                                tcnt++;
                            }
                        }else { //CHECK PERSON_TASK
                            List<Task> tasks = db.getTasks(trb_competence_id, trb_topic_id);
                            for(Task task : tasks){
                                Integer int_id2 = db.newIntegerId("person_task");
                                String person_task_id = db.newId();
                                String task_id = task.getTask_id();
                                int cnt2 = db.GetCount("person_task", " WHERE task_id = '"+task_id+"' AND person_id = '"+person_id+"'");
                                if(cnt2 == 0){
                                    db.query("INSERT INTO person_task (id, person_task_id, task_id, person_id, completed, answers, passed, img_file, not_app, lat_long, vessel_type_id, checked_by_id, app_by_id, date_checked, date_app, officer_remarks, app_remarks, vessel_id) VALUES ("+int_id2+", '"+person_task_id+"', '"+task_id+"', '"+person_id+"', '', '', '', '', '', '', '', '', '', '', '', '', '', '')");
                                }
                                tcnt++;
                            }//END OF TASKS
                        }
                    }
                }
            }
        }
        TextView textView = null;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 25,10,5);

        Button button = null;

        List<TrbFunction> trbfunctions = db.getFunctions(dept, "vessel_type_id = '' AND dp='' AND ice_class=''");
        for (TrbFunction function : trbfunctions) {
            textView = new TextView(ListCompetenceActivity.this);
            textView.setLayoutParams(layoutParams);
            textView.setText(function.getDesc_trb_function());
            textView.setTextColor(Color.parseColor("#000000"));
            textView.setTextSize(18);
            textView.setPadding(5, 5, 5, 5);
            Log.d("RES", function.getDesc_trb_function());
            container.addView(textView);

            //GET COMPETENCES

            List<TrbCompetence> trbCompetences = db.getCompetences(function.getTrb_function_id());
            for (TrbCompetence trbCompetence : trbCompetences) {

                button = new Button(ListCompetenceActivity.this);
                button.setText(trbCompetence.getRef_no() + " - " + URLDecoder.decode(trbCompetence.getDesc_competence()));
                button.setBackgroundResource(R.drawable.button_comp);
                button.setTextColor(Color.WHITE);
                button.setId(trbCompetence.getId());

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd = new ProgressDialog(ListCompetenceActivity.this);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage("Loading. Please wait .... ");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();

                        Intent intent = new Intent(ListCompetenceActivity.this, PersonTaskActivity.class);
                        String id = db.getFieldString("trb_competence_id","id ='"+v.getId()+"'","trb_competence");
                        intent.putExtra("id", id);
                        intent.putExtra("task_type", task_type);
                        startActivity(intent);
                        finish();
                    }
                });

                container.addView(button);
            }
        }
        String vessel_id = db.getFieldString("vessel_id","person_id = '"+person_id+"' ORDER BY sign_on DESC LIMIT 1","shipboard");
        String vessel_type_id = db.getFieldString("vessel_type_id","vessel_id = '"+vessel_id+"'", "vessel");
        if(!vessel_id.equals("")){
            //GET FUNCTIONS WITH VESSEL TYPE
            trbfunctions = db.getFunctions(dept, "vessel_type_id = '"+vessel_type_id+"' AND dp='' AND ice_class=''");
            for (TrbFunction function : trbfunctions) {
                textView = new TextView(ListCompetenceActivity.this);
                textView.setLayoutParams(layoutParams);
                textView.setText(function.getDesc_trb_function());
                textView.setTextColor(Color.parseColor("#000000"));
                textView.setTextSize(18);
                textView.setPadding(5, 5, 5, 5);
                Log.d("RES", function.getDesc_trb_function());
                container.addView(textView);

                //GET COMPETENCES

                List<TrbCompetence> trbCompetences = db.getCompetences(function.getTrb_function_id());
                for (TrbCompetence trbCompetence : trbCompetences) {

                    button = new Button(ListCompetenceActivity.this);
                    button.setText(trbCompetence.getRef_no() + " - " + URLDecoder.decode(trbCompetence.getDesc_competence()));
                    button.setBackgroundResource(R.drawable.button_comp);
                    button.setTextColor(Color.WHITE);
                    button.setId(trbCompetence.getId());

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ListCompetenceActivity.this, PersonTaskActivity.class);
                            String id = db.getFieldString("trb_competence_id","id ='"+v.getId()+"'","trb_competence");
                            intent.putExtra("id", id);
                            startActivity(intent);
                            finish();
                        }
                    });

                    container.addView(button);
                }
            }

            String dp = db.getFieldString("dp","vessel_id = '"+vessel_id+"'", "vessel");
            if(dp.equals("Y")){
                //GET FUNCTIONS WITH DYNAMIC POSITIONING
                trbfunctions = db.getFunctions(dept, "vessel_type_id = '' AND dp='Y'");
                for (TrbFunction function : trbfunctions) {
                    textView = new TextView(ListCompetenceActivity.this);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(function.getDesc_trb_function());
                    textView.setTextColor(Color.parseColor("#000000"));
                    textView.setTextSize(18);
                    textView.setPadding(5, 5, 5, 5);
                    Log.d("RES", function.getDesc_trb_function());
                    container.addView(textView);

                    //GET COMPETENCES

                    List<TrbCompetence> trbCompetences = db.getCompetences(function.getTrb_function_id());
                    for (TrbCompetence trbCompetence : trbCompetences) {

                        button = new Button(ListCompetenceActivity.this);
                        button.setText(trbCompetence.getRef_no() + " - " + URLDecoder.decode(trbCompetence.getDesc_competence()));
                        button.setBackgroundResource(R.drawable.button_comp);
                        button.setTextColor(Color.WHITE);
                        button.setId(trbCompetence.getId());

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ListCompetenceActivity.this, PersonTaskActivity.class);
                                String id = db.getFieldString("trb_competence_id","id ='"+v.getId()+"'","trb_competence");
                                intent.putExtra("id", id);
                                startActivity(intent);
                                finish();
                            }
                        });

                        container.addView(button);
                    }
                }
            }

            String ice_class = db.getFieldString("ice_class","vessel_id = '"+vessel_id+"'", "vessel");
            if(ice_class.equals("Y")){
                //GET FUNCTIONS WITH DYNAMIC POSITIONING
                trbfunctions = db.getFunctions(dept, "vessel_type_id = '' AND ice_class='Y'");
                for (TrbFunction function : trbfunctions) {
                    textView = new TextView(ListCompetenceActivity.this);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(function.getDesc_trb_function());
                    textView.setTextColor(Color.parseColor("#000000"));
                    textView.setTextSize(18);
                    textView.setPadding(5, 5, 5, 5);
                    Log.d("RES", function.getDesc_trb_function());
                    container.addView(textView);

                    //GET COMPETENCES

                    List<TrbCompetence> trbCompetences = db.getCompetences(function.getTrb_function_id());
                    for (TrbCompetence trbCompetence : trbCompetences) {

                        button = new Button(ListCompetenceActivity.this);
                        button.setText(trbCompetence.getRef_no() + " - " + URLDecoder.decode(trbCompetence.getDesc_competence()));
                        button.setBackgroundResource(R.drawable.button_comp);
                        button.setTextColor(Color.WHITE);
                        button.setId(trbCompetence.getId());

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ListCompetenceActivity.this, PersonTaskActivity.class);
                                String id = db.getFieldString("trb_competence_id","id ='"+v.getId()+"'","trb_competence");
                                intent.putExtra("id", id);
                                startActivity(intent);
                                finish();
                            }
                        });

                        container.addView(button);
                    }
                }
            }
        }


        return;
    }


    //GUIDELINES
    public void guide(){
        //Toast.makeText(GuidelinesActivity.this, "Clicked 1.1", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false)
                .setPositiveButton("CLOSE",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                    }
                });
        //dialog.setMessage("Please Select any option");
        ScrollView scrollView = new ScrollView(ListCompetenceActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20,10,20,10);
        scrollView.setLayoutParams(layoutParams);

        LinearLayout linearLayout = new LinearLayout(ListCompetenceActivity.this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(ListCompetenceActivity.this);
        textView.setText("Task Guidelines");
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setTextSize(20);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.LEFT);
        textView.setPadding(10,10,10,10);
        linearLayout.addView(textView);

        String text = "";
        if(dept.equals("DECK")){
            text += "<html><body>";
            text += "<p align=\"justify\" style=\"margin-left:10px;margin-right:10px;\">Function: Navigation</p>";
            text += "<ul>" +
                    "<li>Plan and conduct a passage and determine position</li>" +
                    "<li>Maintain a safe navigational watch</li>" +
                    "<li>Use of radar and ARPA to maintain the safety of navigation</li>" +
                    "<li>Use of ECDIS to maintain the safety of navigation</li>" +
                    "<li>Respond to emergencies</li>" +
                    "<li>Respond to a distress signal at sea</li>" +
                    "<li>Use the IMO Standard Marine Communication Phrases and use English in oral and written form.</li>" +
                    "<li>Transmit and receive information by visual signalling</li>" +
                    "<li>Manoeuvre the ship</li></ul>";
            text += "<p align=\"justify\" style=\"margin-left:10px;margin-right:10px;\">Function: Cargo Handling and Stowage</p>";
            text += "<ul><li>Monitor the loading, stowage, securing, care during the voyage and the unloading of cargoes.</li>" +
                    "<li>Inspect and report defects and damage to cargo spaces, hatch covers and ballast tanks.</li></ul>";
            text += "<p align=\"justify\" style=\"margin-left:10px;margin-right:10px;\">Function: Controlling the Operation of the Ship and Care for Persons On Board</p>";
            text += "<ul><li>Ensure compliance with pollution-prevention requirements</li>" +
                    "<li>Maintain seaworthiness of the ship</li>" +
                    "<li>Prevent, control and fight fires on board.</li>" +
                    "<li>Operate life-saving appliances</li>" +
                    "<li>Apply medical first aid on board ship</li>" +
                    "<li>Monitor compliance with legislative requirements.</li>" +
                    "<li>Contribute to the safety of personnel and ship.</li></ul>";
            text += "</body></html>";
        }else{
            text += "<html><body>";
            text += "<p align=\"justify\" style=\"margin-left:10px;margin-right:10px;\">Function: Marine Engineering</p>";
            text += "<ul>" +
                    "<li>Maintain a safe engineering watch</li>" +
                    "<li>Use English in written and oral form</li>" +
                    "<li>Use internal communication systems</li>" +
                    "<li>Operate main and auxiliary machinery and associated control systems</li>" +
                    "<li>Operate fuel, lubrication, ballast and other pumping systems and associated control systems</li>" +
                    "</ul>";
            text += "<p align=\"justify\" style=\"margin-left:10px;margin-right:10px;\">Function: Electrical, electronic and control engineering</p>";
            text += "<ul>" +
                    "<li>Operate electrical, electronic and control systems</li>" +
                    "<li>Maintenance and repair of electrical, electronic equipment</li></ul>";
            text += "<p align=\"justify\" style=\"margin-left:10px;margin-right:10px;\">Function: Maintenance and Repair</p>";
            text += "<ul>" +
                    "<li>Appropriate use of hand tools, machine tools, and measuring instruments for fabrication and repair on board</li>" +
                    "<li>Maintenance and repair of shipboard machinery and equipment</li>" +
                    "</ul>";
            text += "<p align=\"justify\" style=\"margin-left:10px;margin-right:10px;\">Function: Controlling the operation of the ship and care for persons on board</p>";
            text += "<ul>" +
                    "<li>Ensure compliance with pollution prevention requirements</li>" +
                    "<li>Maintain seaworthiness of the ship</li>" +
                    "<li>Prevent, control and fight fires on board</li>" +
                    "<li>Operate lifesaving appliances</li>" +
                    "<li>Apply medical first aid on board</li>" +
                    "<li>Monitor compliance with legislative requirements</li>" +
                    "<li>Application of leadership and teamworking skills</li>" +
                    "<li>Contribute to safety of personnel and ship</li>" +
                    "</ul>";
            text += "</body></html>";
        }


        WebView webView = new WebView(ListCompetenceActivity.this);
        webView.loadData(text, "text/html", "utf-8");
        linearLayout.addView(webView);

        scrollView.addView(linearLayout);

        dialog.setView(scrollView);
        dialog.setPositiveButton("Close", null);

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.black));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ListCompetenceActivity.this, MainActivity.class);
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
