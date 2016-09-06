package com.sender.team.sender;

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

    public void setDelivererData(List<DelivererData> data){
        if (this.data != data){
            this.data = data;
        }
        notifyDataSetChanged();
    }

    public int getDeliverId(int position){
        return data.get(position).getDeilver_id();
    }

    OnDialogListener listener;
    public interface OnDialogListener {
        void DialogShow(int position);
        void delivererShow(int position, View view,DelivererData data);
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
        dvh.setListener(this);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClickSend(int position) {
        listener.DialogShow(position);
    }

    @Override
    public void onClickDeliverer(int position, View view) {
        listener.delivererShow(position,view, data.get(position));
    }
}
