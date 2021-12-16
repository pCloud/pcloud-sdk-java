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

import com.pcloud.sdk.UserInfo;

class RealUserInfo implements UserInfo {
    private final long userId;

    private final String email;

    private final boolean isEmailVerified;

    private final long totalQuota;

    private final long usedQuota;

    RealUserInfo(long userId, String email, boolean isEmailVerified, long totalQuota, long usedQuota) {
        this.userId = userId;
        this.email = email;
        this.isEmailVerified = isEmailVerified;
        this.totalQuota = totalQuota;
        this.usedQuota = usedQuota;
    }

    @Override
    public long userId() {
        return userId;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public boolean emailVerified() {
        return isEmailVerified;
    }

    @Override
    public long totalQuota() {
        return totalQuota;
    }

    @Override
    public long usedQuota() {
        return usedQuota;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealUserInfo that = (RealUserInfo) o;

        if (userId != that.userId) return false;
        if (isEmailVerified != that.isEmailVerified) return false;
        if (totalQuota != that.totalQuota) return false;
        if (usedQuota != that.usedQuota) return false;
        return email.equals(that.email);

    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + email.hashCode();
        result = 31 * result + (isEmailVerified ? 1 : 0);
        result = 31 * result + (int) (totalQuota ^ (totalQuota >>> 32));
        result = 31 * result + (int) (usedQuota ^ (usedQuota >>> 32));
        return result;
    }
}
