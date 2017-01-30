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

/**
 * A container for a pCloud account's user data.
 */
public interface UserInfo {

    /**
     * Returns the id of this User.
     */
    long getUserId();

    /**
     * Returns the email of this User.
     * <p>
     * Cannot be null.
     */
    String getEmail();

    /**
     * Returns {@code true} if the user email address has been verified, {@code false} otherwise.
     */
    boolean isEmailVerified();

    /**
     * Returns the available storage space in bytes.
     */
    long getTotalQuota();

    /**
     * Returns the size of user content in bytes.
     */
    long getUsedQuota();
}
