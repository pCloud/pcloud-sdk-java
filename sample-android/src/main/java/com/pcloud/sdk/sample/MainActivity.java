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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pcloud.sdk.AuthorizationActivity;
import com.pcloud.sdk.AuthorizationResult;

public class MainActivity extends Activity {

    private static final int PCLOUD_AUTHORIZATION_REQUEST_CODE = 123;

    private TextView apiKeyView;
    private TextView authorizationResultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authorizationResultView = (TextView) findViewById(R.id.authorizationResult);
        apiKeyView = (TextView) findViewById(R.id.apiKey);

        findViewById(R.id.authorizeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiKeyView.setText(null);
                authorizationResultView.setText(null);
                //TODO Set YOUR application Client ID
                Intent authIntent = AuthorizationActivity.createIntent(MainActivity.this, "My Application Client ID");
                startActivityForResult(authIntent, PCLOUD_AUTHORIZATION_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PCLOUD_AUTHORIZATION_REQUEST_CODE) {
            AuthorizationResult result = (AuthorizationResult) data.getSerializableExtra(AuthorizationActivity.KEY_AUTHORIZATION_RESULT);
            authorizationResultView.setText(result.name());

            if (result == AuthorizationResult.ACCESS_GRANTED) {
                String accessToken = data.getStringExtra(AuthorizationActivity.KEY_ACCESS_TOKEN);
                long userId = data.getLongExtra(AuthorizationActivity.KEY_USER_ID, 0);
                apiKeyView.setText(accessToken);
                //TODO: Do what's needed :)
            } else {
                //TODO: Add proper handling for denied grants or errors.
            }
        }
    }
}
