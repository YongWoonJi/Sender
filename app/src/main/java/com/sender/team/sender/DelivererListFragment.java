package com.sender.team.sender;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sender.team.sender.data.DelivererListData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.DelivererListRequest;

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
        DelivererListRequest request = new DelivererListRequest(getContext(), "1","1");
        NetworkManager.getInstance().getNetworkData(1,request, new NetworkManager.OnResultListener<NetworkResult<DelivererListData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<DelivererListData>> request, NetworkResult<DelivererListData> result) {
                for (int i = 0; i< result.getResult().getData().size(); i++){
                    ((SendActivity)getActivity()).addMarker(result.getResult().getData().get(i));
                }
                mAdapter.setDelivererData(result.getResult().getData());
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<DelivererListData>> request, String errorMessage, Throwable e) {
                Toast.makeText(getActivity(), "delivererlist fail" + errorMessage, Toast.LENGTH_SHORT).show();
                Log.i("DelivererListFragment",errorMessage);
            }
        });




//        for (int i = 0; i < 10; i++) {
//            data = new DelivererDataTemp("오름맨" + i, "010-0***-****", null, 9.4f, null, null);
//            list.add(data);
//        }

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


//        List<ReviewDataTemp> list = new ArrayList<>();
//        ReviewDataTemp data;
//        for (int i = 0; i < 10; i++) {
//            data = new ReviewDataTemp();
//            data.name = "정현맨" + i;
//            data.message = "좋아요 ㅎㅎ";
//            data.rating = 8.9f;
//            list.add(data);
//        }
//
//        adapter.setReviewData(list);
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


    @Override
    public void onStop() {
        super.onStop();
        ((SendActivity)getActivity()).searchView.setVisibility(View.VISIBLE);
        ((SendActivity)getActivity()).searchBtn.setVisibility(View.VISIBLE);
        ((SendActivity)getActivity()).headerView.setVisibility(View.GONE);
        ((SendActivity)getActivity()).mMap.clear();
    }

//    public void setSenderData(String name, String phone, String price, double hereLat, double hereLng, double addrLat, double addrLng){
//        String n = name;
//        String n2 = phone;
//        String n3 = price;
//        double n4 = hereLat;
//        double n5 = hereLng;
//        double n6 = addrLat;
//        double n7 = addrLng;
//    }


}
