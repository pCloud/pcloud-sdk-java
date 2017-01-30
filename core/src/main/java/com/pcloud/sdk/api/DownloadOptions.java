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

/**
 * DownloadOptions is used to build and encapsulate API call options for generating {@link FileLink}.
 */
public final class DownloadOptions {

    /**
     * Creates a default configuration of {@link DownloadOptions}.
     */
    public static final DownloadOptions DEFAULT = new Builder()
            .skipFilename(false)
            .forceDownload(false)
            .contentType(null)
            .build();

    private boolean skipFilename;
    private boolean forceDownload;
    private String contentType;

    /**
     * Creates new default {@link Builder}.
     * <p>
     * <p> {@link Builder} can be used to customise API call parameters to get {@link FileLink}.
     */
    public static Builder create() {
        return DEFAULT.newBuilder();
    }

    DownloadOptions(boolean skipFilename, boolean forceDownload, String contentType) {
        this.skipFilename = skipFilename;
        this.forceDownload = forceDownload;
        this.contentType = contentType;
    }

    /**
     * Returns the value of {@code contentType} parameter of the {@link DownloadOptions}.
     * <p>
     * <p>See {@link Builder#contentType(String)}
     * <p>For more information, see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">here</a></p>
     */
    public String contentType() {
        return contentType;
    }

    /**
     * Returns the value of {@code skipFilename} parameter of the {@link DownloadOptions}.
     * <p>
     * <p>See {@link Builder#skipFilename(boolean skipFilename)}
     */
    public boolean skipFilename() {
        return skipFilename;
    }

    /**
     * Returns the value of {@code forceDownload} parameter of the {@link DownloadOptions}.
     * <p>
     * <p>See {@link Builder#forceDownload(boolean)}
     */
    public boolean forceDownload() {
        return forceDownload;
    }

    /**
     * Creates {@link Builder} from {@link DownloadOptions}.
     * <p>
     * <p>If you have already created {@link DownloadOptions} use this method to create  {@link Builder} from it and then change it's parameters.
     *
     * @return {@link Builder}
     */
    public Builder newBuilder() {
        return new Builder(skipFilename, forceDownload, contentType);
    }

    /**
     * Use this Builder to set wanted parameters to get {@link FileLink}.
     * <p>
     * <p>For more details see <a href="https://docs.pcloud.com/methods/streaming/getfilelink.html">here</a></p>
     */
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

        /**
         * Sets {@code skipFilename} option.
         * <p>
         * <p>If {@code skipFilename} is set to FALSE the name of the file will be included in the generated {@link FileLink}. Otherwise the name will not be included.
         *
         * @return {@link Builder}
         */
        public Builder skipFilename(boolean skipFilename) {
            this.skipFilename = skipFilename;
            return this;
        }

        /**
         * Sets {@code forceDownload} option.
         * <p>
         * <p>If {@code forceDownload} is TRUE {@code content-type} will be set to {@code application/octet-stream}.
         * In this case {@code content-type} option should be set to null.
         * <p>For more details see <a href="https://docs.pcloud.com/methods/streaming/getfilelink.html">here</a></p>
         *
         * @return {@link Builder}
         */
        public Builder forceDownload(boolean forceDownload) {
            this.forceDownload = forceDownload;
            return this;
        }

        /**
         * Sets {@code contentType} option.
         * <p>
         * <p>{@code contentType} option can be null. If it is left to null the API will return {@link FileLink} with default content-type for the file extension.
         * <p>For more details see <a href="https://docs.pcloud.com/methods/streaming/getfilelink.html">here</a></p>
         *
         * @return {@link Builder}
         */
        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * Creates new {@link DownloadOptions} with the set {@code skipFilename, forceDownload and contentType} options.
         */
        public DownloadOptions build() {
            return new DownloadOptions(skipFilename, forceDownload, contentType);
        }
    }
}
