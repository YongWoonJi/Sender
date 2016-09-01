package com.sender.team.sender;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sender.team.sender.data.UserData;

/**
 * Created by Tacademy on 2016-09-01.
 */
public class PropertyManager {
    private static PropertyManager instance;
    public static PropertyManager getInstance() {
        if (instance == null) {
            synchronized (PropertyManager.class) {
                if (instance == null) {
                    instance = new PropertyManager();
                }
                return instance;
            }
        }
        return instance;
    }

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    private PropertyManager() {
        Context context = MyApplication.getContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPrefs.edit();
    }

    UserData userData;
    public void setUserData(UserData data) {
        userData = data;
    }

    public UserData getUserData() {
        return userData;
    }
}