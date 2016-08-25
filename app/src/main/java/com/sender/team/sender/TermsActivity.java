package com.sender.team.sender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsActivity extends AppCompatActivity {

    @BindView(R.id.text_title)
    TextView termsTitle;
    @BindView(R.id.text_terms)
    TextView termsView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int type = intent.getIntExtra("type", -1);
        String title = null;
        String content = null;
        switch (type) {
            case MenuAdapter.SERVICE_TERMS :
                title = getResources().getString(R.string.service_terms);
                content = getResources().getString(R.string.service_terms_content);
                break;
            case MenuAdapter.INFO_TERMS :
                title = getResources().getString(R.string.service_terms_personal);
                content = getResources().getString(R.string.service_terms_personal_content);
                break;
            case MenuAdapter.GPS_TERMS:
                title = getResources().getString(R.string.service_terms_gps);
                content = getResources().getString(R.string.service_terms_gps_content);
                break;
        }

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
            termsTitle.setText(title);
            termsView.setText(Html.fromHtml(content));
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
