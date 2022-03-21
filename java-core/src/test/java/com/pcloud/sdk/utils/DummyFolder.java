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

package com.pcloud.sdk.utils;

import com.pcloud.sdk.RemoteEntry;
import com.pcloud.sdk.RemoteFile;
import com.pcloud.sdk.RemoteFolder;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DummyFolder implements RemoteFolder {

    private String name;
    private long folderId;

    public DummyFolder(String name, long folderId) {
        this.name = name;
        this.folderId = folderId;
    }

    @Override
    public String id() {
        return "f" + folderId;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Date lastModified() {
        return null;
    }

    @Override
    public Date created() {
        return null;
    }

    @Override
    public long parentFolderId() {
        return 0;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    @Override
    public RemoteFolder asFolder() {
        return this;
    }

    @Override
    public RemoteFile asFile() {
        return null;
    }

    @Override
    public RemoteEntry copy(RemoteFolder toFolder) throws IOException {
        return null;
    }

    @Override
    public RemoteEntry copy(RemoteFolder toFolder, boolean overwrite) throws IOException {
        return null;
    }

    @Override
    public RemoteEntry move(RemoteFolder toFolder) throws IOException {
        return null;
    }

    @Override
    public RemoteEntry rename(String newFilename) throws IOException {
        return null;
    }

    @Override
    public boolean delete() throws IOException {
        return false;
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public boolean canModify() {
        return true;
    }

    @Override
    public boolean canDelete() {
        return true;
    }

    @Override
    public boolean isMine() {
        return true;
    }

    @Override
    public boolean isShared() {
        return false;
    }

    @Override
    public long folderId() {
        return folderId;
    }

    @Override
    public List<RemoteEntry> children() {
        return Collections.emptyList();
    }

    @Override
    public RemoteFolder reload() throws IOException {
        return null;
    }

    @Override
    public RemoteFolder reload(boolean recursively) throws IOException {
        return null;
    }

    @Override
    public boolean delete(boolean recursively) throws IOException {
        return false;
    }

    @Override
    public boolean canCreate() {
        return false;
    }
}
