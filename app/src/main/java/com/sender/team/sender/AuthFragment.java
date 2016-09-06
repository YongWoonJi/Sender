package com.sender.team.sender;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.AddPhoneRequest;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class AuthFragment extends Fragment {

    @BindView(R.id.edit_phone)
    EditText editPhone;

    @BindView(R.id.edit_authNum)
    EditText editAuth;

    @BindView(R.id.text_time)
    TextView textTime;


    public AuthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auth, container, false);
        ButterKnife.bind(this, view);

        checkPermission();

        Button btn = (Button) view.findViewById(R.id.btn_getNum);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                int num = random.nextInt(9000) + 1000;
                editAuth.setText("" + num);

                mHandler.post(runnable);
            }
        });


        btn = (Button) view.findViewById(R.id.btn_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("인증되었습니다");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        btn = (Button) view.findViewById(R.id.btn_finish);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPhoneRequest request = new AddPhoneRequest(getContext(), "010-1234-5678");
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_SECURE, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                        if (!TextUtils.isEmpty(result.getResult())) {
                            Log.i("SignUpActivity", result.getResult());
                        } else {
                            Log.i("SignUpActivity", result.getError());
                        }

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<String>> request, String errorMessage, Throwable e) {
                        Log.i("SignUpActivity", "리퀘스트 실패");
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                });

            }
        });

        return view;
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Permission");
                builder.setMessage("전화번호를 얻어옵니다");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermission();
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                });
                builder.create().show();
                return;
            }
            requestPermission();
        } else {
            Context context = getContext();
            TelephonyManager manager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            String phoneNum = manager.getLine1Number();
            editPhone.setText(phoneNum);
        }
    }


    private static final int RC_PERMISSION = 100;
    private void requestPermission() {
        requestPermissions(new String[] {Manifest.permission.READ_PHONE_STATE}, RC_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSION) {
            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Context context = getContext();
                TelephonyManager manager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
                String phoneNum = manager.getLine1Number();
                editPhone.setText(phoneNum);
            }
        }
    }

    int minute = 3;
    long startTime = -1;
    Handler mHandler = new Handler(Looper.getMainLooper());

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long time = SystemClock.elapsedRealtime();
            if (startTime == -1) {
                startTime = time;
            }
            int gap = (int) (time - startTime);
            int second = 60 - gap / 1000;
            int rest = 1000 - (gap % 1000);
            if (second >= 0) {
                if (second == 60) {
                    textTime.setText(minute + " : 00");
                    minute--;
                } else {
                    if (second < 10) {
                        textTime.setText(minute + " : 0" + second);
                        if (second == 0) {
                            startTime = -1;
                            minute--;
                        }
                    } else {
                        textTime.setText(minute + " : " + second);
                    }
                }
                mHandler.postDelayed(this, rest);
            } else {
//                if (minute == 0) {
//                    return;
//                }
//                startTime = -1;
//                minute--;
//                second = 59;
//                textTime.setText(minute + " : " + second);
//                mHandler.postDelayed(this, rest);
            }
        }
    };

}
