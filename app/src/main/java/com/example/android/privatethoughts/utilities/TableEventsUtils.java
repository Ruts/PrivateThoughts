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

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.privatethoughts.data.JournalContract;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Asynchronous task for database CRUD funtions
 */
public class TableEventsUtils extends AsyncTask<TableTaskParams, Void, Void>{

    private static final FirebaseDatabase sFirebaseDatabase = FirebaseDatabase.getInstance();
    private static final DatabaseReference sDatabaseReference = sFirebaseDatabase.getReference("server/saving-data/journal");
    private static final String TABLE_REFRENCE = "journal_table";

    /**
     * Background task to perform CRUD databse functions
     * @param tableTaskParams set of variabled needed to perform the CRUD functions in the right locations
     * @return null
     */
    @Override
    protected Void doInBackground(TableTaskParams... tableTaskParams) {
        int operation = tableTaskParams[0]._operation;
        Context context = tableTaskParams[0]._context;
        Uri uri = tableTaskParams[0]._uri;
        ContentValues contentValues = tableTaskParams[0]._contentValues;

        DatabaseReference journalDatabse = sDatabaseReference.child(TABLE_REFRENCE);

        /**
         * Switch function todetermin the operation to be handled
         */
        switch (operation) {
            case TableTaskParams.INSERT_INDEX: {
                context.getContentResolver().insert(uri, contentValues);
                Map<String, JournalTable> journalEntry = new HashMap<>();

                String uriLastPath = uri.getLastPathSegment();

                if (uriLastPath.matches(JournalContract.PATH_JOURNAL)) {
                    JournalTable journalTable = new JournalTable(
                            contentValues.getAsString(JournalContract.JournalEntry.COLUMN_EMAIL),
                            contentValues.getAsString(JournalContract.JournalEntry.COLUMN_TITLE),
                            contentValues.getAsString(JournalContract.JournalEntry.COLUMN_CONTENT),
                            contentValues.getAsString(JournalContract.JournalEntry.COLUMN_COLOUR),
                            contentValues.getAsString(JournalContract.JournalEntry.COLUMN_PASSWORD),
                            contentValues.getAsLong(JournalContract.JournalEntry.COLUMN_TIMESTAMP));

                    journalEntry.put(contentValues.getAsString(JournalContract.JournalEntry.COLUMN_EMAIL), journalTable);

                    journalDatabse.setValue(journalEntry);
                }
                break;
            }
            case TableTaskParams.UPDATE_INDEX: {
                context.getContentResolver().update(uri, contentValues, null, null);
                Map<String, Object> journalEntry = new HashMap<>();

                JournalTable journalTable = new JournalTable(
                        contentValues.getAsString(JournalContract.JournalEntry.COLUMN_EMAIL),
                        contentValues.getAsString(JournalContract.JournalEntry.COLUMN_TITLE),
                        contentValues.getAsString(JournalContract.JournalEntry.COLUMN_CONTENT),
                        contentValues.getAsString(JournalContract.JournalEntry.COLUMN_COLOUR),
                        contentValues.getAsString(JournalContract.JournalEntry.COLUMN_PASSWORD),
                        contentValues.getAsLong(JournalContract.JournalEntry.COLUMN_TIMESTAMP));

                journalEntry.put(contentValues.getAsString(JournalContract.JournalEntry.COLUMN_EMAIL), journalTable);

                journalDatabse.updateChildren(journalEntry);
                break;
            }
            case TableTaskParams.DELETE_INDEX: {
                context.getContentResolver().delete(uri, null, null);
                break;
            }
            default:
                throw new UnsupportedOperationException("unable to match operation index: " + operation);
        }

        return null;
    }

    /**
     * Inner class defining table structure for firebase persistence
     */
    public static class JournalTable {
        public String _email;
        public String _title;
        public String _content;
        public String _color;
        public String _password;
        public long _timestamp;

        /**
         * initiate table entry fields
         * @param email     user email
         * @param title     entry title
         * @param content   entry body
         * @param color     entry colour
         * @param password  entry password
         * @param timestamp entry timestamp
         */
        public JournalTable(String email, String title, String content, String color, String password, long timestamp) {
            this._email = email;
            this._title = title;
            this._content = content;
            this._color = color;
            this._password = password;
            this._timestamp = timestamp;
        }
    }
}
