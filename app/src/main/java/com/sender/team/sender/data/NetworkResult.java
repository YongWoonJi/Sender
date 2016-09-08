package com.sender.team.sender.data;

/**
 * Created by Tacademy on 2016-08-26.
 */
public class NetworkResult<T> {
    private T result;
    private int error;

    public T getResult() {
        return this.result;
    }

    public int getError() {
        return error;
    }
}
