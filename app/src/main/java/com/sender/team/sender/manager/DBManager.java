package com.sender.team.sender.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sender.team.sender.MyApplication;
import com.sender.team.sender.data.ChatContract;
import com.sender.team.sender.data.ChattingReceiveData;
import com.sender.team.sender.data.UserData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tacademy on 2016-09-02.
 */
public class DBManager extends SQLiteOpenHelper {

    private static DBManager instance;

    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    private static final String DB_NAME = "chat_db";
    private static final int DB_VERSION = 1;

    private DBManager() {
        super(MyApplication.getContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + ChatContract.ChatUser.TABLE + "(" +
                ChatContract.ChatUser._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ChatContract.ChatUser.COLUMN_SERVER_ID + " INTEGER," +
                ChatContract.ChatUser.COLUMN_NAME + " TEXT," +
//                ChatContract.ChatUser.COLUMN_EMAIL + " TEXT NOT NULL," +
                ChatContract.ChatUser.COLUMN_TYPE + " INTEGER," +
                ChatContract.ChatUser.COLUMN_IMAGE + " TEXT," +
                ChatContract.ChatUser.COLUMN_LAST_MESSAGE_ID + " INTEGER);";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE " + ChatContract.ChatMessage.TABLE + "(" +
                ChatContract.ChatMessage._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ChatContract.ChatMessage.COLUMN_USER_ID + " INTEGER," +
                ChatContract.ChatMessage.COLUMN_TYPE + " INTEGER," +
                ChatContract.ChatMessage.COLUMN_MESSAGE + " TEXT," +
                ChatContract.ChatMessage.COLUMN_CREATED + " INTEGER);";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    ContentValues values = new ContentValues();

    public long addUser(ChattingReceiveData data) {
        if (getUserId(Long.valueOf(data.getSender().getId())) == -1) {
            SQLiteDatabase db = getWritableDatabase();
            values.clear();
            values.put(ChatContract.ChatUser.COLUMN_SERVER_ID, data.getSender().getId());
            values.put(ChatContract.ChatUser.COLUMN_NAME, data.getSender().getName());
            values.put(ChatContract.ChatUser.COLUMN_IMAGE, data.getSender().getFileUrl());
//            values.put(ChatContract.ChatUser.COLUMN_EMAIL, user.getEmail());
            return db.insert(ChatContract.ChatUser.TABLE, null, values);
        }
        throw new IllegalArgumentException("aleady user added");
    }

    public long addUser(UserData user) {
        if (getUserId(Long.parseLong(user.getUser_id())) == -1) {
            SQLiteDatabase db = getWritableDatabase();
            values.clear();
            values.put(ChatContract.ChatUser.COLUMN_SERVER_ID, user.getUser_id());
            values.put(ChatContract.ChatUser.COLUMN_NAME, user.getName());
            values.put(ChatContract.ChatUser.COLUMN_IMAGE, user.getFileUrl());
//            values.put(ChatContract.ChatUser.COLUMN_EMAIL, user.getEmail());
            return db.insert(ChatContract.ChatUser.TABLE, null, values);
        }
        throw new IllegalArgumentException("aleady user added");
    }

    Map<Long, Long> resolveUserId = new HashMap<>();

    public long addMessage(UserData user, int type, String message, Date date) {
        Long uid = resolveUserId.get(Long.parseLong(user.getUser_id()));
        if (uid == null) {
            long id = getUserId(Long.parseLong(user.getUser_id()));
            if (id == -1) {
                id = addUser(user);
            }
            resolveUserId.put(Long.parseLong(user.getUser_id()), id);
            uid = id;
        }
        SQLiteDatabase db = getWritableDatabase();
        values.clear();
        values.put(ChatContract.ChatMessage.COLUMN_USER_ID, (long) uid);
        values.put(ChatContract.ChatMessage.COLUMN_TYPE, type);
        values.put(ChatContract.ChatMessage.COLUMN_MESSAGE, message);
        long current = date.getTime();
        values.put(ChatContract.ChatMessage.COLUMN_CREATED, current);
        try {
            db.beginTransaction();
            long mid = db.insert(ChatContract.ChatMessage.TABLE, null, values);

            values.clear();
            values.put(ChatContract.ChatUser.COLUMN_LAST_MESSAGE_ID, mid);
            String selection = ChatContract.ChatUser._ID + " = ?";
            String[] args = {"" + uid};
            db.update(ChatContract.ChatUser.TABLE, values, selection, args);
            db.setTransactionSuccessful();
            return mid;
        } finally {
            db.endTransaction();
        }

    }


    public long addMessage(ChattingReceiveData data, int type, String message, Date date){
        Long uid = resolveUserId.get(data.getSender().getId());
        if (uid == null) {
            long id = getUserId(data.getSender().getId());
            if (id == -1) {
                id = addUser(data);
            }
            resolveUserId.put(data.getSender().getId(), id);
            uid = id;
        }
        SQLiteDatabase db = getWritableDatabase();
        values.clear();
        values.put(ChatContract.ChatMessage.COLUMN_USER_ID, uid);
        values.put(ChatContract.ChatMessage.COLUMN_TYPE, type);
        values.put(ChatContract.ChatMessage.COLUMN_MESSAGE, data.getMessage());
        long current = date.getTime();
        values.put(ChatContract.ChatMessage.COLUMN_CREATED, current);
        try {
            db.beginTransaction();
            long mid = db.insert(ChatContract.ChatMessage.TABLE, null, values);

            values.clear();
            values.put(ChatContract.ChatUser.COLUMN_LAST_MESSAGE_ID, mid);
            String selection = ChatContract.ChatUser._ID + " = ?";
            String[] args = {"" + uid};
            db.update(ChatContract.ChatUser.TABLE, values, selection, args);
            db.setTransactionSuccessful();
            return mid;
        } finally {
            db.endTransaction();
        }
    }

    public long getUserId(long serverId) {
        String selection = ChatContract.ChatUser.COLUMN_SERVER_ID + " = ?";
        String[] args = {"" + serverId};
        String[] columns = {ChatContract.ChatUser._ID};
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(ChatContract.ChatUser.TABLE, columns, selection, args, null, null, null);
        try {
            if (c.moveToNext()) {
                long id = c.getLong(c.getColumnIndex(ChatContract.ChatUser._ID));
                return id;
            }
        } finally {
            c.close();
        }
        return -1;
    }

    public Cursor getChatMessage(ChattingReceiveData data) {
        long userid = -1;
        Long uid = resolveUserId.get(Long.valueOf(data.getSender().getId()));
        if (uid == null) {
            long id = getUserId(Long.valueOf(data.getSender().getId()));
            if (id != -1) {
                resolveUserId.put(Long.valueOf(data.getSender().getId()), id);
                userid = id;
            }
        } else {
            userid = uid;
        }

        String[] columns = {ChatContract.ChatMessage._ID,
                ChatContract.ChatMessage.COLUMN_TYPE,
                ChatContract.ChatMessage.COLUMN_MESSAGE,
                ChatContract.ChatMessage.COLUMN_CREATED
                };
        String selection = ChatContract.ChatMessage.COLUMN_USER_ID + " = ?";
        String[] args = {"" + userid};
        String sort = ChatContract.ChatMessage.COLUMN_CREATED + " ASC";
        SQLiteDatabase db = getReadableDatabase();
        return db.query(ChatContract.ChatMessage.TABLE, columns, selection, args, null, null, sort);
    }

    public Cursor getChatMessage(UserData user) {
        long userid = -1;
        Long uid = resolveUserId.get(Long.parseLong(user.getUser_id()));
        if (uid == null) {
            long id = getUserId(Long.parseLong(user.getUser_id()));
            if (id != -1) {
                resolveUserId.put(Long.parseLong(user.getUser_id()), id);
                userid = id;
            }
        } else {
            userid = uid;
        }

        String[] columns = {ChatContract.ChatMessage._ID,
                ChatContract.ChatMessage.COLUMN_TYPE,
                ChatContract.ChatMessage.COLUMN_MESSAGE,
                ChatContract.ChatMessage.COLUMN_CREATED};
        String selection = ChatContract.ChatMessage.COLUMN_USER_ID + " = ?";
        String[] args = {"" + userid};
        String sort = ChatContract.ChatMessage.COLUMN_CREATED + " ASC";
        SQLiteDatabase db = getReadableDatabase();
        return db.query(ChatContract.ChatMessage.TABLE, columns, selection, args, null, null, sort);
    }

//    public Cursor getChatReceiveData(){
//        String selection = ChatContract.ChatMessage.COLUMN_USER_ID + " = ?";
//        String[] args = {""+serverId};
//        String[] columns = {ChatContract.ChatUser._ID};
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c = db.query(ChatContract.ChatUser.TABLE, columns, selection, args, null, null, null);
//    }

}