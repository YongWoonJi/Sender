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
 * Created by Tacademy on 2016-08-29.
 */
public class DelivererRegisterRequest extends AbstractRequest<NetworkResult<String>> {
    Request request;
    public DelivererRegisterRequest(Context context, String user_id, String here_lat, String here_lon,
                                    String next_lat, String next_lon, String dep_time, String arr_time) {
        HttpUrl url = getBaseUrlBuilder()
                .addPathSegment("contracts")
                .addPathSegment("delivering")
                .build();

        RequestBody body = new FormBody.Builder()
                .add("user_id", user_id)
                .add("here_lat", here_lat)
                .add("here_lon", here_lon)
                .add("next_lat", next_lat)
                .add("next_lon", next_lon)
                .add("dep_time", dep_time)
                .add("arr_time", arr_time)
                .build();

        request = new Request.Builder()
                .url(url)
                .post(body)
                .tag(context)
                .build();
    }


    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<String>>(){}.getType();
    }

    @Override
    public Request getRequest() {
        return request;
    }
}
