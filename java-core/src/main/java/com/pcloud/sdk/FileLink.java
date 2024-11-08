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
 * A link to the contents of a file entry on a pCloud account's filesystem.
 */
public interface FileLink extends ContentLink {
    /**
     * @return the hash of the file content. Can be used to detect content changes.
     * @see RemoteFile#hash()
     */
    String hash();
}
