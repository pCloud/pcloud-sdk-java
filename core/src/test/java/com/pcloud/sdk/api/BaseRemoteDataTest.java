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

package com.pcloud.sdk.api;

import com.pcloud.sdk.utils.DummyDataSink;
import com.pcloud.sdk.utils.DummyProgressListener;
import okio.BufferedSource;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.pcloud.sdk.internal.IOUtils.closeQuietly;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public abstract class BaseRemoteDataTest {

    RemoteData testInstance;

    protected RemoteData testInstance() {
        return testInstance;
    }

    protected void testInstance(RemoteData testInstance) {
        this.testInstance = testInstance;
    }

    @Test
    public void byteStream_Returns_NonNull_OrThrows() {
        InputStream inputStream = null;
        try {
            inputStream = testInstance.byteStream();
            assertNotNull(inputStream);
        } catch (IOException ignored) {

        }finally {
            closeQuietly(inputStream);
        }
    }

    @Test
    public void source_Returns_NonNull_OrThrows() {
        BufferedSource source = null;
        try {
            source = testInstance.source();
            assertNotNull(source);
        } catch (IOException ignored) {

        }finally {
            closeQuietly(source);
        }
    }

    @Test
    public void download_WritesToSink_OrThrows() {
        try {
            DataSink sink = spy(new DummyDataSink());
            testInstance.download(sink);
            verify(sink, times(1)).readAll(any(BufferedSource.class));
        } catch (IOException ignored) {
        }
    }

    @Test
    public void download_WritesToSink_OrThrows2() {
        ProgressListener listener = new DummyProgressListener();
        try {
            DataSink sink = spy(new DummyDataSink());
            testInstance.download(sink, listener);
            verify(sink, times(1)).readAll(any(BufferedSource.class));
        } catch (IOException ignored) {
        }
    }

    @Test
    public void download_Calls_ProgressListener() {
        ProgressListener listener = spy(new DummyProgressListener());
        try {
            DataSink sink = spy(new DummyDataSink());
            testInstance.download(sink, listener);
            verify(listener, atLeastOnce()).onProgress(anyLong(), anyLong());
        } catch (IOException ignored) {
        }
    }
}