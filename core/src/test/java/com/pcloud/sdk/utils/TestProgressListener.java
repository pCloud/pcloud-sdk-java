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

package com.pcloud.sdk.utils;

import com.pcloud.sdk.ProgressListener;

public class TestProgressListener implements ProgressListener {

    private long done;
    private long total;

    @Override
    public void onProgress(long done, long total) {
        this.done = done;
        this.total = total;
    }

    public long done() {
        return done;
    }

    public long total() {
        return total;
    }
}
