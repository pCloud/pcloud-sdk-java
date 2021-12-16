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

package com.pcloud.sdk;

import java.util.Objects;

/**
 * A container for the possible options when creating file links.
 * <p>
 * DownloadOptions is a immutable container for the allowed options when creating file
 * links through {@link ApiClient#createFileLink(RemoteFile, DownloadOptions)},
 * {@link ApiClient#createFileLink(long, DownloadOptions)} or {@link RemoteFile#createFileLink()}.
 * <p>
 * Instances of the class can be created either the {@link #create()} method
 * or by mutating an existing object by calling {@link #newBuilder()}.
 * <p>
 * The {@link #DEFAULT} instance is pre-configured for the most common usage cases.
 */
public final class DownloadOptions {

    /**
     * The default instance of {@link DownloadOptions}.
     * <ul>
     * <li>
     * The content-type will be determined from the file extension.
     * </li>
     * <li>
     * File name <b>WILL BE</b> included in the link.
     * </li>
     * <li>
     * Download will not be forced.
     * </li>
     * </ul>
     */
    public static final DownloadOptions DEFAULT = new Builder()
            .forceDownload(false)
            .skipFilename(false)
            .contentType(null)
            .build();

    private final boolean skipFilename;
    private final boolean forceDownload;
    private final String contentType;

    /**
     * Creates new default {@link Builder}.
     * <p>
     * {@link Builder} can be used to customise API call parameters to get {@link FileLink}.
     *
     * @return a new {@link Builder} instance
     */
    public static Builder create() {
        return DEFAULT.newBuilder();
    }

    private DownloadOptions(boolean skipFilename, boolean forceDownload, String contentType) {
        this.skipFilename = skipFilename;
        this.forceDownload = forceDownload;
        this.contentType = contentType;
    }

    /**
     * @return the value of {@code contentType} parameter of the {@link DownloadOptions}.
     * <p>For more information on content types, see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">here</a>.
     * @see Builder#contentType(String)
     */
    public String contentType() {
        return contentType;
    }

    /**
     * @return {@code true} if the filename will be omitted in the generated link, {@code false} otherwise
     * @see Builder#skipFilename(boolean skipFilename)
     */
    public boolean skipFilename() {
        return skipFilename;
    }

    /**
     * @return {@code true} if download will be forced, {@code false} otherwise
     * @see Builder#forceDownload(boolean)
     */
    public boolean forceDownload() {
        return forceDownload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadOptions that = (DownloadOptions) o;

        if (skipFilename != that.skipFilename) return false;
        if (forceDownload != that.forceDownload) return false;
        return Objects.equals(contentType, that.contentType);

    }

    @Override
    public int hashCode() {
        int result = (skipFilename ? 1 : 0);
        result = 31 * result + (forceDownload ? 1 : 0);
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        return result;
    }

    /**
     * Creates {@link Builder} from {@link DownloadOptions}.
     * <p>
     * If you have already created {@link DownloadOptions} use this method to create  {@link Builder} from it and then change it's parameters.
     *
     * @return {@link Builder}
     */
    public Builder newBuilder() {
        return new Builder(skipFilename, forceDownload, contentType);
    }

    /**
     * A builder for configuring and creating new {@link DownloadOptions} instances.
     * <p>
     * For more details see <a href="https://docs.pcloud.com/methods/streaming/getfilelink.html" target="_blank">here</a>
     *
     * @see DownloadOptions#newBuilder()
     * @see DownloadOptions#create()
     */
    @SuppressWarnings("WeakerAccess")
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
         * @param skipFilename {@code true} to omit the filename in the generated link, {@code false} otherwise
         * @return the same {@link Builder} instance
         */
        public Builder skipFilename(boolean skipFilename) {
            this.skipFilename = skipFilename;
            return this;
        }

        /**
         * Sets {@code forceDownload} option.
         * <p>
         * If {@code forceDownload} is TRUE {@code content-type} will be set to {@code application/octet-stream}.
         * In this case {@code content-type} option should be set to null.
         * <p>
         * For more details see <a href="https://docs.pcloud.com/methods/streaming/getfilelink.html" target="_blank">here</a>
         *
         * @param forceDownload {@code true} to force a download, {@code false} otherwise
         * @return the same {@link Builder} instance
         */
        public Builder forceDownload(boolean forceDownload) {
            this.forceDownload = forceDownload;
            return this;
        }

        /**
         * Sets {@code contentType} option.
         * <p>
         * {@code contentType} option can be null. If it is left to null the API will return {@link FileLink} with default content-type for the file extension.
         * <p>
         * For more details see <a href="https://docs.pcloud.com/methods/streaming/getfilelink.html" target="_blank">here</a>
         *
         * @param contentType a content type
         * @return the same {@link Builder} instance
         */
        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * Creates new {@link DownloadOptions} with the set {@code skipFilename, forceDownload and contentType} options.
         *
         * @return a new {@link DownloadOptions} object with the configuration from this build
         */
        public DownloadOptions build() {
            return new DownloadOptions(skipFilename, forceDownload, contentType);
        }
    }
}
