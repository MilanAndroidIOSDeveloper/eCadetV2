package com.elosoftbiz.etrb_trmf;

public class Direction {
    Integer id;
    String direction_id;
    String code_direction;
    String desc_direction;
    String prio;
    String login_id;
    String last_update;

    public Direction(Integer id, String direction_id, String code_direction, String desc_direction, String prio, String login_id, String last_update){
        this.id = id;
        this.direction_id = direction_id;
        this.code_direction = code_direction;
        this.desc_direction = desc_direction;
        this.prio = prio;
        this.login_id = login_id;
        this.last_update = last_update;
    }

    public Direction(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDirection_id() {
        return direction_id;
    }

    public void setDirection_id(String direction_id) {
        this.direction_id = direction_id;
    }

    public String getCode_direction() {
        return code_direction;
    }

    public void setCode_direction(String code_direction) {
        this.code_direction = code_direction;
    }

    public String getDesc_direction() {
        return desc_direction;
    }

    public void setDesc_direction(String desc_direction) {
        this.desc_direction = desc_direction;
    }

    public String getPrio() {
        return prio;
    }

    public void setPrio(String prio) {
        this.prio = prio;
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
