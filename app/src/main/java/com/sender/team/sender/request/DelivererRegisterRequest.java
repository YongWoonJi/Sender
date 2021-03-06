package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.DeliveringIdData;
import com.sender.team.sender.data.NetworkResult;

import java.lang.reflect.Type;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Tacademy on 2016-08-29.
 */
public class DelivererRegisterRequest extends AbstractRequest<NetworkResult<DeliveringIdData>> {
    Request request;
    public DelivererRegisterRequest(Context context, String here_lat, String here_lon, String here_unit,
                                    String next_lat, String next_lon, String next_unit, String dep_time, String arr_time) {
        HttpUrl url = getSecureUrlBuilder()
                .port(443)
                .addPathSegment("deliverings")
                .build();

        RequestBody body = new FormBody.Builder()
                .add("here_lat", here_lat)
                .add("here_lon", here_lon)
                .add("here_unit", here_unit)
                .add("next_lat", next_lat)
                .add("next_lon", next_lon)
                .add("next_unit", next_unit)
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
        return new TypeToken<NetworkResult<DeliveringIdData>>(){}.getType();
    }

    @Override
    public Request getRequest() {
        return request;
    }
}
