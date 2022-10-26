package com.example.yidianClock;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.yidianClock.model.LunchAlarm;
import com.example.yidianClock.model.SleepAlarm;

public class MyDB {

    public static void init(Context context) {
        SharedPreferences sp = context.getSharedPreferences("JustDoOnce", MODE_PRIVATE);;
        if (sp.getBoolean("myDB_init", true)) {
            new LunchAlarm().save();
            new SleepAlarm().save();
            sp.edit().putBoolean("myDB_init", false).apply();
        }
    }
}