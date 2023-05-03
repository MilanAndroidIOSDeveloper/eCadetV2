package com.elosoftbiz.etrb_trmf;

public class Task {
    Integer id, prio, phase_no;
    String task_id, ref_no_task, desc_task, trb_competence_id, trb_topic_id, cat_task_id, learning_activity, direction_id, criteria, trb_function_id, trb_task_group_id;

    public Task(String task_id, Integer id, String ref_no_task, String desc_task, String trb_competence_id, String trb_topic_id, Integer prio, Integer phase_no, String cat_task_id, String learning_activity, String direction_id, String criteria, String trb_function_id, String trb_task_group_id){
        this.task_id=task_id;
        this.id = id;
        this.ref_no_task = ref_no_task;
        this.desc_task = desc_task;
        this.trb_competence_id = trb_competence_id;
        this.trb_topic_id = trb_topic_id;
        this.prio = prio;
        this.phase_no = phase_no;
        this.cat_task_id = cat_task_id;
        this.learning_activity = learning_activity;
        this.direction_id = direction_id;
        this.criteria = criteria;
        this.trb_function_id = trb_function_id;
        this.trb_task_group_id = trb_task_group_id;
    }

    public Task(){

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

    public Integer getPhase_no() {
        return phase_no;
    }

    public void setPhase_no(Integer phase_no) {
        this.phase_no = phase_no;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getRef_no_task() {
        return ref_no_task;
    }

    public void setRef_no_task(String ref_no_task) {
        this.ref_no_task = ref_no_task;
    }

    public String getDesc_task() {
        return desc_task;
    }

    public void setDesc_task(String desc_task) {
        this.desc_task = desc_task;
    }

    public String getTrb_competence_id() {
        return trb_competence_id;
    }

    public void setTrb_competence_id(String trb_competence_id) {
        this.trb_competence_id = trb_competence_id;
    }



    public String getTrb_topic_id() {
        return trb_topic_id;
    }

    public void setTrb_topic_id(String trb_topic_id) {
        this.trb_topic_id = trb_topic_id;
    }

    public String getCat_task_id() {
        return cat_task_id;
    }

    public void setCat_task_id(String cat_task_id) {
        this.cat_task_id = cat_task_id;
    }

    public String getLearning_activity() {
        return learning_activity;
    }

    public void setLearning_activity(String learning_activity) {
        this.learning_activity = learning_activity;
    }

    public String getDirection_id() {
        return direction_id;
    }

    public void setDirection_id(String direction_id) {
        this.direction_id = direction_id;
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

    public String getTrb_task_group_id() {
        return trb_task_group_id;
    }

    public void setTrb_task_group_id(String trb_task_group_id) {
        this.trb_task_group_id = trb_task_group_id;
    }
}
