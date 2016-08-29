package com.sender.team.sender;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.ReviewDataTemp;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.MyPageRequest;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPageActivity extends AppCompatActivity {

    @BindView(R.id.rv_list)
    RecyclerView listView;
    ReviewAdapter mAdapter;

    @BindView(R.id.text_delivery_count)
    TextView deliveryCount;
    @BindView(R.id.text_my_email)
    TextView email;
    @BindView(R.id.text_request_count)
    TextView requestCount;
    @BindView(R.id.text_my_phone)
    TextView phone;
    @BindView(R.id.text_my_name)
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        mAdapter = new ReviewAdapter();
        listView.setAdapter(mAdapter);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        initData();
    }

    private void initData() {
        MyPageRequest request = new MyPageRequest(this);
        NetworkManager.getInstance().getNetworkData(request, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                name.setText(result.getResult().getName());
                email.setText(result.getResult().getEmail());
                phone.setText(result.getResult().getPhone());
                requestCount.setText("" + result.getResult().getDeliver_req());
                deliveryCount.setText("" + result.getResult().getDeliver_com());

            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<UserData>> request, String errorMessage, Throwable e) {
                Toast.makeText(MyPageActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


        List<ReviewDataTemp> dataList = new ArrayList<>();
        ReviewDataTemp data;
        for (int i = 0; i < 7; i++) {
            data = new ReviewDataTemp();
            data.name = "정현맨" + i;
            data.rating = i;
            data.message = "우왕굳";
            dataList.add(data);
        }
        mAdapter.setReviewData(dataList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
