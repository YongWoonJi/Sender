package com.sender.team.sender;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.AddPhoneRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class AuthFragment extends Fragment {


    public AuthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auth, container, false);

        Button btn = (Button) view.findViewById(R.id.btn_finish);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPhoneRequest request = new AddPhoneRequest(getContext(), "010-1234-5678");
                NetworkManager.getInstance().getNetworkData(request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                        if (!TextUtils.isEmpty(result.getResult())) {
                            Log.i("LoginActivity", result.getResult());
                        } else {
                            Log.i("LoginActivity", result.getError());
                        }

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<String>> request, String errorMessage, Throwable e) {
                        Log.i("LoginActivity", "리퀘스트 실패");
                    }
                });

            }
        });

        return view;
    }
}
