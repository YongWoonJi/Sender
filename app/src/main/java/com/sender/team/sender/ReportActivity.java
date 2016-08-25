package com.sender.team.sender;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button btn = (Button)findViewById(R.id.btn_report);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSend();
            }
        });
    }

    AlertDialog dialog;
    private void clickSend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setMessage("위 내용을 등록하시겠습니까?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ReportActivity.this, "문의내용이 등록되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ReportActivity.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
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
}
