package com.sender.team.sender;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sender.team.sender.data.ChatContract;
import com.sender.team.sender.data.ChattingReceiveData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.DBManager;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.ChattingReceiveRequest;
import com.sender.team.sender.request.ChattingSendRequest;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChattingActivity extends AppCompatActivity{

    public static final String HEADER_TYPE = "headertype";
    public static final int SEND_HEADER = 1;
    public static final int DELIVERER_HEADER = 2;

    @BindView(R.id.rv_list2)
    RecyclerView listview;

    @BindView(R.id.btn_message_send)
    Button messageSend;

    @BindView(R.id.edit_message_send)
    EditText editMessage;

    ChattingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
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

        mAdapter = new ChattingAdapter();
        listview.setAdapter(mAdapter);
        listview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        editMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    onClickSendMessage();
                }
                return false;
            }
        });

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent1 = new Intent(ChattingActivity.this, ChattingProfileActivity.class);
//                startActivity(intent1);
//            }
//        });

    }


    UserData user = PropertyManager.getInstance().getUserData();

    @OnClick(R.id.btn_message_send)
    public void onClickSendMessage(){
        final String message = editMessage.getText().toString();
        if (!TextUtils.isEmpty(message)){
            String userUrl = PropertyManager.getInstance().getUserData().getFileUrl();

            ChattingSendRequest request = new ChattingSendRequest(this, "1",message, userUrl);
            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_SECURE, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                @Override
                public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                    if (!TextUtils.isEmpty(result.getResult())){
                        DBManager.getInstance().addMessage(user, ChatContract.ChatMessage.TYPE_SEND, message, new Date());
                        updateMessage();

                        ChattingReceiveRequest receiveRequest = new ChattingReceiveRequest(ChattingActivity.this);
                        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_SECURE, receiveRequest, new NetworkManager.OnResultListener<NetworkResult<ArrayList<ChattingReceiveData>>>() {
                            @Override
                            public void onSuccess(NetworkRequest<NetworkResult<ArrayList<ChattingReceiveData>>> request, NetworkResult<ArrayList<ChattingReceiveData>> result) {
                                DBManager.getInstance().addMessage(result.getResult().get(0), ChatContract.ChatMessage.TYPE_RECEIVE, new Date());
                                updateMessage(result.getResult().get(0));
                            }

                            @Override
                            public void onFail(NetworkRequest<NetworkResult<ArrayList<ChattingReceiveData>>> request, String errorMessage, Throwable e) {

                            }
                        });

                    }
                }

                @Override
                public void onFail(NetworkRequest<NetworkResult<String>> request, String errorMessage, Throwable e) {
                    Toast.makeText(ChattingActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            });
            editMessage.setText("");
            if (mAdapter.getItemCount() > 0){
                listview.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }
        }else{
            Toast.makeText(ChattingActivity.this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        updateMessage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.changeCursor(null);
    }

    private void updateMessage() {
        Cursor c = DBManager.getInstance().getChatMessage(user);
        mAdapter.changeCursor(c);
    }
    private void updateMessage(ChattingReceiveData data) {
        Cursor c = DBManager.getInstance().getChatMessage(data);
        mAdapter.changeCursor(c);
    }




}
