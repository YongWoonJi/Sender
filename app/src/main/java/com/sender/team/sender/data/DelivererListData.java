package com.sender.team.sender.data;

import java.util.ArrayList;
import java.util.List;

public class DelivererListData {
    private List<DelivererData> data = new ArrayList<>();
    private int totalPage;
    private int itemsPerPage;
    private int currentPage;

    public List<DelivererData> getData() {
        return this.data;
    }

    public void setData(List<DelivererData> data) {
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
