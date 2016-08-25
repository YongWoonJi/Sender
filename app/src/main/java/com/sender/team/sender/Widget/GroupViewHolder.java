package com.sender.team.sender.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sender.team.sender.data.MenuGroup;
import com.sender.team.sender.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class GroupViewHolder extends RecyclerView.ViewHolder {

    public MenuGroup item;

    @BindView(R.id.text_title)
    TextView textTitle;

    @BindView(R.id.layout)
    public LinearLayout layout;

    public GroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(MenuGroup data) {
        this.item = data;
        textTitle.setText(data.groupName);
    }
}
