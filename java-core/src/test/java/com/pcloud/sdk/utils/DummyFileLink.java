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
import com.pcloud.sdk.FileLink;
import com.pcloud.sdk.ProgressListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import okio.BufferedSource;

public class DummyFileLink implements FileLink {

    private URL url;

    public DummyFileLink() {
        try {
            this.url = new URL("http", UUID.randomUUID().toString(), "");
        } catch (MalformedURLException e) {
            // Not an option
        }
    }

    public DummyFileLink(URL url) {
        this.url = url;
    }

    @Override
    public Date expirationDate() {
        return new Date(Long.MAX_VALUE);
    }

    @Override
    public List<URL> urls() {
        return Collections.singletonList(url);
    }

    @Override
    public URL bestUrl() {
        return url;
    }

    @Override
    public InputStream byteStream() throws IOException {
        return null;
    }

    @Override
    public InputStream byteStream(URL linkVariant) throws IOException, ApiError {
        return null;
    }

    @Override
    public BufferedSource source() throws IOException {
        return null;
    }

    @Override
    public BufferedSource source(URL linkVariant) throws IOException, ApiError {
        return null;
    }

    @Override
    public void download(DataSink sink, ProgressListener listener) throws IOException,ApiError {

    }

    @Override
    public void download(DataSink sink) throws IOException,ApiError {

    }

    @Override
    public void download(URL linkVariant, DataSink sink, ProgressListener listener) throws IOException, ApiError {

    }
}
