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

import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.ApiError;
import com.pcloud.sdk.Call;
import com.pcloud.sdk.ContentLink;
import com.pcloud.sdk.DataSink;
import com.pcloud.sdk.FileLink;
import com.pcloud.sdk.ProgressListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okio.BufferedSource;

class RealFileLink extends RealContentLink implements FileLink {

    private final String hash;

    RealFileLink(ApiClient apiClient, Date expirationDate, List<URL> links, String hash) {
        super(apiClient, expirationDate, links);
        this.hash = hash;
    }

    @NotNull
    @Override
    public String hash() {
        return hash;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealFileLink )) return false;
        final RealFileLink that = (RealFileLink)o;
        return expirationDate().equals(that.expirationDate()) && urls().equals(that.urls()) && hash.equals(that.hash);
    }

    @Override
    public int hashCode() {
        int result = expirationDate().hashCode();
        result = 31 * result + urls().hashCode();
        result = 31 * result + hash.hashCode();
        return result;
    }
}
