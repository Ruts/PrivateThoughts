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

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * implements functions for converting date to long and back.
 * also passes date inrequred formates
 */
public class JournalDateUtils {
    /**
     * Creates a string for the day
     * @param timestampInMillis time in millis
     * @return the string showing the day
     */
    public static String getDayString(long timestampInMillis) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
        return dayFormat.format(timestampInMillis);
    }

    /**
     * Creates the date string
     * @param timestampInMillis time in millis
     * @return string showing date
     */
    public static String getDateString(long timestampInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, ''yy");
        return dateFormat.format(timestampInMillis);
    }

    /**
     * Creates time string
     * @param timestampInMillis time in millis
     * @return string showing time
     */
    public static String getTimeString(long timestampInMillis) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(timestampInMillis);
    }
}
