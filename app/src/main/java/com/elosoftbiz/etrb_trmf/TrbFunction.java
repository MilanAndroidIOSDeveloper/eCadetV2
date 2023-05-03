package com.elosoftbiz.etrb_trmf;

public class TrbFunction {
    Integer id, prio;
    String trb_function_id, desc_trb_function, dept, vessel_type_id, dp, ice_class, spec_to, engine_type_id, ref_no_function;

    public TrbFunction(Integer id, String trb_function_id, String desc_trb_function, String dept, Integer prio, String vessel_type_id, String dp, String ice_class, String spec_to, String engine_type_id, String ref_no_function){
        this.id = id;
        this.trb_function_id = trb_function_id;
        this.desc_trb_function = desc_trb_function;
        this.dept = dept;
        this.prio = prio;
        this.vessel_type_id = vessel_type_id;
        this.dp = dp;
        this.ice_class = ice_class;
        this.spec_to = spec_to;
        this.engine_type_id = engine_type_id;
        this.ref_no_function = ref_no_function;
    }

    public  TrbFunction(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPrio() {
        return prio;
    }

    public void setPrio(Integer prio) {
        this.prio = prio;
    }

    public String getTrb_function_id() {
        return trb_function_id;
    }

    public void setTrb_function_id(String trb_function_id) {
        this.trb_function_id = trb_function_id;
    }

    public String getDesc_trb_function() {
        return desc_trb_function;
    }

    public void setDesc_trb_function(String desc_trb_function) {
        this.desc_trb_function = desc_trb_function;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getVessel_type_id() {
        return vessel_type_id;
    }

    public void setVessel_type_id(String vessel_type_id) {
        this.vessel_type_id = vessel_type_id;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getIce_class() {
        return ice_class;
    }

    public void setIce_class(String ice_class) {
        this.ice_class = ice_class;
    }

    public String getSpec_to() {
        return spec_to;
    }

    public void setSpec_to(String spec_to) {
        this.spec_to = spec_to;
    }

    public String getEngine_type_id() {
        return engine_type_id;
    }

    public void setEngine_type_id(String engine_type_id) {
        this.engine_type_id = engine_type_id;
    }

    public String getRef_no_function() {
        return ref_no_function;
    }

    public void setRef_no_function(String ref_no_function) {
        this.ref_no_function = ref_no_function;
    }
}
