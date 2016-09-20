package com.sender.team.sender;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sender.team.sender.data.ChattingListData;
import com.sender.team.sender.data.UserData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChattingProfileActivity extends AppCompatActivity {
    public static final String EXTRA_USER = "exuser";
    public static final String EXTRA_CUSER = "cuser";

    @BindView(R.id.imageView2)
    ImageView imageProfile;
    @BindView(R.id.text_name)
    TextView name;
    @BindView(R.id.text_phone)
    TextView phone;
    @BindView(R.id.img_close)
    ImageView img;

    UserData user;
    ChattingListData cUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_profile);
        ButterKnife.bind(this);

        user = (UserData) getIntent().getSerializableExtra(EXTRA_USER);
        if (user == null) {
            cUser = (ChattingListData) getIntent().getSerializableExtra(EXTRA_CUSER);
        }

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initData();
    }

    @OnClick(R.id.btn_profile_call)
    void onClick() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.getText().toString()));
        startActivity(intent);
    }

    private void initData() {
        if (user != null) {
            if (!TextUtils.isEmpty(user.getName())) {
                name.setText(user.getName());
            }
            if (!TextUtils.isEmpty(user.getPhone())) {
                phone.setText(user.getPhone());
            }
            if (!TextUtils.isEmpty(user.getFileUrl())) {
                Glide.with(this).load(user.getFileUrl()).into(imageProfile);
            }
        } else {
            if (!TextUtils.isEmpty(cUser.getName())) {
                name.setText(cUser.getName());
            }
            if (!TextUtils.isEmpty(cUser.getPhone())) {
                phone.setText(cUser.getPhone());
            }
            if (!TextUtils.isEmpty(cUser.getImageUrl())) {
                Glide.with(this).load(cUser.getImageUrl()).into(imageProfile);
            }
        }

    }


}
