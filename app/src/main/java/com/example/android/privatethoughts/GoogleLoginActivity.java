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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.privatethoughts.data.JournalContract;
import com.example.android.privatethoughts.utilities.VerificationTransformationUtils;
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

import java.util.ArrayList;
import java.util.List;

/**
 *activity for google authentication
 */

public class GoogleLoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = GoogleLoginActivity.class.getSimpleName();

    public static final String[] MAIN_JOURNAL_ACCOUNT_PROJECTION = {
            JournalContract.JournalAccount.COLUMN_EMAIL,
            JournalContract.JournalAccount.COLUMN_PASSWORD,
            JournalContract.JournalAccount.COLUMN_USERNAME
    };

    public static final int INDEX_JOURNAL_ACCOUNT_EMAIL = 0;
    public static final int INDEX_JOURNAL_ACCOUNT_PASSWORD = 1;
    public static final int INDEX_JOURNAL_ACCOUNT_USERNAME = 2;

    public static final int ID_JOURNAL_ACCOUNT_LOADER = 33;

    public static final int DEFAULT_PASSWORD = 1234;

    private Cursor mCursor;

    private FirebaseAuth mFirebaseAuth;

    private static final int RC_SIGN_IN = 11;
    private GoogleSignInClient mGoogleSignInClient;

    private SignInButton mSignInButton;
    private ProgressBar mLoaderIndicatorSignIn, mLoaderIndicatorAccount;
    private ProgressDialog mProgressDialog;
    private TextView mRegistrationButton, mLoginButton, mExitButton;
    private EditText mPassword;
    private AutoCompleteTextView mEmailAddress;

    public static final String MY_PREFRENCES = "my_prefrences";
    public static final String EMAIL_KEY = "email_key";
    public static final String PASSWORD_KEY = "password_key";
    private SharedPreferences mSharedPreferences;

    public static String EMAIL_ACCOUNT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        mLoaderIndicatorSignIn = findViewById(R.id.pb_load_indicator_sign_in);
        mLoaderIndicatorAccount = findViewById(R.id.pb_load_indicator_account_names);
        mEmailAddress = findViewById(R.id.textview_email);
        mPassword = findViewById(R.id.edit_password);
        mRegistrationButton = findViewById(R.id.textview_register);
        mLoginButton = findViewById(R.id.btn_login);
        mExitButton = findViewById(R.id.btn_exit);
        mSignInButton = findViewById(R.id.sign_in_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valid()) {
                    setEmailAccount(mEmailAddress.getText().toString());
                    editSharedPrefrences(mEmailAddress.getText().toString());
                    signIn();
                }
            }
        });

        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoogleLoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSignInButton.setSize(SignInButton.SIZE_WIDE);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(googleSignInIntent, RC_SIGN_IN);

                showLoading();
            }
        });

        mProgressDialog = new ProgressDialog(GoogleLoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Authenticating...");

        FirebaseApp.initializeApp(this);
        mFirebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        mSharedPreferences = this.getSharedPreferences(GoogleLoginActivity.MY_PREFRENCES, Context.MODE_PRIVATE);

        String email = mSharedPreferences.getString(EMAIL_KEY, null);

        if (account != null) {
            setEmailAccount(account.getEmail());
            editSharedPrefrences(account.getEmail());
            firebaseAuthWithGoogle(account);
        } else if (email != null && !email.isEmpty()) {
            setEmailAccount(email);
            editSharedPrefrences(email);
            signIn();
        } else {
            getSupportLoaderManager().initLoader(ID_JOURNAL_ACCOUNT_LOADER, null, this);
        }
    }

    private boolean valid() {
        boolean valid = true;

        String email = mEmailAddress.getText().toString();
        String password = mPassword.getText().toString();

        if (email.isEmpty()) {
            mEmailAddress.setError(getString(R.string.error_no_email));
            valid = false;
        } else if(!VerificationTransformationUtils.checkEmail(email)) {
            mEmailAddress.setError(getString(R.string.error_invalid_email));
            valid = false;
        }

        if (password.isEmpty()) {
            mPassword.setError(getString(R.string.error_no_password));
            valid = false;
        } else {
            if (mCursor != null && mCursor.getCount() > 0) {
                mCursor.moveToFirst();

                do {
                    if (mCursor.getString(INDEX_JOURNAL_ACCOUNT_EMAIL).matches(email)) {
                        if (Integer.parseInt(password) != mCursor.getInt(INDEX_JOURNAL_ACCOUNT_PASSWORD)) {
                            mPassword.setError(getString(R.string.error_incorrect_password));
                            valid = false;
                            break;
                        }
                    }
                } while (mCursor.moveToNext());
            }
        }

        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        googleSignIn(firebaseUser);
    }

    private void googleSignIn(FirebaseUser account) {
        if (account != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void signIn() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showProgressDialog();

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount googleAccount = task.getResult(ApiException.class);
            setEmailAccount(googleAccount.getEmail());
            editSharedPrefrences(googleAccount.getEmail());
            firebaseAuthWithGoogle(googleAccount);
        } catch (ApiException e) {
            Log.w(TAG, "Sign in failed code = " + e.getStatusCode());
            googleSignIn(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        mFirebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            googleSignIn(firebaseUser);
                        } else {
                            showSignInButton();
                            Toast.makeText(GoogleLoginActivity.this, getString(R.string.msg_failed_login), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void editSharedPrefrences(String emailAccount){
        mSharedPreferences = getSharedPreferences(MY_PREFRENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(EMAIL_KEY, emailAccount);
        editor.putInt(PASSWORD_KEY, DEFAULT_PASSWORD);
        editor.apply();
    }

    private void setEmailAccount(String emailAccount){
        EMAIL_ACCOUNT = VerificationTransformationUtils.getEmailString(emailAccount);
    }

    /**
     * hide the recycler view and show the progress bar
     */
    private void showLoading(){
        mLoaderIndicatorSignIn.setVisibility(View.VISIBLE);
        mSignInButton.setVisibility(View.GONE);
    }

    /**
     * hide the progress bar, dismiss progress dialogand show the sign in buttob
     */
    private void showSignInButton(){
        mLoaderIndicatorSignIn.setVisibility(View.GONE);
        mSignInButton.setVisibility(View.VISIBLE);
        mProgressDialog.dismiss();
    }

    /**
     * hide the progress bar, show progress dialog and show the sign in buttob
     */
    private void showProgressDialog() {
        mLoaderIndicatorSignIn.setVisibility(View.GONE);
        mSignInButton.setVisibility(View.VISIBLE);
        mProgressDialog.show();
    }

    /**
     * hide the progress bar and show email view
     */
    private void showEmailView() {
        mLoaderIndicatorAccount.setVisibility(View.GONE);
        mEmailAddress.setVisibility(View.VISIBLE);
        mEmailAddress.requestFocus();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case ID_JOURNAL_ACCOUNT_LOADER:
                Uri journalQueryUri = JournalContract.JournalAccount.CONTENT_URI;

                return new CursorLoader(this,
                        journalQueryUri,
                        MAIN_JOURNAL_ACCOUNT_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader ID not found; " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursor = data;

        List<String> emailAccountList = new ArrayList<>();

        if (data != null && data.getCount() > 0) {
            data.moveToFirst();

            do {
                emailAccountList.add(data.getString(INDEX_JOURNAL_ACCOUNT_EMAIL));
            } while (data.moveToNext());
        }

        String[] emailAccountArray = emailAccountList.toArray(new String[emailAccountList.size()]);

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emailAccountArray);

        mEmailAddress.setThreshold(1);
        mEmailAddress.setAdapter(arrayAdapter);

        mEmailAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPassword.requestFocus();
            }
        });

        showEmailView();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
