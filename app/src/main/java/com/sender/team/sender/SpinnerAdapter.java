package com.sender.team.sender;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tacademy on 2016-09-01.
 */
public class SpinnerAdapter extends BaseAdapter {
    List<String> items = new ArrayList<>();
    public void addAll(String[] items) {
        this.items.addAll(Arrays.asList(items));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
