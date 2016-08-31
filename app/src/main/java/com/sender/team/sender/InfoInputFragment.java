package com.sender.team.sender;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
    EditText receivePhone;

    @BindView(R.id.object_image)
    ImageView objectImage;

    private static final int RC_GET_IMAGE = 1;
    private static final int RC_CATPURE_IMAGE = 2;
    private static final int INDEX_CAMERA = 0;
    private static final int INDEX_GALLERY = 1;

    private static final String FIELD_SAVE_FILE = "savedfile";
    private static final String FIELD_UPLOAD_FILE = "uploadfile";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info_input, container, false);
        ButterKnife.bind(this, view);
        Button btn = (Button) view.findViewById(R.id.btn_deliverer);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SendActivity)getActivity()).searchView.setVisibility(View.GONE);
                ((SendActivity)getActivity()).searchBtn.setVisibility(View.GONE);
                ((SendActivity)getActivity()).headerView.setVisibility(View.VISIBLE);

                String obName = objectName.getText().toString();
                String obPrice = objectPrice.getText().toString();
                String phone = receivePhone.getText().toString();
//                if (!TextUtils.isEmpty(obName)&&!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(obPrice)){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new DelivererListFragment())
                        .addToBackStack(null)
                        .commit();

//                }else{
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
    public void onUploadImage(View view){

    }

    double hereLat;
    double hereLng;
    double addrLat;
    double addrLng;

    public void setSenderData(double hereLat, double hereLng, double addrLat, double addrLng){
        this.hereLat  = hereLat;
        this.hereLng = hereLng;
        this.addrLat = addrLat;
        this.addrLng  = addrLng;


    }

}
