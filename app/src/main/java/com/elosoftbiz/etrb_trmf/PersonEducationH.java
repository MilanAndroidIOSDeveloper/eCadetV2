package com.elosoftbiz.etrb_trmf;

public class PersonEducationH {
    String person_education_h_id, person_id, gce_level, school_name, school_address, certificate_date, login_id, last_update;
    Integer id;

    PersonEducationH(String person_education_h_id, Integer id, String person_id, String gce_level, String school_name, String school_address, String certificate_date, String login_id, String last_update){
        this.person_education_h_id=person_education_h_id;
        this.id = id;
        this.person_id = person_id;
        this.gce_level = gce_level;
        this.school_name = school_name;
        this.school_address = school_address;
        this.certificate_date = certificate_date;
        this.login_id = login_id;
        this.last_update = last_update;
    }

    PersonEducationH(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_education_h_id() {
        return person_education_h_id;
    }

    public void setPerson_education_h_id(String person_education_h_id) {
        this.person_education_h_id = person_education_h_id;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getGce_level() {
        return gce_level;
    }

    public void setGce_level(String gce_level) {
        this.gce_level = gce_level;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getSchool_address() {
        return school_address;
    }

    public void setSchool_address(String school_address) {
        this.school_address = school_address;
    }

    public String getCertificate_date() {
        return certificate_date;
    }

    public void setCertificate_date(String certificate_date) {
        this.certificate_date = certificate_date;
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
