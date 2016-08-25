package com.sender.team.sender;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sender.team.sender.data.MenuChild;
import com.sender.team.sender.data.MenuGroup;
import com.sender.team.sender.data.NaviItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class NaviFragment extends Fragment {

    @BindView(R.id.rv_navi)
    RecyclerView rv_navi;

    MenuAdapter mAdapter;

    public NaviFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navi, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new MenuAdapter();
        rv_navi.setAdapter(mAdapter);
        rv_navi.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        List<NaviItem> data = new ArrayList<>();
        data.add(new MenuGroup(MenuAdapter.GROUP, "마이페이지", 0, false));
        data.add(new MenuGroup(MenuAdapter.GROUP, "공지사항", 0, false));
        data.add(new MenuGroup(MenuAdapter.GROUP, "알림설정", 0, false));
        data.add(new MenuGroup(MenuAdapter.GROUP, "약관동의", 0, true));
        data.add(new MenuGroup(MenuAdapter.GROUP, "로그아웃", 0, false));

        ((MenuGroup) data.get(3)).children = new ArrayList<>();
        ((MenuGroup) data.get(3)).children.add(new MenuChild(MenuAdapter.SERVICE_TERMS, "서비스 이용약관"));
        ((MenuGroup) data.get(3)).children.add(new MenuChild(MenuAdapter.INFO_TERMS, "개인정보 취급방침"));
        ((MenuGroup) data.get(3)).children.add(new MenuChild(MenuAdapter.GPS_TERMS, "위치정보 이용약관"));

        mAdapter.setData(data);
        return view;

//        Button btn = (Button)view.findViewById(R.id.btn_navi_mypage);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(),MyPageActivity.class);
//                startActivity(intent);
//            }
//        });
//        btn = (Button)view.findViewById(R.id.btn_navi_notice);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(),NoticeActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        btn = (Button)view.findViewById(R.id.btn_navi_terms);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(),TermsActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        final Button btn2 = (Button)view.findViewById(R.id.btn_navi_logout);
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btn2.setVisibility(View.GONE);
//            }
//        });
    }

}
