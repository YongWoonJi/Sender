package com.sender.team.sender;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportFragmentManager().beginTransaction().add(R.id.container, new LoginFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
