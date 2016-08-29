package com.sender.team.sender.data;

import java.util.ArrayList;

public class ReviewListData implements java.io.Serializable {
    private static final long serialVersionUID = -3517856242515951816L;
    private int totalPage;
    private int itemsPerPage;
    private ArrayList<ReviewData> review;
    private int currentPage;

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

    public ArrayList<ReviewData> getReview() {
        return this.review;
    }

    public void setReview(ArrayList<ReviewData> review) {
        this.review = review;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
