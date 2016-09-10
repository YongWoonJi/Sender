package com.sender.team.sender;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
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
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.data.UserData;
import com.sender.team.sender.gcm.RegistrationIntentService;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.manager.PropertyManager;
import com.sender.team.sender.request.FacebookRequest;
import com.sender.team.sender.request.MyPageRequest;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    public final static String FACEBOOK_LOGOUT = "facebooklogout";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    LoginManager loginManager;
    CallbackManager callbackManager;

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        mHandler = new Handler(Looper.getMainLooper());

        String code = getIntent().getStringExtra(FACEBOOK_LOGOUT);
        if (code != null) {
            if (code.equals("facebooklogout")) {
                loginManager.logOut();
            }
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loginAndMoveMain();
            }
        };
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkRegistrationId();
            }
        }, 1500);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST && resultCode == Activity.RESULT_OK) {
            loginAndMoveMain();
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
            dialog = builder.create();
            dialog.show();
            return false;
        }
        return true;
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
                    if (result.getError().equals("0")) {
                        loginSharedPreference();
                    }
                }
            });
        }
    }

    private void moveMainActivity() {
        MyPageRequest request = new MyPageRequest(this);
        NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<UserData>>() {
            @Override
            public void onSuccess(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result) {
                PropertyManager.getInstance().setUserData(result.getResult());
                UserData user = (UserData) getIntent().getSerializableExtra(ChattingActivity.EXTRA_USER);
                if (user == null) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    Intent chatIntent = new Intent(SplashActivity.this, ChattingActivity.class);
                    chatIntent.putExtra(ChattingActivity.EXTRA_USER, user);
                    Intent[] intents = {mainIntent, chatIntent};
                    startActivities(intents);
                }
                finish();
            }

            @Override
            public void onFail(NetworkRequest<NetworkResult<UserData>> request, NetworkResult<UserData> result, String errorMessage, Throwable e) {

            }
        });
    }

    private void moveSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        // 만약 현재 정보를 intent에 추가해야하면 putExtra 코드 추가해야함
        startActivity(intent);
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
                        moveMainActivity();
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
                                moveMainActivity();
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
                        if (result != null) {
                            if (result.getResult() == 0) {
                                moveSignUpActivity();
                            } else if (result.getResult() == 1) {
                                PropertyManager.getInstance().setFacebookId(token.getUserId());
                                moveMainActivity();
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

    @OnClick(R.id.btn_naver)
    public void onClickNaver() {
        moveSignUpActivity();
    }

    @OnClick(R.id.btn_logout)
    public void onClickLogout() {
        loginManager.logOut();
    }

    private void setFacebookId() {

    }

}
