package com.twitter.api.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

import com.twitter.api.dialogs.TwitterDialog;
import com.twitter.api.oauth.OauthAccessToken;
import com.twitter.api.oauth.OauthConsumer;
import com.twitter.api.oauth.OauthProvider;
import com.twitter.api.oauth.OauthUtil;

import java.net.URL;

/**
 * Main twitter android lib interface.
 *
 * @author Lorensius W. L. T <lorenz@londatiga.ent>
 * Modified By: David Castillo Fuentes <dcaf82@gmail.com>
 *
 */
public class Twitter {
    private Context mContext;

    private SharedPreferences mSharedPref;
    private Editor mEditor;

    private OauthConsumer mConsumer;
    private OauthAccessToken mAccessToken;
    private OauthProvider mProvider;

    private SigninListener mListener;

    private RequestTokenTask mRequestTokenTask;
    private AccessTokenTask mAccesTokenTask;

    private boolean mIsLoading = false;

    private static final String SHARED_PREF = "Android_Twitter_Preferences";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String ACCESS_TOKEN_SECRET = "access_token_secret";

    public Twitter(Context context, String clientId, String clientSecret, String callbackUrl, boolean signinOnly) {
        init(context, clientId, clientSecret, callbackUrl, signinOnly);
    }

    public Twitter(Context context, String clientId, String clientSecret, String callbackUrl) {
        init(context, clientId, clientSecret, callbackUrl, false);
    }

    public OauthConsumer getConsumer() {
        return mConsumer;
    }

    public OauthAccessToken getAccessToken() {
        return mAccessToken;
    }

    public boolean sessionActive() {
        return (mAccessToken == null) ? false : true;
    }

    public void clearSession() {
        mEditor.putString(ACCESS_TOKEN, null);
        mEditor.putString(ACCESS_TOKEN_SECRET, null);
        mEditor.commit();
    }

    public void signin(SigninListener listener) {
        mListener = listener;
        (mRequestTokenTask = new RequestTokenTask()).execute();
    }

    public void cancel() {
        if (mIsLoading) {
            if (mAccesTokenTask != null) {
                mAccesTokenTask.cancel(true);
            }

            if (mRequestTokenTask == null) {
                mRequestTokenTask.cancel(true);
            }
        }
    }

    private void init(Context context, String clientId, String clientSecret, String callbackUrl, boolean signinOnly) {
        String url = (signinOnly) ? OauthUtil.OAUTH_AUTHENTICATION_URL : OauthUtil.OAUTH_AUTHORIZATION_URL;

        mContext = context;

        mConsumer = new OauthConsumer(clientId, clientSecret, callbackUrl);
        mProvider = new OauthProvider(mConsumer, OauthUtil.OAUTH_REQUEST_URL, url, OauthUtil.OAUTH_ACCESSTOKEN_URL);

        mSharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        mEditor = mSharedPref.edit();

        loadAccessToken();
    }

    private void loadAccessToken() {
        String token = mSharedPref.getString(ACCESS_TOKEN, null);
        String tokenSecret = mSharedPref.getString(ACCESS_TOKEN_SECRET, null);

        if (token != null && tokenSecret != null) {
            mAccessToken = new OauthAccessToken(token, tokenSecret);
        }
    }

    private void storeAccessToken(String token, String tokenSecret) {
        mEditor.putString(ACCESS_TOKEN, token);
        mEditor.putString(ACCESS_TOKEN_SECRET, tokenSecret);

        mEditor.commit();
    }

    private void openAuthorizationDialog(final String url) {
        TwitterDialog dialog = new TwitterDialog(mContext,
                mConsumer.getCallbackUrl(),
                url, new TwitterDialog.TwDialogListener() {

            @Override
            public void onError(String value) {
                mListener.onError("Authorization failed");
            }

            @Override
            public void onComplete(String verifier) {
                (mAccesTokenTask = new AccessTokenTask(verifier)).execute();
            }
        });

        dialog.show();
    }

    public class RequestTokenTask extends AsyncTask<URL, Integer, Long> {
        ProgressDialog progressDlg;

        String authUrl ="";

        public RequestTokenTask() {
            progressDlg = new ProgressDialog(mContext);
            progressDlg.setMessage("Signing in Twitter...");
            progressDlg.setCancelable(true);
        }

        protected void onCancelled() {
            progressDlg.dismiss();
            mIsLoading = false;
        }

        protected void onPreExecute() {
            progressDlg.show();
            mIsLoading = true;
        }

        protected Long doInBackground(URL... urls) {
            long result = 0;

            try {
                authUrl = mProvider.getAuthorizationUrl();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
            mIsLoading = false;

            progressDlg.dismiss();

            if (!authUrl.equals("")) {
                openAuthorizationDialog(authUrl);
            } else {
                mListener.onError("Failed to get request token");
            }
        }
    }

    public class AccessTokenTask extends AsyncTask<URL, Integer, Long> {
        ProgressDialog progressDlg;

        String verifier="";

        OauthAccessToken accessToken;

        public AccessTokenTask(String verifier) {
            this.verifier = verifier;
            progressDlg = new ProgressDialog(mContext);
            progressDlg.setMessage("Signing in Twitter...");
            progressDlg.setCancelable(true);
        }

        protected void onCancelled() {
            progressDlg.dismiss();
            mIsLoading = false;
        }

        protected void onPreExecute() {
            progressDlg.show();
            mIsLoading = true;
        }

        protected Long doInBackground(URL... urls) {
            long result = 0;

            try {
                accessToken = mProvider.retrieveAccessToken(verifier);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
            mIsLoading = false;

            progressDlg.dismiss();

            if (accessToken != null) {
                mAccessToken = accessToken;

                storeAccessToken(accessToken.getToken(), accessToken.getSecret());

                mListener.onSuccess(accessToken, mProvider.getUserId(), mProvider.getScreenName());
            } else {
                mListener.onError("Failed to get access token");
            }
        }
    }

    public interface SigninListener {
        void onSuccess(OauthAccessToken accessToken, String userId, String screenName);
        void onError(String error);
    }
}
