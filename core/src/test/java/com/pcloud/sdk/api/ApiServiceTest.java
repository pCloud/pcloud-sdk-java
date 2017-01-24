package com.pcloud.sdk.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Georgi on 24.1.2017 г..
 */
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
        assertNotNull(instance.listFiles(mockFolder("folder", 1234)));
    }

    @Test
    public void listFiles_ThrowsOnNullRemoteFolderArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.listFiles(null);
    }

    @Test
    public void createFolder_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.createFolder(mockFolder("folder", 1234), "new"));
        assertNotNull(instance.createFolder(1234, "new"));
    }

    @Test
    public void createFolder_ThrowsOnNullFolderName() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.createFolder(0, null);
    }

    @Test
    public void createFolder_ThrowsOnNullRemoteFolderArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.createFolder(null, null);
    }

    @Test
    public void deleteFolder_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.deleteFolder(mockFolder("bye", 1)));
        assertNotNull(instance.deleteFolder(1));
    }

    @Test
    public void deleteFolder_ThrowsOnNullRemoteFolderArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.deleteFolder(null);
    }

    @Test
    public void renameFolder_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.renameFolder(mockFolder("name", 1), "someName"));
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
    public void moveFolder_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.moveFolder(1, 2));
        assertNotNull(instance.moveFolder(mockFolder("1", 1), mockFolder("2", 2)));
    }

    @Test
    public void moveFolder_ThrowsOnNullRemoteFolderArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.moveFolder(null, null);
    }

    @Test
    public void copyFolder_ReturnsANonNullCall() throws Exception {
        assertNotNull(instance.copyFolder(1, 2));
        assertNotNull(instance.moveFolder(mockFolder("1", 1), mockFolder("2", 2)));
    }

    @Test
    public void copyFolder_ThrowsOnNullRemoteFolderArgument() throws Exception {
        exceptionRule.expect(IllegalArgumentException.class);
        instance.copyFolder(null, null);
    }

    

    private static RemoteFolder mockFolder(String name, long folderId){
        RemoteFolder mock = mock(RemoteFolder.class);
        when(mock.getFolderId()).thenReturn(folderId);
        when(mock.getId()).thenReturn("f"+folderId);
        when(mock.isFolder()).thenReturn(true);
        when(mock.isFile()).thenReturn(false);
        when(mock.getName()).thenReturn(name);
        when(mock.asFolder()).thenReturn(mock);
        when(mock.getChildren()).thenReturn(Collections.<FileEntry>emptyList());
        return mock;
    }
}