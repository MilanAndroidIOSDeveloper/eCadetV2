package com.elosoftbiz.etrb_trmf;

public class WorkPractice {
    Integer id;
    String work_practice_id;
    String desc_work_practice;
    String dept;
    String prio;
    String login_id;
    String last_update;

    public WorkPractice(Integer id, String work_practice_id, String desc_work_practice, String dept, String prio, String login_id, String last_update){
        this.id = id;
        this.work_practice_id = work_practice_id;
        this.desc_work_practice = desc_work_practice;
        this.dept = dept;
        this.prio = prio;
        this.login_id = login_id;
        this.last_update = last_update;
    }

    public WorkPractice(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWork_practice_id() {
        return work_practice_id;
    }

    public void setWork_practice_id(String work_practice_id) {
        this.work_practice_id = work_practice_id;
    }

    public String getDesc_work_practice() {
        return desc_work_practice;
    }

    public void setDesc_work_practice(String desc_work_practice) {
        this.desc_work_practice = desc_work_practice;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
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
