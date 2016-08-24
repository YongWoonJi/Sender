package com.sender.team.sender;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class DelivererHeaderFragment extends Fragment {


    public DelivererHeaderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_deliverer_header, container, false);
        Button btn = (Button)view.findViewById(R.id.btn_end);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"배송완료 처리 되었습니다.",Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
        return view;
    }

}
