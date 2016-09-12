package com.sender.team.sender;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sender.team.sender.data.ContractIdData;
import com.sender.team.sender.data.ContractsData;
import com.sender.team.sender.data.ContractsInfoData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.ReverseGeocodingData;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.DBManager;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.ContractsInfoRequest;
import com.sender.team.sender.request.ContractsRequest;
import com.sender.team.sender.request.OtherUserRequest;
import com.sender.team.sender.request.ReverseGeocodingRequest;
import com.sender.team.sender.request.SenderInfoRequest;

import java.text.ParseException;
import java.util.Calendar;

public class AcceptActivity extends Activity {

    public static final String STATE_CONTRACT_BEFORE = "1";
    public static final String STATE_CONTRACT_SUCCESS = "2";
    public static final String STATE_CONTRACT_FAIL = "9";
    String start;
    String end;

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


        SenderInfoRequest request = new SenderInfoRequest(this, PropertyManager.getInstance().getDeliveringData().getDelivering_id());
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<ContractsData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<ContractsData>> request, NetworkResult<ContractsData> result) {
                final ContractsData data = result.getResult();

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
                                try {
                                    Calendar ca = Calendar.getInstance();
                                    ca.setTime(Utils.convertStringToTime(data.getArr_time()));

                                    textWord.setText(start + " -> " + end + "\n" +
                                            ca.get(Calendar.HOUR_OF_DAY) + ":" + ca.get(Calendar.MINUTE) + " 도착 / " +
                                            data.getInfo() + " / " + data.getPrice() + "원");
                                    textDetail.setText(data.getName() + "님으로부터 배송요청이 왔습니다\n수락하시겠습니까?");
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(AcceptActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                                builder.setTitle("배송요청");
                                builder.setIcon(R.mipmap.ic_launcher);
                                builder.setView(dialogView);

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //13.계약 체결하기
                                        ContractsRequest request = new ContractsRequest(AcceptActivity.this, data.getContract_id(), "" + PropertyManager.getInstance().getReceiver_id(),null, STATE_CONTRACT_SUCCESS);//1자리에 contract_id 들어가야함
                                        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<ContractIdData>>() {
                                            @Override
                                            public void onSuccess(NetworkRequest<NetworkResult<ContractIdData>> request, NetworkResult<ContractIdData> result) {

                                                String address = start+" > "+end;
                                                getsendingInfo(String.valueOf(result.getResult().getContract_id()),address);
                                                Toast.makeText(AcceptActivity.this, "계약성공: " + result, Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFail(NetworkRequest<NetworkResult<ContractIdData>> request, NetworkResult<ContractIdData> result, String errorMessage, Throwable e) {
                                                Toast.makeText(AcceptActivity.this, "계약 실패", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });


                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        Intent intent2 = new Intent(getApplicationContext(), ChattingActivity.class);
                                        intent2.putExtra("key", ChattingActivity.DELIVERER_HEADER);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                        Intent[] intents = {intent, intent2};

                                        startActivities(intents);
                                        finish();
                                    }
                                });

                                builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ContractsRequest request = new ContractsRequest(AcceptActivity.this, "" + PropertyManager.getInstance().getContractIdData().getContract_id(), "" + PropertyManager.getInstance().getReceiver_id(), null, STATE_CONTRACT_FAIL);
                                        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<ContractIdData>>() {
                                            @Override
                                            public void onSuccess(NetworkRequest<NetworkResult<ContractIdData>> request, NetworkResult<ContractIdData> result) {
                                                Toast.makeText(AcceptActivity.this, "계약 거절 완료", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFail(NetworkRequest<NetworkResult<ContractIdData>> request, NetworkResult<ContractIdData> result, String errorMessage, Throwable e) {
                                                Toast.makeText(AcceptActivity.this, "계약 거절 실패", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        finish();
                                    }
                                });

                                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        finish();
                                    }
                                });

                                AlertDialog dialog = builder.create();
//                                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                dialog.show();
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

    public void getsendingInfo(String contract_id, final String address){
        ContractsInfoRequest request = new ContractsInfoRequest(this,contract_id);
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<ContractsInfoData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<ContractsInfoData>> request, NetworkResult<ContractsInfoData> result) {
                String id = String.valueOf(result.getResult().getSending_user_id());
                OtherUserRequest otherUserRequest = new OtherUserRequest(AcceptActivity.this,id);
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, otherUserRequest, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                        UserData data = result.getResult();
                        data.setAddress(address);
                        DBManager.getInstance().addUser(data);
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result, String errorMessage, Throwable e) {

                    }
                });
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<ContractsInfoData>> request, NetworkResult<ContractsInfoData> result, String errorMessage, Throwable e) {

            }
        });
    }

}
