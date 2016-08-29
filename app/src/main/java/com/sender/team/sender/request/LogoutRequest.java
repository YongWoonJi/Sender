package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.NetworkResult;

import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Tacademy on 2016-08-26.
 */
public class LogoutRequest extends AbstractRequest<NetworkResult<String>> {
    Request request;
        public LogoutRequest(Context context){
            HttpUrl url = getBaseUrlBuilder()
                    .addPathSegment("auth")
                    .addPathSegment("local")
                    .addPathSegment("logout")
                    .build();
            request = new Request.Builder()
                    .url(url)
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
        return new TypeToken<NetworkResult<String>>(){}.getType();
    }


}
