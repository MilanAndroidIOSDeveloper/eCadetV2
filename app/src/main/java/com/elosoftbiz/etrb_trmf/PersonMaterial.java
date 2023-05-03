package com.elosoftbiz.etrb_trmf;

public class PersonMaterial {
    Integer id;
    String person_material_id, person_id, material, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks, date_material;

    public PersonMaterial(String person_material_id, Integer id, String person_id, String material, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks, String date_material){
        this.person_material_id=person_material_id;
        this.id = id;
        this.person_id = person_id;
        this.material = material;
        this.checked_by_id = checked_by_id;
        this.app_by_id = app_by_id;
        this.date_checked = date_checked;
        this.date_app = date_app;
        this.checked_remarks = checked_remarks;
        this.app_remarks = app_remarks;
        this.date_material = date_material;
    }

    public PersonMaterial(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_material_id() {
        return person_material_id;
    }

    public void setPerson_material_id(String person_material_id) {
        this.person_material_id = person_material_id;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
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

    public String getDate_material() {
        return date_material;
    }

    public void setDate_material(String date_material) {
        this.date_material = date_material;
    }
}
