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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pcloud.sdk.api.ApiService;
import com.pcloud.sdk.api.ApiServiceBuilder;
import com.pcloud.sdk.api.FileEntry;
import com.pcloud.sdk.internal.networking.serialization.DateTypeAdapter;
import com.pcloud.sdk.internal.networking.serialization.FileEntryDeserializer;
import com.pcloud.sdk.authentication.RealAuthenticator;
import okhttp3.*;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

class RealApiServiceBuilder implements ApiServiceBuilder {

    private Cache cache;
    private Executor callbackExecutor;
    private ConnectionPool connectionPool;
    private Dispatcher dispatcher;
    private int readTimeoutMs;
    private int writeTimeoutMs;
    private int connecttimeoutMs;
    private com.pcloud.sdk.authentication.Authenticator authenticator;

    RealApiServiceBuilder() {
    }

    @Override
    public ApiServiceBuilder cache(Cache cache) {
        this.cache = cache;
        return this;
    }

    @Override
    public ApiServiceBuilder connectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        return this;
    }

    @Override
    public ApiServiceBuilder dispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        return this;
    }

    @Override
    public ApiServiceBuilder withClient(OkHttpClient client) {
        return cache(client.cache())
                .connectionPool(client.connectionPool())
                .dispatcher(client.dispatcher())
                .readTimeout(client.readTimeoutMillis(), TimeUnit.MILLISECONDS)
                .writeTimeout(client.writeTimeoutMillis(), TimeUnit.MILLISECONDS)
                .connectTimeout(client.connectTimeoutMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public ApiServiceBuilder readTimeout(long timeout, TimeUnit timeUnit) {
        this.readTimeoutMs = (int) timeUnit.toMillis(timeout);
        return this;
    }

    @Override
    public ApiServiceBuilder writeTimeout(long timeout, TimeUnit timeUnit) {
        this.writeTimeoutMs = (int) timeUnit.toMillis(timeout);
        return this;
    }

    @Override
    public ApiServiceBuilder connectTimeout(long timeout, TimeUnit timeUnit) {
        this.connecttimeoutMs = (int) timeUnit.toMillis(timeout);
        return this;
    }

    @Override
    public ApiServiceBuilder authenticator(com.pcloud.sdk.authentication.Authenticator authenticator) {
        this.authenticator = authenticator;
        return this;
    }

    @Override
    public ApiServiceBuilder callbackExecutor(Executor callbackExecutor) {
        this.callbackExecutor = callbackExecutor;
        return this;
    }

    @Override
    public ApiService create() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(this.readTimeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(this.writeTimeoutMs, TimeUnit.MILLISECONDS)
                .connectTimeout(this.connecttimeoutMs, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .addInterceptor(new GloabalParamsRequestInterceptor());

        if (dispatcher != null) {
                httpClientBuilder.dispatcher(dispatcher);
        }

        if (connectionPool != null) {
            httpClientBuilder.connectionPool(connectionPool);
        }

        if (cache != null) {
            httpClientBuilder.cache(cache);
        }

        httpClientBuilder.authenticator(Authenticator.NONE);
        if (authenticator != null) {
            httpClientBuilder.addInterceptor((RealAuthenticator)authenticator);
        }

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(FileEntry.class, new FileEntryDeserializer())
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        return new RealApiService(gson, httpClientBuilder.build(), this.callbackExecutor);
    }
}
