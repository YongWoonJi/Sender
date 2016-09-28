package com.sender.team.sender.data;

public class Review {
    private String review_date;
    private float star;
    private String name;
    private String fileUrl;
    private String content;

    public String getDate() {
        return this.review_date;
    }

    public void setDate(String date) {
        this.review_date = date;
    }

    public float getStar() {
        return this.star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String pic) {
        this.fileUrl = pic;
    }
}
