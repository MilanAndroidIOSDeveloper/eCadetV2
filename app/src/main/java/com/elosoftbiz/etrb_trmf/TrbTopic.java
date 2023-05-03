package com.elosoftbiz.etrb_trmf;

public class TrbTopic {
    Integer id, prio;
    String trb_topic_id, ref_no_topic, desc_topic, trb_competence_id, criteria, trb_function_id;

    public TrbTopic(String trb_topic_id, Integer id, String ref_no_topic, String desc_topic, String trb_competence_id, String criteria, Integer prio, String trb_function_id){
        this.trb_topic_id=trb_topic_id;
        this.id = id;
        this.ref_no_topic = ref_no_topic;
        this.desc_topic = desc_topic;
        this.trb_competence_id = trb_competence_id;
        this.criteria = criteria;
        this.prio = prio;
        this.trb_function_id = trb_function_id;
    }

    public TrbTopic(){

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

    public String getTrb_topic_id() {
        return trb_topic_id;
    }

    public void setTrb_topic_id(String trb_topic_id) {
        this.trb_topic_id = trb_topic_id;
    }

    public String getRef_no_topic() {
        return ref_no_topic;
    }

    public void setRef_no_topic(String ref_no_topic) {
        this.ref_no_topic = ref_no_topic;
    }

    public String getDesc_topic() {
        return desc_topic;
    }

    public void setDesc_topic(String desc_topic) {
        this.desc_topic = desc_topic;
    }

    public String getTrb_competence_id() {
        return trb_competence_id;
    }

    public void setTrb_competence_id(String trb_competence_id) {
        this.trb_competence_id = trb_competence_id;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getTrb_function_id() {
        return trb_function_id;
    }

    public void setTrb_function_id(String trb_function_id) {
        this.trb_function_id = trb_function_id;
    }
}
