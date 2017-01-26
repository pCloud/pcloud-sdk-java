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

import com.pcloud.sdk.api.ApiError;
import com.pcloud.sdk.api.Call;
import com.pcloud.sdk.api.Callback;

import java.io.IOException;
import java.util.concurrent.Executor;

final class ExecutorCallbackCall<T> implements Call<T> {

    private Call<T> delegate;
    private Executor callbackExecutor;

    ExecutorCallbackCall(Call<T> delegate, Executor callbackExecutor) {
        this.delegate = delegate;
        this.callbackExecutor = callbackExecutor;
    }

    @Override
    public T execute() throws IOException, ApiError {
        return delegate.execute();
    }

    @Override
    public void enqueue(final Callback<T> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback argument cannot be null.");
        }

        delegate.enqueue(new Callback<T>() {

            @Override
            public void onResponse(Call<T> call, final T response) {
                callbackExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(ExecutorCallbackCall.this, response);
                    }
                });
            }

            @Override public void onFailure(Call<T> call, final Throwable t) {
                callbackExecutor.execute(new Runnable() {
                    @Override public void run() {
                        callback.onFailure(ExecutorCallbackCall.this, t);
                    }
                });
            }
        });
    }

    @Override
    public boolean isExecuted() {
        return delegate.isExecuted();
    }

    @Override
    public void cancel() {
        delegate.cancel();
    }

    @Override
    public boolean isCanceled() {
        return delegate.isCanceled();
    }

    @SuppressWarnings("CloneDoesntCallSuperClone") // Performing deep clone.
    @Override public ExecutorCallbackCall<T> clone() {
        return new ExecutorCallbackCall<>(delegate.clone(), callbackExecutor);
    }

    Call<T> delegate() {
        return delegate;
    }

    Executor executor() {
        return callbackExecutor;
    }
}
