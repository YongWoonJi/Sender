package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.Gson;
import com.sender.team.sender.data.POIResult;
import com.sender.team.sender.manager.NetworkRequest;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Created by Tacademy on 2016-08-30.
 */
public class POISearchRequest extends NetworkRequest<POIResult> {

    Request request;
    public POISearchRequest(Context context, String keyword) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("apis.skplanetx.com")
                .addPathSegments("/tmap/pois")
                .addQueryParameter("version","1")
                .addQueryParameter("searchKeyword", keyword)
                .addQueryParameter("resCoordType","WGS84GEO")
                .build();

        request = new Request.Builder()
                .url(url)
                .header("Accept","application/json")
                .header("appKey","be81c52f-a25c-328d-bc36-227a92c20195")
                .tag(context)
                .build();
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    protected POIResult parse(ResponseBody body) throws IOException {
        Gson gson = new Gson();
        POIResult result = gson.fromJson(body.charStream(), POIResult.class);
        return result;
    }
}
