package com.elosoftbiz.etrb_trmf;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ForApprovalActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;
    String person_id, vessel_id, dept;
    String str = "", err_message, upLoadServerUri;
    HttpResponse response;
    int serverResponseCode = 0;
    JSONObject json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_approval);
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

        db = new DatabaseHelper(this);
        person_id = db.getFieldString("person_id"," vessel_officer ='N'","person");
        dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");
        upLoadServerUri = getString(R.string.url) + "upload_files.php";
        /****** START MENU *******/
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
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
        /****** END MENU *******/
        main_container = findViewById(R.id.main_container);
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

    public void display() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        int cnt = db.GetCount("person_task", " WHERE person_id= '"+person_id+"' AND for_app = 'Y' AND completed != ''");
        if(cnt > 0) {
            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(layoutParams);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setBackgroundResource(R.drawable.border_darkblue);

            TextView textView = new TextView(context);
            textView.setText("Description");
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(layoutParams1);
            textView.setPadding(5,5,5,5);
            layout.addView(textView);

            textView = new TextView(context);
            textView.setText("Date Performed");
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(layoutParams1);
            textView.setPadding(5,5,5,5);
            layout.addView(textView);

            textView = new TextView(context);
            textView.setText("");
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(layoutParams1);
            textView.setPadding(5,5,5,5);
            layout.addView(textView);

            main_container.addView(layout);

            List<PersonTask> list = db.getAllPersonTaskWhere(" for_app = 'Y' AND completed != ''");
            for (PersonTask list1 : list){
                layout = new LinearLayout(context);
                layout.setLayoutParams(layoutParams);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.setBackgroundResource(R.drawable.border1dp);
                String desc_task = db.getFieldString("desc_task", "task_id = '"+list1.getTask_id()+"'", "task");
                desc_task = URLDecoder.decode(desc_task);

                textView = new TextView(context);
                textView.setText(desc_task);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(18);
                textView.setLayoutParams(layoutParams1);
                textView.setPadding(5,5,5,5);
                layout.addView(textView);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
                String completed = list1.getCompleted();
                try {
                    Date date = format.parse(completed);
                    completed = format2.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                textView = new TextView(context);
                textView.setText(completed);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(18);
                textView.setLayoutParams(layoutParams1);
                textView.setPadding(5,5,5,5);
                layout.addView(textView);

                textView = new TextView(context);
                textView.setText("View");
                textView.setTextColor(getColor(R.color.link));
                textView.setTextSize(18);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams1);
                textView.setPadding(5,5,5,5);
                textView.setId(list1.getId());
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String person_task_id = db.getFieldString("person_task_id", " id= "+v.getId(), "person_task");
                        Intent intent = new Intent(context, TaskUpdateActivity.class);
                        intent.putExtra("person_task_id", person_task_id);
                        intent.putExtra("type", "approve");
                        startActivity(intent);
                        finish();
                    }
                });
                layout.addView(textView);

                main_container.addView(layout);
            }
        }else{
            TextView textView = new TextView(context);
            textView.setLayoutParams(layoutParams);
            textView.setText("No sub-tasks for approval");
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.BLACK);
            main_container.addView(textView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(context, SeaServiceActivity.class);
                startActivity(intent);
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
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.black));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.black));
    }
}