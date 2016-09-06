package com.sender.team.sender.data;

public class ChattingReceiveData {
    private String date;
    private UserData sender;
    private String message;

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UserData getSender() {
        return this.sender;
    }

    public void setSender(UserData sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
