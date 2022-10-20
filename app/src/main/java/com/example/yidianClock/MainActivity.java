package com.example.yidianClock;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yidianClock.databinding.ActivityMainBinding;
import com.permissionx.guolindev.PermissionX;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private int hour;
    private int minutes;
    private int theLunchTime = 50;
    private float theSleepTime = 7;
    private String alarmContent = "死猪，该起床了";
    private int shockInterval = 30;
    private boolean isShock = true;
    private TimePoint[] lunchPOT = {new TimePoint("11:00"), new TimePoint("16:00")};
    private TimePoint[] sleepPOT = {new TimePoint("21:30"), new TimePoint("02:30")};
    private boolean isRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        setAlarm_Eventually();

        //午休时长设置/改变后更新
        mainBinding.theLunchTimeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    setTheLunchTime(Integer.parseInt(s.toString()));
                }
            }
        });

        //晚睡时长设置/改变后更新
        mainBinding.theSleepTimeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    setTheSleepTime(Float.parseFloat(s.toString()));
                }
            }
        });

        //闹钟响铃时显示文本的设置/更新
        mainBinding.alarmContentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                setAlarmContent(s.toString());
            }
        });

        //震动间隔的设置/更新
        mainBinding.shockIntervalEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    setShockInterval(Integer.parseInt(s.toString()));
                }
            }
        });

        //ToggleButton的监听
        mainBinding.isShockButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                isShock = false;
                mainBinding.shockInterValSetLayout.setVisibility(View.GONE);
            } else {
                //必须要有，要不然变量还维持之前设定的值
                isShock = true;
                mainBinding.shockInterValSetLayout.setVisibility(View.VISIBLE);
            }
        });

        //FloatingActionButton的监听
        mainBinding.fab.setOnClickListener(v -> setAlarm_Eventually());
        
    }

    /**
     * 应用开启直接设置或点击fab设置（最终版本）
     */
    public void setAlarm_Eventually() {
        if (getStatus() != null) {
            //不管是白天还是黑夜，都要设置两个闹钟
            //震动闹钟
            if (isShock) {
                updateHAM(getShockInterval(), false);
                setAlarm(getHour(), getMinutes(), true);
            }
            //更新hour和minutes
            if (getStatus().equals("day")) {
                updateHAM(getTheLunchTime(), false);
            } else {
                updateHAM(getTheSleepTime(), true);
            }
            //目标闹钟
            if (!isRequest) {
                requestAndSet();
            } else {
                setAlarm(getHour(), getMinutes());
            }
        } else {
            Toast.makeText(this, "现在还没到您设置的休息时段呢", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据类型和更新hour和minutes的值
     * @param value 可能是int和float类型，代表设定的分钟或小时
     * @param isNight 是否是晚上（白天和震动的情况类似，只是值不同）
     */
    public void updateHAM(Number value, boolean isNight) {
        int hour;
        int minutes;
        int restMinutes;

        //获取系统当前时间
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        int currentMinutes = cal.get(Calendar.MINUTE);

        //根据类型更新值，一个是hour，一个是restMinutes
        if (isNight) {
            float valueF = (float) value;
            int theIntegerPart = (int) Math.floor(valueF);
            float theDecimalPart = valueF - theIntegerPart;
            hour = currentHour + theIntegerPart;
            restMinutes = (int) (60 * theDecimalPart);
        } else {
            hour = currentHour;
            restMinutes = (int) value;
        }

        minutes = currentMinutes + restMinutes;
        while (minutes > 60) {
            minutes -= 60;
            //currentHour不可能为24，只能是0，所以不用考虑会大于24的情况
            hour += 1;
        }
        setHour(hour);
        setMinutes(minutes);
    }

//    public static void main(String[] args) {
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINESE);
//        String currentTimeStr = format.format(new Date());
//        TimePoint lunchStart = new TimePoint("11:00");
//        TimePoint lunchEnd = new TimePoint("16:00");
//        TimePoint sleepStart = new TimePoint("21:30");
//        TimePoint sleepEnd = new TimePoint("2:30");
//        TimePoint current = new TimePoint(currentTimeStr);
//
//        //验证比较正确性
//        boolean isTrue;
//        isTrue = lunchStart.compareTo(lunchEnd) < 0 && sleepStart.compareTo(sleepEnd) < 0;
//        System.out.println(isTrue);
//
//        if (current.compareTo(lunchStart) >= 0 && current.compareTo(lunchEnd) <= 0) {
//            System.out.println("当前时间设置在一般午休时段内");
//        } else if (current.compareTo(sleepStart) >= 0 && current.compareTo(sleepEnd) <= 0) {
//            System.out.println("当前时间设置在晚睡一般时段内");
//        } else System.out.println("还没到您设置的休息时段呢");
//
//    }

    /**
     * 判断现在是白天还是晚上（根据用户设置的一般时段而定，非客观）
     * @return 白天返回day，晚上返回night，其他返回null
     */
    public String getStatus() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINESE);
        String currentTimeStr = format.format(new Date());
        TimePoint lunchStart = getLunchPOT()[0];
        TimePoint lunchEnd = getLunchPOT()[1];
        TimePoint sleepStart = getSleepPOT()[0];
        TimePoint sleepEnd = getSleepPOT()[1];
        TimePoint current = new TimePoint(currentTimeStr);

        if (current.compareTo(lunchStart) >= 0 && current.compareTo(lunchEnd) <= 0) return "day";
        if (current.compareTo(sleepStart) >= 0 && current.compareTo(sleepEnd) <= 0) return "night";
        return null;
    }



    /**
     * 初次使用，申请权限并设定系统闹钟
     * 只执行一次
     */
    public void requestAndSet() {
        //申请权限
        PermissionX.init(this)
                .permissions(Manifest.permission.SET_ALARM)
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        //设置系统闹钟
                        setAlarm(getHour(), getMinutes());
                    } else {
                        Toast.makeText(this, "您已拒绝此权限", Toast.LENGTH_SHORT).show();
                    }
                });
        isRequest = true;
    }

    public void setAlarm(int hour, int minutes, String message, boolean isSilent) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, message);
        //设置闹钟响铃时震动
        intent.putExtra(AlarmClock.EXTRA_VIBRATE, true);
        //设置闹钟时不显示系统闹钟界面
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        if (isSilent) {
            intent.putExtra(AlarmClock.EXTRA_RINGTONE, AlarmClock.VALUE_RINGTONE_SILENT);
        }
        startActivity(intent);
    }

    public void setAlarm(int hour, int minutes, boolean isSilent) {
        setAlarm(hour, minutes, getAlarmContent(), isSilent);
    }

    public void setAlarm(int hour, int minutes) {
        setAlarm(hour, minutes, getAlarmContent(), false);
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

    public int getTheLunchTime() {
        return theLunchTime;
    }

    public void setTheLunchTime(int theLunchTime) {
        if (theLunchTime >= 120) {
            Toast.makeText(this, "您的午休时间过长，为避免影响正常作息\n请重新输入", Toast.LENGTH_SHORT).show();

        } else if (theLunchTime >= 20) {
            this.theLunchTime = theLunchTime;
        } else {
            Toast.makeText(this, "您的午休时间过短，尚不能恢复精力\n请重新输入", Toast.LENGTH_SHORT).show();
        }
    }

    public float getTheSleepTime() {
        return theSleepTime;
    }

    //theSleepHour可以为小数
    public void setTheSleepTime(float theSleepTime) {
        if (theSleepTime >= 10) {
            Toast.makeText(this, "您晚睡的时间过长，过犹不及\n请重新输入", Toast.LENGTH_SHORT).show();
        } else if (theSleepTime >= 3.5) {
            this.theSleepTime = theSleepTime;
        } else {
            Toast.makeText(this, "您晚睡的时间过短，不及恢复\n请重新输入", Toast.LENGTH_SHORT).show();
        }
    }

    public String getAlarmContent() {
        return alarmContent;
    }

    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }

    public int getShockInterval() {
        return shockInterval;
    }

    public void setShockInterval(int shockInterval) {
        this.shockInterval = shockInterval;
    }

    public TimePoint[] getLunchPOT() {
        return lunchPOT;
    }

    public void setLunchPOT(TimePoint[] lunchPOT) {
        this.lunchPOT = lunchPOT;
    }

    public TimePoint[] getSleepPOT() {
        return sleepPOT;
    }

    public void setSleepPOT(TimePoint[] sleepPOT) {
        this.sleepPOT = sleepPOT;
    }
}