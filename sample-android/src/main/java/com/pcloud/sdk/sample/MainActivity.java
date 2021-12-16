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

package com.pcloud.sdk.sample;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.pcloud.sdk.AuthorizationActivity;
import com.pcloud.sdk.AuthorizationData;
import com.pcloud.sdk.AuthorizationRequest;
import com.pcloud.sdk.AuthorizationResult;

public class MainActivity extends Activity {

    private static final int PCLOUD_AUTHORIZATION_REQUEST_CODE = 123;

    private TextView apiKeyView;
    private TextView authorizationResultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authorizationResultView = findViewById(R.id.authorizationResult);
        apiKeyView = findViewById(R.id.apiKey);

        findViewById(R.id.authorizeButton).setOnClickListener(v -> {
            apiKeyView.setText(null);
            authorizationResultView.setText(null);
            Intent authIntent = AuthorizationActivity.createIntent(
                    MainActivity.this,
                    AuthorizationRequest.create()
                            .setType(AuthorizationRequest.Type.TOKEN)
                            .setClientId("6u4r0n9pXIf") //TODO Set YOUR application Client ID
                            .setForceAccessApproval(true)
                            .addPermission("manageshares")
                            .build());
            startActivityForResult(authIntent, PCLOUD_AUTHORIZATION_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PCLOUD_AUTHORIZATION_REQUEST_CODE) {
            AuthorizationData authData = AuthorizationActivity.getResult(data);
            AuthorizationResult result = authData.result;
            authorizationResultView.setText(result.name());
            apiKeyView.setOnClickListener(null);

            switch (result) {
                case ACCESS_GRANTED:
                    //TODO: Do what's needed :)
                    apiKeyView.setText(authData.toString());
                    apiKeyView.setOnClickListener(v -> copyTokenToClipboard(authData));
                    Log.d("pCloud", "Account access granted, authData:\n" + authData);
                    break;
                case ACCESS_DENIED:
                    //TODO: Add proper handling for denied grants.
                    Log.d("pCloud", "Account access denied");
                    break;
                case AUTH_ERROR:
                    //TODO: Add error handling.
                    apiKeyView.setText(authData.errorMessage);
                    Log.d("pCloud", "Account access grant error:\n" + authData.errorMessage);
                    break;
                case CANCELLED:
                    //TODO: Handle cancellation.
                    Log.d("pCloud", "Account access grant cancelled:");
                    break;
            }
        }
    }

    private void copyTokenToClipboard(AuthorizationData authData) {
        ClipData clipData = ClipData.newPlainText("pCloud API Token", authData.token);
        ((ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
        Toast.makeText(this, "Token copied to clipboard.", Toast.LENGTH_SHORT).show();
    }
}
