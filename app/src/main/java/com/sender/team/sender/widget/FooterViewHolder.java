package com.sender.team.sender.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sender.team.sender.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JYW on 2016-08-25.
 */
public class FooterViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.text_footer)
    public TextView textFooter;

    Context context;
    public FooterViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }


}
