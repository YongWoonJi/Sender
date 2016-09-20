package com.sender.team.sender;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.manager.DBManager;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.ChattingSendRequest;
import com.sender.team.sender.request.ContractsUpdateRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class DelivererHeaderFragment extends Fragment {

    private static final String EXTRA_USER_ID = "userid";
    private static final String EXTRA_CONTRACT_ID = "contractid";
    public static final int START_DELIVERY = 2;
    public static final int END_DELIVERY = 3;

    public DelivererHeaderFragment() {
        // Required empty public constructor
    }

    String userId, contractId;
    public static DelivererHeaderFragment newInstance(String userId, String contractId) {
        DelivererHeaderFragment fragment = new DelivererHeaderFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_USER_ID, userId);
        args.putString(EXTRA_CONTRACT_ID, contractId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(EXTRA_USER_ID);
            contractId = getArguments().getString(EXTRA_CONTRACT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View headerView =inflater.inflate(R.layout.fragment_deliverer_header, container, false);

        final ImageView imageStatusOne = (ImageView) headerView.findViewById(R.id.image_status_one);
        final Button btnStart = (Button) headerView.findViewById(R.id.btn_delivery_start);
        final Button btnEnd = (Button) headerView.findViewById(R.id.btn_delivery_end);
        btnEnd.setEnabled(false);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String state = DBManager.getInstance().getState(Long.parseLong(userId), contractId);
                if (state.equals(ChattingActivity.STATE_PRODUCT_DELIVER)) {
                    //15. 배송 상태 변경하기
                    ContractsUpdateRequest request = new ContractsUpdateRequest(getContext(), PropertyManager.getInstance().getLastContractId(), "" + START_DELIVERY);
                    NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                        @Override
                        public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                            imageStatusOne.setImageResource(R.color.colorstatusblue);
                            btnStart.setBackgroundResource(R.color.chatting_background);
                            btnEnd.setBackgroundResource(R.color.fontcolor);
                            btnEnd.setEnabled(true);
                            btnStart.setEnabled(false);
                            Toast.makeText(getContext(), "배송 시작", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                            Toast.makeText(getContext(), "배송 시작 실패:"+errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "아직 물건 전달 완료가 되지 않았습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //15. 배송 상태 변경하기
//                final ImageView imageStatusTwo = (ImageView) headerView.findViewById(R.id.image_status_two);
//                ContractsUpdateRequest request = new ContractsUpdateRequest(getContext(), PropertyManager.getInstance().getLastContractId(), "" + END_DELIVERY);
//                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
//                    @Override
//                    public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
//                        imageStatusTwo.setImageResource(R.color.colorstatusblue);
//                        btnEnd.setBackgroundResource(R.color.chatting_background);
//                        Toast.makeText(getContext(),"배송완료 처리 되었습니다.",Toast.LENGTH_SHORT).show();
//                        getActivity().finish();
//                    }
//
//                    @Override
//                    public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
//                        Toast.makeText(getContext(), "배송 완료 실패", Toast.LENGTH_SHORT).show();
//                    }
//                });
                popupDialog();
            }
        });

        Button btn = (Button) headerView.findViewById(R.id.btn_call);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PropertyManager.getInstance().getLastChatuserPhone()));
                startActivity(intent);
            }
        });
        return headerView;
    }


    AlertDialog dialog;
    private void popupDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_basic, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog_Transparent);
        builder.setView(view);

        TextView textTitle = (TextView)view.findViewById(R.id.text_dialog);
        textTitle.setText("배송완료 하시겠습니까?");

        TextView textContents = (TextView)view. findViewById(R.id.text_dialog_two);
        textContents.setText("배송 완료하시면 채팅창이 비활성화 되고\n채팅창은 일주일뒤에 자동 삭제됩니다");

        Button btn = (Button) view.findViewById(R.id.btn_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChattingSendRequest request = new ChattingSendRequest(getContext(), contractId, userId, ChattingActivity.STATE_DELIVERY_COMPLETE, null);
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                        Toast.makeText(getContext(),"배송완료 처리 되었습니다.",Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {

                    }
                });
            }
        });

        btn = (Button) view.findViewById(R.id.btn_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        float dp = 300;
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        params.width = pixel;
        dialog.getWindow().setAttributes(params);

    }
}
