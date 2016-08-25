package com.sender.team.sender;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sender.team.sender.data.MenuChild;
import com.sender.team.sender.data.MenuGroup;
import com.sender.team.sender.data.NaviItem;
import com.sender.team.sender.widget.ChildViewHolder;
import com.sender.team.sender.widget.GroupViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER = 0;
    public static final int GROUP = 1;
    public static final int CHILD = 2;

    public static final int SERVICE_TERMS = 0;
    public static final int INFO_TERMS = 1;
    public static final int GPS_TERMS = 2;

    Context context;
    private List<NaviItem> data;

    public void setData(List<NaviItem> data) {
        if (this.data != data) {
            this.data = data;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        switch (viewType) {
            case GROUP : {
                View view = LayoutInflater.from(context).inflate(R.layout.view_group, parent, false);
                GroupViewHolder gvh = new GroupViewHolder(view);
                return gvh;
            }
            case CHILD : {
                View view = LayoutInflater.from(context).inflate(R.layout.view_child, parent, false);
                ChildViewHolder cvh = new ChildViewHolder(view);
                return cvh;
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final NaviItem item = data.get(position);
        switch (item.type) {
            case GROUP : {
                final MenuGroup groupItem = (MenuGroup) item;
                final GroupViewHolder groupViewHolder = (GroupViewHolder) holder;
                groupViewHolder.setData(groupItem);

                groupViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (groupItem.isHaveChild) {
                            if (groupItem.children == null) {
                                groupItem.children = new ArrayList<>();
                                int count = 0;
                                int pos = data.indexOf(groupViewHolder.item);
                                while (pos < data.size() && data.get(pos + 1).type == CHILD) {
                                    groupItem.children.add((MenuChild) data.remove(pos + 1));
                                    count++;
                                }
                                notifyItemRangeRemoved(pos + 1, count);
                            } else {
                                int pos = data.indexOf(groupViewHolder.item);
                                int index = pos + 1;
                                for (MenuChild child : groupItem.children) {
                                    data.add(index, child);
                                    index++;
                                }
                                notifyItemRangeInserted(pos + 1, index - pos - 1);
                                groupItem.children = null;
                            }
                        } else {
                            switch (position) {
                                case 0 : {
                                    Intent intent = new Intent(context, MyPageActivity.class);
                                    context.startActivity(intent);
                                    break;
                                }
                                case 1 : {
                                    Intent intent = new Intent(context, NoticeActivity.class);
                                    context.startActivity(intent);
                                    break;
                                }
                                case 4 : {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    context.startActivity(intent);
                                    break;
                                }
                            }
                        }
                    }
                });
                break;
            }
            case CHILD :
                final MenuChild data = (MenuChild) item;
                final ChildViewHolder childViewHolder = (ChildViewHolder) holder;
                childViewHolder.setData(data);

                childViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (data != null) {
                            switch (data.childMenuType) {
                                case SERVICE_TERMS: {
                                    Intent intent = new Intent(context, TermsActivity.class);
                                    intent.putExtra("type", SERVICE_TERMS);
                                    context.startActivity(intent);
                                    break;
                                }
                                case INFO_TERMS: {
                                    Intent intent = new Intent(context, TermsActivity.class);
                                    intent.putExtra("type", INFO_TERMS);
                                    context.startActivity(intent);
                                    break;
                                }
                                case GPS_TERMS: {
                                    Intent intent = new Intent(context, TermsActivity.class);
                                    intent.putExtra("type", GPS_TERMS);
                                    context.startActivity(intent);
                                    break;
                                }
                            }
                        }
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}