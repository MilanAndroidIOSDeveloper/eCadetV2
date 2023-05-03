package com.elosoftbiz.etrb_trmf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
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

public class InternationalRegActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    WebView tv_instruct;

    DatabaseHelper db;
    String person_id;
    LinearLayout main_container;
    ProgressDialog pd;

    String str = "", err_message, photo_file;
    HttpResponse response;
    JSONObject json = null;
    String rule35 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_international_reg);

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

        main_container = findViewById(R.id.main_container);
        db = new DatabaseHelper(InternationalRegActivity.this);
        person_id = db.getCadetId();

        String dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");

        pd = new ProgressDialog(InternationalRegActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading. Please wait .... ");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();



        if(dept.equals("ENGINE")){
            tv_instruct.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,5,5,5);

            TextView textView = new TextView(InternationalRegActivity.this);
            textView.setText("THIS PART IS NOT APPLICABLE TO ENGINE DEPARTMENT");
            textView.setTextColor(Color.RED);
            textView.setTextSize(20);
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            main_container.addView(textView);
            pd.dismiss();
            return;
        }

        int cnt = db.GetCount("person", "");
        if(cnt > 0){ //WITH DATA
            new generate(context).execute();
        }else{ //DEMO
            tv_instruct.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,5,5,5);

            TextView textView = new TextView(InternationalRegActivity.this);
            textView.setText("Your app is not yet activated. Please download your data to use the eTRB's full features.");
            textView.setTextColor(Color.RED);
            textView.setTextSize(20);
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            main_container.addView(textView);
            pd.dismiss();
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
        int cnt = db.GetCount("person_regulation", " WHERE person_id = '"+person_id+"'");
        if(cnt == 0){
            autoCreate();
        }
        LinearLayout.LayoutParams layoutParams0 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams0.setMargins(5,0,5,0);
        TextView textView = new TextView(context);
        textView.setText("The Cadet shall have thorough comprehension of the following ColRegs Content:");
        textView.setLayoutParams(layoutParams0);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        textView.setPadding(15,5,15,5);
        textView.setBackgroundColor(Color.BLACK);
        main_container.addView(textView);


        //GET REGULATION H
        List<RegulationH> regulationHS = db.getRegulationH();
        for(RegulationH regulationH : regulationHS){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5,-5,5,5);

            LinearLayout linearLayout = new LinearLayout(InternationalRegActivity.this);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setBackgroundColor(getColor(R.color.colorPrimaryDark));

            textView = new TextView(InternationalRegActivity.this);
            textView.setText(URLDecoder.decode(regulationH.getDesc_regulation_h()));
            textView.setTextSize(18);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.LEFT);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(10,10,10,10);
            linearLayout.addView(textView);
            main_container.addView(linearLayout);

            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.setMargins(5,-10,5,5);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3);


            LinearLayout linearLayout1 = new LinearLayout(InternationalRegActivity.this);
            linearLayout1.setLayoutParams(layoutParams1);
            linearLayout1.setBackgroundColor(getColor(R.color.darkGray));

            TextView textView1 = new TextView(InternationalRegActivity.this);
            textView1.setText("Rule");
            textView1.setTextSize(18);
            textView1.setTextColor(Color.BLACK);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL);
            textView1.setLayoutParams(layoutParams3);
            textView1.setPadding(10,5,5,5);
            linearLayout1.addView(textView1);

            TextView textView2 = new TextView(InternationalRegActivity.this);
            textView2.setText("STO");
            textView2.setTextSize(18);
            textView2.setTextColor(Color.BLACK);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL);
            textView2.setLayoutParams(layoutParams2);
            textView2.setPadding(10,5,5,5);
            linearLayout1.addView(textView2);

            TextView textView3 = new TextView(InternationalRegActivity.this);
            textView3.setText("Date");
            textView3.setTextSize(18);
            textView3.setTextColor(Color.BLACK);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL);
            textView3.setLayoutParams(layoutParams2);
            textView3.setPadding(10,5,5,5);
            linearLayout1.addView(textView3);
            main_container.addView(linearLayout1);

            //get regulation d
            List<RegulationD> regulationDS = db.getRegulationD(regulationH.getRegulation_h_id());
            for(final RegulationD regulationD : regulationDS){
                LinearLayout linearLayout1_ = new LinearLayout(InternationalRegActivity.this);
                linearLayout1_.setLayoutParams(layoutParams1);
                linearLayout1_.setBackgroundResource(R.drawable.border1dp);

                TextView textView1_ = new TextView(InternationalRegActivity.this);
                textView1_.setText(URLDecoder.decode(regulationD.getDesc_regulation_d()));
                textView1_.setTextSize(18);
                textView1_.setTextColor(Color.BLUE);
                textView1_.setGravity(Gravity.LEFT);
                textView1_.setLayoutParams(layoutParams3);
                textView1_.setPadding(10,5,5,5);
                linearLayout1_.addView(textView1_);
                textView1_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Rules", "" + regulationD.id);
                        ruleLoad(regulationD.regulation_d_id);
                    }
                });

                String checked_by_id = db.getFieldString("checked_by_id", " person_id = '"+person_id+"' AND regulation_d_id = '"+regulationD.getRegulation_d_id()+"'", "person_regulation");
                String officer = db.getFieldString("full_name", " person_id = '"+checked_by_id+"'", "person");

                TextView textView2_ = new TextView(InternationalRegActivity.this);
                textView2_.setText(officer);
                textView2_.setTextSize(18);
                textView2_.setTextColor(Color.BLACK);
                textView2_.setGravity(Gravity.CENTER_HORIZONTAL);
                textView2_.setLayoutParams(layoutParams2);
                textView2_.setPadding(10,5,5,5);
                linearLayout1_.addView(textView2_);

                String date_checked = db.getFieldString("date_checked", " person_id = '"+person_id+"' AND regulation_d_id = '"+regulationD.getRegulation_d_id()+"'", "person_regulation");

                TextView textView3_ = new TextView(InternationalRegActivity.this);
                textView3_.setText(date_checked);
                textView3_.setTextSize(18);
                textView3_.setTextColor(Color.BLACK);
                textView3_.setGravity(Gravity.CENTER_HORIZONTAL);
                textView3_.setLayoutParams(layoutParams2);
                textView3_.setPadding(10,5,5,5);
                linearLayout1_.addView(textView3_);
                main_container.addView(linearLayout1_);
            }
        }
    }

    public void autoCreate(){
        List<RegulationH> regulationH = db.getRegulationH();
        for(RegulationH regulationH1 : regulationH){
            List<RegulationD> regulationDS = db.getRegulationD(regulationH1.getRegulation_h_id());
            for(RegulationD regulationD : regulationDS){
                Integer id = db.newIntegerId("person_regulation");
                String person_regulation_id = db.newId();
                String regulation_d_id = regulationD.getRegulation_d_id();
                db.query("INSERT INTO person_regulation (id, person_regulation_id, person_id, regulation_d_id, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks) VALUES ("+id+", '"+person_regulation_id+"', '"+person_id+"', '"+regulation_d_id+"', '','','','','','')");

                int conn = getConnection.getConnectionType(InternationalRegActivity.this);
                if(conn != 0){ //WITH CONN
                    new SyncOnline(context, person_regulation_id).execute();
                }else{
                    Integer backup_item_id = db.newIntegerId("backup_item");
                    db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_regulation', '" + person_regulation_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");

                }

            }
        }

        return;
    }

    public void ruleLoad(String regulation_d_id){
        String contents = db.getFieldString("contents", " regulation_d_id = '"+regulation_d_id+"'", "regulation_d");
        String desc_regulation_d = db.getFieldString("desc_regulation_d", " regulation_d_id = '"+regulation_d_id+"'", "regulation_d");
        Integer rule_no = db.getFieldInt("id", " regulation_d_id = '"+regulation_d_id+"'","regulation_d");
        Log.d("Rules", "Load - " + rule_no + " " + regulation_d_id);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InternationalRegActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(15,5,15,5);

        WebView textView = new WebView(InternationalRegActivity.this);
        textView.loadData(getRules(rule_no), "text/html", "utf-8");


        //textView.setTextColor(Color.BLACK);
        //textView.setTextSize(18);
        textView.setPadding(35,35,35,35);
        textView.setLayoutParams(layoutParams);

        alertDialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        alertDialogBuilder.setView(textView);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(InternationalRegActivity.this, R.color.black));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(InternationalRegActivity.this, R.color.white));

    }

    private class SyncOnline extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public String id;
        public SyncOnline(Context context, String id){
            this.context = context;
            this.id = id;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            String url = getString(R.string.url);
            //GET FROM TBL
            String saved_person_id = db.getFieldString("person_id", "person_regulation_id = '" + id + "'", "person_regulation");
            String saved_regulation_d_id = db.getFieldString("regulation_d_id", "person_regulation_id = '" + id + "'", "person_regulation");
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_regulation_id = '" + id + "'", "person_regulation");
            String saved_app_by_id = db.getFieldString("app_by_id", "person_regulation_id = '" + id + "'", "person_regulation");
            String saved_date_checked = db.getFieldString("date_checked", "person_regulation_id = '" + id + "'", "person_regulation");
            String saved_date_app = db.getFieldString("date_app", "person_regulation_id = '" + id + "'", "person_regulation");
            String saved_checked_remarks = db.getFieldString("checked_remarks", "person_regulation_id = '" + id + "'", "person_regulation");
            saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
            String saved_app_remarks = db.getFieldString("app_remarks", "person_regulation_id = '" + id + "'", "person_regulation");
            saved_app_remarks = URLEncoder.encode(saved_app_remarks);

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_regulation&id="+id+"&person_id="+saved_person_id+"&regulation_d_id="+saved_regulation_d_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&event=ADD");
            Log.d("CONNECT", url +"sync.php?table=person_regulation&id="+id+"&person_id="+saved_person_id+"&regulation_d_id="+saved_regulation_d_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&event=ADD");

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                Log.d("CONNECT", str);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response + str);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response + str);
            }

            return null;
        }
        protected void onPostExecute(Void result){
            return;

        }
    }

    public String getRules(Integer ruleNo){
        String rules = "";
        if(ruleNo == 1){
            rules = "<html><body style='padding:10px'><h3>Rule 1 (Application)</h3>" +
                    "<p align='justify'>" +
                    "(a) These Rules shall apply to all vessels upon the high seas and in all waters connected therewith navigable by seagoing vessels.<br/><br/>" +
                    "(b) Nothing in these Rules shall interfere with the operation of special rules made by an appropriate authority for roadsteads, harbours, rivers, lakes or inland waterways connected with the high seas and navigable by seagoing vessels. Such special rules shall conform as closely as possible to these Rules.<br/><br/>" +
                    "(c) Nothing in these Rules shall interfere with the operation of any special rules made by the Government of any State with respect to additional station or signal lights, shapes or whistle signals for ships of war and vessels proceeding under convoy, or with respect to additional station or signal lights or shapes for fishing vessels engaged in fishing as a fleet. These additional station or signal lights, shapes or whistles shall, so far as possible, be such that they cannot be mistaken for any light, shape or signal authorised elsewhere under these Rules.<br/><br/>" +
                    "(d) Traffic separation schemes may be adopted by the Organisation for the purpose of these Rules.<br/><br/>" +
                    "(e) Whenever the Government concerned shall have determined that a vessel of special construction or purpose cannot comply fully with the provision of any of these Rules with respect to the number, position, range or arc of visibility of lights or shapes, as well as to the disposition and characteristics of sound-signalling appliances, such vessel shall comply with such other provisions in regard to the number, position, range or arc of visibility of lights or shapes, as well as to the disposition and characteristics of sound-signalling appliances, as her Government shall have determined to be the closest possible compliance with these Rules in respect of that vessel.\n" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=43&Itemid=376&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 2){
            rules = "<html><body style='padding:10px'><h3>Rule 2 (Responsibility)</h3>" +
                    "<p align='justify'>" +
                    "(a) Nothing in these Rules shall exonerate any vessel, or the owner, master or crew thereof, from the consequences of any neglect to comply with these Rules or of the neglect of any precautions which may be required by the ordinary practice of seamen, or by the special circumstances of the case.<br/><br/>" +
                    "(b) In construing and complying with these Rules due regard shall be had to all dangers of navigation and collision and to any special circumstances, including the limitations of the vessels involved, which may make a departure from these Rules necessary to avoid immediate danger." +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=45&Itemid=377&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 3){
            rules = "<html><body style='padding:10px'><h3>Rule 3 (Definitions)</h3>" +
                    "<p align='justify'>" +
                    "For the purpose of these Rules, except where the context otherwise requires:<br/><br/>" +
                    "(a) The word \"vessel\" includes every description of water craft, including non-displacement craft, WIG craft and seaplanes, used or capable of being used as a means of transportation on water.<br/><br/>" +
                    "(b) The term \"power-driven vessel\" means any vessel propelled by machinery.<br/><br/>" +
                    "(c) The term \"sailing vessel\" means any vessel under sail provided that propelling machinery, if fitted, is not being used.<br/><br/>" +
                    "(d) The term \"vessel engaged in fishing\" means any vessel fishing with nets, lines, trawls or other fishing apparatus which restricts manoeuvrability, but does not include a vessel fishing with trolling lines or other fishing apparatus which do not restrict manoeuvrability.<br/><br/>" +
                    "(e) The term \"seaplane\" includes any aircraft designed to manoeuvre on the water.<br/><br/>" +
                    "(f) The term \"vessel not under command\" means a vessel which through some exceptional circumstance is unable to manoeuvre as required by these Rules and is therefore unable to keep out of the way of another vessel.<br/><br/>" +
                    "(g) The term \"vessel restricted in her ability to manoeuvre\" means a vessel which from the nature of her work is restricted in her ability to manoeuvre as required by these Rules and is therefore unable to keep out of the way of another vessel. The term \"vessels restricted in their ability to manoeuvre\" shall include but not be limited to:<br/><br/></p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>(i) a vessel engaged in laying, servicing or picking up a navigation mark, submarine cable or pipeline;<br/><br/>" +
                    "(ii) a vessel engaged in dredging, surveying or underwater operations;<br/><br/>" +
                    "(iii) a vessel engaged in replenishment or transferring persons, provisions or cargo while underway;<br/><br/>" +
                    "(iv) a vessel engaged in launching or recovery of aircraft;<br/><br/>" +
                    "(v) a vessel engaged in mine clearance operations;<br/><br/>" +
                    "(vi) a vessel engaged in a towing operation such as severely restricts the towing vessel and her tow in their ability to deviate from their course.<br/><br/></p>" +
                    "<p align='justify'>(h) The term \"vessel constrained by her draught\" means a power-driven vessel which, because of her draught in relation to available depth and width of navigable water, is severely restricted in her ability to deviate from the course she is following.<br/><br/>" +
                    "(i) The word \"underway\" means that a vessel is not at anchor, or made fast to the shore, or aground.<br/><br/>" +
                    "(j) The words \"length\" and \"breadth\" of a vessel mean her length overall and greatest breadth.<br/><br/>" +
                    "(k) Vessel shall be deemed to be in sight of one another only when one can be observed visually from the other.<br/><br/>" +
                    "(l) The term \"restricted visibility\" means any condition in which visibility is restricted by fog, mist, falling snow, heavy rainstorms, sandstorms or any other similar causes.<br/><br/>" +
                    "(m) The term \"Wing-In-Ground (WIG) craft\" means a multimodal craft which, in its main operational mode, flies in close proximity to the surface by utilizing surface-effect action.<br/><br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=46&Itemid=378&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 4){
            rules = "<html><body style='padding:10px'><h3>Rule 4 (Application)</h3>" +
                    "<p align='justify'>" +
                    "Rules in this section apply in any conditions of visibility." +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=47&Itemid=379&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 5){
            rules = "<html><body style='padding:10px'><h3>Rule 5 (Look-out)</h3>" +
                    "<p align='justify'>" +
                    "Requires that \"every vessel shall at all times maintain a proper look-out by sight and hearing as well as by all available means appropriate in the prevailing circumstances and conditions so as to make a full appraisal of the situation and of the risk of collision." +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=48&Itemid=380&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 6){
            rules = "<html><body style='padding:10px'><h3>Rule 6 (Safe speed)</h3>" +
                    "<p align='justify'>" +
                    "Every vessel shall at all times proceed at a safe speed so that she can take proper and effective action to avoid a collision and be stopped within a distance appropriate to the prevailing circumstances and conditions.<br/><br/>" +
                    "In determining a safe speed the following factors shall be among those taken into account:<br/><br/>" +
                    "(a) By all vessels:<br/></p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) the state of visibility;<br/><br/>" +
                    "(ii) the traffic density including concentrations of fishing vessels or any other vessels;<br/><br/>" +
                    "(iii) the manoeuvrability of the vessel with special reference to stopping distance and turning ability in the prevailing conditions;<br/><br/>" +
                    "(iv) at night the presence of background light such as from shore lights or from backscatter of her own lights;<br/><br/>" +
                    "(v) the state of wind, sea and current, and the proximity of navigational hazards;<br/><br/>" +
                    "(vi) the draught in relation to the available depth of water.<br/>" +
                    "</p>" +
                    "<p align='justify'>(b) Additionally, by vessels with operational radar:</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) the characteristics, efficiency and limitations of the radar equipment;<br/><br/>" +
                    "(ii) any constraints imposed by the radar range scale in use;<br/><br/>" +
                    "(iii) the effect on radar detection of the sea state, weather and other sources of interference;<br/><br/>" +
                    "(iv) the possibility that small vessels, ice and other floating objects may not be detected by radar at an adequate range;<br/><br/>" +
                    "(v) the number, location and movements of vessels detected by radar;<br/><br/>" +
                    "(vi) the more exact assessment of the visibility that may be possible when radar is used to determine the range of vessels or other objects in the vicinity.<br/><br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=48&Itemid=380&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 7){
            rules = "<html><body style='padding:10px'><h3>Rule 7 (Risk of collision)</h3>" +
                    "<p align='justify'>" +
                    "(a) Every vessel shall use all available means appropriate to the prevailing circumstances and conditions to determine if risk of collision exists. If there is any doubt such risk shall be deemed to exist.<br/><br/>" +
                    "(b) Proper use shall be made of radar equipment if fitted and operational, including long-range scanning to obtain early warning of risk of collision and radar plotting or equivalent systematic observations of detected objects.<br/><br/>" +
                    "(c) Assumptions shall not be made on the basis of scanty information, especially scanty radar information.<br/><br/>" +
                    "(d) In determining if risk of collision exists the following considerations shall be among those taken into account:</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) such risk shall be deemed to exist it the compass bearing of an approaching vessel does not appreciably change;<br/><br/>" +
                    "(ii) such risk may sometimes exist even when an appreciable bearing change is evident, particularly when approaching a very large vessel or a tow or when approaching a vessel at close range.<br/><br/>" +
                    "</p>" +

                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=50&Itemid=382&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 8){
            rules = "<html><body style='padding:10px'><h3>Rule 8 (Action to avoid a collision)</h3>" +
                    "<p align='justify'>" +
                    "(a) Any action taken to avoid collision shall be taken in accordance with the Rules of this Part and shall, if the circumstances of the case admit, be positive, made in ample time and with due regard to the observance of good seamanship.<br/><br/>" +
                    "(b) Any alteration of course and/or speed to avoid collision shall, if the circumstances of the case admit, be large enough to be readily apparent to another vessel observing visually or by radar; a succession of small alterations of course and/or speed should be avoided.<br/><br/>" +
                    "(c) If there is sufficient sea-room, alteration of course alone may be the most effective action to avoid a close-quarters situation provided that it is made in good time, is substantial and does not result in another close-quarters situation.<br/><br/>" +
                    "(d) Action taken to avoid a collision with another vessel shall be such as to result in passing at a safe distance. The effectiveness of the action shall be carefully checked until the other vessel is finally past and clear.<br/><br/>" +
                    "(e) If necessary to avoid collision or allow more time to assess the situation, a vessel shall slacken her speed or take all way off by stopping or reversing her means of propulsion.<br/><br/>" +
                    "(f)<br/></p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) A vessel which, by any of these Rules, is required not to impede the passage or safe passage of another vessel shall, when required by the circumstances of the case, take early action to allow sufficient sea-room for the safe passage of the other vessel.<br/><br/>" +
                    "(ii) A vessel required not to impede the passage or safe passage of another vessel is not relieved of this obligation if approaching the other vessel so as to involve risk of collision and shall, when taking action, have full regard to the action which may be required by the Rules of this Part.<br/><br/>" +
                    "(iii) A vessel, the passage of which is not to be impeded remains fully obliged to comply with the Rules of this part when the two vessels are approaching one another so as to involve risk of collision." +
                    "</p>" +

                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=51&Itemid=383&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 9){
            rules = "<html><body style='padding:10px'><h3>Rule 9 (Narrow channels)</h3>" +
                    "<p align='justify'>" +
                    "(a) A vessel proceeding along the course of a narrow channel or fairway shall keep as near to the outer limit or the channel or fairway which lies on her starboard side as is safe and practicable.<br/><br/>" +
                    "(b) A vessel of less than 20 m in length or a sailing vessel shall not impede the passage of a vessel which can safely navigate only within a narrow channel or fairway.<br/><br/>" +
                    "(c) A vessel engaged in fishing shall not impede the passage of any other vessel navigating within a narrow channel or fairway.<br/><br/>" +
                    "(d) A vessel shall not cross a narrow channel of fairway if such crossing impedes the passage of a vessel which can safely navigate only within such channel or fairway. The latter vessel may use the sound signal prescribed in Rule 34 (d) if in doubt as to the intention of the crossing vessel.<br/><br/>" +
                    "(e)<br/></p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) In a narrow channel or fairway when overtaking can only take place if the vessel to be overtaken has to take action to permit safe passing, the vessel intending to overtake shall indicate her intention by sounding the appropriate signal prescribed in Rule 34 (c)(i). The vessel to be overtaken shall, if in agreement, sound the appropriate signal prescribed in Rule 34 (c)(ii) and take steps to permit safe passing. If in doubt she may sound the signals prescribed in Rule 34 (d).<br/><br/>" +
                    "(ii) This rule does not relieve the overtaking vessel of her obligation under Rule 13.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(f) A vessel nearing a bend or an area of narrow channel or fairway where other vessels may be obscured by an intervening obstruction shall navigate with particular alertness and caution and shall sound the appropriate signal prescribed in Rule 34 (e).<br/><br/>" +
                    "(g) Any vessel shall, if the circumstances of the case admit, avoid anchoring in a narrow channel." +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=44&Itemid=384&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 10){
            rules = "<html><body style='padding:10px'><h3>Rule 10 (Traffic separation schemes)</h3>" +
                    "<p align='justify'>" +
                    "(a) This Rule applies to traffic separation schemes adopted by the Organisation and does not relieve any vessel of her obligation under any other Rule.<br/><br/>" +
                    "(b) A vessel using a traffic separation scheme shall:<br/></p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) proceed in the appropriate traffic lane in the general direction of traffic flow for that lane;<br/><br/>" +
                    "(ii) so far as practicable keep clear of a traffic separation line or separation zone;<br/><br/>" +
                    "(iii) normally join or leave a traffic lane at the termination of the lane, but when joining or leaving from either side shall do so at as small an angle to the general direction of traffic flow as practicable.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(c) A vessel shall, so far as practicable, avoid crossing traffic lanes but if obliged to do so shall cross on a heading as nearly as practicable at right angles to the general direction of traffic flow.<br/><br/>" +
                    "(d)<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) A vessel shall not use an inshore traffic zones when she can safely use the appropriate traffic lane within the adjacent traffic separation scheme. However, vessels of less than 20 m in length, sailing vessels and vessels engaged in fishing may use the inshore traffic zones.<br/><br/>" +
                    "(ii) Notwithstanding subparagraph (d)(i), a vessel may use an inshore traffic zone when en route to or from a port, offshore installation or structure, pilot station or any other place situated within the inshore traffic zone, or to avoid immediate danger.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(e) A vessel other than a crossing vessel or a vessel joining or leaving a lane shall not normally enter a separation zone or cross a separation line except:<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) in cases of emergency to avoid immediate danger;<br/><br/>" +
                    "(ii) to engage in fishing within a separation zone.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(f) A vessel navigating in areas near the terminations of traffic separation schemes shall do so with particular caution.<br/><br/>" +
                    "(g) A vessel shall so far as practicable avoid anchoring in a traffic separation scheme or in areas near its terminations.<br/><br/>" +
                    "(h) A vessel not using a traffic separation scheme shall avoid it by as wide a margin as is practicable.<br/><br/>" +
                    "(i) A vessel engaged in fishing shall not impede the passage of any vessel following a traffic lane.<br/><br/>" +
                    "(j) A vessel of less than 20 m in length or a sailing vessel shall not impede the safe passage of a power-driven vessel following a traffic lane.<br/><br/>" +
                    "(k) A vessel restricted in her ability to manoeuvre when engaged in an operation for the maintenance of safety of navigation in a traffic separation scheme is exempted from complying with this Rule to the extent necessary to carry out the operation.<br/><br/>" +
                    "(l) A vessel restricted in her ability to manoeuvre when engaged in an operation for the laying, servicing or picking up of a submarine cable, within a traffic separation scheme, is exempted from complying with this Rule to the extent necessary to carry out the operation." +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=52&Itemid=385&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 11){
            rules = "<html><body style='padding:10px'><h3>Rule 11 (Application)</h3>" +
                    "<p align='justify'>Rules in this Section shall apply to vessels in sight of one another." +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=53&Itemid=386&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 12){
            rules = "<html><body style='padding:10px'><h3>Rule 12 (Sailing vessels)</h3>" +
                    "<p align='justify'>(a) When two sailing vessels are approaching one another, so as to involve risk of collision, one of them shall keep out of the way of the other as follows:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) when each has the wind on a different side, the vessel which has the wind on the port side shall keep out of the way of the other;<br/><br/>" +
                    "(ii) when both have the wind on the same side, the vessel which is to windward shall keep out of the way of the vessel which is to leeward;<br/><br/>" +
                    "(iii) if a vessel with the wind on the port side sees a vessel to windward and cannot determine with certainty whether the other vessel has the wind on her port or starboard side, she shall keep out of the way of the other.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(b) For the purposes of this Rule the windward side shall be deemed to be the side opposite to that on which the mainsail is carried or, in the case of a square-rigged vessel, the side opposite to that on which the largest fore-and-aft sail is carried." +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=54&Itemid=387&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 13){
            rules = "<html><body style='padding:10px'><h3>Rule 13 (Overtaking)</h3>" +
                    "<p align='justify'>" +
                    "(a) Notwithstanding anything contained in the Rules of Part B, Sections I and II, any vessel overtaking any other shall keep out of the way of the vessel being overtaken.<br/><br/>" +
                    "(b) A vessel shall be deemed to be overtaking when coming up with another vessel from a direction more than 22.5° abaft her beam, that is, in such a position with reference to the vessel she is overtaking, that at night she would be able to see only the sternlight of that vessel but neither of her sidelights.<br/><br/>" +
                    "(c) When a vessel is in any doubt as to whether she is overtaking another, she shall assume that this is the case and act accordingly.<br/><br/>" +
                    "(d) Any subsequent alteration of the bearing between the two vessels shall not make the overtaking vessel a crossing vessel within the meaning of these Rules or relieve her of the duty of keeping clear of the overtaken vessel until she is finally past and clear.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=55&Itemid=388&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 14){
            rules = "<html><body style='padding:10px'><h3>Rule 14 (Head-on situation)</h3>" +
                    "<p align='justify'>" +
                    "(a) When two power-driven vessels are meeting on reciprocal or nearly reciprocal courses so as to involve risk of collision each shall alter her course to starboard so that each shall pass on the port side of the other.<br/><br/>" +
                    "(b) Such a situation shall be deemed to exist when a vessel sees the other ahead or nearly ahead and by night she could see the masthead lights of the other in line or nearly in a line and/or both sidelights and by day she observes the corresponding aspect of the other vessel.<br/><br/>" +
                    "(c) When a vessel is in any doubt as to whether such a situation exists she shall assume that it does exist and act accordingly.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=55&Itemid=388&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 15){
            rules = "<html><body style='padding:10px'><h3>Rule 15 (Crossing situation)</h3>" +
                    "<p align='justify'>" +
                    "When two power-driven vessels are crossing so as to involve risk of collision, the vessel which has the other on her own starboard side shall keep out of the way and shall, if the circumstances of the case admit, avoid crossing ahead of the other vessel.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=20&Itemid=360&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 16){
            rules = "<html><body style='padding:10px'><h3>Rule 16 (Action by give-way vessel)</h3>" +
                    "<p align='justify'>" +
                    "Every vessel which is directed to keep out of the way of another vessel shall, as far as possible, take early and substantial action to keep well clear.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=24&Itemid=361&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 17){
            rules = "<html><body style='padding:10px'><h3>Rule 17 (Action by stand-on vessel)</h3>" +
                    "<p align='justify'>" +
                    "(a)<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) Where one of two vessels is to keep out of the way the other shall keep her course and speed.<br/><br/>" +
                    "(ii) The latter vessel may, however, take action to avoid collision by her manoeuvre alone, as soon as it becomes apparent to her that the vessel required to keep out of the way is not taking appropriate action in compliance with these Rules.<br/>" +
                    "</p>"+
                    "<p align='justify'>" +
                    "(b) When, from any cause, the vessel required to keep her course and speed finds herself so close that collision cannot be avoided by the action of the give-way vessel alone, she shall take such action as will best aid to avoid collision.<br/><br/>" +
                    "(c) A power-driven vessel which takes action in a crossing situation in accordance with subparagraph (a)(ii) of this Rule to avoid collision with another power-driven vessel shall, if the circumstances at the case admit, not alter course to port for a vessel on her own port side.<br/><br/>" +
                    "(d) This Rule does not relieve the give-way vessel of her obligation to keep out of the way.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=57&Itemid=390&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 18){
            rules = "<html><body style='padding:10px'><h3>Rule 18 (Responsibilities between vessels)</h3>" +
                    "<p align='justify'>" +
                    "Except where Rule 9, Rule 10, and Rule 13 otherwise require:<br/><br/>" +
                    "(a) A power-driven vessel underway shall keep out of the way of:<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) a vessel not under command;<br/><br/>" +
                    "(ii) a vessel restricted in her ability to manoeuvre;<br/><br/>" +
                    "(iii) a vessel engaged in fishing;<br/><br/>" +
                    "(iv) a sailing vessel.<br/>" +
                    "</p>"+
                    "<p align='justify'>" +
                    "(b) A sailing vessel underway shall keep out of the way of:<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) a vessel not under command;<br/><br/>" +
                    "(ii) a vessel restricted in her ability to manoeuvre;<br/><br/>" +
                    "(iii) a vessel engaged in fishing.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(c) A vessel engaged in fishing when underway shall, so far as possible, keep out of the way of:<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) a vessel not under command;<br/><br/>" +
                    "(ii) a vessel restricted in her ability to manoeuvre.<br/>" +
                    "</p>" +
                    "<p align='justify'>(d)</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) Any vessel other than a vessel not under command or a vessel restricted in her ability to manoeuvre shall, if the circumstances of the case admit, avoid impeding the safe passage of a vessel constrained by her draught, exhibiting the signals in Rule 28.<br/><br/>" +
                    "(ii) A vessel constrained by her draught shall navigate with particular caution having full regard to her special condition.<br/>" +
                    "</p>" +
                    "<p align='justify'>(e) A seaplane on the water shall, in general, keep well clear of all vessels and avoid impeding their navigation. In circumstances, however, where risk of collision exists, she shall comply with the Rules of this part.<br/><br/>" +
                    "(f)<br/></p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) A WIG craft, when taking off, landing and in flight near the surface, shall keep well clear of all other vessels and avoid impeding their navigation;<br/><br/>" +
                    "(ii) A WIG craft operating on the water surface shall comply with the Rules of this Part as a power-driven vessel.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=58&Itemid=391&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 19){
            rules = "<html><body style='padding:10px'><h3>Rule 19 (Conduct of vessels in restricted visibility)</h3>" +
                    "<p align='justify'>" +
                    "(a) This Rule applies to vessels not in sight of one another when navigating in or near an area of restricted visibility.<br/><br/>" +
                    "(b) Every vessel shall proceed at a safe speed adapted to the prevailing circumstances and conditions of restricted visibility. A power-driven vessel shall have her engines ready for immediate manoeuvre.<br/><br/>" +
                    "(c) Every vessel shall have due regard to the prevailing circumstances and conditions of restricted visibility when complying with the Rules of Section I of this Part.<br/><br/>" +
                    "(d) A vessel which detects by radar alone the presence of another vessel shall determine if a close-quarters situation is developing and/or risk of collision exists. If so, she shall take avoiding action in ample time, provided that when such action consists of an alteration of course, so far as possible the following shall be avoided:<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) an alteration of course to port for a vessel forwards of the beam, other than for a vessel being overtaken;<br/><br/>" +
                    "(ii) an alteration of course towards a vessel abeam or abaft the beam.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(e) Except where it has been determined that a risk of collision does not exist, every vessel which hears apparently forwards of her beam the fog signal of another vessel, or which cannot avoid a close-quarters situation with another vessel forwards of her beam, shall reduce her speed to the minimum at which she can be kept on her course. She shall if necessary take all her way off and in any event navigate with extreme caution until danger of collision is over.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=59&Itemid=392&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 20){
            rules = "<html><body style='padding:10px'><h3>Rule 20 (Application)</h3>" +
                    "<p align='justify'>" +
                    "(a) Rules in this part shall be complied with in all weathers.<br/><br/>" +
                    "(b) The Rules concerning lights shall be complied with from sunset to sunrise, and during such times no other lights shall be exhibited, except such lights as cannot be mistaken for the lights specified in these Rules or do not impair their visibility or distinctive character, or interfere with the keeping of a proper look-out.<br/><br/>" +
                    "(c) The lights prescribed by these Rules shall, if carried, also be exhibited from sunrise to sunset in restricted visibility and may be exhibited in all other circumstances when it is deemed necessary.<br/><br/>" +
                    "(d) The Rules concerning shapes shall be complied with by day.<br/><br/>" +
                    "(e) The lights and shapes specified in these Rules shall comply with the provisions of Annex I to these Regulations.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=60&Itemid=393&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 21){
            rules = "<html><body style='padding:10px'><h3>Rule 21 (Definitions)</h3>" +
                    "<p align='justify'>" +
                    "(a) \"Masthead light\" means a white light placed over the fore-and-aft centreline of the vessel showing an unbroken light over an arc of the horizon of 225° and so fixed as to show the light from right ahead to 22.5° abaft the beam on either side of the vessel.<br/><br/>" +
                    "(b) \"Sidelights\" means a green light on the starboard side and a red light on the port side each showing an unbroken light over an arc of the horizon of 112.5° and so fixed as to show the light from right ahead to 22.5° abaft the beam on its respective side. In a vessel of less than 20 m in length the sidelights may be combined in one lantern carried on the fore-and-aft centreline of the vessel.<br/><br/>" +
                    "(c) \"Sternlight\" means a white light placed as nearly as practicable at the stern showing an unbroken light over an arc of the horizon of 135° and so fixed as to show the light 67.5° from right aft on each side of the vessel.<br/><br/>" +
                    "(d) \"Towing light\" means a yellow light having the same characteristics as the \"sternlight\" defined in paragraph (c) of this Rule.<br/><br/>" +
                    "(e) \"All-round light\" means a light showing an unbroken light over an arc of the horizon of 360°.<br/><br/>" +
                    "(f) \"Flashing light\" means a light flashing at regular intervals at a frequency of 120 flashes or more per minute.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=61&Itemid=394&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 22){
            rules = "<html><body style='padding:10px'><h3>Rule 22 (Visibility of lights)</h3>" +
                    "<p align='justify'>" +
                    "The lights prescribed in these Rules shall have an intensity as specified in section 8 of Annex I to these Regulations so as to be visible at the following minimum ranges:<br/><br/>" +
                    "(a) In vessels of 50 m or more in length:<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "    - a masthead light, 6 miles;<br/><br/>" +
                    "    - a sidelight, 3 miles;<br/><br/>" +
                    "    - a sternlight, 3 miles;<br/><br/>" +
                    "    - a towing light, 3 miles;<br/><br/>" +
                    "    - a white, red, green or yellow all-round light, 3 miles.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(b) In vessels of 12 m or more in length but less than than 50 m in length:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "    - a masthead light, 5 miles; except that where the length of the vessel is less than 20 m, 3 miles;<br/><br/>" +
                    "    - a sidelight, 2 miles;<br/><br/>" +
                    "    - a sternlight, 2 miles;<br/><br/>" +
                    "    - a towing light, 2 miles;<br/><br/>" +
                    "    - a white, red, green or yellow all-round light, 2 miles.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(c) In vessels of less than 12 m in length:<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "    - a masthead light, 2 miles;<br/><br/>" +
                    "    - a sidelight, 1 miles;<br/><br/>" +
                    "    - a sternlight, 2 miles;<br/><br/>" +
                    "    - a towing light, 2 miles;<br/><br/>" +
                    "    - a white, red, green or yellow all-round light, 2 miles.<br/>" +
                    "</p>" +
                    "<p align='justify'>(d) In inconspicuous, partly submerged vessels or objects being towed:<br/></p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>- a white all-round light, 3 miles.<br/></p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=62&Itemid=395&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 23){
            rules = "<html><body style='padding:10px'><h3>Rule 23 (Power-driven vessels underway)</h3>" +
                    "<p align='justify'>(a) A power-driven vessel underway shall exhibit:<br/></p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) a masthead light forward;<br/><br/>" +
                    "(ii) a second masthead light abaft of and higher than the forward one; except that a vessel of less than 50 m in length shall not be obliged to exhibit such light but may do so;<br/><br/>" +
                    "(iii) sidelights;<br/><br/>" +
                    "(iv) a sternlight.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(b) An air-cushion vessel when operating in the non-displacement mode shall, in addition to the lights prescribed in paragraph (a) of this Rule, exhibit an all-round flashing yellow light.<br/><br/>" +
                    "(c) A WIG craft only when taking off, landing and in flight near the surface shall, in addition to the lights prescribed in paragraph (a) of this Rule, exhibit a high intensity all-round flashing red light.<br/><br/>" +
                    "(d)<br/></p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) A power-driven vessel of less than 12 m in length may in lieu of the lights prescribed in paragraph (a) of this Rule exhibit an all-round white light and sidelights;<br/><br/>" +
                    "(ii) a power-driven vessel of less than 7 m in length whose maximum speed does not exceed 7 knots may in lieu of the lights prescribed in paragraph (a) of this Rule exhibit an all-round white light and shall, if practicable, also exhibit sidelights;<br/><br/>" +
                    "(iii) the masthead light or all-round white light on a power-driven vessel of less than 12 m in length may be displaced from the fore-and-aft centreline of the vessel if centreline fitting is not practicable, provided that the sidelights are combined in one lantern which shall be carried on the fore-and-aft centreline of the vessel or located as nearly as practicable in the same fore-and-aft line as the masthead light or the all-round white light.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=31&Itemid=366&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 24){
            rules = "<html><body style='padding:10px'><h3>Rule 24 (Towing and pushing)</h3>" +
                    "<p align='justify'>(a) A power-driven vessel when towing shall exhibit:<br/></p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) instead of the light prescribed in Rule 23 (a)(i) or (a)(ii), two masthead lights in a vertical line. When the length of the tow, measured from the stern of the towing vessel to the after end of the tow exceeds 200 m, three such lights in a vertical line;<br/><br/>" +
                    "(ii) sidelights;<br/><br/>" +
                    "(iii) a sternlight;<br/><br/>" +
                    "(iv) a towing light in a vertical line above the sternlight;<br/><br/>" +
                    "(v) when the length of the tow exceeds 200 m, a diamond shape where it can best be seen.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(b) When a pushing vessel and a vessel being pushed ahead are rigidly connected in a composite unit they shall be regarded as a power-driven vessel and exhibit the lights prescribed in Rule 23.<br/><br/>" +
                    "(c) A power-driven vessel when pushing ahead or towing alongside, except in the case of a composite unit, shall exhibit:<br/></p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) instead of the light prescribed in Rule 23 (a)(i) or (a)(ii), two masthead lights in a vertical line;<br/><br/>" +
                        "(ii) sidelights;<br/><br/>" +
                        "(iii) a sternlight.<br/></p>" +
                    "<p align='justify'>" +
                    "(d) A power-driven vessel to which paragraph (a) or (c) of this Rule applies shall also comply with Rule 23 (a)(ii).<br/><br/>" +
                    "(e) A vessel or object being towed, other that those mentioned in paragraph (g) of this Rule, shall exhibit:<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) sidelights;<br/><br/>" +
                        "(ii) a sternlight;<br/><br/>" +
                        "(iii) when the length of the tow exceeds 200 m, a diamond shape where it can best be seen.<br/>" +
                    "</p>" +
                    "<p align='justify'>(f) Provided that any number of vessels being towed alongside or pushed in a group shall be lighted as one vessel,<br/></p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) a vessel being pushed ahead, not being part of a composite unit, shall exhibit at the forward end, sidelights.<br/><br/>" +
                        "(ii) a vessel being towed alongside shall exhibit a sternlight and at the forward end, sidelights.<br/>"+
                    "</p>" +
                    "<p align='justify'>" +
                    "(g) An inconspicuous, partly submerged vessel or object, or combination of such vessel or objects being towed, shall exhibit:<br/>" +
                    "</p>"+
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "    (i) if it is less than 25 m in breadth, one all-round white light at or near the forward end and one at or near the after end except that dracones need not exhibit a light at or near the forward end;<br/><br/>" +
                        "    (ii) if it is 25 m or more in breadth, two additional all-round white lights at or near the extremities of its breadth;<br/><br/>" +
                        "    (iii) if it exceeds 100 m in length, additional all-round white lights between the lights prescribed in sub-paragraphs (i) and (ii) so that the distance between the lights shall not exceed 100 m.<br/><br/>" +
                        "    (iv) a diamond shape at or near the aftermost extremity of the last vessel or object being towed and if the length of the tow exceeds 200 m an additional diamond shape where it can best be seen and located as far forward as is practicable.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(h) Where from any sufficient cause it is impracticable for a vessel or object being towed to exhibit the lights or shapes prescribed in paragraph (e) or (g) of this Rule, all possible measures shall be taken to light the vessel or object towed or at least to indicate the presence of such vessel or object.<br/><br/>" +
                    "(i) Where from any sufficient cause it is impracticable for a vessel not normally engaged in towing operations to display the lights prescribed in paragraph (a) or (c) of this Rule, such vessel shall not be required to exhibit those lights when engaged in towing another vessel in distress or otherwise in need of assistance. All possible measures shall be taken to indicate the nature of the relationship between the towing vessel and the vessel being towed as authorised by Rule 36, in particular by illuminating the towline.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=32&Itemid=396&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 25){
            rules = "<html><body style='padding:10px'><h3>Rule 25 (Sailing vessels underway and vessels under oars)</h3>" +
                    "<p align='justify'>(a) A sailing vessel underway shall exhibit:</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) sidelights;<br/><br/>" +
                        "(ii) a sternlight." +
                    "</p>" +
                    "<p align='justify'>" +
                    "(b) In a sailing vessel of less than 20 m in length the lights prescribed in paragraph (a) of this Rule may be combined in one lantern at or near the top of the mast where it can best be seen.<br/><br/>" +
                    "(c) A sailing vessel underway may, in addition to the lights prescribed in paragraph (a) of this Rule, exhibit at or near the top of the mast, where they can best be seen, two all-round lights in a vertical line, the upper being red and the lower green, but these lights shall not be exhibited in conjunction with the combined lantern permitted by paragraph (b) of this Rule.<br/><br/>" +
                    "(d)<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) A sailing vessel of less than 7 m in length shall, if practicable, exhibit the lights prescribed in paragraph (a) or (b) of this Rule, but if she does not, she shall have ready at hand an electric torch or lighted lantern showing a white light which shall be exhibited in sufficient time to prevent collision.<br/><br/>" +
                        "(ii) A vessel under oars may exhibit the lights prescribed in this Rule for sailing vessels, but if she does not, she shall have ready at hand an electric torch or lighted lantern showing a white light which shall be exhibited in sufficient time to prevent a collision.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(e) A vessel proceeding under sail when also being propelled by machinery shall exhibit forward where it can best be seen a conical shape, apex downwards." +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=33&Itemid=397&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 26){
            rules = "<html><body style='padding:10px'><h3>Rule 26 (Fishing vessels)</h3>" +
                    "<p align='justify'>" +
                    "(a) A vessel engaged in fishing, whether underway or at anchor, shall exhibit only the lights and shapes prescribed in this Rule.<br/><br/>" +
                    "(b) A vessel when engaged in trawling, by which is meant the dragging through the water of a dredge net or other apparatus used as a fishing appliance, shall exhibit:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) two all-round lights in a vertical line, the upper being green and the lower white, or a shape consisting of two cones with their apexes together in a vertical line one above the other;<br/><br/>" +
                        "(ii) a masthead light abaft of and higher that the all-round green light; a vessel of less than 50 metres in length shall not be obliged to exhibit such a light but may do so;<br/><br/>" +
                        "(iii) when making way through the water, in addition to the lights prescribed in this paragraph, sidelights and a sternlight.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(c) A vessel engaged in fishing, other than trawling shall exhibit:<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) two all-round lights in a vertical line, the upper being red and the lower white, or a shape consisting of two cones with their apexes together in a vertical line one above the other;<br/><br/>" +
                        "(ii) when there is outlying gear extending more than 150 m horizontally from the vessel, an all-round white light or a cone apex upwards in the direction of the gear;<br/><br/>" +
                        "(iii) when making way through the water, in addition to the lights prescribed in this paragraph, sidelights and a sternlight.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(d) The additional signals described in Annex II to these Regulations apply to a vessel engaged in fishing in close proximity to other vessels engaged in fishing.<br/><br/>" +
                    "(e) A vessel when not engaged in fishing shall not exhibit the lights or shapes prescribed in this Rule, but only those prescribed for a vessel of her length.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=34&Itemid=398&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 27){
            rules = "<html><body style='padding:10px'><h3>Rule 27 (Vessels not under command or restricted in their ability to manoeuvre)</h3>" +
                    "<p align='justify'>" +
                    "(a) A vessel not under command shall exhibit:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "    (i) two all-round red lights in a vertical line where they can best be seen;<br/><br/>" +
                        "    (ii) two balls or similar shapes in a vertical line where they can best be seen;<br/><br/>" +
                        "    (iii) when making way through the water, in addition to the lights prescribed in this paragraph, sidelights and a sternlight.\n" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(b) A vessel restricted in her ability to manoeuvre, except a vessel engaged in mine clearance operations, shall exhibit:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) three all-round lights in a vertical line where they can best be seen. The highest and lowest of these lights shall be red and the middle light shall be white;<br/><br/>" +
                        "(ii) three shapes in a vertical line where they can best be seen. The highest and lowest of these shapes shall be balls and the middle one a diamond;<br/><br/>" +
                        "(iii) when making way through the water, a masthead light or lights, sidelights and a sternlight, in addition to the lights prescribed in subparagraph (i);<br/><br/>" +
                        "(iv) when at anchor, in addition to the lights or shapes prescribed in subparagraphs (i) and (ii), the light, lights or shape prescribed in Rule 30." +
                    "</p>" +
                    "<p align='justify'>" +
                    "(c) A power-driven vessel engaged in a towing operation such as severely restricts the towing vessel and her tow in their ability to deviate from their course shall, in addition to the lights and shapes prescribed in Rule 24 (a), exhibit the lights or shapes prescribed in subparagraphs (b)(i) and (ii) of this Rule.<br/><br/>" +
                    "(d) A vessel engaged in dredging or underwater operations, when restricted in her ability to manoeuvre, shall exhibit the lights and shapes prescribed in subparagraph(b)(i), (ii) and (iii) of this Rule and shall in addition, when an obstruction exists, exhibit:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) two all-round red lights or two balls in a vertical line to indicate the side on which the obstruction exists;<br/><br/>" +
                        "(ii) two all-round green lights or two diamonds in a vertical line to indicate the side on which another vessel may pass;<br/><br/>" +
                        "(iii) when at anchor, the lights or shapes prescribed in this paragraph instead of the lights or shape prescribed in Rule 30." +
                    "</p>" +
                    "<p align='justify'>" +
                        "(e) Whenever the size of a vessel engaged in diving operations makes it impracticable to exhibit all the lights and shapes prescribed in the paragraph (d) of this Rule, the following shall be exhibited:\n" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) three all-round lights in a vertical line where they can best be seen. The highest and lowest of these lights shall be red and the middle light shall be white;<br/><br/>" +
                        "(ii) a rigid replica of the International Code flag \"A\" not less than 1 metre in height. Measures shall be taken to ensure its all-round visibility.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(f) A vessel engaged in mine clearance operations shall in addition to the lights prescribed for a power-driven vessel in Rule 23 or to the lights or shape prescribed for a vessel at anchor in Rule 30 as appropriate, exhibit three all-round green lights or three balls. One of these lights or shapes shall be exhibited near the foremast head and one at each end of the fore yard. These lights or shapes indicate that it is dangerous for another vessel to approach within 1000 m of the mine clearance vessel.<br/><br/>" +
                    "(g) Vessels of less than 12 m in length, except those engaged in diving operations, shall not be required to exhibit the lights and shapes prescribed in this Rule.<br/><br/>" +
                    "(h) The signals prescribed in this Rule are not signals of vessels in distress and requiring assistance. Such signals are contained in Annex IV to these Regulations." +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=35&Itemid=399&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 28){
            rules = "<html><body style='padding:10px'><h3>Rule 28 (Vessels constrained by their draught)</h3>" +
                    "<p align='justify'>" +
                    "A vessel constrained by her draught may, in addition to the lights prescribed for power-driven vessels in Rule 23, exhibit where they can best be seen three all-round red lights in a vertical line, or a cylinder." +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=36&Itemid=400&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 29){
            rules = "<html><body style='padding:10px'><h3>Rule 29 (Pilot vessels)</h3>" +
                    "<p align='justify'>" +
                    "(a) A vessel engaged on pilotage duty shall exhibit:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "    (i) at or near the masthead, two all-round lights in a vertical line, the upper being white and the lower red;<br/><br/>" +
                        "    (ii) when underway, in addition, sidelights and a sternlight;<br/><br/>" +
                        "    (iii) when at anchor, in addition to the lights prescribed in sub-paragraph (i), the light, lights or shape prescribed in Rule 30 for vessels at anchor.<br/>" +
                    "</p>" +
                    "<p align='justify'>(b) A pilot vessel when not engaged on pilotage duty shall exhibit the lights or shapes prescribed for a similar vessel of her length.<br/></p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=37&Itemid=401&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 30){
            rules = "<html><body style='padding:10px'><h3>Rule 30 (Anchored vessels and vessels aground)</h3>" +
                    "<p align='justify'>" +
                    "(a) A vessel at anchor shall exhibit where it can best be seen:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) in the fore part, an all-round white light or one ball;<br/><br/>" +
                        "(ii) at or near the stern and at a lower level that the light prescribed in subparagraph (i), an all-round white light.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(b) A vessel of less than 50 m in length may exhibit an all-round white light where it can best be seen instead of the lights prescribed in paragraph (a) of this Rule.<br/><br/>" +
                    "(c) A vessel at anchor may, and a vessel of 100 m and more in length shall, also use the available working or equivalent lights to illuminate her decks.<br/><br/>" +
                    "(d) A vessel aground shall exhibit the lights prescribed in paragraph (a) or (b) of this Rule and in addition, where they can best be seen:<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "    (i) two all-round red lights in a vertical line;<br/><br/>" +
                        "    (ii) three balls in a vertical line.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(e) A vessel of less than 7 m in length, when at anchor, not in or near a narrow channel, fairway or anchorage, or where other vessels normally navigate, shall not be required to exhibit the lights or shape prescribed in paragraphs (a) and (b) of this Rule.<br/><br/>" +
                    "(f) A vessel of less than 12 m in length, when aground, shall not be required to exhibit the lights or shapes prescribed in subparagraphs (d)(i) and (ii) of this Rule.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=38&Itemid=402&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 31){
            rules = "<html><body style='padding:10px'><h3>Rule 31 (Seaplanes)</h3>" +
                    "<p align='justify'>" +
                    "Where it is impractical for a seaplane or a WIG craft to exhibit lights and shapes of the characteristics or in the positions prescribed in the Rules of this part she shall exhibit lights and shapes as closely similar in characteristics and position as is possible." +
                    "<br/></p>" +
                   "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=63&Itemid=403&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 32){
            rules = "<html><body style='padding:10px'><h3>Rule 32 (Definitions)</h3>" +
                    "<p align='justify'>" +
                    "(a) The word \"whistle\" means any sound signalling appliance capable of producing the prescribed blasts and which complies with the specifications in Annex III to these Regulations.<br/><br/>" +
                    "(b) The term \"short blast\" means a blast of about one second duration.<br/><br/>" +
                    "(c) The term \"prolonged blast\" means a blast of from four to six seconds duration.<br/>" +
                    "<br/></p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=64&Itemid=404&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 33){
            rules = "<html><body style='padding:10px'><h3>Rule 33 (Equipment for sound signals)</h3>" +
                    "<p align='justify'>" +
                    "(a) A vessel of 12 m or more in length shall be provided with a whistle, a vessel of 20 m or more in length shall be provided with a bell in addition to a whistle, and a vessel of 100 m or more in length shall, in addition, be provided with a gong, the tone and sound of which cannot be confused with that of the bell. The whistle, bell and gong shall comply with the specifications in Annex III to these Regulations. The bell or gong or both may be replaced by other equipment having the same respective sound characteristics, provided that manual sounding of the prescribed signals shall always be possible.<br/><br/>" +
                    "(b) A vessel of less than 12 m in length shall not be obliged to carry the sound signalling appliances prescribed in paragraph (a) of this Rule but if she does not, she shall be provided with some other means of making an efficient sound signal.<br/>" +
                    "<br/></p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=65&Itemid=405&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 34){
            rules = "<html><body style='padding:10px'><h3>Rule 34 (Manoeuvring and warning signals)</h3>" +
                    "<p align='justify'>" +
                    "(a) When vessel are in sight of one another, a power-driven vessel underway, when manoeuvring as authorised or required by these Rules, shall indicate that manoeuvre by the following signals on her whistle:<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "    - one short blast to mean \"I am altering my course to starboard\";<br/><br/>" +
                        "    - two short blasts to mean \"I am altering my course to port\";<br/><br/>" +
                        "    - three short blasts to mean \"I am operating astern propulsion\".<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(b) Any vessel may supplement the whistle signals prescribed in paragraph (a) of this Rule by light signals, repeated as appropriate, whilst the manoeuvre is being carried out:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) these signals shall have the following significance:<br/><br/>" +
                        "- one flash to mean \"I am altering my course to starboard\";<br/><br/>" +
                        "- two flashes to mean \"I am altering my course to port\";<br/><br/>" +
                        "- three flashes to mean \"I am operating astern propulsion\";<br/><br/>" +
                        "(ii) the duration of each flash shall be about one second, the interval between flashes shall be about one second, and the interval between successive signals shall be not less than ten seconds;<br/><br/>" +
                        "(iii) the light used for this signal shall, if fitted, be an all-round white light, visible at a minimum range of 5 miles, and shall comply with the provisions of Annex I to these Regulations.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(c) When in sight of one another in a narrow channel or fairway:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) a vessel intending to overtake another shall in compliance with Rule 9 (e)(i) indicate her intention by the following signals on her whistle:<br/><br/>" +
                        "- two prolonged blasts followed by one short blast to mean \"I intend to overtake you on your starboard side\";<br/><br/>" +
                        "- two prolonged blasts followed by two short blasts to mean \"I intend to overtake you on your port side\".<br/><br/>" +
                        "(ii) the vessel about to be overtaken when acting in accordance with Rule 9 (e)(i) shall indicate her agreement by the following signal on her whistle:<br/><br/>" +
                        "- one prolonged, one short, one prolonged and one short blast, in that order.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(d) When vessels in sight of one another are approaching each other and from any cause either vessel fails to understand the intentions or actions of the other, or is in doubt whether sufficient action is being taken by the other to avoid collision, the vessel in doubt shall immediately indicate such doubt by giving at least five short and rapid blasts on the whistle. Such signal may be supplemented by a light signal of at least five short and rapid flashes.<br/><br/>" +
                    "(e) A vessel nearing a bend or an area of the channel of fairway where other vessels may be obscured by an intervening obstruction shall sound one prolonged blast. Such signal shall be answered with a prolonged blast by any approaching vessel that may be within hearing around the bend or behind the intervening obstruction.<br/><br/>" +
                    "(f) If whistles are fitted on a vessel at a distance apart of more that 100 m, one whistle only shall be used for giving manoeuvring and warning signals.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=66&Itemid=406&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 35){
            rules = "<html><body style='padding:10px'><h3>Rule 35 (Sound signals in restricted visibility) </h3><br/>" +
                    "<p align=\"justify\">\n" +
                    "        In or near an area of restricted visibility, whether by day or night, the signals prescribed in this Rule shall be used as follows:<br/>\n" +
                    "        (a) A power-driven vessel making way through the water shall sound at intervals of not more than 2 minutes one prolonged blast.<br/><br/>\n" +
                    "        (b) A power-driven vessel underway but stopped and making no way through the water shall sound at intervals of not more than 2 minutes two prolonged blasts in succession with an interval of about 2 seconds between them.<br/><br/>\n" +
                    "        (c) A vessel not under command, a vessel restricted in her ability to manoeuvre, a vessel constrained by her draught, a sailing vessel, a vessel engaged in fishing and a vessel engaged in towing or pushing another vessel shall, instead of the signals prescribed in paragraphs (a) or (b) of this Rule, sound at intervals of not more than 2 minutes three blasts in succession, namely one prolonged followed by two short blasts.<br/><br/>\n" +
                    "        (d) A vessel engaged in fishing, when at anchor, and a vessel restricted in her ability to manoeuvre when carrying out her work at anchor, shall instead of the signals prescribed in paragraph (g) of this Rule sound the signal prescribed in paragraph (c) of this Rule.<br/><br/>\n" +
                    "        (e) A vessel towed or if more than one vessel is towed the last vessel of the tow, if manned, shall at intervals of not more than 2 minutes sound four blasts in succession, namely one prolonged followed by three short blasts. When practicable, this signal shall be made immediately after the signal made by the towing vessel.<br/><br/>\n" +
                    "        (f) When a pushing vessel and a vessel being pushed ahead are rigidly connected in a composite unit they shall be regarded as a power-driven vessel and shall give the signals prescribed in paragraphs (a) or (b) or this Rule.<br/><br/>\n" +
                    "        (g) A vessel at anchor shall at intervals of not more than one minute ring the bell rapidly for about 5 seconds. In a vessel of 100 m of more in length the bell shall be sounded in the forepart of the vessel and immediately after the ringing of the bell the gong shall be sounded rapidly for about 5 seconds in the after part of the vessel. A vessel at anchor may in addition sound three blasts in a succession, namely one short, one prolonged and one short blast, to give warning of her position and of the possibility of collision to an approaching vessel.<br/><br/>\n" +
                    "        (h) A vessel aground shall give the bell signal and if required the gong signal prescribed in paragraph (g) of this Rule and shall, in addition, give three separate and distinct strokes on the bell immediately before and after the rapid ringing of the bell. A vessel aground may in addition sound an appropriate whistle signal.<br/><br/>\n" +
                    "        (i) A vessel of 12 m or more but less than 20 m in length shall not be obliged to give the bell signals prescribed in paragraphs (g) and (h) of this Rule. However, if she does not, she shall make some other efficient sound signal at intervals of not more than 2 minutes.<br/><br/>\n" +
                    "        (j) A vessel of less than 12 m in length shall not be obliged to give the above-mentioned signals but, if she does not, shall make some other efficient sound signal at intervals of not more than 2 minutes.<br/><br/>\n" +
                    "        (k) A pilot vessel when engaged on pilotage duty may in addition to the signals prescribed in paragraphs (a), (b) or (g) of this Rule sound an identity signal consisting of four short blasts.\n" +
                    "    </p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=66&Itemid=406&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 36){
            rules = "<html><body style='padding:10px'><h3>Rule 36 (Signals to attract attention) </h3>" +
                    "<p align=\"justify\">" +
                    "If necessary to attract the attention of another vessel any vessel may make light or sound signals that cannot be mistaken for any signal authorised elsewhere in these Rules, or may direct the beam of her searchlight in the direction of the danger, in such a way as not to embarrass any vessel. Any light to attract the attention of another vessel shall be such that it cannot be mistaken for any aid to navigation. For the purpose of this Rule the use of high-intensity intermittent or revolving lights, such as strobe lights, shall be avoided. </p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=68&Itemid=408&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 37) {
            rules = "<html><body style='padding:10px'><h3>Rule 37 (Distress signals) </h3>" +
                    "<p align=\"justify\">" +
                    "When a vessel is in distress and requires assistance she shall use or exhibit the signals described in Annex IV to these Regulations.<br/></p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=69&Itemid=409&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";

        }else if(ruleNo == 38) {
            rules = "<html><body style='padding:10px'><h3>Rule 38 (Exemptions) </h3>" +
                    "<p align=\"justify\">" +
                    "Any vessel (or class of vessels) provided that she complies with the requirements of the International Regulation for Preventing Collisions and Sea, 1960, the keel of which is laid or which is at a corresponding stage of construction before the entry into force of these Regulations may be exempted from compliance therewith as follows:<br/><br/>" +
                    "(a) The installation of lights with ranges prescribed in Rule 22, until four years after the date of entry into force of these Regulations.<br/><br/>" +
                    "(b) The installation of lights with colour specifications as prescribed in Section 7 of Annex I to these Regulations, until four years after the date of entry into force of these Regulations.<br/><br/>" +
                    "(c) The repositioning of lights as a result of conversion from Imperial to metric units and rounding off measurement figures, permanent exemption.<br/><br/>" +
                    "(d)<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) The repositioning of masthead lights on vessels of less than 150 m in length, resulting from the prescriptions of section 3 (a) of Annex I to these Regulations, permanent exemption.<br/><br/>" +
                        "(ii) The repositioning of masthead lights on vessels of 150 m or more in length, resulting from the prescriptions of section 3 (a) of Annex I to these Regulations, until nine years after the date of entry into force of these Regulations.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                        "(e) The repositioning of masthead lights resulting from the prescriptions of section 2 (b) of Annex I to these Regulations, until nine years after the date of entry into force of these Regulations.<br/><br/>" +
                        "(f) The repositioning of sidelights resulting from the prescriptions of section 2 (g) and 3 (b) of Annex I to these Regulations, until nine years after the date of entry into force of these Regulations.<br/><br/>" +
                        "(g) The requirements for sound signal appliances prescribed in Annex III to these Regulations, until nine years after the date of entry into force of these Regulations.<br/><br/>" +
                        "(h) The repositioning of all-round lights resulting from the prescription of section 9 (b) of Annex I to these Regulations, permanent exemption.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=70&Itemid=410&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 39) {
            rules = "<html><body style='padding:10px'><h3>Section 1 (Definition) </h3>" +
                    "<p align=\"justify\">" +
                    "The term \"height above the hull\" means height above the uppermost continuous deck. This height shall be measured from the position vertically beneath the location of the light. <br/><br/>" +
                    "<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 40) {
            rules = "<html><body style='padding:10px'><h3>Section 2 (Vertical positioning and spacing of lights) </h3>" +
                    "<p align=\"justify\">" +
                    "(a) On a power-driven vessel of 20 m of more in length the masthead lights shall be placed as follows: <br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) the forwards masthead light, or if only one masthead light is carried, then that light, at a height above the hull of not less than 6 metres, and, if the breadth of the vessel exceeds 6 m, then at a height above the hull not less than such breadth, so however that the light need not be placed at a greater height above the hull than 12 m;<br/><br/>" +
                        "(ii) when two masthead lights are carried the after one shall be at least 4.5 m vertically higher than the forward one.<br/>" +
                    "</p>" +
                    "<p align=\"justify\">" +
                    "(b) The vertical separation of masthead lights of power-driven vessels shall be such that in all normal conditions of trim the after light will be seen over and separate from the forward light at a distance of 1000 m from the stem when viewed from sea-level.<br/><br/>" +
                    "(c) The masthead light of a power-driven vessel of 12 m but less than 20 m in length shall be placed at a height above the gunwale of not less than 2.5 m.<br/><br/>" +
                    "(d) A power-driven vessel of less than 12 m in length may carry the uppermost light at a height of less than 2.5 m above the gunwale. When, however, a masthead light is carried in addition to sidelights and a sternlight or the all-round light prescribed in Rule 23 (d)(i) is carried in addition to sidelights, then such masthead light or all-round light shall be carried at least 1 m higher than the sidelights.<br/><br/>" +
                    "(e) One of the two or three masthead lights prescribed for a power-driven vessel when engaged in towing or pushing another vessel shall be placed in the same position as either the forward masthead light or the after masthead light; provided that, if carried on the aftermast, the lowest after masthead lights shall be at least 4.5 m vertically higher than the forward masthead light.<br/><br/>" +
                    "(f)<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) The masthead light or lights prescribed in Rule 23 (a) shall be so placed as to be above and clear of all other lights and obstructions except as described in subparagraph (ii).<br/><br/>" +
                        "(ii) When it is impracticable to carry the all-round lights prescribed by Rule 27 (b)(i) or Rule 28 below the masthead lights, they may be carried above the after masthead light(s) or vertically in between the forwards masthead light(s) and after masthead light(s), provided that in the latter case the requirement of section 3 (c) of this annex shall be complied with.<br/>" +
                    "</p>" +
                    "<p align=\"justify\">" +
                    "(g) The sidelights of a power-driven vessel shall be placed at a height above the hull not greater than three quarters of that of the forward masthead light. They shall not be so low as to be interfered with by deck lights.<br/><br/>" +
                    "(h) The sidelights, if in a combined lantern and carried on a power-driven vessel of less than 20 m in length, shall be placed not less than 1 m below the masthead light.<br/><br/>" +
                    "(i) When the Rules prescribe two or three lights to be carried in a vertical line, they shall be spaced as follows:<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) on a vessel of 20 m in length or more such lights shall be spaced not less than 2 m apart, and the lowest of these lights shall, except where a towing light is required, be placed at a height of not less than 4 m above the hull;<br/><br/>" +
                        "(ii) on a vessel of less than 20 m in length such lights shall be spaced not less than 1 m apart and the lowest of these lights shall, except where a towing light is required, be placed at a height of not less than 2 m above the gunwale;<br/><br/>" +
                        "(iii) when three lights are carried they shall be equally spaced.<br/>" +
                    "</p>" +
                    "<p align=\"justify\">" +
                    "(j) The lower of the two all-round lights prescribed for a vessel when engaged in fishing shall be at a height above the sidelights not less than twice the distance between the two vertical lights.<br/><br/>" +
                    "(k) The forward anchor light prescribed in Rule 30 (a)(i), when two are carried, shall not be less than 4.5 m above the after one. On a vessel of 50 m or more in length this forward anchor light shall be placed at a height of not less than 6 m above the hull.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 41) {
            rules = "<html><body style='padding:10px'><h3>Section 3 (Horizontal positioning and spacing of lights) </h3>" +
                    "<p align=\"justify\">" +
                    "(a) When two masthead lights are prescribed for a power-driven vessel, the horizontal distance between them shall not be less than one half of the length of the vessel but need not be more than 100 m. The forward light shall be placed not more than one quarter on the length of the vessel from the stem.<br/><br/>" +
                    "(b) On a power-driven vessel on 20 m or more in length the sidelights shall not be placed in front of the forward masthead lights. They shall be placed at or near the side of the vessel.<br/><br/>" +
                    "(c) When the lights prescribed in Rule 27 (b)(i) or Rule 28 are placed vertically between the forward masthead light(s) and the after masthead light(s) these all-round lights shall be placed at a horizontal distance of not less than 2 m from the fore-and-aft centreline of the vessel in the athwartship direction.<br/><br/>" +
                    "(d) When only one masthead light is prescribed for power-driven vessel, this light shall be exhibeted forward of amidship; except that a vessel of less than 20 m in length need not exhibit this light forward of amidship but shall exhibit it as far forward as is practicable.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 42) {
            rules = "<html><body style='padding:10px'><h3>Section 4 (Details of location of direction-indicating lights for fishing vessels, dredgers and vessels engaged in underwater operations) </h3>" +
                    "<p align=\"justify\">" +
                    "(a) The light indicating the direction of the outlying gear from a vessel engaged in fishing as prescribed in Rule 26 (c)(ii) shall be placed at a horizontal distance of not less than 2 m and not more than 6 m away from the two all-round red and white lights. This light shall be placed not higher than the all-round white light prescribed in Rule 26 (c)(i) and not lower than the sidelights.<br/><br/>" +
                    "(b) The lights and shapes on a vessel engaged in dredging or underwater operations to indicate the obstructed side and/or the side on which it is safe to pass, as prescribed in Rule 27 (d)(i) and (ii), shall be placed at the maximum practical horizontal distance, but in no case less than 2 m, from the lights or shapes prescribed in Rule 27 (b)(i) and (ii). In no case shall the upper of these lights or shapes be at a greater height than the lower of the three lights or shapes prescribed in Rule 27 (b)(i) and (ii).<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 43) {
            rules = "<html><body style='padding:10px'><h3>Section 5 (Screens for sidelights) </h3>" +
                    "<p align=\"justify\">" +
                    "The sidelights of vessels of 20 m or more in length shall be fitted with inboard screens painted matt black, and meeting the requirements of section 9 of this annex. On vessels of less than 20 m in length the sidelights, if necessary to meet the requirements of section 9 of this annex, shall be fitted with inboard matt black screens. With a combined lantern, using a single vertical filament and a very narrow division between the green and red sections, external screens need not be fitted.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 44) {
            rules = "<html><body style='padding:10px'><h3>Section 6 (Shapes) </h3>" +
                    "<p align=\"justify\">" +
                    "(a) Shapes shall be black and of the following sizes:<br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) a ball shall have a diameter of not less than 0.6 m:<br/><br/>" +
                        "(ii) a cone shall have a base diameter of not less than 0.6 m and a height equal to its diameter:<br/><br/>" +
                        "(iii) a cylinder shall have a diameter of at least 0.6 m and a height of twice its diameter:<br/><br/>" +
                        "(iv) a diamond shape shall consist of two cones as defined in (ii) above having a common base.<br/>" +
                    "</p>" +
                    "<p align=\"justify\">" +
                    "(b) The vertical distance between shapes shall be at least 1.5 m.<br/><br/>" +
                    "(c) In a vessels of less than 20 m in length shapes of lesser dimensions but commensurate with the size of the vessel may be used and the distance apart may be correspondingly reduced.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 45) {
            rules = "<html><body style='padding:10px'><h3>Section 7 (Colour specification of lights) </h3>" +
                    "<p align=\"justify\">" +
                    "The chromaticity of all navigation lights shall conform to the following standards, which lie within the boundaries of the area of the diagram specified for each colour by the International Commission on Illumination (CIE).<br/><br/>" +
                    "The boundaries of the area for each colour are given by indicating the corner co-ordinates, which are as follows:<br/>" +
                    "</p>" +
                    "<p align=\"justify\">" +
                    "(i) White<br/>" +
                    "x 0.525 0.525 0.452 0.310 0.310 0.443<br/>" +
                    "y 0.382 0.440 0.440 0.348 0.283 0.382<br/><br/>" +
                    "(ii) Green<br/>" +
                    "x 0.028 0.009 0.300 0.203<br/>" +
                    "y 0.385 0.723 0.511 0.356<br/><br/>" +
                    "(iii) Red<br/>" +
                    "x 0.680 0.660 0.735 0.721<br/>" +
                    "y 0.320 0.320 0.265 0.259<br/><br/>" +
                    "(iv) Yellow<br/>" +
                    "x 0.612 0.618 0.575 0.575<br/>" +
                    "y 0.382 0.382 0.425 0.406<br/><br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 46) {
            rules = "<html><body style='padding:10px'><h3>Section 8 (Intensity of lights) </h3>" +
                    "<p align=\"justify\">" +
                    "(a) The minimum luminous intensity of lights shall be calculated by using the formula:<br/><br/>" +
                    "I = 3.43 x 10<sup>6</sup>x T x D<sup>2</sup>x K<sup>-D</sup><br/><br/>" +
                    "Where<br/>" +
                    "    I is luminous intensity in candelas under service conditions,<br/>" +
                    "    T is threshold factor 2 X 10-7 lux,<br/>" +
                    "    D is range of visibility (luminous range) of the light in nautical miles,<br/>" +
                    "    K is atmospheric transmissivity.<br/>" +
                    "    For prescribed lights the value of K shall be 0.8, corresponding to a meteorological visibility of approximately 13 nautical miles.<br/><br/>" +
                    "(b) A selection of figures derived from the formula is given in the following table:<br/></p>" +
                    "<table>" +
                    "<tr style='text-align:center'><td>Range of visibility (luminous range) of light in nautical miles</td>" +
                    "   <td>Luminous intensity of light in candelas for <br/>K = 0.8</td></tr>" +
                    "<tr style='text-align:center'>" +
                    "   <td>D</td>" +
                    "   <td>I</td></tr>" +
                    "<tr style='text-align:center'>" +
                    "   <td>1</td>" +
                    "   <td>0.9</td></tr>" +
                    "<tr style='text-align:center'>" +
                    "   <td>2</td>" +
                    "   <td>4.3</td></tr>" +
                    "<tr style='text-align:center'>" +
                    "   <td>3</td>" +
                    "   <td>12</td></tr>" +
                    "<tr style='text-align:center'>" +
                    "   <td>4</td>" +
                    "   <td>27</td></tr>" +
                    "<tr style='text-align:center'>" +
                    "   <td>5</td>" +
                    "   <td>52</td></tr>" +
                    "<tr style='text-align:center'>" +
                    "   <td>6</td>" +
                    "   <td>94</td></tr>" +
                    "</table>" +
                    "<p align=\"center\">" +
                    "NOTE: The maximum luminous intensity of navigation lights should be limited to avoid undue glare. This shall not be achieved by a variable control of the luminous intensity." +
                    "</p>" +

                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 47) {
            rules = "<html><body style='padding:10px'><h3>Section 9a (Horizontal sectors)</h3>" +
                    "<p align=\"justify\">" +
                    "(a)" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) In the forward direction, sidelights as fitted on the vessel shall show the minimum required intensities. The intensities shall decrease to reach practical cut-off between 1° and 3° outside the prescribed sectors.<br/><br/>" +
                    "(ii) For sternlights and masthead lights and at 22.5° abaft the beam for sidelights, the minimum required intensities shall be maintained over the arc of the horizon up to 5° within the limits of the sectors prescribed in Rule 21. From 5° within the prescribed sectors the intensity may decrease by 50% up to the prescribed limits; it shall decrease steadily to reach practical cut-off at not more than 5° outside the prescribed sectors.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 48) {
            rules = "<html><body style='padding:10px'><h3>Section 9b (Horizontal sectors)</h3>" +
                    "<p align=\"justify\">" +
                    "(b)" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) All-round lights shall be so located as not to be obscured by masts, topmasts or structures within angular sectors of more than 6°, except anchor lights prescribed in Rule 30, which need not be placed at an impractical height above the hull.<br/><br/>" +
                    "(ii) If it is impracticable to comply with paragraph (b)(i) of this section by exhibiting only one all-round light, two all-round lights shall be used suitably positioned or screened so that they appear, as far as practicable, as one light at a distance of one mile.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 49) {
            rules = "<html><body style='padding:10px'><h3>Section 10 (Vertical sectors)</h3>" +
                    "<p align=\"justify\">" +
                    "(a) The vertical sectors of electric lights as fitted, with the exception of lights on sailing vessels underway, shall ensure that:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "(i) at least the required minimum intensity is maintained at all angles from 5° above to 5° below the horizontal;<br/><br/>" +
                    "(ii) at least 60% of the required intensity is maintained from 7.5° above to 7.5° below the horizontal.<br/>" +
                    "</p>" +
                    "<p align=\"justify\">" +
                    "(b) In the case of sailing vessels underway the vertical sectors of electric lights as fitted shall ensure that:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) at least the required minimum intensity is maintained at all angles from 5° above to 5° below the horizontal;<br/><br/>" +
                        "(ii) at least 50% of the required minimum intensity is maintained from 25° above to 25° below the horizontal.<br/>" +
                    "</p>" +
                    "<p align=\"justify\">" +
                    "(c) In the case of lights other than electric these specifications shall be met as closely as possible.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 50) {
            rules = "<html><body style='padding:10px'><h3>Section 11 (Intensity of non-electric lights)</h3>" +
                    "<p align=\"justify\">" +
                    "Non-electric lights shall so far as practicable comply with the minimum intensities, as specified in the table given in section 8 of this annex.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 51) {
            rules = "<html><body style='padding:10px'><h3>Section 12 (Manoeuvring light)</h3>" +
                    "<p align=\"justify\">" +
                    "Notwithstanding the provisions of paragraph 2 (f) of this annex the manoeuvring light described in Rule 34 (b) shall be placed in the same fore-and-aft vertical plane as the masthead light or lights and, where practicable, at a minimum height of 2 m vertically above the forward masthead light, provided that it shall be carried not less than 2 m vertically above or below the after masthead light. On a vessel where only one masthead light is carried the manoeuvring light is carried, the manoeuvring light, if fitted, shall be carried where it can best be seen, not less than 2 m vertically apart from the masthead light.<br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 52) {
            rules = "<html><body style='padding:10px'><h3>Section 13 (High-speed craft*)</h3>" +
                    "<p align=\"justify\">" +
                    "(a) The masthead light of high-speed craft may be placed at a height related to the breadth of the craft lower than prescribed in paragraph 2 (a)(i) of this annex, provided that the base angle of the isosceles triangles formed by the sidelights and masthead light, when seen in end elevation, is not less than 27°.<br/><br/>" +
                    "(b) On high sped-craft of 50 m or more in length, the vertical separation between foremast and mainmast of 4.5 m required by paragraph 2 (a)(ii) of this annex may be modified provided that such distance shall not be less than the value determined by the following formula:<br/>" +
                    "</p>" +
                    "<p align='center'>y = <u>(a + 17Ψ)C</u> + 2<br/>1000</p>" +
                    "<p align=\"justify\">where:</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                    "y is the height of the mainmast light above the foremast light in metres;<br/><br/>" +
                    "a is the height of the foremast light above the water surface in service condition in metres;<br/><br/>" +
                    "Ψ is the trim in service condition in degrees.<br/><br/>" +
                    "C is the horizontal separation of the masthead lights in metres.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "* Refer to the International Code of Safety for High-Speed Craft, 1994 and the International Code of Safety for High-Speed Craft, 2000." +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 53) {
            rules = "<html><body style='padding:10px'><h3>Section 14 (Approval)</h3>" +
                    "<p align=\"justify\">" +
                    "The construction of light and shapes and the installation of lights on board the vessel shall be to the satisfaction of the appropriate authority of the State whose flag the vessel in entitled to fly.<br/><br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=118&Itemid=499&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 54) {
            rules = "<html><body style='padding:10px'><h3>Annex II - Additional signals for fishing vessel fishing in close proximity </h3>" +
                    "<p align=\"justify\">" +
                    "1. General<br/><br/>" +
                    "The lights mentioned herein shall, is exhibited in pursuance of Rule 26 (d), be places where they can best be seen. They shall be at least 0.9 m apart but at a lower level than lights prescribed in Rule 26 (b)(i) and (c)(i). The lights shall be visible all round the horizon at a distance of at least 1 mile but at a lesser distance then the lights prescribed by these Rules for fishing vessels.<br/><br/>" +
                    "2. Signals for trawlers<br/><br/>" +
                    "(a) Vessels of 20 m or more in length when engaged in trawling, whether using demersal or pelagic gear, shall exhibit:<br/><br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px'>" +
                        "(i) when shooting their nets:<br/>" +
                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;two white lights in a vertical line;<br/>" +
                        "(ii) when hauling their nets:<br/>" +
                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;one white light over one red light in a vertical line;<br/>" +
                        "(iii) when the net has come fast upon an obstruction:<br/>" +
                        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;two red lights in a vertical line.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(b) Each vessel of 20 m or more in length engaged in pair trawling shall exhibit:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px'>" +
                        "(i) by night, a searchlight directed forwards and in the direction of the other vessel of the pair;<br/>" +
                        "(ii) when shooting or hauling their nets or when nets have come fast upon an obstruction, the lights prescribed in 2 (a) above.<br/>" +
                    "</p>" +
                    "<p align='justify'>" +
                    "(c) A vessel of less than 20 m in length engaged in trawling, whether using demersal or pelagic gear or engaged in pair trawling, may exhibit the lights prescribed in paragraphs (a) or (b) of this section, as appropriate.<br/><br/>" +
                    "3. Signals for purse seiners<br/><br/>" +
                    "Vessels engaged in fishing with purse seine gear may exhibit two yellow lights in a vertical line. These lights shall flash alternately every second and with equal lights and occultation duration. These lights may be exhibited only when the vessel is hampered by its fishing gear." +
                    "<br/><br/></p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=119&Itemid=500&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 55) {
            rules = "<html><body style='padding:10px'><h3>Annex III - Details of sound signal appliances  </h3>" +
                    "<p align=\"justify\">" +
                    "1. Whistles<br/><br/>" +
                    "(a) Frequencies and range of audibility<br/><br/>" +
                    "The fundamental frequency of the signal shall lie within the range 70-700 Hz. The range of audibility of the signal from a whistle shall be determined by those frequencies, which may include the fundamental and/or one or more higher frequencies, which lie within the range 180-700Hz (± 1%) for a vessel of 20 m or more in length, or 180-2100 Hz(±1%) for a vessel of less than 20 m in length and which provide the sound pressure levels specified in paragraph 1 (c) below.<br/><br/>" +
                    "(b) Limits of fundamental frequencies<br/><br/>" +
                    "To ensure a wide variety of whistle characteristics, the fundamental frequency of a whistle shall be between the following limits:" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px'>" +
                        "(i) 70-200 Hz, for a vessel 200 m or more in length;<br/>" +
                        "(ii) 130-350 Hz, for a vessel 75 m but less than 200 m in length;<br/>" +
                        "(iii) 250-700 Hz, for a vessel less than 75 m in length.<br/>" +
                    "</p>" +
                    "<p align=\"justify\">" +
                    "(c) Sound signal intensity and range of audibility<br/><br/>" +
                    "A whistle fitted in a vessel shall provide, in the direction of maximum intensity of the whistle and at a distance of 1 m from it, a sound pressure level in at least one 1/3-octave band within the range of frequencies 180-700 Hz (± 1%) for a vessel of 20 m or more in length, or 180-2100 Hz (±1%) for a vessel of less than 20 m in length, of not less than the appropriate figure given in the table below." +
                    "</p>" +
                    "<table style='width:100%'>" +
                    "<tr style='text-align:center;vertical-align:top'>" +
                    "   <td>Length of vessel in metres</td>" +
                    "   <td>1/3-octave band level at 1 m in dB referred to 2x10-5 N/m2</td>" +
                    "   <td>Audibility range in nautical miles</td></tr>" +
                    "<tr style='text-align:center;vertical-align:top'>" +
                    "   <td>200 or more<br/></td>" +
                    "   <td>143</td>" +
                    "   <td>2</td>" +
                    "</tr>" +
                    "<tr style='text-align:center;vertical-align:top'>" +
                    "   <td>75 but less than 200<br/></td>" +
                    "   <td>138</td>" +
                    "   <td>1.5</td>" +
                    "</tr>" +
                    "<tr style='text-align:center;vertical-align:top'>" +
                    "   <td>20 but less than 75<br/></td>" +
                    "   <td>130</td>" +
                    "   <td>1</td>" +
                    "</tr>" +
                    "<tr style='text-align:center;vertical-align:top'>" +
                    "   <td>Less than 20</td>" +
                    "   <td>120*<br/>115**<br/>111***</td>" +
                    "   <td>0.5</td>" +
                    "</tr>" +
                    "</table>" +
                    "<p align='center'><br/>" +
                    "* When the measured frequencies lie within the range 180-450 Hz<br/>" +
                    "** When the measured frequencies lie within the range 450-800 Hz<br/>" +
                    "*** When the measured frequencies lie within the range 800-2100 Hz<br/></p>" +
                    "<p align='justify'>" +
                    "The range of audibility in the table above is for information and is approximately the range at which a whistle may be heard on its forward axis with 90% probability in conditions of still air on board a vessel having average background noise level at the listening posts (taken to be 68 dB in the octave band centred on 250 Hz and 63 dB in the octave band centred on 500 Hz).<br/><br/>" +
                    "In practice the range at which a whistle may be heard is extremely variable and depends critically on weather conditions; the values given can be regarded as typical but under conditions of strong wind or high ambient noise level at the listening post the range may be much reduced.<br/><br/>" +
                    "(d) Directional properties<br/><br/>" +
                    "The sound pressure level of a directional whistle shall be not more than 4 dB below the prescribed sound pressure level on the axis at any direction in the horizontal plane within ± 45° on the axis. The sound pressure level at any other direction in the horizontal plane shall be not more than 10 dB below the prescribed sound pressure level on the axis, so that the range in any direction will be at least half the range on the forward axis. The sound pressure level shall be measured in that 1/3-octave band which determines the audibility range.<br/><br/>" +
                    "(e) Positioning of whistles<br/><br/>" +
                    "When a directional whistle is to be used as the only whistle on a vessel, it shall be installed with its maximum intensity directed straight ahead. A whistle shall be placed as high as practicable on a vessel, in order to reduce interception of the emitted sound by obstructions and also to minimize hearing damage risk to personnel. The sound pressure level of the vessel's own signal at listening posts shall not exceed 110 dB (A) and so far as practicable should not exceed 100 dB (A).<br/><br/>" +
                    "(f) Fitting of more than one whistle<br/><br/>" +
                    "If whistles are fitted at a distance apart of more than 100 metres, if shall be so arranged that they are not sounded simultaneously.<br/><br/>" +
                    "(g) Combined whistle systems<br/><br/>" +
                    "If due to the presence of obstructions the sound field of a single whistle or of one of the whistles referred to in paragraph 1 (f) above is likely to have a zone of greatly reduced signal level, it is recommend that a combined whistle system be fitted so as to overcome this reduction. For the purposes of the Rules a combined whistle system is to be regarded as a single whistle. The whistles of a combined system shall be located at a distance apart of not more than 100 m and arranges to be sounded simultaneously. The frequency of any one whistle shall differ from those of the others by at least 10 Hz.<br/><br/>" +
                    "2. Bell or gong<br/><br/>" +
                    "(a) Intensity of signal<br/><br/>" +
                    "A bell or gong, or other device having similar sound characteristics shall produce a sound pressure level of not less than 110 dB at a distance of 1 m from it.<br/><br/>" +
                    "(b) Construction<br/><br/>" +
                    "Bells and gongs shall be made of corrosion-resistant material and designed to give a clear tone. The diameter of the mouth of the bell shall be not less than 300 mm, for vessels of 20 m or more in length. Where practicable, a power-driven bell striker is recommended to ensure constant force but manual operation shall be possible. The mass of the striker shall be not less than 3% of the mass of the bell.<br/><br/>" +
                    "3. Approval<br/><br/>" +
                    "The construction of sound signal appliances, their performance and their installation on board the vessel shall be to the satisfaction of the appropriate authority of the State whose flag the vessel is entitled to fly.<br/><br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=120&Itemid=501&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }else if(ruleNo == 56) {
            rules = "<html><body style='padding:10px'><h3>Annex IV - Distress signals</h3>" +
                    "<p align=\"justify\">" +
                    "1. The following signals, used or exhibited either together or separately, indicate distress and need of assistance:<br/><br/>" +
                    "(a) a gun or other explosive signal fired at intervals of about a minute;<br/><br/>" +
                    "(b) a continuous sounding with any fog signalling apparatus;<br/><br/>" +
                    "(c) rockets or shells, throwing red stars fired one at a time at short intervals;<br/><br/>" +
                    "(d) a signal made by any signalling method consisting of the group . . . - - - . . . (SOS) in the Morse Code;<br/><br/>" +
                    "(e) a signal sent by radiotelephony consisting of the spoken word \"MAYDAY\";<br/><br/>" +
                    "(f) the International Code Signal of distress indicated by N.C.;<br/><br/>" +
                    "(g) a signal consisting of a square flag having above or below it a ball or anything resembling a ball;<br/><br/>" +
                    "(h) flames on the vessel (as from a burning tar barrel, oil barrel, etc.);<br/><br/>" +
                    "(i) a rocket parachute flare or hand flare showing a red light;<br/><br/>" +
                    "(j) a smoke signal giving off orange-coloured smoke;<br/><br/>" +
                    "(k) slowly and repeatedly raising and lowering arms outstretched to each side;<br/><br/>" +
                    "(l) a distress alert by means of digital selective calling (DSC) transmitted on:<br/><br/>" +
                    "</p>" +
                    "<p align='justify' style='margin-left:20px;margin-right:20px;'>" +
                        "(i) VHF channel 70; or<br/><br/>" +
                        "(ii) MF/HF on the frequencies 2187.5 kHZ, 8414.5 kHZ, 4207.5 kHZ, 6312 kHZ, 12577 kHZ or 16804.5 kHZ;<br/>" +
                    "</p>" +
                    "<p align=\"justify\">" +
                    "(m) a ship-to-shore distress alert transmitted by the ship's Inmarsat or other mobile satellite service provider ship earth station;<br/><br/>" +
                    "(n) signals transmitted by emergency position indicating radio beacons;<br/><br/>" +
                    "(o) approved signals transmitted by radiocommunication systems, including survival craft radar transponders.<br/><br/>" +
                    "2. The use or exhibition of any of the foregoing signals except for the purpose of indicating distress and need of assistance and the use of other signals which may be confused with any of the above signals, is prohibited.<br/><br/>" +
                    "3. Attention is drawn to the relevant sections of the International Code of Signals, the International Aeronautical and Maritime Search and Rescue Manual, Volume III and the following signals:<br/><br/>" +
                    "(a) a piece of orange coloured canvas with either a black square and circle or other appropriate symbol (for identification from the air);<br/><br/>" +
                    "(b) a dye marker.<br/><br/>" +
                    "</p>" +
                    "<p style='font-size:8px'><i>Source : https://www.ecolregs.com/index.php?option=com_k2&view=item&layout=item&id=121&Itemid=502&lang=en / https://www.imo.org/en/OurWork/Safety/Pages/Preventing-Collisions.aspx</i></p></body></html>";
        }
        Log.d("Rules", ruleNo + rules);
        return rules;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(InternationalRegActivity.this, MainActivity.class);
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
