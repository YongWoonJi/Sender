package com.sender.team.sender.data;

/**
 * Created by Tacademy on 2016-08-26.
 */
public class NetworkResult<T> {
    private T result;
    private String error;

    public T getResult() {
        return this.result;
    }

    public String getError() {
        return error;
    }
}
