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

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.privatethoughts.data.JournalContract;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Main activity with a recyler view containing all the journal entries.
 */

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        JournalAdapter.JournalAdapterOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String[] MAIN_JOURNAL_PROJECTION = {
            JournalContract.JournalEntry.COLUMN_TITLE,
            JournalContract.JournalEntry.COLUMN_CONTENT,
            JournalContract.JournalEntry.COLUMN_TIMESTAMP,
            JournalContract.JournalEntry.COLUMN_COLOUR,
            JournalContract.JournalEntry.COLUMN_EMAIL,
            JournalContract.JournalEntry.COLUMN_PASSWORD
    };

    public static final int INDEX_JOURNAL_TITLE = 0;
    public static final int INDEX_JOURNAL_CONTENT = 1;
    public static final int INDEX_JOURNAL_TIMESTAMP = 2;
    public static final int INDEX_JOURNAL_COLOUR = 3;
    public static final int INDEX_JOURNAL_EMAIL = 4;
    public static final int INDEX_JOURNAL_PASSWORD = 5;

    public static final int ID_JOURNAL_LOADER = 11;

    private GoogleSignInClient mGoogleSignInClient;

    private JournalAdapter mJournalAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    private ProgressBar mLoaderIndicator;
    private FloatingActionButton mButtonAddJournalEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonAddJournalEntry = findViewById(R.id.button_add_journal_entry);
        mRecyclerView = findViewById(R.id.recycler_view_journal);
        mLoaderIndicator = findViewById(R.id.pb_load_indicator);

        mButtonAddJournalEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InsertActivity.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mJournalAdapter = new JournalAdapter(this, this);

        mRecyclerView.setAdapter(mJournalAdapter);

        showLoading();

        getSupportLoaderManager().initLoader(ID_JOURNAL_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.activity_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();

            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

            mGoogleSignInClient.signOut();

            GoogleLoginActivity.EMAIL_ACCOUNT = null;

            SharedPreferences sharedpreferences =
                    this.getSharedPreferences(GoogleLoginActivity.MY_PREFRENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(this, GoogleLoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_JOURNAL_LOADER:
                Uri journalQueryUri = JournalContract.JournalEntry.CONTENT_URI;

                String sortOrder = JournalContract.JournalEntry.COLUMN_TIMESTAMP + " DESC";

                return new CursorLoader(this,
                        journalQueryUri,
                        MAIN_JOURNAL_PROJECTION,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader ID not found; " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getSupportActionBar().setTitle(getString(R.string.app_name) + " (" + data.getCount() + ")");

        mJournalAdapter.swapCursor(data);

        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;

        mRecyclerView.smoothScrollToPosition(mPosition);

        if (data != null && data.getCount() != 0){
            showJournalEntries();
        } else {
            hideLoading();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mJournalAdapter.swapCursor(null);
    }

    @Override
    public void onClick(long timestamp) {
        Intent intent = new Intent(MainActivity.this, InsertActivity.class);
        Uri uriForTimestampQuery = JournalContract.JournalEntry.buildJournalUriWithTimestamp(timestamp);
        intent.setData(uriForTimestampQuery);
        startActivity(intent);
    }

    /**
     * hide the recycler view and show the progress bar
     */
    private void showLoading(){
        mLoaderIndicator.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    /**
     * hide the progress bar and show the recycler view
     */
    private void showJournalEntries(){
        mLoaderIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * hide both the progress bar and the recylerview
     */
    private void hideLoading() {
        mLoaderIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }
}
