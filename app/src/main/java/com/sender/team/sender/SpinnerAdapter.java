package com.sender.team.sender;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sender.team.sender.data.DeliveringHistoryData;

/**
 * Created by Tacademy on 2016-09-01.
 */
public class SpinnerAdapter extends BaseAdapter {
//    List<String> items = new ArrayList<>();
    DeliveringHistoryData data;
    public void setItems(DeliveringHistoryData data) {
//        this.items = items;
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (data == null)return 0;
        return data.getData().size();
    }

    @Override
    public Object getItem(int position) {
        return data.getData();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if (convertView == null) {
            view = (TextView) LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        } else {
            view = (TextView) convertView;
        }
        view.setText(data.getData().get(position).getName() + "  -  " + data.getData().get(position).getDate());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if (convertView == null) {
            view = (TextView)LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        } else {
            view = (TextView) convertView;
        }
        view.setText(data.getData().get(position).getName() + "  -  " + data.getData().get(position).getDate());
        return view;
    }
}
