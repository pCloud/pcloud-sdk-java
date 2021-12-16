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

import okio.*;

import java.io.IOException;

final class ProgressCountingSource extends ForwardingSource {
    private long bytesReportedOnLastNotification;
    private final long notificationThresholdBytes;
    private final ProgressListener listener;

    private long totalBytesRead;
    private final long totalBytes;

    ProgressCountingSource(Source delegate, long totalBytes, ProgressListener listener, long notificationThresholdBytes) {
        super(delegate);
        this.totalBytes = totalBytes;
        this.notificationThresholdBytes = notificationThresholdBytes;
        this.listener = listener;
    }

    @Override
    public long read(@NotNull Buffer sink, long byteCount) throws IOException {
        long bytesRead = super.read(sink, byteCount);
        this.totalBytesRead += bytesRead != -1 ? bytesRead : 0;

        if (totalBytesRead - bytesReportedOnLastNotification >= notificationThresholdBytes) {
            listener.onProgress(totalBytesRead, totalBytes);
            bytesReportedOnLastNotification = totalBytesRead;
        }

        return bytesRead;
    }
}
