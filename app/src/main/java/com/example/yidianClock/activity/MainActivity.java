package com.example.yidianClock.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yidianClock.MyDB;
import com.example.yidianClock.MyPicker;
import com.example.yidianClock.YDAlarm;
import com.example.yidianClock.databinding.ActivityMainBinding;
import com.example.yidianClock.receiver.UnlockReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private final YDAlarm alarm = new YDAlarm(this);
    private final UnlockReceiver unlockReceiver = new UnlockReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        //创建数据库，并初始化数据。注意！这是耗时操作！！！
        MyDB.init(this);
        alarm.setFinally();

        FloatingActionButton fab = mainBinding.fab;
        //fab短按监听
        fab.setOnClickListener(v -> {
            alarm.set("19:25", "测试！");
//            alarm.setFinally();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                alarm.dismissAlarm();
//            } else {
//                Toast.makeText(this, "您的手机Android版本过低，无法自动取消闹钟，请到系统闹钟界面手动取消",
//                        Toast.LENGTH_SHORT).show();
//            }
        });
        //fab长按监听
        fab.setOnLongClickListener(v -> {
            //始终开启
            alarm.setLimitAlarm();
            //将设置闲娱限止的状态存入sp中
            SharedPreferences sp = getSharedPreferences("sp", MODE_PRIVATE);
            sp.edit().putBoolean("isLimitAlarmSet", true).apply();
            return true;
        });

//        mainBinding.lunchPOTLayout.setOnClickListener(v -> MyPicker.getInstance(this).setAndShow());
        //主页闹钟图片，点击跳转到系统闹钟列表
        mainBinding.alarmHomeImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册解锁和亮屏广播
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
}