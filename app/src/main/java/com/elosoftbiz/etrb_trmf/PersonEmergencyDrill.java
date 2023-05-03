package com.elosoftbiz.etrb_trmf;

public class PersonEmergencyDrill {
    Integer id;
    String person_emergency_drill_id;
    String person_id;
    String vessel_id;
    String date_familiarization;
    String details;
    String filename;
    String checked_by_id;
    String date_checked;
    String checked_remarks;

    public PersonEmergencyDrill(Integer id, String person_emergency_drill_id, String person_id, String vessel_id, String date_familiarization, String details, String filename, String checked_by_id, String date_checked, String checked_remarks){
        this.id = id;
        this.person_emergency_drill_id = person_emergency_drill_id;
        this.person_id = person_id;
        this.vessel_id = vessel_id;
        this.date_familiarization = date_familiarization;
        this.details = details;
        this.filename = filename;
        this.checked_by_id = checked_by_id;
        this.date_checked = date_checked;
        this.checked_remarks = checked_remarks;
    }

    public PersonEmergencyDrill(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_emergency_drill_id() {
        return person_emergency_drill_id;
    }

    public void setPerson_emergency_drill_id(String person_emergency_drill_id) {
        this.person_emergency_drill_id = person_emergency_drill_id;
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

    public String getDate_familiarization() {
        return date_familiarization;
    }

    public void setDate_familiarization(String date_familiarization) {
        this.date_familiarization = date_familiarization;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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
}
