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

import okio.BufferedSource;

import java.util.List;

public interface ApiService {

    Call<RemoteFolder> getFolder(long folderId);

    Call<List<FileEntry>> listFiles(RemoteFolder folder);

    Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data);

    Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data, ProgressListener listener);

    Call<RemoteFile> createFile(long folderId, String filename, DataSource data);

    Call<RemoteFile> createFile(long folderId, String filename, DataSource data, ProgressListener listener);

    Call<Boolean> deleteFile(RemoteFile file);

    Call<Boolean> deleteFile(long fileId);

    Call<FileLink> getDownloadLink(RemoteFile file, DownloadOptions options);

    Call<FileLink> getDownloadLink(long fileid, DownloadOptions options);

    Call<Void> download(FileLink fileLink, DataSink sink);

    Call<Void> download(FileLink fileLink, DataSink sink, ProgressListener listener);

    Call<BufferedSource> download(RemoteFile file);

    Call<BufferedSource> download(FileLink fileLink);

    ApiServiceBuilder newBuilder();
}
