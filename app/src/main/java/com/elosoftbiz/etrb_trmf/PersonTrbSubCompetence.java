package com.elosoftbiz.etrb_trmf;

public class PersonTrbSubCompetence {
    Integer id;
    String person_trb_sub_competence_id, trb_sub_competenceid, person_id, checked_by_id, date_checked, checked_remarks;

    public PersonTrbSubCompetence(Integer id, String person_trb_sub_competence_id, String trb_sub_competenceid, String person_id, String checked_by_id, String date_checked, String checked_remarks){
        this.id = id;
        this.person_trb_sub_competence_id = person_trb_sub_competence_id;
        this.trb_sub_competenceid = trb_sub_competenceid;
        this.person_id = person_id;
        this.checked_by_id = checked_by_id;
        this.date_checked = date_checked;
        this.checked_remarks = checked_remarks;
    }

    public PersonTrbSubCompetence(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_trb_sub_competence_id() {
        return person_trb_sub_competence_id;
    }

    public void setPerson_trb_sub_competence_id(String person_trb_sub_competence_id) {
        this.person_trb_sub_competence_id = person_trb_sub_competence_id;
    }

    public String getTrb_sub_competenceid() {
        return trb_sub_competenceid;
    }

    public void setTrb_sub_competenceid(String trb_sub_competenceid) {
        this.trb_sub_competenceid = trb_sub_competenceid;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
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

    public String getChecked_remarks() {
        return checked_remarks;
    }

    public void setChecked_remarks(String checked_remarks) {
        this.checked_remarks = checked_remarks;
    }
}
