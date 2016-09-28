package com.sender.team.sender.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sender.team.sender.R;
import com.sender.team.sender.Utils;
import com.sender.team.sender.data.ChattingListData;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tacademy on 2016-09-02.
 */
public class ChattingListViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.container)
    public RelativeLayout layout;

    @BindView(R.id.image_profile)
    ImageView imageProfile;

    @BindView(R.id.text_name)
    TextView textName;

    @BindView(R.id.text_message)
    TextView textMessage;

    @BindView(R.id.text_time)
    TextView textTime;

    @BindView(R.id.text_empty)
    TextView textEmpty;

    Context context;
    public ChattingListViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    ChattingListData item;
    public void setData(ChattingListData data) {
        this.item = data;

        if (data.getType() == ChattingListData.TYPE_EMPTY) {
            textName.setVisibility(View.GONE);
            textMessage.setVisibility(View.GONE);
            textTime.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
            imageProfile.setImageResource(R.drawable.profile);
            textEmpty.setText(data.getMessage());
        } else {
            textName.setVisibility(View.VISIBLE);
            textMessage.setVisibility(View.VISIBLE);
            textTime.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(data.getImageUrl())) {
                Glide.with(context).load(data.getImageUrl()).into(imageProfile);
            }
            if (!TextUtils.isEmpty(data.getName())) {
                textName.setText(data.getName());
            }
            if (!TextUtils.isEmpty(data.getMessage())) {
                textMessage.setText(data.getMessage());
            } else if (TextUtils.isEmpty(data.getMessage()) && data.getMessageType() > 1) {
                textMessage.setText("사진");
            } else {
                textMessage.setText("채팅 내용이 없습니다");
            }
            if (!TextUtils.isEmpty(data.getTime())) {
                textTime.setText(Utils.getCurrentTime(Long.parseLong(data.getTime())));
            }
        }
    }

    public ChattingListData getData() {
        return this.item;
    }

    public Context getContext() {
        return this.context;
    }
}
