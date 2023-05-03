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

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PersonInspectActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    LinearLayout main_container;
    DatabaseHelper db;
    ProgressDialog pd;
    String person_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_inspect);
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
        String dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");

        main_container = findViewById(R.id.main_container);

        int cnt = db.GetCount("person", "");
        if(cnt > 0){
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

        }else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,5,5,0);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            viewParam.weight = 1;

            TextView textView = new TextView(this);
            textView.setLayoutParams(viewParam);
            textView.setText("Ship Name");
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(18);
            textView.setPadding(5,5,5,5);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView);

            TextView textView1 = new TextView(this);
            textView1.setLayoutParams(viewParam);
            textView1.setText("Comments");
            textView1.setPadding(5,5,5,5);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(18);
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(this);
            textView2.setLayoutParams(viewParam);
            textView2.setText("Master Signature");
            textView2.setPadding(5,5,5,5);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(18);
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(this);
            textView3.setLayoutParams(viewParam);
            textView3.setText("Date Signed");
            textView3.setPadding(5,5,5,5);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setTextColor(Color.WHITE);
            textView3.setTextSize(18);
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView3);

            main_container.addView(linearLayout);

            textView = new TextView(this);
            textView.setLayoutParams(layoutParams);
            textView.setText("NOT YET ACTIVATED");
            textView.setTextColor(Color.RED);
            textView.setTextSize(18);
            textView.setPadding(5,5,5,5);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setBackgroundResource(R.drawable.border1dp);

            main_container.addView(textView);
        }
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
    public void display(){
        int cnt = db.GetCount("person_inspect", " WHERE person_id = '"+person_id+"'");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2);

        TextView textView = new TextView(this);
        textView.setLayoutParams(layoutParams1);
        textView.setText("Ship Name");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        textView.setPadding(5,5,5,5);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        TextView textView1 = new TextView(this);
        textView1.setLayoutParams(layoutParams2);
        textView1.setText("Comments");
        textView1.setPadding(5,5,5,5);
        textView1.setGravity(Gravity.CENTER_HORIZONTAL);
        textView1.setTextColor(Color.WHITE);
        textView1.setTextSize(18);
        textView1.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView1);

        TextView textView2 = new TextView(this);
        textView2.setLayoutParams(layoutParams1);
        textView2.setText("Master Signature");
        textView2.setPadding(5,5,5,5);
        textView2.setGravity(Gravity.CENTER_HORIZONTAL);
        textView2.setTextColor(Color.WHITE);
        textView2.setTextSize(18);
        textView2.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView2);

        TextView textView3 = new TextView(this);
        textView3.setLayoutParams(layoutParams1);
        textView3.setText("Date Signed");
        textView3.setPadding(5,5,5,5);
        textView3.setGravity(Gravity.CENTER_HORIZONTAL);
        textView3.setTextColor(Color.WHITE);
        textView3.setTextSize(18);
        textView3.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView3);

        main_container.addView(linearLayout);

        if(cnt > 0){
            List<PersonInspect> list = db.getPersonInspect(person_id);
            for (PersonInspect list1 : list){
                linearLayout = new LinearLayout(this);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setBackgroundResource(R.drawable.border1dp);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                String name_vessel = db.getFieldString("name_vessel", "vessel_id = '"+list1.getVessel_id()+"'", "vessel");
                textView = new TextView(this);
                textView.setLayoutParams(layoutParams1);
                textView.setText(name_vessel);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(18);
                textView.setPadding(5,5,5,5);
                linearLayout.addView(textView);

                String comments = URLDecoder.decode(list1.getComments());
                textView1 = new TextView(this);
                textView1.setLayoutParams(layoutParams2);
                textView1.setText(comments);
                textView1.setPadding(5,5,5,5);
                textView1.setTextColor(Color.BLACK);
                textView1.setTextSize(18);
                linearLayout.addView(textView1);

                String full_name = db.getFieldString("full_name", "person_id = '"+list1.getChecked_by_id()+"'", "person");
                textView2 = new TextView(this);
                textView2.setLayoutParams(layoutParams1);
                textView2.setText(full_name);
                textView2.setPadding(5,5,5,5);
                textView2.setTextColor(Color.BLACK);
                textView2.setTextSize(18);
                linearLayout.addView(textView2);

                String date_checked = list1.getDate_checked();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
                try {
                    Date date = format.parse(date_checked);
                    date_checked = format2.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                textView3 = new TextView(this);
                textView3.setLayoutParams(layoutParams1);
                textView3.setText(date_checked);
                textView3.setPadding(5,5,5,5);
                textView3.setTextColor(Color.BLACK);
                textView3.setTextSize(18);
                linearLayout.addView(textView3);

                main_container.addView(linearLayout);
            }
        }else{
            textView = new TextView(this);
            textView.setLayoutParams(layoutParams);
            textView.setText("No records to display.");
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(18);
            textView.setPadding(5,5,5,5);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setBackgroundResource(R.drawable.border1dp);

            main_container.addView(textView);
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