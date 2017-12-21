package es.academy.solidgear.surveyx.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import es.academy.solidgear.surveyx.R;
import es.academy.solidgear.surveyx.managers.NetworkManager;
import es.academy.solidgear.surveyx.managers.SharedPrefsManager;
import es.academy.solidgear.surveyx.model.LoginModel;
import es.academy.solidgear.surveyx.services.requests.UserLoginRequest;
import es.academy.solidgear.surveyx.ui.fragments.ErrorDialogFragment;

/**
 * Created by Siro on 10/12/2014.
 */
public class LoginActivity extends BaseActivity {
    private static final String AUTH_ERROR = "com.android.volley.AuthFailureError";

    private ProgressBar mProgressBar;
    private Button mLoginButton;
    private EditText mUsername;
    private EditText mPassword;
    private AppCompatCheckBox mRemember;
    private SharedPrefsManager mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        mSharedPref = SharedPrefsManager.getInstance(this);

        String token = rememberMeGetToken();
        if (token != null){
            openMainActivity(token);
            finish();
        }

        mProgressBar = (ProgressBar) findViewById(R.id.progressBarLogin);
        mUsername = (EditText) findViewById(R.id.userLoginText);
        mPassword = (EditText) findViewById(R.id.passLoginText);
        mLoginButton = (Button) findViewById(R.id.login_button);
        TextView sgLoginText = (TextView) findViewById(R.id.sgLoginText);
        mRemember = (AppCompatCheckBox) findViewById(R.id.rememberCheckBox);


        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/KGSecondChancesSketch.ttf");
        sgLoginText.setTypeface(tf);
        mLoginButton.setTypeface(tf);

        Typeface tfMuseum = Typeface.createFromAsset(getAssets(), "fonts/Museo300-Regular.otf");
        mUsername.setTypeface(tfMuseum);
        mPassword.setTypeface(tfMuseum);
        if(hayLoginAnterior()){
            mUsername.setText(SharedPrefsManager.getInstance(this).getString("ULTIMOUSUARIO"));
            mPassword.requestFocus();
        }



        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
    }

    private void doLogin() {
        final String username = mUsername.getText().toString();

        showLoginInProgress();
        Response.Listener<LoginModel> onLoginSuccess = new Response.Listener<LoginModel>() {
            @Override
            public void onResponse(LoginModel response) {
                String token = response.getToken();
                if (mRemember.isChecked())
                    rememberMeSetToken(token);
                else
                    rememberMeSetToken(null);
                openMainActivity(token);
                SharedPrefsManager.getInstance(LoginActivity.this).putString("ULTIMOUSUARIO", username);
                finish();
            }
        };

        Response.ErrorListener onLoginError = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                rememberMeSetToken(null);
                if (error.toString().equals(AUTH_ERROR)) {
                    showAuthenticationError();
                    focusRequestToUsername();
                    cleanIfFail();
                } else {
                    ErrorDialogFragment.OnClickClose onClickClose = new ErrorDialogFragment.OnClickClose() {
                        @Override
                        public void onClickClose() {

                        }
                    };
                    showGenericError(error.toString(), onClickClose);
                }
                hideLoginInProgress();
            }
        };


        String password = mPassword.getText().toString();
        UserLoginRequest request = new UserLoginRequest(username,
                password,
                onLoginSuccess,
                onLoginError);



        NetworkManager.getInstance(this).makeRequest(request);
    }

    private void cleanIfFail() {
        mPassword.setText("");
        mUsername.setText("");
    }

    private void focusRequestToUsername() {
        mUsername.requestFocus();
    }

    private void showLoginInProgress() {
        mLoginButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoginInProgress() {
        mLoginButton.setEnabled(true);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void openMainActivity(String token) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_TOKEN, token);
        startActivity(intent);
    }

    private void showAuthenticationError() {
        Toast.makeText(LoginActivity.this, "Incorrect login", Toast.LENGTH_LONG).show();
    }


    private void rememberMeSetToken(String token) {
        mSharedPref.putString("rememberMeToken", token);
    }

    private String rememberMeGetToken() {
        return mSharedPref.getString("rememberMeToken");

    private boolean hayLoginAnterior() {
        if(SharedPrefsManager.getInstance(this).getString("ULTIMOUSUARIO") == null){
            return false;
        }
        return true;
    }
}
