/*
 * Copyright (c) 2017 pCloud AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pcloud.sdk;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Map;

/**
 * A utility {@link Activity} class for obtaining a OAuth 2.0 access token for PCloud's API
 * <p>
 * Start this activity to display to users a screen where they can grant permissions for your API application to access their accounts.
 * <p>
 * To obtain an access token by using this class the following steps must be done:
 * <ul>
 * <li>Create an {@link Intent} via the {@link #createIntent(Context, String, Uri)} method</li>
 * <li>Launch the intent via {@link Activity#startActivityForResult(Intent, int)} or {@link android.app.Fragment#startActivityForResult(Intent, int)} with an arbitrary request code.</li>
 * <li>Handle the result of the authentication process by listening on the {@link Activity#onActivityResult(int, int, Intent)}
 * or {@link android.app.Fragment#onActivityResult(int, int, Intent)} callbacks.
 * <li>Get a {@link AuthorizationResult} object from the returned {@link Intent} by calling {@link Intent#getSerializableExtra(String)} (String)} with {@link #KEY_AUTHORIZATION_RESULT}.</li>
 * <li>Refer to documentation of {@link AuthorizationResult} about the meaning of diferent values.</li>
 * <li>If the user granted permissions, a non-null access token will be available by calling {@link Intent#getStringExtra(String)} with {@link #KEY_ACCESS_TOKEN} on the returned {@link Intent}.</li>
 * <li>If the user granted permissions, a user identifier will be available by calling {@link Intent#getLongExtra(String, long)} with {@link #KEY_USER_ID} on the returned {@link Intent}.</li>
 * </ul>
 * <h3>
 * NOTE: Instances of this activity launched via {@link Intent} objects not created via the
 * {@link #createIntent(Context, String, Uri)} or
 * {@link #createIntent(Context, String)}
 * methods will throw an {@link IllegalStateException}.
 * </h3>
 *
 * @see AuthorizationResult
 */
public final class AuthorizationActivity extends Activity {

    public final static String KEY_AUTHORIZATION_RESULT = "com.pcloud.authentication.AuthorizationActivity.KEY_AUTHORIZATION_RESULT";
    public final static String KEY_ACCESS_TOKEN = "com.pcloud.authentication.AuthorizationActivity.KEY_ACCESS_TOKEN";
    public static final String KEY_USER_ID = "com.pcloud.authentication.AuthorizationActivity.KEY_USER_ID";

    public final static Uri DEFAULT_REDIRECT_URL = Uri.parse("https://oauth2redirect");

    private final static Uri BASE_AUTHORIZE_PAGE_URL = Uri.parse("https://my.pcloud.com/oauth2/authorize");
    private final static String ARGUMENT_API_KEY = "com.pcloud.authentication.AuthorizationActivity.ARGUMENT_API_KEY";
    private final static String ARGUMENT_REDIRECT_URI = "com.pcloud.authentication.AuthorizationActivity.ARGUMENT_REDIRECT_URI";

    /**
     * Create a new {@link Intent} for launching an instance of [@link {@link AuthorizationActivity}}.
     *
     * @param context The {@link Context} to be bound to the new intent. Must not be null.
     *                <p>
     *                <b>NOTE: If a non-{@link Activity} context is provided the resulting intent will have the {@link Intent#FLAG_ACTIVITY_NEW_TASK} flag set.</b>
     * @param apiKey  The API key for the application that will request access permission.
     *                <p>
     *                For more details on how to obtain a key, see <a href="https://docs.pcloud.com/oauth/index.html">here</a>.
     * @return a non-null {@link Intent} for launching an instance of [@link {@link AuthorizationActivity}}.
     * @see #createIntent(Context, String, Uri)
     */
    @SuppressWarnings("unused")
    public static Intent createIntent(@NonNull Context context, @NonNull String apiKey) {
        return createIntent(context, apiKey, null);
    }

    /**
     * Create a new {@link Intent} for launching an instance of [@link {@link AuthorizationActivity}}.
     *
     * @param context     The {@link Context} to be bound to the new intent. Must not be null.
     *                    <p>
     *                    <b>NOTE: If a non-{@link Activity} context is provided the resulting intent will have the {@link Intent#FLAG_ACTIVITY_NEW_TASK} flag set.</b>
     * @param apiKey      The API key for the application that will request access permission.
     *                    <p>
     *                    For more details on how to obtain a key, see <a href="https://docs.pcloud.com/oauth/index.html">here</a>.
     * @param redirectUrl An optional URL that will be called after successful authentication.
     *                    <p>
     *                    If set to null, {@link #DEFAULT_REDIRECT_URL} will be used.
     * @return a non-null {@link Intent} for launching an instance of [@link {@link AuthorizationActivity}}.
     */
    @SuppressWarnings("unused")
    public static Intent createIntent(@NonNull Context context, @NonNull String apiKey, @Nullable Uri redirectUrl) {
        //noinspection ConstantConditions
        if (apiKey == null) {
            throw new IllegalArgumentException("Context argument cannot be null.");
        }

        //noinspection ConstantConditions
        if (apiKey == null) {
            throw new IllegalArgumentException("API key argument cannot be null.");
        }

        if (redirectUrl == null) {
            redirectUrl = DEFAULT_REDIRECT_URL;
        }

        Intent intent = new Intent(context, AuthorizationActivity.class);
        intent.putExtra(ARGUMENT_API_KEY, apiKey);
        intent.putExtra(ARGUMENT_REDIRECT_URI, redirectUrl);

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        return intent;
    }

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String apiKey = getIntent().getStringExtra(ARGUMENT_API_KEY);
        final Uri redirectUri = getIntent().getParcelableExtra(ARGUMENT_REDIRECT_URI);

        if (apiKey == null || redirectUri == null) {
            throw new IllegalStateException("Activity has been launched with an Intent not created" +
                    " via the AuthorizationActivity.createIntent() methods.");
        }

        setContentView(R.layout.activity_pcloud_authentication);
        webView = (WebView) findViewById(R.id.webView);

        WebSettings ws = webView.getSettings();
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        WebViewClient wc = new WebViewClient() {

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                onReceivedError(view,
                        error.getErrorCode(), error.getDescription().toString(),
                        request.getUrl().toString());
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (!isFinishing()) {
                    setResultAndFinish(AuthorizationResult.AUTH_ERROR);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.startsWith(redirectUri.toString())) {
                    Map<String, String> parameters = Utils.parseUrlFragmentParameters(url);
                    if (parameters.containsKey("access_token")) {
                        String token = parameters.get("access_token");
                        long userId = Long.parseLong(parameters.get("userid"));
                        setResultAndFinish(AuthorizationResult.ACCESS_GRANTED, token, userId);
                    } else {
                        setResultAndFinish(AuthorizationResult.ACCESS_DENIED);
                    }
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        };

        webView.setWebViewClient(wc);

        if (savedInstanceState == null) {
            String url = BASE_AUTHORIZE_PAGE_URL.buildUpon()
                    .appendQueryParameter("response_type", "token")
                    .appendQueryParameter("client_id", apiKey)
                    .appendQueryParameter("redirect_uri", redirectUri.toString())
                    .toString();
            webView.loadUrl(url);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        setResultAndFinish(AuthorizationResult.CANCELLED);
    }

    private void setResultAndFinish(@NonNull AuthorizationResult authorizationResult) {
        setResultAndFinish(authorizationResult, null, 0);
    }

    private void setResultAndFinish(@NonNull AuthorizationResult authorizationResult, @Nullable String token, long userId) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_AUTHORIZATION_RESULT, authorizationResult);
        if (token != null) {
            resultIntent.putExtra(KEY_ACCESS_TOKEN, token);
            resultIntent.putExtra(KEY_USER_ID, userId);
        }
        int activityResult = authorizationResult == AuthorizationResult.CANCELLED ?
                RESULT_CANCELED : RESULT_OK;
        setResult(activityResult, resultIntent);
        finish();
    }
}
