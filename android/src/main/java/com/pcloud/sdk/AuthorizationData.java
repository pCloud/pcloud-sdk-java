/*
 * Copyright (c) 2017 pCloud AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pcloud.sdk;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Gives information about the outcome of an initiated authorization.
 *
 * @see AuthorizationActivity
 * @see AuthorizationActivity#getResult(Intent)
 */
@SuppressWarnings("WeakerAccess")
public class AuthorizationData implements Parcelable {

    public final AuthorizationRequest request;
    public final AuthorizationResult result;
    public final String token;
    public final long userId;
    public final long locationId;
    public final String authCode;
    public final String apiHost;
    public final String errorMessage;

    AuthorizationData(
            @NonNull AuthorizationRequest request,
            @NonNull AuthorizationResult result,
            @Nullable String token,
            long userId,
            long locationId,
            @Nullable String authCode,
            @Nullable String apiHost,
            @Nullable String errorMessage) {
        this.request = request;
        this.result = result;
        this.token = token;
        this.userId = userId;
        this.locationId = locationId;
        this.authCode = authCode;
        this.apiHost = apiHost;
        this.errorMessage = errorMessage;
    }

    protected AuthorizationData(Parcel in) {
        request = in.readParcelable(AuthorizationRequest.class.getClassLoader());
        result = (AuthorizationResult) in.readSerializable();
        token = in.readString();
        userId = in.readLong();
        locationId = in.readLong();
        authCode = in.readString();
        apiHost = in.readString();
        errorMessage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(request, flags);
        dest.writeSerializable(result);
        dest.writeString(token);
        dest.writeLong(userId);
        dest.writeLong(locationId);
        dest.writeString(authCode);
        dest.writeString(apiHost);
        dest.writeString(errorMessage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AuthorizationData> CREATOR = new Creator<AuthorizationData>() {
        @Override
        public AuthorizationData createFromParcel(Parcel in) {
            return new AuthorizationData(in);
        }

        @Override
        public AuthorizationData[] newArray(int size) {
            return new AuthorizationData[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorizationData that = (AuthorizationData) o;

        if (userId != that.userId) return false;
        if (locationId != that.locationId) return false;
        if (!request.equals(that.request)) return false;
        if (result != that.result) return false;
        if (!Objects.equals(token, that.token)) return false;
        if (!Objects.equals(authCode, that.authCode))
            return false;
        if (!Objects.equals(apiHost, that.apiHost)) return false;
        return Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        int result1 = request.hashCode();
        result1 = 31 * result1 + result.hashCode();
        result1 = 31 * result1 + (token != null ? token.hashCode() : 0);
        result1 = 31 * result1 + (int) (userId ^ (userId >>> 32));
        result1 = 31 * result1 + (int) (locationId ^ (locationId >>> 32));
        result1 = 31 * result1 + (authCode != null ? authCode.hashCode() : 0);
        result1 = 31 * result1 + (apiHost != null ? apiHost.hashCode() : 0);
        result1 = 31 * result1 + (errorMessage != null ? errorMessage.hashCode() : 0);
        return result1;
    }

    @NotNull
    @Override
    public String toString() {
        return "AuthorizationData{" +
                "request=" + request +
                ", result=" + result +
                ", token='" + token + '\'' +
                ", userId=" + userId +
                ", locationId=" + locationId +
                ", authCode='" + authCode + '\'' +
                ", apiHost='" + apiHost + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}