package com.sender.team.sender;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sender.team.sender.data.DelivererData;
import com.sender.team.sender.widget.DelivererViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class DelivererAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DelivererViewHolder.OnSendListener {

    List<DelivererData> data = new ArrayList<>();

    int checkedPosition = -1;

    Context context;

    public DelivererAdapter(Context context) {
        this.context = context;
    }

    public void setDelivererData(final List<DelivererData> data) {
        if (this.data != data) {
            this.data = data;
        }
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public int getDeliverId(int position) {
        return data.get(position).getDeilver_id();
    }

    public int getId(int position) {
        return data.get(position).getId();
    }

    OnDialogListener listener;

    public interface OnDialogListener {
        void dialogShow(int position);

        void delivererShow(int position, View view, DelivererData data);
    }

    public void setOnDialogListener(OnDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_deliverer, parent, false);
        DelivererViewHolder holder = new DelivererViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DelivererViewHolder dvh = (DelivererViewHolder) holder;
        dvh.setDelivererData(data.get(position));
        dvh.setChecked(checkedPosition == position);
        dvh.setListener(this);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    @Override
    public void onClickSend(int position) {
        listener.dialogShow(position);
    }

    @Override
    public void onClickDeliverer(int position, View view) {
        setItemChecked(position, true);
        if (listener != null) {
            listener.delivererShow(position, view, data.get(position));
        }
    }

    public void setItemChecked(int position, boolean isChecked) {
        if (checkedPosition != position) {
            if (isChecked) {
                checkedPosition = position;
                notifyDataSetChanged();
            }
        } else {
            if (!isChecked) {
                checkedPosition = -1;
                notifyDataSetChanged();
            }
        }
    }
}
