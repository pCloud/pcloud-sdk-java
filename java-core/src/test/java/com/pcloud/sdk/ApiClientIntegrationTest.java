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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import okio.BufferedSource;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ApiClientIntegrationTest {

    private ApiClient apiClient;

    @Before
    public void setUp() {
        String token = System.getenv("PCLOUD_TEST_TOKEN");
        apiClient = PCloudSdk.newClientBuilder()
                .authenticator(Authenticators.newOAuthAuthenticator(token))
                .create();
    }

    @Test
    public void testListFolder() throws IOException, ApiError {
        apiClient.listFolder(RemoteFolder.ROOT_FOLDER_ID).execute();
    }

    @Test
    public void testGetFolder() throws Exception {
        long id = RemoteFolder.ROOT_FOLDER_ID;
        RemoteFolder folder = apiClient.listFolder(id, true).execute();
        assertEquals(id, folder.folderId());
    }

    @Test
    public void testListFolderPath() throws Exception {
        apiClient.listFolder("/").execute();
    }

    @Test
    public void testGetFolderPath() throws Exception {
        long id = RemoteFolder.ROOT_FOLDER_ID;
        RemoteFolder folder = apiClient.listFolder("/", true).execute();
        assertEquals(id, folder.folderId());
    }

    @Test
    public void testCreateFolder() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();

        assertTrue(entryExistsInRoot(remoteFolder));
    }

    @Test
    public void testDeleteFolder() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();
        assertTrue(apiClient.deleteFolder(remoteFolder).execute());
    }

    @Test
    public void testDeleteFolderRecursively() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();
        createRemoteFolder(remoteFolder.folderId());
        assertTrue(apiClient.deleteFolder(remoteFolder, true).execute());
    }

    @Test
    public void testRenameFolder() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();
        String randomNewName = UUID.randomUUID().toString();

        RemoteFolder renamedFolder = apiClient.renameFolder(remoteFolder, randomNewName).execute();

        assertEquals(remoteFolder.folderId(), renamedFolder.folderId());
        assertNotEquals(remoteFolder.name(), renamedFolder.name());
    }

    @Test
    public void testMoveFolder() throws IOException, ApiError {
        RemoteFolder remoteFolder1 = createRemoteFolder();
        RemoteFolder remoteFolder2 = createRemoteFolder();

        apiClient.moveFolder(remoteFolder1, remoteFolder2).execute();

        assertTrue(entryExistsInFolder(remoteFolder1, remoteFolder2.folderId()));
        assertFalse(entryExistsInFolder(remoteFolder1, RemoteFolder.ROOT_FOLDER_ID));
    }

    @Test
    public void testCopyFolder() throws IOException, ApiError {
        RemoteFolder remoteFolder1 = createRemoteFolder();
        RemoteFolder remoteFolder2 = createRemoteFolder();

        apiClient.copyFolder(remoteFolder1, remoteFolder2).execute();

        assertTrue(entryExistsInFolder(remoteFolder1, remoteFolder2.folderId()));
        assertTrue(entryExistsInFolder(remoteFolder1, RemoteFolder.ROOT_FOLDER_ID));
    }


    @Test
    public void testCreateFile() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        RemoteFile remoteFile = apiClient.createFile(RemoteFolder.ROOT_FOLDER_ID, someName + ".txt", DataSource.create(fileContents)).execute();

        assertTrue(entryExistsInRoot(remoteFile));
        assertEquals(fileContents.length, remoteFile.size());
    }

    @Test
    public void testCreateFileWithDate() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        Date dateModified = new Date(1484902140000L);//Random date
        ProgressListener listener = Mockito.mock(ProgressListener.class);
        RemoteFile remoteFile = apiClient.createFile(RemoteFolder.ROOT_FOLDER_ID, someName + ".txt", DataSource.create(fileContents), dateModified, listener).execute();

        assertTrue(entryExistsInRoot(remoteFile));
        assertEquals(fileContents.length, remoteFile.size());
        assertEquals(dateModified, remoteFile.created());
    }

    @Test
    public void testCreateFileWithOverrideOption() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString() + ".txt";
        byte[] fileContents = someName.getBytes();
        byte[] file2Contents = UUID.randomUUID().toString().getBytes();
        RemoteFile remoteFile = apiClient.createFile(RemoteFolder.ROOT_FOLDER_ID, someName, DataSource.create(fileContents), UploadOptions.OVERRIDE_FILE).execute();
        RemoteFile remoteFile2 = apiClient.createFile(RemoteFolder.ROOT_FOLDER_ID, someName, DataSource.create(file2Contents), UploadOptions.OVERRIDE_FILE).execute();

        assertFalse(entryExistsInRoot(remoteFile));
        assertTrue(entryExistsInRoot(remoteFile2));
        assertEquals(someName, remoteFile2.name());
        assertEquals(file2Contents.length, remoteFile2.size());
    }

    @Test
    public void testDeleteFile() throws IOException, ApiError {
        RemoteFile remoteFile = createRemoteFile();

        Boolean isDeleted = apiClient.deleteFile(remoteFile).execute();

        assertTrue(isDeleted);
        assertFalse(entryExistsInRoot(remoteFile));
    }

    @Test
    public void testMoveFile() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();
        RemoteFile remoteFile = createRemoteFile();

        apiClient.moveFile(remoteFile, remoteFolder).execute();

        assertTrue(entryExistsInFolder(remoteFile, remoteFolder.folderId()));
        assertFalse(entryExistsInFolder(remoteFile, RemoteFolder.ROOT_FOLDER_ID));
    }


    @Test
    public void testCopyFile() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();
        RemoteFile remoteFile = createRemoteFile();

        RemoteFile copiedFile = apiClient.copyFile(remoteFile, remoteFolder).execute();

        assertTrue(entryExistsInFolder(copiedFile, remoteFolder.folderId()));
        assertTrue(entryExistsInFolder(remoteFile, RemoteFolder.ROOT_FOLDER_ID));
    }


    @Test
    public void testRenameFile() throws IOException, ApiError {
        RemoteFile remoteFile = createRemoteFile();
        String randomNewName = UUID.randomUUID().toString();

        RemoteFile renamedFile = apiClient.renameFile(remoteFile, randomNewName + ".txt").execute();

        assertEquals(remoteFile.fileId(), renamedFile.fileId());
        assertNotEquals(remoteFile.name(), renamedFile.name());
    }

    @Test
    public void testStatFileWithId() throws IOException, ApiError {
        RemoteFile remoteFile = createRemoteFile();
        String randomNewName = UUID.randomUUID().toString();

        RemoteFile fetchedFile = apiClient.stat(remoteFile.fileId()).execute();

        assertEquals(remoteFile.fileId(), fetchedFile.fileId());
    }

    @Test
    public void testStatFileWithPath() throws IOException, ApiError {
        RemoteFile remoteFile = createRemoteFile();

        RemoteFile fetchedFile = apiClient.stat("/" + remoteFile.name()).execute();

        assertEquals(remoteFile.fileId(), fetchedFile.fileId());
    }

    @Test
    public void testDownloadFileFromLink() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        RemoteFile remoteFile = apiClient.createFile(RemoteFolder.ROOT_FOLDER_ID, someName + ".txt", DataSource.create(fileContents)).execute();
        DownloadOptions options = DownloadOptions.create()
                .skipFilename(true)
                .forceDownload(false)
                .contentType(remoteFile.contentType())
                .build();

        FileLink fileLink = apiClient.createFileLink(remoteFile, options).execute();
        BufferedSource source = apiClient.download(fileLink).execute();
        assertArrayEquals(fileContents, source.readByteArray());
    }

    @Test
    public void testDownloadFile() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        RemoteFile remoteFile = apiClient.createFile(RemoteFolder.ROOT_FOLDER_ID, someName + ".txt", DataSource.create(fileContents)).execute();

        BufferedSource source = apiClient.download(remoteFile).execute();
        assertArrayEquals(fileContents, source.readByteArray());
    }

    private RemoteFolder createRemoteFolder() throws IOException, ApiError {
        return createRemoteFolder(RemoteFolder.ROOT_FOLDER_ID);
    }

    private RemoteFolder createRemoteFolder(long parentFolderId) throws IOException, ApiError {
        String randomFolderName = UUID.randomUUID().toString();
        return apiClient.createFolder(parentFolderId, randomFolderName).execute();
    }

    private RemoteFile createRemoteFile() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        return apiClient.createFile(RemoteFolder.ROOT_FOLDER_ID, someName + ".txt", DataSource.create(fileContents)).execute();
    }

    private boolean entryExistsInRoot(RemoteEntry entry) throws IOException, ApiError {
        return entryExistsInFolder(entry, RemoteFolder.ROOT_FOLDER_ID);
    }

    private boolean entryExistsInFolder(RemoteEntry entry, long parentFolderId) throws IOException, ApiError {
        RemoteFolder root = apiClient.listFolder(parentFolderId).execute();
        for (RemoteEntry e : root.children()) {
            if (e.equals(entry)) {
                return true;
            }
        }
        return false;
    }

    @After
    public void shutDown() {
        apiClient.shutdown();
    }

}