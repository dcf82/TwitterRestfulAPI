package com.twitter.api.http;

import android.content.ContentValues;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.twitter.api.config.Definitions;
import com.twitter.api.oauth.OauthAccessToken;
import com.twitter.api.oauth.OauthConsumer;
import com.twitter.api.oauth.OauthHeader;
import com.twitter.api.oauth.OauthSignature;
import com.twitter.api.oauth.OauthUtil;
import com.twitter.api.util.Debug;
import com.twitter.api.util.OkHttpUtil;

import java.util.Set;

/**
 * Http connection with Twiter API.
 *
 * @author Lorensius W. L. T <lorenz@londatiga.ent>
 * Modified By: David Castillo Fuentes <dcaf82@gmail.com>
 *
 */
public class HttpConn {
    private OauthConsumer mOauthConsumer;
    private OauthAccessToken mAccessToken;

    public HttpConn(OauthConsumer consumer, OauthAccessToken acessToken) {
        mOauthConsumer = consumer;
        mAccessToken = acessToken;
    }

    public String connectPost(String requestUri, ContentValues params) throws Exception {

        try {
            HttpParams httpParams = new HttpParams();

            String nonce = OauthUtil.createNonce();
            String timestamp = OauthUtil.getTimeStamp();

            httpParams.put("oauth_consumer_key", new HttpValues(mOauthConsumer.getConsumerKey()));
            httpParams.put("oauth_nonce", new HttpValues(nonce));
            httpParams.put("oauth_signature_method", new HttpValues(OauthUtil.SIGNATURE_METHOD));
            httpParams.put("oauth_timestamp", new HttpValues(timestamp));
            httpParams.put("oauth_token", new HttpValues(mAccessToken.getToken()));
            httpParams.put("oauth_version", new HttpValues(OauthUtil.OAUTH_VERSION));

            if (params != null) {
                Set<String> ketSet = params.keySet();
                for (String param : ketSet) {
                    httpParams.put(param, new HttpValues(params.get(param).toString()));
                }
            }

            OauthSignature reqSignature	= new OauthSignature();

            String sigBase = reqSignature.createSignatureBase(Definitions.POST, requestUri, httpParams.getQueryString());
            String signature = reqSignature.createRequestSignature(sigBase, mOauthConsumer.getConsumerSecret(),
                    mAccessToken.getSecret());

            String authHeader = OauthHeader.buildRequestHeader(
                    mOauthConsumer.getConsumerKey(),
                    nonce,
                    signature,
                    OauthUtil.SIGNATURE_METHOD,
                    timestamp,
                    mAccessToken.getToken(),
                    OauthUtil.OAUTH_VERSION);

            Debug.i("Signature base " + sigBase);
            Debug.i("Signature " + signature);

            Debug.i("POST " + requestUri);
            Debug.i("Authorization " + authHeader);

            OkHttpClient client = new OkHttpClient();
            RequestBody body = OkHttpUtil.buildRequestBody(params);
            Request request = new Request.Builder()
                    .url(requestUri)
                    .post(body)
                    .addHeader("Authorization", authHeader)
                    .build();
            Response res = client.newCall(request).execute();
            if (res.isSuccessful()) {
                return res.body().string();
            }

        } catch (Exception e) {
            Debug.e("Error :: " + e);
        }
        return null;
    }

    public String connectGet(String requestUri, ContentValues params) throws Exception {
        try {
            HttpParams httpParams = new HttpParams();

            String nonce = OauthUtil.createNonce();
            String timestamp = OauthUtil.getTimeStamp();

            httpParams.put("oauth_consumer_key", new HttpValues(mOauthConsumer.getConsumerKey()));
            httpParams.put("oauth_nonce", new HttpValues(nonce));
            httpParams.put("oauth_signature_method", new HttpValues(OauthUtil.SIGNATURE_METHOD));
            httpParams.put("oauth_timestamp", new HttpValues(timestamp));
            httpParams.put("oauth_token", new HttpValues(mAccessToken.getToken()));
            httpParams.put("oauth_version", new HttpValues(OauthUtil.OAUTH_VERSION));

            String requestUrl = requestUri;

            if (params != null) {
                StringBuilder requestParamSb = new StringBuilder();
                Object[] ketSet = params.keySet().toArray();

                for (int i = 0; i < ketSet.length; i++) {
                    String param = ketSet[i].toString();
                    String value = params.get(param).toString();
                    httpParams.put(param, new HttpValues(value));
                    requestParamSb.append(param + "=" + value + ((i != ketSet.length - 1) ? "&" : ""));
                }

                String requestParam = requestParamSb.toString();

                requestUrl = requestUri + ((requestUri.contains("?")) ? "&" + requestParam : "?" + requestParam);
            }

            OauthSignature reqSignature	= new OauthSignature();

            String sigBase = reqSignature.createSignatureBase(Definitions.GET, requestUri,
                    httpParams.getQueryString());
            String signature = reqSignature.createRequestSignature(sigBase, mOauthConsumer.getConsumerSecret(),
                    mAccessToken.getSecret());

            String authHeader = OauthHeader.buildRequestHeader(
                    mOauthConsumer.getConsumerKey(),
                    nonce,
                    signature,
                    OauthUtil.SIGNATURE_METHOD,
                    timestamp,
                    mAccessToken.getToken(),
                    OauthUtil.OAUTH_VERSION);

            Debug.i("Signature base " + sigBase);
            Debug.i("Signature " + signature);

            Debug.i("GET " + requestUrl);
            Debug.i("Authorization " + authHeader);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .get()
                    .url(requestUrl)
                    .addHeader("Authorization", authHeader)
                    .build();

            Response res = client.newCall(request).execute();
            if (res.isSuccessful()) {
                return res.body().string();
            }
        } catch (Exception e) {
            Debug.e("Error :: " + e);
        }
        return null;
    }
}
