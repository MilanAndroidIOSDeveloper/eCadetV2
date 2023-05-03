package com.elosoftbiz.etrb_trmf;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.navigation.NavigationView;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class PersonNarrativeActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    DatabaseHelper db;
    String person_id, dept, task_id, participation, officer_id, person_task_id, task_type;
    TextView tvDescTask;
    List<String> partArray =  new ArrayList<>();
    List<Person> persons;
    List<String> personArray =  new ArrayList<>();
    Spinner spinner, spinnerSupervisor;
    TextView tv_guide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_narrative);

        context = this;

        /* LOGO AND HEADER NAME HERE */
        mToolbar = findViewById( R.id.nav_action );
        setSupportActionBar( mToolbar );
        mDrawerLayout = findViewById( R.id.drawerLayout );
        mToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.open, R.string.close );
        mDrawerLayout.addDrawerListener( mToggle );
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        /* END OF LOGO AND HEADER NAME ***/

        db = new DatabaseHelper(this);
        person_id = db.getCadetId();
        dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");
        /* START MENU *******/
        navigationView = findViewById(R.id.navigation_view);

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

        partArray.add("- Level of Participation -");
        partArray.add("Guided");
        partArray.add("Instructed");
        partArray.add("Supervised");

        ArrayAdapter arrayAdapter_part = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, partArray);
        arrayAdapter_part.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter_part);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                participation  = parent.getItemAtPosition(position).toString();
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.black));

                textView.setTextSize(15);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        persons = db.getPersons(" vessel_officer = 'Y'"); //POPULATE VESSEL SPINNER
        personArray.add("Select Supervisor *");

        for (Person row : persons) {
            personArray.add(row.getFull_name());
        }

        ArrayAdapter arrayAdapter_officer = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, personArray);
        arrayAdapter_officer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerSupervisor = findViewById(R.id.spinnerSupervisor);
        spinnerSupervisor.setAdapter(arrayAdapter_officer);
        spinnerSupervisor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                officer_id  = parent.getItemAtPosition(position).toString();
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(18);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /* END MENU *******/
        Intent intent = getIntent();
        task_id = intent.getStringExtra("task_id");
        person_task_id = intent.getStringExtra("person_task_id");
        Log.d("RESULT", person_task_id);
        task_type = intent.getStringExtra("task_type");

        String desc_task = db.getFieldString("desc_task", " task_id = '"+task_id+"'", "task");
        desc_task = URLDecoder.decode(desc_task);
        tvDescTask = findViewById(R.id.tvDescTask);
        tvDescTask.setText(desc_task);

        tv_guide  = findViewById(R.id.tv_guide);
        tv_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ScrollView scrollView = new ScrollView(context);
                scrollView.setLayoutParams(layoutParams);
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setOrientation(LinearLayout.VERTICAL);


                TextView textView = new TextView(context);
                textView.setText("Cadet Guidance");
                textView.setPadding(20,20, 20,10);
                textView.setTextColor(getColor(R.color.black));
                textView.setTextSize(20);

                linearLayout.addView(textView);
                /*
                textView = new TextView(context);
                textView.setText("Definition of OBT Narrative Monitoring Report: is an account of shipboard training/assessment experience of an ongoing or accomplished task that is extracted from the cadet Training Record Book or as part of the 6 months Watchkeeping activity presented through a sequence of written (or spoken) words, still or moving images, or any combination of these.\n\n" +
                        "Part A, fill the following: Activity area, Level of Participation, Supervisor, Pictures related to the task, Equipment particular, Initial condition, Alignment to course in the curriculum?, How the course (in the MHEI) could be improved?\n\n"+
                        "Part B - Narrative:\n" +
                        "Yesterday 31 May 2022 at 0900 hours, I was ordered by the 1st Engineer to prepare the Main Engine for departure, Sulzer 6RND 90 with 2160 kW per cylinder.\n" +
                        "The Jacket Water cooling pump is already running and maintained at 70oC inlet temperature by steam heating and continuously re-circulated to the engine, so I started first the Lubricating Oil pump 1 and set L.O. pump 2 to stand-by; this is to ensure that if L.O. pump 1 failed, L.O. pump 2 automatically starts and take over to supply lubricating oil to the main engine. \n" +
                        "Then, I checked the status of the starting air bottles which was 24 bars and 26 bars respectively, Main Air Compressor 1 is set at auto and number 2 is also on auto on a leading/lagging role configuration. I drained the air bottles of condensate and opened the outlet valve of Starting Air Bottle 1; I then checked the main engine indicator cocks to make sure they are open. \n" +
                        "I called the bridge informing them that I am preparing the engine for departure and asked whether the vessel is securely fastened, and if the propeller is free to turn or that there are no personnel in the vicinity as I intended to turn the engine by air pressure….\n\n" +
                        "Note to cadet:\n\n" +
                        "1.This is just an example, the actual procedure to be followed is the one written in the vessel SMS manual.\n" +
                        "2.Participation nomenclature\n" +
                        "a.Observer: Cadet is a spectator, not an official party to the activity\n" +
                        "b.Assistant: Cadet assists another crew in performing a task\n" +
                        "c.Lead (supervised): Cadet leads an activity in performing a task, supervised by an engineer. (E.g., Cadet measures the cylinder liner with the wiper assisting)\n" +
                        "d.Solo (supervised): Cadet performs the task alone but under supervision of an engineer. (E.g., preparation of the main engine for departure as above).\n" +
                        "3.Preparing the engine for departure involve a lot of steps including but not limited to. \n" +
                        "a.\tStarting piston cooling water pump (Sulzer RND) \n" +
                        "b.\tStarting the Fuel circulating pump to be followed by the Fuel feed pump \n" +
                        "c.\tStarting additional generating set and synchronize to bus \n" +
                        "d.\tInspecting and testing the steering gear, etcetera.\n" +
                        "4.\tThe narrative report ends when the task is completed, in this case, the Engineer on Watch informed the Bridge that the engine is ready for departure. \n\n" +
                        "Note to MHEI\n" +
                        "1.\tThis sample should be part of the orientation of cadets prior to embarkation.\n" +
                        "2.\tParagraphs written in blue are not part of the OBT narrative report\n" +
                        "3.\tWhat should be written in the narrative report?\n" +
                        "a.\tAs a minimum, all steps written in the vessel SMS for that particular task (engine preparation for departure) is to be written, plus any activities that he did that may have interrupted his engine preparation; example, he was ordered to also give the sounding of the bunker tanks 1 & 2, or that he found that the seal of the JCWP was leaking and repaired it before continuing with the preparation.  \n" +
                        "b.\tThe reflection area is optional, but it is a good feedback mechanism that MHEI may use to their advantage in improving their curriculum and becoming aware of the challenges face by their students onboard. This forces the student to think critically to answer the following questions.\n" +
                        "i.\tIs there alignment between this activity and what I learned in the school?\n" +
                        "ii.\tWhat recommendations I could write to improve our curriculum?\n" +
                        "4.\tWhy is the activity in this format?  \n" +
                        "a.\tThe first part is fill in the blank, teach the student how to accomplish forms.\n" +
                        "b.\tThe second part in essay/narrative format to improve the learner’s communication skills and force him to properly articulate what he is doing into written words (and would do so again during OBT assessment in oral form), unconsciously acquiring expertise in the English language, and the following STCW competence.\n" +
                        "\n" +
                        "Table A-III/1 Function: Marine Engineering at the Operational Level\n" +
                        "Competence: Use English in written and oral form\n" +
                        "KUP: Adequate knowledge of the English language to enable the officer to use engineering publications and to perform engineering duties\n" +
                        "Criteria for evaluating competence: Communications are clear and understood\n");
                textView.setPadding(20,20, 20,10);
                textView.setTextColor(getColor(R.color.black));
                textView.setTextSize(18);
                linearLayout.addView(textView);
                */

                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.narrative_report1);
                imageView.setAdjustViewBounds(true);
                linearLayout.addView(imageView);

                imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.narrative_report2);
                imageView.setAdjustViewBounds(true);
                linearLayout.addView(imageView);

                imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.narrative_report3);
                imageView.setAdjustViewBounds(true);
                linearLayout.addView(imageView);

                scrollView.addView(linearLayout);
                alertDialogBuilder.setView(scrollView);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black));

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, TaskUpdateActivity.class);
        intent.putExtra("person_tak_id", person_task_id);
        intent.putExtra("task_type", task_type);
        startActivity(intent);
        finish();
    }
}