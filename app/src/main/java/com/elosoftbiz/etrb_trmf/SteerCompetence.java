package com.elosoftbiz.etrb_trmf;

public class SteerCompetence {
    Integer id, prio;
    String steer_competence_id, ref_no, desc_competence;

    public SteerCompetence(String steer_competence_id, Integer id, String ref_no, String desc_competence, Integer prio){
        this.steer_competence_id=steer_competence_id;
        this.id = id;
        this.ref_no = ref_no;
        this.desc_competence = desc_competence;
        this.prio = prio;
    }

    public SteerCompetence(){

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

    public String getSteer_competence_id() {
        return steer_competence_id;
    }

    public void setSteer_competence_id(String steer_competence_id) {
        this.steer_competence_id = steer_competence_id;
    }

    public String getRef_no() {
        return ref_no;
    }

    public void setRef_no(String ref_no) {
        this.ref_no = ref_no;
    }

    public String getDesc_competence() {
        return desc_competence;
    }

    public void setDesc_competence(String desc_competence) {
        this.desc_competence = desc_competence;
    }
}
