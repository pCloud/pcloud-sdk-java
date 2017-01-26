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

import com.pcloud.sdk.api.*;
import okio.BufferedSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class DummyFile implements RemoteFile {

    private int fileId;

    private String name;

    public DummyFile(int fileId, String name) {
        this.fileId = fileId;
        this.name = name;
    }

    @Override
    public String getId() {
        return "f" + fileId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Date getLastModified() {
        return null;
    }

    @Override
    public Date getCreated() {
        return null;
    }

    @Override
    public long getParentFolderId() {
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
    public long getFileId() {
        return fileId;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public String getHash() {
        return null;
    }

    @Override
    public FileLink getDownloadLink(DownloadOptions options) throws IOException, ApiError {
        return null;
    }

    @Override
    public FileLink getDownloadLink() throws IOException, ApiError {
        return null;
    }
}
