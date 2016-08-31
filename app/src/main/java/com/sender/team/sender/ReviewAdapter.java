package com.sender.team.sender;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sender.team.sender.data.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewViewHolder>{

    List<Review> data = new ArrayList<>();

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void setReviewData(List<Review> data){
//        if (this.data !=data){
            this.data.addAll(data);
//        }
        notifyDataSetChanged();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_review, parent, false);
        ReviewViewHolder holder = new ReviewViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        ReviewViewHolder rvh = holder;
        rvh.setData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
