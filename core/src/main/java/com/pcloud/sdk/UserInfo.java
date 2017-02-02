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

/**
 * A container for a pCloud account's user data.
 */
@SuppressWarnings("unused")
public interface UserInfo {

    /**
     * @return the id of the user.
     */
    long getUserId();

    /**
     * @return the email of this User. Cannot be null.
     */
    String getEmail();

    /**
     * @return {@code true} if the user email address has been verified, {@code false} otherwise.
     */
    boolean isEmailVerified();

    /**
     * @return the available storage space for the user in bytes.
     */
    long getTotalQuota();

    /**
     * @return the size of user content in bytes.
     */
    long getUsedQuota();
}
