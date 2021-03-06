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
public class ReviewRequest extends AbstractRequest<NetworkResult<String>> {
    Request request;
    public ReviewRequest(Context context, String contract_id, String content ,String star){
        HttpUrl url = getBaseUrlBuilder()
                .port(80)
                .addPathSegment("reviews")
                .build();

        RequestBody body = new FormBody.Builder()
                .add("contract_id", contract_id )
                .add("content", content )
                .add("star", star )
                .build();

        request = new Request.Builder()
                .url(url)
                .post(body)
                .tag(context)
                .build();
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
