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
import android.util.Log;
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

import java.util.List;

public class ShipParticularActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;

    TextView tv_title;
    DatabaseHelper db;
    ImageButton IB_add;
    String person_id;
    ProgressDialog pd;
    LinearLayout main_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_particular);

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

        tv_title = findViewById(R.id.tv_title);
        IB_add = findViewById(R.id.IB_add);
        IB_add.setVisibility(View.GONE);
        main_container = findViewById(R.id.main_container);

        db = new DatabaseHelper(this);
        person_id = db.getCadetId();
        String dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");

        if(person_id.equals("")){ //NO DATA, DEMO
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,5,5,5);

            TextView textView = new TextView(ShipParticularActivity.this);
            textView.setText("DEMO VERSION");
            textView.setTextColor(Color.RED);
            textView.setTextSize(20);
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            main_container.addView(textView);
        }else{
            IB_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShipParticularActivity.this, ShipParticularFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

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

            int cnt2 = db.GetCount("shipboard", " WHERE person_id = '"+person_id+"'");
            if(cnt2 > 0){
                new generate(context).execute();
            }else{ //WITHOUT SEA SERVICE
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
                IB_add.setVisibility(View.GONE);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(5,5,5,5);

                TextView textView = new TextView(ShipParticularActivity.this);
                textView.setText("No Sea Service Record detected. Please make sure you saved your current on board status.");
                textView.setTextColor(Color.RED);
                textView.setTextSize(20);
                textView.setLayoutParams(layoutParams);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                main_container.addView(textView);
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
        protected void onPreExecute()
        {
            super.onPreExecute();
            pd = new ProgressDialog(ShipParticularActivity.this);
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

        int cnt = db.GetCount("person_vessel", " WHERE person_id = '"+person_id+"'");
        if(cnt > 0){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            layoutParams.setMargins(5,5,5,5);

            LinearLayout linearLayout = new LinearLayout(ShipParticularActivity.this);
            linearLayout.setLayoutParams(layoutParams);

            final TextView textView = new TextView(ShipParticularActivity.this);
            textView.setText("Ship Name");
            textView.setTextSize(18);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setLayoutParams(layoutParams1);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(ShipParticularActivity.this);
            textView1.setText("Vessel Type");
            textView1.setTextSize(18);
            textView1.setTextColor(Color.WHITE);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setLayoutParams(layoutParams1);
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(ShipParticularActivity.this);
            textView2.setText("IMO No.");
            textView2.setTextSize(18);
            textView2.setTextColor(Color.WHITE);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setLayoutParams(layoutParams1);
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(ShipParticularActivity.this);
            textView3.setText("Call Sign");
            textView3.setTextSize(18);
            textView3.setTextColor(Color.WHITE);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setLayoutParams(layoutParams1);
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView3);

            final TextView textView4 = new TextView(ShipParticularActivity.this);
            textView4.setText("Flag");
            textView4.setTextSize(18);
            textView4.setTextColor(Color.WHITE);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL);
            textView4.setLayoutParams(layoutParams1);
            textView4.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView4);

            final TextView textView5 = new TextView(ShipParticularActivity.this);
            textView5.setText("");
            textView5.setTextSize(18);
            textView5.setTextColor(Color.WHITE);
            textView5.setGravity(Gravity.CENTER_HORIZONTAL);
            textView5.setLayoutParams(layoutParams1);
            textView5.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView5);

            textView.post(new Runnable() {
                @Override
                public void run() {
                    int height = textView.getLineCount() * textView1.getMeasuredHeight();
                    Log.d("HI", "" + height + " - " + textView1.getMeasuredHeight());
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();

                    params.height = height;

                    textView.setLayoutParams(params);
                    textView2.setLayoutParams(params);
                    textView3.setLayoutParams(params);
                    textView4.setLayoutParams(params);
                    textView5.setLayoutParams(params);
                }
            });

            main_container.addView(linearLayout);

            List<PersonVessel> personVessels = db.getPersonVessels(person_id);
            for(final PersonVessel personVessel : personVessels){
                LinearLayout.LayoutParams layoutParams1_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1_.setMargins(5,-5,5,5);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

                LinearLayout linearLayout1 = new LinearLayout(ShipParticularActivity.this);
                linearLayout1.setLayoutParams(layoutParams1_);
                linearLayout1.setBackgroundResource(R.drawable.border1dp);

                String name_vessel = db.getFieldString("name_vessel", "vessel_id ='"+personVessel.getVessel_id()+"'", "vessel");
                TextView textView_ = new TextView(ShipParticularActivity.this);
                textView_.setText(name_vessel);
                textView_.setTextColor(getResources().getColor(R.color.black));
                textView_.setTextSize(18);
                textView_.setLayoutParams(layoutParams2);
                textView_.setGravity(Gravity.LEFT);
                textView_.setPadding(5,5,5,5);
                linearLayout1.addView(textView_);

                String vessel_type_id = db.getFieldString("vessel_type_id", "vessel_id ='"+personVessel.getVessel_id()+"'", "vessel");
                String desc_vessel_type = db.getFieldString("desc_vessel_type", "vessel_type_id ='"+vessel_type_id+"'", "vessel_type");

                TextView textView1_ = new TextView(ShipParticularActivity.this);
                textView1_.setText(desc_vessel_type);
                textView1_.setTextColor(getResources().getColor(R.color.black));
                textView1_.setTextSize(18);
                textView1_.setLayoutParams(layoutParams2);
                textView1_.setGravity(Gravity.LEFT);
                textView1_.setPadding(5,5,5,5);
                linearLayout1.addView(textView1_);

                TextView textView2_ = new TextView(ShipParticularActivity.this);
                textView2_.setText(personVessel.getImo_number());
                textView2_.setTextColor(getResources().getColor(R.color.black));
                textView2_.setTextSize(18);
                textView2_.setLayoutParams(layoutParams2);
                textView2_.setGravity(Gravity.LEFT);
                textView2_.setPadding(5,5,5,5);
                linearLayout1.addView(textView2_);

                TextView textView3_ = new TextView(ShipParticularActivity.this);
                textView3_.setText(personVessel.getCall_sign());
                textView3_.setTextColor(getResources().getColor(R.color.black));
                textView3_.setTextSize(18);
                textView3_.setLayoutParams(layoutParams2);
                textView3_.setGravity(Gravity.LEFT);
                textView3_.setPadding(5,5,5,5);
                linearLayout1.addView(textView3_);

                TextView textView4_ = new TextView(ShipParticularActivity.this);
                textView4_.setText(personVessel.getFlag());
                textView4_.setTextColor(getResources().getColor(R.color.black));
                textView4_.setTextSize(18);
                textView4_.setLayoutParams(layoutParams2);
                textView4_.setGravity(Gravity.LEFT);
                textView4_.setPadding(5,5,5,5);
                linearLayout1.addView(textView4_);

                TextView textView5_ = new TextView(ShipParticularActivity.this);
                textView5_.setText("Update");
                textView5_.setTextColor(Color.BLUE);
                textView5_.setTextSize(18);
                textView5_.setLayoutParams(layoutParams2);
                textView5_.setGravity(Gravity.CENTER_HORIZONTAL);
                textView5_.setPadding(5,5,5,5);
                linearLayout1.addView(textView5_);

                textView5_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ShipParticularActivity.this, ShipParticularFormActivity.class);
                        intent.putExtra("person_vessel_id", personVessel.getPerson_vessel_id());
                        startActivity(intent);
                        finish();

                    }
                });

                main_container.addView(linearLayout1);
            }
            LinearLayout.LayoutParams layoutParams1_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1_.setMargins(5,-5,5,5);

            TextView tvAdd = new TextView(ShipParticularActivity.this);
            tvAdd.setText("+ ADD NEW RECORD");
            tvAdd.setLayoutParams(layoutParams1_);
            tvAdd.setTextSize(18);
            tvAdd.setTextColor(Color.BLUE);
            tvAdd.setTypeface(null, Typeface.BOLD);
            tvAdd.setPadding(5,5,5,5);

            tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShipParticularActivity.this, ShipParticularFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            main_container.addView(tvAdd);
        }else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            layoutParams.setMargins(5,5,5,5);

            LinearLayout linearLayout = new LinearLayout(ShipParticularActivity.this);
            linearLayout.setLayoutParams(layoutParams);

            TextView textView = new TextView(ShipParticularActivity.this);
            textView.setText("Ship Name");
            textView.setTextSize(18);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setLayoutParams(layoutParams1);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView);

            TextView textView1 = new TextView(ShipParticularActivity.this);
            textView1.setText("Vessel Type");
            textView1.setTextSize(18);
            textView1.setTextColor(Color.WHITE);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setLayoutParams(layoutParams1);
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView1);

            TextView textView2 = new TextView(ShipParticularActivity.this);
            textView2.setText("IMO No.");
            textView2.setTextSize(18);
            textView2.setTextColor(Color.WHITE);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setLayoutParams(layoutParams1);
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView2);

            TextView textView3 = new TextView(ShipParticularActivity.this);
            textView3.setText("Call Sign");
            textView3.setTextSize(18);
            textView3.setTextColor(Color.WHITE);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setLayoutParams(layoutParams1);
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView3);

            TextView textView4 = new TextView(ShipParticularActivity.this);
            textView4.setText("Flag");
            textView4.setTextSize(18);
            textView4.setTextColor(Color.WHITE);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL);
            textView4.setLayoutParams(layoutParams1);
            textView4.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView4);

            main_container.addView(linearLayout);

            LinearLayout.LayoutParams layoutParams1_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1_.setMargins(5,-5,5,5);

            TextView textView5 = new TextView(ShipParticularActivity.this);
            textView5.setText("+ ADD NEW RECORD");
            textView5.setLayoutParams(layoutParams1_);
            textView5.setTextSize(18);
            textView5.setTextColor(Color.BLUE);
            textView5.setBackgroundResource(R.drawable.border);
            textView5.setPadding(5,5,5,5);

            textView5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShipParticularActivity.this, ShipParticularFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            main_container.addView(textView5);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShipParticularActivity.this, MainActivity.class);
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
