package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.ContractsInfoData;
import com.sender.team.sender.data.NetworkResult;

import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Tacademy on 2016-08-26.
 */
public class ContractsInfoRequest extends AbstractRequest<NetworkResult<ContractsInfoData>> {
    Request request;
    public ContractsInfoRequest(Context context, String constract_id){
        HttpUrl url = getBaseUrlBuilder()
                .addPathSegment("contracts")
                .addPathSegment(constract_id)
                .build();

        request = new Request.Builder()
                .url(url)
                .tag(context)
                .build();
    }
    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<ContractsInfoData>>(){}.getType();
    }
}
