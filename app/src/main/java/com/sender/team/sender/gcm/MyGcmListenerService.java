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

import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.gcm.GcmListenerService;
import com.sender.team.sender.AcceptActivity;
import com.sender.team.sender.ChattingActivity;
import com.sender.team.sender.R;
import com.sender.team.sender.SplashActivity;
import com.sender.team.sender.Utils;
import com.sender.team.sender.data.ChatContract;
import com.sender.team.sender.data.ChattingListData;
import com.sender.team.sender.data.ChattingReceiveData;
import com.sender.team.sender.data.ChattingReceiveMessage;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.DBManager;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.ChattingReceiveRequest;
import com.sender.team.sender.request.OtherUserRequest;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MyGcmListenerService extends GcmListenerService {

    public static final String ACTION_CHAT = "com.sender.team.sender.action.chatmessage";
    public static final String ACTION_CONFIRM = "com.sender.team.sender.action.confirm";
    public static final String EXTRA_CHAT_USER = "chatuser";
    public static final String EXTRA_RESULT = "result";
    public static final String EXTRA_RESULT_CONFIRM = "result_confirm";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_SENDER_ID = "sender_id";
    public static final String EXTRA_CONTRACT_ID = "contract_id";
    public static final String EXTRA_REJECT = "reject";

    public static final String TYPE_DELIVERY = "delivery";
    public static final String TYPE_CHATTING = "chat";
    public static final String TYPE_CONFIRM = "confirm";
    public static final String TYPE_REJECT = "reject";


    LocalBroadcastManager mLBM;
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        mLBM = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
            if (PropertyManager.getInstance().getAlarmSetting()) {
                String type = data.getString(EXTRA_TYPE);
                switch (type) {
                    case TYPE_DELIVERY:
                        popupDeliveryRequest(data);
                        break;
                    case TYPE_CHATTING:
                        chattingReceive(data);
                        break;
                    case TYPE_CONFIRM:
                        confirmNotification(data.getString(EXTRA_TYPE));
                        break;
                    case TYPE_REJECT:
                        deliveryReject(data);
                        break;
                }
            }
        }
    }

    private void chattingReceive(Bundle data){
        ChattingReceiveRequest request = new ChattingReceiveRequest(this, data.getString(EXTRA_SENDER_ID), data.getString(EXTRA_CONTRACT_ID));
        try {
            NetworkResult<ChattingReceiveData> result = NetworkManager.getInstance().getNetworkDataSync(NetworkManager.CLIENT_STANDARD, request);
            ChattingReceiveData cData = result.getResult();
            for (ChattingReceiveMessage c : cData.getData()) {
                if (c.getMessage().equals(ChattingActivity.STATE_PRODUCT_DELIVER)) {
                    DBManager.getInstance().updateState(cData.getSender(), ChattingActivity.STATE_PRODUCT_DELIVER, Utils.convertStringToTime(c.getDate()));
                } else if (c.getMessage().equals(ChattingActivity.STATE_DELIVERY_COMPLETE)) {
                    DBManager.getInstance().updateState(cData.getSender(), ChattingActivity.STATE_DELIVERY_COMPLETE, Utils.convertStringToTime(c.getDate()));
                } else {
                    DBManager.getInstance().addMessage(cData.getSender(), -1, c.getUrl(), ChatContract.ChatMessage.TYPE_RECEIVE, c.getMessage(), Utils.convertStringToTime(c.getDate()));
                    Intent i = new Intent(ACTION_CHAT);
                    i.putExtra(EXTRA_CHAT_USER, cData.getSender());
                    mLBM.sendBroadcastSync(i);
                    boolean processed = i.getBooleanExtra(EXTRA_RESULT, false);
                    if (!processed) {
                        sendChatNotification(cData);
                        sendToast(cData, c);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void sendChatNotification(ChattingReceiveData m) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra(ChattingActivity.EXTRA_USER, m.getSender());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                if (!TextUtils.isEmpty(m.getSender().getFileUrl())) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = Glide.with(getApplicationContext()).load(m.getSender().getFileUrl()).asBitmap().into(-1, -1).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        notificationBuilder.setLargeIcon(bitmap);
                    }
                } else {
                    notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.default_profile));
                }
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(m.getSender().getName())
                .setContentText(m.getData().get(m.getData().size() - 1).getMessage())
                .setNumber(m.getData().size())
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendToast(final ChattingReceiveData data, final ChattingReceiveMessage c) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.toast_notify, null);
                ImageView imageProfile = (ImageView) view.findViewById(R.id.imageProfile);
                TextView textName = (TextView) view.findViewById(R.id.textName);
                TextView textMessage = (TextView) view.findViewById(R.id.textMessage);
                Glide.with(getApplicationContext()).load(data.getSender().getFileUrl()).into(imageProfile);
                if (!TextUtils.isEmpty(data.getSender().getName())) {
                    textName.setText(data.getSender().getName());
                }
                if (!TextUtils.isEmpty(c.getMessage())) {
                    textMessage.setText(c.getMessage());
                } else {
                    textMessage.setText("사진");
                }

                Toast toast = new Toast(getApplicationContext());
                float dp = 65;
                int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, pixel);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();
            }
        });
    }

    private void sendNotificationConfirm() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("SENDER")
                .setContentText("배송요청이 수락 되었습니다")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void confirmNotification(final String message) {
        OtherUserRequest request = new OtherUserRequest(this, PropertyManager.getInstance().getReceiver_id());
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                UserData data;
                data = result.getResult();
                data.setContractId(PropertyManager.getInstance().getLastContractId());
                DBManager.getInstance().addMessage(data, ChattingListData.TYPE_SENDER, null, ChatContract.ChatMessage.TYPE_SEND, null, new Date());

                Intent i = new Intent(ACTION_CONFIRM);
                mLBM.sendBroadcastSync(i);
                boolean processed = i.getBooleanExtra(EXTRA_RESULT_CONFIRM, false);
                if (!processed) {
                    sendNotificationConfirm();
                }
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result, String errorMessage, Throwable e) {

            }
        });
    }


    private void sendNotification(String message) {
        Intent intent = new Intent(this, AcceptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("SENDER")
                .setContentText("배송요청이 왔습니다")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void popupDeliveryRequest(Bundle data) {
        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            Intent intent = new Intent(this, AcceptActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            sendNotification(data.getString("type"));
        }
    }

    private void deliveryReject(Bundle data) {
            sendRejectNotification();
    }

    private void sendRejectNotification() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("SENDER")
                .setContentText("배송요청이 왔습니다")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
