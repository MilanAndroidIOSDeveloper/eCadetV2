package com.elosoftbiz.etrb_trmf;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.net.URLDecoder;

public class ForAppTaskFormActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tvFunction, tvCompetence, tvKUP, tvTaskGrup, tvTask, tvCriteria;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;
    String person_id, vessel_id, dept, person_task_id;
    String str = "", err_message, upLoadServerUri;
    HttpResponse response;
    int serverResponseCode = 0;
    JSONObject json = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_app_task_form);

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

        tvFunction = findViewById(R.id.tvFunction);
        tvCompetence = findViewById(R.id.tvCompetence);
        tvKUP = findViewById(R.id.tvKUP);
        tvTaskGrup = findViewById(R.id.tvTaskGroup);
        tvTask = findViewById(R.id.tvTask);
        tvCriteria = findViewById(R.id.tvCriteria);

        Intent intent = getIntent();
        if (intent.hasExtra("person_task_id")) {
            person_task_id = intent.getStringExtra("person_task_id");
        }
        String task_id = db.getFieldString("task_id", " person_task_id = '"+person_task_id+"'", "person_task");
        String trb_function_id = db.getFieldString("trb_function_id", " task_id = '"+task_id+"'", "task");
        String desc_trb_function = db.getFieldString("desc_trb_function", "trb_function_id = '"+trb_function_id+"'", "trb_function");
        desc_trb_function = URLDecoder.decode(desc_trb_function);
        String trb_competence_id = db.getFieldString("trb_competence_id", " task_id = '"+task_id+"'", "task");
        String desc_competence =  db.getFieldString("desc_competence", " trb_competence_id = '"+trb_competence_id+"'", "trb_competence");
        desc_competence = URLDecoder.decode(desc_competence);
        String trb_topic_id = db.getFieldString("trb_topic_id", " task_id = '"+task_id+"'", "task");
        String desc_topic = db.getFieldString("desc_topic", " trb_topic_id = '"+trb_topic_id+"'", "trb_topic");
        desc_topic = URLDecoder.decode(desc_topic);
        desc_topic = URLDecoder.decode(desc_topic);
        String trb_task_group_id = db.getFieldString("trb_task_group_id", " task_id = '"+task_id+"'", "task");
        String desc_task_group = db.getFieldString("desc_task_group", " trb_task_group_id = '"+trb_task_group_id+"'", "trb_task_group");
        desc_task_group = URLDecoder.decode(desc_task_group);
        String desc_task = db.getFieldString("desc_task", " task_id = '"+task_id+"'", "task");
        desc_task = URLDecoder.decode(desc_task);
        String criteria = db.getFieldString("criteria", " task_id = '"+task_id+"'", "task");
        criteria = URLDecoder.decode(criteria);
        tvTask.setText("SUB-TASK : "+desc_task);
        tvFunction.setText("FUNCTION : " + desc_trb_function);
        tvCompetence.setText("COMPETENCE : "+desc_competence);
        tvKUP.setText("KUP : "+ desc_topic);
        tvTaskGrup.setText("TASK : "+desc_task_group);
        tvCriteria.setText("STANDARD CRITERIA FOR THIS TASK\n"+criteria);
    }
}