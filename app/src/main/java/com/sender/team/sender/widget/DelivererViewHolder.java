package com.sender.team.sender.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sender.team.sender.MyApplication;
import com.sender.team.sender.R;
import com.sender.team.sender.data.DelivererData;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class DelivererViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text_deliverer_rating)
    TextView rating;
    @BindView(R.id.text_deliverer_nickname)
    TextView name;
    @BindView(R.id.text_deliverer_location)
    TextView location;
    @BindView(R.id.image_deliverer)
    ImageView delivererImage;

    OnSendListener listener;
    public interface OnSendListener {
        void onClickSend(int position);
        void onClickDeliverer(int position, View view);
    }

    public void setListener(OnSendListener listener) {
        this.listener = listener;
    }

    public DelivererViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickDeliverer(getAdapterPosition(), view);
            }
        });

        Button btn = (Button) itemView.findViewById(R.id.btn_send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickSend(getAdapterPosition());
            }
        });
    }

    DelivererData data;
    public void setDelivererData(final DelivererData data){
        this.data = data;
        Glide.with(MyApplication.getContext()).load(data.getFileUrl()).into(delivererImage);
        name.setText(data.getName());
        rating.setText(""+data.getStar());
        location.setText(data.getStartingPoint() + " > "+ data.getDestination());
    }
}
