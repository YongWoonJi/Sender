package com.sender.team.sender.data;

/**
 * Created by Tacademy on 2016-09-01.
 */
public class ContractIdData {
    private int sending_id;
    private int contract_id;
    private int sending_user_id;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSendingId() {
        return sending_id;
    }

    public void setSendingId(int sending_id) {
        this.sending_id = sending_id;
    }

    public int getSending_user_id() {
        return sending_user_id;
    }

    public void setSending_user_id(int sending_user_id) {
        this.sending_user_id = sending_user_id;
    }

    public int getContract_id() {
        return contract_id;
    }

    public void setContract_id(int contract_id) {
        this.contract_id = contract_id;
    }
}
