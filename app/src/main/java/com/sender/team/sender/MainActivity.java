package com.sender.team.sender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sender.team.sender.data.ChatContract;
import com.sender.team.sender.data.ChattingListData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.gcm.MyGcmListenerService;
import com.sender.team.sender.manager.DBManager;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.LogoutRequest;
import com.sender.team.sender.request.MyPageRequest;
import com.sender.team.sender.request.UserLeaveRequest;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sender.team.sender.MyApplication.getContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MenuAdapter.OnNaviMenuSelectedListener {

    public static final int RC_PERMISSION_EXTERNAL_STORAGE = 100;
    private static final int MESSAGE_BACK_KEY_TIMEOUT = 1;
    public static final int VIEWPAGER_COUNT = 5;

    private Boolean isFabOpen = false;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private ImageView iconView;
    private ChattingListAdapter mAdapter;
    private ColorDrawable toolbarColor;
    private Drawable homeAsUp;
    private Handler handler;


    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.appbar)
    AppBarLayout appbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.content)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.navi_fragment)
    RelativeLayout naviFragment;

    @BindView(R.id.tab)
    LinearLayout tabs;

    @BindView(R.id.rv_list)
    RecyclerView listView;

    @BindView(R.id.toolbar_logo)
    ImageView toolbarLogo;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.circleAnimIndicator)
    CircleAnimIndicator circleAnimIndicator;

    @BindView(R.id.fab_background)
    RelativeLayout fab_background;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.fab1)
    FloatingActionButton fab1;

    @BindView(R.id.fab2)
    FloatingActionButton fab2;

    @BindView(R.id.fab3)
    FloatingActionButton fab3;

    @BindView(R.id.image_icon1)
    ImageView tabIcon1;

    @BindView(R.id.image_icon2)
    ImageView tabIcon2;

    @BindView(R.id.image_icon3)
    ImageView tabIcon3;

    @BindView(R.id.text_menu_title1)
    TextView textMenu1;

    @BindView(R.id.text_menu_title2)
    TextView textMenu2;

    @BindView(R.id.text_menu_title3)
    TextView textMenu3;

    @BindView(R.id.indicator1)
    View indicator1;

    @BindView(R.id.indicator2)
    View indicator2;

    @BindView(R.id.tab1)
    LinearLayout tab1;

    @BindView(R.id.tab2)
    LinearLayout tab2;

    @BindView(R.id.tab3)
    LinearLayout tab3;


    int index;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            index++;
            viewPager.setCurrentItem(index);
            handler.postDelayed(this, 3000);
        }
    };


    LocalBroadcastManager mLBM;
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initData();
                    Toast.makeText(MainActivity.this, "계약이 성립되었습니다", Toast.LENGTH_SHORT).show();
                }
            });
            intent.putExtra(MyGcmListenerService.EXTRA_RESULT_CONFIRM, true);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.btn_menu_before);

        init();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                }
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_EXTERNAL_STORAGE);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        closeNavi(false);
        initData();
        mLBM.registerReceiver(mReceiver, new IntentFilter(MyGcmListenerService.ACTION_CONFIRM));
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLBM.unregisterReceiver(mReceiver);
        handler.removeCallbacks(runnable);
    }



    private void init() {
        mLBM = LocalBroadcastManager.getInstance(this);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MESSAGE_BACK_KEY_TIMEOUT :
                        isBackpressed = false;
                        break;
                }
            }
        };
        setStatusBar();
        initViewPager();
        initTabLayout();
        initRecyclerView();
        initFab();
        toolbarColor = new ColorDrawable(Color.rgb(255, 255, 255));
        homeAsUp = ContextCompat.getDrawable(this, R.drawable.btn_menu_before);

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float offset = appBarLayout.getY() / (appbar.getTotalScrollRange() / 2);
                if (offset < -1) {
                    offset = -1;
                }
                toolbarColor.setAlpha((int)((offset * -1) * 255));
                getSupportActionBar().setBackgroundDrawable(toolbarColor);
                changeToolbarIconsColor(offset);

                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 5000);
            }
        });

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                float xPositionOpenDrawer = naviFragment.getWidth();
                float xPositionWindowContent = (slideOffset * xPositionOpenDrawer);
                coordinatorLayout.setTranslationX(xPositionWindowContent);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(final View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


    }

    private void initRecyclerView() {
        mAdapter = new ChattingListAdapter();
        listView.setAdapter(mAdapter);
        listView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        float dp = 76;
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        listView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).size(1).margin(pixel, 0).build());
    }

    private void initViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(VIEWPAGER_COUNT * 1000);
        index = viewPager.getCurrentItem();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                handler.removeCallbacks(runnable);
                index = viewPager.getCurrentItem();
                handler.postDelayed(runnable, 5000);
            }

            @Override
            public void onPageSelected(int position) {
                circleAnimIndicator.selectDot(position % VIEWPAGER_COUNT);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        circleAnimIndicator.setItemMargin(15);
        circleAnimIndicator.setAnimDuration(300);
        circleAnimIndicator.createDotPanel(5, R.drawable.non_indicator, R.drawable.non_indicator);

        handler.postDelayed(runnable, 5000);
    }



    private void initTabLayout() {
        tabIcon1.setImageResource(R.drawable.btn_tab01_icon);
        textMenu1.setText("요청하기");

        tabIcon2.setImageResource(R.drawable.btn_tab02_icon);
        textMenu2.setText("배송하기");

        tabIcon3.setImageResource(R.drawable.btn_tab03_icon);
        textMenu3.setText("마이페이지");

        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SendActivity.class);
                startActivity(intent);
            }
        });

        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DelivererActivity.class);
                startActivity(intent);
            }
        });

        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyPageRequest request = new MyPageRequest(MainActivity.this);
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                        PropertyManager.getInstance().setUserData(result.getResult());
                        Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result, String errorMessage, Throwable e) {

                    }
                });
            }
        });
    }

    private void initFab() {
        fab_background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
                    animateFAB();
                }
                return true;
            }
        });

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                w.setStatusBarColor(Color.TRANSPARENT);
//            }
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            };
        }, 2000);
    }

    private void changeToolbarIconsColor(float offset) {
        int value = (int) (255 - (-130 * offset));
        int r, g, b;
        if (value == 0) {
            value = 255;
            r = g = b = 255;
        } else {
            r = 255;
            g = (int) (255 - (-194 * offset));
            b = (int) (255 - (-185 * offset));
        }
        homeAsUp.setColorFilter(Color.rgb(value, value, value), PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(homeAsUp);

        if (iconView != null) {
            iconView.setColorFilter(Color.rgb(value, value, value));
        }

        toolbarLogo.setColorFilter(Color.rgb(r, g, b));

        tabs.setBackgroundColor(Color.rgb(r, g, b));

        value = (int) (159 + (-96 * offset));
        if (value == 0) {
            value = 125;
        }
        tabIcon1.setColorFilter(Color.rgb(value, value, value));
        tabIcon2.setColorFilter(Color.rgb(value, value, value));
        tabIcon3.setColorFilter(Color.rgb(value, value, value));
        textMenu1.setTextColor(Color.rgb(value, value, value));
        textMenu2.setTextColor(Color.rgb(value, value, value));
        textMenu3.setTextColor(Color.rgb(value, value, value));
        indicator1.setBackgroundColor(Color.argb(150, value, value, value));
        indicator2.setBackgroundColor(Color.argb(150, value, value, value));
    }

    ChattingListData data;
    ArrayList<ChattingListData> list;
    private void initData() {
        Cursor cursor = DBManager.getInstance().getChatUser();
        if (cursor != null){
            list = new ArrayList<>();
            while (cursor.moveToNext()) {
                data = new ChattingListData();
                data.setId(cursor.getLong(cursor.getColumnIndex(ChatContract.ChatUser.COLUMN_SERVER_ID)));
                data.setContractId(cursor.getLong(cursor.getColumnIndex(ChatContract.ChatMessage.COLUMN_CONTRACT_ID)));
                data.setName(cursor.getString(cursor.getColumnIndex(ChatContract.ChatUser.COLUMN_NAME)));
                data.setImageUrl(cursor.getString(cursor.getColumnIndex(ChatContract.ChatUser.COLUMN_PROFILE_IMAGE)));
                data.setMessage(cursor.getString(cursor.getColumnIndex(ChatContract.ChatMessage.COLUMN_MESSAGE)));
                data.setPhone(cursor.getString(cursor.getColumnIndex(ChatContract.ChatUser.COLUMN_PHONE)));
                data.setTime(cursor.getString(cursor.getColumnIndex(ChatContract.ChatMessage.COLUMN_CREATED)));
                data.setType(cursor.getInt(cursor.getColumnIndex(ChatContract.ChatUser.COLUMN_HEADER_TYPE)));
                data.setMessageType(cursor.getInt(cursor.getColumnIndex(ChatContract.ChatMessage.COLUMN_TYPE)));
                list.add(data);
            }

            int count = 0;
            while (count < (10 - cursor.getCount())) {
                data = new ChattingListData();
                data.setMessage(getString(R.string.empty_chatting_list));
                data.setType(ChattingListData.TYPE_EMPTY);
                list.add(data);
                count++;
            }
            mAdapter.setData(list);
            cursor.close();
        }

    }



    public void animateFAB() {
        if(isFabOpen){
            fab_background.setVisibility(View.GONE);
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
        } else {
            fab_background.setVisibility(View.VISIBLE);
            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                animateFAB();
                break;
            case R.id.fab1:{
                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                startActivity(intent);
                animateFAB();
                break;
            }
            case R.id.fab2:{
                Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                startActivity(intent);
                animateFAB();
                break;
            }
            case R.id.fab3:{
                Intent intent = new Intent(MainActivity.this, FAQActivity.class);
                startActivity(intent);
                animateFAB();
                break;
            }
        }
    }

    private boolean isBackpressed = false;
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isFabOpen) {
            animateFAB();
        } else {
            if (!isBackpressed) {
                Toast.makeText(this, getString(R.string.finish), Toast.LENGTH_SHORT).show();
                isBackpressed = true;
                handler.sendEmptyMessageDelayed(MESSAGE_BACK_KEY_TIMEOUT, 2000);
            } else {
                handler.removeMessages(MESSAGE_BACK_KEY_TIMEOUT);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.menu_info);
        View view = MenuItemCompat.getActionView(item);
        iconView = (ImageView) view.findViewById(R.id.toolbar_menu_icon);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                view = inflater.inflate(R.layout.view_main_version, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                dialog.show();

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                float dp = 300;
                int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
                params.width = pixel;
                dialog.getWindow().setAttributes(params);

                Button btn = (Button) view.findViewById(R.id.btn_version_ok);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void Logout() {
        LogoutRequest request = new LogoutRequest(this);
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                if (!TextUtils.isEmpty(result.getResult())) {
                    Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                    if (!TextUtils.isEmpty(PropertyManager.getInstance().getFacebookId())) {
                        PropertyManager.getInstance().setFacebookId("");
                        intent.putExtra(SplashActivity.FACEBOOK_LOGOUT, "facebooklogout");
                    } else if (!TextUtils.isEmpty(PropertyManager.getInstance().getNaverToken())){
                        PropertyManager.getInstance().setNaverToken("");
                        intent.putExtra(SplashActivity.NAVER_LOGOUT, "naverlogout");
                    }

                    PropertyManager.getInstance().setUserData(null);
                    Toast.makeText(MainActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "로그아웃 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                Toast.makeText(MainActivity.this, "request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void unregister() {
        View view = LayoutInflater.from(this).inflate(R.layout.view_dialog_basic, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dialog_Transparent);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        float dp = 300;
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        params.width = pixel;
        dialog.getWindow().setAttributes(params);

        Button btn = (Button) view.findViewById(R.id.btn_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserLeaveRequest request = new UserLeaveRequest(MainActivity.this, PropertyManager.getInstance().getUserData().getUser_id());
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                        if (!TextUtils.isEmpty(result.getResult())) {
                            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                            if (!TextUtils.isEmpty(PropertyManager.getInstance().getFacebookId())) {
                                PropertyManager.getInstance().setFacebookId("");
                                intent.putExtra(SplashActivity.FACEBOOK_LEAVE, "facebookleave");
                            } else if (!TextUtils.isEmpty(PropertyManager.getInstance().getNaverToken())){
                                PropertyManager.getInstance().setNaverToken("");
                                intent.putExtra(SplashActivity.NAVER_LEAVE, "naverleave");
                            }

                            PropertyManager.getInstance().setUserData(null);
                            Toast.makeText(MainActivity.this, "정상적으로 탈퇴되었습니다", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "탈퇴 처리 실패", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result, String errorMessage, Throwable e) {
                        Toast.makeText(MainActivity.this, "request failed", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void closeNavi(boolean animate) {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START, animate);
        }
    }

    @Override
    public void resetChild() {
        listener.childReset();
    }

    NaviChildClickListener listener;
    public interface NaviChildClickListener {
        void childReset();
    }

    public void setNaviChildClickListener(NaviChildClickListener listener) {
        this.listener = listener;
    }
}
