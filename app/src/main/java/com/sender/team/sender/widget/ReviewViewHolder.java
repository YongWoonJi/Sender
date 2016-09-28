package com.sender.team.sender.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sender.team.sender.MyApplication;
import com.sender.team.sender.R;
import com.sender.team.sender.Utils;
import com.sender.team.sender.data.Review;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class ReviewViewHolder extends RecyclerView.ViewHolder {

    ImageView userImg;
    TextView name, rating, message, date;

    public ReviewViewHolder(View itemView) {
        super(itemView);
        userImg = (ImageView) itemView.findViewById(R.id.review_img);
        name = (TextView)itemView.findViewById(R.id.text_review_name);
        rating = (TextView)itemView.findViewById(R.id.text_review_rating);
        message = (TextView)itemView.findViewById(R.id.text_review_message);
        date = (TextView)itemView.findViewById(R.id.text_review_date);
    }

    Review data;
    public void setData(Review data) {
        this.data = data;
        Glide.with(MyApplication.getContext()).load(data.getFileUrl()).into(userImg);
        name.setText(data.getName());
        rating.setText(String.format("%.1f", data.getStar()));
        message.setText(data.getContent());
        date.setText(Utils.getCurrentTime(data.getDate()));
    }
}
