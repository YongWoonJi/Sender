package com.sender.team.sender;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.MyPageRequest;
import com.sender.team.sender.request.ProfilePictureUploadRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Tacademy on 2016-09-22.
 */

public class FileTarget extends SimpleTarget<Bitmap> {
    public FileTarget(Context context, String fileName, int width, int height) {
        this(context, fileName, width, height, Bitmap.CompressFormat.JPEG, 100);
    }
    public FileTarget(Context context, String fileName, int width, int height, Bitmap.CompressFormat format, int quality) {
        super(width, height);
        this.context = context;
        this.fileName = fileName;
        this.format = format;
        this.quality = quality;
    }
    Context context;
    String fileName;
    Bitmap.CompressFormat format;
    int quality;
    @Override
    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
        try {
            FileOutputStream out = new FileOutputStream(fileName);
            bitmap.compress(format, quality, out);
            out.flush();
            out.close();
            onFileSaved();
        } catch (IOException e) {
            e.printStackTrace();
            onSaveException(e);
        }
    }
    public void onFileSaved() {
        // do nothing, should be overriden (optional)
        File file = new File(fileName);
        ProfilePictureUploadRequest request = new ProfilePictureUploadRequest(context, file);
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                MyPageRequest req = new MyPageRequest(context);
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, req, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                        PropertyManager.getInstance().setUserData(result.getResult());
                        Log.i("AAA", "프로퍼티 저장 성공");
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result, String errorMessage, Throwable e) {
                        Log.i("AAA", "이미지 업로드 성공");
                    }
                });
                Log.i("AAA", "이미지 업로드 성공");
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                Log.i("AAA", "이미지 업로드 실패");
            }
        });

    }
    public void onSaveException(Exception e) {
        // do nothing, should be overriden (optional)
    }

}
