package com.sender.team.sender;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sender.team.sender.data.ContractIdData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.SenderRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoInputFragment extends Fragment {

    private static final int RC_PERMISSION_GET_IMAGE = 101;
    private static final int RC_PERMISSION_GET_CAPTURE_IMAGE = 102;


    public InfoInputFragment() {
        // Required empty public constructor
    }

    OnMessageCallback callback;

    public interface OnMessageCallback {
        void onClickButton();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMessageCallback) {
            callback = (OnMessageCallback) context;
        }
    }

    @BindView(R.id.edit_object_name)
    EditText objectName;

    @BindView(R.id.edit_object_price)
    EditText objectPrice;

    @BindView(R.id.edit_receiver_phone)
    EditText receiverPhone;

    @BindView(R.id.edit_infoinput_hour)
    EditText requestHour;

    @BindView(R.id.edit_infoinput_min)
    EditText requestMin;

    @BindView(R.id.object_image)
    ImageView objectImage;

    @BindView(R.id.edit_memo)
    EditText requestMemo;

    private static final int RC_GET_IMAGE = 1;
    private static final int RC_CATPURE_IMAGE = 2;
    private static final int INDEX_CAMERA = 0;
    private static final int INDEX_GALLERY = 1;

    private static final String FIELD_SAVE_FILE = "savedfile";
    private static final String FIELD_UPLOAD_FILE = "uploadfile";

    File savedFile = null;
    File uploadFile = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            String path = savedInstanceState.getString(FIELD_SAVE_FILE);
            if (!TextUtils.isEmpty(path)) {
                savedFile = new File(path);
            }
            path = savedInstanceState.getString(FIELD_UPLOAD_FILE);
            if (!TextUtils.isEmpty(path)) {
                uploadFile = new File(path);
                Glide.with(this)
                        .load(uploadFile)
                        .into(objectImage);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info_input, container, false);
        ButterKnife.bind(this, view);

        ViewGroup.LayoutParams layoutParams = ((SendActivity) getActivity()).mapFragment.getView().getLayoutParams();
        float dp = 220;
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        layoutParams.height = pixel;
        ((SendActivity) getActivity()).mapFragment.getView().setLayoutParams(layoutParams);

        requestHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (requestHour.isFocusable()) {
                    requestHour.setGravity(Gravity.RIGHT);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())) {
                    requestHour.setGravity(Gravity.LEFT);
                }
            }
        });

        Button btn = (Button) view.findViewById(R.id.btn_deliverer);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time = Utils.getCurrentDate() + " " + requestHour.getText().toString() + ":" + requestMin.getText().toString() + ":00";
                String obName = objectName.getText().toString();
                String obPrice = objectPrice.getText().toString();
                String phone = receiverPhone.getText().toString();
                String memo = requestMemo.getText().toString();

                if (!TextUtils.isEmpty(obName) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(obPrice) && !TextUtils.isEmpty(time)) {

                    if (!((SendActivity) getActivity()).isRequestCheck) {
                        ((SendActivity) getActivity()).receiveData(obName, phone, obPrice, time, uploadFile, memo);
                        ((SendActivity) getActivity()).isRequestCheck = true;
                    }// 백스택 했을 때 리퀘스트가 다시 안되도록 SendActivity에 boolean 변수를 두고 사용

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new DelivererListFragment())
                            .addToBackStack(null)
                            .commit();

                    ((SendActivity) getActivity()).headerlayout.setVisibility(View.GONE);
                    ((SendActivity) getActivity()).headerView.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams layoutParams = ((SendActivity) getActivity()).mapFragment.getView().getLayoutParams();
                    float dp = 268;
                    int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
                    layoutParams.height = pixel;
                    ((SendActivity) getActivity()).mapFragment.getView().setLayoutParams(layoutParams);

                } else {
                    Toast.makeText(getActivity(), "이름, 번호, 가격을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                if (callback != null) {
                    callback.onClickButton();
                }
            }
        });

        ((SendActivity) getActivity()).backMarker();
        return view;
    }

    @OnClick(R.id.object_image)
    public void onUploadImage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.object_image);
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

    private void getGalleryImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_GET_IMAGE);
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
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_GET_CAPTURE_IMAGE);
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
        File dir = getActivity().getExternalFilesDir("capture");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        savedFile = new File(dir, "my_image_" + System.currentTimeMillis() + ".jpeg");
        return Uri.fromFile(savedFile);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (savedFile != null) {
            outState.putString(FIELD_SAVE_FILE, savedFile.getAbsolutePath());
        }
        if (uploadFile != null) {
            outState.putString(FIELD_UPLOAD_FILE, uploadFile.getAbsolutePath());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GET_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                Cursor c = getActivity().getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (c.moveToNext()) {
                    String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                    float dp = 400;
                    int viewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    float width = bitmap.getWidth();
                    float height = bitmap.getHeight();

                    if (height > viewHeight) {
                        float percente = height / 100;
                        float scale = viewHeight / percente;
                        width *= (scale / 100);
                        height *= (scale / 100);
                    }
                    Bitmap resizing = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
                    File out = new File(getContext().getExternalCacheDir(), System.currentTimeMillis() + " temp.jpg");
                    try {
                        FileOutputStream fos = new FileOutputStream(out);
                        resizing.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    uploadFile = out;
                    Glide.with(this)
                            .load(uploadFile)
                            .into(objectImage);
                }
            }
        } else if (requestCode == RC_CATPURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                float dp = 400;
                int viewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(savedFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                float width = bitmap.getWidth();
                float height = bitmap.getHeight();

                if (height > viewHeight) {
                    float percente = height / 100;
                    float scale = viewHeight / percente;
                    width *= (scale / 100);
                    height *= (scale / 100);
                }
                Bitmap resizing = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
                File out = new File(getContext().getExternalCacheDir(), System.currentTimeMillis() + "_temp.jpg");
                try {
                    FileOutputStream fos = new FileOutputStream(out);
                    resizing.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadFile = out;
                Glide.with(this)
                        .load(uploadFile)
                        .into(objectImage);
            }
        }
    }


    public void setSenderData(final Context context, double hereLat, double hereLng, double addrLat, double addrLng,
                              String obName, String phone, String obPrice, String time, File uploadFile, String memo) {

        final String hLat = String.valueOf(hereLat);
        final String hLng = String.valueOf(hereLng);
        final String aLat = String.valueOf(addrLat);
        final String aLng = String.valueOf(addrLng);
        //SendAcitivty의 현위치와 선택한 위치의 위도 경도값을 받아온다.
        Log.i("InfoInputFragment",hLat+" , "+hLng+" , "+aLat+" , "+aLng);

        SenderRequest request = new SenderRequest(context, hLat, hLng, aLat, aLng, time, phone, obPrice, obName, uploadFile, memo);
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<ContractIdData>>() {

            @Override
            public void onSuccess(NetworkRequest<NetworkResult<ContractIdData>> request, NetworkResult<ContractIdData> result) {
                PropertyManager.getInstance().setLastContractId("" + result.getResult().getContract_id());
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<ContractIdData>> request, NetworkResult<ContractIdData> result, String errorMessage, Throwable e) {

            }
        });
    }

}
