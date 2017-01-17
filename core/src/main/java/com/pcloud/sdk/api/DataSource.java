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

public abstract class DataSource {

    /**
     * Provide the length of the data instance content.
     */
    public abstract long contentLength();

    /**
     * Writes data content to a {@link BufferedSink}.
     *
     * @param sink {@link BufferedSink}
     * @throws IOException
     */
    public abstract void writeTo(BufferedSink sink) throws IOException;

    /**
     * Creates Data instance from a byte array.
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
     * Creates a new instance from a {@linkplain ByteString}.
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
     * Creates a new instance from a {@linkplain File}.
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
