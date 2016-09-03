package com.sender.team.sender.data;

public class ChattingReceiveData {
    private String date;
    private ChattingReceiveSender sender;
    private String message;

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ChattingReceiveSender getSender() {
        return this.sender;
    }

    public void setSender(ChattingReceiveSender sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
