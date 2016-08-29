package com.sender.team.sender.data;

import java.io.Serializable;

public class UserData implements Serializable {
    private int deliver_req;
    private int deliver_com;
    private int user_id;
    private String phone;
    private String name;
    private String pic;
    private int activation;
    private String email;
    private String introduction;
    private String error;

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

    public int getUser_id() {
        return this.user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return this.pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
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
}
