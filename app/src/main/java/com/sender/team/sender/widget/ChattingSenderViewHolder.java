package com.sender.team.sender.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sender.team.sender.R;

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

    public ChattingSenderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void setChatSenderData(String content, String time){
        sendContent.setText(content);
        sendTime.setText(time);
    }
}
