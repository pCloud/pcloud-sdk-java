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

import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.ApiError;
import com.pcloud.sdk.Call;
import com.pcloud.sdk.DataSink;
import com.pcloud.sdk.FileLink;
import com.pcloud.sdk.ProgressListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okio.BufferedSource;

class RealFileLink implements FileLink {

    private final ApiClient apiClient;
    private final Date expirationDate;
    private final List<URL> links;

    RealFileLink(ApiClient apiClient, Date expirationDate, List<URL> links) {
        this.apiClient = apiClient;
        this.expirationDate = expirationDate;
        this.links = Collections.unmodifiableList(links);
    }

    public Date expirationDate() {
        return expirationDate;
    }

    public List<URL> urls() {
        return links;
    }

    public URL bestUrl(){
        return links.get(0);
    }

    @Override
    public InputStream byteStream() throws IOException, ApiError {
        return source(bestUrl()).inputStream();
    }

    @Override
    public InputStream byteStream(URL linkVariant) throws IOException, ApiError {
        return source(linkVariant).inputStream();
    }

    @Override
    public BufferedSource source() throws IOException, ApiError {
        return source(bestUrl());
    }

    @Override
    public BufferedSource source(URL linkVariant) throws IOException, ApiError {
        requireUrlFromLink(this, linkVariant);
        boolean success = false;
        Call<BufferedSource> call = apiClient.download(this);
        try {
            BufferedSource source = call.execute();
            success = true;
            return source;
        } catch (ApiError apiError) {
            throw new IOException("API error occurred while trying to read from download link.", apiError);
        } finally {
            if (!success) {
                call.cancel();
            }
        }
    }

    @Override
    public void download(DataSink sink, ProgressListener listener) throws IOException, ApiError {
        apiClient.download(this, sink, listener).execute();
    }

    @Override
    public void download(URL linkVariant, DataSink sink, ProgressListener listener) throws IOException, ApiError {
        requireUrlFromLink(this, linkVariant);
        apiClient.download(this, linkVariant, sink, listener).execute();
    }

    @Override
    public void download(DataSink sink) throws IOException, ApiError {
        apiClient.download(this, bestUrl(), sink, null).execute();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealFileLink that = (RealFileLink) o;

        if (!expirationDate.equals(that.expirationDate)) return false;
        return links.equals(that.links);

    }

    @Override
    public int hashCode() {
        int result = expirationDate.hashCode();
        result = 31 * result + links.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s | Valid until:%s", bestUrl(), expirationDate);
    }

    static void requireLinkNotNull(FileLink fileLink) {
        if (fileLink == null) {
            throw new IllegalArgumentException("FileLink argument cannot be null.");
        }
    }

    static void requireUrlFromLink(FileLink fileLink, URL linkVariant) {
        if (!fileLink.urls().contains(linkVariant)) {
            throw new IllegalArgumentException("Provided url must be one of the variants in the provided file link.");
        }
    }
}
