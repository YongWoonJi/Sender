package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.DeliveringHistoryData;
import com.sender.team.sender.data.NetworkResult;

import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Tacademy on 2016-09-02.
 */
public class DeliveringHistoryRequest extends AbstractRequest<NetworkResult<DeliveringHistoryData>> {
    Request request;

    public DeliveringHistoryRequest(Context context) {
        HttpUrl url = getBaseUrlBuilder()
                .addPathSegments("members/me/deliverings")
                .build();

        request = new Request.Builder()
                .url(url)
                .tag(context)
                .build();
    }

    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<DeliveringHistoryData>>(){}.getType();
    }

    @Override
    public Request getRequest() {
        return request;
    }
}
