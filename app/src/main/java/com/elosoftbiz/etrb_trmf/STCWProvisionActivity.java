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

public class STCWProvisionActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_stcwprovision);

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

        DatabaseHelper db = new DatabaseHelper(this);
        String dept = db.getFieldString("dept", "vessel_officer = 'N'", "person");
        //CHANGE MENU HERE
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
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


        tv_content = findViewById(R.id.tv_content);
        String text;
        if(dept.equals("DECK")){
            text = "<html><body>" +
                    "<h3 style=\"margin-top:5px;margin-left:10px;margin-right:10px;\"><u><b>STCW PROVISIONS</b></u></h3>";
            text += "<p style=\"margin-left:10px;margin-right:10px;\"><u>Extracts from STCW 2010. Section B-II/1</u></p><br/>";
            text += "<p align=\"center\" style=\"margin-left:20px;margin-right:20px;\"><b>CHAPTER II<br/>" +
                    "Guidance regarding the master and the deck department</b></p>";
            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\"><b>Section B-II/1</b><br/><i>Guidance regarding the certification of officers in charge of a navigational watch on ships of 500 gross tonnage or more</i></p>";
            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\"><b>Training</b><br/>1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Every candidate for certification as officer in charge of a navigational watch should have completed a planned and structured programme of training designed to assist a prospective officer to achieve the standard of competence in accordance with table A-II/1.</p>";
            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\">2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The structure of the programme of training should be set out in a training plan which clearly expresses, for all parties involved, the objectives of each stage of training on board and ashore. It is important that the prospective officer, tutors, ships' staff and company personnel are clear about the competences which are to be achieved at the end of the programme and how they are to be achieved through a combination of education, training and practical experience on board and ashore.</p>";
            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\">3&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The mandatory periods of seagoing service are of prime importance in learning the job of being a ship's officer and in achieving the overall standard of competence required. Properly planned and structured, the periods of seagoing service will enable prospective officers to acquire and practice skills and will offer opportunities for competences achieved to be demonstrated and assessed.</p>";
            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\">4&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Where the seagoing service forms part of an approved training programme, the following principles should be observed:</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The programme of onboard training should be an integral part of the overall training plan.</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The programme of onboard training should be managed and coordinated by the company which manages the ship on which the seagoing service is to be performed</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.3&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The prospective officer should be provided with a training record book* to enable a comprehensive record of practical training and experience at sea to be maintained. The training record book should be laid out in such a way that it can provide detailed information about the tasks and duties which should be undertaken and the progress towards their completion. Duly completed, the record book will provide unique evidence that a structured programme of onboard training has been completed which can be taken into account in the process of evaluating competence for the issue of a certificate.</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.4&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;At all times, the prospective officer should be aware of two identifiable individuals who are immediately responsible for the management of the programme of onboard training. The first of these is a qualified seagoing officer, referred to as the \"shipboard training officer”, who, under the authority of the master, should organize and supervise the programme of training for the duration of each voyage. The second should be a person nominated by the company, referred to as the \"company training officer\", who should have an overall responsibility for the training programme and for coordination with colleges and training institutions.</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.5&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The company should ensure that appropriate periods are set aside for completion of the programme of onboard training within the normal operational requirements of the ship.</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\"><b>Roles and responsibilities</b><br/>5&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The following section summarizes the roles and responsibilities of those individuals involved in organizing and conducting onboard training:</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The company training officer should be responsible for:</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.1.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;overall administration of the programme of training;</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.1.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;monitoring the progress of the prospective officer throughout; and</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.1.3&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;issuing guidance as required and ensuring that all concerned with the training programme play their parts.</p>";

            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The shipboard training officer should be responsible for:</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.2.1 &nbsp;&nbsp;&nbsp;&nbsp;  organizing the programme of practical training at sea;</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.2.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ensuring, in a supervisory capacity, that the training record book is properly maintained and that all other requirements are fulfilled, and</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.2.3&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;making sure, so far as is practicable, that the time the prospective officer spends on board is as useful as possible in terms of training and experience, and is consistent with the objectives of the training programme, the progress of training and the operational constraints of the ship</p>";

            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.3&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The master's responsibilities should be to:</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.3.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;provide the link between the shipboard training officer and the company training officer ashore;</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.3.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fulfil the role of continuity if the shipboard training officer is relieved during the voyage, and</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.3.3&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ensure that all concerned are effectively carrying out the onboard training programme.</p>";

            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.4&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The prospective officer's responsibilities should be to:</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.4.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;follow diligently the programme of training as laid down,</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.4.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;make the most of the opportunities presented, be they in or outside working hours, and</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.4.3&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;keep the training record book up to date and ensure that it is available at all times for scrutiny.</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\"><b>Induction</b><br/>6&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;At the beginning of the programme and at the start of each voyage on a different ship, prospective officers should be given full information and guidance as to what is expected of them and how the training programme is to be organized Induction presents the opportunity to brief prospective officers about important aspects of the tasks they will be undertaking, with particular regard to sale working practices and protection of the marine environment.</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\"><b>Shipboard programme of training</b><br/>7&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The training record book should contain, amongst other things, a number of training tasks or duties which should be undertaken as part of the approved programme of onboard training. Such tasks and duties should relate to at least the following areas:</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;steering systems;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;general seamanship;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.3&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mooring, anchoring and port operations;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.4&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;life-saving and fire-fighting appliances;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.5&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;systems and equipment;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.6&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;cargo work;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.7&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;bridge work and watchkeeping; and</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.8&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;engine-room familiarization</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\">8&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;It is extremely important that the prospective officer is given adequate opportunity for supervised bridge watchkeeping experience, particularly in the later stages of the onboard training programme. </p>";
            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\">9&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The performance of the prospective officers in each of the tasks and duties itemized in the training record book should be initialed by a qualified officer when, in the opinion of the officer concerned, a prospective officer has achieved a satisfactory standard of proficiency. It is important to appreciate that a prospective officer may need to demonstrate ability on several occasions before a qualified officer is confident that a satisfactory standard has been achieved.</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\"><b>Monitoring and reviewing</b><br/>10&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Guidance and reviewing are essential to ensure that prospective officers are fully aware of the progress they are making and to enable them to join in decisions about their future programme. To be effective, reviews should be linked to information gained through the training record book and other sources as appropriate. The training record book should be scrutinized and endorsed formally by the master and the shipboard training officer at the beginning, during and at the end of each voyage. The training record book should also be examined and endorsed by the company training officer between voyages.</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\"><b>Assessment of abilities and skills in navigational watchkeeping</b><br/>11&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A candidate for certification who is required to have received special training and assessment of abilities and skills in navigational watchkeeping duties should be required to provide evidence, through demonstration either on a simulator or on board ship as part of an approved programme of shipboard training, that the skills and ability to perform as officer in charge of a navigational watch in at least the following areas have been acquired, namely to:</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;prepare for and conduct a passage, including:</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.1.1 &nbsp;&nbsp; interpreting and applying information obtained from charts;</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.1.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fixing position in coastal waters;</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.1.3&nbsp;&nbsp; applying basic information obtained from tide tables and other nautical publications;</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.1.4&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;checking and operating bridge equipment;</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.1.5&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;checking magnetic and gyro-compasses;</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.1.6 assessing available meteorological information;</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.1.7&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;using celestial bodies to fix position;</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.1.8&nbsp;&nbsp; determining the compass error by celestial and terrestrial means; and</p>";
            text += "<p align=\"justify\" style=\"margin-left:80px;margin-right:20px;\">.1.9&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;performing calculations for sailings of up to 24 hours;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;operate and apply information obtained from electronic navigation systems;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.3&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;operate radar, ARPA and ECDIS and apply radar information for navigation and collision avoidance;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.4&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;operate propulsion and steering systems to control heading and speed;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.5&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;implement navigational watch routines and procedures:</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.6&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;implement the manoeuvres required for rescue of persons overboard;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.7&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;initiate action to be taken in the event of an imminent emergency situation (e.g., fire, collision, stranding) and action in the immediate aftermath of an emergency;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.8&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;initiate action to be taken in event of malfunction or failure of major items of equipment or plant(e.g., steering gear, power, navigation systems);</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.9&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;conduct radiocommunications and visual and sound signalling in normal and emergency situations; and</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.10&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;monitor and operate safety and alarm systems, including internal communications.</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\">12&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Assessment of abilities and skills in navigational watchkeeping should:</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;be made against the criteria for evaluating competence for the function of navigation set out in table A-II/1;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ensure that the candidate performs navigational watchkeeping duties in accordance with the Principles to be observed in keeping a safe navigational watch (section A-VIII/2, part 4-1) and the Guidance on keeping a navigational watch (section B-VIII/2. part 4-1).</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\"><b>Evaluation of competence</b><br/>13&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The standard of competence to be achieved for certification as officer in charge of a navigational watch is set out in table A-II/1. The standard specifies the knowledge and skill required and the application of that knowledge and skill to the standard of performance required on board ship.</p>";
            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\">14&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Scope of knowledge is implicit in the concept of competence. Assessment of competence should, therefore, encompass more than the immediate technical requirements of the job, the skills and tasks to be performed, and should reflect the broader aspects needed to meet the full expectations of competent performance as a ship's officer. This includes relevant knowledge, theory, principles and cognitive skills which, to varying degrees, underpin all levels of competence. It also encompasses proficiency in what to do, how and when to do it, and why it should be done. Properly applied, this will help to ensure that a candidate can:</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;work competently in different ships and across a range of circumstances,</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;anticipate, prepare for and deal with contingencies; and</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.3&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;adapt to now and changing requirements.</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\">15&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The criteria for evaluating competence (column 4 of table A-II/1) identify, primarily in outcome terms, the essential aspects of competent performance. They are expressed so that assessment of a candidate's performance can be made against them and should be adequately documented in the training record book.</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\">16&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Evaluation of competence is the process of:</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;collecting sufficient valid and reliable evidence about the candidate's knowledge, understanding and proficiency to accomplish the tasks, duties and responsibilities listed in column 1 of table A-II/1; and </p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;judging that evidence against the criteria specified in the standard.</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\">17&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The arrangements for evaluating competence should be designed to take account of different methods of assessment which can provide different types of evidence about candidates' competence, e.g.:</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;direct observation of work activities (including seagoing service);</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;skills/proficiency/competency tests;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.3&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;projects and assignments;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.4&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;evidence from previous experience; and</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.5&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;written, oral and computer-based questioning techniques**</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\">18&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;One or more of the first four methods listed should almost invariably be used to provide evidence of ability, in addition to appropriate questioning techniques to provide evidence of supporting knowledge and understanding.</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\"><b>Training in celestial navigation</b><br/>19&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The following areas summarize the recommended training in celestial navigation:</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;correctly adjust sextant for adjustable errors;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;determine corrected reading of the sextant altitude of celestial bodies;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.3&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;accurate sight reduction computation, using a preferred method;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.4&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;calculate the time of meridian altitude of the sun;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.5&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;calculate latitude by Polaris or by meridian altitude of the sun;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.6&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;accurate plotting of position line(s) and position fixing;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.7&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;determine time of visible rising/setting sun by a preferred method;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.8&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;identify and select the most suitable celestial bodies in the twilight period;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.9&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;determine compass error by azimuth or by amplitude, using a preferred method;</p>";
            text += "<p align=\"justify\" style=\"margin-left:50px;margin-right:20px;\">.10&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;nautical astronomy as required to support the required competence in paragraphs 19.1 to 19.9 above.</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;\">20&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Training in celestial navigation may include the use of electronic nautical almanac and celestial navigation calculation software.</p>";

            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;font-size:8px\"><br/>*The relevant IMO Model Course(s) and a simular document produced by the International Shipping Federation may be of assistance in the preparation of training record books.</p>";
            text += "<p align=\"justify\" style=\"margin-left:20px;margin-right:20px;font-size:8px\">**The relevant IMO Model Course(s) may be of assisstance in the preparation of courses.</p>";
            text += "</body></html>";
        }else{
            text = "<html><body>";
            text += "<h3 style=\"margin-top:5px;margin-left:10px;margin-right:10px;\"><u><b>STCW PROVISIONS</b></u></h3>";
            text += "<p style=\"margin-left:10px;margin-right:10px;\"><u>Extracts from STCW 2010. Section B-III/1</u></p><br/>";
            text += "<p align=\"center\" style=\"margin-left:20px;margin-right:20px;\"><b>CHAPTER III<br/>" +
                    "Guidance regarding the engine department</b></p>";
            text += "<p style=\"margin-left:10px;margin-right:10px;\"><br/><b>Section B-III/1</b></p>";
            text += "<p align='justify' style=\"margin-left:10px;margin-right:10px;\">Guidance regarding the certification of officers in charge of an engineering watch in a manned engine-room or as designated duty engineers in a periodically unmanned engine-room<br/><br/>" +
                    "1 In table A-III/1, the tools referred to should include hand tools, common measuring equipment, centre lathes, drilling machines, welding equipment and milling machines as appropriate.<br/>" +
                    "2 Training in workshop skills ashore can be carried out in a training institution or approved workshop.<br/>" +
                    "3 Onboard training should be adequately documented in the training record book by qualified assessors.<br/>" +
                    "</p>";
            text += "<p style=\"margin-left:10px;margin-right:10px;\"><br/><b>Section B-III/2</b></p>";
            text += "<p align='justify' style='margin-left:10px;margin-right:10px;'>Guidance regarding the certification of chief engineer officers and second engineer officers of ships powered by main propulsion machinery of 3,000 kW propulsion power or more </br> </br>(No provisions)<br/><br/>" +
                    "Guidance regarding training of engineering personnel having management responsibilities for the operation and safety of electrical power plant above 1,000 volts<br/><br/>" +
                    "1 Training of engineering personnel having management responsibilities for the operation and safety of electrical power plant of more than 1,000 V should at least include:<br/></p>";
            text += "<p align='justify' style=\"margin-left:25px;margin-right:10px;\">" +
                    ".1 the functional, operational and safety requirements for a marine high-voltage system;<br/><br/>" +
                    ".2 assignment of suitably qualified personnel to carry out maintenance and repair of high-voltage switchgear of various types;<br/><br/>" +
                    ".3 taking remedial action necessary during faults in a high-voltage system;<br/><br/>" +
                    ".4 producing a switching strategy for isolating components of a high-voltage system;<br/><br/>" +
                    ".5 selecting suitable apparatus for isolation and testing of high-voltage equipment;<br/><br/>" +
                    ".6 carrying out a switching and isolation procedure on a marine high-voltage system, complete with safety documentation; and<br/><br/>" +
                    ".7 performing tests of insulation resistance and polarization index on high-voltage equipment.<br/><br/>" +
                    "</p>";
            text += "<p style=\"margin-left:10px;margin-right:10px;\"><b>Section B-III/3</b></p>";
            text += "<p align='justify' style='margin-left:10px;margin-right:10px;'>Guidance regarding the certification of chief engineer officers and second engineer officers of ships powered by main propulsion machinery between 750 kW and 3,000 kW propulsion power <br/><br/>(No provisions)<br/><br/></p>";
            text += "<p style=\"margin-left:10px;margin-right:10px;\"><b>Section B-III/4</b></p>";
            text += "<p align='justify' style='margin-left:10px;margin-right:10px;'>Guidance regarding the training and certification of ratings forming part of a watch in a manned engine-room or designated to perform duties in a periodically unmanned engine-room<br/><br/>1 In addition to the requirements stated in section A-III/4 of this Code, Parties are encouraged, for safety reasons, to include the following items in the training of ratings forming part of an engineering watch:</p>";
            text += "<p align='justify' style='margin-left:25px;margin-right:10px;'>" +
                    ".1 a basic knowledge of routine pumping operations, such as bilge, ballast and cargo pumping systems;<br/><br/>" +
                    ".2 a basic knowledge of electrical installations and the associated dangers;<br/><br/>" +
                    ".3 a basic knowledge of maintenance and repair of machinery and tools used in the engine-room; and<br/><br/>" +
                    ".4 a basic knowledge of stowage and arrangements for bringing stores on board." +
                    "</p>";
            text += "<p style=\"margin-left:10px;margin-right:10px;\"><b>Section B-III/5</b></p>";
            text += "<p align='justify' style='margin-left:10px;margin-right:10px;'>Guidance regarding the certification of ratings as able seafarer engine <br/><br/>" +
                    "Onboard training should be documented in an approved training record book.</p>";
            text += "<p style=\"margin-left:10px;margin-right:10px;\"><b>Section B-III/6</b></p>";
            text += "<p align='justify' style='margin-left:10px;margin-right:10px;'>Guidance regarding training and certification for electro-technical officers <br/><br/>" +
                    "In addition to the requirements stated in table A-III/6 of this Code, Parties are encouraged to take into account resolution A.702(17) concerning radio maintenance guidelines for the Global Maritime Distress and Safety System (GMDSS) within their training programmes.</p>";
            text += "<p style=\"margin-left:10px;margin-right:10px;\"><b>Section B-III/7</b></p>";
            text += "<p align='justify' style='margin-left:10px;margin-right:10px;'>Guidance regarding training and certification for electro-technical ratings<br/><br/>(No provisions)</p>";
            text += "</body></html>";
        }

        tv_content.loadData(text, "text/html", "utf-8");
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(STCWProvisionActivity.this, MainActivity.class);
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
