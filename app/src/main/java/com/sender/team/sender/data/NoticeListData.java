package com.sender.team.sender.data;

import java.util.ArrayList;

public class NoticeListData {
    private ArrayList<NoticeData> data;
    private int totalPage;
    private int itemsPerPage;
    private int currentPage;

    public ArrayList<NoticeData> getData() {
        return this.data;
    }

    public void setData(ArrayList<NoticeData> data) {
        this.data = data;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getItemsPerPage() {
        return this.itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
