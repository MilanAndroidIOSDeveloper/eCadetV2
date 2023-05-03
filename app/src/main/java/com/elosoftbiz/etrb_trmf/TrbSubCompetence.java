package com.elosoftbiz.etrb_trmf;

public class TrbSubCompetence {
    Integer id;
    String trb_sub_competence_id, ref_no, desc_sub_competence, trb_competence_id, criteria_id;

    public TrbSubCompetence(Integer id, String trb_sub_competence_id, String ref_no, String desc_sub_competence, String trb_competence_id, String criteria_id){
        this.id = id;
        this.trb_sub_competence_id = trb_sub_competence_id;
        this.ref_no = ref_no;
        this.desc_sub_competence = desc_sub_competence;
        this.trb_competence_id = trb_competence_id;
        this.criteria_id = criteria_id;
    }

    public TrbSubCompetence(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTrb_sub_competence_id() {
        return trb_sub_competence_id;
    }

    public void setTrb_sub_competence_id(String trb_sub_competence_id) {
        this.trb_sub_competence_id = trb_sub_competence_id;
    }

    public String getRef_no() {
        return ref_no;
    }

    public void setRef_no(String ref_no) {
        this.ref_no = ref_no;
    }

    public String getDesc_sub_competence() {
        return desc_sub_competence;
    }

    public void setDesc_sub_competence(String desc_sub_competence) {
        this.desc_sub_competence = desc_sub_competence;
    }

    public String getTrb_competence_id() {
        return trb_competence_id;
    }

    public void setTrb_competence_id(String trb_competence_id) {
        this.trb_competence_id = trb_competence_id;
    }

    public String getCriteria_id() {
        return criteria_id;
    }

    public void setCriteria_id(String criteria_id) {
        this.criteria_id = criteria_id;
    }
}
