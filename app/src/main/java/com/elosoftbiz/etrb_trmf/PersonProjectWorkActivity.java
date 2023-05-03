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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PersonProjectWorkActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;
    String person_id, dept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_project_work);
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
        person_id = db.getFieldString("person_id", "vessel_officer = 'N'", "person");
        dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");
        main_container = findViewById(R.id.main_container);
        int cnt = db.GetCount("person", "");
        if(cnt > 0){
            new generate(context).execute();
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
            //int cnt = db.GetCount("person_project_work", " WHERE person_id = '"+person_id+"'");
            //if(cnt == 0){
            //    List<ProjectWorkH> list = db.getProjectWorkH(dept, " AND trb_function_id IN (SELECT trb_function_id FROM trb_function WHERE vessel_type_id = '')");
            //}

            startProgress();

            return null;
        }
        protected void onPostExecute(Void result){
            pd.dismiss();

        }
    }

    public void display(){
        int cnt = db.GetCount("person_project_work", "");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1 );
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,2 );

        List<ProjectWorkH> list  = db.getProjectWorkH(dept, " AND trb_function_id IN (SELECT trb_function_id FROM trb_function WHERE vessel_type_id = '')");
        for (ProjectWorkH list1 : list){
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            TextView textView = new TextView(context);
            textView.setLayoutParams(layoutParams);
            textView.setText(URLDecoder.decode(list1.getCompetence_area()));
            textView.setTextSize(18);
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.BLACK);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);
            main_container.addView(linearLayout);

            linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            textView = new TextView(context);
            textView.setLayoutParams(layoutParams2);
            textView.setText("Description");
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            textView = new TextView(context);
            textView.setLayoutParams(layoutParams1);
            textView.setText("");
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);
            main_container.addView(linearLayout);

            List<ProjectWorkD> list2 = db.getProjectWorkD(list1.getProject_work_h_id());
            for (ProjectWorkD list3 : list2){
                linearLayout = new LinearLayout(context);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setBackgroundResource(R.drawable.border1dp);
                String vessel_id = db.getFieldString("vessel_id", " person_id = '"+person_id+"' AND project_work_d_id = '"+list3.getProject_work_d_id()+"'", "person_project_work");
                String name_vessel = db.getFieldString("name_vessel", "vessel_id= '"+vessel_id+"'", "vessel");
                String date_completed = db.getFieldString("date_completed", " person_id = '"+person_id+"' AND project_work_d_id = '"+list3.getProject_work_d_id()+"'", "person_project_work");
                String date_checked = db.getFieldString("date_checked", " person_id = '"+person_id+"' AND project_work_d_id = '"+list3.getProject_work_d_id()+"'", "person_project_work");
                String checked_by_id = db.getFieldString("checked_by_id", " person_id = '"+person_id+"' AND project_work_d_id = '"+list3.getProject_work_d_id()+"'", "person_project_work");
                String checked_by = db.getFieldString("full_name", " person_id = '"+checked_by_id+"'", "person");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
                try {
                    Date date = format.parse(date_completed);
                    date_completed = format2.format(date);
                    date = format.parse(date_checked);
                    date_checked = format2.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                textView = new TextView(context);
                textView.setLayoutParams(layoutParams2);
                textView.setText(URLDecoder.decode(list3.getDetails()) + "\n\nShip Name: "+name_vessel+"\nDate Uploaded: "+date_completed+"\nDate Accepted: "+date_checked+"\nSTO: "+checked_by);
                textView.setTextSize(18);
                textView.setTextColor(Color.BLACK);
                textView.setPadding(5,5,5,5);
                linearLayout.addView(textView);
                Integer id = db.getFieldInt("id", " person_id = '"+person_id+"' AND project_work_d_id = '"+list3.getProject_work_d_id()+"'", "person_project_work");
                textView = new TextView(context);
                textView.setId(id);
                textView.setLayoutParams(layoutParams1);
                if(checked_by.equals("")){
                    textView.setText("Update");
                }else{
                    textView.setText("Completed");
                }

                textView.setTextSize(18);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getColor(R.color.link));
                textView.setPadding(5,5,5,5);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String person_project_work_id = db.getFieldString("person_project_work_id", " id = "+ v.getId(), "person_project_work");
                        Intent intent = new Intent(context, PersonProjectWorkFormActivity.class);
                        intent.putExtra("person_project_work_id", person_project_work_id);
                        startActivity(intent);
                        finish();
                    }
                });
                linearLayout.addView(textView);
                main_container.addView(linearLayout);
            }

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