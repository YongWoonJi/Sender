package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.manager.NetworkRequest;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Created by Tacademy on 2016-08-26.
 */
public class MyPageRequest extends NetworkRequest<NetworkResult<UserData>> {
    Request request;
    public MyPageRequest(Context context){
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("ec2-52-78-70-38.ap-northeast-2.compute.amazonaws.com")
                .addPathSegment("members")
                .addPathSegment("me")
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
    protected NetworkResult<UserData> parse(ResponseBody body) throws IOException {
        String text = body.string();
        Gson gson = new Gson();
        Type type = new TypeToken<NetworkResult<UserData>>(){}.getType();
        NetworkResult<UserData> result = gson.fromJson(text, type);
        return result;
    }

}
