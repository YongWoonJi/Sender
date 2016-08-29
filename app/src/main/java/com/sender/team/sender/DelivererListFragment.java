package com.sender.team.sender;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sender.team.sender.data.DelivererDataTemp;
import com.sender.team.sender.data.ReviewDataTemp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DelivererListFragment extends Fragment implements DelivererAdapter.OnDialogListener {

    @BindView(R.id.rv_view)
    RecyclerView rv_view;

    DelivererAdapter mAdapter;

    public DelivererListFragment() {
        // Required empty public constructor
    }

    AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_deliverer_list, container, false);
        ButterKnife.bind(this, view);
        mAdapter = new DelivererAdapter();
        mAdapter.setOnDialogListener(this);
        rv_view.setAdapter(mAdapter);

        List<DelivererDataTemp> list = new ArrayList<>();
        DelivererDataTemp data;
        for (int i = 0; i < 10; i++) {
            data = new DelivererDataTemp("오름맨" + i, "010-0***-****", null, 9.4f, null, null);
            list.add(data);
        }
        mAdapter.setDelivererData(list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_view.setLayoutManager(manager);

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
                DialogShow();
            }
        });
        dialog = builder.create();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    DialogShow();
                }
                return false;
            }
        });
        dialog.show();
    }

    @Override
    public void DialogShow() {
        View view = getLayoutInflater(null).inflate(R.layout.view_dialog_review, null, false);
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.rv_view_dialog);
        ReviewAdapter adapter = new ReviewAdapter();
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        List<ReviewDataTemp> list = new ArrayList<>();
        ReviewDataTemp data;
        for (int i = 0; i < 10; i++) {
            data = new ReviewDataTemp();
            data.name = "정현맨" + i;
            data.message = "좋아요 ㅎㅎ";
            data.rating = 8.9f;
            list.add(data);
        }
        adapter.setReviewData(list);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("DELIVERER ID");
        builder.setView(view);
        builder.setPositiveButton("요청하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clickSend();
            }
        });
        dialog = builder.create();
        dialog.show();
    }
}
