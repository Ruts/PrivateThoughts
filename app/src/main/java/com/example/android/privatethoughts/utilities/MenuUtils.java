/*
 * Copyright 2017 The Android Open Source Project
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
 */

package com.example.android.privatethoughts.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.privatethoughts.LoginActivity;
import com.example.android.privatethoughts.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Defines actions to be taken when a menu item is clicked
 */
public class MenuUtils {
    /**
     * Defines action to be taken when the log out menu item is clicked
     * @param context of the app
     */
    public static void logoutAction(Context context) {
        GoogleSignInClient mGoogleSignInClient;

        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
        mGoogleSignInClient.signOut();

        LoginActivity.EMAIL_ACCOUNT = null;

        SharedPreferences sharedpreferences =
                context.getSharedPreferences(LoginActivity.MY_PREFRENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }
}
