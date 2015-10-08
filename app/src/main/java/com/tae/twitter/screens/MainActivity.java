package com.tae.twitter.screens;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tae.twitter.R;
import com.tae.twitter.configs.Config;
import com.twitter.api.beans.TwitterUser;
import com.twitter.api.core.Twitter;
import com.twitter.api.core.TwitterRequest;
import com.twitter.api.oauth.OauthAccessToken;

/**
 * Login Activity Class
 *
 * @author David Castillo Fuentes <dcaf82@gmail.com>
 *
 */
public class MainActivity extends BaseActivity {
    private Twitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTwitter = new Twitter(this, Config.CONSUMER_KEY, Config.CONSUMER_SECRET, Config.CALLBACK_URL);

        if (mTwitter.sessionActive()) {
            startActivity(new Intent(this, UserActivity.class));
            finish();
        } else {
            findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    signinTwitter();
                }
            });
        }
    }

    private void signinTwitter() {
        mTwitter.signin(new Twitter.SigninListener() {
            @Override
            public void onSuccess(OauthAccessToken accessToken, String userId, String screenName) {
                if(isFinishing()) return;
                getCredentials();
            }

            @Override
            public void onError(String error) {
                if(isFinishing()) return;
                showToast(error);
            }
        });
    }

    private void getCredentials() {
        final ProgressDialog progressDlg = new ProgressDialog(this);
        progressDlg.setMessage(getString(R.string.credentials));
        progressDlg.setCancelable(false);
        progressDlg.show();

        TwitterRequest request = new TwitterRequest(mTwitter.getConsumer(), mTwitter.getAccessToken());

        request.verifyCredentials(new TwitterRequest.VerifyCredentialListener() {

            @Override
            public void onSuccess(TwitterUser user) {
                if(isFinishing()) return;
                progressDlg.dismiss();
                saveCredential(user.screenName, user.name, user.profileImageUrl);
                startActivity(new Intent(getActivity(), UserActivity.class));
                finish();
            }

            @Override
            public void onError(String error) {
                if(isFinishing()) return;
                progressDlg.dismiss();
                showToast(error);
            }
        });
    }
}
