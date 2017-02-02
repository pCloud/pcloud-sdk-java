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
import com.pcloud.sdk.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class RealRemoteFolder extends RealRemoteEntry implements RemoteFolder {

    @Expose
    @SerializedName("folderid")
    private Long folderId;

    @Expose
    @SerializedName("contents")
    private List<RemoteEntry> children;

    RealRemoteFolder(ApiService apiService) {
        super(apiService);
    }

    @Override
    public long getFolderId() {
        return folderId;
    }

    @Override
    public List<RemoteEntry> getChildren() {
        return children;
    }

    @Override
    public RemoteFolder reload() throws IOException {
        return reload(false);
    }

    @Override
    public RemoteFolder reload(boolean recursively) throws IOException {
        try {
            return ownerService().listFolder(getFolderId(), recursively).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public boolean delete(boolean recursively) throws IOException {
        try {
            return ownerService().deleteFolder(this, recursively).execute();
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
            return ownerService().copyFolder(this, toFolder).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public RemoteFolder copy(RemoteFolder toFolder, boolean overwrite) throws IOException {
        try {
            return ownerService().copyFolder(this, toFolder, overwrite).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public RemoteFolder move(RemoteFolder toFolder) throws IOException {
        try {
            return ownerService().moveFolder(this, toFolder).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public RemoteFolder rename(String newFilename) throws IOException {
        try {
            return ownerService().renameFolder(this, newFilename).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    static class InstanceCreator implements com.google.gson.InstanceCreator<RealRemoteFolder> {

        private ApiService apiService;

        InstanceCreator(ApiService apiService) {
            this.apiService = apiService;
        }

        @Override
        public RealRemoteFolder createInstance(Type type) {
            return new RealRemoteFolder(apiService);
        }
    }
}
