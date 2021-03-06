package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.NetworkResult;

import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Tacademy on 2016-08-26.
 */
public class LogoutRequest extends AbstractRequest<NetworkResult<String>> {
    Request request;
        public LogoutRequest(Context context){
            HttpUrl url = getBaseUrlBuilder()
                    .port(80)
                    .addPathSegment("auth")
                    .addPathSegment("logout")
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
        return new TypeToken<NetworkResult<String>>(){}.getType();
    }


}
