package com.pcloud.sdk;

import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A container for a @{link {@link RemoteFile}} and the available checksums of its contents.
 */
public interface Checksums {

    /**
     * Return the SHA-1 digest of the file, if avaialable.
     *<p>
     * For details on availability, refer to the related <a href="https://docs.pcloud.com/methods/file/checksumfile.html" target="_blank">documentation page</a>.
     *
     * @return SHA-1 digest or {@code null} if not available
     */
    @Nullable
    ByteString getSha1();

    /**
     * Return the SHA-256 digest of the file, if avaialable.
     *<p>
     * For details on availability, refer to the related <a href="https://docs.pcloud.com/methods/file/checksumfile.html" target="_blank">documentation page</a>.
     *
     * @return SHA-256 digest or {@code null} if not available
     */
    @Nullable
    ByteString getSha256();

    /**
     * Return the MD5 digest of the file, if avaialable.
     *<p>
     * For details on availability, refer to the related <a href="https://docs.pcloud.com/methods/file/checksumfile.html" target="_blank">documentation page</a>.
     *
     * @return MD5 digest or {@code null} if not available
     */
    @Nullable
    ByteString getMd5();

    /**
     * Return the metadata about the target file.
     *
     * @return non-null {@link RemoteFile }
     */
    @NotNull
    RemoteFile getFile();
}
