package com.sender.team.sender.data;

/**
 * Created by Tacademy on 2016-09-02.
 */
public class ChattingListData {
    public static final int TYPE_SENDER = 0;
    public static final int TYPE_DELIVERER = 1;
    public static final int TYPE_EMPTY = 2;

    private String name;
    private String message;
    private String time;
    private String imageUrl;
    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
