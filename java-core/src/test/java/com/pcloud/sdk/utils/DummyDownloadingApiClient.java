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

import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.ApiError;
import com.pcloud.sdk.Authenticator;
import com.pcloud.sdk.Call;
import com.pcloud.sdk.Callback;
import com.pcloud.sdk.DataSink;
import com.pcloud.sdk.DataSource;
import com.pcloud.sdk.DownloadOptions;
import com.pcloud.sdk.FileLink;
import com.pcloud.sdk.ProgressListener;
import com.pcloud.sdk.RemoteEntry;
import com.pcloud.sdk.RemoteFile;
import com.pcloud.sdk.RemoteFolder;
import com.pcloud.sdk.UploadOptions;
import com.pcloud.sdk.UserInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executor;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okio.BufferedSource;
import okio.Okio;

public class DummyDownloadingApiClient implements ApiClient {

    public static ApiClient create(byte[] data) {
        return new DummyDownloadingApiClient(data);
    }

    public static ApiClient create() {
        return new DummyDownloadingApiClient(new byte[0]);
    }

    private static BufferedSource createSource(byte[] data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data)));
    }

    private byte[] data;

    private DummyDownloadingApiClient(byte[] data) {
        this.data = data;
    }

    @Override
    public Call<RemoteFolder> listFolder(long folderId) {
        return null;
    }

    @Override
    public Call<RemoteFolder> listFolder(long folderId, boolean recursively) {
        return null;
    }

    @Override
    public Call<RemoteFolder> listFolder(String path) {
        return null;
    }

    @Override
    public Call<RemoteFolder> listFolder(String path, boolean recursively) {
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
    public Call<RemoteFolder> moveFolder(String path, String toPath) {
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
    public Call<RemoteFolder> copyFolder(long folderId, long toFolderId, boolean overwrite) {
        return null;
    }

    @Override
    public Call<RemoteFolder> copyFolder(RemoteFolder folder, RemoteFolder toFolder, boolean overwrite) {
        return null;
    }

    @Override
    public Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data) {
        return null;
    }

    @Override
    public Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data, UploadOptions uploadOptions) {
        return null;
    }

    @Override
    public Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data, Date modifiedDate, ProgressListener listener) {
        return null;
    }

    @Override
    public Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data, Date modifiedDate, ProgressListener listener, UploadOptions uploadOptions) {
        return null;
    }

    @Override
    public Call<RemoteFile> createFile(long folderId, String filename, DataSource data) {
        return null;
    }

    @Override
    public Call<RemoteFile> createFile(long folderId, String filename, DataSource data, UploadOptions uploadOptions) {
        return null;
    }

    @Override
    public Call<RemoteFile> createFile(long folderId, String filename, DataSource data, Date modifiedDate, ProgressListener listener) {
        return null;
    }

    @Override
    public Call<RemoteFile> createFile(long folderId, String filename, DataSource data, Date modifiedDate, ProgressListener listener, UploadOptions uploadOptions) {
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
    public Call<FileLink> createFileLink(RemoteFile file, DownloadOptions options) {
        return new DummyCall<FileLink>(new DummyDownloadLink());
    }

    @Override
    public Call<FileLink> createFileLink(long fileid, DownloadOptions options) {
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
    public Call<RemoteFile> copyFile(long fileId, long toFolderId, boolean overwrite) {
        return null;
    }

    @Override
    public Call<RemoteFile> copyFile(RemoteFile file, RemoteFolder toFolder) {
        return null;
    }

    @Override
    public Call<RemoteFile> copyFile(RemoteFile file, RemoteFolder toFolder, boolean overwrite) {
        return null;
    }

    @Override
    public Call<RemoteEntry> copy(RemoteEntry file, RemoteFolder toFolder) {
        return null;
    }

    @Override
    public Call<? extends RemoteEntry> copy(RemoteEntry file, RemoteFolder toFolder, boolean overwriteFiles) {
        return null;
    }

    @Override
    public Call<? extends RemoteEntry> copy(String id, long toFolderId) {
        return null;
    }

    @Override
    public Call<? extends RemoteEntry> copy(String id, long toFolderId, boolean overwriteFiles) {
        return null;
    }

    @Override
    public Call<RemoteEntry> move(RemoteEntry file, RemoteFolder toFolder) {
        return null;
    }

    @Override
    public Call<? extends RemoteEntry> move(String id, long toFolderId) {
        return null;
    }

    @Override
    public Call<Boolean> delete(RemoteEntry file) {
        return null;
    }

    @Override
    public Call<Boolean> delete(String id) {
        return null;
    }

    @Override
    public Call<? extends RemoteEntry> rename(RemoteEntry file, String newFilename) {
        return null;
    }

    @Override
    public Call<? extends RemoteEntry> rename(String id, String newFilename) {
        return null;
    }

    @Override
    public Call<RemoteFile> loadFile(long fileid) {
        return null;
    }

    @Override
    public Call<RemoteFile> loadFile(String path) {
        return null;
    }

    @Override
    public Call<RemoteFolder> loadFolder(long folderId) {
        return null;
    }

    @Override
    public Call<RemoteFolder> loadFolder(String path) {
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
    public Call<RemoteFile> moveFile(String path, String toPath) {
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
    public Executor callbackExecutor() {
        return null;
    }

    @Override
    public Dispatcher dispatcher() {
        return null;
    }

    @Override
    public ConnectionPool connectionPool() {
        return null;
    }

    @Override
    public Cache cache() {
        return null;
    }

    @Override
    public int readTimeoutMs() {
        return 0;
    }

    @Override
    public int writeTimeoutMs() {
        return 0;
    }

    @Override
    public int connectTimeoutMs() {
        return 0;
    }

    @Override
    public long progressCallbackThreshold() {
        return 0;
    }

    @Override
    public Authenticator authenticator() {
        return null;
    }

    @Override
    public String apiHost() {
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
                DummyDownloadingApiClient.this.download(this, sink, listener).execute();
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
                listener.onProgress(0, 0);
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
