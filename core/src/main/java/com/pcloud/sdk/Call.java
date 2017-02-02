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

package com.pcloud.sdk;

import java.io.IOException;

/**
 * An abstraction over a RPC call to pCloud's API servers.
 * <p>
 * A {@link Call} object resemebles a future request to execute a method having the following properties:
 * <ul>
 * <li>Allows for asynchronous execution by the {@link #enqueue(Callback)} method with a {@link Callback} object.</li>
 * <li>Allows for synchronous execution on the same thread by calling the {@link #execute()} method.</li>
 * <li>Can be executed only once, state can be checked via the {@link #isExecuted()} property.</li>
 * <li>Once created, a {@link Call} instance can be cloned via the {@link #clone()} method and executed again.</li>
 * <li>Executed calls can be cancelled via the {@link #cancel()} method. It is safe to call {@link #cancel()} multiple times.</li>
 * <li>Running calls that are being cancelled will result in an {@link IOException} being thrown or reported via {@link Callback#onFailure(Call, Throwable)}.</li>
 * </ul>
 *
 * @param <T> the type of the returned result
 */
public interface Call<T> extends Cloneable {
    /**
     * Synchronously send the request and return its response.
     *
     * @return the result of the call
     * @throws IOException if a problem occurred talking to the server.
     * @throws ApiError    returning any reported problems by the pCloud API.
     */
    T execute() throws IOException, ApiError;

    /**
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred talking to the server, creating the request, or processing the response.
     *
     * @param callback a non-null object that will be notified when a result is available or an error occurs.
     * @throws IllegalArgumentException on a null {@code callback} argument.
     */
    void enqueue(Callback<T> callback);

    /**
     * Returns true if this call has been either {@linkplain #execute() executed} or {@linkplain
     * #enqueue(Callback) enqueued}. It is an error to execute or enqueue a call more than once.
     *
     * @return {@code true} if this call has been executed, {@code false} otherwise
     */
    boolean isExecuted();

    /**
     * Cancel this call. An attempt will be made to cancel in-flight calls, and if the call has not
     * yet been executed it never will be.
     */
    void cancel();

    /**
     * @return {@code true} if this call has been cancelled, {@code false} otherwise
     */
    boolean isCanceled();

    /**
     * @return a new, identical call to this one which can be enqueued or executed again.
     */
    Call<T> clone();
}