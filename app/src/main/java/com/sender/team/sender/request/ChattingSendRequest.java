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
 * Created by Tacademy on 2016-08-26.
 */
public class ChattingSendRequest extends AbstractRequest<NetworkResult<String>> {
    MediaType mediaType = MediaType.parse("image/*");
    Request request;
    public ChattingSendRequest(Context context, String receiver_id, String message, File pic){
        HttpUrl url = getBaseUrlBuilder()
                .port(8080)
                .addPathSegment("notification")
                .addPathSegment("chattings")
                .build();

        MultipartBody.Builder builder;

        if (pic == null){
            builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("receiver_id", receiver_id)
                    .addFormDataPart("message", message);
        } else {
            builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("receiver_id", receiver_id)
                    .addFormDataPart("message", message);
            builder.addFormDataPart("pic", pic.getName(), RequestBody.create(mediaType, pic));
        }

        RequestBody body = builder.build();
        request = new Request.Builder()
                .url(url)
                .post(body)
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
