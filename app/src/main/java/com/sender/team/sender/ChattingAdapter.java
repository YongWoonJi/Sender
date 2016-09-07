package com.sender.team.sender;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sender.team.sender.data.ChatContract;
import com.sender.team.sender.widget.ChattingReceiverViewHolder;
import com.sender.team.sender.widget.ChattingSenderViewHolder;

import java.io.File;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class ChattingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ChattingReceiverViewHolder.ChatReceiverImage{

    Cursor cursor;
    File senderChatImage;
    String name, profileImage;

    public void changeCursor(Cursor c) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = c;
        notifyDataSetChanged();
    }

    public void setRecieveData(File chatImage, String name, String profileImage){
        this.senderChatImage = chatImage;
        this.name = name;
        this.profileImage = profileImage;
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
                holder.setOnClickChatReceiverImage(this);
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
                String image = cursor.getString(cursor.getColumnIndex(ChatContract.ChatMessage.COLUMN_IMAGE));
                String message = cursor.getString(cursor.getColumnIndex(ChatContract.ChatMessage.COLUMN_MESSAGE));
                long time = cursor.getLong(cursor.getColumnIndex(ChatContract.ChatMessage.COLUMN_CREATED));
                svh.setChatSenderData(image, message, time);
                break;
            }

            case VIEW_TYPE_RECEIVE :{
                ChattingReceiverViewHolder rvh = (ChattingReceiverViewHolder)holder;
                String message = cursor.getString(cursor.getColumnIndex(ChatContract.ChatMessage.COLUMN_MESSAGE));
                long time = cursor.getLong(cursor.getColumnIndex(ChatContract.ChatMessage.COLUMN_CREATED));
                String image = cursor.getString(cursor.getColumnIndex(ChatContract.ChatMessage.COLUMN_IMAGE));
                rvh.setChatReceiverData(profileImage,name,message,time,image);
//                rvh.setChatReceiverData(data.getSender().getFileUrl(),data.getSender().getName(),message,time);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }

    // 채팅 이미지를 누르면 ChattingProfileActivity로 이동하기 위한 옵저버 패턴
    @Override
    public void onClickReceiverImage(View view, int position) {
        mListener.onClickChatImage(view, position);
    }

    public interface ChattingImage{
        public void onClickChatImage(View view, int position);
    }

    ChattingImage mListener;
    public void setOnClickChatImageListener(ChattingImage mListener){
        this.mListener = mListener;
    }
}
