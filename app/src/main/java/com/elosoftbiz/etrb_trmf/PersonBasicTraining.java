package com.elosoftbiz.etrb_trmf;

public class PersonBasicTraining {
    Integer id;
    String person_basic_training_id, person_id, basic_training_id, location_name, date_completed,
            doc_ref_no, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, institution;

    public PersonBasicTraining(String person_basic_training_id, Integer id, String person_id, String basic_training_id, String location_name, String date_completed, String doc_ref_no, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks, String institution){
        this.person_basic_training_id=person_basic_training_id;
        this.id = id;
        this.person_id = person_id;
        this.basic_training_id = basic_training_id;
        this.location_name = location_name;
        this.date_completed = date_completed;
        this.doc_ref_no = doc_ref_no;
        this.checked_by_id = checked_by_id;
        this.app_by_id = app_by_id;
        this.date_checked = date_checked;
        this.date_app = date_app;
        this.checked_remarks = checked_remarks;
        this.app_remarks = app_remarks;
        this.institution = institution;
    }

    public PersonBasicTraining(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_basic_training_id() {
        return person_basic_training_id;
    }

    public void setPerson_basic_training_id(String person_basic_training_id) {
        this.person_basic_training_id = person_basic_training_id;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getBasic_training_id() {
        return basic_training_id;
    }

    public void setBasic_training_id(String basic_training_id) {
        this.basic_training_id = basic_training_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getDate_completed() {
        return date_completed;
    }

    public void setDate_completed(String date_completed) {
        this.date_completed = date_completed;
    }

    public String getDoc_ref_no() {
        return doc_ref_no;
    }

    public void setDoc_ref_no(String doc_ref_no) {
        this.doc_ref_no = doc_ref_no;
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

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }
}
