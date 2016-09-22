package com.sender.team.sender;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.BoardRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuestionActivity extends AppCompatActivity {

    private static final String FIELD_SAVE_FILE = "savedfile";
    private static final String FIELD_UPLOAD_FILE = "uploadfile";

    private static final int INDEX_CAMERA = 0;
    private static final int INDEX_GALLAERY = 1;
    private static final int RC_CAPTURE_IMAGE = 1;
    private static final int RC_GET_IMAGE = 2;



    @BindView(R.id.edit_title)
    EditText editTitle;

    @BindView(R.id.edit_contents)
    EditText editContents;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.image_picture)
    ImageView imagePicture;

    @BindView(R.id.text_title_count)
    TextView titleCount;

    @BindView(R.id.text_contents_count)
    TextView contentsCount;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    File savedFile = null;
    File uploadFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);


        editTitle.addTextChangedListener(new TextWatcher() {
            String strCur;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
                strCur = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( s.length() > 100 ) {
                    editTitle.setText(strCur);
                    editTitle.setSelection(start);
                } else{
                    titleCount.setText("(" + s.length() + "/100)");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editContents.addTextChangedListener(new TextWatcher() {
            String strCur;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
                strCur = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if( s.length() > 700 ) {
                    editContents.setText(strCur);
                    editContents.setSelection(start);
                } else{
                    contentsCount.setText("(" + s.length() + "/700)");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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


    @OnClick(R.id.image_picture)
    public void onClickImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.image_register);
        builder.setItems(R.array.select_image, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case INDEX_CAMERA :
                        getCaptureImage();
                        break;
                    case INDEX_GALLAERY :
                        getGalleryImage();
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void getCaptureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getSaveFile());
        startActivityForResult(intent, RC_CAPTURE_IMAGE);
    }


    private void getGalleryImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, RC_GET_IMAGE);
    }


    private Uri getSaveFile() {
        File dir = getExternalFilesDir("capture");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        savedFile = new File(dir, "images_" + System.currentTimeMillis() + ".jpeg");
        return Uri.fromFile(savedFile);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (savedFile != null) {
            outState.putString(FIELD_SAVE_FILE, savedFile.getAbsolutePath());
        }
        if (uploadFile != null) {
            outState.putString(FIELD_UPLOAD_FILE, uploadFile.getAbsolutePath());
        }
    }


    public int exifOrientationToDegrees(int exifOrientation) {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        } return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if(degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch(OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GET_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                Cursor c = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (c.moveToNext()) {
                    String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
                    int viewHeight = 1600;
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    float width = bitmap.getWidth();
                    float height = bitmap.getHeight();

                    if (height > viewHeight) {
                        float percente = height / 100;
                        float scale = viewHeight / percente;
                        width *= (scale / 100);
                        height *= (scale / 100);
                    }
                    Bitmap resizing = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
                    File out = new File(getExternalCacheDir(), System.currentTimeMillis() + " temp.jpg");
                    try {
                        FileOutputStream fos = new FileOutputStream(out);
                        resizing.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    uploadFile = out;
                    Glide.with(this)
                            .load(uploadFile)
                            .into(imagePicture);
                }
            }
        } else if (requestCode == RC_CAPTURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                int viewHeight = 1600;
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(savedFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(savedFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegrees(exifOrientation);
                bitmap = rotate(bitmap, exifDegree);
                float width = bitmap.getWidth();
                float height = bitmap.getHeight();

                if (height > viewHeight) {
                    float percente = height / 100;
                    float scale = viewHeight / percente;
                    width *= (scale / 100);
                    height *= (scale / 100);
                }
                Bitmap resizing = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
                File out = new File(getExternalCacheDir(), System.currentTimeMillis() + "_temp.jpg");
                try {
                    FileOutputStream fos = new FileOutputStream(out);
                    resizing.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadFile = out;
                Glide.with(this)
                        .load(uploadFile)
                        .into(imagePicture);
            }
        }
    }


    @OnClick(R.id.btn_question)
    public void onClickQuestion() {
        View view = LayoutInflater.from(this).inflate(R.layout.view_dialog_basic, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dialog_Transparent);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        TextView textView = (TextView) view.findViewById(R.id.text_dialog);
        textView.setText("위 내용을 등록하시겠습니까?");

        TextView textContents = (TextView) view.findViewById(R.id.text_dialog_two);
        textContents.setVisibility(View.GONE);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_dialog);
        imageView.setImageResource(R.drawable.pop_logo04);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 825;
        dialog.getWindow().setAttributes(params);


        Button btn = (Button) view.findViewById(R.id.btn_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUploadRequest();
            }
        });

        btn = (Button) view.findViewById(R.id.btn_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
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

    private void sendUploadRequest() {
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
            default :
                esType = "0";
                break;
        }
        String boardType = "2";

        BoardRequest request = new BoardRequest(this, "", esType, boardType, title, contents, uploadFile);
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                Toast.makeText(QuestionActivity.this, "문의내용이 등록되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                Toast.makeText(QuestionActivity.this, "문의내용 등록에 실패하였습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
