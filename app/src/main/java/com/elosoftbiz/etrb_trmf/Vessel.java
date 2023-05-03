package com.elosoftbiz.etrb_trmf;

public class Vessel {
    Integer id, year_built;
    String vessel_id, name_vessel, owner_company_id, operator_company_id, flag_registry_id, hp, kw, grt, trade_type, imo_number, call_sign, vessel_type_id, dp, ice_class;
    String motor, gt, st;

    public Vessel(String vessel_id, Integer id, String name_vessel, String owner_company_id, String operator_company_id, Integer year_built, String flag_registry_id, String hp, String kw, String grt, String trade_type, String imo_number, String call_sign, String vessel_type_id, String dp, String ice_class, String motor, String st, String gt){
        this.vessel_id=vessel_id;
        this.id = id;
        this.name_vessel = name_vessel;
        this.owner_company_id = owner_company_id;
        this.operator_company_id = operator_company_id;
        this.year_built = year_built;
        this.flag_registry_id = flag_registry_id;
        this.hp = hp;
        this.kw = kw;
        this.grt = grt;
        this.trade_type = trade_type;
        this.imo_number = imo_number;
        this.call_sign = call_sign;
        this.vessel_type_id = vessel_type_id;
        this.dp = dp;
        this.ice_class = ice_class;
        this.motor = motor;
        this.st = st;
        this.gt = gt;
    }
    public Vessel(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getYear_built() {
        return year_built;
    }

    public void setYear_built(Integer year_built) {
        this.year_built = year_built;
    }

    public String getVessel_id() {
        return vessel_id;
    }

    public void setVessel_id(String vessel_id) {
        this.vessel_id = vessel_id;
    }

    public String getName_vessel() {
        return name_vessel;
    }

    public void setName_vessel(String name_vessel) {
        this.name_vessel = name_vessel;
    }

    public String getOwner_company_id() {
        return owner_company_id;
    }

    public void setOwner_company_id(String owner_company_id) {
        this.owner_company_id = owner_company_id;
    }

    public String getOperator_company_id() {
        return operator_company_id;
    }

    public void setOperator_company_id(String operator_company_id) {
        this.operator_company_id = operator_company_id;
    }

    public String getFlag_registry_id() {
        return flag_registry_id;
    }

    public void setFlag_registry_id(String flag_registry_id) {
        this.flag_registry_id = flag_registry_id;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getKw() {
        return kw;
    }

    public void setKw(String kw) {
        this.kw = kw;
    }

    public String getGrt() {
        return grt;
    }

    public void setGrt(String grt) {
        this.grt = grt;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getImo_number() {
        return imo_number;
    }

    public void setImo_number(String imo_number) {
        this.imo_number = imo_number;
    }

    public String getCall_sign() {
        return call_sign;
    }

    public void setCall_sign(String call_sign) {
        this.call_sign = call_sign;
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

    public String getMotor() {
        return motor;
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getGt() {
        return gt;
    }

    public void setGt(String gt) {
        this.gt = gt;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }
}
