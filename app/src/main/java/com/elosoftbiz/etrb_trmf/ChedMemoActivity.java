package com.elosoftbiz.etrb_trmf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebView;
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

public class ChedMemoActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    WebView tv_content;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ched_memo);

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


        tv_content = findViewById(R.id.tv_content);
        String text;
        text = "<html><body>" +
                "<h3 align=\"center\" style=\"margin-top:5px;\"><u><b>CHED Memorandum Order Provisions</b></u></h3>" +
                "<p align=\"justify\" style=\"padding:10px;background-color:transparent;\">";
        text+= "The use of GlobalMET TRB is in accordance with <b>CHED Memorandum Order (CMO) No. 20, Series of 2014</b> which provides for the revised implementing guidelines on the approved seagoing service requirement for the conferment to the degree in <b>Bachelor of Science in Marine Transportation (BSMT)</b> and <b>Bachelor of Science in Marine Engineering (BSMarE)</b> Programs. In addition, the GlobalMET Training Record Book provides a means for documented information regarding the various tasks through accompanying workbooks and supplement for specialized ships. This will help the Shipboard Training Officers of the Maritime Higher Education Institutions (HMEI) to validate the accomplished tasks by the cadet (Deck/Engine) on board during their shipboard training. The said CMO specifically provides:</p>";
        text+= "<p align=\"justify\" style=\"margin-left:30px;margin-right:30px;\"><b>\"Section 9. Requirement to Ensure Authenticity of the TRB</b><br/><br/>" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MHEIS shall at all times be responsible to ensure that only approved TRB (i.e. ISF, <b>Global Met</b> and other approved TRB) shall be issued to the cadets/students undergoing twelve (12) months seagoing service. Hence, in order to ensure the authenticity of the TRB that would be presented by the cadets/students after completing the required seagoing service, and for purposes of validation, assessment and issuance of Special Order (SO) for graduation, all MHEls shall ensure that:</p>";

        text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:50px;\"><br/>";
        text += "A. All pages of the TRB must be stamped with the Cadets/Students Number of the cadets/students to whom it would be issued; <br/><br/>";
        text += "B. Information such as name of the issuing maritime HEl and the name and cadets/students number to whom the TRB would be issued must also be stamped at a conspicuous page, preferably next to the front page of the TRB; and <br/><br/>";
        text += "C. TRB control number issued to cadets/students shall be indicated in the enrollment list to be submitted to CHED.\"";
        text += "</p>";
        text+= "</body></html>";
        tv_content.loadData(text, "text/html", "utf-8");

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChedMemoActivity.this, MainActivity.class);
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
