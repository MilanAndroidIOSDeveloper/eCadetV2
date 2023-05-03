package com.elosoftbiz.etrb_trmf;

public class Client {
    Integer id;
    String client_id, code_client, name_client,url;

    public Client(Integer id, String client_id, String code_client, String name_client, String url){
        this.id = id;
        this.client_id = client_id;
        this.code_client = code_client;
        this.name_client = name_client;
        this.url = url;
    }

    public Client(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getCode_client() {
        return code_client;
    }

    public void setCode_client(String code_client) {
        this.code_client = code_client;
    }

    public String getName_client() {
        return name_client;
    }

    public void setName_client(String name_client) {
        this.name_client = name_client;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
