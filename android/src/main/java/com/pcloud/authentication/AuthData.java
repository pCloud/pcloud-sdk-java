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

package com.pcloud.authentication;

import android.os.Parcel;
import android.os.Parcelable;

public class AuthData implements Parcelable {

    public enum Result {
        OK(0), CANCELED(1), ACCESS_DENIED(2);
        public final int code;

        public int getCode() {
            return code;
        }

        Result(int code) {
            this.code = code;

        }
    }

    private String token;
    private Result result;

    AuthData(String token, Result result){
        this.token = token;
        this.result = result;
    }

    public boolean isAuthSuccessfull(){
        return token != null;
    }

    public String getToken() {
        return token;
    }

    public Result getResult() {
        return result;
    }

    private AuthData(Parcel in) {
        token = in.readString();
        result = (Result) in.readValue(Result.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeValue(result);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AuthData> CREATOR = new Parcelable.Creator<AuthData>() {
        @Override
        public AuthData createFromParcel(Parcel in) {
            return new AuthData(in);
        }

        @Override
        public AuthData[] newArray(int size) {
            return new AuthData[size];
        }
    };
}