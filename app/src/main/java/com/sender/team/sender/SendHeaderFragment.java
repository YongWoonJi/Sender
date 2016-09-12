package com.sender.team.sender;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.ContractsUpdateRequest;
import com.sender.team.sender.request.OtherUserRequest;
import com.sender.team.sender.request.ReviewRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendHeaderFragment extends Fragment {

    public static boolean statusFlag = false;
    public static final int START_DELIVERY = 2;
    public static final int END_DELIVERY = 3;

    public SendHeaderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_header, container, false);

        final ImageView imageStatusTwo = (ImageView) view.findViewById(R.id.image_status_two);
        final Button btnEnd = (Button) view.findViewById(R.id.btn_end);
            btnEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //15. 배송 상태 변경하기
                    ContractsUpdateRequest request = new ContractsUpdateRequest(getContext(), "1", "" + END_DELIVERY);
                    NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                        @Override
                        public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                            imageStatusTwo.setImageResource(R.color.colorstatusblue);
                            btnEnd.setBackgroundResource(R.color.chatting_background);
                            statusFlag = true;
                        }

                        @Override
                        public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                        }
                    });
                    clickSend();
                }
            });
        btnEnd.setEnabled(false);

        final ImageView imageStatusOne = (ImageView) view.findViewById(R.id.image_status_one);
        final Button btnStart = (Button) view.findViewById(R.id.btn_start);

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
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                    }
                });
            }
        });

        return view;
    }

    AlertDialog dialog;

    private void clickSend() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_evalution, null);
        final TextView textName = (TextView) view.findViewById(R.id.text_name);
        final TextView textRating = (TextView) view.findViewById(R.id.text_rating);
        final ImageView imageProfile = (ImageView) view.findViewById(R.id.image_profile);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        final EditText editComment = (EditText) view.findViewById(R.id.edit_comment);
        final OtherUserRequest request = new OtherUserRequest(getContext(), "1");
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                Glide.with(getContext()).load(result.getResult().getFileUrl()).into(imageProfile);
                textName.setText(result.getResult().getName());
                textRating.setText("" + result.getResult().getStar());
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result, String errorMessage, Throwable e) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


        Button btn = (Button) view.findViewById(R.id.btn_review_submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float star = ratingBar.getRating();
                String comment = editComment.getText().toString();
                if (star <= 0 && TextUtils.isEmpty(comment)) {
                    Toast.makeText(getActivity(), "리뷰를 입력해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    // ReviewRequest 추가해야함
                    String userId = PropertyManager.getInstance().getUserData().getUser_id();
                    ReviewRequest request = new ReviewRequest(getContext(), userId, "1", comment, "" + (int) star);
                    NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                        @Override
                        public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                            Toast.makeText(getContext(), result.getResult().toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                            Toast.makeText(getContext(), "리뷰 등록 실패:" + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                Toast.makeText(getContext(), "배송이 완료되었습니다", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
//        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                float star = ratingBar.getRating();
//                String comment = editComment.getText().toString();
//                if (star <= 0 && TextUtils.isEmpty(comment)) {
//                    Toast.makeText(getActivity(), "리뷰를 입력해 주세요", Toast.LENGTH_SHORT).show();
//                } else {
//                    // ReviewRequest 추가해야함
//                    ReviewRequest request= new ReviewRequest(getContext(),"1", "1", comment, ""+(int)star);
//                    NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
//                        @Override
//                        public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
//                            Toast.makeText(getContext(), result.getResult().toString(), Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
//                            Toast.makeText(getContext(), "리뷰 등록 실패:"+errorMessage, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//                Toast.makeText(getContext(), "배송이 완료되었습니다", Toast.LENGTH_SHORT).show();
//                getActivity().finish();
//            }
//        });

        dialog = builder.create();
        dialog.show();


    }

}
