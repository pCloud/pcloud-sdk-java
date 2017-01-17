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

public interface ApiServiceBuilder {
    ApiServiceBuilder cache(Cache cache);

    ApiServiceBuilder connectionPool(ConnectionPool connectionPool);

    ApiServiceBuilder dispatcher(Dispatcher dispatcher);

    ApiServiceBuilder withClient(OkHttpClient client);

    ApiServiceBuilder readTimeout(long timeout, TimeUnit timeUnit);

    ApiServiceBuilder writeTimeout(long timeout, TimeUnit timeUnit);

    ApiServiceBuilder connectTimeout(long timeout, TimeUnit timeUnit);

    ApiServiceBuilder retryOnFailure(boolean retryOnFailure);

    ApiServiceBuilder authenticator(Authenticator authenticator);

    ApiServiceBuilder callbackExecutor(Executor callbackExecutor);

    ApiServiceBuilder progressCallbackThreshold(int bytes);

    ApiService create();
}
