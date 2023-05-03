package com.elosoftbiz.etrb_trmf;

public class PersonOfficer {
    Integer id;
    String person_officer_id, person_id, officer_id, last_update, from_date, to_date, comp_officer_ok, vessel_id, assessor_id, master_id, chief_eng_id;

    public PersonOfficer(String person_officer_id, Integer id, String person_id, String officer_id, String last_update, String from_date, String to_date, String comp_officer_ok, String vessel_id, String assessor_id, String master_id, String chief_eng_id){
        this.person_officer_id=person_officer_id;
        this.id = id;
        this.person_id = person_id;
        this.officer_id = officer_id;
        this.last_update = last_update;
        this.from_date = from_date;
        this.to_date = to_date;
        this.comp_officer_ok = comp_officer_ok;
        this.vessel_id = vessel_id;
        this.assessor_id = assessor_id;
        this.master_id = master_id;
        this.chief_eng_id = chief_eng_id;
    }

    public  PersonOfficer(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_officer_id() {
        return person_officer_id;
    }

    public void setPerson_officer_id(String person_officer_id) {
        this.person_officer_id = person_officer_id;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getOfficer_id() {
        return officer_id;
    }

    public void setOfficer_id(String officer_id) {
        this.officer_id = officer_id;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getComp_officer_ok() {
        return comp_officer_ok;
    }

    public void setComp_officer_ok(String comp_officer_ok) {
        this.comp_officer_ok = comp_officer_ok;
    }

    public String getVessel_id() {
        return vessel_id;
    }

    public void setVessel_id(String vessel_id) {
        this.vessel_id = vessel_id;
    }

    public String getAssessor_id() {
        return assessor_id;
    }

    public void setAssessor_id(String assessor_id) {
        this.assessor_id = assessor_id;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public String getChief_eng_id() {
        return chief_eng_id;
    }

    public void setChief_eng_id(String chief_eng_id) {
        this.chief_eng_id = chief_eng_id;
    }
}
