package com.sender.team.sender.request;

import com.google.gson.Gson;
import com.sender.team.sender.manager.NetworkRequest;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

/**
 * Created by Tacademy on 2016-08-29.
 */
public abstract class AbstractRequest<T> extends NetworkRequest<T>{

    protected HttpUrl.Builder getSecureUrlBuilder() {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme("https");
        builder.host("ec2-52-78-70-38.ap-northeast-2.compute.amazonaws.com");
        return builder;
    }

    protected HttpUrl.Builder getBaseUrlBuilder() {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme("http");
        builder.host("ec2-52-78-70-38.ap-northeast-2.compute.amazonaws.com");
        return builder;
    }

    @Override
    protected T parse(ResponseBody body) throws IOException {
        String text = body.string();
        Gson gson = new Gson();
        T result = gson.fromJson(text, getType());
        return result;
    }

    protected abstract Type getType();
}
