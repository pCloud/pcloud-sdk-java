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

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.pcloud.sdk.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

abstract class RealRemoteEntry implements RemoteEntry {

    private ApiClient apiClient;

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("parentfolderid")
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

    protected RealRemoteEntry(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Date lastModified() {
        return lastModified;
    }

    @Override
    public Date created() {
        return created;
    }

    @Override
    public long parentFolderId() {
        return parentFolderId;
    }

    @Override
    public boolean isFile() {
        return !isFolder;
    }

    @Override
    public boolean isFolder() {
        return isFolder;
    }

    @Override
    public RemoteFolder asFolder() {
        throw new IllegalStateException("This entry is not a folder");
    }

    @Override
    public RemoteFile asFile() {
        throw new IllegalStateException("This entry is not a file");
    }

    @Override
    public boolean delete() throws IOException {
        try {
            return apiClient.delete(this).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    protected ApiClient ownerClient(){
        return apiClient;
    }

    static class TypeAdapterFactory implements com.google.gson.TypeAdapterFactory {

        private static final TypeToken<RemoteEntry> FILE_ENTRY_TYPE_TOKEN = new TypeToken<RemoteEntry>(){};
        private static final TypeToken<RemoteFile> REMOTE_FILE_TYPE_TOKEN = new TypeToken<RemoteFile>(){};
        private static final TypeToken<RemoteFolder> REMOTE_FOLDER_TYPE_TOKEN = new TypeToken<RemoteFolder>(){};

        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (FILE_ENTRY_TYPE_TOKEN.equals(type)){
                return gson.getAdapter((TypeToken<T>) FILE_ENTRY_TYPE_TOKEN);
            } else if (REMOTE_FILE_TYPE_TOKEN.equals(type)) {
                return gson.getAdapter((Class<T>) RealRemoteFile.class);
            } else if (REMOTE_FOLDER_TYPE_TOKEN.equals(type)) {
                return gson.getAdapter((Class<T>) RealRemoteFolder.class);
            }

            return null;
        }
    }

    static class FileEntryDeserializer implements JsonDeserializer<RemoteEntry> {

        @Override
        public RemoteEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if(json.getAsJsonObject().get("isfolder").getAsBoolean()){
                return context.deserialize(json, RealRemoteFolder.class);
            }else{
                return context.deserialize(json, RealRemoteFile.class);
            }
        }
    }
}
