package com.sender.team.sender.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sender.team.sender.MyApplication;
import com.sender.team.sender.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tacademy on 2016-09-02.
 */
public class ChattingReceiverViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_chatting_receiver)
    ImageView receiverImage;

    @BindView(R.id.text_chatting_receiver_name)
    TextView receiverName;

    @BindView(R.id.text_chatting_receiver_content)
    TextView receiverContent;

    @BindView(R.id.text_chatting_receive_time)
    TextView receiverTime;

    public ChattingReceiverViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void setChatReceiverData(String url, String name, String message, String time){

        Glide.with(MyApplication.getContext()).load(url).into(receiverImage);
        receiverName.setText(name);
        receiverContent.setText(message);
        receiverTime.setText(time);
    }
}
