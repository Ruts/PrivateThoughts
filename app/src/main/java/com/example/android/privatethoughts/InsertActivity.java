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
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.android.privatethoughts.data.JournalContract;
import com.example.android.privatethoughts.databinding.ActivityInsertBinding;
import com.example.android.privatethoughts.utilities.InsertEntry;
import com.example.android.privatethoughts.utilities.JournalColourUtils;
import com.example.android.privatethoughts.utilities.JournalDateUtils;

/**
 * activity to display a single journal entry and allows for editing
 */

public class InsertActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ID_DETAIL_JOURNAL = 22;

    private Uri mUri;

    private ActivityInsertBinding mActivityInsertBinding;

    private boolean mNewEntry = true;

    public static final String TITLE_INDEX = "title";
    public static final String CONTENT_INDEX = "content";
    public static final String COLOUR_INDEX = "colour";
    public String viewColour = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivityInsertBinding = DataBindingUtil.setContentView(this, R.layout.activity_insert);

        if (getIntent().hasExtra(COLOUR_INDEX)) {
            setFieldsFromIntent();
            if (getIntent().getData() != null) {
                mUri = getIntent().getData();
                mNewEntry = false;
            }
        } else if (getIntent().getData() != null) {
            mUri = getIntent().getData();
            mNewEntry = false;
            getSupportLoaderManager().initLoader(ID_DETAIL_JOURNAL, null, this);
        }

        mActivityInsertBinding.constraintLayoutView
                .setBackgroundColor(JournalColourUtils.getColourResource(this, viewColour));
    }

    @Override
    protected void onResume() {
        super.onResume();

        mActivityInsertBinding.constraintLayoutView
                .setBackgroundColor(JournalColourUtils.getColourResource(this, viewColour));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.activity_insert, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.action_save) {
            if (validate()) {
                onInsert();
            }
            onBackPressed();
        }

        if (id == R.id.action_delete) {
            onDelete();
            onBackPressed();
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

    public void changeColour(View view) {
        Intent intent = new Intent(this, ColourActivity.class);

        if (getIntent().getData() != null) {
            intent.setData(getIntent().getData());
        }

        intent.putExtra(TITLE_INDEX, mActivityInsertBinding.editTitle.getText().toString());
        intent.putExtra(CONTENT_INDEX, mActivityInsertBinding.editContent.getText().toString());

        startActivity(intent);
    }

    private void setFieldsFromIntent() {
        viewColour = getIntent().getStringExtra(COLOUR_INDEX);

        String title = getIntent().getStringExtra(TITLE_INDEX);
        mActivityInsertBinding.editTitle.setText(title);

        String content = getIntent().getStringExtra(CONTENT_INDEX);
        mActivityInsertBinding.editContent.setText(content);

        mActivityInsertBinding.constraintLayoutView
                .setBackgroundColor(JournalColourUtils.getColourResource(this, viewColour));
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

        if (mNewEntry) {
            this.getContentResolver().insert(JournalContract.JournalEntry.CONTENT_URI, contentValues);
        } else {
            this.getContentResolver().update(mUri, contentValues, null, null);
        }
    }

    private void onDelete() {
        if (!mNewEntry) {
            this.getContentResolver().delete(mUri, null, null);
        }
    }
}
