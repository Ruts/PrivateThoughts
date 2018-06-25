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
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * this class is th content provider for the Private Thoughts app.
 * it insert, updates, deletes and queries.
 */

public class JournalProvider extends ContentProvider{

    public static final int CODE_JOURNAL = 100;
    public static final int CODE_JOURNAL_WITH_TIMESDTAMP = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private JournalDbHelper mDbHelper;

    /**
     *implements a uri matcher that compares a uri to CODE_JOURNAL or CODE_JOURNAL_WITH_TIMESDTAMP
     * @return a uri matcher that distinguishes between CODE_JOURNAL_WITH_TIMESDTAMP and CODE_JOURNAL uris
     */
    public static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = JournalContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, JournalContract.PATH_JOURNAL, CODE_JOURNAL);
        uriMatcher.addURI(authority, JournalContract.PATH_JOURNAL + "/#", CODE_JOURNAL_WITH_TIMESDTAMP);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new JournalDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_JOURNAL: {
                String normalizedTimestamp = uri.getLastPathSegment();
                String[] selectionParameters = new String[]{normalizedTimestamp};

                cursor = mDbHelper.getReadableDatabase().query(
                        JournalContract.JournalEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionParameters,
                        null,
                        null,
                        sortOrder);

                break;
            }
            case CODE_JOURNAL_WITH_TIMESDTAMP: {
                cursor = mDbHelper.getReadableDatabase().query(
                        JournalContract.JournalEntry.TABLE_NAME,
                        projection,
                        JournalContract.JournalEntry.COLUMN_TIMESTAMP + " = ? ",
                        selectionArgs,
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

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Methode has yet to be initialized");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case CODE_JOURNAL: {
                long _id = mDbHelper.getWritableDatabase().insert(
                        JournalContract.JournalEntry.TABLE_NAME,
                        null,
                        values);

                if (_id > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return uri.buildUpon()
                        .appendPath(Integer.toString(values.getAsInteger(
                                JournalContract.JournalEntry.COLUMN_TIMESTAMP)))
                        .build();
            }
            default:
                return null;
        }
    }

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
                String[] selectionParameters = new String[]{normalizedTimastamp};

                countRowsDeleted = mDbHelper.getWritableDatabase().delete(
                        JournalContract.JournalEntry.TABLE_NAME,
                        JournalContract.JournalEntry.COLUMN_TIMESTAMP + " = ? ",
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

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case CODE_JOURNAL: {
                int _id = mDbHelper.getWritableDatabase().update(
                        JournalContract.JournalEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

                if (_id > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return _id;
            }
            default:
                return 0;
        }
    }

    public int getCount() {
        Cursor cursor;

        cursor = mDbHelper.getReadableDatabase().query(
                JournalContract.JournalEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        int count = cursor.getCount();

        cursor.close();

        return count;
    }
}
