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

import com.pcloud.sdk.ApiError;
import com.pcloud.sdk.DataSink;
import com.pcloud.sdk.DownloadOptions;
import com.pcloud.sdk.FileLink;
import com.pcloud.sdk.ProgressListener;
import com.pcloud.sdk.RemoteEntry;
import com.pcloud.sdk.RemoteFile;
import com.pcloud.sdk.RemoteFolder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import okio.BufferedSource;

public class DummyFile implements RemoteFile {

    private int fileId;

    private String name;

    public DummyFile(int fileId, String name) {
        this.fileId = fileId;
        this.name = name;
    }

    @Override
    public String id() {
        return "f" + fileId;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Date lastModified() {
        return null;
    }

    @Override
    public Date created() {
        return null;
    }

    @Override
    public long parentFolderId() {
        return 0;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean isFolder() {
        return false;
    }

    @Override
    public RemoteFolder asFolder() {
        return null;
    }

    @Override
    public RemoteFile asFile() {
        return this;
    }

    @Override
    public RemoteEntry copy(RemoteFolder toFolder) throws IOException {
        return null;
    }

    @Override
    public RemoteEntry copy(RemoteFolder toFolder, boolean overwrite) throws IOException {
        return null;
    }

    @Override
    public RemoteEntry move(RemoteFolder toFolder) throws IOException {
        return null;
    }

    @Override
    public RemoteEntry rename(String newFilename) throws IOException {
        return null;
    }

    @Override
    public boolean delete() throws IOException {
        return false;
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public boolean canModify() {
        return true;
    }

    @Override
    public boolean canDelete() {
        return true;
    }

    @Override
    public boolean isMine() {
        return true;
    }

    @Override
    public boolean isShared() {
        return false;
    }

    @Override
    public InputStream byteStream() throws IOException {
        return null;
    }

    @Override
    public BufferedSource source() throws IOException {
        return null;
    }

    @Override
    public void download(DataSink sink, ProgressListener listener) throws IOException {

    }

    @Override
    public void download(DataSink sink) throws IOException {

    }

    @Override
    public long fileId() {
        return fileId;
    }

    @Override
    public String contentType() {
        return null;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public String hash() {
        return null;
    }

    @Override
    public FileLink createFileLink(DownloadOptions options) throws IOException, ApiError {
        return null;
    }

    @Override
    public FileLink createFileLink() throws IOException, ApiError {
        return null;
    }
}
