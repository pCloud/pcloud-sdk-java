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

package com.pcloud.sdk.utils;

import com.pcloud.sdk.api.ApiError;
import com.pcloud.sdk.api.Call;
import com.pcloud.sdk.api.Callback;

import java.io.IOException;

public class DummyCall<T> implements Call<T> {

    private T result;
    private boolean executed;
    private boolean cancelled;

    public DummyCall(T result) {
        this.result = result;
    }

    @Override
    public T execute() throws IOException, ApiError {
        if (executed) {
            throw new IllegalStateException("Call cannot be executed more than once. Consider using clone().");
        }

        if (!cancelled) {
            executed = true;
            return result;
        } else {
            throw new IOException("Cancelled");
        }
    }

    @Override
    public void enqueue(Callback<T> callback) {
        if (executed) {
            throw new IllegalStateException("Call cannot be executed more than once. Consider using clone().");
        }

        if (!cancelled) {
            executed = true;
            callback.onResponse(this, result);
        }else {
            callback.onFailure(this, new IOException("Cancelled"));
        }
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Override
    public boolean isCanceled() {
        return cancelled;
    }

    @Override
    public Call<T> clone() {
        return new DummyCall<>(result);
    }
}
