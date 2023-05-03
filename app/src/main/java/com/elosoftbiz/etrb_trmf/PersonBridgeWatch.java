package com.elosoftbiz.etrb_trmf;

public class PersonBridgeWatch {
    Integer id;
    String person_bridge_watch_id, person_id, vessel_id, date_watchkeeping, from_time, to_time, voyage_number, voyage_desc, watch_type, remarks, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks;
    Double total_hrs;

    public PersonBridgeWatch(String person_bridge_watch_id, Integer id, String person_id, String vessel_id, String date_watchkeeping, String from_time, String to_time, String voyage_number, String voyage_desc, String watch_type, String remarks, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks, Double total_hrs){
        this.person_bridge_watch_id=person_bridge_watch_id;
        this.id = id;
        this.person_id = person_id;
        this.vessel_id = vessel_id;
        this.date_watchkeeping = date_watchkeeping;
        this.from_time = from_time;
        this.to_time = to_time;
        this.voyage_number = voyage_number;
        this.voyage_desc = voyage_desc;
        this.watch_type = watch_type;
        this.remarks = remarks;
        this.checked_by_id = checked_by_id;
        this.app_by_id = app_by_id;
        this.date_checked = date_checked;
        this.date_app = date_app;
        this.checked_remarks = checked_remarks;
        this.app_remarks = app_remarks;
        this.total_hrs = total_hrs;
    }

    public PersonBridgeWatch(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_bridge_watch_id() {
        return person_bridge_watch_id;
    }

    public void setPerson_bridge_watch_id(String person_bridge_watch_id) {
        this.person_bridge_watch_id = person_bridge_watch_id;
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

    public String getDate_watchkeeping() {
        return date_watchkeeping;
    }

    public void setDate_watchkeeping(String date_watchkeeping) {
        this.date_watchkeeping = date_watchkeeping;
    }

    public String getFrom_time() {
        return from_time;
    }

    public void setFrom_time(String from_time) {
        this.from_time = from_time;
    }

    public String getTo_time() {
        return to_time;
    }

    public void setTo_time(String to_time) {
        this.to_time = to_time;
    }

    public String getVoyage_number() {
        return voyage_number;
    }

    public void setVoyage_number(String voyage_number) {
        this.voyage_number = voyage_number;
    }

    public String getVoyage_desc() {
        return voyage_desc;
    }

    public void setVoyage_desc(String voyage_desc) {
        this.voyage_desc = voyage_desc;
    }

    public String getWatch_type() {
        return watch_type;
    }

    public void setWatch_type(String watch_type) {
        this.watch_type = watch_type;
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

    public Double getTotal_hrs() {
        return total_hrs;
    }

    public void setTotal_hrs(Double total_hrs) {
        this.total_hrs = total_hrs;
    }
}
