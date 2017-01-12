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
import com.pcloud.sdk.api.*;
import com.pcloud.sdk.authentication.Authenticator;
import com.pcloud.sdk.internal.networking.GetFolderResponse;

import java.io.IOException;
import java.nio.charset.Charset;

public class Main {

    public static void main(String... args) {
        String token = System.getenv("pcloud_token");
        ApiService apiService = PCloudApi.newApiService()
                .authenticator(Authenticator.newOAuthAuthenticator(token))
                .create();
        try {
            RemoteFolder folder = apiService.getFolder(RemoteFolder.ROOT_FOLDER_ID).execute();
            printFolder(folder);

            RemoteFile newFile = uploadData(apiService);
            printFileAttributes(newFile);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ApiError apiError) {
            apiError.printStackTrace();
        }
    }

    private static void printFolder(RemoteFolder folder) throws IOException, ApiError {
        for (FileEntry entry : folder.getChildren()) {
            printFileAttributes(entry);
        }
    }

    private static void printFileAttributes(FileEntry entry) {
        System.out.format("%s | Created:%s | Modified: %s | size:%s\n", entry.getName(), entry.getCreated(), entry.getLastModified(), entry.isFile() ? String.valueOf(entry.asFile().getSize()) : "-");
    }

    private static RemoteFile uploadData(ApiService apiService) throws IOException, ApiError {
        String someText = "An empty text file";
        byte[] fileContents = someText.getBytes();
        return apiService.createFile(RemoteFolder.ROOT_FOLDER_ID, "someText.txt", Data.create(fileContents)).execute();
    }
}
