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

import com.pcloud.sdk.PCloudSdk;
import okio.BufferedSource;

import java.util.Date;
import java.util.List;

/**
 * The General interface that exposes pCloud API's methods.
 * <p>
 * The class is a factory for {@linkplain Call calls} which can be used to execute certain operations on a pCloud account.
 * <h3>
 * The ApiService is designed with reuse in mind, it's best used when created once and shared among code using it,
 * mainly due to used connection and thread pooling.
 * <li>
 * Users should configure and create instances through the {@link ApiServiceBuilder} interface.
 * <li>Builder instances can be created via the {@link PCloudSdk#newApiServiceBuilder()} method
 * or from an existing {@linkplain ApiService} instance through the {@linkplain #newBuilder()} method.
 * <li>Shared instance's configuration can be changed by using the {@linkplain #newBuilder()} method that will return
 * a pre-configured builder.
 * <li>There is no explicit need to call {@linkplain #shutdown()}, any idle threads and connections will automatically get closed once not used anymore.
 * <li>All methods return non-null {@link Call} instances.
 * <li>All methods are thread-safe.
 * <li>All calls resulting in a collection of objects, return unmodifiable {@link java.util.Collection}-derived objects.
 * <h3>Bear in mind that shutting down a {@link ApiService} instance will also affect all instances sharing the underlying resources.
 */

public interface ApiService {

    /**
     * Load a specified folder.
     * <p>
     * Same as calling {@link #getFolder(long, boolean)} )} with {@code recursive} set to false.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/folder/listfolder.html">documentation page</a>
     *
     * @param folderId {@link RemoteFolder} id
     * @return {@link Call}
     */
    Call<RemoteFolder> getFolder(long folderId);

    /**
     * Load a specified folder.
     * <p>
     * Loads the metadata about the folder with the provided folder id.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/folder/listfolder.html">documentation page</a>.
     *
     * @param folderId    target folder id
     * @param recursively if true, a full folder tree will be returned,
     *                    otherwise the resulting {@linkplain RemoteFolder folder} will contain only its direct children
     * @return {@link Call}
     */
    Call<RemoteFolder> getFolder(long folderId, boolean recursively);

    /**
     * List the specified folder's children.
     * <p>
     * Loads the metadata about folder's direct children, if any.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/folder/listfolder.html">documentation page</a>.
     *
     * @param folder The {@link RemoteFolder} to list. Must not be null.
     * @return {@link Call} resulting in a non-null list of children. An empty list is returned if folder is empty.
     */
    Call<List<FileEntry>> listFiles(RemoteFolder folder);

    /**
     * Create a new folder.
     * <p>Create a new folder in the specified folder</p>
     * <p>For more information, see the related <a href="https://docs.pcloud.com/methods/folder/createfolder.html">documentation page</a>.</p>
     *
     * @param parentFolderId The id of the parent folder for the newly created folder
     * @param folderName     The new folder name
     * @return {@link Call} resulting in the metadata for the new folder
     */
    Call<RemoteFolder> createFolder(long parentFolderId, String folderName);

    /**
     * Create a folder.
     * <p>
     * Same as calling {@link #createFolder(long, String)} with {@code parentFolderId} taken from {@linkplain RemoteFolder#getFolderId()}.
     *
     * @param parentFolder The parent {@link RemoteFolder} for the newly created folder. Must not be null.
     * @param folderName   The new folder name
     * @return {@link Call}
     */
    Call<RemoteFolder> createFolder(RemoteFolder parentFolder, String folderName);

    /**
     * Delete a specified folder recursively.
     * <p>
     * Same as calling {@link #deleteFolder(long, boolean)} (long, String)} with {@code recursively} set to false.
     *
     * @param folderId The id if the folder you would like to delete
     * @return {@link Call}
     */
    Call<Boolean> deleteFolder(long folderId);

    /**
     * Delete a specified folder recursively.
     * <p>For more information, see the related documentation pages
     * <a href="https://docs.pcloud.com/methods/folder/deletefolder.html">here</a></p> and <a href="https://docs.pcloud.com/methods/folder/deletefolderrecursive.html">here</a>.
     *
     * @param folderId    The id if the folder you would like to delete
     * @param recursively If set to true, all child files will also be deleted.
     *                    If set to false, the operation will fail on any non-empty folder
     * @return {@link Call} resulting in true if the operation is successful, or false otherwise
     */
    Call<Boolean> deleteFolder(long folderId, boolean recursively);

    /**
     * Delete a specified folder recursively.
     *
     * @param folder The {@link RemoteFolder} you would like to delete. Must not be null.
     * @see #deleteFolder(long)
     */
    Call<Boolean> deleteFolder(RemoteFolder folder);

    /**
     * Delete a specified folder recursively.
     *
     * @param folder The {@link RemoteFolder} you would like to delete. Must not be null.
     * @see #deleteFolder(long, boolean)
     */
    Call<Boolean> deleteFolder(RemoteFolder folder, boolean recursively);

    /**
     * Rename a specified folder.
     * <p>For more information, see the related <a href="https://docs.pcloud.com/methods/folder/renamefolder.html">documentation page</a>.</p>
     *
     * @param folderId      The id of the folder you would like to rename
     * @param newFolderName The new folder name
     * @return {@link Call} resulting in the renamed folder's metadata.
     */
    Call<RemoteFolder> renameFolder(long folderId, String newFolderName);

    /**
     * Rename a specified folder.
     *
     * @param folder        The {@link RemoteFolder} you would like to rename. Must not be null.
     * @param newFolderName The new folder name
     * @return {@link Call} resulting in the renamed folder's metadata.
     * @see #renameFolder(long, String)
     */
    Call<RemoteFolder> renameFolder(RemoteFolder folder, String newFolderName);

    /**
     * Change a specified folder's parent.
     * <p>For more information, see the related <a href="https://docs.pcloud.com/methods/folder/renamefolder.html">documentation page</a>.</p>
     *
     * @param folderId   The id of the folder you would like to move
     * @param toFolderId The id of the new parent folder
     * @return {@link Call} resulting in the moved folder's metadata.
     */
    Call<RemoteFolder> moveFolder(long folderId, long toFolderId);

    /**
     * Change a specified folder's parent.
     *
     * @param folder   The {@link RemoteFolder} you would like to move. Must not be null.
     * @param toFolder The new parent {@link RemoteFolder}. Must not be null.
     * @return {@link Call} resulting in the moved folder's metadata.
     * @see #moveFolder(long, long)
     */
    Call<RemoteFolder> moveFolder(RemoteFolder folder, RemoteFolder toFolder);

    /**
     * Copy specified folder.
     * <p>For more information, see the related <a href="https://docs.pcloud.com/methods/folder/renamefolder.html">documentation page</a>.</p>
     *
     * @param folderId   The id of the folder you would like to copy
     * @param toFolderId The id of the destination folder
     * @return {@link Call} resulting in the copied folder's metadata.
     */
    Call<RemoteFolder> copyFolder(long folderId, long toFolderId);

    /**
     * Copy specified folder.
     *
     * @param folder   The {@link RemoteFolder} you would like to copy. Must not be null.
     * @param toFolder The destination {@link RemoteFolder}. Must not be null.
     * @return {@link Call} resulting in the copied folder's metadata.
     * @see #copyFolder(long, long)
     */
    Call<RemoteFolder> copyFolder(RemoteFolder folder, RemoteFolder toFolder);

    /**
     * Create a new file.
     * <p>
     * Same as calling {@link #createFile(RemoteFolder, String, DataSource, Date, ProgressListener)} with null {@code modifiedDate} and {@code listener} arguments.
     *
     * @param folder   The {@link RemoteFolder} where you would like to create the file. Must not be null.
     * @param filename The file name. Must not be null.
     * @param data     {@link DataSource} object providing the file content. Must not be null.
     * @return {@link Call} resulting in the new file's metadata
     * @see #createFile(long, String, DataSource, Date, ProgressListener)
     */
    Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data);

    /**
     * Create a new file.
     * <p>
     * Same as calling {@link #createFile(long, String, DataSource, Date, ProgressListener)} with {@code folderId} taken from {@linkplain RemoteFolder#getFolderId()}.
     *
     * @param folder       The {@link RemoteFolder} where you would like to create the file. Must not be null.
     * @param filename     The file name. Must not be null.
     * @param modifiedDate The last modification date to be used. If set to null, the upload date will be used instead.
     * @param data         {@link DataSource} object providing the file content. Must not be null.
     * @param listener     The listener to be used to notify about upload progress. If null, no progress will be reported.
     * @return {@link Call} resulting in the new file's metadata
     * @see #createFile(long, String, DataSource, Date, ProgressListener)
     * @see DataSource
     * @see ProgressListener
     */
    Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data, Date modifiedDate, ProgressListener listener);

    /**
     * Create a new file.
     * <p>
     * Same as calling {@link #createFile(long, String, DataSource, Date, ProgressListener)} with null {@code modifiedDate} and {@code listener} arguments.
     *
     * @param folderId The {@link RemoteFolder} id where you would like to create the file
     * @param filename The file name. Must not be null.
     * @param data     {@link DataSource} object providing the file content. Must not be null.
     * @return {@link Call} resulting in the new file's metadata
     * @see #createFile(long, String, DataSource, Date, ProgressListener)
     */
    Call<RemoteFile> createFile(long folderId, String filename, DataSource data);

    /**
     * Create a new file.
     * <p>
     * Creates a new file with the specified name in the specified folder.
     * <p>
     * If set, the {@code modifiedDate} parameter will be se tas the last modification date of the file.
     * <p>
     * The provided {@link DataSource} object will be used to populate the file's contents.
     * <p>
     * If a {@link ProgressListener} is provided, it will be notified on every {@code n} bytes uploaded, as set per {@link ApiServiceBuilder#progressCallbackThreshold(int)}
     * <p>
     * To create an empty file, call the method with a {@link DataSource#EMPTY} argument.
     *
     * @param folderId     The id of the folder you would like to create the file.
     * @param filename     The file name. Must not be null.
     * @param modifiedDate The last modification date to be used. If set to null, the upload date will be used instead.
     * @param data         {@link DataSource} object providing the file content. Must not be null.
     * @param listener     The listener to be used to notify about upload progress. If null, no progress will be reported.
     * @return {@link Call} resulting in the new file's metadata
     * @see DataSource
     * @see ProgressListener
     */
    Call<RemoteFile> createFile(long folderId, String filename, DataSource data, Date modifiedDate, ProgressListener listener);

    Call<Boolean> deleteFile(RemoteFile file);

    Call<Boolean> deleteFile(long fileId);

    Call<FileLink> getDownloadLink(RemoteFile file, DownloadOptions options);

    Call<FileLink> getDownloadLink(long fileid, DownloadOptions options);

    Call<Void> download(FileLink fileLink, DataSink sink);

    Call<Void> download(FileLink fileLink, DataSink sink, ProgressListener listener);

    Call<BufferedSource> download(RemoteFile file);

    Call<BufferedSource> download(FileLink fileLink);

    /**
     * Copy specified file.
     *
     * @param fileId     The id of the {@link RemoteFile} you would like to copy.
     * @param toFolderId The {@link RemoteFolder} id where you would like to copy the file.
     * @return {@link Call}
     */
    Call<RemoteFile> copyFile(long fileId, long toFolderId);

    /**
     * Same as {@link #copyFile(long, long)}
     *
     * @param file     The {@link RemoteFile} which you would like to copy.Must not be null.
     * @param toFolder The {@link RemoteFolder}  where you would like to copy the file.Must not be null.
     * @return {@link Call}
     */
    Call<RemoteFile> copyFile(RemoteFile file, RemoteFolder toFolder);

    /**
     * Move specified file.
     *
     * @param fileId     The id of the {@link RemoteFile} you would like to move.
     * @param toFolderId The {@link RemoteFolder} id where you would like to move the file.
     * @return {@link Call}
     */
    Call<RemoteFile> moveFile(long fileId, long toFolderId);

    /**
     * Same as {@link #moveFile(long, long)}
     *
     * @param file     The {@link RemoteFile} which you would like to move. Must not be null.
     * @param toFolder The {@link RemoteFolder}  where you would like to move the file.
     * @return {@link Call}
     */
    Call<RemoteFile> moveFile(RemoteFile file, RemoteFolder toFolder);

    /**
     * Rename specified file.
     *
     * @param fileId      The id of the {@link RemoteFile} you would like to rename.
     * @param newFileName The new file name. Must not be null.
     * @return {@link Call}
     */
    Call<RemoteFile> renameFile(long fileId, String newFileName);

    /**
     * Same as {@link #renameFile(RemoteFile, String)}
     *
     * @param file        The {@link RemoteFile} you would like to rename.Must not be null.
     * @param newFileName The new file name. Must not be null.
     * @return {@link Call}
     */
    Call<RemoteFile> renameFile(RemoteFile file, String newFileName);


    /**
     * Get {@link UserInfo} .
     *
     * @return {@link Call}
     */
    Call<UserInfo> getUserInfo();

    /**
     * Returns new ApiServiceBuilder.
     */
    ApiServiceBuilder newBuilder();

    void shutdown();
}
