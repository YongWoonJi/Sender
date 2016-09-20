package com.sender.team.sender;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
        GoogleMap.OnInfoWindowClickListener{

    boolean isInfoOpen = false;
    GoogleMap mMap;
    LocationManager mLM;
    String mProvider = LocationManager.NETWORK_PROVIDER;

    @BindView(R.id.edit_search_poi)
    EditText searchView;

    @BindView(R.id.headerLayout)
    RelativeLayout headerlayout;

    @BindView(R.id.layout_deliverer_search_content)
    RelativeLayout headerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ArrayAdapter<POI> mAdapter;
    Map<POI, Marker> markerResolver = new HashMap<>();
    Map<Marker, POI> poiResolver = new HashMap<>();

    POI selectPoi;

    Map<DelivererData, Marker> deliverMarkerResolver = new HashMap<>();
    Map<Marker, DelivererData> deliverDelivererDataResolver = new HashMap<>();

    Map<Integer, DelivererData> deliverSelect = new HashMap<>();
    Map<DelivererData, Integer> deliverNumber = new HashMap<>();

    @BindView(R.id.listView)
    ListView listView;

    @BindView(R.id.btn_search)
    Button searchBtn;

    //    @BindView(R.id.map_marker)
    TextView mapMarker;

    Marker marker;
    boolean editFlag = false;

    SupportMapFragment mapFragment;

    View markerView, markerSelectView;

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
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final POI poi = (POI) listView.getItemAtPosition(position);
                animateMap(poi.getLatitude(), poi.getLongitude(), new Runnable() {

                    @Override
                    public void run() {
                        selectPoi = poi;
                        Marker m = markerResolver.get(poi);
                        m.showInfoWindow();
                        editFlag = true;
                        searchView.setText(poi.getName());
                        editFlag = false;
                        listView.setVisibility(View.GONE);
                    }
                });
                mMap.clear();
                addMarker(poi);
            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        onClickSearch();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!editFlag) {
                    try {
                        poiSearch(charSequence.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SendActivity.this, "검색실패", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

    private void poiSearch(String keyword) throws Exception {
        if (!TextUtils.isEmpty(keyword)) {
            POISearchRequest request = new POISearchRequest(SendActivity.this, keyword);
            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_TMAP, request, new NetworkManager.OnResultListener<POIResult>() {
                @Override
                public void onSuccess(NetworkRequest<POIResult> request, POIResult result) {
                    listView.setVisibility(View.VISIBLE);
                    mAdapter.clear();
                    if (result != null) {
                        mAdapter.addAll(result.getSearchPoiInfo().getPois().getPoi());
                        if (result.getSearchPoiInfo().getPois().getPoi().length > 0) {
                            POI poi = result.getSearchPoiInfo().getPois().getPoi()[0];
                            moveMap(poi.getLatitude(), poi.getLongitude());
                        }
                    }
                }

                @Override
                public void onFail(NetworkRequest<POIResult> request, POIResult result, String errorMessage, Throwable e) {
                    Toast.makeText(SendActivity.this, "검색을 실패했습니다. : " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.i("Send", errorMessage);
                }
            });
        }
    }

    @OnClick(R.id.btn_search)
    public void onClickSearch() {
        String text = searchView.getText().toString();
        try {
            poiSearch(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
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

        Location location = mLM.getLastKnownLocation(mProvider);
        moveMap(location.getLatitude(), location.getLongitude());
        markerView = LayoutInflater.from(this).inflate(R.layout.view_custom_marker, null);
//        markerSelectView = LayoutInflater.from(this).inflate(R.layout.view_custom_marker_select, null);
        mapMarker = (TextView) markerView.findViewById(R.id.text_map_marker);
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

//    private void addMarker(double lat, double lng, String title) {
//        if (marker != null) {
//            marker.remove();
//            marker = null;
//        }
//
//        MarkerOptions options = new MarkerOptions();
//        options.position(new LatLng(lat, lng));
//        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//        options.anchor(0.5f, 1);
//        options.title(title);
//        options.snippet("snippet - " + title);
//        options.draggable(true);
//
//        marker = mMap.addMarker(options);
//    }


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
    protected void mapSet(DelivererData data){
        deliverMarkerResolver.put(data, marker);
        deliverDelivererDataResolver.put(marker, data);
        deliverSelect.put(data.getPosition(), data);
        deliverNumber.put(data, data.getPosition());
    }
    protected Marker addMarker(DelivererData data, boolean isSelectedMarker) {

        MarkerOptions options = new MarkerOptions();
        double lat = Double.parseDouble(data.getHere_lat());
        double lon = Double.parseDouble(data.getHere_lon());
        options.position(new LatLng(lat, lon));
//        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        if (isSelectedMarker) {
            mapMarker.setBackgroundResource(R.drawable.marker_select);
            mapMarker.setTextColor(Color.BLACK);
        } else {
            mapMarker.setBackgroundResource(R.drawable.marker);
            mapMarker.setTextColor(Color.WHITE);
        }
        options.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, markerView)));
        options.anchor(0.5f, 1);
        options.title(data.getName());
        int position = data.getPosition();
        mapMarker.setText("" + position);
        return mMap.addMarker(options);
    }

    // 마커 비트맵 변환
    private Bitmap createDrawableFromView(Context context, View markerView) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        markerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        markerView.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        markerView.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        markerView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);

        return bitmap;
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
        mAdapter.clear();
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
            ((InfoInputFragment) f).setSenderData(getApplicationContext(), hereLat, hereLng, addrLat, addrLng, obName, phone, price, time, uploadFile, memo);
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

    //back key를 눌렀을 때 선택했었던 장소 마커 다시 찍음
    public void backMarker() {
        if (selectPoi != null) {
            mMap.clear();
            addMarker(selectPoi);
        }
    }


    Marker selectedMarker = null;
    public void showMarkerInfo(DelivererData data, int position) {
//        changeSelectedMarker(deliverMarkerResolver.get(data), data);
        if (selectedMarker == null) {
            selectedMarker = addMarker(data, true);
            selectedMarker.showInfoWindow();
            selectedMarker.setZIndex(8.0f);
        } else {
            selectedMarker.remove();
            selectedMarker = addMarker(data, true);
            selectedMarker.showInfoWindow();
            selectedMarker.setZIndex(8.0f);
        }


        double lat = Double.parseDouble(data.getHere_lat());
        double lng = Double.parseDouble(data.getHere_lon());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 9));
    }


//    private void changeSelectedMarker(Marker marker, DelivererData data) {
//        // 선택했던 마커 되돌리기
//        if (selectedMarker != null) {
//            addMarker(deliverDelivererDataResolver.get(selectedMarker), false);
//            selectedMarker.remove();
//        }
//
//        // 선택한 마커 표시
//        if (marker != null) {
//            selectedMarker = addMarker(data, true);
//            selectedMarker.showInfoWindow();
//            selectedMarker.setZIndex(1.0f);
//            marker.remove();
//        }
//        double lat = Double.parseDouble(data.getHere_lat());
//        double lng = Double.parseDouble(data.getHere_lon());
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 11));
//    }
}
