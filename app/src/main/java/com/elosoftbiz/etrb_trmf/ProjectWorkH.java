package com.elosoftbiz.etrb_trmf;

public class ProjectWorkH {
    Integer id;
    String project_work_h_id;
    String dept;
    String competence_area;
    String prio;
    String login_id;
    String last_update;
    String trb_function_id;

    public ProjectWorkH(Integer id, String project_work_h_id, String dept, String competence_area, String prio, String login_id, String last_update, String trb_function_id){
        this.id = id;
        this.project_work_h_id = project_work_h_id;
        this.dept = dept;
        this.competence_area = competence_area;
        this.prio = prio;
        this.login_id = login_id;
        this.last_update = last_update;
        this.trb_function_id = trb_function_id;
    }

    public ProjectWorkH(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProject_work_h_id() {
        return project_work_h_id;
    }

    public void setProject_work_h_id(String project_work_h_id) {
        this.project_work_h_id = project_work_h_id;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getCompetence_area() {
        return competence_area;
    }

    public void setCompetence_area(String competence_area) {
        this.competence_area = competence_area;
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

    public String getTrb_function_id() {
        return trb_function_id;
    }

    public void setTrb_function_id(String trb_function_id) {
        this.trb_function_id = trb_function_id;
    }
}
