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

package com.pcloud.sdk.internal;

import com.pcloud.sdk.api.ApiError;
import com.pcloud.sdk.api.Callback;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class OkHttpCallTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private okhttp3.Call mockOkHttpCall;
    private OkHttpCall<Object> testCall;
    private ResponseAdapter<Object> responseAdapter;
    private Request testRequest;
    private Callback<Object> testCallback;
    private ArgumentCaptor<okhttp3.Callback> callbackArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        testRequest = new Request.Builder().url("http://www.google.com").build();
        mockOkHttpCall = mock(okhttp3.Call.class);
        when(mockOkHttpCall.request()).thenReturn(testRequest);
        responseAdapter = mock(ResponseAdapter.class);
        testCall = new OkHttpCall<>(mockOkHttpCall, responseAdapter);
        testCallback = mock(Callback.class);
        callbackArgumentCaptor = ArgumentCaptor.forClass(okhttp3.Callback.class);
    }

    @Test
    public void execute_CallsDelegateMethod() throws Exception {
        testCall.execute();
        verify(mockOkHttpCall).execute();
    }

    @Test
    public void execute_PassesResponseAdapterResult() throws Exception {
        Response response = new Response.Builder().request(testRequest)
                .code(500)
                .protocol(Protocol.HTTP_1_1)
                .build();

        Object expectedResponse = new Object();
        when(mockOkHttpCall.execute()).thenReturn(response);

        when(responseAdapter.adapt(eq(response))).thenReturn(expectedResponse);

        Object actualResponse = testCall.execute();
        verify(mockOkHttpCall).execute();
        verify(responseAdapter).adapt(eq(response));
        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    public void execute_PassesThrownExceptions() throws Exception {
        IOException error = new IOException();
        doThrow(error).when(mockOkHttpCall).execute();
        expectedException.expect(is(error));
        testCall.execute();

        ApiError apiError = new ApiError(5000, "");
        doThrow(apiError).when(mockOkHttpCall).execute();
        expectedException.expect(is(apiError));
        testCall.execute();
    }

    @Test
    public void cancel_CallsDelegateMethod() throws Exception {
        testCall.cancel();
        verify(mockOkHttpCall).cancel();
    }

    @Test
    public void isExecuted_ReturnsDelegateState() throws Exception {
        boolean expectedValue = true;
        when(mockOkHttpCall.isExecuted()).thenReturn(expectedValue);
        boolean actualValue = testCall.isExecuted();
        verify(mockOkHttpCall).isExecuted();
        assertEquals(actualValue, expectedValue);
    }

    @Test
    public void isCancelled_ReturnsDelegateState() throws Exception {
        boolean expectedValue = true;
        when(mockOkHttpCall.isCanceled()).thenReturn(expectedValue);
        boolean actualValue = testCall.isCanceled();
        verify(mockOkHttpCall).isCanceled();
        assertEquals(actualValue, expectedValue);
    }

    @Test
    public void enqueue_CallsDelegateMethodWithNonNullArgument() throws Exception {
        testCall.enqueue(testCallback);
        verify(mockOkHttpCall).enqueue(callbackArgumentCaptor.capture());

        okhttp3.Callback passedCallback = callbackArgumentCaptor.getValue();
        assertNotNull(passedCallback);
    }

    @Test
    public void enqueue_CallsFailureMethod_OnDelegateFailureCalled() throws Exception {
        testCall.enqueue(testCallback);
        verify(mockOkHttpCall).enqueue(callbackArgumentCaptor.capture());

        okhttp3.Callback passedCallback = callbackArgumentCaptor.getValue();
        assertNotNull(passedCallback);

        IOException error = new IOException("something wrong");
        passedCallback.onFailure(mockOkHttpCall, error);
        verify(testCallback).onFailure(eq(testCall), eq(error));
    }

    @Test
    public void enqueue_CallsFailureMethod_OnResponseConversionFailure() throws Exception {
        testCall.enqueue(testCallback);
        verify(mockOkHttpCall).enqueue(callbackArgumentCaptor.capture());
        okhttp3.Callback passedCallback = callbackArgumentCaptor.getValue();

        Response response = new Response.Builder().request(testRequest)
                .code(500)
                .protocol(Protocol.HTTP_1_1)
                .build();

        final Exception apiError = new ApiError(5000, "");
        doThrow(apiError).when(responseAdapter).adapt(eq(response));
        passedCallback.onResponse(mockOkHttpCall, response);
        verify(testCallback).onFailure(eq(testCall), eq(apiError));
    }

    @Test
    public void enqueue_CallsResponseMethod_OnResponseConversionSuccess() throws Exception {
        testCall.enqueue(testCallback);
        verify(mockOkHttpCall).enqueue(callbackArgumentCaptor.capture());
        okhttp3.Callback passedCallback = callbackArgumentCaptor.getValue();

        Response response = new Response.Builder().request(testRequest)
                .code(200)
                .protocol(Protocol.HTTP_1_1)
                .build();

        Object adaptedResponse = new Object();
        when(responseAdapter.adapt(eq(response))).thenReturn(adaptedResponse);
        passedCallback.onResponse(mockOkHttpCall, response);
        verify(testCallback).onResponse(eq(testCall), eq(adaptedResponse));
    }

    @Test
    public void clone_ReturnsNewObject() throws Exception {
        OkHttpCall<Object> clonedObject = testCall.clone();
        assertNotNull(clonedObject);
        assertNotEquals(testCall, clonedObject);
    }

    @Test
    public void clone_ReturnsObjectWithSameAdapter() throws Exception {
        OkHttpCall<Object> clonedObject = testCall.clone();
        assertEquals(testCall.responseAdapter(), clonedObject.responseAdapter());
    }

    @Test
    public void clone__ReturnsObjectWithClonedDelegateCall() throws Exception {
        OkHttpCall<Object> clonedObject = testCall.clone();
        verify(mockOkHttpCall).clone();
        assertNotEquals(testCall.rawCall(), clonedObject.rawCall());
    }
}