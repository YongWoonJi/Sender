package com.sender.team.sender.data;

public class ReviewData implements java.io.Serializable {
    private static final long serialVersionUID = 1252840161305180521L;
    private String date;
    private int star;
    private String reviewer_id;
    private String content;

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStar() {
        return this.star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getReviewer_id() {
        return this.reviewer_id;
    }

    public void setReviewer_id(String reviewer_id) {
        this.reviewer_id = reviewer_id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
