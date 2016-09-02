package com.sender.team.sender;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sender.team.sender.data.ChatContract;
import com.sender.team.sender.data.ChattingData;
import com.sender.team.sender.widget.ChattingReceiverViewHolder;
import com.sender.team.sender.widget.ChattingSenderViewHolder;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class ChattingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Cursor cursor;
    ChattingData data;

    public void changeCursor(Cursor c) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = c;
        notifyDataSetChanged();
    }

    public void setRecieveData(){
        this.data = data;
    }

    private static final int VIEW_TYPE_SEND = 1;
    private static final int VIEW_TYPE_RECEIVE = 2;

    @Override
    public int getItemViewType(int position) {
        cursor.moveToPosition(position);
        int type = cursor.getInt(cursor.getColumnIndex(ChatContract.ChatMessage.COLUMN_TYPE));
        switch (type) {
            case ChatContract.ChatMessage.TYPE_SEND :
                return VIEW_TYPE_SEND;
            case ChatContract.ChatMessage.TYPE_RECEIVE :
                return VIEW_TYPE_RECEIVE;
        }
        throw new IllegalArgumentException("invalid type");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_SEND : {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chatting_send, parent, false);
                ChattingSenderViewHolder holder = new ChattingSenderViewHolder(view);
                return holder;
            }
            case VIEW_TYPE_RECEIVE : {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chatting_receive, parent, false);
                ChattingReceiverViewHolder holder = new ChattingReceiverViewHolder(view);
                return holder;
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SEND : {
                ChattingSenderViewHolder svh = (ChattingSenderViewHolder)holder;
                String message = cursor.getString(cursor.getColumnIndex(ChatContract.ChatMessage.COLUMN_MESSAGE));
                String time = cursor.getString(cursor.getColumnIndex(ChatContract.ChatMessage.COLUMN_CREATED));
                svh.setChatSenderData(message,time);
                break;
            }
            case VIEW_TYPE_RECEIVE :{
                ChattingReceiverViewHolder rvh = (ChattingReceiverViewHolder)holder;
                rvh.setChatReceiverData(data);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }
}
