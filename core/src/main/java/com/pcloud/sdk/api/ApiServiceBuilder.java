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
    /**
     * Sets the response cache to be used to read and write cached responses.
     */
    ApiServiceBuilder cache(Cache cache);

    /**
     * Sets the connectionPool used to recycle HTTP and HTTPS connections.
     * <p>
     * <p>If unset, a new connection pool will be used.
     */
    ApiServiceBuilder connectionPool(ConnectionPool connectionPool);

    /**
     * Sets the {@link Dispatcher} used to set policy and execute asynchronous requests. Must not be null.
     */

    ApiServiceBuilder dispatcher(Dispatcher dispatcher);

    /**
     * Sets {@link OkHttpClient} instance which will be used for API calls. Must not be null.
     */
    ApiServiceBuilder withClient(OkHttpClient client);

    /**
     * Sets the default read timeout for new connections. A value of 0 means no timeout, otherwise
     * values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
     */
    ApiServiceBuilder readTimeout(long timeout, TimeUnit timeUnit);

    /**
     * Sets the default write timeout for new connections. A value of 0 means no timeout, otherwise
     * values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
     */
    ApiServiceBuilder writeTimeout(long timeout, TimeUnit timeUnit);

    /**
     * Sets the default connect timeout for new connections. A value of 0 means no timeout,
     * otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted to
     * milliseconds.
     */
    ApiServiceBuilder connectTimeout(long timeout, TimeUnit timeUnit);

    /**
     * Sets {@link Authenticator}.
     */
    ApiServiceBuilder authenticator(Authenticator authenticator);

    /**
     * Sets executor for {@link Callback}.
     */
    ApiServiceBuilder callbackExecutor(Executor callbackExecutor);

    /**
     * Setup new {@link ApiService}.
     *
     * @return {@link ApiService}
     */
    ApiService create();
}
