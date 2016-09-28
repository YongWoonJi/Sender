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
import com.sender.team.sender.manager.PropertyManager;

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
        PropertyManager.getInstance().setOnUserDataChangeListener(new PropertyManager.OnUserDataChangeListener() {
            @Override
            public void OnUserDataChange() {
                if (PropertyManager.getInstance().getUserData() != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
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
        data.add(new MenuGroup(MenuAdapter.HEADER, null, 0, false, 0));
        data.add(new MenuGroup(MenuAdapter.GROUP, "마이페이지", R.drawable.ic_mypage, false, 0));
        data.add(new MenuGroup(MenuAdapter.GROUP, "공지사항", R.drawable.ic_notice, false, 0));
        data.add(new MenuGroup(MenuAdapter.GROUP, "알림설정", R.drawable.ic_alarm, false, 1));
        data.add(new MenuGroup(MenuAdapter.GROUP, "약관동의", R.drawable.ic_check, true, 0));
        data.add(new MenuGroup(MenuAdapter.GROUP, "로그아웃", R.drawable.ic_lock, false, 0));
        data.add(new MenuGroup(MenuAdapter.FOOTER, null, 0, false , 0));

        ((MenuGroup) data.get(4)).children = new ArrayList<>();
        ((MenuGroup) data.get(4)).children.add(new MenuChild(MenuAdapter.SERVICE_TERMS, "서비스 이용약관"));
        ((MenuGroup) data.get(4)).children.add(new MenuChild(MenuAdapter.INFO_TERMS, "개인정보 취급방침"));
        ((MenuGroup) data.get(4)).children.add(new MenuChild(MenuAdapter.GPS_TERMS, "위치정보 이용약관"));

        mAdapter.setData(data);
        mAdapter.setOnFinishListener((MainActivity)getActivity());

        ((MainActivity)getActivity()).setNaviChildClickListener(new MainActivity.NaviChildClickListener() {
            @Override
            public void childReset() {
                MenuGroup data = (MenuGroup) mAdapter.getItemAtPosition(4);
                data.children = new ArrayList<>();
                data.children.add(new MenuChild(MenuAdapter.SERVICE_TERMS, "서비스 이용약관"));
                data.children.add(new MenuChild(MenuAdapter.INFO_TERMS, "개인정보 취급방침"));
                data.children.add(new MenuChild(MenuAdapter.GPS_TERMS, "위치정보 이용약관"));
                List<NaviItem> list = mAdapter.getData();
                for (int i = 0; i < 3; i++) {
                    list.remove(5);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }
}
