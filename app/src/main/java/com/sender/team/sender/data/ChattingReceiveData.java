package com.sender.team.sender.data;

import java.util.ArrayList;

public class ChattingReceiveData {
    private ArrayList<ChattingReceiveMessage> data;
    private UserData sender;

    public ArrayList<ChattingReceiveMessage> getData() {
        return this.data;
    }

    public void setData(ArrayList<ChattingReceiveMessage> data) {
        this.data = data;
    }

    public UserData getSender() {
        return this.sender;
    }

    public void setSender(UserData sender) {
        this.sender = sender;
    }
}
