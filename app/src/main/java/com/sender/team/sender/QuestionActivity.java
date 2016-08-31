package com.sender.team.sender;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuestionActivity extends AppCompatActivity {

    private static final String FIELD_SAVE_FILE = "savedfile";
    private static final String FIELD_UPLOAD_FILE = "uploadfile";

    @BindView(R.id.edit_title)
    EditText editTitle;

    @BindView(R.id.edit_contents)
    EditText editContents;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.image_picture)
    ImageView imagePicture;

    File savedFile = null;
    File uploadFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            String path = savedInstanceState.getString(FIELD_SAVE_FILE);
            if (!TextUtils.isEmpty(path)) {
                savedFile = new File(path);
            }
            path = savedInstanceState.getString(FIELD_UPLOAD_FILE);
            if (!TextUtils.isEmpty(path)) {
                uploadFile = new File(path);
                Glide.with(this)
                        .load(uploadFile)
                        .into(imagePicture);
            }
        }

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

    private Uri getSaveFile() {
        return null;
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
        String esType;
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_email :
                esType = "0";
                break;
            case R.id.radio_sms :
                esType = "1";
                break;
        }
        String boardType = "2";


        
    }
}
