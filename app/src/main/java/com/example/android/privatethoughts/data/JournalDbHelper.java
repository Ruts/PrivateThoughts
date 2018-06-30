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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.privatethoughts.data.JournalContract.JournalEntry;
import com.example.android.privatethoughts.data.JournalContract.JournalAccount;

/**
 * this class manges the database for Private Thoughts app
 */

public class JournalDbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "journal.db";
    public static final int DATABASE_VERSION = 5;

    public JournalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_JOURNAL_TABLE =
                "CREATE TABLE " + JournalEntry.TABLE_NAME + "(" +
                        JournalEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        JournalEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
                        JournalEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        JournalEntry.COLUMN_CONTENT + " TEXT, " +
                        JournalEntry.COLUMN_COLOUR + " TEXT, " +
                        JournalEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                        JournalEntry.COLUMN_PASSWORD + " TEXT, " +
                        "UNIQUE (" + JournalEntry.COLUMN_TIMESTAMP + ") ON CONFLICT REPLACE)";

        db.execSQL(SQL_CREATE_JOURNAL_TABLE);

        final String SQL_CREATE_JOURNAL_ACCOUNT_TABLE =
                "CREATE TABLE " + JournalAccount.TABLE_NAME + "(" +
                        JournalAccount._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        JournalAccount.COLUMN_USERNAME + " INTEGER NOT NULL, " +
                        JournalAccount.COLUMN_EMAIL + " TEXT NOT NULL, " +
                        JournalAccount.COLUMN_PASSWORD + " INTEGER NOT NULL, " +
                        "UNIQUE (" + JournalAccount.COLUMN_EMAIL + ") ON CONFLICT REPLACE)";

        db.execSQL(SQL_CREATE_JOURNAL_ACCOUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + JournalEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + JournalAccount.TABLE_NAME);
        onCreate(db);
    }
}
