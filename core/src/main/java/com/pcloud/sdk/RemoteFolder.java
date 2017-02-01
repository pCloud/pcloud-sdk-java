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

import java.util.List;

/**
 * An abstraction over a file entry for a folder on a pCloud account's filesystem.
 *
 * @see RemoteEntry
 * @see RemoteFolder
 */
public interface RemoteFolder extends RemoteEntry {

    /**
     * The id of a pCloud account's filesystem root folder.
     */
    int ROOT_FOLDER_ID = 0;

    /**
     * Returns the folderId for this folder.
     */
    long getFolderId();

    /**
     * Returns the folder's children.
     * <p>
     * Cannot be null.
     */
    List<RemoteEntry> getChildren();
}
