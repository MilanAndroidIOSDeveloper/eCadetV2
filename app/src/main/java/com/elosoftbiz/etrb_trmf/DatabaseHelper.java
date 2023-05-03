package com.elosoftbiz.etrb_trmf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elosoftinc. on 4/24/19.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "etrb_trmf.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS person");
        db.execSQL("CREATE TABLE person " +
                "(id INTEGER PRIMARY KEY,  person_id TEXT," +
                "code_person TEXT,lname TEXT," +
                "fname TEXT, mname TEXT," +
                "st_address TEXT, city_id TEXT," +
                "province_id TEXT, phone TEXT," +
                "mobile TEXT, st_address_province TEXT," +
                "phone_province TEXT, email TEXT, " +
                "gender TEXT, civ_status TEXT," +
                "birth_date TEXT, birth_place TEXT," +
                "spouse_name TEXT, children INTEGER," +
                "father_name TEXT, mother_name TEXT," +
                "notes TEXT, active TEXT," +
                "date_reg TEXT, compman_id TEXT," +
                "company_id TEXT, " +
                "amt_paid NUMERIC, rank_id TEXT, vessel_officer TEXT," +
                "dept TEXT, vessel_id TEXT," +
                "course_id TEXT, school_id TEXT," +
                "school_admin TEXT, type TEXT, batch_no INTEGER, " +
                "pct_done DOUBLE, days_on_board INTEGER, " +
                "photo_file TEXT, passport_no TEXT," +
                "cdc_no TEXT, indos_no TEXT, height TEXT, weight TEXT," +
                "enrtry_date TEXT, marks TEXT, blood_group TEXT," +
                "emergency_contact_name TEXT, emergency_contact_address TEXT, " +
                "emergency_contact_no TEXT, emergency_relationship TEXT," +
                "nationality TEXT, passport_no_issue_date TEXT, " +
                "cdc_no_issue_date TEXT, cdc_no_issue_place TEXT," +
                "emergency_email TEXT, sponsor_id TEXT, dig_signature TEXT, " +
                "srn TEXT, srb_no TEXT, srb_date TEXT, sid_no TEXT, passport_date TEXT," +
                "full_name TEXT, logged_in TEXT, w_fr TEXT, client_id TEXT, login_name TEXT, login_pass TEXT, login_type_id TEXT)");

        /**TRB FUNCTION**/
        db.execSQL("DROP TABLE IF EXISTS trb_function");
        db.execSQL("CREATE TABLE trb_function (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "trb_function_id TEXT, desc_trb_function TEXT, " +
                "dept TEXT, prio INTEGER," +
                "vessel_type_id TEXT," +
                "dp TEXT, ice_class TEXT, spec_to TEXT, engine_type_id TEXT, ref_no_function TEXT)");

        /**TRB COMPETENCE**/
        db.execSQL("DROP TABLE IF EXISTS trb_competence");
        db.execSQL("CREATE TABLE trb_competence (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "trb_competence_id TEXT, ref_no TEXT, " +
                "desc_competence TEXT, trb_function_id TEXT, prio INTEGER, criteria TEXT, instruction TEXT )");
        db.execSQL("CREATE INDEX idx_trb_competence ON trb_competence (trb_competence_id, desc_competence, trb_function_id);");

        /**TRB TOPIC**/
        db.execSQL("DROP TABLE IF EXISTS trb_topic");
        db.execSQL("CREATE TABLE trb_topic (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "trb_topic_id TEXT, ref_no_topic TEXT, " +
                "desc_topic TEXT, trb_competence_id TEXT, criteria TEXT, prio INTEGER, trb_function_id TEXT )");
        db.execSQL("CREATE INDEX idx_trb_topic ON trb_topic (trb_topic_id, ref_no_topic, desc_topic, trb_competence_id, criteria)");

        /**TRB SUBCOMPETENCE**/
        db.execSQL("DROP TABLE IF EXISTS trb_sub_competence");
        db.execSQL("CREATE TABLE trb_sub_competence (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "trb_sub_competence_id TEXT, ref_no TEXT, " +
                "desc_sub_competence TEXT, trb_competence_id TEXT, criteria_id TEXT )");

        /**TASK GROUP**/
        db.execSQL("DROP TABLE IF EXISTS trb_task_group");
        db.execSQL("CREATE TABLE trb_task_group (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "trb_task_group_id TEXT, trb_topic_id TEXT, " +
                "trb_competence_id TEXT, trb_function_id TEXT, " +
                "ref_no_task_group TEXT, desc_task_group TEXT, cond_task_group TEXT, prio INTEGER )");
        db.execSQL("CREATE UNIQUE INDEX index_trb_task_group ON trb_task_group (id, trb_task_group_id, trb_topic_id, trb_competence_id, trb_function_id)");


        /**TASK**/
        db.execSQL("DROP TABLE IF EXISTS task");
        db.execSQL("CREATE TABLE task (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "task_id TEXT, ref_no_task TEXT, " +
                "desc_task TEXT, trb_competence_id TEXT, " +
                "trb_topic_id TEXT, prio INTEGER, phase_no INTEGER, cat_task_id TEXT, learning_activity TEXT, direction_id TEXT," +
                " criteria TEXT, trb_function_id TEXT, trb_task_group_id TEXT )");
        db.execSQL("CREATE UNIQUE INDEX index_task ON task (id, task_id, desc_task, trb_competence_id, trb_topic_id)");

        /*** PERSON TRB TOPIC ***/
        db.execSQL("DROP TABLE IF EXISTS person_trb_topic");
        db.execSQL("CREATE TABLE person_trb_topic(person_trb_topic_id TEXT, id INTEGER PRIMARY KEY, trb_topic_id TEXT, person_id TEXT, checked_by_id TEXT, date_checked TEXT, checked_remarks TEXT)");
        db.execSQL("CREATE INDEX idx_person_trb_topic ON person_trb_topic (person_trb_topic_id, trb_topic_id)");

        /*** PERSON TRB TOPIC ***/
        db.execSQL("DROP TABLE IF EXISTS person_trb_sub_competence");
        db.execSQL("CREATE TABLE person_trb_sub_competence(person_trb_sub_competence_id TEXT, id INTEGER PRIMARY KEY, trb_sub_competence_id TEXT, person_id TEXT, checked_by_id TEXT, date_checked TEXT, checked_remarks TEXT)");
        db.execSQL("CREATE INDEX idx_person_trb_sub_competence ON person_trb_sub_competence (person_trb_sub_competence_id, trb_sub_competence_id)");

        /*** STEER COMPETENCE ***/
        db.execSQL("DROP TABLE IF EXISTS steer_competence");
        db.execSQL("CREATE TABLE steer_competence( steer_competence_id TEXT, id INTEGER PRIMARY KEY, ref_no TEXT, desc_competence TEXT, prio INTEGER)");
        db.execSQL("CREATE UNIQUE INDEX index_steer_competence ON steer_competence (id, steer_competence_id, ref_no, desc_competence)");

        /*** STEER TOPIC ***/
        db.execSQL("DROP TABLE IF EXISTS steer_topic");
        db.execSQL("CREATE TABLE steer_topic(steer_topic_id TEXT, id INTEGER PRIMARY KEY, ref_no_topic TEXT, desc_steer_topic TEXT, steer_competence_id TEXT, criteria TEXT, prio INTEGER)");
        db.execSQL("CREATE UNIQUE INDEX index_steer_topic ON steer_topic (id, steer_topic_id, ref_no_topic, desc_steer_topic)");

        /*** STEER TASK ***/
        db.execSQL("DROP TABLE IF EXISTS steer_task");
        db.execSQL("CREATE TABLE steer_task(steer_task_id TEXT, id INTEGER PRIMARY KEY, ref_no TEXT, desc_steer_task TEXT, steer_competence_id TEXT, steer_topic_id TEXT, prio INTEGER, phase_no INTEGER)");
        db.execSQL("CREATE UNIQUE INDEX index_steer_task ON steer_task (id, steer_task_id, ref_no, desc_steer_task, steer_competence_id)");

        /*** PERSON STEER TOPIC ***/
        db.execSQL("DROP TABLE IF EXISTS person_steer_topic");
        db.execSQL("CREATE TABLE person_steer_topic(person_steer_topic_id TEXT, id INTEGER PRIMARY KEY, steer_topic_id TEXT, person_id TEXT, checked_by_id TEXT, date_checked TEXT, checked_remarks TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_steer_topic ON person_steer_topic (id, person_steer_topic_id, steer_topic_id, person_id, checked_by_id)");

        /*** PERSON STEER TASK ***/
        db.execSQL("DROP TABLE IF EXISTS person_steer_task");
        db.execSQL("CREATE TABLE person_steer_task(person_steer_task_id TEXT, id INTEGER PRIMARY KEY, steer_task_id TEXT, person_id TEXT, vessel_id TEXT, completed TEXT, answers TEXT, passed TEXT, not_app TEXT, lat_long TEXT, checked_by_id TEXT, app_by_id TEXT, date_checked TEXT, date_app TEXT, checked_remarks TEXT, app_remarks TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_steer_task ON person_steer_task (id, person_steer_task_id, steer_task_id, person_id, vessel_id)");

        //*** LOGIN WITH LOGIN ****//
        db.execSQL("DROP TABLE IF EXISTS login");
        db.execSQL("CREATE TABLE login (id INTEGER PRIMARY KEY, login_id TEXT," +
                "email TEXT, login_name TEXT, login_pass TEXT, session_id TEXT, " +
                "login_type_id TEXT, initial TEXT, lname TEXT, fname TEXT, mname TEXT, " +
                "company_id TEXT, designation TEXT)");

        //***PROGRAM ****//
        db.execSQL("DROP TABLE IF EXISTS program");
        db.execSQL("CREATE TABLE program (id INTEGER PRIMARY KEY, program_id TEXT," +
                "desc_program TEXT, dept TEXT," +
                "prio INTEGER)");
        db.execSQL("CREATE UNIQUE INDEX index_program ON program (id, program_id, desc_program)");
        db.execSQL("DROP TABLE IF EXISTS person_program");
        db.execSQL("CREATE TABLE person_program (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "person_program_id TEXT, desc_person_program TEXT," +
                "person_id TEXT," +
                "program_id TEXT, " +
                "from_date TEXT, to_date TEXT, checked_by_id TEXT, app_by_id TEXT, " +
                "date_checked TEXT, date_app TEXT, checked_remarks TEXT, app_remarks TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_program ON person_program (id, person_program_id, desc_person_program, person_id, program_id)");
        db.execSQL("DROP TABLE IF EXISTS backup_history");
        db.execSQL("CREATE TABLE backup_history (id INTEGER PRIMARY KEY, back_up_date TEXT," +
                "back_up_time TEXT, back_up_status TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_backup_history ON backup_history (id, back_up_date, back_up_time, back_up_status)");

        //*** SHIPBOARD ****//
        db.execSQL("DROP TABLE IF EXISTS shipboard");
        db.execSQL("CREATE TABLE shipboard(id INTEGER PRIMARY KEY , shipboard_id TEXT, sign_on TEXT, sign_off TEXT, engine_watch_mos TEXT, engine_watch_yrs TEXT, voyage_mos TEXT, person_id TEXT, vessel_id TEXT, voyage_days TEXT, imo_number TEXT, srb_file TEXT, cert_file TEXT, checked_by_id TEXT, date_checked TEXT, contract_file TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_shipboard ON shipboard (id, shipboard_id, person_id, sign_on, sign_off, vessel_id)");

        //*** VESSEL ****//
        db.execSQL("DROP TABLE IF EXISTS vessel");
        db.execSQL("CREATE TABLE vessel (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "vessel_id TEXT, name_vessel TEXT," +
                "owner_company_id TEXT, operator_company_id TEXT, " +
                "year_built INTEGER, flag_registry_id TEXT," +
                "hp DOUBLE, kw DOUBLE, grt DOUBLE," +
                "trade_type TEXT, imo_number TEXT, call_sign TEXT, vessel_type_id TEXT," +
                "dp TEXT, ice_class TEXT, motor TEXT, st TEXT, gt TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_vessel ON vessel (id, vessel_id, name_vessel, vessel_type_id, dp, ice_class)");

        //*** VESSEL TYPE ****//
        db.execSQL("DROP TABLE IF EXISTS vessel_type");
        db.execSQL("CREATE TABLE vessel_type (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "vessel_type_id TEXT, desc_vessel_type TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_vessel_type ON vessel_type (id, vessel_type_id, desc_vessel_type)");

        //*** PERSON VESSEL ****//
        db.execSQL("DROP TABLE IF EXISTS person_vessel");
        db.execSQL("CREATE TABLE person_vessel( person_vessel_id TEXT, id TEXT, person_id TEXT, " +
                "vessel_id TEXT, imo_number TEXT, call_sign TEXT, flag TEXT, length_over_all TEXT, " +
                "breadth TEXT, depth TEXT, summer_draft TEXT, summer_freeboard TEXT, gross_tonnage TEXT, " +
                "net_tonnage TEXT, dead_weight TEXT, light_displacement TEXT, fresh_water TEXT, " +
                "immersion_at_load TEXT, trimming_moment_d TEXT, bale_capacity_d TEXT, grain_capacity_d TEXT, " +
                "liquid_capacity_d TEXT, refrigerated_capacity_d TEXT, container_capacity_d TEXT, " +
                "fresh_water_capacity_d TEXT, daily_fresh_water_gen_d TEXT, daily_fresh_water_con_d TEXT, " +
                "main_engine_make_e TEXT, main_engine_type TEXT, main_engine_stroke_e TEXT, main_engine_bore_e TEXT, " +
                "main_engine_output TEXT, main_engine_reduction_gear_e TEXT, main_engine_turbo_charger_e TEXT, " +
                "main_engine_service_speed TEXT, main_engine_boiler_d TEXT, main_engine_bunker_capacity_d TEXT, " +
                "main_engine_daily_consumption_d TEXT, main_engine_steering_gear_d TEXT, auxiliary_make_e TEXT, " +
                "auxiliary_type_e TEXT, auxiliary_stroke_e TEXT, auxiliary_bore_e TEXT, auxiliary_output_e TEXT, " +
                "auxiliary_turbo_charger_e TEXT, auxiliary_normal_electrical_e TEXT, auxiliary_boiler_make_e TEXT, " +
                "auxiliary_boiler_working_pressure_e TEXT, auxiliary_boiler_type_waste_e TEXT, " +
                "fuel_main_engine_fuel_type_e TEXT, fuel_viscosity_e TEXT, fuel_specific_fuel_con_e TEXT, " +
                "fuel_boiler_fuel_type_e TEXT, fuel_viscosity_range_e TEXT, fuel_generator_fuel_type_e TEXT, " +
                "fuel_bunker_capacity_e TEXT, fuel_daily_con_e TEXT, others_heavy_fuel_oil_e TEXT, " +
                "others_lub_oil_purifier_e TEXT, others_air_compressor_e TEXT, others_oily_water_separator_e TEXT, " +
                "others_water_capacity_fw_e TEXT, others_water_capacity_dw_e TEXT, others_fw_generator_e TEXT, " +
                "others_av_cons_e TEXT, others_steering_type_e TEXT, others_er_lifting_gear_e TEXT, others_swl_e TEXT, " +
                "others_sewage_treatment_e TEXT, mooring_natural_fiber_d TEXT, mooring_synthetic_fiber_d TEXT, " +
                "mooring_wires_d TEXT, mooring_towing_spring_d TEXT, anchors_port TEXT, anchors_starboard TEXT, " +
                "anchors_stern_d TEXT, anchors_spare TEXT, anchors_cable TEXT, lifesaving_lifeboat_type_d TEXT, " +
                "lifesaving_lifeboat_no TEXT, lifesaving_liferaft_no TEXT, lifesaving_lifeboat_dimension_d TEXT, " +
                "lifesaving_lifeboat_capacity TEXT, lifesaving_liferaft_capacity TEXT, lifesaving_lifeboat_davits TEXT, " +
                "lifesaving_lifeboat_fall TEXT, lifesaving_lifebuoys_no TEXT, fire_extinguisher_no TEXT, " +
                "fire_water TEXT, fire_foam TEXT, fire_dry_powder TEXT, fire_co2 TEXT, fire_firehoses TEXT, " +
                "fire_breathing_e TEXT, fire_breathing_no_e TEXT, fire_fixed_fire_system_d TEXT, fire_scba_d TEXT," +
                " cargo_handling_derricks TEXT, cargo_handling_cranes TEXT, cargo_handling_winches TEXT, " +
                "cargo_handling_other_d TEXT, cargo_handling_ballast_d TEXT, cargo_handling_tank_d TEXT, " +
                "cargo_handling_pump_no TEXT, cargo_handling_pipelines TEXT, cargo_handling_type_rating_e TEXT, cargo_handling_ballast_pump_e TEXT, " +
                "navigational_radar_d TEXT, navigational_log_d TEXT, navigational_gps_d TEXT, " +
                "navigational_magnetic_d TEXT, navigational_gyro_d TEXT, navigational_echo_d TEXT, " +
                "navigational_auto_d TEXT, navigational_vhf_d TEXT, navigational_mf_hf_d TEXT, navigational_sat_d TEXT, " +
                "navigational_ecdis_d TEXT, navigational_sart_d TEXT, navigational_navtex_d TEXT, " +
                "navigational_ais_d TEXT, navigational_vdr_d TEXT, checked_by_id TEXT, app_by_id TEXT, " +
                "date_checked TEXT, date_app TEXT, checked_remarks TEXT, app_remarks TEXT, filename TEXT, date_saved TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_vessel ON person_vessel (id, person_vessel_id, person_id, vessel_id)");

        //*** BASIC TRAINING ****//
        db.execSQL("DROP TABLE IF EXISTS basic_training");
        db.execSQL("CREATE TABLE basic_training (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "basic_training_id TEXT, desc_basic_training TEXT," +
                "dept TEXT, prio INTEGER, training_type TEXT, stcw_code TEXT )");
        db.execSQL("CREATE UNIQUE INDEX index_basic_training ON basic_training (id, basic_training_id, desc_basic_training, training_type)");

        //*** PERSON BASIC TRAINING ****//
        db.execSQL("DROP TABLE IF EXISTS person_basic_training");
        db.execSQL("CREATE TABLE person_basic_training (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "person_basic_training_id TEXT, person_id TEXT, " +
                "basic_training_id TEXT, " +
                "location_name TEXT, date_completed TEXT, " +
                "doc_ref_no TEXT," +
                "checked_by_id TEXT, app_by_id TEXT, " +
                "date_checked TEXT, date_app TEXT, " +
                "checked_remarks TEXT, " +
                "app_remarks TEXT, institution TEXT )");
        db.execSQL("CREATE UNIQUE INDEX index_person_basic_training ON person_basic_training (id, person_basic_training_id, person_id, basic_training_id)");

        //*** PERSON TO ****//
        db.execSQL("DROP TABLE IF EXISTS person_to");
        db.execSQL("CREATE TABLE person_to (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "person_to_id TEXT, person_id TEXT, date_signed TEXT, " +
                "checked_by_id TEXT, app_by_id TEXT,  date_checked TEXT, date_app TEXT," +
                "checked_remarks TEXT, app_remarks TEXT, vessel_id TEXT )");
        db.execSQL("CREATE UNIQUE INDEX index_person_to ON person_to (id, person_to_id, person_id, date_signed)");

        //*** PERSON CE ****//
        db.execSQL("DROP TABLE IF EXISTS person_ce");
        db.execSQL("CREATE TABLE person_ce (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "person_ce_id TEXT, person_id TEXT, vessel_id TEXT, " +
                "comments TEXT, checked_by_id TEXT, app_by_id TEXT," +
                "date_checked TEXT, date_app TEXT, checked_remarks TEXT, app_remarks TEXT )");
        db.execSQL("CREATE UNIQUE INDEX index_person_ce ON person_ce (id, person_ce_id, person_id, vessel_id)");

        //*** PERSON INSPECT ****//
        db.execSQL("DROP TABLE IF EXISTS person_inspect");
        db.execSQL("CREATE TABLE person_inspect(id INTEGER PRIMARY KEY , person_inspect_id TEXT, person_id TEXT, comments TEXT, checked_by_id TEXT, app_by_id TEXT, date_checked TEXT, date_app TEXT, checked_remarks TEXT, app_remarks TEXT, company_id TEXT, vessel_id TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_inspect ON person_inspect (id, person_inspect_id, person_id, checked_by_id)");

        //*** PERSON MATERIAL ****//
        db.execSQL("DROP TABLE IF EXISTS person_material");
        db.execSQL("CREATE TABLE person_material (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "person_material_id TEXT, person_id TEXT, " +
                "material TEXT, checked_by_id TEXT, app_by_id TEXT, date_checked TEXT, date_app TEXT," +
                "checked_remarks TEXT, app_remarks TEXT, date_material TEXT )");
        db.execSQL("CREATE UNIQUE INDEX index_person_material ON person_material (id, person_material_id, person_id, material)");

        //*** SAFETY ****//
        db.execSQL("DROP TABLE IF EXISTS safety");
        db.execSQL("CREATE TABLE safety (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "safety_id TEXT, desc_task TEXT, prio INTEGER, dept TEXT )");
        db.execSQL("CREATE UNIQUE INDEX index_safety ON safety (id, safety_id, desc_task)");

        //*** PERSON SAFETY ****//
        db.execSQL("DROP TABLE IF EXISTS person_safety");
        db.execSQL("CREATE TABLE person_safety (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "person_safety_id TEXT, person_id TEXT, safety_id TEXT, date_completed TEXT," +
                "ship_id TEXT, checked_by_id TEXT, app_by_id TEXT, date_checked TEXT, date_app TEXT," +
                "checked_remarks TEXT, app_remarks TEXT, na TEXT )");
        db.execSQL("CREATE UNIQUE INDEX index_person_safety ON person_safety (id, person_safety_id, person_id, safety_id)");

        db.execSQL("DROP TABLE IF EXISTS person_special_course");
        db.execSQL("CREATE TABLE person_special_course(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "person_special_course_id TEXT, person_id TEXT, course_title TEXT, date_started TEXT, " +
                "date_ended TEXT, location TEXT)");

        //*** PERSON OFFICER ****//
        db.execSQL("DROP TABLE IF EXISTS person_officer");
        db.execSQL("CREATE TABLE person_officer(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "person_officer_id TEXT, person_id TEXT, officer_id TEXT, from_date TEXT, " +
                "to_date TEXT, comp_officer_ok TEXT, vessel_id TEXT, last_update TEXT, assessor_id TEXT, master_id TEXT, chief_eng_id TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_officer ON person_officer (id, person_officer_id, person_id, officer_id, assessor_id, master_id)");

        /**PERSON TASK**/
        db.execSQL("DROP TABLE IF EXISTS person_task");
        db.execSQL("CREATE TABLE person_task (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "person_task_id TEXT, task_id TEXT, " +
                "person_id TEXT, completed TEXT, answers TEXT, passed TEXT, img_file TEXT," +
                "not_app TEXT, lat_long TEXT, vessel_type_id TEXT, checked_by_id TEXT, app_by_id TEXT," +
                "date_checked TEXT, date_app TEXT, officer_remarks TEXT, app_remarks TEXT, " +
                "vessel_id TEXT, assessed TEXT, answers2 TEXT, completed2 TEXT, passed2 TEXT, " +
                "additional_comment TEXT, for_app TEXT, activity_area TEXT, intial_cond TEXT," +
                "feedback TEXT, location TEXT, equipments TEXT, esig_file TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_task ON person_task (id, person_task_id, task_id, person_id, completed)");


        /**PERSON TASK FILE**/
        db.execSQL("DROP TABLE IF EXISTS person_task_file");
        db.execSQL("CREATE TABLE person_task_file (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "person_task_file_id TEXT, filename TEXT, " +
                "file_desc TEXT, uploaded TEXT, person_id TEXT," +
                "task_id TEXT, person_task_id TEXT, checked_by_id TEXT, app_by_id TEXT, " +
                "date_checked TEXT, date_app TEXT, checked_remarks TEXT, app_remarks TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_task_file ON person_task_file (id, person_task_file_id, person_id, filename)");

        /** PERSON EDUCATION**/
        db.execSQL("DROP TABLE IF EXISTS person_education_h");
        db.execSQL("CREATE TABLE person_education_h (id INTEGER PRIMARY KEY AUTOINCREMENT, person_education_h_id TEXT, " +
                "person_id TEXT, gce_level TEXT, school_name TEXT, school_address TEXT, certificate_date TEXT," +
                "login_id TEXT, last_update TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_education_h ON person_education_h (id, person_education_h_id, person_id, gce_level, school_name)");

        db.execSQL("DROP TABLE IF EXISTS person_education_d");
        db.execSQL("CREATE TABLE person_education_d (id INTEGER PRIMARY KEY AUTOINCREMENT, person_education_d_id TEXT, " +
                "person_education_h_id TEXT, subject TEXT, grade DOUBLE)");
        db.execSQL("CREATE UNIQUE INDEX index_person_education_d ON person_education_d (id, person_education_d_id, person_education_h_id, subject)");

        db.execSQL("CREATE TABLE country (id INTEGER PRIMARY KEY AUTOINCREMENT, country_id TEXT, name_country TEXT)");

        /**MUSTER**/
        db.execSQL("DROP TABLE IF EXISTS person_muster");
        db.execSQL("CREATE TABLE person_muster (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "person_muster_id TEXT, person_id TEXT, " +
                "vessel_id TEXT, lifeboat_station TEXT, lifeboat_duties TEXT," +
                "emergency_station TEXT, emergency_duties TEXT, oil_spill_duties TEXT, safety_officer_id TEXT, " +
                "security_officer_id TEXT, master_id TEXT, date_checked TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_muster ON person_muster (id, person_muster_id, person_id, vessel_id, lifeboat_station)");

        /*** REGULATION H ***/
        db.execSQL("DROP TABLE IF EXISTS regulation_h");
        db.execSQL("CREATE TABLE regulation_h( regulation_h_id TEXT, id INTEGER PRIMARY KEY AUTOINCREMENT, desc_regulation_h TEXT, prio INTEGER, login_id TEXT, last_update TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_regulation_h ON regulation_h (id, regulation_h_id, desc_regulation_h)");

        /*** REGULATION D ***/
        db.execSQL("DROP TABLE IF EXISTS regulation_d");
        db.execSQL("CREATE TABLE regulation_d( regulation_d_id TEXT, id INTEGER PRIMARY KEY AUTOINCREMENT, regulation_h_id TEXT, desc_regulation_d TEXT, prio INTEGER, contents TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_regulation_d ON regulation_d (id, regulation_d_id, desc_regulation_d)");
        /*** PERSON REGULATION ***/
        db.execSQL("DROP TABLE IF EXISTS person_regulation");
        db.execSQL("CREATE TABLE person_regulation( person_regulation_id TEXT, id INTEGER PRIMARY KEY AUTOINCREMENT, person_id TEXT, regulation_d_id TEXT, checked_by_id TEXT, app_by_id TEXT, date_checked TEXT, date_app TEXT, checked_remarks TEXT, app_remarks TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_regulation ON person_regulation (id, person_regulation_id, person_id, regulation_d_id)");

        /*** PERSON BRIDGE WATCH***/
        db.execSQL("DROP TABLE IF EXISTS person_bridge_watch");
        db.execSQL("CREATE TABLE person_bridge_watch( person_bridge_watch_id TEXT, id INTEGER PRIMARY KEY AUTOINCREMENT, person_id TEXT, vessel_id TEXT, " +
                "date_watchkeeping TEXT, from_time TEXT, to_time TEXT, voyage_number TEXT, voyage_desc TEXT, watch_type TEXT, " +
                "remarks TEXT, checked_by_id TEXT, app_by_id TEXT, date_checked TEXT, date_app TEXT, " +
                "checked_remarks TEXT, app_remarks TEXT, total_hrs DOUBLE)");
        db.execSQL("CREATE UNIQUE INDEX index_person_bridge_watch ON person_bridge_watch (id, person_bridge_watch_id, person_id, vessel_id, date_watchkeeping, from_time)");

        /*** PERSON STEERING ***/
        db.execSQL("DROP TABLE IF EXISTS person_steering");
        db.execSQL("CREATE TABLE person_steering( person_steering_id TEXT, id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "person_id TEXT, vessel_id TEXT, steering_type TEXT, voyage_from TEXT, voyage_to TEXT, " +
                "date_steering TEXT, steering_from TEXT, steering_to TEXT, total_hrs Double, remarks TEXT, " +
                "checked_by_id TEXT, app_by_id TEXT, date_checked TEXT, date_app TEXT, checked_remarks TEXT, " +
                "app_remarks TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_steering ON person_steering (id, person_steering_id, person_id, vessel_id, steering_type, date_steering)");

        /*** PERSON PORT WATCHES ***/
        db.execSQL("DROP TABLE IF EXISTS person_port_watch");
        db.execSQL("CREATE TABLE person_port_watch( person_port_watch_id TEXT, id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "person_id TEXT, vessel_id TEXT, date_watch TEXT, from_time TEXT, to_time TEXT, " +
                "voyage_number TEXT, port_name TEXT, desc_cargo TEXT, remarks TEXT, checked_by_id TEXT, " +
                "app_by_id TEXT, date_checked TEXT, date_app TEXT, checked_remarks TEXT, app_remarks TEXT, total_hrs DOUBLE)");
        db.execSQL("CREATE UNIQUE INDEX index_person_port_watch ON person_port_watch (id, person_port_watch_id, person_id, vessel_id, date_watch, from_time)");

        /*** ABBREVIATION ***/
        db.execSQL("DROP TABLE IF EXISTS abbreviation");
        db.execSQL("CREATE TABLE abbreviation( abbreviation_id TEXT, id INTEGER PRIMARY KEY AUTOINCREMENT, code_abbreviation TEXT, desc_abbreviation TEXT, login_id TEXT, last_update TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_abbreviation ON abbreviation (id, code_abbreviation)");

        /**GLOBAL SETTINGS**/
        db.execSQL("DROP TABLE IF EXISTS global_sys");
        db.execSQL("CREATE TABLE global_sys (id INTEGER PRIMARY KEY AUTOINCREMENT, global_sys_id TEXT, days_on_board INTEGER, submission_alert_pct INTEGER, submission_alert_days INTEGER, assessment_alert_pct INTEGER, assessment_alert_days INTEGER, company_alert_days INTEGER)");
        db.execSQL("DROP TABLE IF EXISTS backup_item");
        db.execSQL("CREATE TABLE backup_item (id INTEGER PRIMARY KEY AUTOINCREMENT, tbl TEXT, tbl_id TEXT, backup_date TEXT, backup_time TEXT, backup_event TEXT, backuped TEXT)");



        //--------------------------------------------------------//
        /*** PERSON JOURNAL ***/
        db.execSQL("DROP TABLE IF EXISTS person_journal");
        db.execSQL("CREATE TABLE person_journal(id INTEGER PRIMARY KEY , person_journal_id TEXT, journal_date TEXT, journal_time TEXT, person_id TEXT, vessel_id TEXT, ship_position_lat TEXT, fixing_method TEXT, course_speed TEXT, activities TEXT, fo_do TEXT, average_rpm TEXT, average_speed TEXT, checked_by_id TEXT, date_checked TEXT, login_id TEXT, last_update TEXT, journal_time_to TEXT, hrs TEXT, port_depart TEXT, port_dest TEXT, ship_position_long TEXT, ship_position_vicinity TEXT, fo_rob TEXT, fo_dob TEXT, fo_lob TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_journal ON person_journal (id, person_journal_id, person_id, vessel_id, journal_date, journal_time)");

        /* CLIENT */
        db.execSQL("DROP TABLE IF EXISTS client");
        db.execSQL("CREATE TABLE client (id INTEGER PRIMARY KEY AUTOINCREMENT, client_id TEXT, code_client TEXT, name_client TEXT, url TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_client ON client (id, client_id, url)");

        db.execSQL("DROP TABLE IF EXISTS safety_h");
        db.execSQL("CREATE TABLE safety_h(id INTEGER PRIMARY KEY , safety_h_id TEXT, desc_safety_h TEXT, table_header TEXT, prio TEXT, dept TEXT, login_id TEXT, last_update TEXT, remarks TEXT)");
        db.execSQL("DROP TABLE IF EXISTS safety_d");
        db.execSQL("CREATE TABLE safety_d(id INTEGER PRIMARY KEY , safety_d_id TEXT, safety_h_id TEXT, desc_safety_d TEXT, prio TEXT, safety_group TEXT)");

        db.execSQL("DROP TABLE IF EXISTS cat_task");
        db.execSQL("CREATE TABLE cat_task(id INTEGER PRIMARY KEY , cat_task_id TEXT, ref_no TEXT, desc_cat_task TEXT, login_id TEXT, last_update TEXT)");
        db.execSQL("DROP TABLE IF EXISTS direction");
        db.execSQL("CREATE TABLE direction(id INTEGER PRIMARY KEY , direction_id TEXT, code_direction TEXT, desc_direction TEXT, prio TEXT, login_id TEXT, last_update TEXT)");

        db.execSQL("DROP TABLE IF EXISTS project_work_h");
        db.execSQL("CREATE TABLE project_work_h(id INTEGER PRIMARY KEY , project_work_h_id TEXT, dept TEXT, competence_area TEXT, prio TEXT, login_id TEXT, last_update TEXT, trb_function_id TEXT)");

        db.execSQL("DROP TABLE IF EXISTS project_work_d");
        db.execSQL("CREATE TABLE project_work_d(id INTEGER PRIMARY KEY , project_work_d_id TEXT, project_work_h_id TEXT, details TEXT, prio TEXT, login_id TEXT, last_update TEXT)");

        db.execSQL("DROP TABLE IF EXISTS alarm_signal");
        db.execSQL("CREATE TABLE alarm_signal(id INTEGER PRIMARY KEY , alarm_signal_id TEXT, desc_signal TEXT, dept TEXT, prio TEXT, login_id TEXT, last_update TEXT)");

        db.execSQL("DROP TABLE IF EXISTS person_work_practice");
        db.execSQL("CREATE TABLE person_work_practice(id INTEGER PRIMARY KEY , person_work_practice_id TEXT, person_id TEXT, vessel_id TEXT, work_practice_id TEXT, checked_by_id TEXT, date_checked TEXT, checked_remarks TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_work_practice ON person_work_practice (id, person_work_practice_id, person_id, vessel_id, work_practice_id)");

        db.execSQL("DROP TABLE IF EXISTS work_practice");
        db.execSQL("CREATE TABLE work_practice(id INTEGER PRIMARY KEY , work_practice_id TEXT, desc_work_practice TEXT, dept TEXT, prio TEXT, login_id TEXT, last_update TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_work_practice ON work_practice (id, work_practice_id, desc_work_practice, dept)");

        db.execSQL("DROP TABLE IF EXISTS person_journal_engine");
        db.execSQL("CREATE TABLE person_journal_engine(id INTEGER PRIMARY KEY , person_journal_engine_id TEXT, journal_date TEXT, journal_time TEXT, person_id TEXT, vessel_id TEXT, ship_position_lat TEXT, fixing_method TEXT, course_speed TEXT, activities TEXT, fo_cons TEXT, average_rpm TEXT, average_speed TEXT, checked_by_id TEXT, date_checked TEXT, login_id TEXT, last_update TEXT, journal_time_to TEXT, hrs TEXT, port_depart TEXT, port_dest TEXT, ship_position_long TEXT, ship_position_vicinity TEXT, do_cons TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_journal_engine ON person_journal_engine (id, person_journal_engine_id, journal_date, journal_time)");

        db.execSQL("DROP TABLE IF EXISTS person_crew_list");
        db.execSQL("CREATE TABLE person_crew_list(id INTEGER PRIMARY KEY , person_crew_list_id TEXT, person_id TEXT, vessel_id TEXT, date_uploaded TEXT, filename TEXT, checked_by_id TEXT, date_checked TEXT, checked_remarks TEXT, login_id TEXT, last_update TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_crew_list ON person_crew_list (id, person_crew_list_id, person_id, vessel_id)");

        db.execSQL("DROP TABLE IF EXISTS person_emergency_alarm");
        db.execSQL("CREATE TABLE person_emergency_alarm(id INTEGER PRIMARY KEY , person_emergency_alarm_id TEXT, person_id TEXT, vessel_id TEXT, alarm_signal_id TEXT, checked_by_id TEXT, date_checked TEXT, checked_remarks TEXT, na TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_emergency_alarm ON person_emergency_alarm (id, person_emergency_alarm_id, person_id, vessel_id)");

        db.execSQL("DROP TABLE IF EXISTS person_emergency_drill");
        db.execSQL("CREATE TABLE person_emergency_drill(id INTEGER PRIMARY KEY , person_emergency_drill_id TEXT, person_id TEXT, vessel_id TEXT, date_familiarization TEXT, details TEXT, filename TEXT, checked_by_id TEXT, date_checked TEXT, checked_remarks TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_emergency_drill ON person_emergency_alarm (id, person_emergency_alarm_id, person_id, vessel_id)");

        db.execSQL("DROP TABLE IF EXISTS person_emergency_muster");
        db.execSQL("CREATE TABLE person_emergency_muster(id INTEGER PRIMARY KEY , person_emergency_muster_id TEXT, person_id TEXT, vessel_id TEXT, date_familiarization TEXT, details TEXT, filename TEXT, checked_by_id TEXT, date_checked TEXT, checked_remarks TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_emergency_muster ON person_emergency_muster (id, person_emergency_muster_id, person_id, vessel_id)");

        db.execSQL("DROP TABLE IF EXISTS person_project_work");
        db.execSQL("CREATE TABLE person_project_work(id INTEGER PRIMARY KEY , person_project_work_id TEXT, person_id TEXT, project_work_d_id TEXT, date_completed TEXT, vessel_id TEXT, details TEXT, eval TEXT, checked_by_id TEXT, date_checked TEXT, checked_remarks TEXT, login_id TEXT, last_update TEXT, for_app TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_project_work ON person_project_work (id, person_project_work_id, person_id, vessel_id)");

        db.execSQL("DROP TABLE IF EXISTS person_project_work_file");
        db.execSQL("CREATE TABLE person_project_work_file(id INTEGER PRIMARY KEY , person_project_work_file_id TEXT, person_project_work_id TEXT, filename TEXT, file_desc TEXT, prio TEXT)");
        db.execSQL("CREATE UNIQUE INDEX index_person_project_work_file ON person_project_work_file (id, person_project_work_file_id, person_project_work_id, filename)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    //GET COUNT
    public int GetCount(String table, String where){
        Integer cnt = 0;
        String selectQuery =  "SELECT COUNT(*) FROM " + table + " " + where;
        Log.d("QUERY", selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        cnt = cursor.getInt(0);
        cursor.close();

        return cnt;
    }
    //EXECUTE QUERY
    public void execQuery(String query){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            // Log.d("QUERY : ", query);
            db.execSQL( query );

        }catch (SQLiteException e) {
            if (!e.getMessage().startsWith("duplicate column name:")) {
                throw e;
            }
        }
    }

    //***START OF PERSON TABLE ****//
    public void addPerson(Integer id, String person_id, String code_person, String lname, String fname, String mname, String st_address, String city_id, String province_id, String phone, String mobile, String st_address_province, String phone_province, String email, String gender, String civ_status, String birth_date, String birth_place, String spouse_name, Integer children, String father_name, String mother_name, String notes, String active, String date_reg, String compman_id, String company_id, Double amt_paid, String rank_id, String vessel_officer, String dept, String vessel_id, String course_id, String school_id, String school_admin, String type, Integer batch_no, Double pct_done, Integer days_on_board, String photo_file, String passport_no, String cdc_no, String indos_no, String height, String weight, String enrtry_date, String marks, String blood_group, String emergency_contact_name, String emergency_contact_address, String emergency_contact_no, String emergency_relationship, String nationality, String passport_no_issue_date, String cdc_no_issue_date, String cdc_no_issue_place, String full_name, String logged_in, String w_fr, String emergency_email, String sponsor_id, String dig_signature, String srn, String srb_no, String srb_date, String sid_no, String login_name, String login_pass, String login_type_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("code_person", code_person);
        values.put("lname", lname);
        values.put("fname", fname);
        values.put("mname", mname);
        values.put("st_address", st_address);
        values.put("city_id", city_id);
        values.put("province_id", province_id);
        values.put("phone", phone);
        values.put("mobile", mobile);
        values.put("st_address_province", st_address_province);
        values.put("phone_province", phone_province);
        values.put("email", email);
        values.put("gender", gender);
        values.put("civ_status", civ_status);
        values.put("birth_date", birth_date);
        values.put("birth_place", birth_place);
        values.put("spouse_name", spouse_name);
        values.put("children", children);
        values.put("father_name", father_name);
        values.put("mother_name", mother_name);
        values.put("notes", notes);
        values.put("active", active);
        values.put("date_reg", date_reg);
        values.put("compman_id", compman_id);
        values.put("company_id", company_id);
        values.put("amt_paid", amt_paid);
        values.put("rank_id", rank_id);
        values.put("vessel_officer", vessel_officer);
        values.put("dept", dept);
        values.put("vessel_id", vessel_id);
        values.put("course_id", course_id);
        values.put("school_id", school_id);
        values.put("school_admin", school_admin);
        values.put("type", type);
        values.put("batch_no", batch_no);
        values.put("pct_done", pct_done);
        values.put("days_on_board", days_on_board);
        values.put("photo_file", photo_file);
        values.put("passport_no", passport_no);
        values.put("cdc_no", cdc_no);
        values.put("indos_no", indos_no);
        values.put("height", height);
        values.put("weight", weight);
        values.put("enrtry_date", enrtry_date);
        values.put("marks", marks);
        values.put("blood_group", blood_group);
        values.put("emergency_contact_name", emergency_contact_name);
        values.put("emergency_contact_address", emergency_contact_address);
        values.put("emergency_contact_no", emergency_contact_no);
        values.put("emergency_relationship", emergency_relationship);
        values.put("nationality", nationality);
        values.put("passport_no_issue_date", passport_no_issue_date);
        values.put("cdc_no_issue_date", cdc_no_issue_date);
        values.put("cdc_no_issue_place", cdc_no_issue_place);
        values.put("full_name", full_name);
        values.put("logged_in", logged_in);
        values.put("w_fr", w_fr);
        values.put("emergency_email", emergency_email);
        values.put("sponsor_id", sponsor_id);
        values.put("dig_signature", dig_signature);
        values.put("srn", srn);
        values.put("srb_no", srb_no);
        values.put("srb_date", srb_date);
        values.put("sid_no",  sid_no);
        values.put("login_name",  login_name);
        values.put("login_pass",  login_pass);
        values.put("login_type_id",  login_type_id);

        // Inserting Row
        //db.insert("person", null, getFieldString);
        try {
            db.insert("person", null, values);
            Log.d("RESULT", "INSERT PERSON " + lname);
        } catch (SQLException e) {
            Log.e("ERROR PERSON INS : ", "" + e);
        }

        db.close(); // Closing database connection
    }


    public void addLogin(String login_id, Integer id, String email, String login_name, String login_pass, String session_id, String login_type_id, String initial, String lname, String fname, String mname, String company_id, String designation){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("login_id", login_id);
        values.put("id", id);
        values.put("email", email);
        values.put("login_name", login_name);
        values.put("login_pass", login_pass);
        values.put("session_id", session_id);
        values.put("login_type_id", login_type_id);
        values.put("initial", initial);
        values.put("lname", lname);
        values.put("fname", fname);
        values.put("mname", mname);
        values.put("company_id", company_id);
        values.put("designation", designation);


        try {
            db.insert("login", null, values);
        } catch (SQLException e) {
            Log.e("ERROR login INS : ", "" + e);}

        db.close();
    }

    //TRB FUNCTION
    public void addTrbFunction(Integer id, String trb_function_id, String desc_trb_function, String dept, Integer prio, String vessel_type_id, String dp, String ice_class, String spec_to){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("trb_function_id", trb_function_id);
        values.put("desc_trb_function", desc_trb_function);
        values.put("dept", dept);
        values.put("prio", prio);
        values.put("vessel_type_id", vessel_type_id);
        values.put("dp", dp);
        values.put("ice_class", ice_class);
        values.put("spec_to", spec_to);

        try {
            db.insert("trb_function", null, values);
        } catch (SQLException e) {
            Log.e("ERROR TRF INS : ", "" + e);}

        db.close();
    }
    //GET FUNCTIONS
    public List<TrbFunction> getFunctions(String dept, String where) {
        List<TrbFunction> list = new ArrayList<TrbFunction>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(where.equals("")){
            selectQuery = "SELECT  * FROM trb_function WHERE dept = '"+dept+"' ORDER BY prio";
        }else{
            selectQuery = "SELECT  * FROM trb_function WHERE dept = '"+dept+"' AND "+ where +" ORDER BY prio";
        }
        Log.d("RESULT",  "SELECT FUNCTION "+ selectQuery);


        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                TrbFunction trbFunction = new TrbFunction();
                trbFunction.setId(cursor.getInt(0));
                trbFunction.setTrb_function_id(cursor.getString(1));
                trbFunction.setDesc_trb_function(cursor.getString(2));
                trbFunction.setDept(cursor.getString(3));
                trbFunction.setPrio(cursor.getInt(4));
                trbFunction.setVessel_type_id(cursor.getString(5));
                trbFunction.setDp(cursor.getString(6));
                trbFunction.setIce_class(cursor.getString(7));
                trbFunction.setSpec_to(cursor.getString(8));
                trbFunction.setEngine_type_id(cursor.getString(9));
                trbFunction.setRef_no_function(cursor.getString(10));
                // Add to list
                list.add(trbFunction);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //PERSON JOURNAL
    public void addPersonJournal(Integer id, String person_journal_id, String journal_date, String journal_time, String person_id, String vessel_id, String ship_position, String fixing_method, String course_speed, String activities, String fo_do, String average_rpm, String average_speed, String checked_by_id, String date_checked, String login_id, String last_update){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("person_journal_id", person_journal_id);
        values.put("journal_date", journal_date);
        values.put("journal_time", journal_time);
        values.put("person_id", person_id);
        values.put("vessel_id", vessel_id);
        values.put("ship_position", ship_position);
        values.put("fixing_method", fixing_method);
        values.put("course_speed", course_speed);
        values.put("activities", activities);
        values.put("fo_do", fo_do);
        values.put("average_rpm", average_rpm);
        values.put("average_speed", average_speed);
        values.put("checked_by_id", checked_by_id);
        values.put("date_checked", date_checked);
        values.put("login_id", login_id);
        values.put("last_update", last_update);

        try {
            db.insert("person_journal", null, values);
        } catch (SQLException e) {
            Log.e("ERROR PJ INS : ", "" + e);}

        db.close();
    }

    //GET JOURNALS
    public List<PersonJournal> getPersonJournal(String person_id) {
        List<PersonJournal> list = new ArrayList<PersonJournal>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_journal WHERE person_id ='"+person_id+"' ORDER BY journal_date, journal_time DESC";

        Log.d("RESULT", selectQuery);


        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonJournal personJournal = new PersonJournal();
                personJournal.setPerson_journal_id(cursor.getString(0));
                personJournal.setId(cursor.getInt(1));
                personJournal.setJournal_date(cursor.getString(2));
                personJournal.setJournal_time(cursor.getString(3));
                personJournal.setPerson_id(cursor.getString(4));
                personJournal.setVessel_id(cursor.getString(5));
                personJournal.setShip_position_lat(cursor.getString(6));
                personJournal.setFixing_method(cursor.getString(7));
                personJournal.setCourse_speed(cursor.getString(8));
                personJournal.setActivities(cursor.getString(9));
                personJournal.setFo_do(cursor.getString(10));
                personJournal.setAverage_rpm(cursor.getString(11));
                personJournal.setAverage_speed(cursor.getString(12));
                personJournal.setChecked_by_id(cursor.getString(13));
                personJournal.setDate_checked(cursor.getString(14));
                personJournal.setLogin_id(cursor.getString(15));
                personJournal.setLast_update(cursor.getString(16));
                personJournal.setJournal_time_to(cursor.getString(17));
                personJournal.setHrs(cursor.getString(18));
                personJournal.setPort_depart(cursor.getString(19));
                personJournal.setPort_dest(cursor.getString(20));
                personJournal.setShip_position_long(cursor.getString(21));
                personJournal.setShip_position_vicinity(cursor.getString(22));
                personJournal.setFo_rob(cursor.getString(23));
                personJournal.setFo_dob(cursor.getString(24));
                personJournal.setFo_lob(cursor.getString(25));

                // Add to list
                list.add(personJournal);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //GET JOURNALS ENGINE
    public List<PersonJournalEngine> getPersonJournalEngine(String person_id) {
        List<PersonJournalEngine> list = new ArrayList<PersonJournalEngine>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_journal_engine WHERE person_id ='"+person_id+"' ORDER BY journal_date, journal_time DESC";

        Log.d("RESULT", selectQuery);


        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonJournalEngine list2 = new PersonJournalEngine();
                list2.setId(cursor.getInt(0));
                list2.setPerson_journal_engine_id(cursor.getString(1));
                list2.setJournal_date(cursor.getString(2));
                list2.setJournal_time(cursor.getString(3));
                list2.setPerson_id(cursor.getString(4));
                list2.setVessel_id(cursor.getString(5));
                list2.setShip_position_lat(cursor.getString(6));
                list2.setFixing_method(cursor.getString(7));
                list2.setCourse_speed(cursor.getString(8));
                list2.setActivities(cursor.getString(9));
                list2.setFo_cons(cursor.getString(10));
                list2.setAverage_rpm(cursor.getString(11));
                list2.setAverage_speed(cursor.getString(12));
                list2.setChecked_by_id(cursor.getString(13));
                list2.setDate_checked(cursor.getString(14));
                list2.setLogin_id(cursor.getString(15));
                list2.setLast_update(cursor.getString(16));
                list2.setJournal_time_to(cursor.getString(17));
                list2.setHrs(cursor.getString(18));
                list2.setPort_depart(cursor.getString(19));
                list2.setPort_dest(cursor.getString(20));
                list2.setShip_position_long(cursor.getString(21));
                list2.setShip_position_vicinity(cursor.getString(22));
                list2.setDo_cons(cursor.getString(23));

                // Add to list
                list.add(list2);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //TRB COMPETENCE
    public void addTrbCompetence(Integer id, String trb_competence_id, String ref_no, String desc_competence, String trb_function_id, Integer prio){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", id);
        values.put("trb_competence_id", trb_competence_id);
        values.put("ref_no", ref_no);
        values.put("desc_competence", desc_competence);
        values.put("trb_function_id", trb_function_id);
        values.put("prio", prio);


        try {
            db.insert("trb_competence", null, values);
        } catch (SQLException e) {
            Log.e("ERROR TRC INS : ", "" + e);}

        db.close();

    }

    //GET FUNCTIONS
    public List<TrbCompetence> getCompetences(String trb_function_id) {
        List<TrbCompetence> list = new ArrayList<TrbCompetence>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM trb_competence WHERE trb_function_id = '"+trb_function_id+"' ORDER BY prio";
        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                TrbCompetence trbCompetence = new TrbCompetence();
                trbCompetence.setId(cursor.getInt(0));
                trbCompetence.setTrb_competence_id(cursor.getString(1));
                trbCompetence.setRef_no(cursor.getString(2));
                trbCompetence.setDesc_competence(cursor.getString(3));
                trbCompetence.setTrb_function_id(cursor.getString(4));
                trbCompetence.setPrio(cursor.getInt(5));
                trbCompetence.setCriteria(cursor.getString(6));
                trbCompetence.setInstruction(cursor.getString(7));
                // Add to list
                list.add(trbCompetence);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //TRB TOPIC
    public void addTrbTopic(String trb_topic_id, Integer id, String ref_no_topic, String desc_topic, String trb_competence_id, String criteria, Integer prio){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trb_topic_id", trb_topic_id);
        values.put("id", id);
        values.put("ref_no_topic", ref_no_topic);
        values.put("desc_topic", desc_topic);
        values.put("trb_competence_id", trb_competence_id);
        values.put("criteria", criteria);
        values.put("prio", prio);


        try {
            db.insert("trb_topic", null, values);
        } catch (SQLException e) {
            Log.e("ERROR trb_topic INS : ", "" + e);}

        db.close();
    }

    //GET TOPIC
    public List<TrbTopic> getTopics(String trb_competence_id) {
        List<TrbTopic> list = new ArrayList<TrbTopic>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM trb_topic WHERE trb_competence_id = '"+trb_competence_id+"' ORDER BY prio";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                TrbTopic trbTopic = new TrbTopic();
                trbTopic.setId(cursor.getInt(0));
                trbTopic.setTrb_topic_id(cursor.getString(1));
                trbTopic.setRef_no_topic(cursor.getString(2));
                trbTopic.setDesc_topic(cursor.getString(3));
                trbTopic.setTrb_competence_id(cursor.getString(4));
                trbTopic.setCriteria(cursor.getString(5));
                trbTopic.setPrio(cursor.getInt(6));

                // Add to list
                list.add(trbTopic);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    public List<TrbSubCompetence> getTrbSubComp(String trb_competence_id) {
        List<TrbSubCompetence> list = new ArrayList<TrbSubCompetence>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM trb_sub_competence WHERE trb_competence_id = '"+trb_competence_id+"' ORDER BY ref_no";
        Log.d("RESULT",selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                TrbSubCompetence trbSubCompetence = new TrbSubCompetence();
                trbSubCompetence.setId(cursor.getInt(0));
                trbSubCompetence.setTrb_sub_competence_id(cursor.getString(1));
                trbSubCompetence.setRef_no(cursor.getString(2));
                trbSubCompetence.setDesc_sub_competence(cursor.getString(3));
                trbSubCompetence.setTrb_competence_id(cursor.getString(4));
                trbSubCompetence.setCriteria_id(cursor.getString(5));

                // Add to list
                list.add(trbSubCompetence);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    public List<TrbTopic> getTrbTopics(String trb_competence_id) {
        List<TrbTopic> list = new ArrayList<TrbTopic>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM trb_topic WHERE trb_competence_id = '"+trb_competence_id+"' ORDER BY prio";
        Log.d("RESULT",selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                TrbTopic list2 = new TrbTopic();
                list2.setId(cursor.getInt(0));
                list2.setTrb_topic_id(cursor.getString(1));
                list2.setRef_no_topic(cursor.getString(2));
                list2.setDesc_topic(cursor.getString(3));
                list2.setTrb_competence_id(cursor.getString(4));
                list2.setCriteria(cursor.getString(5));
                list2.setPrio(cursor.getInt(6));
                list2.setTrb_function_id(cursor.getString(7));

                // Add to list
                list.add(list2);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //TASK
    public void addTask(String task_id, Integer id, String ref_no, String desc_task, String trb_competence_id, String trb_topic_id, Integer prio, Integer phase_no){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("task_id", task_id);
        values.put("id", id);
        values.put("ref_no", ref_no);
        values.put("desc_task", desc_task);
        values.put("trb_competence_id", trb_competence_id);
        values.put("trb_topic_id", trb_topic_id);
        values.put("prio", prio);
        values.put("phase_no", phase_no);


        try {
            db.insert("task", null, values);
        } catch (SQLException e) {
            Log.e("ERROR task INS : ", "" + e);}

        db.close();
    }

    //GET TOPIC
    public List<Task> getTasks(String trb_competence_id, String trb_topic_id) {
        List<Task> list = new ArrayList<Task>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(trb_topic_id.equals("") && trb_competence_id.equals("")){
            selectQuery = "SELECT  * FROM task ORDER BY prio";
        }else if(!trb_competence_id.equals("") && trb_topic_id.equals("")){
            selectQuery = "SELECT  * FROM task WHERE trb_competence_id = '"+trb_competence_id+"' ORDER BY prio";
        }else if(trb_competence_id.equals("") && !trb_topic_id.equals("")){
            selectQuery = "SELECT  * FROM task WHERE trb_topic_id = '"+trb_topic_id+"' ORDER BY prio";
        }else{
            selectQuery = "SELECT  * FROM task WHERE trb_competence_id = '"+trb_competence_id+"' AND trb_topic_id = '"+trb_topic_id+"' ORDER BY prio";
        }
        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getInt(0));
                task.setTask_id(cursor.getString(1));
                task.setRef_no_task(cursor.getString(2));
                task.setDesc_task(cursor.getString(3));
                task.setTrb_competence_id(cursor.getString(4));
                task.setTrb_topic_id(cursor.getString(5));
                task.setPrio(cursor.getInt(6));
                task.setPhase_no(cursor.getInt(7));
                task.setCat_task_id(cursor.getString(8));
                task.setLearning_activity(cursor.getString(9));
                task.setDirection_id(cursor.getString(10));
                task.setCriteria(cursor.getString(11));
                task.setTrb_function_id(cursor.getString(12));
                task.setTrb_task_group_id(cursor.getString(13));
                // Add to list
                list.add(task);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    public List<TrbTaskGroup> getTrbTaskGroup(String trb_competence_id, String trb_topic_id) {
        List<TrbTaskGroup> list = new ArrayList<TrbTaskGroup>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(!trb_topic_id.equals("")){
            selectQuery = "SELECT  * FROM trb_task_group WHERE trb_topic_id = '"+trb_topic_id+"' ORDER BY prio";
        }else if(!trb_competence_id.equals("")){
            selectQuery = "SELECT  * FROM trb_task_group WHERE trb_competence_id = '"+trb_competence_id+"' ORDER BY prio";
        }else {
            selectQuery = "SELECT  * FROM trb_task_group ORDER BY prio";
        }

        Log.d("RESULT",selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                TrbTaskGroup trbTaskGroup = new TrbTaskGroup();
                trbTaskGroup.setId(cursor.getInt(0));
                trbTaskGroup.setTrb_task_group_id(cursor.getString(1));
                trbTaskGroup.setTrb_topic_id(cursor.getString(2));
                trbTaskGroup.setTrb_competence_id(cursor.getString(3));
                trbTaskGroup.setTrb_function_id(cursor.getString(4));
                trbTaskGroup.setRef_no_task_group(cursor.getString(5));
                trbTaskGroup.setDesc_task_group(cursor.getString(6));
                trbTaskGroup.setCond_task_group(cursor.getString(7));
                trbTaskGroup.setPrio(cursor.getInt(8));
                // Add to list
                list.add(trbTaskGroup);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //BASIC TRAINING
    public void addBasicTraining(String basic_training_id, Integer id, String desc_basic_training, String dept, Integer prio, String training_type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("basic_training_id", basic_training_id);
        values.put("id", id);
        values.put("desc_basic_training", desc_basic_training);
        values.put("dept", dept);
        values.put("prio", prio);
        values.put("training_type", training_type);

        try {
            db.insert("basic_training", null, values);
        } catch (SQLException e) {
            Log.e("ERROR ", "basic_training INS" + e);
        }

        db.close();
    }

    //GET BASIC TRAINING
    public List<BasicTraining> getBasicTrainings(String dept, String where) {
        List<BasicTraining> list = new ArrayList<BasicTraining>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(dept.equals("") && where.equals("")){
            selectQuery  = "SELECT  * FROM basic_training ORDER BY prio";
        }else if(dept.equals("") && !where.equals("")){
            selectQuery  = "SELECT  * FROM basic_training WHERE "+where+" ORDER BY prio";
        }else{
            selectQuery  = "SELECT  * FROM basic_training WHERE dept = '' OR dept = '"+dept+"' ORDER BY prio";
        }


        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                BasicTraining basicTraining = new BasicTraining();
                basicTraining.setId(cursor.getInt(0));
                basicTraining.setBasic_training_id(cursor.getString(1));
                basicTraining.setDesc_basic_training(cursor.getString(2));
                basicTraining.setDept(cursor.getString(3));
                basicTraining.setPrio(cursor.getInt(4));
                basicTraining.setTraining_type(cursor.getString(5));

                // Add to list
                list.add(basicTraining);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //PERSON BASIC TRAINING
    public void addPersonBasicTraining(String person_basic_training_id, Integer id, String person_id, String basic_training_id, String location_name, String date_completed, String doc_ref_no, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks, String institution){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("person_basic_training_id", person_basic_training_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("basic_training_id", basic_training_id);
        values.put("location_name", location_name);
        values.put("date_completed", date_completed);
        values.put("doc_ref_no", doc_ref_no);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("checked_remarks", checked_remarks);
        values.put("app_remarks", app_remarks);
        values.put("institution", institution);

        try {
            db.insert("person_basic_training", null, values);
        } catch (SQLException e) {
            Log.e("ERROR ", "person_basic_training INS" + e);
        }

        db.close();
    }

    //ADD SHIPBOARD
    public void addShipboard(String shipboard_id, Integer id, String sign_on, String sign_off, Integer engine_watch_mos, Integer engine_watch_yrs, Integer voyage_mos, String person_id, String vessel_id, Integer voyage_days, String imo_number){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("shipboard_id", shipboard_id);
        values.put("id", id);
        values.put("sign_on", sign_on);
        values.put("sign_off", sign_off);
        values.put("engine_watch_mos", engine_watch_mos);
        values.put("engine_watch_yrs", engine_watch_yrs);
        values.put("voyage_mos", voyage_mos);
        values.put("person_id", person_id);
        values.put("vessel_id", vessel_id);
        values.put("voyage_days", voyage_days);
        values.put("imo_number", imo_number);


        try {
            db.insert("shipboard", null, values);
        } catch (SQLException e) {
            Log.e("ERROR shipboard INS : ", "" + e);}


        db.close();
    }

    //GET SHIPBOARD
    public List<Shipboard> getShipboard(String person_id) {
        List<Shipboard> list = new ArrayList<Shipboard>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM shipboard " +
                "WHERE person_id = '"+person_id+"' ORDER BY sign_on";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Shipboard shipboard = new Shipboard();
                shipboard.setId(cursor.getInt(0));
                shipboard.setShipboard_id(cursor.getString(1));
                shipboard.setSign_on(cursor.getString(2));
                shipboard.setSign_off(cursor.getString(3));
                shipboard.setEngine_watch_mos(cursor.getString(4));
                shipboard.setEngine_watch_yrs(cursor.getString(5));
                shipboard.setVoyage_mos(cursor.getString(6));
                shipboard.setPerson_id(cursor.getString(7));
                shipboard.setVessel_id(cursor.getString(8));
                shipboard.setVoyage_days(cursor.getString(9));
                shipboard.setImo_number(cursor.getString(10));
                shipboard.setSrb_file(cursor.getString(11));
                shipboard.setCert_file(cursor.getString(12));
                shipboard.setChecked_by_id(cursor.getString(13));
                shipboard.setDate_checked(cursor.getString(14));
                shipboard.setContract_file(cursor.getString(15));

                // Add to list
                list.add(shipboard);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //GET BASIC TRAINING
    public List<PersonBasicTraining> getPersonBasicTrainings(String person_id) {
        List<PersonBasicTraining> list = new ArrayList<PersonBasicTraining>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_basic_training " +
                "WHERE person_id = '"+person_id+"' ORDER BY date_completed";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonBasicTraining personBasicTraining = new PersonBasicTraining();
                personBasicTraining.setId(cursor.getInt(0));
                personBasicTraining.setPerson_basic_training_id(cursor.getString(1));
                personBasicTraining.setPerson_id(cursor.getString(2));
                personBasicTraining.setBasic_training_id(cursor.getString(3));
                personBasicTraining.setLocation_name(cursor.getString(4));
                personBasicTraining.setDate_completed(cursor.getString(5));
                personBasicTraining.setDoc_ref_no(cursor.getString(6));
                personBasicTraining.setChecked_by_id(cursor.getString(7));
                personBasicTraining.setApp_by_id(cursor.getString(8));
                personBasicTraining.setDate_checked(cursor.getString(9));
                personBasicTraining.setDate_app(cursor.getString(10));
                personBasicTraining.setChecked_remarks(cursor.getString(11));
                personBasicTraining.setApp_remarks(cursor.getString(12));
                personBasicTraining.setInstitution(cursor.getString(13));

                // Add to list
                list.add(personBasicTraining);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //ADD VESSELTYPE
    public void addVesselType(String vessel_type_id, Integer id, String desc_vessel_type){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("vessel_type_id", vessel_type_id);
        values.put("id", id);
        values.put("desc_vessel_type", desc_vessel_type);


        try {
            db.insert("vessel_type", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "vessel_type INS " + e);}

        db.close();
    }

    //ADD VESSEL
    public void addVessel(String vessel_id, Integer id, String name_vessel, String owner_company_id, String operator_company_id, String year_built, String flag_registry_id, String hp, String kw, String grt, String trade_type, String imo_number, String call_sign, String vessel_type_id, String dp, String ice_class, String motor, String st, String gt){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("vessel_id", vessel_id);
        values.put("id", id);
        values.put("name_vessel", name_vessel);
        values.put("owner_company_id", owner_company_id);
        values.put("operator_company_id", operator_company_id);
        values.put("year_built", year_built);
        values.put("flag_registry_id", flag_registry_id);
        values.put("hp", hp);
        values.put("kw", kw);
        values.put("grt", grt);
        values.put("trade_type", trade_type);
        values.put("imo_number", imo_number);
        values.put("call_sign", call_sign);
        values.put("vessel_type_id", vessel_type_id);
        values.put("dp", dp);
        values.put("ice_class", ice_class);
        values.put("motor", motor);
        values.put("st", st);
        values.put("gt", gt);

        try {
            db.insert("vessel", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "vessel INS " + e);}

        db.close();
    }

    //GET VESSEL
    public List<Vessel> getVessels() {
        List<Vessel> list = new ArrayList<Vessel>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM vessel ORDER BY name_vessel";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Vessel vessel = new Vessel();
                vessel.setId(cursor.getInt(0));
                vessel.setVessel_id(cursor.getString(1));
                vessel.setName_vessel(cursor.getString(2));
                vessel.setOwner_company_id(cursor.getString(3));
                vessel.setOperator_company_id(cursor.getString(4));
                vessel.setYear_built(cursor.getInt(5));
                vessel.setFlag_registry_id(cursor.getString(6));
                vessel.setHp(cursor.getString(7));
                vessel.setKw(cursor.getString(8));
                vessel.setGrt(cursor.getString(9));
                vessel.setTrade_type(cursor.getString(10));
                vessel.setImo_number(cursor.getString(11));
                vessel.setCall_sign(cursor.getString(12));
                vessel.setVessel_type_id(cursor.getString(13));
                vessel.setDp(cursor.getString(14));
                vessel.setIce_class(cursor.getString(15));
                vessel.setMotor(cursor.getString(16));
                vessel.setSt(cursor.getString(17));
                vessel.setGt(cursor.getString(18));

                // Add to list
                list.add(vessel);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //ADD PERSON TASK
    public void addPersonTask(String person_task_id, Integer id, String task_id, String person_id, String completed, String answers, String passed, String img_file, String not_app, String lat_long, String vessel_type_id, String checked_by_id, String app_by_id, String date_checked, String date_app, String officer_remarks, String app_remarks, String vessel_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("person_task_id", person_task_id);
        values.put("id", id);
        values.put("task_id", task_id);
        values.put("person_id", person_id);
        values.put("completed", completed);
        values.put("answers", answers);
        values.put("passed", passed);
        values.put("img_file", img_file);
        values.put("not_app", not_app);
        values.put("lat_long", lat_long);
        values.put("vessel_type_id", vessel_type_id);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("officer_remarks", officer_remarks);
        values.put("app_remarks", app_remarks);
        values.put("vessel_id", vessel_id);


        try {
            db.insert("person_task", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_task INS " + e);}

        db.close();
    }

    //GET PERSON TASK
    public List<PersonTask> getPersonTask(String trb_competence_id, String trb_topic_id, String trb_task_group_id) {
        List<PersonTask> list = new ArrayList<PersonTask>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = null;

        if(trb_competence_id.equals("") && trb_topic_id.equals("") && trb_task_group_id.equals("")){
            selectQuery = "SELECT * FROM person_task ";
        }else if(trb_competence_id.equals("") && trb_topic_id.equals("") && !trb_task_group_id.equals("")){
            selectQuery = "SELECT person_task.*, task.desc_task, task.prio " +
                    "FROM person_task " +
                    "LEFT JOIN task ON person_task.task_id = task.task_id " +
                    "WHERE task.trb_task_group_id = '"+trb_task_group_id+"'" +
                    " ORDER BY prio";
        }else{
            selectQuery = "SELECT person_task.*, task.desc_task, task.prio " +
                    "FROM person_task " +
                    "LEFT JOIN task ON person_task.task_id = task.task_id " +
                    "WHERE task.trb_competence_id = '"+trb_competence_id+"' " +
                    "AND trb_topic_id = '"+trb_topic_id+"'" +
                    " ORDER BY prio";
        }

        Log.d("RESULT", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonTask personTask = new PersonTask();
                personTask.setId(cursor.getInt(0));
                personTask.setPerson_task_id(cursor.getString(1));
                personTask.setTask_id(cursor.getString(2));
                personTask.setPerson_id(cursor.getString(3));
                personTask.setCompleted(cursor.getString(4));
                personTask.setAnswers(cursor.getString(5));
                personTask.setPassed(cursor.getString(6));
                personTask.setImg_file(cursor.getString(7));
                personTask.setNot_app(cursor.getString(8));
                personTask.setLat_long(cursor.getString(9));
                personTask.setVessel_type_id(cursor.getString(10));
                personTask.setChecked_by_id(cursor.getString(11));
                personTask.setApp_by_id(cursor.getString(12));
                personTask.setDate_checked(cursor.getString(13));
                personTask.setDate_app(cursor.getString(14));
                personTask.setOfficer_remarks(cursor.getString(15));
                personTask.setOfficer_remarks(cursor.getString(16));
                personTask.setVessel_id(cursor.getString(17));
                personTask.setAssessed(cursor.getString(18));
                personTask.setAnswers2(cursor.getString(19));
                personTask.setCompleted2(cursor.getString(20));
                personTask.setPassed2(cursor.getString(21));
                personTask.setAdditional_comment(cursor.getString(22));
                personTask.setFor_app(cursor.getString(23));
                personTask.setActivity_area(cursor.getString(24));
                personTask.setIntial_cond(cursor.getString(25));
                personTask.setFeedback(cursor.getString(26));
                personTask.setLocation(cursor.getString(27));
                personTask.setEquipments(cursor.getString(28));

                // Add to list
                list.add(personTask);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //GET PERSON TASK
    public List<PersonTask> getAllPersonTask() {
        List<PersonTask> list = new ArrayList<PersonTask>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM person_task";
        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonTask personTask = new PersonTask();
                personTask.setId(cursor.getInt(0));
                personTask.setPerson_task_id(cursor.getString(1));
                personTask.setTask_id(cursor.getString(2));
                personTask.setPerson_id(cursor.getString(3));
                personTask.setCompleted(cursor.getString(4));
                personTask.setAnswers(cursor.getString(5));
                personTask.setPassed(cursor.getString(6));
                personTask.setImg_file(cursor.getString(7));
                personTask.setNot_app(cursor.getString(8));
                personTask.setLat_long(cursor.getString(9));
                personTask.setVessel_type_id(cursor.getString(10));
                personTask.setChecked_by_id(cursor.getString(11));
                personTask.setApp_by_id(cursor.getString(12));
                personTask.setDate_checked(cursor.getString(13));
                personTask.setDate_app(cursor.getString(14));
                personTask.setOfficer_remarks(cursor.getString(15));
                personTask.setOfficer_remarks(cursor.getString(16));
                personTask.setVessel_id(cursor.getString(17));
                personTask.setAssessed(cursor.getString(18));
                personTask.setAnswers2(cursor.getString(19));
                personTask.setCompleted2(cursor.getString(20));
                personTask.setPassed2(cursor.getString(21));
                personTask.setAdditional_comment(cursor.getString(22));
                personTask.setFor_app(cursor.getString(23));
                personTask.setActivity_area(cursor.getString(24));
                personTask.setIntial_cond(cursor.getString(25));
                personTask.setFeedback(cursor.getString(26));
                personTask.setLocation(cursor.getString(27));
                personTask.setEquipments(cursor.getString(28));

                // Add to list
                list.add(personTask);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //GET PERSON TASK
    public List<PersonTask> getAllPersonTaskWhere(String where) {
        List<PersonTask> list = new ArrayList<PersonTask>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM person_task WHERE " + where + "ORDER BY completed DESC";
        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonTask personTask = new PersonTask();
                personTask.setId(cursor.getInt(0));
                personTask.setPerson_task_id(cursor.getString(1));
                personTask.setTask_id(cursor.getString(2));
                personTask.setPerson_id(cursor.getString(3));
                personTask.setCompleted(cursor.getString(4));
                personTask.setAnswers(cursor.getString(5));
                personTask.setPassed(cursor.getString(6));
                personTask.setImg_file(cursor.getString(7));
                personTask.setNot_app(cursor.getString(8));
                personTask.setLat_long(cursor.getString(9));
                personTask.setVessel_type_id(cursor.getString(10));
                personTask.setChecked_by_id(cursor.getString(11));
                personTask.setApp_by_id(cursor.getString(12));
                personTask.setDate_checked(cursor.getString(13));
                personTask.setDate_app(cursor.getString(14));
                personTask.setOfficer_remarks(cursor.getString(15));
                personTask.setOfficer_remarks(cursor.getString(16));
                personTask.setVessel_id(cursor.getString(17));
                personTask.setAssessed(cursor.getString(18));
                personTask.setAnswers2(cursor.getString(19));
                personTask.setCompleted2(cursor.getString(20));
                personTask.setPassed2(cursor.getString(21));
                personTask.setAdditional_comment(cursor.getString(22));
                personTask.setFor_app(cursor.getString(23));
                personTask.setActivity_area(cursor.getString(24));
                personTask.setIntial_cond(cursor.getString(25));
                personTask.setFeedback(cursor.getString(26));
                personTask.setLocation(cursor.getString(27));
                personTask.setEquipments(cursor.getString(28));

                // Add to list
                list.add(personTask);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //ADD PERSON TASK FILE
    public void addPersonTaskFile(String person_task_file_id, Integer id, String filename, String file_desc, String uploaded, String person_id, String task_id, String person_task_id, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("person_task_file_id", person_task_file_id);
        values.put("id", id);
        values.put("filename", filename);
        values.put("file_desc", file_desc);
        values.put("uploaded", uploaded);
        values.put("person_id", person_id);
        values.put("task_id", task_id);
        values.put("person_task_id", person_task_id);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("checked_remarks", checked_remarks);
        values.put("app_remarks", app_remarks);


        try {
            db.insert("person_task_file", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_task_file INS " + e);}

        db.close();
    }

    //GET PERSON TASK FILES
    public List<PersonTaskFile> getPersonTaskFiles(String person_task_id) {
        List<PersonTaskFile> list = new ArrayList<PersonTaskFile>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(person_task_id.equals("")){
            selectQuery = "SELECT * FROM person_task_file";
        }else{
            selectQuery = "SELECT * FROM person_task_file WHERE person_task_id = '"+person_task_id+"'";
        }


        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonTaskFile personTaskFile = new PersonTaskFile();
                personTaskFile.setId(cursor.getInt(0));
                personTaskFile.setPerson_task_file_id(cursor.getString(1));
                personTaskFile.setFilename(cursor.getString(2));
                personTaskFile.setFile_desc(cursor.getString(3));
                personTaskFile.setUploaded(cursor.getString(4));
                personTaskFile.setPerson_id(cursor.getString(5));
                personTaskFile.setTask_id(cursor.getString(6));
                personTaskFile.setPerson_task_id(cursor.getString(7));
                personTaskFile.setChecked_by_id(cursor.getString(8));
                personTaskFile.setApp_by_id(cursor.getString(9));
                personTaskFile.setDate_checked(cursor.getString(10));
                personTaskFile.setDate_app(cursor.getString(11));
                personTaskFile.setChecked_remarks(cursor.getString(12));
                personTaskFile.setApp_remarks(cursor.getString(13));

                // Add to list
                list.add(personTaskFile);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }



    //GET PERSONS
    public List<Person> getPersons(String where) {
        List<Person> list = new ArrayList<Person>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = null;
        if(where.equals("")){
            selectQuery = "SELECT * FROM person ORDER BY lname";
        }else{
            selectQuery = "SELECT * FROM person WHERE "+where + " ORDER BY lname";
        }

        Log.d("QUERY", " Off : " + selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Person person = new Person();
                person.setId(cursor.getInt(0));
                person.setPerson_id(cursor.getString(1));
                person.setCode_person(cursor.getString(2));
                person.setLname(cursor.getString(3));
                person.setFname(cursor.getString(4));
                person.setMname(cursor.getString(5));
                person.setSt_address(cursor.getString(6));
                person.setCity_id(cursor.getString(7));
                person.setProvince_id(cursor.getString(8));
                person.setPhone(cursor.getString(9));
                person.setMobile(cursor.getString(10));
                person.setSt_address_province(cursor.getString(11));
                person.setPhone_province(cursor.getString(12));
                person.setEmail(cursor.getString(13));
                person.setGender(cursor.getString(14));
                person.setCiv_status(cursor.getString(15));
                person.setBirth_date(cursor.getString(16));
                person.setBirth_place(cursor.getString(17));
                person.setSpouse_name(cursor.getString(18));
                person.setChildren(cursor.getInt(19));
                person.setFather_name(cursor.getString(20));
                person.setMother_name(cursor.getString(21));
                person.setNotes(cursor.getString(22));
                person.setActive(cursor.getString(23));
                person.setDate_reg(cursor.getString(24));
                person.setCompman_id(cursor.getString(25));
                person.setCompany_id(cursor.getString(26));
                person.setAmt_paid(cursor.getDouble(27));
                person.setRank_id(cursor.getString(28));
                person.setVessel_officer(cursor.getString(29));
                person.setDept(cursor.getString(30));
                person.setVessel_id(cursor.getString(31));
                person.setCourse_id(cursor.getString(32));
                person.setSchool_id(cursor.getString(33));
                person.setSchool_admin(cursor.getString(34));
                person.setType(cursor.getString(35));
                person.setBatch_no(cursor.getInt(36));
                person.setPct_done(cursor.getDouble(37));
                person.setDays_on_board(cursor.getInt(38));
                person.setPhoto_file(cursor.getString(39));
                person.setPassport_no(cursor.getString(40));
                person.setCdc_no(cursor.getString(41));
                person.setIndos_no(cursor.getString(42));
                person.setHeight(cursor.getString(43));
                person.setWeight(cursor.getString(44));
                person.setEnrtry_date(cursor.getString(45));
                person.setMarks(cursor.getString(46));
                person.setBlood_group(cursor.getString(47));
                person.setEmergency_contact_name(cursor.getString(48));
                person.setEmergency_contact_address(cursor.getString(49));
                person.setEmergency_contact_no(cursor.getString(50));
                person.setEmergency_relationship(cursor.getString(51));
                person.setNationality(cursor.getString(52));
                person.setPassport_no_issue_date(cursor.getString(53));
                person.setCdc_no_issue_date(cursor.getString(54));
                person.setCdc_no_issue_place(cursor.getString(55));
                person.setEmergency_email(cursor.getString(56));
                person.setSponsor_id(cursor.getString(57));
                person.setDig_signature(cursor.getString(58));
                person.setSrn(cursor.getString(59));
                person.setSrb_no(cursor.getString(60));
                person.setSrb_date(cursor.getString(61));
                person.setSid_no(cursor.getString(62));
                person.setPassport_date(cursor.getString(63));
                person.setFull_name(cursor.getString(64));
                person.setLogged_in(cursor.getString(65));
                person.setW_fr(cursor.getString(66));


                // Add to list
                list.add(person);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //ADD PersonOfficer
    public void addPersonOfficer(String person_officer_id, Integer id, String person_id, String officer_id, String last_update, String from_date, String to_date, String comp_officer_ok, String vessel_id, String assessor_id, String master_id, String chief_eng_id){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_officer_id", person_officer_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("officer_id", officer_id);
        values.put("last_update", last_update);
        values.put("from_date", from_date);
        values.put("to_date", to_date);
        values.put("comp_officer_ok", comp_officer_ok);
        values.put("vessel_id", vessel_id);
        values.put("assessor_id", assessor_id);
        values.put("master_id", master_id);
        values.put("chief_eng_id", chief_eng_id);

        try {
            db.insert("person_officer", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_officer INS " + e);}

        db.close();
    }

    //GET PERSON OFFICER
    public List<PersonOfficer> getPersonOfficers(String person_id) {
        List<PersonOfficer> list = new ArrayList<PersonOfficer>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM person_officer WHERE person_id = '"+person_id+"' ORDER BY from_date DESC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonOfficer personOfficer = new PersonOfficer();
                personOfficer.setId(cursor.getInt(0));
                personOfficer.setPerson_officer_id(cursor.getString(1));
                personOfficer.setPerson_id(cursor.getString(2));
                personOfficer.setOfficer_id(cursor.getString(3));
                personOfficer.setFrom_date(cursor.getString(4));
                personOfficer.setTo_date(cursor.getString(5));
                personOfficer.setComp_officer_ok(cursor.getString(6));
                personOfficer.setVessel_id(cursor.getString(7));
                personOfficer.setLast_update(cursor.getString(8));
                personOfficer.setAssessor_id(cursor.getString(9));
                personOfficer.setMaster_id(cursor.getString(10));
                personOfficer.setChief_eng_id(cursor.getString(11));

                // Add to list
                list.add(personOfficer);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //ADD PersonEducationH
    public void addPersonEducationH(String person_education_h_id, Integer id, String person_id, String gce_level, String school_name, String school_address, String certificate_date, String login_id, String last_update){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_education_h_id", person_education_h_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("gce_level", gce_level);
        values.put("school_name", school_name);
        values.put("school_address", school_address);
        values.put("certificate_date", certificate_date);
        values.put("login_id", login_id);
        values.put("last_update", last_update);


        try {
            db.insert("person_education_h", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_education_h INS " + e);}

        db.close();
    }

    //GET PERSON EDUCATION H
    public List<PersonEducationH> getPersonEducationH(String person_id) {
        List<PersonEducationH> list = new ArrayList<PersonEducationH>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM person_education_h WHERE person_id = '"+person_id+"' ORDER BY certificate_date ASC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonEducationH personEducationH = new PersonEducationH();
                personEducationH.setId(cursor.getInt(0));
                personEducationH.setPerson_education_h_id(cursor.getString(1));
                personEducationH.setPerson_id(cursor.getString(2));
                personEducationH.setGce_level(cursor.getString(3));
                personEducationH.setSchool_name(cursor.getString(4));
                personEducationH.setSchool_address(cursor.getString(5));
                personEducationH.setCertificate_date(cursor.getString(6));
                personEducationH.setLogin_id(cursor.getString(7));
                personEducationH.setLast_update(cursor.getString(8));

                // Add to list
                list.add(personEducationH);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD PersonEducationD
    public void addPersonEducationD(String person_education_d_id, Integer id, String person_education_h_id, String subject, Double grade){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_education_d_id", person_education_d_id);
        values.put("id", id);
        values.put("person_education_h_id", person_education_h_id);
        values.put("subject", subject);
        values.put("grade", grade);


        try {
            db.insert("person_education_d", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_education_d INS " + e);}

        db.close();
    }

    //GET PERSON EDUCATION D
    public List<PersonEducationD> getPersonEducationD(String person_education_h_id) {
        List<PersonEducationD> list = new ArrayList<PersonEducationD>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM person_education_d WHERE person_education_h_id = '"+person_education_h_id+"' ORDER BY subject ASC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonEducationD personEducationD = new PersonEducationD();
                personEducationD.setId(cursor.getInt(0));
                personEducationD.setPerson_education_d_id(cursor.getString(1));
                personEducationD.setPerson_education_h_id(cursor.getString(2));
                personEducationD.setSubject(cursor.getString(3));
                personEducationD.setGrade(cursor.getDouble(4));

                // Add to list
                list.add(personEducationD);
            } while (cursor.moveToNext());
        }

        return list;
    }

    //ADD PERSON TO
    public void addPersonTo(String person_to_id, Integer id, String person_id, String date_signed, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks, String vessel_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("person_to_id", person_to_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("date_signed", date_signed);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("checked_remarks", checked_remarks);
        values.put("app_remarks", app_remarks);
        values.put("vessel_id", vessel_id);


        try {
            db.insert("person_to", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_to INS " + e);}

        db.close();
    }

    //GET STO REVIEW
    public List<PersonTo> getPersonTo(String person_id) {
        List<PersonTo> list = new ArrayList<PersonTo>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_to " +
                "WHERE person_id = '"+person_id+"' ORDER BY date_signed ASC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonTo personTo = new PersonTo();
                personTo.setId(cursor.getInt(0));
                personTo.setPerson_to_id(cursor.getString(1));
                personTo.setPerson_id(cursor.getString(2));
                personTo.setDate_signed(cursor.getString(3));
                personTo.setChecked_by_id(cursor.getString(4));
                personTo.setApp_by_id(cursor.getString(5));
                personTo.setDate_checked(cursor.getString(6));
                personTo.setDate_app(cursor.getString(7));
                personTo.setChecked_remarks(cursor.getString(8));
                Log.d("QUERY", cursor.getString(8));
                personTo.setApp_remarks(cursor.getString(9));
                personTo.setVessel_id(cursor.getString(10));

                // Add to list
                list.add(personTo);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD PersonCe
    public void addPersonCe(String person_ce_id, Integer id, String person_id, String vessel_id, String comments, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_ce_id", person_ce_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("vessel_id", vessel_id);
        values.put("comments", comments);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("checked_remarks", checked_remarks);
        values.put("app_remarks", app_remarks);


        try {
            db.insert("person_ce", null, values);
        } catch (SQLException e) {
            Log.e("ERROR  : ", "person_ce INS" + e);}

        db.close();
    }

    //GET MASTER REVIEW
    public List<PersonCe> getPersonCe(String person_id) {
        List<PersonCe> list = new ArrayList<PersonCe>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_ce " +
                "WHERE person_id = '"+person_id+"' ORDER BY date_checked ASC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonCe personCe = new PersonCe();
                personCe.setId(cursor.getInt(0));
                personCe.setPerson_ce_id(cursor.getString(1));
                personCe.setPerson_id(cursor.getString(2));
                personCe.setVessel_id(cursor.getString(3));
                personCe.setComments(cursor.getString(4));
                personCe.setChecked_by_id(cursor.getString(5));
                personCe.setApp_by_id(cursor.getString(6));
                personCe.setDate_checked(cursor.getString(7));
                personCe.setDate_app(cursor.getString(8));
                personCe.setChecked_remarks(cursor.getString(9));
                personCe.setApp_remarks(cursor.getString(10));

                // Add to list
                list.add(personCe);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD Personinspect
    public void addPersonInspect(String person_inspect_id, Integer id, String person_id, String comments, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks, String company_id, String vessel_id){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_inspect_id", person_inspect_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("comments", comments);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("checked_remarks", checked_remarks);
        values.put("app_remarks", app_remarks);
        values.put("company_id", company_id);
        values.put("vessel_id", vessel_id);

        try {
            db.insert("person_inspect", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_inspect INS " + e);}

        db.close();
    }

    //GET COMPANY REVIEW
    public List<PersonInspect> getPersonInspect(String person_id) {
        List<PersonInspect> list = new ArrayList<PersonInspect>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_inspect " +
                "WHERE person_id = '"+person_id+"' ORDER BY date_checked ASC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonInspect personInspect = new PersonInspect();
                personInspect.setId(cursor.getInt(0));
                personInspect.setPerson_inspect_id(cursor.getString(1));
                personInspect.setPerson_id(cursor.getString(2));
                personInspect.setComments(cursor.getString(3));
                personInspect.setChecked_by_id(cursor.getString(4));
                personInspect.setApp_by_id(cursor.getString(5));
                personInspect.setDate_checked(cursor.getString(6));
                personInspect.setDate_app(cursor.getString(7));
                personInspect.setChecked_remarks(cursor.getString(8));
                personInspect.setApp_remarks(cursor.getString(9));
                personInspect.setCompany_id(cursor.getString(10));
                personInspect.setVessel_id(cursor.getString(11));

                // Add to list
                list.add(personInspect);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD PersonMaterial
    public void addPersonMaterial(String person_material_id, Integer id, String person_id, String material, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks, String date_material){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_material_id", person_material_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("material", material);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("checked_remarks", checked_remarks);
        values.put("app_remarks", app_remarks);
        values.put("date_material", date_material);


        try {
            db.insert("person_material", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_material INS " + e);}

        db.close();
    }

    //GET MATERIALS
    public List<PersonMaterial> getPersonMaterials(String person_id) {
        List<PersonMaterial> list = new ArrayList<PersonMaterial>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_material " +
                "WHERE person_id = '"+person_id+"' ORDER BY date_checked ASC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonMaterial personMaterial = new PersonMaterial();
                personMaterial.setId(cursor.getInt(0));
                personMaterial.setPerson_material_id(cursor.getString(1));
                personMaterial.setPerson_id(cursor.getString(2));
                personMaterial.setMaterial(cursor.getString(3));
                personMaterial.setChecked_by_id(cursor.getString(4));
                personMaterial.setApp_by_id(cursor.getString(5));
                personMaterial.setDate_checked(cursor.getString(6));
                personMaterial.setDate_app(cursor.getString(7));
                personMaterial.setChecked_remarks(cursor.getString(8));

                personMaterial.setApp_remarks(cursor.getString(9));
                personMaterial.setDate_material(cursor.getString(10));

                // Add to list
                list.add(personMaterial);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD PERSON SAFETY
    public void addPersonSafety(String person_safety_id, Integer id, String person_id, String safety_id, String date_completed, String ship_id, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks, String na){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_safety_id", person_safety_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("safety_id", safety_id);
        values.put("date_completed", date_completed);
        values.put("ship_id", ship_id);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("checked_remarks", checked_remarks);
        values.put("app_remarks", app_remarks);
        values.put("na", na);


        try {
            db.insert("person_safety", null, values);
            Log.d("QUERY", " PS 2 " + person_safety_id);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_safety INS " + e);}

        db.close();
    }

    //GET PERSON SAFETY
    public List<PersonSafety> getPersonSafety(String person_id, String where) {
        List<PersonSafety> list = new ArrayList<PersonSafety>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_safety LEFT JOIN safety ON person_safety.safety_id = safety.id " +
                "WHERE person_id = '"+person_id+"' "+where+" ORDER BY person_safety.ship_id, safety.prio ASC";

        Log.d("QUERYSafety", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonSafety personSafety = new PersonSafety();
                personSafety.setId(cursor.getInt(0));
                personSafety.setPerson_safety_id(cursor.getString(1));
                personSafety.setPerson_id(cursor.getString(2));
                personSafety.setSafety_id(cursor.getString(3));
                personSafety.setDate_completed(cursor.getString(4));
                personSafety.setShip_id(cursor.getString(5));
                personSafety.setChecked_by_id(cursor.getString(6));
                personSafety.setApp_by_id(cursor.getString(7));
                personSafety.setDate_checked(cursor.getString(8));
                personSafety.setDate_app(cursor.getString(9));
                personSafety.setChecked_remarks(cursor.getString(10));
                personSafety.setApp_remarks(cursor.getString(11));

                // Add to list
                list.add(personSafety);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //GET PERSON SAFETY
    public List<PersonSafety> getAllPersonSafety() {
        List<PersonSafety> list = new ArrayList<PersonSafety>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_safety";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonSafety personSafety = new PersonSafety();
                personSafety.setId(cursor.getInt(0));
                personSafety.setPerson_safety_id(cursor.getString(1));
                personSafety.setPerson_id(cursor.getString(2));
                personSafety.setSafety_id(cursor.getString(3));
                personSafety.setDate_completed(cursor.getString(4));
                personSafety.setShip_id(cursor.getString(5));
                personSafety.setChecked_by_id(cursor.getString(6));
                personSafety.setApp_by_id(cursor.getString(7));
                personSafety.setDate_checked(cursor.getString(8));
                personSafety.setDate_app(cursor.getString(9));
                personSafety.setChecked_remarks(cursor.getString(10));
                personSafety.setApp_remarks(cursor.getString(11));

                // Add to list
                list.add(personSafety);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD SAFETY
    public void addSafety(String safety_id, Integer id, String desc_task, String prio, String dept){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("safety_id", safety_id);
        values.put("id", id);
        values.put("desc_task", desc_task);
        values.put("prio", prio);
        values.put("dept", dept);


        try {
            db.insert("safety", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "safety INS " + e);}

        db.close();
    }

    //GET SAFETY
    public List<Safety> getSafety(String dept) {
        List<Safety> list = new ArrayList<Safety>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery  = "";
        if(dept.equals("")){
            selectQuery = "SELECT  * FROM safety ORDER BY prio";
        }else {
            selectQuery = "SELECT  * FROM safety WHERE dept = '"+dept+"' OR dept = '' ORDER BY prio";
        }
        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Safety safety = new Safety();
                safety.setId(cursor.getInt(0));
                safety.setSafety_id(cursor.getString(1));
                safety.setDesc_task(cursor.getString(2));
                safety.setPrio(cursor.getString(3));
                safety.setDept(cursor.getString(4));

                // Add to list
                list.add(safety);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //GET SAFETYH
    public List<SafetyH> getSafetyH(String dept) {
        List<SafetyH> list = new ArrayList<SafetyH>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery  = "";
        if(dept.equals("")){
            selectQuery = "SELECT  * FROM safety_h ORDER BY prio";
        }else {
            selectQuery = "SELECT  * FROM safety_h WHERE dept = '"+dept+"' OR dept = '' ORDER BY prio";
        }
        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                SafetyH safetyh = new SafetyH();
                safetyh.setId(cursor.getInt(0));
                safetyh.setSafety_h_id(cursor.getString(1));
                safetyh.setDesc_safety_h(cursor.getString(2));
                safetyh.setPrio(cursor.getString(3));
                safetyh.setDept(cursor.getString(4));

                // Add to list
                list.add(safetyh);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //GET SAFETYH
    public List<SafetyD> getSafetyD(String safety_h_id) {
        List<SafetyD> list = new ArrayList<SafetyD>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM safety_d WHERE safety_h_id = '"+safety_h_id+"' ORDER BY prio";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                SafetyD safetyd = new SafetyD();
                safetyd.setId(cursor.getInt(0));
                safetyd.setSafety_d_id(cursor.getString(1));
                safetyd.setSafety_h_id(cursor.getString(2));
                safetyd.setDesc_safety_d(cursor.getString(3));
                safetyd.setPrio(cursor.getString(4));

                // Add to list
                list.add(safetyd);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //GET LOGIN
    public List<Login> getLogins(String where) {
        List<Login> list = new ArrayList<Login>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "";
        if(where.equals("")){
            selectQuery = "SELECT * FROM login  ORDER BY lname";
        }else {
            selectQuery = "SELECT * FROM login WHERE " + where + " ORDER BY lname";
        }

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Login login = new Login();
                login.setId(cursor.getInt(0));
                login.setLogin_id(cursor.getString(1));
                login.setEmail(cursor.getString(2));
                login.setLogin_name(cursor.getString(3));
                login.setLogin_pass(cursor.getString(4));
                login.setSession_id(cursor.getString(5));
                login.setLogin_type_id(cursor.getString(6));
                login.setInitial(cursor.getString(7));
                login.setLname(cursor.getString(8));
                login.setFname(cursor.getString(9));
                login.setMname(cursor.getString(10));
                login.setCompany_id(cursor.getString(11));
                login.setDesignation(cursor.getString(12));

                // Add to list
                list.add(login);
            } while (cursor.moveToNext());
        }

        return list;
    }

    //ADD PersonOfficer
    public void addPersonMuster(String person_muster_id, Integer id, String person_id, String vessel_id, String lifeboat_station, String lifeboat_duties, String emergency_station, String emergency_duties, String oil_spill_duties, String safety_officer_id, String security_officer_id, String master_id, String date_checked){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_muster_id", person_muster_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("vessel_id", vessel_id);
        values.put("lifeboat_station", lifeboat_station);
        values.put("lifeboat_duties", lifeboat_duties);
        values.put("emergency_station", emergency_station);
        values.put("emergency_duties", emergency_duties);
        values.put("oil_spill_duties", oil_spill_duties);
        values.put("safety_officer_id", safety_officer_id);
        values.put("security_officer_id", security_officer_id);
        values.put("master_id", master_id);
        values.put("date_checked", date_checked);


        try {
            db.insert("person_muster", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_muster INS " + e);}

        db.close();
    }

    //GET MUSTERS
    public List<PersonMuster> getPersonMuster(String person_id, String where) {
        List<PersonMuster> list = new ArrayList<PersonMuster>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_muster WHERE person_id = '"+person_id+"' "+where+"" +
                " ORDER BY vessel_id ASC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonMuster personMuster = new PersonMuster();
                personMuster.setId(cursor.getInt(0));
                personMuster.setPerson_muster_id(cursor.getString(1));
                personMuster.setPerson_id(cursor.getString(2));
                personMuster.setVessel_id(cursor.getString(3));
                personMuster.setLifeboat_station(cursor.getString(4));
                personMuster.setLifeboat_duties(cursor.getString(5));
                personMuster.setEmergency_station(cursor.getString(6));
                personMuster.setEmergency_duties(cursor.getString(7));
                personMuster.setOil_spill_duties(cursor.getString(8));
                personMuster.setSafety_officer_id(cursor.getString(9));
                personMuster.setSecurity_officer_id(cursor.getString(10));
                personMuster.setMaster_id(cursor.getString(11));
                personMuster.setDate_checked(cursor.getString(12));

                // Add to list
                list.add(personMuster);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD PERSON VESSEL
    public void addPersonVessel(String person_vessel_id, Integer id, String person_id, String vessel_id, String imo_number, String call_sign, String flag, String length_over_all, String breadth, String depth, String summer_draft, String summer_freeboard, String gross_tonnage, String net_tonnage, String dead_weight, String light_displacement, String fresh_water, String immersion_at_load, String trimming_moment_d, String bale_capacity_d, String grain_capacity_d, String liquid_capacity_d, String refrigerated_capacity_d, String container_capacity_d, String fresh_water_capacity_d, String daily_fresh_water_gen_d, String daily_fresh_water_con_d, String main_engine_make_e, String main_engine_type, String main_engine_stroke_e, String main_engine_bore_e, String main_engine_output, String main_engine_reduction_gear_e, String main_engine_turbo_charger_e, String main_engine_service_speed, String main_engine_boiler_d, String main_engine_bunker_capacity_d, String main_engine_daily_consumption_d, String main_engine_steering_gear_d, String auxiliary_make_e, String auxiliary_type_e, String auxiliary_stroke_e, String auxiliary_bore_e, String auxiliary_output_e, String auxiliary_turbo_charger_e, String auxiliary_normal_electrical_e, String auxiliary_boiler_make_e, String auxiliary_boiler_working_pressure_e, String auxiliary_boiler_type_waste_e, String fuel_main_engine_fuel_type_e, String fuel_viscosity_e, String fuel_specific_fuel_con_e, String fuel_boiler_fuel_type_e, String fuel_viscosity_range_e, String fuel_generator_fuel_type_e, String fuel_bunker_capacity_e, String fuel_daily_con_e, String others_heavy_fuel_oil_e, String others_lub_oil_purifier_e, String others_air_compressor_e, String others_oily_water_separator_e, String others_water_capacity_fw_e, String others_water_capacity_dw_e, String others_fw_generator_e, String others_av_cons_e, String others_steering_type_e, String others_er_lifting_gear_e, String others_swl_e, String others_sewage_treatment_e, String mooring_natural_fiber_d, String mooring_synthetic_fiber_d, String mooring_wires_d, String mooring_towing_spring_d, String anchors_port, String anchors_starboard, String anchors_stern_d, String anchors_spare, String anchors_cable, String lifesaving_lifeboat_type_d, String lifesaving_lifeboat_no, String lifesaving_liferaft_no, String lifesaving_lifeboat_dimension_d, String lifesaving_lifeboat_capacity, String lifesaving_liferaft_capacity, String lifesaving_lifeboat_davits, String lifesaving_lifeboat_fall, String lifesaving_lifebuoys_no, String fire_extinguisher_no, String fire_water, String fire_foam, String fire_dry_powder, String fire_co2, String fire_firehoses, String fire_breathing_e, String fire_breathing_no_e, String fire_fixed_fire_system_d, String fire_scba_d, String cargo_handling_derricks, String cargo_handling_cranes, String cargo_handling_winches, String cargo_handling_other_d, String cargo_handling_ballast_d, String cargo_handling_tank_d, String cargo_handling_pump_no, String cargo_handling_pipelines, String cargo_handling_type_rating_e, String cargo_handling_ballast_pump_e, String navigational_radar_d, String navigational_log_d, String navigational_gps_d, String navigational_magnetic_d, String navigational_gyro_d, String navigational_echo_d, String navigational_auto_d, String navigational_vhf_d, String navigational_mf_hf_d, String navigational_sat_d, String navigational_ecdis_d, String navigational_sart_d, String navigational_navtex_d, String navigational_ais_d, String navigational_vdr_d, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("person_vessel_id", person_vessel_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("vessel_id", vessel_id);
        values.put("imo_number", imo_number);
        values.put("call_sign", call_sign);
        values.put("flag", flag);
        values.put("length_over_all", length_over_all);
        values.put("breadth", breadth);
        values.put("depth", depth);
        values.put("summer_draft", summer_draft);
        values.put("summer_freeboard", summer_freeboard);
        values.put("gross_tonnage", gross_tonnage);
        values.put("net_tonnage", net_tonnage);
        values.put("dead_weight", dead_weight);
        values.put("light_displacement", light_displacement);
        values.put("fresh_water", fresh_water);
        values.put("immersion_at_load", immersion_at_load);
        values.put("trimming_moment_d", trimming_moment_d);
        values.put("bale_capacity_d", bale_capacity_d);
        values.put("grain_capacity_d", grain_capacity_d);
        values.put("liquid_capacity_d", liquid_capacity_d);
        values.put("refrigerated_capacity_d", refrigerated_capacity_d);
        values.put("container_capacity_d", container_capacity_d);
        values.put("fresh_water_capacity_d", fresh_water_capacity_d);
        values.put("daily_fresh_water_gen_d", daily_fresh_water_gen_d);
        values.put("daily_fresh_water_con_d", daily_fresh_water_con_d);
        values.put("main_engine_make_e", main_engine_make_e);
        values.put("main_engine_type", main_engine_type);
        values.put("main_engine_stroke_e", main_engine_stroke_e);
        values.put("main_engine_bore_e", main_engine_bore_e);
        values.put("main_engine_output", main_engine_output);
        values.put("main_engine_reduction_gear_e", main_engine_reduction_gear_e);
        values.put("main_engine_turbo_charger_e", main_engine_turbo_charger_e);
        values.put("main_engine_service_speed", main_engine_service_speed);
        values.put("main_engine_boiler_d", main_engine_boiler_d);
        values.put("main_engine_bunker_capacity_d", main_engine_bunker_capacity_d);
        values.put("main_engine_daily_consumption_d", main_engine_daily_consumption_d);
        values.put("main_engine_steering_gear_d", main_engine_steering_gear_d);
        values.put("auxiliary_make_e", auxiliary_make_e);
        values.put("auxiliary_type_e", auxiliary_type_e);
        values.put("auxiliary_stroke_e", auxiliary_stroke_e);
        values.put("auxiliary_bore_e", auxiliary_bore_e);
        values.put("auxiliary_output_e", auxiliary_output_e);
        values.put("auxiliary_turbo_charger_e", auxiliary_turbo_charger_e);
        values.put("auxiliary_normal_electrical_e", auxiliary_normal_electrical_e);
        values.put("auxiliary_boiler_make_e", auxiliary_boiler_make_e);
        values.put("auxiliary_boiler_working_pressure_e", auxiliary_boiler_working_pressure_e);
        values.put("auxiliary_boiler_type_waste_e", auxiliary_boiler_type_waste_e);
        values.put("fuel_main_engine_fuel_type_e", fuel_main_engine_fuel_type_e);
        values.put("fuel_viscosity_e", fuel_viscosity_e);
        values.put("fuel_specific_fuel_con_e", fuel_specific_fuel_con_e);
        values.put("fuel_boiler_fuel_type_e", fuel_boiler_fuel_type_e);
        values.put("fuel_viscosity_range_e", fuel_viscosity_range_e);
        values.put("fuel_generator_fuel_type_e", fuel_generator_fuel_type_e);
        values.put("fuel_bunker_capacity_e", fuel_bunker_capacity_e);
        values.put("fuel_daily_con_e", fuel_daily_con_e);
        values.put("others_heavy_fuel_oil_e", others_heavy_fuel_oil_e);
        values.put("others_lub_oil_purifier_e", others_lub_oil_purifier_e);
        values.put("others_air_compressor_e", others_air_compressor_e);
        values.put("others_oily_water_separator_e", others_oily_water_separator_e);
        values.put("others_water_capacity_fw_e", others_water_capacity_fw_e);
        values.put("others_water_capacity_dw_e", others_water_capacity_dw_e);
        values.put("others_fw_generator_e", others_fw_generator_e);
        values.put("others_av_cons_e", others_av_cons_e);
        values.put("others_steering_type_e", others_steering_type_e);
        values.put("others_er_lifting_gear_e", others_er_lifting_gear_e);
        values.put("others_swl_e", others_swl_e);
        values.put("others_sewage_treatment_e", others_sewage_treatment_e);
        values.put("mooring_natural_fiber_d", mooring_natural_fiber_d);
        values.put("mooring_synthetic_fiber_d", mooring_synthetic_fiber_d);
        values.put("mooring_wires_d", mooring_wires_d);
        values.put("mooring_towing_spring_d", mooring_towing_spring_d);
        values.put("anchors_port", anchors_port);
        values.put("anchors_starboard", anchors_starboard);
        values.put("anchors_stern_d", anchors_stern_d);
        values.put("anchors_spare", anchors_spare);
        values.put("anchors_cable", anchors_cable);
        values.put("lifesaving_lifeboat_type_d", lifesaving_lifeboat_type_d);
        values.put("lifesaving_lifeboat_no", lifesaving_lifeboat_no);
        values.put("lifesaving_liferaft_no", lifesaving_liferaft_no);
        values.put("lifesaving_lifeboat_dimension_d", lifesaving_lifeboat_dimension_d);
        values.put("lifesaving_lifeboat_capacity", lifesaving_lifeboat_capacity);
        values.put("lifesaving_liferaft_capacity", lifesaving_liferaft_capacity);
        values.put("lifesaving_lifeboat_davits", lifesaving_lifeboat_davits);
        values.put("lifesaving_lifeboat_fall", lifesaving_lifeboat_fall);
        values.put("lifesaving_lifebuoys_no", lifesaving_lifebuoys_no);
        values.put("fire_extinguisher_no", fire_extinguisher_no);
        values.put("fire_water", fire_water);
        values.put("fire_foam", fire_foam);
        values.put("fire_dry_powder", fire_dry_powder);
        values.put("fire_co2", fire_co2);
        values.put("fire_firehoses", fire_firehoses);
        values.put("fire_breathing_e", fire_breathing_e);
        values.put("fire_breathing_no_e", fire_breathing_no_e);
        values.put("fire_fixed_fire_system_d", fire_fixed_fire_system_d);
        values.put("fire_scba_d", fire_scba_d);
        values.put("cargo_handling_derricks", cargo_handling_derricks);
        values.put("cargo_handling_cranes", cargo_handling_cranes);
        values.put("cargo_handling_winches", cargo_handling_winches);
        values.put("cargo_handling_other_d", cargo_handling_other_d);
        values.put("cargo_handling_ballast_d", cargo_handling_ballast_d);
        values.put("cargo_handling_tank_d", cargo_handling_tank_d);
        values.put("cargo_handling_pump_no", cargo_handling_pump_no);
        values.put("cargo_handling_pipelines", cargo_handling_pipelines);
        values.put("cargo_handling_type_rating_e", cargo_handling_type_rating_e);
        values.put("cargo_handling_ballast_pump_e", cargo_handling_ballast_pump_e);
        values.put("navigational_radar_d", navigational_radar_d);
        values.put("navigational_log_d", navigational_log_d);
        values.put("navigational_gps_d", navigational_gps_d);
        values.put("navigational_magnetic_d", navigational_magnetic_d);
        values.put("navigational_gyro_d", navigational_gyro_d);
        values.put("navigational_echo_d", navigational_echo_d);
        values.put("navigational_auto_d", navigational_auto_d);
        values.put("navigational_vhf_d", navigational_vhf_d);
        values.put("navigational_mf_hf_d", navigational_mf_hf_d);
        values.put("navigational_sat_d", navigational_sat_d);
        values.put("navigational_ecdis_d", navigational_ecdis_d);
        values.put("navigational_sart_d", navigational_sart_d);
        values.put("navigational_navtex_d", navigational_navtex_d);
        values.put("navigational_ais_d", navigational_ais_d);
        values.put("navigational_vdr_d", navigational_vdr_d);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("checked_remarks", checked_remarks);
        values.put("app_remarks", app_remarks);


        try {
            db.insert("person_vessel", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_vessel INS " + e);}

        db.close();
    }

    //GET PERSON VESSEL
    public List<PersonVessel> getPersonVessels(String person_id) {
        List<PersonVessel> list = new ArrayList<PersonVessel>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_vessel " +
                "WHERE person_id = '"+person_id+"'";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonVessel personVessel = new PersonVessel();
                personVessel.setPerson_vessel_id(cursor.getString(0));
                personVessel.setId(cursor.getInt(1));
                personVessel.setPerson_id(cursor.getString(2));
                personVessel.setVessel_id(cursor.getString(3));
                personVessel.setImo_number(cursor.getString(4));
                personVessel.setCall_sign(cursor.getString(5));
                personVessel.setFlag(cursor.getString(6));

                personVessel.setLength_over_all(cursor.getString(7));
                personVessel.setBreadth(cursor.getString(8));
                personVessel.setDepth(cursor.getString(9));
                personVessel.setSummer_draft(cursor.getString(10));
                personVessel.setSummer_freeboard(cursor.getString(11));
                personVessel.setGross_tonnage(cursor.getString(12));
                personVessel.setNet_tonnage(cursor.getString(13));
                personVessel.setDead_weight(cursor.getString(14));
                personVessel.setLight_displacement(cursor.getString(15));
                personVessel.setFresh_water(cursor.getString(16));
                personVessel.setImmersion_at_load(cursor.getString(17));
                personVessel.setTrimming_moment_d(cursor.getString(18));
                personVessel.setBale_capacity_d(cursor.getString(19));
                personVessel.setGrain_capacity_d(cursor.getString(20));
                personVessel.setLiquid_capacity_d(cursor.getString(21));
                personVessel.setRefrigerated_capacity_d(cursor.getString(22));
                personVessel.setContainer_capacity_d(cursor.getString(23));
                personVessel.setFresh_water_capacity_d(cursor.getString(24));
                personVessel.setDaily_fresh_water_gen_d(cursor.getString(25));
                personVessel.setDaily_fresh_water_con_d(cursor.getString(26));

                personVessel.setMain_engine_make_e(cursor.getString(27));
                personVessel.setMain_engine_type(cursor.getString(28));
                personVessel.setMain_engine_stroke_e(cursor.getString(29));
                personVessel.setMain_engine_bore_e(cursor.getString(30));
                personVessel.setMain_engine_output(cursor.getString(31));
                personVessel.setMain_engine_reduction_gear_e(cursor.getString(32));
                personVessel.setMain_engine_turbo_charger_e(cursor.getString(33));
                personVessel.setMain_engine_service_speed(cursor.getString(34));
                personVessel.setMain_engine_boiler_d(cursor.getString(35));
                personVessel.setMain_engine_bunker_capacity_d(cursor.getString(36));
                personVessel.setMain_engine_daily_consumption_d(cursor.getString(37));
                personVessel.setMain_engine_steering_gear_d(cursor.getString(38));

                personVessel.setAuxiliary_make_e(cursor.getString(39));
                personVessel.setAuxiliary_type_e(cursor.getString(40));
                personVessel.setAuxiliary_stroke_e(cursor.getString(41));
                personVessel.setAuxiliary_bore_e(cursor.getString(42));
                personVessel.setAuxiliary_output_e(cursor.getString(43));
                personVessel.setAuxiliary_turbo_charger_e(cursor.getString(44));
                personVessel.setAuxiliary_normal_electrical_e(cursor.getString(45));

                personVessel.setAuxiliary_boiler_make_e(cursor.getString(46));
                personVessel.setAuxiliary_boiler_working_pressure_e(cursor.getString(47));
                personVessel.setAuxiliary_boiler_type_waste_e(cursor.getString(48));

                personVessel.setFuel_main_engine_fuel_type_e(cursor.getString(49));
                personVessel.setFuel_viscosity_e(cursor.getString(50));
                personVessel.setFuel_specific_fuel_con_e(cursor.getString(51));
                personVessel.setFuel_boiler_fuel_type_e(cursor.getString(52));
                personVessel.setFuel_viscosity_range_e(cursor.getString(53));
                personVessel.setFuel_generator_fuel_type_e(cursor.getString(54));
                personVessel.setFuel_bunker_capacity_e(cursor.getString(55));
                personVessel.setFuel_daily_con_e(cursor.getString(56));

                personVessel.setOthers_heavy_fuel_oil_e(cursor.getString(57));
                personVessel.setOthers_lub_oil_purifier_e(cursor.getString(58));
                personVessel.setOthers_air_compressor_e(cursor.getString(59));
                personVessel.setOthers_oily_water_separator_e(cursor.getString(60));
                personVessel.setOthers_water_capacity_fw_e(cursor.getString(61));
                personVessel.setOthers_water_capacity_dw_e(cursor.getString(62));
                personVessel.setOthers_fw_generator_e(cursor.getString(63));
                personVessel.setOthers_av_cons_e(cursor.getString(64));
                personVessel.setOthers_steering_type_e(cursor.getString(65));
                personVessel.setOthers_er_lifting_gear_e(cursor.getString(66));
                personVessel.setOthers_swl_e(cursor.getString(67));
                personVessel.setOthers_sewage_treatment_e(cursor.getString(68));

                personVessel.setMooring_natural_fiber_d(cursor.getString(69));
                personVessel.setMooring_synthetic_fiber_d(cursor.getString(70));
                personVessel.setMooring_wires_d(cursor.getString(71));
                personVessel.setMooring_towing_spring_d(cursor.getString(72));

                personVessel.setAnchors_port(cursor.getString(73));
                personVessel.setAnchors_starboard(cursor.getString(74));
                personVessel.setAnchors_stern_d(cursor.getString(75));
                personVessel.setAnchors_spare(cursor.getString(76));
                personVessel.setAnchors_cable(cursor.getString(77));

                personVessel.setLifesaving_lifeboat_type_d(cursor.getString(78));
                personVessel.setLifesaving_lifeboat_no(cursor.getString(79));
                personVessel.setLifesaving_liferaft_no(cursor.getString(80));
                personVessel.setLifesaving_lifeboat_dimension_d(cursor.getString(81));
                personVessel.setLifesaving_lifeboat_capacity(cursor.getString(82));
                personVessel.setLifesaving_liferaft_capacity(cursor.getString(83));
                personVessel.setLifesaving_lifeboat_davits(cursor.getString(84));
                personVessel.setLifesaving_lifeboat_fall(cursor.getString(85));
                personVessel.setLifesaving_lifebuoys_no(cursor.getString(86));

                personVessel.setFire_extinguisher_no(cursor.getString(87));
                personVessel.setFire_water(cursor.getString(88));
                personVessel.setFire_foam(cursor.getString(89));
                personVessel.setFire_dry_powder(cursor.getString(90));
                personVessel.setFire_co2(cursor.getString(91));
                personVessel.setFire_firehoses(cursor.getString(92));
                personVessel.setFire_breathing_e(cursor.getString(93));
                personVessel.setFire_breathing_no_e(cursor.getString(94));
                personVessel.setFire_fixed_fire_system_d(cursor.getString(95));
                personVessel.setFire_scba_d(cursor.getString(96));

                personVessel.setCargo_handling_derricks(cursor.getString(97));
                personVessel.setCargo_handling_cranes(cursor.getString(98));
                personVessel.setCargo_handling_winches(cursor.getString(99));
                personVessel.setCargo_handling_other_d(cursor.getString(100));
                personVessel.setCargo_handling_ballast_d(cursor.getString(101));
                personVessel.setCargo_handling_tank_d(cursor.getString(102));
                personVessel.setCargo_handling_pump_no(cursor.getString(103));
                personVessel.setCargo_handling_pipelines(cursor.getString(104));
                personVessel.setCargo_handling_type_rating_e(cursor.getString(105));
                personVessel.setCargo_handling_ballast_pump_e(cursor.getString(106));

                personVessel.setNavigational_radar_d(cursor.getString(107));
                personVessel.setNavigational_log_d(cursor.getString(108));
                personVessel.setNavigational_gps_d(cursor.getString(109));
                personVessel.setNavigational_magnetic_d(cursor.getString(110));
                personVessel.setNavigational_gyro_d(cursor.getString(111));
                personVessel.setNavigational_echo_d(cursor.getString(112));
                personVessel.setNavigational_auto_d(cursor.getString(113));
                personVessel.setNavigational_vhf_d(cursor.getString(114));
                personVessel.setNavigational_mf_hf_d(cursor.getString(115));
                personVessel.setNavigational_sat_d(cursor.getString(116));
                personVessel.setNavigational_ecdis_d(cursor.getString(117));
                personVessel.setNavigational_sart_d(cursor.getString(118));
                personVessel.setNavigational_navtex_d(cursor.getString(119));
                personVessel.setNavigational_ais_d(cursor.getString(120));
                personVessel.setNavigational_vdr_d(cursor.getString(121));

                personVessel.setChecked_by_id(cursor.getString(122));
                personVessel.setApp_by_id(cursor.getString(123));
                personVessel.setDate_checked(cursor.getString(124));
                personVessel.setDate_app(cursor.getString(125));
                personVessel.setChecked_remarks(cursor.getString(126));
                personVessel.setApp_remarks(cursor.getString(127));

                // Add to list
                list.add(personVessel);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //ADD RegulationH
    public void addRegulationH(String regulation_h_id, Integer id, String desc_regulation_h, String prio, String login_id, String last_update){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("regulation_h_id", regulation_h_id);
        values.put("id", id);
        values.put("desc_regulation_h", desc_regulation_h);
        values.put("prio", prio);
        values.put("login_id", login_id);
        values.put("last_update", last_update);

        try {
            db.insert("regulation_h", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "regulation_h INS " + e);}

        db.close();
    }

    //****GET REGULATION H*****//
    public List<RegulationH> getRegulationH() {
        List<RegulationH> list = new ArrayList<RegulationH>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM regulation_h ORDER BY prio ASC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                RegulationH regulationH = new RegulationH();
                regulationH.setRegulation_h_id(cursor.getString(0));
                regulationH.setId(cursor.getInt(1));
                regulationH.setDesc_regulation_h(cursor.getString(2));
                regulationH.setPrio(cursor.getInt(3));
                regulationH.setLogin_id(cursor.getString(4));
                regulationH.setLast_update(cursor.getString(5));

                // Add to list
                list.add(regulationH);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD RegulationH
    public void addRegulationD(String regulation_d_id, Integer id, String regulation_h_id, String desc_regulation_d, String prio, String contents){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("regulation_d_id", regulation_d_id);
        values.put("id", id);
        values.put("regulation_h_id", regulation_h_id);
        values.put("desc_regulation_d", desc_regulation_d);
        values.put("prio", prio);
        values.put("contents", contents);

        try {
            db.insert("regulation_d", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "regulation_d INS " + e);}

        db.close();
    }

    //****GET REGULATION D*****//
    public List<RegulationD> getRegulationD(String regulation_h_id) {
        List<RegulationD> list = new ArrayList<RegulationD>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM regulation_d WHERE regulation_h_id = '"+regulation_h_id+"' ORDER BY prio ASC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                RegulationD regulationD = new RegulationD();
                regulationD.setRegulation_d_id(cursor.getString(0));
                regulationD.setId(cursor.getInt(1));
                regulationD.setRegulation_h_id(cursor.getString(2));
                regulationD.setDesc_regulation_d(cursor.getString(3));
                regulationD.setPrio(cursor.getInt(4));
                regulationD.setContents(cursor.getString(5));

                // Add to list
                list.add(regulationD);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD PersonPortWatch
    public void addPersonRegulation(String person_regulation_id, Integer id, String person_id, String regulation_d_id, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_regulation_id", person_regulation_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("regulation_d_id", regulation_d_id);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("checked_remarks", checked_remarks);
        values.put("app_remarks", app_remarks);


        try {
            db.insert("person_regulation", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_regulation INS " + e);}

        db.close();
    }

    //****GET PERSON REGULATION*****//
    public List<PersonRegulation> getPersonRegulation(String person_id, String regulation_d_id) {
        List<PersonRegulation> list = new ArrayList<PersonRegulation>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_regulation WHERE person_id = '"+person_id+"' AND regulation_d_id = '"+regulation_d_id+"'";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonRegulation personRegulation = new PersonRegulation();
                personRegulation.setRegulation_d_id(cursor.getString(0));
                personRegulation.setId(cursor.getInt(1));
                personRegulation.setPerson_id(cursor.getString(2));
                personRegulation.setRegulation_d_id(cursor.getString(3));
                personRegulation.setChecked_by_id(cursor.getString(4));
                personRegulation.setApp_by_id(cursor.getString(5));
                personRegulation.setDate_checked(cursor.getString(6));
                personRegulation.setDate_app(cursor.getString(7));
                personRegulation.setChecked_remarks(cursor.getString(8));
                personRegulation.setApp_remarks(cursor.getString(9));

                // Add to list
                list.add(personRegulation);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD ABBREVIATION
    public void addAbbreviation(String abbreviation_id, Integer id, String code_abbreviation, String desc_abbreviation, String login_id, String last_update){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("abbreviation_id", abbreviation_id);
        values.put("id", id);
        values.put("code_abbreviation", code_abbreviation);
        values.put("desc_abbreviation", desc_abbreviation);
        values.put("login_id", login_id);
        values.put("last_update", last_update);


        try {
            db.insert("abbreviation", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "abbreviation INS " + e);}

        db.close();
    }

    //****GET PERSON REGULATION*****//
    public List<Abbreviation> getAbbreviations(String where) {
        List<Abbreviation> list = new ArrayList<Abbreviation>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(where.equals("")){
            selectQuery = "SELECT  * FROM abbreviation ORDER BY code_abbreviation ASC";
        }else{
            selectQuery = "SELECT  * FROM abbreviation WHERE " + where + " ORDER BY code_abbreviation ASC";
        }
        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Abbreviation abbreviation = new Abbreviation();
                abbreviation.setAbbreviation_id(cursor.getString(0));
                abbreviation.setId(cursor.getInt(1));
                abbreviation.setCode_abbreviation(cursor.getString(2));
                abbreviation.setDesc_abbreviation(cursor.getString(3));

                // Add to list
                list.add(abbreviation);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD PersonBridgeWatch
    public void addPersonBridgeWatch(String person_bridge_watch_id, Integer id, String person_id, String vessel_id, String date_watchkeeping, String from_time, String to_time, String voyage_number, String voyage_desc, String watch_type, String remarks, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks, Double total_hrs){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_bridge_watch_id", person_bridge_watch_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("vessel_id", vessel_id);
        values.put("date_watchkeeping", date_watchkeeping);
        values.put("from_time", from_time);
        values.put("to_time", to_time);
        values.put("voyage_number", voyage_number);
        values.put("voyage_desc", voyage_desc);
        values.put("watch_type", watch_type);
        values.put("remarks", remarks);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("checked_remarks", checked_remarks);
        values.put("app_remarks", app_remarks);
        values.put("total_hrs", total_hrs);


        try {
            db.insert("person_bridge_watch", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "" + e);}

        db.close();
    }

    //****GET PERSON BRIDGE WATCH*****//
    public List<PersonBridgeWatch> getPersonBridgeWatch(String person_id) {
        List<PersonBridgeWatch> list = new ArrayList<PersonBridgeWatch>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_bridge_watch WHERE person_id = '"+person_id+"' ORDER BY date_watchkeeping ASC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonBridgeWatch personBridgeWatch = new PersonBridgeWatch();
                personBridgeWatch.setPerson_bridge_watch_id(cursor.getString(0));
                personBridgeWatch.setId(cursor.getInt(1));
                personBridgeWatch.setPerson_id(cursor.getString(2));
                personBridgeWatch.setVessel_id(cursor.getString(3));
                personBridgeWatch.setDate_watchkeeping(cursor.getString(4));
                personBridgeWatch.setFrom_time(cursor.getString(5));
                personBridgeWatch.setTo_time(cursor.getString(6));
                personBridgeWatch.setVoyage_number(cursor.getString(7));
                personBridgeWatch.setVoyage_desc(cursor.getString(8));
                personBridgeWatch.setWatch_type(cursor.getString(9));
                personBridgeWatch.setRemarks(cursor.getString(10));
                personBridgeWatch.setChecked_by_id(cursor.getString(11));
                personBridgeWatch.setApp_by_id(cursor.getString(12));
                personBridgeWatch.setDate_checked(cursor.getString(13));
                personBridgeWatch.setDate_app(cursor.getString(14));
                personBridgeWatch.setChecked_remarks(cursor.getString(15));
                personBridgeWatch.setApp_remarks(cursor.getString(16));
                personBridgeWatch.setTotal_hrs(cursor.getDouble(17));

                // Add to list
                list.add(personBridgeWatch);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD PERSONSTEERING
    public void addPersonSteering(String person_steering_id, Integer id, String person_id, String vessel_id, String steering_type, String voyage_from, String voyage_to, String date_steering, String steering_from, String steering_to, String total_hrs, String remarks, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_steering_id", person_steering_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("vessel_id", vessel_id);
        values.put("steering_type", steering_type);
        values.put("voyage_from", voyage_from);
        values.put("voyage_to", voyage_to);
        values.put("date_steering", date_steering);
        values.put("steering_from", steering_from);
        values.put("steering_to", steering_to);
        values.put("total_hrs", total_hrs);
        values.put("remarks", remarks);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("checked_remarks", checked_remarks);
        values.put("app_remarks", app_remarks);


        try {
            db.insert("person_steering", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_steering INS " + e);}

        db.close();
    }

    //****GET PERSON STEERING *****//
    public List<PersonSteering> getPersonSteering(String person_id) {
        List<PersonSteering> list = new ArrayList<PersonSteering>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_steering WHERE person_id = '"+person_id+"' " +
                "ORDER BY date_steering ASC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonSteering personSteering = new PersonSteering();
                personSteering.setPerson_steering_id(cursor.getString(0));
                personSteering.setId(cursor.getInt(1));
                personSteering.setPerson_id(cursor.getString(2));
                personSteering.setVessel_id(cursor.getString(3));
                personSteering.setSteering_type(cursor.getString(4));
                personSteering.setVoyage_from(cursor.getString(5));
                personSteering.setVoyage_to(cursor.getString(6));
                personSteering.setDate_steering(cursor.getString(7));
                personSteering.setSteering_from(cursor.getString(8));
                personSteering.setSteering_to(cursor.getString(9));
                personSteering.setTotal_hrs(cursor.getDouble(10));
                personSteering.setRemarks(cursor.getString(11));
                personSteering.setChecked_by_id(cursor.getString(12));
                personSteering.setApp_by_id(cursor.getString(13));
                personSteering.setDate_checked(cursor.getString(14));
                personSteering.setDate_app(cursor.getString(15));
                personSteering.setChecked_remarks(cursor.getString(16));
                personSteering.setApp_remarks(cursor.getString(17));

                // Add to list
                list.add(personSteering);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD PersonPortWatch
    public void addPersonPortWatch(String person_port_watch_id, Integer id, String person_id, String vessel_id, String date_watch, String from_time, String to_time, String voyage_number, String port_name, String desc_cargo, String remarks, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks, Double total_hrs){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_port_watch_id", person_port_watch_id);
        values.put("id", id);
        values.put("person_id", person_id);
        values.put("vessel_id", vessel_id);
        values.put("date_watch", date_watch);
        values.put("from_time", from_time);
        values.put("to_time", to_time);
        values.put("voyage_number", voyage_number);
        values.put("port_name", port_name);
        values.put("desc_cargo", desc_cargo);
        values.put("remarks", remarks);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("checked_remarks", checked_remarks);
        values.put("app_remarks", app_remarks);
        values.put("total_hrs", total_hrs);


        try {
            db.insert("person_port_watch", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_port_watch INS " + e);}

        db.close();
    }

    //****GET PERSON PORT WATCHES *****//
    public List<PersonPortWatch> getPersonPortWatches(String person_id) {
        List<PersonPortWatch> list = new ArrayList<PersonPortWatch>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM person_port_watch WHERE person_id = '"+person_id+"' " +
                "ORDER BY date_watch ASC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonPortWatch personPortWatch = new PersonPortWatch();
                personPortWatch.setPerson_port_watch_id(cursor.getString(0));
                personPortWatch.setId(cursor.getInt(1));
                personPortWatch.setPerson_id(cursor.getString(2));
                personPortWatch.setVessel_id(cursor.getString(3));
                personPortWatch.setDate_watch(cursor.getString(4));
                personPortWatch.setFrom_time(cursor.getString(5));
                personPortWatch.setTo_time(cursor.getString(6));
                personPortWatch.setVoyage_number(cursor.getString(7));
                personPortWatch.setPort_name(cursor.getString(8));
                personPortWatch.setDesc_cargo(cursor.getString(9));
                personPortWatch.setRemarks(cursor.getString(10));
                personPortWatch.setChecked_by_id(cursor.getString(11));
                personPortWatch.setApp_by_id(cursor.getString(12));
                personPortWatch.setDate_checked(cursor.getString(13));
                personPortWatch.setDate_app(cursor.getString(14));
                personPortWatch.setChecked_remarks(cursor.getString(15));
                personPortWatch.setApp_remarks(cursor.getString(16));
                personPortWatch.setTotal_hrs(cursor.getDouble(17));

                // Add to list
                list.add(personPortWatch);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD SteerCompetence
    public void addSteerCompetence(String steer_competence_id, Integer id, String ref_no, String desc_competence, Integer prio){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("steer_competence_id", steer_competence_id);
        values.put("id", id);
        values.put("ref_no", ref_no);
        values.put("desc_competence", desc_competence);
        values.put("prio", prio);


        try {
            db.insert("steer_competence", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "steer_competence INS " + e);}

        db.close();
    }

    //****GET STEER COMPETENCE *****//
    public List<SteerCompetence> getSteerCompetence() {
        List<SteerCompetence> list = new ArrayList<SteerCompetence>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM steer_competence ORDER BY prio ASC";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                SteerCompetence steerCompetence = new SteerCompetence();
                steerCompetence.setSteer_competence_id(cursor.getString(0));
                steerCompetence.setId(cursor.getInt(1));
                steerCompetence.setRef_no(cursor.getString(2));
                steerCompetence.setDesc_competence(cursor.getString(3));
                steerCompetence.setPrio(cursor.getInt(4));
                // Add to list
                list.add(steerCompetence);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //****GET STEER TOPIC *****//
    public List<SteerTopic> getSteerTopic(String where) {
        List<SteerTopic> list = new ArrayList<SteerTopic>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";

        if(where.equals("")){
            selectQuery = "SELECT  * FROM steer_topic ORDER BY prio ASC";
        }else{
            selectQuery = "SELECT  * FROM steer_topic WHERE "+where+" ORDER BY prio ASC";
        }


        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                SteerTopic steerTopic = new SteerTopic();
                steerTopic.setSteer_topic_id(cursor.getString(0));
                steerTopic.setId(cursor.getInt(1));
                steerTopic.setRef_no_topic(cursor.getString(2));
                steerTopic.setDesc_steer_topic(cursor.getString(3));
                steerTopic.setSteer_competence_id(cursor.getString(4));
                steerTopic.setCriteria(cursor.getString(5));
                steerTopic.setPrio(cursor.getInt(6));
                // Add to list
                list.add(steerTopic);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD PersonPortWatch
    public void addPersonSteerTask(String person_steer_task_id, Integer id, String steer_task_id, String person_id, String vessel_id, String completed, String answers, String passed, String not_app, String lat_long, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_steer_task_id", person_steer_task_id);
        values.put("id", id);
        values.put("steer_task_id", steer_task_id);
        values.put("person_id", person_id);
        values.put("vessel_id", vessel_id);
        values.put("completed", completed);
        values.put("answers", answers);
        values.put("passed", passed);
        values.put("not_app", not_app);
        values.put("lat_long", lat_long);
        values.put("checked_by_id", checked_by_id);
        values.put("app_by_id", app_by_id);
        values.put("date_checked", date_checked);
        values.put("date_app", date_app);
        values.put("checked_remarks", checked_remarks);
        values.put("app_remarks", app_remarks);


        try {
            db.insert("person_steer_task", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_steer_task INS " + e);}

        db.close();
    }

    //ADD SteerTask
    public void addSteerTask(String steer_task_id, Integer id, String ref_no, String desc_steer_task, String steer_competence_id, String steer_topic_id, String prio, String phase_no){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("steer_task_id", steer_task_id);
        values.put("id", id);
        values.put("ref_no", ref_no);
        values.put("desc_steer_task", desc_steer_task);
        values.put("steer_competence_id", steer_competence_id);
        values.put("steer_topic_id", steer_topic_id);
        values.put("prio", prio);
        values.put("phase_no", phase_no);


        try {
            db.insert("steer_task", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "steer_task INS " + e);}

        db.close();
    }

    //****GET STEER TOPIC *****//
    public List<SteerTask> getSteerTask(String where) {
        List<SteerTask> list = new ArrayList<SteerTask>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(where.equals("")){
            selectQuery = "SELECT  * FROM steer_task  ORDER BY prio ASC";
        }else{
            selectQuery = "SELECT  * FROM steer_task WHERE "+where+" ORDER BY prio ASC";
        }

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                SteerTask steerTask = new SteerTask();
                steerTask.setSteer_task_id(cursor.getString(0));
                steerTask.setId(cursor.getInt(1));
                steerTask.setRef_no(cursor.getString(2));
                steerTask.setDesc_steer_task(cursor.getString(3));
                steerTask.setSteer_competence_id(cursor.getString(4));
                steerTask.setSteer_topic_id(cursor.getString(5));
                steerTask.setPrio(cursor.getInt(6));
                steerTask.setPhase_no(cursor.getInt(7));
                // Add to list
                list.add(steerTask);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD PERSONSTEERING
    public void addPersonSteerTopic(String person_steer_topic_id, Integer id, String steer_topic_id, String person_id, String checked_by_id, String date_checked, String checked_remarks){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_steer_topic_id", person_steer_topic_id);
        values.put("id", id);
        values.put("steer_topic_id", steer_topic_id);
        values.put("person_id", person_id);
        values.put("checked_by_id", checked_by_id);
        values.put("date_checked", date_checked);
        values.put("checked_remarks", checked_remarks);


        try {
            db.insert("person_steer_topic", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_steer_topic INS " + e);}

        db.close();
    }

    //ADD SteerTopic
    public void addSteerTopic(String steer_topic_id, Integer id, String ref_no_topic, String desc_steer_topic, String steer_competence_id, String criteria, String prio){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("steer_topic_id", steer_topic_id);
        values.put("id", id);
        values.put("ref_no_topic", ref_no_topic);
        values.put("desc_steer_topic", desc_steer_topic);
        values.put("steer_competence_id", steer_competence_id);
        values.put("criteria", criteria);
        values.put("prio", prio);


        try {
            db.insert("steer_topic", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "steer_topic INS " + e);}

        db.close();
    }
    //****GET STEER TOPIC *****//
    public List<PersonSteerTopic> getPersonSteerTopic(String where) {
        List<PersonSteerTopic> list = new ArrayList<PersonSteerTopic>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(where.equals("")){
            selectQuery = "SELECT  * FROM person_steer_topic";
        }else{
            selectQuery = "SELECT  * FROM person_steer_topic WHERE " + where;
        }


        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonSteerTopic personSteerTopic = new PersonSteerTopic();
                personSteerTopic.setPerson_steer_topic_id(cursor.getString(0));
                personSteerTopic.setId(cursor.getInt(1));
                personSteerTopic.setSteer_topic_id(cursor.getString(2));
                personSteerTopic.setPerson_id(cursor.getString(3));
                personSteerTopic.setChecked_by_id(cursor.getString(4));
                personSteerTopic.setDate_checked(cursor.getString(5));
                personSteerTopic.setChecked_remarks(cursor.getString(6));
                // Add to list
                list.add(personSteerTopic);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //****GET STEER TOPIC *****//
    public List<PersonSteerTask> getPersonSteerTask(String where) {
        List<PersonSteerTask> list = new ArrayList<PersonSteerTask>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(where.equals("")){
            selectQuery = "SELECT  * FROM person_steer_task";
        }else{
            selectQuery = "SELECT  * FROM person_steer_task WHERE " + where;
        }


        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonSteerTask personSteerTask = new PersonSteerTask();
                personSteerTask.setPerson_steer_task_id(cursor.getString(0));
                personSteerTask.setId(cursor.getInt(1));
                personSteerTask.setSteer_task_id(cursor.getString(2));
                personSteerTask.setPerson_id(cursor.getString(3));
                personSteerTask.setVessel_id(cursor.getString(4));
                personSteerTask.setCompleted(cursor.getString(5));
                personSteerTask.setAnswers(cursor.getString(6));
                personSteerTask.setPassed(cursor.getString(7));
                personSteerTask.setNot_app(cursor.getString(8));
                personSteerTask.setLat_long(cursor.getString(9));
                personSteerTask.setChecked_by_id(cursor.getString(10));
                personSteerTask.setApp_by_id(cursor.getString(11));
                personSteerTask.setDate_checked(cursor.getString(12));
                personSteerTask.setDate_app(cursor.getString(13));
                personSteerTask.setChecked_remarks(cursor.getString(14));
                personSteerTask.setApp_remarks(cursor.getString(15));
                // Add to list
                list.add(personSteerTask);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    //ADD PersonTrbTopic
    public void addPersonTrbTopic(String person_trb_topic_id, Integer id, String trb_topic_id, String person_id, String checked_by_id, String date_checked, String checked_remarks){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_trb_topic_id", person_trb_topic_id);
        values.put("id", id);
        values.put("trb_topic_id", trb_topic_id);
        values.put("person_id", person_id);
        values.put("checked_by_id", checked_by_id);
        values.put("date_checked", date_checked);
        values.put("checked_remarks", checked_remarks);


        try {
            db.insert("person_trb_topic", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_trb_topic INS " + e);}

        db.close();
    }

    //ADD PersonTrbTopic
    public void addPersonTrbSubComp(String person_trb_sub_competence_id, Integer id, String trb_topic_id, String person_id, String checked_by_id, String date_checked, String checked_remarks){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("person_trb_sub_competence_id", person_trb_sub_competence_id);
        values.put("id", id);
        values.put("trb_sub_competence_id", trb_topic_id);
        values.put("person_id", person_id);
        values.put("checked_by_id", checked_by_id);
        values.put("date_checked", date_checked);
        values.put("checked_remarks", checked_remarks);


        try {
            db.insert("person_trb_sub_competence", null, values);
        } catch (SQLException e) {
            Log.e("ERROR : ", "person_trb_sub_competence INS " + e);}

        db.close();
    }

    //****GET BACKUPITEM*****//
    public List<BackupItem> getBackupItems(String where) {
        List<BackupItem> list = new ArrayList<BackupItem>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(where.equals("")){
            selectQuery = "SELECT  * FROM backup_item ORDER BY backup_date ASC";
        }else{
            selectQuery = "SELECT  * FROM backup_item WHERE " + where + " ORDER BY backup_date ASC";
        }
        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                BackupItem backupItem = new BackupItem();
                backupItem.setId(cursor.getInt(0));
                backupItem.setTbl(cursor.getString(1));
                backupItem.setTbl_id(cursor.getString(2));
                backupItem.setBackup_date(cursor.getString(3));
                backupItem.setBackup_time(cursor.getString(4));
                backupItem.setBackup_event(cursor.getString(5));
                backupItem.setBackuped(cursor.getString(6));

                // Add to list
                list.add(backupItem);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }


    public String getFieldString(String field, String where_col, String table){
        String result = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        if(where_col.equals("")){
            query = "SELECT " + field + " FROM " + table;
        }else{
            query = "SELECT " + field + " FROM " + table + " WHERE " + where_col;
        }

        Cursor cursor = db.rawQuery(query , null);
        Log.d("QUERY", query);
        if (cursor.moveToFirst()) {
            do {
                result = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        if(result == null){
            result = "";
        }
        if(result.equals("null")){
            result = "";
        }
        return result;
    }

    public Double getFieldDouble(String field, String where_col, String table){
        Double result = 0.00;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        if(where_col.equals("")){
            query = "SELECT " + field + " FROM " + table;
        }else{
            query = "SELECT " + field + " FROM " + table + " WHERE " + where_col;
        }

        Cursor cursor = db.rawQuery(query , null);
        Log.d("QUERY", query);
        if (cursor.moveToFirst()) {
            do {
                result = cursor.getDouble(0);
            }while (cursor.moveToNext());
        }
        if(result == null){
            result = 0.00;
        }
        return result;
    }

    public Integer getFieldInt(String field, String where_col, String table){
        Integer result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        if(where_col.equals("")){
            query = "SELECT " + field + " FROM " + table;
        }else{
            query = "SELECT " + field + " FROM " + table + " WHERE " + where_col;
        }

        Cursor cursor = db.rawQuery(query , null);
        Log.d("QUERY", query);
        if (cursor.moveToFirst()) {
            do {
                result = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        if(result == null){
            result = 0;
        }
        return result;
    }

    public String newId(){
        String result = "";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try{
            cursor = db.rawQuery("SELECT lower(hex(randomblob(18)))", null);
            if (cursor.moveToFirst()) {
                do
                {
                    result = cursor.getString(0);
                }while (cursor.moveToNext());
            }
        }finally {
            if(cursor != null)
                cursor.close();
        }


        return result;
    }

    public Integer newIntegerId(String table){
        Integer id = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            cursor = db.rawQuery("SELECT MAX(id) AS id FROM " + table, null);
            if (cursor.moveToFirst()) {
                do
                {
                    id = cursor.getInt(0);
                }while (cursor.moveToNext());
            }
            id++;
        }finally {
            if(cursor != null)
                cursor.close();
        }


        return id;
    }

    public void query(String query_param){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(query_param);
    }

    public String getCadetId(){
        String result = "";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT person_id FROM person WHERE vessel_officer = 'N'", null);
        if (cursor.moveToFirst()) {
            do
            {
                result = cursor.getString(0);
            }while (cursor.moveToNext());
        }

        return result;
    }

    public Integer get_for_assessment(String person_id){
        Integer result = 0;


        return result;
    }

    public List<Task> getTaskWhere(String where) {

        List<Task> list = new ArrayList<Task>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM task WHERE desc_task LIKE '%"+ where +"%' ORDER BY desc_task";
        Log.d("RESULT", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getInt(0));
                task.setTask_id(cursor.getString(1));
                task.setRef_no_task(cursor.getString(2));
                task.setDesc_task(cursor.getString(3));
                task.setTrb_competence_id(cursor.getString(4));
                task.setTrb_topic_id(cursor.getString(5));
                task.setPrio(cursor.getInt(6));
                task.setPhase_no(cursor.getInt(7));
                task.setCat_task_id(cursor.getString(8));
                task.setLearning_activity(cursor.getString(9));
                task.setDirection_id(cursor.getString(10));
                task.setCriteria(cursor.getString(11));
                task.setTrb_function_id(cursor.getString(12));
                task.setTrb_task_group_id(cursor.getString(13));

                // Adding contact to list
                list.add(task);
            } while (cursor.moveToNext());
        }
        // return list
        return list;
    }

    //GET GLOBALSYS
    public List<GlobalSys> getGlobalSys() {
        List<GlobalSys> list = new ArrayList<GlobalSys>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery =  "SELECT  * FROM global_sys";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                GlobalSys globalSys = new GlobalSys();
                globalSys.setId(cursor.getInt(0));
                globalSys.setGlobal_sys_id(cursor.getString(1));
                globalSys.setDays_on_board(cursor.getInt(2));
                globalSys.setSubmission_alert_pct(cursor.getInt(3));
                globalSys.setSubmission_alert_days(cursor.getInt(4));
                globalSys.setAssessment_alert_pct(cursor.getInt(5));
                globalSys.setAssessment_alert_days(cursor.getInt(6));
                globalSys.setCompany_alert_days(cursor.getInt(7));

                // Add to list
                list.add(globalSys);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //GET PERSON TRB TOPICS
    public List<PersonTrbTopic> getPersonTrbTopic(String person_id) {
        List<PersonTrbTopic> list = new ArrayList<PersonTrbTopic>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM person_trb_topic WHERE person_id = '"+person_id+"'";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonTrbTopic personTrbTopic = new PersonTrbTopic();
                personTrbTopic.setPerson_trb_topic_id(cursor.getString(0));
                personTrbTopic.setId(cursor.getInt(1));
                personTrbTopic.setTrb_topic_id(cursor.getString(2));
                personTrbTopic.setPerson_id(cursor.getString(3));
                personTrbTopic.setChecked_by_id(cursor.getString(4));
                personTrbTopic.setDate_checked(cursor.getString(5));
                personTrbTopic.setChecked_remarks(cursor.getString(6));

                // Add to list
                list.add(personTrbTopic);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //GET PERSON TRB TOPICS
    public List<PersonTrbSubCompetence> getPersonTrbSubComp(String person_id) {
        List<PersonTrbSubCompetence> list = new ArrayList<PersonTrbSubCompetence>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM person_trb_sub_competence WHERE person_id = '"+person_id+"'";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonTrbSubCompetence personTrbSubCompetence = new PersonTrbSubCompetence();
                personTrbSubCompetence.setId(cursor.getInt(1));
                personTrbSubCompetence.setPerson_trb_sub_competence_id(cursor.getString(0));
                personTrbSubCompetence.setTrb_sub_competenceid(cursor.getString(2));
                personTrbSubCompetence.setPerson_id(cursor.getString(3));
                personTrbSubCompetence.setChecked_by_id(cursor.getString(4));
                personTrbSubCompetence.setDate_checked(cursor.getString(5));
                personTrbSubCompetence.setChecked_remarks(cursor.getString(6));

                // Add to list
                list.add(personTrbSubCompetence);
            } while (cursor.moveToNext());
        }

        // return contact list
        return list;
    }

    //GET PERSON TRB TOPICS
    public List<VesselType> getVesselTypes() {
        List<VesselType> list = new ArrayList<VesselType>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM vessel_type";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                VesselType vesselType = new VesselType();
                vesselType.setId(cursor.getInt(0));
                vesselType.setVessel_type_id(cursor.getString(1));
                vesselType.setDesc_vessel_type(cursor.getString(2));

                // Add to list
                list.add(vesselType);
            } while (cursor.moveToNext());
        }
        // return contact list
        return list;
    }

    //GET PERSON CURRENT VESSEL
    public String getCadetVessel(String person_id){
        String result = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT vessel_id FROM shipboard WHERE person_id = '"+person_id+"' ORDER BY sign_on DESC LIMIT 1";

        Cursor cursor = db.rawQuery(query , null);
        Log.d("QUERY", query);
        if (cursor.moveToFirst()) {
            do {
                result = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        if(result == null){
            result = "";
        }
        if(result.equals("null")){
            result = "";
        }
        return result;
    }

    //GET PERSON TRB TOPICS
    public List<PersonWorkPractice> getPersonWorkPractice(String person_id) {
        List<PersonWorkPractice> list = new ArrayList<PersonWorkPractice>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM person_work_practice";

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonWorkPractice list2 = new PersonWorkPractice();
                list2.setId(cursor.getInt(0));
                list2.setPerson_work_practice_id(cursor.getString(1));
                list2.setPerson_id(cursor.getString(2));
                list2.setVessel_id(cursor.getString(3));
                list2.setWork_practice_id(cursor.getString(4));
                list2.setChecked_by_id(cursor.getString(5));
                list2.setDate_checked(cursor.getString(6));
                list2.setChecked_remarks(cursor.getString(7));

                // Add to list
                list.add(list2);
            } while (cursor.moveToNext());
        }
        // return contact list
        return list;
    }

    //GET WORK PRACTICE
    public List<WorkPractice> getWorkPractice(String where) {
        List<WorkPractice> list = new ArrayList<WorkPractice>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(where.equals("")){
            selectQuery = "SELECT * FROM work_practice ORDER BY prio";
        }else{
            selectQuery = "SELECT * FROM work_practice WHERE " + where + " ORDER BY prio";
        }

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                WorkPractice list2 = new WorkPractice();
                list2.setId(cursor.getInt(0));
                list2.setWork_practice_id(cursor.getString(1));
                list2.setDesc_work_practice(cursor.getString(2));
                list2.setDept(cursor.getString(3));
                list2.setPrio(cursor.getString(4));
                list2.setLogin_id(cursor.getString(5));
                list2.setLast_update(cursor.getString(6));

                // Add to list
                list.add(list2);
            } while (cursor.moveToNext());
        }
        // return contact list
        return list;
    }

    //GET PERSON CREW LIST
    public List<PersonCrewList> getPersonCrewList(String person_id) {
        List<PersonCrewList> list = new ArrayList<PersonCrewList>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(person_id.equals("")){
            selectQuery = "SELECT * FROM person_crew_list ORDER BY date_uploaded DESC";
        }else{
            selectQuery = "SELECT * FROM person_crew_list WHERE person_id = '"+person_id+"' ORDER BY date_uploaded";
        }

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonCrewList list2 = new PersonCrewList();
                list2.setId(cursor.getInt(0));
                list2.setPerson_crew_list_id(cursor.getString(1));
                list2.setPerson_id(cursor.getString(2));
                list2.setVessel_id(cursor.getString(3));
                list2.setDate_uploaded(cursor.getString(4));
                list2.setFilename(cursor.getString(5));
                list2.setChecked_by_id(cursor.getString(6));
                list2.setDate_checked(cursor.getString(7));
                list2.setChecked_remarks(cursor.getString(8));
                list2.setLogin_id(cursor.getString(9));
                list2.setLast_update(cursor.getString(10));

                // Add to list
                list.add(list2);
            } while (cursor.moveToNext());
        }
        // return contact list
        return list;
    }

    //GET PERSON EMERGENCY DRILL
    public List<PersonEmergencyDrill> getPersonEmergencyDrill(String person_id) {
        List<PersonEmergencyDrill> list = new ArrayList<PersonEmergencyDrill>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(person_id.equals("")){
            selectQuery = "SELECT * FROM person_emergency_drill ORDER BY date_familiarization DESC";
        }else{
            selectQuery = "SELECT * FROM person_emergency_drill WHERE person_id = '"+person_id+"' ORDER BY date_familiarization DESC";
        }

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonEmergencyDrill list2 = new PersonEmergencyDrill();
                list2.setId(cursor.getInt(0));
                list2.setPerson_emergency_drill_id(cursor.getString(1));
                list2.setPerson_id(cursor.getString(2));
                list2.setVessel_id(cursor.getString(3));
                list2.setDate_familiarization(cursor.getString(4));
                list2.setDetails(cursor.getString(5));
                list2.setFilename(cursor.getString(6));
                list2.setChecked_by_id(cursor.getString(7));
                list2.setDate_checked(cursor.getString(8));
                list2.setChecked_remarks(cursor.getString(9));

                // Add to list
                list.add(list2);
            } while (cursor.moveToNext());
        }
        // return contact list
        return list;
    }

    //GET PERSON EMERGENCY MUSTER
    public List<PersonEmergencyMuster> getPersonEmergencyMuster(String person_id) {
        List<PersonEmergencyMuster> list = new ArrayList<PersonEmergencyMuster>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(person_id.equals("")){
            selectQuery = "SELECT * FROM person_emergency_muster ORDER BY date_familiarization DESC";
        }else{
            selectQuery = "SELECT * FROM person_emergency_muster WHERE person_id = '"+person_id+"' ORDER BY date_familiarization DESC";
        }

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonEmergencyMuster list2 = new PersonEmergencyMuster();
                list2.setId(cursor.getInt(0));
                list2.setPerson_emergency_muster_id(cursor.getString(1));
                list2.setPerson_id(cursor.getString(2));
                list2.setVessel_id(cursor.getString(3));
                list2.setDate_familiarization(cursor.getString(4));
                list2.setDetails(cursor.getString(5));
                list2.setFilename(cursor.getString(6));
                list2.setChecked_by_id(cursor.getString(7));
                list2.setDate_checked(cursor.getString(8));
                list2.setChecked_remarks(cursor.getString(9));

                // Add to list
                list.add(list2);
            } while (cursor.moveToNext());
        }
        // return contact list
        return list;
    }

    public List<ProjectWorkH> getProjectWorkH(String dept, String where) {
        List<ProjectWorkH> list = new ArrayList<ProjectWorkH>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(dept.equals("") && where.equals("")){
            selectQuery = "SELECT * FROM project_work_h ORDER BY prio ASC";
        }else if(!dept.equals("") && where.equals("")){
            selectQuery = "SELECT * FROM project_work_h WHERE dept= '"+dept+"' ORDER BY prio ASC";
        }else{
            selectQuery = "SELECT * FROM project_work_h WHERE dept = '"+dept+"' "+where+" ORDER BY prio ASC";
        }

        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ProjectWorkH list2 = new ProjectWorkH();
                list2.setId(cursor.getInt(0));
                list2.setProject_work_h_id(cursor.getString(1));
                list2.setDept(cursor.getString(2));
                list2.setCompetence_area(cursor.getString(3));
                list2.setLogin_id(cursor.getString(4));
                list2.setLast_update(cursor.getString(5));
                list2.setTrb_function_id(cursor.getString(6));

                // Add to list
                list.add(list2);
            } while (cursor.moveToNext());
        }
        // return contact list
        return list;
    }

    public List<ProjectWorkD> getProjectWorkD(String project_work_h_id) {
        List<ProjectWorkD> list = new ArrayList<ProjectWorkD>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(project_work_h_id.equals("")){
            selectQuery = "SELECT * FROM project_work_d ORDER BY prio ASC";
        }else{
            selectQuery = "SELECT * FROM project_work_d WHERE project_work_h_id = '"+project_work_h_id+"' ORDER BY prio ASC";
        }
        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ProjectWorkD list2 = new ProjectWorkD();
                list2.setId(cursor.getInt(0));
                list2.setProject_work_d_id(cursor.getString(1));
                list2.setProject_work_h_id(cursor.getString(2));
                list2.setDetails(cursor.getString(3));
                list2.setPrio(cursor.getString(4));
                list2.setLogin_id(cursor.getString(5));
                list2.setLast_update(cursor.getString(6));
                list.add(list2);
            } while (cursor.moveToNext());
        }
        // return contact list
        return list;
    }

    public List<PersonProjectWorkFile> getPersonProjectWorkFile(String person_project_work_id) {
        List<PersonProjectWorkFile> list = new ArrayList<PersonProjectWorkFile>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "";
        if(person_project_work_id.equals("")){
            selectQuery = "SELECT * FROM person_project_work_file ORDER BY prio ASC";
        }else{
            selectQuery = "SELECT * FROM person_project_work_file WHERE person_project_work_id = '"+person_project_work_id+"' ORDER BY prio ASC";
        }
        Log.d("QUERY", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PersonProjectWorkFile list2 = new PersonProjectWorkFile();
                list2.setId(cursor.getInt(0));
                list2.setPerson_project_work_file_id(cursor.getString(1));
                list2.setPerson_project_work_id(cursor.getString(2));
                list2.setFilename(cursor.getString(3));
                list2.setFile_desc(cursor.getString(4));
                list2.setPrio(cursor.getString(5));
                list.add(list2);
            } while (cursor.moveToNext());
        }
        // return contact list
        return list;
    }

    public Integer checkIfExists(String column_name, String value, String table_name){
        Integer count = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+table_name+" WHERE "+ column_name +" = '"+value+"'", null);
        Log.d("QUERY" , "SELECT * FROM "+table_name+" WHERE "+ column_name +" = '"+value+"'");
        count = cursor.getCount();
        Log.d("QUERY", "" + count);

        return count;
    }

    public boolean isFieldExist(String tableName, String fieldName)
    {
        boolean isExist = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("PRAGMA table_info("+tableName+")",null);
        res.moveToFirst();
        do {
            String currentColumn = res.getString(1);
            if (currentColumn.equals(fieldName)) {
                isExist = true;
            }
        } while (res.moveToNext());
        return isExist;
    }
}
