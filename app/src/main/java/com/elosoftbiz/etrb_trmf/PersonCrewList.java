package com.elosoftbiz.etrb_trmf;

public class PersonCrewList {
    Integer id;
    String person_crew_list_id;
    String person_id;
    String vessel_id;
    String date_uploaded;
    String filename;
    String checked_by_id;
    String date_checked;
    String checked_remarks;
    String login_id;
    String last_update;

    public PersonCrewList(Integer id, String person_crew_list_id, String person_id, String vessel_id, String date_uploaded, String filename, String checked_by_id, String date_checked, String checked_remarks, String login_id, String last_update){
        this.id = id;
        this.person_crew_list_id = person_crew_list_id;
        this.person_id = person_id;
        this.vessel_id = vessel_id;
        this.date_uploaded = date_uploaded;
        this.filename = filename;
        this.checked_by_id = checked_by_id;
        this.date_checked = date_checked;
        this.checked_remarks = checked_remarks;
        this.login_id = login_id;
        this.last_update = last_update;
    }

    public PersonCrewList(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_crew_list_id() {
        return person_crew_list_id;
    }

    public void setPerson_crew_list_id(String person_crew_list_id) {
        this.person_crew_list_id = person_crew_list_id;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getVessel_id() {
        return vessel_id;
    }

    public void setVessel_id(String vessel_id) {
        this.vessel_id = vessel_id;
    }

    public String getDate_uploaded() {
        return date_uploaded;
    }

    public void setDate_uploaded(String date_uploaded) {
        this.date_uploaded = date_uploaded;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
