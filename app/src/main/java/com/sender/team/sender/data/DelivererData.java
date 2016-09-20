package com.sender.team.sender.data;

public class DelivererData {
    private String here_lat;
    private String next_lon;
    private int id;
    private int delivering_id;
    private String dep_time;
    private String here_lon;
    private String next_lat;
    private String arr_time;
    private String name;
    private String phone;
    private float star;
    private String originalFilename;
    private String fileUrl;
    private String here_unit;
    private String next_unit;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getHere_lat() {
        return this.here_lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHere_lat(String here_lat) {
        this.here_lat = here_lat;
    }

    public String getNext_lon() {
        return this.next_lon;
    }

    public void setNext_lon(String next_lon) {
        this.next_lon = next_lon;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeilver_id() {
        return this.delivering_id ;
    }

    public void setDeilver_id(int deilver_id) {
        this.delivering_id  = deilver_id;
    }

    public String getDep_time() {
        return this.dep_time;
    }

    public void setDep_time(String dep_time) {
        this.dep_time = dep_time;
    }

    public String getHere_lon() {
        return this.here_lon;
    }

    public void setHere_lon(String here_lon) {
        this.here_lon = here_lon;
    }

    public String getNext_lat() {
        return this.next_lat;
    }

    public void setNext_lat(String next_lat) {
        this.next_lat = next_lat;
    }

    public String getArr_time() {
        return this.arr_time;
    }

    public void setArr_time(String arr_time) {
        this.arr_time = arr_time;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getHere_unit() {
        return here_unit;
    }

    public void setHere_unit(String here_unit) {
        this.here_unit = here_unit;
    }

    public String getNext_unit() {
        return next_unit;
    }

    public void setNext_unit(String next_unit) {
        this.next_unit = next_unit;
    }
}
