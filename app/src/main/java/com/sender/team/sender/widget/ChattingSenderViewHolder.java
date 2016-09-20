package com.sender.team.sender.widget;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sender.team.sender.MyApplication;
import com.sender.team.sender.R;
import com.sender.team.sender.Utils;

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

    public void setChatSenderData(String image, String content, long time){
        if (!TextUtils.isEmpty(image)) {
            Glide.with(MyApplication.getContext()).load(image).into(senderImage);
        }
        if (!TextUtils.isEmpty(content)) {
            sendContent.setText(content);
        } else {
            sendContent.setVisibility(View.GONE);
        }
        if (time != 0) {
            sendTime.setText(Utils.getCurrentTime(time));
        }
    }
}
