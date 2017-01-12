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

package com.pcloud.sdk.internal;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.pcloud.sdk.api.*;
import com.pcloud.sdk.api.Call;
import com.pcloud.sdk.internal.networking.ApiResponse;
import com.pcloud.sdk.internal.networking.GetFolderResponse;

import okhttp3.HttpUrl;
import okhttp3.*;
import okhttp3.Request;
import okio.BufferedSink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import static okhttp3.internal.Util.closeQuietly;

class RealApiService implements ApiService {

    private Gson gson;
    private OkHttpClient httpClient;
    private Executor callbackExecutor;

    private static final HttpUrl API_BASE_URL = HttpUrl.parse("https://api.pcloud.com");

    RealApiService(Gson gson, OkHttpClient httpClient, Executor callbackExecutor) {
        this.gson = gson;
        this.httpClient = httpClient;
        this.callbackExecutor = callbackExecutor;
    }

    @Override
    public Call<RemoteFolder> getFolder(long folderId) {
        return newCall(createListFolderRequest(folderId), new ResponseAdapter<RemoteFolder>() {
            @Override
            public RemoteFolder adapt(Response response) throws IOException, ApiError {
                return getAsApiResponse(response, GetFolderResponse.class).getFolder();
            }
        });
    }

    @Override
    public Call<List<FileEntry>> listFiles(RemoteFolder folder) {
        return newCall(createListFolderRequest(folder.getFolderId()), new ResponseAdapter<List<FileEntry>>() {
            @Override
            public List<FileEntry> adapt(Response response) throws IOException, ApiError {
                return getAsApiResponse(response, GetFolderResponse.class).getFolder()
                        .getChildren();
            }
        });
    }

    private Request createListFolderRequest(long folderId) {
        return newRequest()
                .url(API_BASE_URL.newBuilder()
                        .addPathSegment("listfolder")
                        .addQueryParameter("folderid", String.valueOf(folderId))
                        .addQueryParameter("noshares", String.valueOf(1))
                        .build())
                .get()
                .build();
    }

    @Override
    public Call<RemoteFolder> createFolder(long parentFolderId, String folderName) {
        if (folderName == null) {
            throw new IllegalArgumentException("Folder name is null");
        }
        return newCall(createFolderRequest(parentFolderId, folderName), new ResponseAdapter<RemoteFolder>() {
            @Override
            public RemoteFolder adapt(Response response) throws IOException, ApiError {
                return getAsApiResponse(response, GetFolderResponse.class).getFolder();
            }
        });
    }

    private Request createFolderRequest(long parentFolderId, String folderName) {
        RequestBody body = new FormBody.Builder()
                .add("folderid", String.valueOf(parentFolderId))
                .add("name", folderName)
                .build();

        return newRequest()
                .url(API_BASE_URL.newBuilder()
                        .addPathSegment("createfolder").build())
                .post(body)
                .build();
    }

    @Override
    public Call<RemoteFolder> deleteFolder(long folderId) {
        return newCall(createDeleteFolderRequest(folderId), new ResponseAdapter<RemoteFolder>() {
            @Override
            public RemoteFolder adapt(Response response) throws IOException, ApiError {
                return getAsApiResponse(response, GetFolderResponse.class).getFolder();
            }
        });
    }

    private Request createDeleteFolderRequest(long folderId) {
        RequestBody body = new FormBody.Builder()
                .add("folderid", String.valueOf(folderId))
                .build();

        return newRequest()
                .url(API_BASE_URL.newBuilder()
                        .addPathSegment("deletefolder")
                        .build())
                .post(body)
                .build();
    }

    @Override
    public Call<RemoteFolder> renameFolder(long folderId, String newFolderName) {
        if (newFolderName == null) {
            throw new IllegalArgumentException("Folder name is null");
        }
        return newCall(createRenameFolderRequest(folderId, newFolderName), new ResponseAdapter<RemoteFolder>() {
            @Override
            public RemoteFolder adapt(Response response) throws IOException, ApiError {
                return getAsApiResponse(response, GetFolderResponse.class).getFolder();
            }
        });
    }

    private Request createRenameFolderRequest(long folderId, String newFolderName) {
        RequestBody body = new FormBody.Builder()
                .add("folderid", String.valueOf(folderId))
                .add("toname", newFolderName)
                .build();

        return newRequest()
                .url(API_BASE_URL.newBuilder()
                        .addPathSegment("renamefolder")
                        .build())
                .post(body)
                .build();
    }

    @Override
    public Call<RemoteFolder> moveFolder(long folderId, long toFolderId) {
        return newCall(createMoveFolderRequest(folderId, toFolderId), new ResponseAdapter<RemoteFolder>() {
            @Override
            public RemoteFolder adapt(Response response) throws IOException, ApiError {
                return getAsApiResponse(response, GetFolderResponse.class).getFolder();
            }
        });
    }

    private Request createMoveFolderRequest(long folderId, long toFolderId) {
        RequestBody body = new FormBody.Builder()
                .add("folderid", String.valueOf(folderId))
                .add("tofolderid", String.valueOf(toFolderId))
                .build();

        return newRequest()
                .url(API_BASE_URL.newBuilder()
                        .addPathSegment("renamefolder")
                        .build())
                .post(body)
                .build();
    }

    @Override
    public ApiServiceBuilder newBuilder() {
        throw new UnsupportedOperationException();
    }

    private Request.Builder newRequest() {
        return new Request.Builder().url(API_BASE_URL);
    }

    private <T> Call<T> newCall(Request request, ResponseAdapter<T> adapter) {
        Call<T> apiCall = new OkHttpCall<>(httpClient.newCall(request), adapter);
        if (callbackExecutor != null) {
            return new ExecutorCallbackCall<>(apiCall, callbackExecutor);
        } else {
            return apiCall;
        }
    }

    private <T extends ApiResponse> T getAsApiResponse(Response response, Class<? extends T> bodyType) throws IOException, ApiError {
        T body = deserializeResponseBody(response, bodyType);
        if (body.isSuccessful()) {
            return body;
        } else {
            throw new ApiError(body.getStatusCode(), body.getMessage());
        }
    }

    private <T> T deserializeResponseBody(Response response, Class<? extends T> bodyType) throws IOException {
        try {
            if (!response.isSuccessful()) {
                throw new IOException(String.format(Locale.US,
                        "API returned \'%d - %s\' HTTP error.", response.code(), response.message()));
            }

            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(response.body().byteStream())));
            try {
                return gson.fromJson(reader, bodyType);
            } catch (JsonSyntaxException e) {
                throw new IOException("Malformed JSON response.");
            } finally {
                closeQuietly(reader);
            }
        } finally {
            closeQuietly(response);
        }
    }
}
