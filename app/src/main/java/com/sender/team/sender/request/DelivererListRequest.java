package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.DelivererListData;
import com.sender.team.sender.data.NetworkResult;

import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Tacademy on 2016-08-29.
 */
public class DelivererListRequest extends AbstractRequest<NetworkResult<DelivererListData>> {
    Request request;
    public DelivererListRequest(Context context, String currentPage, String itemsPerPage) {
        HttpUrl url = getSecureUrlBuilder()
                .port(443)
                .addPathSegment("deliverings")
                .addQueryParameter("currentPage", currentPage)
                .addQueryParameter("itemsPerPage", itemsPerPage)
                .build();
        request = new Request.Builder()
                .url(url)
                .tag(context)
                .build();
    }


    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<DelivererListData>>(){}.getType();
    }

    @Override
    public Request getRequest() {
        return request;
    }
}
