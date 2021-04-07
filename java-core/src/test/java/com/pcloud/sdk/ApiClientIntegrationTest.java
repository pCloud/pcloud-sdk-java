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
        String apiHost = System.getenv("PCLOUD_TEST_API_HOST");
        apiClient = PCloudSdk.newClientBuilder()
                .authenticator(Authenticators.newOAuthAuthenticator(token))
                .apiHost(apiHost)
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
    public void testListFolderByPath() throws Exception {
        apiClient.listFolder("/").execute();
    }

    @Test
    public void testGetFolderByPath() throws Exception {
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
    public void testCreateFolderByPath() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder("/");

        assertTrue(entryExistsInFolder(remoteFolder, remoteFolder.parentFolderId()));
    }

    @Test
    public void testDeleteFolder() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();
        assertTrue(apiClient.deleteFolder(remoteFolder).execute());
    }

    @Test
    public void testDeleteFolderByPath() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder("/");
        assertTrue(apiClient.deleteFolder("/" + remoteFolder.name()).execute());
    }

    @Test
    public void testDeleteFolderRecursively() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();
        createRemoteFolder(remoteFolder.folderId());
        assertTrue(apiClient.deleteFolder(remoteFolder, true).execute());
    }

    @Test
    public void testDeleteFolderRecursivelyByPath() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder("/");
        createRemoteFolder("/" + remoteFolder.name() + "/");
        assertTrue(apiClient.deleteFolder("/" + remoteFolder.name(), true).execute());
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

        RemoteFolder movedFolder = apiClient.moveFolder(remoteFolder1, remoteFolder2).execute();

        assertTrue(entryExistsInFolder(movedFolder, remoteFolder2.folderId()));
        assertFalse(entryExistsInFolder(remoteFolder1, RemoteFolder.ROOT_FOLDER_ID));
    }

    @Test
    public void testMoveFolderByPath() throws IOException, ApiError {
        RemoteFolder remoteFolder1 = createRemoteFolder("/");
        RemoteFolder remoteFolder2 = createRemoteFolder("/");

        RemoteFolder movedFolder = apiClient.moveFolder("/" + remoteFolder1.name(), "/" + remoteFolder2.name() + "/" + remoteFolder1.name()).execute();

        assertTrue(entryExistsInFolder(movedFolder, remoteFolder2.folderId()));
        assertFalse(entryExistsInFolder(remoteFolder1, RemoteFolder.ROOT_FOLDER_ID));
    }

    @Test
    public void testCopyFolder() throws IOException, ApiError {
        RemoteFolder folderToBeCopied = createRemoteFolder();
        RemoteFolder destinationFolder = createRemoteFolder();

        RemoteFolder copiedFolder = apiClient.copyFolder(folderToBeCopied, destinationFolder).execute();

        assertTrue(entryExistsInFolder(copiedFolder, destinationFolder.folderId()));
        assertTrue(entryExistsInFolder(folderToBeCopied, folderToBeCopied.parentFolderId()));
    }

    @Test
    public void testCreateFile() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        RemoteFile remoteFile = apiClient.createFile(RemoteFolder.ROOT_FOLDER_ID, someName + ".txt", DataSource.create(fileContents)).execute();

        assertTrue(entryExistsInRoot(remoteFile));
        assertEquals(fileContents.length, remoteFile.size());
        assertArrayEquals(fileContents, getFileContent(remoteFile));
    }

    @Test
    public void testCreateFileByPath() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        RemoteFile remoteFile = apiClient.createFile("/", someName + ".txt", DataSource.create(fileContents)).execute();

        assertTrue(entryExistsInRoot(remoteFile));
        assertEquals(fileContents.length, remoteFile.size());
        assertArrayEquals(fileContents, getFileContent(remoteFile));
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
        assertArrayEquals(fileContents, getFileContent(remoteFile));
        assertEquals(dateModified, remoteFile.created());
    }

    @Test
    public void testCreateFileWithDateByPath() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        Date dateModified = new Date(1484902140000L);//Random date
        ProgressListener listener = Mockito.mock(ProgressListener.class);
        RemoteFile remoteFile = apiClient.createFile("/", someName + ".txt", DataSource.create(fileContents), dateModified, listener).execute();

        assertTrue(entryExistsInFolder(remoteFile, RemoteFolder.ROOT_FOLDER_ID));
        assertEquals(fileContents.length, remoteFile.size());
        assertArrayEquals(fileContents, getFileContent(remoteFile));
        assertEquals(dateModified, remoteFile.created());
    }

    @Test
    public void testCreateFileWithOverrideOption() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString() + ".txt";
        byte[] fileContents = UUID.randomUUID().toString().getBytes();
        byte[] file2Contents = UUID.randomUUID().toString().getBytes();

        RemoteFile remoteFile = apiClient.createFile(RemoteFolder.ROOT_FOLDER_ID, someName, DataSource.create(fileContents), UploadOptions.OVERRIDE_FILE).execute();
        assertEquals(someName, remoteFile.name());
        assertTrue(entryExistsInRoot(remoteFile));
        assertEquals(fileContents.length, remoteFile.size());
        assertArrayEquals(fileContents, getFileContent(remoteFile));

        RemoteFile remoteFile2 = apiClient.createFile(RemoteFolder.ROOT_FOLDER_ID, someName, DataSource.create(file2Contents), UploadOptions.OVERRIDE_FILE).execute();
        assertEquals(someName, remoteFile2.name());
        assertEquals(file2Contents.length, remoteFile2.size());
        assertArrayEquals(file2Contents, getFileContent(remoteFile2));
        if (remoteFile.fileId() != remoteFile2.fileId()) {
            assertFalse(entryExistsInRoot(remoteFile));
        }
    }

    @Test
    public void testCreateFileWithOverrideOptionByPath() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString() + ".txt";
        byte[] fileContents = UUID.randomUUID().toString().getBytes();
        byte[] file2Contents = UUID.randomUUID().toString().getBytes();

        RemoteFile remoteFile = apiClient.createFile("/", someName, DataSource.create(fileContents), UploadOptions.OVERRIDE_FILE).execute();
        assertEquals(someName, remoteFile.name());
        assertTrue(entryExistsInRoot(remoteFile));
        assertEquals(fileContents.length, remoteFile.size());
        assertArrayEquals(fileContents, getFileContent(remoteFile));

        RemoteFile remoteFile2 = apiClient.createFile("/", someName, DataSource.create(file2Contents), UploadOptions.OVERRIDE_FILE).execute();
        assertEquals(someName, remoteFile2.name());
        assertEquals(file2Contents.length, remoteFile2.size());
        assertArrayEquals(file2Contents, getFileContent(remoteFile2));
        if (remoteFile.fileId() != remoteFile2.fileId()) {
            assertFalse(entryExistsInRoot(remoteFile));
        }
    }

    @Test
    public void testDeleteFile() throws IOException, ApiError {
        RemoteFile remoteFile = createRemoteFile();

        Boolean isDeleted = apiClient.deleteFile(remoteFile).execute();

        assertTrue(isDeleted);
        assertFalse(entryExistsInRoot(remoteFile));
    }

    @Test
    public void testDeleteFileByPath() throws IOException, ApiError {
        RemoteFile remoteFile = createRemoteFile("/");

        Boolean isDeleted = apiClient.deleteFile("/" + remoteFile.name()).execute();

        assertTrue(isDeleted);
        assertFalse(entryExistsInRoot(remoteFile));
    }

    @Test
    public void testMoveFile() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();
        RemoteFile remoteFile = createRemoteFile();

        RemoteFile movedFile = apiClient.moveFile(remoteFile, remoteFolder).execute();

        assertTrue(entryExistsInFolder(movedFile, remoteFolder.folderId()));
        assertFalse(entryExistsInFolder(remoteFile, RemoteFolder.ROOT_FOLDER_ID));
    }

    @Test
    public void testMoveFileByPath() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder("/");
        RemoteFile remoteFile = createRemoteFile("/");

        RemoteFile movedFile = apiClient.moveFile("/" + remoteFile.name(), "/" + remoteFolder.name() + "/").execute();

        assertTrue(entryExistsInFolder(movedFile, remoteFolder.folderId()));
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
    public void testLoadFileWithId() throws IOException, ApiError {
        RemoteFile remoteFile = createRemoteFile();
        RemoteFile fetchedFile = apiClient.loadFile(remoteFile.fileId()).execute();

        assertEquals(remoteFile.fileId(), fetchedFile.fileId());
    }

    @Test
    public void testLoadFileWithPath() throws IOException, ApiError {
        RemoteFile remoteFile = createRemoteFile("/");

        RemoteFile fetchedFile = apiClient.loadFile("/" + remoteFile.name()).execute();

        assertEquals(remoteFile.fileId(), fetchedFile.fileId());
    }

    @Test
    public void testLoadFolderWithId() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder();

        RemoteFolder fetchedFolder = apiClient.loadFolder(remoteFolder.folderId()).execute();

        assertEquals(remoteFolder.folderId(), fetchedFolder.folderId());
        assertTrue(fetchedFolder.children().isEmpty());
    }

    @Test
    public void testLoadFolderWithPath() throws IOException, ApiError {
        RemoteFolder remoteFolder = createRemoteFolder("/");

        RemoteFolder fetchedFolder = apiClient.loadFolder("/" + remoteFolder.name()).execute();

        assertEquals(remoteFolder.folderId(), fetchedFolder.folderId());
        assertTrue(fetchedFolder.children().isEmpty());
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
    public void testDownloadFileFromLinkByPath() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        RemoteFile remoteFile = apiClient.createFile("/", someName + ".txt", DataSource.create(fileContents)).execute();
        DownloadOptions options = DownloadOptions.create()
                .skipFilename(true)
                .forceDownload(false)
                .contentType(remoteFile.contentType())
                .build();

        FileLink fileLink = apiClient.createFileLink("/" + remoteFile.name(), options).execute();
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

    private RemoteFolder createRemoteFolder(String path) throws IOException, ApiError {
        String randomFolderName = UUID.randomUUID().toString();
        return apiClient.createFolder(path + randomFolderName).execute();
    }

    private RemoteFile createRemoteFile() throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        return apiClient.createFile(RemoteFolder.ROOT_FOLDER_ID, someName + ".txt", DataSource.create(fileContents)).execute();
    }

    private RemoteFile createRemoteFile(long parentFolderId) throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        return apiClient.createFile(parentFolderId, someName + ".txt", DataSource.create(fileContents)).execute();
    }

    private RemoteFile createRemoteFile(String path) throws IOException, ApiError {
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        return apiClient.createFile(path, someName + ".txt", DataSource.create(fileContents)).execute();
    }

    private boolean entryExistsInRoot(RemoteEntry entry) throws IOException, ApiError {
        return entryExistsInFolder(entry, RemoteFolder.ROOT_FOLDER_ID);
    }

    private boolean entryExistsInFolder(RemoteEntry entry, long parentFolderId) throws IOException, ApiError {
        try {
            RemoteEntry entryFromApi;
            if (entry.isFile()) {
                entryFromApi = apiClient.loadFile(entry.asFile().fileId()).execute();
            } else {
                entryFromApi = apiClient.loadFolder(entry.asFolder().folderId()).execute();
            }
            return entryFromApi.parentFolderId() == parentFolderId;
        } catch (ApiError apiError) {
            if (apiError.errorCode() == 2009) {
                return false;
            } else {
                throw apiError;
            }
        }
    }

    private byte[] getFileContent(RemoteFile file) throws IOException, ApiError {
        try (BufferedSource source = apiClient.download(file).execute()) {
            return source.readByteArray();
        }
    }

    @After
    public void shutDown() {
        apiClient.shutdown();
    }

}