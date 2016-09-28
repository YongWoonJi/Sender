package com.sender.team.sender;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.NoticeData;
import com.sender.team.sender.data.NoticeListData;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.NoticeRequest;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoticeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ExpandableListView listView;
    NoticeAdapter mAdapter;

    int expandposition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ExpandableListView) findViewById(R.id.expandableListView);
        mAdapter = new NoticeAdapter(this);
        listView.setAdapter(mAdapter);

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int old = expandposition;
                expandposition = groupPosition;
                if (old != -1) {
                    listView.collapseGroup(old);
                }
            }
        });

        listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                if (expandposition == groupPosition) {
                    expandposition = -1;
                }
            }
        });

        initData();
    }

    private void initData() {
        NoticeRequest request = new NoticeRequest(this, "1", "100");
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<NoticeListData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<NoticeListData>> request, NetworkResult<NoticeListData> result) {
                ArrayList<NoticeData> data = result.getResult().getData();
                for (NoticeData d : data) {
                    mAdapter.put(d.getTitle(), d.getContent(), d.getWrite_date(), d.getFileUrl());
                }
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<NoticeListData>> request, NetworkResult<NoticeListData> result, String errorMessage, Throwable e) {

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}


