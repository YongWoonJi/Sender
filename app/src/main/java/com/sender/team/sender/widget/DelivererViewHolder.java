package com.sender.team.sender.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sender.team.sender.MyApplication;
import com.sender.team.sender.R;
import com.sender.team.sender.data.DelivererData;
import com.sender.team.sender.data.ReverseGeocodingData;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.ReverseGeocodingRequest;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tacademy on 2016-08-25.
 */
public class DelivererViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text_deliverer_rating)
    TextView rating;
    @BindView(R.id.text_deliverer_nickname)
    TextView name;
    @BindView(R.id.text_deliverer_location)
    TextView location;
    @BindView(R.id.image_deliverer)
    ImageView delivererImage;
    String start, end;

    OnSendListener listener;
    public interface OnSendListener {
        void onClickSend(int position);
        void onClickDeliverer(int position, View view);
    }

    public void setListener(OnSendListener listener) {
        this.listener = listener;
    }

    public DelivererViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickDeliverer(getAdapterPosition(), view);
            }
        });

        Button btn = (Button) itemView.findViewById(R.id.btn_send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickSend(getAdapterPosition());
            }
        });
    }

    DelivererData data;
    public void setDelivererData(final DelivererData data){
        this.data = data;
        Glide.with(MyApplication.getContext()).load(data.getFileUrl()).into(delivererImage);
        name.setText(data.getName());
        rating.setText(""+data.getStar());

        ReverseGeocodingRequest request = new ReverseGeocodingRequest(MyApplication.getContext(),data.getHere_lat(),data.getHere_lon());
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<ReverseGeocodingData>() {
            @Override
            public void onSuccess(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result) {
                start = result.getAddressInfo().getLegalDong();

                ReverseGeocodingRequest request2 = new ReverseGeocodingRequest(MyApplication.getContext(),data.getNext_lat(),data.getNext_lon());
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request2, new NetworkManager.OnResultListener<ReverseGeocodingData>() {
                    @Override
                    public void onSuccess(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result) {
                        end = result.getAddressInfo().getLegalDong();
                        location.setText(start + " > "+ end);
                    }

                    @Override
                    public void onFail(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result, String errorMessage, Throwable e) {

                    }
                });
            }

            @Override
            public void onFail(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result, String errorMessage, Throwable e) {

            }
        });


    }

}
