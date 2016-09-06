package com.sender.team.sender.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sender.team.sender.MyApplication;
import com.sender.team.sender.R;
import com.sender.team.sender.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tacademy on 2016-09-02.
 */
public class ChattingSenderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_chatting_sender_content)
    TextView sendContent;

    @BindView(R.id.text_chatting_sender_time)
    TextView sendTime;

    @BindView(R.id.image_chatting_sender_img)
    ImageView senderImage;

    public ChattingSenderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void setChatSenderData(File image, String content, long time){
        Glide.with(MyApplication.getContext()).load(image).into(senderImage);
        sendContent.setText(content);
        sendTime.setText(Utils.getCurrentTime(time));
    }
}
