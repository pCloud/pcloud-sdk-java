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

package com.pcloud.sdk.api;

/**
 * A Callback listener used together with the {@link Call#enqueue(Callback)} method.
 *
 * @param <T> the type parameter
 */
public interface Callback<T> {
    /**
     * Called on a successful operation
     *
     * @param call     the call
     * @param response a result
     */
    void onResponse(Call<T> call, T response);

    /**
     * Called on failed operation
     *
     * @param call the call
     * @param t    the error that caused the failure
     */
    void onFailure(Call<T> call, Throwable t);
}
