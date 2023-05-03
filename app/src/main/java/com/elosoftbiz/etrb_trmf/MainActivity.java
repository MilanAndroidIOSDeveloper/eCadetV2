package com.elosoftbiz.etrb_trmf;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;

    NavigationView navigationView;
    Context context;

    DatabaseHelper db;
    ProgressDialog pd;
    NetworkChangeReceiver receiver;

    LinearLayout main_container;
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    String[] permissions= new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA

    };
    String dept = "", person_id, vessel_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");
        main_container = findViewById(R.id.main_container);

        int cnt = db.GetCount("person", "");
        if(cnt > 0){ //WITH DATA

            Integer cnt_backup = db.GetCount("backup_item", " WHERE backuped != 'Y'");
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= 19){
                Log.d("RESULT", "Net");
                //if(cnt_backup > 0){
                    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                    receiver = new NetworkChangeReceiver();
                    this.registerReceiver(receiver, filter);
               // }

            }

            person_id = db.getFieldString("person_id", " vessel_officer = 'N'", "person");

            String lname = db.getFieldString("lname", " vessel_officer = 'N'", "person");
            String fname = db.getFieldString("fname", " vessel_officer = 'N'", "person");
            String mname = db.getFieldString("mname", " vessel_officer = 'N'", "person");
            String dept = db.getFieldString("dept", " vessel_officer = 'N'", "person");
            String school_id = db.getFieldString("school_id", " person_id ='"+person_id+"'", "person");
            String company_id = db.getFieldString("company_id", " person_id ='"+person_id+"'", "person");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            layoutParams.setMargins(10,5,10,10);

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

            TextView tvFullName = findViewById(R.id.tvFullName);
            tvFullName.setText(lname + ", " + fname + " " + mname);
            ImageView ivProfile = findViewById(R.id.ivProfile);

            String photo_file = db.getFieldString("photo_file", "vessel_officer = 'N'", "person");
            File file = new File("/storage/emulated/0/Android/data/com.elosoftbiz.etrb_trmf/files/" + Environment.DIRECTORY_PICTURES + "/" + photo_file);
            String currentPhotoPath = file.getAbsolutePath();
            if(photo_file.equals("") || photo_file.equals("null")){
                ivProfile.setImageResource(R.drawable.profile);
            }else{
                if(file.exists()){
                    Bitmap bm = BitmapFactory.decodeFile(currentPhotoPath);
                    ivProfile.setImageBitmap(bm);
                    Log.d("RESULT",  "" + file);
                    // Toast.makeText(ProfileActivity.this, "" + file, Toast.LENGTH_LONG).show();
                }else{
                    Log.d("RESULT",  "NO IMAGE" + file);
                    ivProfile.setImageResource(R.drawable.profile);
                }
            }
            TextView tvSchool= findViewById(R.id.tvSchool);
            tvSchool.setText("School : " + school_id);
            TextView tvCompany = findViewById(R.id.tvCompany);
            tvCompany.setText("Company : " + company_id);
            TextView tvDept= findViewById(R.id.tvDept);
            tvDept.setText("Department : " +dept);

            TextView tv1Cnt = findViewById(R.id.tv1Cnt);
            TextView tv2Cnt = findViewById(R.id.tv2Cnt);
            TextView tv3Cnt = findViewById(R.id.tv3Cnt);
            TextView tv4Cnt = findViewById(R.id.tv4Cnt);
            TextView tv5Cnt = findViewById(R.id.tv5Cnt);

            int cnt1 = db.GetCount("shipboard","");
            String date_from = "";
            long timeDiff = 0;
            long daysDiff = 0;

            if(cnt1 > 0){
                date_from = db.getFieldString("sign_on","person_id ='"+person_id+"' ORDER BY sign_off DESC LIMIT 1","shipboard");
                SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date c = Calendar.getInstance().getTime(); //get current date
                String dateTo = sdFormat.format(c);  //format date

                if(date_from.equals("")){
                    int yr = Calendar.getInstance().get(Calendar.YEAR);;
                    date_from = yr + "-01-01";

                }
                Date startDateObj = null;
                Date endDateObj = null;

                try {
                    startDateObj = sdFormat.parse(date_from + " 00:00:01");
                    endDateObj = sdFormat.parse(dateTo + " 24:00:00");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                timeDiff = endDateObj.getTime() - startDateObj.getTime();
                daysDiff = timeDiff/(1000*60*60*24);
            }
            tv1Cnt.setText("" + daysDiff);

            int taskForApp = db.GetCount("person_task", " WHERE person_id = '"+person_id+"' AND for_app = 'Y' AND completed != ''");
            tv2Cnt.setText("" + taskForApp);
            tv2Cnt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ForApprovalActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            int otTasks = db.GetCount("task", " WHERE cat_task_id = (SELECT cat_task_id FROM cat_task WHERE ref_no ='OT')");
            int otTasksDone = db.GetCount("person_task", " WHERE task_id IN (SELECT task_id FROM task WHERE cat_task_id = (SELECT cat_task_id FROM cat_task WHERE ref_no ='OT')) AND passed = 'Y' AND passed2 = 'Y'");
            int spTasks = db.GetCount("task", " WHERE cat_task_id = (SELECT cat_task_id FROM cat_task WHERE ref_no ='SP')");
            int spTasksDone = db.GetCount("person_task", " WHERE task_id IN (SELECT task_id FROM task WHERE cat_task_id = (SELECT cat_task_id FROM cat_task WHERE ref_no ='SP')) AND passed = 'Y' AND passed2 = 'Y'");
            int pwTasksDone = db.GetCount("person_project_work", " WHERE checked_by_id != '' AND checked_by_id != 'null'");
            tv3Cnt.setText("" + otTasksDone);
            tv4Cnt.setText("" + spTasksDone);
            tv5Cnt.setText("" + pwTasksDone);

            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout linear_sea_service = findViewById(R.id.linear_sea_service);
            TextView tv_ssr_title = new TextView(MainActivity.this);
            tv_ssr_title.setLayoutParams(layoutParams1);
            tv_ssr_title.setText("SEA SERVICE RECORD");
            tv_ssr_title.setTextColor(Color.WHITE);
            tv_ssr_title.setTextSize(20);
            tv_ssr_title.setGravity(Gravity.CENTER_HORIZONTAL);
            tv_ssr_title.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            linear_sea_service.addView(tv_ssr_title);

            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);

            LinearLayout linearLayout4 = new LinearLayout(MainActivity.this);
            linearLayout4.setLayoutParams(layoutParams1);

            TextView tv_ssr1 = new TextView(MainActivity.this);
            tv_ssr1.setText("SIGN ON");
            tv_ssr1.setTextColor(Color.WHITE);
            tv_ssr1.setTextSize(20);
            tv_ssr1.setBackgroundColor(getResources().getColor(R.color.darkGray));
            tv_ssr1.setGravity(Gravity.CENTER_HORIZONTAL);
            tv_ssr1.setLayoutParams(layoutParams2);
            linearLayout4.addView(tv_ssr1);

            TextView tv_ssr2 = new TextView(MainActivity.this);
            tv_ssr2.setText("SIGN OFF");
            tv_ssr2.setTextColor(Color.WHITE);
            tv_ssr2.setTextSize(20);
            tv_ssr2.setBackgroundColor(getResources().getColor(R.color.darkGray));
            tv_ssr2.setGravity(Gravity.CENTER_HORIZONTAL);
            tv_ssr2.setLayoutParams(layoutParams2);
            linearLayout4.addView(tv_ssr2);

            TextView tv_ssr3 = new TextView(MainActivity.this);
            tv_ssr3.setText("DAYS");
            tv_ssr3.setTextColor(Color.WHITE);
            tv_ssr3.setTextSize(20);
            tv_ssr3.setBackgroundColor(getResources().getColor(R.color.darkGray));
            tv_ssr3.setGravity(Gravity.CENTER_HORIZONTAL);
            tv_ssr3.setLayoutParams(layoutParams2);
            linearLayout4.addView(tv_ssr3);

            TextView tv_ssr4 = new TextView(MainActivity.this);
            tv_ssr4.setText("VESSEL");
            tv_ssr4.setTextColor(Color.WHITE);
            tv_ssr4.setTextSize(20);
            tv_ssr4.setBackgroundColor(getResources().getColor(R.color.darkGray));
            tv_ssr4.setGravity(Gravity.CENTER_HORIZONTAL);
            tv_ssr4.setLayoutParams(layoutParams2);
            linearLayout4.addView(tv_ssr4);

            TextView tv_ssr5 = new TextView(MainActivity.this);
            tv_ssr5.setText("IMO NO.");
            tv_ssr5.setTextColor(Color.WHITE);
            tv_ssr5.setTextSize(20);
            tv_ssr5.setBackgroundColor(getResources().getColor(R.color.darkGray));
            tv_ssr5.setGravity(Gravity.CENTER_HORIZONTAL);
            tv_ssr5.setLayoutParams(layoutParams2);
            linearLayout4.addView(tv_ssr5);

            linear_sea_service.addView(linearLayout4);

            int cnt2 = db.GetCount("shipboard", "");
            int total = 0;
            if(cnt2 > 0){
                List<Shipboard> shipboards = db.getShipboard(person_id);
                for(Shipboard shipboard : shipboards){
                    LinearLayout linearLayout5 = new LinearLayout(MainActivity.this);
                    linearLayout5.setLayoutParams(layoutParams1);
                    linearLayout5.setBackgroundResource(R.drawable.border1dp);

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");

                    String sign_on = "";
                    String sign_off = "";

                    String vessel = "";
                    String imo_number = "";


                    sign_on = shipboard.getSign_on();
                    sign_off = shipboard.getSign_off();
                    vessel_id = shipboard.getVessel_id();
                    vessel = db.getFieldString("name_vessel","vessel_id = '"+vessel_id+"'","vessel");
                    imo_number = shipboard.getImo_number();
                    String sign_on_ = "";
                    String sign_off_ = "";
                    try {
                        Date date = format.parse(sign_on);
                        sign_on_ = format2.format(date);
                        date = format.parse(sign_off);
                        sign_off_ = format2.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date c = Calendar.getInstance().getTime(); //get current date
                    String dateTo = sdFormat.format(c);  //format date

                    if(sign_on.equals("")){
                        int yr = Calendar.getInstance().get(Calendar.YEAR);
                        sign_on = yr + "-01-01";

                    }
                    Date startDateObj = null;
                    Date endDateObj = null;

                    try {
                        startDateObj = sdFormat.parse(sign_on + " 00:00:01");
                        if(sign_off.equals("")){
                            endDateObj = sdFormat.parse(dateTo + " 24:00:00");
                        }else{
                            endDateObj = sdFormat.parse(sign_off + " 24:00:00");
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    timeDiff = endDateObj.getTime() - startDateObj.getTime();
                    daysDiff = timeDiff/(1000*60*60*24);

                    long days = 0;
                    if(daysDiff > 0){
                        days = daysDiff;
                        total += days;
                    }

                    TextView tv_ssr1_1 = new TextView(MainActivity.this);
                    tv_ssr1_1.setText(sign_on_);
                    tv_ssr1_1.setTextColor(Color.BLACK);
                    tv_ssr1_1.setTextSize(20);
                    tv_ssr1_1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv_ssr1_1.setLayoutParams(layoutParams2);
                    linearLayout5.addView(tv_ssr1_1);

                    TextView tv_ssr2_1 = new TextView(MainActivity.this);
                    tv_ssr2_1.setText(sign_off_);
                    tv_ssr2_1.setTextColor(Color.BLACK);
                    tv_ssr2_1.setTextSize(20);
                    //tv_ssr2_1.setBackgroundResource(R.drawable.border);
                    tv_ssr2_1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv_ssr2_1.setLayoutParams(layoutParams2);
                    linearLayout5.addView(tv_ssr2_1);

                    TextView tv_ssr3_1 = new TextView(MainActivity.this);
                    tv_ssr3_1.setText("" + days);
                    tv_ssr3_1.setTextColor(Color.BLACK);
                    tv_ssr3_1.setTextSize(20);
                    // tv_ssr3_1.setBackgroundResource(R.drawable.border);
                    tv_ssr3_1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv_ssr3_1.setLayoutParams(layoutParams2);
                    linearLayout5.addView(tv_ssr3_1);

                    TextView tv_ssr4_1 = new TextView(MainActivity.this);
                    tv_ssr4_1.setText(vessel);
                    tv_ssr4_1.setTextColor(Color.BLACK);
                    tv_ssr4_1.setTextSize(20);
                    //tv_ssr4_1.setBackgroundResource(R.drawable.border);
                    tv_ssr4_1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv_ssr4_1.setLayoutParams(layoutParams2);
                    linearLayout5.addView(tv_ssr4_1);

                    TextView tv_ssr5_1 = new TextView(MainActivity.this);
                    tv_ssr5_1.setText(imo_number);
                    tv_ssr5_1.setTextColor(Color.BLACK);
                    tv_ssr5_1.setTextSize(20);
                    tv_ssr5_1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv_ssr5_1.setLayoutParams(layoutParams2);
                    linearLayout5.addView(tv_ssr5_1);
                    linear_sea_service.addView(linearLayout5);
                }

                LinearLayout linear_tasks = findViewById(R.id.linear_tasks);

                //TASKS FOR COMPLETION
                TextView tv_task_title = new TextView(MainActivity.this);
                tv_task_title.setLayoutParams(layoutParams1);
                tv_task_title.setText("TASKS COMPLETION PER FUNCTION");
                tv_task_title.setTextColor(Color.WHITE);
                tv_task_title.setTextSize(20);
                tv_task_title.setGravity(Gravity.CENTER_HORIZONTAL);
                tv_task_title.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                linear_tasks.addView(tv_task_title);

                LinearLayout linearLayout6 = new LinearLayout(MainActivity.this);
                linearLayout6.setLayoutParams(layoutParams1);

                LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2);
                final TextView tv_task1 = new TextView(MainActivity.this);
                tv_task1.setText("FUNCTION");
                tv_task1.setTextColor(Color.WHITE);
                tv_task1.setTextSize(20);
                tv_task1.setBackgroundResource(R.drawable.border_dark_gray2);
                tv_task1.setGravity(Gravity.CENTER_HORIZONTAL);
                tv_task1.setLayoutParams(layoutParams3);
                tv_task1.setPadding(5,5,5,5);
                linearLayout6.addView(tv_task1);

                final TextView tv_task2 = new TextView(MainActivity.this);
                tv_task2.setText("NO. OF TASKS");
                tv_task2.setTextColor(Color.WHITE);
                tv_task2.setTextSize(20);
                tv_task2.setBackgroundResource(R.drawable.border_dark_gray2);
                tv_task2.setGravity(Gravity.CENTER_HORIZONTAL);
                tv_task2.setLayoutParams(layoutParams2);
                tv_task2.setPadding(5,5,5,5);
                linearLayout6.addView(tv_task2);

                final TextView tv_task3 = new TextView(MainActivity.this);
                tv_task3.setText("TASKS DONE");
                tv_task3.setTextColor(Color.WHITE);
                tv_task3.setTextSize(20);
                tv_task3.setBackgroundResource(R.drawable.border_dark_gray2);
                tv_task3.setGravity(Gravity.CENTER_HORIZONTAL);
                tv_task3.setLayoutParams(layoutParams2);
                tv_task3.setPadding(5,5,5,5);
                linearLayout6.addView(tv_task3);

                final TextView tv_task4 = new TextView(MainActivity.this);
                tv_task4.setText("SUBMITTED");
                tv_task4.setTextColor(Color.WHITE);
                tv_task4.setTextSize(20);
                tv_task4.setBackgroundResource(R.drawable.border_dark_gray2);
                tv_task4.setGravity(Gravity.CENTER_HORIZONTAL);
                tv_task4.setLayoutParams(layoutParams2);
                tv_task4.setPadding(5,5,5,5);
                linearLayout6.addView(tv_task4);

                final TextView tv_task5 = new TextView(MainActivity.this);
                tv_task5.setText("N/A");
                tv_task5.setTextColor(Color.WHITE);
                tv_task5.setTextSize(20);
                tv_task5.setBackgroundResource(R.drawable.border_dark_gray2);
                tv_task5.setGravity(Gravity.CENTER_HORIZONTAL);
                tv_task5.setLayoutParams(layoutParams2);
                tv_task5.setPadding(5,5,5,5);
                linearLayout6.addView(tv_task5);

                final TextView tv_task6 = new TextView(MainActivity.this);
                tv_task6.setText("PENDING");
                tv_task6.setTextColor(Color.WHITE);
                tv_task6.setTextSize(20);
                tv_task6.setBackgroundResource(R.drawable.border_dark_gray2);
                tv_task6.setGravity(Gravity.CENTER_HORIZONTAL);
                tv_task6.setLayoutParams(layoutParams2);
                tv_task6.setPadding(5,5,5,5);
                linearLayout6.addView(tv_task6);

                linear_tasks.addView(linearLayout6);
                int overall_total = 0;
                List<TrbFunction> trbFunctions = db.getFunctions(dept, "vessel_type_id='' AND dp='' AND ice_class = ''");
                for (TrbFunction functions : trbFunctions){
                    LinearLayout linearLayout7 = new LinearLayout(MainActivity.this);
                    linearLayout7.setLayoutParams(layoutParams1);
                    linearLayout7.setBackgroundResource(R.drawable.border1dp);

                    final TextView tv_task1_1 = new TextView(MainActivity.this);
                    tv_task1_1.setText(functions.getDesc_trb_function());
                    tv_task1_1.setTextColor(Color.BLACK);
                    tv_task1_1.setTextSize(20);
                    tv_task1_1.setGravity(Gravity.LEFT);
                    tv_task1_1.setLayoutParams(layoutParams3);
                    tv_task1_1.setPadding(5,0,5,10);
                    linearLayout7.addView(tv_task1_1);

                    int task_cnt = db.GetCount("task", " WHERE trb_competence_id IN (SELECT trb_competence_id FROM trb_competence WHERE trb_function_id = '"+functions.getTrb_function_id()+"')");
                    int task_done = db.GetCount("task", " WHERE trb_competence_id IN (SELECT trb_competence_id FROM trb_competence WHERE trb_function_id = '"+functions.getTrb_function_id()+"') AND task_id IN (SELECT task_id FROM person_task WHERE passed = 'Y')");
                    int for_approval = db.GetCount("task", " WHERE trb_competence_id IN (SELECT trb_competence_id FROM trb_competence WHERE trb_function_id = '"+functions.getTrb_function_id()+"') AND task_id IN (SELECT task_id FROM person_task WHERE (completed != '' AND completed != '0000-00-00') AND (passed != 'Y'))");
                    int na = db.GetCount("task", " WHERE trb_competence_id IN (SELECT trb_competence_id FROM trb_competence WHERE trb_function_id = '"+functions.getTrb_function_id()+"') AND task_id IN (SELECT task_id FROM person_task WHERE not_app = 'Y')");
                    overall_total += task_cnt;
                    int pending = task_cnt - task_done;

                    final TextView tv_task2_1 = new TextView(MainActivity.this);
                    tv_task2_1.setText("" + task_cnt);
                    tv_task2_1.setTextColor(Color.BLACK);
                    tv_task2_1.setTextSize(20);
                    //tv_task2_1.setBackgroundResource(R.drawable.border);
                    tv_task2_1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv_task2_1.setLayoutParams(layoutParams2);
                    tv_task2_1.setPadding(5,0,5,5);
                    linearLayout7.addView(tv_task2_1);

                    final TextView tv_task3_1 = new TextView(MainActivity.this);
                    tv_task3_1.setText("" + task_done);
                    tv_task3_1.setTextColor(Color.BLACK);
                    tv_task3_1.setTextSize(20);
                    //tv_task3_1.setBackgroundResource(R.drawable.border);
                    tv_task3_1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv_task3_1.setLayoutParams(layoutParams2);
                    tv_task3_1.setPadding(5,0,5,0);
                    linearLayout7.addView(tv_task3_1);

                    final TextView tv_task4_1 = new TextView(MainActivity.this);
                    tv_task4_1.setText("" + for_approval);
                    tv_task4_1.setTextColor(Color.BLACK);
                    tv_task4_1.setTextSize(20);
                    //tv_task4_1.setBackgroundResource(R.drawable.border);
                    tv_task4_1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv_task4_1.setLayoutParams(layoutParams2);
                    tv_task4_1.setPadding(5,0,5,0);
                    linearLayout7.addView(tv_task4_1);

                    final TextView tv_task5_1 = new TextView(MainActivity.this);
                    tv_task5_1.setText("" + na);
                    tv_task5_1.setTextColor(Color.BLACK);
                    tv_task5_1.setTextSize(20);
                    //tv_task5_1.setBackgroundResource(R.drawable.border);
                    tv_task5_1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv_task5_1.setLayoutParams(layoutParams2);
                    tv_task5_1.setPadding(5,0,5,0);
                    linearLayout7.addView(tv_task5_1);

                    final TextView tv_task6_1 = new TextView(MainActivity.this);
                    tv_task6_1.setText("" + pending);
                    tv_task6_1.setTextColor(Color.BLACK);
                    tv_task6_1.setTextSize(20);
                    //tv_task6_1.setBackgroundResource(R.drawable.border);
                    tv_task6_1.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv_task6_1.setLayoutParams(layoutParams2);
                    tv_task6_1.setPadding(5,0,5,0);
                    linearLayout7.addView(tv_task6_1);


                    linear_tasks.addView(linearLayout7);
                }

                if(!vessel_id.equals("")){
                    String vessel_type_id = db.getFieldString("vessel_type_id", "vessel_id = '"+vessel_id+"'", "vessel");
                    trbFunctions = db.getFunctions(dept, "vessel_type_id='"+vessel_type_id+"' AND dp='' AND ice_class = ''");
                    for (TrbFunction functions : trbFunctions){
                        LinearLayout linearLayout7 = new LinearLayout(MainActivity.this);
                        linearLayout7.setLayoutParams(layoutParams1);
                        linearLayout7.setBackgroundResource(R.drawable.border1dp);

                        final TextView tv_task1_1 = new TextView(MainActivity.this);
                        tv_task1_1.setText(functions.getDesc_trb_function());
                        tv_task1_1.setTextColor(Color.BLACK);
                        tv_task1_1.setTextSize(20);
                        tv_task1_1.setGravity(Gravity.LEFT);
                        tv_task1_1.setLayoutParams(layoutParams3);
                        tv_task1_1.setPadding(5,0,5,10);
                        linearLayout7.addView(tv_task1_1);

                        int task_cnt = db.GetCount("task", " WHERE trb_competence_id IN (SELECT trb_competence_id FROM trb_competence WHERE trb_function_id = '"+functions.getTrb_function_id()+"')");
                        int task_done = db.GetCount("task", " WHERE trb_competence_id IN (SELECT trb_competence_id FROM trb_competence WHERE trb_function_id = '"+functions.getTrb_function_id()+"') AND task_id IN (SELECT task_id FROM person_task WHERE passed = 'Y')");
                        int for_approval = db.GetCount("task", " WHERE trb_competence_id IN (SELECT trb_competence_id FROM trb_competence WHERE trb_function_id = '"+functions.getTrb_function_id()+"') AND task_id IN (SELECT task_id FROM person_task WHERE (completed != '' AND completed != '0000-00-00') AND (passed != 'Y'))");
                        int na = db.GetCount("task", " WHERE trb_competence_id IN (SELECT trb_competence_id FROM trb_competence WHERE trb_function_id = '"+functions.getTrb_function_id()+"') AND task_id IN (SELECT task_id FROM person_task WHERE not_app = 'Y')");
                        overall_total += task_cnt;
                        int pending = task_cnt - task_done;

                        final TextView tv_task2_1 = new TextView(MainActivity.this);
                        tv_task2_1.setText("" + task_cnt);
                        tv_task2_1.setTextColor(Color.BLACK);
                        tv_task2_1.setTextSize(20);
                        //tv_task2_1.setBackgroundResource(R.drawable.border);
                        tv_task2_1.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv_task2_1.setLayoutParams(layoutParams2);
                        tv_task2_1.setPadding(5,0,5,5);
                        linearLayout7.addView(tv_task2_1);

                        final TextView tv_task3_1 = new TextView(MainActivity.this);
                        tv_task3_1.setText("" + task_done);
                        tv_task3_1.setTextColor(Color.BLACK);
                        tv_task3_1.setTextSize(20);
                        //tv_task3_1.setBackgroundResource(R.drawable.border);
                        tv_task3_1.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv_task3_1.setLayoutParams(layoutParams2);
                        tv_task3_1.setPadding(5,0,5,0);
                        linearLayout7.addView(tv_task3_1);

                        final TextView tv_task4_1 = new TextView(MainActivity.this);
                        tv_task4_1.setText("" + for_approval);
                        tv_task4_1.setTextColor(Color.BLACK);
                        tv_task4_1.setTextSize(20);
                        //tv_task4_1.setBackgroundResource(R.drawable.border);
                        tv_task4_1.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv_task4_1.setLayoutParams(layoutParams2);
                        tv_task4_1.setPadding(5,0,5,0);
                        linearLayout7.addView(tv_task4_1);

                        final TextView tv_task5_1 = new TextView(MainActivity.this);
                        tv_task5_1.setText("" + na);
                        tv_task5_1.setTextColor(Color.BLACK);
                        tv_task5_1.setTextSize(20);
                        //tv_task5_1.setBackgroundResource(R.drawable.border);
                        tv_task5_1.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv_task5_1.setLayoutParams(layoutParams2);
                        tv_task5_1.setPadding(5,0,5,0);
                        linearLayout7.addView(tv_task5_1);

                        final TextView tv_task6_1 = new TextView(MainActivity.this);
                        tv_task6_1.setText("" + pending);
                        tv_task6_1.setTextColor(Color.BLACK);
                        tv_task6_1.setTextSize(20);
                        //tv_task6_1.setBackgroundResource(R.drawable.border);
                        tv_task6_1.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv_task6_1.setLayoutParams(layoutParams2);
                        tv_task6_1.setPadding(5,0,5,0);
                        linearLayout7.addView(tv_task6_1);
                        linear_tasks.addView(linearLayout7);
                    }
                }
            }


        }else{ //NO DATA YET
            main_container.removeAllViews();
            if(!isNetworkAvailable()){
                Toast.makeText(this, "NO INTERNET ACCESS! MAKE SURE YOU ARE CONNECTED TO A STABLE CONNECTION.", Toast.LENGTH_SHORT).show();
            }

            LinearLayout layout1 = new LinearLayout(this);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.setMargins(20, 120, 20, 10);
            layout1.setOrientation(LinearLayout.VERTICAL);
            layout1.setPadding(20, 10, 20, 10);
            layout1.setLayoutParams(params1);

            TextView textView = new TextView(this);
            textView.setPadding(20, 10, 20, 20);
            textView.setText("");
            textView.setTextColor(Color.parseColor("#ed071a"));
            textView.setTextSize(25);
            textView.setGravity(Gravity.CENTER);
            layout1.addView(textView);

            textView = new TextView(this);
            textView.setPadding(10, 10, 10, 10);
            textView.setText("*** Download data from cloud by clicking the button below ***");
            textView.setTextColor(Color.parseColor("#ed071a"));
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            layout1.addView(textView);

            Drawable icon = context.getResources().getDrawable(R.drawable.ic_cloud_download_white_24dp);
            icon.setBounds(20, 0, 0, 0 );
            Button btn_download = new Button(this);
            btn_download.setPadding(20, 10, 20, 20);
            btn_download.setText("DOWNLOAD NOW");
            btn_download.setTextColor(Color.parseColor("#FFFFFF"));
            btn_download.setBackgroundColor(Color.parseColor("#000000"));
            btn_download.setTextSize(30);
            btn_download.setGravity(Gravity.CENTER_HORIZONTAL);
            btn_download.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null );
            layout1.addView(btn_download);

            btn_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), DownloadFromServerActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            main_container.addView(layout1);
        }

        //*** CREATE DIRECTORY HERE ***//
        File myFile = new File(getApplicationContext().getExternalFilesDir("eCADET").getAbsolutePath());

        if(!myFile.exists()){
            //Toast.makeText( MainActivity.this, "File Not Found. Make sure the file is located on device's storage.", Toast.LENGTH_SHORT ).show();
            Log.d("RESULT", "create folder");
            createFolder();
        }else{
            Log.d("RESULT", "folder existed");
        }
    }

    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
            File myFile = new File( Environment.getExternalStorageDirectory() + "/eCADET");

            if(!myFile.exists()){
                //Toast.makeText( MainActivity.this, "File Not Found. Make sure the file is located on device's storage.", Toast.LENGTH_SHORT ).show();
                createFolder();
            }else{
                Log.d("RESULT", "folder existed");
            }
        }
    }

    private void createFolder(){
        File file = new File(getApplicationContext().getExternalFilesDir("eCADET").getAbsolutePath());


        if (!file.exists()) {
            file.mkdirs();
            Log.d("RESULT", "created folder" + file);
        }
    }




    private boolean isNetworkAvailable() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setMessage("Are you sure you want to quit?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                }
            });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //finish();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.black));
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.black));
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }
    }


}
