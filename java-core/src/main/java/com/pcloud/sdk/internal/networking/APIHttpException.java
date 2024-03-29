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

import java.io.IOException;
import java.util.Locale;

public class APIHttpException extends IOException {

    private final int code;

    public APIHttpException(int code, String message) {
        super(String.format(Locale.US, "API returned '%d - %s' HTTP error.", code, message));
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
