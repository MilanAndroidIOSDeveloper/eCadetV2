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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BridgeWatchkeepingActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_bridge_watchkeeping);

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

            TextView textView = new TextView(BridgeWatchkeepingActivity.this);
            textView.setLayoutParams(viewParam);
            textView.setText("Date of Watchkeeping");
            textView.setBackgroundResource(R.drawable.border_darkblue);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(BridgeWatchkeepingActivity.this);
            textView1.setLayoutParams(viewParam);
            textView1.setText("Number of watchkeeping duty hours");
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(15);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setPadding(5,5,5,5);
            linearLayout.addView(textView1);

            final TextView textView5 = new TextView(BridgeWatchkeepingActivity.this);
            textView5.setLayoutParams(viewParam);
            textView5.setText("Ship Name");
            textView5.setBackgroundResource(R.drawable.border_darkblue);
            textView5.setTextColor(Color.WHITE);
            textView5.setTextSize(15);
            textView5.setGravity(Gravity.CENTER_HORIZONTAL);
            textView5.setPadding(5,5,5,5);
            linearLayout.addView(textView5);

            final TextView textView2 = new TextView(BridgeWatchkeepingActivity.this);
            textView2.setLayoutParams(viewParam);
            textView2.setText("STO");
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(15);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setPadding(5,5,5,5);
            linearLayout.addView(textView2);

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
                //GENERATE
                new generate(context).execute();
            }else{

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView textView = new TextView(BridgeWatchkeepingActivity.this);
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
            pd = new ProgressDialog(BridgeWatchkeepingActivity.this);
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
        int cnt = db.GetCount("person_journal", "");
        if(cnt > 0){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);

            LinearLayout linearLayout = new LinearLayout(BridgeWatchkeepingActivity.this);
            linearLayout.setLayoutParams(layoutParams);

            final TextView textView = new TextView(BridgeWatchkeepingActivity.this);
            textView.setLayoutParams(layoutParams1);
            textView.setText("Date of Watchkeeping");
            textView.setBackgroundResource(R.drawable.border_darkblue);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(BridgeWatchkeepingActivity.this);
            textView1.setLayoutParams(layoutParams1);
            textView1.setText("Time of Watchkeeping");
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(15);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setPadding(5,5,5,5);
            linearLayout.addView(textView1);

            final TextView textView5 = new TextView(BridgeWatchkeepingActivity.this);
            textView5.setLayoutParams(layoutParams1);
            textView5.setText("Ship Name");
            textView5.setBackgroundResource(R.drawable.border_darkblue);
            textView5.setTextColor(Color.WHITE);
            textView5.setTextSize(15);
            textView5.setGravity(Gravity.CENTER_HORIZONTAL);
            textView5.setPadding(5,5,5,5);
            linearLayout.addView(textView5);

            final TextView textView2 = new TextView(BridgeWatchkeepingActivity.this);
            textView2.setLayoutParams(layoutParams1);
            textView2.setText("STO Signature");
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(15);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setPadding(5,5,5,5);
            linearLayout.addView(textView2);

            final TextView textView4 = new TextView(BridgeWatchkeepingActivity.this);
            textView4.setLayoutParams(layoutParams1);
            textView4.setText("");
            textView4.setBackgroundResource(R.drawable.border_darkblue);
            textView4.setTextColor(Color.WHITE);
            textView4.setTextSize(15);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL);
            textView4.setPadding(5,5,5,5);
            linearLayout.addView(textView4);

            main_container.addView(linearLayout);

            List<PersonJournal> list = db.getPersonJournal(person_id);
            for(final PersonJournal list1 : list){

                LinearLayout linearLayout_ = new LinearLayout(BridgeWatchkeepingActivity.this);
                linearLayout_.setLayoutParams(layoutParams);
                linearLayout_.setBackgroundResource(R.drawable.border1dp);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
                String journal_date = list1.getJournal_date();
                try {
                    Date date = format.parse(journal_date);
                    journal_date = format2.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                TextView textView_ = new TextView(BridgeWatchkeepingActivity.this);
                textView_.setLayoutParams(layoutParams1);
                textView_.setText(journal_date);
                textView_.setTextColor(Color.BLACK);
                textView_.setTextSize(15);
                textView_.setGravity(Gravity.LEFT);
                textView_.setPadding(5,5,5,5);
                linearLayout_.addView(textView_);
                String from_time = list1.getJournal_time();
                String to_time = list1.getJournal_time_to();

                TextView textView1_ = new TextView(BridgeWatchkeepingActivity.this);
                textView1_.setLayoutParams(layoutParams1);
                textView1_.setText(from_time + " - " + to_time);
                textView1_.setTextColor(Color.BLACK);
                textView1_.setTextSize(15);
                textView1_.setGravity(Gravity.LEFT);
                textView1_.setPadding(5,5,5,5);
                linearLayout_.addView(textView1_);

                String name_vessel = db.getFieldString("name_vessel", "vessel_id = '"+list1.getVessel_id()+"'", "vessel");
                TextView textView5_ = new TextView(BridgeWatchkeepingActivity.this);
                textView5_.setLayoutParams(layoutParams1);
                textView5_.setText(name_vessel);
                textView5_.setTextColor(Color.BLACK);
                textView5_.setTextSize(15);
                textView5_.setGravity(Gravity.LEFT);
                textView5_.setPadding(5,5,5,5);
                linearLayout_.addView(textView5_);

                String full_name = db.getFieldString("full_name", "person_id = '"+list1.getChecked_by_id()+"'", "person");
                TextView textView2_ = new TextView(BridgeWatchkeepingActivity.this);
                textView2_.setLayoutParams(layoutParams1);
                textView2_.setText(full_name);
                textView2_.setTextColor(Color.BLACK);
                textView2_.setTextSize(15);
                textView2_.setGravity(Gravity.LEFT);
                textView2_.setPadding(5,5,5,5);
                linearLayout_.addView(textView2_);

                TextView textView4_ = new TextView(BridgeWatchkeepingActivity.this);
                textView4_.setLayoutParams(layoutParams1);
                textView4_.setText("Update");
                textView4_.setTextColor(Color.BLUE);
                textView4_.setTextSize(15);
                textView4_.setGravity(Gravity.CENTER_HORIZONTAL);
                textView4_.setPadding(5,5,5,5);
                textView4_.setId(list1.getId());
                textView4_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String person_journal_id = db.getFieldString("person_journal_id", " id = "+ v.getId(), "person_journal");
                        Intent intent = new Intent(context, BridgeWatchkeepingFormActivity.class);
                        intent.putExtra("person_journal_id", person_journal_id);
                        startActivity(intent);
                        finish();
                    }
                });
                linearLayout_.addView(textView4_);

                main_container.addView(linearLayout_);


            }
            LinearLayout.LayoutParams layoutParams_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams_.setMargins(5,-5,5,5);

            TextView tvAdd = new TextView(BridgeWatchkeepingActivity.this);
            tvAdd.setLayoutParams(layoutParams);
            tvAdd.setText("+ ADD NEW RECORD");
            tvAdd.setTypeface(null, Typeface.BOLD);
            tvAdd.setTextColor(Color.BLUE);
            tvAdd.setTextSize(15);
            tvAdd.setGravity(Gravity.LEFT);
            tvAdd.setPadding(10,5,5,5);
            tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BridgeWatchkeepingActivity.this, BridgeWatchkeepingFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            main_container.addView(tvAdd);

        }else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,10,5,5);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

            LinearLayout linearLayout = new LinearLayout(BridgeWatchkeepingActivity.this);
            linearLayout.setLayoutParams(layoutParams);

            final TextView textView = new TextView(BridgeWatchkeepingActivity.this);
            textView.setLayoutParams(layoutParams1);
            textView.setText("Date of Watchkeeping");
            textView.setBackgroundResource(R.drawable.border_darkblue);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(BridgeWatchkeepingActivity.this);
            textView1.setLayoutParams(layoutParams1);
            textView1.setText("Number of watchkeeping duty hours");
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(15);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setPadding(5,5,5,5);
            linearLayout.addView(textView1);

            final TextView textView5 = new TextView(BridgeWatchkeepingActivity.this);
            textView5.setLayoutParams(layoutParams1);
            textView5.setText("Ship Name");
            textView5.setBackgroundResource(R.drawable.border_darkblue);
            textView5.setTextColor(Color.WHITE);
            textView5.setTextSize(15);
            textView5.setGravity(Gravity.CENTER_HORIZONTAL);
            textView5.setPadding(5,5,5,5);
            linearLayout.addView(textView5);

            final TextView textView2 = new TextView(BridgeWatchkeepingActivity.this);
            textView2.setLayoutParams(layoutParams1);
            textView2.setText("STO");
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(15);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setPadding(5,5,5,5);
            linearLayout.addView(textView2);

            textView2.post(new Runnable() {
                @Override
                public void run() {
                    int height = textView2.getLineCount() * textView.getMeasuredHeight();
                    Log.d("HI", "" + height + " - " + textView.getMeasuredHeight());
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();

                    params.height = height;

                    textView.setLayoutParams(params);
                    textView1.setLayoutParams(params);
                    textView2.setLayoutParams(params);
                    textView5.setLayoutParams(params);
                }
            });

            main_container.addView(linearLayout);

            LinearLayout.LayoutParams layoutParams_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams_.setMargins(5,-5,5,5);

            TextView textView4 = new TextView(BridgeWatchkeepingActivity.this);
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
                    Intent intent = new Intent(BridgeWatchkeepingActivity.this, BridgeWatchkeepingFormActivity.class);
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
        Intent intent = new Intent(BridgeWatchkeepingActivity.this, RecordWatchActivity.class);
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
