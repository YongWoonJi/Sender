package com.sender.team.sender;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.MyPageRequest;

public class SplashActivity extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MyPageRequest request = new MyPageRequest(this);
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_SECURE, request, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                PropertyManager.getInstance().setUserData(result.getResult());
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<UserData>> request, String errorMessage, Throwable e) {

            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            enableGPSSetting();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, 2000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }

    private void enableGPSSetting() {
        ContentResolver res = getContentResolver();
        boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(res, LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            new AlertDialog.Builder(this)
                    .setMessage("GPS가 필요한 서비스입니다.\nGPS를 켜시겠습니까?")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).create().show();
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}
