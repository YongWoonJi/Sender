package com.sender.team.sender;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.gcm.RegistrationIntentService;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.FacebookRequest;
import com.sender.team.sender.request.MyPageRequest;
import com.sender.team.sender.request.NaverRequest;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sender.team.sender.SendActivity.ACTION_SEND;
import static com.sender.team.sender.gcm.MyGcmListenerService.ACTION_REJECT;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.btn_facebook)
    Button btnFacebook;

    @BindView(R.id.btn_naver)
    Button btnNaver;

    @BindView(R.id.image_logo)
    ImageView imageLogo;

    @BindView(R.id.image_cube_small)
    ImageView imageCubeSmall;

    @BindView(R.id.image_cube_big)
    ImageView imageCubeBig;


    public static final String FACEBOOK_LOGOUT = "facebooklogout";
    public static final String NAVER_LOGOUT = "naverlogout";
    public static final String FACEBOOK_LEAVE = "facebookleave";
    public static final String NAVER_LEAVE = "naverleave";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int RC_PERMISSION = 100;
    private boolean isPermissionGranted;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

/////////////////////////////////네이버
    private static String OAUTH_CLIENT_ID = "4D9YQ2XzAzTq1hA7B5P9";
    private static String OAUTH_CLIENT_SECRET = "ImY1ls7Vfr";
    private static String OAUTH_CLIENT_NAME = "SENDER";
    private static OAuthLogin mOAuthLoginInstance ;


    LoginManager loginManager;
    CallbackManager callbackManager;

    Handler mHandler;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            checkRegistrationId();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);


        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        mOAuthLoginInstance = OAuthLogin.getInstance();
        mOAuthLoginInstance.init(
                SplashActivity.this
                ,OAUTH_CLIENT_ID
                ,OAUTH_CLIENT_SECRET
                ,OAUTH_CLIENT_NAME
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        );

        mHandler = new Handler(Looper.getMainLooper());

        String code = getIntent().getStringExtra(FACEBOOK_LOGOUT);
        if (code != null) {
            if (code.equals(FACEBOOK_LOGOUT)) {
                loginManager.logOut();
            }
        } else if ((code = getIntent().getStringExtra(NAVER_LOGOUT)) != null) {
            if (code.equals(NAVER_LOGOUT)) {
                mOAuthLoginInstance.logoutAndDeleteToken(this);
            }
        } else if ((code = getIntent().getStringExtra(FACEBOOK_LEAVE)) != null) {
            if (code.equals(FACEBOOK_LEAVE)) {
                loginManager.logOut();
            }
        } else if ((code = getIntent().getStringExtra(NAVER_LEAVE)) != null) {
            if (code.equals(NAVER_LEAVE)) {
                mOAuthLoginInstance.logoutAndDeleteToken(this);
            }
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loginAndMoveMain();
            }
        };
        mHandler.postDelayed(runnable, 1500);
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkRegistrationId();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacks(runnable);
        NetworkManager.getInstance().cancelAll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST && resultCode == Activity.RESULT_OK) {
            loginAndMoveMain();
            return;
        } else if (requestCode == RC_SIGN_UP_CANCELED && resultCode == Activity.RESULT_CANCELED) {
            mOAuthLoginInstance.logoutAndDeleteToken(this);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    AlertDialog dialog;
    private boolean enableGPSSetting() {
        ContentResolver res = getContentResolver();
        boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(res, LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("GPS가 필요한 서비스입니다.\nGPS를 켜시겠습니까?");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setCancelable(false);
            dialog = builder.create();
            dialog.show();
            return false;
        }

        requestPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return isPermissionGranted;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            }
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, RC_PERMISSION);
        } else {
            isPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSION) {
            if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = true;
                loginAndMoveMain();
            } else {
                isPermissionGranted = false;
                finish();
            }
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Dialog dialog = apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                });
                dialog.show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private void checkRegistrationId() {
        if (checkPlayServices()) {
            String regId = PropertyManager.getInstance().getRegistrationId();
            if (!regId.equals("")) {
                loginAndMoveMain();
            } else {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }
    }

    private void loginAndMoveMain() {
        if (enableGPSSetting()) {
            MyPageRequest request = new MyPageRequest(this);
            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
                @Override
                public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                    if (result.getResult() != null) {
                        PropertyManager.getInstance().setUserData(result.getResult());
                        moveMainActivity();
                    } else {
                        loginSharedPreference();
                    }
                }

                @Override
                public void onFail(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result, String errorMessage, Throwable e) {
//                    if (result.getError().equals("0")) {
                        loginSharedPreference();
//                    }
                }
            });
        }
    }

    private void moveMainActivity() {
        UserData user = (UserData) getIntent().getSerializableExtra(ChattingActivity.EXTRA_USER);
        if (user == null) {
            Intent intent = getIntent();
            String reject = intent.getStringExtra(ACTION_REJECT);
            if (!TextUtils.isEmpty(reject)) {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                Intent sendIntent = new Intent(SplashActivity.this, SendActivity.class);
                sendIntent.putExtra(ACTION_SEND, "send");
                Intent[] intents = {mainIntent, sendIntent};
                startActivities(intents);
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        } else {
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            Intent chatIntent = new Intent(SplashActivity.this, ChattingActivity.class);
            chatIntent.putExtra(ChattingActivity.EXTRA_USER, user);
            Intent[] intents = {mainIntent, chatIntent};
            startActivities(intents);
        }
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static final int RC_SIGN_UP_CANCELED = 9;
    private void moveSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        // 만약 현재 정보를 intent에 추가해야하면 putExtra 코드 추가해야함
        startActivityForResult(intent, RC_SIGN_UP_CANCELED);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void setLoginDisplay() {
        if (btnFacebook.getVisibility() == View.GONE && btnNaver.getVisibility() == View.GONE) {
            btnFacebook.setVisibility(View.VISIBLE);
            btnNaver.setVisibility(View.VISIBLE);

            Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.activity_splash_cube_fade_out);
            imageCubeBig.startAnimation(animation);
            animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.activity_splash_cube_fade_in);
            imageCubeSmall.setVisibility(View.VISIBLE);
            imageCubeSmall.startAnimation(animation);
            btnFacebook.startAnimation(animation);
            btnNaver.startAnimation(animation);

            animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.activity_splash_logo_slide_down);
            imageLogo.startAnimation(animation);
        }
    }

    private void loginSharedPreference() {
        if (isFacebookLogin()) {
            processFacebookLogin();
        } else if (isNaverLogin()) {
            processNaverLogin();
        } else {
            setLoginDisplay();
        }
    }

    private boolean isFacebookLogin() {
        if (!TextUtils.isEmpty(PropertyManager.getInstance().getFacebookId())) {
            return true;
        }
        return false;
    }

    private boolean isNaverLogin() {
        if (!TextUtils.isEmpty(PropertyManager.getInstance().getNaverToken())) {
            return true;
        }
        return false;
    }

    private void processNaverLogin() {
        String accessToken = mOAuthLoginInstance.getAccessToken(this);
        if (accessToken != null) {
            if (!accessToken.equals(PropertyManager.getInstance().getNaverToken())) {
                resetNaverAndSetLoginDisplay();
            }
            NaverRequest request = new NaverRequest(this, accessToken, PropertyManager.getInstance().getRegistrationId());
            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<Integer>>() {
                @Override
                public void onSuccess(NetworkRequest<NetworkResult<Integer>> request, NetworkResult<Integer> result) {
                    if (result.getResult() == 0) {
                        moveSignUpActivity();
                    } else if (result.getResult() == 1) {
                        loginAndMoveMain();
                    } else {
                        resetNaverAndSetLoginDisplay();
                    }
                }

                @Override
                public void onFail(NetworkRequest<NetworkResult<Integer>> request, NetworkResult<Integer> result, String errorMessage, Throwable e) {
                    mOAuthLoginInstance.logoutAndDeleteToken(SplashActivity.this);
                    setLoginDisplay();
                }
            });
        } else {
            setLoginDisplay();
        }

    }


    private void resetNaverAndSetLoginDisplay() {
        mOAuthLoginInstance.logoutAndDeleteToken(this);
        PropertyManager.getInstance().setNaverToken("");
        setLoginDisplay();
    }

    private void processFacebookLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken != null) {
            if (!accessToken.getUserId().equals(PropertyManager.getInstance().getFacebookId())) {
                resetFacebookAndSetLoginDisplay();
                return;
            }
            String token = accessToken.getToken();
            String regId = PropertyManager.getInstance().getRegistrationId();
            FacebookRequest request = new FacebookRequest(this, token, regId);
            NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<Integer>>() {
                @Override
                public void onSuccess(NetworkRequest<NetworkResult<Integer>> request, NetworkResult<Integer> result) {
                    if (result.getResult() == 0) {
                        // 등록이 안된 사용자
                        moveSignUpActivity();
                    } else if (result.getResult() == 1) {
                        loginAndMoveMain();
                    } else {
                        resetFacebookAndSetLoginDisplay();
                    }
                }

                @Override
                public void onFail(NetworkRequest<NetworkResult<Integer>> request, NetworkResult<Integer> result, String errorMessage, Throwable e) {
                    loginManager.logOut();
//                    facebookLogin();
                    setLoginDisplay();
                }
            });
        } else {
//            facebookLogin();
            setLoginDisplay();
        }
    }


    private void resetFacebookAndSetLoginDisplay() {
        loginManager.logOut();
        PropertyManager.getInstance().setFacebookId("");
        setLoginDisplay();
    }

    private void facebookLogin() {
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken token = AccessToken.getCurrentAccessToken();
                if (!token.getUserId().equals(PropertyManager.getInstance().getFacebookId())) {
                    resetFacebookAndSetLoginDisplay();
                    return;
                }
                FacebookRequest request = new FacebookRequest(SplashActivity.this, token.getToken(), PropertyManager.getInstance().getRegistrationId());
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<Integer>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<Integer>> request, NetworkResult<Integer> result) {
                        if (result != null) {
                            if (result.getResult() == 0) {
                                // 등록이 안된 사용자
                                moveSignUpActivity();
                            } else if (result.getResult() == 1) {
                                PropertyManager.getInstance().setFacebookId(token.getUserId());
                                loginAndMoveMain();
                            } else {
                                resetFacebookAndSetLoginDisplay();
                            }
                        }
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<Integer>> request, NetworkResult<Integer> result, String errorMessage, Throwable e) {
                        resetFacebookAndSetLoginDisplay();
                    }
                });
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        loginManager.logInWithReadPermissions(this, Arrays.asList("email"));
    }


    @OnClick(R.id.btn_facebook)
    public void onClickFacebook() {
        loginManager.setDefaultAudience(DefaultAudience.FRIENDS);
        loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken token = AccessToken.getCurrentAccessToken();
                FacebookRequest request = new FacebookRequest(SplashActivity.this, token.getToken(), PropertyManager.getInstance().getRegistrationId());
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<Integer>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<Integer>> request, NetworkResult<Integer> result) {
                        if (result.getResult() != null) {
                            if (result.getResult() == 0) {
                                PropertyManager.getInstance().setFacebookId(token.getUserId());
                                moveSignUpActivity();
                            } else if (result.getResult() == 1) {
                                PropertyManager.getInstance().setFacebookId(token.getUserId());
                                loginAndMoveMain();
                            } else {
                                // 로그인 실패
                                Toast.makeText(SplashActivity.this, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                loginManager.logOut();
                            }
                        }
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<Integer>> request, NetworkResult<Integer> result, String errorMessage, Throwable e) {
                        Toast.makeText(SplashActivity.this, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        loginManager.logOut();
                    }
                });
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        loginManager.logInWithReadPermissions(this, Arrays.asList("email"));
    }

    //네이버 버튼 클릭하면 핸들러 호출
    @OnClick(R.id.btn_naver)
    public void onClickNaver() {
        mOAuthLoginInstance.startOauthLoginActivity(SplashActivity.this, mOAuthLoginHandler);
    }

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                final String accessToken = mOAuthLoginInstance.getAccessToken(SplashActivity.this);
                NaverRequest request = new NaverRequest(SplashActivity.this, accessToken, PropertyManager.getInstance().getRegistrationId());
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<Integer>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<Integer>> request, NetworkResult<Integer> result) {
                        if (result.getResult() != null) {
                            if (result.getResult() == 0) {
                                moveSignUpActivity();
                            } else if (result.getResult() == 1) {
                                PropertyManager.getInstance().setNaverToken(accessToken);
                                loginAndMoveMain();
                            } else {
                                Toast.makeText(SplashActivity.this, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                mOAuthLoginInstance.logoutAndDeleteToken(SplashActivity.this);
                            }
                        }
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<Integer>> request, NetworkResult<Integer> result, String errorMessage, Throwable e) {
                        Toast.makeText(SplashActivity.this, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        mOAuthLoginInstance.logoutAndDeleteToken(SplashActivity.this);
                    }
                });

            }
        }
    };

}
