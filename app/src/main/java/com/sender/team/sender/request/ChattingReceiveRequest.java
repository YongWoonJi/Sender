package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.ChattingReceiveData;
import com.sender.team.sender.data.NetworkResult;

import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Tacademy on 2016-09-02.
 */
public class ChattingReceiveRequest extends AbstractRequest<NetworkResult<ArrayList<ChattingReceiveData>>> {

    Request request;
    public ChattingReceiveRequest(Context context){
        HttpUrl url = getSecureUrlBuilder()
                .addPathSegment("chattings")
                .build();

        request = new Request.Builder()
                .url(url)
                .tag(context)
                .build();
    }

    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<ArrayList<ChattingReceiveData>>>(){}.getType();
    }

    @Override
    public Request getRequest() {
        return request;
    }
}
