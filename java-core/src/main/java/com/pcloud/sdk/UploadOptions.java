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

import java.util.Date;

/**
 * A container for the possible options when creating file.
 * <p>
 * UploadOptions is a immutable container for the allowed options when creating files
 * through {@link ApiClient#createFile(RemoteFolder, String, DataSource, Date, ProgressListener, UploadOptions)},
 * {@link ApiClient#createFile(RemoteFolder, String, DataSource, UploadOptions)}.
 * {@link ApiClient#createFile(long, String, DataSource, Date, ProgressListener, UploadOptions)},
 * or {@link ApiClient#createFile(long, String, DataSource, UploadOptions)}
 * <p>
 * Instances of the class can be created either the {@link #create()} method
 * or by mutating an existing object by calling {@link #newBuilder()}.
 * <p>
 * The {@link #DEFAULT} instance is pre-configured for the most common usage cases.
 */
public final class UploadOptions {

    /**
     * The default instance of {@link UploadOptions}.
     * <ul>
     * <li>
     * Files <b>WILL BE NOT</b> overridden on the server but instead saved with a number in the end.
     * </li>
     * <li>
     * Partial uploads (files not fully red before a connection break) <b>WILL NOT BE</b> saved on the server
     * </li>
     * </ul>
     */
    public static final UploadOptions DEFAULT = new Builder()
            .overrideFile(false)
            .partialUpload(false)
            .build();

    /**
     * The default instance of {@link UploadOptions}.
     * <ul>
     * <li>
     * Files <b>WILL BE</b> overridden on the server.
     * </li>
     * <li>
     * Partial uploads (files not fully red before a connection break) <b>WILL NOT BE</b> saved on the server
     * </li>
     * </ul>
     */
    public static final UploadOptions OVERRIDE_FILE = new Builder()
            .overrideFile(true)
            .partialUpload(false)
            .build();


    /**
     * The default instance of {@link UploadOptions}.
     * <ul>
     * <li>
     * Files <b>WILL BE NOT</b> overridden on the server but instead saved with a number in the end.
     * </li>
     * <li>
     * Partial uploads (files not fully red before a connection break) <b>WILL BE</b> saved on the server
     * </li>
     * </ul>
     */
    public static final UploadOptions PARTIAL_UPLOAD = new Builder()
            .overrideFile(false)
            .partialUpload(true)
            .build();


    private final boolean overrideFile;
    private final boolean partialUpload;

    /**
     * Creates new default {@link Builder}.
     * <p>
     * {@link Builder} can be used to customise API call parameters for <a href="https://docs.pcloud.com/methods/file/uploadfile.html" target="_blank">uploadfile</a>.
     *
     * @return a new {@link Builder} instance
     */
    public static Builder create() {
        return DEFAULT.newBuilder();
    }

    private UploadOptions(boolean overrideFile, boolean partialUpload) {
        this.overrideFile = overrideFile;
        this.partialUpload = partialUpload;
    }

    /**
     * @return {@code true} if the file will be overridden on the backend, {@code false} otherwise
     * @see Builder#overrideFile(boolean overrideFile)
     */
    public boolean overrideFile() {
        return overrideFile;
    }

    /**
     * @return {@code true} if download will be forced, {@code false} otherwise
     * @see Builder#partialUpload(boolean)
     */
    public boolean partialUpload() {
        return partialUpload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UploadOptions that = (UploadOptions) o;

        return (overrideFile != that.overrideFile) && (partialUpload != that.partialUpload);
    }

    @Override
    public int hashCode() {
        int result = (overrideFile ? 1 : 0);
        result = 31 * result + (partialUpload ? 1 : 0);
        return result;
    }

    /**
     * Creates {@link Builder} from {@link UploadOptions}.
     * <p>
     * If you have already created {@link UploadOptions} use this method to create  {@link Builder} from it and then change it's parameters.
     *
     * @return {@link Builder}
     */
    public Builder newBuilder() {
        return new Builder(overrideFile, partialUpload);
    }

    /**
     * A builder for configuring and creating new {@link UploadOptions} instances.
     * <p>
     * For more details see <a href="https://docs.pcloud.com/methods/file/uploadfile.html" target="_blank">here</a>
     *
     * @see UploadOptions#newBuilder()
     * @see UploadOptions#create()
     */
    @SuppressWarnings("WeakerAccess")
    public static class Builder {

        private boolean overrideFile;
        private boolean partialUpload;

        private Builder() {
        }

        private Builder(boolean overrideFile, boolean partialUpload) {
            this.overrideFile = overrideFile;
            this.partialUpload = partialUpload;
        }


        /**
         * Sets {@code overrideFile} option.
         * <p>
         * If {@code overrideFile} is  {@code TRUE} files with the same name with be overridden on the server.
         * This controls the api parameter {@code renameifexists}.
         * <p>
         * For more details see <a href="https://docs.pcloud.com/methods/file/uploadfile.html" target="_blank">here</a>
         *
         * @param overrideFile {@code true} to override existing files with same name, {@code false} otherwise
         * @return the same {@link Builder} instance
         */
        public Builder overrideFile(boolean overrideFile) {
            this.overrideFile = overrideFile;
            return this;
        }

        /**
         * Sets {@code nopartial} option.
         * <p>
         * If {@code partialUpload} is {@code TRUE} files not fully red will be saved on the server.
         * This controls the api parameter {@code nopartial}.
         * <p>
         * For more details see <a href="https://docs.pcloud.com/methods/file/uploadfile.html" target="_blank">here</a>
         *
         * @param partialUpload {@code true} to force partial upload saves, {@code false} otherwise
         * @return the same {@link Builder} instance
         */
        public Builder partialUpload(boolean partialUpload) {
            this.partialUpload = partialUpload;
            return this;
        }

        /**
         * Creates new {@link UploadOptions} with the set {@code overrideFile, partialUpload} options.
         *
         * @return a new {@link UploadOptions} object with the configuration from this build
         */
        public UploadOptions build() {
            return new UploadOptions(overrideFile, partialUpload);
        }
    }
}
