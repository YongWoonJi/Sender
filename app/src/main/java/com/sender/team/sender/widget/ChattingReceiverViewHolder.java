package com.sender.team.sender.widget;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sender.team.sender.MyApplication;
import com.sender.team.sender.R;
import com.sender.team.sender.Utils;
import com.sender.team.sender.data.ChattingListData;
import com.sender.team.sender.data.UserData;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tacademy on 2016-09-02.
 */
public class ChattingReceiverViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.image_chatting_receiver)
    ImageView receiverImage;

    @BindView(R.id.text_chatting_receiver_name)
    TextView receiverName;

    @BindView(R.id.text_chatting_receiver_content)
    TextView receiverContent;

    @BindView(R.id.text_chatting_receive_time)
    TextView receiverTime;

    @BindView(R.id.image_chatting_receiver_img)
    ImageView receiverImageContentImage;

    public ChattingReceiverViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickReceiverImage(view, getAdapterPosition());
            }
        });
    }

    public void setChatReceiverData(UserData user, String message, long time, String image){
        if (!TextUtils.isEmpty(user.getFileUrl())) {
            Glide.with(MyApplication.getContext()).load(user.getFileUrl()).into(receiverImage);
        }
        if (!TextUtils.isEmpty(user.getName())) {
            receiverName.setText(user.getName());
        }
        if (!TextUtils.isEmpty(message)) {
            receiverContent.setText(message);
        }
        if (time != 0) {
            receiverTime.setText(Utils.getCurrentTime(time));
        }
        if (!TextUtils.isEmpty(image)){
            Glide.with(MyApplication.getContext()).load(image).into(receiverImageContentImage);
        }
    }


    public void setChatReceiverData(ChattingListData user, String message, long time, String image){
        if (!TextUtils.isEmpty(user.getImageUrl())) {
            Glide.with(MyApplication.getContext()).load(user.getImageUrl()).into(receiverImage);
        }
        if (!TextUtils.isEmpty(user.getName())) {
            receiverName.setText(user.getName());
        }
        if (!TextUtils.isEmpty(message)) {
            receiverContent.setText(message);
        }
        if (time != 0) {
            receiverTime.setText(Utils.getCurrentTime(time));
        }
        if (!TextUtils.isEmpty(image)){
            Glide.with(MyApplication.getContext()).load(image).into(receiverImageContentImage);
        }
    }


    // 상대방 프로필을 보는 액티비티로 이동하기 위한 인터페이스
    ChatReceiverImage mListener;
    public void setOnClickChatReceiverImage(ChatReceiverImage mListener){
        this.mListener = mListener;
    }

    public interface ChatReceiverImage {
        public void onClickReceiverImage(View view, int position);
    }
}
