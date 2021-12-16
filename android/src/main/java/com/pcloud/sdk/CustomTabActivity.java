package com.pcloud.sdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class CustomTabActivity extends Activity {

    public static final int CUSTOM_TAB_REDIRECT_REQUEST_CODE = 1338;
    public static final String CUSTOM_TAB_REDIRECT_ACTION = "CustomTabActivity.CUSTOM_TAB_REDIRECT_ACTION";
    public static final String DESTROY_ACTION = "CustomTabActivity.DESTROY_ACTION";

    private BroadcastReceiver closeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, AuthorizationActivity.class);
        intent.setAction(CUSTOM_TAB_REDIRECT_ACTION);
        intent.putExtra(AuthorizationActivity.EXTRA_REDIRECT_URL, getIntent().getData());

        // these flags will open CustomTabMainActivity from the back stack as well as closing this
        // activity and the custom tab opened by CustomTabMainActivity.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivityForResult(intent, CUSTOM_TAB_REDIRECT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            // We weren't able to open CustomTabMainActivity from the back stack. Send a broadcast
            // instead.
            Intent broadcast = new Intent(CUSTOM_TAB_REDIRECT_ACTION);
            broadcast.putExtra(AuthorizationActivity.EXTRA_REDIRECT_URL, getIntent().getData());
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);

            // Wait for the custom tab to be removed from the back stack before finishing.
            closeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    CustomTabActivity.this.finish();
                }
            };
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(closeReceiver, new IntentFilter(DESTROY_ACTION));
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(closeReceiver);
        super.onDestroy();
    }
}
