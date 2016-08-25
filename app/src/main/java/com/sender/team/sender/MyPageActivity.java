package com.sender.team.sender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.sender.team.sender.Data.ReviewData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPageActivity extends AppCompatActivity {

    @BindView(R.id.rv_list)
    RecyclerView listView;
    ReviewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        mAdapter = new ReviewAdapter();
        listView.setAdapter(mAdapter);
        listView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        initData();
    }

    private void initData() {
        List<ReviewData> dataList = new ArrayList<>();
        ReviewData data;
        for (int i =0; i < 7; i++){
            data = new ReviewData();
            data.name = "정현맨"+i;
            data.rating = i;
            data.message = "우왕굳";
            dataList.add(data);
        }
        mAdapter.setReviewData(dataList);
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
