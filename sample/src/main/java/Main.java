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

import com.pcloud.sdk.PCloudSdk;
import com.pcloud.sdk.api.*;
import com.pcloud.sdk.authentication.Authenticator;

import java.io.*;
import java.util.Date;
import java.util.UUID;

public class Main {

    public static void main(String... args) {
        String token = System.getenv("pcloud_token");
        ApiService apiService = PCloudSdk.newApiService()
                .authenticator(Authenticator.newOAuthAuthenticator(token))
                .create();
        try {
            RemoteFolder folder = apiService.getFolder(RemoteFolder.ROOT_FOLDER_ID, true).execute();
            printFolder(folder);

            RemoteFile newFile = uploadData(apiService);
            printFileAttributes(newFile);

            FileLink downloadLink = apiService.getDownloadLink(newFile, DownloadOptions.DEFAULT).execute();
            System.out.print(downloadLink.getBestUrl());

            RemoteFile bigFile = uploadFile(apiService, new File("some file path"));
            System.out.println(bigFile.getDownloadLink());
            downloadFile(bigFile, new File("some directory path"));

            UserInfo userInfo = apiService.getUserInfo().execute();
            System.out.format(" User email: %s | Total quota %s | Used quota %s " , userInfo.getEmail(), userInfo.getTotalQuota(), userInfo.getUsedQuota());


        } catch (IOException | ApiError e) {
            e.printStackTrace();
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
        String someName = UUID.randomUUID().toString();
        byte[] fileContents = someName.getBytes();
        return apiService.createFile(RemoteFolder.ROOT_FOLDER_ID, someName + ".txt", DataSource.create(fileContents)).execute();
    }

    private static RemoteFile uploadFile(ApiService apiService, File file) throws IOException, ApiError {

        return apiService.createFile(RemoteFolder.ROOT_FOLDER_ID, file.getName(), DataSource.create(file), new Date(file.lastModified()), new ProgressListener() {
            public void onProgress(long done, long total) {
                System.out.format("\rUploading... %.1f\n", ((double) done / (double) total) * 100d);
            }
        }).execute();
    }

    private static File downloadFile(RemoteFile remoteFile, File folder) throws IOException, ApiError {
        File destination = new File(folder, remoteFile.getName());
        remoteFile.download(DataSink.create(destination), new ProgressListener() {
            public void onProgress(long done, long total) {
                System.out.format("\rDownloading... %.1f\n", ((double) done / (double) total) * 100d);
            }
        });
        return destination;
    }
}
