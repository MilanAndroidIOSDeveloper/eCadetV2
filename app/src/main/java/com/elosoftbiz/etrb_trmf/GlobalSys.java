package com.elosoftbiz.etrb_trmf;

public class GlobalSys {
    Integer id, days_on_board, submission_alert_pct, submission_alert_days, assessment_alert_pct, assessment_alert_days, company_alert_days;
    String global_sys_id;

    public GlobalSys(Integer id, String global_sys_id, Integer days_on_board, Integer submission_alert_pct, Integer submission_alert_days, Integer assessment_alert_pct, Integer assessment_alert_days, Integer company_alert_days ){
        this.id = id;
        this.global_sys_id = global_sys_id;
        this.days_on_board = days_on_board;
        this.submission_alert_pct = submission_alert_pct;
        this.submission_alert_days = submission_alert_days;
        this.assessment_alert_pct = assessment_alert_pct;
        this.assessment_alert_days = assessment_alert_days;
        this.company_alert_days = company_alert_days;
    }

    public GlobalSys(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDays_on_board() {
        return days_on_board;
    }

    public void setDays_on_board(Integer days_on_board) {
        this.days_on_board = days_on_board;
    }

    public Integer getSubmission_alert_pct() {
        return submission_alert_pct;
    }

    public void setSubmission_alert_pct(Integer submission_alert_pct) {
        this.submission_alert_pct = submission_alert_pct;
    }

    public Integer getSubmission_alert_days() {
        return submission_alert_days;
    }

    public void setSubmission_alert_days(Integer submission_alert_days) {
        this.submission_alert_days = submission_alert_days;
    }

    public Integer getAssessment_alert_pct() {
        return assessment_alert_pct;
    }

    public void setAssessment_alert_pct(Integer assessment_alert_pct) {
        this.assessment_alert_pct = assessment_alert_pct;
    }

    public Integer getAssessment_alert_days() {
        return assessment_alert_days;
    }

    public void setAssessment_alert_days(Integer assessment_alert_days) {
        this.assessment_alert_days = assessment_alert_days;
    }

    public Integer getCompany_alert_days() {
        return company_alert_days;
    }

    public void setCompany_alert_days(Integer company_alert_days) {
        this.company_alert_days = company_alert_days;
    }

    public String getGlobal_sys_id() {
        return global_sys_id;
    }

    public void setGlobal_sys_id(String global_sys_id) {
        this.global_sys_id = global_sys_id;
    }
}
