package com.sender.team.sender;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sender.team.sender.data.ReviewDataTemp;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class ReviewViewHolder extends RecyclerView.ViewHolder {

//    ImageView userImg;
    TextView name, rating, message;

    public ReviewViewHolder(View itemView) {
        super(itemView);
//        userImg = (ImageView) itemView.findViewById(R.id.)
        name = (TextView)itemView.findViewById(R.id.text_review_name);
        rating = (TextView)itemView.findViewById(R.id.text_review_rating);
        message = (TextView)itemView.findViewById(R.id.text_review_message);
    }

    ReviewDataTemp data;
    public void setData(ReviewDataTemp data){
        this.data = data;
        name.setText(data.name);
        rating.setText(""+(int) data.rating);
        message.setText(data.message);
    }
}
