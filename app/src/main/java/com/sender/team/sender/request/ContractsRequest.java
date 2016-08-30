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
public class ContractsRequest extends AbstractRequest<NetworkResult<String>> {
    Request request;
    public ContractsRequest(Context context, String constract_id ,String deliverer_id ){
        HttpUrl url = getBaseUrlBuilder()
                .addPathSegment("contracts")
                .build();

        RequestBody body = new FormBody.Builder()
                .add("constract_id", constract_id )
                .add("deliverer_id", deliverer_id )
                .build();

        request = new Request.Builder()
                .url(url)
                .put(body)
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
