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

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.File;
import java.io.IOException;

import static com.pcloud.sdk.internal.IOUtils.closeQuietly;

public abstract class DataSink {

    public abstract void readAll(BufferedSource source) throws IOException;

    public static DataSink create(final File file) {
        return new DataSink() {
            @Override
            public void readAll(BufferedSource source) throws IOException{
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