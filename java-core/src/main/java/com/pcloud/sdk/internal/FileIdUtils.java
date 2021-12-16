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

package com.pcloud.sdk.internal;

import java.util.Locale;

class FileIdUtils {

    public static final char DIRECTORY_ID_PREFIX = 'd';
    public static final char FILE_ID_PREFIX = 'f';

    private FileIdUtils() {}

    public static void isFileOrThrow(String id) {
        if (isFolder(id)) {
            throw new IllegalArgumentException(String.format(Locale.US, "'%s' is not a file identifier.", id));
        }
    }

    public static void isFolderOrThrow(String id) {
        if (!isFolder(id)) {
            throw new IllegalArgumentException(String.format(Locale.US, "'%s' is not a file identifier.", id));
        }
    }

    public static long toFileId(String id) {
        isFileOrThrow(id);
        return Long.parseLong(id.substring(1));
    }

    public static long toFolderId(String id) {
        isFolderOrThrow(id);
        return Long.parseLong(id.substring(1));
    }

    public static boolean isFile(String id) {
        return !isFolder(id);
    }

    public static boolean isFolder(String id) {
        char prefix = id.charAt(0);
        switch (prefix) {
            case DIRECTORY_ID_PREFIX:
                return true;
            case FILE_ID_PREFIX:
                return false;
            default:
                throw new IllegalArgumentException(String.format(Locale.US, "'%s' is not a valid pCloud file identifier.", id));
        }
    }

}
