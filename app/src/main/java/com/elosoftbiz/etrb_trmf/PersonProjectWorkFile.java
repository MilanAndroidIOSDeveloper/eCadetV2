package com.elosoftbiz.etrb_trmf;

public class PersonProjectWorkFile {
    Integer id;
    String person_project_work_file_id;
    String person_project_work_id;
    String filename;
    String file_desc;
    String prio;

    public PersonProjectWorkFile(Integer id, String person_project_work_file_id, String person_project_work_id, String filename, String file_desc, String prio){
        this.id = id;
        this.person_project_work_file_id = person_project_work_file_id;
        this.person_project_work_id = person_project_work_id;
        this.filename = filename;
        this.file_desc = file_desc;
        this.prio = prio;
    }

    public PersonProjectWorkFile(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPerson_project_work_file_id() {
        return person_project_work_file_id;
    }

    public void setPerson_project_work_file_id(String person_project_work_file_id) {
        this.person_project_work_file_id = person_project_work_file_id;
    }

    public String getPerson_project_work_id() {
        return person_project_work_id;
    }

    public void setPerson_project_work_id(String person_project_work_id) {
        this.person_project_work_id = person_project_work_id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFile_desc() {
        return file_desc;
    }

    public void setFile_desc(String file_desc) {
        this.file_desc = file_desc;
    }

    public String getPrio() {
        return prio;
    }

    public void setPrio(String prio) {
        this.prio = prio;
    }
}
