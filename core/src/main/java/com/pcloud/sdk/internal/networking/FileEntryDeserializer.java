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

package com.pcloud.sdk.internal.networking;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.pcloud.sdk.api.FileEntry;
import com.pcloud.sdk.api.RemoteFile;
import com.pcloud.sdk.api.RemoteFolder;

import java.lang.reflect.Type;

public class FileEntryDeserializer implements JsonDeserializer<FileEntry> {
    @Override
    public FileEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json.getAsJsonObject().get("isfolder").getAsBoolean()){
            return context.deserialize(json, RemoteFolder.class);
        }else{
            return context.deserialize(json, RemoteFile.class);
        }
    }
}
