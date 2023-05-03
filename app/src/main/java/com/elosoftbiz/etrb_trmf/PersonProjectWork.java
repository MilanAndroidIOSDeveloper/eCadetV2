package com.elosoftbiz.etrb_trmf;

public class PersonProjectWork {
    Integer id;
    String person_project_work_id;
    String person_id;
    String project_work_d_id;
    String date_completed;
    String vessel_id;
    String details;
    String eval;
    String checked_by_id;
    String date_checked;
    String checked_remarks;
    String login_id;
    String last_update;
    String for_app;

    public PersonProjectWork(Integer id, String person_project_work_id, String person_id, String project_work_d_id, String date_completed, String vessel_id, String details, String eval, String checked_by_id, String date_checked, String checked_remarks, String login_id, String last_update, String for_app){
        this.id = id;
        this.person_project_work_id = person_project_work_id;
        this.person_id = person_id;
        this.project_work_d_id = project_work_d_id;
        this.date_completed = date_completed;
        this.vessel_id = vessel_id;
        this.details = details;
        this.eval = eval;
        this.checked_by_id = checked_by_id;
        this.date_checked = date_checked;
        this.checked_remarks = checked_remarks;
        this.login_id = login_id;
        this.last_update = last_update;
        this.for_app = for_app;
    }

    public PersonProjectWork(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_project_work_id() {
        return person_project_work_id;
    }

    public void setPerson_project_work_id(String person_project_work_id) {
        this.person_project_work_id = person_project_work_id;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getProject_work_d_id() {
        return project_work_d_id;
    }

    public void setProject_work_d_id(String project_work_d_id) {
        this.project_work_d_id = project_work_d_id;
    }

    public String getDate_completed() {
        return date_completed;
    }

    public void setDate_completed(String date_completed) {
        this.date_completed = date_completed;
    }

    public String getVessel_id() {
        return vessel_id;
    }

    public void setVessel_id(String vessel_id) {
        this.vessel_id = vessel_id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getEval() {
        return eval;
    }

    public void setEval(String eval) {
        this.eval = eval;
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

    public String getFor_app() {
        return for_app;
    }

    public void setFor_app(String for_app) {
        this.for_app = for_app;
    }
}
