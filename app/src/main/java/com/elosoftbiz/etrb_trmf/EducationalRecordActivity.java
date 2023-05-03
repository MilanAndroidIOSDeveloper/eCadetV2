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

public class EducationalRecordActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_educational_record);

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
        String dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");

        if(person_id.equals("")){ //NO DATA

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,5,5,5);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            viewParam.weight = 1;

            TextView textView = new TextView(this);
            textView.setLayoutParams(viewParam);
            textView.setText("Level");
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
            textView.setPadding(5,5,5,5);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(this);
            textView1.setLayoutParams(viewParam);
            textView1.setText("School Name");
            textView1.setPadding(5,5,5,5);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(15);
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(this);
            textView2.setLayoutParams(viewParam);
            textView2.setText("Address");
            textView2.setPadding(5,5,5,5);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(15);
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(this);
            textView3.setLayoutParams(viewParam);
            textView3.setText("Date Certificate Issued");
            textView3.setPadding(5,5,5,5);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setTextColor(Color.WHITE);
            textView3.setTextSize(15);
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
            /*
            TextView textView = new TextView(EducationalRecordActivity.this);
            textView.setText("DEMO VERSION");
            textView.setTextColor(Color.RED);
            textView.setTextSize(20);
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            */

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

            IB_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EducationalRecordActivity.this, EducationalRecordFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            //GENERATE
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
            pd = new ProgressDialog(EducationalRecordActivity.this);
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
        int cnt = db.GetCount("person_education_h", "");
        if(cnt > 0){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

            LinearLayout linearLayout =  new LinearLayout(this);
            layoutParams.setMargins(5,10,5,5);
            linearLayout.setLayoutParams(layoutParams);

            final TextView textView = new TextView(this);
            textView.setLayoutParams(params);
            textView.setText("Level");
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
            textView.setPadding(5,5,5,5);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(this);
            textView1.setLayoutParams(params);
            textView1.setText("School Name");
            textView1.setPadding(5,5,5,5);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(15);
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(this);
            textView2.setLayoutParams(params);
            textView2.setText("Address");
            textView2.setPadding(5,5,5,5);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(15);
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(this);
            textView3.setLayoutParams(params);
            textView3.setText("Date Certificate Issued");
            textView3.setPadding(5,5,5,5);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setTextColor(Color.WHITE);
            textView3.setTextSize(15);
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView3);

            final TextView textView4 = new TextView(this);
            textView4.setLayoutParams(params);
            textView4.setText("");
            textView4.setPadding(5,5,5,5);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL);
            textView4.setTextColor(Color.WHITE);
            textView4.setTextSize(15);
            textView4.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView4);

            main_container.addView(linearLayout);

            textView3.post(new Runnable() {
                @Override
                public void run() {
                    int height = textView3.getLineCount() * textView.getMeasuredHeight();
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
            List<PersonEducationH> personEducationHS = db.getPersonEducationH(person_id);
            for(final PersonEducationH personEducationH : personEducationHS){

                LinearLayout linearLayout1 =  new LinearLayout(this);
                layoutParams.setMargins(5,-5,5,5);
                linearLayout1.setLayoutParams(layoutParams);
                linearLayout1.setBackgroundResource(R.drawable.border1dp);

                final TextView textView_ = new TextView(EducationalRecordActivity.this);
                textView_.setText(personEducationH.getGce_level());
                textView_.setTextColor(getResources().getColor(R.color.black));
                textView_.setTextSize(15);
                textView_.setLayoutParams(params);
                //textView_.setBackgroundResource(R.drawable.border);
                textView_.setPadding(10,5,5,10);
                linearLayout1.addView(textView_);

                final TextView textView1_ = new TextView(EducationalRecordActivity.this);
                textView1_.setText(personEducationH.getSchool_name());
                textView1_.setTextColor(getResources().getColor(R.color.black));
                textView1_.setTextSize(15);
                textView1_.setLayoutParams(params);
                //textView1_.setBackgroundResource(R.drawable.border);
                textView1_.setPadding(10,5,5,5);
                linearLayout1.addView(textView1_);

                final TextView textView2_ = new TextView(EducationalRecordActivity.this);
                textView2_.setText(personEducationH.getSchool_address());
                textView2_.setTextColor(getResources().getColor(R.color.black));
                textView2_.setTextSize(15);
                textView2_.setLayoutParams(params);
                //textView2_.setBackgroundResource(R.drawable.border);
                textView2_.setPadding(10,5,5,5);
                linearLayout1.addView(textView2_);

                final TextView textView3_ = new TextView(EducationalRecordActivity.this);
                textView3_.setText(personEducationH.getCertificate_date());
                textView3_.setTextColor(getResources().getColor(R.color.black));
                textView3_.setTextSize(15);
                textView3_.setLayoutParams(params);
                //textView3_.setBackgroundResource(R.drawable.border);
                textView3_.setPadding(10,5,5,5);
                linearLayout1.addView(textView3_);

                final TextView textView4_ = new TextView(EducationalRecordActivity.this);
                textView4_.setText("Update");
                textView4_.setTextSize(15);
                textView4_.setTextColor(Color.BLUE);
                textView4_.setGravity(Gravity.CENTER_HORIZONTAL);
                textView4_.setLayoutParams(params);
                textView4_.setPadding(5,5,5,5);
                //textView4_.setBackgroundResource(R.drawable.border);
                linearLayout1.addView(textView4_);

                textView4_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EducationalRecordActivity.this, EducationalRecordFormActivity.class);
                        intent.putExtra("person_education_h_id", personEducationH.getPerson_education_h_id());
                        startActivity(intent);
                        finish();
                    }
                });

                textView1_.post(new Runnable() {
                    @Override
                    public void run() {
                        int height = textView1_.getLineCount() * textView4_.getMeasuredHeight();
                        int height2 = textView_.getLineCount() * textView4_.getMeasuredHeight();

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView4_.getLayoutParams();
                        if(height < height2){
                            height = height2;
                        }
                        params.height = height;

                        textView_.setLayoutParams(params);
                        textView1_.setLayoutParams(params);
                        textView2_.setLayoutParams(params);
                        textView3_.setLayoutParams(params);
                        textView4_.setLayoutParams(params);
                    }
                });
                main_container.addView(linearLayout1);
            }
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.setMargins(5,-5,5,5);

            TextView tvAdd =  new TextView(EducationalRecordActivity.this);
            tvAdd.setLayoutParams(layoutParams1);
            tvAdd.setText("+ ADD NEW RECORD");
            tvAdd.setTextSize(15);
            tvAdd.setTextColor(Color.BLUE);
            tvAdd.setTypeface(null, Typeface.BOLD);
            tvAdd.setPadding(5,5,5,5);
            tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EducationalRecordActivity.this, EducationalRecordFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            main_container.addView(tvAdd);

        }else{  //NO RECORD
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

            LinearLayout linearLayout =  new LinearLayout(this);
            layoutParams.setMargins(5,10,5,5);
            linearLayout.setLayoutParams(layoutParams);

            final TextView textView = new TextView(this);
            textView.setLayoutParams(params);
            textView.setText("Level");
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
            textView.setPadding(5,5,5,5);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(this);
            textView1.setLayoutParams(params);
            textView1.setText("School Name");
            textView1.setPadding(5,5,5,5);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(15);
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(this);
            textView2.setLayoutParams(params);
            textView2.setText("Address");
            textView2.setPadding(5,5,5,5);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(15);
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(this);
            textView3.setLayoutParams(params);
            textView3.setText("Date Certificate Issued");
            textView3.setPadding(5,5,5,5);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setTextColor(Color.WHITE);
            textView3.setTextSize(15);
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView3);

            main_container.addView(linearLayout);

            textView3.post(new Runnable() {
                @Override
                public void run() {
                    int height = textView3.getLineCount() * textView.getMeasuredHeight();
                    Log.d("HI", "" + height + " - " + textView.getMeasuredHeight());
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();

                    params.height = height;

                    textView.setLayoutParams(params);
                    textView1.setLayoutParams(params);
                    textView2.setLayoutParams(params);
                    textView3.setLayoutParams(params);
                }
            });

            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.setMargins(5,-5,5,5);

            TextView textView4 =  new TextView(EducationalRecordActivity.this);
            textView4.setLayoutParams(layoutParams1);
            textView4.setText("+ ADD NEW RECORD");
            textView4.setTextSize(15);
            textView4.setTextColor(Color.BLUE);
            textView4.setBackgroundResource(R.drawable.border);
            textView4.setPadding(5,5,5,5);
            textView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EducationalRecordActivity.this, EducationalRecordFormActivity.class);
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
        Intent intent = new Intent(EducationalRecordActivity.this, MainActivity.class);
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
