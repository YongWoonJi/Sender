package com.sender.team.sender;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.OtherUserRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChattingProfileActivity extends AppCompatActivity {
    @BindView(R.id.imageView2)
    ImageView imageProfile;
    @BindView(R.id.text_name)
    TextView name;
    @BindView(R.id.text_phone)
    TextView phone;
    @BindView(R.id.img_close)
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_profile);
        ButterKnife.bind(this);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        initData();
    }

    @OnClick(R.id.btn_profile_call)
    void onClick() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-0000-0000"));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
        finish();
    }

    private void initData() {
        OtherUserRequest request = new OtherUserRequest(this, "3");
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_SECURE, request, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                name.setText(result.getResult().getName());
                phone.setText(result.getResult().getPhone());
                String userImage = result.getResult().getPic();
                Log.i("ChattingProfile", userImage);
                Glide.with(ChattingProfileActivity.this)
                        .load(userImage)
                        .into(imageProfile);
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<UserData>> request, String errorMessage, Throwable e) {
                Toast.makeText(ChattingProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
