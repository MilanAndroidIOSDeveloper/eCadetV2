package com.elosoftbiz.etrb_trmf;

public class Person {
    Integer id , children, batch_no, days_on_board;
    String person_id, code_person, lname, fname, mname, st_address, city_id, province_id, phone, mobile, st_address_province, phone_province;
    String email, gender, civ_status, birth_date, birth_place, spouse_name,	father_name, mother_name, notes, active, date_reg, compman_id;
    String company_id, rank_id, vessel_officer, dept, vessel_id, course_id, school_id, school_admin, type, photo_file, passport_no;
    Double amt_paid, pct_done;
    String cdc_no, indos_no, height, weight, enrtry_date, marks, blood_group, emergency_contact_name, emergency_contact_address, emergency_contact_no;
    String emergency_relationship, nationality, passport_no_issue_date, cdc_no_issue_date, cdc_no_issue_place, full_name, logged_in, w_fr;
    String login_name, login_pass, login_type_id, emergency_email, sponsor_id, dig_signature, srn, srb_no, srb_date, sid_no, passport_date;

    public Person(Integer id, String person_id, String code_person, String lname, String fname, String mname, String st_address, String city_id, String province_id, String phone, String mobile, String st_address_province, String phone_province, String email, String gender, String civ_status, String birth_date, String birth_place, String spouse_name, Integer children, String father_name, String mother_name, String notes, String active, String login_id, String last_update, String date_reg, String compman_id, String company_id, Double amt_paid, String rank_id, String vessel_officer, String dept, String vessel_id, String course_id, String school_id, String school_admin, String type, Integer batch_no, Double pct_done, Integer days_on_board, String photo_file, String passport_no, String cdc_no, String indos_no, String height, String weight, String enrtry_date, String marks, String blood_group, String emergency_contact_name, String emergency_contact_address, String emergency_contact_no, String emergency_relationship, String nationality, String passport_no_issue_date, String cdc_no_issue_date, String cdc_no_issue_place, String full_name, String logged_in, String w_fr, String login_name, String login_pass, String login_type_id, String emergency_email, String sponsor_id, String dig_signature, String srn, String srb_no, String srb_date, String sid_no, String passport_date){
        this.id=id;
        this.person_id = person_id;
        this.code_person=code_person;
        this.lname=lname;
        this.fname=fname;
        this.mname=mname;
        this.st_address=st_address;
        this.city_id=city_id;
        this.province_id=province_id;
        this.phone=phone;
        this.mobile=mobile;
        this.st_address_province=st_address_province;
        this.phone_province=phone_province;
        this.email=email;
        this.gender=gender;
        this.civ_status=civ_status;
        this.birth_date=birth_date;
        this.birth_place=birth_place;
        this.spouse_name=spouse_name;
        this.children=children;
        this.father_name=father_name;
        this.mother_name=mother_name;
        this.notes=notes;
        this.active=active;
        this.date_reg=date_reg;
        this.compman_id=compman_id;
        this.company_id=company_id;
        this.amt_paid=amt_paid;
        this.rank_id=rank_id;
        this.vessel_officer=vessel_officer;
        this.dept=dept;
        this.vessel_id=vessel_id;
        this.course_id=course_id;
        this.school_id=school_id;
        this.school_admin=school_admin;
        this.type= type;
        this.batch_no=batch_no;
        this.pct_done=pct_done;
        this.days_on_board=days_on_board;
        this.photo_file=photo_file;
        this.passport_no=passport_no;
        this.cdc_no=cdc_no;
        this.indos_no=indos_no;
        this.height=height;
        this.weight=weight;
        this.enrtry_date=enrtry_date;
        this.marks=marks;
        this.blood_group=blood_group;
        this.emergency_contact_name=emergency_contact_name;
        this.emergency_contact_address=emergency_contact_address;
        this.emergency_contact_no=emergency_contact_no;
        this.emergency_relationship=emergency_relationship;
        this.nationality=nationality;
        this.passport_no_issue_date=passport_no_issue_date;
        this.cdc_no_issue_date=cdc_no_issue_date;
        this.cdc_no_issue_place=cdc_no_issue_place;
        this.full_name = full_name;
        this.logged_in = logged_in;
        this.w_fr = w_fr;
        this.login_name = login_name;
        this.login_pass = login_pass;
        this.login_type_id =login_type_id;
        this.emergency_email = emergency_email;
        this.sponsor_id = sponsor_id;
        this.dig_signature = dig_signature;
        this.srn = srn;
        this.srb_no = srb_no;
        this.srb_date = srb_date;
        this.sid_no = sid_no;
        this.passport_date = passport_date;
    }

    public Person(){

    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public Integer getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children = children;
    }

    public Integer getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(Integer batch_no) {
        this.batch_no = batch_no;
    }

    public Integer getDays_on_board() {
        return days_on_board;
    }

    public void setDays_on_board(Integer days_on_board) {
        this.days_on_board = days_on_board;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getCode_person() {
        return code_person;
    }

    public void setCode_person(String code_person) {
        this.code_person = code_person;
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

    public String getSt_address() {
        return st_address;
    }

    public void setSt_address(String st_address) {
        this.st_address = st_address;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getProvince_id() {
        return province_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSt_address_province() {
        return st_address_province;
    }

    public void setSt_address_province(String st_address_province) {
        this.st_address_province = st_address_province;
    }

    public String getPhone_province() {
        return phone_province;
    }

    public void setPhone_province(String phone_province) {
        this.phone_province = phone_province;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCiv_status() {
        return civ_status;
    }

    public void setCiv_status(String civ_status) {
        this.civ_status = civ_status;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getBirth_place() {
        return birth_place;
    }

    public void setBirth_place(String birth_place) {
        this.birth_place = birth_place;
    }

    public String getSpouse_name() {
        return spouse_name;
    }

    public void setSpouse_name(String spouse_name) {
        this.spouse_name = spouse_name;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public String getMother_name() {
        return mother_name;
    }

    public void setMother_name(String mother_name) {
        this.mother_name = mother_name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getDate_reg() {
        return date_reg;
    }

    public void setDate_reg(String date_reg) {
        this.date_reg = date_reg;
    }

    public String getCompman_id() {
        return compman_id;
    }

    public void setCompman_id(String compman_id) {
        this.compman_id = compman_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getRank_id() {
        return rank_id;
    }

    public void setRank_id(String rank_id) {
        this.rank_id = rank_id;
    }

    public String getVessel_officer() {
        return vessel_officer;
    }

    public void setVessel_officer(String vessel_officer) {
        this.vessel_officer = vessel_officer;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getVessel_id() {
        return vessel_id;
    }

    public void setVessel_id(String vessel_id) {
        this.vessel_id = vessel_id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getSchool_admin() {
        return school_admin;
    }

    public void setSchool_admin(String school_admin) {
        this.school_admin = school_admin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhoto_file() {
        return photo_file;
    }

    public void setPhoto_file(String photo_file) {
        this.photo_file = photo_file;
    }

    public String getPassport_no() {
        return passport_no;
    }

    public void setPassport_no(String passport_no) {
        this.passport_no = passport_no;
    }

    public Double getAmt_paid() {
        return amt_paid;
    }

    public void setAmt_paid(Double amt_paid) {
        this.amt_paid = amt_paid;
    }

    public Double getPct_done() {
        return pct_done;
    }

    public void setPct_done(Double pct_done) {
        this.pct_done = pct_done;
    }

    public String getCdc_no() {
        return cdc_no;
    }

    public void setCdc_no(String cdc_no) {
        this.cdc_no = cdc_no;
    }

    public String getIndos_no() {
        return indos_no;
    }

    public void setIndos_no(String indos_no) {
        this.indos_no = indos_no;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getEnrtry_date() {
        return enrtry_date;
    }

    public void setEnrtry_date(String enrtry_date) {
        this.enrtry_date = enrtry_date;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getEmergency_contact_name() {
        return emergency_contact_name;
    }

    public void setEmergency_contact_name(String emergency_contact_name) {
        this.emergency_contact_name = emergency_contact_name;
    }

    public String getEmergency_contact_address() {
        return emergency_contact_address;
    }

    public void setEmergency_contact_address(String emergency_contact_address) {
        this.emergency_contact_address = emergency_contact_address;
    }

    public String getEmergency_contact_no() {
        return emergency_contact_no;
    }

    public void setEmergency_contact_no(String emergency_contact_no) {
        this.emergency_contact_no = emergency_contact_no;
    }

    public String getEmergency_relationship() {
        return emergency_relationship;
    }

    public void setEmergency_relationship(String emergency_relationship) {
        this.emergency_relationship = emergency_relationship;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPassport_no_issue_date() {
        return passport_no_issue_date;
    }

    public void setPassport_no_issue_date(String passport_no_issue_date) {
        this.passport_no_issue_date = passport_no_issue_date;
    }

    public String getCdc_no_issue_date() {
        return cdc_no_issue_date;
    }

    public void setCdc_no_issue_date(String cdc_no_issue_date) {
        this.cdc_no_issue_date = cdc_no_issue_date;
    }

    public String getCdc_no_issue_place() {
        return cdc_no_issue_place;
    }

    public void setCdc_no_issue_place(String cdc_no_issue_place) {
        this.cdc_no_issue_place = cdc_no_issue_place;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getLogged_in() {
        return logged_in;
    }

    public void setLogged_in(String logged_in) {
        this.logged_in = logged_in;
    }

    public String getW_fr() {
        return w_fr;
    }

    public void setW_fr(String w_fr) {
        this.w_fr = w_fr;
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

    public String getLogin_type_id() {
        return login_type_id;
    }

    public void setLogin_type_id(String login_type_id) {
        this.login_type_id = login_type_id;
    }

    public String getEmergency_email() {
        return emergency_email;
    }

    public void setEmergency_email(String emergency_email) {
        this.emergency_email = emergency_email;
    }

    public String getSponsor_id() {
        return sponsor_id;
    }

    public void setSponsor_id(String sponsor_id) {
        this.sponsor_id = sponsor_id;
    }

    public String getDig_signature() {
        return dig_signature;
    }

    public void setDig_signature(String dig_signature) {
        this.dig_signature = dig_signature;
    }

    public String getSrn() {
        return srn;
    }

    public void setSrn(String srn) {
        this.srn = srn;
    }

    public String getSrb_no() {
        return srb_no;
    }

    public void setSrb_no(String srb_no) {
        this.srb_no = srb_no;
    }

    public String getSrb_date() {
        return srb_date;
    }

    public void setSrb_date(String srb_date) {
        this.srb_date = srb_date;
    }

    public String getSid_no() {
        return sid_no;
    }

    public void setSid_no(String sid_no) {
        this.sid_no = sid_no;
    }

    public String getPassport_date() {
        return passport_date;
    }

    public void setPassport_date(String passport_date) {
        this.passport_date = passport_date;
    }
}
