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

package com.example.android.privatethoughts.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.privatethoughts.LoginActivity;

/**
 * content provider class for the Private Thoughts app.
 * it insert, updates, deletes and queries.
 */
public class JournalProvider extends ContentProvider{

    public static final int CODE_JOURNAL = 100;
    public static final int CODE_JOURNAL_WITH_TIMESDTAMP = 101;
    public static final int CODE_JOURNAL_ACCOUNT = 102;
    public static final int CODE_JOURNAL_ACCOUNT_WITH_EMAIL = 103;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private JournalDbHelper mDbHelper;

    /**
     *implements a uri matcher for uri matching
     * @return a uri matcher that with all codes loaded
     */
    public static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = JournalContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, JournalContract.PATH_JOURNAL, CODE_JOURNAL);
        uriMatcher.addURI(authority, JournalContract.PATH_JOURNAL + "/#", CODE_JOURNAL_WITH_TIMESDTAMP);
        uriMatcher.addURI(authority, JournalContract.PATH_JOURNAL_ACCOUNT, CODE_JOURNAL_ACCOUNT);
        uriMatcher.addURI(authority, JournalContract.PATH_JOURNAL_ACCOUNT + "/#", CODE_JOURNAL_ACCOUNT_WITH_EMAIL);

        return uriMatcher;
    }

    /**
     * Initialize content provider on startup
     * @return true if successfully loaded
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new JournalDbHelper(getContext());
        return true;
    }

    /**
     *Handles Query requests
     * @param uri           URI to quey
     * @param projection    list of columns requested, if null, loads all
     * @param selection     selection criteria for filter, if null, no filter added
     * @param selectionArgs values of the filter
     * @param sortOrder     arrangment of the coursor
     * @return cursor containing the info
     */
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        /**
         * Switch function based on uri passed after comparing it to the predifined uris using the uri matcher
         */
        switch (sUriMatcher.match(uri)) {
            case CODE_JOURNAL: {
                String[] selectionParameters = new String[]{LoginActivity.EMAIL_ACCOUNT};

                cursor = mDbHelper.getReadableDatabase().query(
                        JournalContract.JournalEntry.TABLE_NAME,
                        projection,
                        JournalContract.JournalEntry.COLUMN_EMAIL + " = ? ",
                        selectionParameters,
                        null,
                        null,
                        sortOrder);

                break;
            }
            case CODE_JOURNAL_WITH_TIMESDTAMP: {
                String normalizedTimestamp = uri.getLastPathSegment();
                String[] selectionParameters = new String[]{normalizedTimestamp, LoginActivity.EMAIL_ACCOUNT};

                cursor = mDbHelper.getReadableDatabase().query(
                        JournalContract.JournalEntry.TABLE_NAME,
                        projection,
                        JournalContract.JournalEntry.COLUMN_TIMESTAMP + " = ? "
                                + " AND " + JournalContract.JournalEntry.COLUMN_EMAIL + " = ? ",
                        selectionParameters,
                        null,
                        null,
                        sortOrder);

                break;
            }
            case CODE_JOURNAL_ACCOUNT: {
               cursor = mDbHelper.getReadableDatabase().query(
                        JournalContract.JournalAccount.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }
            case CODE_JOURNAL_ACCOUNT_WITH_EMAIL: {
                String emailAddress = uri.getLastPathSegment();
                String[] selectionParameters = new String[]{emailAddress};

                cursor = mDbHelper.getReadableDatabase().query(
                        JournalContract.JournalAccount.TABLE_NAME,
                        projection,
                        JournalContract.JournalAccount.COLUMN_EMAIL + " = ? ",
                        selectionParameters,
                        null,
                        null,
                        sortOrder);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unable to match URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * funtion not needed hence not implemented
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Methode has yet to be initialized");
    }

    /**
     * Handles inisertions to the database
     * @param uri       the uri of the table
     * @param values    the values to be inserted
     * @return the uri pointing to the new table entry
     */
    @Override
    public Uri insert(@NonNull Uri uri, @NonNull ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_JOURNAL: {
                db.beginTransaction();
                int rowsInserted = 0;

                try {
                    long _id = mDbHelper.getWritableDatabase().insert(
                            JournalContract.JournalEntry.TABLE_NAME,
                            null,
                            values);

                    if (_id != -1) {
                        rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return uri.buildUpon()
                        .appendPath(Integer.toString(values.getAsInteger(
                                JournalContract.JournalEntry.COLUMN_TIMESTAMP)))
                        .build();
            }
            case CODE_JOURNAL_ACCOUNT: {
                db.beginTransaction();
                int rowsInserted = 0;

                try {
                    long _id = mDbHelper.getWritableDatabase().insert(
                            JournalContract.JournalAccount.TABLE_NAME,
                            null,
                            values);

                    if (_id != -1) {
                        rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return uri.buildUpon()
                        .appendPath(values.getAsString(JournalContract.JournalAccount.COLUMN_EMAIL))
                        .build();
            }
            default:
                return null;
        }
    }

    /**
     * Handles deletion from the tables
     * @param uri           URI poitning to the table entry to be deleted
     * @param selection     selection criteria for filter, if null, no filter added
     * @param selectionArgs values of the filter
     * @return the number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int countRowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE_JOURNAL: {
                countRowsDeleted = mDbHelper.getWritableDatabase().delete(
                        JournalContract.JournalEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;
            }
            case CODE_JOURNAL_WITH_TIMESDTAMP: {
                String normalizedTimastamp = uri.getLastPathSegment();
                String[] selectionParameters = new String[]{normalizedTimastamp, LoginActivity.EMAIL_ACCOUNT};

                countRowsDeleted = mDbHelper.getWritableDatabase().delete(
                        JournalContract.JournalEntry.TABLE_NAME,
                        JournalContract.JournalEntry.COLUMN_TIMESTAMP + " = ? "
                                + " AND " + JournalContract.JournalEntry.COLUMN_EMAIL + " = ? ",
                        selectionParameters);

                break;
            }
            case CODE_JOURNAL_ACCOUNT: {
                countRowsDeleted = mDbHelper.getWritableDatabase().delete(
                        JournalContract.JournalAccount.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;
            }
            case CODE_JOURNAL_ACCOUNT_WITH_EMAIL: {
                String emailAddress = uri.getLastPathSegment();
                String[] selectionParameters = new String[]{emailAddress};

                countRowsDeleted = mDbHelper.getWritableDatabase().delete(
                        JournalContract.JournalAccount.TABLE_NAME,
                        JournalContract.JournalAccount.COLUMN_EMAIL + " = ? ",
                        selectionParameters);

                break;
            }
            default:
                throw new UnsupportedOperationException("unable to match URI: " + uri);
        }

        if (countRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return countRowsDeleted;
    }

    /**
     * Handled updates to the table
     * @param uri           URI of the table entry to e changed
     * @param values        new value
     * @param selection     selection criteria for filter, if null, no filter added
     * @param selectionArgs values of the filter
     * @return number of rows added
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case CODE_JOURNAL_WITH_TIMESDTAMP: {
                String normalizedTimastamp = uri.getLastPathSegment();
                String[] selectionParameters = new String[]{normalizedTimastamp, LoginActivity.EMAIL_ACCOUNT};

                int _id = mDbHelper.getWritableDatabase().update(
                        JournalContract.JournalEntry.TABLE_NAME,
                        values,
                        JournalContract.JournalEntry.COLUMN_TIMESTAMP + " = ? "
                                + " AND " + JournalContract.JournalEntry.COLUMN_EMAIL + " = ? ",
                        selectionParameters);

                if (_id > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return _id;
            }
            case CODE_JOURNAL_ACCOUNT_WITH_EMAIL: {
                String emailAddress = uri.getLastPathSegment();
                String[] selectionParameters = new String[]{emailAddress};

                int _id = mDbHelper.getWritableDatabase().update(
                        JournalContract.JournalAccount.TABLE_NAME,
                        values,
                        JournalContract.JournalAccount.COLUMN_EMAIL + " = ? ",
                        selectionParameters);

                if (_id > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return _id;
            }
            default:
                return 0;
        }
    }

    /**
     * Handles counting number of rows
     * @param uri table URI
     * @return the number of roes from te chosen table
     */
    public int getCount(@NonNull Uri uri) {
        int count = 0;

        switch (sUriMatcher.match(uri)) {
            case CODE_JOURNAL: {
                String[] selectionParameters = new String[]{LoginActivity.EMAIL_ACCOUNT};

                Cursor cursor;
                cursor = mDbHelper.getReadableDatabase().query(
                        JournalContract.JournalEntry.TABLE_NAME,
                        null,
                        JournalContract.JournalEntry.COLUMN_EMAIL + " = ? ",
                        selectionParameters,
                        null,
                        null,
                        null);

                count = cursor.getCount();

                cursor.close();

                break;
            }
            case CODE_JOURNAL_ACCOUNT: {
                Cursor cursor;
                cursor = mDbHelper.getReadableDatabase().query(
                        JournalContract.JournalAccount.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

                count = cursor.getCount();

                cursor.close();
                break;
            }
            default:
                throw new UnsupportedOperationException("unable to match URI: " + uri);
        }

        return count;
    }
}
