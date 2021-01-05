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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A utility {@link Activity} class for obtaining a OAuth 2.0 access token for PCloud's API
 * <p>
 * Start this activity to display to users a screen where they can grant permissions for your API application to access their accounts.
 * <p>
 * To obtain an access token by using this class the following steps must be done:
 * <ul>
 * <li>Create an {@link Intent} via the {@link #createIntent(Context, AuthorizationRequest)} method</li>
 * <li>Launch the intent via {@link Activity#startActivityForResult(Intent, int)} or {@link android.app.Fragment#startActivityForResult(Intent, int)} with an arbitrary request code.</li>
 * <li>Handle the result of the authentication process by listening on the {@link Activity#onActivityResult(int, int, Intent)}
 * or {@link android.app.Fragment#onActivityResult(int, int, Intent)} callbacks.
 * <li>Get a {@link AuthorizationData} object from the returned {@link Intent} by calling {@link AuthorizationActivity#getResult(Intent)}.</li>
 * </ul>
 * <h3>
 * NOTE: Instances of this activity launched via {@link Intent} objects not created via the
 * {@link #createIntent(Context, AuthorizationRequest)} or
 * {@link #createIntent(Context, String)}
 * methods will throw an {@link IllegalStateException}.
 * </h3>
 *
 * @see AuthorizationData
 */
public final class AuthorizationActivity extends Activity {

    private final static String DEFAULT_REDIRECT_URL_STRING = "https://oauth2redirect";
    private static final Uri REDIRECT_URI = Uri.parse(DEFAULT_REDIRECT_URL_STRING);
    private final static Uri BASE_AUTHORIZE_PAGE_URI = Uri.parse("https://my.pcloud.com/oauth2/authorize");
    private final static String ARGUMENT_AUTH_REQUEST = "com.pcloud.authentication.AuthorizationActivity.ARGUMENT_AUTH_REQUEST";
    private final static String ARGUMENT_AUTH_RESULT = "com.pcloud.authentication.AuthorizationActivity.ARGUMENT_AUTH_RESULT";

    /**
     * @deprecated Use the properties of {@linkplain AuthorizationData}.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public final static String KEY_AUTHORIZATION_RESULT = "com.pcloud.authentication.AuthorizationActivity.KEY_AUTHORIZATION_RESULT";
    /**
     * @deprecated Use the properties of {@linkplain AuthorizationData}.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public final static String KEY_ACCESS_TOKEN = "com.pcloud.authentication.AuthorizationActivity.KEY_ACCESS_TOKEN";

    /**
     * @deprecated Use the properties of {@linkplain AuthorizationData}.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public final static String KEY_USER_ID = "com.pcloud.authentication.AuthorizationActivity.KEY_USER_ID";

    /**
     * The default redirect url that will be called upon successful authorization.
     *
     * @deprecated Specifying custom redirect URLs is deprecated, the field will will be removed
     * in a future release.
     */
    @Deprecated
    public final static Uri DEFAULT_REDIRECT_URL = REDIRECT_URI;

    /**
     * Create a new {@link Intent} for launching an instance of [@link {@link AuthorizationActivity}}.
     *
     * @param context The {@link Context} to be bound to the new intent. Must not be null.
     *                <p>
     * @param apiKey  The API key for the application that will request access permission.
     *                <p>
     *                For more details on how to obtain a key, see <a href="https://docs.pcloud.com/oauth/index.html" target="_blank">here</a>.
     * @return a non-null {@link Intent} for launching an instance of [@link {@link AuthorizationActivity}}.
     * @see #createIntent(Context, String, Uri)
     * @deprecated Use {@linkplain #createIntent(Context, AuthorizationRequest)}
     */
    @Deprecated
    public static Intent createIntent(@NonNull Context context, @NonNull String apiKey) {
        return createIntent(context, AuthorizationRequest.create()
                .setType(AuthorizationRequest.Type.TOKEN)
                .setClientId(apiKey)
                .build());
    }

    /**
     * Create a new {@link Intent} for launching an instance of [@link {@link AuthorizationActivity}}.
     *
     * @param context     The {@link Context} to be bound to the new intent. Must not be null.
     *                    <p>
     * @param apiKey      The API key for the application that will request access permission.
     *                    <p>
     *                    For more details on how to obtain a key, see <a href="https://docs.pcloud.com/oauth/index.html" target="_blank">here</a>.
     * @param redirectUrl An optional URL that will be called after successful authentication.
     *                    <p>
     *                    If set to null, {@link #DEFAULT_REDIRECT_URL} will be used.
     * @return a non-null {@link Intent} for launching an instance of [@link {@link AuthorizationActivity}}.
     * @deprecated The option to specify a custom redirect url is deprecated. Calling this method will ignore the provided custom url.
     * <p>
     * Use {@linkplain #createIntent(Context, AuthorizationRequest)} instead.
     */
    @Deprecated
    public static Intent createIntent(@NonNull Context context, @NonNull String apiKey, @Nullable Uri redirectUrl) {
        //noinspection deprecation
        return createIntent(context, apiKey);
    }

    /**
     * Create a new {@link Intent} for launching an instance of [@link {@link AuthorizationActivity}}.
     *
     * @param context The {@link Context} to be bound to the new intent. Must not be null.
     * @param request non-null {@linkplain AuthorizationRequest}
     * @return non-null {@linkplain Intent}
     */
    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull AuthorizationRequest request) {
        //noinspection ConstantConditions
        if (context == null) {
            throw new IllegalArgumentException("Context argument cannot be null.");
        }

        //noinspection ConstantConditions
        if (request == null) {
            throw new IllegalArgumentException("AuthorizationRequest argument cannot be null.");
        }
        Intent intent = new Intent(context, AuthorizationActivity.class);
        intent.putExtra(ARGUMENT_AUTH_REQUEST, request);

        return intent;
    }

    /**
     * @param intent non-null {@link Intent} returned by {@linkplain Activity#onActivityResult(int, int, Intent)}
     *               after an {@linkplain AuthorizationActivity} instance has been started via
     *               {@linkplain Activity#startActivityForResult(Intent, int)}
     * @return non-null {@linkplain AuthorizationData}
     * @throws IllegalArgumentException if the intent parameter is invalid
     */
    @NonNull
    public static AuthorizationData getResult(@NonNull Intent intent) {
        if (!intent.hasExtra(ARGUMENT_AUTH_RESULT)) {
            throw new IllegalArgumentException("Invalid intent provided.");
        }

        return intent.getParcelableExtra(ARGUMENT_AUTH_RESULT);
    }

    private WebView webView;
    private AuthorizationRequest request;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(ARGUMENT_AUTH_REQUEST)) {
            throw new IllegalStateException("AuthorizationActivity must be launched with an Intent created" +
                    " via the AuthorizationActivity.createIntent() methods.");
        }

        request = intent.getParcelableExtra(ARGUMENT_AUTH_REQUEST);

        setContentView(R.layout.activity_pcloud_authentication);
        webView = findViewById(R.id.webView);

        WebViewClient webViewClient = new WebViewClient() {

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                onReceivedError(view,
                        error.getErrorCode(), error.getDescription().toString(),
                        request.getUrl().toString());
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (!isFinishing()) {
                    String message = String.format(Locale.US, "Error while loading url '%s':\n%d: %s", failingUrl, errorCode, description);
                    notifyAuthNotSuccessfulAndFinish(AuthorizationResult.AUTH_ERROR, message);
                }
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (uri != null && Objects.equals(REDIRECT_URI.getScheme(), uri.getScheme()) &&
                        Objects.equals(REDIRECT_URI.getAuthority(), uri.getAuthority())) {
                    return shouldOverrideUrlLoading(view, uri.toString());
                } else {
                    return false;
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith(DEFAULT_REDIRECT_URL_STRING)) {
                    try {
                        Map<String, String> parameters = Utils.parseUrlFragmentParameters(url);
                        if (parameters.containsKey("access_token") || parameters.containsKey("code")) {
                            String token = request.type == AuthorizationRequest.Type.TOKEN ?
                                    getStringUrlParameter(parameters, "access_token") : null;
                            String authCode = request.type == AuthorizationRequest.Type.CODE ?
                                    getStringUrlParameter(parameters, "code") : null;
                            long userId = getLongUrlParameter(parameters, "userid");
                            long locationId = getLongUrlParameter(parameters, "locationid");
                            String apiHostname = getStringUrlParameter(parameters, "hostname");

                            setResultAndFinish(new AuthorizationData(
                                    request,
                                    AuthorizationResult.ACCESS_GRANTED,
                                    token,
                                    userId,
                                    locationId,
                                    authCode,
                                    apiHostname,
                                    null
                            ));
                        } else {
                            notifyAuthNotSuccessfulAndFinish(AuthorizationResult.ACCESS_DENIED, null);
                        }
                    } catch (Exception e) {
                        notifyAuthNotSuccessfulAndFinish(AuthorizationResult.AUTH_ERROR, e.getMessage());
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };
        webView.setWebViewClient(webViewClient);
        WebSettings ws = webView.getSettings();
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);

        if (savedInstanceState == null) {
            webView.loadUrl(buildAuthorizePageUri(request).toString());
        }
    }

    private Uri buildAuthorizePageUri(AuthorizationRequest request) {
        String responseType = request.type == AuthorizationRequest.Type.CODE ? "code" : "token";
        Uri.Builder uriBuilder = BASE_AUTHORIZE_PAGE_URI.buildUpon()
                .appendQueryParameter("response_type", responseType)
                .appendQueryParameter("client_id", request.clientId)
                .appendQueryParameter("redirect_uri", DEFAULT_REDIRECT_URL_STRING);
        if (request.forceAccessApproval) {
            uriBuilder.appendQueryParameter("force_reapprove", "true");
        }
        if (!request.permissions.isEmpty()) {
            StringBuilder permissions = new StringBuilder();
            Iterator<String> iterator = request.permissions.iterator();
            while (iterator.hasNext()) {
                permissions.append(iterator.next());
                if (iterator.hasNext()) {
                    permissions.append(",");
                }
            }
            uriBuilder.appendQueryParameter("permissions", permissions.toString());
        }
        return uriBuilder.build();
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
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            notifyAuthNotSuccessfulAndFinish(AuthorizationResult.CANCELLED, null);
        }
    }

    private long getLongUrlParameter(@NonNull Map<String, String> parameters, @NonNull String key) {
        String value = getStringUrlParameter(parameters, key);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("'" + value + "' is not a valid value for '" + key + "'.");
        }
    }

    private String getStringUrlParameter(@NonNull Map<String, String> parameters, @NonNull String key) {
        String value = parameters.get(key);
        if (value == null) {
            throw new IllegalStateException("Missing '" + key + "' response parameter.");
        }
        return value;
    }

    @SuppressWarnings("deprecation")
    private void setResultAndFinish(AuthorizationData data) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(ARGUMENT_AUTH_RESULT, data);

        /*
         * Add the deprecated extras for backwards-compatibility.
         * */
        resultIntent.putExtra(KEY_AUTHORIZATION_RESULT, data.result);
        resultIntent.putExtra(KEY_USER_ID, data.userId);
        if (data.result == AuthorizationResult.ACCESS_GRANTED
                && data.request.type == AuthorizationRequest.Type.TOKEN) {
            resultIntent.putExtra(KEY_ACCESS_TOKEN, data.token);
        }
        int activityResult = data.result == AuthorizationResult.CANCELLED ?
                RESULT_CANCELED : RESULT_OK;
        setResult(activityResult, resultIntent);
        finish();
    }

    private void notifyAuthNotSuccessfulAndFinish(
            @NonNull AuthorizationResult authorizationResult,
            @Nullable String errorMessage) {
        setResultAndFinish(
                new AuthorizationData(request,
                        authorizationResult,
                        null,
                        0,
                        0,
                        null,
                        null,
                        errorMessage));
    }
}
