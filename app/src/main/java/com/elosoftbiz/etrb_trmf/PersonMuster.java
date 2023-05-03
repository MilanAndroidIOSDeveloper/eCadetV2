package com.elosoftbiz.etrb_trmf;

public class PersonMuster {
    Integer id;
    String person_muster_id, person_id, vessel_id, lifeboat_station, lifeboat_duties, emergency_station, emergency_duties, oil_spill_duties, safety_officer_id, security_officer_id, master_id, date_checked;

    public PersonMuster(String person_muster_id, Integer id, String person_id, String vessel_id, String lifeboat_station, String lifeboat_duties, String emergency_station, String emergency_duties, String oil_spill_duties, String safety_officer_id, String security_officer_id, String master_id, String date_checked){
        this.person_muster_id=person_muster_id;
        this.id = id;
        this.person_id = person_id;
        this.vessel_id = vessel_id;
        this.lifeboat_station = lifeboat_station;
        this.lifeboat_duties = lifeboat_duties;
        this.emergency_station = emergency_station;
        this.emergency_duties = emergency_duties;
        this.oil_spill_duties = oil_spill_duties;
        this.safety_officer_id = safety_officer_id;
        this.security_officer_id = security_officer_id;
        this.master_id = master_id;
        this.date_checked = date_checked;
    }

    public PersonMuster(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_muster_id() {
        return person_muster_id;
    }

    public void setPerson_muster_id(String person_muster_id) {
        this.person_muster_id = person_muster_id;
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

    public String getLifeboat_station() {
        return lifeboat_station;
    }

    public void setLifeboat_station(String lifeboat_station) {
        this.lifeboat_station = lifeboat_station;
    }

    public String getLifeboat_duties() {
        return lifeboat_duties;
    }

    public void setLifeboat_duties(String lifeboat_duties) {
        this.lifeboat_duties = lifeboat_duties;
    }

    public String getEmergency_station() {
        return emergency_station;
    }

    public void setEmergency_station(String emergency_station) {
        this.emergency_station = emergency_station;
    }

    public String getEmergency_duties() {
        return emergency_duties;
    }

    public void setEmergency_duties(String emergency_duties) {
        this.emergency_duties = emergency_duties;
    }

    public String getOil_spill_duties() {
        return oil_spill_duties;
    }

    public void setOil_spill_duties(String oil_spill_duties) {
        this.oil_spill_duties = oil_spill_duties;
    }

    public String getSafety_officer_id() {
        return safety_officer_id;
    }

    public void setSafety_officer_id(String safety_officer_id) {
        this.safety_officer_id = safety_officer_id;
    }

    public String getSecurity_officer_id() {
        return security_officer_id;
    }

    public void setSecurity_officer_id(String security_officer_id) {
        this.security_officer_id = security_officer_id;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public String getDate_checked() {
        return date_checked;
    }

    public void setDate_checked(String date_checked) {
        this.date_checked = date_checked;
    }
}
