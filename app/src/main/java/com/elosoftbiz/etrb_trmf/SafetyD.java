package com.elosoftbiz.etrb_trmf;

public class SafetyD {
    Integer id;
    String safety_d_id;
    String safety_h_id;
    String desc_safety_d;
    String prio;

    public SafetyD(Integer id, String safety_d_id, String safety_h_id, String desc_safety_d, String prio){
        this.id = id;
        this.safety_d_id = safety_d_id;
        this.safety_h_id = safety_h_id;
        this.desc_safety_d = desc_safety_d;
        this.prio = prio;
    }

    public SafetyD(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSafety_d_id() {
        return safety_d_id;
    }

    public void setSafety_d_id(String safety_d_id) {
        this.safety_d_id = safety_d_id;
    }

    public String getSafety_h_id() {
        return safety_h_id;
    }

    public void setSafety_h_id(String safety_h_id) {
        this.safety_h_id = safety_h_id;
    }

    public String getDesc_safety_d() {
        return desc_safety_d;
    }

    public void setDesc_safety_d(String desc_safety_d) {
        this.desc_safety_d = desc_safety_d;
    }

    public String getPrio() {
        return prio;
    }

    public void setPrio(String prio) {
        this.prio = prio;
    }
}
