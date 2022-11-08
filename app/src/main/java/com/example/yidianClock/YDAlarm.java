package com.example.yidianClock;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.yidianClock.model.LunchAlarm;
import com.example.yidianClock.model.MyAlarm;
import com.example.yidianClock.model.SleepAlarm;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class YDAlarm {
    private final Context context;

    //针对午休和晚睡这两个特定场景———————————————————————————
    private float restTime;
    private boolean isSetShockTip = true;
    private boolean isSetTask = true;
    private int interval;
    private String ringtoneUriStr;

    //一般设置（针对的是目标闹钟）————————————————————————————————————————
    private String content;
    //不管如何，震动始终都会有，只为是否响铃提供选择
    private boolean isRing;
    // TODO: 2022/10/22 ring的类型

    //类中所需，不用管它们——————————————————————————————————
    private int hour;
    private int minutes;
    //是否已经申请了权限
//    private boolean isRequested = false;
    private LunchAlarm firstLunchAlarm;
    private SleepAlarm firstSleepAlarm;
    private SharedPreferences sp;

    //——————————————————————————————————————————————————————————————————————————————————————

    public YDAlarm(Context context) {
        this(context, true);
    }

    //专为其他程序调用此类中的闹钟设置方法而定制
    public YDAlarm(Context context, boolean isRing) {
        this.context = context;
        this.isRing = isRing;
    }

    //——————————————————————————————————————————————————————————————————————————————————————

    /**
     * 判断现在是白天还是晚上（根据用户设置的一般时段而定，非客观）
     * @return 白天返回day，晚上返回night，其他返回null
     */
    public String getStatus() {
        sp = context.getSharedPreferences("sp", MODE_PRIVATE);
        // TODO: 2022/10/25
        if (sp.getBoolean("getStatus_justDoOnce", true)) {
            //只在安装完成后执行一次，一切都使用默认值，为防止应用崩溃！
            firstLunchAlarm = new LunchAlarm();
            firstSleepAlarm = new SleepAlarm();
            sp.edit().putBoolean("getStatus_justDoOnce", false).apply();
        } else {
            //之后就从数据库中取数据，每设定一次闹钟就取一次（这样能跟得上闹钟参数更新的节奏）
            firstLunchAlarm = LitePal.findFirst(LunchAlarm.class);
            firstSleepAlarm = LitePal.findFirst(SleepAlarm.class);
        }
        //数据库操作，从LunchAlarm表和SleepAlarm表中取出一般时段数据
        TimePoint lunchStart = new TimePoint(firstLunchAlarm.getLunchStart());
        TimePoint lunchEnd = new TimePoint(firstLunchAlarm.getLunchEnd());
        TimePoint sleepStart = new TimePoint(firstSleepAlarm.getSleepStart());
        TimePoint sleepEnd = new TimePoint(firstSleepAlarm.getSleepEnd());

        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINESE);
        String currentTimeStr = format.format(new Date());
        TimePoint current = new TimePoint(currentTimeStr);

        if (current.compareTo(lunchStart) >= 0 && current.compareTo(lunchEnd) <= 0) return "day";
        if (current.compareTo(sleepStart) >= 0 && current.compareTo(sleepEnd) <= 0) return "night";

        return null;
    }

    /**
     * 设定一个震动提示
     * @param interval 提示闹钟的时长
     */
    public void setShockTip(int interval) {
        updateHAM(interval);
        TimePoint sleepShockTipTP = new TimePoint(getHour() + ":" + getMinutes());
        boolean isOver = sleepShockTipTP.compareTo(new TimePoint(firstSleepAlarm.getSleepEnd())) >= 0;
        if (isSetShockTip()) {
            if (getStatus().equals("night") && isOver) {
                Toast.makeText(context, "太晚了，放心躺着吧，就不震动提示了", Toast.LENGTH_SHORT).show();
            } else {
                set(getHour() + ":" + getMinutes(), "睡不着就起来干活吧");
            }
        }
    }

    /**
     * 判断更新后的时间是否在仅震动时间之前（只针对晚睡）
     * @return true在之前
     */
    public boolean isBefore() {
        TimePoint sleepAlarmTP = new TimePoint(getHour() + ":" + getMinutes());
        String beforeTimeStr = firstSleepAlarm.getBeforeTimeStr();
        return sleepAlarmTP.compareTo(new TimePoint(beforeTimeStr)) < 0;
    }

    /**
     * 应用开启直接设置或点击fab设置（最终版本）
     */
    public void setFinally() {
        if (getStatus() != null) {
            boolean isNight = getStatus().equals("night");
            //从数据库取值，设置闹钟
            MyAlarm myAlarm = new MyAlarm(isNight);
            myAlarm.getDataFromDB();

            setRestTime(myAlarm.getRestTime());
            setContent(myAlarm.getAlarmContent());
            setSetShockTip(myAlarm.isShockTipSet());
            setSetTask(myAlarm.isTaskSet());
            setRing(myAlarm.isRing());
            setRingtoneUriStr(myAlarm.getRingtoneUriStr());
            setInterval(myAlarm.getShockInterval());

            //震动闹钟
            setShockTip(getInterval());
            //更新hour和minutes
            updateHAM(getRestTime());
            //只有在开启仅震动……功能，并设定了之前的晚睡响铃闹钟才会将isRing设为false
            if (firstSleepAlarm.isJustShockOn() && isNight &&
                    isRing() && isBefore()) setRing(false);
            //目标闹钟
            setAlarm(getHour(), getMinutes());
            sp.edit().putBoolean("isTargetAlarmSet", true).apply();
            Log.i("TestTag", getHour() + ":" + getMinutes());
            sp.edit().putString("targetAlarmTime", getHour() + ":" + getMinutes()).apply();
        } else {
            Toast.makeText(context, "现在不在您设置的一般休息时段内，故不设置闹钟", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 其他应用可调用此类的这个方法来设置一般性的闹钟提示
     * 默认只震动不响铃，默认不打开闹钟列表界面（不管闲娱限止）
     * @param timeStr 时间字符串
     * @param content 目的或提示
     */
    public void set(String timeStr, String content) {
        String[] timeArr = timeStr.split(":");
        int hour = Integer.parseInt(timeArr[0]);
        int minutes = Integer.parseInt(timeArr[1]);

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, content);
        //设置闹钟响铃时震动
        intent.putExtra(AlarmClock.EXTRA_VIBRATE, true);
        //设置闹钟时不显示系统闹钟界面
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        //无铃声
        intent.putExtra(AlarmClock.EXTRA_RINGTONE, AlarmClock.VALUE_RINGTONE_SILENT);

        context.startActivity(intent);
    }

    /**
     * 设置一个响铃的闲娱限止闹钟，默认60分钟
     */
    public void setLimitAlarm() {
        updateHAM(60);
        sp.edit().putString("limitAlarmTime", getHour() + ":" + getMinutes()).apply();
        setRing(true);
        setAlarm(getHour(), getMinutes(), "闲娱限止！");
    }

    /**
     * 设置系统闹钟，直接和系统闹钟打交道（适合本类）
     * 可控制震动、响铃这个次要变量
     * @param hour 小时
     * @param minutes 分钟
     * @param content 显示文本
     */
    public void setAlarm(int hour, int minutes, String content) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, content);
        //设置闹钟响铃时震动
        intent.putExtra(AlarmClock.EXTRA_VIBRATE, true);

        boolean isMoreAndCloseTo = new MyUtils(context).isMoreAndCloseTo(
                new TimePoint(sp.getString("limitAlarmTime", "0:0")));
        //设置了闲娱限止且不临近，才会跳转显示
        if (sp.getBoolean("isLimitAlarmSet", false) && !isMoreAndCloseTo) {
            Toast.makeText(context, "请手动关闭闲娱限止闹钟", Toast.LENGTH_LONG).show();
            //非一般性场景，但为了尽可能地引导准确，顺便设置一下
            sp.edit().putBoolean("isTargetAlarmSet", false).apply();
        } else {
            //设置闹钟时不显示系统闹钟界面
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        }
        //不管显不显示，都设为false
        sp.edit().putBoolean("isLimitAlarmSet", false).apply();
        if (!isRing) {
            intent.putExtra(AlarmClock.EXTRA_RINGTONE, AlarmClock.VALUE_RINGTONE_SILENT);
        } else {
            //设置铃声
            intent.putExtra(AlarmClock.EXTRA_RINGTONE, getRingtoneUriStr());
        }

        context.startActivity(intent);
    }

    /**
     * 简化版重载，主要针对午休和晚睡
     * @param hour 小时
     * @param minutes 分钟
     */
    public void setAlarm(int hour, int minutes) {
        setAlarm(hour, minutes, getContent());
    }

    /**
     * 根据content取消系统闹钟（没什么屌用！）
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void dismissAlarm() {
        Intent intent = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
        intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_ALL);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        context.startActivity(intent);
    }

    /**
     * 打开闹钟列表
     */
    public void showAlarm() {
        Intent intent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
        context.startActivity(intent);
    }

    /**
     * 根据类型和更新hour和minutes的值
     * @param timeLength 时间长度，包括午休和晚睡的restTime和震动提示的interval
     */
    public void updateHAM(float timeLength) {
        int hour;
        int minutes;
        int restMinutes;

        //获取系统当前时间
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        int currentMinutes = cal.get(Calendar.MINUTE);

        int theIntegerPart = (int) Math.floor(timeLength);
        float theDecimalPart = timeLength - theIntegerPart;
        //根据类型更新值，一个是hour，一个是restMinutes
        if (timeLength < 12) {
            int addHour = currentHour + theIntegerPart;
            if (addHour >= 24) {
                hour = addHour - 24;
            } else hour = addHour;
            restMinutes = (int) (60 * theDecimalPart);
        } else {
            hour = currentHour;
            restMinutes = theIntegerPart;
        }

        minutes = currentMinutes + restMinutes;
        while (minutes >= 60) {
            minutes -= 60;
            //currentHour不可能为24，只能是0，所以不用考虑会大于24的情况
            hour += 1;
        }
        setHour(hour);
        setMinutes(minutes);
    }


//    /**
//     * 初次使用，申请权限并设定系统闹钟
//     * 只执行一次
//     */
//    public void requestAndSet() {
//        //申请权限
//        PermissionX.init((FragmentActivity) context)// TODO: 2022/10/22 不知道这里有没有问题
//                .permissions(Manifest.permission.SET_ALARM)
//                .request((allGranted, grantedList, deniedList) -> {
//                    if (allGranted) {
//                        //设置系统闹钟
//                        setAlarm(getHour(), getMinutes());
//                    } else {
//                        Toast.makeText(context, "您已拒绝此权限", Toast.LENGTH_SHORT).show();
//                    }
//                });
//        isRequested = true;
//    }

    //——————————————————————————————————————————————————————————————————————————————————————


    public String getRingtoneUriStr() {
        return ringtoneUriStr;
    }

    public void setRingtoneUriStr(String ringtoneUriStr) {
        this.ringtoneUriStr = ringtoneUriStr;
    }

    public boolean isSetTask() {
        return isSetTask;
    }

    public void setSetTask(boolean setTask) {
        isSetTask = setTask;
    }

    public boolean isRing() {
        return isRing;
    }

    public void setRing(boolean ring) {
        isRing = ring;
    }

    public float getRestTime() {
        return restTime;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setRestTime(float restTime) {
        this.restTime = restTime;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public boolean isSetShockTip() {
        return isSetShockTip;
    }

    public void setSetShockTip(boolean setShockTip) {
        isSetShockTip = setShockTip;
    }
}

