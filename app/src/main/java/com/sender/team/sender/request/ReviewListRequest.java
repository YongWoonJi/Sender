package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.ReviewListData;

import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Tacademy on 2016-08-26.
 */
public class ReviewListRequest extends AbstractRequest<NetworkResult<ReviewListData>> {
    Request request;
    public ReviewListRequest(Context context, String currentPage , String itemsPerPage, String deliverer_id ){
        HttpUrl url = getBaseUrlBuilder()
                .addPathSegment("reviews")
                .addQueryParameter("currentPage",currentPage)
                .addQueryParameter("itemsPerPage",itemsPerPage)
                .addQueryParameter("deliverer_id",deliverer_id)
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
        return new TypeToken<NetworkResult<ReviewListData>>(){}.getType();
    }
}
