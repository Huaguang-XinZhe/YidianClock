package com.example.yidianClock;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yidianClock.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private final YDAlarm alarm = new YDAlarm(this);

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
            alarm.setFinally();
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
            return true;
        });
    }
}