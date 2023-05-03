package com.elosoftbiz.etrb_trmf;

public class Abbreviation {
    Integer id;
    String abbreviation_id, code_abbreviation, desc_abbreviation, login_id, last_update;

    public Abbreviation(String abbreviation_id, Integer id, String code_abbreviation, String desc_abbreviation, String login_id, String last_update){
        this.abbreviation_id=abbreviation_id;
        this.id = id;
        this.code_abbreviation = code_abbreviation;
        this.desc_abbreviation = desc_abbreviation;
        this.login_id = login_id;
        this.last_update = last_update;
    }

    public Abbreviation(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAbbreviation_id() {
        return abbreviation_id;
    }

    public void setAbbreviation_id(String abbreviation_id) {
        this.abbreviation_id = abbreviation_id;
    }

    public String getCode_abbreviation() {
        return code_abbreviation;
    }

    public void setCode_abbreviation(String code_abbreviation) {
        this.code_abbreviation = code_abbreviation;
    }

    public String getDesc_abbreviation() {
        return desc_abbreviation;
    }

    public void setDesc_abbreviation(String desc_abbreviation) {
        this.desc_abbreviation = desc_abbreviation;
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
}
