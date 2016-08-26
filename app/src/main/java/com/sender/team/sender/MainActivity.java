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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MenuAdapter.OnNaviMenuSelectedListener {

    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2,fab3;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private RelativeLayout fab_background;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.content)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.navi_fragment)
    RelativeLayout naviFragment;

    private float lastTranslate = 0.0f;

    private static final String TAB1 = "tab1";
    private static final String TAB2 = "tab2";
    private static final String TAB3 = "tab3";
    @BindView(R.id.tab)
    TabLayout tabs;
    @BindView(R.id.listview)
    ListView listView;

    ArrayAdapter<String> mAdapter;

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

        tabs.addTab(tabs.newTab().setText("요청하기").setTag(TAB1));
        tabs.addTab(tabs.newTab().setText("배송하기").setTag(TAB2));
        tabs.addTab(tabs.newTab().setText("마이페이지").setTag(TAB3));
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


        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);
        initData();

    }

    private void initData() {
        mAdapter.add("센더");
        mAdapter.add("딜리버러");
        mAdapter.add("딜리버러");
        mAdapter.add("센더");
        mAdapter.add("센더");
        mAdapter.add("딜리버러");
        mAdapter.add("센더");
        mAdapter.add("딜리버러");
        mAdapter.add("센더");
        mAdapter.add("센더");
        mAdapter.add("딜리버러");
        mAdapter.add("딜리버러");
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

    @OnItemClick(R.id.listview)
    public void onItemClick(int position, long id){

        if (mAdapter.getItem(position).equals("센더")){
            Intent intent = new Intent(MainActivity.this, ChattingActivity.class);
            intent.putExtra("key", 1);
            startActivity(intent);
        }else{
            Intent intent = new Intent(MainActivity.this, ChattingActivity.class);
            intent.putExtra("key", 2);
            startActivity(intent);
        }
    }


    @Override
    public void Logout() {
        Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void unregister() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("정말 SENDER를\n떠나시는 건가요?\n\n탈퇴하시면 SENDER에서의 기록이 모두 삭제됩니다");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "정상적으로 탈퇴되었습니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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
