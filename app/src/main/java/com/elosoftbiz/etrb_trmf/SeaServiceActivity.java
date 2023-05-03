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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SeaServiceActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title;
    LinearLayout main_container;
    ProgressDialog pd;
    DatabaseHelper db;
    ImageButton IB_add;
    String person_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sea_service);

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
        person_id = db.getFieldString("person_id", "vessel_officer = 'N'", "person");
        String dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");

        tv_title = findViewById(R.id.tv_title);
        main_container = findViewById(R.id.main_container);
        IB_add = findViewById(R.id.IB_add);
        IB_add.setVisibility(View.GONE);

        int cnt = db.GetCount("person", "");
        if(cnt > 0){ //WITH DATA
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

            IB_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SeaServiceActivity.this, SeaServiceFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

            LinearLayout linearLayout =  new LinearLayout(this);
            layoutParams.setMargins(5,10,5,5);
            linearLayout.setLayoutParams(layoutParams);

            final TextView textView = new TextView(this);
            textView.setLayoutParams(params);
            textView.setText("Ship Name");
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(18);
            textView.setPadding(5,5,5,5);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView);

            final TextView textView1 = new TextView(this);
            textView1.setLayoutParams(params);
            textView1.setText("IMO Number");
            textView1.setPadding(5,5,5,5);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(18);
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(this);
            textView2.setLayoutParams(params);
            textView2.setText("Signed On");
            textView2.setPadding(5,5,5,5);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(18);
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(this);
            textView3.setLayoutParams(params);
            textView3.setText("Signed Off");
            textView3.setPadding(5,5,5,5);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setTextColor(Color.WHITE);
            textView3.setTextSize(18);
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView3);

            final TextView textView4 = new TextView(this);
            textView4.setLayoutParams(params);
            textView4.setText("Voyage Months");
            textView4.setPadding(5,5,5,5);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL);
            textView4.setTextColor(Color.WHITE);
            textView4.setTextSize(18);
            textView4.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView4);

            final TextView textView5 = new TextView(this);
            textView5.setLayoutParams(params);
            textView5.setText("Voyage Days");
            textView5.setPadding(5,5,5,5);
            textView5.setGravity(Gravity.CENTER_HORIZONTAL);
            textView5.setTextColor(Color.WHITE);
            textView5.setTextSize(18);
            textView5.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView5);

            final TextView textView6 = new TextView(this);
            textView6.setLayoutParams(params);
            textView6.setText("");
            textView6.setPadding(5,5,5,5);
            textView6.setGravity(Gravity.CENTER_HORIZONTAL);
            textView6.setTextColor(Color.WHITE);
            textView6.setTextSize(18);
            textView6.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView6);

            main_container.addView(linearLayout);

            textView4.post(new Runnable() {
                @Override
                public void run() {
                    int height = textView4.getLineCount() * textView.getMeasuredHeight();
                    Log.d("HI", "" + height + " - " + textView1.getMeasuredHeight());
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();

                    params.height = height;
                    textView.setLayoutParams(params);
                    textView4.setLayoutParams(params);
                    textView5.setLayoutParams(params);
                    textView6.setLayoutParams(params);
                }
            });

            //GENERATE
            new generate(context).execute();
        }else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,5,5,5);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            viewParam.weight = 1;

            TextView textView = new TextView(this);
            textView.setLayoutParams(viewParam);
            textView.setText("Ship Name");
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(18);
            textView.setPadding(5,5,5,5);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView);

            TextView textView1 = new TextView(this);
            textView1.setLayoutParams(viewParam);
            textView1.setText("IMO Number");
            textView1.setPadding(5,5,5,5);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setTextColor(Color.WHITE);
            textView1.setTextSize(18);
            textView1.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView1);

            final TextView textView2 = new TextView(this);
            textView2.setLayoutParams(viewParam);
            textView2.setText("Signed On");
            textView2.setPadding(5,5,5,5);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setTextColor(Color.WHITE);
            textView2.setTextSize(18);
            textView2.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView2);

            final TextView textView3 = new TextView(this);
            textView3.setLayoutParams(viewParam);
            textView3.setText("Signed Off");
            textView3.setPadding(5,5,5,5);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setTextColor(Color.WHITE);
            textView3.setTextSize(18);
            textView3.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView3);

            final TextView textView4 = new TextView(this);
            textView4.setLayoutParams(viewParam);
            textView4.setText("Voyage Months");
            textView4.setPadding(5,5,5,5);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL);
            textView4.setTextColor(Color.WHITE);
            textView4.setTextSize(18);
            textView4.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView4);

            final TextView textView5 = new TextView(this);
            textView5.setLayoutParams(viewParam);
            textView5.setText("Voyage Days");
            textView5.setPadding(5,5,5,5);
            textView5.setGravity(Gravity.CENTER_HORIZONTAL);
            textView5.setTextColor(Color.WHITE);
            textView5.setTextSize(18);
            textView5.setBackgroundResource(R.drawable.border_darkblue);
            linearLayout.addView(textView5);

            main_container.addView(linearLayout);

            linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            layoutParams.setMargins(5,-2,5,5);

            textView = new TextView(this);
            textView.setLayoutParams(viewParam);
            textView.setText("NOT YET ACTIVATED");
            textView.setTextColor(Color.RED);
            textView.setTextSize(18);
            textView.setPadding(5,5,5,5);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setBackgroundResource(R.drawable.border1dp);
            linearLayout.addView(textView);
            main_container.addView(linearLayout);
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
            pd = new ProgressDialog(SeaServiceActivity.this);
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
        int cnt = db.GetCount("shipboard", "");
        if(cnt > 0){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,-5,5,5);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

            List<Shipboard> shipboard = db.getShipboard(person_id);
            for(final Shipboard shipboard1 : shipboard){
                LinearLayout linearLayout = new LinearLayout(SeaServiceActivity.this);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setBackgroundResource(R.drawable.border1dp);

                String name_vessel = db.getFieldString("name_vessel", "vessel_id='"+shipboard1.getVessel_id()+"'","vessel");
                final TextView textView = new TextView(SeaServiceActivity.this);
                textView.setTextSize(18);
                textView.setText(name_vessel);
                textView.setTextColor(Color.BLACK);
                //textView.setBackgroundResource(R.drawable.border);
                textView.setLayoutParams(params);
                textView.setPadding(5,5,5,5);
                linearLayout.addView(textView);

                final TextView textView1 = new TextView(SeaServiceActivity.this);
                textView1.setTextSize(18);
                textView1.setText(shipboard1.getImo_number());
                textView1.setTextColor(Color.BLACK);
                //textView1.setBackgroundResource(R.drawable.border);
                textView1.setLayoutParams(params);
                textView1.setPadding(5,5,5,5);
                linearLayout.addView(textView1);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
                String sign_on = shipboard1.getSign_on();
                String sign_off = "";
                try {
                    Date date = format.parse(sign_on);
                    sign_on = format2.format(date);
                    date = format.parse(sign_off);
                    sign_off = format2.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                final TextView textView2 = new TextView(SeaServiceActivity.this);
                textView2.setTextSize(18);
                textView2.setText(sign_on);
                textView2.setTextColor(Color.BLACK);
                //textView2.setBackgroundResource(R.drawable.border);
                textView2.setLayoutParams(params);
                textView2.setPadding(5,5,5,5);
                linearLayout.addView(textView2);

                final TextView textView3 = new TextView(SeaServiceActivity.this);
                textView3.setTextSize(18);
                textView3.setText(sign_off);
                textView3.setTextColor(Color.BLACK);
                //textView3.setBackgroundResource(R.drawable.border);
                textView3.setLayoutParams(params);
                textView3.setPadding(5,5,5,5);
                linearLayout.addView(textView3);

                final TextView textView4 = new TextView(SeaServiceActivity.this);
                textView4.setTextSize(18);
                textView4.setText("");
                textView4.setTextColor(Color.BLACK);
                //textView4.setBackgroundResource(R.drawable.border);
                textView4.setLayoutParams(params);
                textView4.setPadding(5,5,5,5);
                linearLayout.addView(textView4);

                final TextView textView5 = new TextView(SeaServiceActivity.this);
                textView5.setTextSize(18);
                textView5.setText("");
                textView5.setTextColor(Color.BLACK);
                //textView5.setBackgroundResource(R.drawable.border);
                textView5.setLayoutParams(params);
                textView5.setPadding(5,5,5,5);
                linearLayout.addView(textView5);

                final TextView textView6 = new TextView(SeaServiceActivity.this);
                textView6.setTextSize(18);
                textView6.setText("Update");
                textView6.setTextColor(Color.BLUE);
                textView6.setGravity(Gravity.CENTER_HORIZONTAL);
                //textView6.setBackgroundResource(R.drawable.border);
                textView6.setLayoutParams(params);
                textView6.setPadding(5,5,5,5);
                linearLayout.addView(textView6);

                textView6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SeaServiceActivity.this, SeaServiceFormActivity.class);
                        intent.putExtra("shipboard_id", shipboard1.getShipboard_id());
                        startActivity(intent);
                        finish();
                    }
                });

                main_container.addView(linearLayout);

                textView1.post(new Runnable() {
                    @Override
                    public void run() {
                        int height = textView1.getLineCount() * textView.getMeasuredHeight();
                        Log.d("HI", "" + height + " - " + textView1.getMeasuredHeight());
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();

                        params.height = height;

                        textView.setLayoutParams(params);
                        textView2.setLayoutParams(params);
                        textView3.setLayoutParams(params);
                        textView4.setLayoutParams(params);
                        textView5.setLayoutParams(params);
                        textView6.setLayoutParams(params);
                    }
                });
            }
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,-5,5,5);

            TextView tvAdd =  new TextView(SeaServiceActivity.this);
            tvAdd.setLayoutParams(layoutParams1);
            tvAdd.setText("+ ADD NEW RECORD");
            tvAdd.setTextSize(15);
            tvAdd.setTextColor(Color.BLUE);
            tvAdd.setTypeface(null, Typeface.BOLD);
            tvAdd.setPadding(5,5,5,5);

            tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SeaServiceActivity.this, SeaServiceFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            main_container.addView(tvAdd);
        }else{
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,-5,5,5);

            TextView textView =  new TextView(SeaServiceActivity.this);
            textView.setLayoutParams(layoutParams);
            textView.setText("+ ADD NEW RECORD");
            textView.setTextSize(15);
            textView.setTextColor(Color.BLUE);
            textView.setBackgroundResource(R.drawable.border);
            textView.setPadding(5,5,5,5);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SeaServiceActivity.this, SeaServiceFormActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            main_container.addView(textView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SeaServiceActivity.this, MainActivity.class);
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
