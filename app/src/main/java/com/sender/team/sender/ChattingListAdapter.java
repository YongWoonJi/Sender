package com.sender.team.sender;

import android.content.Intent;
import android.database.Cursor;
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
    Cursor cursor;

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ChattingListViewHolder clh = (ChattingListViewHolder) holder;

        clh.setData(data.get(position));
//        if (data.get(position).getType() != ChattingListData.TYPE_EMPTY) {
//            clh.layout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(clh.getContext(), ChattingActivity.class);
//                    if (clh.getData().getType() == ChattingListData.TYPE_SENDER) {
//                        intent.putExtra(ChattingActivity.HEADER_TYPE, ChattingActivity.SEND_HEADER);
//                    } else if (clh.getData().getType() == ChattingListData.TYPE_DELIVERER) {
//                        intent.putExtra(ChattingActivity.HEADER_TYPE, ChattingActivity.DELIVERER_HEADER);
//                    }
////                intent.putExtra(ChattingActivity.RECEIVER_NAME, data.get(position).getName());
////                intent.putExtra(ChattingActivity.RECEIVER_IMAGE, data.get(position).getImageUrl());
//                    clh.getContext().startActivity(intent);
//                }
//            });
//        }
        clh.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(clh.getContext(), ChattingActivity.class);
                if (clh.getData().getType() == ChattingListData.TYPE_SENDER) {
                    intent.putExtra(ChattingActivity.HEADER_TYPE, ChattingActivity.SEND_HEADER);
                } else if (clh.getData().getType() == ChattingListData.TYPE_DELIVERER) {
                    intent.putExtra(ChattingActivity.HEADER_TYPE, ChattingActivity.DELIVERER_HEADER);
                }

                intent.putExtra(ChattingActivity.EXTRA_CHATTINGLIST_DATA, data.get(position));
                clh.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
