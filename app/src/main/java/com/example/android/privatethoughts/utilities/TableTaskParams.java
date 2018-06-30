package com.example.android.privatethoughts.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public class TableTaskParams {
    public static final int INSERT_INDEX = 0;
    public static final int UPDATE_INDEX = 1;
    public static final int DELETE_INDEX = 2;

    public Context _context;
    public ContentValues _contentValues;
    public Uri _uri;
    public int _operation;

    public TableTaskParams(Context context, ContentValues contentValues, Uri uri, int operation) {
        this._context = context;
        this._contentValues = contentValues;
        this._uri = uri;
        this._operation = operation;
    }
}
