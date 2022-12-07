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

import static com.pcloud.sdk.internal.IOUtils.closeQuietly;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.pcloud.sdk.utils.DummyDataSink;
import com.pcloud.sdk.utils.TestProgressListener;

import org.junit.Test;

import java.io.InputStream;

import okio.BufferedSource;

public abstract class BaseRemoteDataTest {

    RemoteData testInstance;

    protected RemoteData testInstance() {
        return testInstance;
    }

    protected void testInstance(RemoteData testInstance) {
        this.testInstance = testInstance;
    }

    @Test
    public void byteStream_Returns_NonNull_OrThrows() throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = testInstance.byteStream();
            assertNotNull(inputStream);
        } finally {
            closeQuietly(inputStream);
        }
    }

    @Test
    public void source_Returns_NonNull_OrThrows() throws Exception {
        BufferedSource source = null;
        try {
            source = testInstance.source();
            assertNotNull(source);
        } finally {
            closeQuietly(source);
        }
    }

    @Test
    public void download_WritesToSink_OrThrows() throws Exception {
        DataSink sink = spy(new DummyDataSink());
        testInstance.download(sink);
        verify(sink, times(1)).readAll(any(BufferedSource.class));
    }

    @Test
    public void download_WritesToSink_OrThrows2() throws Exception {
        ProgressListener listener = new TestProgressListener();
        DataSink sink = spy(new DummyDataSink());
        testInstance.download(sink, listener);
        verify(sink, times(1)).readAll(any(BufferedSource.class));
    }

    @Test
    public void download_Calls_ProgressListener() throws Exception {
        ProgressListener listener = spy(new TestProgressListener());
        DataSink sink = spy(new DummyDataSink());
        testInstance.download(sink, listener);
        verify(listener, atLeastOnce()).onProgress(anyLong(), anyLong());
    }
}