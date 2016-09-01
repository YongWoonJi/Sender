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
public class BoardRequest extends AbstractRequest<NetworkResult<String>> {
    MediaType mediaType = MediaType.parse("image/*");
    Request request;
    public BoardRequest(Context context, String name, String esType, String boardType, String title, String content, File file) {
        HttpUrl url = getSecureUrlBuilder()
                .addPathSegment("boards")
                .build();

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("nickname", name)
                .addFormDataPart("esType", esType)
                .addFormDataPart("boardType", boardType)
                .addFormDataPart("title", title)
                .addFormDataPart("content", content);
        if (file != null) {
            builder.addFormDataPart("pic", file.getName(), RequestBody.create(mediaType, file));
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
