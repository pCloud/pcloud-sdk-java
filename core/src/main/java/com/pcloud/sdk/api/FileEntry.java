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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public abstract class FileEntry {
    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("parentFolderid")
    private long parentFolderId;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("modified")
    private Date lastModified;

    @Expose
    @SerializedName("created")
    private Date created;

    @Expose
    @SerializedName("isfolder")
    private boolean isFolder;

    /**
     * Returns the id for the entry.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name for the entry.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the last modified date for the entry.
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Returns the creation date for the entry.
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Returns the parent-folder id for the entry.
     */
    public long getParentFolderId() {
        return parentFolderId;
    }

    /**
     * Returns {@code true} if this is a file.
     */
    public boolean isFile() {
        return !isFolder;
    }

    /**
     * Returns {@code true} if this is a folder.
     */
    public boolean isFolder() {
        return isFolder;
    }

    /**
     * Returns this FileEntry as {@link RemoteFolder}
     *
     * @return {@link RemoteFolder}
     * @throws IllegalStateException if this method is not overriden
     */
    public RemoteFolder asFolder() {
        throw new IllegalStateException("Entry is not a folder");
    }

    /**
     * Returns this FileEntry as {@link RemoteFile}
     *
     * @return {@link RemoteFile}
     * @throws IllegalStateException if this method is not overriden
     */
    public RemoteFile asFile() {
        throw new IllegalStateException("Entry is not a file");
    }
}
