package com.elosoftbiz.etrb_trmf;

public class PersonWorkPractice {
    Integer id;
    String person_work_practice_id;
    String person_id;
    String vessel_id;
    String work_practice_id;
    String checked_by_id;
    String date_checked;
    String checked_remarks;

    public  PersonWorkPractice(Integer id, String person_work_practice_id, String person_id, String vessel_id, String work_practice_id, String checked_by_id, String date_checked, String checked_remarks){
        this.id = id;
        this.person_work_practice_id = person_work_practice_id;
        this.person_id = person_id;
        this.vessel_id = vessel_id;
        this.work_practice_id = work_practice_id;
        this.checked_by_id = checked_by_id;
        this.date_checked = date_checked;
        this.checked_remarks = checked_remarks;
    }

    public  PersonWorkPractice(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_work_practice_id() {
        return person_work_practice_id;
    }

    public void setPerson_work_practice_id(String person_work_practice_id) {
        this.person_work_practice_id = person_work_practice_id;
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

    public String getWork_practice_id() {
        return work_practice_id;
    }

    public void setWork_practice_id(String work_practice_id) {
        this.work_practice_id = work_practice_id;
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
