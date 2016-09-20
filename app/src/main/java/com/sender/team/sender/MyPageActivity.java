package com.sender.team.sender;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.ReviewListData;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.MyPageRequest;
import com.sender.team.sender.request.ProfilePictureUploadRequest;
import com.sender.team.sender.request.ReviewListRequest;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sender.team.sender.MyApplication.getContext;

public class MyPageActivity extends AppCompatActivity {

    @BindView(R.id.rv_list)
    RecyclerView listView;

    @BindView(R.id.text_delivery_count)
    TextView deliveryCount;

    @BindView(R.id.text_my_email)
    TextView email;

    @BindView(R.id.text_request_count)
    TextView requestCount;

    @BindView(R.id.text_my_phone)
    TextView phone;

    @BindView(R.id.text_my_name)
    TextView name;

    @BindView(R.id.my_image)
    ImageView profileImage;

    @BindView(R.id.text_rating_count)
    TextView rating;

    @BindView(R.id.text_review_count)
    TextView reviewCount;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.text_empty_my_review)
    TextView emptyText;

    private static final int RC_PERMISSION_GET_IMAGE = 201;
    private static final int RC_PERMISSION_GET_CAPTURE_IMAGE = 202;

    private static final int RC_GET_IMAGE = 1;
    private static final int RC_CATPURE_IMAGE = 2;
    private static final int INDEX_CAMERA = 0;
    private static final int INDEX_GALLERY = 1;

    private static final String FIELD_SAVE_FILE = "savedfile";
    private static final String FIELD_UPLOAD_FILE = "uploadfile";

    ReviewAdapter mAdapter;

    File savedFile = null;
    File uploadFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);

        mAdapter = new ReviewAdapter();
        listView.setAdapter(mAdapter);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        initData(savedInstanceState);
        getReviewListData();

    }

    private void initData(Bundle savedInstanceState) {
        UserData user = PropertyManager.getInstance().getUserData();
        if (savedInstanceState == null) {
            Glide.with(MyPageActivity.this).load(user.getFileUrl()).into(profileImage);
        } else {
            String path = savedInstanceState.getString(FIELD_SAVE_FILE);
            if (!TextUtils.isEmpty(path)) {
                savedFile = new File(path);
            }
            path = savedInstanceState.getString(FIELD_UPLOAD_FILE);
            if (!TextUtils.isEmpty(path)) {
                uploadFile = new File(path);
                Glide.with(this)
                        .load(uploadFile)
                        .into(profileImage);
            }
        }

        if (!TextUtils.isEmpty(user.getName())) {
            name.setText(user.getName());
        }
        if (!TextUtils.isEmpty(user.getEmail())) {
            email.setText(user.getEmail());
        }
        if (!TextUtils.isEmpty(user.getPhone())) {
            phone.setText(user.getPhone());
        }
        if (!TextUtils.isEmpty("" + user.getStar())) {
            rating.setText("" + user.getStar());
        }
        if (!TextUtils.isEmpty("" + user.getDeliver_req())) {
            requestCount.setText("" + user.getDeliver_req());
        }
        if (!TextUtils.isEmpty("" + user.getDeliver_com())) {
            deliveryCount.setText("" + user.getDeliver_com());
        }
    }

    @OnClick(R.id.my_image)
    public void onChangeImage(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.profile_image);
        builder.setItems(R.array.select_image, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case INDEX_CAMERA :
                        getCaptureImage();
                        break;
                    case INDEX_GALLERY :
                        getGalleryImage();
                        break;
                }
            }
        });
        builder.create().show();
    }

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
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_GET_CAPTURE_IMAGE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GET_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                Cursor c = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (c.moveToNext()) {
                    String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                    uploadFile = new File(path);
                    Glide.with(this)
                            .load(uploadFile)
                            .into(profileImage);
                }
            }
        } else if (requestCode == RC_CATPURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                uploadFile = savedFile;
                Glide.with(this)
                        .load(uploadFile)
                        .into(profileImage);
            }
        }

        /// 이미지 뷰에 그릴때 업로드
        if (uploadFile != null) {
            ProfilePictureUploadRequest request = new ProfilePictureUploadRequest(this, uploadFile);
            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                @Override
                public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                    MyPageRequest req = new MyPageRequest(MyPageActivity.this);
                    NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, req, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
                        @Override
                        public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                            PropertyManager.getInstance().setUserData(result.getResult());
                        }

                        @Override
                        public void onFail(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result, String errorMessage, Throwable e) {

                        }
                    });
                    Toast.makeText(MyPageActivity.this, "이미지 등록에 성공했습니다", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                    Toast.makeText(MyPageActivity.this, "이미지 등록에 실패했습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void getReviewListData() {
        //리뷰 리스트 보기
        String userId = PropertyManager.getInstance().getUserData().getUser_id();
        ReviewListRequest reviewRequest = new ReviewListRequest(MyPageActivity.this, "1", "100", userId);

        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, reviewRequest, new NetworkManager.OnResultListener<NetworkResult<ReviewListData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<ReviewListData>> request, NetworkResult<ReviewListData> result) {
                if (result.getResult() != null) {
                    mAdapter.clear();
                    mAdapter.setReviewData(result.getResult().getData().getReview());
                    reviewCount.setText("" + result.getResult().getData().getReview().size());
                    emptyText.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                } else {
                    reviewCount.setText("0");
                    emptyText.setText(R.string.empty_reivew);
                    emptyText.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<ReviewListData>> request, NetworkResult<ReviewListData> result, String errorMessage, Throwable e) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
}
}
