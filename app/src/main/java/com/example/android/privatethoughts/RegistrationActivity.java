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

import android.content.ContentValues;
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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.privatethoughts.data.JournalContract;
import com.example.android.privatethoughts.utilities.TableEventsUtils;
import com.example.android.privatethoughts.utilities.TableTaskParams;
import com.example.android.privatethoughts.utilities.VerificationTransformationUtils;

/**
 * Class that defines teh registration process
 */
public class RegistrationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final int ID_JOURNAL_ACCOUNT_LOADER = 33;

    private TextView mBackButton, mNextButton;
    private EditText mFullName, mEmail, mPassword, mPasswordConfirmation;
    private SharedPreferences mSharedPreferences;

    private Cursor mCursor;

    /**
     * Initialize the activity and its views
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mBackButton = findViewById(R.id.btn_back);
        mNextButton = findViewById(R.id.btn_next);
        mFullName = findViewById(R.id.edit_full_name);
        mEmail = findViewById(R.id.edit_email);
        mPassword = findViewById(R.id.edit_password);
        mPasswordConfirmation = findViewById(R.id.edit_password_confirmation);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valide()){
                    insertAccount();
                    setSharedPrefrences();
                    setEmailAccount(mEmail.getText().toString());

                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        getSupportLoaderManager().initLoader(ID_JOURNAL_ACCOUNT_LOADER, null, this);
    }

    /**
     * Defines actions to be taken when back is pressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Set the eamil account of the user signing up
     * @param emailAccount of new user
     */
    private void setEmailAccount(String emailAccount){
        LoginActivity.EMAIL_ACCOUNT = VerificationTransformationUtils.getEmailString(emailAccount);
    }

    /**
     * Set the shared prefrences with the information of the new user
     */
    private void setSharedPrefrences() {
        mSharedPreferences = getSharedPreferences(LoginActivity.MY_PREFRENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(LoginActivity.EMAIL_KEY, mEmail.getText().toString());
        editor.putInt(LoginActivity.PASSWORD_KEY, Integer.parseInt(mPassword.getText().toString()));
        editor.apply();
    }

    /**
     * Inserts an account in the table
     */
    private void insertAccount() {
        ContentValues contentValues = new ContentValues();

        String fullName =  mFullName.getText().toString();
        String email =  mEmail.getText().toString();
        int password;

        try {
            password = Integer.parseInt(mPassword.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new NumberFormatException("String was not in number formate");
        }

        contentValues.put(JournalContract.JournalAccount.COLUMN_EMAIL, email);
        contentValues.put(JournalContract.JournalAccount.COLUMN_USERNAME, fullName);
        contentValues.put(JournalContract.JournalAccount.COLUMN_PASSWORD, password);

        TableTaskParams tableTaskParams =
                new TableTaskParams(this, contentValues,
                        JournalContract.JournalAccount.CONTENT_URI, TableTaskParams.INSERT_INDEX);

        new TableEventsUtils().execute(tableTaskParams);
    }

    /**
     * Validate the edit text views
     * @return true if data is valide, false otherwise
     */
    private boolean valide() {
        boolean valide = true;

        String fullName = mFullName.getText().toString();
        String email = mEmail.getText().toString();

        if (fullName.isEmpty()){
            mFullName.setError(getString(R.string.error_no_full_name));
            valide = false;
        } else {
            if (mCursor != null && mCursor.getCount() > 0) {
                mCursor.moveToFirst();

                do {
                    if (mCursor.getString(LoginActivity.INDEX_JOURNAL_ACCOUNT_EMAIL).matches(email)) {
                        mFullName.setError(getString(R.string.error_taken_email));
                        valide = false;
                        break;
                    }
                } while (mCursor.moveToNext());
            }
        }

        if (email.isEmpty()) {
            mEmail.setError(getString(R.string.error_no_email));
            valide = false;
        } else if (!(VerificationTransformationUtils.checkEmail(email))) {
            mEmail.setError(getString(R.string.error_invalid_email));
            valide = false;
        }

        if (mPassword.getText().toString().isEmpty()) {
            mPassword.setError(getString(R.string.error_no_password));
            valide = false;
        }

        if (mPasswordConfirmation.getText().toString().isEmpty()) {
            mPasswordConfirmation.setError(getString(R.string.error_no_password));
            valide = false;
        }

        if (!(mPassword.getText().toString().isEmpty()) && !(mPasswordConfirmation.getText().toString().isEmpty())) {
            int password;
            int passwordConfirmation;

            try {
                password = Integer.parseInt(mPassword.getText().toString());
                passwordConfirmation = Integer.parseInt(mPasswordConfirmation.getText().toString());

            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new NumberFormatException("String was not in number formate");
            }
            if (password != passwordConfirmation) {
                mPassword.setError(getString(R.string.error_password_match));
                mPasswordConfirmation.setError(getString(R.string.error_password_match));
                valide = false;
            }
        }
        
        return valide;
    }

    /**
     * Creats loader and performs cursor loading from databse
     * @param id    od loader
     * @param args  variables passed to loader
     * @return cursor with data from the database
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case ID_JOURNAL_ACCOUNT_LOADER:
                Uri journalQueryUri = JournalContract.JournalAccount.CONTENT_URI;

                return new CursorLoader(this,
                        journalQueryUri,
                        LoginActivity.MAIN_JOURNAL_ACCOUNT_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader ID not found; " + id);
        }
    }

    /**
     * Defines action to be taken after loader creation is finished. sets the fields if the information exists
     * @param loader that has performed teh action
     * @param data information recieved;
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursor = data;
    }

    /**
     * Defines action to be taken when the loader is reset
     * @param loader that has been executed
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
