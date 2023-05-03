package com.elosoftbiz.etrb_trmf;

public class PersonTaskFile {
    Integer id;
    String person_task_file_id, filename, file_desc, uploaded, person_id, task_id, person_task_id, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks;

    public PersonTaskFile(String person_task_file_id, Integer id, String filename, String file_desc, String uploaded, String person_id, String task_id, String person_task_id, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks){
        this.person_task_file_id=person_task_file_id;
        this.id = id;
        this.filename = filename;
        this.file_desc = file_desc;
        this.uploaded = uploaded;
        this.person_id = person_id;
        this.task_id = task_id;
        this.person_task_id = person_task_id;
        this.checked_by_id = checked_by_id;
        this.app_by_id = app_by_id;
        this.date_checked = date_checked;
        this.date_app = date_app;
        this.checked_remarks = checked_remarks;
        this.app_remarks = app_remarks;

    }

    public PersonTaskFile(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_task_file_id() {
        return person_task_file_id;
    }

    public void setPerson_task_file_id(String person_task_file_id) {
        this.person_task_file_id = person_task_file_id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFile_desc() {
        return file_desc;
    }

    public void setFile_desc(String file_desc) {
        this.file_desc = file_desc;
    }

    public String getUploaded() {
        return uploaded;
    }

    public void setUploaded(String uploaded) {
        this.uploaded = uploaded;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getPerson_task_id() {
        return person_task_id;
    }

    public void setPerson_task_id(String person_task_id) {
        this.person_task_id = person_task_id;
    }

    public String getChecked_by_id() {
        return checked_by_id;
    }

    public void setChecked_by_id(String checked_by_id) {
        this.checked_by_id = checked_by_id;
    }

    public String getApp_by_id() {
        return app_by_id;
    }

    public void setApp_by_id(String app_by_id) {
        this.app_by_id = app_by_id;
    }

    public String getDate_checked() {
        return date_checked;
    }

    public void setDate_checked(String date_checked) {
        this.date_checked = date_checked;
    }

    public String getDate_app() {
        return date_app;
    }

    public void setDate_app(String date_app) {
        this.date_app = date_app;
    }

    public String getChecked_remarks() {
        return checked_remarks;
    }

    public void setChecked_remarks(String checked_remarks) {
        this.checked_remarks = checked_remarks;
    }

    public String getApp_remarks() {
        return app_remarks;
    }

    public void setApp_remarks(String app_remarks) {
        this.app_remarks = app_remarks;
    }
}
