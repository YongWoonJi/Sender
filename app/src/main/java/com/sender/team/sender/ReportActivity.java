package com.sender.team.sender;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sender.team.sender.data.DeliveringHistoryData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.BoardRequest;
import com.sender.team.sender.request.DeliveringHistoryRequest;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportActivity extends AppCompatActivity {

    private static final String esType = "2";

    private static final String FIELD_SAVE_FILE = "savefile";
    private static final String FIELD_UPLOAD_FILE = "uploadfile";
    private static final int INDEX_CAMERA = 0;
    private static final int INDEX_GALLAERY = 1;
    private static final int RC_CAPTURE_IMAGE = 1;
    private static final int RC_GET_IMAGE = 2;


    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.edit_contents)
    EditText editContents;

    @BindView(R.id.image_photo)
    ImageView imagePhoto;

    SpinnerAdapter mAdapter;
    String selectDeliverer;

    File saveFile = null;
    File uploadFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAdapter = new SpinnerAdapter();
        spinner.setAdapter(mAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectDeliverer = (String) spinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        initData();

        if (savedInstanceState != null) {
            String path = savedInstanceState.getString(FIELD_SAVE_FILE);
            if (!TextUtils.isEmpty(path)) {
                saveFile = new File(path);
            }

            path = savedInstanceState.getString(FIELD_UPLOAD_FILE);
            if (!TextUtils.isEmpty(path)) {
                uploadFile = new File(path);
                Glide.with(this).load(uploadFile).into(imagePhoto);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (saveFile != null) {
            outState.putString(FIELD_SAVE_FILE, saveFile.getAbsolutePath());
        }
        if (uploadFile != null) {
            outState.putString(FIELD_UPLOAD_FILE, saveFile.getAbsolutePath());
        }
    }

    private void initData() {
        DeliveringHistoryRequest request = new DeliveringHistoryRequest(this);
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<DeliveringHistoryData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<DeliveringHistoryData>> request, NetworkResult<DeliveringHistoryData> result) {
                DeliveringHistoryData data = result.getResult();
                mAdapter.setItems(data.getName());
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<DeliveringHistoryData>> request, NetworkResult<DeliveringHistoryData> result, String errorMessage, Throwable e) {

            }
        });
    }


    @OnClick(R.id.btn_report)
    public void onClickButton() {
        clickSend();
    }

    @OnClick(R.id.image_photo)
    public void addImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이미지를 선택해주세요");
        builder.setItems(R.array.select_image, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                switch (position) {
                    case INDEX_CAMERA :
                        getCaptureImage();
                        break;
                    case INDEX_GALLAERY :
                        getGalleryImage();
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void getCaptureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getSaveFile());
        startActivityForResult(intent, RC_CAPTURE_IMAGE);
    }

    private void getGalleryImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, RC_GET_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_CAPTURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                uploadFile = saveFile;
                Glide.with(this).load(uploadFile).into(imagePhoto);
            }
        } else if (requestCode == RC_GET_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                Cursor c = getContentResolver().query(uri, new String[] {MediaStore.Images.Media.DATA}, null, null, null);
                if (c.moveToNext()) {
                    String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                    uploadFile = new File(path);
                    Glide.with(this).load(uploadFile).into(imagePhoto);
                }
            }
        }
    }

    private Uri getSaveFile() {
        File dir = getExternalFilesDir("capture");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        saveFile = new File(dir, "images_" + System.currentTimeMillis() + ".jpeg");
        return Uri.fromFile(saveFile);
    }

    AlertDialog dialog;
    private void clickSend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setMessage("위 내용을 등록하시겠습니까?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendRequest();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ReportActivity.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
        dialog = builder.create();
        dialog.show();
    }


    private void sendRequest() {
        String boardType;
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_praise :
                boardType = "0";
                break;
            case R.id.radio_report :
                boardType = "1";
                break;
            default :
                boardType = "0";
                break;
        }
        String content = editContents.getText().toString();

        BoardRequest request = new BoardRequest(this, PropertyManager.getInstance().getUserData().getName(), esType, boardType, "", content, uploadFile);
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                if (!TextUtils.isEmpty(result.getResult())) {
                    Toast.makeText(ReportActivity.this, "등록되었습니다", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ReportActivity.this, "게시글 등록에 실패하였습니다", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                Toast.makeText(ReportActivity.this, "request failed", Toast.LENGTH_SHORT).show();
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
