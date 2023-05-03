package com.elosoftbiz.etrb_trmf;

public class PersonSafety {
    Integer id;
    String person_safety_id, person_id, safety_id, date_completed, ship_id, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, na;

    public PersonSafety(String person_safety_id, Integer id, String person_id, String safety_id, String date_completed, String ship_id, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks, String na){

        this.person_safety_id=person_safety_id;
        this.id = id;
        this.person_id = person_id;
        this.safety_id = safety_id;
        this.date_completed = date_completed;
        this.ship_id = ship_id;
        this.checked_by_id = checked_by_id;
        this.app_by_id = app_by_id;
        this.date_checked = date_checked;
        this.date_app = date_app;
        this.checked_remarks = checked_remarks;
        this.app_remarks = app_remarks;
        this.na = na;
    }

    public PersonSafety(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_safety_id() {
        return person_safety_id;
    }

    public void setPerson_safety_id(String person_safety_id) {
        this.person_safety_id = person_safety_id;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getSafety_id() {
        return safety_id;
    }

    public void setSafety_id(String safety_id) {
        this.safety_id = safety_id;
    }

    public String getDate_completed() {
        return date_completed;
    }

    public void setDate_completed(String date_completed) {
        this.date_completed = date_completed;
    }

    public String getShip_id() {
        return ship_id;
    }

    public void setShip_id(String ship_id) {
        this.ship_id = ship_id;
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

    public String getNa() {
        return na;
    }

    public void setNa(String na) {
        this.na = na;
    }
}
