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

import java.util.List;

public class PortWatchesActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_port_watches);

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

            TextView textView = new TextView(PortWatchesActivity.this);
            textView.setLayoutParams(viewParam);
            textView.setText("Date");
            textView.setBackgroundResource(R.drawable.border_darkblue);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(PortWatchesActivity.this);
            textView1.setLayoutParams(viewParam);
            textView1.setText("Time (From/To)");
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(15);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setPadding(5,5,5,5);
            linearLayout.addView(textView1);

            final TextView textView7 = new TextView(PortWatchesActivity.this);
            textView7.setLayoutParams(viewParam);
            textView7.setText("Total Hrs.");
            textView7.setBackgroundResource(R.drawable.border_darkblue);
            textView7.setTextColor(Color.WHITE);
            textView7.setTextSize(15);
            textView7.setGravity(Gravity.CENTER_HORIZONTAL);
            textView7.setPadding(5,5,5,5);
            linearLayout.addView(textView7);

            final TextView textView2 = new TextView(PortWatchesActivity.this);
            textView2.setLayoutParams(viewParam);
            textView2.setText("Voyage Number");
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(15);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setPadding(5,5,5,5);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(PortWatchesActivity.this);
            textView3.setLayoutParams(viewParam);
            textView3.setText("Name of port/terminal");
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            textView3.setTextColor(Color.WHITE);
            textView3.setTextSize(15);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setPadding(5,5,5,5);
            linearLayout.addView(textView3);

            final TextView textView4 = new TextView(PortWatchesActivity.this);
            textView4.setLayoutParams(viewParam);
            textView4.setText("Description of Cargo Operation");
            textView4.setBackgroundResource(R.drawable.border_darkblue);
            textView4.setTextColor(Color.WHITE);
            textView4.setTextSize(15);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL);
            textView4.setPadding(5,5,5,5);
            linearLayout.addView(textView4);

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
                IB_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PortWatchesActivity.this, PortWatchesFormActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                //GENERATE
                new generate(context).execute();
            }else{


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView textView = new TextView(PortWatchesActivity.this);
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
            pd = new ProgressDialog(PortWatchesActivity.this);
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
        int cnt = db.GetCount("person_port_watch", "");
        if(cnt > 0){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,10,5,5);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

            LinearLayout linearLayout = new LinearLayout(PortWatchesActivity.this);
            linearLayout.setLayoutParams(layoutParams);

            final TextView textView = new TextView(PortWatchesActivity.this);
            textView.setLayoutParams(layoutParams1);
            textView.setText("Date");
            textView.setBackgroundResource(R.drawable.border_darkblue);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(PortWatchesActivity.this);
            textView1.setLayoutParams(layoutParams1);
            textView1.setText("Time (From/To)");
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(15);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setPadding(5,5,5,5);
            linearLayout.addView(textView1);

            final TextView textView7 = new TextView(PortWatchesActivity.this);
            textView7.setLayoutParams(layoutParams1);
            textView7.setText("Total Hrs.");
            textView7.setBackgroundResource(R.drawable.border_darkblue);
            textView7.setTextColor(Color.WHITE);
            textView7.setTextSize(15);
            textView7.setGravity(Gravity.CENTER_HORIZONTAL);
            textView7.setPadding(5,5,5,5);
            linearLayout.addView(textView7);

            final TextView textView2 = new TextView(PortWatchesActivity.this);
            textView2.setLayoutParams(layoutParams1);
            textView2.setText("Voyage Number");
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(15);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setPadding(5,5,5,5);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(PortWatchesActivity.this);
            textView3.setLayoutParams(layoutParams1);
            textView3.setText("Name of port/terminal");
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            textView3.setTextColor(Color.WHITE);
            textView3.setTextSize(15);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setPadding(5,5,5,5);
            linearLayout.addView(textView3);

            final TextView textView4 = new TextView(PortWatchesActivity.this);
            textView4.setLayoutParams(layoutParams1);
            textView4.setText("Description of Cargo Operation");
            textView4.setBackgroundResource(R.drawable.border_darkblue);
            textView4.setTextColor(Color.WHITE);
            textView4.setTextSize(15);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL);
            textView4.setPadding(5,5,5,5);
            linearLayout.addView(textView4);

            final TextView textView5 = new TextView(PortWatchesActivity.this);
            textView5.setLayoutParams(layoutParams1);
            textView5.setText("");
            textView5.setBackgroundResource(R.drawable.border_darkblue);
            textView5.setTextColor(Color.WHITE);
            textView5.setTextSize(15);
            textView5.setGravity(Gravity.CENTER_HORIZONTAL);
            textView5.setPadding(5,5,5,5);
            linearLayout.addView(textView5);

            textView4.post(new Runnable() {
                @Override
                public void run() {
                    int height = textView4.getLineCount() * textView.getMeasuredHeight();
                    Log.d("HI", "" + height + " - " + textView.getMeasuredHeight());
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();

                    params.height = height;

                    textView.setLayoutParams(params);
                    textView1.setLayoutParams(params);
                    textView2.setLayoutParams(params);
                    textView3.setLayoutParams(params);
                    textView4.setLayoutParams(params);
                    textView5.setLayoutParams(params);
                    textView7.setLayoutParams(params);
                }
            });

            main_container.addView(linearLayout);

            List<PersonPortWatch> personPortWatches = db.getPersonPortWatches(person_id);
            for(final PersonPortWatch personPortWatch : personPortWatches){
                LinearLayout.LayoutParams layoutParams_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams_.setMargins(5,-5,5,5);
                LinearLayout.LayoutParams layoutParams1_ = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                LinearLayout linearLayout_ = new LinearLayout(PortWatchesActivity.this);
                linearLayout_.setLayoutParams(layoutParams_);
                linearLayout_.setBackgroundResource(R.drawable.border1dp);

                TextView textView_ = new TextView(PortWatchesActivity.this);
                textView_.setLayoutParams(layoutParams1_);
                textView_.setText(personPortWatch.getDate_watch());
                textView_.setTextColor(Color.BLACK);
                textView_.setTextSize(15);
                textView_.setGravity(Gravity.LEFT);
                textView_.setPadding(5,5,5,5);
                linearLayout_.addView(textView_);

                TextView textView1_ = new TextView(PortWatchesActivity.this);
                textView1_.setLayoutParams(layoutParams1_);
                textView1_.setText(personPortWatch.getFrom_time() + "/" + personPortWatch.getTo_time());
                textView1_.setTextColor(Color.BLACK);
                textView1_.setTextSize(15);
                textView1_.setGravity(Gravity.LEFT);
                textView1_.setPadding(5,5,5,5);
                linearLayout_.addView(textView1_);

                TextView textView6_ = new TextView(PortWatchesActivity.this);
                textView6_.setLayoutParams(layoutParams1_);
                textView6_.setText(""+personPortWatch.getTotal_hrs());
                textView6_.setTextColor(Color.BLACK);
                textView6_.setTextSize(15);
                textView6_.setGravity(Gravity.LEFT);
                textView6_.setPadding(5,5,5,5);
                linearLayout_.addView(textView6_);

                TextView textView2_ = new TextView(PortWatchesActivity.this);
                textView2_.setLayoutParams(layoutParams1_);
                textView2_.setText(personPortWatch.getVoyage_number());
                textView2_.setTextColor(Color.BLACK);
                textView2_.setTextSize(15);
                textView2_.setGravity(Gravity.LEFT);
                textView2_.setPadding(5,5,5,5);
                linearLayout_.addView(textView2_);

                TextView textView3_ = new TextView(PortWatchesActivity.this);
                textView3_.setLayoutParams(layoutParams1_);
                textView3_.setText(personPortWatch.getPort_name());
                textView3_.setTextColor(Color.BLACK);
                textView3_.setTextSize(15);
                textView3_.setGravity(Gravity.LEFT);
                textView3_.setPadding(5,5,5,5);
                linearLayout_.addView(textView3_);

                TextView textView4_ = new TextView(PortWatchesActivity.this);
                textView4_.setLayoutParams(layoutParams1_);
                textView4_.setText(personPortWatch.getDesc_cargo());
                textView4_.setTextColor(Color.BLACK);
                textView4_.setTextSize(15);
                textView4_.setGravity(Gravity.LEFT);
                textView4_.setPadding(5,5,5,5);
                linearLayout_.addView(textView4_);

                TextView textView5_ = new TextView(PortWatchesActivity.this);
                textView5_.setLayoutParams(layoutParams1_);
                textView5_.setText("Update");
                textView5_.setTextColor(Color.BLUE);
                textView5_.setTextSize(15);
                textView5_.setGravity(Gravity.CENTER_HORIZONTAL);
                textView5_.setPadding(5,5,5,5);
                linearLayout_.addView(textView5_);


                textView5_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PortWatchesActivity.this, PortWatchesFormActivity.class);
                        intent.putExtra("person_port_watch_id", personPortWatch.getPerson_port_watch_id());
                        startActivity(intent);
                        finish();
                    }
                });
                main_container.addView(linearLayout_);
            }

            LinearLayout.LayoutParams layoutParams_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams_.setMargins(5,-5,5,5);


            TextView tvAdd = new TextView(PortWatchesActivity.this);
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
                    Intent intent = new Intent(PortWatchesActivity.this, PortWatchesFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            main_container.addView(tvAdd);
        }else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,10,5,5);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

            LinearLayout linearLayout = new LinearLayout(PortWatchesActivity.this);
            linearLayout.setLayoutParams(layoutParams);

            final TextView textView = new TextView(PortWatchesActivity.this);
            textView.setLayoutParams(layoutParams1);
            textView.setText("Date");
            textView.setBackgroundResource(R.drawable.border_darkblue);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(PortWatchesActivity.this);
            textView1.setLayoutParams(layoutParams1);
            textView1.setText("Time (From/To)");
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(15);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setPadding(5,5,5,5);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(PortWatchesActivity.this);
            textView2.setLayoutParams(layoutParams1);
            textView2.setText("Voyage Number");
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(15);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setPadding(5,5,5,5);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(PortWatchesActivity.this);
            textView3.setLayoutParams(layoutParams1);
            textView3.setText("Name of port/terminal");
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            textView3.setTextColor(Color.WHITE);
            textView3.setTextSize(15);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setPadding(5,5,5,5);
            linearLayout.addView(textView3);

            final TextView textView4 = new TextView(PortWatchesActivity.this);
            textView4.setLayoutParams(layoutParams1);
            textView4.setText("Description of Cargo Operation");
            textView4.setBackgroundResource(R.drawable.border_darkblue);
            textView4.setTextColor(Color.WHITE);
            textView4.setTextSize(15);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL);
            textView4.setPadding(5,5,5,5);
            linearLayout.addView(textView4);

            final TextView textView5 = new TextView(PortWatchesActivity.this);
            textView5.setLayoutParams(layoutParams1);
            textView5.setText("");
            textView5.setBackgroundResource(R.drawable.border_darkblue);
            textView5.setTextColor(Color.WHITE);
            textView5.setTextSize(15);
            textView5.setGravity(Gravity.CENTER_HORIZONTAL);
            textView5.setPadding(5,5,5,5);
            linearLayout.addView(textView5);

            textView4.post(new Runnable() {
                @Override
                public void run() {
                    int height = textView4.getLineCount() * textView.getMeasuredHeight();
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

            LinearLayout.LayoutParams layoutParams_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams_.setMargins(5,-5,5,5);


            TextView textView6 = new TextView(PortWatchesActivity.this);
            textView6.setLayoutParams(layoutParams_);
            textView6.setText("+ ADD NEW RECORD");
            textView6.setBackgroundResource(R.drawable.border1dp);
            textView6.setTextColor(Color.BLUE);
            textView6.setTextSize(15);
            textView6.setGravity(Gravity.LEFT);
            textView6.setPadding(10,5,5,5);

            textView6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PortWatchesActivity.this, PortWatchesFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            main_container.addView(textView6);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PortWatchesActivity.this, RecordWatchActivity.class);
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
