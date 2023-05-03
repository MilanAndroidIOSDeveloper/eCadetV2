package com.elosoftbiz.etrb_trmf;

public class PersonEducationD {
    String person_education_d_id, person_education_h_id, subject;
    Double grade;
    Integer id;

    public PersonEducationD(String person_education_d_id, Integer id, String person_education_h_id, String subject, Double grade){
        this.person_education_d_id=person_education_d_id;
        this.id = id;
        this.person_education_h_id = person_education_h_id;
        this.subject = subject;
        this.grade = grade;
    }

    public PersonEducationD(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_education_d_id() {
        return person_education_d_id;
    }

    public void setPerson_education_d_id(String person_education_d_id) {
        this.person_education_d_id = person_education_d_id;
    }

    public String getPerson_education_h_id() {
        return person_education_h_id;
    }

    public void setPerson_education_h_id(String person_education_h_id) {
        this.person_education_h_id = person_education_h_id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }
}
