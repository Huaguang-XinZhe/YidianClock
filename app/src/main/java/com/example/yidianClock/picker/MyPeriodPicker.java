package com.example.yidianClock.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.yidianClock.R;
import com.example.yidianClock.utils.TimePoint;
import com.example.yidianClock.model.LunchAlarm;
import com.example.yidianClock.model.SleepAlarm;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.litepal.LitePal;

public class MyPeriodPicker {
    private final Context context;
    private final boolean isNight;
    private BottomSheetDialog bottomSheetDialog;
    private int startHour = 10;
    private int startMinute = 30;
    private int endHour = 16;
    private int endMinute = 30;
    private boolean isStartSet = false;
    private OnConfirm confirm;

    public MyPeriodPicker(Context context, boolean isNight) {
        this.context = context;
        this.isNight = isNight;
    }

    //声明属于该类的自定义接口
    public interface OnConfirm {
        void doIt();
    }

    /**
     * 这是供外部调用的方法，传入接口对象
     * @param confirm OnConfirm接口对象
     */
    public void setOnConfirm(OnConfirm confirm) {
        this.confirm = confirm;
    }

    /**
     * 从底部弹出时段选择弹框（设置并弹出）
     */
    public void setAndShow(){
        bottomSheetDialog = new BottomSheetDialog(context);
        @SuppressLint("InflateParams")
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_time_picker_period,null);
        bottomSheetDialog.setContentView(dialogView);

        TimePicker startTimePicker = dialogView.findViewById(R.id.timepicker_start);
        TimePicker endTimePicker = dialogView.findViewById(R.id.timepicker_end);
        TextView tvCancel = dialogView.findViewById(R.id.tv_cancel);
        TextView tvConfirm = dialogView.findViewById(R.id.tv_confirm);

        startTimePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);  //设置点击事件不弹键盘
        startTimePicker.setIs24HourView(true);   //设置时间显示为24小时
        endTimePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);  //设置点击事件不弹键盘
        endTimePicker.setIs24HourView(true);   //设置时间显示为24小时

        //在TimePicker启动的时候预设时间
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //根据类型更新预设时间
            updateDefaultTime();
            //设置到picker
            startTimePicker.setHour(startHour);
            startTimePicker.setMinute(startMinute); //设置当前分（0-59）
            endTimePicker.setHour(endHour);
            endTimePicker.setMinute(endMinute); //设置当前分（0-59）
            //使分钟部分30分钟，30分钟的显示
            set30Display(startTimePicker, true);
            set30Display(endTimePicker, false);
        }

        bottomSheetDialog.show();

        //获取起始点的时间
        startTimePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            startHour = hourOfDay;
            startMinute = minute;
            isStartSet = true;

            Log.i("getSongsList", "startHour = " + startHour + "，startMinute = " + startMinute);

            //根据用户设置的起始点，重新预设结束点
            int hour = getStartHour() + 3;
            if (hour >= 24) {
                hour -= 24;
            }
            setEndHour(hour);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                endTimePicker.setHour(getEndHour());
            }
        });
        //获取结束点的时间
        endTimePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            endHour = hourOfDay;
            endMinute = minute;

            Log.i("getSongsList", "endHour = " + endHour + "，endMinute = " + endMinute);

            //如果用户没设置起始点，一开始就来设置结束点的话，那就倒推预设起始点
            int hour = getEndHour() - 3;
            if (hour < 0) {
                hour += 24;
            }
            if (!isStartSet) {
                setStartHour(hour);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startTimePicker.setHour(getStartHour());
                }
            }
        });

        tvCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        tvConfirm.setOnClickListener(v -> {
            TimePoint startTP = new TimePoint(startHour + ":" + startMinute);
            TimePoint endTP = new TimePoint(endHour + ":" + endMinute);

            if (endTP.compareTo(startTP) <= 0) {
                Toast.makeText(context, "结束时间小于开始时间，请重新选择", Toast.LENGTH_SHORT).show();
            }

            bottomSheetDialog.dismiss();

            //供外部实现
            if (confirm != null) {
                confirm.doIt();
            }
        });

    }

    /**
     * 根据一般时段的类型更新预设时间，直接从数据库取值
     */
    private void updateDefaultTime() {
        LunchAlarm firstLunchAlarm = LitePal.findFirst(LunchAlarm.class);
        SleepAlarm firstSleepAlarm = LitePal.findFirst(SleepAlarm.class);
        String[] startTimeArr;
        String[] endTimeArr;

        if (isNight) {
            startTimeArr = firstSleepAlarm.getSleepStart().split(":");
            endTimeArr = firstSleepAlarm.getSleepEnd().split(":");
        } else {
            //尽管有默认值也得明确设置，否则就会数据错乱。因为值在设置isNight块时就已经改变了
            startTimeArr = firstLunchAlarm.getLunchStart().split(":");
            endTimeArr = firstLunchAlarm.getLunchEnd().split(":");
        }

        startHour = Integer.parseInt(startTimeArr[0]);
        startMinute = Integer.parseInt(startTimeArr[1]);
        endHour = Integer.parseInt(endTimeArr[0]);
        endMinute = Integer.parseInt(endTimeArr[1]);
    }

    /**
     * 获取用户设置好的时段字符串
     * @return 时段字符串，如：21:30 ~ 2:30
     */
    public String getPOT() {
        String start;
        String end;

        if (getStartMinute() == 0) {
            start = getStartHour() + ":00";
        } else {
            start = getStartHour() + ":30";
        }
        if (getEndMinute() == 0) {
            end = getEndHour() + ":00";
        } else {
            end = getEndHour() + ":30";
        }

        return start + " ~ " + end;
    }

    /**
     * 将分钟选择器部分按30分钟间隔显示
     * @param picker TimePicker对象
     * @param isStartTime 专门为解决分钟转动不触发时间改变问题而设立，用于开始和结束的判断
     */
    private void set30Display(TimePicker picker, boolean isStartTime) {
        String[] displayArr = {"00", "30"};
        NumberPicker minutePicker;
        View minute = picker.findViewById(Resources.getSystem().
                getIdentifier("minute", "id", "android"));
        if (minute instanceof NumberPicker) {
            minutePicker = (NumberPicker) minute;
            //下面这两行是必须的，没有这两行程序会崩溃
            //数组的长度必须与这个值（getMaxValve() - getMinValve() + 1）相等
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(1);
            minutePicker.setDisplayedValues(displayArr);
            //使分钟部分转动，小时部分不转（如果直接传入null，将会导致分钟转动不触发时间改变）
            minutePicker.setOnValueChangedListener((picker1, oldVal, newVal) -> {
                Log.i("getSongsList", "newVal = " + newVal);
                //解决分钟转动不触发时间改变监听的问题
                if (isStartTime) {
                    startMinute = newVal;
                } else {
                    endMinute = newVal;
                }
            });

        }
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }
}
