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

import android.content.Context;

import com.example.android.privatethoughts.R;

/**
 * Defines colour text to value mapping
 */
public class JournalColourUtils {

    /**
     * defines the colour value based on the option selected
     * @param context of the app
     * @param colour string value selected
     * @return colour resource value
     */
    public static int getColourResource(Context context, String colour) {
        if (colour.matches("Yellow")) {
            return context.getResources().getColor(R.color.light_yellow);
        } else if (colour.matches("Purple")) {
            return context.getResources().getColor(R.color.light_purple);
        } else if (colour.matches("Pink")) {
            return context.getResources().getColor(R.color.light_pink);
        } else if (colour.matches("Blue")) {
            return context.getResources().getColor(R.color.light_blue);
        } else if (colour.matches("Green")) {
            return context.getResources().getColor(R.color.light_green);
        } else {
            return context.getResources().getColor(R.color.white);
        }
    }
}
