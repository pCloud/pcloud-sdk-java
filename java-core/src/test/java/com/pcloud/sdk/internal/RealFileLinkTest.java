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

import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.BaseFileLinkTest;
import com.pcloud.sdk.utils.DummyDownloadingApiClient;
import org.junit.After;
import org.junit.Before;

import java.net.URL;
import java.util.Collections;
import java.util.Date;

public class RealFileLinkTest extends BaseFileLinkTest {

    private ApiClient service;

    @Before
    public void setUp() throws Exception {
        service = DummyDownloadingApiClient.create();
        testInstance(new RealFileLink(service, new Date(), Collections.singletonList(new URL("http://www.pcloud.com")), "123124"));
    }

    @After
    public void tearDown() throws Exception {
        service.shutdown();
    }
}