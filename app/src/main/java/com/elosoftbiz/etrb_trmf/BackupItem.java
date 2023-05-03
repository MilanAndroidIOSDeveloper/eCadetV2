package com.elosoftbiz.etrb_trmf;

public class BackupItem {
    Integer id;
    String tbl, tbl_id, backup_date, backup_time, backup_event, backuped;

    public BackupItem(Integer id, String tbl, String tbl_id, String backup_date, String backup_time, String backup_event, String backuped){
        this.id = id;
        this.tbl = tbl;
        this.tbl_id = tbl_id;
        this.backup_date = backup_date;
        this.backup_time = backup_time;
        this.backup_event = backup_event;
        this.backuped = backuped;
    }
    public BackupItem(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTbl() {
        return tbl;
    }

    public void setTbl(String tbl) {
        this.tbl = tbl;
    }

    public String getTbl_id() {
        return tbl_id;
    }

    public void setTbl_id(String tbl_id) {
        this.tbl_id = tbl_id;
    }

    public String getBackup_date() {
        return backup_date;
    }

    public void setBackup_date(String backup_date) {
        this.backup_date = backup_date;
    }

    public String getBackup_time() {
        return backup_time;
    }

    public void setBackup_time(String backup_time) {
        this.backup_time = backup_time;
    }

    public String getBackup_event() {
        return backup_event;
    }

    public void setBackup_event(String backup_event) {
        this.backup_event = backup_event;
    }

    public String getBackuped() {
        return backuped;
    }

    public void setBackuped(String backuped) {
        this.backuped = backuped;
    }
}
