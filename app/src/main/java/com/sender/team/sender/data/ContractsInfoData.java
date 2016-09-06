package com.sender.team.sender.data;

public class ContractsInfoData {
    private int delivering_user_id;
    private int deliverer_id;
    private String res_time;
    private int contract_id;
    private int state;
    private int sending_id;
    private int sending_user_id;
    private String req_time;

    public int getDelivering_user_id() {
        return delivering_user_id;
    }

    public void setDelivering_user_id(int delivering_user_id) {
        this.delivering_user_id = delivering_user_id;
    }

    public int getSending_user_id() {
        return sending_user_id;
    }

    public void setSending_user_id(int sending_user_id) {
        this.sending_user_id = sending_user_id;
    }

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

    public int getSending_id() {
        return this.sending_id;
    }

    public void setSending_id(int sending_id) {
        this.sending_id = sending_id;
    }

    public String getReq_time() {
        return this.req_time;
    }

    public void setReq_time(String req_time) {
        this.req_time = req_time;
    }
}
