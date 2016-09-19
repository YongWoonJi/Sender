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
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    private UserData userData;
    private String otherDelivererId;
    private int receiver_id;
    private ContractIdData contractIdData;

    private static final String KEY_REGISTRATION_ID = "regid";
    private static final String KEY_FACEBOOK_ID = "facebookid";
    private static final String KEY_MY_DELIVERING_ID = "delivering_id";
    private static final String KEY_RECEIVER_ID = "receiverid";
    private static final String KEY_OTHERDELIVERING_ID = "otherdelivering_id";
    private static final String KEY_LAST_CHATUSER_PHONE = "lastchatphone";
    private static final String KEY_LAST_CONTRACT_ID = "lastcontractid";
    private static final String KEY_CONTRACTED_RECEIVER_ID = "contractedreceiverid";

    private PropertyManager() {
        Context context = MyApplication.getContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPrefs.edit();
    }


    public String getContractedReceiverId() {
        return mPrefs.getString(KEY_CONTRACTED_RECEIVER_ID, "");
    }

    public void setContractedReceiverId(String id) {
        mEditor.putString(KEY_CONTRACTED_RECEIVER_ID, id);
        mEditor.commit();
    }

    public String getLastContractId() {
        return mPrefs.getString(KEY_LAST_CONTRACT_ID, "");
    }

    public void setLastContractId(String contractId) {
        mEditor.putString(KEY_LAST_CONTRACT_ID, "" + contractId);
        mEditor.commit();
    }

    public String getLastChatuserPhone() {
        return mPrefs.getString(KEY_LAST_CHATUSER_PHONE, "");
    }

    public void setLastChatuserPhone(String phone) {
        mEditor.putString(KEY_LAST_CHATUSER_PHONE, phone);
        mEditor.commit();
    }

    public String getMyDeliveringId() {
        return mPrefs.getString(KEY_MY_DELIVERING_ID, "");
    }

    public void setMyDeliveringId(String delid) {
        mEditor.putString(KEY_MY_DELIVERING_ID, delid);
        mEditor.commit();
    }

    public String getOtherDeliveringId() {
        return mPrefs.getString(KEY_OTHERDELIVERING_ID, "");
    }

    public void setOtherDeliveringId(String otherDelivererId) {
        mEditor.putString(KEY_OTHERDELIVERING_ID, otherDelivererId);
        mEditor.commit();
    }

    public String getReceiver_id() {
        return mPrefs.getString(KEY_RECEIVER_ID, "");
    }

    public void setReceiver_id(int receiver_id) {
        mEditor.putString(KEY_RECEIVER_ID, "" + receiver_id);
        mEditor.commit();
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

}
