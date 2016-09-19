package com.sender.team.sender;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sender.team.sender.data.ChatContract;
import com.sender.team.sender.data.ContractIdData;
import com.sender.team.sender.data.ContractsData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.ReverseGeocodingData;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.DBManager;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.ContractsRequest;
import com.sender.team.sender.request.OtherUserRequest;
import com.sender.team.sender.request.ReverseGeocodingRequest;
import com.sender.team.sender.request.SenderInfoRequest;

import java.util.Date;

public class AcceptActivity extends Activity {

    public static final String STATE_CONTRACT_BEFORE = "1";
    public static final String STATE_CONTRACT_SUCCESS = "2";
    public static final String STATE_CONTRACT_FAIL = "9";
    String start;
    String end;

    ContractsData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.accept_layout, null);
        final ImageView imageProduct = (ImageView) dialogView.findViewById(R.id.image_product);
        final TextView textWord = (TextView) dialogView.findViewById(R.id.text_word);
        final TextView textDetail = (TextView) dialogView.findViewById(R.id.text_details);
        final TextView textMemo = (TextView) dialogView.findViewById(R.id.text_accept_memo);


        SenderInfoRequest request = new SenderInfoRequest(this, PropertyManager.getInstance().getMyDeliveringId());
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<ContractsData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<ContractsData>> request, NetworkResult<ContractsData> result) {
                data = result.getResult();


                ReverseGeocodingRequest geo_request = new ReverseGeocodingRequest(AcceptActivity.this, data.getHere_lat(), data.getHere_lon());
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_TMAP, geo_request, new NetworkManager.OnResultListener<ReverseGeocodingData>() {
                    @Override
                    public void onSuccess(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result) {
                        start = result.getAddressInfo().getLegalDong();
                        ReverseGeocodingRequest geo_request2 = new ReverseGeocodingRequest(AcceptActivity.this, data.getAddr_lat(), data.getAddr_lon());
                        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_TMAP, geo_request2, new NetworkManager.OnResultListener<ReverseGeocodingData>() {
                            @Override
                            public void onSuccess(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result) {
                                end = result.getAddressInfo().getLegalDong();
                                Glide.with(AcceptActivity.this)
                                        .load(data.getPic()[0].getFileUrl())
                                        .into(imageProduct);


                                textWord.setText(start + " -> " + end + "\n" +
                                        Utils.getCurrentTime(data.getArr_time()) + " 도착 / " +
                                        data.getInfo() + " / " + data.getPrice() + "원");
                                textDetail.setText(data.getName() + "님으로부터 배송요청이 왔습니다\n수락하시겠습니까?");
                                textMemo.setText(data.getMemo());


                                AlertDialog.Builder builder = new AlertDialog.Builder(AcceptActivity.this, R.style.AppTheme_Dialog_Transparent);
                                builder.setIcon(R.mipmap.ic_launcher);
                                builder.setView(dialogView);

                                Button btn = (Button) dialogView.findViewById(R.id.btn_accept_ok);
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //13.계약 체결하기
                                        ContractsRequest request = new ContractsRequest(AcceptActivity.this, data.getContract_id(), "" + data.getId(), null, STATE_CONTRACT_SUCCESS);
                                        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<ContractIdData>>() {
                                            @Override
                                            public void onSuccess(NetworkRequest<NetworkResult<ContractIdData>> request, NetworkResult<ContractIdData> result) {
                                                OtherUserRequest req = new OtherUserRequest(AcceptActivity.this, data.getId());
                                                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, req, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
                                                    @Override
                                                    public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                                                        UserData user = result.getResult();
                                                        user.setAddress(start + " -> " + end);
                                                        user.setContractId(data.getContract_id());
                                                        DBManager.getInstance().addMessage(user, null, ChatContract.ChatMessage.TYPE_RECEIVE, null, new Date());
                                                        Intent intent = new Intent(AcceptActivity.this, SplashActivity.class);
                                                        intent.putExtra(ChattingActivity.EXTRA_USER, user);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        Toast.makeText(AcceptActivity.this, "계약성공: " + result, Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onFail(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result, String errorMessage, Throwable e) {

                                                    }
                                                });

                                            }

                                            @Override
                                            public void onFail(NetworkRequest<NetworkResult<ContractIdData>> request, NetworkResult<ContractIdData> result, String errorMessage, Throwable e) {
                                                Toast.makeText(AcceptActivity.this, "계약 실패", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                                    }
                                });

                                btn = (Button) dialogView.findViewById(R.id.btn_accept_cancel);
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ContractsRequest request = new ContractsRequest(AcceptActivity.this, data.getContract_id(), data.getId(), null, STATE_CONTRACT_FAIL);
                                        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<ContractIdData>>() {
                                            @Override
                                            public void onSuccess(NetworkRequest<NetworkResult<ContractIdData>> request, NetworkResult<ContractIdData> result) {
                                                Toast.makeText(AcceptActivity.this, "계약이 거절되었습니다", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFail(NetworkRequest<NetworkResult<ContractIdData>> request, NetworkResult<ContractIdData> result, String errorMessage, Throwable e) {
                                                Toast.makeText(AcceptActivity.this, "계약 거절 실패", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        finish();
                                    }
                                });
//                                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                                    @Override
//                                    public void onCancel(DialogInterface dialog) {
//                                        finish();
//                                    }
//                                });

                                AlertDialog dialog = builder.create();
//                                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                dialog.show();

                                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                                params.width = 825;
                                dialog.getWindow().setAttributes(params);
                            }

                            @Override
                            public void onFail(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result, String errorMessage, Throwable e) {

                            }
                        });
                    }

                    @Override
                    public void onFail(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result, String errorMessage, Throwable e) {

                    }
                });
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<ContractsData>> request, NetworkResult<ContractsData> result, String errorMessage, Throwable e) {

            }
        });

    }

}
