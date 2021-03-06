package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;

import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Tacademy on 2016-08-26.
 */
public class OtherUserRequest extends AbstractRequest<NetworkResult<UserData>> {
    Request request;
    public OtherUserRequest(Context context, String user_id){
        HttpUrl url = getSecureUrlBuilder()
                .addPathSegment("members")
                .addPathSegment(user_id)
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
        return new TypeToken<NetworkResult<UserData>>(){}.getType();
    }


}
