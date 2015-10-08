package com.twitter.api.oauth;

import android.content.ContentValues;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.twitter.api.config.Definitions;
import com.twitter.api.http.HttpParams;
import com.twitter.api.http.HttpValues;
import com.twitter.api.util.Debug;
import com.twitter.api.util.OkHttpUtil;

/**
 * Oauth provider.
 *
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * Modified By: David Castillo Fuentes <dcaf82@gmail.com>
 *
 */
public class OauthProvider {
    private OauthConsumer mOauthConsumer;
    private OauthToken mOauthToken;
    private OauthAccessToken mAccessToken;

    private String mRequestTokenUrl;
    private String mAccessTokenUrl;
    private String mAuthorizationUrl;

    private String mScreenName = "";
    private String mUserId = "";

    public OauthProvider(OauthConsumer oauthConsumer, String requestTokenUrl,
                         String authorizationUrl, String accessTokenUrl) {
        mOauthConsumer = oauthConsumer;
        mRequestTokenUrl = requestTokenUrl;
        mAuthorizationUrl = authorizationUrl;
        mAccessTokenUrl = accessTokenUrl;
    }

    public String getAuthorizationUrl() throws Exception {
        String url = "";

        HttpParams httpParams = new HttpParams();

        OauthSignature reqSignature	= new OauthSignature();

        String nonce = OauthUtil.createNonce();
        String timestamp = OauthUtil.getTimeStamp();

        httpParams.put("oauth_callback", new HttpValues(mOauthConsumer.getCallbackUrl()));
        httpParams.put("oauth_consumer_key", new HttpValues(mOauthConsumer.getConsumerKey()));
        httpParams.put("oauth_nonce", new HttpValues(nonce));
        httpParams.put("oauth_signature_method", new HttpValues(OauthUtil.SIGNATURE_METHOD));
        httpParams.put("oauth_timestamp", new HttpValues(timestamp));
        httpParams.put("oauth_version", new HttpValues(OauthUtil.OAUTH_VERSION));

        try {
            String sigBase = reqSignature.createSignatureBase(Definitions.POST, mRequestTokenUrl,
                    httpParams.getQueryString());
            String signature = reqSignature.createRequestSignature(sigBase, mOauthConsumer.getConsumerSecret(), "");

            String authHeader = OauthHeader.buildRequestTokenHeader(
                    mOauthConsumer.getCallbackUrl(),
                    mOauthConsumer.getConsumerKey(),
                    nonce,
                    signature,
                    OauthUtil.SIGNATURE_METHOD,
                    timestamp,
                    OauthUtil.OAUTH_VERSION);

            Debug.i("Signature base " + sigBase);
            Debug.i("Signature " + signature);

            Debug.i("POST " + mRequestTokenUrl);
            Debug.i("Authorization " + authHeader);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(mRequestTokenUrl)
                    .method(Definitions.POST, null)
                    .addHeader("Authorization", authHeader)
                    .build();

            Response res = client.newCall(request).execute();
            String response = res.body().string();

            if (res.isSuccessful()) {
                processRequestToken(response);
                if (mOauthToken == null) {
                    throw new Exception("Failed to get request token");
                } else {
                    url  = mAuthorizationUrl + "?oauth_token=" + mOauthToken.getToken();
                }
            }
        } catch (Exception e) {
            Debug.e("Error :: " + e);
        }
        return url;
    }

    public OauthAccessToken retrieveAccessToken(String oauthVerifier) throws Exception {
        if (mOauthToken == null) {
            throw new Exception("Request token is empty, please call getAuthorizationUrl before calling this method");
        }

        HttpParams httpParams = new HttpParams();

        OauthSignature reqSignature = new OauthSignature();

        String nonce = OauthUtil.createNonce();
        String timestamp = OauthUtil.getTimeStamp();

        httpParams.put("oauth_verifier", new HttpValues(oauthVerifier));
        httpParams.put("oauth_consumer_key", new HttpValues(mOauthConsumer.getConsumerKey()));
        httpParams.put("oauth_nonce", new HttpValues(nonce));
        httpParams.put("oauth_signature_method", new HttpValues(OauthUtil.SIGNATURE_METHOD));
        httpParams.put("oauth_timestamp", new HttpValues(timestamp));
        httpParams.put("oauth_token", new HttpValues(mOauthToken.getToken()));
        httpParams.put("oauth_version", new HttpValues(OauthUtil.OAUTH_VERSION));

        try {
            String sigBase = reqSignature.createSignatureBase(Definitions.POST, mAccessTokenUrl,
                    httpParams.getQueryString());
            String signature = reqSignature.createRequestSignature(sigBase,
                    mOauthConsumer.getConsumerSecret(), mOauthToken.getSecret());

            String authHeader = OauthHeader.buildRequestHeader(
                    mOauthConsumer.getConsumerKey(),
                    nonce,
                    signature,
                    OauthUtil.SIGNATURE_METHOD,
                    timestamp,
                    mOauthToken.getToken(),
                    oauthVerifier,
                    OauthUtil.OAUTH_VERSION);

            Debug.i("Signature base " + sigBase);
            Debug.i("Signature " + signature);

            Debug.i("POST " + mAccessTokenUrl);
            Debug.i("Authorization " + authHeader);

            OkHttpClient client = new OkHttpClient();

            ContentValues values = new ContentValues();
            values.put("oauth_verifier", oauthVerifier);

            RequestBody body = OkHttpUtil.buildRequestBody(values);

            Request request = new Request.Builder()
                    .url(mAccessTokenUrl)
                    .post(body)
                    .addHeader("Authorization", authHeader)
                    .build();

            Response res = client.newCall(request).execute();
            String response = res.body().string();

            if (res.isSuccessful()) {
                processAccessToken(response);
            }
        } catch (Exception e) {
            Debug.e("Error :: " + e);
        }
        return mAccessToken;
    }

    public OauthToken getToken() {
        return mOauthToken;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public String getUserId() {
        return mUserId;
    }

    private void processRequestToken(String response) {
        if (response.contains("&")) {
            String arrs[] = response.split("&");
            int length = arrs.length;

            String token = "";
            String secret = "";
            boolean confr = true;

            for (int i = 0; i < length; i++) {
                String[] temp = arrs[i].split("=");

                if (temp[0].equals(OauthUtil.OAUTH_TOKEN)) {
                    token = temp[1];
                }

                if (temp[0].equals(OauthUtil.OAUTH_TOKEN_SECRET)) {
                    secret = temp[1];
                }

                if (temp[0].equals(OauthUtil.OAUTH_CALLBACK_CONFIRMED)) {
                    confr = (temp[1].equals("true")) ? true : false;
                }
            }

            if (!token.equals("") && !secret.equals("")) {
                mOauthToken = new OauthToken(token, secret, confr);
            } else {
                mOauthToken = null;
            }
        }
    }

    private void processAccessToken(String response) {
        if (response.contains("&")) {
            String arrs[] = response.split("&");
            int length = arrs.length;

            String token = "";
            String secret = "";

            for (int i = 0; i < length; i++) {
                String[] temp = arrs[i].split("=");

                if (temp[0].equals(OauthUtil.OAUTH_TOKEN)) {
                    token = temp[1];
                }

                if (temp[0].equals(OauthUtil.OAUTH_TOKEN_SECRET)) {
                    secret = temp[1];
                }

                if (temp[0].equals(OauthUtil.USER_ID)) {
                    mUserId = temp[1];
                }

                if (temp[0].equals(OauthUtil.SCREEN_NAME)) {
                    mScreenName = temp[1];
                }
            }

            if (!token.equals("") && !secret.equals("")) {
                mAccessToken = new OauthAccessToken(token, secret);
            } else {
                mAccessToken = null;
            }
        }
    }
}
