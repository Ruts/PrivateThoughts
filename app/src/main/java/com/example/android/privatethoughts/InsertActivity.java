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

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.android.privatethoughts.data.JournalContract;
import com.example.android.privatethoughts.databinding.ActivityInsertBinding;
import com.example.android.privatethoughts.utilities.MenuUtils;
import com.example.android.privatethoughts.utilities.TableEventsUtils;
import com.example.android.privatethoughts.utilities.TableTaskParams;
import com.example.android.privatethoughts.utilities.JournalColourUtils;
import com.google.android.gms.common.util.ArrayUtils;

/**
 * activity to display a single journal entry and allows for editing
 */

public class InsertActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ID_DETAIL_JOURNAL = 22;
    private static final String JOURNAL_SHARE_HASHTAG = " #PrivateThoughts";

    private Uri mUri;

    private Cursor mCursor;

    private ActivityInsertBinding mActivityInsertBinding;

    private boolean mNewEntry = true;

    public String viewColour = "default";
    public static String setPassword = "";
    private String mPassword = "";

    private MenuItem mLock, mUnlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivityInsertBinding = DataBindingUtil.setContentView(this, R.layout.activity_insert);

        if (getIntent().getData() != null) {
            mUri = getIntent().getData();
            mNewEntry = false;
            getSupportLoaderManager().initLoader(ID_DETAIL_JOURNAL, null, this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!(mActivityInsertBinding.editTitle.getText().toString().isEmpty())) {
            onInsert();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.activity_insert, menu);

        mLock = menu.findItem(R.id.action_lock);
        mUnlock = menu.findItem(R.id.action_unlock);

        if (getIntent().hasExtra(MainActivity.INDEX_PROTECTED)
                && getIntent().getExtras().getBoolean(MainActivity.INDEX_PROTECTED)) {
            showUnlockMenuItem();
        } else {
            showLockMenuItem();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View dialogLayout = layoutInflater.inflate(R.layout.dialog_set_password, null);
        final EditText mPasswordEdit = dialogLayout.findViewById(R.id.edit_entry_password);

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.action_save) {
            if (validate()) {
                onInsert();
            }
            finish();
            return true;
        }

        if (id == R.id.action_lock) {
            builder.setView(dialogLayout)
                    .setTitle(getString(R.string.msg_lock))
                    .setPositiveButton(R.string.msg_set, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String password = mPasswordEdit.getText().toString();

                            if (password != null && !(password.isEmpty())) {
                                mPassword = password;
                                showUnlockMenuItem();
                            } else {
                                showSnackbar(getString(R.string.error_passowrd_hint));
                            }
                        }
                    })
                    .setNegativeButton(R.string.msg_exit, null)
                    .show();
            return true;
        }

        if (id == R.id.action_unlock) {
            builder.setView(dialogLayout)
                    .setTitle(getString(R.string.msg_unlock_password))
                    .setPositiveButton(R.string.msg_set, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String password = mPasswordEdit.getText().toString();

                            if (password != null && !(password.isEmpty())) {
                                if (password.matches(mCursor.getString(MainActivity.INDEX_JOURNAL_PASSWORD))
                                        || password.matches(mPassword)) {
                                    mPassword = "";
                                    showLockMenuItem();
                                } else {
                                    showSnackbar(getString(R.string.error_incorrect_password));
                                }
                            } else {
                                showSnackbar(getString(R.string.error_incorrect_password));
                            }
                        }
                    })
                    .setNegativeButton(R.string.msg_exit, null)
                    .show();
            return true;
        }

        if (id == R.id.action_colour) {
            final int currentColor = ArrayUtils.indexOf(getResources().getStringArray(R.array.colour_choices), viewColour);

            builder.setTitle(R.string.msg_choose_colour)
                    .setSingleChoiceItems(R.array.colour_choices, currentColor, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String[] colours = getResources().getStringArray(R.array.colour_choices);

                            String colour = colours[which];
                            viewColour = colour;
                            mActivityInsertBinding.constraintLayoutView
                                    .setBackgroundColor(JournalColourUtils
                                            .getColourResource(InsertActivity.this, viewColour));

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.msg_exit, null)
                    .show();
            return true;
        }

        if (id == R.id.action_share) {
            final Intent shareIntent = createShareJournalIntent();
            final PackageManager packageManager = this.getPackageManager();

            if (shareIntent.resolveActivity(packageManager) == null) {
                showSnackbar(getString(R.string.error_cant_send));
                return true;
            }

            if (mPassword != null && !(mPassword.isEmpty())) {
                builder.setView(dialogLayout)
                        .setTitle(getString(R.string.msg_unlock_password))
                        .setPositiveButton(R.string.msg_unlock, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String passwordEdit = mPasswordEdit.getText().toString();

                                if (passwordEdit != null && !(passwordEdit.isEmpty())) {
                                    if (mPassword.matches(passwordEdit)) {
                                        startActivity(shareIntent);
                                    } else {
                                        showSnackbar(getString(R.string.error_incorrect_password));
                                    }
                                } else {
                                    showSnackbar(getString(R.string.error_incorrect_password));
                                }
                            }
                        })
                        .setNegativeButton(R.string.msg_exit, null)
                        .show();
            } else {
                startActivity(shareIntent);
            }
            return true;
        }

        if (id == R.id.action_delete) {
            final AlertDialog.Builder builderDelete = new AlertDialog.Builder(this);
            builderDelete.setIcon(R.drawable.ic_delete_48px)
                    .setTitle(getString(R.string.msg_delete))
                    .setMessage(getString(R.string.msg_confirm_deletion))
                    .setPositiveButton(R.string.msg_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onDelete();
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.msg_exit, null);

            if (mPassword != null && !(mPassword.isEmpty())) {
                builder.setView(dialogLayout)
                        .setTitle(getString(R.string.msg_unlock_password))
                        .setPositiveButton(R.string.msg_unlock, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String passwordEdit = mPasswordEdit.getText().toString();

                                if (passwordEdit != null && !(passwordEdit.isEmpty())) {
                                    if (mPassword.matches(passwordEdit)) {
                                        builderDelete.show();
                                    } else {
                                        showSnackbar(getString(R.string.error_incorrect_password));
                                    }
                                } else {
                                    showSnackbar(getString(R.string.error_incorrect_password));
                                }
                            }
                        })
                        .setNegativeButton(R.string.msg_exit, null)
                        .show();
            } else {
                builderDelete.show();
            }
            return true;
        }

        if (id == R.id.action_logout) {
            if (!(mActivityInsertBinding.editTitle.getText().toString().isEmpty())) {
                onInsert();
            }

            MenuUtils.logoutAction(this);

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case ID_DETAIL_JOURNAL:
                return new CursorLoader(this,
                        mUri,
                        MainActivity.MAIN_JOURNAL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader ID not found; " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;

        boolean dataIsPresent = false;

        if (data != null && data.moveToFirst()) dataIsPresent = true;

        if (!dataIsPresent) return;

        String title = data.getString(MainActivity.INDEX_JOURNAL_TITLE);
        mActivityInsertBinding.editTitle.setText(title);

        String content = data.getString(MainActivity.INDEX_JOURNAL_CONTENT);
        mActivityInsertBinding.editContent.setText(content);

        String colour = data.getString(MainActivity.INDEX_JOURNAL_COLOUR);
        if (colour != null || !colour.isEmpty()) {
            mActivityInsertBinding.constraintLayoutView
                    .setBackgroundColor(JournalColourUtils.getColourResource(this, colour));
            viewColour = colour;
        }

        String password = data.getString(MainActivity.INDEX_JOURNAL_PASSWORD);
        if (password != null && !(password.isEmpty())) {
            mPassword = password;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * validates fields to be sent to database
     * @return true if fields are okay, false otherwise
     */
    private boolean validate(){
        boolean passed = true;

        String title = mActivityInsertBinding.editTitle.getText().toString();

        if (title.isEmpty()) {
            mActivityInsertBinding.editTitle.setError(getString(R.string.error_no_title));
            passed = false;
        }

        return passed;
    }

    private void onInsert(){
        ContentValues contentValues = new ContentValues();

        String title =  mActivityInsertBinding.editTitle.getText().toString();
        String content =  mActivityInsertBinding.editContent.getText().toString();
        long timestamp = System.currentTimeMillis();

        contentValues.put(JournalContract.JournalEntry.COLUMN_TITLE, title);
        contentValues.put(JournalContract.JournalEntry.COLUMN_CONTENT, content);
        contentValues.put(JournalContract.JournalEntry.COLUMN_TIMESTAMP, timestamp);
        contentValues.put(JournalContract.JournalEntry.COLUMN_COLOUR, viewColour);
        contentValues.put(JournalContract.JournalEntry.COLUMN_EMAIL, LoginActivity.EMAIL_ACCOUNT);
        contentValues.put(JournalContract.JournalEntry.COLUMN_PASSWORD, mPassword);

        if (mNewEntry) {
            TableTaskParams tableTaskParams =
                    new TableTaskParams(this, contentValues,
                            JournalContract.JournalEntry.CONTENT_URI, TableTaskParams.INSERT_INDEX);
            new TableEventsUtils().execute(tableTaskParams);
        } else {
            TableTaskParams tableTaskParams =
                    new TableTaskParams(this, contentValues, mUri, TableTaskParams.UPDATE_INDEX);
            new TableEventsUtils().execute(tableTaskParams);
        }
    }

    private void onDelete() {
        if (!mNewEntry) {
            TableTaskParams tableTaskParams =
                    new TableTaskParams(this, null, mUri, TableTaskParams.DELETE_INDEX);

            new TableEventsUtils().execute(tableTaskParams);
        }
    }

    private Intent createShareJournalIntent() {
        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType("text/Plain")
                .setText(mCursor.getString(MainActivity.INDEX_JOURNAL_TITLE) + JOURNAL_SHARE_HASHTAG
                        + "\n\n" + mCursor.getString(MainActivity.INDEX_JOURNAL_CONTENT))
                .getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return intent;
    }

    private void showSnackbar(String message) {
        Snackbar.make(mActivityInsertBinding.constraintLayoutView, message, Snackbar.LENGTH_LONG).show();
    }

    public void showUnlockMenuItem() {
        mUnlock.setVisible(true);
        mLock.setVisible(false);
    }

    public void showLockMenuItem() {
        mUnlock.setVisible(false);
        mLock.setVisible(true);
    }
}
