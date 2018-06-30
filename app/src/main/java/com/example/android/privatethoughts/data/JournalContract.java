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
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;

import com.example.android.privatethoughts.GoogleLoginActivity;

/**
 *describes the table structure as well as URL's
 */

public class JournalContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.privatethoughts";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_JOURNAL = "journal";
    public static final String PATH_JOURNAL_ACCOUNT = "journal_account";

    public static final class JournalEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_JOURNAL)
                .build();

        public static final String TABLE_NAME = "journal_";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_COLOUR = "colour";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";

        /**
         * implememnt a uri with a timestamp attached at the end which point to th e table entry
         * @param timestamp the time of the insert or edit normalizzed into milliseconds
         * @return Uri pointing to the specofoc table row corresponding to the timestamp passed
         */
        public static Uri buildJournalUriWithTimestamp(long timestamp) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(timestamp))
                    .build();
        }
    }

    public static final class JournalAccount implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_JOURNAL_ACCOUNT)
                .build();

        public static final String TABLE_NAME = "journal_account";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";

        /**
         * implememnt a uri with a timestamp attached at the end which point to th e table entry
         * @param email the time of the insert or edit normalizzed into milliseconds
         * @return Uri pointing to the specofoc table row corresponding to the timestamp passed
         */
        public static Uri buildJournalAccountUriWithEmail(String email) {
            return CONTENT_URI.buildUpon()
                    .appendPath(email)
                    .build();
        }
    }
}
