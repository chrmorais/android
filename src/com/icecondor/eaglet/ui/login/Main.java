package com.icecondor.eaglet.ui.login;

import java.net.URI;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.icecondor.eaglet.Condor;
import com.icecondor.eaglet.Constants;
import com.icecondor.eaglet.R;
import com.icecondor.eaglet.ui.BaseActivity;
import com.icecondor.eaglet.ui.UiActions;
import com.icecondor.eaglet.ui.alist.MainActivity;

public class Main extends BaseActivity implements UiActions, OnEditorActionListener {
    public static String PREF_KEY_AUTHENTICATED_USER_ID = "icecondor_authenticated_user_id";
    LoginFragment loginFragment;
    private LoginFragmentEmail loginEmailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.APP_TAG, "login.Main onCreate");
        setContentView(R.layout.login);

        loginFragment = new LoginFragment();
        loginEmailFragment = new LoginFragmentEmail();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (savedInstanceState == null) {
            switchFragment(loginFragment);
            switchLoginFragment(loginEmailFragment);
        }
    }

    protected void switchLoginFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.login_body_fragment, fragment).commit();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        refreshStatusFromCondor(condor);
    }

    private void refreshStatusFromCondor(Condor condor) {
        if(condor.isConnecting()) {
            loginFragment.setStatusText("connecting... *");
        } else {
            loginIsOk();
        }

    }

    private void loginIsOk() {
        loginFragment.setStatusText("connected.");
        loginEmailFragment.enableLoginField();
    }

    /* UiActions */
    @Override
    public void onConnecting(URI uri) {
        Log.d(Constants.APP_TAG, "login.Main onConnecting");
        loginFragment.setStatusText("connecting...");
    }

    @Override
    public void onConnected() {
        Log.d(Constants.APP_TAG, "login.Main onConnected");
        loginIsOk();
    }

    @Override
    public void onDisconnected() {
        Log.d(Constants.APP_TAG, "login.Main onDisconnected");
        loginFragment.setStatusText("disconnected!");
    }

    @Override
    public void onNewActivity() {
    }

    @Override
    public void onTimeout() {
        Log.d(Constants.APP_TAG, "login.Main onTimeout");
    }

    /* Login email field */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(v.getId() == R.id.login_email_field) {
            if(actionId == EditorInfo.IME_ACTION_SEND) {
                Log.d(Constants.APP_TAG, "LoginFragment: action: "+actionId+" emailField "+v.getText());
                if(!condor.isConnecting()) {
                    condor.doAccountCheck(v.getText().toString());
                    startActivity(new Intent(this, MainActivity.class));
                }
            }
        }
        return false;
    }

}
