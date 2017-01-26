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
import com.pcloud.sdk.api.*;
import okio.BufferedSource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

class RealRemoteFile extends RealFileEntry implements RemoteFile {

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

    RealRemoteFile(ApiService apiService) {
        super(apiService);
    }

    @Override
    public long getFileId() {
        return fileId;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public FileLink getDownloadLink(DownloadOptions options) throws IOException, ApiError {
        return ownerService().getDownloadLink(fileId, options).execute();
    }

    @Override
    public FileLink getDownloadLink() throws IOException, ApiError {
        return getDownloadLink(DownloadOptions.create()
                .skipFilename(true)
                .forceDownload(false)
                .contentType(getContentType())
                .build());
    }

    @Override
    public RemoteFile asFile() {
        return this;
    }

    @Override
    public InputStream byteStream() throws IOException {
        return source().inputStream();
    }

    @Override
    public BufferedSource source() throws IOException {
        try {
            return ownerService().download(this).execute();
        } catch (ApiError apiError) {
            throw new IOException("API error occurred while trying to download file.", apiError);
        }
    }

    @Override
    public void download(DataSink sink, ProgressListener listener) throws IOException {
        DownloadOptions options = DownloadOptions.create()
                .skipFilename(true)
                .forceDownload(false)
                .contentType(getContentType())
                .build();
        try {
            ownerService().getDownloadLink(this, options).execute().download(sink, listener);
        } catch (ApiError apiError) {
            throw new IOException("API error occurred while trying to download file.", apiError);
        }
    }

    @Override
    public void download(DataSink sink) throws IOException {
        download(sink, null);
    }

    static class InstanceCreator implements com.google.gson.InstanceCreator<RealRemoteFile> {

        private ApiService apiService;

        InstanceCreator(ApiService apiService) {
            this.apiService = apiService;
        }

        @Override
        public RealRemoteFile createInstance(Type type) {
            return new RealRemoteFile(apiService);
        }
    }
}
