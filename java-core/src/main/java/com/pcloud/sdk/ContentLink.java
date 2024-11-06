package com.pcloud.sdk;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import okio.BufferedSource;

/**
 * A link to the contents of a file entry on a pCloud account's filesystem.
 */
public interface ContentLink extends RemoteData {
    /**
     * @return the {@link Date} until the link is valid. Cannot be null.
     */
    Date expirationDate();

    /**
     * @return a list of {@link URL}s that can be used to access the file contents. Cannot be null or empty.
     */
    List<URL> urls();

    /**
     * @return a {@link URL} considered to have the best access times. Cannot be null.
     */
    URL bestUrl();

    /**
     * Open an {@link InputStream} to the resource.
     * <p>
     * Attempts to open a byte stream to the resource contents.
     * <h3>
     * The method will block until a stream is opened or an exception is thrown.
     * </h3>
     * <h3>
     * NOTE: It is the caller's responsibility to close the resulting {@link InputStream} object
     * or resource leaks will occur.
     * </h3>
     *
     * @param linkVariant which URL variant from {@link #urls()} to be used
     * @return an {@link InputStream} to the contents of the resource. Cannot be null.
     * @throws IOException on a network or API error.
     * @see #source()
     */
    InputStream byteStream(URL linkVariant) throws IOException, ApiError;

    /**
     * Open an {@link BufferedSource} to the resource.
     * <p>
     * Attempts to open a byte stream to the resource contents.
     * <h3>
     * The method will block until a stream is opened or an exception is thrown.
     * </h3>
     * <h3>
     * NOTE: It is the caller's responsibility to close the resulting {@link BufferedSource} object
     * </h3>
     * or resource leaks will occur.
     *
     * @param linkVariant which URL variant from {@link #urls()} to be used
     * @return an {@link BufferedSource} to the contents of the resource. Cannot be null.
     * @throws IOException on a network, IO or API error.
     */
    BufferedSource source(URL linkVariant) throws IOException, ApiError;

    /**
     * Download the contained data to a {@link DataSink}
     * <p>
     * Attempts to download the resource data to the specified location,
     * optionally sending progress updates via a {@link ProgressListener}.
     * <h3>
     * The method will block until the download operation completes or an exception is thrown.
     * </h3>
     *
     * @param linkVariant which URL variant from {@link #urls()} to be used
     * @param sink        a {@link DataSink} instance where data will be downloaded. Must not be null.
     * @param listener    The listener to be used to notify about upload progress. If null, no progress will be reported.
     * @throws IOException              on a network, IO or API error.
     * @throws IllegalArgumentException on a null {@code sink} argument.
     * @throws IllegalArgumentException if the {@code linkVariant} is not from this link.
     */
    void download(URL linkVariant, DataSink sink, ProgressListener listener) throws IOException, ApiError;
}
