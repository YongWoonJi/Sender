package com.sender.team.sender;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.view.inputmethod.InputMethodManager;
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
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.ChattingSendRequest;

import java.io.File;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sender.team.sender.MyApplication.getContext;

public class ChattingActivity extends AppCompatActivity implements ChattingAdapter.ChattingImage{

    private static final int RC_PERMISSION_GET_IMAGE = 301;
    private static final int RC_PERMISSION_GET_CAPTURE_IMAGE = 302;

    public static final String HEADER_TYPE = "headertype";
    public static final String EXTRA_USER = "user";
    public static final String EXTRA_CHATTINGLIST_DATA = "main_user";


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;

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

    InputMethodManager mInputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);

        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        user = (UserData) getIntent().getSerializableExtra(EXTRA_USER);
        if (user == null) {
            cUser = (ChattingListData) getIntent().getSerializableExtra(EXTRA_CHATTINGLIST_DATA);
            PropertyManager.getInstance().setLastChatuserPhone(cUser.getPhone());
            PropertyManager.getInstance().setContractedReceiverId("" + cUser.getId());
            PropertyManager.getInstance().setLastContractId("" + cUser.getContractId());
            mAdapter = new ChattingAdapter(cUser);
            toolbarTitle.setText(cUser.getName());
            isUserDataEmpty = true;
        } else {
            PropertyManager.getInstance().setLastChatuserPhone(user.getPhone());
            PropertyManager.getInstance().setContractedReceiverId("" + user.getUser_id());
            PropertyManager.getInstance().setLastContractId(user.getContractId());
            mAdapter = new ChattingAdapter(user);
            toolbarTitle.setText(user.getName());
        }


        listview.setAdapter(mAdapter);
        listview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        mLBM = LocalBroadcastManager.getInstance(this);

        Intent intent = getIntent();
        int i = intent.getIntExtra(HEADER_TYPE, -1);
        if (i == -1) {
            if (isUserDataEmpty) {
                i = (int) DBManager.getInstance().getHeaderType(cUser.getId(), "" + cUser.getContractId());
            } else {
                i = (int) DBManager.getInstance().getHeaderType(Long.parseLong(user.getUser_id()), user.getContractId());
            }
        }

        switch (i){
            case ChattingListData.TYPE_SENDER:{
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SendHeaderFragment()).commit();
                break;
            }
            case ChattingListData.TYPE_DELIVERER: {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new DelivererHeaderFragment()).commit();
                break;
            }
                default:
                    break;
        }
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


    public void downKeyboard(EditText editText) {
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
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
            ChattingSendRequest request = new ChattingSendRequest(this, contractId, receiverId , message, null);
            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                @Override
                public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                    if (!TextUtils.isEmpty(result.getResult())){
                        if (isUserDataEmpty) {
                            DBManager.getInstance().addMessage(cUser, -1, null, ChatContract.ChatMessage.TYPE_SEND, message, new Date());
                        } else {
                            DBManager.getInstance().addMessage(user, -1, null, ChatContract.ChatMessage.TYPE_SEND, message, new Date());
                        }
                        updateMessage();
                    }
                }

                @Override
                public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                    Toast.makeText(ChattingActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            });

            editMessage.setText("");
            downKeyboard(editMessage);
        } else {
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
            if (isUserDataEmpty) {
                if (u.getUser_id().equals("" + cUser.getId())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateMessage();
                        }
                    });
                    intent.putExtra(MyGcmListenerService.EXTRA_RESULT, true);
                }
            } else {
                if (u.getUser_id().equals(user.getUser_id())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateMessage();
                        }
                    });
                    intent.putExtra(MyGcmListenerService.EXTRA_RESULT, true);
                }
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

        if (mAdapter.getItemCount() > 0) {
            listview.smoothScrollToPosition(mAdapter.getItemCount());
        }
    }

    @Override
    public void onClickChatImage(View view, int position) {
        Intent intent = new Intent(this, ChattingProfileActivity.class);
        if (isUserDataEmpty) {
            intent.putExtra(ChattingProfileActivity.EXTRA_CUSER, cUser);
        } else {
            intent.putExtra(ChattingProfileActivity.EXTRA_USER, user);
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                }
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_GET_IMAGE);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, RC_GET_IMAGE);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, RC_GET_IMAGE);
        }
    }

    private void getCaptureImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                }
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_GET_CAPTURE_IMAGE);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getSaveFile());
                startActivityForResult(intent, RC_CATPURE_IMAGE);
            }
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getSaveFile());
            startActivityForResult(intent, RC_CATPURE_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSION_GET_IMAGE) {
            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, RC_GET_IMAGE);
            }
        } else if (requestCode == RC_PERMISSION_GET_CAPTURE_IMAGE) {
            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getSaveFile());
                startActivityForResult(intent, RC_CATPURE_IMAGE);
            }
        }
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

                    String contractId;
                    String receiverId;
                    if (isUserDataEmpty) {
                        contractId = String.valueOf(cUser.getContractId());
                        receiverId = String.valueOf(cUser.getId());
                    } else {
                        contractId = user.getContractId();
                        receiverId = user.getUser_id();
                    }
                    ChattingSendRequest request = new ChattingSendRequest(this, contractId, receiverId, "", uploadFile);
                    NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                        @Override
                        public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                            if (!TextUtils.isEmpty(result.getResult())){
                                if (isUserDataEmpty) {
                                    DBManager.getInstance().addMessage(cUser, -1, path, ChatContract.ChatMessage.TYPE_SEND, null, new Date());
                                } else {
                                    DBManager.getInstance().addMessage(user, -1, path, ChatContract.ChatMessage.TYPE_SEND, null, new Date());
                                }
                                updateMessage();
                            }
                        }

                        @Override
                        public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                            Toast.makeText(ChattingActivity.this, "fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } else if (requestCode == RC_CATPURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                path = savedFile.getAbsolutePath();
                uploadFile = savedFile;

                String contractId;
                String receiverId;
                if (isUserDataEmpty) {
                    contractId = String.valueOf(cUser.getContractId());
                    receiverId = String.valueOf(cUser.getId());
                } else {
                    contractId = user.getContractId();
                    receiverId = user.getUser_id();
                }
                ChattingSendRequest request = new ChattingSendRequest(this, contractId, receiverId, "", uploadFile);
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                        if (!TextUtils.isEmpty(result.getResult())){
                            if (isUserDataEmpty) {
                                DBManager.getInstance().addMessage(cUser, -1, path, ChatContract.ChatMessage.TYPE_SEND, null, new Date());
                            } else {
                                DBManager.getInstance().addMessage(user, -1, path, ChatContract.ChatMessage.TYPE_SEND, null, new Date());
                            }
                            updateMessage();
                        }
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                        Toast.makeText(ChattingActivity.this, "fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }



}
