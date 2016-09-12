package com.sender.team.sender;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.sender.team.sender.data.DelivererData;
import com.sender.team.sender.data.POI;
import com.sender.team.sender.data.POIResult;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.POISearchRequest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendActivity extends AppCompatActivity implements InfoInputFragment.OnMessageCallback
        , OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener {

    boolean isInfoOpen = false;
    GoogleMap mMap;
    LocationManager mLM;
    String mProvider = LocationManager.NETWORK_PROVIDER;

    @BindView(R.id.edit_search_poi)
    EditText searchView;

    @BindView(R.id.text_deliverer_search_content)
    TextView headerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ArrayAdapter<POI> mAdapter;
    Map<POI, Marker> markerResolver = new HashMap<>();
    Map<Marker, POI> poiResolver = new HashMap<>();

    POI selectPoi;

    Map<DelivererData, Marker> deliverMarkerResolver = new HashMap<>();
    Map<Marker, DelivererData> deliverDelivererDataResolver = new HashMap<>();

    Map<Integer, DelivererData> deliverSelect = new HashMap<>();

    @BindView(R.id.listView)
    ListView listView;

    @BindView(R.id.btn_search)
    Button searchBtn;

    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        ButterKnife.bind(this);
        mAdapter = new ArrayAdapter<POI>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);
        listView.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new InfoInputFragment())
                .commit();
        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final POI poi = (POI) listView.getItemAtPosition(position);
                animateMap(poi.getLatitude(), poi.getLongitude(), new Runnable() {

                    @Override
                    public void run() {
                        listView.setVisibility(View.GONE);
                        selectPoi = poi;
                        Marker m = markerResolver.get(poi);
                        m.showInfoWindow();
                        searchView.setText(poi.getName());
                    }
                });
                mMap.clear();
                addMarker(poi);
            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId){
                    case EditorInfo.IME_ACTION_SEARCH:
                        onClickSearch();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    private void animateMap(DelivererData data, final Runnable callback) {
        if (mMap != null) {
            double lat = Double.parseDouble(data.getHere_lat());
            double lng = Double.parseDouble(data.getHere_lon());
            CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(lat, lng));
            mMap.animateCamera(update, new GoogleMap.CancelableCallback() {
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

    private void animateMap(double lat, double lng, final Runnable callback) {
        if (mMap != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(lat, lng));
            mMap.animateCamera(update, new GoogleMap.CancelableCallback() {
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

    @OnClick(R.id.btn_search)
    public void onClickSearch() {
        String keyword = searchView.getText().toString();
        if (!TextUtils.isEmpty(keyword)) {
            POISearchRequest request = new POISearchRequest(SendActivity.this, keyword);
            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_TMAP, request, new NetworkManager.OnResultListener<POIResult>() {
                @Override
                public void onSuccess(NetworkRequest<POIResult> request, POIResult result) {
                    listView.setVisibility(View.VISIBLE);
                    clear();

                    mAdapter.addAll(result.getSearchPoiInfo().getPois().getPoi());
                    for (POI poi : result.getSearchPoiInfo().getPois().getPoi()) {
                        addMarker(poi);
                    }
                    if (result.getSearchPoiInfo().getPois().getPoi().length > 0) {
                        POI poi = result.getSearchPoiInfo().getPois().getPoi()[0];
                        moveMap(poi.getLatitude(), poi.getLongitude());
                    }
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }

                @Override
                public void onFail(NetworkRequest<POIResult> request, POIResult result, String errorMessage, Throwable e) {
                    Toast.makeText(SendActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.i("Send", errorMessage);
                }
            });
        }
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
    public void onBackPressed() {
        if (isInfoOpen) {
            getSupportFragmentManager().popBackStack();
            isInfoOpen = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClickButton() {
        isInfoOpen = true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);


    }

    private void clear() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            POI poi = mAdapter.getItem(i);
            Marker m = markerResolver.get(poi);
            m.remove();
        }
        mAdapter.clear();
    }

    double hereLat;
    double hereLng;
    double addrLat;
    double addrLng;

    private void addMarker(double lat, double lng, String title) {
        if (marker != null) {
            marker.remove();
            marker = null;
        }

        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(lat, lng));
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        options.anchor(0.5f, 1);
        options.title(title);
        options.snippet("snippet - " + title);
        options.draggable(true);

        marker = mMap.addMarker(options);
    }


    private void addMarker(POI poi) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(poi.getLatitude(), poi.getLongitude()));
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        options.anchor(0.5f, 1);
        options.title(poi.getName());
        options.snippet(poi.getMiddleAddrName() + " " + poi.getLowerAddrName());

        Marker marker = mMap.addMarker(options);
        markerResolver.put(poi, marker);
        poiResolver.put(marker, poi);

        addrLat = poi.getLatitude();
        addrLng = poi.getLongitude();
    }

    protected void addMarker(DelivererData data, int i) {

        MarkerOptions options = new MarkerOptions();
        double lat = Double.parseDouble(data.getHere_lat());
        double lon = Double.parseDouble(data.getHere_lon());
        options.position(new LatLng(lat,lon));
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        options.anchor(0.5f, 1);
        options.title(data.getName());

        Marker marker = mMap.addMarker(options);
        deliverMarkerResolver.put(data, marker);
        deliverDelivererDataResolver.put(marker, data);
        deliverSelect.put(i,data);

    }

    private void moveMap(double lat, double lng) {
        if (mMap != null) {
            LatLng latLng = new LatLng(lat, lng);
            CameraPosition position = new CameraPosition.Builder()
                    .target(latLng)
                    .bearing(30)
                    .tilt(45)
                    .zoom(17)
                    .build();
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
//            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);

            mMap.moveCamera(update);
//        map.animateCamera(update);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mLM.getLastKnownLocation(mProvider);
        if (location != null) {
            mListener.onLocationChanged(location);
        }
        mLM.requestSingleUpdate(mProvider, mListener, null);
        isRequestCheck = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLM.removeUpdates(mListener);
        isRequestCheck = false;
    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            hereLat = location.getLatitude();
            hereLng = location.getLongitude();
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

    @Override
    public void onCameraMove() {
        CameraPosition position = mMap.getCameraPosition();
        LatLng target = position.target;
        Projection projection = mMap.getProjection();
        VisibleRegion region = projection.getVisibleRegion();
    }

    boolean isRequestCheck = false;

    public void receiveData(String obName, String phone, String price, String time, File uploadFile, String memo) {
        Fragment f = new InfoInputFragment();
        if (f != null) {
            ((InfoInputFragment) f).setSenderData(getApplicationContext(),hereLat, hereLng, addrLat, addrLng, obName, phone, price, time,uploadFile, memo);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
//        addMarker(latLng.latitude, latLng.longitude, "my marker");
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng latLng = marker.getPosition();
        Log.i("GoogleMapActivity", "lat : " + latLng.latitude + ", lng : " + latLng.longitude);
    }
    //back key를 눌렀을 때 선택했었던 장소 마커 다시 찍음
    public void backMarker(){
        if(selectPoi != null){
            mMap.clear();
            addMarker(selectPoi);
        }
    }

    public void showMarkerInfo(DelivererData data){
        reDrawMarker();
        MarkerOptions options = new MarkerOptions();
        double lat = Double.parseDouble(data.getHere_lat());
        double lng = Double.parseDouble(data.getHere_lon());
        options.position(new LatLng(lat,lng));
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        options.anchor(0.5f, 1);
        options.title(data.getName());
        Marker m = mMap.addMarker(options);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),11));
        m.showInfoWindow();
    }

    public void reDrawMarker(){
        mMap.clear();
        for (int i = 0; i<deliverSelect.size(); i++){
            addMarker(deliverSelect.get(i), i);
        }
    }

}
