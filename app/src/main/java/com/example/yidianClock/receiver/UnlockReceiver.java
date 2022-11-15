package com.example.yidianClock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.yidianClock.MyUtils;
import com.example.yidianClock.TimePoint;
import com.example.yidianClock.YDAlarm;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UnlockReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TestTag", "解锁啦！");
        SharedPreferences sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
        String targetAlarmTime = sp.getString("targetAlarmTime", "0:0");
        boolean isMoreAndCloseTo = MyUtils.getInstance(context).isMoreAndCloseTo(new TimePoint(targetAlarmTime));
        //为应对在闲娱限止时段内解锁手机，而引起的列表弹出，故增加一个判断条件
        String limitAlarmTime = sp.getString("limitAlarmTime", "0:0");
        boolean isBeforeLimit = new TimePoint(MyUtils.getCurrentTime())
                .compareTo(new TimePoint(limitAlarmTime)) <= 0;
        if (!isMoreAndCloseTo && sp.getBoolean("isTargetAlarmSet", false) && !isBeforeLimit) {
            //打开闹钟列表
            new YDAlarm(context).showAlarm();
            Toast.makeText(context, "请手动删除所有睡前闹钟", Toast.LENGTH_LONG).show();
        }
        //一解锁就应该设为false，不管是中途醒来，还是后来的关闭闹钟
        sp.edit().putBoolean("isTargetAlarmSet", false).apply();
    }
}