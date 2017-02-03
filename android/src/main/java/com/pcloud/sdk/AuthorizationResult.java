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
 * Gives information about the outcome of an initiated authorization.
 * @see AuthorizationActivity
 */
@SuppressWarnings("WeakerAccess")
public enum AuthorizationResult {
    /**
     * The user granted permissions to the calling application.
     * <p> See the description of {@link AuthorizationActivity} on how to obtain the access token.
     */
    ACCESS_GRANTED,

    /**
     * The authorization was cancelled by the user.
     */
    CANCELLED,

    /**
     * The user did not grant permissions to the calling application.
     */
    ACCESS_DENIED,

    /**
     * There was an error during the authorization process.
     */
    AUTH_ERROR
}