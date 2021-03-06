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
public class ProfilePictureUploadRequest extends AbstractRequest<NetworkResult<String>> {
    MediaType mediaType = MediaType.parse("image/*");
    Request request;

    public ProfilePictureUploadRequest(Context context, File file) {
        HttpUrl url = getBaseUrlBuilder()
                .port(80)
                .addPathSegment("members")
                .addPathSegment("me")
                .build();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (file != null) {
            builder.addFormDataPart("pic", file.getName(), RequestBody.create(mediaType, file));
        }
        RequestBody body = builder.build();
        request = new Request.Builder()
                .url(url)
                .put(body)
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
