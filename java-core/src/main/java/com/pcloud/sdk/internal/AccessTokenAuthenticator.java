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

package com.pcloud.sdk.internal;

import org.jetbrains.annotations.NotNull;

import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.Callable;

final class AccessTokenAuthenticator extends RealAuthenticator {

    private final Callable<String> tokenProvider;

    AccessTokenAuthenticator(Callable<String> tokenProvider){
        if (tokenProvider == null) {
            throw new IllegalArgumentException("'tokenProvider' argument cannot be null.");
        }

        this.tokenProvider = tokenProvider;
    }

    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        String accessToken;
        try {
            accessToken = tokenProvider.call();
        } catch (Exception e) {
            throw new IOException("Error while providing access token.", e);
        }
        Request request = accessToken != null ?
                setBearerTokenToRequest(chain.request(), accessToken) : chain.request();
        return chain.proceed(request);
    }

    private Request setBearerTokenToRequest(Request request, String accessToken) {
        if (accessToken != null) {
            return request.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .build();
        }
        return request;
    }
}
