package com.elosoftbiz.etrb_trmf;

public class PersonSteering {
    Integer id;
    String person_steering_id, person_id, vessel_id, steering_type, voyage_from, voyage_to, date_steering, steering_from, steering_to, remarks, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks;
    Double total_hrs;

    public PersonSteering(String person_steering_id, Integer id, String person_id, String vessel_id, String steering_type, String voyage_from, String voyage_to, String date_steering, String steering_from, String steering_to, Double total_hrs, String remarks, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks){
        this.person_steering_id=person_steering_id;
        this.id = id;
        this.person_id = person_id;
        this.vessel_id = vessel_id;
        this.steering_type = steering_type;
        this.voyage_from = voyage_from;
        this.voyage_to = voyage_to;
        this.date_steering = date_steering;
        this.steering_from = steering_from;
        this.steering_to = steering_to;
        this.total_hrs = total_hrs;
        this.remarks = remarks;
        this.checked_by_id = checked_by_id;
        this.app_by_id = app_by_id;
        this.date_checked = date_checked;
        this.date_app = date_app;
        this.checked_remarks = checked_remarks;
        this.app_remarks = app_remarks;
    }

    public PersonSteering(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_steering_id() {
        return person_steering_id;
    }

    public void setPerson_steering_id(String person_steering_id) {
        this.person_steering_id = person_steering_id;
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

    public String getSteering_type() {
        return steering_type;
    }

    public void setSteering_type(String steering_type) {
        this.steering_type = steering_type;
    }

    public String getVoyage_from() {
        return voyage_from;
    }

    public void setVoyage_from(String voyage_from) {
        this.voyage_from = voyage_from;
    }

    public String getVoyage_to() {
        return voyage_to;
    }

    public void setVoyage_to(String voyage_to) {
        this.voyage_to = voyage_to;
    }

    public String getDate_steering() {
        return date_steering;
    }

    public void setDate_steering(String date_steering) {
        this.date_steering = date_steering;
    }

    public String getSteering_from() {
        return steering_from;
    }

    public void setSteering_from(String steering_from) {
        this.steering_from = steering_from;
    }

    public String getSteering_to() {
        return steering_to;
    }

    public void setSteering_to(String steering_to) {
        this.steering_to = steering_to;
    }

    public Double getTotal_hrs() {
        return total_hrs;
    }

    public void setTotal_hrs(Double total_hrs) {
        this.total_hrs = total_hrs;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
