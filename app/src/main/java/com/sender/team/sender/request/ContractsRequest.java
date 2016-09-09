package com.sender.team.sender.request;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.sender.team.sender.data.ContractIdData;
import com.sender.team.sender.data.NetworkResult;

import java.lang.reflect.Type;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Tacademy on 2016-08-26.
 */
public class ContractsRequest extends AbstractRequest<NetworkResult<ContractIdData>> {
    Request request;
    public ContractsRequest(Context context, String constract_id ,String delivering_id, String state){
        HttpUrl url = getBaseUrlBuilder()
                .port(80)
                .addPathSegment("contracts")
                .build();

        RequestBody body = null;

        if (delivering_id == null && state != null) {
            body = new FormBody.Builder()
                    .add("contract_id", constract_id)
                    .add("state", state )
                    .build();
        } else if(delivering_id != null && state == null) {
            body = new FormBody.Builder()
                    .add("contract_id", constract_id)
                    .add("delivering_id", delivering_id )
                    .build();
        }

        request = new Request.Builder()
                .url(url)
                .put(body)
                .tag(context)
                .build();
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    protected Type getType() {
        return new TypeToken<NetworkResult<ContractIdData>>(){}.getType();
    }
}
