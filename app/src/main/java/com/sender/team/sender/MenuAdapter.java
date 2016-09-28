package com.sender.team.sender;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sender.team.sender.data.MenuChild;
import com.sender.team.sender.data.MenuGroup;
import com.sender.team.sender.data.NaviItem;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.MyPageRequest;
import com.sender.team.sender.widget.ChildViewHolder;
import com.sender.team.sender.widget.FooterViewHolder;
import com.sender.team.sender.widget.GroupViewHolder;
import com.sender.team.sender.widget.HeaderViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER = 0;
    public static final int GROUP = 1;
    public static final int CHILD = 2;
    public static final int FOOTER = 3;

    public static final int SERVICE_TERMS = 0;
    public static final int INFO_TERMS = 1;
    public static final int GPS_TERMS = 2;

    Context context;
    private List<NaviItem> data;

    OnNaviMenuSelectedListener listener;
    public interface OnNaviMenuSelectedListener {
        void Logout();
        void unregister();
        void closeNavi(boolean animate);
        void resetChild();
    }

    public void setOnFinishListener(OnNaviMenuSelectedListener listener) {
        this.listener = listener;
    }

    public void setData(List<NaviItem> data) {
        if (this.data != data) {
            this.data = data;
        }
        notifyDataSetChanged();
    }

    public List<NaviItem> getData() {
        return this.data;
    }

    public NaviItem getItemAtPosition(int position) {
        return data.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        switch (viewType) {
            case HEADER : {
                View view = LayoutInflater.from(context).inflate(R.layout.view_header, parent, false);
                HeaderViewHolder hvh = new HeaderViewHolder(view);
                return hvh;
            }
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
            case FOOTER : {
                View view = LayoutInflater.from(context).inflate(R.layout.view_footer, parent, false);
                FooterViewHolder fvh = new FooterViewHolder(view);
                return  fvh;
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final NaviItem item = data.get(position);
        switch (item.type) {
            case HEADER : {
                HeaderViewHolder hvh = (HeaderViewHolder) holder;
                hvh.setData(PropertyManager.getInstance().getUserData());
                hvh.homeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.closeNavi(true);
                    }
                });
                break;
            }
            case FOOTER : {
                FooterViewHolder fvh= (FooterViewHolder) holder;
                fvh.textFooter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.unregister();
                    }
                });
                break;
            }
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
                                case 1 : {
                                    MyPageRequest request = new MyPageRequest(context);
                                    NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
                                        @Override
                                        public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                                            PropertyManager.getInstance().setUserData(result.getResult());
                                            Intent intent = new Intent(context, MyPageActivity.class);
                                            context.startActivity(intent);
                                        }

                                        @Override
                                        public void onFail(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result, String errorMessage, Throwable e) {

                                        }
                                    });
                                    break;
                                }
                                case 2 : {
                                    Intent intent = new Intent(context, NoticeActivity.class);
                                    context.startActivity(intent);
                                    break;
                                }
                                case 3 : {
                                    if (PropertyManager.getInstance().getAlarmSetting()) {
                                        PropertyManager.getInstance().setAlarmSetting(false);
                                        groupViewHolder.imageSwitch.setImageResource(R.drawable.btn_alarm_off);
                                        Toast.makeText(MyApplication.getContext(), "푸시알림 끔", Toast.LENGTH_SHORT).show();
                                    } else {
                                        PropertyManager.getInstance().setAlarmSetting(true);
                                        groupViewHolder.imageSwitch.setImageResource(R.drawable.btn_alarm_on);
                                        Toast.makeText(MyApplication.getContext(), "푸시알림 켬", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                }
                                case 5 : {
                                    listener.Logout();
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
                                    listener.resetChild();
                                    break;
                                }
                                case INFO_TERMS: {
                                    Intent intent = new Intent(context, TermsActivity.class);
                                    intent.putExtra("type", INFO_TERMS);
                                    context.startActivity(intent);
                                    listener.resetChild();
                                    break;
                                }
                                case GPS_TERMS: {
                                    Intent intent = new Intent(context, TermsActivity.class);
                                    intent.putExtra("type", GPS_TERMS);
                                    context.startActivity(intent);
                                    listener.resetChild();
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
