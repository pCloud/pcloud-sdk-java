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
import com.pcloud.sdk.authentication.*;
import com.pcloud.sdk.authentication.Authenticator;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

class RealApiService implements ApiService {

    private Retrofit retrofit;
    private OkHttpClient httpClient;
    private Gson gson;

    static ApiService.Builder create(){
        return new Builder();
    }

    private RealApiService(Builder builder) {
        this.httpClient = new OkHttpClient.Builder()
                .dispatcher(builder.dispatcher)
                .connectionPool(builder.connectionPool)
                .cache(builder.cache)
                .authenticator((RealAuthenticator)builder.authenticator)
                .readTimeout(builder.readTimeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(builder.writeTimeoutMs,TimeUnit.MILLISECONDS)
                .connectTimeout(builder.connecttimeoutMs,TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(builder.retryOnFailure)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();

        this.gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://api.pcloud.com/")
                .client(this.httpClient)
                .callbackExecutor(builder.callbackExecutor)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Override
    public ApiService.Builder newBuilder() {
        return new Builder(this);
    }

    private static class Builder implements ApiService.Builder {
    
        private Cache cache;
        private Executor callbackExecutor;
        private ConnectionPool connectionPool;
        private Dispatcher dispatcher;
        private int readTimeoutMs;
        private int writeTimeoutMs;
        private int connecttimeoutMs;
        private boolean retryOnFailure;
        private com.pcloud.sdk.authentication.Authenticator authenticator;

        private Builder() {
        }

        private Builder(RealApiService service) {
            this.cache = service.httpClient.cache();
            this.callbackExecutor = service.retrofit.callbackExecutor();
            this.connectionPool = service.httpClient.connectionPool();
            this.dispatcher = service.httpClient.dispatcher();
            this.readTimeoutMs = service.httpClient.readTimeoutMillis();
            this.writeTimeoutMs = service.httpClient.writeTimeoutMillis();
            this.connecttimeoutMs = service.httpClient.connectTimeoutMillis();
            this.retryOnFailure = service.httpClient.retryOnConnectionFailure();
            this.authenticator = (Authenticator) service.httpClient.authenticator();
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
                    .connectTimeout(client.connectTimeoutMillis(), TimeUnit.MILLISECONDS)
                    .retryOnFailure(client.retryOnConnectionFailure());
        }
    
        @Override
        public ApiService.Builder readTimeout(long timeout, TimeUnit timeUnit) {
            this.readTimeoutMs = (int) timeUnit.toMillis(timeout);
            return this;
        }
    
        @Override
        public ApiService.Builder writeTimeout(long timeout, TimeUnit timeUnit) {
            this.readTimeoutMs = (int) timeUnit.toMillis(timeout);
            return this;
        }
    
        @Override
        public ApiService.Builder connectTimeout(long timeout, TimeUnit timeUnit) {
            this.readTimeoutMs = (int) timeUnit.toMillis(timeout);
            return this;
        }
    
        @Override
        public ApiService.Builder retryOnFailure(boolean retryOnFailure) {
            this.retryOnFailure = retryOnFailure;
            return this;
        }
    
        @Override
        public ApiService.Builder authenticator(com.pcloud.sdk.authentication.Authenticator authenticator) {
            this.authenticator = authenticator;
            return this;
        }
    
        @Override
        public ApiService.Builder callbackExecutor(Executor callbackExecutor) {
            this.callbackExecutor = callbackExecutor;
            return this;
        }
    
        @Override
        public ApiService create(){
            return new RealApiService(this);
        }
    
        private static OkHttpClient createHttpClient(Builder builder){
            return new OkHttpClient.Builder()
                    .dispatcher(builder.dispatcher)
                    .connectionPool(builder.connectionPool)
                    .cache(builder.cache)
                    .authenticator((RealAuthenticator)builder.authenticator)
                    .readTimeout(builder.readTimeoutMs,TimeUnit.MILLISECONDS)
                    .writeTimeout(builder.writeTimeoutMs,TimeUnit.MILLISECONDS)
                    .connectTimeout(builder.connecttimeoutMs,TimeUnit.MILLISECONDS)
                    .retryOnConnectionFailure(builder.retryOnFailure)
                    .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                    .build();
        }
    }
}