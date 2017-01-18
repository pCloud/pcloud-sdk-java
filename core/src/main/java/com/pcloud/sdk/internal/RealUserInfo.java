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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pcloud.sdk.api.UserInfo;

 class RealUserInfo implements UserInfo {
    @Expose
    @SerializedName("userid")
    private long userId;

    @Expose
    @SerializedName("email")
    private String email;

    @Expose
    @SerializedName("emailverified")
    private boolean isEmailVerified;

    @Expose
    @SerializedName("quota")
    private long totalQuota;

    @Expose
    @SerializedName("usedquota")
    private long usedQuota;

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    @Override
    public long getTotalQuota() {
        return totalQuota;
    }

    @Override
    public long getUsedQuota() {
        return usedQuota;
    }

    RealUserInfo(long userId, String email, boolean isEmailVerified, long totalQuota, long usedQuota) {
        this.userId = userId;
        this.email = email;
        this.isEmailVerified = isEmailVerified;
        this.totalQuota = totalQuota;
        this.usedQuota = usedQuota;
    }
}
