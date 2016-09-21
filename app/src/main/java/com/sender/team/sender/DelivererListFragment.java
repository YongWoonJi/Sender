package com.sender.team.sender;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sender.team.sender.data.ContractIdData;
import com.sender.team.sender.data.DelivererData;
import com.sender.team.sender.data.DelivererListData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.ReviewListData;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.ContractsRequest;
import com.sender.team.sender.request.DelivererListRequest;
import com.sender.team.sender.request.ReviewListRequest;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

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
    int deliverId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_deliverer_list, container, false);
        ButterKnife.bind(this, view);

        ((SendActivity) getActivity()).headerlayout.setVisibility(View.GONE);
        ((SendActivity) getActivity()).headerView.setVisibility(View.VISIBLE);

        mAdapter = new DelivererAdapter(getContext());
        mAdapter.setOnDialogListener(this);
        rv_view.setAdapter(mAdapter);
        rv_view.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).size(1).build());
        DelivererListRequest request = new DelivererListRequest(getContext(), "1", "100");
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<DelivererListData>>() {

            @Override
            public void onSuccess(NetworkRequest<NetworkResult<DelivererListData>> request, NetworkResult<DelivererListData> result) {
                List<DelivererData> list = new ArrayList<DelivererData>();
                DelivererData data;
                for (int i = 0; i < result.getResult().getData().size(); i++) {
                    data = result.getResult().getData().get(i);
                    data.setPosition(i+1);
                    ((SendActivity) getActivity()).addMarker(data, false);
                    ((SendActivity) getActivity()).mapSet(data);
                    list.add(data);
                }
                mAdapter.setDelivererData(list);
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<DelivererListData>> request, NetworkResult<DelivererListData> result, String errorMessage, Throwable e) {
                Toast.makeText(getActivity(), "delivererlist fail" + errorMessage, Toast.LENGTH_SHORT).show();
                Log.i("DelivererListFragment", errorMessage);
            }
        });

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_view.setLayoutManager(manager);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void clickSend() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_basic, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AppTheme_Dialog_Transparent);
        builder.setView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_dialog);
        imageView.setImageResource(R.drawable.pop_logo03);
        TextView textView = (TextView) view.findViewById(R.id.text_dialog);
        textView.setText("요청하시겠습니까?");
        TextView textContents = (TextView) view.findViewById(R.id.text_dialog_two);
        textContents.setVisibility(View.GONE);

        dialog = builder.create();
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 825;
        dialog.getWindow().setAttributes(params);

        Button btn = (Button) view.findViewById(R.id.btn_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialogShow(deliverId);
            }
        });

        btn = (Button) view.findViewById(R.id.btn_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContractsRequest request = new ContractsRequest(getActivity(), PropertyManager.getInstance().getLastContractId(), PropertyManager.getInstance().getReceiver_id(), PropertyManager.getInstance().getOtherDeliveringId(), null);
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<ContractIdData>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<ContractIdData>> request, NetworkResult<ContractIdData> result) {
                        if (result.getResult() != null && result.getError() == null){
                            Toast.makeText(getActivity(), "success : " + result.getResult().toString(), Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } else if (result.getResult() == null && result.getError() != null){
                            Toast.makeText(getActivity(), "fail : " + result.getError(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<ContractIdData>> request, NetworkResult<ContractIdData> result, String errorMessage, Throwable e) {
                        Toast.makeText(getActivity(), "fail : " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    @Override
    public void dialogShow(final int position) {
        final View view = getLayoutInflater(null).inflate(R.layout.view_dialog_review, null, false);
        final RecyclerView listView = (RecyclerView) view.findViewById(R.id.rv_view_dialog);
        final ReviewAdapter adapter = new ReviewAdapter();
        final TextView emptyReview = (TextView)view.findViewById(R.id.text_empty_my_review);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        deliverId = position;
        String delId = String.valueOf(mAdapter.getDeliverId(position));
        PropertyManager.getInstance().setOtherDeliveringId(delId);

        ReviewListRequest request = new ReviewListRequest(getContext(), "1", "100", delId);
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<ReviewListData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<ReviewListData>> request, NetworkResult<ReviewListData> result) {
                if (result.getResult() != null) {
                    adapter.clear();
                    adapter.setReviewData(result.getResult().getData().getReview());
                    emptyReview.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                } else {
                    emptyReview.setText(R.string.empty_reivew);
                    emptyReview.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AppTheme_Dialog_Transparent);
                builder.setView(view);
                Button btn = (Button) view.findViewById(R.id.btn_review_ok);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PropertyManager.getInstance().setReceiver_id(mAdapter.getId(position));
                        dialog.dismiss();
                        clickSend();
                    }
                });
                btn = (Button) view.findViewById(R.id.btn_review_cancel);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = 825;
                dialog.getWindow().setAttributes(params);
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<ReviewListData>> request, NetworkResult<ReviewListData> result, String errorMessage, Throwable e) {
                Toast.makeText(getActivity(), "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void delivererShow(int position, View view, DelivererData data) {
        ((SendActivity) getActivity()).showMarkerInfo(data, position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((SendActivity) getActivity()).headerlayout.setVisibility(View.VISIBLE);
        ((SendActivity) getActivity()).headerView.setVisibility(View.GONE);
        mAdapter.clear();
    }
}
