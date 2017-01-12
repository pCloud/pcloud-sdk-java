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

import com.pcloud.sdk.PCloudApi;
import com.pcloud.sdk.api.ApiError;
import com.pcloud.sdk.api.ApiService;
import com.pcloud.sdk.api.FileEntry;
import com.pcloud.sdk.api.RemoteFolder;
import com.pcloud.sdk.authentication.Authenticator;
import com.pcloud.sdk.internal.networking.GetFolderResponse;

import java.io.IOException;

public class Main {

    public static void main(String... args) {
        String token = System.getenv("pcloud_token");
        ApiService apiService = PCloudApi.newApiService()
                .authenticator(Authenticator.newOAuthAuthenticator(token))
                .create();
        try {
            RemoteFolder folder = apiService.getFolder(0).execute();
            for (FileEntry entry : folder.getChildren()) {
                System.out.format("%s | Created:%s | Modified: %s | size:%s\n", entry.getName(), entry.getCreated(), entry.getLastModified(), entry.isFile() ? String.valueOf(entry.asFile().getSize()) : "-");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ApiError apiError) {
            apiError.printStackTrace();
        }
    }
}
