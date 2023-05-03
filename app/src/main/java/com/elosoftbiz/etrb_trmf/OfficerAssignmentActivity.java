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

public class OfficerAssignmentActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    DatabaseHelper db;
    ProgressDialog pd;

    String dept, person_id;
    LinearLayout container;
    TextView tv_title;
    ImageButton IB_add;

    LinearLayout.LayoutParams layoutParams, layoutParams1, layoutParams2, params, params2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_assignment);
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
        person_id = db.getCadetId();
        dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");
        container = findViewById(R.id.container);
        tv_title = findViewById(R.id.tv_title);

        int cnt = db.GetCount("person", "");
        if(cnt > 0) { //WITH DATA
            //CHANGE MENU HERE
            if (dept.equals("DECK")) {
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.main_menu_deck);
                new MenuDeck(navigationView, this, mDrawerLayout);
            } else {
                navigationView.getMenu().clear();
                navigationView.inflateMenu(R.menu.main_menu_engine);
                new MenuEngine(navigationView, this, mDrawerLayout);
            }

            int cnt2 = db.GetCount("shipboard", " WHERE person_id = '" + person_id + "'");
            if (cnt2 > 0) {

                new generate(context).execute();
            } else {
                tv_title.setText("Responsible Officers\nNo record on Sea Service found. Please save one first.");
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
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(OfficerAssignmentActivity.this);
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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView textView = new TextView(context);
        textView.setLayoutParams(layoutParams2);
        textView.setText("Ship Name");
        textView.setTextColor(getColor(R.color.white));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        textView = new TextView(context);
        textView.setLayoutParams(layoutParams2);
        textView.setText("Date of Effectivity From");
        textView.setTextColor(getColor(R.color.white));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        textView = new TextView(context);
        textView.setLayoutParams(layoutParams2);
        textView.setText("OTS");
        textView.setTextColor(getColor(R.color.white));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        textView = new TextView(context);
        textView.setLayoutParams(layoutParams2);
        textView.setText("STO");
        textView.setTextColor(getColor(R.color.white));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        textView = new TextView(context);
        textView.setLayoutParams(layoutParams2);
        textView.setText("Master");
        textView.setTextColor(getColor(R.color.white));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        textView = new TextView(context);
        textView.setLayoutParams(layoutParams2);
        textView.setText("Chief Engineer");
        textView.setTextColor(getColor(R.color.white));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);

        textView = new TextView(context);
        textView.setLayoutParams(layoutParams2);
        textView.setText("");
        textView.setTextColor(getColor(R.color.white));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(18);
        textView.setPadding(5,5,5,5);
        textView.setBackgroundResource(R.drawable.border_darkblue);
        linearLayout.addView(textView);
        container.addView(linearLayout);
        int cnt = db.GetCount("person_officer", "");
        if(cnt > 0){
            List<PersonOfficer> list = db.getPersonOfficers(person_id);
            for(PersonOfficer list1 : list){
                linearLayout = new LinearLayout(context);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setBackgroundResource(R.drawable.border1dp);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                String name_vessel = db.getFieldString("name_vessel", " vessel_id = '"+list1.getVessel_id()+"'", "vessel");
                textView = new TextView(context);
                textView.setLayoutParams(layoutParams2);
                textView.setText(name_vessel);
                textView.setTextColor(getColor(R.color.black));
                textView.setTextSize(18);
                textView.setPadding(5,5,5,5);
                linearLayout.addView(textView);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
                String from_date = list1.getFrom_date();

                try {
                    Date date = format.parse(from_date);
                    from_date = format2.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                textView = new TextView(context);
                textView.setLayoutParams(layoutParams2);
                textView.setText(from_date);
                textView.setTextColor(getColor(R.color.black));
                textView.setTextSize(18);
                textView.setPadding(5,5,5,5);
                linearLayout.addView(textView);

                String ots = db.getFieldString("full_name", " person_id = '"+list1.getOfficer_id()+"'", "person");
                textView = new TextView(context);
                textView.setLayoutParams(layoutParams2);
                textView.setText(ots);
                textView.setTextColor(getColor(R.color.black));
                textView.setTextSize(18);
                textView.setPadding(5,5,5,5);
                linearLayout.addView(textView);

                String sto = db.getFieldString("full_name", " person_id = '"+list1.getAssessor_id()+"'", "person");
                textView = new TextView(context);
                textView.setLayoutParams(layoutParams2);
                textView.setText(sto);
                textView.setTextColor(getColor(R.color.black));
                textView.setTextSize(18);
                textView.setPadding(5,5,5,5);
                linearLayout.addView(textView);

                String master = db.getFieldString("full_name", " person_id = '"+list1.getMaster_id()+"'", "person");
                textView = new TextView(context);
                textView.setLayoutParams(layoutParams2);
                textView.setText(master);
                textView.setTextColor(getColor(R.color.black));
                textView.setTextSize(18);
                textView.setPadding(5,5,5,5);
                linearLayout.addView(textView);

                String ce = db.getFieldString("full_name", " person_id = '"+list1.getChief_eng_id()+"'", "person");
                textView = new TextView(context);
                textView.setLayoutParams(layoutParams2);
                textView.setText(ce);
                textView.setTextColor(getColor(R.color.black));
                textView.setTextSize(18);
                textView.setPadding(5,5,5,5);
                linearLayout.addView(textView);

                textView = new TextView(context);
                textView.setLayoutParams(layoutParams2);
                textView.setText("Update");
                textView.setTextColor(getColor(R.color.link));
                textView.setTextSize(18);
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(5,5,5,5);
                textView.setId(list1.getId());

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String person_officer_id = db.getFieldString("person_officer_id", "id="+v.getId(), "person_officer");
                        Intent intent = new Intent(context, OfficerAssignmentFormActivity.class);
                        intent.putExtra("person_officer_id", person_officer_id);
                        startActivity(intent);
                        finish();
                    }
                });
                linearLayout.addView(textView);

                container.addView(linearLayout);
            }

        }else{
            linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackgroundResource(R.drawable.border1dp);

            textView = new TextView(context);
            textView.setText("No records to display.");
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(18);
            textView.setPadding(5,5,5,5);
            textView.setLayoutParams(layoutParams);
            linearLayout.addView(textView);
            container.addView(linearLayout);
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
        Intent intent = new Intent(OfficerAssignmentActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
