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
 * Created by Tacademy on 2016-08-26.
 */
public class ContractsUpdateRequest extends AbstractRequest<NetworkResult<String>> {
    Request request;
    public ContractsUpdateRequest(Context context, String constract_id, String state){
        HttpUrl url = getBaseUrlBuilder()
                .addPathSegment("contracts")
                .addPathSegment(constract_id)
                .build();

        RequestBody body = new FormBody.Builder()
                .add("state", state )
                .build();

        request = new Request.Builder()
                .url(url)
                .put(body)
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
