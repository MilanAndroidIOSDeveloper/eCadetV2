package com.elosoftbiz.etrb_trmf;

public class PersonVessel {
    Integer id;
    String person_vessel_id, person_id, vessel_id, imo_number, call_sign, flag, length_over_all, breadth, depth, summer_draft, summer_freeboard, gross_tonnage, net_tonnage, dead_weight, light_displacement, fresh_water, immersion_at_load, trimming_moment_d, bale_capacity_d, grain_capacity_d, liquid_capacity_d, refrigerated_capacity_d, container_capacity_d, fresh_water_capacity_d, daily_fresh_water_gen_d, daily_fresh_water_con_d, main_engine_make_e, main_engine_type, main_engine_stroke_e, main_engine_bore_e, main_engine_output, main_engine_reduction_gear_e, main_engine_turbo_charger_e, main_engine_service_speed, main_engine_boiler_d, main_engine_bunker_capacity_d, main_engine_daily_consumption_d, main_engine_steering_gear_d, auxiliary_make_e, auxiliary_type_e, auxiliary_stroke_e, auxiliary_bore_e, auxiliary_output_e, auxiliary_turbo_charger_e, auxiliary_normal_electrical_e, auxiliary_boiler_make_e, auxiliary_boiler_working_pressure_e, auxiliary_boiler_type_waste_e, fuel_main_engine_fuel_type_e, fuel_viscosity_e, fuel_specific_fuel_con_e, fuel_boiler_fuel_type_e, fuel_viscosity_range_e, fuel_generator_fuel_type_e, fuel_bunker_capacity_e, fuel_daily_con_e, others_heavy_fuel_oil_e, others_lub_oil_purifier_e, others_air_compressor_e, others_oily_water_separator_e, others_water_capacity_fw_e, others_water_capacity_dw_e, others_fw_generator_e, others_av_cons_e, others_steering_type_e, others_er_lifting_gear_e, others_swl_e, others_sewage_treatment_e, mooring_natural_fiber_d, mooring_synthetic_fiber_d, mooring_wires_d, mooring_towing_spring_d, anchors_port, anchors_starboard, anchors_stern_d, anchors_spare, anchors_cable, lifesaving_lifeboat_type_d, lifesaving_lifeboat_no, lifesaving_liferaft_no, lifesaving_lifeboat_dimension_d, lifesaving_lifeboat_capacity, lifesaving_liferaft_capacity, lifesaving_lifeboat_davits, lifesaving_lifeboat_fall, lifesaving_lifebuoys_no, fire_extinguisher_no, fire_water, fire_foam, fire_dry_powder, fire_co2, fire_firehoses, fire_breathing_e, fire_breathing_no_e, fire_fixed_fire_system_d, fire_scba_d, cargo_handling_derricks, cargo_handling_cranes, cargo_handling_winches, cargo_handling_other_d, cargo_handling_ballast_d, cargo_handling_tank_d, cargo_handling_pump_no, cargo_handling_pipelines, cargo_handling_type_rating_e, cargo_handling_ballast_pump_e, navigational_radar_d, navigational_log_d, navigational_gps_d, navigational_magnetic_d, navigational_gyro_d, navigational_echo_d, navigational_auto_d, navigational_vhf_d, navigational_mf_hf_d, navigational_sat_d, navigational_ecdis_d, navigational_sart_d, navigational_navtex_d, navigational_ais_d, navigational_vdr_d, checked_by_id, app_by_id, date_checked, date_app, checked_remarks, app_remarks;

    public PersonVessel(String person_vessel_id, Integer id, String person_id, String vessel_id, String imo_number, String call_sign, String flag, String length_over_all, String breadth, String depth, String summer_draft, String summer_freeboard, String gross_tonnage, String net_tonnage, String dead_weight, String light_displacement, String fresh_water, String immersion_at_load, String trimming_moment_d, String bale_capacity_d, String grain_capacity_d, String liquid_capacity_d, String refrigerated_capacity_d, String container_capacity_d, String fresh_water_capacity_d, String daily_fresh_water_gen_d, String daily_fresh_water_con_d, String main_engine_make_e, String main_engine_type, String main_engine_stroke_e, String main_engine_bore_e, String main_engine_output, String main_engine_reduction_gear_e, String main_engine_turbo_charger_e, String main_engine_service_speed, String main_engine_boiler_d, String main_engine_bunker_capacity_d, String main_engine_daily_consumption_d, String main_engine_steering_gear_d, String auxiliary_make_e, String auxiliary_type_e, String auxiliary_stroke_e, String auxiliary_bore_e, String auxiliary_output_e, String auxiliary_turbo_charger_e, String auxiliary_normal_electrical_e, String auxiliary_boiler_make_e, String auxiliary_boiler_working_pressure_e, String auxiliary_boiler_type_waste_e, String fuel_main_engine_fuel_type_e, String fuel_viscosity_e, String fuel_specific_fuel_con_e, String fuel_boiler_fuel_type_e, String fuel_viscosity_range_e, String fuel_generator_fuel_type_e, String fuel_bunker_capacity_e, String fuel_daily_con_e, String others_heavy_fuel_oil_e, String others_lub_oil_purifier_e, String others_air_compressor_e, String others_oily_water_separator_e, String others_water_capacity_fw_e, String others_water_capacity_dw_e, String others_fw_generator_e, String others_av_cons_e, String others_steering_type_e, String others_er_lifting_gear_e, String others_swl_e, String others_sewage_treatment_e, String mooring_natural_fiber_d, String mooring_synthetic_fiber_d, String mooring_wires_d, String mooring_towing_spring_d, String anchors_port, String anchors_starboard, String anchors_stern_d, String anchors_spare, String anchors_cable, String lifesaving_lifeboat_type_d, String lifesaving_lifeboat_no, String lifesaving_liferaft_no, String lifesaving_lifeboat_dimension_d, String lifesaving_lifeboat_capacity, String lifesaving_liferaft_capacity, String lifesaving_lifeboat_davits, String lifesaving_lifeboat_fall, String lifesaving_lifebuoys_no, String fire_extinguisher_no, String fire_water, String fire_foam, String fire_dry_powder, String fire_co2, String fire_firehoses, String fire_breathing_e, String fire_breathing_no_e, String fire_fixed_fire_system_d, String fire_scba_d, String cargo_handling_derricks, String cargo_handling_cranes, String cargo_handling_winches, String cargo_handling_other_d, String cargo_handling_ballast_d, String cargo_handling_tank_d, String cargo_handling_pump_no, String cargo_handling_pipelines, String cargo_handling_type_rating_e, String cargo_handling_ballast_pump_e, String navigational_radar_d, String navigational_log_d, String navigational_gps_d, String navigational_magnetic_d, String navigational_gyro_d, String navigational_echo_d, String navigational_auto_d, String navigational_vhf_d, String navigational_mf_hf_d, String navigational_sat_d, String navigational_ecdis_d, String navigational_sart_d, String navigational_navtex_d, String navigational_ais_d, String navigational_vdr_d, String checked_by_id, String app_by_id, String date_checked, String date_app, String checked_remarks, String app_remarks){
        this.person_vessel_id=person_vessel_id;
        this.id = id;
        this.person_id = person_id;
        this.vessel_id = vessel_id;
        this.imo_number = imo_number;
        this.call_sign = call_sign;
        this.flag = flag;
        this.length_over_all = length_over_all;
        this.breadth = breadth;
        this.depth = depth;
        this.summer_draft = summer_draft;
        this.summer_freeboard = summer_freeboard;
        this.gross_tonnage = gross_tonnage;
        this.net_tonnage = net_tonnage;
        this.dead_weight = dead_weight;
        this.light_displacement = light_displacement;
        this.fresh_water = fresh_water;
        this.immersion_at_load = immersion_at_load;
        this.trimming_moment_d = trimming_moment_d;
        this.bale_capacity_d = bale_capacity_d;
        this.grain_capacity_d = grain_capacity_d;
        this.liquid_capacity_d = liquid_capacity_d;
        this.refrigerated_capacity_d = refrigerated_capacity_d;
        this.container_capacity_d = container_capacity_d;
        this.fresh_water_capacity_d = fresh_water_capacity_d;
        this.daily_fresh_water_gen_d = daily_fresh_water_gen_d;
        this.daily_fresh_water_con_d = daily_fresh_water_con_d;
        this.main_engine_make_e = main_engine_make_e;
        this.main_engine_type = main_engine_type;
        this.main_engine_stroke_e = main_engine_stroke_e;
        this.main_engine_bore_e = main_engine_bore_e;
        this.main_engine_output = main_engine_output;
        this.main_engine_reduction_gear_e = main_engine_reduction_gear_e;
        this.main_engine_turbo_charger_e = main_engine_turbo_charger_e;
        this.main_engine_service_speed = main_engine_service_speed;
        this.main_engine_boiler_d = main_engine_boiler_d;
        this.main_engine_bunker_capacity_d = main_engine_bunker_capacity_d;
        this.main_engine_daily_consumption_d = main_engine_daily_consumption_d;
        this.main_engine_steering_gear_d = main_engine_steering_gear_d;
        this.auxiliary_make_e = auxiliary_make_e;
        this.auxiliary_type_e = auxiliary_type_e;
        this.auxiliary_stroke_e = auxiliary_stroke_e;
        this.auxiliary_bore_e = auxiliary_bore_e;
        this.auxiliary_output_e = auxiliary_output_e;
        this.auxiliary_turbo_charger_e = auxiliary_turbo_charger_e;
        this.auxiliary_normal_electrical_e = auxiliary_normal_electrical_e;
        this.auxiliary_boiler_make_e = auxiliary_boiler_make_e;
        this.auxiliary_boiler_working_pressure_e = auxiliary_boiler_working_pressure_e;
        this.auxiliary_boiler_type_waste_e = auxiliary_boiler_type_waste_e;
        this.fuel_main_engine_fuel_type_e = fuel_main_engine_fuel_type_e;
        this.fuel_viscosity_e = fuel_viscosity_e;
        this.fuel_specific_fuel_con_e = fuel_specific_fuel_con_e;
        this.fuel_boiler_fuel_type_e = fuel_boiler_fuel_type_e;
        this.fuel_viscosity_range_e = fuel_viscosity_range_e;
        this.fuel_generator_fuel_type_e = fuel_generator_fuel_type_e;
        this.fuel_bunker_capacity_e = fuel_bunker_capacity_e;
        this.fuel_daily_con_e = fuel_daily_con_e;
        this.others_heavy_fuel_oil_e = others_heavy_fuel_oil_e;
        this.others_lub_oil_purifier_e = others_lub_oil_purifier_e;
        this.others_air_compressor_e = others_air_compressor_e;
        this.others_oily_water_separator_e = others_oily_water_separator_e;
        this.others_water_capacity_fw_e = others_water_capacity_fw_e;
        this.others_water_capacity_dw_e = others_water_capacity_dw_e;
        this.others_fw_generator_e = others_fw_generator_e;
        this.others_av_cons_e = others_av_cons_e;
        this.others_steering_type_e = others_steering_type_e;
        this.others_er_lifting_gear_e = others_er_lifting_gear_e;
        this.others_swl_e = others_swl_e;
        this.others_sewage_treatment_e = others_sewage_treatment_e;
        this.mooring_natural_fiber_d = mooring_natural_fiber_d;
        this.mooring_synthetic_fiber_d = mooring_synthetic_fiber_d;
        this.mooring_wires_d = mooring_wires_d;
        this.mooring_towing_spring_d = mooring_towing_spring_d;
        this.anchors_port = anchors_port;
        this.anchors_starboard = anchors_starboard;
        this.anchors_stern_d = anchors_stern_d;
        this.anchors_spare = anchors_spare;
        this.anchors_cable = anchors_cable;
        this.lifesaving_lifeboat_type_d = lifesaving_lifeboat_type_d;
        this.lifesaving_lifeboat_no = lifesaving_lifeboat_no;
        this.lifesaving_liferaft_no = lifesaving_liferaft_no;
        this.lifesaving_lifeboat_dimension_d = lifesaving_lifeboat_dimension_d;
        this.lifesaving_lifeboat_capacity = lifesaving_lifeboat_capacity;
        this.lifesaving_liferaft_capacity = lifesaving_liferaft_capacity;
        this.lifesaving_lifeboat_davits = lifesaving_lifeboat_davits;
        this.lifesaving_lifeboat_fall = lifesaving_lifeboat_fall;
        this.lifesaving_lifebuoys_no = lifesaving_lifebuoys_no;
        this.fire_extinguisher_no = fire_extinguisher_no;
        this.fire_water = fire_water;
        this.fire_foam = fire_foam;
        this.fire_dry_powder = fire_dry_powder;
        this.fire_co2 = fire_co2;
        this.fire_firehoses = fire_firehoses;
        this.fire_breathing_e = fire_breathing_e;
        this.fire_breathing_no_e = fire_breathing_no_e;
        this.fire_fixed_fire_system_d = fire_fixed_fire_system_d;
        this.fire_scba_d = fire_scba_d;
        this.cargo_handling_derricks = cargo_handling_derricks;
        this.cargo_handling_cranes = cargo_handling_cranes;
        this.cargo_handling_winches = cargo_handling_winches;
        this.cargo_handling_other_d = cargo_handling_other_d;
        this.cargo_handling_ballast_d = cargo_handling_ballast_d;
        this.cargo_handling_tank_d = cargo_handling_tank_d;
        this.cargo_handling_pump_no = cargo_handling_pump_no;
        this.cargo_handling_pipelines = cargo_handling_pipelines;
        this.cargo_handling_type_rating_e = cargo_handling_type_rating_e;
        this.cargo_handling_ballast_pump_e = cargo_handling_ballast_pump_e;
        this.navigational_radar_d = navigational_radar_d;
        this.navigational_log_d = navigational_log_d;
        this.navigational_gps_d = navigational_gps_d;
        this.navigational_magnetic_d = navigational_magnetic_d;
        this.navigational_gyro_d = navigational_gyro_d;
        this.navigational_echo_d = navigational_echo_d;
        this.navigational_auto_d = navigational_auto_d;
        this.navigational_vhf_d = navigational_vhf_d;
        this.navigational_mf_hf_d = navigational_mf_hf_d;
        this.navigational_sat_d = navigational_sat_d;
        this.navigational_ecdis_d = navigational_ecdis_d;
        this.navigational_sart_d = navigational_sart_d;
        this.navigational_navtex_d = navigational_navtex_d;
        this.navigational_ais_d = navigational_ais_d;
        this.navigational_vdr_d = navigational_vdr_d;
        this.checked_by_id = checked_by_id;
        this.app_by_id = app_by_id;
        this.date_checked = date_checked;
        this.date_app = date_app;
        this.checked_remarks = checked_remarks;
        this.app_remarks = app_remarks;
    }

    public PersonVessel(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_vessel_id() {
        return person_vessel_id;
    }

    public void setPerson_vessel_id(String person_vessel_id) {
        this.person_vessel_id = person_vessel_id;
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

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getLength_over_all() {
        return length_over_all;
    }

    public void setLength_over_all(String length_over_all) {
        this.length_over_all = length_over_all;
    }

    public String getBreadth() {
        return breadth;
    }

    public void setBreadth(String breadth) {
        this.breadth = breadth;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getSummer_draft() {
        return summer_draft;
    }

    public void setSummer_draft(String summer_draft) {
        this.summer_draft = summer_draft;
    }

    public String getSummer_freeboard() {
        return summer_freeboard;
    }

    public void setSummer_freeboard(String summer_freeboard) {
        this.summer_freeboard = summer_freeboard;
    }

    public String getGross_tonnage() {
        return gross_tonnage;
    }

    public void setGross_tonnage(String gross_tonnage) {
        this.gross_tonnage = gross_tonnage;
    }

    public String getNet_tonnage() {
        return net_tonnage;
    }

    public void setNet_tonnage(String net_tonnage) {
        this.net_tonnage = net_tonnage;
    }

    public String getDead_weight() {
        return dead_weight;
    }

    public void setDead_weight(String dead_weight) {
        this.dead_weight = dead_weight;
    }

    public String getLight_displacement() {
        return light_displacement;
    }

    public void setLight_displacement(String light_displacement) {
        this.light_displacement = light_displacement;
    }

    public String getFresh_water() {
        return fresh_water;
    }

    public void setFresh_water(String fresh_water) {
        this.fresh_water = fresh_water;
    }

    public String getImmersion_at_load() {
        return immersion_at_load;
    }

    public void setImmersion_at_load(String immersion_at_load) {
        this.immersion_at_load = immersion_at_load;
    }

    public String getTrimming_moment_d() {
        return trimming_moment_d;
    }

    public void setTrimming_moment_d(String trimming_moment_d) {
        this.trimming_moment_d = trimming_moment_d;
    }

    public String getBale_capacity_d() {
        return bale_capacity_d;
    }

    public void setBale_capacity_d(String bale_capacity_d) {
        this.bale_capacity_d = bale_capacity_d;
    }

    public String getGrain_capacity_d() {
        return grain_capacity_d;
    }

    public void setGrain_capacity_d(String grain_capacity_d) {
        this.grain_capacity_d = grain_capacity_d;
    }

    public String getLiquid_capacity_d() {
        return liquid_capacity_d;
    }

    public void setLiquid_capacity_d(String liquid_capacity_d) {
        this.liquid_capacity_d = liquid_capacity_d;
    }

    public String getRefrigerated_capacity_d() {
        return refrigerated_capacity_d;
    }

    public void setRefrigerated_capacity_d(String refrigerated_capacity_d) {
        this.refrigerated_capacity_d = refrigerated_capacity_d;
    }

    public String getContainer_capacity_d() {
        return container_capacity_d;
    }

    public void setContainer_capacity_d(String container_capacity_d) {
        this.container_capacity_d = container_capacity_d;
    }

    public String getFresh_water_capacity_d() {
        return fresh_water_capacity_d;
    }

    public void setFresh_water_capacity_d(String fresh_water_capacity_d) {
        this.fresh_water_capacity_d = fresh_water_capacity_d;
    }

    public String getDaily_fresh_water_gen_d() {
        return daily_fresh_water_gen_d;
    }

    public void setDaily_fresh_water_gen_d(String daily_fresh_water_gen_d) {
        this.daily_fresh_water_gen_d = daily_fresh_water_gen_d;
    }

    public String getDaily_fresh_water_con_d() {
        return daily_fresh_water_con_d;
    }

    public void setDaily_fresh_water_con_d(String daily_fresh_water_con_d) {
        this.daily_fresh_water_con_d = daily_fresh_water_con_d;
    }

    public String getMain_engine_make_e() {
        return main_engine_make_e;
    }

    public void setMain_engine_make_e(String main_engine_make_e) {
        this.main_engine_make_e = main_engine_make_e;
    }

    public String getMain_engine_type() {
        return main_engine_type;
    }

    public void setMain_engine_type(String main_engine_type) {
        this.main_engine_type = main_engine_type;
    }

    public String getMain_engine_stroke_e() {
        return main_engine_stroke_e;
    }

    public void setMain_engine_stroke_e(String main_engine_stroke_e) {
        this.main_engine_stroke_e = main_engine_stroke_e;
    }

    public String getMain_engine_bore_e() {
        return main_engine_bore_e;
    }

    public void setMain_engine_bore_e(String main_engine_bore_e) {
        this.main_engine_bore_e = main_engine_bore_e;
    }

    public String getMain_engine_output() {
        return main_engine_output;
    }

    public void setMain_engine_output(String main_engine_output) {
        this.main_engine_output = main_engine_output;
    }

    public String getMain_engine_reduction_gear_e() {
        return main_engine_reduction_gear_e;
    }

    public void setMain_engine_reduction_gear_e(String main_engine_reduction_gear_e) {
        this.main_engine_reduction_gear_e = main_engine_reduction_gear_e;
    }

    public String getMain_engine_turbo_charger_e() {
        return main_engine_turbo_charger_e;
    }

    public void setMain_engine_turbo_charger_e(String main_engine_turbo_charger_e) {
        this.main_engine_turbo_charger_e = main_engine_turbo_charger_e;
    }

    public String getMain_engine_service_speed() {
        return main_engine_service_speed;
    }

    public void setMain_engine_service_speed(String main_engine_service_speed) {
        this.main_engine_service_speed = main_engine_service_speed;
    }

    public String getMain_engine_boiler_d() {
        return main_engine_boiler_d;
    }

    public void setMain_engine_boiler_d(String main_engine_boiler_d) {
        this.main_engine_boiler_d = main_engine_boiler_d;
    }

    public String getMain_engine_bunker_capacity_d() {
        return main_engine_bunker_capacity_d;
    }

    public void setMain_engine_bunker_capacity_d(String main_engine_bunker_capacity_d) {
        this.main_engine_bunker_capacity_d = main_engine_bunker_capacity_d;
    }

    public String getMain_engine_daily_consumption_d() {
        return main_engine_daily_consumption_d;
    }

    public void setMain_engine_daily_consumption_d(String main_engine_daily_consumption_d) {
        this.main_engine_daily_consumption_d = main_engine_daily_consumption_d;
    }

    public String getMain_engine_steering_gear_d() {
        return main_engine_steering_gear_d;
    }

    public void setMain_engine_steering_gear_d(String main_engine_steering_gear_d) {
        this.main_engine_steering_gear_d = main_engine_steering_gear_d;
    }

    public String getAuxiliary_make_e() {
        return auxiliary_make_e;
    }

    public void setAuxiliary_make_e(String auxiliary_make_e) {
        this.auxiliary_make_e = auxiliary_make_e;
    }

    public String getAuxiliary_type_e() {
        return auxiliary_type_e;
    }

    public void setAuxiliary_type_e(String auxiliary_type_e) {
        this.auxiliary_type_e = auxiliary_type_e;
    }

    public String getAuxiliary_stroke_e() {
        return auxiliary_stroke_e;
    }

    public void setAuxiliary_stroke_e(String auxiliary_stroke_e) {
        this.auxiliary_stroke_e = auxiliary_stroke_e;
    }

    public String getAuxiliary_bore_e() {
        return auxiliary_bore_e;
    }

    public void setAuxiliary_bore_e(String auxiliary_bore_e) {
        this.auxiliary_bore_e = auxiliary_bore_e;
    }

    public String getAuxiliary_output_e() {
        return auxiliary_output_e;
    }

    public void setAuxiliary_output_e(String auxiliary_output_e) {
        this.auxiliary_output_e = auxiliary_output_e;
    }

    public String getAuxiliary_turbo_charger_e() {
        return auxiliary_turbo_charger_e;
    }

    public void setAuxiliary_turbo_charger_e(String auxiliary_turbo_charger_e) {
        this.auxiliary_turbo_charger_e = auxiliary_turbo_charger_e;
    }

    public String getAuxiliary_normal_electrical_e() {
        return auxiliary_normal_electrical_e;
    }

    public void setAuxiliary_normal_electrical_e(String auxiliary_normal_electrical_e) {
        this.auxiliary_normal_electrical_e = auxiliary_normal_electrical_e;
    }

    public String getAuxiliary_boiler_make_e() {
        return auxiliary_boiler_make_e;
    }

    public void setAuxiliary_boiler_make_e(String auxiliary_boiler_make_e) {
        this.auxiliary_boiler_make_e = auxiliary_boiler_make_e;
    }

    public String getAuxiliary_boiler_working_pressure_e() {
        return auxiliary_boiler_working_pressure_e;
    }

    public void setAuxiliary_boiler_working_pressure_e(String auxiliary_boiler_working_pressure_e) {
        this.auxiliary_boiler_working_pressure_e = auxiliary_boiler_working_pressure_e;
    }

    public String getAuxiliary_boiler_type_waste_e() {
        return auxiliary_boiler_type_waste_e;
    }

    public void setAuxiliary_boiler_type_waste_e(String auxiliary_boiler_type_waste_e) {
        this.auxiliary_boiler_type_waste_e = auxiliary_boiler_type_waste_e;
    }

    public String getFuel_main_engine_fuel_type_e() {
        return fuel_main_engine_fuel_type_e;
    }

    public void setFuel_main_engine_fuel_type_e(String fuel_main_engine_fuel_type_e) {
        this.fuel_main_engine_fuel_type_e = fuel_main_engine_fuel_type_e;
    }

    public String getFuel_viscosity_e() {
        return fuel_viscosity_e;
    }

    public void setFuel_viscosity_e(String fuel_viscosity_e) {
        this.fuel_viscosity_e = fuel_viscosity_e;
    }

    public String getFuel_specific_fuel_con_e() {
        return fuel_specific_fuel_con_e;
    }

    public void setFuel_specific_fuel_con_e(String fuel_specific_fuel_con_e) {
        this.fuel_specific_fuel_con_e = fuel_specific_fuel_con_e;
    }

    public String getFuel_boiler_fuel_type_e() {
        return fuel_boiler_fuel_type_e;
    }

    public void setFuel_boiler_fuel_type_e(String fuel_boiler_fuel_type_e) {
        this.fuel_boiler_fuel_type_e = fuel_boiler_fuel_type_e;
    }

    public String getFuel_viscosity_range_e() {
        return fuel_viscosity_range_e;
    }

    public void setFuel_viscosity_range_e(String fuel_viscosity_range_e) {
        this.fuel_viscosity_range_e = fuel_viscosity_range_e;
    }

    public String getFuel_generator_fuel_type_e() {
        return fuel_generator_fuel_type_e;
    }

    public void setFuel_generator_fuel_type_e(String fuel_generator_fuel_type_e) {
        this.fuel_generator_fuel_type_e = fuel_generator_fuel_type_e;
    }

    public String getFuel_bunker_capacity_e() {
        return fuel_bunker_capacity_e;
    }

    public void setFuel_bunker_capacity_e(String fuel_bunker_capacity_e) {
        this.fuel_bunker_capacity_e = fuel_bunker_capacity_e;
    }

    public String getFuel_daily_con_e() {
        return fuel_daily_con_e;
    }

    public void setFuel_daily_con_e(String fuel_daily_con_e) {
        this.fuel_daily_con_e = fuel_daily_con_e;
    }

    public String getOthers_heavy_fuel_oil_e() {
        return others_heavy_fuel_oil_e;
    }

    public void setOthers_heavy_fuel_oil_e(String others_heavy_fuel_oil_e) {
        this.others_heavy_fuel_oil_e = others_heavy_fuel_oil_e;
    }

    public String getOthers_lub_oil_purifier_e() {
        return others_lub_oil_purifier_e;
    }

    public void setOthers_lub_oil_purifier_e(String others_lub_oil_purifier_e) {
        this.others_lub_oil_purifier_e = others_lub_oil_purifier_e;
    }

    public String getOthers_air_compressor_e() {
        return others_air_compressor_e;
    }

    public void setOthers_air_compressor_e(String others_air_compressor_e) {
        this.others_air_compressor_e = others_air_compressor_e;
    }

    public String getOthers_oily_water_separator_e() {
        return others_oily_water_separator_e;
    }

    public void setOthers_oily_water_separator_e(String others_oily_water_separator_e) {
        this.others_oily_water_separator_e = others_oily_water_separator_e;
    }

    public String getOthers_water_capacity_fw_e() {
        return others_water_capacity_fw_e;
    }

    public void setOthers_water_capacity_fw_e(String others_water_capacity_fw_e) {
        this.others_water_capacity_fw_e = others_water_capacity_fw_e;
    }

    public String getOthers_water_capacity_dw_e() {
        return others_water_capacity_dw_e;
    }

    public void setOthers_water_capacity_dw_e(String others_water_capacity_dw_e) {
        this.others_water_capacity_dw_e = others_water_capacity_dw_e;
    }

    public String getOthers_fw_generator_e() {
        return others_fw_generator_e;
    }

    public void setOthers_fw_generator_e(String others_fw_generator_e) {
        this.others_fw_generator_e = others_fw_generator_e;
    }

    public String getOthers_av_cons_e() {
        return others_av_cons_e;
    }

    public void setOthers_av_cons_e(String others_av_cons_e) {
        this.others_av_cons_e = others_av_cons_e;
    }

    public String getOthers_steering_type_e() {
        return others_steering_type_e;
    }

    public void setOthers_steering_type_e(String others_steering_type_e) {
        this.others_steering_type_e = others_steering_type_e;
    }

    public String getOthers_er_lifting_gear_e() {
        return others_er_lifting_gear_e;
    }

    public void setOthers_er_lifting_gear_e(String others_er_lifting_gear_e) {
        this.others_er_lifting_gear_e = others_er_lifting_gear_e;
    }

    public String getOthers_swl_e() {
        return others_swl_e;
    }

    public void setOthers_swl_e(String others_swl_e) {
        this.others_swl_e = others_swl_e;
    }

    public String getOthers_sewage_treatment_e() {
        return others_sewage_treatment_e;
    }

    public void setOthers_sewage_treatment_e(String others_sewage_treatment_e) {
        this.others_sewage_treatment_e = others_sewage_treatment_e;
    }

    public String getMooring_natural_fiber_d() {
        return mooring_natural_fiber_d;
    }

    public void setMooring_natural_fiber_d(String mooring_natural_fiber_d) {
        this.mooring_natural_fiber_d = mooring_natural_fiber_d;
    }

    public String getMooring_synthetic_fiber_d() {
        return mooring_synthetic_fiber_d;
    }

    public void setMooring_synthetic_fiber_d(String mooring_synthetic_fiber_d) {
        this.mooring_synthetic_fiber_d = mooring_synthetic_fiber_d;
    }

    public String getMooring_wires_d() {
        return mooring_wires_d;
    }

    public void setMooring_wires_d(String mooring_wires_d) {
        this.mooring_wires_d = mooring_wires_d;
    }

    public String getMooring_towing_spring_d() {
        return mooring_towing_spring_d;
    }

    public void setMooring_towing_spring_d(String mooring_towing_spring_d) {
        this.mooring_towing_spring_d = mooring_towing_spring_d;
    }

    public String getAnchors_port() {
        return anchors_port;
    }

    public void setAnchors_port(String anchors_port) {
        this.anchors_port = anchors_port;
    }

    public String getAnchors_starboard() {
        return anchors_starboard;
    }

    public void setAnchors_starboard(String anchors_starboard) {
        this.anchors_starboard = anchors_starboard;
    }

    public String getAnchors_stern_d() {
        return anchors_stern_d;
    }

    public void setAnchors_stern_d(String anchors_stern_d) {
        this.anchors_stern_d = anchors_stern_d;
    }

    public String getAnchors_spare() {
        return anchors_spare;
    }

    public void setAnchors_spare(String anchors_spare) {
        this.anchors_spare = anchors_spare;
    }

    public String getAnchors_cable() {
        return anchors_cable;
    }

    public void setAnchors_cable(String anchors_cable) {
        this.anchors_cable = anchors_cable;
    }

    public String getLifesaving_lifeboat_type_d() {
        return lifesaving_lifeboat_type_d;
    }

    public void setLifesaving_lifeboat_type_d(String lifesaving_lifeboat_type_d) {
        this.lifesaving_lifeboat_type_d = lifesaving_lifeboat_type_d;
    }

    public String getLifesaving_lifeboat_no() {
        return lifesaving_lifeboat_no;
    }

    public void setLifesaving_lifeboat_no(String lifesaving_lifeboat_no) {
        this.lifesaving_lifeboat_no = lifesaving_lifeboat_no;
    }

    public String getLifesaving_liferaft_no() {
        return lifesaving_liferaft_no;
    }

    public void setLifesaving_liferaft_no(String lifesaving_liferaft_no) {
        this.lifesaving_liferaft_no = lifesaving_liferaft_no;
    }

    public String getLifesaving_lifeboat_dimension_d() {
        return lifesaving_lifeboat_dimension_d;
    }

    public void setLifesaving_lifeboat_dimension_d(String lifesaving_lifeboat_dimension_d) {
        this.lifesaving_lifeboat_dimension_d = lifesaving_lifeboat_dimension_d;
    }

    public String getLifesaving_lifeboat_capacity() {
        return lifesaving_lifeboat_capacity;
    }

    public void setLifesaving_lifeboat_capacity(String lifesaving_lifeboat_capacity) {
        this.lifesaving_lifeboat_capacity = lifesaving_lifeboat_capacity;
    }

    public String getLifesaving_liferaft_capacity() {
        return lifesaving_liferaft_capacity;
    }

    public void setLifesaving_liferaft_capacity(String lifesaving_liferaft_capacity) {
        this.lifesaving_liferaft_capacity = lifesaving_liferaft_capacity;
    }

    public String getLifesaving_lifeboat_davits() {
        return lifesaving_lifeboat_davits;
    }

    public void setLifesaving_lifeboat_davits(String lifesaving_lifeboat_davits) {
        this.lifesaving_lifeboat_davits = lifesaving_lifeboat_davits;
    }

    public String getLifesaving_lifeboat_fall() {
        return lifesaving_lifeboat_fall;
    }

    public void setLifesaving_lifeboat_fall(String lifesaving_lifeboat_fall) {
        this.lifesaving_lifeboat_fall = lifesaving_lifeboat_fall;
    }

    public String getLifesaving_lifebuoys_no() {
        return lifesaving_lifebuoys_no;
    }

    public void setLifesaving_lifebuoys_no(String lifesaving_lifebuoys_no) {
        this.lifesaving_lifebuoys_no = lifesaving_lifebuoys_no;
    }

    public String getFire_extinguisher_no() {
        return fire_extinguisher_no;
    }

    public void setFire_extinguisher_no(String fire_extinguisher_no) {
        this.fire_extinguisher_no = fire_extinguisher_no;
    }

    public String getFire_water() {
        return fire_water;
    }

    public void setFire_water(String fire_water) {
        this.fire_water = fire_water;
    }

    public String getFire_foam() {
        return fire_foam;
    }

    public void setFire_foam(String fire_foam) {
        this.fire_foam = fire_foam;
    }

    public String getFire_dry_powder() {
        return fire_dry_powder;
    }

    public void setFire_dry_powder(String fire_dry_powder) {
        this.fire_dry_powder = fire_dry_powder;
    }

    public String getFire_co2() {
        return fire_co2;
    }

    public void setFire_co2(String fire_co2) {
        this.fire_co2 = fire_co2;
    }

    public String getFire_firehoses() {
        return fire_firehoses;
    }

    public void setFire_firehoses(String fire_firehoses) {
        this.fire_firehoses = fire_firehoses;
    }

    public String getFire_breathing_e() {
        return fire_breathing_e;
    }

    public void setFire_breathing_e(String fire_breathing_e) {
        this.fire_breathing_e = fire_breathing_e;
    }

    public String getFire_breathing_no_e() {
        return fire_breathing_no_e;
    }

    public void setFire_breathing_no_e(String fire_breathing_no_e) {
        this.fire_breathing_no_e = fire_breathing_no_e;
    }

    public String getFire_fixed_fire_system_d() {
        return fire_fixed_fire_system_d;
    }

    public void setFire_fixed_fire_system_d(String fire_fixed_fire_system_d) {
        this.fire_fixed_fire_system_d = fire_fixed_fire_system_d;
    }

    public String getFire_scba_d() {
        return fire_scba_d;
    }

    public void setFire_scba_d(String fire_scba_d) {
        this.fire_scba_d = fire_scba_d;
    }

    public String getCargo_handling_derricks() {
        return cargo_handling_derricks;
    }

    public void setCargo_handling_derricks(String cargo_handling_derricks) {
        this.cargo_handling_derricks = cargo_handling_derricks;
    }

    public String getCargo_handling_cranes() {
        return cargo_handling_cranes;
    }

    public void setCargo_handling_cranes(String cargo_handling_cranes) {
        this.cargo_handling_cranes = cargo_handling_cranes;
    }

    public String getCargo_handling_winches() {
        return cargo_handling_winches;
    }

    public void setCargo_handling_winches(String cargo_handling_winches) {
        this.cargo_handling_winches = cargo_handling_winches;
    }

    public String getCargo_handling_other_d() {
        return cargo_handling_other_d;
    }

    public void setCargo_handling_other_d(String cargo_handling_other_d) {
        this.cargo_handling_other_d = cargo_handling_other_d;
    }

    public String getCargo_handling_ballast_d() {
        return cargo_handling_ballast_d;
    }

    public void setCargo_handling_ballast_d(String cargo_handling_ballast_d) {
        this.cargo_handling_ballast_d = cargo_handling_ballast_d;
    }

    public String getCargo_handling_tank_d() {
        return cargo_handling_tank_d;
    }

    public void setCargo_handling_tank_d(String cargo_handling_tank_d) {
        this.cargo_handling_tank_d = cargo_handling_tank_d;
    }

    public String getCargo_handling_pump_no() {
        return cargo_handling_pump_no;
    }

    public void setCargo_handling_pump_no(String cargo_handling_pump_no) {
        this.cargo_handling_pump_no = cargo_handling_pump_no;
    }

    public String getCargo_handling_pipelines() {
        return cargo_handling_pipelines;
    }

    public void setCargo_handling_pipelines(String cargo_handling_pipelines) {
        this.cargo_handling_pipelines = cargo_handling_pipelines;
    }

    public String getCargo_handling_type_rating_e() {
        return cargo_handling_type_rating_e;
    }

    public void setCargo_handling_type_rating_e(String cargo_handling_type_rating_e) {
        this.cargo_handling_type_rating_e = cargo_handling_type_rating_e;
    }

    public String getCargo_handling_ballast_pump_e() {
        return cargo_handling_ballast_pump_e;
    }

    public void setCargo_handling_ballast_pump_e(String cargo_handling_ballast_pump_e) {
        this.cargo_handling_ballast_pump_e = cargo_handling_ballast_pump_e;
    }

    public String getNavigational_radar_d() {
        return navigational_radar_d;
    }

    public void setNavigational_radar_d(String navigational_radar_d) {
        this.navigational_radar_d = navigational_radar_d;
    }

    public String getNavigational_log_d() {
        return navigational_log_d;
    }

    public void setNavigational_log_d(String navigational_log_d) {
        this.navigational_log_d = navigational_log_d;
    }

    public String getNavigational_gps_d() {
        return navigational_gps_d;
    }

    public void setNavigational_gps_d(String navigational_gps_d) {
        this.navigational_gps_d = navigational_gps_d;
    }

    public String getNavigational_magnetic_d() {
        return navigational_magnetic_d;
    }

    public void setNavigational_magnetic_d(String navigational_magnetic_d) {
        this.navigational_magnetic_d = navigational_magnetic_d;
    }

    public String getNavigational_gyro_d() {
        return navigational_gyro_d;
    }

    public void setNavigational_gyro_d(String navigational_gyro_d) {
        this.navigational_gyro_d = navigational_gyro_d;
    }

    public String getNavigational_echo_d() {
        return navigational_echo_d;
    }

    public void setNavigational_echo_d(String navigational_echo_d) {
        this.navigational_echo_d = navigational_echo_d;
    }

    public String getNavigational_auto_d() {
        return navigational_auto_d;
    }

    public void setNavigational_auto_d(String navigational_auto_d) {
        this.navigational_auto_d = navigational_auto_d;
    }

    public String getNavigational_vhf_d() {
        return navigational_vhf_d;
    }

    public void setNavigational_vhf_d(String navigational_vhf_d) {
        this.navigational_vhf_d = navigational_vhf_d;
    }

    public String getNavigational_mf_hf_d() {
        return navigational_mf_hf_d;
    }

    public void setNavigational_mf_hf_d(String navigational_mf_hf_d) {
        this.navigational_mf_hf_d = navigational_mf_hf_d;
    }

    public String getNavigational_sat_d() {
        return navigational_sat_d;
    }

    public void setNavigational_sat_d(String navigational_sat_d) {
        this.navigational_sat_d = navigational_sat_d;
    }

    public String getNavigational_ecdis_d() {
        return navigational_ecdis_d;
    }

    public void setNavigational_ecdis_d(String navigational_ecdis_d) {
        this.navigational_ecdis_d = navigational_ecdis_d;
    }

    public String getNavigational_sart_d() {
        return navigational_sart_d;
    }

    public void setNavigational_sart_d(String navigational_sart_d) {
        this.navigational_sart_d = navigational_sart_d;
    }

    public String getNavigational_navtex_d() {
        return navigational_navtex_d;
    }

    public void setNavigational_navtex_d(String navigational_navtex_d) {
        this.navigational_navtex_d = navigational_navtex_d;
    }

    public String getNavigational_ais_d() {
        return navigational_ais_d;
    }

    public void setNavigational_ais_d(String navigational_ais_d) {
        this.navigational_ais_d = navigational_ais_d;
    }

    public String getNavigational_vdr_d() {
        return navigational_vdr_d;
    }

    public void setNavigational_vdr_d(String navigational_vdr_d) {
        this.navigational_vdr_d = navigational_vdr_d;
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
}
