package com.sender.team.sender;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sender.team.sender.data.ChatContract;
import com.sender.team.sender.data.ChattingListData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.gcm.MyGcmListenerService;
import com.sender.team.sender.manager.DBManager;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.ChattingSendRequest;

import java.io.File;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChattingActivity extends AppCompatActivity implements ChattingAdapter.ChattingImage{

    public static final String HEADER_TYPE = "headertype";
    public static final int SEND_HEADER = 1;
    public static final int DELIVERER_HEADER = 2;
    public static final String EXTRA_USER = "user";
    public static final String EXTRA_CHATTINGLIST_DATA = "main_user";


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv_list2)
    RecyclerView listview;

    @BindView(R.id.edit_message_send)
    EditText editMessage;

    @BindView(R.id.image_chatting_picture)
    ImageView imageChatPicture;

    ChattingAdapter mAdapter;

    UserData user;
    ChattingListData cUser;
    private boolean isUserDataEmpty = false;

    LocalBroadcastManager mLBM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);


        user = (UserData) getIntent().getSerializableExtra(EXTRA_USER);
        if (user == null) {
            cUser = (ChattingListData) getIntent().getSerializableExtra(EXTRA_CHATTINGLIST_DATA);
            isUserDataEmpty = true;
        }


        mAdapter = new ChattingAdapter();
        listview.setAdapter(mAdapter);
        listview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        mLBM = LocalBroadcastManager.getInstance(this);

        Intent intent = getIntent();
        int i = intent.getIntExtra(HEADER_TYPE, 0);

        switch (i){
            case SEND_HEADER :{
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SendHeaderFragment()).commit();
                break;
            }
            case DELIVERER_HEADER : {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new DelivererHeaderFragment()).commit();
                break;
            }
                default:
                    break;
        }

        /////////////////////////////// 임의 데이터 처리
//        PropertyManager.getInstance().getUserData().setUser_id("1");
//        PropertyManager.getInstance().getUserData().setAddress("");
//        PropertyManager.getInstance().getUserData().setFileUrl("");
//        DBManager.getInstance().addMessage(PropertyManager.getInstance().getUserData(), null, ChatContract.ChatMessage.TYPE_SEND, "안녕하세요?", new Date());
//        DBManager.getInstance().addMessage(PropertyManager.getInstance().getUserData(), null, ChatContract.ChatMessage.TYPE_RECEIVE, "어 그래", new Date());
//        DBManager.getInstance().addMessage(PropertyManager.getInstance().getUserData(), null, ChatContract.ChatMessage.TYPE_SEND, "윗쪽과 왼쪽에는 1px의 검정 라인으로 늘어날 부분을 지정하고 오른쪽과 아랫쪽에는 마찬가지로 검정 라인으로 내용이 들어갈 부분을 지정해 줍니다.", new Date());
//        DBManager.getInstance().addMessage(PropertyManager.getInstance().getUserData(), null, ChatContract.ChatMessage.TYPE_RECEIVE, "사드 배치는 한국의 내정으로 중국 정부는 간섭할 권리가 없다\", \"사드 배치가 이유가 있다\", \"우리도 사드를 배치하자\", \"북한이 탄도미사일에다 핵실험까지 하는 마당이 한국의 사드 배치 결정이 어떻게 틀린 것이냐\"는 내용의 글이 잇따랐다", new Date());

        editMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    onClickSendMessage();
                }
                return false;
            }
        });

        mAdapter.setOnClickChatImageListener(this); // 옵저버 연결

        imageChatPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if (savedInstanceState != null) {
            String path = savedInstanceState.getString(FIELD_SAVE_FILE);
            if (!TextUtils.isEmpty(path)) {
                savedFile = new File(path);
            }
            path = savedInstanceState.getString(FIELD_UPLOAD_FILE);
            if (!TextUtils.isEmpty(path)) {
                uploadFile = new File(path);
//                Glide.with(this)
//                        .load(uploadFile)
//                        .into(objectImage);
            }
        }

        imageChatPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChattingActivity.this);
                builder.setTitle(R.string.profile_image);
                builder.setItems(R.array.select_image, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case INDEX_CAMERA:
                                getCaptureImage();
                                break;
                            case INDEX_GALLERY:
                                getGalleryImage();
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });

    }


    @OnClick(R.id.btn_message_send)
    public void onClickSendMessage(){
        final String message = editMessage.getText().toString();
        if (!TextUtils.isEmpty(message)) {
//            String userUrl = PropertyManager.getInstance().getUserData().getFileUrl();

            String contractId;
            String receiverId;
            if (isUserDataEmpty) {
                contractId = String.valueOf(cUser.getContractId());
                receiverId = String.valueOf(cUser.getId());
            } else {
                contractId = user.getContractId();
                receiverId = user.getUser_id();
            }
            ChattingSendRequest request = new ChattingSendRequest(this, contractId, receiverId ,message, uploadFile);
            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                @Override
                public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                    if (!TextUtils.isEmpty(result.getResult())){
                        DBManager.getInstance().addMessage(user, path, ChatContract.ChatMessage.TYPE_SEND, message, new Date());
                        updateMessage();

                    }
                }

                @Override
                public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                    Toast.makeText(ChattingActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            });

            editMessage.setText("");
            if (mAdapter.getItemCount() > 0) {
                listview.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }
        }else {
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
        mLBM.registerReceiver(mReceiver, new IntentFilter(MyGcmListenerService.ACTION_CHAT));
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UserData u = (UserData) intent.getSerializableExtra(MyGcmListenerService.EXTRA_CHAT_USER);
            if (u.getUser_id() == user.getUser_id()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateMessage();
                    }
                });
                intent.putExtra(MyGcmListenerService.EXTRA_RESULT, true);
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.changeCursor(null);
        mLBM.unregisterReceiver(mReceiver);
    }

    private void updateMessage() {
        Cursor c = null;
        if (user != null) {
            c = DBManager.getInstance().getChatMessage(user);
        } else if (cUser != null) {
            c = DBManager.getInstance().getChatMessage(cUser);
        }
        mAdapter.changeCursor(c);
    }

    @Override
    public void onClickChatImage(View view, int position) {
        Intent intent = new Intent(this, ChattingProfileActivity.class);
        startActivity(intent);
    }

    private static final int RC_GET_IMAGE = 1;
    private static final int RC_CATPURE_IMAGE = 2;
    private static final int INDEX_CAMERA = 0;
    private static final int INDEX_GALLERY = 1;

    private static final String FIELD_SAVE_FILE = "savedfile";
    private static final String FIELD_UPLOAD_FILE = "uploadfile";

    File savedFile = null;
    File uploadFile = null;

    private void getGalleryImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, RC_GET_IMAGE);
    }

    private void getCaptureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getSaveFile());
        startActivityForResult(intent, RC_CATPURE_IMAGE);
    }

    private Uri getSaveFile() {
        File dir = getExternalFilesDir("capture");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        savedFile = new File(dir, "my_image_" + System.currentTimeMillis() + ".jpeg");
        return Uri.fromFile(savedFile);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (savedFile != null) {
            outState.putString(FIELD_SAVE_FILE, savedFile.getAbsolutePath());
        }
        if (uploadFile != null) {
            outState.putString(FIELD_UPLOAD_FILE, uploadFile.getAbsolutePath());
        }
    }

    String path;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GET_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                Cursor c = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (c.moveToNext()) {
                    path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                    uploadFile = new File(path);
//                    Glide.with(this)
//                            .load(uploadFile)
//                            .into(profileImage);
                }
            }
        } else if (requestCode == RC_CATPURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                path = savedFile.getAbsolutePath();
                uploadFile = savedFile;
//                Glide.with(this)
//                        .load(uploadFile)
//                        .into(profileImage);
            }
        }
    }



}
