package com.sender.team.sender;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sender.team.sender.data.ContractIdData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.SenderRequest;
import com.sender.team.sender.widget.ArrayWheelAdapter;
import com.sender.team.sender.widget.NumericWheelAdapter;
import com.sender.team.sender.widget.OnWheelChangedListener;
import com.sender.team.sender.widget.OnWheelScrollListener;
import com.sender.team.sender.widget.WheelView;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sender.team.sender.R.id.hour;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoInputFragment extends Fragment {

    private static final int RC_PERMISSION_GET_IMAGE = 101;
    private static final int RC_PERMISSION_GET_CAPTURE_IMAGE = 102;
    private static final String EXTRA_SEND_ID = "sendstring";

    public InfoInputFragment() {
        // Required empty public constructor
    }

    public static InfoInputFragment newInstance(String send) {
        InfoInputFragment fragment = new InfoInputFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_SEND_ID, send);
        fragment.setArguments(args);
        return fragment;
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
    TextView requestHour;

    @BindView(R.id.edit_infoinput_min)
    TextView requestMin;

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

    // Time changed flag
    private boolean timeChanged = false;
    private boolean timeScrolled = false;

    String rejectSend;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rejectSend = getArguments().getString(EXTRA_SEND_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info_input, container, false);
        ButterKnife.bind(this, view);

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
                    requestHour.setGravity(Gravity.RIGHT);
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
                if (!TextUtils.isEmpty(objectName.getText().toString()) && !TextUtils.isEmpty(objectPrice.getText().toString()) && !TextUtils.isEmpty(receiverPhone.getText().toString()) && !TextUtils.isEmpty(requestMemo.getText().toString()) &&!TextUtils.isEmpty(requestHour.getText()) && !TextUtils.isEmpty(requestHour.getText())) {
                    String hour;
                    int inputHour;
                    StringBuffer sb = new StringBuffer(requestHour.getText().toString());
                    if (sb.substring(0, 2).toString().equals("오후")) {
                        hour = sb.delete(0, 4).toString();
                        if (hour.equals("12")) {
                            inputHour = Integer.parseInt(hour.toString());
                        } else {
                            inputHour = Integer.parseInt(hour.toString()) + 12;
                        }
                    } else {
                        hour = sb.delete(0, 4).toString();
                        if (hour.equals("12")) {
                            inputHour = 0;
                        } else {
                            inputHour = Integer.parseInt(hour.toString());
                        }
                    }

                    String hh = String.format("%02d", inputHour);
                    String mm = String.format("%02d", Integer.parseInt(requestMin.getText().toString()));
                    String time = Utils.getCurrentDate() + " " + hh + ":" + mm + ":00";
                    String obName = objectName.getText().toString();
                    String obPrice = objectPrice.getText().toString();
                    String phone = receiverPhone.getText().toString();
                    String memo = requestMemo.getText().toString();

                    if (rejectSend != null) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new DelivererListFragment())
                                .addToBackStack(null)
                                .commit();
                        ((SendActivity) getActivity()).headerlayout.setVisibility(View.GONE);
                        ((SendActivity) getActivity()).headerView.setVisibility(View.VISIBLE);

                    } else {
                        if (!TextUtils.isEmpty(obName.trim()) && !TextUtils.isEmpty(phone.trim()) && !TextUtils.isEmpty(obPrice.trim()) && !TextUtils.isEmpty(time.trim())) {
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
                    }

                    if (callback != null) {
                        callback.onClickButton();
                    }
                } else {
                    Toast.makeText(getContext(), "배송정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((SendActivity) getActivity()).backMarker();
        return view;
    }

    private static final int RC_CONTACTS = 300;
    @OnClick(R.id.btn_contacts)
    public void onClickContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, RC_CONTACTS);
        } else {
            if (checkPermission()) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, RC_CONTACTS);
            }
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                // dialog...
                requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, RC_CONTACTS);
                return false;
            }
            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, RC_CONTACTS);
            return false;
        }
        return true;
    }




    @OnClick(R.id.linearLayout5)
    public void onClick(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_timepicker, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog_Transparent);
        builder.setView(view);
        final android.support.v7.app.AlertDialog dialog = builder.create();

        //TimePicker
        String[] list = new String[] {"오전","오후"};
        final WheelView ampm = (WheelView) view.findViewById(R.id.ampm);
        ampm.setAdapter(new ArrayWheelAdapter<>(list));

        final WheelView hours = (WheelView) view.findViewById(hour);
        hours.setAdapter(new NumericWheelAdapter(1, 12));
        hours.setCyclic(true);
//		hours.setLabel(" 시");

        final WheelView mins = (WheelView) view.findViewById(R.id.mins);
        mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
//		mins.setLabel(" 분");
        mins.setCyclic(true);

        // set current time
        Calendar c = Calendar.getInstance();
        int curHours = c.get(Calendar.HOUR);
        int curMinutes = c.get(Calendar.MINUTE);
        int curAmPm = c.get(Calendar.AM_PM);

        hours.setCurrentItem(curHours-1);
        mins.setCurrentItem(curMinutes);
        ampm.setCurrentItem(curAmPm);

        // add listeners
        addChangingListener(ampm, "");
        addChangingListener(mins, "");
        addChangingListener(hours, "");

        OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!timeScrolled) {
                    timeChanged = true;
//                    picker.setCurrentHour(hours.getCurrentItem());
//                    picker.setCurrentMinute(mins.getCurrentItem());
                    timeChanged = false;
                }
            }
        };

        ampm.addChangingListener(wheelListener);
        hours.addChangingListener(wheelListener);
        mins.addChangingListener(wheelListener);

        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
                timeScrolled = true;
            }
            public void onScrollingFinished(WheelView wheel) {
                timeScrolled = false;
                timeChanged = true;
//                picker.setCurrentHour(hours.getCurrentItem()+1);
//                picker.setCurrentMinute(mins.getCurrentItem());
                timeChanged = false;
            }
        };

        ampm.addScrollingListener(scrollListener);
        hours.addScrollingListener(scrollListener);
        mins.addScrollingListener(scrollListener);

//        picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//            public void onTimeChanged(TimePicker  view, int hourOfDay, int minute) {
//                if (!timeChanged) {
//                    hours.setCurrentItem(hourOfDay, true);
//                    mins.setCurrentItem(minute, true);
//                }
//            }
//        });
        ///////////////////////////////////////////////TimePicker


        dialog.show();


        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        float dp = 300;
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        params.width = pixel;
        dialog.getWindow().setAttributes(params);

        Button btn = (Button) view.findViewById(R.id.btn_timepicker_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn = (Button) view.findViewById(R.id.btn_timepicker_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = hours.getCurrentItem() + 1;
                if(ampm.getCurrentItem() == 0) {
                    requestHour.setText("오전  " + hour);
                    requestMin.setText("" + mins.getCurrentItem());

                } else{
                    requestHour.setText("오후  " + hour);
                    requestMin.setText("" + mins.getCurrentItem());
                }
                dialog.dismiss();
            }
        });
    }
    /**
     * Adds changing listener for wheel that updates the wheel label
     * @param wheel the wheel
     * @param label the wheel label
     */
    private void addChangingListener(final WheelView wheel, final String label) {
        wheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                wheel.setLabel(newValue != 1 ? label : label);
            }
        });
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
                Uri cameraUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".provider", getSaveFile());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                startActivityForResult(intent, RC_CATPURE_IMAGE);
            }
        } else {
            Uri cameraUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".provider", getSaveFile());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
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
        } else if (requestCode == RC_CONTACTS) {
            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, RC_CONTACTS);
            }
        }
    }

    private File getSaveFile() {
        File dir = getActivity().getExternalFilesDir("capture");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        savedFile = new File(dir, "my_image_" + System.currentTimeMillis() + ".jpeg");
        return savedFile;
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


    String number;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GET_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                Cursor c = getContext().getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
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
        } else if (requestCode == RC_CONTACTS) {
            if (resultCode == Activity.RESULT_OK) {
                Cursor cursor = getContext().getContentResolver().query(data.getData(),
                        new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
                cursor.moveToFirst();
                number = cursor.getString(1);
                receiverPhone.setText(number.replace("-", ""));
                cursor.close();
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
