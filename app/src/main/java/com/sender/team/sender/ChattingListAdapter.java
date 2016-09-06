package com.sender.team.sender;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sender.team.sender.data.ChattingListData;
import com.sender.team.sender.widget.ChattingListViewHolder;

import java.util.List;

/**
 * Created by Tacademy on 2016-09-02.
 */
public class ChattingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<ChattingListData> data;

    public void setData(List<ChattingListData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public ChattingListData getItemAtPosition(int position) {
        if (data != null) {
            return data.get(position);
        }
        throw new NullPointerException("data is null");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_chatting_list, parent, false);
        ChattingListViewHolder holder = new ChattingListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ChattingListViewHolder clh = (ChattingListViewHolder) holder;
        clh.setData(data.get(position));
        clh.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clh.getData().getType() == 0) {
                    Intent intent = new Intent(clh.getContext(), ChattingActivity.class);
                    intent.putExtra(ChattingActivity.HEADER_TYPE, ChattingActivity.SEND_HEADER);
                    clh.getContext().startActivity(intent);
                } else if (clh.getData().getType() == 1){
                    Intent intent = new Intent(clh.getContext(), ChattingActivity.class);
                    intent.putExtra(ChattingActivity.HEADER_TYPE, ChattingActivity.DELIVERER_HEADER);
                    clh.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}