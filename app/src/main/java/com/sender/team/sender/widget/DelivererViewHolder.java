package com.sender.team.sender.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.sender.team.sender.R;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class DelivererViewHolder extends RecyclerView.ViewHolder {

    OnSendListener listener;
    public interface OnSendListener {
        void OnClickSend();
    }

    public void setListener(OnSendListener listener) {
        this.listener = listener;
    }

    public DelivererViewHolder(View itemView) {
        super(itemView);
        Button btn = (Button) itemView.findViewById(R.id.btn_send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnClickSend();
            }
        });
    }


}
