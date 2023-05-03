package com.elosoftbiz.etrb_trmf;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.navigation.NavigationView;

public class OTRActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    PDFView pdfView;
    String person_id, dept;
    DatabaseHelper db;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otractivity);

        context = this;
        db = new DatabaseHelper(context);
        /*** LOGO AND HEADER NAME HERE ***/
        mToolbar = findViewById( R.id.nav_action );
        setSupportActionBar( mToolbar );
        mDrawerLayout = findViewById( R.id.drawerLayout );
        mToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.open, R.string.close );
        mDrawerLayout.addDrawerListener( mToggle );
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        /*** END OF LOGO AND HEADER NAME ***/

        person_id = db.getCadetId();
        dept = db.getFieldString("dept","person_id = '"+person_id+"'", "person");


        /****** START MENU *******/
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if(dept.equals("DECK")){
            new MenuDeck(navigationView, this, mDrawerLayout);
        }else{
            new MenuEngine(navigationView, this, mDrawerLayout);
        }
        /****** END MENU *******/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OTRActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}