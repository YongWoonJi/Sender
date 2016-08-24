package com.sender.team.sender;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class NaviFragment extends Fragment {


    public NaviFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navi, container, false);
        Button btn = (Button)view.findViewById(R.id.btn_navi_mypage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),MyPageActivity.class);
                startActivity(intent);
            }
        });
        btn = (Button)view.findViewById(R.id.btn_navi_notice);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),NoticeActivity.class);
                startActivity(intent);
            }
        });

        btn = (Button)view.findViewById(R.id.btn_navi_terms);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),TermsActivity.class);
                startActivity(intent);
            }
        });

        final Button btn2 = (Button)view.findViewById(R.id.btn_navi_logout);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn2.setVisibility(View.GONE);
            }
        });
        return view;
    }

}
