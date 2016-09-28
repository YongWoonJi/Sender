package com.sender.team.sender;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment {

    private static final String POSITION = "position";

    private int position;

    public ViewPagerFragment() {
        // Required empty public constructor
    }

    public static ViewPagerFragment newInstance(int position) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        Bundle b = new Bundle();
        b.putInt(POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        ImageView iv = (ImageView) view.findViewById(R.id.view_picture);
        switch (position) {
            case 0:
                iv.setImageResource(R.drawable.sender_main4);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), NoticeActivity.class);
                        startActivity(intent);
                    }
                });
                break;
            case 1:
                iv.setImageResource(R.drawable.sender_main1);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), NoticeActivity.class);
                        startActivity(intent);
                    }
                });
                break;
            case 2:
                iv.setImageResource(R.drawable.sender_main);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), NoticeActivity.class);
                        startActivity(intent);
                    }
                });
                break;
            case 3:
                iv.setImageResource(R.drawable.sender_main3);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), NoticeActivity.class);
                        startActivity(intent);
                    }
                });
                break;
            case 4:
                iv.setImageResource(R.drawable.sender_main2);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), NoticeActivity.class);
                        startActivity(intent);
                    }
                });
                break;
        }
        return view;
    }

}
