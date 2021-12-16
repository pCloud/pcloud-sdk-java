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

package com.pcloud.sdk.internal;

import com.pcloud.sdk.ProgressListener;

import org.jetbrains.annotations.NotNull;

import okio.Buffer;
import okio.ForwardingSink;
import okio.Sink;

import java.io.IOException;

final class ProgressCountingSink extends ForwardingSink {
    private long bytesReportedOnLastNotification;
    private final long notificationThresholdBytes;
    private final ProgressListener listener;

    private long totalBytesWritten;
    private final long totalBytes;

    ProgressCountingSink(Sink delegate, long totalBytes, ProgressListener listener, long notificationThresholdBytes) {
        super(delegate);
        this.totalBytes = totalBytes;
        this.notificationThresholdBytes = notificationThresholdBytes;
        this.listener = listener;
    }

    @Override
    public void write(@NotNull Buffer source, long byteCount) throws IOException {
        super.write(source, byteCount);
        totalBytesWritten += byteCount;

        if (totalBytesWritten - bytesReportedOnLastNotification >= notificationThresholdBytes) {
            listener.onProgress(totalBytesWritten, totalBytes);
            bytesReportedOnLastNotification = totalBytesWritten;
        }
    }
}
