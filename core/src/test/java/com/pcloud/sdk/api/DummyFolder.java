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

package com.pcloud.sdk.api;

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
    public String getId() {
        return "f" + folderId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Date getLastModified() {
        return null;
    }

    @Override
    public Date getCreated() {
        return null;
    }

    @Override
    public long getParentFolderId() {
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
    public long getFolderId() {
        return folderId;
    }

    @Override
    public List<FileEntry> getChildren() {
        return Collections.emptyList();
    }
}
