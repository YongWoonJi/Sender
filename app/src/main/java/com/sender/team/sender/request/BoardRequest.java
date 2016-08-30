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
 * Created by Tacademy on 2016-08-26.
 */
public class BoardRequest extends AbstractRequest<NetworkResult<String>> {
    Request request;
    public BoardRequest(Context context, String nickname, String esType, String boardType, String title, String content, String pic){
        HttpUrl url = getSecureUrlBuilder()
                .addPathSegment("boards")
                .build();

        RequestBody body = new FormBody.Builder()
                .add("nickname", nickname)
                .add("esType", esType)
                .add("boardType", boardType)
                .add("title", title)
                .add("content", content)
                .add("pic", pic)
                .build();

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
