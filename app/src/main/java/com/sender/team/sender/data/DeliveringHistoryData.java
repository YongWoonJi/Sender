package com.sender.team.sender.data;

import java.util.ArrayList;

/**
 * Created by Tacademy on 2016-09-02.
 */
public class DeliveringHistoryData {
    private int totalCount;
    private ArrayList<String> name;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public ArrayList<String> getName() {
        return name;
    }

    public void setName(ArrayList<String> name) {
        this.name = name;
    }
}
