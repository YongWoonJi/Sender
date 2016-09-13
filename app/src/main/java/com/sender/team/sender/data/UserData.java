package com.sender.team.sender.data;

import java.io.Serializable;

public class UserData implements Serializable {
    private int deliver_req;
    private int deliver_com;
    private String id;
    private String phone;
    private String name;
    private String fileUrl;
    private int activation;
    private String email;
    private String introduction;
    private float star;
    private String address;
    private String contractId;

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDeliver_req() {
        return this.deliver_req;
    }

    public void setDeliver_req(int deliver_req) {
        this.deliver_req = deliver_req;
    }

    public int getDeliver_com() {
        return this.deliver_com;
    }

    public void setDeliver_com(int deliver_com) {
        this.deliver_com = deliver_com;
    }

    public String getUser_id() {
        return this.id;
    }

    public void setUser_id(String user_id) {
        this.id = user_id;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getActivation() {
        return this.activation;
    }

    public void setActivation(int activation) {
        this.activation = activation;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIntroduction() {
        return this.introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
