package com.sender.team.sender;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sender.team.sender.data.ChattingListData;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.LogoutRequest;
import com.sender.team.sender.request.OtherUserRequest;
import com.sender.team.sender.request.UserLeaveRequest;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MenuAdapter.OnNaviMenuSelectedListener {

    private static final String TAB1 = "tab1";
    private static final String TAB2 = "tab2";
    private static final String TAB3 = "tab3";

    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2,fab3;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private RelativeLayout fab_background;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.content)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.navi_fragment)
    RelativeLayout naviFragment;

    @BindView(R.id.tab)
    TabLayout tabs;

    @BindView(R.id.rv_list)
    RecyclerView listView;

    ActionBarDrawerToggle mDrawerToggle;
    ChattingListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                float xPositionOpenDrawer = naviFragment.getWidth();
                float xPositionWindowContent = (slideOffset * xPositionOpenDrawer);
                coordinatorLayout.setTranslationX(xPositionWindowContent);
            }
        };
        drawer.addDrawerListener(mDrawerToggle);
        fab_background = (RelativeLayout)findViewById(R.id.fab_background);
        fab_background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fab3 = (FloatingActionButton)findViewById(R.id.fab3);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        tabs.addTab(tabs.newTab().setText("요청하기").setTag(TAB1).setIcon(R.drawable.btn_tab01_icon));
        tabs.addTab(tabs.newTab().setText("배송하기").setTag(TAB2).setIcon(R.drawable.btn_tab02_icon));
        tabs.addTab(tabs.newTab().setText("마이페이지").setTag(TAB3).setIcon(R.drawable.btn_tab03_icon));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:{
                        Intent intent = new Intent(MainActivity.this, SendActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 1:{
                        Intent intent = new Intent(MainActivity.this, DelivererActivity.class);
                        startActivity(intent);
                        break;
                    }

                    case 2:{
                        Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                        startActivity(intent);
                        break;
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:{
                        Intent intent = new Intent(MainActivity.this, SendActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 1:{
                        Intent intent = new Intent(MainActivity.this, DelivererActivity.class);
                        startActivity(intent);
                        break;
                    }

                    case 2:{
                        Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });

        initData();
    }

    ChattingListData data;
    ArrayList<ChattingListData> list;
    private void initData() {
        OtherUserRequest request = new OtherUserRequest(this, "3");
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_SECURE, request, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                if (result.getResult() != null) {
                    data = new ChattingListData();
                    list = new ArrayList<>();
                    list.add(data);
                    data.setImageUrl(result.getResult().getFileUrl());
                    data.setName(result.getResult().getName());
                    data.setType(ChattingListData.TYPE_DELIVERER);
                    mAdapter = new ChattingListAdapter();
                    mAdapter.setData(list);
                    listView.setAdapter(mAdapter);
                    listView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
                }
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result, String errorMessage, Throwable e) {

            }
        });
    }


    public void animateFAB(){

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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isFabOpen) {
            animateFAB();
        } else {
            super.onBackPressed();
        }
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
                    PropertyManager.getInstance().setFacebookId("");
                    PropertyManager.getInstance().setUserData(null);
                    Toast.makeText(MainActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                    intent.putExtra(SplashActivity.FACEBOOK_LOGOUT, "facebooklogout");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("정말 SENDER를\n떠나시는 건가요?\n\n탈퇴하시면 SENDER에서의 기록이 모두 삭제됩니다");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UserLeaveRequest request = new UserLeaveRequest(MainActivity.this, PropertyManager.getInstance().getUserData().getUser_id());
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<String>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<String>> request, NetworkResult<String> result) {
                        if (!TextUtils.isEmpty(result.getResult())) {
                            Toast.makeText(MainActivity.this, "정상적으로 탈퇴되었습니다", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
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
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}
