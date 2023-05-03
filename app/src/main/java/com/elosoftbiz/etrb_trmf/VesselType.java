package com.elosoftbiz.etrb_trmf;

public class VesselType {
    Integer id;
    String vessel_type_id, desc_vessel_type;

    public VesselType(Integer id, String vessel_type_id, String desc_vessel_type){
        this.id = id;
        this.vessel_type_id = vessel_type_id;
        this.desc_vessel_type = desc_vessel_type;
    }
    public VesselType(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVessel_type_id() {
        return vessel_type_id;
    }

    public void setVessel_type_id(String vessel_type_id) {
        this.vessel_type_id = vessel_type_id;
    }

    public String getDesc_vessel_type() {
        return desc_vessel_type;
    }

    public void setDesc_vessel_type(String desc_vessel_type) {
        this.desc_vessel_type = desc_vessel_type;
    }
}
