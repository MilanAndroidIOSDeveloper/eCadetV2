package com.elosoftbiz.etrb_trmf;

public class PersonEmergencyAlarm {
    Integer id;
    String person_emergency_alarm_id;
    String person_id;
    String vessel_id;
    String alarm_signal_id;
    String checked_by_id;
    String date_checked;
    String checked_remarks;
    String na;

    public PersonEmergencyAlarm(Integer id, String person_emergency_alarm_id, String person_id, String vessel_id, String alarm_signal_id, String checked_by_id, String date_checked, String checked_remarks, String na){
        this.id = id;
        this.person_emergency_alarm_id = person_emergency_alarm_id;
        this.person_id = person_id;
        this.vessel_id = vessel_id;
        this.alarm_signal_id = alarm_signal_id;
        this.checked_by_id = checked_by_id;
        this.date_checked = date_checked;
        this.checked_remarks = checked_remarks;
        this.na = na;

    }

    public PersonEmergencyAlarm(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_emergency_alarm_id() {
        return person_emergency_alarm_id;
    }

    public void setPerson_emergency_alarm_id(String person_emergency_alarm_id) {
        this.person_emergency_alarm_id = person_emergency_alarm_id;
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

    public String getAlarm_signal_id() {
        return alarm_signal_id;
    }

    public void setAlarm_signal_id(String alarm_signal_id) {
        this.alarm_signal_id = alarm_signal_id;
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

    public String getNa() {
        return na;
    }

    public void setNa(String na) {
        this.na = na;
    }
}
