package com.sender.team.sender.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sender.team.sender.R;
import com.sender.team.sender.data.UserData;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JYW on 2016-08-25.
 */
public class HeaderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.homeLayout)
    public LinearLayout homeLayout;
    @BindView(R.id.text_userName)
    TextView textUserName;
    @BindView(R.id.text_header_email)
    TextView textEmail;
    @BindView(R.id.text_header_rating)
    TextView textRating;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    UserData item;
    Context context;
    public void setData(UserData data) {
        this.item = data;
        if (!TextUtils.isEmpty(data.getFileUrl())) {
            Glide.with(context).load(data.getFileUrl()).into(imageProfile);
        }
        if (!TextUtils.isEmpty(data.getName())) {
            textUserName.setText(data.getName());
        }
        if (TextUtils.isEmpty(data.getEmail())) {
            textEmail.setText(context.getString(R.string.empty_email));
        } else {
            textEmail.setText(data.getEmail());
        }
        textRating.setText(String.format("%.1f", data.getStar()));
    }

}
