package com.sender.team.sender.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sender.team.sender.MyApplication;
import com.sender.team.sender.data.ContractIdData;
import com.sender.team.sender.data.UserData;

/**
 * Created by Tacademy on 2016-09-01.
 */
public class PropertyManager {
    private static PropertyManager instance;
    public static PropertyManager getInstance() {
//        if (instance == null) {
//            synchronized (PropertyManager.class) {
                if (instance == null) {
                    instance = new PropertyManager();
                }
                return instance;
//            }
//        }
//        return instance;
    }

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    private UserData userData;
    private String otherDelivererId;
    private int receiver_id;
    private ContractIdData contractIdData;

    private static final String KEY_REGISTRATION_ID = "regid";
    private static final String KEY_FACEBOOK_ID = "facebookid";
    private static final String KEY_DELIVERING_ID = "delivering_id";
    private static final String KEY_OTHERDELIVERING_ID = "otherdelivering_id";
    private static final String KEY_CONTRACT_ID = "contract_id";

    private String here_lat;
    private String here_lng;
    private String addr_lat;
    private String addr_lng;

    private PropertyManager() {
        Context context = MyApplication.getContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPrefs.edit();
    }

    public String getMyDeliveringId() {
        return mPrefs.getString(KEY_DELIVERING_ID, "");
    }

    public void setMyDeliveringId(String delid) {
        mEditor.putString(KEY_DELIVERING_ID, delid);
        mEditor.commit();
    }

    public String getOtherDelivererId() {
        return mPrefs.getString(KEY_OTHERDELIVERING_ID, "");
    }

    public void setOtherDelivererId(String otherDelivererId) {
        mEditor.putString(KEY_OTHERDELIVERING_ID, otherDelivererId);
        mEditor.commit();
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setUserData(UserData data) {
        userData = data;
    }

    public UserData getUserData() {
        return userData;
    }

    public ContractIdData getContractIdData() {
        return contractIdData;
    }

    public void setContractIdData(ContractIdData contractIdData) {
        this.contractIdData = contractIdData;
    }

    public void setRegistrationId(String regid) {
        mEditor.putString(KEY_REGISTRATION_ID, regid);
        mEditor.commit();
    }

    public String getRegistrationId() {
        return mPrefs.getString(KEY_REGISTRATION_ID, "");
    }

    public void setFacebookId(String facebookId) {
        mEditor.putString(KEY_FACEBOOK_ID, facebookId);
        mEditor.commit();
    }

    public String getFacebookId() {
        return mPrefs.getString(KEY_FACEBOOK_ID, "");
    }

    public String getHere_lat() {
        return here_lat;
    }

    public String getHere_lng() {
        return here_lng;
    }

    public String getAddr_lat() {
        return addr_lat;
    }

    public String getAddr_lng() {
        return addr_lng;
    }
}
