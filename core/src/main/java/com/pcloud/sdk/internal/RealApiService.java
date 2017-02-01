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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.pcloud.sdk.*;
import com.pcloud.sdk.Call;
import com.pcloud.sdk.internal.networking.*;
import com.pcloud.sdk.internal.networking.ApiResponse;
import com.pcloud.sdk.internal.networking.GetFileResponse;
import com.pcloud.sdk.internal.networking.GetFolderResponse;
import com.pcloud.sdk.internal.networking.UploadFilesResponse;

import com.pcloud.sdk.internal.networking.serialization.DateTypeAdapter;
import com.pcloud.sdk.internal.networking.serialization.UnmodifiableListTypeFactory;
import okhttp3.HttpUrl;
import okhttp3.*;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static com.pcloud.sdk.internal.IOUtils.closeQuietly;

class RealApiService implements ApiService {

    private long progressCallbackThresholdBytes;
    private Gson gson;
    private OkHttpClient httpClient;
    private Executor callbackExecutor;

    private static final HttpUrl API_BASE_URL = HttpUrl.parse("https://api.pcloud.com");

    RealApiService() {
        this(new RealApiServiceBuilder());
    }

    RealApiService(RealApiServiceBuilder builder) {
        Map<String, String> globalParams = new TreeMap<>();
        globalParams.put("timeformat", String.valueOf("timestamp"));
        String userAgent = String.format(Locale.US, "pCloud SDK Java %s", Version.NAME);
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(builder.readTimeoutMs(), TimeUnit.MILLISECONDS)
                .writeTimeout(builder.writeTimeoutMs(), TimeUnit.MILLISECONDS)
                .connectTimeout(builder.connectTimeoutMs(), TimeUnit.MILLISECONDS)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .addInterceptor(new GlobalRequestInterceptor(userAgent, globalParams));

        if (builder.dispatcher() != null) {
            httpClientBuilder.dispatcher(builder.dispatcher());
        }

        if (builder.connectionPool() != null) {
            httpClientBuilder.connectionPool(builder.connectionPool());
        }

        if (builder.cache() != null) {
            httpClientBuilder.cache(builder.cache());
        }

        httpClientBuilder.authenticator(okhttp3.Authenticator.NONE);
        if (builder.authenticator() != null) {
            httpClientBuilder.addInterceptor((RealAuthenticator) builder.authenticator());
        }

        this.httpClient = httpClientBuilder.build();
        this.callbackExecutor = builder.callbackExecutor();
        this.progressCallbackThresholdBytes = builder.progressCallbackThresholdBytes();
        this.gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapterFactory(new RealRemoteEntry.TypeAdapterFactory())
                .registerTypeAdapterFactory(new UnmodifiableListTypeFactory())
                .registerTypeAdapter(RemoteEntry.class, new RealRemoteEntry.FileEntryDeserializer())
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .registerTypeAdapter(RealRemoteFile.class, new RealRemoteFile.InstanceCreator(this))
                .registerTypeAdapter(RealRemoteFolder.class, new RealRemoteFolder.InstanceCreator(this))
                .create();
    }

    @Override
    public Call<RemoteFolder> getFolder(long folderId) {
        return getFolder(folderId, false);
    }

    @Override
    public Call<RemoteFolder> getFolder(long folderId, boolean recursively) {
        return newCall(createListFolderRequest(folderId, recursively), new ResponseAdapter<RemoteFolder>() {
            @Override
            public RemoteFolder adapt(Response response) throws IOException, ApiError {
                return getAsApiResponse(response, GetFolderResponse.class).getFolder();
            }
        });
    }

    @Override
    public Call<List<RemoteEntry>> listFiles(RemoteFolder folder) {
        if (folder == null) {
            throw new IllegalArgumentException("Folder argument cannot be null.");
        }

        return newCall(createListFolderRequest(folder.getFolderId(), false), new ResponseAdapter<List<RemoteEntry>>() {
            @Override
            public List<RemoteEntry> adapt(Response response) throws IOException, ApiError {
                return getAsApiResponse(response, GetFolderResponse.class).getFolder()
                        .getChildren();
            }
        });
    }

    @Override
    public Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data) {
        return createFile(folder.getFolderId(), filename, data, null, null);
    }

    @Override
    public Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data, Date modifiedDate, ProgressListener listener) {
        if (folder == null) {
            throw new IllegalArgumentException("Folder argument cannot be null.");
        }
        return createFile(folder.getFolderId(), filename, data, modifiedDate, listener);
    }

    @Override
    public Call<RemoteFile> createFile(long folderId, String filename, DataSource data) {
        return createFile(folderId, filename, data, null, null);
    }

    @Override
    public Call<RemoteFile> createFile(long folderId, String filename, final DataSource data, Date modifiedDate, final ProgressListener listener) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null.");
        }
        if (data == null) {
            throw new IllegalArgumentException("File data cannot be null.");
        }

        RequestBody dataBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("multipart/form-data");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                if (listener != null) {
                    ProgressListener realListener = listener;
                    if (callbackExecutor != null) {
                        realListener = new ExecutorProgressListener(listener, callbackExecutor);
                    }

                    sink = Okio.buffer(new ProgressCountingSink(
                            sink,
                            data.contentLength(),
                            realListener,
                            progressCallbackThresholdBytes));
                }
                data.writeTo(sink);
                sink.flush();
            }

            @Override
            public long contentLength() throws IOException {
                return data.contentLength();
            }
        };

        RequestBody compositeBody = new MultipartBody.Builder("--")
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", filename, dataBody)
                .build();

        HttpUrl.Builder urlBuilder = API_BASE_URL.newBuilder().
                addPathSegment("uploadfile")
                .addQueryParameter("folderid", String.valueOf(folderId))
                .addQueryParameter("renameifexists", String.valueOf(1))
                .addQueryParameter("nopartial", String.valueOf(1));

        if (modifiedDate != null) {
            urlBuilder.addQueryParameter("mtime", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(modifiedDate.getTime())));
        }

        Request uploadRequest = new Request.Builder()
                .url(urlBuilder.build())
                .method("POST", compositeBody)
                .build();

        return newCall(uploadRequest, new ResponseAdapter<RemoteFile>() {
            @Override
            public RemoteFile adapt(Response response) throws IOException, ApiError {
                UploadFilesResponse body = getAsApiResponse(response, UploadFilesResponse.class);
                if (!body.getUploadedFiles().isEmpty()) {
                    return body.getUploadedFiles().get(0);
                } else {
                    throw new IOException("API uploaded file but did not return remote file data.");
                }
            }
        });
    }

    @Override
    public Call<Boolean> deleteFile(RemoteFile file) {
        if (file == null) {
            throw new IllegalArgumentException("File argument cannot be null.");
        }
        return deleteFile(file.getFileId());
    }

    @Override
    public Call<Boolean> deleteFile(long fileId) {
        Request request = new Request.Builder()
                .url(API_BASE_URL.newBuilder()
                        .addPathSegment("deletefile")
                        .build())
                .get()
                .post(new FormBody.Builder()
                        .add("fileid", String.valueOf(fileId))
                        .build())
                .build();
        return newCall(request, new ResponseAdapter<Boolean>() {
            @Override
            public Boolean adapt(Response response) throws IOException, ApiError {
                GetFileResponse body = deserializeResponseBody(response, GetFileResponse.class);
                return body.isSuccessful() && body.getFile() != null;
            }
        });
    }

    @Override
    public Call<FileLink> createFileLink(RemoteFile file, DownloadOptions options) {
        if (file == null) {
            throw new IllegalArgumentException("File argument cannot be null.");
        }
        return createFileLink(file.getFileId(), options);
    }

    @Override
    public Call<FileLink> createFileLink(long fileId, DownloadOptions options) {
        if (options == null) {
            throw new IllegalArgumentException("DownloadOptions parameter cannot be null.");
        }

        Request request = newDownloadLinkRequest(fileId, options);

        return newCall(request, new ResponseAdapter<FileLink>() {
            @Override
            public FileLink adapt(Response response) throws IOException, ApiError {
                return getAsFileLink(response);
            }
        });

    }

    private FileLink getAsFileLink(Response response) throws IOException, ApiError {
        GetLinkResponse body = getAsApiResponse(response, GetLinkResponse.class);
        List<URL> downloadUrls = new ArrayList<>(body.getHosts().size());
        for (String host : body.getHosts()) {
            downloadUrls.add(new URL("https", host, body.getPath()));
        }

        return new RealFileLink(RealApiService.this, body.getExpires(), downloadUrls);
    }

    private Request newDownloadLinkRequest(long fileId, DownloadOptions options) {
        HttpUrl.Builder urlBuilder = API_BASE_URL.newBuilder().
                addPathSegment("getfilelink")
                .addQueryParameter("fileid", String.valueOf(fileId));

        if (options.forceDownload()) {
            urlBuilder.addQueryParameter("forcedownload", String.valueOf(1));
        }

        if (options.skipFilename()) {
            urlBuilder.addQueryParameter("skipfilename", String.valueOf(1));
        }

        if (options.contentType() != null) {
            MediaType mediaType = MediaType.parse(options.contentType());
            if (mediaType == null) {
                throw new IllegalArgumentException("Invalid or not well-formatted content type DownloadOptions argument");
            }
            urlBuilder.addQueryParameter("contenttype", mediaType.toString());
        }

        return new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();
    }

    @Override
    public Call<Void> download(FileLink fileLink, DataSink sink) {
        return download(fileLink, sink, null);
    }

    @Override
    public Call<Void> download(FileLink fileLink, final DataSink sink, final ProgressListener listener) {
        if (fileLink == null) {
            throw new IllegalArgumentException("FileLink argument cannot be null.");
        }

        if (sink == null) {
            throw new IllegalArgumentException("DataSink argument cannot be null.");
        }

        Request request = newDownloadRequest(fileLink);

        return newCall(request, new ResponseAdapter<Void>() {
            @Override
            public Void adapt(Response response) throws IOException, ApiError {
                try {
                    if (response.code() == 404) {
                        throw new FileNotFoundException("The requested file cannot be found or the file link has expired.");
                    }

                    BufferedSource source = getAsRawBytes(response);
                    if (listener != null) {
                        ProgressListener realListener = listener;
                        if (callbackExecutor != null) {
                            realListener = new ExecutorProgressListener(listener, callbackExecutor);
                        }

                        source = Okio.buffer(new ProgressCountingSource(
                                source,
                                response.body().contentLength(),
                                realListener,
                                progressCallbackThresholdBytes));
                    }

                    sink.readAll(source);
                    return null;
                } finally {
                    closeQuietly(response);
                }
            }
        });
    }

    @Override
    public Call<BufferedSource> download(RemoteFile file) {
        if (file == null) {
            throw new IllegalArgumentException("RemoteFile argument cannot be null.");
        }

        DownloadOptions options = DownloadOptions.create()
                .skipFilename(false)
                .contentType(file.getContentType())
                .build();
        return newCall(newDownloadLinkRequest(file.getFileId(), options), new ResponseAdapter<BufferedSource>() {
            @Override
            public BufferedSource adapt(Response response) throws IOException, ApiError {
                FileLink link = getAsFileLink(response);
                return newDownloadCall(link);
            }
        });
    }

    @Override
    public Call<BufferedSource> download(FileLink fileLink) {
        if (fileLink == null) {
            throw new IllegalArgumentException("FileLink argument cannot be null.");
        }

        return newCall(newDownloadRequest(fileLink), new ResponseAdapter<BufferedSource>() {
            @Override
            public BufferedSource adapt(Response response) throws IOException, ApiError {
                return getAsRawBytes(response);
            }
        });
    }

    @Override
    public Call<RemoteFile> copyFile(long fileId, long toFolderId) {
        RequestBody body = new FormBody.Builder()
                .add("fileid", String.valueOf(fileId))
                .add("tofolderid", String.valueOf(toFolderId))
                .add("noover", String.valueOf(1))
                .build();

        Request request = newRequest()
                .url(API_BASE_URL.newBuilder()
                        .addPathSegment("copyfile")
                        .build())
                .post(body)
                .build();

        return newCall(request, new ResponseAdapter<RemoteFile>() {
            @Override
            public RemoteFile adapt(Response response) throws IOException, ApiError {
                return getAsApiResponse(response, GetFileResponse.class).getFile();
            }
        });
    }

    @Override
    public Call<RemoteFile> copyFile(RemoteFile file, RemoteFolder toFolder) {
        if (file == null) {
            throw new IllegalArgumentException("file argument cannot be null.");
        }
        if (toFolder == null) {
            throw new IllegalArgumentException("toFolder argument cannot be null.");
        }

        return copyFile(file.getFileId(), toFolder.getFolderId());
    }

    @Override
    public Call<RemoteFile> moveFile(long fileId, long toFolderId) {
        RequestBody body = new FormBody.Builder()
                .add("fileid", String.valueOf(fileId))
                .add("tofolderid", String.valueOf(toFolderId))
                .build();

        Request request = newRequest()
                .url(API_BASE_URL.newBuilder()
                        .addPathSegment("renamefile")
                        .build())
                .post(body)
                .build();

        return newCall(request, new ResponseAdapter<RemoteFile>() {
            @Override
            public RemoteFile adapt(Response response) throws IOException, ApiError {
                return getAsApiResponse(response, GetFileResponse.class).getFile();
            }
        });
    }

    @Override
    public Call<RemoteFile> moveFile(RemoteFile file, RemoteFolder toFolder) {
        if (file == null) {
            throw new IllegalArgumentException("file argument cannot be null.");
        }
        if (toFolder == null) {
            throw new IllegalArgumentException("toFolder argument cannot be null.");
        }

        return moveFile(file.getFileId(), toFolder.getFolderId());
    }

    @Override
    public Call<RemoteFile> renameFile(long fileId, String newFilename) {
        if (newFilename == null) {
            throw new IllegalArgumentException("newFileName argument cannot be null.");
        }

        RequestBody body = new FormBody.Builder()
                .add("fileid", String.valueOf(fileId))
                .add("toname", newFilename)
                .build();

        Request request = newRequest()
                .url(API_BASE_URL.newBuilder()
                        .addPathSegment("renamefile")
                        .build())
                .post(body)
                .build();

        return newCall(request, new ResponseAdapter<RemoteFile>() {
            @Override
            public RemoteFile adapt(Response response) throws IOException, ApiError {
                return getAsApiResponse(response, GetFileResponse.class).getFile();
            }
        });
    }

    @Override
    public Call<RemoteFile> renameFile(RemoteFile file, String newFilename) {
        if (file == null) {
            throw new IllegalArgumentException("file argument cannot be null.");
        }

        return renameFile(file.getFileId(), newFilename);
    }

    @Override
    public Call<RemoteFolder> createFolder(RemoteFolder parentFolder, String folderName) {
        if (parentFolder == null) {
            throw new IllegalArgumentException("folder argument cannot be null.");
        }
        return createFolder(parentFolder.getFolderId(), folderName);
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
    public Call<Boolean> deleteFolder(RemoteFolder folder) {
        if (folder == null) {
            throw new IllegalArgumentException("folder argument cannot be null.");
        }
        return deleteFolder(folder.getFolderId(), false);
    }

    @Override
    public Call<Boolean> deleteFolder(RemoteFolder folder, boolean recursively) {
        if (folder == null) {
            throw new IllegalArgumentException("folder argument cannot be null.");
        }
        return deleteFolder(folder.getFolderId(), recursively);
    }

    @Override
    public Call<Boolean> deleteFolder(long folderId) {
        return deleteFolder(folderId, false);
    }

    @Override
    public Call<Boolean> deleteFolder(long folderId, boolean recursively) {
        RequestBody body = new FormBody.Builder()
                .add("folderid", String.valueOf(folderId))
                .build();

        Request request = newRequest()
                .url(API_BASE_URL.newBuilder()
                        .addPathSegment(recursively ? "deletefolderrecursive" : "deletefolder")
                        .build())
                .post(body)
                .build();

        return newCall(request, new ResponseAdapter<Boolean>() {
            @Override
            public Boolean adapt(Response response) throws IOException, ApiError {
                getAsApiResponse(response, ApiResponse.class);
                return true;
            }
        });
    }

    @Override
    public Call<RemoteFolder> renameFolder(RemoteFolder folder, String newFolderName) {
        if (folder == null) {
            throw new IllegalArgumentException("folder argument cannot be null.");
        }
        return renameFolder(folder.getFolderId(), newFolderName);
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
    public Call<RemoteFolder> moveFolder(RemoteFolder folder, RemoteFolder toFolder) {
        if (folder == null || toFolder == null) {
            throw new IllegalArgumentException("folder argument cannot be null.");
        }
        return moveFolder(folder.getFolderId(), toFolder.getFolderId());
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
    public Call<RemoteFolder> copyFolder(RemoteFolder folder, RemoteFolder toFolder) {
        if (folder == null || toFolder == null) {
            throw new IllegalArgumentException("folder argument cannot be null.");
        }
        return copyFolder(folder.getFolderId(), toFolder.getFolderId());
    }

    @Override
    public Call<RemoteFolder> copyFolder(long folderId, long toFolderId) {
        return newCall(createCopyFolderRequest(folderId, toFolderId), new ResponseAdapter<RemoteFolder>() {
            @Override
            public RemoteFolder adapt(Response response) throws IOException, ApiError {
                return getAsApiResponse(response, GetFolderResponse.class).getFolder();
            }
        });
    }

    private Request createCopyFolderRequest(long folderId, long toFolderId) {
        RequestBody body = new FormBody.Builder()
                .add("folderid", String.valueOf(folderId))
                .add("tofolderid", String.valueOf(toFolderId))
                .build();

        return newRequest()
                .url(API_BASE_URL.newBuilder()
                        .addPathSegment("copyfolder")
                        .build())
                .post(body)
                .build();
    }

    @Override
    public Call<UserInfo> getUserInfo() {
        Request request = newRequest()
                .url(API_BASE_URL.newBuilder()
                        .addPathSegment("userinfo")
                        .build())
                .get().build();

        return newCall(request, new ResponseAdapter<UserInfo>() {
            @Override
            public UserInfo adapt(Response response) throws IOException, ApiError {
                UserInfoResponse body = getAsApiResponse(response, UserInfoResponse.class);
                return new RealUserInfo(body.getUserId(),
                        body.getEmail(),
                        body.isEmailVerified(),
                        body.getTotalQuota(),
                        body.getUsedQuota());
            }
        });
    }

    @Override
    public RealApiServiceBuilder newBuilder() {
        Authenticator authenticator = null;
        for (Interceptor interceptor : httpClient.interceptors()) {
            if (interceptor instanceof RealAuthenticator) {
                authenticator = (Authenticator) interceptor;
                break;
            }
        }
        return new RealApiServiceBuilder(httpClient, callbackExecutor, progressCallbackThresholdBytes, authenticator);
    }

    @Override
    public void shutdown() {
        this.httpClient.connectionPool().evictAll();
        this.httpClient.dispatcher().executorService().shutdownNow();
        closeQuietly(this.httpClient.cache());
    }

    long progressCallbackThresholdBytes() {
        return progressCallbackThresholdBytes;
    }

    OkHttpClient httpClient() {
        return httpClient;
    }

    Executor callbackExecutor() {
        return callbackExecutor;
    }

    private Request.Builder newRequest() {
        return new Request.Builder().url(API_BASE_URL);
    }

    private Request createListFolderRequest(long folderId, boolean recursive) {
        HttpUrl.Builder urlBuilder = API_BASE_URL.newBuilder()
                .addPathSegment("listfolder")
                .addQueryParameter("folderid", String.valueOf(folderId))
                .addQueryParameter("noshares", String.valueOf(1));
        if (recursive) {
            urlBuilder.addEncodedQueryParameter("recursive", String.valueOf(1));
        }

        return newRequest()
                .url(urlBuilder.build())
                .get()
                .build();
    }

    private <T> Call<T> newCall(Request request, ResponseAdapter<T> adapter) {
        Call<T> apiCall = new OkHttpCall<>(httpClient.newCall(request), adapter);
        if (callbackExecutor != null) {
            return new ScheduledCall<>(apiCall, callbackExecutor);
        } else {
            return apiCall;
        }
    }

    private <T extends ApiResponse> T getAsApiResponse(Response response, Class<? extends T> bodyType) throws IOException, ApiError {
        T body = deserializeResponseBody(response, bodyType);
        if (body == null) {
            throw new IOException("API returned an empty response body.");
        }
        if (body.isSuccessful()) {
            return body;
        } else {
            throw new ApiError(body.getStatusCode(), body.getMessage());
        }
    }

    private <T> T deserializeResponseBody(Response response, Class<? extends T> bodyType) throws IOException {
        try {
            if (!response.isSuccessful()) {
                throw new APIHttpException(response.code(), response.message());
            }

            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(response.body().byteStream())));
            try {
                return gson.fromJson(reader, bodyType);
            } catch (JsonSyntaxException e) {
                throw new IOException("Malformed JSON response.", e);
            } finally {
                closeQuietly(reader);
            }
        } finally {
            closeQuietly(response);
        }
    }

    private Request newDownloadRequest(FileLink link) {
        return new Request.Builder()
                .url(link.getBestUrl())
                .get()
                .build();
    }

    private BufferedSource newDownloadCall(FileLink fileLink) throws IOException {
        return newDownloadCall(newDownloadRequest(fileLink));
    }

    private BufferedSource newDownloadCall(Request request) throws IOException {
        Response response = httpClient.newCall(request).execute();
        return getAsRawBytes(response);
    }

    private BufferedSource getAsRawBytes(Response response) throws APIHttpException {
        boolean callWasSuccessful = false;
        try {
            if (response.isSuccessful()) {
                callWasSuccessful = true;
                return response.body().source();
            } else {
                throw new APIHttpException(response.code(), response.message());
            }
        } finally {
            if (!callWasSuccessful) {
                closeQuietly(response);
            }
        }
    }
}
