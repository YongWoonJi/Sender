package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.NetworkResult;

import java.io.File;
import java.lang.reflect.Type;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Tacademy on 2016-08-29.
 */
public class SenderRequest extends AbstractRequest<NetworkResult<String>> {
    MediaType mediaType = MediaType.parse("image/*");
    Request request;
    public SenderRequest (Context context, String user_id, String addr_lat, String addr_lon, String rec_phone,
                          String price, String info, File file, String memo){
        HttpUrl url = getSecureUrlBuilder()
                .addPathSegment("contracts")
                .build();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("addr_lat", addr_lat)
                .addFormDataPart("addr_lon", addr_lon)
                .addFormDataPart("rec_phone", rec_phone)
                .addFormDataPart("price", price)
                .addFormDataPart("info", info);
        if (file != null) {
            builder.addFormDataPart("pic", file.getName(), RequestBody.create(mediaType, file));
        }
        builder.addFormDataPart("memo", memo);
        RequestBody body = builder.build();
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
