package com.pcloud.sdk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;

public class CustomTab {
    private final Uri uri;

    private CustomTabsServiceConnection connection;
    private CustomTabsSession customTabsSession;

    public CustomTab(Uri authorizationUri) {
        uri = authorizationUri;
        connection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(@NonNull ComponentName componentName, @NonNull CustomTabsClient client) {
                client.warmup(0L);
                customTabsSession = client.newSession(null);
                if(customTabsSession != null) {
                    customTabsSession.mayLaunchUrl(uri, null, null);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
    }

    public void openCustomTab(final Activity activity, String packageName) {
        CustomTabsClient.bindCustomTabsService(activity, activity.getPackageName(), connection);
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder(customTabsSession)
                                                .setShowTitle(true)
                                                .build();
        customTabsIntent.intent.setPackage(packageName);
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        customTabsIntent.intent.setData(uri);
        customTabsIntent.launchUrl(activity, uri);
    }


    public void onDestroy(Activity parentActivity) {
        if (connection != null) {
            parentActivity.unbindService(connection);
            connection = null;
            customTabsSession = null;
        }
    }

}
