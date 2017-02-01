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

import com.pcloud.sdk.*;
import okio.BufferedSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;

class RealFileLink implements FileLink {

    private ApiService apiService;
    private Date expirationDate;
    private List<URL> links;

    RealFileLink(ApiService apiService, Date expirationDate, List<URL> links) {
        this.apiService = apiService;
        this.expirationDate = expirationDate;
        this.links = Collections.unmodifiableList(links);
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public List<URL> getUrls() {
        return links;
    }

    public URL getBestUrl(){
        return links.get(0);
    }

    @Override
    public InputStream byteStream() throws IOException {
        return source().inputStream();
    }

    @Override
    public BufferedSource source() throws IOException {
        boolean success = false;
        Call<BufferedSource> call = apiService.download(this);
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
    public void download(DataSink sink, ProgressListener listener) throws IOException {
        try {
            apiService.download(this, sink, listener).execute();
        } catch (ApiError apiError) {
            throw new IOException("API error occurred while trying to download link.", apiError);
        }
    }

    @Override
    public void download(DataSink sink) throws IOException {
        download(sink, null);
    }

    @Override
    public String toString() {
        return getBestUrl().toString();
    }
}
