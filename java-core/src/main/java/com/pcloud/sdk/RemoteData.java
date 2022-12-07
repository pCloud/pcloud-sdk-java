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
import java.io.InputStream;

import okio.BufferedSource;

/**
 * A data container storing a pCloud account's file entry data.
 */
@SuppressWarnings("WeakerAccess")
public interface RemoteData {
    /**
     * Open an {@link InputStream} to the resource.
     * <p>
     * Attempts to open a byte stream to the resource contents.
     * <h3>
     * The method will block until a stream is opened or an exception is thrown.
     * </h3>
     * <h3>
     * NOTE: It is the caller's responsibility to close the resulting {@link InputStream} object
     * or resource leaks will occur.
     * </h3>
     *
     * @return an {@link InputStream} to the contents of the resource. Cannot be null.
     * @throws IOException on a network or API error.
     * @see #source()
     */
    InputStream byteStream() throws IOException, ApiError;

    /**
     * Open an {@link BufferedSource} to the resource.
     * <p>
     * Attempts to open a byte stream to the resource contents.
     * <h3>
     * The method will block until a stream is opened or an exception is thrown.
     * </h3>
     * <h3>
     * NOTE: It is the caller's responsibility to close the resulting {@link BufferedSource} object
     * </h3>
     * or resource leaks will occur.
     *
     * @return an {@link BufferedSource} to the contents of the resource. Cannot be null.
     * @throws IOException on a network, IO or API error.
     */
    BufferedSource source() throws IOException, ApiError;

    /**
     * Download the contained data to a {@link DataSink}
     * <p>
     * Attempts to download the resource data to the specified location,
     * optionally sending progress updates via a {@link ProgressListener}.
     * <h3>
     * The method will block until the download operation completes or an exception is thrown.
     * </h3>
     *
     * @param sink     a {@link DataSink} instance where data will be downloaded. Must not be null.
     * @param listener The listener to be used to notify about upload progress. If null, no progress will be reported.
     * @throws IOException              on a network, IO or API error.
     * @throws IllegalArgumentException on a null {@code sink} argument.
     */
    void download(DataSink sink, ProgressListener listener) throws IOException, ApiError;

    /**
     * Download the contained data to a {@link DataSink}
     * <h3>
     * The method will block until the download operation completes or an exception is thrown.
     * </h3>
     *
     * @param sink a {@link DataSink} instance where data will be downloaded. Must not be null.
     * @throws IOException              on a network, IO or API error.
     * @throws IllegalArgumentException on a null {@code sink} argument.
     * @see #download(DataSink, ProgressListener)
     */
    void download(DataSink sink) throws IOException, ApiError;
}
