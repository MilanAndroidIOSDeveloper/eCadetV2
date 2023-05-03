package com.elosoftbiz.etrb_trmf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShipParticularFormActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Context context;
    TextView tv_title, tv_dimension, tv_main_engine, tv_auxiliary,tv_auxiliary_boiler, tv_fuel, tv_others, tv_mooring, tv_anchors, tv_lifesaving, tv_fire, tv_cargo_handling, tv_navigational;
    LinearLayout subjects_container;
    ProgressDialog pd;
    DatabaseHelper db;

    Button btn_cancel, btn_delete, btn_save, btn_filename;
    String person_id, person_vessel_id = "", vessel_id, dept, filename = "", imageFileName, currentPhotoPath;
    Spinner spinner_vessel_id;
    List<String> vesselArray =  new ArrayList<>();
    TextInputLayout et_imo_number, et_call_sign, et_flag, et_length_over_all, et_breadth, et_depth, et_summer_draft, et_summer_freeboard, et_gross_tonnage, et_net_tonnage, et_dead_weight, et_light_displacement, et_fresh_water, et_immersion_at_load, et_trimming_moment_d, et_bale_capacity_d, et_grain_capacity_d, et_liquid_capacity_d, et_refrigerated_capacity_d, et_container_capacity_d, et_fresh_water_capacity_d, et_daily_fresh_water_gen_d, et_daily_fresh_water_con_d, et_main_engine_make_e, et_main_engine_type, et_main_engine_stroke_e, et_main_engine_bore_e, et_main_engine_output, et_main_engine_reduction_gear_e, et_main_engine_turbo_charger_e, et_main_engine_service_speed, et_main_engine_boiler_d, et_main_engine_bunker_capacity_d, et_main_engine_daily_consumption_d, et_main_engine_steering_gear_d, et_auxiliary_make_e, et_auxiliary_type_e, et_auxiliary_stroke_e, et_auxiliary_bore_e, et_auxiliary_output_e, et_auxiliary_turbo_charger_e, et_auxiliary_normal_electrical_e, et_auxiliary_boiler_make_e, et_auxiliary_boiler_working_pressure_e, et_auxiliary_boiler_type_waste_e, et_fuel_main_engine_fuel_type_e, et_fuel_viscosity_e, et_fuel_specific_fuel_con_e, et_fuel_boiler_fuel_type_e, et_fuel_viscosity_range_e, et_fuel_generator_fuel_type_e, et_fuel_bunker_capacity_e, et_fuel_daily_con_e, et_others_heavy_fuel_oil_e, et_others_lub_oil_purifier_e, et_others_air_compressor_e, et_others_oily_water_separator_e, et_others_water_capacity_fw_e, et_others_water_capacity_dw_e, et_others_fw_generator_e, et_others_av_cons_e, et_others_steering_type_e, et_others_er_lifting_gear_e, et_others_swl_e, et_others_sewage_treatment_e, et_mooring_natural_fiber_d, et_mooring_synthetic_fiber_d, et_mooring_wires_d, et_mooring_towing_spring_d, et_anchors_port, et_anchors_starboard, et_anchors_stern_d, et_anchors_spare, et_anchors_cable, et_lifesaving_lifeboat_type_d, et_lifesaving_lifeboat_no, et_lifesaving_liferaft_no, et_lifesaving_lifeboat_dimension_d, et_lifesaving_lifeboat_capacity, et_lifesaving_liferaft_capacity, et_lifesaving_lifeboat_davits, et_lifesaving_lifeboat_fall, et_lifesaving_lifebuoys_no, et_fire_extinguisher_no, et_fire_water, et_fire_foam, et_fire_dry_powder, et_fire_co2, et_fire_firehoses, et_fire_breathing_e, et_fire_breathing_no_e, et_fire_fixed_fire_system_d, et_fire_scba_d, et_cargo_handling_derricks, et_cargo_handling_cranes, et_cargo_handling_winches, et_cargo_handling_other_d, et_cargo_handling_ballast_d, et_cargo_handling_tank_d, et_cargo_handling_pump_no, et_cargo_handling_pipelines, et_cargo_handling_type_rating_e, et_cargo_handling_ballast_pump_e, et_navigational_radar_d, et_navigational_log_d, et_navigational_gps_d, et_navigational_magnetic_d, et_navigational_gyro_d, et_navigational_echo_d, et_navigational_auto_d, et_navigational_vhf_d, et_navigational_mf_hf_d, et_navigational_sat_d, et_navigational_ecdis_d, et_navigational_sart_d, et_navigational_navtex_d, et_navigational_ais_d, et_navigational_vdr_d, et_checked_by_id, et_app_by_id, et_date_checked, et_date_app, et_checked_remarks, et_app_remarks;
    LinearLayout ll_auxiliary,ll_auxiliary_boiler, ll_fuel, ll_others, ll_mooring, ll_anchors, ll_navigational, approval_container;
    TextView tv_filename;
    String str = "", err_message, upLoadServerUri;
    HttpResponse response;
    int serverResponseCode = 0;
    JSONObject json = null;
    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_particular_form);

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
        person_id = db.getCadetId();
        dept = db.getFieldString("dept", " person_id = '"+person_id+"'", "person");
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

        Intent intent = getIntent();
        if (intent.hasExtra("person_vessel_id")) {
            person_vessel_id = intent.getStringExtra("person_vessel_id");
        }

        //GET VIEWS
        spinner_vessel_id = findViewById(R.id.spinner_vessel_id);
        tv_dimension = findViewById(R.id.tv_dimension);
        tv_main_engine = findViewById(R.id.tv_main_engine);
        tv_auxiliary = findViewById(R.id.tv_auxiliary);
        tv_auxiliary_boiler = findViewById(R.id.tv_auxiliary_boiler);
        tv_fuel = findViewById(R.id.tv_fuel);
        tv_others = findViewById(R.id.tv_others);
        tv_mooring = findViewById(R.id.tv_mooring);
        tv_anchors = findViewById(R.id.tv_anchors);
        tv_lifesaving = findViewById(R.id.tv_lifesaving);
        tv_fire = findViewById(R.id.tv_fire);
        tv_cargo_handling = findViewById(R.id.tv_cargo_handling);
        tv_navigational = findViewById(R.id.tv_navigational);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_delete = findViewById(R.id.btn_delete);
        btn_save = findViewById(R.id.btn_save);

        ll_auxiliary = findViewById(R.id.ll_auxiliary);
        ll_auxiliary_boiler = findViewById(R.id.ll_auxiliary_boiler);
        ll_fuel = findViewById(R.id.ll_fuel);
        ll_others = findViewById(R.id.ll_others);
        ll_mooring = findViewById(R.id.ll_mooring);
        ll_anchors = findViewById(R.id.ll_anchors);
        ll_navigational = findViewById(R.id.ll_navigational);
        approval_container = findViewById(R.id.approval_container);

        et_imo_number = findViewById(R.id.et_imo_number);
        et_call_sign = findViewById(R.id.et_call_sign);
        et_flag = findViewById(R.id.et_flag);
        et_length_over_all = findViewById(R.id.et_length_over_all);
        et_breadth = findViewById(R.id.et_breadth);
        et_depth = findViewById(R.id.et_depth);
        et_summer_draft = findViewById(R.id.et_summer_draft);
        et_summer_freeboard = findViewById(R.id.et_summer_freeboard);
        et_gross_tonnage = findViewById(R.id.et_gross_tonnage);
        et_net_tonnage = findViewById(R.id.et_net_tonnage);
        et_dead_weight = findViewById(R.id.et_dead_weight);
        et_light_displacement = findViewById(R.id.et_light_displacement);
        et_fresh_water = findViewById(R.id.et_fresh_water);
        et_immersion_at_load = findViewById(R.id.et_immersion_at_load);
        et_trimming_moment_d = findViewById(R.id.et_trimming_moment_d);
        et_bale_capacity_d = findViewById(R.id.et_bale_capacity_d);
        et_grain_capacity_d = findViewById(R.id.et_grain_capacity_d);
        et_liquid_capacity_d = findViewById(R.id.et_liquid_capacity_d);
        et_refrigerated_capacity_d = findViewById(R.id.et_refrigerated_capacity_d);
        et_container_capacity_d = findViewById(R.id.et_container_capacity_d);
        et_fresh_water_capacity_d = findViewById(R.id.et_fresh_water_capacity_d);
        et_daily_fresh_water_gen_d = findViewById(R.id.et_daily_fresh_water_gen_d);
        et_daily_fresh_water_con_d = findViewById(R.id.et_daily_fresh_water_con_d);
        et_main_engine_make_e = findViewById(R.id.et_main_engine_make_e);
        et_main_engine_type = findViewById(R.id.et_main_engine_type);
        et_main_engine_stroke_e = findViewById(R.id.et_main_engine_stroke_e);
        et_main_engine_bore_e = findViewById(R.id.et_main_engine_bore_e);
        et_main_engine_output = findViewById(R.id.et_main_engine_output);
        et_main_engine_reduction_gear_e = findViewById(R.id.et_main_engine_reduction_gear_e);
        et_main_engine_turbo_charger_e = findViewById(R.id.et_main_engine_turbo_charger_e);
        et_main_engine_service_speed = findViewById(R.id.et_main_engine_service_speed);
        et_main_engine_boiler_d = findViewById(R.id.et_main_engine_boiler_d);
        et_main_engine_bunker_capacity_d = findViewById(R.id.et_main_engine_bunker_capacity_d);
        et_main_engine_daily_consumption_d = findViewById(R.id.et_main_engine_daily_consumption_d);
        et_main_engine_steering_gear_d = findViewById(R.id.et_main_engine_steering_gear_d);
        et_auxiliary_make_e = findViewById(R.id.et_auxiliary_make_e);
        et_auxiliary_type_e = findViewById(R.id.et_auxiliary_type_e);
        et_auxiliary_stroke_e = findViewById(R.id.et_auxiliary_stroke_e);
        et_auxiliary_bore_e = findViewById(R.id.et_auxiliary_bore_e);
        et_auxiliary_output_e = findViewById(R.id.et_auxiliary_output_e);
        et_auxiliary_turbo_charger_e = findViewById(R.id.et_auxiliary_turbo_charger_e);
        et_auxiliary_normal_electrical_e = findViewById(R.id.et_auxiliary_normal_electrical_e);
        et_auxiliary_boiler_make_e = findViewById(R.id.et_auxiliary_boiler_make_e);
        et_auxiliary_boiler_working_pressure_e = findViewById(R.id.et_auxiliary_boiler_working_pressure_e);
        et_auxiliary_boiler_type_waste_e = findViewById(R.id.et_auxiliary_boiler_type_waste_e);
        et_fuel_main_engine_fuel_type_e = findViewById(R.id.et_fuel_main_engine_fuel_type_e);
        et_fuel_viscosity_e = findViewById(R.id.et_fuel_viscosity_e);
        et_fuel_specific_fuel_con_e = findViewById(R.id.et_fuel_specific_fuel_con_e);
        et_fuel_boiler_fuel_type_e = findViewById(R.id.et_fuel_boiler_fuel_type_e);
        et_fuel_viscosity_range_e = findViewById(R.id.et_fuel_viscosity_range_e);
        et_fuel_generator_fuel_type_e = findViewById(R.id.et_fuel_generator_fuel_type_e);
        et_fuel_bunker_capacity_e = findViewById(R.id.et_fuel_bunker_capacity_e);
        et_fuel_daily_con_e = findViewById(R.id.et_fuel_daily_con_e);
        et_others_heavy_fuel_oil_e = findViewById(R.id.et_others_heavy_fuel_oil_e);
        et_others_lub_oil_purifier_e = findViewById(R.id.et_others_lub_oil_purifier_e);
        et_others_air_compressor_e = findViewById(R.id.et_others_air_compressor_e);
        et_others_oily_water_separator_e = findViewById(R.id.et_others_oily_water_separator_e);
        et_others_water_capacity_fw_e = findViewById(R.id.et_others_water_capacity_fw_e);
        et_others_water_capacity_dw_e = findViewById(R.id.et_others_water_capacity_dw_e);
        et_others_fw_generator_e = findViewById(R.id.et_others_fw_generator_e);
        et_others_av_cons_e = findViewById(R.id.et_others_av_cons_e);
        et_others_steering_type_e = findViewById(R.id.et_others_steering_type_e);
        et_others_er_lifting_gear_e = findViewById(R.id.et_others_er_lifting_gear_e);
        et_others_swl_e = findViewById(R.id.et_others_swl_e);
        et_others_sewage_treatment_e = findViewById(R.id.et_others_sewage_treatment_e);
        et_mooring_natural_fiber_d = findViewById(R.id.et_mooring_natural_fiber_d);
        et_mooring_synthetic_fiber_d = findViewById(R.id.et_mooring_synthetic_fiber_d);
        et_mooring_wires_d = findViewById(R.id.et_mooring_wires_d);
        et_mooring_towing_spring_d = findViewById(R.id.et_mooring_towing_spring_d);
        et_anchors_port = findViewById(R.id.et_anchors_port);
        et_anchors_starboard = findViewById(R.id.et_anchors_starboard);
        et_anchors_stern_d = findViewById(R.id.et_anchors_stern_d);
        et_anchors_spare = findViewById(R.id.et_anchors_spare);
        et_anchors_cable = findViewById(R.id.et_anchors_cable);
        et_lifesaving_lifeboat_type_d = findViewById(R.id.et_lifesaving_lifeboat_type_d);
        et_lifesaving_lifeboat_no = findViewById(R.id.et_lifesaving_lifeboat_no);
        et_lifesaving_liferaft_no = findViewById(R.id.et_lifesaving_liferaft_no);
        et_lifesaving_lifeboat_dimension_d = findViewById(R.id.et_lifesaving_lifeboat_dimension_d);
        et_lifesaving_lifeboat_capacity = findViewById(R.id.et_lifesaving_lifeboat_capacity);
        et_lifesaving_liferaft_capacity = findViewById(R.id.et_lifesaving_liferaft_capacity);
        et_lifesaving_lifeboat_davits = findViewById(R.id.et_lifesaving_lifeboat_davits);
        et_lifesaving_lifeboat_fall = findViewById(R.id.et_lifesaving_lifeboat_fall);
        et_lifesaving_lifebuoys_no = findViewById(R.id.et_lifesaving_lifebuoys_no);
        et_fire_extinguisher_no = findViewById(R.id.et_fire_extinguisher_no);
        et_fire_water = findViewById(R.id.et_fire_water);
        et_fire_foam = findViewById(R.id.et_fire_foam);
        et_fire_dry_powder = findViewById(R.id.et_fire_dry_powder);
        et_fire_co2 = findViewById(R.id.et_fire_co2);
        et_fire_firehoses = findViewById(R.id.et_fire_firehoses);
        et_fire_breathing_e = findViewById(R.id.et_fire_breathing_e);
        et_fire_breathing_no_e = findViewById(R.id.et_fire_breathing_no_e);
        et_fire_fixed_fire_system_d = findViewById(R.id.et_fire_fixed_fire_system_d);
        et_fire_scba_d = findViewById(R.id.et_fire_scba_d);
        et_cargo_handling_derricks = findViewById(R.id.et_cargo_handling_derricks);
        et_cargo_handling_cranes = findViewById(R.id.et_cargo_handling_cranes);
        et_cargo_handling_winches = findViewById(R.id.et_cargo_handling_winches);
        et_cargo_handling_other_d = findViewById(R.id.et_cargo_handling_other_d);
        et_cargo_handling_ballast_d = findViewById(R.id.et_cargo_handling_ballast_d);
        et_cargo_handling_tank_d = findViewById(R.id.et_cargo_handling_tank_d);
        et_cargo_handling_pump_no = findViewById(R.id.et_cargo_handling_pump_no);
        et_cargo_handling_pipelines = findViewById(R.id.et_cargo_handling_pipelines);
        et_cargo_handling_type_rating_e = findViewById(R.id.et_cargo_handling_type_rating_e);
        et_cargo_handling_ballast_pump_e = findViewById(R.id.et_cargo_handling_ballast_pump_e);
        et_navigational_radar_d = findViewById(R.id.et_navigational_radar_d);
        et_navigational_log_d = findViewById(R.id.et_navigational_log_d);
        et_navigational_gps_d = findViewById(R.id.et_navigational_gps_d);
        et_navigational_magnetic_d = findViewById(R.id.et_navigational_magnetic_d);
        et_navigational_gyro_d = findViewById(R.id.et_navigational_gyro_d);
        et_navigational_echo_d = findViewById(R.id.et_navigational_echo_d);
        et_navigational_auto_d = findViewById(R.id.et_navigational_auto_d);
        et_navigational_vhf_d = findViewById(R.id.et_navigational_vhf_d);
        et_navigational_mf_hf_d = findViewById(R.id.et_navigational_mf_hf_d);
        et_navigational_sat_d = findViewById(R.id.et_navigational_sat_d);
        et_navigational_ecdis_d = findViewById(R.id.et_navigational_ecdis_d);
        et_navigational_sart_d = findViewById(R.id.et_navigational_sart_d);
        et_navigational_navtex_d = findViewById(R.id.et_navigational_navtex_d);
        et_navigational_ais_d = findViewById(R.id.et_navigational_ais_d);
        et_navigational_vdr_d = findViewById(R.id.et_navigational_vdr_d);

        btn_filename = findViewById(R.id.btn_filename);
        tv_filename  = findViewById(R.id.tv_filename);

        vesselArray.add("Select Ship *");
        List<Vessel> vessels = db.getVessels();
        for(Vessel vessel : vessels){
            vesselArray.add(vessel.getName_vessel());
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, vesselArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_vessel_id.setAdapter(arrayAdapter);
        spinner_vessel_id.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vessel_id  = parent.getItemAtPosition(position).toString();
                TextView textView = (TextView)parent.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.black));
                textView.setTextSize(15);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //LIMIT VIEWS DEPENDS ON DEPT
        if(dept.equals("DECK")){ //ENGINE SPECIFIC FIELD INVIVSIBLE
            ll_auxiliary.setVisibility(View.GONE);
            ll_auxiliary_boiler.setVisibility(View.GONE);
            ll_fuel.setVisibility(View.GONE);
            ll_others.setVisibility(View.GONE);


        }else{
            ll_mooring.setVisibility(View.GONE);
            ll_navigational.setVisibility(View.GONE);
        }

        if(person_vessel_id.equals("")){ //ADD
            btn_delete.setVisibility(View.GONE);
            approval_container.setVisibility(View.GONE);
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String vessel_id_ = db.getFieldString("vessel_id", " name_vessel='"+vessel_id+"'", "vessel");
                    int cnt = db.GetCount("person_vessel", " WHERE vessel_id = '"+ vessel_id_ +"'");
                    if(cnt > 0){
                        Toast.makeText(ShipParticularFormActivity.this, "An existing record with the ship selected has been detected. Adding new is not allowed", Toast.LENGTH_LONG).show();
                        return;
                    }else{
                        String imo_number = et_imo_number.getEditText().getText().toString();
                        String call_sign = et_call_sign.getEditText().getText().toString();
                        String flag = et_flag.getEditText().getText().toString();
                        String length_over_all = et_length_over_all.getEditText().getText().toString();
                        String breadth = et_breadth.getEditText().getText().toString();
                        String depth = et_depth.getEditText().getText().toString();
                        String summer_draft = et_summer_draft.getEditText().getText().toString();
                        String summer_freeboard = et_summer_freeboard.getEditText().getText().toString();
                        String gross_tonnage = et_gross_tonnage.getEditText().getText().toString();
                        String net_tonnage = et_net_tonnage.getEditText().getText().toString();
                        String dead_weight = et_dead_weight.getEditText().getText().toString();
                        String light_displacement = et_light_displacement.getEditText().getText().toString();
                        String fresh_water = et_fresh_water.getEditText().getText().toString();
                        String immersion_at_load = et_immersion_at_load.getEditText().getText().toString();
                        String trimming_moment_d = et_trimming_moment_d.getEditText().getText().toString();
                        String bale_capacity_d = et_bale_capacity_d.getEditText().getText().toString();
                        String grain_capacity_d = et_grain_capacity_d.getEditText().getText().toString();
                        String liquid_capacity_d = et_liquid_capacity_d.getEditText().getText().toString();
                        String refrigerated_capacity_d = et_refrigerated_capacity_d.getEditText().getText().toString();
                        String container_capacity_d = et_container_capacity_d.getEditText().getText().toString();
                        String fresh_water_capacity_d = et_fresh_water_capacity_d.getEditText().getText().toString();
                        String daily_fresh_water_gen_d = et_daily_fresh_water_gen_d.getEditText().getText().toString();
                        String daily_fresh_water_con_d = et_daily_fresh_water_con_d.getEditText().getText().toString();
                        String main_engine_make_e = et_main_engine_make_e.getEditText().getText().toString();
                        String main_engine_type = et_main_engine_type.getEditText().getText().toString();
                        String main_engine_stroke_e = et_main_engine_stroke_e.getEditText().getText().toString();
                        String main_engine_bore_e = et_main_engine_bore_e.getEditText().getText().toString();
                        String main_engine_output = et_main_engine_output.getEditText().getText().toString();
                        String main_engine_reduction_gear_e = et_main_engine_reduction_gear_e.getEditText().getText().toString();
                        String main_engine_turbo_charger_e = et_main_engine_turbo_charger_e.getEditText().getText().toString();
                        String main_engine_service_speed = et_main_engine_service_speed.getEditText().getText().toString();
                        String main_engine_boiler_d = et_main_engine_boiler_d.getEditText().getText().toString();
                        String main_engine_bunker_capacity_d = et_main_engine_bunker_capacity_d.getEditText().getText().toString();
                        String main_engine_daily_consumption_d = et_main_engine_daily_consumption_d.getEditText().getText().toString();
                        String main_engine_steering_gear_d = et_main_engine_steering_gear_d.getEditText().getText().toString();
                        String auxiliary_make_e = et_auxiliary_make_e.getEditText().getText().toString();
                        String auxiliary_type_e = et_auxiliary_type_e.getEditText().getText().toString();
                        String auxiliary_stroke_e = et_auxiliary_stroke_e.getEditText().getText().toString();
                        String auxiliary_bore_e = et_auxiliary_bore_e.getEditText().getText().toString();
                        String auxiliary_output_e = et_auxiliary_output_e.getEditText().getText().toString();
                        String auxiliary_turbo_charger_e = et_auxiliary_turbo_charger_e.getEditText().getText().toString();
                        String auxiliary_normal_electrical_e = et_auxiliary_normal_electrical_e.getEditText().getText().toString();
                        String auxiliary_boiler_make_e = et_auxiliary_boiler_make_e.getEditText().getText().toString();
                        String auxiliary_boiler_working_pressure_e = et_auxiliary_boiler_working_pressure_e.getEditText().getText().toString();
                        String auxiliary_boiler_type_waste_e = et_auxiliary_boiler_type_waste_e.getEditText().getText().toString();
                        String fuel_main_engine_fuel_type_e = et_fuel_main_engine_fuel_type_e.getEditText().getText().toString();
                        String fuel_viscosity_e = et_fuel_viscosity_e.getEditText().getText().toString();
                        String fuel_specific_fuel_con_e = et_fuel_specific_fuel_con_e.getEditText().getText().toString();
                        String fuel_boiler_fuel_type_e = et_fuel_boiler_fuel_type_e.getEditText().getText().toString();
                        String fuel_viscosity_range_e = et_fuel_viscosity_range_e.getEditText().getText().toString();
                        String fuel_generator_fuel_type_e = et_fuel_generator_fuel_type_e.getEditText().getText().toString();
                        String fuel_bunker_capacity_e = et_fuel_bunker_capacity_e.getEditText().getText().toString();
                        String fuel_daily_con_e = et_fuel_daily_con_e.getEditText().getText().toString();
                        String others_heavy_fuel_oil_e = et_others_heavy_fuel_oil_e.getEditText().getText().toString();
                        String others_lub_oil_purifier_e = et_others_lub_oil_purifier_e.getEditText().getText().toString();
                        String others_air_compressor_e = et_others_air_compressor_e.getEditText().getText().toString();
                        String others_oily_water_separator_e = et_others_oily_water_separator_e.getEditText().getText().toString();
                        String others_water_capacity_fw_e = et_others_water_capacity_fw_e.getEditText().getText().toString();
                        String others_water_capacity_dw_e = et_others_water_capacity_dw_e.getEditText().getText().toString();
                        String others_fw_generator_e = et_others_fw_generator_e.getEditText().getText().toString();
                        String others_av_cons_e = et_others_av_cons_e.getEditText().getText().toString();
                        String others_steering_type_e = et_others_steering_type_e.getEditText().getText().toString();
                        String others_er_lifting_gear_e = et_others_er_lifting_gear_e.getEditText().getText().toString();
                        String others_swl_e = et_others_swl_e.getEditText().getText().toString();
                        String others_sewage_treatment_e = et_others_sewage_treatment_e.getEditText().getText().toString();
                        String mooring_natural_fiber_d = et_mooring_natural_fiber_d.getEditText().getText().toString();
                        String mooring_synthetic_fiber_d = et_mooring_synthetic_fiber_d.getEditText().getText().toString();
                        String mooring_wires_d = et_mooring_wires_d.getEditText().getText().toString();
                        String mooring_towing_spring_d = et_mooring_towing_spring_d.getEditText().getText().toString();
                        String anchors_port = et_anchors_port.getEditText().getText().toString();
                        String anchors_starboard = et_anchors_starboard.getEditText().getText().toString();
                        String anchors_stern_d = et_anchors_stern_d.getEditText().getText().toString();
                        String anchors_spare = et_anchors_spare.getEditText().getText().toString();
                        String anchors_cable = et_anchors_cable.getEditText().getText().toString();
                        String lifesaving_lifeboat_type_d = et_lifesaving_lifeboat_type_d.getEditText().getText().toString();
                        String lifesaving_lifeboat_no = et_lifesaving_lifeboat_no.getEditText().getText().toString();
                        String lifesaving_liferaft_no = et_lifesaving_liferaft_no.getEditText().getText().toString();
                        String lifesaving_lifeboat_dimension_d = et_lifesaving_lifeboat_dimension_d.getEditText().getText().toString();
                        String lifesaving_lifeboat_capacity = et_lifesaving_lifeboat_capacity.getEditText().getText().toString();
                        String lifesaving_liferaft_capacity = et_lifesaving_liferaft_capacity.getEditText().getText().toString();
                        String lifesaving_lifeboat_davits = et_lifesaving_lifeboat_davits.getEditText().getText().toString();
                        String lifesaving_lifeboat_fall = et_lifesaving_lifeboat_fall.getEditText().getText().toString();
                        String lifesaving_lifebuoys_no = et_lifesaving_lifebuoys_no.getEditText().getText().toString();
                        String fire_extinguisher_no = et_fire_extinguisher_no.getEditText().getText().toString();
                        String fire_water = et_fire_water.getEditText().getText().toString();
                        String fire_foam = et_fire_foam.getEditText().getText().toString();
                        String fire_dry_powder = et_fire_dry_powder.getEditText().getText().toString();
                        String fire_co2 = et_fire_co2.getEditText().getText().toString();
                        String fire_firehoses = et_fire_firehoses.getEditText().getText().toString();
                        String fire_breathing_e = et_fire_breathing_e.getEditText().getText().toString();
                        String fire_breathing_no_e = et_fire_breathing_no_e.getEditText().getText().toString();
                        String fire_fixed_fire_system_d = et_fire_fixed_fire_system_d.getEditText().getText().toString();
                        String fire_scba_d = et_fire_scba_d.getEditText().getText().toString();
                        String cargo_handling_derricks = et_cargo_handling_derricks.getEditText().getText().toString();
                        String cargo_handling_cranes = et_cargo_handling_cranes.getEditText().getText().toString();
                        String cargo_handling_winches = et_cargo_handling_winches.getEditText().getText().toString();
                        String cargo_handling_other_d = et_cargo_handling_other_d.getEditText().getText().toString();
                        String cargo_handling_ballast_d = et_cargo_handling_ballast_d.getEditText().getText().toString();
                        String cargo_handling_tank_d = et_cargo_handling_tank_d.getEditText().getText().toString();
                        String cargo_handling_pump_no = et_cargo_handling_pump_no.getEditText().getText().toString();
                        String cargo_handling_pipelines = et_cargo_handling_pipelines.getEditText().getText().toString();
                        String cargo_handling_type_rating_e = et_cargo_handling_type_rating_e.getEditText().getText().toString();
                        String cargo_handling_ballast_pump_e = et_cargo_handling_ballast_pump_e.getEditText().getText().toString();
                        String navigational_radar_d = et_navigational_radar_d.getEditText().getText().toString();
                        String navigational_log_d = et_navigational_log_d.getEditText().getText().toString();
                        String navigational_gps_d = et_navigational_gps_d.getEditText().getText().toString();
                        String navigational_magnetic_d = et_navigational_magnetic_d.getEditText().getText().toString();
                        String navigational_gyro_d = et_navigational_gyro_d.getEditText().getText().toString();
                        String navigational_echo_d = et_navigational_echo_d.getEditText().getText().toString();
                        String navigational_auto_d = et_navigational_auto_d.getEditText().getText().toString();
                        String navigational_vhf_d = et_navigational_vhf_d.getEditText().getText().toString();
                        String navigational_mf_hf_d = et_navigational_mf_hf_d.getEditText().getText().toString();
                        String navigational_sat_d = et_navigational_sat_d.getEditText().getText().toString();
                        String navigational_ecdis_d = et_navigational_ecdis_d.getEditText().getText().toString();
                        String navigational_sart_d = et_navigational_sart_d.getEditText().getText().toString();
                        String navigational_navtex_d = et_navigational_navtex_d.getEditText().getText().toString();
                        String navigational_ais_d = et_navigational_ais_d.getEditText().getText().toString();
                        String navigational_vdr_d = et_navigational_vdr_d.getEditText().getText().toString();


                        if(vessel_id.equals("Select Ship *")){
                            Toast.makeText(ShipParticularFormActivity.this, "Ship is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(imo_number.equals("")){
                            Toast.makeText(ShipParticularFormActivity.this, "IMO Number is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(call_sign.equals("")){
                            Toast.makeText(ShipParticularFormActivity.this, "Call Sign is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(flag.equals("")){
                            Toast.makeText(ShipParticularFormActivity.this, "Flag is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(net_tonnage.equals("")){
                            Toast.makeText(ShipParticularFormActivity.this, "Net Tonnage is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(gross_tonnage.equals("")){
                            Toast.makeText(ShipParticularFormActivity.this, "Gross Tonnage is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(main_engine_output.equals("")){
                            Toast.makeText(ShipParticularFormActivity.this, "Output is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        pd = new ProgressDialog(ShipParticularFormActivity.this);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage("Processing. Please wait .... ");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();
                        String person_vessel_id = db.newId();
                        Integer id = db.newIntegerId("person_vessel");

                        db.execQuery("INSERT INTO person_vessel (person_vessel_id , id, person_id, vessel_id, imo_number, call_sign, flag, length_over_all, breadth, depth, summer_draft, summer_freeboard, gross_tonnage, net_tonnage, dead_weight, light_displacement, fresh_water, immersion_at_load, trimming_moment_d, bale_capacity_d, grain_capacity_d, liquid_capacity_d, refrigerated_capacity_d, container_capacity_d, fresh_water_capacity_d, daily_fresh_water_gen_d, daily_fresh_water_con_d, main_engine_make_e, main_engine_type, main_engine_stroke_e, main_engine_bore_e, main_engine_output, main_engine_reduction_gear_e, main_engine_turbo_charger_e, main_engine_service_speed, main_engine_boiler_d, main_engine_bunker_capacity_d, main_engine_daily_consumption_d, main_engine_steering_gear_d, auxiliary_make_e, auxiliary_type_e, auxiliary_stroke_e, auxiliary_bore_e, auxiliary_output_e, auxiliary_turbo_charger_e, auxiliary_normal_electrical_e, auxiliary_boiler_make_e, auxiliary_boiler_working_pressure_e, auxiliary_boiler_type_waste_e, fuel_main_engine_fuel_type_e, fuel_viscosity_e, fuel_specific_fuel_con_e, fuel_boiler_fuel_type_e, fuel_viscosity_range_e, fuel_generator_fuel_type_e, fuel_bunker_capacity_e, fuel_daily_con_e, others_heavy_fuel_oil_e, others_lub_oil_purifier_e, others_air_compressor_e, others_oily_water_separator_e, others_water_capacity_fw_e, others_water_capacity_dw_e, others_fw_generator_e, others_av_cons_e, others_steering_type_e, others_er_lifting_gear_e, others_swl_e, others_sewage_treatment_e, mooring_natural_fiber_d, mooring_synthetic_fiber_d, mooring_wires_d, mooring_towing_spring_d, anchors_port, anchors_starboard, anchors_stern_d, anchors_spare, anchors_cable, lifesaving_lifeboat_type_d, lifesaving_lifeboat_no, lifesaving_liferaft_no, lifesaving_lifeboat_dimension_d, lifesaving_lifeboat_capacity, lifesaving_liferaft_capacity, lifesaving_lifeboat_davits, lifesaving_lifeboat_fall, lifesaving_lifebuoys_no, fire_extinguisher_no, fire_water, fire_foam, fire_dry_powder, fire_co2, fire_firehoses, fire_breathing_e, fire_breathing_no_e, fire_fixed_fire_system_d, fire_scba_d, cargo_handling_derricks, cargo_handling_cranes, cargo_handling_winches, cargo_handling_other_d, cargo_handling_ballast_d, cargo_handling_tank_d, cargo_handling_pump_no, cargo_handling_pipelines, cargo_handling_type_rating_e, cargo_handling_ballast_pump_e, navigational_radar_d, navigational_log_d, navigational_gps_d, navigational_magnetic_d, navigational_gyro_d, navigational_echo_d, navigational_auto_d, navigational_vhf_d, navigational_mf_hf_d, navigational_sat_d, navigational_ecdis_d, navigational_sart_d, navigational_navtex_d, navigational_ais_d, navigational_vdr_d, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks filename, date_saved) VALUES ('" + person_vessel_id + "', '" + id + "', '" + person_id + "', '" + vessel_id_ + "', '" + imo_number + "', '" + call_sign + "', '" + flag + "', '" + length_over_all + "', '" + breadth + "', '" + depth + "', '" + summer_draft + "', '" + summer_freeboard + "', '" + gross_tonnage + "', '" + net_tonnage + "', '" + dead_weight + "', '" + light_displacement + "', '" + fresh_water + "', '" + immersion_at_load + "', '" + trimming_moment_d + "', '" + bale_capacity_d + "', '" + grain_capacity_d + "', '" + liquid_capacity_d + "', '" + refrigerated_capacity_d + "', '" + container_capacity_d + "', '" + fresh_water_capacity_d + "', '" + daily_fresh_water_gen_d + "', '" + daily_fresh_water_con_d + "', '" + main_engine_make_e + "', '" + main_engine_type + "', '" + main_engine_stroke_e + "', '" + main_engine_bore_e + "', '" + main_engine_output + "', '" + main_engine_reduction_gear_e + "', '" + main_engine_turbo_charger_e + "', '" + main_engine_service_speed + "', '" + main_engine_boiler_d + "', '" + main_engine_bunker_capacity_d + "', '" + main_engine_daily_consumption_d + "', '" + main_engine_steering_gear_d + "', '" + auxiliary_make_e + "', '" + auxiliary_type_e + "', '" + auxiliary_stroke_e + "', '" + auxiliary_bore_e + "', '" + auxiliary_output_e + "', '" + auxiliary_turbo_charger_e + "', '" + auxiliary_normal_electrical_e + "', '" + auxiliary_boiler_make_e + "', '" + auxiliary_boiler_working_pressure_e + "', '" + auxiliary_boiler_type_waste_e + "', '" + fuel_main_engine_fuel_type_e + "', '" + fuel_viscosity_e + "', '" + fuel_specific_fuel_con_e + "', '" + fuel_boiler_fuel_type_e + "', '" + fuel_viscosity_range_e + "', '" + fuel_generator_fuel_type_e + "', '" + fuel_bunker_capacity_e + "', '" + fuel_daily_con_e + "', '" + others_heavy_fuel_oil_e + "', '" + others_lub_oil_purifier_e + "', '" + others_air_compressor_e + "', '" + others_oily_water_separator_e + "', '" + others_water_capacity_fw_e + "', '" + others_water_capacity_dw_e + "', '" + others_fw_generator_e + "', '" + others_av_cons_e + "', '" + others_steering_type_e + "', '" + others_er_lifting_gear_e + "', '" + others_swl_e + "', '" + others_sewage_treatment_e + "', '" + mooring_natural_fiber_d + "', '" + mooring_synthetic_fiber_d + "', '" + mooring_wires_d + "', '" + mooring_towing_spring_d + "', '" + anchors_port + "', '" + anchors_starboard + "', '" + anchors_stern_d + "', '" + anchors_spare + "', '" + anchors_cable + "', '" + lifesaving_lifeboat_type_d + "', '" + lifesaving_lifeboat_no + "', '" + lifesaving_liferaft_no + "', '" + lifesaving_lifeboat_dimension_d + "', '" + lifesaving_lifeboat_capacity + "', '" + lifesaving_liferaft_capacity + "', '" + lifesaving_lifeboat_davits + "', '" + lifesaving_lifeboat_fall + "', '" + lifesaving_lifebuoys_no + "', '" + fire_extinguisher_no + "', '" + fire_water + "', '" + fire_foam + "', '" + fire_dry_powder + "', '" + fire_co2 + "', '" + fire_firehoses + "', '" + fire_breathing_e + "', '" + fire_breathing_no_e + "', '" + fire_fixed_fire_system_d + "', '" + fire_scba_d + "', '" + cargo_handling_derricks + "', '" + cargo_handling_cranes + "', '" + cargo_handling_winches + "', '" + cargo_handling_other_d + "', '" + cargo_handling_ballast_d + "', '" + cargo_handling_tank_d + "', '" + cargo_handling_pump_no + "', '" + cargo_handling_pipelines + "', '" + cargo_handling_type_rating_e + "', '" + cargo_handling_ballast_pump_e + "', '" + navigational_radar_d + "', '" + navigational_log_d + "', '" + navigational_gps_d + "', '" + navigational_magnetic_d + "', '" + navigational_gyro_d + "', '" + navigational_echo_d + "', '" + navigational_auto_d + "', '" + navigational_vhf_d + "', '" + navigational_mf_hf_d + "', '" + navigational_sat_d + "', '" + navigational_ecdis_d + "', '" + navigational_sart_d + "', '" + navigational_navtex_d + "', '" + navigational_ais_d + "', '" + navigational_vdr_d + "', '', '', '', '', '', '', '"+filename+"', DATE())");

                        int conn = getConnection.getConnectionType(ShipParticularFormActivity.this);
                        if(conn != 0){ //WITH NET
                            new SyncOnline(context, person_vessel_id, "ADD").execute();
                        }else{
                            Integer backup_item_id = db.newIntegerId("backup_item");
                            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_vessel', '" + person_vessel_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'ADD', 'N')");
                            pd.dismiss();

                            Intent intent1 = new Intent(ShipParticularFormActivity.this, ShipParticularActivity.class);
                            startActivity(intent1);
                            finish();
                            Toast.makeText(ShipParticularFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }else{ //SAVE
            String saved_vessel_id = db.getFieldString("vessel_id", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            vessel_id = db.getFieldString("name_vessel", "vessel_id = '"+saved_vessel_id+"'", "vessel");
            String saved_imo_number = db.getFieldString("imo_number", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_call_sign = db.getFieldString("call_sign", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_flag = db.getFieldString("flag", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_length_over_all = db.getFieldString("length_over_all", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_breadth = db.getFieldString("breadth", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_depth = db.getFieldString("depth", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_summer_draft = db.getFieldString("summer_draft", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_summer_freeboard = db.getFieldString("summer_freeboard", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_gross_tonnage = db.getFieldString("gross_tonnage", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_net_tonnage = db.getFieldString("net_tonnage", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_dead_weight = db.getFieldString("dead_weight", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_light_displacement = db.getFieldString("light_displacement", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fresh_water = db.getFieldString("fresh_water", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_immersion_at_load = db.getFieldString("immersion_at_load", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_trimming_moment_d = db.getFieldString("trimming_moment_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_bale_capacity_d = db.getFieldString("bale_capacity_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_grain_capacity_d = db.getFieldString("grain_capacity_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_liquid_capacity_d = db.getFieldString("liquid_capacity_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_refrigerated_capacity_d = db.getFieldString("refrigerated_capacity_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_container_capacity_d = db.getFieldString("container_capacity_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fresh_water_capacity_d = db.getFieldString("fresh_water_capacity_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_daily_fresh_water_gen_d = db.getFieldString("daily_fresh_water_gen_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_daily_fresh_water_con_d = db.getFieldString("daily_fresh_water_con_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_main_engine_make_e = db.getFieldString("main_engine_make_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_main_engine_type = db.getFieldString("main_engine_type", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_main_engine_stroke_e = db.getFieldString("main_engine_stroke_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_main_engine_bore_e = db.getFieldString("main_engine_bore_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_main_engine_output = db.getFieldString("main_engine_output", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_main_engine_reduction_gear_e = db.getFieldString("main_engine_reduction_gear_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_main_engine_turbo_charger_e = db.getFieldString("main_engine_turbo_charger_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_main_engine_service_speed = db.getFieldString("main_engine_service_speed", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_main_engine_boiler_d = db.getFieldString("main_engine_boiler_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_main_engine_bunker_capacity_d = db.getFieldString("main_engine_bunker_capacity_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_main_engine_daily_consumption_d = db.getFieldString("main_engine_daily_consumption_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_main_engine_steering_gear_d = db.getFieldString("main_engine_steering_gear_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_auxiliary_make_e = db.getFieldString("auxiliary_make_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_auxiliary_type_e = db.getFieldString("auxiliary_type_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_auxiliary_stroke_e = db.getFieldString("auxiliary_stroke_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_auxiliary_bore_e = db.getFieldString("auxiliary_bore_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_auxiliary_output_e = db.getFieldString("auxiliary_output_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_auxiliary_turbo_charger_e = db.getFieldString("auxiliary_turbo_charger_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_auxiliary_normal_electrical_e = db.getFieldString("auxiliary_normal_electrical_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_auxiliary_boiler_make_e = db.getFieldString("auxiliary_boiler_make_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_auxiliary_boiler_working_pressure_e = db.getFieldString("auxiliary_boiler_working_pressure_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_auxiliary_boiler_type_waste_e = db.getFieldString("auxiliary_boiler_type_waste_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fuel_main_engine_fuel_type_e = db.getFieldString("fuel_main_engine_fuel_type_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fuel_viscosity_e = db.getFieldString("fuel_viscosity_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fuel_specific_fuel_con_e = db.getFieldString("fuel_specific_fuel_con_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fuel_boiler_fuel_type_e = db.getFieldString("fuel_boiler_fuel_type_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fuel_viscosity_range_e = db.getFieldString("fuel_viscosity_range_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fuel_generator_fuel_type_e = db.getFieldString("fuel_generator_fuel_type_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fuel_bunker_capacity_e = db.getFieldString("fuel_bunker_capacity_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fuel_daily_con_e = db.getFieldString("fuel_daily_con_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_others_heavy_fuel_oil_e = db.getFieldString("others_heavy_fuel_oil_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_others_lub_oil_purifier_e = db.getFieldString("others_lub_oil_purifier_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_others_air_compressor_e = db.getFieldString("others_air_compressor_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_others_oily_water_separator_e = db.getFieldString("others_oily_water_separator_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_others_water_capacity_fw_e = db.getFieldString("others_water_capacity_fw_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_others_water_capacity_dw_e = db.getFieldString("others_water_capacity_dw_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_others_fw_generator_e = db.getFieldString("others_fw_generator_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_others_av_cons_e = db.getFieldString("others_av_cons_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_others_steering_type_e = db.getFieldString("others_steering_type_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_others_er_lifting_gear_e = db.getFieldString("others_er_lifting_gear_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_others_swl_e = db.getFieldString("others_swl_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_others_sewage_treatment_e = db.getFieldString("others_sewage_treatment_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_mooring_natural_fiber_d = db.getFieldString("mooring_natural_fiber_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_mooring_synthetic_fiber_d = db.getFieldString("mooring_synthetic_fiber_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_mooring_wires_d = db.getFieldString("mooring_wires_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_mooring_towing_spring_d = db.getFieldString("mooring_towing_spring_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_anchors_port = db.getFieldString("anchors_port", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_anchors_starboard = db.getFieldString("anchors_starboard", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_anchors_stern_d = db.getFieldString("anchors_stern_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_anchors_spare = db.getFieldString("anchors_spare", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_anchors_cable = db.getFieldString("anchors_cable", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_lifesaving_lifeboat_type_d = db.getFieldString("lifesaving_lifeboat_type_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_lifesaving_lifeboat_no = db.getFieldString("lifesaving_lifeboat_no", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_lifesaving_liferaft_no = db.getFieldString("lifesaving_liferaft_no", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_lifesaving_lifeboat_dimension_d = db.getFieldString("lifesaving_lifeboat_dimension_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_lifesaving_lifeboat_capacity = db.getFieldString("lifesaving_lifeboat_capacity", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_lifesaving_liferaft_capacity = db.getFieldString("lifesaving_liferaft_capacity", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_lifesaving_lifeboat_davits = db.getFieldString("lifesaving_lifeboat_davits", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_lifesaving_lifeboat_fall = db.getFieldString("lifesaving_lifeboat_fall", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_lifesaving_lifebuoys_no = db.getFieldString("lifesaving_lifebuoys_no", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fire_extinguisher_no = db.getFieldString("fire_extinguisher_no", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fire_water = db.getFieldString("fire_water", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fire_foam = db.getFieldString("fire_foam", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fire_dry_powder = db.getFieldString("fire_dry_powder", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fire_co2 = db.getFieldString("fire_co2", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fire_firehoses = db.getFieldString("fire_firehoses", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fire_breathing_e = db.getFieldString("fire_breathing_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fire_breathing_no_e = db.getFieldString("fire_breathing_no_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fire_fixed_fire_system_d = db.getFieldString("fire_fixed_fire_system_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_fire_scba_d = db.getFieldString("fire_scba_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_cargo_handling_derricks = db.getFieldString("cargo_handling_derricks", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_cargo_handling_cranes = db.getFieldString("cargo_handling_cranes", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_cargo_handling_winches = db.getFieldString("cargo_handling_winches", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_cargo_handling_other_d = db.getFieldString("cargo_handling_other_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_cargo_handling_ballast_d = db.getFieldString("cargo_handling_ballast_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_cargo_handling_tank_d = db.getFieldString("cargo_handling_tank_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_cargo_handling_pump_no = db.getFieldString("cargo_handling_pump_no", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_cargo_handling_pipelines = db.getFieldString("cargo_handling_pipelines", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_cargo_handling_type_rating_e = db.getFieldString("cargo_handling_type_rating_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_cargo_handling_ballast_pump_e = db.getFieldString("cargo_handling_ballast_pump_e", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_radar_d = db.getFieldString("navigational_radar_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_log_d = db.getFieldString("navigational_log_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_gps_d = db.getFieldString("navigational_gps_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_magnetic_d = db.getFieldString("navigational_magnetic_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_gyro_d = db.getFieldString("navigational_gyro_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_echo_d = db.getFieldString("navigational_echo_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_auto_d = db.getFieldString("navigational_auto_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_vhf_d = db.getFieldString("navigational_vhf_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_mf_hf_d = db.getFieldString("navigational_mf_hf_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_sat_d = db.getFieldString("navigational_sat_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_ecdis_d = db.getFieldString("navigational_ecdis_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_sart_d = db.getFieldString("navigational_sart_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_navtex_d = db.getFieldString("navigational_navtex_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_ais_d = db.getFieldString("navigational_ais_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_navigational_vdr_d = db.getFieldString("navigational_vdr_d", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_app_by_id = db.getFieldString("app_by_id", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_date_checked = db.getFieldString("date_checked", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_date_app = db.getFieldString("date_app", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_checked_remarks = db.getFieldString("checked_remarks", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_app_remarks = db.getFieldString("app_remarks", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            String saved_filename = db.getFieldString("filename", "person_vessel_id = '" + person_vessel_id + "'", "person_vessel");
            filename = saved_filename;
            //SET VALUES
            spinner_vessel_id.setSelection(arrayAdapter.getPosition(vessel_id));
            et_imo_number.getEditText().setText(saved_imo_number);
            et_call_sign.getEditText().setText(saved_call_sign);
            et_flag.getEditText().setText(saved_flag);
            et_length_over_all.getEditText().setText(saved_length_over_all);
            et_breadth.getEditText().setText(saved_breadth);
            et_depth.getEditText().setText(saved_depth);
            et_summer_draft.getEditText().setText(saved_summer_draft);
            et_summer_freeboard.getEditText().setText(saved_summer_freeboard);
            et_gross_tonnage.getEditText().setText(saved_gross_tonnage);
            et_net_tonnage.getEditText().setText(saved_net_tonnage);
            et_dead_weight.getEditText().setText(saved_dead_weight);
            et_light_displacement.getEditText().setText(saved_light_displacement);
            et_fresh_water.getEditText().setText(saved_fresh_water);
            et_immersion_at_load.getEditText().setText(saved_immersion_at_load);
            et_trimming_moment_d.getEditText().setText(saved_trimming_moment_d);
            et_bale_capacity_d.getEditText().setText(saved_bale_capacity_d);
            et_grain_capacity_d.getEditText().setText(saved_grain_capacity_d);
            et_liquid_capacity_d.getEditText().setText(saved_liquid_capacity_d);
            et_refrigerated_capacity_d.getEditText().setText(saved_refrigerated_capacity_d);
            et_container_capacity_d.getEditText().setText(saved_container_capacity_d);
            et_fresh_water_capacity_d.getEditText().setText(saved_fresh_water_capacity_d);
            et_daily_fresh_water_gen_d.getEditText().setText(saved_daily_fresh_water_gen_d);
            et_daily_fresh_water_con_d.getEditText().setText(saved_daily_fresh_water_con_d);
            et_main_engine_make_e.getEditText().setText(saved_main_engine_make_e);
            et_main_engine_type.getEditText().setText(saved_main_engine_type);
            et_main_engine_stroke_e.getEditText().setText(saved_main_engine_stroke_e);
            et_main_engine_bore_e.getEditText().setText(saved_main_engine_bore_e);
            et_main_engine_output.getEditText().setText(saved_main_engine_output);
            et_main_engine_reduction_gear_e.getEditText().setText(saved_main_engine_reduction_gear_e);
            et_main_engine_turbo_charger_e.getEditText().setText(saved_main_engine_turbo_charger_e);
            et_main_engine_service_speed.getEditText().setText(saved_main_engine_service_speed);
            et_main_engine_boiler_d.getEditText().setText(saved_main_engine_boiler_d);
            et_main_engine_bunker_capacity_d.getEditText().setText(saved_main_engine_bunker_capacity_d);
            et_main_engine_daily_consumption_d.getEditText().setText(saved_main_engine_daily_consumption_d);
            et_main_engine_steering_gear_d.getEditText().setText(saved_main_engine_steering_gear_d);
            et_auxiliary_make_e.getEditText().setText(saved_auxiliary_make_e);
            et_auxiliary_type_e.getEditText().setText(saved_auxiliary_type_e);
            et_auxiliary_stroke_e.getEditText().setText(saved_auxiliary_stroke_e);
            et_auxiliary_bore_e.getEditText().setText(saved_auxiliary_bore_e);
            et_auxiliary_output_e.getEditText().setText(saved_auxiliary_output_e);
            et_auxiliary_turbo_charger_e.getEditText().setText(saved_auxiliary_turbo_charger_e);
            et_auxiliary_normal_electrical_e.getEditText().setText(saved_auxiliary_normal_electrical_e);
            et_auxiliary_boiler_make_e.getEditText().setText(saved_auxiliary_boiler_make_e);
            et_auxiliary_boiler_working_pressure_e.getEditText().setText(saved_auxiliary_boiler_working_pressure_e);
            et_auxiliary_boiler_type_waste_e.getEditText().setText(saved_auxiliary_boiler_type_waste_e);
            et_fuel_main_engine_fuel_type_e.getEditText().setText(saved_fuel_main_engine_fuel_type_e);
            et_fuel_viscosity_e.getEditText().setText(saved_fuel_viscosity_e);
            et_fuel_specific_fuel_con_e.getEditText().setText(saved_fuel_specific_fuel_con_e);
            et_fuel_boiler_fuel_type_e.getEditText().setText(saved_fuel_boiler_fuel_type_e);
            et_fuel_viscosity_range_e.getEditText().setText(saved_fuel_viscosity_range_e);
            et_fuel_generator_fuel_type_e.getEditText().setText(saved_fuel_generator_fuel_type_e);
            et_fuel_bunker_capacity_e.getEditText().setText(saved_fuel_bunker_capacity_e);
            et_fuel_daily_con_e.getEditText().setText(saved_fuel_daily_con_e);
            et_others_heavy_fuel_oil_e.getEditText().setText(saved_others_heavy_fuel_oil_e);
            et_others_lub_oil_purifier_e.getEditText().setText(saved_others_lub_oil_purifier_e);
            et_others_air_compressor_e.getEditText().setText(saved_others_air_compressor_e);
            et_others_oily_water_separator_e.getEditText().setText(saved_others_oily_water_separator_e);
            et_others_water_capacity_fw_e.getEditText().setText(saved_others_water_capacity_fw_e);
            et_others_water_capacity_dw_e.getEditText().setText(saved_others_water_capacity_dw_e);
            et_others_fw_generator_e.getEditText().setText(saved_others_fw_generator_e);
            et_others_av_cons_e.getEditText().setText(saved_others_av_cons_e);
            et_others_steering_type_e.getEditText().setText(saved_others_steering_type_e);
            et_others_er_lifting_gear_e.getEditText().setText(saved_others_er_lifting_gear_e);
            et_others_swl_e.getEditText().setText(saved_others_swl_e);
            et_others_sewage_treatment_e.getEditText().setText(saved_others_sewage_treatment_e);
            et_mooring_natural_fiber_d.getEditText().setText(saved_mooring_natural_fiber_d);
            et_mooring_synthetic_fiber_d.getEditText().setText(saved_mooring_synthetic_fiber_d);
            et_mooring_wires_d.getEditText().setText(saved_mooring_wires_d);
            et_mooring_towing_spring_d.getEditText().setText(saved_mooring_towing_spring_d);
            et_anchors_port.getEditText().setText(saved_anchors_port);
            et_anchors_starboard.getEditText().setText(saved_anchors_starboard);
            et_anchors_stern_d.getEditText().setText(saved_anchors_stern_d);
            et_anchors_spare.getEditText().setText(saved_anchors_spare);
            et_anchors_cable.getEditText().setText(saved_anchors_cable);
            et_lifesaving_lifeboat_type_d.getEditText().setText(saved_lifesaving_lifeboat_type_d);
            et_lifesaving_lifeboat_no.getEditText().setText(saved_lifesaving_lifeboat_no);
            et_lifesaving_liferaft_no.getEditText().setText(saved_lifesaving_liferaft_no);
            et_lifesaving_lifeboat_dimension_d.getEditText().setText(saved_lifesaving_lifeboat_dimension_d);
            et_lifesaving_lifeboat_capacity.getEditText().setText(saved_lifesaving_lifeboat_capacity);
            et_lifesaving_liferaft_capacity.getEditText().setText(saved_lifesaving_liferaft_capacity);
            et_lifesaving_lifeboat_davits.getEditText().setText(saved_lifesaving_lifeboat_davits);
            et_lifesaving_lifeboat_fall.getEditText().setText(saved_lifesaving_lifeboat_fall);
            et_lifesaving_lifebuoys_no.getEditText().setText(saved_lifesaving_lifebuoys_no);
            et_fire_extinguisher_no.getEditText().setText(saved_fire_extinguisher_no);
            et_fire_water.getEditText().setText(saved_fire_water);
            et_fire_foam.getEditText().setText(saved_fire_foam);
            et_fire_dry_powder.getEditText().setText(saved_fire_dry_powder);
            et_fire_co2.getEditText().setText(saved_fire_co2);
            et_fire_firehoses.getEditText().setText(saved_fire_firehoses);
            et_fire_breathing_e.getEditText().setText(saved_fire_breathing_e);
            et_fire_breathing_no_e.getEditText().setText(saved_fire_breathing_no_e);
            et_fire_fixed_fire_system_d.getEditText().setText(saved_fire_fixed_fire_system_d);
            et_fire_scba_d.getEditText().setText(saved_fire_scba_d);
            et_cargo_handling_derricks.getEditText().setText(saved_cargo_handling_derricks);
            et_cargo_handling_cranes.getEditText().setText(saved_cargo_handling_cranes);
            et_cargo_handling_winches.getEditText().setText(saved_cargo_handling_winches);
            et_cargo_handling_other_d.getEditText().setText(saved_cargo_handling_other_d);
            et_cargo_handling_ballast_d.getEditText().setText(saved_cargo_handling_ballast_d);
            et_cargo_handling_tank_d.getEditText().setText(saved_cargo_handling_tank_d);
            et_cargo_handling_pump_no.getEditText().setText(saved_cargo_handling_pump_no);
            et_cargo_handling_pipelines.getEditText().setText(saved_cargo_handling_pipelines);
            et_cargo_handling_type_rating_e.getEditText().setText(saved_cargo_handling_type_rating_e);
            et_cargo_handling_ballast_pump_e.getEditText().setText(saved_cargo_handling_ballast_pump_e);
            et_navigational_radar_d.getEditText().setText(saved_navigational_radar_d);
            et_navigational_log_d.getEditText().setText(saved_navigational_log_d);
            et_navigational_gps_d.getEditText().setText(saved_navigational_gps_d);
            et_navigational_magnetic_d.getEditText().setText(saved_navigational_magnetic_d);
            et_navigational_gyro_d.getEditText().setText(saved_navigational_gyro_d);
            et_navigational_echo_d.getEditText().setText(saved_navigational_echo_d);
            et_navigational_auto_d.getEditText().setText(saved_navigational_auto_d);
            et_navigational_vhf_d.getEditText().setText(saved_navigational_vhf_d);
            et_navigational_mf_hf_d.getEditText().setText(saved_navigational_mf_hf_d);
            et_navigational_sat_d.getEditText().setText(saved_navigational_sat_d);
            et_navigational_ecdis_d.getEditText().setText(saved_navigational_ecdis_d);
            et_navigational_sart_d.getEditText().setText(saved_navigational_sart_d);
            et_navigational_navtex_d.getEditText().setText(saved_navigational_navtex_d);
            et_navigational_ais_d.getEditText().setText(saved_navigational_ais_d);
            et_navigational_vdr_d.getEditText().setText(saved_navigational_vdr_d);
            tv_filename.setText(saved_filename);

            if(saved_checked_by_id.equals("")){
                approval_container.setVisibility(View.GONE);

            }else{
                btn_delete.setVisibility(View.GONE);
                btn_save.setVisibility(View.GONE);
                btn_cancel.setText("Back");
                TextView tv_checked_by_id = findViewById(R.id.tv_checked_by_id);
                TextView tv_date_checked = findViewById(R.id.tv_date_checked);

                String name = db.getFieldString("full_name", "person_id = '"+saved_checked_by_id+"'", "person");
                tv_checked_by_id.setText("Checked By : " + name);
                tv_date_checked.setText("Date Checked : " + saved_date_checked);
            }

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String vessel_id_ = db.getFieldString("vessel_id", " name_vessel='" + vessel_id + "'", "vessel");
                    int cnt = db.GetCount("person_vessel", " WHERE vessel_id = '" + vessel_id_ + "' AND person_vessel_id != '" + person_vessel_id + "'");
                    if (cnt > 0) {
                        Toast.makeText(ShipParticularFormActivity.this, "An existing record with the new ship selected has been detected. Updating is not allowed", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        String imo_number = et_imo_number.getEditText().getText().toString();
                        String call_sign = et_call_sign.getEditText().getText().toString();
                        String flag = et_flag.getEditText().getText().toString();
                        String length_over_all = et_length_over_all.getEditText().getText().toString();
                        String breadth = et_breadth.getEditText().getText().toString();
                        String depth = et_depth.getEditText().getText().toString();
                        String summer_draft = et_summer_draft.getEditText().getText().toString();
                        String summer_freeboard = et_summer_freeboard.getEditText().getText().toString();
                        String gross_tonnage = et_gross_tonnage.getEditText().getText().toString();
                        String net_tonnage = et_net_tonnage.getEditText().getText().toString();
                        String dead_weight = et_dead_weight.getEditText().getText().toString();
                        String light_displacement = et_light_displacement.getEditText().getText().toString();
                        String fresh_water = et_fresh_water.getEditText().getText().toString();
                        String immersion_at_load = et_immersion_at_load.getEditText().getText().toString();
                        String trimming_moment_d = et_trimming_moment_d.getEditText().getText().toString();
                        String bale_capacity_d = et_bale_capacity_d.getEditText().getText().toString();
                        String grain_capacity_d = et_grain_capacity_d.getEditText().getText().toString();
                        String liquid_capacity_d = et_liquid_capacity_d.getEditText().getText().toString();
                        String refrigerated_capacity_d = et_refrigerated_capacity_d.getEditText().getText().toString();
                        String container_capacity_d = et_container_capacity_d.getEditText().getText().toString();
                        String fresh_water_capacity_d = et_fresh_water_capacity_d.getEditText().getText().toString();
                        String daily_fresh_water_gen_d = et_daily_fresh_water_gen_d.getEditText().getText().toString();
                        String daily_fresh_water_con_d = et_daily_fresh_water_con_d.getEditText().getText().toString();
                        String main_engine_make_e = et_main_engine_make_e.getEditText().getText().toString();
                        String main_engine_type = et_main_engine_type.getEditText().getText().toString();
                        String main_engine_stroke_e = et_main_engine_stroke_e.getEditText().getText().toString();
                        String main_engine_bore_e = et_main_engine_bore_e.getEditText().getText().toString();
                        String main_engine_output = et_main_engine_output.getEditText().getText().toString();
                        String main_engine_reduction_gear_e = et_main_engine_reduction_gear_e.getEditText().getText().toString();
                        String main_engine_turbo_charger_e = et_main_engine_turbo_charger_e.getEditText().getText().toString();
                        String main_engine_service_speed = et_main_engine_service_speed.getEditText().getText().toString();
                        String main_engine_boiler_d = et_main_engine_boiler_d.getEditText().getText().toString();
                        String main_engine_bunker_capacity_d = et_main_engine_bunker_capacity_d.getEditText().getText().toString();
                        String main_engine_daily_consumption_d = et_main_engine_daily_consumption_d.getEditText().getText().toString();
                        String main_engine_steering_gear_d = et_main_engine_steering_gear_d.getEditText().getText().toString();
                        String auxiliary_make_e = et_auxiliary_make_e.getEditText().getText().toString();
                        String auxiliary_type_e = et_auxiliary_type_e.getEditText().getText().toString();
                        String auxiliary_stroke_e = et_auxiliary_stroke_e.getEditText().getText().toString();
                        String auxiliary_bore_e = et_auxiliary_bore_e.getEditText().getText().toString();
                        String auxiliary_output_e = et_auxiliary_output_e.getEditText().getText().toString();
                        String auxiliary_turbo_charger_e = et_auxiliary_turbo_charger_e.getEditText().getText().toString();
                        String auxiliary_normal_electrical_e = et_auxiliary_normal_electrical_e.getEditText().getText().toString();
                        String auxiliary_boiler_make_e = et_auxiliary_boiler_make_e.getEditText().getText().toString();
                        String auxiliary_boiler_working_pressure_e = et_auxiliary_boiler_working_pressure_e.getEditText().getText().toString();
                        String auxiliary_boiler_type_waste_e = et_auxiliary_boiler_type_waste_e.getEditText().getText().toString();
                        String fuel_main_engine_fuel_type_e = et_fuel_main_engine_fuel_type_e.getEditText().getText().toString();
                        String fuel_viscosity_e = et_fuel_viscosity_e.getEditText().getText().toString();
                        String fuel_specific_fuel_con_e = et_fuel_specific_fuel_con_e.getEditText().getText().toString();
                        String fuel_boiler_fuel_type_e = et_fuel_boiler_fuel_type_e.getEditText().getText().toString();
                        String fuel_viscosity_range_e = et_fuel_viscosity_range_e.getEditText().getText().toString();
                        String fuel_generator_fuel_type_e = et_fuel_generator_fuel_type_e.getEditText().getText().toString();
                        String fuel_bunker_capacity_e = et_fuel_bunker_capacity_e.getEditText().getText().toString();
                        String fuel_daily_con_e = et_fuel_daily_con_e.getEditText().getText().toString();
                        String others_heavy_fuel_oil_e = et_others_heavy_fuel_oil_e.getEditText().getText().toString();
                        String others_lub_oil_purifier_e = et_others_lub_oil_purifier_e.getEditText().getText().toString();
                        String others_air_compressor_e = et_others_air_compressor_e.getEditText().getText().toString();
                        String others_oily_water_separator_e = et_others_oily_water_separator_e.getEditText().getText().toString();
                        String others_water_capacity_fw_e = et_others_water_capacity_fw_e.getEditText().getText().toString();
                        String others_water_capacity_dw_e = et_others_water_capacity_dw_e.getEditText().getText().toString();
                        String others_fw_generator_e = et_others_fw_generator_e.getEditText().getText().toString();
                        String others_av_cons_e = et_others_av_cons_e.getEditText().getText().toString();
                        String others_steering_type_e = et_others_steering_type_e.getEditText().getText().toString();
                        String others_er_lifting_gear_e = et_others_er_lifting_gear_e.getEditText().getText().toString();
                        String others_swl_e = et_others_swl_e.getEditText().getText().toString();
                        String others_sewage_treatment_e = et_others_sewage_treatment_e.getEditText().getText().toString();
                        String mooring_natural_fiber_d = et_mooring_natural_fiber_d.getEditText().getText().toString();
                        String mooring_synthetic_fiber_d = et_mooring_synthetic_fiber_d.getEditText().getText().toString();
                        String mooring_wires_d = et_mooring_wires_d.getEditText().getText().toString();
                        String mooring_towing_spring_d = et_mooring_towing_spring_d.getEditText().getText().toString();
                        String anchors_port = et_anchors_port.getEditText().getText().toString();
                        String anchors_starboard = et_anchors_starboard.getEditText().getText().toString();
                        String anchors_stern_d = et_anchors_stern_d.getEditText().getText().toString();
                        String anchors_spare = et_anchors_spare.getEditText().getText().toString();
                        String anchors_cable = et_anchors_cable.getEditText().getText().toString();
                        String lifesaving_lifeboat_type_d = et_lifesaving_lifeboat_type_d.getEditText().getText().toString();
                        String lifesaving_lifeboat_no = et_lifesaving_lifeboat_no.getEditText().getText().toString();
                        String lifesaving_liferaft_no = et_lifesaving_liferaft_no.getEditText().getText().toString();
                        String lifesaving_lifeboat_dimension_d = et_lifesaving_lifeboat_dimension_d.getEditText().getText().toString();
                        String lifesaving_lifeboat_capacity = et_lifesaving_lifeboat_capacity.getEditText().getText().toString();
                        String lifesaving_liferaft_capacity = et_lifesaving_liferaft_capacity.getEditText().getText().toString();
                        String lifesaving_lifeboat_davits = et_lifesaving_lifeboat_davits.getEditText().getText().toString();
                        String lifesaving_lifeboat_fall = et_lifesaving_lifeboat_fall.getEditText().getText().toString();
                        String lifesaving_lifebuoys_no = et_lifesaving_lifebuoys_no.getEditText().getText().toString();
                        String fire_extinguisher_no = et_fire_extinguisher_no.getEditText().getText().toString();
                        String fire_water = et_fire_water.getEditText().getText().toString();
                        String fire_foam = et_fire_foam.getEditText().getText().toString();
                        String fire_dry_powder = et_fire_dry_powder.getEditText().getText().toString();
                        String fire_co2 = et_fire_co2.getEditText().getText().toString();
                        String fire_firehoses = et_fire_firehoses.getEditText().getText().toString();
                        String fire_breathing_e = et_fire_breathing_e.getEditText().getText().toString();
                        String fire_breathing_no_e = et_fire_breathing_no_e.getEditText().getText().toString();
                        String fire_fixed_fire_system_d = et_fire_fixed_fire_system_d.getEditText().getText().toString();
                        String fire_scba_d = et_fire_scba_d.getEditText().getText().toString();
                        String cargo_handling_derricks = et_cargo_handling_derricks.getEditText().getText().toString();
                        String cargo_handling_cranes = et_cargo_handling_cranes.getEditText().getText().toString();
                        String cargo_handling_winches = et_cargo_handling_winches.getEditText().getText().toString();
                        String cargo_handling_other_d = et_cargo_handling_other_d.getEditText().getText().toString();
                        String cargo_handling_ballast_d = et_cargo_handling_ballast_d.getEditText().getText().toString();
                        String cargo_handling_tank_d = et_cargo_handling_tank_d.getEditText().getText().toString();
                        String cargo_handling_pump_no = et_cargo_handling_pump_no.getEditText().getText().toString();
                        String cargo_handling_pipelines = et_cargo_handling_pipelines.getEditText().getText().toString();
                        String cargo_handling_type_rating_e = et_cargo_handling_type_rating_e.getEditText().getText().toString();
                        String cargo_handling_ballast_pump_e = et_cargo_handling_ballast_pump_e.getEditText().getText().toString();
                        String navigational_radar_d = et_navigational_radar_d.getEditText().getText().toString();
                        String navigational_log_d = et_navigational_log_d.getEditText().getText().toString();
                        String navigational_gps_d = et_navigational_gps_d.getEditText().getText().toString();
                        String navigational_magnetic_d = et_navigational_magnetic_d.getEditText().getText().toString();
                        String navigational_gyro_d = et_navigational_gyro_d.getEditText().getText().toString();
                        String navigational_echo_d = et_navigational_echo_d.getEditText().getText().toString();
                        String navigational_auto_d = et_navigational_auto_d.getEditText().getText().toString();
                        String navigational_vhf_d = et_navigational_vhf_d.getEditText().getText().toString();
                        String navigational_mf_hf_d = et_navigational_mf_hf_d.getEditText().getText().toString();
                        String navigational_sat_d = et_navigational_sat_d.getEditText().getText().toString();
                        String navigational_ecdis_d = et_navigational_ecdis_d.getEditText().getText().toString();
                        String navigational_sart_d = et_navigational_sart_d.getEditText().getText().toString();
                        String navigational_navtex_d = et_navigational_navtex_d.getEditText().getText().toString();
                        String navigational_ais_d = et_navigational_ais_d.getEditText().getText().toString();
                        String navigational_vdr_d = et_navigational_vdr_d.getEditText().getText().toString();


                        if (vessel_id.equals("Select Ship *")) {
                            Toast.makeText(ShipParticularFormActivity.this, "Ship is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (imo_number.equals("")) {
                            Toast.makeText(ShipParticularFormActivity.this, "IMO Number is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (call_sign.equals("")) {
                            Toast.makeText(ShipParticularFormActivity.this, "Call Sign is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (flag.equals("")) {
                            Toast.makeText(ShipParticularFormActivity.this, "Flag is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (net_tonnage.equals("")) {
                            Toast.makeText(ShipParticularFormActivity.this, "Net Tonnage is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (gross_tonnage.equals("")) {
                            Toast.makeText(ShipParticularFormActivity.this, "Gross Tonnage is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (main_engine_output.equals("")) {
                            Toast.makeText(ShipParticularFormActivity.this, "Output is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        pd = new ProgressDialog(ShipParticularFormActivity.this);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage("Processing. Please wait .... ");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();

                        db.execQuery("UPDATE person_vessel SET imo_number = '" + imo_number + "', call_sign = '" + call_sign + "', flag = '" + flag + "', length_over_all = '" + length_over_all + "', breadth = '" + breadth + "', depth = '" + depth + "', summer_draft = '" + summer_draft + "', summer_freeboard = '" + summer_freeboard + "', gross_tonnage = '" + gross_tonnage + "', net_tonnage = '" + net_tonnage + "', dead_weight = '" + dead_weight + "', light_displacement = '" + light_displacement + "', fresh_water = '" + fresh_water + "', immersion_at_load = '" + immersion_at_load + "', trimming_moment_d = '" + trimming_moment_d + "', bale_capacity_d = '" + bale_capacity_d + "', grain_capacity_d = '" + grain_capacity_d + "', liquid_capacity_d = '" + liquid_capacity_d + "', refrigerated_capacity_d = '" + refrigerated_capacity_d + "', container_capacity_d = '" + container_capacity_d + "', fresh_water_capacity_d = '" + fresh_water_capacity_d + "', daily_fresh_water_gen_d = '" + daily_fresh_water_gen_d + "', daily_fresh_water_con_d = '" + daily_fresh_water_con_d + "', main_engine_make_e = '" + main_engine_make_e + "', main_engine_type = '" + main_engine_type + "', main_engine_stroke_e = '" + main_engine_stroke_e + "', main_engine_bore_e = '" + main_engine_bore_e + "', main_engine_output = '" + main_engine_output + "', main_engine_reduction_gear_e = '" + main_engine_reduction_gear_e + "', main_engine_turbo_charger_e = '" + main_engine_turbo_charger_e + "', main_engine_service_speed = '" + main_engine_service_speed + "', main_engine_boiler_d = '" + main_engine_boiler_d + "', main_engine_bunker_capacity_d = '" + main_engine_bunker_capacity_d + "', main_engine_daily_consumption_d = '" + main_engine_daily_consumption_d + "', main_engine_steering_gear_d = '" + main_engine_steering_gear_d + "', auxiliary_make_e = '" + auxiliary_make_e + "', auxiliary_type_e = '" + auxiliary_type_e + "', auxiliary_stroke_e = '" + auxiliary_stroke_e + "', auxiliary_bore_e = '" + auxiliary_bore_e + "', auxiliary_output_e = '" + auxiliary_output_e + "', auxiliary_turbo_charger_e = '" + auxiliary_turbo_charger_e + "', auxiliary_normal_electrical_e = '" + auxiliary_normal_electrical_e + "', auxiliary_boiler_make_e = '" + auxiliary_boiler_make_e + "', auxiliary_boiler_working_pressure_e = '" + auxiliary_boiler_working_pressure_e + "', auxiliary_boiler_type_waste_e = '" + auxiliary_boiler_type_waste_e + "', fuel_main_engine_fuel_type_e = '" + fuel_main_engine_fuel_type_e + "', fuel_viscosity_e = '" + fuel_viscosity_e + "', fuel_specific_fuel_con_e = '" + fuel_specific_fuel_con_e + "', fuel_boiler_fuel_type_e = '" + fuel_boiler_fuel_type_e + "', fuel_viscosity_range_e = '" + fuel_viscosity_range_e + "', fuel_generator_fuel_type_e = '" + fuel_generator_fuel_type_e + "', fuel_bunker_capacity_e = '" + fuel_bunker_capacity_e + "', fuel_daily_con_e = '" + fuel_daily_con_e + "', others_heavy_fuel_oil_e = '" + others_heavy_fuel_oil_e + "', others_lub_oil_purifier_e = '" + others_lub_oil_purifier_e + "', others_air_compressor_e = '" + others_air_compressor_e + "', others_oily_water_separator_e = '" + others_oily_water_separator_e + "', others_water_capacity_fw_e = '" + others_water_capacity_fw_e + "', others_water_capacity_dw_e = '" + others_water_capacity_dw_e + "', others_fw_generator_e = '" + others_fw_generator_e + "', others_av_cons_e = '" + others_av_cons_e + "', others_steering_type_e = '" + others_steering_type_e + "', others_er_lifting_gear_e = '" + others_er_lifting_gear_e + "', others_swl_e = '" + others_swl_e + "', others_sewage_treatment_e = '" + others_sewage_treatment_e + "', mooring_natural_fiber_d = '" + mooring_natural_fiber_d + "', mooring_synthetic_fiber_d = '" + mooring_synthetic_fiber_d + "', mooring_wires_d = '" + mooring_wires_d + "', mooring_towing_spring_d = '" + mooring_towing_spring_d + "', anchors_port = '" + anchors_port + "', anchors_starboard = '" + anchors_starboard + "', anchors_stern_d = '" + anchors_stern_d + "', anchors_spare = '" + anchors_spare + "', anchors_cable = '" + anchors_cable + "', lifesaving_lifeboat_type_d = '" + lifesaving_lifeboat_type_d + "', lifesaving_lifeboat_no = '" + lifesaving_lifeboat_no + "', lifesaving_liferaft_no = '" + lifesaving_liferaft_no + "', lifesaving_lifeboat_dimension_d = '" + lifesaving_lifeboat_dimension_d + "', lifesaving_lifeboat_capacity = '" + lifesaving_lifeboat_capacity + "', lifesaving_liferaft_capacity = '" + lifesaving_liferaft_capacity + "', lifesaving_lifeboat_davits = '" + lifesaving_lifeboat_davits + "', lifesaving_lifeboat_fall = '" + lifesaving_lifeboat_fall + "', lifesaving_lifebuoys_no = '" + lifesaving_lifebuoys_no + "', fire_extinguisher_no = '" + fire_extinguisher_no + "', fire_water = '" + fire_water + "', fire_foam = '" + fire_foam + "', fire_dry_powder = '" + fire_dry_powder + "', fire_co2 = '" + fire_co2 + "', fire_firehoses = '" + fire_firehoses + "', fire_breathing_e = '" + fire_breathing_e + "', fire_breathing_no_e = '" + fire_breathing_no_e + "', fire_fixed_fire_system_d = '" + fire_fixed_fire_system_d + "', fire_scba_d = '" + fire_scba_d + "', cargo_handling_derricks = '" + cargo_handling_derricks + "', cargo_handling_cranes = '" + cargo_handling_cranes + "', cargo_handling_winches = '" + cargo_handling_winches + "', cargo_handling_other_d = '" + cargo_handling_other_d + "', cargo_handling_ballast_d = '" + cargo_handling_ballast_d + "', cargo_handling_tank_d = '" + cargo_handling_tank_d + "', cargo_handling_pump_no = '" + cargo_handling_pump_no + "', cargo_handling_pipelines = '" + cargo_handling_pipelines + "', cargo_handling_type_rating_e = '" + cargo_handling_type_rating_e + "', cargo_handling_ballast_pump_e = '" + cargo_handling_ballast_pump_e + "', navigational_radar_d = '" + navigational_radar_d + "', navigational_log_d = '" + navigational_log_d + "', navigational_gps_d = '" + navigational_gps_d + "', navigational_magnetic_d = '" + navigational_magnetic_d + "', navigational_gyro_d = '" + navigational_gyro_d + "', navigational_echo_d = '" + navigational_echo_d + "', navigational_auto_d = '" + navigational_auto_d + "', navigational_vhf_d = '" + navigational_vhf_d + "', navigational_mf_hf_d = '" + navigational_mf_hf_d + "', navigational_sat_d = '" + navigational_sat_d + "', navigational_ecdis_d = '" + navigational_ecdis_d + "', navigational_sart_d = '" + navigational_sart_d + "', navigational_navtex_d = '" + navigational_navtex_d + "', navigational_ais_d = '" + navigational_ais_d + "', navigational_vdr_d = '" + navigational_vdr_d + "', filename = '"+filename+"', date_saved = DATE() WHERE person_vessel_id = '" + person_vessel_id + "'");

                        int conn = getConnection.getConnectionType(ShipParticularFormActivity.this);
                        if(conn != 0){
                            new SyncOnline(context, person_vessel_id, "UPDATE").execute();

                        }else{
                            Integer backup_item_id = db.newIntegerId("backup_item");
                            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_vessel', '" + person_vessel_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'UPDATE', 'N')");
                            pd.dismiss();
                            Intent intent1 = new Intent(ShipParticularFormActivity.this, ShipParticularActivity.class);
                            startActivity(intent1);
                            finish();
                            Toast.makeText(ShipParticularFormActivity.this, "Record successfully saved.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

        }

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShipParticularFormActivity.this);

                alertDialogBuilder.setMessage("Are you sure you want to delete this record?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        delete(person_vessel_id);
                    }
                });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(ShipParticularFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ShipParticularFormActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(ShipParticularFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ShipParticularFormActivity.this, R.color.black));

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShipParticularFormActivity.this);

                alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved.");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(ShipParticularFormActivity.this, ShipParticularActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(ShipParticularFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ShipParticularFormActivity.this, R.color.black));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(ShipParticularFormActivity.this, R.color.white));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ShipParticularFormActivity.this, R.color.black));
            }
        });
        btn_filename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    public void delete(String person_vessel_id){
        pd = new ProgressDialog(ShipParticularFormActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Processing. Please wait .... ");
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();

        db.query("DELETE FROM person_vessel WHERE person_vessel_id = '"+person_vessel_id+"'");
        int conn = getConnection.getConnectionType(ShipParticularFormActivity.this);
        if(conn != 0){
            new SyncOnlineDelete(context,  "person_vessel", person_vessel_id).execute();
        }else{
            Integer backup_item_id = db.newIntegerId("backup_item");
            db.query("INSERT INTO backup_item (id, tbl, tbl_id, backup_date, backup_time, backup_event, backuped) VALUES ("+backup_item_id+", 'person_vessel', '" + person_vessel_id+ "', datetime('now', 'localtime'), datetime('now', 'localtime'), 'DELETE', 'N')");
            pd.dismiss();

            Intent intent = new Intent(ShipParticularFormActivity.this, ShipParticularActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(ShipParticularFormActivity.this, "Record successfully deleted.", Toast.LENGTH_LONG).show();

        }

    }

    private class SyncOnline extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public String id, event;
        public SyncOnline(Context context, String id, String event)
        {
            this.context = context;
            this.id = id;
            this.event = event;

        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            String url = getString(R.string.url);
            //GET FROM TBL
            String saved_person_id = db.getFieldString("person_id", "person_vessel_id = '" + id + "'", "person_vessel");
            String saved_vessel_id = db.getFieldString("vessel_id", "person_vessel_id = '" + id + "'", "person_vessel");
            String saved_imo_number = db.getFieldString("imo_number", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_imo_number = URLEncoder.encode(saved_imo_number);
            String saved_call_sign = db.getFieldString("call_sign", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_call_sign = URLEncoder.encode(saved_call_sign);
            String saved_flag = db.getFieldString("flag", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_flag = URLEncoder.encode(saved_flag);
            String saved_length_over_all = db.getFieldString("length_over_all", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_flag = URLEncoder.encode(saved_flag);
            String saved_breadth = db.getFieldString("breadth", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_breadth = URLEncoder.encode(saved_breadth);
            String saved_depth = db.getFieldString("depth", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_depth = URLEncoder.encode(saved_depth);
            String saved_summer_draft = db.getFieldString("summer_draft", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_summer_draft = URLEncoder.encode(saved_summer_draft);
            String saved_summer_freeboard = db.getFieldString("summer_freeboard", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_summer_freeboard = URLEncoder.encode(saved_summer_freeboard);
            String saved_gross_tonnage = db.getFieldString("gross_tonnage", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_gross_tonnage = URLEncoder.encode(saved_gross_tonnage);
            String saved_net_tonnage = db.getFieldString("net_tonnage", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_net_tonnage = URLEncoder.encode(saved_net_tonnage);
            String saved_dead_weight = db.getFieldString("dead_weight", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_dead_weight = URLEncoder.encode(saved_dead_weight);
            String saved_light_displacement = db.getFieldString("light_displacement", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_light_displacement = URLEncoder.encode(saved_light_displacement);
            String saved_fresh_water = db.getFieldString("fresh_water", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fresh_water = URLEncoder.encode(saved_fresh_water);
            String saved_immersion_at_load = db.getFieldString("immersion_at_load", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_immersion_at_load = URLEncoder.encode(saved_immersion_at_load);
            String saved_trimming_moment_d = db.getFieldString("trimming_moment_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_trimming_moment_d = URLEncoder.encode(saved_trimming_moment_d);
            String saved_bale_capacity_d = db.getFieldString("bale_capacity_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_bale_capacity_d = URLEncoder.encode(saved_bale_capacity_d);
            String saved_grain_capacity_d = db.getFieldString("grain_capacity_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_grain_capacity_d = URLEncoder.encode(saved_grain_capacity_d);
            String saved_liquid_capacity_d = db.getFieldString("liquid_capacity_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_liquid_capacity_d = URLEncoder.encode(saved_liquid_capacity_d);
            String saved_refrigerated_capacity_d = db.getFieldString("refrigerated_capacity_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_refrigerated_capacity_d = URLEncoder.encode(saved_refrigerated_capacity_d);
            String saved_container_capacity_d = db.getFieldString("container_capacity_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_container_capacity_d = URLEncoder.encode(saved_container_capacity_d);
            String saved_fresh_water_capacity_d = db.getFieldString("fresh_water_capacity_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fresh_water_capacity_d = URLEncoder.encode(saved_fresh_water_capacity_d);
            String saved_daily_fresh_water_gen_d = db.getFieldString("daily_fresh_water_gen_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_daily_fresh_water_gen_d = URLEncoder.encode(saved_daily_fresh_water_gen_d);
            String saved_daily_fresh_water_con_d = db.getFieldString("daily_fresh_water_con_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_daily_fresh_water_con_d = URLEncoder.encode(saved_daily_fresh_water_con_d);
            String saved_main_engine_make_e = db.getFieldString("main_engine_make_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_main_engine_make_e = URLEncoder.encode(saved_main_engine_make_e);
            String saved_main_engine_type = db.getFieldString("main_engine_type", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_main_engine_type = URLEncoder.encode(saved_main_engine_type);
            String saved_main_engine_stroke_e = db.getFieldString("main_engine_stroke_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_main_engine_stroke_e = URLEncoder.encode(saved_main_engine_stroke_e);
            String saved_main_engine_bore_e = db.getFieldString("main_engine_bore_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_main_engine_bore_e = URLEncoder.encode(saved_main_engine_bore_e);
            String saved_main_engine_output = db.getFieldString("main_engine_output", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_main_engine_output = URLEncoder.encode(saved_main_engine_output);
            String saved_main_engine_reduction_gear_e = db.getFieldString("main_engine_reduction_gear_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_main_engine_reduction_gear_e = URLEncoder.encode(saved_main_engine_reduction_gear_e);
            String saved_main_engine_turbo_charger_e = db.getFieldString("main_engine_turbo_charger_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_main_engine_turbo_charger_e = URLEncoder.encode(saved_main_engine_turbo_charger_e);
            String saved_main_engine_service_speed = db.getFieldString("main_engine_service_speed", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_main_engine_service_speed = URLEncoder.encode(saved_main_engine_service_speed);
            String saved_main_engine_boiler_d = db.getFieldString("main_engine_boiler_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_main_engine_boiler_d = URLEncoder.encode(saved_main_engine_boiler_d);
            String saved_main_engine_bunker_capacity_d = db.getFieldString("main_engine_bunker_capacity_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_main_engine_bunker_capacity_d = URLEncoder.encode(saved_main_engine_bunker_capacity_d);
            String saved_main_engine_daily_consumption_d = db.getFieldString("main_engine_daily_consumption_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_main_engine_daily_consumption_d = URLEncoder.encode(saved_main_engine_daily_consumption_d);
            String saved_main_engine_steering_gear_d = db.getFieldString("main_engine_steering_gear_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_main_engine_steering_gear_d = URLEncoder.encode(saved_main_engine_steering_gear_d);
            String saved_auxiliary_make_e = db.getFieldString("auxiliary_make_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_auxiliary_make_e = URLEncoder.encode(saved_auxiliary_make_e);
            String saved_auxiliary_type_e = db.getFieldString("auxiliary_type_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_auxiliary_type_e = URLEncoder.encode(saved_auxiliary_type_e);
            String saved_auxiliary_stroke_e = db.getFieldString("auxiliary_stroke_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_auxiliary_stroke_e = URLEncoder.encode(saved_auxiliary_stroke_e);
            String saved_auxiliary_bore_e = db.getFieldString("auxiliary_bore_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_auxiliary_bore_e = URLEncoder.encode(saved_auxiliary_bore_e);
            String saved_auxiliary_output_e = db.getFieldString("auxiliary_output_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_auxiliary_output_e = URLEncoder.encode(saved_auxiliary_output_e);
            String saved_auxiliary_turbo_charger_e = db.getFieldString("auxiliary_turbo_charger_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_auxiliary_turbo_charger_e = URLEncoder.encode(saved_auxiliary_turbo_charger_e);
            String saved_auxiliary_normal_electrical_e = db.getFieldString("auxiliary_normal_electrical_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_auxiliary_normal_electrical_e = URLEncoder.encode(saved_auxiliary_normal_electrical_e);
            String saved_auxiliary_boiler_make_e = db.getFieldString("auxiliary_boiler_make_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_auxiliary_boiler_make_e = URLEncoder.encode(saved_auxiliary_boiler_make_e);
            String saved_auxiliary_boiler_working_pressure_e = db.getFieldString("auxiliary_boiler_working_pressure_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_auxiliary_boiler_working_pressure_e = URLEncoder.encode(saved_auxiliary_boiler_working_pressure_e);
            String saved_auxiliary_boiler_type_waste_e = db.getFieldString("auxiliary_boiler_type_waste_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_auxiliary_boiler_type_waste_e = URLEncoder.encode(saved_auxiliary_boiler_type_waste_e);
            String saved_fuel_main_engine_fuel_type_e = db.getFieldString("fuel_main_engine_fuel_type_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fuel_main_engine_fuel_type_e = URLEncoder.encode(saved_fuel_main_engine_fuel_type_e);
            String saved_fuel_viscosity_e = db.getFieldString("fuel_viscosity_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fuel_viscosity_e = URLEncoder.encode(saved_fuel_viscosity_e);
            String saved_fuel_specific_fuel_con_e = db.getFieldString("fuel_specific_fuel_con_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fuel_specific_fuel_con_e = URLEncoder.encode(saved_fuel_specific_fuel_con_e);
            String saved_fuel_boiler_fuel_type_e = db.getFieldString("fuel_boiler_fuel_type_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fuel_boiler_fuel_type_e = URLEncoder.encode(saved_fuel_boiler_fuel_type_e);
            String saved_fuel_viscosity_range_e = db.getFieldString("fuel_viscosity_range_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fuel_viscosity_range_e = URLEncoder.encode(saved_fuel_viscosity_range_e);
            String saved_fuel_generator_fuel_type_e = db.getFieldString("fuel_generator_fuel_type_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fuel_generator_fuel_type_e = URLEncoder.encode(saved_fuel_generator_fuel_type_e);
            String saved_fuel_bunker_capacity_e = db.getFieldString("fuel_bunker_capacity_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fuel_bunker_capacity_e = URLEncoder.encode(saved_fuel_bunker_capacity_e);
            String saved_fuel_daily_con_e = db.getFieldString("fuel_daily_con_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fuel_daily_con_e = URLEncoder.encode(saved_fuel_daily_con_e);
            String saved_others_heavy_fuel_oil_e = db.getFieldString("others_heavy_fuel_oil_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_others_heavy_fuel_oil_e = URLEncoder.encode(saved_others_heavy_fuel_oil_e);
            String saved_others_lub_oil_purifier_e = db.getFieldString("others_lub_oil_purifier_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_others_lub_oil_purifier_e = URLEncoder.encode(saved_others_lub_oil_purifier_e);
            String saved_others_air_compressor_e = db.getFieldString("others_air_compressor_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_others_air_compressor_e = URLEncoder.encode(saved_others_air_compressor_e);
            String saved_others_oily_water_separator_e = db.getFieldString("others_oily_water_separator_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_others_oily_water_separator_e = URLEncoder.encode(saved_others_oily_water_separator_e);
            String saved_others_water_capacity_fw_e = db.getFieldString("others_water_capacity_fw_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_others_water_capacity_fw_e = URLEncoder.encode(saved_others_water_capacity_fw_e);
            String saved_others_water_capacity_dw_e = db.getFieldString("others_water_capacity_dw_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_others_water_capacity_dw_e = URLEncoder.encode(saved_others_water_capacity_dw_e);
            String saved_others_fw_generator_e = db.getFieldString("others_fw_generator_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_others_fw_generator_e = URLEncoder.encode(saved_others_fw_generator_e);
            String saved_others_av_cons_e = db.getFieldString("others_av_cons_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_others_av_cons_e = URLEncoder.encode(saved_others_av_cons_e);
            String saved_others_steering_type_e = db.getFieldString("others_steering_type_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_others_steering_type_e = URLEncoder.encode(saved_others_steering_type_e);
            String saved_others_er_lifting_gear_e = db.getFieldString("others_er_lifting_gear_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_others_er_lifting_gear_e = URLEncoder.encode(saved_others_er_lifting_gear_e);
            String saved_others_swl_e = db.getFieldString("others_swl_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_others_swl_e = URLEncoder.encode(saved_others_swl_e);
            String saved_others_sewage_treatment_e = db.getFieldString("others_sewage_treatment_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_others_sewage_treatment_e = URLEncoder.encode(saved_others_sewage_treatment_e);
            String saved_mooring_natural_fiber_d = db.getFieldString("mooring_natural_fiber_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_mooring_natural_fiber_d = URLEncoder.encode(saved_mooring_natural_fiber_d);
            String saved_mooring_synthetic_fiber_d = db.getFieldString("mooring_synthetic_fiber_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_mooring_synthetic_fiber_d = URLEncoder.encode(saved_mooring_synthetic_fiber_d);
            String saved_mooring_wires_d = db.getFieldString("mooring_wires_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_mooring_wires_d = URLEncoder.encode(saved_mooring_wires_d);
            String saved_mooring_towing_spring_d = db.getFieldString("mooring_towing_spring_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_mooring_towing_spring_d = URLEncoder.encode(saved_mooring_towing_spring_d);
            String saved_anchors_port = db.getFieldString("anchors_port", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_anchors_port = URLEncoder.encode(saved_anchors_port);
            String saved_anchors_starboard = db.getFieldString("anchors_starboard", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_anchors_starboard = URLEncoder.encode(saved_anchors_starboard);
            String saved_anchors_stern_d = db.getFieldString("anchors_stern_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_anchors_stern_d = URLEncoder.encode(saved_anchors_stern_d);
            String saved_anchors_spare = db.getFieldString("anchors_spare", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_anchors_spare = URLEncoder.encode(saved_anchors_spare);
            String saved_anchors_cable = db.getFieldString("anchors_cable", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_anchors_cable = URLEncoder.encode(saved_anchors_cable);
            String saved_lifesaving_lifeboat_type_d = db.getFieldString("lifesaving_lifeboat_type_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_lifesaving_lifeboat_type_d = URLEncoder.encode(saved_lifesaving_lifeboat_type_d);
            String saved_lifesaving_lifeboat_no = db.getFieldString("lifesaving_lifeboat_no", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_lifesaving_lifeboat_no = URLEncoder.encode(saved_lifesaving_lifeboat_no);
            String saved_lifesaving_liferaft_no = db.getFieldString("lifesaving_liferaft_no", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_lifesaving_liferaft_no = URLEncoder.encode(saved_lifesaving_liferaft_no);
            String saved_lifesaving_lifeboat_dimension_d = db.getFieldString("lifesaving_lifeboat_dimension_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_lifesaving_lifeboat_dimension_d = URLEncoder.encode(saved_lifesaving_lifeboat_dimension_d);
            String saved_lifesaving_lifeboat_capacity = db.getFieldString("lifesaving_lifeboat_capacity", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_lifesaving_lifeboat_capacity = URLEncoder.encode(saved_lifesaving_lifeboat_capacity);
            String saved_lifesaving_liferaft_capacity = db.getFieldString("lifesaving_liferaft_capacity", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_lifesaving_liferaft_capacity = URLEncoder.encode(saved_lifesaving_liferaft_capacity);
            String saved_lifesaving_lifeboat_davits = db.getFieldString("lifesaving_lifeboat_davits", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_lifesaving_lifeboat_davits = URLEncoder.encode(saved_lifesaving_lifeboat_davits);
            String saved_lifesaving_lifeboat_fall = db.getFieldString("lifesaving_lifeboat_fall", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_lifesaving_lifeboat_fall = URLEncoder.encode(saved_lifesaving_lifeboat_fall);
            String saved_lifesaving_lifebuoys_no = db.getFieldString("lifesaving_lifebuoys_no", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_lifesaving_lifebuoys_no = URLEncoder.encode(saved_lifesaving_lifebuoys_no);
            String saved_fire_extinguisher_no = db.getFieldString("fire_extinguisher_no", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fire_extinguisher_no = URLEncoder.encode(saved_fire_extinguisher_no);
            String saved_fire_water = db.getFieldString("fire_water", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fire_water = URLEncoder.encode(saved_fire_water);
            String saved_fire_foam = db.getFieldString("fire_foam", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fire_foam = URLEncoder.encode(saved_fire_foam);
            String saved_fire_dry_powder = db.getFieldString("fire_dry_powder", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fire_dry_powder = URLEncoder.encode(saved_fire_dry_powder);
            String saved_fire_co2 = db.getFieldString("fire_co2", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fire_co2 = URLEncoder.encode(saved_fire_co2);
            String saved_fire_firehoses = db.getFieldString("fire_firehoses", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fire_firehoses = URLEncoder.encode(saved_fire_firehoses);
            String saved_fire_breathing_e = db.getFieldString("fire_breathing_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fire_breathing_e = URLEncoder.encode(saved_fire_breathing_e);
            String saved_fire_breathing_no_e = db.getFieldString("fire_breathing_no_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fire_breathing_no_e = URLEncoder.encode(saved_fire_breathing_no_e);
            String saved_fire_fixed_fire_system_d = db.getFieldString("fire_fixed_fire_system_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fire_fixed_fire_system_d = URLEncoder.encode(saved_fire_fixed_fire_system_d);
            String saved_fire_scba_d = db.getFieldString("fire_scba_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_fire_scba_d = URLEncoder.encode(saved_fire_scba_d);
            String saved_cargo_handling_derricks = db.getFieldString("cargo_handling_derricks", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_cargo_handling_derricks = URLEncoder.encode(saved_cargo_handling_derricks);
            String saved_cargo_handling_cranes = db.getFieldString("cargo_handling_cranes", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_cargo_handling_cranes = URLEncoder.encode(saved_cargo_handling_cranes);
            String saved_cargo_handling_winches = db.getFieldString("cargo_handling_winches", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_cargo_handling_winches = URLEncoder.encode(saved_cargo_handling_winches);
            String saved_cargo_handling_other_d = db.getFieldString("cargo_handling_other_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_cargo_handling_other_d = URLEncoder.encode(saved_cargo_handling_other_d);
            String saved_cargo_handling_ballast_d = db.getFieldString("cargo_handling_ballast_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_cargo_handling_ballast_d = URLEncoder.encode(saved_cargo_handling_ballast_d);
            String saved_cargo_handling_tank_d = db.getFieldString("cargo_handling_tank_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_cargo_handling_tank_d = URLEncoder.encode(saved_cargo_handling_tank_d);
            String saved_cargo_handling_pump_no = db.getFieldString("cargo_handling_pump_no", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_cargo_handling_pump_no = URLEncoder.encode(saved_cargo_handling_pump_no);
            String saved_cargo_handling_pipelines = db.getFieldString("cargo_handling_pipelines", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_cargo_handling_pipelines = URLEncoder.encode(saved_cargo_handling_pipelines);
            String saved_cargo_handling_type_rating_e = db.getFieldString("cargo_handling_type_rating_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_cargo_handling_type_rating_e = URLEncoder.encode(saved_cargo_handling_type_rating_e);
            String saved_cargo_handling_ballast_pump_e = db.getFieldString("cargo_handling_ballast_pump_e", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_cargo_handling_ballast_pump_e = URLEncoder.encode(saved_cargo_handling_ballast_pump_e);
            String saved_navigational_radar_d = db.getFieldString("navigational_radar_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_radar_d = URLEncoder.encode(saved_navigational_radar_d);
            String saved_navigational_log_d = db.getFieldString("navigational_log_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_log_d = URLEncoder.encode(saved_navigational_log_d);
            String saved_navigational_gps_d = db.getFieldString("navigational_gps_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_gps_d = URLEncoder.encode(saved_navigational_gps_d);
            String saved_navigational_magnetic_d = db.getFieldString("navigational_magnetic_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_magnetic_d = URLEncoder.encode(saved_navigational_magnetic_d);
            String saved_navigational_gyro_d = db.getFieldString("navigational_gyro_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_gyro_d = URLEncoder.encode(saved_navigational_gyro_d);
            String saved_navigational_echo_d = db.getFieldString("navigational_echo_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_echo_d = URLEncoder.encode(saved_navigational_echo_d);
            String saved_navigational_auto_d = db.getFieldString("navigational_auto_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_auto_d = URLEncoder.encode(saved_navigational_auto_d);
            String saved_navigational_vhf_d = db.getFieldString("navigational_vhf_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_vhf_d = URLEncoder.encode(saved_navigational_vhf_d);
            String saved_navigational_mf_hf_d = db.getFieldString("navigational_mf_hf_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_mf_hf_d = URLEncoder.encode(saved_navigational_mf_hf_d);
            String saved_navigational_sat_d = db.getFieldString("navigational_sat_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_sat_d = URLEncoder.encode(saved_navigational_sat_d);
            String saved_navigational_ecdis_d = db.getFieldString("navigational_ecdis_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_ecdis_d = URLEncoder.encode(saved_navigational_ecdis_d);
            String saved_navigational_sart_d = db.getFieldString("navigational_sart_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_sart_d = URLEncoder.encode(saved_navigational_sart_d);
            String saved_navigational_navtex_d = db.getFieldString("navigational_navtex_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_navtex_d = URLEncoder.encode(saved_navigational_navtex_d);
            String saved_navigational_ais_d = db.getFieldString("navigational_ais_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_ais_d = URLEncoder.encode(saved_navigational_ais_d);
            String saved_navigational_vdr_d = db.getFieldString("navigational_vdr_d", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_navigational_vdr_d = URLEncoder.encode(saved_navigational_vdr_d);
            String saved_checked_by_id = db.getFieldString("checked_by_id", "person_vessel_id = '" + id + "'", "person_vessel");
            String saved_app_by_id = db.getFieldString("app_by_id", "person_vessel_id = '" + id + "'", "person_vessel");
            String saved_date_checked = db.getFieldString("date_checked", "person_vessel_id = '" + id + "'", "person_vessel");
            String saved_date_app = db.getFieldString("date_app", "person_vessel_id = '" + id + "'", "person_vessel");
            String saved_checked_remarks = db.getFieldString("checked_remarks", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
            String saved_app_remarks = db.getFieldString("app_remarks", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_app_remarks = URLEncoder.encode(saved_app_remarks);
            String saved_filename = db.getFieldString("filename", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_filename = URLEncoder.encode(saved_filename);
            String saved_date_saved = db.getFieldString("date_saved", "person_vessel_id = '" + id + "'", "person_vessel");
            saved_date_saved = URLEncoder.encode(saved_date_saved);

            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table=person_vessel&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&imo_number="+saved_imo_number+"&call_sign="+saved_call_sign+"&flag="+saved_flag+"&length_over_all="+saved_length_over_all+"&breadth="+saved_breadth+"&depth="+saved_depth+"&summer_draft="+saved_summer_draft+"&summer_freeboard="+saved_summer_freeboard+"&gross_tonnage="+saved_gross_tonnage+"&net_tonnage="+saved_net_tonnage+"&dead_weight="+saved_dead_weight+"&light_displacement="+saved_light_displacement+"&fresh_water="+saved_fresh_water+"&immersion_at_load="+saved_immersion_at_load+"&trimming_moment_d="+saved_trimming_moment_d+"&bale_capacity_d="+saved_bale_capacity_d+"&grain_capacity_d="+saved_grain_capacity_d+"&liquid_capacity_d="+saved_liquid_capacity_d+"&refrigerated_capacity_d="+saved_refrigerated_capacity_d+"&container_capacity_d="+saved_container_capacity_d+"&fresh_water_capacity_d="+saved_fresh_water_capacity_d+"&daily_fresh_water_gen_d="+saved_daily_fresh_water_gen_d+"&daily_fresh_water_con_d="+saved_daily_fresh_water_con_d+"&main_engine_make_e="+saved_main_engine_make_e+"&main_engine_type="+saved_main_engine_type+"&main_engine_stroke_e="+saved_main_engine_stroke_e+"&main_engine_bore_e="+saved_main_engine_bore_e+"&main_engine_output="+saved_main_engine_output+"&main_engine_reduction_gear_e="+saved_main_engine_reduction_gear_e+"&main_engine_turbo_charger_e="+saved_main_engine_turbo_charger_e+"&main_engine_service_speed="+saved_main_engine_service_speed+"&main_engine_boiler_d="+saved_main_engine_boiler_d+"&main_engine_bunker_capacity_d="+saved_main_engine_bunker_capacity_d+"&main_engine_daily_consumption_d="+saved_main_engine_daily_consumption_d+"&main_engine_steering_gear_d="+saved_main_engine_steering_gear_d+"&auxiliary_make_e="+saved_auxiliary_make_e+"&auxiliary_type_e="+saved_auxiliary_type_e+"&auxiliary_stroke_e="+saved_auxiliary_stroke_e+"&auxiliary_bore_e="+saved_auxiliary_bore_e+"&auxiliary_output_e="+saved_auxiliary_output_e+"&auxiliary_turbo_charger_e="+saved_auxiliary_turbo_charger_e+"&auxiliary_normal_electrical_e="+saved_auxiliary_normal_electrical_e+"&auxiliary_boiler_make_e="+saved_auxiliary_boiler_make_e+"&auxiliary_boiler_working_pressure_e="+saved_auxiliary_boiler_working_pressure_e+"&auxiliary_boiler_type_waste_e="+saved_auxiliary_boiler_type_waste_e+"&fuel_main_engine_fuel_type_e="+saved_fuel_main_engine_fuel_type_e+"&fuel_viscosity_e="+saved_fuel_viscosity_e+"&fuel_specific_fuel_con_e="+saved_fuel_specific_fuel_con_e+"&fuel_boiler_fuel_type_e="+saved_fuel_boiler_fuel_type_e+"&fuel_viscosity_range_e="+saved_fuel_viscosity_range_e+"&fuel_generator_fuel_type_e="+saved_fuel_generator_fuel_type_e+"&fuel_bunker_capacity_e="+saved_fuel_bunker_capacity_e+"&fuel_daily_con_e="+saved_fuel_daily_con_e+"&others_heavy_fuel_oil_e="+saved_others_heavy_fuel_oil_e+"&others_lub_oil_purifier_e="+saved_others_lub_oil_purifier_e+"&others_air_compressor_e="+saved_others_air_compressor_e+"&others_oily_water_separator_e="+saved_others_oily_water_separator_e+"&others_water_capacity_fw_e="+saved_others_water_capacity_fw_e+"&others_water_capacity_dw_e="+saved_others_water_capacity_dw_e+"&others_fw_generator_e="+saved_others_fw_generator_e+"&others_av_cons_e="+saved_others_av_cons_e+"&others_steering_type_e="+saved_others_steering_type_e+"&others_er_lifting_gear_e="+saved_others_er_lifting_gear_e+"&others_swl_e="+saved_others_swl_e+"&others_sewage_treatment_e="+saved_others_sewage_treatment_e+"&mooring_natural_fiber_d="+saved_mooring_natural_fiber_d+"&mooring_synthetic_fiber_d="+saved_mooring_synthetic_fiber_d+"&mooring_wires_d="+saved_mooring_wires_d+"&mooring_towing_spring_d="+saved_mooring_towing_spring_d+"&anchors_port="+saved_anchors_port+"&anchors_starboard="+saved_anchors_starboard+"&anchors_stern_d="+saved_anchors_stern_d+"&anchors_spare="+saved_anchors_spare+"&anchors_cable="+saved_anchors_cable+"&lifesaving_lifeboat_type_d="+saved_lifesaving_lifeboat_type_d+"&lifesaving_lifeboat_no="+saved_lifesaving_lifeboat_no+"&lifesaving_liferaft_no="+saved_lifesaving_liferaft_no+"&lifesaving_lifeboat_dimension_d="+saved_lifesaving_lifeboat_dimension_d+"&lifesaving_lifeboat_capacity="+saved_lifesaving_lifeboat_capacity+"&lifesaving_liferaft_capacity="+saved_lifesaving_liferaft_capacity+"&lifesaving_lifeboat_davits="+saved_lifesaving_lifeboat_davits+"&lifesaving_lifeboat_fall="+saved_lifesaving_lifeboat_fall+"&lifesaving_lifebuoys_no="+saved_lifesaving_lifebuoys_no+"&fire_extinguisher_no="+saved_fire_extinguisher_no+"&fire_water="+saved_fire_water+"&fire_foam="+saved_fire_foam+"&fire_dry_powder="+saved_fire_dry_powder+"&fire_co2="+saved_fire_co2+"&fire_firehoses="+saved_fire_firehoses+"&fire_breathing_e="+saved_fire_breathing_e+"&fire_breathing_no_e="+saved_fire_breathing_no_e+"&fire_fixed_fire_system_d="+saved_fire_fixed_fire_system_d+"&fire_scba_d="+saved_fire_scba_d+"&cargo_handling_derricks="+saved_cargo_handling_derricks+"&cargo_handling_cranes="+saved_cargo_handling_cranes+"&cargo_handling_winches="+saved_cargo_handling_winches+"&cargo_handling_other_d="+saved_cargo_handling_other_d+"&cargo_handling_ballast_d="+saved_cargo_handling_ballast_d+"&cargo_handling_tank_d="+saved_cargo_handling_tank_d+"&cargo_handling_pump_no="+saved_cargo_handling_pump_no+"&cargo_handling_pipelines="+saved_cargo_handling_pipelines+"&cargo_handling_type_rating_e="+saved_cargo_handling_type_rating_e+"&cargo_handling_ballast_pump_e="+saved_cargo_handling_ballast_pump_e+"&navigational_radar_d="+saved_navigational_radar_d+"&navigational_log_d="+saved_navigational_log_d+"&navigational_gps_d="+saved_navigational_gps_d+"&navigational_magnetic_d="+saved_navigational_magnetic_d+"&navigational_gyro_d="+saved_navigational_gyro_d+"&navigational_echo_d="+saved_navigational_echo_d+"&navigational_auto_d="+saved_navigational_auto_d+"&navigational_vhf_d="+saved_navigational_vhf_d+"&navigational_mf_hf_d="+saved_navigational_mf_hf_d+"&navigational_sat_d="+saved_navigational_sat_d+"&navigational_ecdis_d="+saved_navigational_ecdis_d+"&navigational_sart_d="+saved_navigational_sart_d+"&navigational_navtex_d="+saved_navigational_navtex_d+"&navigational_ais_d="+saved_navigational_ais_d+"&navigational_vdr_d="+saved_navigational_vdr_d+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&filename="+saved_filename+"&date_saved="+saved_date_saved+ "&event=" + event);
            Log.d("CONNECT", url +"sync.php?table=person_vessel&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&imo_number="+saved_imo_number+"&call_sign="+saved_call_sign+"&flag="+saved_flag+"&length_over_all="+saved_length_over_all+"&breadth="+saved_breadth+"&depth="+saved_depth+"&summer_draft="+saved_summer_draft+"&summer_freeboard="+saved_summer_freeboard+"&gross_tonnage="+saved_gross_tonnage+"&net_tonnage="+saved_net_tonnage+"&dead_weight="+saved_dead_weight+"&light_displacement="+saved_light_displacement+"&fresh_water="+saved_fresh_water+"&immersion_at_load="+saved_immersion_at_load+"&trimming_moment_d="+saved_trimming_moment_d+"&bale_capacity_d="+saved_bale_capacity_d+"&grain_capacity_d="+saved_grain_capacity_d+"&liquid_capacity_d="+saved_liquid_capacity_d+"&refrigerated_capacity_d="+saved_refrigerated_capacity_d+"&container_capacity_d="+saved_container_capacity_d+"&fresh_water_capacity_d="+saved_fresh_water_capacity_d+"&daily_fresh_water_gen_d="+saved_daily_fresh_water_gen_d+"&daily_fresh_water_con_d="+saved_daily_fresh_water_con_d+"&main_engine_make_e="+saved_main_engine_make_e+"&main_engine_type="+saved_main_engine_type+"&main_engine_stroke_e="+saved_main_engine_stroke_e+"&main_engine_bore_e="+saved_main_engine_bore_e+"&main_engine_output="+saved_main_engine_output+"&main_engine_reduction_gear_e="+saved_main_engine_reduction_gear_e+"&main_engine_turbo_charger_e="+saved_main_engine_turbo_charger_e+"&main_engine_service_speed="+saved_main_engine_service_speed+"&main_engine_boiler_d="+saved_main_engine_boiler_d+"&main_engine_bunker_capacity_d="+saved_main_engine_bunker_capacity_d+"&main_engine_daily_consumption_d="+saved_main_engine_daily_consumption_d+"&main_engine_steering_gear_d="+saved_main_engine_steering_gear_d+"&auxiliary_make_e="+saved_auxiliary_make_e+"&auxiliary_type_e="+saved_auxiliary_type_e+"&auxiliary_stroke_e="+saved_auxiliary_stroke_e+"&auxiliary_bore_e="+saved_auxiliary_bore_e+"&auxiliary_output_e="+saved_auxiliary_output_e+"&auxiliary_turbo_charger_e="+saved_auxiliary_turbo_charger_e+"&auxiliary_normal_electrical_e="+saved_auxiliary_normal_electrical_e+"&auxiliary_boiler_make_e="+saved_auxiliary_boiler_make_e+"&auxiliary_boiler_working_pressure_e="+saved_auxiliary_boiler_working_pressure_e+"&auxiliary_boiler_type_waste_e="+saved_auxiliary_boiler_type_waste_e+"&fuel_main_engine_fuel_type_e="+saved_fuel_main_engine_fuel_type_e+"&fuel_viscosity_e="+saved_fuel_viscosity_e+"&fuel_specific_fuel_con_e="+saved_fuel_specific_fuel_con_e+"&fuel_boiler_fuel_type_e="+saved_fuel_boiler_fuel_type_e+"&fuel_viscosity_range_e="+saved_fuel_viscosity_range_e+"&fuel_generator_fuel_type_e="+saved_fuel_generator_fuel_type_e+"&fuel_bunker_capacity_e="+saved_fuel_bunker_capacity_e+"&fuel_daily_con_e="+saved_fuel_daily_con_e+"&others_heavy_fuel_oil_e="+saved_others_heavy_fuel_oil_e+"&others_lub_oil_purifier_e="+saved_others_lub_oil_purifier_e+"&others_air_compressor_e="+saved_others_air_compressor_e+"&others_oily_water_separator_e="+saved_others_oily_water_separator_e+"&others_water_capacity_fw_e="+saved_others_water_capacity_fw_e+"&others_water_capacity_dw_e="+saved_others_water_capacity_dw_e+"&others_fw_generator_e="+saved_others_fw_generator_e+"&others_av_cons_e="+saved_others_av_cons_e+"&others_steering_type_e="+saved_others_steering_type_e+"&others_er_lifting_gear_e="+saved_others_er_lifting_gear_e+"&others_swl_e="+saved_others_swl_e+"&others_sewage_treatment_e="+saved_others_sewage_treatment_e+"&mooring_natural_fiber_d="+saved_mooring_natural_fiber_d+"&mooring_synthetic_fiber_d="+saved_mooring_synthetic_fiber_d+"&mooring_wires_d="+saved_mooring_wires_d+"&mooring_towing_spring_d="+saved_mooring_towing_spring_d+"&anchors_port="+saved_anchors_port+"&anchors_starboard="+saved_anchors_starboard+"&anchors_stern_d="+saved_anchors_stern_d+"&anchors_spare="+saved_anchors_spare+"&anchors_cable="+saved_anchors_cable+"&lifesaving_lifeboat_type_d="+saved_lifesaving_lifeboat_type_d+"&lifesaving_lifeboat_no="+saved_lifesaving_lifeboat_no+"&lifesaving_liferaft_no="+saved_lifesaving_liferaft_no+"&lifesaving_lifeboat_dimension_d="+saved_lifesaving_lifeboat_dimension_d+"&lifesaving_lifeboat_capacity="+saved_lifesaving_lifeboat_capacity+"&lifesaving_liferaft_capacity="+saved_lifesaving_liferaft_capacity+"&lifesaving_lifeboat_davits="+saved_lifesaving_lifeboat_davits+"&lifesaving_lifeboat_fall="+saved_lifesaving_lifeboat_fall+"&lifesaving_lifebuoys_no="+saved_lifesaving_lifebuoys_no+"&fire_extinguisher_no="+saved_fire_extinguisher_no+"&fire_water="+saved_fire_water+"&fire_foam="+saved_fire_foam+"&fire_dry_powder="+saved_fire_dry_powder+"&fire_co2="+saved_fire_co2+"&fire_firehoses="+saved_fire_firehoses+"&fire_breathing_e="+saved_fire_breathing_e+"&fire_breathing_no_e="+saved_fire_breathing_no_e+"&fire_fixed_fire_system_d="+saved_fire_fixed_fire_system_d+"&fire_scba_d="+saved_fire_scba_d+"&cargo_handling_derricks="+saved_cargo_handling_derricks+"&cargo_handling_cranes="+saved_cargo_handling_cranes+"&cargo_handling_winches="+saved_cargo_handling_winches+"&cargo_handling_other_d="+saved_cargo_handling_other_d+"&cargo_handling_ballast_d="+saved_cargo_handling_ballast_d+"&cargo_handling_tank_d="+saved_cargo_handling_tank_d+"&cargo_handling_pump_no="+saved_cargo_handling_pump_no+"&cargo_handling_pipelines="+saved_cargo_handling_pipelines+"&cargo_handling_type_rating_e="+saved_cargo_handling_type_rating_e+"&cargo_handling_ballast_pump_e="+saved_cargo_handling_ballast_pump_e+"&navigational_radar_d="+saved_navigational_radar_d+"&navigational_log_d="+saved_navigational_log_d+"&navigational_gps_d="+saved_navigational_gps_d+"&navigational_magnetic_d="+saved_navigational_magnetic_d+"&navigational_gyro_d="+saved_navigational_gyro_d+"&navigational_echo_d="+saved_navigational_echo_d+"&navigational_auto_d="+saved_navigational_auto_d+"&navigational_vhf_d="+saved_navigational_vhf_d+"&navigational_mf_hf_d="+saved_navigational_mf_hf_d+"&navigational_sat_d="+saved_navigational_sat_d+"&navigational_ecdis_d="+saved_navigational_ecdis_d+"&navigational_sart_d="+saved_navigational_sart_d+"&navigational_navtex_d="+saved_navigational_navtex_d+"&navigational_ais_d="+saved_navigational_ais_d+"&navigational_vdr_d="+saved_navigational_vdr_d+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+ "&event=" + event);

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
            if(!saved_filename.equals("")){
                saved_filename = URLDecoder.decode(saved_filename);
                uploadImage(saved_filename);
            }

            return null;
        }
        protected void onPostExecute(Void result){
            pd.dismiss();
            Intent intent = new Intent(ShipParticularFormActivity.this, ShipParticularActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(ShipParticularFormActivity.this, "Record saved successfully.", Toast.LENGTH_LONG).show();

        }
    }

    private class SyncOnlineDelete extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public String table;
        public String tbl_id;
        public SyncOnlineDelete(Context context, String table, String id)
        {
            this.context = context;
            this.table  = table;
            this.tbl_id = id;

        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            String url = getString(R.string.url);
            //GET FROM TBL
            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"sync.php?table="+table+"&id="+tbl_id+"&event=DELETE");
            Log.d("CONNECT", url +"sync.php?table="+table+"&id="+tbl_id+"&event=DELETE");

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
            pd.dismiss();
            Intent intent = new Intent(ShipParticularFormActivity.this, ShipParticularActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(ShipParticularFormActivity.this, "Record deleted successfully.", Toast.LENGTH_LONG).show();



        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Are you sure you want to leave? Changes you make will not be saved.");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(ShipParticularFormActivity.this, ShipParticularActivity.class);
                startActivity(intent);
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.black));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.elosoftbiz.etrb_trmf.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                //setPic();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "ship_" + timeStamp + ".jpg";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File image = File.createTempFile("eRTB_" + person_task_id, ".jpg", storageDir);
        File image = new File(storageDir, imageFileName);

        //Toast.makeText(PersonTaskActivity.this, "" + imageFileName, Toast.LENGTH_SHORT).show();
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
       tv_filename.setText(imageFileName);
       filename = imageFileName;
    }

    public int uploadImage(String sourceFileUri) {
        final String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/"+ sourceFileUri);

        if (!sourceFile.isFile()) {
            Log.d("RESULT", "Source File not exist :" +upLoadServerUri + "" + sourceFileUri);

            return 0;

        }else{
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();


                Log.d("RESULT", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Log.d("RESULT: ", sourceFileUri + "File Upload Complete.");
                        }
                    });
                }else{
                    Log.d("RESULT: ", "REUPLOAD " + sourceFileUri);
                    uploadImage(sourceFileUri);
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.d("RESULT", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();


                Log.d("RESULT", "Exception : " + e.getMessage(), e);
            }
            return serverResponseCode;

        } // End else block
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( pd!=null && pd.isShowing() ){
            pd.cancel();
        }
    }
}
