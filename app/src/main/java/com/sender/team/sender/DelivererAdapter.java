package com.sender.team.sender;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sender.team.sender.data.DelivererData;
import com.sender.team.sender.data.ReverseGeocodingData;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.ReverseGeocodingRequest;
import com.sender.team.sender.widget.DelivererViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class DelivererAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DelivererViewHolder.OnSendListener {

    List<DelivererData> data = new ArrayList<>();

    int checkedPosition = -1;

    Context context;

    public DelivererAdapter(Context context) {
        this.context = context;
    }

    public void setDelivererData(final List<DelivererData> data) {
        if (this.data != data) {
            for (int i = 0; i < data.size(); i++) {
                final int position = i;
                ReverseGeocodingRequest request = new ReverseGeocodingRequest(context, data.get(position).getHere_lat(), data.get(position).getHere_lon());
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_TMAP, request, new NetworkManager.OnResultListener<ReverseGeocodingData>() {
                    @Override
                    public void onSuccess(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result) {
                        if (result != null) {
                            data.get(position).setStartingPoint(result.getAddressInfo().getLegalDong());
                            ReverseGeocodingRequest request2 = new ReverseGeocodingRequest(context, data.get(position).getNext_lat(), data.get(position).getNext_lon());
                            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_TMAP, request2, new NetworkManager.OnResultListener<ReverseGeocodingData>() {
                                @Override
                                public void onSuccess(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result) {
                                    if (result != null) {
                                        data.get(position).setDestination(result.getAddressInfo().getLegalDong());
                                    } else {
                                        data.remove(position);
                                    }
                                }

                                @Override
                                public void onFail(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result, String errorMessage, Throwable e) {

                                }
                            });
                        } else {
                            data.remove(position);
                        }
                    }

                    @Override
                    public void onFail(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result, String errorMessage, Throwable e) {

                    }
                });
            }
            this.data = data;
        }
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public int getDeliverId(int position) {
        return data.get(position).getDeilver_id();
    }

    public int getId(int position) {
        return data.get(position).getId();
    }

    OnDialogListener listener;

    public interface OnDialogListener {
        void DialogShow(int position);

        void delivererShow(int position, View view, DelivererData data);
    }

    public void setOnDialogListener(OnDialogListener listener) {
        this.listener = listener;
    }

    public void setListenerReset(){
        this.listener = null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_deliverer, parent, false);
        DelivererViewHolder holder = new DelivererViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DelivererViewHolder dvh = (DelivererViewHolder) holder;
        dvh.setDelivererData(data.get(position));
        dvh.setChecked(checkedPosition == position);
        dvh.setListener(this);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    @Override
    public void onClickSend(int position) {
        listener.DialogShow(position);
    }

    @Override
    public void onClickDeliverer(int position, View view) {
        setItemChecked(position, true);
        if (listener != null) {
            listener.delivererShow(position, view, data.get(position));
        }
    }

    public void setItemChecked(int position, boolean isChecked) {
        if (checkedPosition != position) {
            if (isChecked) {
                checkedPosition = position;
                notifyDataSetChanged();
            }
        } else {
            if (!isChecked) {
                checkedPosition = -1;
                notifyDataSetChanged();
            }
        }
    }
}
