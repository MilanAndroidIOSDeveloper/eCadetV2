package com.elosoftbiz.etrb_trmf;

public class RegulationH {
    Integer id, prio;
    String regulation_h_id,  desc_regulation_h, login_id, last_update;

    public RegulationH(String regulation_h_id, Integer id, String desc_regulation_h, Integer prio, String login_id, String last_update){
        this.regulation_h_id=regulation_h_id;
        this.id = id;
        this.desc_regulation_h = desc_regulation_h;
        this.prio = prio;
        this.login_id = login_id;
        this.last_update = last_update;
    }

    public RegulationH(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegulation_h_id() {
        return regulation_h_id;
    }

    public void setRegulation_h_id(String regulation_h_id) {
        this.regulation_h_id = regulation_h_id;
    }

    public String getDesc_regulation_h() {
        return desc_regulation_h;
    }

    public void setDesc_regulation_h(String desc_regulation_h) {
        this.desc_regulation_h = desc_regulation_h;
    }

    public Integer getPrio() {
        return prio;
    }

    public void setPrio(Integer prio) {
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
