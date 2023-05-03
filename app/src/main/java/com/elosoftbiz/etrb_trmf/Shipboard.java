package com.elosoftbiz.etrb_trmf;

public class Shipboard {
    Integer id;
    String shipboard_id;
    String sign_on;
    String sign_off;
    String engine_watch_mos;
    String engine_watch_yrs;
    String voyage_mos;
    String person_id;
    String vessel_id;
    String voyage_days;
    String imo_number;
    String srb_file;
    String cert_file;
    String checked_by_id;
    String date_checked;
    String contract_file;


    public Shipboard(Integer id, String shipboard_id, String sign_on, String sign_off, String engine_watch_mos, String engine_watch_yrs, String voyage_mos, String person_id, String vessel_id, String voyage_days, String imo_number, String srb_file, String cert_file, String checked_by_id, String date_checked, String contract_file){
        this.id = id;
        this.shipboard_id = shipboard_id;
        this.sign_on = sign_on;
        this.sign_off = sign_off;
        this.engine_watch_mos = engine_watch_mos;
        this.engine_watch_yrs = engine_watch_yrs;
        this.voyage_mos = voyage_mos;
        this.person_id = person_id;
        this.vessel_id = vessel_id;
        this.voyage_days = voyage_days;
        this.imo_number = imo_number;
        this.srb_file = srb_file;
        this.cert_file = cert_file;
        this.checked_by_id = checked_by_id;
        this.date_checked = date_checked;
        this.contract_file = contract_file;
    }

    public Shipboard(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShipboard_id() {
        return shipboard_id;
    }

    public void setShipboard_id(String shipboard_id) {
        this.shipboard_id = shipboard_id;
    }

    public String getSign_on() {
        return sign_on;
    }

    public void setSign_on(String sign_on) {
        this.sign_on = sign_on;
    }

    public String getSign_off() {
        return sign_off;
    }

    public void setSign_off(String sign_off) {
        this.sign_off = sign_off;
    }

    public String getEngine_watch_mos() {
        return engine_watch_mos;
    }

    public void setEngine_watch_mos(String engine_watch_mos) {
        this.engine_watch_mos = engine_watch_mos;
    }

    public String getEngine_watch_yrs() {
        return engine_watch_yrs;
    }

    public void setEngine_watch_yrs(String engine_watch_yrs) {
        this.engine_watch_yrs = engine_watch_yrs;
    }

    public String getVoyage_mos() {
        return voyage_mos;
    }

    public void setVoyage_mos(String voyage_mos) {
        this.voyage_mos = voyage_mos;
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

    public String getVoyage_days() {
        return voyage_days;
    }

    public void setVoyage_days(String voyage_days) {
        this.voyage_days = voyage_days;
    }

    public String getImo_number() {
        return imo_number;
    }

    public void setImo_number(String imo_number) {
        this.imo_number = imo_number;
    }

    public String getSrb_file() {
        return srb_file;
    }

    public void setSrb_file(String srb_file) {
        this.srb_file = srb_file;
    }

    public String getCert_file() {
        return cert_file;
    }

    public void setCert_file(String cert_file) {
        this.cert_file = cert_file;
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

    public String getContract_file() {
        return contract_file;
    }

    public void setContract_file(String contract_file) {
        this.contract_file = contract_file;
    }
}
