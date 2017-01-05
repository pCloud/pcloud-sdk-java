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

package com.pcloud.sdk.api;

import com.pcloud.sdk.authentication.Authenticator;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public interface ApiService {

    Builder newBuilder();

    interface Builder {
        Builder cache(Cache cache);

        Builder connectionPool(ConnectionPool connectionPool);

        Builder dispatcher(Dispatcher dispatcher);

        Builder withClient(OkHttpClient client);

        Builder readTimeout(long timeout, TimeUnit timeUnit);

        Builder writeTimeout(long timeout, TimeUnit timeUnit);

        Builder connectTimeout(long timeout, TimeUnit timeUnit);

        Builder retryOnFailure(boolean retryOnFailure);

        Builder authenticator(Authenticator authenticator);

        Builder callbackExecutor(Executor callbackExecutor);

        ApiService create();
    }
}
