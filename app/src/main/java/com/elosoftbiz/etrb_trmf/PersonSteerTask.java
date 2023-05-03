package com.elosoftbiz.etrb_trmf;

public class PersonSteerTask {
    Integer id;
    String person_steer_task_id, steer_task_id, person_id, vessel_id, completed, answers, passed, not_app, lat_long, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks;

    public PersonSteerTask(String person_steer_task_id, Integer id, String steer_task_id, String person_id, String vessel_id, String completed, String answers, String passed, String not_app, String lat_long, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks){
        this.person_steer_task_id=person_steer_task_id;
        this.id = id;
        this.steer_task_id = steer_task_id;
        this.person_id = person_id;
        this.vessel_id = vessel_id;
        this.completed = completed;
        this.answers = answers;
        this.passed = passed;
        this.not_app = not_app;
        this.lat_long = lat_long;
        this.checked_by_id = checked_by_id;
        this.app_by_id = app_by_id;
        this.date_checked = date_checked;
        this.date_app = date_app;
        this.checked_remarks = checked_remarks;
        this.app_remarks = app_remarks;
    }

    public PersonSteerTask(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_steer_task_id() {
        return person_steer_task_id;
    }

    public void setPerson_steer_task_id(String person_steer_task_id) {
        this.person_steer_task_id = person_steer_task_id;
    }

    public String getSteer_task_id() {
        return steer_task_id;
    }

    public void setSteer_task_id(String steer_task_id) {
        this.steer_task_id = steer_task_id;
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

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getPassed() {
        return passed;
    }

    public void setPassed(String passed) {
        this.passed = passed;
    }

    public String getNot_app() {
        return not_app;
    }

    public void setNot_app(String not_app) {
        this.not_app = not_app;
    }

    public String getLat_long() {
        return lat_long;
    }

    public void setLat_long(String lat_long) {
        this.lat_long = lat_long;
    }

    public String getChecked_by_id() {
        return checked_by_id;
    }

    public void setChecked_by_id(String checked_by_id) {
        this.checked_by_id = checked_by_id;
    }

    public String getApp_by_id() {
        return app_by_id;
    }

    public void setApp_by_id(String app_by_id) {
        this.app_by_id = app_by_id;
    }

    public String getDate_checked() {
        return date_checked;
    }

    public void setDate_checked(String date_checked) {
        this.date_checked = date_checked;
    }

    public String getDate_app() {
        return date_app;
    }

    public void setDate_app(String date_app) {
        this.date_app = date_app;
    }

    public String getChecked_remarks() {
        return checked_remarks;
    }

    public void setChecked_remarks(String checked_remarks) {
        this.checked_remarks = checked_remarks;
    }

    public String getApp_remarks() {
        return app_remarks;
    }

    public void setApp_remarks(String app_remarks) {
        this.app_remarks = app_remarks;
    }
}
