package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.ContractsData;
import com.sender.team.sender.data.NetworkResult;

import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Tacademy on 2016-08-29.
 */
public class SenderInfoRequest extends AbstractRequest<NetworkResult<ContractsData>> {

    Request request;
    public SenderInfoRequest(Context context, String sender_id){
        HttpUrl url = getBaseUrlBuilder()
                .addPathSegment("contracts")
                .addQueryParameter("sender", sender_id)
                .build();

        request = new Request.Builder()
                .url(url)
                .tag(context)
                .build();
    }

    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<ContractsData>>(){}.getType();
    }

    @Override
    public Request getRequest() {
        return request;
    }
}
