package com.elosoftbiz.etrb_trmf;

public class SafetyH {
    Integer id;
    String safety_h_id;
    String desc_safety_h;
    String table_header;
    String prio;
    String dept;
    String login_id;
    String last_update;
    String remarks;

    public SafetyH(Integer id, String safety_h_id, String desc_safety_h, String table_header, String prio, String dept, String login_id, String last_update, String remarks){
        this.id = id;
        this.safety_h_id = safety_h_id;
        this.desc_safety_h = desc_safety_h;
        this.table_header = table_header;
        this.prio = prio;
        this.dept = dept;
        this.login_id = login_id;
        this.last_update = last_update;
        this.remarks = remarks;
    }

    public SafetyH(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSafety_h_id() {
        return safety_h_id;
    }

    public void setSafety_h_id(String safety_h_id) {
        this.safety_h_id = safety_h_id;
    }

    public String getDesc_safety_h() {
        return desc_safety_h;
    }

    public void setDesc_safety_h(String desc_safety_h) {
        this.desc_safety_h = desc_safety_h;
    }

    public String getTable_header() {
        return table_header;
    }

    public void setTable_header(String table_header) {
        this.table_header = table_header;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
