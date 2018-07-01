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

/**
 * Class to define variables needed for database CRUD functions
 */
public class TableTaskParams {
    public static final int INSERT_INDEX = 0;
    public static final int UPDATE_INDEX = 1;
    public static final int DELETE_INDEX = 2;

    public Context _context;
    public ContentValues _contentValues;
    public Uri _uri;
    public int _operation;

    /**
     * Initializes the variables to be used
     * @param context       of the app
     * @param contentValues the values of the table entry
     * @param uri           the url of the table or row
     * @param operation     the operation to be carried out
     */
    public TableTaskParams(Context context, ContentValues contentValues, Uri uri, int operation) {
        this._context = context;
        this._contentValues = contentValues;
        this._uri = uri;
        this._operation = operation;
    }
}
