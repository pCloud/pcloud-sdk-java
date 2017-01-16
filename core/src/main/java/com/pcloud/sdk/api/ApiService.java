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

import java.util.List;

/**
 * The ApiService public interface
 */
public interface ApiService {

    /**
     * This method will list a folder contents.
     *
     * @param folderId {@link RemoteFolder} id
     * @return {@link Call}
     */
    Call<RemoteFolder> getFolder(long folderId);

    /**
     * This method will list a folder contents .
     *
     * @param folder The {@link RemoteFolder} to list. Must not be null.
     * @return {@link Call}
     */
    Call<List<FileEntry>> listFiles(RemoteFolder folder);

    /**
     * This method will create a folder.
     *
     * @param parentFolderId The id of the parent folder for the newly created folder
     * @param folderName     The new folder name
     * @return {@link Call}
     */
    Call<RemoteFolder> createFolder(long parentFolderId, String folderName);

    /**
     * This method will create a folder.
     *
     * @param parentFolder The parent {@link RemoteFolder} for the newly created folder. Must not be null.
     * @param folderName   The new folder name
     * @return {@link Call}
     */
    Call<RemoteFolder> createFolder(RemoteFolder parentFolder, String folderName);

    /**
     * This method will delete a folder.
     *
     * @param folderId The id if the folder you would like to delete
     * @return {@link Call}
     */
    Call<RemoteFolder> deleteFolder(long folderId);

    /**
     * This method will delete a folder.
     *
     * @param folder {@link RemoteFolder} you would like to delete. Must not be null.
     * @return {@link Call}
     */
    Call<RemoteFolder> deleteFolder(RemoteFolder folder);


    /**
     * This method will rename a folder.
     *
     * @param folderId      The id of the folder you would like to rename
     * @param newFolderName The new folder name
     * @return {@link Call}
     */
    Call<RemoteFolder> renameFolder(long folderId, String newFolderName);

    /**
     * This method will rename a folder.
     *
     * @param folder        The {@link RemoteFolder} you would like to rename. Must not be null.
     * @param newFolderName The new folder name
     * @return {@link Call}
     */
    Call<RemoteFolder> renameFolder(RemoteFolder folder, String newFolderName);

    /**
     * This method will move folder
     *
     * @param folderId   The id of the folder you would like to move
     * @param toFolderId The id of the destination folder
     * @return {@link Call}
     */
    Call<RemoteFolder> moveFolder(long folderId, long toFolderId);

    /**
     * This method will move folder.
     *
     * @param folder   The {@link RemoteFolder} you would like to move. Must not be null.
     * @param toFolder The destination {@link RemoteFolder}
     * @return {@link Call}
     */
    Call<RemoteFolder> moveFolder(RemoteFolder folder, RemoteFolder toFolder);

    /**
     * This method will copy folder.
     *
     * @param folderId   The id of the folder you would like to copy
     * @param toFolderId The id of the destination folder
     * @return {@link Call}
     */
    Call<RemoteFolder> copyFolder(long folderId, long toFolderId);

    /**
     * This method will copy folder.
     *
     * @param folder   The {@link RemoteFolder} you would like to copy. Must not be null.
     * @param toFolder The destination {@link RemoteFolder}
     * @return {@link Call}
     */
    Call<RemoteFolder> copyFolder(RemoteFolder folder, RemoteFolder toFolder);

    /**
     * This method will create(upload) a file.
     *
     * @param folder   The {@link RemoteFolder} where you would like to create the file. Must not be null.
     * @param filename The file name. Must not be null.
     * @param data     {@link Data} object provides the file content.
     * @return {@link Call}
     */
    Call<RemoteFile> createFile(RemoteFolder folder, String filename, Data data);

    /**
     * This method will create(upload) a file.
     *
     * @param folderId The {@link RemoteFolder} id where you would like to create the file
     * @param filename The file name. Must not be null.
     * @param data     {@link Data} object provides the file content.
     * @return {@link Call}
     */
    Call<RemoteFile> createFile(long folderId, String filename, Data data);

    ApiServiceBuilder newBuilder();
}
