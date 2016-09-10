package com.sender.team.sender.data;

import java.util.ArrayList;

/**
 * Created by Tacademy on 2016-09-02.
 */
public class DeliveringHistoryData {
    private int totalCount;
    private ArrayList<CompleteDelivererData> data;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public ArrayList<CompleteDelivererData> getData() {
        return data;
    }

    public void setData(ArrayList<CompleteDelivererData> name) {
        this.data = name;
    }
}
