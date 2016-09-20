package com.sender.team.sender.data;

import android.provider.BaseColumns;

/**
 * Created by Tacademy on 2016-09-02.
 */
public class ChatContract {
    public interface ChatUser extends BaseColumns {
        public static final String TABLE = "chatuser";
        public static final String COLUMN_SERVER_ID = "sid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PROFILE_IMAGE = "profileimageurl";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_LAST_MESSAGE_ID = "lastid";
        public static final String COLUMN_CHAT_CONTRACT_ID = "chatcontractid";
        public static final String COLUMN_HEADER_TYPE = "headertype";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_STATE = "state";
    }

    public interface ChatMessage extends BaseColumns {
        public static final int TYPE_SEND = 0;
        public static final int TYPE_RECEIVE = 1;
        public static final int TYPE_DATE = 2;

        public static final String TABLE = "chatmessage";
        public static final String COLUMN_USER_ID = "uid";
        public static final String COLUMN_CONTRACT_ID = "contractid";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_IMAGE = "imageurl";
        public static final String COLUMN_CREATED = "created";
    }
}
