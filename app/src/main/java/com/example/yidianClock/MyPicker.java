package com.example.yidianClock;

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

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MyPicker {
    private final Context context;
    private BottomSheetDialog bottomSheetDialog;
    private int startHour = 10;
    private int startMinute = 30;
    private int endHour = 16;
    private int endMinute = 30;
    private boolean isStartSet = false;

    private MyPicker(Context context) {
        this.context = context;
    }
    @SuppressLint("StaticFieldLeak")
    private static MyPicker myPicker;
    //实现单例
    public static MyPicker getInstance(Context context) {
        if (myPicker == null) {
            myPicker = new MyPicker(context);
        }
        return myPicker;
    }

    /**
     * 从底部弹出时段选择弹框（设置并弹出）
     */
    public void setAndShow(){
        bottomSheetDialog = new BottomSheetDialog(context);
        @SuppressLint("InflateParams")
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_time_picker,null);
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
            startTimePicker.setHour(startHour);
            startTimePicker.setMinute(startMinute); //设置当前分（0-59）
            endTimePicker.setHour(endHour);
            endTimePicker.setMinute(endMinute); //设置当前分（0-59）
            //使分钟部分30分钟，30分钟的显示
            set30Display(startTimePicker);
            set30Display(endTimePicker);
        }

        bottomSheetDialog.show();

        //获取起始点的时间
        startTimePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            startHour = hourOfDay;
            startMinute = minute;
            isStartSet = true;

            //根据用户设置的起始点，重新预设结束点
            setEndHour(getStartHour() + 5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                endTimePicker.setHour(getEndHour());
            }
        });
        //获取结束点的时间
        endTimePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            endHour = hourOfDay;
            endMinute = minute;

            //如果用户没设置起始点，一开始就来设置结束点的话，那就倒推预设起始点
            if (!isStartSet) {
                setStartHour(getEndHour() - 5);
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

            Log.e("开始时间：",startHour +":"+startMinute );
            Log.e("结束时间：",endHour +":"+endMinute);

            bottomSheetDialog.dismiss();
        });

    }

    /**
     * 将分钟选择器部分按30分钟间隔显示
     * @param picker TimePicker对象
     */
    private void set30Display(TimePicker picker) {
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
