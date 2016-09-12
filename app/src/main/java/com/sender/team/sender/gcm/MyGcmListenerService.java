/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sender.team.sender.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.sender.team.sender.AcceptActivity;
import com.sender.team.sender.ChattingActivity;
import com.sender.team.sender.MyApplication;
import com.sender.team.sender.R;
import com.sender.team.sender.SplashActivity;
import com.sender.team.sender.Utils;
import com.sender.team.sender.data.ChatContract;
import com.sender.team.sender.data.ChattingReceiveData;
import com.sender.team.sender.data.ContractIdData;
import com.sender.team.sender.data.ContractsInfoData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.ReverseGeocodingData;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.DBManager;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.ChattingReceiveRequest;
import com.sender.team.sender.request.ContractsInfoRequest;
import com.sender.team.sender.request.ContractsRequest;
import com.sender.team.sender.request.OtherUserRequest;
import com.sender.team.sender.request.ReverseGeocodingRequest;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    public static final String ACTION_CHAT = "com.sender.team.sender.action.chatmessage";
    public static final String EXTRA_CHAT_USER = "chatuser";
    public static final String EXTRA_RESULT = "result";

    LocalBroadcastManager mLBM;

    @Override
    public void onCreate() {
        super.onCreate();
        mLBM = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
            String cid = String.valueOf(PropertyManager.getInstance().getContractIdData().getContract_id());
            ContractsRequest request = new ContractsRequest(MyApplication.getContext(), cid, "" + PropertyManager.getInstance().getReceiver_id(), null, AcceptActivity.STATE_CONTRACT_BEFORE);
            try {
                NetworkResult<ContractIdData> result = NetworkManager.getInstance().getNetworkDataSync(NetworkManager.CLIENT_STANDARD,request);
                ContractIdData idData = result.getResult();

                if (idData.getSendingId() != 0 && idData.getContract_id() != 0){
                    searchDelivererInfo(String.valueOf(idData.getContract_id()));
                    String userId = String.valueOf(idData.getSending_user_id());
                    String contractId = String.valueOf(idData.getContract_id());
                    ChattingReceiveRequest receiveRequest = new ChattingReceiveRequest(this, userId, contractId);
                    NetworkResult<List<ChattingReceiveData>> resultMessage = NetworkManager.getInstance().getNetworkDataSync(NetworkManager.CLIENT_STANDARD,receiveRequest);
                    List<ChattingReceiveData> list = resultMessage.getResult();
                    for (ChattingReceiveData m : list){
                        try {
                            DBManager.getInstance().addMessage(m.getSender(), m.getSender().getFileUrl(), ChatContract.ChatMessage.TYPE_RECEIVE, m.getMessage(), Utils.convertStringToTime(m.getDate()));
                            Intent i = new Intent(ACTION_CHAT);
                            i.putExtra(EXTRA_CHAT_USER,m.getSender());
                            mLBM.sendBroadcastSync(i);
                            boolean processed = i.getBooleanExtra(EXTRA_RESULT, false);
                            if (!processed) {
                                sendNotification(m);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


//        sendNotification(message);
    }



    private void sendNotification(ChattingReceiveData m) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra(ChattingActivity.EXTRA_USER, m.getSender());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Chat Message")
                .setContentTitle(m.getSender().getName())
                .setContentText(m.getMessage())
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }

//    private void sendNotification(String message) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setTicker("GCM Message")
//                .setContentTitle("GCM Message")
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }

    public void searchDelivererInfo(String contractId){
        ContractsInfoRequest request = new ContractsInfoRequest(this,contractId);
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<ContractsInfoData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<ContractsInfoData>> request, NetworkResult<ContractsInfoData> result) {
                String id = String.valueOf(result.getResult().getDelivering_user_id());
                OtherUserRequest otherUserRequest = new OtherUserRequest(MyGcmListenerService.this,id);
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, otherUserRequest, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                        UserData data = result.getResult();
                        makeAddress();
                        data.setAddress(address);
                        DBManager.getInstance().addUser(data);
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result, String errorMessage, Throwable e) {

                    }
                });
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<ContractsInfoData>> request, NetworkResult<ContractsInfoData> result, String errorMessage, Throwable e) {

            }
        });
    }

    String address;
    public String makeAddress(){
        String here_lat = PropertyManager.getInstance().getHere_lat();
        String here_lng = PropertyManager.getInstance().getHere_lng();
        final String addr_lat = PropertyManager.getInstance().getAddr_lat();
        final String addr_lng = PropertyManager.getInstance().getAddr_lng();

        ReverseGeocodingRequest request = new ReverseGeocodingRequest(this, here_lat, here_lng);
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_TMAP, request, new NetworkManager.OnResultListener<ReverseGeocodingData>() {
            @Override
            public void onSuccess(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result) {
                final String start = result.getAddressInfo().getLegalDong();
                ReverseGeocodingRequest request2 = new ReverseGeocodingRequest(MyGcmListenerService.this,addr_lat, addr_lng);
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_TMAP, request2, new NetworkManager.OnResultListener<ReverseGeocodingData>() {
                    @Override
                    public void onSuccess(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result) {
                        String end = result.getAddressInfo().getLegalDong();
                        address =  start + " > " + end;
                    }

                    @Override
                    public void onFail(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result, String errorMessage, Throwable e) {

                    }
                });
            }

            @Override
            public void onFail(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result, String errorMessage, Throwable e) {

            }
        });
    return null;
    }

}
