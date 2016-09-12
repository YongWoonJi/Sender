package com.sender.team.sender;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.ContractsUpdateRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class DelivererHeaderFragment extends Fragment {

    public static final int START_DELIVERY = 2;
    public static final int END_DELIVERY = 3;

    public DelivererHeaderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_deliverer_header, container, false);

        final ImageView imageStatusOne = (ImageView) view.findViewById(R.id.image_status_one);
        final Button btnStart = (Button)view.findViewById(R.id.btn_delivery_start);
        final Button btnEnd = (Button)view.findViewById(R.id.btn_delivery_end);
        btnEnd.setEnabled(false);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //15. 배송 상태 변경하기
                ContractsUpdateRequest request = new ContractsUpdateRequest(getContext(), "1", "" + START_DELIVERY);
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
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //15. 배송 상태 변경하기
                final ImageView imageStatusTwo = (ImageView) view.findViewById(R.id.image_status_two);
                ContractsUpdateRequest request = new ContractsUpdateRequest(getContext(), "1", "" + END_DELIVERY);
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                        imageStatusTwo.setImageResource(R.color.colorstatusblue);
                        btnEnd.setBackgroundResource(R.color.chatting_background);
                        Toast.makeText(getContext(), "배송 완료", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                        Toast.makeText(getContext(), "배송 완료 실패:"+  errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getContext(),"배송완료 처리 되었습니다.",Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });

        Button btn = (Button)view.findViewById(R.id.btn_call);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:010-0000-0000"));
                startActivity(intent);
            }
        });
        return view;
    }
}
