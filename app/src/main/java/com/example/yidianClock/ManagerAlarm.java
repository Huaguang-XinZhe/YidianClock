package com.example.yidianClock;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.yidianClock.receiver.ManagerAlarmReceiver;

import java.util.Calendar;
import java.util.TimeZone;

public class ManagerAlarm {
    Context context;
    AlarmManager manager;
    PendingIntent pi;
    
    @RequiresApi(api = Build.VERSION_CODES.M)
    public ManagerAlarm(Context context) {
        this.context = context;
        manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        //这里只是传入了BroadcastReceiver的实例，并没有注册它
        Intent intent = new Intent(context, ManagerAlarmReceiver.class);
        pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    /**
     * 用AlarmManager设置震动闪光提示
     */
    public void set(int interval) {
        long timeMillis = System.currentTimeMillis() + interval * 60*1000L;
        //在指定的时间启动提示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pi);
        } else {
            manager.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pi);
        }
        Log.i("getSongsList", "震光提示已设置");
        Toast.makeText(context, "震光提示已设置", Toast.LENGTH_SHORT).show();
    }

    /**
     * 取消pi设置下的所有闹钟（在这里是取消所有震光提示）
     */
    public void cancel() {
        manager.cancel(pi);
        Toast.makeText(context, "震光提示已取消", Toast.LENGTH_SHORT).show();
    }

//    /**
//     * 得到目标时间的Millis表示
//     */
//    public long getTargetTimeMillis(int hour, int minutes) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
//        cal.set(Calendar.HOUR_OF_DAY, hour);
//        cal.set(Calendar.MINUTE, minutes);
//        return cal.getTimeInMillis();
//    }
}
