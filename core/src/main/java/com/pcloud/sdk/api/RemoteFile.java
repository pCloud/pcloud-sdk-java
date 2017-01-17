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

import java.io.IOException;

public interface RemoteFile extends FileEntry, RemoteData {

    /**
     * Returns the fileId of the RemoteFile.
     */
    long getFileId();

    /**
     * Returns the content type of the RemoteFile.
     *
     * <p>For more information, see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">here</a></p>
     */
    String getContentType();

    /**
     * Returns the size of the file in bytes.
     */
    long getSize();

    /**
     * Returns a hash of the file content.
     *
     * <p>This parameter can be used to detect content changes.</p>
     */
    String getHash();

    FileLink getDownloadLink(DownloadOptions options) throws IOException, ApiError;

    FileLink getDownloadLink() throws IOException, ApiError;
}
