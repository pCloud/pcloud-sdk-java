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

import com.pcloud.sdk.ApiError;
import com.pcloud.sdk.Call;
import com.pcloud.sdk.Callback;
import okhttp3.Response;

import java.io.IOException;

import static com.pcloud.sdk.internal.IOUtils.closeQuietly;

import org.jetbrains.annotations.NotNull;

final class OkHttpCall<T> implements Call<T> {

    private final okhttp3.Call rawCall;
    private final ResponseAdapter<T> responseAdapter;

    OkHttpCall(okhttp3.Call rawCall, ResponseAdapter<T> adapter) {
        this.rawCall = rawCall;
        this.responseAdapter = adapter;
    }

    @Override
    public T execute() throws IOException, ApiError {
        Response response = rawCall.execute();
        return adapt(response);
    }

    @Override
    public void enqueue(final Callback<T> callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback argument cannot be null.");
        }

        rawCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                callback.onFailure(OkHttpCall.this, e);
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) {
                try {
                    callback.onResponse(OkHttpCall.this, adapt(response));
                } catch (ApiError | IOException e) {
                    closeQuietly(response);
                    callback.onFailure(OkHttpCall.this, e);
                }
            }
        });
    }

    @Override
    public boolean isExecuted() {
        return rawCall.isExecuted();
    }

    @Override
    public void cancel() {
        rawCall.cancel();
    }

    @Override
    public boolean isCanceled() {
        return rawCall.isCanceled();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public OkHttpCall<T> clone() {
        // Class is final, there will be no 'super'.
        return new OkHttpCall<>(rawCall.clone(), responseAdapter);
    }

    okhttp3.Call rawCall() {
        return rawCall;
    }

    ResponseAdapter<T> responseAdapter() {
        return responseAdapter;
    }

    private T adapt(Response response) throws IOException, ApiError {
        return responseAdapter.adapt(response);
    }
}
