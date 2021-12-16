package com.pcloud.sdk;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static final List<String> DEFAULT_CUSTOM_TAB_PACKAGES = Collections.unmodifiableList(Arrays.asList(
            "com.android.chrome", "com.chrome.beta", "com.chrome.dev",
            "org.mozilla.firefox", "org.mozilla.firefox_beta", "org.mozilla.fenix"
    ));


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
    public final List<String> allowedCustomTabPackages;
    public final boolean forceAccessApproval;

    AuthorizationRequest(Builder builder) {
        this.type = builder.type;
        this.clientId = builder.clientId;
        this.permissions = Collections.unmodifiableSet(builder.permissions);
        this.allowedCustomTabPackages = Collections.unmodifiableList(builder.allowedCustomTabPackages);
        this.forceAccessApproval = builder.forceAccessApproval;
    }

    protected AuthorizationRequest(Parcel in) {
        type = Type.fromCode(in.readByte());
        clientId = in.readString();
        final int permissionsSize = in.readInt();
        if (permissionsSize > 0) {
            Set<String> entries = new HashSet<>(permissionsSize);
            for (int i = 0; i< permissionsSize; i++) {
                entries.add(in.readString());
            }
            permissions = Collections.unmodifiableSet(entries);
        } else {
            permissions = Collections.emptySet();
        }
        final int packagesSize = in.readInt();
        if (packagesSize > 0) {
            List<String> allowedCustomTabPackages = new ArrayList<>();
            for (int i = 0; i< packagesSize; i++) {
                allowedCustomTabPackages.add(in.readString());
            }
            this.allowedCustomTabPackages = Collections.unmodifiableList(allowedCustomTabPackages);
        } else {
            this.allowedCustomTabPackages = Collections.emptyList();
        }
        forceAccessApproval = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) type.code);
        dest.writeString(clientId);
        dest.writeInt(permissions.size());
        for (String permission : permissions) {
            dest.writeString(permission);
        }
        dest.writeInt(allowedCustomTabPackages.size());
        for (String allowedPackage : allowedCustomTabPackages) {
            dest.writeString(allowedPackage);
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

    @NonNull
    @Override
    public String toString() {
        return "AuthorizationRequest{" +
                "type=" + type +
                ", clientId='" + clientId + '\'' +
                ", permissions=" + permissions +
                ", customTabsPackages=" + allowedCustomTabPackages +
                ", forceAccessApproval=" + forceAccessApproval +
                '}';
    }

    public static class Builder {

        private AuthorizationRequest.Type type;
        private String clientId;
        private final Set<String> permissions;
        private final List<String> allowedCustomTabPackages;
        private boolean forceAccessApproval = false;

        Builder() {
            allowedCustomTabPackages = new ArrayList<>(DEFAULT_CUSTOM_TAB_PACKAGES);
            permissions = new HashSet<>();
        }

        Builder(AuthorizationRequest request) {
            this.type = request.type;
            this.clientId = request.clientId;
            this.permissions = new HashSet<>(request.permissions);
            this.allowedCustomTabPackages = new ArrayList<>(request.allowedCustomTabPackages);
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
            this.permissions.remove(permission);
            return this;
        }


        /**
         * Configure what application packages (Browser Applications) can be used
         * to provide Custom Tabs support for displaying the OAuth page.
         *
         * Providing an empty list will effectively disable custom tabs and the
         * OAuth flow will be carried out entirely through a WebView
         *
         * @param allowedPackages non-null list of Android package names
         * @return this
         */
        public Builder setAllowedCustomTabPackages(@NonNull List<String> allowedPackages) {
            this.allowedCustomTabPackages.clear();
            this.allowedCustomTabPackages.addAll(allowedPackages);
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
         * @throws IllegalStateException if {@link #setType(AuthorizationRequest.Type)} has not been called.
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

        @NonNull
        @Override
        public String toString() {
            return "Builder{" +
                    "type=" + type +
                    ", clientId='" + clientId + '\'' +
                    ", permissions=" + permissions +
                    ", customTabsPackages=" + allowedCustomTabPackages +
                    ", forceAccessApproval=" + forceAccessApproval +
                    '}';
        }
    }
}
