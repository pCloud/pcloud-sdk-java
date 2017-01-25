package com.pcloud.sdk.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public abstract class ApiServiceTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private ApiService instance;

    protected abstract ApiService createInstance();

    @Before
    public void setUp() throws Exception {
        instance = createInstance();
    }

    @After
    public void tearDown() throws Exception {
        instance.shutdown();
    }

    @Test
    public void getFolder_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.getFolder(0));
    }

    @Test
    public void listFiles_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.listFiles(new DummyFolder("folder", 1234)));
    }

    @Test
    public void listFiles_ThrowsOnNullRemoteFolderArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.listFiles(null);
    }

    @Test
    public void createFolder_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.createFolder(new DummyFolder("folder", 1234), "new"));
        assertNotNull(instance.createFolder(1234, "new"));
    }

    @Test
    public void createFolder_ThrowsOnNullFolderName() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.createFolder(0, null);
    }

    @Test
    public void createFolder_ThrowsOnNullFolderName2() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.createFolder(new DummyFolder("name", 2), null);
    }

    @Test
    public void createFolder_ThrowsOnNullRemoteFolderArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.createFolder(null, null);
    }

    @Test
    public void deleteFolder_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.deleteFolder(new DummyFolder("bye", 1)));
        assertNotNull(instance.deleteFolder(1));
    }

    @Test
    public void deleteFolder_ThrowsOnNullRemoteFolderArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.deleteFolder(null);
    }

    @Test
    public void renameFolder_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.renameFolder(new DummyFolder("name", 1), "someName"));
        assertNotNull(instance.renameFolder(1, "someName"));
    }

    @Test
    public void renameFolder_ThrowsOnNullRemoteFolderArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.renameFolder(null, null);
    }

    @Test
    public void renameFolder_ThrowsOnNullFolderName() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.renameFolder(0, null);
    }

    @Test
    public void renameFolder_ThrowsOnNullFolderName2() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.renameFolder(new DummyFolder("name", 1), null);
    }

    @Test
    public void moveFolder_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.moveFolder(1, 2));
        assertNotNull(instance.moveFolder(new DummyFolder("1", 1), new DummyFolder("2", 2)));
    }

    @Test
    public void moveFolder_ThrowsOnNullRemoteFolderArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.moveFolder(null, null);
    }

    @Test
    public void copyFolder_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.copyFolder(1, 2));
        assertNotNull(instance.copyFolder(new DummyFolder("1", 1), new DummyFolder("2", 2)));
    }

    @Test
    public void copyFolder_ThrowsOnNullRemoteFolderArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.copyFolder(null, null);
    }

    @Test
    public void createFile_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.createFile(1, "somename", new DummyDataSource()));
        assertNotNull(instance.createFile(1, "somename", new DummyDataSource(), null, null));
    }

    @Test
    public void createFile_ThrowsOnNullFilename() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.createFile(1, null, new DummyDataSource());
    }

    @Test
    public void createFile_ThrowsOnNullFilename2() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.createFile(1, null, new DummyDataSource(), new Date(), null);
    }

    @Test
    public void createFile_ThrowsOnNullDataSource() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.createFile(1, "somename", null);
    }

    @Test
    public void createFile_ThrowsOnNullDataSource2() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.createFile(1, "somename", null, new Date(), null);
    }

    @Test
    public void createFile_DoesNotThrowOnNullDate() {
        instance.createFile(1, "somename", new DummyDataSource(), null, null);
    }

    @Test
    public void createFile_DoesNotThrowOnNullProgressListener() {
        instance.createFile(1, "somename", new DummyDataSource(), null, null);
    }

    @Test
    public void deleteFile_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.deleteFile(1));
        assertNotNull(instance.deleteFile(new DummyFile(1, "somename")));
    }

    @Test
    public void deleteFile_ThrowsOnNullRemoteFileArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.deleteFile(null);
    }

    @Test
    public void getDownloadLink_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.getDownloadLink(1, DownloadOptions.DEFAULT));
        assertNotNull(instance.getDownloadLink(new DummyFile(1, "somename"), DownloadOptions.DEFAULT));
    }

    @Test
    public void getDownloadLink_ThrowsOnNullRemoteFileArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.getDownloadLink(null, DownloadOptions.DEFAULT);
    }

    @Test
    public void getDownloadLink_ThrowsOnNullDownloadOptionsArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.getDownloadLink(1, null);
    }

    @Test
    public void getDownloadLink_ThrowsOnNullDownloadOptionsArgument2() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.getDownloadLink(new DummyFile(1, "somename"), null);
    }

    @Test
    public void download_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.download(new DummyFileLink(), new DummyDataSink()));
        assertNotNull(instance.download(new DummyFileLink(), new DummyDataSink(), null));
        assertNotNull(instance.download(new DummyFile(1, "somename")));
        assertNotNull(instance.download(new DummyFileLink()));
    }

    @Test
    public void download_ThrowsOnNullFileLinkArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.download((FileLink) null);
    }

    @Test
    public void download_ThrowsOnNullFileLinkArgument2() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.download(null, new DummyDataSink());
    }

    @Test
    public void download_ThrowsOnNullFileLinkArgument3() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.download(null, new DummyDataSink(), null);
    }

    @Test
    public void download_ThrowsOnNullRemoteFileArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.download((RemoteFile) null);
    }

    @Test
    public void download_DoesNotThrowOnNullProgressListener() {
        instance.download(new DummyFileLink(), new DummyDataSink(), null);
    }

    @Test
    public void copyFile_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.copyFile(1, 2));
        assertNotNull(instance.copyFile(new DummyFile(1, "somename"), new DummyFolder("2", 2)));
    }

    @Test
    public void copyFile_ThrowsOnNullRemoteFileArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.copyFile(null, new DummyFolder("someFolder", 2));
    }

    @Test
    public void copyFile_ThrowsOnNullRemoteFolderArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.copyFile(new DummyFile(1, "somename"), null);
    }

    @Test
    public void moveFile_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.moveFile(1, 2));
        assertNotNull(instance.moveFile(new DummyFile(1, "somename"), new DummyFolder("2", 2)));
    }

    @Test
    public void moveFile_ThrowsOnNullRemoteFileArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.moveFile(null, new DummyFolder("someFolder", 2));
    }

    @Test
    public void moveFile_ThrowsOnNullRemoteFolderArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.moveFile(new DummyFile(1, "somename"), null);
    }

    @Test
    public void renameFile_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.renameFile(1, "newName"));
        assertNotNull(instance.renameFile(new DummyFile(1, "somename"), "newName"));
    }

    @Test
    public void renameFile_ThrowsOnNullFilenameArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.renameFile(1, null);
    }

    @Test
    public void renameFile_ThrowsOnNullFilenameArgument2() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.renameFile(new DummyFile(1, "somename"), null);
    }

    @Test
    public void renameFile_ThrowsOnNullRemoteFileArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.renameFile(null, "somename");
    }

    @Test
    public void getUserInfo_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.getUserInfo());
    }

    @Test
    public void newBuilder_ReturnsNonNullBuilder() throws Exception {
        assertNotNull(instance.newBuilder());
    }

}