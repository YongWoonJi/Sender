package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.DelivererData;
import com.sender.team.sender.data.NetworkResult;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Tacademy on 2016-08-29.
 */
public class DeliveryDetailRequest extends AbstractRequest<NetworkResult<List<DelivererData>>> {
    Request request;
    public DeliveryDetailRequest(Context context, String deliverer_id) {
        HttpUrl url = getBaseUrlBuilder()
                .addPathSegment("contracts")
                .addPathSegment("delivering")
                .addPathSegment(deliverer_id)
                .build();
        request = new Request.Builder()
                .url(url)
                .tag(context)
                .build();
    }


    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<DelivererData>>(){}.getType();
    }

    @Override
    public Request getRequest() {
        return request;
    }
}
