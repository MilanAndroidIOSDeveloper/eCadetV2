package com.elosoftbiz.etrb_trmf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

public class SearchTaskActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;

    DatabaseHelper db;
    TextView result;
    Button btn_search;
    ProgressDialog pd;
    SearchView searchView;
    ScrollView scrollView;

    TableLayout tableLayout;
    TableRow tableRow;
    ProgressDialog progresRing;
    String query, person_id, dept;
    TableRow.LayoutParams layoutParams1;
    List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_task);

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

        db = new DatabaseHelper(SearchTaskActivity.this);
        person_id = db.getCadetId();
        dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");
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

        searchView = findViewById(R.id.searchView);
        scrollView = findViewById(R.id.main_container);
        result = findViewById(R.id.result);
        btn_search = findViewById(R.id.btn_search);

        tableLayout = new TableLayout(SearchTaskActivity.this);
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10,10,10,20);
        tableLayout.setLayoutParams(layoutParams);
        tableLayout.setStretchAllColumns(true);

        layoutParams1 = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.setMargins(10,10,10,20);

        tableRow = new TableRow(SearchTaskActivity.this);
        tableRow.setBackgroundColor(Color.parseColor("#000000"));
        tableRow.setLayoutParams(layoutParams1);

        TextView textView1 = new TextView(SearchTaskActivity.this);
        textView1.setText("Task");
        textView1.setTextColor(Color.parseColor("#FFFFFF"));
        textView1.setGravity(Gravity.CENTER_HORIZONTAL);
        textView1.setLayoutParams(layoutParams1);
        textView1.setPadding(10,10,10,10);
        textView1.setTextSize(18);
        textView1.setWidth(30);
        tableRow.addView(textView1, 0);

        textView1 = new TextView(SearchTaskActivity.this);
        textView1.setText("Competence");
        textView1.setTextColor(Color.parseColor("#FFFFFF"));
        textView1.setGravity(Gravity.CENTER_HORIZONTAL);
        textView1.setLayoutParams(layoutParams1);
        textView1.setPadding(10,10,10,10);
        textView1.setTextSize(18);
        tableRow.addView(textView1, 1);

        textView1 = new TextView(SearchTaskActivity.this);
        textView1.setText("Sub-Competence");
        textView1.setTextColor(Color.parseColor("#FFFFFF"));
        textView1.setGravity(Gravity.CENTER_HORIZONTAL);
        textView1.setLayoutParams(layoutParams1);
        textView1.setPadding(10,10,10,10);
        textView1.setTextSize(18);
        tableRow.addView(textView1, 2);

        tableLayout.addView(tableRow);

        TableRow tableRow1 = new TableRow(SearchTaskActivity.this);
        tableRow1.setLayoutParams(layoutParams1);
        tableRow1.setBackgroundResource(R.drawable.border);

        TextView textView = new TextView(SearchTaskActivity.this);
        textView.setText("Type a keyword to search.");
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setGravity(Gravity.LEFT);
        textView.setLayoutParams(layoutParams1);
        textView.setPadding(10,10,10,10);
        textView.setTextSize(18);

        tableRow1.addView(textView, 0);

        TableRow.LayoutParams the_param;
        the_param = (TableRow.LayoutParams)textView.getLayoutParams();
        the_param.span = 3;
        textView.setLayoutParams(the_param);

        tableLayout.addView(tableRow1);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                scrollView.removeAllViews();
                tableLayout.removeAllViews();
                query = searchView.getQuery().toString();
                if(!query.equals("")){
                    progresRing = ProgressDialog.show(SearchTaskActivity.this, "", "Searching, please wait...", true);
                    progresRing.setCancelable(false);

                    new ProcessData(context).execute();
                }else{

                    LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    TextView textView1 = new TextView(SearchTaskActivity.this);
                    textView1.setText("Type a keyword to search");
                    textView1.setTextColor(Color.parseColor("#000000"));
                    textView1.setGravity(Gravity.LEFT);
                    textView1.setPadding(5,5,5,5);
                    textView1.setTextSize(18);
                    textView1.setLayoutParams(tableRowParams);
                    scrollView.addView(textView1);
                }


            }
        });
    }

    public class ProcessData extends AsyncTask<Void, Void, Void>
    {
        public Context context;

        public ProcessData(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            taskList = db.getTaskWhere(URLEncoder.encode(query));


            return null;
        }
        protected void onPostExecute(Void result)
        {
            int cnt = db.GetCount("task", " WHERE desc_task LIKE '%"+ URLEncoder.encode(query) +"%'");



            if(cnt > 0){
                TableRow tableRow = new TableRow(SearchTaskActivity.this);
                tableRow.setLayoutParams(layoutParams1);
                tableRow.setBackgroundColor(Color.parseColor("#000000"));

                TextView textView1 = new TextView(SearchTaskActivity.this);
                textView1.setText("Task");
                textView1.setTextColor(Color.parseColor("#FFFFFF"));
                textView1.setGravity(Gravity.CENTER_HORIZONTAL);
                textView1.setLayoutParams(layoutParams1);
                textView1.setPadding(5,5,5,5);
                textView1.setTextSize(18);
                tableRow.addView(textView1, 0);

                textView1 = new TextView(SearchTaskActivity.this);
                textView1.setText("Competence");
                textView1.setTextColor(Color.parseColor("#FFFFFF"));
                textView1.setGravity(Gravity.CENTER_HORIZONTAL);
                textView1.setLayoutParams(layoutParams1);
                textView1.setPadding(5,5,5,5);
                textView1.setTextSize(18);
                tableRow.addView(textView1, 1);

                textView1 = new TextView(SearchTaskActivity.this);
                textView1.setText("Sub-Competence");
                textView1.setTextColor(Color.parseColor("#FFFFFF"));
                textView1.setGravity(Gravity.CENTER_HORIZONTAL);
                textView1.setLayoutParams(layoutParams1);
                textView1.setPadding(5,5,5,5);
                textView1.setTextSize(18);
                tableRow.addView(textView1, 2);

                layoutParams1.setMargins(5,5,5,5);

                tableLayout.addView(tableRow);
                for (Task cn : taskList){
                    tableRow = new TableRow(SearchTaskActivity.this);
                    tableRow.setLayoutParams(layoutParams1);
                    tableRow.setBackgroundResource(R.drawable.border);

                    textView1 = new TextView(SearchTaskActivity.this);
                    textView1.setText(URLDecoder.decode(URLDecoder.decode(cn.getDesc_task())));
                    textView1.setTextColor(Color.parseColor("#000000"));
                    textView1.setGravity(Gravity.LEFT);
                    textView1.setLayoutParams(layoutParams1);
                    textView1.setPadding(5,5,5,5);
                    textView1.setTextSize(18);
                    tableRow.addView(textView1, 0);

                    String desc_comp = db.getFieldString("desc_competence"," trb_competence_id = '"+cn.getTrb_competence_id()+"'","trb_competence");

                    textView1 = new TextView(SearchTaskActivity.this);
                    textView1.setText(URLDecoder.decode(desc_comp));
                    textView1.setTextColor(Color.parseColor("#000000"));
                    textView1.setGravity(Gravity.LEFT);
                    textView1.setLayoutParams(layoutParams1);
                    textView1.setPadding(5,5,5,5);
                    textView1.setTextSize(18);
                    tableRow.addView(textView1, 1);
                    String trb_sub_comp = cn.getTrb_topic_id();
                    String sub_comp = db.getFieldString("desc_topic" , " trb_topic_id = '"+trb_sub_comp+"'", "trb_topic");
                    sub_comp = URLDecoder.decode(sub_comp);
                    sub_comp = URLDecoder.decode(sub_comp);
                    textView1 = new TextView(SearchTaskActivity.this);
                    textView1.setText(sub_comp);
                    textView1.setTextColor(Color.parseColor("#000000"));
                    textView1.setGravity(Gravity.CENTER_HORIZONTAL);
                    textView1.setLayoutParams(layoutParams1);
                    textView1.setPadding(5,5,5,5);
                    textView1.setTextSize(18);
                    tableRow.addView(textView1, 2);

                    tableLayout.addView(tableRow);

                }

                scrollView.addView(tableLayout);
            }else{


                LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView textView1 = new TextView(SearchTaskActivity.this);
                textView1.setText("No related task found. Please try another keyword.");
                textView1.setTextColor(Color.parseColor("#000000"));
                textView1.setGravity(Gravity.LEFT);
                textView1.setPadding(5,5,5,5);
                textView1.setTextSize(18);
                textView1.setLayoutParams(tableRowParams);


                scrollView.addView(textView1);
            }


            progresRing.dismiss();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SearchTaskActivity.this, MainActivity.class);
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
