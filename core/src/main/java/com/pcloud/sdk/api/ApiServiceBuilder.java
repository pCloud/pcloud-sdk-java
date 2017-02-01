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

package com.pcloud.sdk.api;

import com.pcloud.sdk.PCloudSdk;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * A builder for configuring an creating new {@link ApiService} instances.
 *
 * @see ApiService
 * @see ApiService#newBuilder()
 * @see PCloudSdk#newApiServiceBuilder()
 */
@SuppressWarnings("unused")
public interface ApiServiceBuilder {
    /**
     * Sets the response cache to be used to read and write cached responses.
     *
     * @see Cache
     */
    ApiServiceBuilder cache(Cache cache);

    /**
     * Sets the connectionPool used to recycle HTTP and HTTPS connections.
     * <p>
     * If unset, a new connection pool with a default configuration will be used.
     *
     * @see ConnectionPool
     */
    ApiServiceBuilder connectionPool(ConnectionPool connectionPool);

    /**
     * Sets the {@link Dispatcher} used to set policy and execute asynchronous requests.
     * <p>
     * Must not be null.
     * <p>
     * If unset, a new dispatcher with a default configuration will be used.
     *
     * @see Dispatcher
     */

    ApiServiceBuilder dispatcher(Dispatcher dispatcher);

    /**
     * Use an existing {@link OkHttpClient} instance and share the {@link Dispatcher},
     * {@link ConnectionPool} and {@link Cache}
     * <p>
     * Must not be null.
     * <p>
     * Previous calls to {@link #dispatcher(Dispatcher)}, {@link #connectionPool(ConnectionPool)},
     * {@link Cache} will be overriden by this call.
     *
     * @see OkHttpClient
     */
    ApiServiceBuilder withClient(OkHttpClient client);

    /**
     * Sets the default read timeout for new connections.
     * <p>
     * A value of 0 means no timeout, otherwise values must be between 1 and
     * {@link Integer#MAX_VALUE} when converted to milliseconds.
     */
    ApiServiceBuilder readTimeout(long timeout, TimeUnit timeUnit);

    /**
     * Sets the default write timeout for new connections.
     * <p>
     * A value of 0 means no timeout, otherwise values must be between 1 and
     * {@link Integer#MAX_VALUE} when converted to milliseconds.
     */
    ApiServiceBuilder writeTimeout(long timeout, TimeUnit timeUnit);

    /**
     * Sets the default connect timeout for new connections.
     * <p>
     * A value of 0 means no timeout, otherwise values must be between 1 and
     * {@link Integer#MAX_VALUE} when converted to milliseconds.
     */
    ApiServiceBuilder connectTimeout(long timeout, TimeUnit timeUnit);

    /**
     * Sets an {@link Authenticator}.
     * <p>
     * Must not be null.
     *
     * @see Authenticator
     */
    ApiServiceBuilder authenticator(Authenticator authenticator);

    /**
     * Set an executor to be used when invoking {@link Callback} instances.
     *
     * @see Executor
     */
    ApiServiceBuilder callbackExecutor(Executor callbackExecutor);

    /**
     * Define a progress notifications threshold
     * <p>
     * If set, a supplied {@link ProgressListener} instance will be invoked
     * on no less than {@code bytes} of transferred data.
     * <p>
     * Can be used to control thread congestion caused by the invocations
     * of the progress listener.
     *
     * @param bytes the minimal amounts of bytes to be transferred before
     *              notifying about a transfer progress
     * @see ProgressListener
     * @see ApiService#createFile(long, String, DataSource, Date, ProgressListener)
     * @see ApiService#download(FileLink, DataSink, ProgressListener)
     * @see RemoteData#download(DataSink, ProgressListener)
     */
    ApiServiceBuilder progressCallbackThreshold(int bytes);

    /**
     * Create a new {@link ApiService} from the provided configuration.
     *
     * @return a non-null {@link ApiService} object
     * @see ApiService
     */
    ApiService create();
}
