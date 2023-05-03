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

public class MusterActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;

    TextView tv_title;
    DatabaseHelper db;
    ImageButton IB_add;
    String person_id;
    ProgressDialog pd;
    LinearLayout main_container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muster);

        context = this;

        /*** LOGO AND HEADER NAME HERE ***/
        mToolbar = findViewById( R.id.nav_action );
        setSupportActionBar( mToolbar );
        mDrawerLayout = findViewById( R.id.drawerLayout );
        mToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.open, R.string.close );
        mDrawerLayout.addDrawerListener( mToggle );
        mToggle.syncState();
        /*** END OF LOGO AND HEADER NAME ***/

        /****** START MENU *******/
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        new MenuDeck(navigationView, this, mDrawerLayout);
        /****** END MENU *******/

        tv_title = findViewById(R.id.tv_title);
        tv_title.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        IB_add = findViewById(R.id.IB_add);
        IB_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusterActivity.this, MusterFormActivity.class);
                startActivity(intent);
                finish();
            }
        });
        main_container = findViewById(R.id.main_container);

        db = new DatabaseHelper(this);
        person_id = db.getCadetId();

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
            pd = new ProgressDialog(MusterActivity.this);
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

        int cnt = db.GetCount("person_muster", " WHERE person_id = '"+person_id+"'");
        if(cnt > 0){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            layoutParams.setMargins(5,5,5, 5);

            LinearLayout linearLayout = new LinearLayout(MusterActivity.this);
            linearLayout.setLayoutParams(layoutParams);

            final TextView textView = new TextView(MusterActivity.this);
            textView.setText("Ship");
            textView.setLayoutParams(layoutParams1);
            textView.setTextSize(15);
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(MusterActivity.this);
            textView1.setText("Lifeboat Muster Station");
            textView1.setLayoutParams(layoutParams1);
            textView1.setTextSize(15);
            textView1.setTextColor(Color.WHITE);
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setPadding(5,5,5,5);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(MusterActivity.this);
            textView2.setText("Lifeboat Duties");
            textView2.setLayoutParams(layoutParams1);
            textView2.setTextSize(15);
            textView2.setTextColor(Color.WHITE);
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setPadding(5,5,5,5);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(MusterActivity.this);
            textView3.setText("Emergency Muster Station");
            textView3.setLayoutParams(layoutParams1);
            textView3.setTextSize(15);
            textView3.setTextColor(Color.WHITE);
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setPadding(5,5,5,5);
            linearLayout.addView(textView3);

            final TextView textView4 = new TextView(MusterActivity.this);
            textView4.setText("Emergency Duties");
            textView4.setLayoutParams(layoutParams1);
            textView4.setTextSize(15);
            textView4.setTextColor(Color.WHITE);
            textView4.setBackgroundResource(R.drawable.border_darkblue);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL);
            textView4.setPadding(5,5,5,5);
            linearLayout.addView(textView4);

            final TextView textView5 = new TextView(MusterActivity.this);
            textView5.setText("");
            textView5.setLayoutParams(layoutParams1);
            textView5.setTextSize(15);
            textView5.setTextColor(Color.WHITE);
            textView5.setBackgroundResource(R.drawable.border_darkblue);
            textView5.setGravity(Gravity.CENTER_HORIZONTAL);
            textView5.setPadding(5,5,5,5);
            linearLayout.addView(textView5);

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

            List<PersonMuster> personMusters = db.getPersonMuster(person_id, "");
            for(final PersonMuster personMuster : personMusters){

                LinearLayout.LayoutParams layoutParams_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams_.setMargins(5,-10,5,5);
                LinearLayout.LayoutParams layoutParams1_ = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);


                LinearLayout linearLayout_ = new LinearLayout(MusterActivity.this);
                linearLayout_.setLayoutParams(layoutParams_);
                linearLayout_.setBackgroundResource(R.drawable.border1dp);

                String name_vessel = db.getFieldString("name_vessel","vessel_id = '"+personMuster.getVessel_id()+"'","vessel");
                final TextView textView_ = new TextView(MusterActivity.this);
                textView_.setText(name_vessel);
                textView_.setLayoutParams(layoutParams1_);
                textView_.setTextSize(15);
                textView_.setTextColor(Color.BLACK);
                textView_.setGravity(Gravity.LEFT);
                textView_.setPadding(5,5,5,5);
                linearLayout_.addView(textView_);

                final TextView textView1_ = new TextView(MusterActivity.this);
                textView1_.setText(personMuster.getLifeboat_station());
                textView1_.setLayoutParams(layoutParams1_);
                textView1_.setTextSize(15);
                textView1_.setTextColor(Color.BLACK);
                textView1_.setGravity(Gravity.LEFT);
                textView1_.setPadding(5,5,5,5);
                linearLayout_.addView(textView1_);

                final TextView textView2_ = new TextView(MusterActivity.this);
                textView2_.setText(personMuster.getLifeboat_duties());
                textView2_.setLayoutParams(layoutParams1_);
                textView2_.setTextSize(15);
                textView2_.setTextColor(Color.BLACK);
                textView2_.setGravity(Gravity.LEFT);
                textView2_.setPadding(5,5,5,5);
                linearLayout_.addView(textView2_);

                final TextView textView3_ = new TextView(MusterActivity.this);
                textView3_.setText(personMuster.getEmergency_station());
                textView3_.setLayoutParams(layoutParams1_);
                textView3_.setTextSize(15);
                textView3_.setTextColor(Color.BLACK);
                textView3_.setGravity(Gravity.LEFT);
                textView3_.setPadding(5,5,5,5);
                linearLayout_.addView(textView3_);

                final TextView textView4_ = new TextView(MusterActivity.this);
                textView4_.setText(personMuster.getEmergency_duties());
                textView4_.setLayoutParams(layoutParams1_);
                textView4_.setTextSize(15);
                textView4_.setTextColor(Color.BLACK);
                textView4_.setGravity(Gravity.LEFT);
                textView4_.setPadding(5,5,5,5);
                linearLayout_.addView(textView4_);

                final TextView textView5_ = new TextView(MusterActivity.this);
                textView5_.setText("Update");
                textView5_.setLayoutParams(layoutParams1_);
                textView5_.setTextSize(15);
                textView5_.setTextColor(Color.BLUE);
                textView5_.setGravity(Gravity.CENTER_HORIZONTAL);
                textView5_.setPadding(5,5,5,5);
                linearLayout_.addView(textView5_);

                textView5_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MusterActivity.this, MusterFormActivity.class);
                        intent.putExtra("person_muster_id", personMuster.getPerson_muster_id());
                        startActivity(intent);
                        finish();
                    }
                });


                main_container.addView(linearLayout_);
            }

        }else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            layoutParams.setMargins(5,5,5,5);

            LinearLayout linearLayout = new LinearLayout(MusterActivity.this);
            linearLayout.setLayoutParams(layoutParams);

            final TextView textView = new TextView(MusterActivity.this);
            textView.setText("Ship");
            textView.setLayoutParams(layoutParams1);
            textView.setTextSize(15);
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(5,5,5,5);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(MusterActivity.this);
            textView1.setText("Lifeboat Muster Station");
            textView1.setLayoutParams(layoutParams1);
            textView1.setTextSize(15);
            textView1.setTextColor(Color.WHITE);
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setPadding(5,5,5,5);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(MusterActivity.this);
            textView2.setText("Lifeboat Duties");
            textView2.setLayoutParams(layoutParams1);
            textView2.setTextSize(15);
            textView2.setTextColor(Color.WHITE);
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setPadding(5,5,5,5);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(MusterActivity.this);
            textView3.setText("Emergency Muster Station");
            textView3.setLayoutParams(layoutParams1);
            textView3.setTextSize(15);
            textView3.setTextColor(Color.WHITE);
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setPadding(5,5,5,5);
            linearLayout.addView(textView3);

            final TextView textView4 = new TextView(MusterActivity.this);
            textView4.setText("Emergency Duties");
            textView4.setLayoutParams(layoutParams1);
            textView4.setTextSize(15);
            textView4.setTextColor(Color.WHITE);
            textView4.setBackgroundResource(R.drawable.border_darkblue);
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
                }
            });

            main_container.addView(linearLayout);

            LinearLayout.LayoutParams layoutParams_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams_.setMargins(5,-5,5,5);

            TextView textView5 = new TextView(MusterActivity.this);
            textView5.setText("No record to display.");
            textView5.setLayoutParams(layoutParams_);
            textView5.setTextSize(15);
            textView5.setTextColor(Color.BLACK);
            textView5.setBackgroundResource(R.drawable.border_darkblue);
            textView5.setGravity(Gravity.LEFT);
            textView5.setPadding(5,5,5,5);
            textView5.setBackgroundResource(R.drawable.border1dp);
            main_container.addView(textView5);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MusterActivity.this, ShipboardSafetyActivity.class);
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
