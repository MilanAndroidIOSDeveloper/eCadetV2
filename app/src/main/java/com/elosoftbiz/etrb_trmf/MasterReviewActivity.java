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
import android.view.ViewGroup;
import android.widget.EditText;
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

import java.util.List;

public class MasterReviewActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title, tv_instruct;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;
    String person_id, dept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_review);

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
        person_id = db.getCadetId();
        dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");
        /****** START MENU *******/
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        new MenuDeck(navigationView, this, mDrawerLayout);
        /****** END MENU *******/

        tv_title = findViewById(R.id.tv_title);
        tv_title.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        tv_instruct = findViewById(R.id.tv_instruct);
        main_container = findViewById(R.id.main_container);

        int cnt = db.GetCount("person", "");
        if(cnt > 0){
            if(dept.equals("DECK")){
                tv_instruct.setText(getString(R.string.master_review_deck));
            }else{
                tv_instruct.setText(getString(R.string.master_review_engine));
            }

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

            //GENERATE
            new generate(context).execute();
        }else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,10,5,5);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            viewParam.weight = 1;

            TextView textView = new TextView(this);
            textView.setLayoutParams(viewParam);
            textView.setText("Ship");
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
            textView.setPadding(5,5,5,5);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(this);
            textView1.setLayoutParams(viewParam);
            textView1.setText("Comments");
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(15);
            textView1.setPadding(5,5,5,5);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(this);
            textView2.setLayoutParams(viewParam);
            textView2.setText("Name");
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(15);
            textView2.setPadding(5,5,5,5);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(this);
            textView3.setLayoutParams(viewParam);
            textView3.setText("Date");
            textView3.setTextColor(Color.WHITE);
            textView3.setTextSize(15);
            textView3.setPadding(5,5,5,5);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView3);

            main_container.addView(linearLayout);

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
            pd = new ProgressDialog(MasterReviewActivity.this);
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

    public void display(){
        int cnt = db.GetCount("person_ce", "");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5,15,5,5);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2);

        LinearLayout linearLayout = new LinearLayout(MasterReviewActivity.this);
        linearLayout.setLayoutParams(layoutParams);

        final TextView textView = new TextView(this);
        textView.setLayoutParams(params);
        textView.setText("Ship");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(15);
        textView.setPadding(5,5,5,5);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        final TextView textView1 = new TextView(this);
        textView1.setLayoutParams(params2);
        textView1.setText("Comments");
        textView1.setTextColor(Color.WHITE);
        textView1.setTextSize(15);
        textView1.setPadding(5,5,5,5);
        textView1.setGravity(Gravity.CENTER_HORIZONTAL);
        textView1.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView1);

        final TextView textView2 = new TextView(this);
        textView2.setLayoutParams(params);
        textView2.setText("Name");
        textView2.setTextColor(Color.WHITE);
        textView2.setTextSize(15);
        textView2.setPadding(5,5,5,5);
        textView2.setGravity(Gravity.CENTER_HORIZONTAL);
        textView2.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView2);

        final TextView textView3 = new TextView(this);
        textView3.setLayoutParams(params);
        textView3.setText("Date");
        textView3.setTextColor(Color.WHITE);
        textView3.setTextSize(15);
        textView3.setPadding(5,5,5,5);
        textView3.setGravity(Gravity.CENTER_HORIZONTAL);
        textView3.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView3);

        textView2.post(new Runnable() {
            @Override
            public void run() {
                int height = textView2.getLineCount() * textView.getMeasuredHeight();
                Log.d("HI", "" + height + " - " + textView1.getMeasuredHeight());
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) textView1.getLayoutParams();

                params.height = height;
                params2.height = height;

                textView.setLayoutParams(params);
                textView1.setLayoutParams(params2);
                textView2.setLayoutParams(params);
                textView3.setLayoutParams(params);
            }
        });

        main_container.addView(linearLayout);

        if(cnt > 0){

            List<PersonCe> personCes = db.getPersonCe(person_id);
            for(PersonCe personCe : personCes){
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1.setMargins(5,-10,5,5);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2);

                LinearLayout linearLayout1 = new LinearLayout(MasterReviewActivity.this);
                linearLayout1.setLayoutParams(layoutParams1);
                linearLayout1.setBackgroundResource(R.drawable.border1dp);

                String name_vessel = db.getFieldString("name_vessel", " vessel_id = '"+personCe.getVessel_id()+"'", "vessel");

                TextView textView_ = new TextView(MasterReviewActivity.this);
                textView_.setText(name_vessel);
                textView_.setTextSize(15);
                textView_.setTextColor(Color.BLACK);
                textView_.setLayoutParams(layoutParams2);
                textView_.setPadding(5,5,5,5);
                linearLayout1.addView(textView_);

                TextView textView1_ = new TextView(MasterReviewActivity.this);
                textView1_.setText(personCe.getComments());
                textView1_.setTextSize(15);
                textView1_.setTextColor(Color.BLACK);
                textView1_.setLayoutParams(layoutParams3);
                textView1_.setPadding(5,5,5,5);
                linearLayout1.addView(textView1_);

                String officer_name = db.getFieldString("full_name", "person_id = '"+personCe.getChecked_by_id()+"'", "person");
                TextView textView2_ = new TextView(MasterReviewActivity.this);
                textView2_.setText(officer_name);
                textView2_.setTextSize(15);
                textView2_.setTextColor(Color.BLACK);
                textView2_.setLayoutParams(layoutParams2);
                textView2_.setPadding(5,5,5,5);
                linearLayout1.addView(textView2_);

                TextView textView3_ = new TextView(MasterReviewActivity.this);
                textView3_.setText(personCe.getDate_checked());
                textView3_.setTextSize(15);
                textView3_.setTextColor(Color.BLACK);
                textView3_.setLayoutParams(layoutParams2);
                textView3_.setPadding(5,5,5,5);
                linearLayout1.addView(textView3_);

                main_container.addView(linearLayout1);
            }






        }else{
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.setMargins(5,-5,5,5);

            TextView textView_ =  new TextView(MasterReviewActivity.this);
            textView_.setLayoutParams(layoutParams1);
            textView_.setText("No record to display.");
            textView_.setTextSize(15);
            textView_.setTextColor(Color.BLACK);
            textView_.setBackgroundResource(R.drawable.border);
            textView_.setPadding(5,5,5,5);
            main_container.addView(textView_);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MasterReviewActivity.this, MainActivity.class);
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
