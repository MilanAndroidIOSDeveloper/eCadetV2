package com.elosoftbiz.etrb_trmf;

public class Safety {
    Integer id;
    String safety_id, desc_task, prio, dept;

    public Safety(String safety_id, Integer id, String desc_task, String prio, String dept){
        this.safety_id=safety_id;
        this.id = id;
        this.desc_task = desc_task;
        this.prio = prio;
        this.dept = dept;
    }

    public Safety(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSafety_id() {
        return safety_id;
    }

    public void setSafety_id(String safety_id) {
        this.safety_id = safety_id;
    }

    public String getDesc_task() {
        return desc_task;
    }

    public void setDesc_task(String desc_task) {
        this.desc_task = desc_task;
    }

    public String getPrio() {
        return prio;
    }

    public void setPrio(String prio) {
        this.prio = prio;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
}
