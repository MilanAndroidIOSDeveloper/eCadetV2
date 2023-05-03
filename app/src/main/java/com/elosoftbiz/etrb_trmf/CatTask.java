package com.elosoftbiz.etrb_trmf;

public class CatTask {
    Integer id;
    String cat_task_id;
    String ref_no;
    String desc_cat_task;
    String login_id;
    String last_update;

    public CatTask(Integer id, String cat_task_id, String ref_no, String desc_cat_task, String login_id, String last_update){
        this.id = id;
        this.cat_task_id = cat_task_id;
        this.ref_no = ref_no;
        this.desc_cat_task = desc_cat_task;
        this.login_id = login_id;
        this.last_update = last_update;
    }

    public CatTask(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCat_task_id() {
        return cat_task_id;
    }

    public void setCat_task_id(String cat_task_id) {
        this.cat_task_id = cat_task_id;
    }

    public String getRef_no() {
        return ref_no;
    }

    public void setRef_no(String ref_no) {
        this.ref_no = ref_no;
    }

    public String getDesc_cat_task() {
        return desc_cat_task;
    }

    public void setDesc_cat_task(String desc_cat_task) {
        this.desc_cat_task = desc_cat_task;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }
}
