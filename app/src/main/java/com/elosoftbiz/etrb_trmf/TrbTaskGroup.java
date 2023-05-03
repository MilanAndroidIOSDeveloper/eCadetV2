package com.elosoftbiz.etrb_trmf;

public class TrbTaskGroup {
    Integer id, prio;
    String trb_task_group_id, trb_topic_id, trb_competence_id, trb_function_id, ref_no_task_group, desc_task_group, cond_task_group;

    public TrbTaskGroup(Integer id, String trb_task_group_id, String trb_topic_id, String trb_competence_id, String trb_function_id, String ref_no_task_group, String desc_task_group, String cond_task_group, Integer prio){

        this.id = id;
        this.trb_task_group_id = trb_task_group_id;
        this.trb_topic_id = trb_topic_id;
        this.trb_competence_id = trb_competence_id;
        this.trb_function_id = trb_function_id;
        this.ref_no_task_group = ref_no_task_group;
        this.desc_task_group = desc_task_group;
        this.cond_task_group = cond_task_group;
        this.prio = prio;
    }

    public TrbTaskGroup(){}

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

    public String getTrb_task_group_id() {
        return trb_task_group_id;
    }

    public void setTrb_task_group_id(String trb_task_group_id) {
        this.trb_task_group_id = trb_task_group_id;
    }

    public String getTrb_topic_id() {
        return trb_topic_id;
    }

    public void setTrb_topic_id(String trb_topic_id) {
        this.trb_topic_id = trb_topic_id;
    }

    public String getTrb_competence_id() {
        return trb_competence_id;
    }

    public void setTrb_competence_id(String trb_competence_id) {
        this.trb_competence_id = trb_competence_id;
    }

    public String getTrb_function_id() {
        return trb_function_id;
    }

    public void setTrb_function_id(String trb_function_id) {
        this.trb_function_id = trb_function_id;
    }

    public String getRef_no_task_group() {
        return ref_no_task_group;
    }

    public void setRef_no_task_group(String ref_no_task_group) {
        this.ref_no_task_group = ref_no_task_group;
    }

    public String getDesc_task_group() {
        return desc_task_group;
    }

    public void setDesc_task_group(String desc_task_group) {
        this.desc_task_group = desc_task_group;
    }

    public String getCond_task_group() {
        return cond_task_group;
    }

    public void setCond_task_group(String cond_task_group) {
        this.cond_task_group = cond_task_group;
    }
}
