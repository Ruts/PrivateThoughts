package com.example.android.privatethoughts.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Switch;

import com.example.android.privatethoughts.data.JournalContract;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class TableEventsUtils extends AsyncTask<TableTaskParams, Void, Void>{

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference("server/saving-data/journal");

    private static final String TABLE_REFRENCE = "journal_table";

    @Override
    protected Void doInBackground(TableTaskParams... tableTaskParams) {
        int operation = tableTaskParams[0]._operation;
        Context context = tableTaskParams[0]._context;
        Uri uri = tableTaskParams[0]._uri;
        ContentValues contentValues = tableTaskParams[0]._contentValues;

        DatabaseReference journalDatabse = databaseReference.child(TABLE_REFRENCE);

        switch (operation) {
            case TableTaskParams.INSERT_INDEX: {
                context.getContentResolver().insert(uri, contentValues);
                Map<String, JournalTabel> journalEntry = new HashMap<>();

                String uriLastPath = uri.getLastPathSegment();

                if (uriLastPath.matches(JournalContract.PATH_JOURNAL)) {
                    JournalTabel journalTabel = new JournalTabel(
                            contentValues.getAsString(JournalContract.JournalEntry.COLUMN_EMAIL),
                            contentValues.getAsString(JournalContract.JournalEntry.COLUMN_TITLE),
                            contentValues.getAsString(JournalContract.JournalEntry.COLUMN_CONTENT),
                            contentValues.getAsString(JournalContract.JournalEntry.COLUMN_COLOUR),
                            contentValues.getAsString(JournalContract.JournalEntry.COLUMN_PASSWORD),
                            contentValues.getAsLong(JournalContract.JournalEntry.COLUMN_TIMESTAMP));

                    journalEntry.put(contentValues.getAsString(JournalContract.JournalEntry.COLUMN_EMAIL), journalTabel);

                    journalDatabse.setValue(journalEntry);
                }
                break;
            }
            case TableTaskParams.UPDATE_INDEX: {
                context.getContentResolver().update(uri, contentValues, null, null);
                Map<String, Object> journalEntry = new HashMap<>();

                JournalTabel journalTabel = new JournalTabel(
                        contentValues.getAsString(JournalContract.JournalEntry.COLUMN_EMAIL),
                        contentValues.getAsString(JournalContract.JournalEntry.COLUMN_TITLE),
                        contentValues.getAsString(JournalContract.JournalEntry.COLUMN_CONTENT),
                        contentValues.getAsString(JournalContract.JournalEntry.COLUMN_COLOUR),
                        contentValues.getAsString(JournalContract.JournalEntry.COLUMN_PASSWORD),
                        contentValues.getAsLong(JournalContract.JournalEntry.COLUMN_TIMESTAMP));

                journalEntry.put(contentValues.getAsString(JournalContract.JournalEntry.COLUMN_EMAIL), journalTabel);

                journalDatabse.updateChildren(journalEntry);
                break;
            }
            case TableTaskParams.DELETE_INDEX: {
                context.getContentResolver().delete(uri, null, null);
//                journalDatabse.child(contentValues.getAsString(JournalContract.JournalEntry.COLUMN_EMAIL)).removeValue();
                break;
            }
            default:
                throw new UnsupportedOperationException("unable to match operation index: " + operation);
        }

        return null;
    }

    public static class JournalTabel {
        public String _email;
        public String _title;
        public String _content;
        public String _color;
        public String _password;
        public long _timestamp;

        public JournalTabel(String email, String title, String content, String color, String password, long timestamp) {
            this._email = email;
            this._title = title;
            this._content = content;
            this._color = color;
            this._password = password;
            this._timestamp = timestamp;
        }
    }
}
