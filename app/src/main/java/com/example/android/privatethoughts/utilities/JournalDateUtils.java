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

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * implements functions for converting date to long and back.
 * also passes date inrequred formates
 */
public class JournalDateUtils {
    public static final long DAYS_IN_MILLIS = TimeUnit.DAYS.toMillis(1);

    public static String getDayString(Context mContext, long timestampInMillis) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
        return dayFormat.format(timestampInMillis);
    }

    public static String getDateString(Context mContext, long timestampInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, ''yy");
        return dateFormat.format(timestampInMillis);
    }

    public static String getTimeString(Context mContext, long timestampInMillis) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(timestampInMillis);
    }

    public static long normalizeDate(long l) {
        long daysSinceEpoch = daysElapsedSinceEpoch(l);
        long milisecondsFromEpoch = daysSinceEpoch * DAYS_IN_MILLIS;
        return milisecondsFromEpoch;
    }

    private static long daysElapsedSinceEpoch(long date) {
        return TimeUnit.MILLISECONDS.toDays(date);
    }
}
