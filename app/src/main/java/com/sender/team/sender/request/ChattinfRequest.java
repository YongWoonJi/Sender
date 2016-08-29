package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.ChattingData;
import com.sender.team.sender.data.NetworkResult;

import java.lang.reflect.Type;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Tacademy on 2016-08-26.
 */
public class ChattinfRequest extends AbstractRequest<NetworkResult<ChattingData>> {
    Request request;
    public ChattinfRequest(Context context, String receiver_id, String message, String pic){
        HttpUrl url = getBaseUrlBuilder()
                .addPathSegment("chattings")
                .build();

        RequestBody body = new FormBody.Builder()
                .add("receiver_id", receiver_id )
                .add("message", message )
                .add("pic", pic )
                .build();

        request = new Request.Builder()
                .url(url)
                .post(body)
                .tag(context)
                .build();
    }

    protected HttpUrl.Builder getBaseUrlBuilder() {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme("http");
        builder.host("ec2-52-78-70-38.ap-northeast-2.compute.amazonaws.com");
        return builder;
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<ChattingData>>(){}.getType();
    }
}
