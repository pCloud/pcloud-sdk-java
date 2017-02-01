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

import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * A link to the contents of a file entry on a pCloud account's filesystem.
 */
public interface FileLink extends RemoteData {

    /**
     * @return the {@link Date} until the link is valid. Cannot be null.
     */
    Date getExpirationDate();

    /**
     * @return a list of {@link URL}s that can be used to access the file contents. Cannot be null or empty.
     */
    List<URL> getUrls();

    /**
     * @return a {@link URL} considered to have the best access times. Cannot be null.
     */
    URL getBestUrl();

}
