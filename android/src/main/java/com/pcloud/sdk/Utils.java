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

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

abstract class Utils {

    static final List<String> CUSTOM_TAB_PACKAGES = Arrays.asList(
            "com.android.chrome", "com.chrome.beta", "com.chrome.dev",
            "org.mozilla.firefox", "org.mozilla.firefox_beta", "org.mozilla.fenix"
    );

    private Utils() {

    }

    @NonNull
    static Map<String, String> parseUrlFragmentParameters(@NonNull String url) {
        String fragment = Uri.parse(url).getFragment();
        if (fragment != null) {
            Map<String, String> parameters = new TreeMap<>();
            String[] keyPairs = fragment.split("&");
            for (String keyPair : keyPairs) {
                int delimiterIndex = keyPair.indexOf('=');
                parameters.put(keyPair.substring(0, delimiterIndex), keyPair.substring(delimiterIndex + 1));
            }

            return parameters;
        }

        return Collections.emptyMap();
    }

    public static String getCustomTabsPackage(Context context, List<String> allowedPackages) {
        if (allowedPackages.isEmpty()) return null;
        Intent serviceIntent = new Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(serviceIntent, 0);
        if (resolveInfos != null) {
            for (String customTabsPackage : allowedPackages) {
                for (ResolveInfo resolveInfo : resolveInfos) {
                    ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                    if (customTabsPackage.equals(serviceInfo.packageName)) {
                        return serviceInfo.packageName;
                    }
                }
            }

        }
        return null;
    }
}
