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

import okio.*;

import java.io.File;
import java.io.IOException;

import static com.pcloud.sdk.internal.IOUtils.closeQuietly;

/**
 * A source of data.
 * <p>
 * An abstraction over the byte stream writing operations,
 * this class allows for a flexible way to write bytes to a {@link BufferedSink}.
 * <p>
 * Generally used for specifying a data source when creating/uploading files.
 * <li>
 * The {@link #create(File)} method can be used for reading data from a local file.
 * <li>
 * The {@link #create(byte[])} method can be used for reading data from a byte array.
 * <li>
 * The {@link #create(ByteString)} method can be used for reading data from Okio's immutable byte arrays.
 * <li>
 * For any other cases just extend the class and do your magic in the {@link #writeTo(BufferedSink)} method.
 */
public abstract class DataSource {

    /**
     * An empty {@link DataSource} instance.
     * <p>
     * Can be used for creating empty files.
     */
    public static final DataSource EMPTY = new DataSource() {
        @Override
        public long contentLength() {
            return 0;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {

        }
    };

    /**
     * Provide the data source length.
     * <p>
     * Override this method to provide the size of the data
     * to be written, if known in advance, otherwise return -1.
     */
    public long contentLength() {
        return -1;
    }

    /**
     * Write the data to a {@link BufferedSink}.
     *
     * @param sink {@link BufferedSink}
     * @throws IOException
     */
    public abstract void writeTo(BufferedSink sink) throws IOException;

    /**
     * Create a {@link DataSource} instance that reads from a byte array.
     *
     * @param data a byte array. Must not be null.
     * @return a {@link DataSink} that will read the given byte array.
     */
    public static DataSource create(final byte[] data) {
        return new DataSource() {

            @Override
            public long contentLength() {
                return data.length;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(data);
            }
        };
    }

    /**
     * Create a {@link DataSource} instance that reads from a byte array.
     *
     * @param data a byte array. Must not be null.
     * @return a {@link DataSink} that will read the given byte array.
     */
    public static DataSource create(final ByteString data) {
        return new DataSource() {

            @Override
            public long contentLength() {
                return data.size();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(data);
            }
        };
    }

    /**
     * Create a {@link DataSource} instance that reads from a byte array.
     *
     * @param file a file which will be read. Must not be null.
     * @return a {@link DataSink} that will read the given file.
     */
    public static DataSource create(final File file) {
        return new DataSource() {

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                BufferedSource source = null;
                try {
                    source = Okio.buffer(Okio.source(file));
                    sink.writeAll(source);
                } finally {
                    closeQuietly(source);
                }
            }
        };
    }
}
