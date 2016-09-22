package com.sender.team.sender;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.DBManager;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.ChattingSendRequest;
import com.sender.team.sender.request.ContractsUpdateRequest;
import com.sender.team.sender.request.OtherUserRequest;
import com.sender.team.sender.request.ReviewRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendHeaderFragment extends Fragment {

    private static final String EXTRA_USER_ID = "userid";
    private static final String EXTRA_CONTRACT_ID = "contractid";

    public static final int START_DELIVERY = 3;
    public static final int END_DELIVERY = 4;

    public SendHeaderFragment() {
        // Required empty public constructor
    }

    String userId, contractId;
    public static SendHeaderFragment newInstance(String userId, String contractId) {
        SendHeaderFragment fragment = new SendHeaderFragment();
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
        View view = inflater.inflate(R.layout.fragment_send_header, container, false);


        final ImageView imageStatusTwo = (ImageView) view.findViewById(R.id.image_status_two);
        final Button btnEnd = (Button) view.findViewById(R.id.btn_end);
            btnEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String state = DBManager.getInstance().getState(Long.parseLong(userId), contractId);
                    if (!TextUtils.isEmpty(state)) {
                        if (state.equals(ChattingActivity.STATE_DELIVERY_COMPLETE)) {
                            clickSend();
                        }
                    } else {
                        Toast.makeText(getContext(), "아직 배송원이 배송을 완료하지 않았습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        btnEnd.setEnabled(false);

        final ImageView imageStatusOne = (ImageView) view.findViewById(R.id.image_status_one);
        final Button btnStart = (Button) view.findViewById(R.id.btn_start);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 물건 전달 완료 GCM 보냄
                ChattingSendRequest request = new ChattingSendRequest(getContext(), contractId, userId, ChattingActivity.STATE_PRODUCT_DELIVER, null);
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
        final OtherUserRequest request = new OtherUserRequest(getContext(), PropertyManager.getInstance().getContractedReceiverId());
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

        ImageView imageView = (ImageView)view.findViewById(R.id.image_dialog_evalution_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Button btn = (Button) view.findViewById(R.id.btn_review_submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float star = ratingBar.getRating();
                String comment = editComment.getText().toString();
                if (star <= 0 && TextUtils.isEmpty(comment.trim())) {
                    Toast.makeText(getActivity(), "리뷰를 입력해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    popupDialog(star, comment);
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog_Transparent);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        float dp = 300;
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        params.width = pixel;
        dialog.getWindow().setAttributes(params);

    }

    private void popupDialog(final float star, final String comment) {
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
                ReviewRequest request = new ReviewRequest(getContext(), PropertyManager.getInstance().getLastContractId(), comment, "" + (int) star);
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                        //15. 배송 상태 변경하기
                        ContractsUpdateRequest req = new ContractsUpdateRequest(getContext(), PropertyManager.getInstance().getLastContractId(), "" + END_DELIVERY);
                        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, req, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                            @Override
                            public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                                Toast.makeText(getContext(), "배송이 완료되었습니다", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                                // 채팅창 비활성화 & DB 일주일뒤 삭제

                            }

                            @Override
                            public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {

                            }
                        });
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                        Toast.makeText(getContext(), "리뷰 등록 실패", Toast.LENGTH_SHORT).show();
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
