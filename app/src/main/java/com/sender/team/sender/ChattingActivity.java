package com.sender.team.sender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class ChattingActivity extends AppCompatActivity {

    public static final int SEND_HEADER = 1;
    public static final int DELIVERER_HEADER = 2;


    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = (ImageView)findViewById(R.id.img_chat_profile);
        Intent intent = getIntent();
        int i = intent.getIntExtra("key", 0);

        switch (i){
            case SEND_HEADER :{
                getSupportFragmentManager().beginTransaction().add(R.id.container,new SendHeaderFragment()).commit();
                break;
            }
            case DELIVERER_HEADER :
            {
                getSupportFragmentManager().beginTransaction().add(R.id.container,new DelivererHeaderFragment()).commit();
                break;
            }
                default:
                    break;
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ChattingActivity.this, ChattingProfileActivity.class);
                startActivity(intent1);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
