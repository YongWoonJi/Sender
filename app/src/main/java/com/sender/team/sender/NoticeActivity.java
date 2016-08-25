package com.sender.team.sender;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ExpandableListView;

public class NoticeActivity extends AppCompatActivity {

    ExpandableListView listView;
    NoticeAdapter mAdapter;

    int width;
    int expandposition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
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
        for (int i = 0; i < 10; i++) {
            mAdapter.put("공지 " + i, "내용 " + i + " 의 내용");
        }
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


