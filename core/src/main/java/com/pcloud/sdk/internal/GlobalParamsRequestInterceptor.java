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

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

class GlobalParamsRequestInterceptor implements Interceptor {

    private String headerValue;

    GlobalParamsRequestInterceptor(Map<String, String> parameters) {
        this.headerValue = buildHeaderValue(parameters);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newRequest = chain.request()
                .newBuilder().
                addHeader("Cookie", headerValue)
                .build();
        return chain.proceed(newRequest);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlobalParamsRequestInterceptor that = (GlobalParamsRequestInterceptor) o;
        return headerValue != null ? headerValue.equals(that.headerValue) : that.headerValue == null;
    }

    @Override
    public int hashCode() {
        return headerValue != null ? headerValue.hashCode() : 0;
    }

    private static String buildHeaderValue(Map<String, String> parameters) {
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = parameters.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> keyValuePair = iterator.next();
            builder.append(keyValuePair.getKey()).append('=').append(keyValuePair.getValue());
            if (iterator.hasNext()){
                builder.append("; ");
            }
        }

        builder.append("Domain=api.pcloud.com; Path=/; Secure; HttpOnly");
        return builder.toString();
    }
}
