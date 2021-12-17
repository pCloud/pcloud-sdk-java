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

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

public class GetChecksumFileResponse extends ApiResponse {

    @Expose
    public String sha1 = null;
    @Expose
    public String md5 = null;
    @Expose
    public String sha256 = null;
    
//    // possibility to also return file metadata not used 
//    @Expose
//    @SerializedName("metadata")
//    private RemoteFile remoteFile;
//
//    
//    public RemoteFile getFile() {
//        return remoteFile;
//    }
    
    /**
     * 
     * @return list of hashes available. possible keys are "SHA-1" (exists always), "MD-5" (us only), "SHA-256" (eu only).
     */
    public Map<String, String> getChecksums() {
    	Map<String, String> result = new HashMap<>();
    	if (sha1!=null) {
    		result.put("SHA-1", sha1);
    	}
    	if (md5!=null) {
    		result.put("MD-5", md5);
    	}
    	if (sha256!=null) {
    		result.put("SHA-256", sha256);
    	}
    	return result;
    }
}
