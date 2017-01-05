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

import java.util.Date;

public abstract class FileEntry {

    private String id;
    private long parentFolderId;
    private String name;
    private Date lastModified;
    private Date created;
    private boolean isFolder;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Date getCreated() {
        return created;
    }

    public long getParentFolderId() {
        return parentFolderId;
    }

    public boolean isFile(){
        return !isFolder;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public RemoteFolder asFolder(){
        throw new IllegalStateException("Entry is not a folder");
    }

    public RemoteFile asFile(){
        throw new IllegalStateException("Entry is not a file");
    }
}
