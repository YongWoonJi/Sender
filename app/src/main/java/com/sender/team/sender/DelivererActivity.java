package com.sender.team.sender;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import com.sender.team.sender.data.ReverseGeocodingData;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.DelivererRegisterRequest;
import com.sender.team.sender.request.POISearchRequest;
import com.sender.team.sender.request.ReverseGeocodingRequest;
import com.sender.team.sender.widget.ArrayWheelAdapter;
import com.sender.team.sender.widget.NumericWheelAdapter;
import com.sender.team.sender.widget.OnWheelChangedListener;
import com.sender.team.sender.widget.OnWheelScrollListener;
import com.sender.team.sender.widget.WheelView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DelivererActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.edit_start)
    EditText editStart;

    @BindView(R.id.edit_end)
    EditText editEnd;

    @BindView(R.id.edit_start_hour)
    TextView editStartHour;

    @BindView(R.id.edit_start_min)
    TextView editStartMin;

    @BindView(R.id.edit_end_hour)
    TextView editEndHour;

    @BindView(R.id.edit_end_min)
    TextView editEndMin;

    @BindView(R.id.list_startSearch)
    ListView listStartSearch;

    @BindView(R.id.list_endSearch)
    ListView listEndSearch;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final int TYPE_START = 0;
    private static final int TYPE_END = 1;

    GoogleMap map;
    GoogleApiClient mApiClient;
    ArrayAdapter<POI> mStartAdapter;
    ArrayAdapter<POI> mEndAdapter;

    POI poiStart, poiEnd;
    InputMethodManager mInputMethodManager;

    boolean editFlag = false;

    // Time changed flag
    private boolean timeChanged = false;
    private boolean timeScrolled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliverer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_back);

        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        editStartHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    editStartHour.setGravity(Gravity.RIGHT);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())) {
                    editStartHour.setGravity(Gravity.LEFT);
                }
            }
        });

        editEndHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    editEndHour.setGravity(Gravity.RIGHT);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())) {
                    editEndHour.setGravity(Gravity.LEFT);
                }
            }
        });

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .build();
        mApiClient.connect();

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
                downKeyboard(editStart);
                poiStart = (POI) listStartSearch.getItemAtPosition(position);
                animateMap(poiStart.getLatitude(), poiStart.getLongitude(), new Runnable() {
                    @Override
                    public void run() {
                        listStartSearch.setVisibility(View.GONE);
                        editFlag = true;
                        editStart.setText(poiStart.getName());
                        editFlag = false;
                    }
                });
                if (startMarker != null) {
                    startMarker.remove();
                }
                addMarker(poiStart, TYPE_START);
            }
        });

        listEndSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                downKeyboard(editEnd);
                poiEnd = (POI) listEndSearch.getItemAtPosition(position);
                animateMap(poiEnd.getLatitude(), poiEnd.getLongitude(), new Runnable() {
                    @Override
                    public void run() {
                        listEndSearch.setVisibility(View.GONE);
                        editFlag = true;
                        editEnd.setText(poiEnd.getName());
                        editFlag = false;
                    }
                });
                if (endMarker != null) {
                    endMarker.remove();
                }
                addMarker(poiEnd, TYPE_END);
            }
        });

        editStart.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch(actionId){
                    case EditorInfo.IME_ACTION_SEARCH:
                        onClickSearchStart();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        editEnd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId){
                    case EditorInfo.IME_ACTION_SEARCH:
                        onClickSearchEnd();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        editStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (!editFlag) {
                        poiSearch(charSequence.toString(), mStartAdapter, listStartSearch);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listEndSearch.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (!editFlag) {
                        poiSearch(charSequence.toString(), mEndAdapter, listEndSearch);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listStartSearch.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void downKeyboard(EditText editText) {
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

////////////////////////////////////////다이얼로그 타임피커/////////////////////////////////////////////////////////////////////
    @OnClick(R.id.linearLayout2)
    public void onClickOne(){
        View view = LayoutInflater.from(this).inflate(R.layout.view_dialog_timepicker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dialog_Transparent);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        //TimePicker
        String[] list = new String[] {"오전","오후"};
        final WheelView ampm = (WheelView) view.findViewById(R.id.ampm);
        ampm.setAdapter(new ArrayWheelAdapter<>(list));

        final WheelView hours = (WheelView) view.findViewById(R.id.hour);
        hours.setAdapter(new NumericWheelAdapter(1, 12));
        hours.setCyclic(true);
//		hours.setLabel(" 시");

        final WheelView mins = (WheelView) view.findViewById(R.id.mins);
        mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
//		mins.setLabel(" 분");
        mins.setCyclic(true);

        // set current time
        Calendar c = Calendar.getInstance();
        int curHours = c.get(Calendar.HOUR);
        int curMinutes = c.get(Calendar.MINUTE);
        int curAmPm = c.get(Calendar.AM_PM);

        hours.setCurrentItem(curHours-1);
        mins.setCurrentItem(curMinutes);
        ampm.setCurrentItem(curAmPm);

        // add listeners
        addChangingListener(ampm, "");
        addChangingListener(mins, "");
        addChangingListener(hours, "");

        OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!timeScrolled) {
                    timeChanged = true;
//                    picker.setCurrentHour(hours.getCurrentItem());
//                    picker.setCurrentMinute(mins.getCurrentItem());
                    timeChanged = false;
                }
            }
        };

        ampm.addChangingListener(wheelListener);
        hours.addChangingListener(wheelListener);
        mins.addChangingListener(wheelListener);

        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
                timeScrolled = true;
            }
            public void onScrollingFinished(WheelView wheel) {
                timeScrolled = false;
                timeChanged = true;
//                picker.setCurrentHour(hours.getCurrentItem()+1);
//                picker.setCurrentMinute(mins.getCurrentItem());
                timeChanged = false;
            }
        };

        ampm.addScrollingListener(scrollListener);
        hours.addScrollingListener(scrollListener);
        mins.addScrollingListener(scrollListener);

//        picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//            public void onTimeChanged(TimePicker  view, int hourOfDay, int minute) {
//                if (!timeChanged) {
//                    hours.setCurrentItem(hourOfDay, true);
//                    mins.setCurrentItem(minute, true);
//                }
//            }
//        });
        ///////////////////////////////////////////////TimePicker


        dialog.show();


        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        float dp = 300;
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        params.width = pixel;
        dialog.getWindow().setAttributes(params);

        Button btn = (Button) view.findViewById(R.id.btn_timepicker_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn = (Button) view.findViewById(R.id.btn_timepicker_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = hours.getCurrentItem()+1;
                if(ampm.getCurrentItem() == 0) {
                    editStartHour.setText("오전  " + hour );
                    editStartMin.setText("" + mins.getCurrentItem());

                } else{
                    editStartHour.setText("오후  " + hour);
                    editStartMin.setText("" + mins.getCurrentItem());
                }
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.linearLayout3)
    public void onClickTwo(){
        View view = LayoutInflater.from(this).inflate(R.layout.view_dialog_timepicker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dialog_Transparent);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        //TimePicker
        String[] list = new String[] {"오전","오후"};
        final WheelView ampm = (WheelView) view.findViewById(R.id.ampm);
        ampm.setAdapter(new ArrayWheelAdapter<>(list));

        final WheelView hours = (WheelView) view.findViewById(R.id.hour);
        hours.setAdapter(new NumericWheelAdapter(1, 12));
        hours.setCyclic(true);
//		hours.setLabel(" 시");

        final WheelView mins = (WheelView) view.findViewById(R.id.mins);
        mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
//		mins.setLabel(" 분");
        mins.setCyclic(true);

        // set current time
        Calendar c = Calendar.getInstance();
        int curHours = c.get(Calendar.HOUR);
        int curMinutes = c.get(Calendar.MINUTE);
        int curAmPm = c.get(Calendar.AM_PM);

        hours.setCurrentItem(curHours-1);
        mins.setCurrentItem(curMinutes);
        ampm.setCurrentItem(curAmPm);

        // add listeners
        addChangingListener(ampm, "");
        addChangingListener(mins, "");
        addChangingListener(hours, "");

        OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!timeScrolled) {
                    timeChanged = true;
//                    picker.setCurrentHour(hours.getCurrentItem());
//                    picker.setCurrentMinute(mins.getCurrentItem());
                    timeChanged = false;
                }
            }
        };

        ampm.addChangingListener(wheelListener);
        hours.addChangingListener(wheelListener);
        mins.addChangingListener(wheelListener);

        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
                timeScrolled = true;
            }
            public void onScrollingFinished(WheelView wheel) {
                timeScrolled = false;
                timeChanged = true;
//                picker.setCurrentHour(hours.getCurrentItem()+1);
//                picker.setCurrentMinute(mins.getCurrentItem());
                timeChanged = false;
            }
        };

        ampm.addScrollingListener(scrollListener);
        hours.addScrollingListener(scrollListener);
        mins.addScrollingListener(scrollListener);

//        picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//            public void onTimeChanged(TimePicker  view, int hourOfDay, int minute) {
//                if (!timeChanged) {
//                    hours.setCurrentItem(hourOfDay, true);
//                    mins.setCurrentItem(minute, true);
//                }
//            }
//        });
        ///////////////////////////////////////////////TimePicker


        dialog.show();


        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        float dp = 300;
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        params.width = pixel;
        dialog.getWindow().setAttributes(params);

        Button btn = (Button) view.findViewById(R.id.btn_timepicker_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn = (Button) view.findViewById(R.id.btn_timepicker_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = hours.getCurrentItem() + 1;
                if(ampm.getCurrentItem() == 0) {
                    editEndHour.setText("오전  " + hour);
                    editEndMin.setText("" + mins.getCurrentItem());

                } else{
                    editEndHour.setText("오후  " + hour);
                    editEndMin.setText("" + mins.getCurrentItem());
                }
                dialog.dismiss();
            }
        });
    }
    /**
     * Adds changing listener for wheel that updates the wheel label
     * @param wheel the wheel
     * @param label the wheel label
     */
    private void addChangingListener(final WheelView wheel, final String label) {
        wheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                wheel.setLabel(newValue != 1 ? label : label);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, mListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mEndAdapter.clear();
        mStartAdapter.clear();
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


    Marker startMarker;
    Marker endMarker;

    private void addMarker(POI poi, int type) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(poi.getLatitude(), poi.getLongitude()));
        options.anchor(0.5f, 1);
        options.title(poi.getName());
        options.snippet(poi.getMiddleAddrName() + " " + poi.getLowerAddrName());
        switch (type) {
            case 0: {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                startMarker = map.addMarker(options);
                break;
            }

            case 1: {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                endMarker = map.addMarker(options);
                break;
            }

        }

    }


    @OnClick(R.id.btn_input)
    public void onClickButton() {
        if (poiStart != null && poiEnd != null
                && !TextUtils.isEmpty(editStartHour.getText().toString().trim())
                && !TextUtils.isEmpty(editStartMin.getText().toString().trim())
                && !TextUtils.isEmpty(editEndHour.getText().toString().trim())
                && !TextUtils.isEmpty(editEndMin.getText().toString().trim())) {

            clickSend();
        } else {
            Toast.makeText(DelivererActivity.this, "정보를 모두 입력해 주세요", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.btn_search_start)
    public void onClickSearchStart() {
        String keyword = editStart.getText().toString();
        try {
            poiSearch(keyword, mStartAdapter, listStartSearch);
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editStart.getWindowToken(), 0);
    }


    @OnClick(R.id.btn_search_end)
    public void onClickSearchEnd() {
        String keyword = editEnd.getText().toString();
        try {
            poiSearch(keyword, mEndAdapter, listEndSearch);
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editEnd.getWindowToken(), 0);
    }

    private void poiSearch(String keyword, final ArrayAdapter<POI> adapter, final ListView list) throws Exception{
        if (!TextUtils.isEmpty(keyword)) {
            POISearchRequest request = new POISearchRequest(DelivererActivity.this, keyword);
            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_TMAP, request, new NetworkManager.OnResultListener<POIResult>() {
                @Override
                public void onSuccess(NetworkRequest<POIResult> request, POIResult result) {
                    adapter.clear();
                    if (result != null) {
                        adapter.addAll(result.getSearchPoiInfo().getPois().getPoi());
                        list.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFail(NetworkRequest<POIResult> request, POIResult result, String errorMessage, Throwable e) {

                }
            });
        }
    }


    AlertDialog dialog;
    String hereAddr, nextAddr, depTime, arrTime;
    private void clickSend() {
        View view = LayoutInflater.from(this).inflate(R.layout.view_dialog_basic, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(DelivererActivity.this, R.style.AppTheme_Dialog_Transparent);
        builder.setView(view);

        TextView textTitle = (TextView)view.findViewById(R.id.text_dialog);
        textTitle.setText("입력을 완료하시겠습니까?");

        TextView textContents = (TextView)view. findViewById(R.id.text_dialog_two);
        textContents.setVisibility(View.GONE);

        Button btn = (Button) view.findViewById(R.id.btn_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startHour;
                String endHour;
                int inputStartHour;
                int inputEndHour;

                StringBuffer sb = new StringBuffer(editStartHour.getText().toString());
                if (sb.substring(0, 2).toString().equals("오후")) {
                    startHour = sb.delete(0, 4).toString();
                    if (startHour.equals("12")) {
                        inputStartHour = Integer.parseInt(startHour.toString());
                    } else {
                        inputStartHour = Integer.parseInt(startHour.toString()) + 12;
                    }
                } else {
                    startHour = sb.delete(0, 4).toString();
                    if (startHour.equals("12")) {
                        inputStartHour = 0;
                    } else {
                        inputStartHour = Integer.parseInt(startHour.toString());
                    }
                }

                sb = new StringBuffer(editEndHour.getText().toString());
                if (sb.substring(0, 2).toString().equals("오후")) {
                    endHour = sb.delete(0, 4).toString();
                    if (endHour.equals("12")) {
                        inputEndHour = Integer.parseInt(endHour.toString());
                    } else {
                        inputEndHour = Integer.parseInt(endHour.toString()) + 12;
                    }
                } else {
                    endHour = sb.delete(0, 4).toString();
                    if (endHour.equals("12")) {
                        inputEndHour = 0;
                    } else {
                        inputEndHour = Integer.parseInt(endHour.toString());
                    }
                }

                String shh = String.format("%02d", inputStartHour);
                String ehh = String.format("%02d", inputEndHour);
                String smm = String.format("%02d", Integer.parseInt(editStartMin.getText().toString()));
                String emm = String.format("%02d", Integer.parseInt(editEndMin.getText().toString()));
                depTime = Utils.getCurrentDate() + " " + shh + ":" + smm + ":00";
                arrTime = Utils.getCurrentDate() + " " + ehh + ":" + emm + ":00";

                ReverseGeocodingRequest geoRequest = new ReverseGeocodingRequest(DelivererActivity.this, "" + poiStart.getLatitude(), "" + poiStart.getLongitude());
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_TMAP, geoRequest, new NetworkManager.OnResultListener<ReverseGeocodingData>() {
                    @Override
                    public void onSuccess(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result) {
                        if (result != null) {
                            hereAddr = result.getAddressInfo().getLegalDong();
                            ReverseGeocodingRequest geoRequest2 = new ReverseGeocodingRequest(DelivererActivity.this, "" + poiEnd.getLatitude(), "" + poiEnd.getLongitude());
                            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_TMAP, geoRequest2, new NetworkManager.OnResultListener<ReverseGeocodingData>() {
                                @Override
                                public void onSuccess(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result) {
                                    nextAddr = result.getAddressInfo().getLegalDong();
                                    DelivererRegisterRequest req = new DelivererRegisterRequest(DelivererActivity.this,
                                            "" + poiStart.getLatitude(), "" + poiStart.getLongitude(), hereAddr,
                                            "" + poiEnd.getLatitude(), "" + poiEnd.getLongitude(), nextAddr, depTime, arrTime);
                                    NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, req, new NetworkManager.OnResultListener<NetworkResult<DeliveringIdData>>() {
                                        @Override
                                        public void onSuccess(NetworkRequest<NetworkResult<DeliveringIdData>> request, NetworkResult<DeliveringIdData> result) {
                                            if (result != null) {
                                                PropertyManager.getInstance().setMyDeliveringId(result.getResult().getDelivering_id());
                                                Toast.makeText(DelivererActivity.this, "요청이 완료되었습니다", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onFail(NetworkRequest<NetworkResult<DeliveringIdData>> request, NetworkResult<DeliveringIdData> result, String errorMessage, Throwable e) {

                                        }
                                    });
                                }

                                @Override
                                public void onFail(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result, String errorMessage, Throwable e) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onFail(NetworkRequest<ReverseGeocodingData> request, ReverseGeocodingData result, String errorMessage, Throwable e) {

                    }
                });
            }
        });

        btn = (Button) view.findViewById(R.id.btn_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        float dp = 300;
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        params.width = pixel;
        dialog.getWindow().setAttributes(params);
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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }


    boolean isConnected = false;
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        isConnected = true;
        getLocation();
    }

    private void getLocation() {
        if (!isConnected) return;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
        if (location != null) {
            moveMap(location.getLatitude(), location.getLongitude());
        }

        LocationRequest request = new LocationRequest();
        request.setFastestInterval(10000);
        request.setInterval(20000);
        request.setNumUpdates(1);
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, request, mListener);
    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            moveMap(location.getLatitude(), location.getLongitude());
        }
    };

    @Override
    public void onConnectionSuspended(int i) {
        isConnected = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
