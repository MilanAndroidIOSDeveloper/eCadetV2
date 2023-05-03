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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
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

public class AbbrevActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    LinearLayout main_container;
    SearchView searchView;
    DatabaseHelper db;
    String person_id, searchQuery = "";
    ProgressDialog pd;
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abbrev);

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
        //menus();
        /****** END MENU *******/

        main_container = findViewById(R.id.main_container);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        searchView = findViewById(R.id.searchView);

        db = new DatabaseHelper(AbbrevActivity.this);
        person_id = db.getCadetId();

        int cnt = db.GetCount("person", "");
        if(cnt > 0){
            new generate(context).execute();
        }else{
            searchView.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,5,5,5);

            TextView textView = new TextView(AbbrevActivity.this);
            textView.setText("Demo Version");
            textView.setTextColor(Color.RED);
            textView.setTextSize(20);
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            main_container.addView(textView);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                new generate(context).execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                new generate(context).execute();
                return false;
            }
        });
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
            pd = new ProgressDialog(AbbrevActivity.this);
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
        main_container.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,20,0,0);

        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3);


        LinearLayout linearLayout = new LinearLayout(AbbrevActivity.this);
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(AbbrevActivity.this);
        textView.setLayoutParams(layoutParams1);
        textView.setText("Abbreviation");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(15);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        textView.setPadding(5,5,5,5);
        linearLayout.addView(textView);

        TextView textView1 = new TextView(AbbrevActivity.this);
        textView1.setLayoutParams(layoutParams2);
        textView1.setText("Description");
        textView1.setTextColor(Color.WHITE);
        textView1.setTextSize(15);
        textView1.setGravity(Gravity.CENTER_HORIZONTAL);
        textView1.setBackgroundResource(R.drawable.border_darkblue);
        textView1.setPadding(5,5,5,5);
        linearLayout.addView(textView1);

        main_container.addView(linearLayout);
        List<Abbreviation> abbreviations = null;
        if(searchQuery.equals("")){
            abbreviations = db.getAbbreviations("");
        }else{
             abbreviations = db.getAbbreviations("code_abbreviation LIKE '%"+searchQuery+"%' OR desc_abbreviation LIKE '%"+searchQuery+"%'");
        }

        for(Abbreviation abbreviation : abbreviations){
            LinearLayout.LayoutParams layoutParams_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,20,0,0);

            LinearLayout.LayoutParams layoutParams1_ = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            LinearLayout.LayoutParams layoutParams2_ = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3);


            LinearLayout linearLayout_ = new LinearLayout(AbbrevActivity.this);
            linearLayout_.setLayoutParams(layoutParams_);
            linearLayout_.setBackgroundResource(R.drawable.border1dp);

            TextView textView_ = new TextView(AbbrevActivity.this);
            textView_.setLayoutParams(layoutParams1_);
            textView_.setText(abbreviation.getCode_abbreviation());
            textView_.setTextColor(Color.BLACK);
            textView_.setTextSize(15);
            textView_.setGravity(Gravity.LEFT);
            textView_.setPadding(5,5,5,5);
            linearLayout_.addView(textView_);

            TextView textView1_ = new TextView(AbbrevActivity.this);
            textView1_.setLayoutParams(layoutParams2_);
            textView1_.setText(URLDecoder.decode(abbreviation.getDesc_abbreviation()));
            textView1_.setTextColor(Color.BLACK);
            textView1_.setTextSize(15);
            textView1_.setGravity(Gravity.LEFT);
            textView1_.setPadding(5,5,5,5);
            linearLayout_.addView(textView1_);

            main_container.addView(linearLayout_);
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AbbrevActivity.this, MainActivity.class);
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
