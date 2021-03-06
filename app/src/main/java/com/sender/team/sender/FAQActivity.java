package com.sender.team.sender;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FAQActivity extends AppCompatActivity {

    @BindView(R.id.expandableListView)
    ExpandableListView listView;
    FAQAdapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    int expandposition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);

        mAdapter = new FAQAdapter();
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
        String group = getResources().getString(R.string.faq_title1);
        String child = getResources().getString(R.string.faq_content1);
        mAdapter.put(group, child);
        group = getResources().getString(R.string.faq_title2);
        child = getResources().getString(R.string.faq_content2);
        mAdapter.put(group, child);
        group = getResources().getString(R.string.faq_title3);
        child = getResources().getString(R.string.faq_content3);
        mAdapter.put(group, child);
        group = getResources().getString(R.string.faq_title4);
        child = getResources().getString(R.string.faq_content4);
        mAdapter.put(group, child);
        group = getResources().getString(R.string.faq_title5);
        child = getResources().getString(R.string.faq_content5);
        mAdapter.put(group, child);
        group = getResources().getString(R.string.faq_title6);
        child = getResources().getString(R.string.faq_content6);
        mAdapter.put(group, child);
        group = getResources().getString(R.string.faq_title7);
        child = getResources().getString(R.string.faq_content7);
        mAdapter.put(group, child);
        group = getResources().getString(R.string.faq_title8);
        child = getResources().getString(R.string.faq_content8);
        mAdapter.put(group, child);
        group = getResources().getString(R.string.faq_title9);
        child = getResources().getString(R.string.faq_content9);
        mAdapter.put(group, child);
        group = getResources().getString(R.string.faq_title10);
        child = getResources().getString(R.string.faq_content10);
        mAdapter.put(group, child);
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

