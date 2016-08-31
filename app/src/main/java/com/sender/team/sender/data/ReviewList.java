package com.sender.team.sender.data;

import java.util.ArrayList;

public class ReviewList implements java.io.Serializable {
    private static final long serialVersionUID = 3886364987236318917L;
    private ArrayList<Review> review;

    public ArrayList<Review> getReview() {
        return this.review;
    }

    public void setReview(ArrayList<Review> review) {
        this.review = review;
    }
}
