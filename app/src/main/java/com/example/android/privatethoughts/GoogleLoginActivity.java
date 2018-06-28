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

package com.example.android.privatethoughts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 *activity for google authentication
 */

public class GoogleLoginActivity extends AppCompatActivity {
    private static final String TAG = GoogleLoginActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth;

    private static final int RC_SIGN_IN = 11;
    private GoogleSignInClient mGoogleSignInClient;

    private SignInButton mSignInButton;
    private ProgressBar mLoaderIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        mLoaderIndicator = findViewById(R.id.pb_load_indicator);

        mSignInButton = findViewById(R.id.sign_in_button);
        mSignInButton.setSize(SignInButton.SIZE_STANDARD);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(googleSignInIntent, RC_SIGN_IN);

                showLoading();
            }
        });

        FirebaseApp.initializeApp(this);
        mFirebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            firebaseAuthWithGoogle(account);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        signIn(firebaseUser);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        mFirebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            signIn(firebaseUser);
                        } else {
                            Toast.makeText(GoogleLoginActivity.this, getString(R.string.msg_failed_login), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(FirebaseUser account) {
        if (account != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showSignInButton();

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount googleAccount = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(googleAccount);
        } catch (ApiException e) {
            Log.w(TAG, "Sign in failed code = " + e.getStatusCode());
            signIn(null);
        }
    }

    /**
     * hide the recycler view and show the progress bar
     */
    private void showLoading(){
        mLoaderIndicator.setVisibility(View.VISIBLE);
        mSignInButton.setVisibility(View.GONE);
    }

    /**
     * hide the progress bar and show the recycler view
     */
    private void showSignInButton(){
        mLoaderIndicator.setVisibility(View.GONE);
        mSignInButton.setVisibility(View.VISIBLE);
        final ProgressDialog progressDialog = new ProgressDialog(GoogleLoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
    }
}
