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


public class DelivererListFragment extends Fragment {

    public DelivererListFragment() {
        // Required empty public constructor
    }

    AlertDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_deliverer_list, container, false);
        Button btn = (Button) view.findViewById(R.id.btn_dialog);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("DELIVERER ID");
                builder.setMessage("딜리버러 리뷰목록..");
                builder.setPositiveButton("요청하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clickSend();
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }

    private void clickSend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("요청하시겠습니까?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), "요청이 완료되었습니다", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "취소되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
        dialog = builder.create();
        dialog.show();
    }
}
