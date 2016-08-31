package com.sender.team.sender;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;

public class QuestionActivity extends AppCompatActivity {

    @BindView(R.id.edit_object_name)
    EditText editTitle;

    @BindView(R.id.edit_receiver_phone)
    EditText editContents;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @OnClick(R.id.btn_question)
    public void onClickQuestion() {
        clickSend();
    }

    AlertDialog dialog;
    private void clickSend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionActivity.this);
        builder.setMessage("위 내용을 등록하시겠습니까?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                request();
                Toast.makeText(QuestionActivity.this, "문의내용이 등록되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(QuestionActivity.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
        dialog = builder.create();
        dialog.show();
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

    private void request() {
        String title = editTitle.getText().toString();
        String contents = editContents.getText().toString();
        int boardType;
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_email :
                boardType = 0;
                break;
            case R.id.radio_sms :
                boardType = 1;
                break;
        }
        // 낼 오전에 이어서...
        
    }
}
