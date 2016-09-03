package com.sender.team.sender.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sender.team.sender.R;
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

    Context context;
    public ChattingListViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    ChattingListData item;
    public void setData(ChattingListData data) {
        this.item = data;
        Glide.with(context).load(data.getImageUrl()).into(imageProfile);
        textName.setText(data.getName());
        textMessage.setText(data.getMessage());
        textTime.setText(data.getTime());
    }

    public ChattingListData getData() {
        return this.item;
    }

    public Context getContext() {
        return this.context;
    }
}
