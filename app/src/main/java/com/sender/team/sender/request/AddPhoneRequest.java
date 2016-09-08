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
 * Created by Tacademy on 2016-08-29.
 */
public class AddPhoneRequest extends AbstractRequest<NetworkResult<String>> {
    Request request;
    public AddPhoneRequest (Context context, String phone){
        HttpUrl url = getSecureUrlBuilder()
                .port(443)
                .addPathSegment("members")
                .build();
        RequestBody body = new FormBody.Builder()
                .add("phone", phone)
                .build();

        request = new Request.Builder()
                .url(url)
                .put(body)
                .tag(context)
                .build();
    }

    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<String>>(){}.getType();
    }

    @Override
    public Request getRequest() {
        return request;
    }
}
