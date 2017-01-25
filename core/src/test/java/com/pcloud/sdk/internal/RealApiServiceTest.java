package com.pcloud.sdk.internal;

import com.google.gson.Gson;
import com.pcloud.sdk.api.ApiService;
import com.pcloud.sdk.api.ApiServiceTest;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RealApiServiceTest extends ApiServiceTest{

    @Override
    protected ApiService createInstance() {
        OkHttpClient mockClient = mock(OkHttpClient.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(new Call() {
            @Override
            public Request request() {
                return null;
            }

            @Override
            public Response execute() throws IOException {
                return null;
            }

            @Override
            public void enqueue(Callback responseCallback) {

            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isExecuted() {
                return false;
            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Override
            public Call clone() {
                return null;
            }
        });
        ExecutorService mockExecutor = mock(ExecutorService.class);
        ConnectionPool connectionPool = new ConnectionPool(0, 1, TimeUnit.MILLISECONDS);
        Dispatcher dispatcher = new Dispatcher(mockExecutor);
        when(mockClient.connectionPool()).thenReturn(connectionPool);
        when(mockClient.dispatcher()).thenReturn(dispatcher);

        return new RealApiService(new Gson(), mockClient, mockExecutor, 1000);
    }

}