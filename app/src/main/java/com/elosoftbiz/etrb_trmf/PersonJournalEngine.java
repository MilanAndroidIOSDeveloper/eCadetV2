package com.elosoftbiz.etrb_trmf;

public class PersonJournalEngine {
    Integer id;
    String person_journal_engine_id;
    String journal_date;
    String journal_time;
    String person_id;
    String vessel_id;
    String ship_position_lat;
    String fixing_method;
    String course_speed;
    String activities;
    String fo_cons;
    String average_rpm;
    String average_speed;
    String checked_by_id;
    String date_checked;
    String login_id;
    String last_update;
    String journal_time_to;
    String hrs;
    String port_depart;
    String port_dest;
    String ship_position_long;
    String ship_position_vicinity;
    String do_cons;

    public PersonJournalEngine(Integer id, String person_journal_engine_id, String journal_date, String journal_time, String person_id, String vessel_id, String ship_position_lat, String fixing_method, String course_speed, String activities, String fo_cons, String average_rpm, String average_speed, String checked_by_id, String date_checked, String login_id, String last_update, String journal_time_to, String hrs, String port_depart, String port_dest, String ship_position_long, String ship_position_vicinity, String do_cons){
        this.id = id;
        this.person_journal_engine_id = person_journal_engine_id;
        this.journal_date = journal_date;
        this.journal_time = journal_time;
        this.person_id = person_id;
        this.vessel_id = vessel_id;
        this.ship_position_lat = ship_position_lat;
        this.fixing_method = fixing_method;
        this.course_speed = course_speed;
        this.activities = activities;
        this.fo_cons = fo_cons;
        this.average_rpm = average_rpm;
        this.average_speed = average_speed;
        this.checked_by_id = checked_by_id;
        this.date_checked = date_checked;
        this.login_id = login_id;
        this.last_update = last_update;
        this.journal_time_to = journal_time_to;
        this.hrs = hrs;
        this.port_depart = port_depart;
        this.port_dest = port_dest;
        this.ship_position_long = ship_position_long;
        this.ship_position_vicinity = ship_position_vicinity;
        this.do_cons = do_cons;

    }

    public PersonJournalEngine(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_journal_engine_id() {
        return person_journal_engine_id;
    }

    public void setPerson_journal_engine_id(String person_journal_engine_id) {
        this.person_journal_engine_id = person_journal_engine_id;
    }

    public String getJournal_date() {
        return journal_date;
    }

    public void setJournal_date(String journal_date) {
        this.journal_date = journal_date;
    }

    public String getJournal_time() {
        return journal_time;
    }

    public void setJournal_time(String journal_time) {
        this.journal_time = journal_time;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getVessel_id() {
        return vessel_id;
    }

    public void setVessel_id(String vessel_id) {
        this.vessel_id = vessel_id;
    }

    public String getShip_position_lat() {
        return ship_position_lat;
    }

    public void setShip_position_lat(String ship_position_lat) {
        this.ship_position_lat = ship_position_lat;
    }

    public String getFixing_method() {
        return fixing_method;
    }

    public void setFixing_method(String fixing_method) {
        this.fixing_method = fixing_method;
    }

    public String getCourse_speed() {
        return course_speed;
    }

    public void setCourse_speed(String course_speed) {
        this.course_speed = course_speed;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public String getFo_cons() {
        return fo_cons;
    }

    public void setFo_cons(String fo_cons) {
        this.fo_cons = fo_cons;
    }

    public String getAverage_rpm() {
        return average_rpm;
    }

    public void setAverage_rpm(String average_rpm) {
        this.average_rpm = average_rpm;
    }

    public String getAverage_speed() {
        return average_speed;
    }

    public void setAverage_speed(String average_speed) {
        this.average_speed = average_speed;
    }

    public String getChecked_by_id() {
        return checked_by_id;
    }

    public void setChecked_by_id(String checked_by_id) {
        this.checked_by_id = checked_by_id;
    }

    public String getDate_checked() {
        return date_checked;
    }

    public void setDate_checked(String date_checked) {
        this.date_checked = date_checked;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getJournal_time_to() {
        return journal_time_to;
    }

    public void setJournal_time_to(String journal_time_to) {
        this.journal_time_to = journal_time_to;
    }

    public String getHrs() {
        return hrs;
    }

    public void setHrs(String hrs) {
        this.hrs = hrs;
    }

    public String getPort_depart() {
        return port_depart;
    }

    public void setPort_depart(String port_depart) {
        this.port_depart = port_depart;
    }

    public String getPort_dest() {
        return port_dest;
    }

    public void setPort_dest(String port_dest) {
        this.port_dest = port_dest;
    }

    public String getShip_position_long() {
        return ship_position_long;
    }

    public void setShip_position_long(String ship_position_long) {
        this.ship_position_long = ship_position_long;
    }

    public String getShip_position_vicinity() {
        return ship_position_vicinity;
    }

    public void setShip_position_vicinity(String ship_position_vicinity) {
        this.ship_position_vicinity = ship_position_vicinity;
    }

    public String getDo_cons() {
        return do_cons;
    }

    public void setDo_cons(String do_cons) {
        this.do_cons = do_cons;
    }
}
