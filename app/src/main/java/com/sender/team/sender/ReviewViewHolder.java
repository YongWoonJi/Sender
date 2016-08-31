package com.sender.team.sender;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sender.team.sender.data.Review;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class ReviewViewHolder extends RecyclerView.ViewHolder {

    ImageView userImg;
    TextView name, rating, message;

    public ReviewViewHolder(View itemView) {
        super(itemView);
//       userImg = (ImageView) itemView.findViewById(R.id.)
        name = (TextView)itemView.findViewById(R.id.text_review_name);
        rating = (TextView)itemView.findViewById(R.id.text_review_rating);
        message = (TextView)itemView.findViewById(R.id.text_review_message);
    }

    Review data;
    public void setData(Review data) {
        this.data = data;
        name.setText(data.getReviewer_id());
        rating.setText("" + data.getStar());
        message.setText(data.getContent());
    }
}
