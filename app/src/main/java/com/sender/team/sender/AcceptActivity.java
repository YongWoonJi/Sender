package com.sender.team.sender;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sender.team.sender.data.ContractsData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.ContractsRequest;
import com.sender.team.sender.request.SenderInfoRequest;

import java.text.ParseException;
import java.util.Calendar;

public class AcceptActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.accept_layout, null);
        final ImageView imageProduct = (ImageView) dialogView.findViewById(R.id.image_product);
        final TextView textWord = (TextView) dialogView.findViewById(R.id.text_word);
        final TextView textDetail = (TextView) dialogView.findViewById(R.id.text_details);


        SenderInfoRequest request = new SenderInfoRequest(this, "1");
        NetworkManager.getInstance().getNetworkData(request, new NetworkManager.OnResultListener<NetworkResult<ContractsData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<ContractsData>> request, NetworkResult<ContractsData> result) {
                ContractsData data = result.getResult();
                Glide.with(AcceptActivity.this)
                        .load(data.getPic()[0].getFileUrl())
                        .into(imageProduct);
                try {
                    Calendar ca = Calendar.getInstance();
                    ca.setTime(Utils.convertStringToTime(data.getArr_time()));

                textWord.setText(ca.get(Calendar.HOUR_OF_DAY) + ":" + ca.get(Calendar.MINUTE) + " 도착 / " + data.getInfo() + " / " + data.getPrice() + "원");
                textDetail.setText(data.getNickname() + "님으로부터 배송요청이 왔습니다\n수락하시겠습니까?");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

           @Override
            public void onFail(NetworkRequest<NetworkResult<ContractsData>> request, String errorMessage, Throwable e) {

            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle("배송요청");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //13.계약 체결하기
                ContractsRequest request = new ContractsRequest(AcceptActivity.this, "1","1");
                NetworkManager.getInstance().getNetworkData(request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                        Toast.makeText(AcceptActivity.this, "계약성공: "+result , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<String>> request, String errorMessage, Throwable e) {
                        Toast.makeText(AcceptActivity.this, "계약 실패", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Intent intent2 = new Intent(getApplicationContext(), ChattingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                Intent[] intents = {intent, intent2};

                startActivities(intents);
                finish();
            }
        });

        builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
//        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
    }
}
