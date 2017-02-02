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

import com.pcloud.sdk.ApiService;
import com.pcloud.sdk.Authenticator;
import okhttp3.*;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

class RealApiServiceBuilder implements ApiService.Builder {

    private Cache cache;
    private Executor callbackExecutor;
    private ConnectionPool connectionPool;
    private Dispatcher dispatcher;
    private int readTimeoutMs;
    private int writeTimeoutMs;
    private int connectTimeoutMs;
    private long progressCallbackThresholdBytes;
    private Authenticator authenticator;

    RealApiServiceBuilder(OkHttpClient okHttpClient, Executor callbackExecutor, long progressCallbackThresholdBytes, Authenticator authenticator) {
        this.cache = okHttpClient.cache();
        this.callbackExecutor = callbackExecutor;
        this.connectionPool = okHttpClient.connectionPool();
        this.dispatcher = okHttpClient.dispatcher();
        this.readTimeoutMs = okHttpClient.readTimeoutMillis();
        this.writeTimeoutMs = okHttpClient.writeTimeoutMillis();
        this.connectTimeoutMs = okHttpClient.connectTimeoutMillis();
        this.progressCallbackThresholdBytes = progressCallbackThresholdBytes;
        this.authenticator = authenticator;
    }

    RealApiServiceBuilder() {
    }

    @Override
    public ApiService.Builder cache(Cache cache) {
        this.cache = cache;
        return this;
    }

    @Override
    public ApiService.Builder connectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        return this;
    }

    @Override
    public ApiService.Builder dispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        return this;
    }

    @Override
    public ApiService.Builder withClient(OkHttpClient client) {
        return cache(client.cache())
                .connectionPool(client.connectionPool())
                .dispatcher(client.dispatcher())
                .readTimeout(client.readTimeoutMillis(), TimeUnit.MILLISECONDS)
                .writeTimeout(client.writeTimeoutMillis(), TimeUnit.MILLISECONDS)
                .connectTimeout(client.connectTimeoutMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public ApiService.Builder readTimeout(long timeout, TimeUnit timeUnit) {
        this.readTimeoutMs = (int) timeUnit.toMillis(timeout);
        return this;
    }

    @Override
    public ApiService.Builder writeTimeout(long timeout, TimeUnit timeUnit) {
        this.writeTimeoutMs = (int) timeUnit.toMillis(timeout);
        return this;
    }

    @Override
    public ApiService.Builder connectTimeout(long timeout, TimeUnit timeUnit) {
        this.connectTimeoutMs = (int) timeUnit.toMillis(timeout);
        return this;
    }

    @Override
    public ApiService.Builder authenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
        return this;
    }

    @Override
    public ApiService.Builder callbackExecutor(Executor callbackExecutor) {
        this.callbackExecutor = callbackExecutor;
        return this;
    }

    @Override
    public ApiService.Builder progressCallbackThreshold(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("Threshold parameter must a positive number.");
        }
        this.progressCallbackThresholdBytes = bytes;
        return this;
    }

    @Override
    public ApiService create() {
        return new RealApiService(this);
    }

    public Cache cache() {
        return cache;
    }

    public Executor callbackExecutor() {
        return callbackExecutor;
    }

    public ConnectionPool connectionPool() {
        return connectionPool;
    }

    public Dispatcher dispatcher() {
        return dispatcher;
    }

    public int readTimeoutMs() {
        return readTimeoutMs;
    }

    public int writeTimeoutMs() {
        return writeTimeoutMs;
    }

    public int connectTimeoutMs() {
        return connectTimeoutMs;
    }

    public long progressCallbackThresholdBytes() {
        return progressCallbackThresholdBytes;
    }

    public Authenticator authenticator() {
        return authenticator;
    }
}
