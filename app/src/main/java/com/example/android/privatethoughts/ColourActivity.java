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

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

/**
 * activity to select the colour of the journal entry
 */

public class ColourActivity extends AppCompatActivity {

    private RadioButton blueRadioButton, pinkRadioButton, yellowRadioButton,
            greenRadioButton, purpleRadioButton, defaultRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colour);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        blueRadioButton =  findViewById(R.id.rb_blue);
        pinkRadioButton = findViewById(R.id.rb_pink);
        yellowRadioButton = findViewById(R.id.rb_yellow);
        greenRadioButton = findViewById(R.id.rb_green);
        purpleRadioButton = findViewById(R.id.rb_purple);
        defaultRadioButton = findViewById(R.id.rb_default);

    }

    public void changeColor(View view) {
        RadioButton radioButton = (RadioButton) view;

        Intent intent = new Intent(this, InsertActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(InsertActivity.COLOUR_INDEX, radioButton.getText().toString());
        intent.putExtra(InsertActivity.TITLE_INDEX, getIntent().getStringExtra(InsertActivity.TITLE_INDEX));
        intent.putExtra(InsertActivity.CONTENT_INDEX, getIntent().getStringExtra(InsertActivity.CONTENT_INDEX));

        if (getIntent().getData() != null) {
            intent.setData(getIntent().getData());
        }

        startActivity(intent);
        finish();
    }
}
