package com.sender.team.sender;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.sender.team.sender.data.NetworkResult;
import com.sender.team.sender.manager.NetworkManager;
import com.sender.team.sender.manager.NetworkRequest;
import com.sender.team.sender.request.FacebookRequest;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    CallbackManager callbackManager;
    LoginManager mLoginManager;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_login, container, false);
//        ButterKnife.bind(this, view);
//
//        callbackManager = CallbackManager.Factory.create();
//        mLoginManager = LoginManager.getInstance();
//        Button btn = (Button) view.findViewById(R.id.btn_facebook);
//
        return null;
    }

    AccessTokenTracker mTracker;
    @Override
    public void onStart() {
        super.onStart();
        if (mTracker == null) {
            mTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                }
            };
        } else {
            mTracker.startTracking();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        mTracker.stopTracking();
    }


//    @OnClick(R.id.btn_naver)
    public void onClickNaverLogin() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new TermsFragment()).commit();
    }
//
//    @OnClick(R.id.btn_facebook)
    public void onClickFacebookLogin() {
        if (isLogin()) {
            logoutFacebook();
        } else {
            loginFacebook();
        }
    }

    private boolean isLogin() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        return token != null;
    }

    private void loginFacebook() {
        mLoginManager.setDefaultAudience(DefaultAudience.FRIENDS);
        mLoginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        mLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken token = AccessToken.getCurrentAccessToken();
                FacebookRequest request = new FacebookRequest(getContext(), token.getToken(), "fPmhn6uzg1g:APA91bFIp2lQYqtiKc-KT8ARAI6PDJsFyp7ZGGhxP7WHS4sba6SLD0tVJOEpdMIcHPps5b6DNcKFpMSbW53u7YSVVj3LbYCCB5QW77bJEng1CTN0XD9uVZW3_rmf8Nl1hDUVd5UjtlLt");
                Log.i("LoginFragment",token.getToken());
                NetworkManager.getInstance().getNetworkData(NetworkManager.CLIENT_STANDARD, request, new NetworkManager.OnResultListener<NetworkResult<Integer>>() {
                    @Override
                    public void onSuccess(NetworkRequest<NetworkResult<Integer>> request, NetworkResult<Integer> result) {
                        if (result != null) {
                            Toast.makeText(getContext(), "로그인되었습니다", Toast.LENGTH_SHORT).show();
                            if (result.getResult() == 0) {
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.container, new TermsFragment()).commit();
                            } else if (result.getResult() == 1) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            // 로그인 실패
                            Toast.makeText(getContext(), "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(NetworkRequest<NetworkResult<Integer>> request, NetworkResult<Integer> result, String errorMessage, Throwable e) {
                        Toast.makeText(getContext(), "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        Log.i("LoginFragment Fail",errorMessage);
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
        mLoginManager.logInWithReadPermissions(this, Arrays.asList("email"));
    }

    private void logoutFacebook() {
        mLoginManager.logOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
