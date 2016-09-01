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
        data.add(new MenuGroup(MenuAdapter.HEADER, null, 0, false));
        data.add(new MenuGroup(MenuAdapter.GROUP, "마이페이지", 0, false));
        data.add(new MenuGroup(MenuAdapter.GROUP, "공지사항", 0, false));
        data.add(new MenuGroup(MenuAdapter.GROUP, "알림설정", 0, false));
        data.add(new MenuGroup(MenuAdapter.GROUP, "약관동의", 0, true));
        data.add(new MenuGroup(MenuAdapter.GROUP, "로그아웃", 0, false));
        data.add(new MenuGroup(MenuAdapter.GROUP, "회원탈퇴", 0, false));

        ((MenuGroup) data.get(4)).children = new ArrayList<>();
        ((MenuGroup) data.get(4)).children.add(new MenuChild(MenuAdapter.SERVICE_TERMS, "서비스 이용약관"));
        ((MenuGroup) data.get(4)).children.add(new MenuChild(MenuAdapter.INFO_TERMS, "개인정보 취급방침"));
        ((MenuGroup) data.get(4)).children.add(new MenuChild(MenuAdapter.GPS_TERMS, "위치정보 이용약관"));

        mAdapter.setData(data);
        mAdapter.setOnFinishListener((MainActivity)getActivity());
        return view;
    }
}
