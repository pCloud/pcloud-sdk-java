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

import java.util.Date;

/**
 * An abstraction over a file entry on a pCloud account's filesystem.
 */
@SuppressWarnings("unused")
public interface FileEntry {

    /**
     * Returns the entry id.
     */
    String getId();

    /**
     * Returns the entry filename.
     */
    String getName();

    /**
     * Returns the last modification date.
     */
    Date getLastModified();

    /**
     * Returns the creation date.
     */
    Date getCreated();

    /**
     * Returns the parent folder's id for the entry.
     * <p>
     * See {@linkplain RemoteFolder#getFolderId()} ()}.
     */
    long getParentFolderId();

    /**
     * Returns {@code true} if this entry is a file.
     */
    boolean isFile();

    /**
     * Returns {@code true} if this entry is a folder.
     */
    boolean isFolder();

    /**
     * Returns this FileEntry as a {@link RemoteFolder}
     * <p>
     * See {@linkplain #isFolder()}.
     *
     * @return {@link RemoteFolder}
     * @throws IllegalStateException if the entry is not a folder
     */
    RemoteFolder asFolder();

    /**
     * Returns this FileEntry as {@link RemoteFile}
     * <p>
     * See {@linkplain #isFile()}.
     *
     * @return {@link RemoteFile}
     * @throws IllegalStateException if the entry is not a file
     */
    RemoteFile asFile();
}
