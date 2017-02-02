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

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import static com.pcloud.sdk.internal.IOUtils.closeQuietly;

/**
 * A consumer of data.
 * <p>
 * An abstraction over the byte stream reading operations, this class allows for a flexible way to read bytes from a {@link BufferedSource}.
 * <ul>
 * <li>The {@link #create(File)} method can be used for writing data to a file.</li>
 * <li>For any other cases just extend the class and do your magic in the {@link #readAll(BufferedSource)} method.</li>
 * </ul>
 */
public abstract class DataSink {

    /**
     * Reads all bytes from a source
     * <p>
     * Do any reading here.
     * <h3> There is no explicit need to call {@link Closeable#close()} on the {@code source} argument, it will be closed for you.</h3>
     *
     * @param source the source
     * @throws IOException the io exception
     */
    public abstract void readAll(BufferedSource source) throws IOException;

    /**
     * Create a {@link DataSink} instance that writes data to a file.
     *
     * @param file a file where the data will be written. Must not be null.
     * @return a {@link DataSink} that will write to the given file.
     * @throws IllegalArgumentException on a null {@code file} argument.
     */
    public static DataSink create(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("File argument cannot be null.");
        }

        return new DataSink() {
            @Override
            public void readAll(BufferedSource source) throws IOException {
                BufferedSink sink = null;
                try {
                    sink = Okio.buffer(Okio.sink(file));
                    source.readAll(sink);
                    sink.flush();
                } finally {
                    closeQuietly(sink);
                    closeQuietly(source);
                }
            }
        };
    }
}
