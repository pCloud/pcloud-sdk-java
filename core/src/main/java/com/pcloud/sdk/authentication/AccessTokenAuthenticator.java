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

package com.pcloud.sdk.authentication;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;

import static okhttp3.internal.Util.closeQuietly;

final class AccessTokenAuthenticator extends RealAuthenticator {

    private static final long RESPONSE_PEEK_LENGTH_BYTES = 1024L;
    private static final int ERROR_LOGIN_REQUIRED = 1000;

    private String accessToken;

    public AccessTokenAuthenticator(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.isSuccessful()) {
            int resultCode = getResponseCode(response);
            if (resultCode == ERROR_LOGIN_REQUIRED) {
                response.body().close();
                response = response.newBuilder().body(null).build();
                Request authenticatedRequest = setBearerTokenToRequest(chain.request());
                response = chain.proceed(authenticatedRequest)
                        .newBuilder()
                        .priorResponse(response)
                        .build();
            }
        }

        return response;
    }

    private Request setBearerTokenToRequest(Request request){
        if (accessToken != null) {
            return request.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .build();
        }
        return request;
    }

    private int getResponseCode(Response response) throws IOException {
        // Some magic here, part of the response will be deserialized in order to deteremine the 'result' field.
        ResponseBody peekBody = null;
        JsonReader reader = null;
        try {
            peekBody = response.peekBody(RESPONSE_PEEK_LENGTH_BYTES);

            reader = new JsonReader(new BufferedReader(new InputStreamReader(peekBody.byteStream())));
            reader.beginObject();
            if ("result".equals(reader.nextName())){
                return reader.nextInt();
            } else {
                throw new ProtocolException("Result code field is not the first field.");
            }

        } finally {
            closeQuietly(peekBody);
            closeQuietly(reader);
        }
    }
}
