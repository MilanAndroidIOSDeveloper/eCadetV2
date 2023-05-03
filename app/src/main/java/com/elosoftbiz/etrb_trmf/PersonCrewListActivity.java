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
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PersonCrewListActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;
    String person_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_crew_list);
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
            new Display(context).execute();
        }else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,5,5,5);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(this);
            textView.setLayoutParams(layoutParams);
            textView.setText("NOT YET ACTIVATED");
            textView.setTextColor(Color.RED);
            textView.setTextSize(18);
            textView.setPadding(5,5,5,5);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setBackgroundResource(R.drawable.border1dp);
            linearLayout.addView(textView);
            main_container.addView(linearLayout);
        }
    }

    private class Display extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public Display(Context context)
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

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //layoutParams.setMargins(10,10,10,10);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(layoutParams);

        LinearLayout linearLayout1 = new LinearLayout(context);
        linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout1.setLayoutParams(layoutParams);

        TextView textView1 = new TextView(context);
        textView1.setText("Ship Name");
        textView1.setBackgroundResource(R.drawable.border_darkblue);
        textView1.setLayoutParams(layoutParams1);
        textView1.setTextColor(Color.WHITE);
        textView1.setTextSize(18);
        textView1.setPadding(5,5,5,5);
        textView1.setGravity(Gravity.CENTER);
        linearLayout1.addView(textView1);

        textView1 = new TextView(context);
        textView1.setText("Crew List File Upload");
        textView1.setBackgroundResource(R.drawable.border_darkblue);
        textView1.setLayoutParams(layoutParams1);
        textView1.setTextColor(Color.WHITE);
        textView1.setTextSize(18);
        textView1.setPadding(5,5,5,5);
        textView1.setGravity(Gravity.CENTER);
        linearLayout1.addView(textView1);

        textView1 = new TextView(context);
        textView1.setText("Date Uploaded");
        textView1.setBackgroundResource(R.drawable.border_darkblue);
        textView1.setLayoutParams(layoutParams1);
        textView1.setTextColor(Color.WHITE);
        textView1.setTextSize(18);
        textView1.setPadding(5,5,5,5);
        textView1.setGravity(Gravity.CENTER);
        linearLayout1.addView(textView1);

        textView1 = new TextView(context);
        textView1.setText("OTS Signature");
        textView1.setBackgroundResource(R.drawable.border_darkblue);
        textView1.setLayoutParams(layoutParams1);
        textView1.setTextColor(Color.WHITE);
        textView1.setTextSize(18);
        textView1.setPadding(5,5,5,5);
        textView1.setGravity(Gravity.CENTER);
        linearLayout1.addView(textView1);

        textView1 = new TextView(context);
        textView1.setText("Date Signed");
        textView1.setBackgroundResource(R.drawable.border_darkblue);
        textView1.setLayoutParams(layoutParams1);
        textView1.setTextColor(Color.WHITE);
        textView1.setTextSize(18);
        textView1.setPadding(5,5,5,5);
        textView1.setGravity(Gravity.CENTER);
        linearLayout1.addView(textView1);

        textView1 = new TextView(context);
        textView1.setText("");
        textView1.setBackgroundResource(R.drawable.border_darkblue);
        textView1.setLayoutParams(layoutParams1);
        textView1.setTextColor(Color.WHITE);
        textView1.setTextSize(18);
        textView1.setPadding(5,5,5,5);
        textView1.setGravity(Gravity.CENTER);
        linearLayout1.addView(textView1);

        linearLayout.addView(linearLayout1);
        int cnt = db.GetCount("person_crew_list", "");
        if(cnt > 0){
            List<PersonCrewList> list = db.getPersonCrewList(person_id);
            for(PersonCrewList list1 : list){
                LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout2.setLayoutParams(layoutParams);
                linearLayout2.setBackgroundResource(R.drawable.border1dp);

                String vessel_name = db.getFieldString("name_vessel", "vessel_id = '"+list1.getVessel_id()+"'", "vessel");
                TextView textView = new TextView(context);
                textView.setLayoutParams(layoutParams1);
                textView.setText(vessel_name);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(18);
                textView.setPadding(5,5,5,5);
                linearLayout2.addView(textView);

                textView = new TextView(context);
                textView.setLayoutParams(layoutParams1);
                textView.setText(list1.getFilename());
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(18);
                textView.setPadding(5,5,5,5);
                linearLayout2.addView(textView);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
                String date_uploaded = list1.getDate_uploaded();
                String date_checked = list1.getDate_checked();
                if(date_checked.equals("null")){
                    date_checked = "";
                }
                try {
                    Date date = format.parse(date_uploaded);
                    date_uploaded = format2.format(date);
                    date = format.parse(date_checked);
                    date_checked = format2.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                textView = new TextView(context);
                textView.setLayoutParams(layoutParams1);
                textView.setText(date_uploaded);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(18);
                textView.setPadding(5,5,5,5);
                linearLayout2.addView(textView);

                String full_name = db.getFieldString("full_name", "person_id = '"+list1.getChecked_by_id()+"'", "person");
                textView = new TextView(context);
                textView.setLayoutParams(layoutParams1);
                textView.setText(full_name);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(18);
                textView.setPadding(5,5,5,5);
                linearLayout2.addView(textView);

                textView = new TextView(context);
                textView.setLayoutParams(layoutParams1);
                textView.setText(date_checked);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(18);
                textView.setPadding(5,5,5,5);
                linearLayout2.addView(textView);

                textView = new TextView(context);
                textView.setLayoutParams(layoutParams1);
                textView.setText("Update");
                textView.setTextColor(getColor(R.color.link));
                textView.setTextSize(18);
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(5,5,5,5);
                textView.setId(list1.getId());

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        String person_crew_list_id = db.getFieldString("person_crew_list_id", "id = "+id, "person_crew_list");

                        Intent intent = new Intent(context, PersonCrewListFormActivity.class);
                        intent.putExtra("person_crew_list_id", person_crew_list_id);
                        startActivity(intent);
                        finish();
                    }
                });
                linearLayout2.addView(textView);

                linearLayout.addView(linearLayout2);
            }
            TextView tvAdd =  new TextView(context);
            tvAdd.setLayoutParams(layoutParams);
            tvAdd.setText("+ ADD NEW RECORD");
            tvAdd.setTextSize(15);
            tvAdd.setTextColor(Color.BLUE);
            tvAdd.setTypeface(null, Typeface.BOLD);
            tvAdd.setPadding(5,5,5,5);

            tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PersonCrewListFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            linearLayout.addView(tvAdd);
        }else{

            TextView textView =  new TextView(context);
            textView.setLayoutParams(layoutParams);
            textView.setText("+ ADD NEW RECORD");
            textView.setTextSize(15);
            textView.setTextColor(Color.BLUE);
            textView.setBackgroundResource(R.drawable.border);
            textView.setPadding(5,5,5,5);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PersonCrewListFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            linearLayout.addView(textView);

        }
        main_container.addView(linearLayout);
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