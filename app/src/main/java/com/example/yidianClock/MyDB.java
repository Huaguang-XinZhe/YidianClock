package com.example.yidianClock;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.yidianClock.model.LunchAlarm;
import com.example.yidianClock.model.SleepAlarm;

public class MyDB {

    public static void init(Context context) {
        SharedPreferences sp = context.getSharedPreferences("sp", MODE_PRIVATE);;
        if (sp.getBoolean("myDBInit_JustDoOnce", true)) {
            new LunchAlarm().save();
            new SleepAlarm().save();
            sp.edit().putBoolean("myDBInit_JustDoOnce", false).apply();
        }
    }
}
