package com.elosoftbiz.etrb_trmf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MenuDeck extends Activity {
    NavigationView navigationView;
    ProgressDialog pd;
    DatabaseHelper db;

    public MenuDeck(NavigationView navigationView, final Context context, final DrawerLayout mDrawerLayout){
        db = new DatabaseHelper(context);
        // ****** START OF NAVIGATION
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        Intent main = new Intent(context, MainActivity.class);
                        context.startActivity(main);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;
                    case R.id.nav_stcw:
                        Intent stcw = new Intent(context, STRActivity.class);
                        context.startActivity(stcw);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;
                    case R.id.nav_otr:
                        Intent otr = new Intent(context, OTRActivity.class);
                        context.startActivity(otr);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;
                    case R.id.nav_guidelines:
                        Intent guide = new Intent(context, GuidelinesActivity.class);
                        context.startActivity(guide);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;
                    case R.id.nav_sea_service:
                        Intent sea_service = new Intent(context, SeaServiceActivity.class);
                        context.startActivity(sea_service);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;
                    case R.id.nav_ship_particular:
                        Intent ship_particular = new Intent(context, ShipParticularActivity.class);
                        context.startActivity(ship_particular);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;
                    case R.id.nav_crew_list:
                        Intent crew_list = new Intent(context, PersonCrewListActivity.class);
                        context.startActivity(crew_list);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;
                    case R.id.nav_person_inspect:
                        Intent person_inspect = new Intent(context, PersonInspectActivity.class);
                        context.startActivity(person_inspect);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;
                    case R.id.nav_person_to:
                        Intent person_to = new Intent(context, StoReviewActivity.class);
                        context.startActivity(person_to);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;

                    case R.id.nav_person_basic_training:
                        Intent prof_training = new Intent(context, ProfessionalTrainingActivity.class);
                        context.startActivity(prof_training);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;

                    case R.id.nav_person_safety:
                        Intent safety = new Intent(context, ShipboardSafetyActivity.class);
                        context.startActivity(safety);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;

                    case R.id.nav_person_emergancy:
                        Toast.makeText(context, "Currently not available", Toast.LENGTH_LONG).show();
                        /*
                        Intent person_emergancy = new Intent(context, PersonEmergencyActivity.class);
                        context.startActivity(person_emergancy);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();

                         */
                        break;

                    case R.id.nav_person_work:
                        Intent person_work = new Intent(context, PersonSafeWorkActivity.class);
                        context.startActivity(person_work);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;
                    case R.id.nav_person_regulation:
                        Intent internationl_reg = new Intent(context, InternationalRegActivity.class);
                        context.startActivity(internationl_reg);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        finish();
                        break;

                    case R.id.nav_task_summary:
                        pd = new ProgressDialog(context);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage("Loading. Please wait .... ");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        Intent task_summary = new Intent(context, TaskSummaryActivity.class);
                        context.startActivity(task_summary);

                        ((Activity)context).finish();
                        break;

                    case R.id.nav_tasks:
                        pd = new ProgressDialog(context);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.setMessage("Loading. Please wait .... ");
                        pd.setIndeterminate(true);
                        pd.setCancelable(false);
                        pd.show();

                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        Intent tasks = new Intent(context, ListCompetenceActivity.class);
                        tasks.putExtra("task_type", "OPERATIONAL TASK");
                        context.startActivity(tasks);
                        ((Activity)context).finish();
                        break;

                    case R.id.nav_person_steering:
                        Intent steering = new Intent(context, SteeringActivity.class);
                        context.startActivity(steering);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;
                    case R.id.nav_person_journal:
                        Intent watchkeeping = new Intent(context, BridgeWatchkeepingActivity.class);
                        context.startActivity(watchkeeping);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        finish();
                        break;
                    case R.id.nav_person_project_work:
                        Intent person_project_work = new Intent(context, PersonProjectWorkActivity.class);
                        context.startActivity(person_project_work);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;
                    case R.id.nav_officer_assign:
                        Intent officer_assign = new Intent(context, OfficerAssignmentActivity.class);
                        context.startActivity(officer_assign);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;

                    case R.id.nav_cadet:
                        Intent cadet = new Intent(context, CadetParticularActivity.class);
                        context.startActivity(cadet);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;

                    case R.id.nav_search:
                        Intent search = new Intent(context, SearchTaskActivity.class);
                        context.startActivity(search);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;
                    case R.id.nav_sync:
                        Intent sync = new Intent(context, BackupActivity.class);
                        context.startActivity(sync);
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        ((Activity)context).finish();
                        break;

                    case R.id.nav_about_us:
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("About " + context.getString(R.string.app_title));
                        LinearLayout linearLayoutInfo = new LinearLayout( context );
                        LinearLayout.LayoutParams layoutParamInfo = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        linearLayoutInfo.setPadding( 20, 10, 10, 20 );
                        linearLayoutInfo.setLayoutParams( layoutParamInfo );

                        TextView version = new TextView( context );
                        String versionName = BuildConfig.VERSION_NAME;

                        version.setText("App Version : " + versionName + "\n" +
                                "OS Version : Android Only\n" +
                                "Req. Min. OS Ver. : Marsmallow (6.0)" + "\n" +
                                "Device : Android Device Only\n" +
                                "Recommended Device : Any android tablets\n\n" +
                                "Server : "+ context.getString(R.string.url));
                        version.setTextColor(ContextCompat.getColor(context, R.color.black));
                        version.setLayoutParams(layoutParamInfo);
                        version.setTextColor(Color.BLACK);
                        version.setPadding(20, 5,20,5);
                        linearLayoutInfo.addView( version );
                        builder.setView( linearLayoutInfo );

                        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        final AlertDialog alertDialogInfo = builder.create();
                        alertDialogInfo.show();

                        alertDialogInfo.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                        alertDialogInfo.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));

                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_reset:
                        AlertDialog.Builder alertDialogBuilder3 = new AlertDialog.Builder(context);
                        alertDialogBuilder3.setMessage("Enter reset code");

                        LinearLayout linearLayout = new LinearLayout(context);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        layoutParams.setMargins(20,15,20,10);
                        linearLayout.setLayoutParams(layoutParams);

                        final EditText editText = new EditText(context);
                        editText.setBackgroundResource(R.drawable.linearborder);
                        editText.setTextSize(15);
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setTextColor(Color.BLACK);
                        editText.setPadding(20, 20,20,20);
                        editText.setLayoutParams(layoutParams);
                        linearLayout.addView(editText);
                        alertDialogBuilder3.setView(linearLayout);

                        alertDialogBuilder3.setPositiveButton("SUBMIT",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        String resetCode = editText.getText().toString();
                                        if(resetCode.equals("etrb2021")){
                                            AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(context);
                                            alertDialogBuilder2.setMessage("Are you sure you want to reset this app? This action is cannot be undone");
                                            alertDialogBuilder2.setPositiveButton("CONFIRM",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface arg0, int arg1) {
                                                            db.query("DELETE FROM backup_history");
                                                            db.query("DELETE FROM backup_item");

                                                            db.query("DELETE FROM abbreviation");
                                                            db.query("DELETE FROM basic_training");
                                                            db.query("DELETE FROM country");
                                                            db.query("DELETE FROM global_sys");
                                                            db.query("DELETE FROM login");
                                                            db.query("DELETE FROM person");
                                                            db.query("DELETE FROM person_basic_training");
                                                            db.query("DELETE FROM person_bridge_watch");
                                                            db.query("DELETE FROM person_ce");
                                                            db.query("DELETE FROM person_education_d");
                                                            db.query("DELETE FROM person_education_h");
                                                            db.query("DELETE FROM person_inspect");
                                                            db.query("DELETE FROM person_material");
                                                            db.query("DELETE FROM person_muster");
                                                            db.query("DELETE FROM person_officer");
                                                            db.query("DELETE FROM person_port_watch");
                                                            db.query("DELETE FROM person_program");
                                                            db.query("DELETE FROM person_regulation");
                                                            db.query("DELETE FROM person_safety");
                                                            db.query("DELETE FROM person_special_course");
                                                            db.query("DELETE FROM person_steer_task");
                                                            db.query("DELETE FROM person_steer_topic");
                                                            db.query("DELETE FROM person_steering");
                                                            db.query("DELETE FROM person_task");
                                                            db.query("DELETE FROM person_task_file");
                                                            db.query("DELETE FROM person_trb_topic");
                                                            db.query("DELETE FROM person_trb_sub_competence");
                                                            db.query("DELETE FROM person_to");
                                                            db.query("DELETE FROM person_vessel");
                                                            db.query("DELETE FROM program");
                                                            db.query("DELETE FROM safety");
                                                            db.query("DELETE FROM shipboard");
                                                            db.query("DELETE FROM vessel");
                                                            db.query("DELETE FROM vessel_type");

                                                            Intent intent1 = new Intent(context, MainActivity.class);
                                                            context.startActivity(intent1);
                                                            ((Activity)context).finish();
                                                            Toast.makeText(context, "You successfully reset this app.", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                            alertDialogBuilder2.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //finish();
                                                }
                                            });
                                            AlertDialog alertDialog2 = alertDialogBuilder2.create();
                                            alertDialog2.show();

                                            alertDialog2.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                                            alertDialog2.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                                            alertDialog2.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(Color.WHITE);
                                            alertDialog2.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(Color.WHITE);


                                        }else{
                                            Toast.makeText(context, "Invalid Reset Code. Please try again.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                });

                        alertDialogBuilder3.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        });
                        AlertDialog alertDialog3 = alertDialogBuilder3.create();
                        alertDialog3.show();

                        alertDialog3.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                        alertDialog3.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black));

                        alertDialog3.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                        alertDialog3.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));


                        break;
                    case R.id.nav_exit:
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        alertDialogBuilder.setMessage("Are you sure you want to quit?");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                ((Activity)context).finish();

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
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black));
                        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.white));

                        break;


                }return false;
            }
        });
    }
}
