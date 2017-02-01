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

import com.pcloud.sdk.*;
import okio.BufferedSource;
import okio.Okio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class DummyDownloadingApiService implements ApiService {

    public static ApiService create(byte[] data){
        return new DummyDownloadingApiService(data);
    }

    public static ApiService create(){
        return new DummyDownloadingApiService(new byte[0]);
    }

    private static BufferedSource createSource(byte[] data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data)));
    }

    private byte[] data;

    private DummyDownloadingApiService(byte[] data) {
        this.data = data;
    }

    @Override
    public Call<RemoteFolder> getFolder(long folderId) {
        return null;
    }

    @Override
    public Call<RemoteFolder> getFolder(long folderId, boolean recursively) {
        return null;
    }

    @Override
    public Call<List<FileEntry>> listFiles(RemoteFolder folder) {
        return null;
    }

    @Override
    public Call<RemoteFolder> createFolder(long parentFolderId, String folderName) {
        return null;
    }

    @Override
    public Call<RemoteFolder> createFolder(RemoteFolder parentFolder, String folderName) {
        return null;
    }

    @Override
    public Call<Boolean> deleteFolder(long folderId) {
        return null;
    }

    @Override
    public Call<Boolean> deleteFolder(long folderId, boolean recursively) {
        return null;
    }

    @Override
    public Call<Boolean> deleteFolder(RemoteFolder folder) {
        return null;
    }

    @Override
    public Call<Boolean> deleteFolder(RemoteFolder folder, boolean recursively) {
        return null;
    }

    @Override
    public Call<RemoteFolder> renameFolder(long folderId, String newFolderName) {
        return null;
    }

    @Override
    public Call<RemoteFolder> renameFolder(RemoteFolder folder, String newFolderName) {
        return null;
    }

    @Override
    public Call<RemoteFolder> moveFolder(long folderId, long toFolderId) {
        return null;
    }

    @Override
    public Call<RemoteFolder> moveFolder(RemoteFolder folder, RemoteFolder toFolder) {
        return null;
    }

    @Override
    public Call<RemoteFolder> copyFolder(long folderId, long toFolderId) {
        return null;
    }

    @Override
    public Call<RemoteFolder> copyFolder(RemoteFolder folder, RemoteFolder toFolder) {
        return null;
    }

    @Override
    public Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data) {
        return null;
    }

    @Override
    public Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data, Date modifiedDate, ProgressListener listener) {
        return null;
    }

    @Override
    public Call<RemoteFile> createFile(long folderId, String filename, DataSource data) {
        return null;
    }

    @Override
    public Call<RemoteFile> createFile(long folderId, String filename, DataSource data, Date modifiedDate, ProgressListener listener) {
        return null;
    }

    @Override
    public Call<Boolean> deleteFile(RemoteFile file) {
        return null;
    }

    @Override
    public Call<Boolean> deleteFile(long fileId) {
        return null;
    }

    @Override
    public Call<FileLink> getDownloadLink(RemoteFile file, DownloadOptions options) {
        return new DummyCall<FileLink>(new DummyDownloadLink());
    }

    @Override
    public Call<FileLink> getDownloadLink(long fileid, DownloadOptions options) {
        return new DummyCall<FileLink>(new DummyDownloadLink());
    }

    @Override
    public Call<Void> download(FileLink fileLink, DataSink sink) {
        return download(fileLink, sink, null);
    }

    @Override
    public Call<Void> download(FileLink fileLink, final DataSink sink, final ProgressListener listener) {
        return new DummyDownloadCall(sink, listener);
    }

    @Override
    public Call<BufferedSource> download(RemoteFile file) {
        return new DummyCall<>(createSource(data));
    }

    @Override
    public Call<BufferedSource> download(FileLink fileLink) {
        return new DummyCall<>(createSource(data));
    }

    @Override
    public Call<RemoteFile> copyFile(long fileId, long toFolderId) {
        return null;
    }

    @Override
    public Call<RemoteFile> copyFile(RemoteFile file, RemoteFolder toFolder) {
        return null;
    }

    @Override
    public Call<RemoteFile> moveFile(long fileId, long toFolderId) {
        return null;
    }

    @Override
    public Call<RemoteFile> moveFile(RemoteFile file, RemoteFolder toFolder) {
        return null;
    }

    @Override
    public Call<RemoteFile> renameFile(long fileId, String newFilename) {
        return null;
    }

    @Override
    public Call<RemoteFile> renameFile(RemoteFile file, String newFilename) {
        return null;
    }

    @Override
    public Call<UserInfo> getUserInfo() {
        return null;
    }

    @Override
    public Builder newBuilder() {
        return null;
    }

    @Override
    public void shutdown() {

    }

    private class DummyDownloadLink extends DummyFileLink {
        @Override
        public void download(DataSink sink) throws IOException {
            this.download(sink, null);
        }

        @Override
        public void download(DataSink sink, ProgressListener listener) throws IOException {
            try {
                DummyDownloadingApiService.this.download(this, sink, listener).execute();
            } catch (ApiError apiError) {
                throw new IOException(apiError);
            }
        }
    }

    private class DummyDownloadCall implements Call<Void> {
        private final DataSink sink;
        private final ProgressListener listener;

        public DummyDownloadCall(DataSink sink, ProgressListener listener) {
            this.sink = sink;
            this.listener = listener;
        }

        @Override
        public Void execute() throws IOException, ApiError {
            writeData();
            return null;
        }

        private void writeData() throws IOException {
            BufferedSource source = createSource(data);
            sink.readAll(source);
            if (listener != null) {
                listener.onProgress(0,0);
            }
        }

        @Override
        public void enqueue(Callback<Void> callback) {
            try {
                writeData();
                callback.onResponse(this, null);
            } catch (IOException e) {
                callback.onFailure(this, e);
            }
        }

        @Override
        public boolean isExecuted() {
            return false;
        }

        @Override
        public void cancel() {

        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public Call<Void> clone() {
            return null;
        }
    }
}
