package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.NoticeListData;

import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Tacademy on 2016-08-29.
 */
public class NoticeRequest extends AbstractRequest<NetworkResult<NoticeListData>> {
    Request request;
    public NoticeRequest (Context context, String currentPage, String itemsPerPage, String type){
        HttpUrl url = getBaseUrlBuilder()
                .addPathSegment("notices")
                .addQueryParameter("currentPage", currentPage)
                .addQueryParameter("itemsPerPage", itemsPerPage)
                .addQueryParameter("type", type)
                .build();

        request = new Request.Builder()
                .url(url)
                .tag(context)
                .build();
    }

    @Override
    protected HttpUrl.Builder getBaseUrlBuilder() {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme("http");
        builder.host("ec2-52-78-70-38.ap-northeast-2.compute.amazonaws.com");
        return builder;
    }

    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<NoticeListData>>(){}.getType();
    }

    @Override
    public Request getRequest() {
        return request;
    }
}
