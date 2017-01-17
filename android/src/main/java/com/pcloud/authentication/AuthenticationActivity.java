/*
 * Copyright (c) 2017 pCloud AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pcloud.authentication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pcloud.R;

import static com.pcloud.authentication.Constants.ACCESS_TOKEN;
import static com.pcloud.authentication.Constants.API_CALL_URI;
import static com.pcloud.authentication.Constants.API_KEY;
import static com.pcloud.authentication.Constants.REDIRECT_URI;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String TAG = AuthenticationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        WebView webView = (WebView) findViewById(R.id.webView);


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
                if (url.contains(ACCESS_TOKEN)) {
                    int start = url.indexOf("=");
                    int end = url.indexOf("&");
                    String token = url.substring(start, end);
                    setActivityResult(token);
                    finish();
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        };
        webView.setWebViewClient(wc);
        String url = buildUrl();
        webView.loadUrl(url);
    }

    private void setActivityResult(String token) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(ACCESS_TOKEN, token);
        setResult(RESULT_OK, resultIntent);
    }

    private String buildUrl() {
        String apiKey = getIntent().getStringExtra(API_KEY);
        String redirectUri = getIntent().getStringExtra(REDIRECT_URI);
        StringBuilder builder = new StringBuilder()
                .append(API_CALL_URI)
                .append(apiKey).append("&")
                .append(REDIRECT_URI).append("=").append(redirectUri);
        return builder.toString();
    }

}
