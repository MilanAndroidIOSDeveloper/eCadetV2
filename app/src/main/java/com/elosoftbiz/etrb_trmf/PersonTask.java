package com.elosoftbiz.etrb_trmf;

public class PersonTask {
    Integer id;
    String person_task_id, task_id, person_id, completed, answers, passed, img_file, not_app, lat_long, vessel_type_id, checked_by_id, app_by_id, date_checked, date_app, officer_remarks, app_remarks, vessel_id;
    String assessed, answers2, completed2, passed2, additional_comment, for_app;
    String activity_area, intial_cond, feedback, location, equipments;

    public PersonTask(String person_task_id, Integer id, String task_id, String person_id, String completed, String answers, String passed, String img_file, String not_app, String lat_long, String vessel_type_id, String checked_by_id, String app_by_id, String date_checked, String date_app, String officer_remarks, String app_remarks, String vessel_id, String assessed, String answers2, String completed2, String passed2, String additional_comment, String for_app, String activity_area, String intial_cond, String feedback, String location, String equipments){
        this.person_task_id=person_task_id;
        this.id = id;
        this.task_id = task_id;
        this.person_id = person_id;
        this.completed = completed;
        this.answers = answers;
        this.passed = passed;
        this.img_file = img_file;
        this.not_app = not_app;
        this.lat_long = lat_long;
        this.vessel_type_id = vessel_type_id;
        this.checked_by_id = checked_by_id;
        this.app_by_id = app_by_id;
        this.date_checked = date_checked;
        this.date_app = date_app;
        this.officer_remarks = officer_remarks;
        this.app_remarks = app_remarks;
        this.vessel_id = vessel_id;
        this.assessed = assessed;
        this.answers2 = answers2;
        this.completed2 = completed2;
        this.passed2 = passed2;
        this.additional_comment = additional_comment;
        this.for_app = for_app;
        this.activity_area = activity_area;
        this.intial_cond = intial_cond;
        this.feedback = feedback;
        this.location = location;
        this.equipments = equipments;
    }

    public PersonTask(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_task_id() {
        return person_task_id;
    }

    public void setPerson_task_id(String person_task_id) {
        this.person_task_id = person_task_id;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
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

    public String getImg_file() {
        return img_file;
    }

    public void setImg_file(String img_file) {
        this.img_file = img_file;
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

    public String getVessel_type_id() {
        return vessel_type_id;
    }

    public void setVessel_type_id(String vessel_type_id) {
        this.vessel_type_id = vessel_type_id;
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

    public String getOfficer_remarks() {
        return officer_remarks;
    }

    public void setOfficer_remarks(String officer_remarks) {
        this.officer_remarks = officer_remarks;
    }

    public String getApp_remarks() {
        return app_remarks;
    }

    public void setApp_remarks(String app_remarks) {
        this.app_remarks = app_remarks;
    }

    public String getVessel_id() {
        return vessel_id;
    }

    public void setVessel_id(String vessel_id) {
        this.vessel_id = vessel_id;
    }

    public String getAssessed() {
        return assessed;
    }

    public void setAssessed(String assessed) {
        this.assessed = assessed;
    }

    public String getAnswers2() {
        return answers2;
    }

    public void setAnswers2(String answers2) {
        this.answers2 = answers2;
    }

    public String getCompleted2() {
        return completed2;
    }

    public void setCompleted2(String completed2) {
        this.completed2 = completed2;
    }

    public String getPassed2() {
        return passed2;
    }

    public void setPassed2(String passed2) {
        this.passed2 = passed2;
    }

    public String getAdditional_comment() {
        return additional_comment;
    }

    public void setAdditional_comment(String additional_comment) {
        this.additional_comment = additional_comment;
    }

    public String getFor_app() {
        return for_app;
    }

    public void setFor_app(String for_app) {
        this.for_app = for_app;
    }

    public String getActivity_area() {
        return activity_area;
    }

    public void setActivity_area(String activity_area) {
        this.activity_area = activity_area;
    }

    public String getIntial_cond() {
        return intial_cond;
    }

    public void setIntial_cond(String intial_cond) {
        this.intial_cond = intial_cond;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEquipments() {
        return equipments;
    }

    public void setEquipments(String equipments) {
        this.equipments = equipments;
    }
}
