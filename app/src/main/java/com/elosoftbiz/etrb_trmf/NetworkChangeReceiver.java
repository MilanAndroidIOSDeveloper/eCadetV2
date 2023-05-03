package com.elosoftbiz.etrb_trmf;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.core.app.NotificationCompat;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NetworkChangeReceiver extends BroadcastReceiver {
    DatabaseHelper db;
    String str = "";
    HttpResponse response;


    String upLoadServerUri = null, url, server2, upLoadServerUri2;
    String dwnload_file_path, dl_photo_file;
    int serverResponseCode = 0;
    int downloadedSize = 0;
    int totalSize = 0, dl_cnt = 0;

    /**********  File Path *************/
    String uploadFilePath = "", err_message;
    String imageFileName = "";

    String person_id, company_id, login_pass;
    JSONObject json = null;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        //url = "https://e-cadet.com/etrb-demo/";
        url = context.getString(R.string.url);
        upLoadServerUri = url + "upload_task_files.php";
        upLoadServerUri2 = url + "upload_student_image.php";
        server2 = "https://elosoftbiz.com/ntc/uploadimage.php";
        dwnload_file_path = url + "task_files/";
        dl_photo_file = url + "photos/";

        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        if (netInfo != null && netInfo.isConnected()){
            Log.d("RESULT - INT", "With Internet");

            db = new DatabaseHelper(context);

            person_id = db.getCadetId();
            company_id = db.getFieldString("company_id", " person_id = '"+person_id+"'", "person");
            //CHECK IF FIRST TYM SETUP
            Integer cnt = db.GetCount("backup_item", "");
            if(cnt == 0){
                new DownloadOfficers(context).execute();
                new DownloadVessel(context).execute();
                new DownloadVesselType(context).execute();

                new DownloadPersonTo(context).execute();
                new DownloadPersonSafety(context).execute();
                new DownloadPersonCe(context).execute();
                new DownloadPersonInspect(context).execute();
                new DownloadPersonTask(context).execute();
                new DownloadPersonTrbSubComp(context).execute();
                return;
            }
            Log.d("RESULT - INT", db.newId());
            Integer upd = db.GetCount("backup_item", " WHERE backuped = 'N'");

            mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setSmallIcon(R.drawable.trmfcube_50);
            mBuilder.setContentTitle("eTRB");
            mBuilder.setContentText("Backing up online is in progress.");
            mBuilder.setProgress(0, 0, true);
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // notificationID allows you to update the notification later on.
            mNotificationManager.notify(1, mBuilder.build());
            if(upd > 0){
                new GetProcessData(context).execute();
            }
            new DownloadOfficers(context).execute();
            new DownloadVessel(context).execute();
            new DownloadVesselType(context).execute();

            new DownloadPersonTo(context).execute();
            new DownloadPersonCe(context).execute();
            new DownloadPersonInspect(context).execute();
            new DownloadPersonTask(context).execute();
            new DownloadPersonTrbSubComp(context).execute();
            /*
            if(upd > 0){
                new GetProcessData(context).execute();
                new DownloadOfficers(context).execute();
                new DownloadOfficerLogin(context).execute();
                new DownloadVessels(context).execute();
                new DownloadVesselTypes(context).execute();
                new DownloadPrograms(context).execute();
                new DownloadSafety(context).execute();
                new DownloadBasicTraining(context).execute();
                new DownloadPersonBasicTraining(context).execute();
                new DownloadPersonSafety(context).execute();
                new DownloadPersonVessel(context).execute();
                new DownloadPersonTo(context).execute();
                new DownloadPersonCe(context).execute();
                new DownloadPersonInspect(context).execute();
                new DownloadPersonOfficer(context).execute();
                new DownloadPersonTask(context).execute();
                new DownloadPersonTaskFile(context).execute();
            }else{
                new DownloadOfficers(context).execute();
                new DownloadOfficerLogin(context).execute();
                new DownloadVessels(context).execute();
                new DownloadVesselTypes(context).execute();
                new DownloadPrograms(context).execute();
                new DownloadSafety(context).execute();
                new DownloadBasicTraining(context).execute();
                new DownloadPersonBasicTraining(context).execute();
                new DownloadPersonSafety(context).execute();
                new DownloadPersonVessel(context).execute();
                new DownloadPersonTo(context).execute();
                new DownloadPersonCe(context).execute();
                new DownloadPersonInspect(context).execute();
                new DownloadPersonOfficer(context).execute();
                new DownloadPersonTask(context).execute();
                new DownloadPersonTaskFile(context).execute();
            }*/
        }else{
            Log.d("RESULT", "No Internet");
        }
    }

    private class GetProcessData extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public GetProcessData(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            List<BackupItem> backupItems = db.getBackupItems(" backuped = 'N'");
            for (BackupItem cn : backupItems) {
                String table = cn.getTbl();
                Log.d("QUERY", " BACKUP - " + cn.getTbl());
                String tbl_id = cn.getTbl_id();
                String event = cn.getBackup_event();
                if(cn.getTbl().equals("person_basic_training")){
                    String person_basic_training_id = tbl_id;

                    //GET FROM TBL
                    String saved_person_id = db.getFieldString("person_id", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
                    String saved_basic_training_id = db.getFieldString("basic_training_id", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
                    String saved_location_name = db.getFieldString("location_name", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
                    saved_location_name = URLEncoder.encode(saved_location_name);
                    String saved_date_completed = db.getFieldString("date_completed", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
                    String saved_doc_ref_no = db.getFieldString("doc_ref_no", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
                    saved_doc_ref_no = URLEncoder.encode(saved_doc_ref_no);
                    String saved_checked_by_id = db.getFieldString("checked_by_id", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
                    String saved_app_by_id = db.getFieldString("app_by_id", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
                    String saved_date_checked = db.getFieldString("date_checked", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
                    String saved_date_app = db.getFieldString("date_app", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
                    String saved_checked_remarks = db.getFieldString("checked_remarks", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
                    saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
                    String saved_app_remarks = db.getFieldString("app_remarks", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
                    saved_app_remarks = URLEncoder.encode(saved_app_remarks);
                    String saved_institution = db.getFieldString("institution", "person_basic_training_id = '" + person_basic_training_id + "'", "person_basic_training");
                    saved_institution = URLEncoder.encode(saved_institution);

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_basic_training&id="+person_basic_training_id+"&person_id="+saved_person_id+"&basic_training_id="+saved_basic_training_id+"&location_name="+saved_location_name+"&date_completed="+saved_date_completed+"&doc_ref_no="+saved_doc_ref_no+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&institution="+saved_institution + "&event="+event);
                    Log.d("QUERY", url +"sync.php?table=person_basic_training&id="+person_basic_training_id+"&person_id="+saved_person_id+"&basic_training_id="+saved_basic_training_id+"&location_name="+saved_location_name+"&date_completed="+saved_date_completed+"&doc_ref_no="+saved_doc_ref_no+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&institution="+saved_institution + "&event=" + event);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("QUERY", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("QUERY", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("QUERY", "" + response + str);
                    }
                }else if(cn.getTbl().equals("person_education_h")){
                    String person_education_h_id = tbl_id;

                    //GET FROM TBL
                    String saved_person_id = db.getFieldString("person_id", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
                    String saved_gce_level = db.getFieldString("gce_level", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
                    saved_gce_level = URLEncoder.encode(saved_gce_level);
                    String saved_school_name = db.getFieldString("school_name", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
                    saved_school_name = URLEncoder.encode(saved_school_name);
                    String saved_school_address = db.getFieldString("school_address", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
                    saved_school_address = URLEncoder.encode(saved_school_address);
                    String saved_certificate_date = db.getFieldString("certificate_date", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
                    saved_certificate_date = URLEncoder.encode(saved_certificate_date);
                    String saved_login_id = db.getFieldString("login_id", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
                    saved_login_id = URLEncoder.encode(saved_login_id);
                    String saved_last_update = db.getFieldString("last_update", "person_education_h_id = '" + person_education_h_id + "'", "person_education_h");
                    saved_last_update = URLEncoder.encode(saved_last_update);

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_education_h&id="+person_education_h_id+"&person_id="+saved_person_id+"&gce_level="+saved_gce_level+"&school_name="+saved_school_name+"&school_address="+saved_school_address+"&certificate_date="+saved_certificate_date+"&login_id="+saved_login_id+"&last_update="+saved_last_update + "&event="+event);
                    Log.d("QUERY", url +"sync.php?table=person_education_h&id="+person_education_h_id+"&person_id="+saved_person_id+"&gce_level="+saved_gce_level+"&school_name="+saved_school_name+"&school_address="+saved_school_address+"&certificate_date="+saved_certificate_date+"&login_id="+saved_login_id+"&last_update="+saved_last_update + "&event="+event);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("QUERY", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("QUERY", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("QUERY", "" + response + str);
                    }
                }else if(cn.getTbl().equals("person_education_d")){
                    String person_education_d_id = tbl_id;

                    //GET FROM TBL
                    String saved_person_education_h_id = db.getFieldString("person_education_h_id", "person_education_d_id = '" + person_education_d_id + "'", "person_education_d");
                    String saved_subject = db.getFieldString("subject", "person_education_d_id = '" + person_education_d_id + "'", "person_education_d");
                    saved_subject = URLEncoder.encode(saved_subject);
                    String saved_grade = db.getFieldString("grade", "person_education_d_id = '" + person_education_d_id + "'", "person_education_d");

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_education_d&id="+person_education_d_id+"&person_education_h_id="+saved_person_education_h_id+"&subject="+saved_subject+"&grade="+saved_grade+"&event="+event);
                    Log.d("QUERY", url +"sync.php?table=person_education_d&id="+person_education_d_id+"&person_education_h_id="+saved_person_education_h_id+"&subject="+saved_subject+"&grade="+saved_grade+"&event="+event);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("QUERY", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("QUERY", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("QUERY", "" + response + str);
                    }
                }else if(cn.getTbl().equals("shipboard")){
                    String id = tbl_id;

                    //GET FROM TBL
                    String saved_sign_on = db.getFieldString("sign_on", "shipboard_id = '" + id + "'", "shipboard");
                    String saved_sign_off = db.getFieldString("sign_off", "shipboard_id = '" + id + "'", "shipboard");
                    String saved_engine_watch_mos = db.getFieldString("engine_watch_mos", "shipboard_id = '" + id + "'", "shipboard");
                    String saved_engine_watch_yrs = db.getFieldString("engine_watch_yrs", "shipboard_id = '" + id + "'", "shipboard");
                    String saved_voyage_mos = db.getFieldString("voyage_mos", "shipboard_id = '" + id + "'", "shipboard");
                    String saved_person_id = db.getFieldString("person_id", "shipboard_id = '" + id + "'", "shipboard");
                    String saved_vessel_id = db.getFieldString("vessel_id", "shipboard_id = '" + id + "'", "shipboard");
                    String saved_voyage_days = db.getFieldString("voyage_days", "shipboard_id = '" + id + "'", "shipboard");
                    String saved_imo_number = db.getFieldString("imo_number", "shipboard_id = '" + id + "'", "shipboard");
                    saved_imo_number = URLEncoder.encode(saved_imo_number);

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=shipboard&id="+id+"&sign_on="+saved_sign_on+"&sign_off="+saved_sign_off+"&engine_watch_mos="+saved_engine_watch_mos+"&engine_watch_yrs="+saved_engine_watch_yrs+"&voyage_mos="+saved_voyage_mos+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&voyage_days="+saved_voyage_days+"&imo_number="+saved_imo_number + "&event="+event);
                    Log.d("QUERY", url +"sync.php?table=shipboard&id="+id+"&sign_on="+saved_sign_on+"&sign_off="+saved_sign_off+"&engine_watch_mos="+saved_engine_watch_mos+"&engine_watch_yrs="+saved_engine_watch_yrs+"&voyage_mos="+saved_voyage_mos+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&voyage_days="+saved_voyage_days+"&imo_number="+saved_imo_number + "&event="+event);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("QUERY", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("QUERY", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("QUERY", "" + response + str);
                    }
                }else if(cn.getTbl().equals("person_material")){
                    String id = tbl_id;

                    //GET FROM TBL
                    String saved_person_id = db.getFieldString("person_id", "person_material_id = '" + id + "'", "person_material");
                    String saved_material = db.getFieldString("material", "person_material_id = '" + id + "'", "person_material");
                    saved_material = URLEncoder.encode(saved_material);
                    String saved_checked_by_id = db.getFieldString("checked_by_id", "person_material_id = '" + id + "'", "person_material");
                    String saved_app_by_id = db.getFieldString("app_by_id", "person_material_id = '" + id + "'", "person_material");
                    String saved_date_checked = db.getFieldString("date_checked", "person_material_id = '" + id + "'", "person_material");
                    String saved_date_app = db.getFieldString("date_app", "person_material_id = '" + id + "'", "person_material");
                    String saved_checked_remarks = db.getFieldString("checked_remarks", "person_material_id = '" + id + "'", "person_material");
                    saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
                    String saved_app_remarks = db.getFieldString("app_remarks", "person_material_id = '" + id + "'", "person_material");
                    saved_app_remarks = URLEncoder.encode(saved_app_remarks);
                    String saved_date_material = db.getFieldString("date_material", "person_material_id = '" + id + "'", "person_material");

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_material&id="+id+"&person_id="+saved_person_id+"&material="+saved_material+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&date_material="+saved_date_material+"&event="+event);
                    Log.d("QUERY", url +"sync.php?table=person_material&id="+id+"&person_id="+saved_person_id+"&material="+saved_material+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&date_material="+saved_date_material+"&event="+event);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("QUERY", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("QUERY", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("QUERY", "" + response + str);
                    }
                }else if(cn.getTbl().equals("person_muster")){
                    String id = tbl_id;

                    //GET FROM TBL
                    String saved_person_id = db.getFieldString("person_id", "person_muster_id = '" + id + "'", "person_muster");
                    String saved_vessel_id = db.getFieldString("vessel_id", "person_muster_id = '" + id + "'", "person_muster");
                    String saved_lifeboat_station = db.getFieldString("lifeboat_station", "person_muster_id = '" + id + "'", "person_muster");
                    saved_lifeboat_station = URLEncoder.encode(saved_lifeboat_station);
                    String saved_lifeboat_duties = db.getFieldString("lifeboat_duties", "person_muster_id = '" + id + "'", "person_muster");
                    saved_lifeboat_duties = URLEncoder.encode(saved_lifeboat_duties);
                    String saved_emergency_station = db.getFieldString("emergency_station", "person_muster_id = '" + id + "'", "person_muster");
                    saved_emergency_station = URLEncoder.encode(saved_emergency_station);
                    String saved_emergency_duties = db.getFieldString("emergency_duties", "person_muster_id = '" + id + "'", "person_muster");
                    saved_emergency_duties = URLEncoder.encode(saved_emergency_duties);
                    String saved_oil_spill_duties = db.getFieldString("oil_spill_duties", "person_muster_id = '" + id + "'", "person_muster");
                    saved_oil_spill_duties = URLEncoder.encode(saved_oil_spill_duties);
                    String saved_safety_officer_id = db.getFieldString("safety_officer_id", "person_muster_id = '" + id + "'", "person_muster");
                    String saved_security_officer_id = db.getFieldString("security_officer_id", "person_muster_id = '" + id + "'", "person_muster");
                    String saved_master_id = db.getFieldString("master_id", "person_muster_id = '" + id + "'", "person_muster");
                    String saved_date_checked = db.getFieldString("date_checked", "person_muster_id = '" + id + "'", "person_muster");

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_muster&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&lifeboat_station="+saved_lifeboat_station+"&lifeboat_duties="+saved_lifeboat_duties+"&emergency_station="+saved_emergency_station+"&emergency_duties="+saved_emergency_duties+"&oil_spill_duties="+saved_oil_spill_duties+"&safety_officer_id="+saved_safety_officer_id+"&security_officer_id="+saved_security_officer_id+"&master_id="+saved_master_id+"&date_checked="+saved_date_checked+"&event="+event);
                    Log.d("QUERY", url + "sync.php?table=person_muster&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&lifeboat_station="+saved_lifeboat_station+"&lifeboat_duties="+saved_lifeboat_duties+"&emergency_station="+saved_emergency_station+"&emergency_duties="+saved_emergency_duties+"&oil_spill_duties="+saved_oil_spill_duties+"&safety_officer_id="+saved_safety_officer_id+"&security_officer_id="+saved_security_officer_id+"&master_id="+saved_master_id+"&date_checked="+saved_date_checked+"&event="+event);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("QUERY", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("QUERY", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("QUERY", "" + response + str);
                    }
                }else if(cn.getTbl().equals("person_vessel")){
                    String id = tbl_id;

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

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_vessel&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&imo_number="+saved_imo_number+"&call_sign="+saved_call_sign+"&flag="+saved_flag+"&length_over_all="+saved_length_over_all+"&breadth="+saved_breadth+"&depth="+saved_depth+"&summer_draft="+saved_summer_draft+"&summer_freeboard="+saved_summer_freeboard+"&gross_tonnage="+saved_gross_tonnage+"&net_tonnage="+saved_net_tonnage+"&dead_weight="+saved_dead_weight+"&light_displacement="+saved_light_displacement+"&fresh_water="+saved_fresh_water+"&immersion_at_load="+saved_immersion_at_load+"&trimming_moment_d="+saved_trimming_moment_d+"&bale_capacity_d="+saved_bale_capacity_d+"&grain_capacity_d="+saved_grain_capacity_d+"&liquid_capacity_d="+saved_liquid_capacity_d+"&refrigerated_capacity_d="+saved_refrigerated_capacity_d+"&container_capacity_d="+saved_container_capacity_d+"&fresh_water_capacity_d="+saved_fresh_water_capacity_d+"&daily_fresh_water_gen_d="+saved_daily_fresh_water_gen_d+"&daily_fresh_water_con_d="+saved_daily_fresh_water_con_d+"&main_engine_make_e="+saved_main_engine_make_e+"&main_engine_type="+saved_main_engine_type+"&main_engine_stroke_e="+saved_main_engine_stroke_e+"&main_engine_bore_e="+saved_main_engine_bore_e+"&main_engine_output="+saved_main_engine_output+"&main_engine_reduction_gear_e="+saved_main_engine_reduction_gear_e+"&main_engine_turbo_charger_e="+saved_main_engine_turbo_charger_e+"&main_engine_service_speed="+saved_main_engine_service_speed+"&main_engine_boiler_d="+saved_main_engine_boiler_d+"&main_engine_bunker_capacity_d="+saved_main_engine_bunker_capacity_d+"&main_engine_daily_consumption_d="+saved_main_engine_daily_consumption_d+"&main_engine_steering_gear_d="+saved_main_engine_steering_gear_d+"&auxiliary_make_e="+saved_auxiliary_make_e+"&auxiliary_type_e="+saved_auxiliary_type_e+"&auxiliary_stroke_e="+saved_auxiliary_stroke_e+"&auxiliary_bore_e="+saved_auxiliary_bore_e+"&auxiliary_output_e="+saved_auxiliary_output_e+"&auxiliary_turbo_charger_e="+saved_auxiliary_turbo_charger_e+"&auxiliary_normal_electrical_e="+saved_auxiliary_normal_electrical_e+"&auxiliary_boiler_make_e="+saved_auxiliary_boiler_make_e+"&auxiliary_boiler_working_pressure_e="+saved_auxiliary_boiler_working_pressure_e+"&auxiliary_boiler_type_waste_e="+saved_auxiliary_boiler_type_waste_e+"&fuel_main_engine_fuel_type_e="+saved_fuel_main_engine_fuel_type_e+"&fuel_viscosity_e="+saved_fuel_viscosity_e+"&fuel_specific_fuel_con_e="+saved_fuel_specific_fuel_con_e+"&fuel_boiler_fuel_type_e="+saved_fuel_boiler_fuel_type_e+"&fuel_viscosity_range_e="+saved_fuel_viscosity_range_e+"&fuel_generator_fuel_type_e="+saved_fuel_generator_fuel_type_e+"&fuel_bunker_capacity_e="+saved_fuel_bunker_capacity_e+"&fuel_daily_con_e="+saved_fuel_daily_con_e+"&others_heavy_fuel_oil_e="+saved_others_heavy_fuel_oil_e+"&others_lub_oil_purifier_e="+saved_others_lub_oil_purifier_e+"&others_air_compressor_e="+saved_others_air_compressor_e+"&others_oily_water_separator_e="+saved_others_oily_water_separator_e+"&others_water_capacity_fw_e="+saved_others_water_capacity_fw_e+"&others_water_capacity_dw_e="+saved_others_water_capacity_dw_e+"&others_fw_generator_e="+saved_others_fw_generator_e+"&others_av_cons_e="+saved_others_av_cons_e+"&others_steering_type_e="+saved_others_steering_type_e+"&others_er_lifting_gear_e="+saved_others_er_lifting_gear_e+"&others_swl_e="+saved_others_swl_e+"&others_sewage_treatment_e="+saved_others_sewage_treatment_e+"&mooring_natural_fiber_d="+saved_mooring_natural_fiber_d+"&mooring_synthetic_fiber_d="+saved_mooring_synthetic_fiber_d+"&mooring_wires_d="+saved_mooring_wires_d+"&mooring_towing_spring_d="+saved_mooring_towing_spring_d+"&anchors_port="+saved_anchors_port+"&anchors_starboard="+saved_anchors_starboard+"&anchors_stern_d="+saved_anchors_stern_d+"&anchors_spare="+saved_anchors_spare+"&anchors_cable="+saved_anchors_cable+"&lifesaving_lifeboat_type_d="+saved_lifesaving_lifeboat_type_d+"&lifesaving_lifeboat_no="+saved_lifesaving_lifeboat_no+"&lifesaving_liferaft_no="+saved_lifesaving_liferaft_no+"&lifesaving_lifeboat_dimension_d="+saved_lifesaving_lifeboat_dimension_d+"&lifesaving_lifeboat_capacity="+saved_lifesaving_lifeboat_capacity+"&lifesaving_liferaft_capacity="+saved_lifesaving_liferaft_capacity+"&lifesaving_lifeboat_davits="+saved_lifesaving_lifeboat_davits+"&lifesaving_lifeboat_fall="+saved_lifesaving_lifeboat_fall+"&lifesaving_lifebuoys_no="+saved_lifesaving_lifebuoys_no+"&fire_extinguisher_no="+saved_fire_extinguisher_no+"&fire_water="+saved_fire_water+"&fire_foam="+saved_fire_foam+"&fire_dry_powder="+saved_fire_dry_powder+"&fire_co2="+saved_fire_co2+"&fire_firehoses="+saved_fire_firehoses+"&fire_breathing_e="+saved_fire_breathing_e+"&fire_breathing_no_e="+saved_fire_breathing_no_e+"&fire_fixed_fire_system_d="+saved_fire_fixed_fire_system_d+"&fire_scba_d="+saved_fire_scba_d+"&cargo_handling_derricks="+saved_cargo_handling_derricks+"&cargo_handling_cranes="+saved_cargo_handling_cranes+"&cargo_handling_winches="+saved_cargo_handling_winches+"&cargo_handling_other_d="+saved_cargo_handling_other_d+"&cargo_handling_ballast_d="+saved_cargo_handling_ballast_d+"&cargo_handling_tank_d="+saved_cargo_handling_tank_d+"&cargo_handling_pump_no="+saved_cargo_handling_pump_no+"&cargo_handling_pipelines="+saved_cargo_handling_pipelines+"&cargo_handling_type_rating_e="+saved_cargo_handling_type_rating_e+"&cargo_handling_ballast_pump_e="+saved_cargo_handling_ballast_pump_e+"&navigational_radar_d="+saved_navigational_radar_d+"&navigational_log_d="+saved_navigational_log_d+"&navigational_gps_d="+saved_navigational_gps_d+"&navigational_magnetic_d="+saved_navigational_magnetic_d+"&navigational_gyro_d="+saved_navigational_gyro_d+"&navigational_echo_d="+saved_navigational_echo_d+"&navigational_auto_d="+saved_navigational_auto_d+"&navigational_vhf_d="+saved_navigational_vhf_d+"&navigational_mf_hf_d="+saved_navigational_mf_hf_d+"&navigational_sat_d="+saved_navigational_sat_d+"&navigational_ecdis_d="+saved_navigational_ecdis_d+"&navigational_sart_d="+saved_navigational_sart_d+"&navigational_navtex_d="+saved_navigational_navtex_d+"&navigational_ais_d="+saved_navigational_ais_d+"&navigational_vdr_d="+saved_navigational_vdr_d+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+ "&event=" + event);
                    Log.d("CONNECT", url +"sync.php?table=person_vessel&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&imo_number="+saved_imo_number+"&call_sign="+saved_call_sign+"&flag="+saved_flag+"&length_over_all="+saved_length_over_all+"&breadth="+saved_breadth+"&depth="+saved_depth+"&summer_draft="+saved_summer_draft+"&summer_freeboard="+saved_summer_freeboard+"&gross_tonnage="+saved_gross_tonnage+"&net_tonnage="+saved_net_tonnage+"&dead_weight="+saved_dead_weight+"&light_displacement="+saved_light_displacement+"&fresh_water="+saved_fresh_water+"&immersion_at_load="+saved_immersion_at_load+"&trimming_moment_d="+saved_trimming_moment_d+"&bale_capacity_d="+saved_bale_capacity_d+"&grain_capacity_d="+saved_grain_capacity_d+"&liquid_capacity_d="+saved_liquid_capacity_d+"&refrigerated_capacity_d="+saved_refrigerated_capacity_d+"&container_capacity_d="+saved_container_capacity_d+"&fresh_water_capacity_d="+saved_fresh_water_capacity_d+"&daily_fresh_water_gen_d="+saved_daily_fresh_water_gen_d+"&daily_fresh_water_con_d="+saved_daily_fresh_water_con_d+"&main_engine_make_e="+saved_main_engine_make_e+"&main_engine_type="+saved_main_engine_type+"&main_engine_stroke_e="+saved_main_engine_stroke_e+"&main_engine_bore_e="+saved_main_engine_bore_e+"&main_engine_output="+saved_main_engine_output+"&main_engine_reduction_gear_e="+saved_main_engine_reduction_gear_e+"&main_engine_turbo_charger_e="+saved_main_engine_turbo_charger_e+"&main_engine_service_speed="+saved_main_engine_service_speed+"&main_engine_boiler_d="+saved_main_engine_boiler_d+"&main_engine_bunker_capacity_d="+saved_main_engine_bunker_capacity_d+"&main_engine_daily_consumption_d="+saved_main_engine_daily_consumption_d+"&main_engine_steering_gear_d="+saved_main_engine_steering_gear_d+"&auxiliary_make_e="+saved_auxiliary_make_e+"&auxiliary_type_e="+saved_auxiliary_type_e+"&auxiliary_stroke_e="+saved_auxiliary_stroke_e+"&auxiliary_bore_e="+saved_auxiliary_bore_e+"&auxiliary_output_e="+saved_auxiliary_output_e+"&auxiliary_turbo_charger_e="+saved_auxiliary_turbo_charger_e+"&auxiliary_normal_electrical_e="+saved_auxiliary_normal_electrical_e+"&auxiliary_boiler_make_e="+saved_auxiliary_boiler_make_e+"&auxiliary_boiler_working_pressure_e="+saved_auxiliary_boiler_working_pressure_e+"&auxiliary_boiler_type_waste_e="+saved_auxiliary_boiler_type_waste_e+"&fuel_main_engine_fuel_type_e="+saved_fuel_main_engine_fuel_type_e+"&fuel_viscosity_e="+saved_fuel_viscosity_e+"&fuel_specific_fuel_con_e="+saved_fuel_specific_fuel_con_e+"&fuel_boiler_fuel_type_e="+saved_fuel_boiler_fuel_type_e+"&fuel_viscosity_range_e="+saved_fuel_viscosity_range_e+"&fuel_generator_fuel_type_e="+saved_fuel_generator_fuel_type_e+"&fuel_bunker_capacity_e="+saved_fuel_bunker_capacity_e+"&fuel_daily_con_e="+saved_fuel_daily_con_e+"&others_heavy_fuel_oil_e="+saved_others_heavy_fuel_oil_e+"&others_lub_oil_purifier_e="+saved_others_lub_oil_purifier_e+"&others_air_compressor_e="+saved_others_air_compressor_e+"&others_oily_water_separator_e="+saved_others_oily_water_separator_e+"&others_water_capacity_fw_e="+saved_others_water_capacity_fw_e+"&others_water_capacity_dw_e="+saved_others_water_capacity_dw_e+"&others_fw_generator_e="+saved_others_fw_generator_e+"&others_av_cons_e="+saved_others_av_cons_e+"&others_steering_type_e="+saved_others_steering_type_e+"&others_er_lifting_gear_e="+saved_others_er_lifting_gear_e+"&others_swl_e="+saved_others_swl_e+"&others_sewage_treatment_e="+saved_others_sewage_treatment_e+"&mooring_natural_fiber_d="+saved_mooring_natural_fiber_d+"&mooring_synthetic_fiber_d="+saved_mooring_synthetic_fiber_d+"&mooring_wires_d="+saved_mooring_wires_d+"&mooring_towing_spring_d="+saved_mooring_towing_spring_d+"&anchors_port="+saved_anchors_port+"&anchors_starboard="+saved_anchors_starboard+"&anchors_stern_d="+saved_anchors_stern_d+"&anchors_spare="+saved_anchors_spare+"&anchors_cable="+saved_anchors_cable+"&lifesaving_lifeboat_type_d="+saved_lifesaving_lifeboat_type_d+"&lifesaving_lifeboat_no="+saved_lifesaving_lifeboat_no+"&lifesaving_liferaft_no="+saved_lifesaving_liferaft_no+"&lifesaving_lifeboat_dimension_d="+saved_lifesaving_lifeboat_dimension_d+"&lifesaving_lifeboat_capacity="+saved_lifesaving_lifeboat_capacity+"&lifesaving_liferaft_capacity="+saved_lifesaving_liferaft_capacity+"&lifesaving_lifeboat_davits="+saved_lifesaving_lifeboat_davits+"&lifesaving_lifeboat_fall="+saved_lifesaving_lifeboat_fall+"&lifesaving_lifebuoys_no="+saved_lifesaving_lifebuoys_no+"&fire_extinguisher_no="+saved_fire_extinguisher_no+"&fire_water="+saved_fire_water+"&fire_foam="+saved_fire_foam+"&fire_dry_powder="+saved_fire_dry_powder+"&fire_co2="+saved_fire_co2+"&fire_firehoses="+saved_fire_firehoses+"&fire_breathing_e="+saved_fire_breathing_e+"&fire_breathing_no_e="+saved_fire_breathing_no_e+"&fire_fixed_fire_system_d="+saved_fire_fixed_fire_system_d+"&fire_scba_d="+saved_fire_scba_d+"&cargo_handling_derricks="+saved_cargo_handling_derricks+"&cargo_handling_cranes="+saved_cargo_handling_cranes+"&cargo_handling_winches="+saved_cargo_handling_winches+"&cargo_handling_other_d="+saved_cargo_handling_other_d+"&cargo_handling_ballast_d="+saved_cargo_handling_ballast_d+"&cargo_handling_tank_d="+saved_cargo_handling_tank_d+"&cargo_handling_pump_no="+saved_cargo_handling_pump_no+"&cargo_handling_pipelines="+saved_cargo_handling_pipelines+"&cargo_handling_type_rating_e="+saved_cargo_handling_type_rating_e+"&cargo_handling_ballast_pump_e="+saved_cargo_handling_ballast_pump_e+"&navigational_radar_d="+saved_navigational_radar_d+"&navigational_log_d="+saved_navigational_log_d+"&navigational_gps_d="+saved_navigational_gps_d+"&navigational_magnetic_d="+saved_navigational_magnetic_d+"&navigational_gyro_d="+saved_navigational_gyro_d+"&navigational_echo_d="+saved_navigational_echo_d+"&navigational_auto_d="+saved_navigational_auto_d+"&navigational_vhf_d="+saved_navigational_vhf_d+"&navigational_mf_hf_d="+saved_navigational_mf_hf_d+"&navigational_sat_d="+saved_navigational_sat_d+"&navigational_ecdis_d="+saved_navigational_ecdis_d+"&navigational_sart_d="+saved_navigational_sart_d+"&navigational_navtex_d="+saved_navigational_navtex_d+"&navigational_ais_d="+saved_navigational_ais_d+"&navigational_vdr_d="+saved_navigational_vdr_d+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+ "&event=" + event);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("CONNECT", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("CONNECT", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("CONNECT", "" + response + str);
                    }
                }else if(cn.getTbl().equals("person")){
                    String id = tbl_id;

                    //GET FROM TBL
                    String saved_st_address = db.getFieldString("st_address", "person_id = '" + person_id + "'", "person");
                    saved_st_address = URLEncoder.encode(saved_st_address);
                    String saved_phone = db.getFieldString("phone", "person_id = '" + person_id + "'", "person");
                    saved_phone = URLEncoder.encode(saved_phone);
                    String saved_mobile = db.getFieldString("mobile", "person_id = '" + person_id + "'", "person");
                    saved_mobile = URLEncoder.encode(saved_mobile);
                    String saved_email = db.getFieldString("email", "person_id = '" + person_id + "'", "person");
                    saved_email = URLEncoder.encode(saved_email);
                    String saved_birth_date = db.getFieldString("birth_date", "person_id = '" + person_id + "'", "person");
                    saved_birth_date = URLEncoder.encode(saved_birth_date);
                    String saved_birth_place = db.getFieldString("birth_place", "person_id = '" + person_id + "'", "person");
                    saved_birth_place = URLEncoder.encode(saved_birth_place);
                    String saved_father_name = db.getFieldString("father_name", "person_id = '" + person_id + "'", "person");
                    saved_father_name = URLEncoder.encode(saved_father_name);
                    String saved_mother_name = db.getFieldString("mother_name", "person_id = '" + person_id + "'", "person");
                    saved_mother_name = URLEncoder.encode(saved_mother_name);
                    String saved_photo_file = db.getFieldString("photo_file", "person_id = '" + person_id + "'", "person");
                    String saved_passport_no = db.getFieldString("passport_no", "person_id = '" + person_id + "'", "person");
                    saved_passport_no = URLEncoder.encode(saved_passport_no);
                    String saved_cdc_no = db.getFieldString("cdc_no", "person_id = '" + person_id + "'", "person");
                    saved_cdc_no = URLEncoder.encode(saved_cdc_no);
                    String saved_indos_no = db.getFieldString("indos_no", "person_id = '" + person_id + "'", "person");
                    saved_indos_no = URLEncoder.encode(saved_indos_no);
                    String saved_height = db.getFieldString("height", "person_id = '" + person_id + "'", "person");
                    saved_height = URLEncoder.encode(saved_height);
                    String saved_weight = db.getFieldString("weight", "person_id = '" + person_id + "'", "person");
                    saved_weight = URLEncoder.encode(saved_weight);
                    String saved_entry_date = db.getFieldString("enrtry_date", "person_id = '" + person_id + "'", "person");
                    saved_entry_date = URLEncoder.encode(saved_entry_date);
                    String saved_marks = db.getFieldString("marks", "person_id = '" + person_id + "'", "person");
                    saved_marks = URLEncoder.encode(saved_marks);
                    String saved_blood_group = db.getFieldString("blood_group", "person_id = '" + person_id + "'", "person");
                    saved_blood_group = URLEncoder.encode(saved_blood_group);
                    String saved_emergency_contact_name = db.getFieldString("emergency_contact_name", "person_id = '" + person_id + "'", "person");
                    saved_emergency_contact_name = URLEncoder.encode(saved_emergency_contact_name);
                    String saved_emergency_contact_address = db.getFieldString("emergency_contact_address", "person_id = '" + person_id + "'", "person");
                    saved_emergency_contact_address = URLEncoder.encode(saved_emergency_contact_address);
                    String saved_emergency_contact_no = db.getFieldString("emergency_contact_no", "person_id = '" + person_id + "'", "person");
                    saved_emergency_contact_no = URLEncoder.encode(saved_emergency_contact_no);
                    String saved_emergency_relationship = db.getFieldString("emergency_relationship", "person_id = '" + person_id + "'", "person");
                    saved_emergency_relationship = URLEncoder.encode(saved_emergency_relationship);
                    String saved_nationality = db.getFieldString("nationality", "person_id = '" + person_id + "'", "person");
                    saved_nationality = URLEncoder.encode(saved_nationality);
                    String saved_passport_no_issue_date = db.getFieldString("passport_no_issue_date", "person_id = '" + person_id + "'", "person");
                    saved_passport_no_issue_date = URLEncoder.encode(saved_passport_no_issue_date);
                    String saved_cdc_no_issue_date = db.getFieldString("cdc_no_issue_date", "person_id = '" + person_id + "'", "person");
                    saved_cdc_no_issue_date = URLEncoder.encode(saved_cdc_no_issue_date);
                    String saved_cdc_no_issue_place = db.getFieldString("cdc_no_issue_place", "person_id = '" + person_id + "'", "person");
                    saved_cdc_no_issue_place = URLEncoder.encode(saved_cdc_no_issue_place);

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person&id="+person_id+"&st_address="+saved_st_address+"&email="+saved_email+"&phone="+saved_phone+"&mobile="+saved_mobile+"&birth_date="+saved_birth_date+"&birth_place="+saved_birth_place+"&father_name="+saved_father_name+"&mother_name="+saved_mother_name+"&photo_file="+saved_photo_file+"&passport_no="+saved_passport_no+"&cdc_no="+saved_cdc_no+"&indos_no="+saved_indos_no+"&height="+saved_height+"&weight="+saved_weight+"&entry_date="+saved_entry_date+"&marks="+saved_marks+"&blood_group="+saved_blood_group+"&emergency_contact_name="+saved_emergency_contact_name+"&emergency_contact_address="+saved_emergency_contact_address+"&emergency_contact_no="+saved_emergency_contact_no+"&emergency_relationship="+saved_emergency_relationship+"&nationality="+saved_nationality+"&passport_no_issue_date="+saved_passport_no_issue_date+"&cdc_no_issue_date="+saved_cdc_no_issue_date+"&cdc_no_issue_place="+saved_cdc_no_issue_place);
                    Log.d("QUERY", url +"sync.php?table=person&id="+person_id+"&st_address="+saved_st_address+"&email="+saved_email+"&phone="+saved_phone+"&mobile="+saved_mobile+"&birth_date="+saved_birth_date+"&birth_place="+saved_birth_place+"&father_name="+saved_father_name+"&mother_name="+saved_mother_name+"&photo_file="+saved_photo_file+"&passport_no="+saved_passport_no+"&cdc_no="+saved_cdc_no+"&indos_no="+saved_indos_no+"&height="+saved_height+"&weight="+saved_weight+"&entry_date="+saved_entry_date+"&marks="+saved_marks+"&blood_group="+saved_blood_group+"&emergency_contact_name="+saved_emergency_contact_name+"&emergency_contact_address="+saved_emergency_contact_address+"&emergency_contact_no="+saved_emergency_contact_no+"&emergency_relationship="+saved_emergency_relationship+"&nationality="+saved_nationality+"&passport_no_issue_date="+saved_passport_no_issue_date+"&cdc_no_issue_date="+saved_cdc_no_issue_date+"&cdc_no_issue_place="+saved_cdc_no_issue_place);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("QUERY", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("QUERY", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("QUERY", "" + response + str);
                    }
                }else if(cn.getTbl().equals("person_photo")){
                    String id = tbl_id;
                    imageFileName = db.getFieldString("photo_file", " person_id = '"+person_id+"'", "person");

                    //GET FROM TBL
                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_photo&id="+person_id+"&photo_file="+imageFileName);
                    Log.d("QUERY", url +"sync.php?table=person_photo&id="+person_id+"&photo_file="+imageFileName);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("QUERY", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("QUERY", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("CONNECT", "" + response + str);
                    }

                    new Thread(new Runnable() {
                        public void run() {
                            uploadPhotoFileImage(context, imageFileName);
                        }
                    }).start();
                }else if(cn.getTbl().equals("person_task")){
                    String person_task_id = tbl_id;

                    //GET FROM TBL
                    String saved_task_id = db.getFieldString("task_id", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_person_id = db.getFieldString("person_id", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_completed = db.getFieldString("completed", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_answers = db.getFieldString("answers", "person_task_id = '" + person_task_id + "'", "person_task");
                    saved_answers = URLEncoder.encode(saved_answers);
                    String saved_passed = db.getFieldString("passed", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_img_file = db.getFieldString("img_file", "person_task_id = '" + person_task_id + "'", "person_task");
                    saved_img_file = URLEncoder.encode(saved_img_file);
                    String saved_not_app = db.getFieldString("not_app", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_lat_long = db.getFieldString("lat_long", "person_task_id = '" + person_task_id + "'", "person_task");
                    saved_lat_long = URLEncoder.encode(saved_lat_long);
                    String saved_vessel_type_id = db.getFieldString("vessel_type_id", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_checked_by_id = db.getFieldString("checked_by_id", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_app_by_id = db.getFieldString("app_by_id", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_date_checked = db.getFieldString("date_checked", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_date_app = db.getFieldString("date_app", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_officer_remarks = db.getFieldString("officer_remarks", "person_task_id = '" + person_task_id + "'", "person_task");
                    saved_officer_remarks = URLEncoder.encode(saved_officer_remarks);
                    String saved_app_remarks = db.getFieldString("app_remarks", "person_task_id = '" + person_task_id + "'", "person_task");
                    saved_app_remarks = URLEncoder.encode(saved_app_remarks);
                    String saved_vessel_id = db.getFieldString("vessel_id", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_assessed = db.getFieldString("assessed", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_answers2 = db.getFieldString("answers2", "person_task_id = '" + person_task_id + "'", "person_task");
                    saved_answers2 = URLEncoder.encode(saved_answers2);
                    String saved_passed2 = db.getFieldString("passed2", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_additional_comment = db.getFieldString("additional_comment", "person_task_id = '" + person_task_id + "'", "person_task");
                    saved_additional_comment = URLEncoder.encode(saved_additional_comment);
                    String saved_for_app = db.getFieldString("for_app", "person_task_id = '" + person_task_id + "'", "person_task");
                    String saved_activity_area = db.getFieldString("activity_area", "person_task_id = '" + person_task_id + "'", "person_task");
                    saved_activity_area = URLEncoder.encode(saved_activity_area);
                    String saved_intial_cond = db.getFieldString("intial_cond", "person_task_id = '" + person_task_id + "'", "person_task");
                    saved_intial_cond = URLEncoder.encode(saved_intial_cond);
                    String saved_feedback = db.getFieldString("feedback", "person_task_id = '" + person_task_id + "'", "person_task");
                    saved_feedback = URLEncoder.encode(saved_feedback);
                    String saved_equipments = db.getFieldString("equipments", "person_task_id = '" + person_task_id + "'", "person_task");
                    saved_equipments = URLEncoder.encode(saved_equipments);

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_task&id="+person_task_id+"&task_id="+saved_task_id+"&person_id="+saved_person_id+"&completed="+saved_completed+"&answers="+saved_answers+"&passed="+saved_passed+"&img_file="+saved_img_file+"&not_app="+saved_not_app+"&lat_long="+saved_lat_long+"&vessel_type_id="+saved_vessel_type_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&officer_remarks="+saved_officer_remarks+"&app_remarks="+saved_app_remarks+"&vessel_id="+saved_vessel_id+"&assessed="+saved_assessed+"&answers2="+saved_answers2 + "&passed2="+saved_passed2+ "&additional_comment"+saved_additional_comment+"&for_app="+saved_for_app + "&activity_area=" + saved_activity_area+"&intial_cond="+saved_intial_cond + "&feedback="+saved_feedback + "&equipments="+saved_equipments + "&event="+event);
                    Log.d("CONNECT", url +"sync.php?table=person_task&id="+person_task_id+"&task_id="+saved_task_id+"&person_id="+saved_person_id+"&completed="+saved_completed+"&answers="+saved_answers+"&passed="+saved_passed+"&img_file="+saved_img_file+"&not_app="+saved_not_app+"&lat_long="+saved_lat_long+"&vessel_type_id="+saved_vessel_type_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&officer_remarks="+saved_officer_remarks+"&app_remarks="+saved_app_remarks+"&vessel_id="+saved_vessel_id+"&event="+event);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("CONNECT", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("CONNECT", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("CONNECT", "" + response + str);
                    }
                }else if(cn.getTbl().equals("person_trb_sub_competence")){
                    String person_trb_sub_competence_id = tbl_id;

                    //GET FROM TBL
                    String saved_trb_sub_competence_id = db.getFieldString("trb_sub_competence_id", "person_trb_sub_competence_id = '" + person_trb_sub_competence_id + "'", "person_trb_sub_competence");
                    String saved_person_id = db.getFieldString("person_id", "person_trb_sub_competence_id = '" + person_trb_sub_competence_id + "'", "person_trb_sub_competence");
                    String saved_checked_by_id = db.getFieldString("checked_by_id", "person_trb_sub_competence_id = '" + person_trb_sub_competence_id + "'", "person_trb_sub_competence");
                    String saved_date_checked = db.getFieldString("date_checked", "person_trb_sub_competence_id = '" + person_trb_sub_competence_id + "'", "person_trb_sub_competence");
                    String saved_checked_remarks = db.getFieldString("checked_remarks", "person_trb_sub_competence_id = '" + person_trb_sub_competence_id + "'", "person_trb_sub_competence");
                    saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_trb_sub_competence&id="+person_trb_sub_competence_id+"&trb_sub_competence_id="+saved_trb_sub_competence_id+"&person_id="+saved_person_id+"&checked_by_id="+saved_checked_by_id+"&date_checked="+saved_date_checked+"&checked_remarks="+saved_checked_remarks+ "&event="+event);
                    Log.d("QUERY", url +"sync.php?table=person_trb_sub_competence&id="+person_trb_sub_competence_id+"&trb_sub_competence_id="+saved_trb_sub_competence_id+"&person_id="+saved_person_id+"&checked_by_id="+saved_checked_by_id+"&date_checked="+saved_date_checked+"&checked_remarks="+saved_checked_remarks+ "&event="+event);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("QUERY", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("QUERY", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("QUERY", "" + response + str);
                    }
                }else if(cn.getTbl().equals("person_task_file")){
                    String id = tbl_id;

                    String saved_filename = db.getFieldString("filename", "person_task_file_id = '" + id + "'", "person_task_file");
                    imageFileName = saved_filename;
                    saved_filename = URLEncoder.encode(saved_filename);
                    String saved_file_desc = db.getFieldString("file_desc", "person_task_file_id = '" + id + "'", "person_task_file");
                    saved_file_desc = URLEncoder.encode(saved_file_desc);
                    String saved_uploaded = db.getFieldString("uploaded", "person_task_file_id = '" + id + "'", "person_task_file");
                    String saved_person_id = db.getFieldString("person_id", "person_task_file_id = '" + id + "'", "person_task_file");
                    String saved_task_id = db.getFieldString("task_id", "person_task_file_id = '" + id + "'", "person_task_file");
                    String saved_person_task_id = db.getFieldString("person_task_id", "person_task_file_id = '" + id + "'", "person_task_file");
                    String saved_checked_by_id = db.getFieldString("checked_by_id", "person_task_file_id = '" + id + "'", "person_task_file");
                    String saved_app_by_id = db.getFieldString("app_by_id", "person_task_file_id = '" + id + "'", "person_task_file");
                    String saved_date_checked = db.getFieldString("date_checked", "person_task_file_id = '" + id + "'", "person_task_file");
                    String saved_date_app = db.getFieldString("date_app", "person_task_file_id = '" + id + "'", "person_task_file");
                    String saved_checked_remarks = db.getFieldString("checked_remarks", "person_task_file_id = '" + id + "'", "person_task_file");
                    saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
                    String saved_app_remarks = db.getFieldString("app_remarks", "person_task_file_id = '" + id + "'", "person_task_file");
                    saved_app_remarks = URLEncoder.encode(saved_app_remarks);

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_task_file&id="+id+"&filename="+saved_filename+"&file_desc="+saved_file_desc+"&uploaded="+saved_uploaded+"&person_id="+saved_person_id+"&task_id="+saved_task_id+"&person_task_id="+saved_person_task_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+ "&event="+event);
                    Log.d("CONNECT", url +"sync.php?table=person_task_file&id="+id+"&filename="+saved_filename+"&file_desc="+saved_file_desc+"&uploaded="+saved_uploaded+"&person_id="+saved_person_id+"&task_id="+saved_task_id+"&person_task_id="+saved_person_task_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+ "&event="+event);
                                                //url +"sync.php?table=person_task_file&id="+id+"&filename="+saved_filename+"&file_desc="+saved_file_desc+"&uploaded="+saved_uploaded+"&person_id="+saved_person_id+"&task_id="+saved_task_id+"&person_task_id="+saved_person_task_id+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+ "&event=ADD"

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("CONNECT", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("CONNECT", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("CONNECT", "" + response + str);
                    }
                    new Thread(new Runnable() {
                        public void run() {
                            uploadImage(context, imageFileName);
                        }
                    }).start();
                }else if(cn.getTbl().equals("person_bridge_watch")){
                    String id = tbl_id;

                    String saved_person_id = db.getFieldString("person_id", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    String saved_vessel_id = db.getFieldString("vessel_id", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    String saved_date_watchkeeping = db.getFieldString("date_watchkeeping", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    String saved_from_time = db.getFieldString("from_time", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    saved_from_time = URLEncoder.encode(saved_from_time);
                    String saved_to_time = db.getFieldString("to_time", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    saved_to_time = URLEncoder.encode(saved_to_time);
                    String saved_voyage_number = db.getFieldString("voyage_number", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    String saved_voyage_desc = db.getFieldString("voyage_desc", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    saved_voyage_desc = URLEncoder.encode(saved_voyage_desc);
                    String saved_watch_type = db.getFieldString("watch_type", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    saved_watch_type = URLEncoder.encode(saved_watch_type);
                    String saved_remarks = db.getFieldString("remarks", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    saved_remarks = URLEncoder.encode(saved_remarks);
                    String saved_checked_by_id = db.getFieldString("checked_by_id", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    String saved_app_by_id = db.getFieldString("app_by_id", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    String saved_date_checked = db.getFieldString("date_checked", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    String saved_date_app = db.getFieldString("date_app", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    String saved_checked_remarks = db.getFieldString("checked_remarks", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
                    String saved_app_remarks = db.getFieldString("app_remarks", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");
                    saved_app_remarks = URLEncoder.encode(saved_app_remarks);
                    String saved_total_hrs = db.getFieldString("total_hrs", "person_bridge_watch_id = '" + id + "'", "person_bridge_watch");

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_bridge_watch&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&date_watchkeeping="+saved_date_watchkeeping+"&from_time="+saved_from_time+"&to_time="+saved_to_time+"&voyage_number="+saved_voyage_number+"&voyage_desc="+saved_voyage_desc+"&watch_type="+saved_watch_type+"&remarks="+saved_remarks+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&total_hrs="+saved_total_hrs+"&event="+event);
                    Log.d("CONNECT", url +"sync.php?table=person_bridge_watch&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&date_watchkeeping="+saved_date_watchkeeping+"&from_time="+saved_from_time+"&to_time="+saved_to_time+"&voyage_number="+saved_voyage_number+"&voyage_desc="+saved_voyage_desc+"&watch_type="+saved_watch_type+"&remarks="+saved_remarks+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&total_hrs="+saved_total_hrs+"&event="+event);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("CONNECT", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("CONNECT", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("CONNECT", "" + response + str);
                    }
                }else if(cn.getTbl().equals("person_officer")){
                    String id = tbl_id;

                    String saved_person_id = db.getFieldString("person_id", "person_officer_id = '" + id + "'", "person_officer");
                    String saved_officer_id = db.getFieldString("officer_id", "person_officer_id = '" + id + "'", "person_officer");
                    String saved_last_update = db.getFieldString("last_update", "person_officer_id = '" + id + "'", "person_officer");
                    saved_last_update = URLEncoder.encode(saved_last_update);
                    String saved_from_date = db.getFieldString("from_date", "person_officer_id = '" + id + "'", "person_officer");
                    String saved_to_date = db.getFieldString("to_date", "person_officer_id = '" + id + "'", "person_officer");
                    String saved_comp_officer_ok = db.getFieldString("comp_officer_ok", "person_officer_id = '" + id + "'", "person_officer");
                    String saved_vessel_id = db.getFieldString("vessel_id", "person_officer_id = '" + id + "'", "person_officer");
                    String saved_assessor_id = db.getFieldString("assessor_id", "person_officer_id = '" + id + "'", "person_officer");
                    String saved_master_id = db.getFieldString("master_id", "person_officer_id = '" + id + "'", "person_officer");

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_officer&id="+id+"&person_id="+saved_person_id+"&officer_id="+saved_officer_id+"&last_update="+saved_last_update+"&from_date="+saved_from_date+"&to_date="+saved_to_date+"&comp_officer_ok="+saved_comp_officer_ok+"&vessel_id="+saved_vessel_id+"&assessor_id="+saved_assessor_id+"&master_id="+saved_master_id+"&event="+event);
                    Log.d("CONNECT", url +"sync.php?table=person_officer&id="+id+"&person_id="+saved_person_id+"&officer_id="+saved_officer_id+"&last_update="+saved_last_update+"&from_date="+saved_from_date+"&to_date="+saved_to_date+"&comp_officer_ok="+saved_comp_officer_ok+"&vessel_id="+saved_vessel_id+"&assessor_id="+saved_assessor_id+"&master_id="+saved_master_id+"&event="+event);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("CONNECT", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("CONNECT", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("CONNECT", "" + response + str);
                    }
                }else if(cn.getTbl().equals("person_port_watch")){
                    String id = tbl_id;

                    String saved_person_id = db.getFieldString("person_id", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_vessel_id = db.getFieldString("vessel_id", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_date_watch = db.getFieldString("date_watch", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_from_time = db.getFieldString("from_time", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_from_time = URLEncoder.encode(saved_from_time);
                    String saved_to_time = db.getFieldString("to_time", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_to_time = URLEncoder.encode(saved_to_time);
                    String saved_voyage_number = db.getFieldString("voyage_number", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_port_name = db.getFieldString("port_name", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_port_name = URLEncoder.encode(saved_port_name);
                    String saved_desc_cargo = db.getFieldString("desc_cargo", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_desc_cargo = URLEncoder.encode(saved_desc_cargo);
                    String saved_remarks = db.getFieldString("remarks", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_remarks = URLEncoder.encode(saved_remarks);
                    String saved_checked_by_id = db.getFieldString("checked_by_id", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_app_by_id = db.getFieldString("app_by_id", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_date_checked = db.getFieldString("date_checked", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_date_app = db.getFieldString("date_app", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_checked_remarks = db.getFieldString("checked_remarks", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
                    String saved_app_remarks = db.getFieldString("app_remarks", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_app_remarks = URLEncoder.encode(saved_app_remarks);
                    String saved_total_hrs = db.getFieldString("total_hrs", "person_port_watch_id = '" + id + "'", "person_port_watch");

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_port_watch&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&date_watch="+saved_date_watch+"&from_time="+saved_from_time+"&to_time="+saved_to_time+"&voyage_number="+saved_voyage_number+"&port_name="+saved_port_name+"&desc_cargo="+saved_desc_cargo+"&remarks="+saved_remarks+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&total_hrs="+saved_total_hrs+"&event="+event);
                    Log.d("CONNECT", url +"sync.php?table=person_port_watch&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&date_watch="+saved_date_watch+"&from_time="+saved_from_time+"&to_time="+saved_to_time+"&voyage_number="+saved_voyage_number+"&port_name="+saved_port_name+"&desc_cargo="+saved_desc_cargo+"&remarks="+saved_remarks+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&total_hrs="+saved_total_hrs+"&event="+event);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("CONNECT", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("CONNECT", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("CONNECT", "" + response + str);
                    }
                }else if(cn.getTbl().equals("person_port_watch")){
                    String id = tbl_id;

                    String saved_person_id = db.getFieldString("person_id", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_vessel_id = db.getFieldString("vessel_id", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_date_watch = db.getFieldString("date_watch", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_from_time = db.getFieldString("from_time", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_from_time = URLEncoder.encode(saved_from_time);
                    String saved_to_time = db.getFieldString("to_time", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_to_time = URLEncoder.encode(saved_to_time);
                    String saved_voyage_number = db.getFieldString("voyage_number", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_port_name = db.getFieldString("port_name", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_port_name = URLEncoder.encode(saved_port_name);
                    String saved_desc_cargo = db.getFieldString("desc_cargo", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_desc_cargo = URLEncoder.encode(saved_desc_cargo);
                    String saved_remarks = db.getFieldString("remarks", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_remarks = URLEncoder.encode(saved_remarks);
                    String saved_checked_by_id = db.getFieldString("checked_by_id", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_app_by_id = db.getFieldString("app_by_id", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_date_checked = db.getFieldString("date_checked", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_date_app = db.getFieldString("date_app", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    String saved_checked_remarks = db.getFieldString("checked_remarks", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_checked_remarks = URLEncoder.encode(saved_checked_remarks);
                    String saved_app_remarks = db.getFieldString("app_remarks", "person_port_watch_id = '" + id + "'", "person_port_watch");
                    saved_app_remarks = URLEncoder.encode(saved_app_remarks);
                    String saved_total_hrs = db.getFieldString("total_hrs", "person_port_watch_id = '" + id + "'", "person_port_watch");

                    HttpClient myClient = new DefaultHttpClient();
                    HttpPost myConnection = new HttpPost(url +"sync.php?table=person_port_watch&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&date_watch="+saved_date_watch+"&from_time="+saved_from_time+"&to_time="+saved_to_time+"&voyage_number="+saved_voyage_number+"&port_name="+saved_port_name+"&desc_cargo="+saved_desc_cargo+"&remarks="+saved_remarks+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&total_hrs="+saved_total_hrs+"&event="+event);
                    Log.d("CONNECT", url +"sync.php?table=person_port_watch&id="+id+"&person_id="+saved_person_id+"&vessel_id="+saved_vessel_id+"&date_watch="+saved_date_watch+"&from_time="+saved_from_time+"&to_time="+saved_to_time+"&voyage_number="+saved_voyage_number+"&port_name="+saved_port_name+"&desc_cargo="+saved_desc_cargo+"&remarks="+saved_remarks+"&checked_by_id="+saved_checked_by_id+"&app_by_id="+saved_app_by_id+"&date_checked="+saved_date_checked+"&date_app="+saved_date_app+"&checked_remarks="+saved_checked_remarks+"&app_remarks="+saved_app_remarks+"&total_hrs="+saved_total_hrs+"&event="+event);

                    try {
                        response = (HttpResponse) myClient.execute(myConnection);
                        str = EntityUtils.toString(response.getEntity(), "UTF-8");
                        Log.d("CONNECT", str);
                        db.query("UPDATE backup_item SET backuped = 'Y' WHERE id =" + cn.getId());
                    } catch (ClientProtocolException e) {
                        err_message = "Cannot connect to server.";
                        e.printStackTrace();
                        Log.d("CONNECT", "" + response + str);
                    } catch (IOException e) {
                        e.printStackTrace();
                        err_message = "Sorry! Something went wrong." + e;
                        Log.d("CONNECT", "" + response + str);
                    }
                }



            }



            return null;
        }
        protected void onPostExecute(Void result){
            return;
        }
    }

    //******** DOWNLOAD OFFICERS ********
    private class DownloadOfficers extends AsyncTask<Void, Void, Void>{
        public Context context;
        public DownloadOfficers(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();
            HttpPost myConnection = new HttpPost(url +"connection.php?login_name=&login_pass=&table=officer&company_id=" + URLEncoder.encode(company_id)+"&person_id="+person_id);
            Log.d("QUERY", url +"connection.php?login_name=&login_pass=&table=officer&company_id=" + URLEncoder.encode(company_id)+"&person_id="+person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("QUERY", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("QUERY", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("QUERY", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {

                        json = jArray.getJSONObject(i);

                        //id = json.getString("id");
                        String id = json.getString("id");

                        String code_person = json.getString("code_person");
                        String lname = json.getString("lname");
                        String fname = json.getString("fname");
                        String mname = json.getString("mname");
                        if(mname.equals("null")){
                            mname = "";
                        }
                        String st_address = json.getString("st_address");
                        String city_id = json.getString("city_id");
                        String province_id = json.getString("province_id");
                        final String phone = json.getString("phone");
                        String mobile = json.getString("mobile");
                        String st_address_province = json.getString("st_address_province");
                        String phone_province = json.getString("phone_province");
                        String email = json.getString("email");
                        String gender = json.getString("gender");
                        String civ_status = json.getString("civ_status");
                        String birth_date = json.getString("birth_date");
                        if(birth_date.equals("null")){
                            birth_date = "";
                        }
                        String birth_place = json.getString("birth_place");
                        String spouse_name = json.getString("spouse_name");
                        Integer children = json.getInt("children");
                        String father_name = json.getString("father_name");
                        String mother_name = json.getString("mother_name");
                        String notes = json.getString("notes");
                        String active = json.getString("active");
                        String date_reg = json.getString("date_reg");
                        String compman_id = json.getString("compman_id");
                        String company_id = json.getString("company_id");
                        Double amt_paid = json.getDouble("amt_paid");
                        String rank_id = json.getString("rank_id");
                        String vessel_officer = "Y";
                        String dept = json.getString("dept");
                        String vessel_id = json.getString("vessel_id");
                        String course_id = json.getString("course_id");
                        String school_id = json.getString("school_id");
                        String school_admin = json.getString("school_admin");
                        String officer_type = json.getString("officer_type");
                        Integer batch_no = json.getInt("batch_no");
                        Double pct_done = json.getDouble("pct_done");
                        Log.d("PCT DONE ", "" + pct_done);
                        if(pct_done.equals("")){
                            pct_done = 0.00;
                        }
                        if(pct_done == null){
                            pct_done = 0.00;
                        }
                        Integer days_on_board = json.getInt("days_on_board");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String last_login = sdf.format(new Date());
                        String full_name = lname + ", " + fname + " " + mname;

                        Log.d("RESULT", "ID : "+ id + " " + lname);
                        String passport_no = json.getString("passport_no");
                        String cdc_no = json.getString("cdc_no");
                        String indos_no = json.getString("indos_no");
                        String height = json.getString("height");
                        String weight = json.getString("weight");
                        String enrtry_date = json.getString("entry_date");
                        if(enrtry_date.equals("null")){
                            enrtry_date = "";
                        }
                        String marks = json.getString("marks");
                        String blood_group = json.getString("blood_group");
                        String emergency_contact_name = json.getString("emergency_contact_name");
                        String emergency_contact_address = json.getString("emergency_contact_address");
                        String emergency_contact_no = json.getString("emergency_contact_no");
                        String emergency_relationship = json.getString("emergency_relationship");
                        String nationality = json.getString("nationality");
                        String passport_no_issue_date = json.getString("passport_no_issue_date");
                        if(passport_no_issue_date.equals("null")){
                            passport_no_issue_date = "";
                        }
                        String cdc_no_issue_date = json.getString("cdc_no_issue_date");
                        if(cdc_no_issue_date.equals("null")){
                            cdc_no_issue_date = "";
                        }
                        String cdc_no_issue_place = json.getString("cdc_no_issue_place");

                        Integer int_id = db.newIntegerId("person");
                        int exist = db.GetCount("person", " WHERE person_id='"+id+"'");
                        if(exist == 0){
                            db.addPerson(int_id, id, code_person, lname, fname, mname, st_address, city_id, province_id, phone, mobile, st_address_province, phone_province, email, gender, civ_status, birth_date, birth_place, spouse_name, children, father_name, mother_name, notes, active, date_reg, compman_id, company_id, amt_paid, rank_id, vessel_officer, dept, vessel_id, course_id, school_id, school_admin, officer_type, batch_no, pct_done, days_on_board, "", passport_no, cdc_no, indos_no, height, weight, enrtry_date, marks, blood_group, emergency_contact_name, emergency_contact_address, emergency_contact_no, emergency_relationship, nationality, passport_no_issue_date, cdc_no_issue_date, cdc_no_issue_place, full_name, "N", "N", "", "", "", "", "","","", "", "", "");

                            Log.d("QUERY", "INSERT INTO(id, person_id, code_person, lname, fname, mname, st_address, city_id, province_id, phone, mobile, st_address_province, phone_province, email, gender, civ_status, birth_date, birth_place, spouse_name, children, father_name, mother_name, notes, active, date_reg, compman_id, company_id, amt_paid, rank_id, vessel_officer, dept, vessel_id, course_id, school_id, school_admin, officer_type, batch_no, pct_done, days_on_board, logged_in, last_login, full_name, photo_file, w_fr) " +
                                    "VALUES ("+int_id+", "+id+", "+ code_person+", "+lname+", "+ fname+", "+mname+", "+ st_address+", "+city_id+", "+province_id+", "+phone+", "+mobile+", "+st_address_province+", "+phone_province+", "+email+", "+gender+", "+civ_status+", "+birth_date+", "+birth_place+", "+spouse_name+", 0, "+father_name+", "+mother_name+", "+notes+", "+active+", "+date_reg+", "+compman_id+", "+company_id+", 0, "+rank_id+", "+vessel_officer+", "+dept+", "+vessel_id+", "+course_id+", "+school_id+", "+school_admin+", "+officer_type+", "+batch_no+", "+pct_done+", "+days_on_board+", '', "+last_login+", "+full_name+", '', 'Y')");

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            return;
        }
    }

    //******** DOWNLOAD DownloadVessel********
    private class DownloadVessel extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadVessel(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name=&login_pass=&table=vessel&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name=&login_pass=&table=vessel&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {
                        json = jArray.getJSONObject(i);

                        String vessel_id = json.getString("id");
                        String name_vessel = json.getString("name_vessel");
                        name_vessel = URLDecoder.decode(name_vessel);
                        String owner_company_id = json.getString("owner_company_id");
                        String operator_company_id = json.getString("operator_company_id");
                        String year_built = json.getString("year_built");
                        String flag_registry_id = json.getString("flag_registry_id");
                        String hp = json.getString("hp");
                        String kw = json.getString("kw");
                        String grt = json.getString("grt");
                        String trade_type = json.getString("trade_type");
                        trade_type = URLDecoder.decode(trade_type);
                        String imo_number = json.getString("imo_number");
                        imo_number = URLDecoder.decode(imo_number);
                        String call_sign = json.getString("call_sign");
                        call_sign = URLDecoder.decode(call_sign);
                        String vessel_type_id = json.getString("vessel_type_id");
                        String dp = json.getString("dp");
                        String ice_class = json.getString("ice_class");
                        String motor = json.getString("motor");
                        String st = json.getString("st");
                        String gt = json.getString("gt");

                        int exist = db.GetCount("vessel", " WHERE vessel_id='"+vessel_id+"'");
                        if(exist == 0){
                            Integer int_id = db.newIntegerId("vessel");
                            db.addVessel(vessel_id , int_id, name_vessel, owner_company_id, operator_company_id, year_built, flag_registry_id, hp, kw, grt, trade_type, imo_number, call_sign, vessel_type_id, dp, ice_class, motor, st, gt);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            return;
        }
    }

    //******** DOWNLOAD DownloadVesselType********
    private class DownloadVesselType extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadVesselType(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name=&login_pass=&table=vessel_type&person_id=" + person_id);
            Log.d("QUERY", url +"connection.php?login_name=&login_pass=&table=vessel_type&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("QUERY", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("QUERY", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("QUERY", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {

                        json = jArray.getJSONObject(i);

                        String vessel_type_id = json.getString("id");
                        String desc_vessel_type = json.getString("desc_vessel_type");
                        desc_vessel_type = URLDecoder.decode(desc_vessel_type);

                        int exist = db.GetCount("vessel_type", " WHERE vessel_type_id='"+vessel_type_id+"'");
                        if(exist == 0) {

                            Integer int_id = db.newIntegerId("vessel_type");

                            db.addVesselType(vessel_type_id , int_id, desc_vessel_type);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            return;
        }
    }

    //******** DOWNLOAD DownloadPersonTo ********
    private class DownloadPersonTo extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonTo(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name=&login_pass=&table=person_to&person_id=" + person_id);
            Log.d("QUERY", url +"connection.php?login_name=&login_pass=&table=person_to&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("QUERY", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("QUERY", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("QUERY", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {

                        json = jArray.getJSONObject(i);

                        String person_to_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String date_signed = json.getString("date_signed");
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);
                        String vessel_id = json.getString("vessel_id");

                        int exist = db.GetCount("person_to", " WHERE person_to_id='"+person_to_id+"'");
                        if(exist == 0) {
                            Integer int_id = db.newIntegerId("person_to");
                            db.addPersonTo(person_to_id , int_id, person_id, date_signed, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, vessel_id);
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            return;
        }
    }

    //******** DOWNLOAD DownloadPersonBridgeWatch ********
    private class DownloadPersonCe extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonCe(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name=&login_pass=&table=person_ce&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name=&login_pass=&table=person_ce&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {

                        json = jArray.getJSONObject(i);

                        String person_ce_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String vessel_id = json.getString("vessel_id");
                        String comments = json.getString("comments");
                        comments = URLDecoder.decode(comments);
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);

                        int exist = db.GetCount("person_ce", " WHERE person_ce_id='"+person_ce_id+"'");
                        if(exist == 0) {
                            Integer int_id = db.newIntegerId("person_ce");
                            db.addPersonCe(person_ce_id , int_id, person_id, vessel_id, comments, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks);
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            return;
        }
    }

    //******** DOWNLOAD DownloadPersonInspect ********
    private class DownloadPersonInspect extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonInspect(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name=&login_pass&table=person_inspect&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name=&login_pass=&table=person_inspect&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {

                        json = jArray.getJSONObject(i);

                        String person_inspect_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String comments = json.getString("comments");
                        comments = URLDecoder.decode(comments);
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);
                        String company_id = json.getString("company_id");
                        String vessel_id = json.getString("vessel_id");

                        int exist = db.GetCount("person_inspect", " WHERE person_inspect_id='"+person_inspect_id+"'");
                        if(exist == 0) {
                            Integer int_id = db.newIntegerId("person_inspect");
                            db.addPersonInspect(person_inspect_id , int_id, person_id, comments, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, company_id, vessel_id);
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            return;

        }
    }

    //******** DOWNLOAD DownloadPersonSafety ********
    private class DownloadPersonSafety extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonSafety(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name=&login_pass=&table=person_safety&person_id=" + person_id);
            Log.d("QUERY", "PERSON SAFETY " + url +"connection.php?login_name=&login_pass=&table=person_safety&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("QUERY", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("QUERY", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("QUERY", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {

                        json = jArray.getJSONObject(i);

                        String person_safety_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String safety_id = json.getString("safety_id");
                        String date_completed = json.getString("date_completed");
                        String ship_id = json.getString("ship_id");

                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);
                        String na = json.getString("na");

                        int exist = db.GetCount("person_safety", " WHERE person_id='"+person_id+"' AND safety_id = '"+safety_id+"' AND ship_id = '"+ship_id+"'");
                        if(exist == 0) {
                            Integer int_id = db.newIntegerId("person_safety");
                            Log.d("QUERY", " PS " + person_safety_id);
                            db.addPersonSafety(person_safety_id , int_id, person_id, safety_id, date_completed, ship_id, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, na);
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            return;
        }
    }

    //******** DOWNLOAD DownloadPersonTask ********
    private class DownloadPersonTask extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonTask(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name=&login_pass&table=person_task&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name=&login_pass=&table=person_task&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {

                        json = jArray.getJSONObject(i);

                        String person_task_id = json.getString("id");
                        String task_id = json.getString("task_id");
                        String completed = json.getString("completed");
                        String passed = json.getString("passed");

                        String answers = json.getString("answers");
                        answers = URLDecoder.decode(answers);
                        String checked_by_id = json.getString("checked_by_id");
                        String app_by_id = json.getString("app_by_id");
                        String date_checked = json.getString("date_checked");
                        String date_app = json.getString("date_app");
                        String officer_remarks = json.getString("officer_remarks");
                        officer_remarks = URLDecoder.decode(officer_remarks);
                        String app_remarks = json.getString("app_remarks");
                        app_remarks = URLDecoder.decode(app_remarks);

                        int exist = db.GetCount("person_task", " WHERE task_id='"+task_id+"' AND person_id= '"+person_id+"'");
                        if(exist != 0) {
                            //Integer int_id = db.newIntegerId("person_inspect");
                            db.execQuery("UPDATE person_task SET checked_by_id ='"+checked_by_id+"', " +
                                    "officer_remarks = '"+officer_remarks+"', date_checked = '"+date_checked+"', " +
                                    "passed = '"+passed+"', app_by_id ='"+app_by_id+"', date_app = '"+date_app+"', " +
                                    "app_remarks = '"+app_remarks+"' WHERE task_id = '"+task_id+"' " +
                                    "AND person_id = '"+person_id+"'");
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            return;

        }
    }

    //******** DOWNLOAD DownloadPersonTrbSubComp ********
    private class DownloadPersonTrbSubComp extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        public DownloadPersonTrbSubComp(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpClient myClient = new DefaultHttpClient();

            HttpPost myConnection = new HttpPost(url +"connection.php?login_name=&login_pass&table=person_trb_sub_competence&person_id=" + person_id);
            Log.d("CONNECT", url +"connection.php?login_name=&login_pass=&table=person_trb_sub_competence&person_id=" + person_id);

            try {
                response = (HttpResponse) myClient.execute(myConnection);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
                err_message = "Connected";
                Log.d("CONNECT", "" + response);

            } catch (ClientProtocolException e) {
                err_message = "Cannot connect to server.";
                e.printStackTrace();
                Log.d("CONNECT", "" + response);
            } catch (IOException e) {
                e.printStackTrace();
                err_message = "Sorry! Something went wrong." + e;
                Log.d("CONNECT", "" + response);
            }

            if(str != null){
                JSONArray jArray = null;
                try {
                    jArray = new JSONArray(str);
                    for (int i = 0; i < jArray.length(); i++) {

                        json = jArray.getJSONObject(i);

                        String person_trb_sub_competence_id = json.getString("id");
                        String person_id = json.getString("person_id");
                        String trb_sub_competence_id = json.getString("trb_sub_competence_id");
                        String checked_by_id = json.getString("checked_by_id");
                        String date_checked = json.getString("date_checked");
                        String checked_remarks = json.getString("checked_remarks");
                        checked_remarks = URLDecoder.decode(checked_remarks);

                        int exist = db.GetCount("person_trb_sub_competence", " WHERE trb_sub_competence_id='"+trb_sub_competence_id+"' AND person_id= '"+person_id+"'");
                        if(exist != 0) {
                            //Integer int_id = db.newIntegerId("person_inspect");
                            db.execQuery("UPDATE person_trb_sub_competence SET checked_by_id ='"+checked_by_id+"', " +
                                    "checked_remarks = '"+checked_remarks+"', date_checked = '"+date_checked+"'" +
                                    "WHERE trb_sub_competence_id = '"+trb_sub_competence_id+"' " +
                                    "AND person_id = '"+person_id+"'");
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        protected void onPostExecute(Void result)
        {
            if(mBuilder != null){
                mBuilder.setContentText("Backup completed")
                        // Removes the progress bar
                        .setProgress(0,0,false);
                mNotificationManager.notify(1, mBuilder.build());
            }

        }
    }

    void downloadFile(Context context, String filename, String table){


        try {
            URL url = null;
            if(table.equals("person_task_file")){
                url = new URL(dwnload_file_path + filename);
            }else{
                url = new URL(dl_photo_file + filename);
            }

            Log.d("RESULT - IMG", "" + url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            //connect
            urlConnection.connect();

            //set the path where we want to save the file
            File SDCardRoot = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //create a new file, to save the downloaded file
            File file = new File(SDCardRoot, filename);

            Log.d("RESULT", "" + SDCardRoot);

            FileOutputStream fileOutput = new FileOutputStream(file);

            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            //this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();
            downloadedSize = 0;

            Log.d("RESULT ", "DL Total Size" + totalSize);
            /*runOnUiThread(new Runnable() {
                public void run() {

                }
            });*/

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                // update the progressbar //
                //runOnUiThread(new Runnable() {
                 //   public void run() {
                //        float per = ((float)downloadedSize/totalSize) * 100;
                 //       Log.d("RESULT ", "DL Size" + downloadedSize + "KB / " + totalSize + "KB (" + (int)per + "%)");
                //    }
                //});
            }
            //close the output stream when complete //
            fileOutput.close();
            //runOnUiThread(new Runnable() {
            //    public void run() {
            //        // pb.dismiss(); // if you want close it..
            //    }
            //});

        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            showError("Error : IOException " + e);
            e.printStackTrace();
        }
        catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }
    }

    void showError(final String err){
        Log.d("ERROR", err);
        /*runOnUiThread(new Runnable() {
            public void run() {
                // Toast.makeText(DownloadFromServerActivity.this, err, Toast.LENGTH_LONG).show();
            }
        });*/
    }

    public int uploadImage(Context context, String sourceFileUri) {
        Log.d("Result - IMAGE", sourceFileUri);
        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/"+ sourceFileUri);
        Log.d("RESULT", "Source File :" + context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/"+ sourceFileUri);
        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :" +uploadFilePath + "" + sourceFileUri);
            return 0;
        }
        else
        {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                Log.d("RESULT - URL ", " " + upLoadServerUri);

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

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.d("QUERY", "HTTP Response is : "  + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    Log.d("RESULT: ", "File Upload Complete.");
                    //uploadImage2(context, sourceFileUri);
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.d("RESULT: ", "MalformedURLException Exception : check script url." + ex);

                Log.e("RESULT", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("RESULT:","Got Exception : see logcat. Exception : "  + e.getMessage() );

            }
            return serverResponseCode;

        } // End else block
    }

    public int uploadImage2(Context context, String sourceFileUri) {
        Log.d("Result - Img", sourceFileUri);
        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/"+ sourceFileUri);
        Log.d("RESULT", "Source File :" + context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/"+ sourceFileUri);
        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :" +uploadFilePath + "" + sourceFileUri);
            return 0;
        }
        else
        {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(server2);
                Log.d("RESULT - URL ", " " + server2);

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

                Log.i("uploadFile", "HTTP Response is : "  + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    Log.d("RESULT: ", "File Upload Complete.");
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.d("RESULT: ", "MalformedURLException Exception : check script url." + ex);

                Log.e("RESULT", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("RESULT:","Got Exception : see logcat. Exception : "  + e.getMessage() );

            }
            return serverResponseCode;

        } // End else block
    }

    public int uploadPhotoFileImage(Context context, String sourceFileUri) {

        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/"+ sourceFileUri);
        if (!sourceFile.isFile()) {
            Log.d("RESULT", "Source File not exist :" +uploadFilePath + "" + sourceFileUri);
            return 0;
        }
        else
        {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri2);

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

                Log.i("RESULT", "HTTP Response is : "  + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    Log.d("RESULT: ", "File Upload Complete.");
                    //uploadImage2(sourceFileUri);
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.d("RESULT: ", "MalformedURLException Exception : check script url." + ex);

                Log.e("RESULT", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("RESULT:","Got Exception : see logcat. Exception : "  + e.getMessage() );

            }
            return serverResponseCode;

        } // End else block
    }

}
