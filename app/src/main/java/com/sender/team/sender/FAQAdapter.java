package com.sender.team.sender;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.sender.team.sender.data.FAQChildData;
import com.sender.team.sender.data.FAQGroupData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tacademy on 2016-08-24.
 */
public class FAQAdapter extends BaseExpandableListAdapter {

    List<FAQGroupData> items = new ArrayList<>();

    public void put(String groupName, String contents) {
        FAQGroupData group = null;
        for(FAQGroupData g : items) {
            if (g.groupName.equals(groupName)) {
                group = g;
                break;
            }
        }
        if (group == null) {
            group = new FAQGroupData();
            group.groupName = groupName;
            items.add(group);
        }

        if (!TextUtils.isEmpty(contents)) {
            FAQChildData child = new FAQChildData();
            child.contents = contents;
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
        View textGroup;
        if(convertView == null){
            textGroup = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_faq_group, parent,false);
        } else {
            textGroup = convertView;
        }
        TextView groupContent = (TextView) textGroup.findViewById(R.id.faq_group_name);
        groupContent.setText(items.get(groupPosition).groupName);
        return textGroup;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View textChild;
        if(convertView == null){
            textChild = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_faq_child, parent,false);
        } else {
            textChild = convertView;
        }
        TextView childContent = (TextView) textChild.findViewById(R.id.faq_child_name);
        childContent.setText(items.get(groupPosition).children.get(childPosition).contents);
        return textChild;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
