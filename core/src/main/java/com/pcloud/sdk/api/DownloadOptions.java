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

public class DownloadOptions {

    public static final DownloadOptions DEFAULT = create()
            .skipFilename(false)
            .forceDownload(false)
            .contentType(null)
            .build();

    private boolean skipFilename;
    private boolean forceDownload;
    private String contentType;

    public static Builder create(){
        return new Builder();
    }

    DownloadOptions(boolean skipFilename, boolean forceDownload, String contentType) {
        this.skipFilename = skipFilename;
        this.forceDownload = forceDownload;
        this.contentType = contentType;
    }

    public String contentType() {
        return contentType;
    }

    public boolean skipFilename() {
        return skipFilename;
    }

    public boolean forceDownload() {
        return forceDownload;
    }

    public Builder newBuilder(){
        return new Builder(skipFilename, forceDownload, contentType);
    }

    public static class Builder {

        private boolean skipFilename;
        private boolean forceDownload;
        private String contentType;

        private Builder() {
        }

        private Builder(boolean skipFilename, boolean forceDownload, String contentType) {
            this.skipFilename = skipFilename;
            this.forceDownload = forceDownload;
            this.contentType = contentType;
        }

        public Builder skipFilename(boolean skipFilename) {
            this.skipFilename = skipFilename;
            return this;
        }

        public Builder forceDownload(boolean forceDownload) {
            this.forceDownload = forceDownload;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public DownloadOptions build() {
            return new DownloadOptions(skipFilename, forceDownload, contentType);
        }
    }
}
