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
import java.util.List;

/**
 * An abstraction over a file entry for a folder on a pCloud account's filesystem.
 *
 * @see RemoteEntry
 * @see RemoteFile
 */
@SuppressWarnings("unused")
public interface RemoteFolder extends RemoteEntry {

    /**
     * The id of a pCloud account's filesystem root folder.
     */
    int ROOT_FOLDER_ID = 0;

    /**
     * @return the folderId for this folder.
     */
    long folderId();

    /**
     * @return the folder's children. Cannot be null.
     */
    List<RemoteEntry> children();

    /**
     * Reload this folder.
     * <p>
     * Same as calling {@link #reload(boolean)} with {@code recursively} argument set to {@code false}.
     *
     * @return a new, updated {@link RemoteFolder} instance
     * @throws IOException on a network or API error.
     * @see ApiClient#listFolder(long)
     */
    RemoteFolder reload() throws IOException;

    /**
     * Reload this folder.
     *
     * @param recursively if true, a full folder tree will be returned, otherwise the resulting {@linkplain RemoteFolder folder} will contain only its direct children
     * @return a new, updated {@link RemoteFolder} instance
     * @throws IOException on a network or API error.
     * @see ApiClient#listFolder(long, boolean)
     */
    RemoteFolder reload(boolean recursively) throws IOException;

    /**
     * Delete this folder.
     *
     * @param recursively If set to {@code true} all child files will also be deleted.
     *                    <p>If set to {@code false}, the operation will fail on any non-empty folder
     * @return {@code true} if the operation is successful, {@code false} otherwise
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @throws IOException              on a network or API error.
     * @see ApiClient#deleteFolder(RemoteFolder, boolean)
     */
    boolean delete(boolean recursively) throws IOException;

    boolean canCreate();
}
