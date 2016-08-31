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

    @BindView(R.id.edit_title)
    EditText objectName;

    @BindView(R.id.edit_object_price)
    EditText objectPrice;

    @BindView(R.id.edit_contents)
    EditText receivePhone;

    @BindView(R.id.object_image)
    ImageView objectImage;

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
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new DelivererListFragment())
                        .addToBackStack(null)
                        .commit();
                if (callback != null) {
                    callback.onClickButton();
                }
            }
        });
        return view;
    }

    public void setLocation(){

    }

}
