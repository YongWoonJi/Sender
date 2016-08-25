package com.sender.team.sender;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.ExpandableListView;

public class FAQActivity extends AppCompatActivity {

    ExpandableListView listView;
    NoticeAdapter mAdapter;

    int expandposition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ExpandableListView) findViewById(R.id.expandableListView);
        mAdapter = new NoticeAdapter();
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

        for (int i = 0; i < mAdapter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }
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

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            listView.setIndicatorBounds(width - GetPixelFromDips(35), width - GetPixelFromDips(5));
        } else {
            listView.setIndicatorBoundsRelative(width - GetPixelFromDips(35), width - GetPixelFromDips(5));
        }
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

