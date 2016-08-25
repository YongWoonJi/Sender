package com.sender.team.sender;

import android.app.DialogFragment;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by JYW on 2016-08-25.
 */
public class FabFragment extends DialogFragment {

    public FabFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fab, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        getDialog().getWindow().setLayout(width, height);
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        lp.x = 100;
        lp.y = 100;
        getDialog().getWindow().setAttributes(lp);
    }
}
