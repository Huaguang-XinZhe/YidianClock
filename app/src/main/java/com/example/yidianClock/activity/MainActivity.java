package com.example.yidianClock.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yidianClock.Flash;
import com.example.yidianClock.ManagerAlarm;
import com.example.yidianClock.MyUtils;
import com.example.yidianClock.YDAlarm;
import com.example.yidianClock.databinding.ActivityMainBinding;
import com.example.yidianClock.model.LunchAlarm;
import com.example.yidianClock.model.SleepAlarm;
import com.example.yidianClock.receiver.UnlockReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private final YDAlarm alarm = new YDAlarm(this);
    private final UnlockReceiver unlockReceiver = new UnlockReceiver();
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        sp = getSharedPreferences("sp", MODE_PRIVATE);

        //创建数据库，并初始化数据。注意！这是耗时操作！！！
        initDBData();
        alarm.setFinally();

        FloatingActionButton fab = mainBinding.fab;
        //fab短按监听
        fab.setOnClickListener(v -> {
            alarm.setFinally();
        });
        //fab长按监听
        fab.setOnLongClickListener(v -> {
            //始终开启
            alarm.setLimitAlarm();
            //将设置闲娱限止的状态存入sp中
            sp.edit().putBoolean("isLimitAlarmSet", true).apply();
            return true;
        });

        //震光提示点击取消
        mainBinding.shockLightTipTV.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                new ManagerAlarm(this).cancel();
            }
        });

//        mainBinding.lunchPOTLayout.setOnClickListener(v -> MyPeriodPicker.getInstance(this).setAndShow());
        //主页闹钟图片，点击跳转到系统闹钟列表
        mainBinding.alarmHomeImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册解锁广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(unlockReceiver, filter);

        //test
//        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        List<Sensor> list = manager.getSensorList(Sensor.TYPE_ALL);
//        for (Sensor sensor : list) {
//            Log.i("Test", sensor.getName());
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(unlockReceiver);
    }

    private void initDBData() {
        if (sp.getBoolean("myDBInit_JustDoOnce", true)) {
            new LunchAlarm().save();
            new SleepAlarm().save();
            sp.edit().putBoolean("myDBInit_JustDoOnce", false).apply();
        }
    }
}