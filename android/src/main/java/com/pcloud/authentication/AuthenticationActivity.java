/*
 * Copyright (c) 2017 pCloud AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pcloud.authentication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pcloud.R;

import static com.pcloud.authentication.Constants.API_KEY;
import static com.pcloud.authentication.Constants.REDIRECT_URI;

public class AuthenticationActivity extends Activity {

    private static final String TAG = AuthenticationActivity.class.getSimpleName();
    private final static String API_CALL_URI = "https://my.pcloud.com/oauth2/authorize?response_type=token";
    private final static String ACCESS_TOKEN = "access_token";
    private final static String AUTH_DATA = "auth_data";

    private WebView webView;
    private String redirectUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        webView = (WebView) findViewById(R.id.webView);

        WebSettings ws = webView.getSettings();
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        WebViewClient wc = new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, description + " " + errorCode);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(url.startsWith(redirectUri)){
                    if (url.contains(ACCESS_TOKEN)) {
                        int start = url.indexOf("=");
                        int end = url.indexOf("&");
                        String token = url.substring(start, end);
                        setSuccessResultAndFinish(token);
                    }else {
                        setFailedResultAndFinish(AuthData.Result.ACCESS_DENIED);
                    }
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        };
        webView.setWebViewClient(wc);
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            String url = buildUrl();
            webView.loadUrl(url);
        }

    }

    private void setSuccessResultAndFinish(String token) {
        Intent resultIntent = new Intent();
        AuthData data = new AuthData(token, AuthData.Result.OK);
        resultIntent.putExtra(AUTH_DATA, data);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void setFailedResultAndFinish(AuthData.Result result){
        Intent resultIntent = new Intent();
        AuthData data = new AuthData(null, result);
        resultIntent.putExtra(AUTH_DATA, data);
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }

    private String buildUrl() {
        String apiKey = getIntent().getStringExtra(API_KEY);
        redirectUri = getIntent().getStringExtra(REDIRECT_URI);
        Uri.Builder builder = Uri.parse(API_CALL_URI).buildUpon()
                .appendQueryParameter(API_KEY, apiKey)
                .appendQueryParameter(REDIRECT_URI, redirectUri);

        return builder.toString();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        setFailedResultAndFinish(AuthData.Result.CANCELED);
    }
}
