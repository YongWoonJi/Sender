package com.sender.team.sender.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sender.team.sender.MyApplication;
import com.sender.team.sender.data.ChatContract;
import com.sender.team.sender.data.ChattingListData;
import com.sender.team.sender.data.UserData;

import java.util.Date;

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
                ChatContract.ChatUser.COLUMN_PROFILE_IMAGE + " TEXT," +
                ChatContract.ChatUser.COLUMN_PHONE + " TEXT," +
                ChatContract.ChatUser.COLUMN_HEADER_TYPE + " TEXT," +
                ChatContract.ChatUser.COLUMN_ADDRESS + " TEXT," +
                ChatContract.ChatUser.COLUMN_LAST_MESSAGE_ID + " INTEGER," +
                ChatContract.ChatUser.COLUMN_CHAT_CONTRACT_ID + " INTEGER);";
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE " + ChatContract.ChatMessage.TABLE + "(" +
                ChatContract.ChatMessage._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ChatContract.ChatMessage.COLUMN_USER_ID + " INTEGER," +
                ChatContract.ChatMessage.COLUMN_CONTRACT_ID + " INTEGER, " +
                ChatContract.ChatMessage.COLUMN_TYPE + " INTEGER," +
                ChatContract.ChatMessage.COLUMN_MESSAGE + " TEXT," +
                ChatContract.ChatMessage.COLUMN_IMAGE + " TEXT," +
                ChatContract.ChatMessage.COLUMN_CREATED + " INTEGER);";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    ContentValues values = new ContentValues();

    public long addUser(UserData user) {
        if (getUserId(Long.parseLong(user.getUser_id()), user.getContractId()) == -1) {
            SQLiteDatabase db = getWritableDatabase();
            values.clear();
            values.put(ChatContract.ChatUser.COLUMN_SERVER_ID, user.getUser_id());
            values.put(ChatContract.ChatUser.COLUMN_NAME, user.getName());
            values.put(ChatContract.ChatUser.COLUMN_PROFILE_IMAGE, user.getFileUrl());
            values.put(ChatContract.ChatUser.COLUMN_PHONE, user.getPhone());
            values.put(ChatContract.ChatUser.COLUMN_CHAT_CONTRACT_ID, user.getContractId());
            return db.insert(ChatContract.ChatUser.TABLE, null, values);
        }
        throw new IllegalArgumentException("aleady user added");
    }

    public long addUser(ChattingListData user) {
        if (getUserId(user.getId(), "" + user.getContractId()) == -1) {
            SQLiteDatabase db = getWritableDatabase();
            values.clear();
            values.put(ChatContract.ChatUser.COLUMN_SERVER_ID, user.getId());
            values.put(ChatContract.ChatUser.COLUMN_NAME, user.getName());
            values.put(ChatContract.ChatUser.COLUMN_PROFILE_IMAGE, user.getImageUrl());
            values.put(ChatContract.ChatUser.COLUMN_PHONE, user.getPhone());
            values.put(ChatContract.ChatUser.COLUMN_CHAT_CONTRACT_ID, user.getContractId());
            return db.insert(ChatContract.ChatUser.TABLE, null, values);
        }
        throw new IllegalArgumentException("aleady user added");
    }


    public long addMessage(UserData user, int headerType, String image, int type, String message, Date date) {
        long id = getUserId(Long.parseLong(user.getUser_id()), user.getContractId());
        if (id == -1) {
            id = addUser(user);
        }

        SQLiteDatabase db = getWritableDatabase();
        values.clear();
        values.put(ChatContract.ChatMessage.COLUMN_USER_ID, id);
        values.put(ChatContract.ChatMessage.COLUMN_CONTRACT_ID, user.getContractId());
        values.put(ChatContract.ChatMessage.COLUMN_TYPE, type);
        values.put(ChatContract.ChatMessage.COLUMN_MESSAGE, message);
        values.put(ChatContract.ChatMessage.COLUMN_IMAGE, image);
        long current = date.getTime();
        values.put(ChatContract.ChatMessage.COLUMN_CREATED, current);

        try {
            db.beginTransaction();
            long mid = db.insert(ChatContract.ChatMessage.TABLE, null, values);

            values.clear();
            values.put(ChatContract.ChatUser.COLUMN_LAST_MESSAGE_ID, mid);
            values.put(ChatContract.ChatUser.COLUMN_CHAT_CONTRACT_ID, user.getContractId());
            if (headerType != -1) {
                values.put(ChatContract.ChatUser.COLUMN_HEADER_TYPE, headerType);
            }
            String selection = ChatContract.ChatUser._ID + " = ? AND " + ChatContract.ChatUser.COLUMN_CHAT_CONTRACT_ID + " = ?";
            String[] args = {"" + id, user.getContractId()};
            db.update(ChatContract.ChatUser.TABLE, values, selection, args);
            db.setTransactionSuccessful();
            return mid;
        } finally {
            db.endTransaction();
        }

    }


    public long addMessage(ChattingListData user, int headerType, String image, int type, String message, Date date) {
        long id = getUserId(user.getId(), "" + user.getContractId());
        if (id == -1) {
            id = addUser(user);
        }

        SQLiteDatabase db = getWritableDatabase();
        values.clear();
        values.put(ChatContract.ChatMessage.COLUMN_USER_ID, id);
        values.put(ChatContract.ChatMessage.COLUMN_CONTRACT_ID, user.getContractId());
        values.put(ChatContract.ChatMessage.COLUMN_TYPE, type);
        values.put(ChatContract.ChatMessage.COLUMN_MESSAGE, message);
        values.put(ChatContract.ChatMessage.COLUMN_IMAGE, image);
        long current = date.getTime();
        values.put(ChatContract.ChatMessage.COLUMN_CREATED, current);

        try {
            db.beginTransaction();
            long mid = db.insert(ChatContract.ChatMessage.TABLE, null, values);

            values.clear();
            values.put(ChatContract.ChatUser.COLUMN_LAST_MESSAGE_ID, mid);
            values.put(ChatContract.ChatUser.COLUMN_CHAT_CONTRACT_ID, user.getContractId());
            if (headerType != -1) {
                values.put(ChatContract.ChatUser.COLUMN_HEADER_TYPE, headerType);
            }
            String selection = ChatContract.ChatUser._ID + " = ? AND " + ChatContract.ChatUser.COLUMN_CHAT_CONTRACT_ID + " = ?";
            String[] args = {"" + id, "" + user.getContractId()};
            db.update(ChatContract.ChatUser.TABLE, values, selection, args);
            db.setTransactionSuccessful();
            return mid;
        } finally {
            db.endTransaction();
        }

    }



    public long getUserId(long serverId, String contractId) {
        String selection = ChatContract.ChatUser.COLUMN_SERVER_ID + " = ? AND " + ChatContract.ChatUser.COLUMN_CHAT_CONTRACT_ID + " = ?";
        String[] args = {"" + serverId, contractId};
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


    public long getHeaderType(long serverId, String contractId) {
        String selection = ChatContract.ChatUser.COLUMN_SERVER_ID + " = ? AND " + ChatContract.ChatUser.COLUMN_CHAT_CONTRACT_ID + " = ?";
        String[] args = {"" + serverId, contractId};
        String[] columns = {ChatContract.ChatUser.COLUMN_HEADER_TYPE};
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(ChatContract.ChatUser.TABLE, columns, selection, args, null, null, null);
        try {
            if (c.moveToNext()) {
                long type = c.getInt(c.getColumnIndex(ChatContract.ChatUser.COLUMN_HEADER_TYPE));
                return type;
            }
        } finally {
            c.close();
        }
        return -1;
    }



    public Cursor getChatUser() {
        String table = ChatContract.ChatUser.TABLE + " INNER JOIN " +
                ChatContract.ChatMessage.TABLE + " ON " +
                ChatContract.ChatUser.TABLE + "." + ChatContract.ChatUser.COLUMN_LAST_MESSAGE_ID + " = " +
                ChatContract.ChatMessage.TABLE + "." + ChatContract.ChatMessage._ID + " AND " +
                ChatContract.ChatUser.TABLE + "." + ChatContract.ChatUser.COLUMN_CHAT_CONTRACT_ID + " = " +
                ChatContract.ChatMessage.TABLE + "." + ChatContract.ChatMessage.COLUMN_CONTRACT_ID;
        String[] columns = {ChatContract.ChatUser.TABLE + "." + ChatContract.ChatUser._ID,
                ChatContract.ChatUser.COLUMN_SERVER_ID,
                ChatContract.ChatUser.COLUMN_NAME,
                ChatContract.ChatUser.COLUMN_PROFILE_IMAGE,
                ChatContract.ChatUser.COLUMN_PHONE,
                ChatContract.ChatUser.COLUMN_HEADER_TYPE,
                ChatContract.ChatUser.COLUMN_ADDRESS,
                ChatContract.ChatMessage.COLUMN_CONTRACT_ID,
                ChatContract.ChatMessage.COLUMN_MESSAGE,
                ChatContract.ChatMessage.COLUMN_CREATED};

        String sort = ChatContract.ChatMessage.COLUMN_CREATED + " DESC";
        SQLiteDatabase db = getReadableDatabase();
        return db.query(table, columns, null, null, null, null, sort);
    }

    public Cursor getChatMessage(UserData user) {
        long userid = -1;
        long id = getUserId(Long.parseLong(user.getUser_id()), user.getContractId());
        if (id != -1) {
            userid = id;
        }

        String[] columns = {ChatContract.ChatMessage._ID,
                ChatContract.ChatMessage.COLUMN_TYPE,
                ChatContract.ChatMessage.COLUMN_MESSAGE,
                ChatContract.ChatMessage.COLUMN_IMAGE,
                ChatContract.ChatMessage.COLUMN_CREATED};
        String selection = ChatContract.ChatMessage.COLUMN_USER_ID + " = ? AND " + ChatContract.ChatMessage.COLUMN_CONTRACT_ID + " = ? AND "
                + ChatContract.ChatMessage.COLUMN_MESSAGE + " NOT NULL OR " + ChatContract.ChatMessage.COLUMN_IMAGE + " NOT NULL";
        String[] args = {"" + userid, user.getContractId()};
        String sort = ChatContract.ChatMessage.COLUMN_CREATED + " ASC";
        SQLiteDatabase db = getReadableDatabase();
        return db.query(ChatContract.ChatMessage.TABLE, columns, selection, args, null, null, sort);
    }

    public Cursor getChatMessage(ChattingListData user) {
        long userid = -1;
        long id = getUserId(user.getId(), "" + user.getContractId());
        if (id != -1) {
            userid = id;
        }

        String[] columns = {ChatContract.ChatMessage._ID,
                ChatContract.ChatMessage.COLUMN_TYPE,
                ChatContract.ChatMessage.COLUMN_MESSAGE,
                ChatContract.ChatMessage.COLUMN_IMAGE,
                ChatContract.ChatMessage.COLUMN_CREATED};
        String selection = ChatContract.ChatMessage.COLUMN_USER_ID + " = ? AND " + ChatContract.ChatMessage.COLUMN_CONTRACT_ID + " = ? AND "
                + ChatContract.ChatMessage.COLUMN_MESSAGE + " NOT NULL OR " + ChatContract.ChatMessage.COLUMN_IMAGE + " NOT NULL";
        String[] args = {"" + userid, String.valueOf(user.getContractId())};
        String sort = ChatContract.ChatMessage.COLUMN_CREATED + " ASC";
        SQLiteDatabase db = getReadableDatabase();
        return db.query(ChatContract.ChatMessage.TABLE, columns, selection, args, null, null, sort);
    }

}
