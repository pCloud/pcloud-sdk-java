/*
 * Copyright (c) 2017 pCloud AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pcloud.sdk.internal;

import org.jetbrains.annotations.NotNull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

class GlobalRequestInterceptor implements Interceptor {

    private final String userAgent;
    private final String cookieValues;

    GlobalRequestInterceptor(String userAgent, Map<String, String> globalParameters) {
        this.userAgent = userAgent;
        this.cookieValues = buildCookieValue(globalParameters);
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newRequest = chain.request()
                .newBuilder()
                .header("User-Agent", userAgent)
                .addHeader("Cookie", cookieValues)
                .build();
        return chain.proceed(newRequest);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GlobalRequestInterceptor that = (GlobalRequestInterceptor) o;
        return cookieValues.equals(that.cookieValues);

    }

    @Override
    public int hashCode() {
        return cookieValues.hashCode();
    }

    private static String buildCookieValue(Map<String, String> parameters) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> keyValuePair : parameters.entrySet()) {
            builder.append(keyValuePair.getKey()).append('=').append(keyValuePair.getValue());
            builder.append("; ");
        }

        builder.append("Domain=api.pcloud.com; Path=/; Secure; HttpOnly");
        return builder.toString();
    }
}
