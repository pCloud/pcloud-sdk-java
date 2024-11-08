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
 * An abstraction over a file entry for a file on a pCloud account's filesystem.
 *
 * @see RemoteEntry
 * @see RemoteFolder
 */
@SuppressWarnings("unused")
public interface RemoteFile extends RemoteEntry, RemoteData {

    /**
     * @return the file identifier.
     */
    long fileId();

    /**
     * @return the content type of the RemoteFile.
     * <p>
     * For more information on content types, see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">here</a>.
     */
    String contentType();

    /**
     * @return the size of the file in bytes.
     */
    long size();

    /**
     * @return {@code true} if a thumbnail can be generated for the file, {@code false} otherwise.
     * */
    boolean hasThumbnail();

    /**
     * @return the hash of the file content. Can be used to detect content changes.
     */
    String hash();

    /**
     * Create a file link for this file
     * <h3>
     * The method will block until the link is created or an exception is thrown.
     * </h3>
     *
     * @param options {@link DownloadOptions} to be used creating the link. Must not be null.
     * @return a {@link FileLink} for this file.
     * @throws IOException              on a network error
     * @throws ApiError                 if error code is returned from pCloud's API
     * @throws IllegalArgumentException on a null {@code options} argument.
     */
    FileLink createFileLink(DownloadOptions options) throws IOException, ApiError;

    /**
     * Create a file link for this file
     * <h3>
     * The method will block until the link is created or an exception is thrown.
     * </h3>
     *
     * @return a {@link FileLink} for this file.
     * @throws IOException on a network error
     * @throws ApiError    if error code is returned from pCloud's API
     */
    FileLink createFileLink() throws IOException, ApiError;
}
