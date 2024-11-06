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

package com.pcloud.sdk.internal.networking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ContentLinkResponse extends ApiResponse {

    @Expose
    @SerializedName("path")
    private String path;

    @Expose
    @SerializedName("expires")
    private Date expires;

    @Expose
    @SerializedName("hosts")
    private List<String> hosts;

    protected ContentLinkResponse() {
    }

    public String getPath() {
        return path;
    }

    public Date getExpires() {
        return expires;
    }

    public List<String> getHosts() {
        return hosts;
    }
}
