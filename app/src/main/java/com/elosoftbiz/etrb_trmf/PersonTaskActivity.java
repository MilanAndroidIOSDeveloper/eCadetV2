package com.elosoftbiz.etrb_trmf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

public class PersonTaskActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    DatabaseHelper db;

    LinearLayout main_container;
    ProgressDialog pd;
    String dept, person_id, task_type;
    String trb_competence_id;
    LinearLayout.LayoutParams layoutParams;

    String str = "", err_message, photo_file;
    HttpResponse response;
    JSONObject json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_task);

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
        trb_competence_id = intent.getStringExtra("id");
        task_type = intent.getStringExtra("task_type");

        db = new DatabaseHelper(this);
        person_id = db.getCadetId();
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


        String desc_competence = db.getFieldString("desc_competence", "trb_competence_id='"+trb_competence_id+"'", "trb_competence");
        desc_competence = URLDecoder.decode(desc_competence);
        String ref_no =  db.getFieldString("ref_no", "trb_competence_id='"+trb_competence_id+"'", "trb_competence");

        main_container = findViewById(R.id.main_container);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10,20,10,20);
        TextView textView = new TextView( context);
        textView.setText(ref_no + " " + desc_competence);
        textView.setTextColor(getColor(R.color.black));
        textView.setTextSize(18);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(layoutParams);

        main_container.addView(textView);

        pd = new ProgressDialog(PersonTaskActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading. Please wait .... ");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();


        new generate(context).execute();
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

    private class generate extends AsyncTask<Void, Void, Void> {
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

    public void display(){
        int cnt = db.GetCount("person_trb_topic", " WHERE person_id = '"+person_id+"' AND trb_topic_id IN (SELECT id FROM trb_topic WHERE trb_competence_id = '"+trb_competence_id+"')");
        Log.d("RESULT", "COUNT : "+ cnt);
        if(cnt == 0){
            autoCreate(trb_competence_id, person_id);
        }
        List<TrbTopic> trbTopics = db.getTopics(trb_competence_id);
        for (TrbTopic trbTopic : trbTopics) {

            LinearLayout.LayoutParams layoutParams0 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParams1_ = new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams layoutParams2_ = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2);
            LinearLayout linearLayout0 = new LinearLayout(PersonTaskActivity.this);
            linearLayout0.setLayoutParams(layoutParams0);

            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(layoutParams0);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(PersonTaskActivity.this);
            textView.setText("KUP");
            textView.setTextSize(18);
            textView.setPadding(5,5,5,5);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundResource(R.drawable.border_orange);
            textView.setLayoutParams(layoutParams1_);
            layout.addView(textView);

            textView = new TextView(PersonTaskActivity.this);
            textView.setText(trbTopic.getRef_no_topic() + " - " + URLDecoder.decode(URLDecoder.decode(trbTopic.getDesc_topic())) );
            textView.setTextSize(18);
            textView.setPadding(15,5,15,5);
            textView.setTextColor(Color.BLACK);
            textView.setBackgroundResource(R.drawable.border_orange);
            textView.setLayoutParams(layoutParams2_);
            layout.addView(textView);

            linearLayout0.addView(layout);

            main_container.addView(linearLayout0);

            //DISPLAY TASK GROUP
            List<TrbTaskGroup> taskGroups = db.getTrbTaskGroup("", trbTopic.getTrb_topic_id());
            for(TrbTaskGroup taskGroup : taskGroups){
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 3);
                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                layoutParams3.gravity = Gravity.CENTER_HORIZONTAL;

                LinearLayout linearLayout1 = new LinearLayout(PersonTaskActivity.this);
                linearLayout1.setLayoutParams(layoutParams);
                linearLayout1.setBackgroundResource(R.drawable.border_green);
                linearLayout1.setOrientation(LinearLayout.VERTICAL);

                TextView tvTaskG = new TextView(PersonTaskActivity.this);
                tvTaskG.setLayoutParams(layoutParams);
                tvTaskG.setText("TASK : " + URLDecoder.decode(taskGroup.getDesc_task_group()));
                tvTaskG.setTextColor(Color.BLACK);
                tvTaskG.setTextSize(18);
                tvTaskG.setPadding(10,10,10,10);
                linearLayout1.addView(tvTaskG);

                tvTaskG = new TextView(PersonTaskActivity.this);
                tvTaskG.setLayoutParams(layoutParams);
                tvTaskG.setText("CONDITION : " + URLDecoder.decode(taskGroup.getCond_task_group()));
                tvTaskG.setTextColor(Color.BLACK);
                tvTaskG.setTextSize(18);
                tvTaskG.setPadding(10,10,10,10);
                linearLayout1.addView(tvTaskG);

                main_container.addView(linearLayout1);

                List<PersonTask> tasks = db.getPersonTask("", "", taskGroup.getTrb_task_group_id());
                for(final PersonTask task : tasks){
                    LinearLayout linearLayout = new LinearLayout(PersonTaskActivity.this);
                    linearLayout.setLayoutParams(layoutParams);

                    final String task_id = task.getTask_id();
                    final String person_task_id = task.getPerson_task_id();
                    String ref_no = db.getFieldString("ref_no_task", "task_id = '"+task_id+"'", "task");
                    String cat_task_id = db.getFieldString("cat_task_id", "task_id = '"+task_id+"'", "task");
                    String ref_no_cat = db.getFieldString("ref_no", "cat_task_id = '"+cat_task_id+"'", "cat_task");
                    String direction_id = db.getFieldString("direction_id", "task_id = '"+task_id+"'", "task");
                    String code_direction = db.getFieldString("code_direction", "direction_id = '"+direction_id+"'", "direction");
                    Integer phase_no = db.getFieldInt("phase_no", "task_id = '"+task_id+"'", "task");
                    String desc_task = db.getFieldString("desc_task", "task_id = '"+task_id+"'", "task");
                    String criteria = db.getFieldString("criteria", "task_id = '"+task_id+"'", "task");
                    criteria = URLDecoder.decode(criteria);
                    /*
                if(phase_no == 1){
                    textView1.setBackgroundResource(R.drawable.border_yellow);
                }else if(phase_no == 2){
                    textView1.setBackgroundResource(R.drawable.border_green);
                }else if(phase_no == 3){
                    textView1.setBackgroundResource(R.drawable.border_blu);
                }else{
                    textView1.setBackgroundColor(getResources().getColor(R.color.white));
                }*/


                    final TextView textView2 = new TextView(PersonTaskActivity.this);
                    textView2.setLayoutParams(layoutParams2);
                    textView2.setText("Sub-Task : " + URLDecoder.decode(URLDecoder.decode(desc_task)) + "\nTask Category : " + ref_no_cat + "\nParticipation : " + code_direction+ "\n\nStandard Criteria for this Task:\n" + criteria);
                    textView2.setTextColor(getResources().getColor(R.color.black));
                    textView2.setTextSize(18);
                    textView2.setPadding(15,5,15,5);
                    textView2.setBackgroundResource(R.drawable.border_white);
                    linearLayout.addView(textView2);

                    textView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            task_on_click(person_task_id);
                        }
                    });

                    final TextView textView3 = new TextView(PersonTaskActivity.this);
                    textView3.setLayoutParams(layoutParams1);

                    if(task.getCompleted().equals("")){
                        textView3.setTextColor(getResources().getColor(R.color.black));
                        textView3.setText("START");
                    }else{
                        if(task.getPassed().equals("Y") && task.getPassed2().equals("Y")){
                            textView3.setTextColor(getResources().getColor(R.color.green));
                            textView3.setText("COMPLETED");
                        }else{
                            if(task.getChecked_by_id().equals("")){
                                textView3.setTextColor(getResources().getColor(R.color.blue));
                                textView3.setText("SUBMITTED");
                            }else{
                                textView3.setTextColor(getResources().getColor(R.color.blue));
                                textView3.setText("FOR REVISION");
                            }

                        }
                    }

                    textView3.setTextSize(15);
                    textView3.setPadding(5,0,5,5);
                    textView3.setBackgroundResource(R.drawable.border_white);
                    textView3.setGravity(Gravity.CENTER_HORIZONTAL);

                    textView3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            task_on_click(person_task_id);
                        }
                    });


                    linearLayout.addView(textView3);


                    main_container.addView(linearLayout);


                }
            }



        }
    }

    public void autoCreate(String trb_competence_id, String person_id){
        List<TrbTopic> trbTopics = db.getTopics(trb_competence_id);
        for (TrbTopic trbTopic : trbTopics){
            Integer int_id = db.newIntegerId("person_trb_topic");
            String person_trb_topic_id = db.newId();
            Integer cnt_topic = db.GetCount("person_trb_topic", " WHERE person_id = '"+person_id+"' AND trb_topic_id= '"+trbTopic.getTrb_topic_id()+"'");
            if(cnt_topic == 0){
                db.execQuery("INSERT INTO person_trb_topic (person_trb_topic_id, id, trb_topic_id, person_id, checked_by_id, date_checked, checked_remarks) VALUES ('"+person_trb_topic_id+"', "+int_id+", '"+trbTopic.getTrb_topic_id()+"', '"+person_id+"', '','','')");
                Log.d("RESULT", "INSERT INTO person_trb_topic (person_trb_topic_id, id, trb_topic_id, person_id, checked_by_id, date_checked, checked_remarks) VALUES ('"+person_trb_topic_id+"', "+int_id+", '"+trbTopic.getTrb_topic_id()+"', '"+person_id+"', '','','')");
            }

            int conn = getConnection.getConnectionType(PersonTaskActivity.this);
            //if(conn != 0){ //WITH CONN
            //    new SyncOnline(context, person_trb_topic_id, person_id, trbTopic.getTrb_topic_id()).execute();
            //}else{
            //    Integer backup_item_id = db.newIntegerId("backup_item");
            //    db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_trb_sub_competence', '" + person_trb_topic_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");

            //}

            List<Task> tasks = db.getTasks(trb_competence_id, trbTopic.getTrb_topic_id());
            for (Task task : tasks){
                Integer int_id_ = db.newIntegerId("person_task");
                String person_task_id = db.newId();
                Integer cnt_task = db.GetCount("person_task", " WHERE person_id = '"+person_id+"' AND task_id= '"+task.getTask_id()+"'");
                if(cnt_task == 0 ){
                    db.execQuery("INSERT INTO person_task (id, person_task_id, task_id, person_id, completed, answers, passed, img_file, not_app, lat_long, vessel_type_id, checked_by_id, app_by_id, date_checked, date_app, officer_remarks, app_remarks, vessel_id) VALUES ("+int_id_+", '"+person_task_id+"', '"+task.getTask_id()+"', '"+person_id+"', '','','','','','','','','','','','','','')");
                    Log.d("RESULT", "INSERT INTO person_task (id, person_task_id, task_id, person_id, completed, answers, passed, img_file, not_app, lat_long, vessel_type_id, checked_by_id, app_by_id, date_checked, date_app, officer_remarks, app_remarks, vessel_id) VALUES ("+int_id_+", '"+person_task_id+"', '"+task.getTask_id()+"', '"+person_id+"', '','','','','','','','','','','','','','')");
                    /*
                    if(conn != 0){
                        new SyncOnline2(context, person_task_id, person_id, task.getTask_id()).execute();
                    }else{
                        Integer backup_item_id = db.newIntegerId("backup_item");
                        db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_task', '" + person_task_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");

                    }

                     */
                }


            }

        }

        return;
    }

    public void task_on_click(String person_task_id){
        Intent intent = new Intent(PersonTaskActivity.this, TaskUpdateActivity.class);
        intent.putExtra("person_task_id", person_task_id);
        intent.putExtra("task_type", task_type);
        startActivity(intent);
        finish();
    }

    private class SyncOnline extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public String id, person_id, trb_topic_id;
        public SyncOnline(Context context, String id, String person_id, String trb_topic_id)
        {
            this.context = context;
            this.id = id;
            this.person_id = person_id;
            this.trb_topic_id = trb_topic_id;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            String url = getString(R.string.url);
            //GET FROM TBL
            String saved_trb_topic_id = db.getFieldString("trb_topic_id", "person_trb_topic_id = '" + id + "'", "person_trb_topic");
            String saved_person_id = db.getFieldString("person_id", "person_trb_topic_id = '" + id + "'", "person_trb_topic");
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_trb_topic_id = '" + id + "'", "person_trb_topic");
            String saved_date_checked = db.getFieldString("date_checked", "person_trb_topic_id = '" + id + "'", "person_trb_topic");
            String saved_checked_remarks = db.getFieldString("checked_remarks", "person_trb_topic_id = '" + id + "'", "person_trb_topic");
            saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_trb_topic&id="+id+"&trb_topic_id="+saved_trb_topic_id+"&person_id="+saved_person_id+"&checked_by_id="+saved_checked_by_id+"&date_checked="+saved_date_checked+"&checked_remarks="+saved_checked_remarks+"&event=ADD");
            Log.d("CONNECT", url +"sync.php?table=person_trb_topic&id="+id+"&trb_topic_id="+saved_trb_topic_id+"&person_id="+saved_person_id+"&checked_by_id="+saved_checked_by_id+"&date_checked="+saved_date_checked+"&checked_remarks="+saved_checked_remarks+"&event=ADD");

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

            return null;
        }
        protected void onPostExecute(Void result){
            return;

        }
    }

    private class SyncOnline2 extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public String id, person_id, task_id;
        public SyncOnline2(Context context, String id, String person_id, String task_id)
        {
            this.context = context;
            this.id = id;
            this.person_id = person_id;
            this.task_id = task_id;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            String url = getString(R.string.url);
            //GET FROM TBL
            String saved_task_id = db.getFieldString("task_id", "person_task_id = '" + id + "'", "person_task");
            String saved_person_id = db.getFieldString("person_id", "person_task_id = '" + id + "'", "person_task");
            String saved_completed = db.getFieldString("completed", "person_task_id = '" + id + "'", "person_task");
            String saved_answers = db.getFieldString("answers", "person_task_id = '" + id + "'", "person_task");
            saved_answers = URLEncoder.encode(saved_answers);
            String saved_passed = db.getFieldString("passed", "person_task_id = '" + id + "'", "person_task");
            String saved_img_file = db.getFieldString("img_file", "person_task_id = '" + id + "'", "person_task");
            saved_img_file = URLEncoder.encode(saved_img_file);
            String saved_not_app = db.getFieldString("not_app", "person_task_id = '" + id + "'", "person_task");
            String saved_lat_long = db.getFieldString("lat_long", "person_task_id = '" + id + "'", "person_task");
            saved_lat_long = URLEncoder.encode(saved_lat_long);
            String saved_vessel_type_id = db.getFieldString("vessel_type_id", "person_task_id = '" + id + "'", "person_task");
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_task_id = '" + id + "'", "person_task");
            String saved_app_by_id = db.getFieldString("app_by_id", "person_task_id = '" + id + "'", "person_task");
            String saved_date_checked = db.getFieldString("date_checked", "person_task_id = '" + id + "'", "person_task");
            String saved_date_app = db.getFieldString("date_app", "person_task_id = '" + id + "'", "person_task");
            String saved_officer_remarks = db.getFieldString("officer_remarks", "person_task_id = '" + id + "'", "person_task");
            saved_officer_remarks = URLEncoder.encode(saved_officer_remarks);
            String saved_app_remarks = db.getFieldString("app_remarks", "person_task_id = '" + id + "'", "person_task");
            saved_app_remarks = URLEncoder.encode(saved_app_remarks);
            String saved_vessel_id = db.getFieldString("vessel_id", "person_task_id = '" + id + "'", "person_task");

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_task&id="+id+"&task_id="+saved_task_id+"&person_id="+saved_person_id+"&completed="+saved_completed+"&answers="+saved_answers+"&passed="+saved_passed+"&img_file="+saved_img_file+"&not_app="+saved_not_app+"&lat_long="+saved_lat_long+"&vessel_type_id="+saved_vessel_type_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&officer_remarks="+saved_officer_remarks+"&app_remarks="+saved_app_remarks+"&vessel_id="+saved_vessel_id+"&event=ADD");
            Log.d("CONNECT", url +"sync.php?person_task&id="+id+"&task_id="+saved_task_id+"&person_id="+saved_person_id+"&completed="+saved_completed+"&answers="+saved_answers+"&passed="+saved_passed+"&img_file="+saved_img_file+"&not_app="+saved_not_app+"&lat_long="+saved_lat_long+"&vessel_type_id="+saved_vessel_type_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&officer_remarks="+saved_officer_remarks+"&app_remarks="+saved_app_remarks+"&vessel_id="+saved_vessel_id+"&event=ADD");

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

            return null;
        }
        protected void onPostExecute(Void result){
            return;

        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( pd!=null && pd.isShowing() ){
            pd.cancel();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PersonTaskActivity.this, ListCompetenceActivity.class);
        intent.putExtra("task_type", task_type);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(pd != null){
            pd.dismiss();
        }
    }
}
