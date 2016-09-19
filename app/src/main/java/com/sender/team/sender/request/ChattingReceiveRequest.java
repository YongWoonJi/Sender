package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.ChattingReceiveData;
import com.sender.team.sender.data.NetworkResult;

import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Tacademy on 2016-09-02.
 */
public class ChattingReceiveRequest extends AbstractRequest<NetworkResult<ChattingReceiveData>> {

    Request request;
    public ChattingReceiveRequest(Context context, String sendId, String contractId){
        HttpUrl url = getSecureUrlBuilder()
                .port(443)
                .addPathSegment("chattings")
                .addQueryParameter("senderId", sendId)
                .addQueryParameter("contractId", contractId)
                .build();

        request = new Request.Builder()
                .url(url)
                .tag(context)
                .build();
    }

    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<ChattingReceiveData>>(){}.getType();
    }

    @Override
    public Request getRequest() {
        return request;
    }
}
