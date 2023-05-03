package com.elosoftbiz.etrb_trmf;

public class PersonSteerTopic {
    Integer id;
    String person_steer_topic_id, steer_topic_id, person_id, checked_by_id, date_checked, checked_remarks;

    public PersonSteerTopic(String person_steer_topic_id, Integer id, String steer_topic_id, String person_id, String checked_by_id, String date_checked, String checked_remarks){
        this.person_steer_topic_id=person_steer_topic_id;
        this.id = id;
        this.steer_topic_id = steer_topic_id;
        this.person_id = person_id;
        this.checked_by_id = checked_by_id;
        this.date_checked = date_checked;
        this.checked_remarks = checked_remarks;
    }

    public PersonSteerTopic(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_steer_topic_id() {
        return person_steer_topic_id;
    }

    public void setPerson_steer_topic_id(String person_steer_topic_id) {
        this.person_steer_topic_id = person_steer_topic_id;
    }

    public String getSteer_topic_id() {
        return steer_topic_id;
    }

    public void setSteer_topic_id(String steer_topic_id) {
        this.steer_topic_id = steer_topic_id;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getChecked_by_id() {
        return checked_by_id;
    }

    public void setChecked_by_id(String checked_by_id) {
        this.checked_by_id = checked_by_id;
    }

    public String getDate_checked() {
        return date_checked;
    }

    public void setDate_checked(String date_checked) {
        this.date_checked = date_checked;
    }

    public String getChecked_remarks() {
        return checked_remarks;
    }

    public void setChecked_remarks(String checked_remarks) {
        this.checked_remarks = checked_remarks;
    }
}
