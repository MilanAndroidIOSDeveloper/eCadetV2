package com.elosoftbiz.etrb_trmf;

public class ProjectWorkD {

    Integer id;
    String project_work_d_id;
    String project_work_h_id;
    String details;
    String prio;
    String login_id;
    String last_update;

    public ProjectWorkD(Integer id, String project_work_d_id, String project_work_h_id, String details, String prio, String login_id, String last_update){
        this.id = id;
        this.project_work_d_id = project_work_d_id;
        this.project_work_h_id = project_work_h_id;
        this.details = details;
        this.prio = prio;
        this.login_id = login_id;
        this.last_update = last_update;
    }

    public ProjectWorkD(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProject_work_d_id() {
        return project_work_d_id;
    }

    public void setProject_work_d_id(String project_work_d_id) {
        this.project_work_d_id = project_work_d_id;
    }

    public String getProject_work_h_id() {
        return project_work_h_id;
    }

    public void setProject_work_h_id(String project_work_h_id) {
        this.project_work_h_id = project_work_h_id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPrio() {
        return prio;
    }

    public void setPrio(String prio) {
        this.prio = prio;
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
