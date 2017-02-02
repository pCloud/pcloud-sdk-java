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

import com.pcloud.sdk.ApiError;
import com.pcloud.sdk.Call;
import com.pcloud.sdk.Callback;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.concurrent.Executor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class ScheduledCallTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final Executor testExecutor = spy(new Executor() {
        @Override public void execute(Runnable runnable) {
            runnable.run();
        }
    });

    private ScheduledCall<Object> testCall;
    private Call<Object> wrappedCall;
    private Callback<Object> testCallback;
    private ArgumentCaptor<Callback> callbackArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        wrappedCall = mock(Call.class);
        testCallback = mock(Callback.class);
        testCall = new ScheduledCall<>(wrappedCall, testExecutor);
        callbackArgumentCaptor =  ArgumentCaptor.forClass(Callback.class);
    }

    @Test
    public void execute_CallsDelegateMethod() throws Exception {
        testCall.execute();
        verify(wrappedCall).execute();
    }

    @Test
    public void execute_PassesThrownExceptions() throws Exception {
        IOException error = new IOException();
        doThrow(error).when(wrappedCall).execute();
        expectedException.expect(is(error));
        testCall.execute();

        ApiError apiError = new ApiError(5000, "");
        doThrow(apiError).when(wrappedCall).execute();
        expectedException.expect(is(apiError));
        testCall.execute();
    }

    @Test
    public void cancel_CallsDelegateMethod() throws Exception {
        testCall.cancel();
        verify(wrappedCall).cancel();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void isExecuted_ReturnsDelegateState() throws Exception {
        boolean expectedValue = true;
        when(wrappedCall.isExecuted()).thenReturn(expectedValue);
        boolean actualValue = testCall.isExecuted();
        verify(wrappedCall).isExecuted();
        assertEquals(actualValue, expectedValue);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void isCancelled_ReturnsDelegateState() throws Exception {
        boolean expectedValue = true;
        when(wrappedCall.isCanceled()).thenReturn(expectedValue);
        boolean actualValue = testCall.isCanceled();
        verify(wrappedCall).isCanceled();
        assertEquals(actualValue, expectedValue);
    }

    @Test
    public void enqueue_Throws_WithNullArgument() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        testCall.enqueue(null);
    }

    @Test
    public void enqueue_CallsDelegateMethodWithNonNullArgument() throws Exception {
        testCall.enqueue(testCallback);
        verify(wrappedCall).enqueue(callbackArgumentCaptor.capture());
        Callback passedCallback = callbackArgumentCaptor.getValue();
        assertNotNull(passedCallback);
    }

    @Test
    public void enqueue_Calls_OnFailure_On_CallbackExecutor() throws Exception {
        testCall.enqueue(testCallback);
        verify(wrappedCall).enqueue(callbackArgumentCaptor.capture());

        Callback passedCallback = callbackArgumentCaptor.getValue();
        assertNotNull(passedCallback);

        IOException error = new IOException("something wrong");
        passedCallback.onFailure(wrappedCall, error);
        verify(testExecutor).execute(any(Runnable.class));
        verifyNoMoreInteractions(testExecutor);
        verify(testCallback).onFailure(eq(testCall), eq(error));
    }

    @Test
    public void enqueue_Calls_OnResponse_On_CallbackExecutor() throws Exception {
        testCall.enqueue(testCallback);
        verify(wrappedCall).enqueue(callbackArgumentCaptor.capture());

        Callback passedCallback = callbackArgumentCaptor.getValue();
        assertNotNull(passedCallback);

        final Object response = new Object();
        passedCallback.onResponse(wrappedCall, response);
        verify(testExecutor).execute(any(Runnable.class));
        verifyNoMoreInteractions(testExecutor);
        verify(testCallback).onResponse(eq(testCall), eq(response));
    }

    @Test
    public void clone_ReturnsNewObject() throws Exception {
        Call<Object> clonedObject = testCall.clone();
        assertNotNull(clonedObject);
        assertNotEquals(testCall, clonedObject);
    }

    @Test
    public void clone_ReturnsObjectWithSameExecutor() throws Exception {
        ScheduledCall<Object> clonedObject = testCall.clone();
        assertEquals(testCall.executor(), clonedObject.executor());
    }

    @Test
    public void clone__ReturnsObjectWithClonedDelegateCall() throws Exception {
        ScheduledCall<Object> clonedObject = testCall.clone();
        verify(wrappedCall).clone();
        assertNotEquals(testCall.delegate(), clonedObject.delegate());
    }
}