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
import com.pcloud.sdk.ApiService;
import com.pcloud.sdk.RemoteEntry;
import com.pcloud.sdk.RemoteFolder;

import java.lang.reflect.Type;
import java.util.List;

public class RealRemoteFolder extends RealRemoteEntry implements RemoteFolder{

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
    public final RealRemoteFolder asFolder() {
        return this;
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
