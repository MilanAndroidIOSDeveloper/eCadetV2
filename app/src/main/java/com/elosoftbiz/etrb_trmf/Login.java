package com.elosoftbiz.etrb_trmf;

public class Login {
    Integer id;
    String login_id, email, login_name, login_pass, session_id, login_type_id, initial, lname, fname, mname, company_id, designation;

    public Login(String login_id, Integer id, String email, String login_name, String login_pass, String session_id, String login_type_id, String initial, String lname, String fname, String mname, String company_id, String designation){
        this.login_id=login_id;
        this.id = id;
        this.email = email;
        this.login_name = login_name;
        this.login_pass = login_pass;
        this.session_id = session_id;
        this.login_type_id = login_type_id;
        this.initial = initial;
        this.lname = lname;
        this.fname = fname;
        this.mname = mname;
        this.company_id = company_id;
        this.designation = designation;
    }

    public Login(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin_name() {
        return login_name;
    }

    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }

    public String getLogin_pass() {
        return login_pass;
    }

    public void setLogin_pass(String login_pass) {
        this.login_pass = login_pass;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getLogin_type_id() {
        return login_type_id;
    }

    public void setLogin_type_id(String login_type_id) {
        this.login_type_id = login_type_id;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
