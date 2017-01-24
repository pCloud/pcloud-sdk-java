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

import com.pcloud.sdk.PCloudApi;
import com.pcloud.sdk.authentication.Authenticator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import okio.BufferedSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ApiServiceTest {

    private ApiService apiService;

    @Before
    public void setUp() {
        String token = System.getenv("pcloud_token");
        apiService = PCloudApi.newApiService()
                .authenticator(Authenticator.newOAuthAuthenticator(token))
                .create();
    }

    @Test
    public void testListFolder() throws IOException, ApiError {
        long id = RemoteFolder.ROOT_FOLDER_ID;
        RemoteFolder folder = apiService.getFolder(id).execute();

        assertEquals(id, folder.getFolderId());
    }

    @Test
    public void testCreateFolder() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();

        assertTrue(verifyEntryPresenceInRoot(remoteFolder));
    }

    @Test
    public void testDeleteFolder() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();

        DeletedEntriesInfo deletedEntriesInfo = apiService.deleteFolder(remoteFolder).execute();

        assertEquals(1, deletedEntriesInfo.getDeletedFoldersCount());
    }

    @Test
    public void testRenameFolder() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();
        String randomNewName = UUID.randomUUID().toString();

        RemoteFolder renamedFolder = apiService.renameFolder(remoteFolder, randomNewName).execute();

        assertEquals(remoteFolder.getFolderId(), renamedFolder.getFolderId());
        assertNotEquals(remoteFolder.getName(), renamedFolder.getName());
    }

    @Test
    public void testMoveFolder() throws IOException, ApiError {
        RemoteFolder remoteFolder1 = createRemoteFolder();
        RemoteFolder remoteFolder2 = createRemoteFolder();

        apiService.moveFolder(remoteFolder1, remoteFolder2).execute();

        assertTrue(verifyEntryPresenceInFolder(remoteFolder1, remoteFolder2.getFolderId()));
        assertFalse(verifyEntryPresenceInFolder(remoteFolder1, RemoteFolder.ROOT_FOLDER_ID));
    }


    @Test
    public void testCopyFolder() throws IOException, ApiError {
        RemoteFolder remoteFolder1 = createRemoteFolder();
        RemoteFolder remoteFolder2 = createRemoteFolder();

        apiService.copyFolder(remoteFolder1, remoteFolder2).execute();

        assertTrue(verifyEntryPresenceInFolder(remoteFolder1, remoteFolder2.getFolderId()));
        assertTrue(verifyEntryPresenceInFolder(remoteFolder1, RemoteFolder.ROOT_FOLDER_ID));
    }

    @Test
    public void testCreateFile() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        RemoteFile remoteFile = apiService.createFile(RemoteFolder.ROOT_FOLDER_ID, someName + ".txt", DataSource.create(fileContents)).execute();

        assertTrue(verifyEntryPresenceInRoot(remoteFile));
        assertEquals(fileContents.length, remoteFile.getSize());
    }


    @Test
    public void testDeleteFile() throws IOException, ApiError {
        RemoteFile remoteFile = createRemoteFile();

        Boolean isDeleted = apiService.deleteFile(remoteFile).execute();

        assertTrue(isDeleted);
        assertFalse(verifyEntryPresenceInRoot(remoteFile));
    }

    @Test
    public void testMoveFile() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();
        RemoteFile remoteFile = createRemoteFile();

        apiService.moveFile(remoteFile, remoteFolder).execute();

        assertTrue(verifyEntryPresenceInFolder(remoteFile, remoteFolder.getFolderId()));
        assertFalse(verifyEntryPresenceInFolder(remoteFile, RemoteFolder.ROOT_FOLDER_ID));
    }


    @Test
    public void testCopyFile() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();
        RemoteFile remoteFile = createRemoteFile();

        RemoteFile copiedFile = apiService.copyFile(remoteFile, remoteFolder).execute();

        assertTrue(verifyEntryPresenceInFolder(copiedFile, remoteFolder.getFolderId()));
        assertTrue(verifyEntryPresenceInFolder(remoteFile, RemoteFolder.ROOT_FOLDER_ID));
    }


    @Test
    public void testRenameFile() throws IOException, ApiError {
        RemoteFile remoteFile = createRemoteFile();
        String randomNewName = UUID.randomUUID().toString();

        RemoteFile renamedFile = apiService.renameFile(remoteFile, randomNewName + ".txt").execute();

        assertEquals(remoteFile.getFileId(), renamedFile.getFileId());
        assertNotEquals(remoteFile.getName(), renamedFile.getName());
    }


    @Test
    public void testDownloadFileFromLink() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        RemoteFile remoteFile = apiService.createFile(RemoteFolder.ROOT_FOLDER_ID, someName + ".txt", DataSource.create(fileContents)).execute();
        DownloadOptions options = DownloadOptions.create()
                .skipFilename(true)
                .forceDownload(false)
                .contentType(remoteFile.getContentType())
                .build();

        FileLink fileLink = apiService.getDownloadLink(remoteFile, options).execute();
        BufferedSource source = apiService.download(fileLink).execute();
        assertTrue(Arrays.equals(fileContents, source.readByteArray()));
    }

    @Test
    public void testDownloadFile() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        RemoteFile remoteFile = apiService.createFile(RemoteFolder.ROOT_FOLDER_ID, someName + ".txt", DataSource.create(fileContents)).execute();

        BufferedSource source = apiService.download(remoteFile).execute();
        assertTrue(Arrays.equals(fileContents, source.readByteArray()));
    }

    private RemoteFolder createRemoteFolder() throws IOException, ApiError {
        String randomFolderName = UUID.randomUUID().toString();
        return apiService.createFolder(RemoteFolder.ROOT_FOLDER_ID, randomFolderName).execute();
    }

    private RemoteFile createRemoteFile() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        return apiService.createFile(RemoteFolder.ROOT_FOLDER_ID, someName + ".txt", DataSource.create(fileContents)).execute();
    }

    private boolean verifyEntryPresenceInRoot(FileEntry entry) throws IOException, ApiError {
        return verifyEntryPresenceInFolder(entry, RemoteFolder.ROOT_FOLDER_ID);
    }

    private boolean verifyEntryPresenceInFolder(FileEntry entry, long parentFolderId) throws IOException, ApiError {
        RemoteFolder root = apiService.getFolder(parentFolderId).execute();
        for (FileEntry e : root.getChildren()) {
            if (e.getId().equals(entry.getId()) || e.getName().equals(entry.getName())) {
                return true;
            }
        }
        return false;
    }

    @After
    public void shutDown() {
        apiService.shutdown();
    }

}