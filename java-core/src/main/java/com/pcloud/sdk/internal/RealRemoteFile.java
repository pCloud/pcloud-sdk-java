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
import okio.BufferedSource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Locale;

class RealRemoteFile extends RealRemoteEntry implements RemoteFile {

    @Expose
    @SerializedName("fileid")
    private long fileId;

    @Expose
    @SerializedName("contenttype")
    private String contentType;

    @Expose
    @SerializedName("size")
    private long size;

    @Expose
    @SerializedName("hash")
    private String hash;

    @Expose
    @SerializedName("thumb")
    private Boolean hasThumbnail;

    RealRemoteFile(ApiClient apiClient) {
        super(apiClient);
    }

    @Override
    public long fileId() {
        return fileId;
    }

    @Override
    public String contentType() {
        return contentType;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public String hash() {
        return hash;
    }

    @Override
    public boolean hasThumbnail() {
        return hasThumbnail;
    }

    @Override
    public FileLink createFileLink(DownloadOptions options) throws IOException, ApiError {
        return ownerClient().createFileLink(fileId, options).execute();
    }

    @Override
    public FileLink createFileLink() throws IOException, ApiError {
        return createFileLink(DownloadOptions.create()
                .skipFilename(true)
                .forceDownload(false)
                .contentType(contentType())
                .build());
    }

    @Override
    public RemoteFile asFile() {
        return this;
    }

    @Override
    public RemoteFile copy(RemoteFolder toFolder) throws IOException {
        return copy(toFolder, false);
    }

    @Override
    public RemoteFile copy(RemoteFolder toFolder, boolean overwrite) throws IOException {
        try {
            return ownerClient().copyFile(this, toFolder, overwrite).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public RemoteFile move(RemoteFolder toFolder) throws IOException {
        try {
            return ownerClient().moveFile(this, toFolder).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public RemoteFile rename(String newFilename) throws IOException {
        try {
            return ownerClient().renameFile(this, newFilename).execute();
        } catch (ApiError apiError) {
            throw new IOException(apiError);
        }
    }

    @Override
    public InputStream byteStream() throws IOException {
        return source().inputStream();
    }

    @Override
    public BufferedSource source() throws IOException {
        boolean success = false;
        Call<BufferedSource> call = ownerClient().download(this);
        try {
            BufferedSource source = call.execute();
            success = true;
            return source;
        } catch (ApiError apiError) {
            throw new IOException("API error occurred while trying to download file.", apiError);
        } finally {
            if (!success) {
                call.cancel();
            }
        }
    }

    @Override
    public void download(DataSink sink, ProgressListener listener) throws IOException {
        DownloadOptions options = DownloadOptions.create()
                .skipFilename(true)
                .forceDownload(false)
                .contentType(contentType())
                .build();
        try {
            ownerClient().createFileLink(this, options).execute().download(sink, listener);
        } catch (ApiError apiError) {
            throw new IOException("API error occurred while trying to download file.", apiError);
        }
    }

    @Override
    public void download(DataSink sink) throws IOException {
        download(sink, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RealRemoteFile that = (RealRemoteFile) o;

        if (fileId != that.fileId) return false;
        if (size != that.size) return false;
        if (!contentType.equals(that.contentType)) return false;
        return hash.equals(that.hash);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (fileId ^ (fileId >>> 32));
        result = 31 * result + contentType.hashCode();
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + hash.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s | ID:%s | Created:%s | Modified: %s | Size:%s", name(), id(), created(), lastModified(), size());
    }

    static class InstanceCreator implements com.google.gson.InstanceCreator<RealRemoteFile> {

        private final ApiClient apiClient;

        InstanceCreator(ApiClient apiClient) {
            this.apiClient = apiClient;
        }

        @Override
        public RealRemoteFile createInstance(Type type) {
            return new RealRemoteFile(apiClient);
        }
    }
}
