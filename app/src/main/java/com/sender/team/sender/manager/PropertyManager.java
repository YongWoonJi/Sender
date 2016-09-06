package com.sender.team.sender.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sender.team.sender.MyApplication;
import com.sender.team.sender.data.ContractIdData;
import com.sender.team.sender.data.DeliveringIdData;
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

    private UserData userData;
    private DeliveringIdData deliveringId;
    private ContractIdData contractIdData;

    private static final String KEY_REGISTRATION_ID = "regid";

    private PropertyManager() {
        Context context = MyApplication.getContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPrefs.edit();
    }

    public void setUserData(UserData data) {
        userData = data;
    }

    public UserData getUserData() {
        return userData;
    }

    public DeliveringIdData getDeliveringId() {
        return deliveringId;
    }

    public void setDeliveringId(DeliveringIdData deliveringId) {
        this.deliveringId = deliveringId;
    }

    public ContractIdData getContractIdData() {
        return contractIdData;
    }

    public void setContractIdData(ContractIdData contractIdData) {
        this.contractIdData = contractIdData;
    }
    public String getRegistrationId() {
        return mPrefs.getString(KEY_REGISTRATION_ID, "");
    }
    public void setRegistrationId(String regid) {
        mEditor.putString(KEY_REGISTRATION_ID, regid);
        mEditor.commit();
    }
}
