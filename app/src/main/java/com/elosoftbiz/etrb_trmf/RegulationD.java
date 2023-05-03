package com.elosoftbiz.etrb_trmf;

public class RegulationD {
    Integer id, prio;
    String regulation_d_id, regulation_h_id, desc_regulation_d, contents;

    public RegulationD(String regulation_d_id, Integer id, String regulation_h_id, String desc_regulation_d, Integer prio, String contents){
        this.regulation_d_id=regulation_d_id;
        this.id = id;
        this.regulation_h_id = regulation_h_id;
        this.desc_regulation_d = desc_regulation_d;
        this.prio = prio;
        this.contents = contents;
    }

    public RegulationD(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPrio() {
        return prio;
    }

    public void setPrio(Integer prio) {
        this.prio = prio;
    }

    public String getRegulation_d_id() {
        return regulation_d_id;
    }

    public void setRegulation_d_id(String regulation_d_id) {
        this.regulation_d_id = regulation_d_id;
    }

    public String getRegulation_h_id() {
        return regulation_h_id;
    }

    public void setRegulation_h_id(String regulation_h_id) {
        this.regulation_h_id = regulation_h_id;
    }

    public String getDesc_regulation_d() {
        return desc_regulation_d;
    }

    public void setDesc_regulation_d(String desc_regulation_d) {
        this.desc_regulation_d = desc_regulation_d;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
