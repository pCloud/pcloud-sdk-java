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
import java.util.Date;

/**
 * An abstraction over a file entry on a pCloud account's filesystem.
 * @see RemoteFile
 * @see RemoteFolder
 */
@SuppressWarnings("unused")
public interface RemoteEntry {

    /**
     * @return the identifier of the file.
     */
    String id();

    /**
     * @return the entry filename.
     */
    String name();

    /**
     * @return the last modification date.
     */
    Date lastModified();

    /**
     * @return the creation date.
     */
    Date created();

    /**
     * @return the parent folder's id for the entry. See {@linkplain RemoteFolder#folderId()} ()}.
     */
    long parentFolderId();

    /**
     * @return {@code true} if this entry is a file.
     */
    boolean isFile();

    /**
     * @return {@code true} if this entry is a folder.
     */
    boolean isFolder();

    /**
     * Returns this RemoteEntry as a {@link RemoteFolder}
     * <p>
     * See {@linkplain #isFolder()}.
     *
     * @return this object as a {@link RemoteFolder}
     * @throws IllegalStateException if the entry is not a folder
     */
    RemoteFolder asFolder();

    /**
     * Returns this RemoteEntry as {@link RemoteFile}
     * <p>
     * See {@linkplain #isFile()}.
     *
     * @return this object as a {@link RemoteFile}
     * @throws IllegalStateException if the entry is not a file
     */
    RemoteFile asFile();

    /**
     * Copy this file to a specified folder.
     *
     * @param toFolder The {@link RemoteFolder} where the file will be copied. Must not be null.
     * @return the copied file.
     * @throws IllegalArgumentException on a null {@code toFolder} argument.
     * @throws IOException              on a network or API error.
     * @see ApiClient#copy(RemoteEntry, RemoteFolder)
     * @see RemoteFolder#canCreate()
     */
    RemoteEntry copy(RemoteFolder toFolder) throws IOException;

    /**
     * Copy this file to a specified folder.
     * <p>
     * The behavior of the {@code overwriteFiles} parameter depends on the type of the {@code file} being passed.
     * For files, see the description {@linkplain ApiClient#copyFile(RemoteFile, RemoteFolder) here},
     * otherwise see {@linkplain ApiClient#copyFolder(RemoteFolder, RemoteFolder, boolean) here}.
     *
     * @param toFolder  The {@link RemoteFolder} where the file will be copied. Must not be null.
     * @param overwrite If set to {@code true}, a file with the same name in the destination folder will be overwritten
     * @return the copied file.
     * @throws IllegalArgumentException on a null {@code toFolder} argument.
     * @throws IOException              on a network or API error.
     * @see ApiClient#copy(RemoteEntry, RemoteFolder, boolean)
     */
    RemoteEntry copy(RemoteFolder toFolder, boolean overwrite) throws IOException;

    /**
     * Move this file to a specified folder.
     *
     * @param toFolder The {@link RemoteFolder} where the file will be moved. Must not be null.
     * @return the copied file.
     * @throws IllegalArgumentException on a null {@code toFolder} argument.
     * @throws IOException              on a network or API error.
     * @see ApiClient#move(RemoteEntry, RemoteFolder)
     * @see #canModify()
     */
    RemoteEntry move(RemoteFolder toFolder) throws IOException;

    /**
     * Rename this file.
     *
     * @param newFilename The new name. Must not be null.
     * @return the renamed file.
     * @throws IllegalArgumentException on a null {@code newFilename} argument.
     * @throws IOException              on a network or API error.
     * @see ApiClient#rename(RemoteEntry, String)
     * @see #canModify()
     */
    RemoteEntry rename(String newFilename) throws IOException;

    /**
     * Delete this file.
     *
     * @return {@code true} if deleted, {@code false} otherwise.
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @throws IOException              on a network or API error.
     * @see ApiClient#delete(RemoteEntry)
     * @see #canDelete()
     */
    boolean delete() throws IOException;

    /**
     * @return {@code true} if the entry's content can be read by the current account, {@code false} otherwise
     * @see #isMine()
     * */
    boolean canRead();

    /**
     * @return {@code true} if the entry's content can be modified by the current account, {@code false} otherwise
     * @see #isMine()
     * */
    boolean canModify();

    /**
     * @return {@code true} if the entry can be deleted by the current account, {@code false} otherwise
     * @see #isMine()
     * */
    boolean canDelete();

    /**
     * @return {@code true} if the entry is owned by the current account, {@code false} otherwise
     * @see #canRead()
     * @see #canModify()
     * @see #canDelete()
     * @see RemoteFolder#canCreate()
     * */
    boolean isMine();

    /**
     * @return {@code true} if the entry is shared with other accounts, {@code false} otherwise
     * */
    boolean isShared();
}
