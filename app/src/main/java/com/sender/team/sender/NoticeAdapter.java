package com.sender.team.sender;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.sender.team.sender.data.NoticeChildData;
import com.sender.team.sender.data.NoticeGroupData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tacademy on 2016-08-24.
 */
public class NoticeAdapter extends BaseExpandableListAdapter {
    List<NoticeGroupData> items = new ArrayList<>();


    public void put(String title, String content) {
        NoticeGroupData group = null;
        for (NoticeGroupData g : items) {
            if (g.title.equals(title)) {
                group = g;
                break;
            }
        }
        if (group == null) {
            group = new NoticeGroupData();
            group.title = title;
            items.add(group);
        }

        if (content != null) {
            NoticeChildData child = new NoticeChildData();
            child.content = content;
            group.children.add(child);
        }

        notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return items.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_notice_group, parent,false);
        }
        TextView textGroup=(TextView) convertView.findViewById(R.id.text_notice_group);
        textGroup.setText(items.get(groupPosition).title);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_faq_child, parent,false);
        }
        TextView textGroup=(TextView) convertView.findViewById(R.id.faq_child_name);
        textGroup.setText(items.get(groupPosition).children.get(childPosition).content);
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
