package com.sender.team.sender;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoInputFragment extends Fragment {


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

//                if (!TextUtils.isEmpty(obName) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(obPrice) && !TextUtils.isEmpty(time)) {
//
//                    if (!((SendActivity) getActivity()).isRequestCheck) {
//                        ((SendActivity) getActivity()).receiveData(obName, phone, obPrice, time, uploadFile, memo);
//                        ((SendActivity) getActivity()).isRequestCheck = true;
//                    }// 백스택 했을 때 리퀘스트가 다시 안되도록 SendActivity에 boolean 변수를 두고 사용

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new DelivererListFragment())
                            .addToBackStack(null)
                            .commit();

                    ((SendActivity) getActivity()).searchView.setVisibility(View.GONE);
                    ((SendActivity) getActivity()).searchBtn.setVisibility(View.GONE);
                    ((SendActivity) getActivity()).headerView.setVisibility(View.VISIBLE);

//                } else {
//                    Toast.makeText(getActivity(), "이름, 번호, 가격을 입력해주세요.", Toast.LENGTH_SHORT).show();
//                }

                if (callback != null) {
                    callback.onClickButton();
                }
            }
        });

        return view;
    }

    @OnClick(R.id.object_image)
    public void onUploadImage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        File dir = getActivity().getExternalFilesDir("capture");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        savedFile = new File(dir, "my_image_" + System.currentTimeMillis() + ".jpeg");
        return Uri.fromFile(savedFile);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedFile != null) {
            savedInstanceState.putString(FIELD_SAVE_FILE, savedFile.getAbsolutePath());
        }
        try {
            if (uploadFile != null) {
                savedInstanceState.putString(FIELD_UPLOAD_FILE, uploadFile.getAbsolutePath());
            }
        }catch (Exception e){

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
                    uploadFile = new File(path);
                    Glide.with(this)
                            .load(uploadFile)
                            .into(objectImage);
                }
            }
        } else if (requestCode == RC_CATPURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                uploadFile = savedFile;
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
                PropertyManager.getInstance().setContractIdData(result.getResult());
                Toast.makeText(context, "success wow " + result.getResult().getContract_id()+"  " + result.getResult().getSendingId(), Toast.LENGTH_SHORT).show();
                Log.i("InfoInputFragment", "contractid = " + result.getResult().getContract_id());
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<ContractIdData>> request, NetworkResult<ContractIdData> result, String errorMessage, Throwable e) {
                Toast.makeText(context, "fail =" + errorMessage, Toast.LENGTH_SHORT).show();
                Log.i("InfoInputFragment", "fail = " + errorMessage);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((SendActivity) getActivity()).backMarker();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

}
