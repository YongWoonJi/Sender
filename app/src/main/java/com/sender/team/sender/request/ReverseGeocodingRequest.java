package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.ReverseGeocodingData;

import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Tacademy on 2016-08-30.
 */
public class ReverseGeocodingRequest extends AbstractRequest<ReverseGeocodingData> {
    Request request;

    public ReverseGeocodingRequest(Context context, String lat, String lon) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("apis.skplanetx.com")
                .addPathSegments("/tmap/geo/reversegeocoding")
                .addQueryParameter("version", "1")
                .addQueryParameter("lat", lat)
                .addQueryParameter("lon", lon)
                .addQueryParameter("coordType", "WGS84GEO")
                .build();

        request = new Request.Builder()
                .url(url)
                .header("Accept","application/json")
                .header("appKey","be81c52f-a25c-328d-bc36-227a92c20195")
                .tag(context)
                .build();

    }

    @Override
    protected Type getType() {
        return new TypeToken<ReverseGeocodingData>(){}.getType();
    }

    @Override
    public Request getRequest() {
        return request;
    }
}
