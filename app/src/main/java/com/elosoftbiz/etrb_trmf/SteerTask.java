package com.elosoftbiz.etrb_trmf;

public class SteerTask {
    Integer id, prio, phase_no;
    String steer_task_id,  ref_no, desc_steer_task, steer_competence_id, steer_topic_id;

    public SteerTask(String steer_task_id, Integer id, String ref_no, String desc_steer_task, String steer_competence_id, String steer_topic_id, Integer prio, Integer phase_no){
        this.steer_task_id=steer_task_id;
        this.id = id;
        this.ref_no = ref_no;
        this.desc_steer_task = desc_steer_task;
        this.steer_competence_id = steer_competence_id;
        this.steer_topic_id = steer_topic_id;
        this.prio = prio;
        this.phase_no = phase_no;
    }

    public SteerTask(){

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

    public String getSteer_task_id() {
        return steer_task_id;
    }

    public void setSteer_task_id(String steer_task_id) {
        this.steer_task_id = steer_task_id;
    }

    public String getRef_no() {
        return ref_no;
    }

    public void setRef_no(String ref_no) {
        this.ref_no = ref_no;
    }

    public String getDesc_steer_task() {
        return desc_steer_task;
    }

    public void setDesc_steer_task(String desc_steer_task) {
        this.desc_steer_task = desc_steer_task;
    }

    public String getSteer_competence_id() {
        return steer_competence_id;
    }

    public void setSteer_competence_id(String steer_competence_id) {
        this.steer_competence_id = steer_competence_id;
    }

    public String getSteer_topic_id() {
        return steer_topic_id;
    }

    public void setSteer_topic_id(String steer_topic_id) {
        this.steer_topic_id = steer_topic_id;
    }
}
