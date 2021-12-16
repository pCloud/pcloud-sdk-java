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

import com.pcloud.sdk.internal.Internal;

import java.util.concurrent.Callable;

/**
 * A collection of {@link Authenticator} factory methods.
 */
public class Authenticators {

    private Authenticators() {
        // No instantiation.
    }

    /**
     * Create a new OAuth 2.0 {@link Authenticator}
     * <p>
     * See the related API documentation <a href="https://docs.pcloud.com/methods/oauth_2.0/authorize.html" target="_blank">pages</a>.
     *
     * @param accessToken a valid access token. Must not be null.
     * @return a new {@link Authenticator} instance that uses PCloud API's OAuth 2.0 tokens.
     */
    public static Authenticator newOAuthAuthenticator(final String accessToken) {
        return Internal.createOAuthAuthenticator(() -> accessToken);
    }

    /**
     * Create a new OAuth 2.0 {@link Authenticator}
     * <p>
     * See the related API documentation <a href="https://docs.pcloud.com/methods/oauth_2.0/authorize.html" target="_blank">pages</a>.
     *
     * @param tokenProvider a non-null {@link Callable} object that will return an pCloud OAuth token.
     * @return a new {@link Authenticator} instance that uses PCloud API's OAuth 2.0 tokens.
     */
    public static Authenticator newOauthAuthenticator(Callable<String> tokenProvider){
        return Internal.createOAuthAuthenticator(tokenProvider);
    }
}
