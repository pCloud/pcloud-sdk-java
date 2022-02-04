package com.pcloud.sdk.internal.networking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pcloud.sdk.Checksums;
import com.pcloud.sdk.RemoteFile;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ChecksumsResponse extends ApiResponse implements Checksums {
    @Expose
    @SerializedName("metadata")
    private RemoteFile file;

    @Expose
    @SerializedName("sha1")
    private ByteString sha1;

    @Expose
    @SerializedName("sha256")
    private ByteString sha256;

    @Expose
    @SerializedName("md5")
    private ByteString md5;

    private ChecksumsResponse()  {
        super();
    }

    public ChecksumsResponse(int statusCode,
                             @Nullable String message,
                             @Nullable ByteString sha1,
                             @Nullable ByteString sha256,
                             @Nullable ByteString md5,
                             @NotNull RemoteFile file) {
        super(statusCode, message);
        if (sha1 == null && sha256 == null && md5 == null) {
            throw new IllegalArgumentException("At least one checksum variant should be non-null.");
        }
        //noinspection ConstantConditions
        if (file == null) {
            throw new NullPointerException("Null file argument.");
        }
        this.sha1 = sha1;
        this.sha256 = sha256;
        this.md5 = md5;
    }

    @Override
    @Nullable
    public ByteString getSha1() {
        return sha1;
    }

    @Override
    @Nullable
    public ByteString getSha256() {
        return sha256;
    }

    @Nullable
    @Override
    public ByteString getMd5() {
        return null;
    }

    @NotNull
    @Override
    public RemoteFile getFile() {
        return file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChecksumsResponse that = (ChecksumsResponse) o;
        return Objects.equals(sha1, that.sha1) && Objects.equals(sha256, that.sha256) && Objects.equals(md5, that.md5) && file.equals(that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sha1, sha256, md5, file);
    }

    @Override
    public String toString() {
        return "ChecksumsResponse{" +
                "file=" + file +
                ", sha1=" + sha1 +
                ", sha256=" + sha256 +
                ", md5=" + md5 +
                '}';
    }
}
