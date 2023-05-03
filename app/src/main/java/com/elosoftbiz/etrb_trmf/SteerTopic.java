package com.elosoftbiz.etrb_trmf;

public class SteerTopic {
    Integer id, prio;
    String steer_topic_id, ref_no_topic, desc_steer_topic, steer_competence_id, criteria;

    public SteerTopic(String steer_topic_id, Integer id, String ref_no_topic, String desc_steer_topic, String steer_competence_id, String criteria, Integer prio){
        this.steer_topic_id=steer_topic_id;
        this.id = id;
        this.ref_no_topic = ref_no_topic;
        this.desc_steer_topic = desc_steer_topic;
        this.steer_competence_id = steer_competence_id;
        this.criteria = criteria;
        this.prio = prio;
    }

    public SteerTopic(){

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

    public String getSteer_topic_id() {
        return steer_topic_id;
    }

    public void setSteer_topic_id(String steer_topic_id) {
        this.steer_topic_id = steer_topic_id;
    }

    public String getRef_no_topic() {
        return ref_no_topic;
    }

    public void setRef_no_topic(String ref_no_topic) {
        this.ref_no_topic = ref_no_topic;
    }

    public String getDesc_steer_topic() {
        return desc_steer_topic;
    }

    public void setDesc_steer_topic(String desc_steer_topic) {
        this.desc_steer_topic = desc_steer_topic;
    }

    public String getSteer_competence_id() {
        return steer_competence_id;
    }

    public void setSteer_competence_id(String steer_competence_id) {
        this.steer_competence_id = steer_competence_id;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }
}
