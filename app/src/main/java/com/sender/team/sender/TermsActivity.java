package com.sender.team.sender;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

public class TermsActivity extends AppCompatActivity {


    TextView termsView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        termsView = (TextView)findViewById(R.id.text_terms);
        String text = getResources().getString(R.string.service_terms_content);
        termsView.setText(Html.fromHtml(text));
    }
}
