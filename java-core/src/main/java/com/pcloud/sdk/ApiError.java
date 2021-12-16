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

import java.util.Locale;

/**
 * Signals for pCloud API errors.
 * <p>
 * For more information on errors and their meaning, see <a href="https://docs.pcloud.com/errors/index.html" target="_blank"> the errors section</a>
 * or browse through method-specific pages at the <a href="https://docs.pcloud.com/" target="_blank">API documentation</a>.
 */
@SuppressWarnings("unused")
public class ApiError extends Exception {

    private final int errorCode;
    private final String errorMessage;

    public ApiError(int errorCode, String errorMessage) {
        super(String.format(Locale.US, "%d - %s", errorCode, errorMessage));
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Returns an error code for the API error.
     *
     * @return the error code
     */
    public int errorCode() {
        return errorCode;
    }

    /**
     * Returns a brief message describing the error.
     *
     * @return a non-null String with the error description
     */
    public String errorMessage() {
        return errorMessage;
    }
}
