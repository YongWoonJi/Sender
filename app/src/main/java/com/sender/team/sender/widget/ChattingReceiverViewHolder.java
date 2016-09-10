package com.sender.team.sender.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sender.team.sender.MyApplication;
import com.sender.team.sender.R;
import com.sender.team.sender.Utils;

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

    public void setChatReceiverData(String profile, String name, String message, long time, String image){
        if (profile != null) {
            Glide.with(MyApplication.getContext()).load(profile).into(receiverImage);
        }
        if (name != null) {
            receiverName.setText(name);
        }
        receiverContent.setText(message);
        receiverTime.setText(Utils.getCurrentTime(time));
        if (image != null){
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
