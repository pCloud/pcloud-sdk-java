package com.pcloud.sdk;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A data class for holding authentication request parameters.
 *
 * @see Builder
 */
public class AuthorizationRequest implements Parcelable {

    /**
     * The type of OAuth flow to be executed.
     * <p>
     * For more information on available auth flows, read <a href=https://docs.pcloud.com/methods/oauth_2.0/authorize.html>this page</a>.
     */
    public enum Type {
        CODE(0),
        TOKEN(1);

        protected final int code;

        Type(int code) {
            this.code = code;
        }

        static Type fromCode(int code) {
            switch (code) {
                case 0:
                    return CODE;
                case 1:
                    return TOKEN;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }


    /**
     * Create a new {@link AuthorizationRequest} instance.
     *
     * @return non-null {@linkplain Builder}
     */
    public static AuthorizationRequest.Builder create() {
        return new AuthorizationRequest.Builder();
    }

    public final Type type;
    public final String clientId;
    public final Set<String> permissions;
    public final boolean forceAccessApproval;

    AuthorizationRequest(Builder builder) {
        this.type = builder.type;
        this.clientId = builder.clientId;
        this.permissions = Collections.unmodifiableSet(builder.permissions);
        this.forceAccessApproval = builder.forceAccessApproval;
    }

    protected AuthorizationRequest(Parcel in) {
        type = Type.fromCode(in.readByte());
        clientId = in.readString();
        int permissionsSize = in.readInt();
        if (permissionsSize > 0) {
            List<String> entries = new ArrayList<>(permissionsSize);
            in.readStringList(entries);
            permissions = Collections.unmodifiableSet(new HashSet<>(entries));
        } else {
            permissions = Collections.emptySet();
        }
        forceAccessApproval = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) type.code);
        dest.writeString(clientId);
        dest.writeInt(permissions.size());
        if (!permissions.isEmpty()) {
            dest.writeStringList(new ArrayList<>(permissions));
        }
        dest.writeByte((byte) (forceAccessApproval ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AuthorizationRequest> CREATOR = new Creator<AuthorizationRequest>() {
        @Override
        public AuthorizationRequest createFromParcel(Parcel in) {
            return new AuthorizationRequest(in);
        }

        @Override
        public AuthorizationRequest[] newArray(int size) {
            return new AuthorizationRequest[size];
        }
    };


    /**
     * Change the parameters of this request.
     *
     * @return new {@linkplain Builder} with the properties of this instance
     */
    public Builder mutate() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorizationRequest that = (AuthorizationRequest) o;

        if (forceAccessApproval != that.forceAccessApproval) return false;
        if (type != that.type) return false;
        if (!clientId.equals(that.clientId)) return false;
        return permissions.equals(that.permissions);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + clientId.hashCode();
        result = 31 * result + permissions.hashCode();
        result = 31 * result + (forceAccessApproval ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AuthorizationRequest{" +
                "type=" + type +
                ", clientId='" + clientId + '\'' +
                ", permissions=" + permissions +
                ", forceAccessApproval=" + forceAccessApproval +
                '}';
    }

    public static class Builder {
        private static final Set<String> EMPTY_PERMISSIONS = Collections.emptySet();

        private AuthorizationRequest.Type type;
        private String clientId;
        private Set<String> permissions = EMPTY_PERMISSIONS;
        private boolean forceAccessApproval = false;

        Builder() {
        }

        Builder(AuthorizationRequest request) {
            this.type = request.type;
            this.clientId = request.clientId;
            this.permissions = request.permissions;
            this.forceAccessApproval = request.forceAccessApproval;
        }

        /**
         * Set the authorization request type
         *
         * @param type non-null {@linkplain Type}
         * @return this
         * @see Type
         */
        public Builder setType(@NonNull AuthorizationRequest.Type type) {
            if (type == null) {
                throw new NullPointerException();
            }
            this.type = type;
            return this;
        }


        /**
         * Set the clientId of the application that will be requesting authorization
         *
         * @param clientId non-null {@code string}
         * @return this
         */
        public Builder setClientId(@NonNull String clientId) {
            if (clientId == null) {
                throw new NullPointerException();
            }
            this.clientId = clientId;
            return this;
        }

        /**
         * Add permission to be requested by the application that will be authorized
         *
         * @param permission non-null {@code string}
         * @return this
         */
        public Builder addPermission(@NonNull String permission) {
            if (permission == null) {
                throw new NullPointerException();
            }
            if (this.permissions == EMPTY_PERMISSIONS) {
                this.permissions = new HashSet<>();
            }
            this.permissions.add(permission);
            return this;
        }

        /**
         * Remove permission being requested by the application that will be authorized
         *
         * @param permission non-null {@code string}
         * @return this
         */
        public Builder removePermission(@NonNull String permission) {
            if (permission == null) {
                throw new NullPointerException();
            }
            if (this.permissions != EMPTY_PERMISSIONS) {
                this.permissions.remove(permission);
            }
            return this;
        }

        /**
         * Force the re-approval of the application that will be authorized
         *
         * @param forceAccessApproval {@code true} to force re-approval, otherwise {@code false}
         * @return this
         */
        public Builder setForceAccessApproval(boolean forceAccessApproval) {
            this.forceAccessApproval = forceAccessApproval;
            return this;
        }

        /**
         * Build a {@linkplain AuthorizationRequest} instance.
         *
         * @return non-null {@linkplain AuthorizationRequest}
         * @throws IllegalStateException if {@link #setType(Type)} has not been called.
         * @throws IllegalStateException if {@link #setClientId(String)}  has not been called.
         */
        public AuthorizationRequest build() {
            if (type == null) {
                throw new IllegalStateException("setType() not called.");
            }
            if (clientId == null) {
                throw new IllegalStateException("setClientId() not called.");
            }
            return new AuthorizationRequest(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Builder builder = (Builder) o;

            if (forceAccessApproval != builder.forceAccessApproval) return false;
            if (type != builder.type) return false;
            if (clientId != null ? !clientId.equals(builder.clientId) : builder.clientId != null)
                return false;
            return permissions.equals(builder.permissions);
        }

        @Override
        public int hashCode() {
            int result = type != null ? type.hashCode() : 0;
            result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
            result = 31 * result + permissions.hashCode();
            result = 31 * result + (forceAccessApproval ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "type=" + type +
                    ", clientId='" + clientId + '\'' +
                    ", permissions=" + permissions +
                    ", forceAccessApproval=" + forceAccessApproval +
                    '}';
        }
    }
}
