package com.sender.team.sender.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sender.team.sender.MyApplication;
import com.sender.team.sender.R;
import com.sender.team.sender.data.MenuGroup;
import com.sender.team.sender.manager.PropertyManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class GroupViewHolder extends RecyclerView.ViewHolder {

    public MenuGroup item;

    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.image_group_icon)
    ImageView imageView;
    @BindView(R.id.image_switch)
    public ImageView imageSwitch;

    @BindView(R.id.layout)
    public LinearLayout layout;


    Context context;
    public GroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        context = itemView.getContext();
    }


    public void setData(MenuGroup data) {
        this.item = data;
        textTitle.setText(data.groupName);
        Glide.with(MyApplication.getContext())
                .load(data.icon)
                .into(imageView);
        if (data.alarm == 1) {
            imageSwitch.setVisibility(View.VISIBLE);
            if (PropertyManager.getInstance().getAlarmSetting()) {
                imageSwitch.setImageResource(R.drawable.btn_alarm_on);
            } else {
                imageSwitch.setImageResource(R.drawable.btn_alarm_off);
            }
        }
    }
}
