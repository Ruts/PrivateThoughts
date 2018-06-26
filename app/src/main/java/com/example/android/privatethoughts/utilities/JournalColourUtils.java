package com.example.android.privatethoughts.utilities;

import android.content.Context;

import com.example.android.privatethoughts.R;

public class JournalColourUtils {

    public static int getColourResource(Context context, String colour) {
        if (colour.matches("Yellow")) {
            return context.getResources().getColor(R.color.light_yellow);
        } else if (colour.matches("Pourple")) {
            return context.getResources().getColor(R.color.light_purple);
        } else if (colour.matches("Red")) {
            return context.getResources().getColor(R.color.light_red);
        } else if (colour.matches("Blue")) {
            return context.getResources().getColor(R.color.light_blue);
        } else if (colour.matches("Green")) {
            return context.getResources().getColor(R.color.light_green);
        } else {
            return context.getResources().getColor(R.color.white);
        }
    }
}