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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import static com.pcloud.authentication.Constants.*;
public class AuthenticationIntentBuilder {

    private String apiKey;
    private Uri redirectUri;

    public AuthenticationIntentBuilder() {
    }

    /**
     * Set your app App Key. You can get it from {@link "https://docs.pcloud.com/oauth/index.html"}
     */
    public AuthenticationIntentBuilder setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }
    /**
     * Set a custom redirect Uri.
     *
     * <p>Access token will be received at this redirect uri.
     * If not set we will use default redirect uri.</p>
     */
    public AuthenticationIntentBuilder setRedirectUri(Uri uri) {
        this.redirectUri = uri;
        return this;
    }

    /**
     * Build intent. You should start it with {@code startActivityForResult} to obtain Access Token.
     */
    public Intent buildWithContext(Context context) {
        if (apiKey == null) {
            throw new IllegalArgumentException("API Key can not be null");
        }
        if (redirectUri == null) {
            redirectUri = Uri.parse(DEFAULT_REDIRECT_URI);
        }
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.putExtra(API_KEY, apiKey);
        intent.putExtra(REDIRECT_URI, redirectUri.toString());

        return intent;
    }
}

