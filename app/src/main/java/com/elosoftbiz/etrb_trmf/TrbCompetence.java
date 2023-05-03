package com.elosoftbiz.etrb_trmf;

public class TrbCompetence {
    Integer id, prio;
    String trb_competence_id, ref_no, desc_competence, trb_function_id, criteria, instruction;

    public TrbCompetence( Integer id, String trb_competence_id,String ref_no, String desc_competence, String trb_function_id, Integer prio, String criteria, String instruction){
        this.id = id;
        this.trb_competence_id=trb_competence_id;
        this.ref_no = ref_no;
        this.desc_competence = desc_competence;
        this.trb_function_id = trb_function_id;
        this.prio = prio;
        this.criteria = criteria;
        this.instruction = criteria;
    }

    public TrbCompetence(){

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

    public String getTrb_competence_id() {
        return trb_competence_id;
    }

    public void setTrb_competence_id(String trb_competence_id) {
        this.trb_competence_id = trb_competence_id;
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

    public String getTrb_function_id() {
        return trb_function_id;
    }

    public void setTrb_function_id(String trb_function_id) {
        this.trb_function_id = trb_function_id;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}
