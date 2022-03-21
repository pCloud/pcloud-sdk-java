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

package com.pcloud.sdk.internal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.ApiError;
import com.pcloud.sdk.RemoteEntry;
import com.pcloud.sdk.RemoteFolder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RealRemoteFolder extends RealRemoteEntry implements RemoteFolder {

    private static final List<RemoteEntry> UNKNOWN_CHILDREN = Collections.unmodifiableList(new ArrayList<>(0));

    @Expose
    @SerializedName("folderid")
    private Long folderId;

    @Expose
    @SerializedName("contents")
    private final List<RemoteEntry> children = UNKNOWN_CHILDREN;

    @Expose
    @SerializedName("cancreate ")
    private boolean canCreate = true;

    RealRemoteFolder(ApiClient apiClient) {
        super(apiClient);
    }

    @Override
    public long folderId() {
        return folderId;
    }

    @Override
    public List<RemoteEntry> children() {
        return children;
    }

    @Override
    public RemoteFolder reload() throws IOException {
        return reload(false);
    }

    @Override
    public RemoteFolder reload(boolean recursively) throws IOException {
        try {
            return ownerClient().listFolder(folderId(), recursively).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public boolean delete(boolean recursively) throws IOException {
        try {
            return ownerClient().deleteFolder(this, recursively).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public final RealRemoteFolder asFolder() {
        return this;
    }

    @Override
    public RemoteFolder copy(RemoteFolder toFolder) throws IOException {
        try {
            return ownerClient().copyFolder(this, toFolder).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public RemoteFolder copy(RemoteFolder toFolder, boolean overwrite) throws IOException {
        try {
            return ownerClient().copyFolder(this, toFolder, overwrite).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public RemoteFolder move(RemoteFolder toFolder) throws IOException {
        try {
            return ownerClient().moveFolder(this, toFolder).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public RemoteFolder rename(String newFilename) throws IOException {
        try {
            return ownerClient().renameFolder(this, newFilename).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public boolean canCreate() {
        return isMine() || canCreate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RealRemoteFolder that = (RealRemoteFolder) o;

        if (!folderId.equals(that.folderId)) return false;
        if (canCreate != that.canCreate) return false;
        return children.equals(that.children);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + folderId.hashCode();
        result = 31 * result + children.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s | ID:%s | Created:%s | Modified: %s | Child count:%s", name(), id(), created(), lastModified(), children == UNKNOWN_CHILDREN ? "?" : children().size());
    }

    static class InstanceCreator implements com.google.gson.InstanceCreator<RealRemoteFolder> {

        private final ApiClient apiClient;

        InstanceCreator(ApiClient apiClient) {
            this.apiClient = apiClient;
        }

        @Override
        public RealRemoteFolder createInstance(Type type) {
            return new RealRemoteFolder(apiClient);
        }
    }
}
