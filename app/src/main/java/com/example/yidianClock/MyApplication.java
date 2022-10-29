package com.example.yidianClock;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.util.Log;

import com.example.yidianClock.receiver.AlertReceiver;

import org.litepal.LitePalApplication;

public class MyApplication extends LitePalApplication {
    private static final String ALARM_ALERT = "com.oppo.alarmclock.alarmclock.ALARM_ALERT";
    @SuppressLint("StaticFieldLeak")
    private static MyApplication app;
    private final AlertReceiver receiver = new AlertReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Log.i("TestTag", "MyApplication.onCreate! 应用进程创建！");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ALARM_ALERT);
        registerReceiver(receiver, filter);
        Log.i("TestTag", "闹钟响起广播注册！");
    }



    public static MyApplication getInstance() {
        return app;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i("TestTag", "低内存释放！");
        unregisterReceiver(receiver);
    }
}
