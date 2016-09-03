package com.sender.team.sender;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import butterknife.ButterKnife;
import butterknife.OnClick;


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
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        callbackManager = CallbackManager.Factory.create();
        mLoginManager = LoginManager.getInstance();

        return view;
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


    @OnClick(R.id.btn_naver)
    public void onClickNaverLogin() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new TermsFragment()).commit();
    }

    @OnClick(R.id.btn_facebook)
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

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
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
