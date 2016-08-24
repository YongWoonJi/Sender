package com.sender.team.sender;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendHeaderFragment extends Fragment {


    public SendHeaderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_header, container, false);
        Button btn = (Button)view.findViewById(R.id.btn_end);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSend();
            }
        });

        return view;
    }
    AlertDialog dialog;
    private void clickSend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("별점 및 리뷰");
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), "배송이 완료되었습니다", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

}
