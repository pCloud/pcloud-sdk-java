package com.pcloud.sdk.internal;

import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.ApiError;
import com.pcloud.sdk.Call;
import com.pcloud.sdk.ContentLink;
import com.pcloud.sdk.DataSink;
import com.pcloud.sdk.ProgressListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okio.BufferedSource;

class RealContentLink implements ContentLink {
    private final ApiClient apiClient;
    private final Date expirationDate;
    private final List<URL> links;

    RealContentLink(ApiClient apiClient, Date expirationDate, List<URL> links) {
        this.apiClient = apiClient;
        this.expirationDate = expirationDate;
        this.links = Collections.unmodifiableList(links);
    }

    static void requireLinkNotNull(ContentLink fileLink) {
        if (fileLink == null) {
            throw new IllegalArgumentException("FileLink argument cannot be null.");
        }
    }

    static void requireUrlFromLink(ContentLink fileLink, URL linkVariant) {
        if (!fileLink.urls().contains(linkVariant)) {
            throw new IllegalArgumentException("Provided url must be one of the variants in the provided file link.");
        }
    }

    @Override
    public Date expirationDate() {
        return expirationDate;
    }

    @Override
    public List<URL> urls() {
        return links;
    }

    @Override
    public URL bestUrl() {
        return links.get(0);
    }

    @Override
    public InputStream byteStream() throws IOException, ApiError {
        return source(bestUrl()).inputStream();
    }

    @Override
    public InputStream byteStream(URL linkVariant) throws IOException, ApiError {
        return source(linkVariant).inputStream();
    }

    @Override
    public BufferedSource source() throws IOException, ApiError {
        return source(bestUrl());
    }

    @Override
    public BufferedSource source(URL linkVariant) throws IOException, ApiError {
        RealContentLink.requireUrlFromLink(this, linkVariant);
        boolean success = false;
        Call<BufferedSource> call = apiClient.download(this);
        try {
            BufferedSource source = call.execute();
            success = true;
            return source;
        } catch (ApiError apiError) {
            throw new IOException("API error occurred while trying to read from download link.", apiError);
        } finally {
            if (!success) {
                call.cancel();
            }
        }
    }

    @Override
    public void download(DataSink sink, ProgressListener listener) throws IOException, ApiError {
        apiClient.download(this, sink, listener).execute();
    }

    @Override
    public void download(URL linkVariant, DataSink sink, ProgressListener listener) throws IOException, ApiError {
        RealContentLink.requireUrlFromLink(this, linkVariant);
        apiClient.download(this, linkVariant, sink, listener).execute();
    }

    @Override
    public void download(DataSink sink) throws IOException, ApiError {
        apiClient.download(this, bestUrl(), sink, null).execute();
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s | Valid until:%s", bestUrl(), expirationDate());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealContentLink)) return false;
        RealContentLink that = (RealContentLink) o;
        return Objects.equals(expirationDate, that.expirationDate) && Objects.equals(links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expirationDate, links);
    }
}
