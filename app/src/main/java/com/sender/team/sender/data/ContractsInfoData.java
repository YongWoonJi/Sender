package com.sender.team.sender.data;

public class ContractsInfoData implements java.io.Serializable {
    private static final long serialVersionUID = 7718013726926587442L;
    private int deliverer_id;
    private String res_time;
    private int contract_id;
    private int state;
    private int sender_id;
    private String req_time;

    public int getDeliverer_id() {
        return this.deliverer_id;
    }

    public void setDeliverer_id(int deliverer_id) {
        this.deliverer_id = deliverer_id;
    }

    public String getRes_time() {
        return this.res_time;
    }

    public void setRes_time(String res_time) {
        this.res_time = res_time;
    }

    public int getContract_id() {
        return this.contract_id;
    }

    public void setContract_id(int contract_id) {
        this.contract_id = contract_id;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSender_id() {
        return this.sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public String getReq_time() {
        return this.req_time;
    }

    public void setReq_time(String req_time) {
        this.req_time = req_time;
    }
}
