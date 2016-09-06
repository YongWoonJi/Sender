package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.NetworkResult;

import java.lang.reflect.Type;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Tacademy on 2016-09-03.
 */
public class FacebookRequest extends AbstractRequest<NetworkResult<Integer>> {

    Request request;

    public FacebookRequest(Context context, String access_token, String registration_token) {
        HttpUrl url = getSecureUrlBuilder()
                .port(4433)
                .addPathSegment("auth")
                .addPathSegment("facebook")
                .addPathSegment("token")
                .build();
        RequestBody body = new FormBody.Builder()
                .add("access_token", access_token)
                .add("registration_token", registration_token)
                .build();

        request = new Request.Builder()
                .url(url)
                .post(body)
                .tag(context)
                .build();
    }

    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<Integer>>(){}.getType();
    }

    @Override
    public Request getRequest() {
        return request;
    }
}
