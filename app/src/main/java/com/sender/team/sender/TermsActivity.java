package com.sender.team.sender;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsActivity extends AppCompatActivity {

    @BindView(R.id.text_terms)
    TextView termsView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        String text = getResources().getString(R.string.service_terms_personal_content);
        termsView.setText(Html.fromHtml(text));
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
