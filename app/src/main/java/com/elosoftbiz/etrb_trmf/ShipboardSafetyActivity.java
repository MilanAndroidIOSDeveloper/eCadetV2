package com.elosoftbiz.etrb_trmf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

public class ShipboardSafetyActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;

    TextView tv_title, tv_instruct;
    DatabaseHelper db;
    ImageButton IB_add;
    String person_id, dept;
    ProgressDialog pd;
    LinearLayout main_container;
    Button btn_boat;

    String str = "", err_message, photo_file;
    HttpResponse response;
    JSONObject json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipboard_safety);

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
        tv_instruct = findViewById(R.id.tv_instruct);
        main_container = findViewById(R.id.main_container);

        db = new DatabaseHelper(this);
        int cnt = db.GetCount("person", "");
        if(cnt > 0 ){ //WITH DATA
            person_id = db.getCadetId();
            dept = db.getFieldString("dept", " person_id ='"+person_id+"'", "person");
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
            int cnt2 = db.GetCount("shipboard", "");
            if(cnt2 > 0){
                new generate(context).execute();
            }else{
                btn_boat.setVisibility(View.GONE);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(5,5,5,5);

                TextView textView = new TextView(ShipboardSafetyActivity.this);
                textView.setText("No Sea Service Record detected. Please make sure you saved your current on board status.");
                textView.setTextColor(Color.RED);
                textView.setTextSize(20);
                textView.setLayoutParams(layoutParams);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                main_container.addView(textView);
            }

        }else{ //DEMO
            btn_boat.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,5,5,5);

            TextView textView = new TextView(ShipboardSafetyActivity.this);
            textView.setText("Your app is not yet activated. Please download your data to use the e-cadet's full features.");
            textView.setTextColor(Color.RED);
            textView.setTextSize(20);
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            main_container.addView(textView);
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
            pd = new ProgressDialog(ShipboardSafetyActivity.this);
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
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        layoutParams.setMargins(10,10,10,10);

        List<SafetyH> safetyHS = db.getSafetyH(dept);
        for (SafetyH cn : safetyHS){
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setBackgroundResource(R.drawable.border);
            linearLayout.setPadding(15,15,15,15);

            ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.ic_arrow_left_blue);
            linearLayout.addView(imageView);

            TextView textView = new TextView(context);
            textView.setText(cn.getDesc_safety_h());
            textView.setTextColor(getColor(R.color.black));
            textView.setTextSize(20);
            textView.setLayoutParams(layoutParams2);
            textView.setId(cn.getId());
            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                        Log.d("RESULT", "Touch down");
                        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        textView.setTextColor(getColor(R.color.colorPrimaryDark));
                        textView.setTextSize(21);
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            textView.setPaintFlags(0);
                            textView.setTextColor(getColor(R.color.black));
                            textView.setTextSize(20);
                            String safety_h_id = db.getFieldString("safety_h_id", "id = "+ v.getId(), "safety_h");
                            Intent intent = new Intent(context, PersonSafetyActivity.class);
                            intent.putExtra("safety_h_id", safety_h_id);
                            startActivity(intent);
                            finish();
                        }
                    }, 500);   //5 seconds


                    return false;
                }
            });

            linearLayout.addView(textView);

            main_container.addView(linearLayout);
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShipboardSafetyActivity.this, MainActivity.class);
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
