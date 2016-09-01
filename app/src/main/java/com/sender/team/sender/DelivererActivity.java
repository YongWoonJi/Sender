package com.sender.team.sender;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sender.team.sender.data.DeliveringIdData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.POI;
import com.sender.team.sender.data.POIResult;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.DelivererRegisterRequest;
import com.sender.team.sender.request.POISearchRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DelivererActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.edit_start)
    EditText editStart;

    @BindView(R.id.edit_end)
    EditText editEnd;

    @BindView(R.id.edit_start_hour)
    EditText editStartHour;

    @BindView(R.id.edit_start_min)
    EditText editStartMin;

    @BindView(R.id.edit_end_hour)
    EditText editEndHour;

    @BindView(R.id.edit_end_min)
    EditText editEndMin;

    @BindView(R.id.list_startSearch)
    ListView listStartSearch;

    @BindView(R.id.list_endSearch)
    ListView listEndSearch;

    private static final int TYPE_START = 0;
    private static final int TYPE_END = 1;

    LocationManager mLM;
    String mProvider = LocationManager.NETWORK_PROVIDER;

    GoogleMap map;
    ArrayAdapter<POI> mStartAdapter;
    ArrayAdapter<POI> mEndAdapter;

    POI poiStart, poiEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverer);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mStartAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mEndAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listStartSearch.setAdapter(mStartAdapter);
        listEndSearch.setAdapter(mEndAdapter);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        fragment.getMapAsync(this);

        listStartSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                poiStart = (POI) listStartSearch.getItemAtPosition(position);
                animateMap(poiStart.getLatitude(), poiStart.getLongitude(), new Runnable() {
                    @Override
                    public void run() {
                        addMarker(poiStart, TYPE_START);
                        listStartSearch.setVisibility(View.GONE);
                    }
                });
            }
        });

        listEndSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                poiEnd = (POI) listEndSearch.getItemAtPosition(position);
                animateMap(poiEnd.getLatitude(), poiEnd.getLongitude(), new Runnable() {
                    @Override
                    public void run() {
                        addMarker(poiEnd, TYPE_END);
                        listEndSearch.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mLM.getLastKnownLocation(mProvider);
        if (location != null) {
            mListener.onLocationChanged(location);
        }

        mLM.requestSingleUpdate(mProvider, mListener, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLM.removeUpdates(mListener);
    }

    private void animateMap(double lat, double lng, final Runnable callback) {
        if (map != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(lat, lng));
            map.animateCamera(update, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    callback.run();
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    private void moveMap(double lat, double lng) {
        if (map != null) {
            LatLng latLng = new LatLng(lat, lng);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 14);
            map.moveCamera(update);
        }
    }

    private void addMarker(POI poi, int type) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(poi.getLatitude(), poi.getLongitude()));
        switch (type) {
            case 0:
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                break;
            case 1:
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                break;
        }
        options.anchor(0.5f, 1);
        options.title(poi.getName());
        options.snippet(poi.getMiddleAddrName() + " " + poi.getLowerAddrName());

        Marker marker = map.addMarker(options);
    }


    @OnClick(R.id.btn_input)
    public void onClickButton() {
        if (!TextUtils.isEmpty(editStartHour.getText()) && !TextUtils.isEmpty(editStartMin.getText()) && !TextUtils.isEmpty(editEndHour.getText()) && !TextUtils.isEmpty(editEndMin.getText())) {
            clickSend();
        } else {
            Toast.makeText(DelivererActivity.this, "시간을 입력해 주세요", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.btn_search_start)
    public void onClickSearchStart() {
        String keyword = editStart.getText().toString();
        if (!TextUtils.isEmpty(keyword)) {
            POISearchRequest request = new POISearchRequest(DelivererActivity.this, keyword);
            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<POIResult>() {
                @Override
                public void onSuccess(NetworkRequest<POIResult> request, POIResult result) {
                    mStartAdapter.clear();
                    mStartAdapter.addAll(result.getSearchPoiInfo().getPois().getPoi());
                    listStartSearch.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFail(NetworkRequest<POIResult> request, String errorMessage, Throwable e) {

                }
            });
        }
    }

    @OnClick(R.id.btn_search_end)
    public void onClickSearchEnd() {
        String keyword = editEnd.getText().toString();
        if (!TextUtils.isEmpty(keyword)) {
            POISearchRequest request = new POISearchRequest(DelivererActivity.this, keyword);
            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<POIResult>() {
                @Override
                public void onSuccess(NetworkRequest<POIResult> request, POIResult result) {
                    mEndAdapter.clear();
                    mEndAdapter.addAll(result.getSearchPoiInfo().getPois().getPoi());
                    listEndSearch.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFail(NetworkRequest<POIResult> request, String errorMessage, Throwable e) {

                }
            });
        }
    }


    AlertDialog dialog;
    private void clickSend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DelivererActivity.this);
        builder.setMessage("입력을 완료하시겠습니까?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String depTime = Utils.getCurrentDate() + " " + editStartHour + ":" + editStartMin + ":00";
                String arrTime = Utils.getCurrentDate() + " " + editEndHour + ":" + editEndMin + ":00";
                DelivererRegisterRequest request = new DelivererRegisterRequest(DelivererActivity.this,
                        PropertyManager.getInstance().getUserData().getUser_id(), "" + poiStart.getLatitude(), "" + poiStart.getLongitude(),
                        "" + poiEnd.getLatitude(), "" + poiEnd.getLongitude(), depTime, arrTime);
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_SECURE, request, new NetworkManager.OnResultListener<NetworkResult<DeliveringIdData>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<DeliveringIdData>> request, NetworkResult<DeliveringIdData> result) {
                        if (result != null) {
                            PropertyManager.getInstance().setDeliveringId(result.getResult());
                            Toast.makeText(DelivererActivity.this, "요청이 완료되었습니다", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DelivererActivity.this, NotificationService.class);
                            startService(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<DeliveringIdData>> request, String errorMessage, Throwable e) {

                    }
                });
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(DelivererActivity.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
        dialog = builder.create();
        dialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            moveMap(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
