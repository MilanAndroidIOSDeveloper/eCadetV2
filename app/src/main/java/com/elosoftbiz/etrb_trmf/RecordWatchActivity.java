package com.elosoftbiz.etrb_trmf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class RecordWatchActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;

    TextView tv_title, tv_1, tv_2, tv_3, tv_4, tv_5, tv_6;
    DatabaseHelper db;
    String person_id;
    ProgressDialog pd;
    LinearLayout main_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_watch);

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

        tv_title = findViewById(R.id.tv_title);
        tv_title.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        main_container = findViewById(R.id.main_container);
        tv_1 = findViewById(R.id.tv_1);
        tv_2 = findViewById(R.id.tv_2);
        tv_3 = findViewById(R.id.tv_3);
        db = new DatabaseHelper(this);

        int cnt = db.GetCount("person", "");
        if(cnt > 0){ //WITH DATA
            person_id = db.getCadetId();
            String dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");
            if(dept.equals("ENGINE")){
                tv_1.setVisibility(View.GONE);
                tv_2.setVisibility(View.GONE);
            }
            tv_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RecordWatchActivity.this, BridgeWatchkeepingActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            tv_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RecordWatchActivity.this, SteeringActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            tv_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RecordWatchActivity.this, PortWatchesActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }else{
            tv_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RecordWatchActivity.this, "You are currently using a demo version. Acquire a license to activate this feature.", Toast.LENGTH_LONG).show();
                }
            });

            tv_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RecordWatchActivity.this, "You are currently using a demo version. Acquire a license to activate this feature.", Toast.LENGTH_LONG).show();
                }
            });

            tv_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RecordWatchActivity.this, "You are currently using a demo version. Acquire a license to activate this feature.", Toast.LENGTH_LONG).show();
                }
            });

            tv_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RecordWatchActivity.this, "You are currently using a demo version. Acquire a license to activate this feature.", Toast.LENGTH_LONG).show();
                }
            });

            tv_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RecordWatchActivity.this, "You are currently using a demo version. Acquire a license to activate this feature.", Toast.LENGTH_LONG).show();
                }
            });

            tv_6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RecordWatchActivity.this, "You are currently using a demo version. Acquire a license to activate this feature.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RecordWatchActivity.this, MainActivity.class);
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
