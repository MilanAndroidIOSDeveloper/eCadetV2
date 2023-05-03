package com.elosoftbiz.etrb_trmf;

public class BasicTraining {
    Integer id, prio;
    String basic_training_id, desc_basic_training, dept, training_type;

    public BasicTraining(String basic_training_id, Integer id, String desc_basic_training, String dept, Integer prio, String training_type){
        this.basic_training_id=basic_training_id;
        this.id = id;
        this.desc_basic_training = desc_basic_training;
        this.dept = dept;
        this.prio = prio;
        this.training_type = training_type;
    }

    public BasicTraining(){

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

    public String getBasic_training_id() {
        return basic_training_id;
    }

    public void setBasic_training_id(String basic_training_id) {
        this.basic_training_id = basic_training_id;
    }

    public String getDesc_basic_training() {
        return desc_basic_training;
    }

    public void setDesc_basic_training(String desc_basic_training) {
        this.desc_basic_training = desc_basic_training;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getTraining_type() {
        return training_type;
    }

    public void setTraining_type(String training_type) {
        this.training_type = training_type;
    }
}
