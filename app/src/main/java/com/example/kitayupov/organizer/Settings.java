package com.example.kitayupov.organizer;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

    public static final String STRING = "organizer";
    public static SortType sortType = SortType.DATE;

    public enum SortType {NAME, DATE, RATING}

    public static void loadSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(STRING, Context.MODE_PRIVATE);
        sortType = SortType.valueOf(preferences.getString("sortType", sortType.name()));
    }

    public static void saveSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sortType", sortType.name())
                .apply();
    }

}
