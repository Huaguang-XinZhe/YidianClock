package com.example.yidianClock.picker;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.widget.TimePicker;

import com.example.yidianClock.databinding.DialogTimePickerBinding;
import com.example.yidianClock.model.SleepAlarm;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.litepal.LitePal;

public class MyPicker {
    Context context;
    BottomSheetDialog bottomSheetDialog;
    int hour;
    int minutes;
    OnConfirm confirm;
    OnCancel cancel;


    public MyPicker(Context context) {
        this.context = context;
    }

    //声明属于该类的自定义接口
    public interface OnConfirm {
        void doIt();
    }

    /**
     * 这是供外部调用的方法，传入接口对象
     * @param confirm OnConfirm接口对象
     */
    public void setOnConfirm(MyPicker.OnConfirm confirm) {
        this.confirm = confirm;
    }

    //声明属于该类的自定义接口
    public interface OnCancel {
        void doIt();
    }

    public void setOnCancel(MyPicker.OnCancel onCancel) {
        this.cancel = onCancel;
    }


    /**
     * 从底部弹出时段选择弹框（设置并弹出）
     */
    public void setAndShow(boolean isNoRingBefore){
        bottomSheetDialog = new BottomSheetDialog(context);
        DialogTimePickerBinding timePickerBinding = DialogTimePickerBinding.inflate(LayoutInflater.from(context));
        bottomSheetDialog.setContentView(timePickerBinding.getRoot());

        timePickerBinding.timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);  //设置点击事件不弹键盘
        timePickerBinding.timePicker.setIs24HourView(true);   //设置时间显示为24小时

        if (!isNoRingBefore) {
            //不到几点不起床弹窗，改变标题
            timePickerBinding.tvTitle.setText("不到几点不起床");
        }

        //在TimePicker启动的时候预设时间
        SleepAlarm sleepAlarm = LitePal.findFirst(SleepAlarm.class);
        String[] beforeTimeStrArr;
        if (isNoRingBefore) {
            beforeTimeStrArr = sleepAlarm.getBeforeTimeStr_noRingBefore().split(":");
        } else {
            beforeTimeStrArr = sleepAlarm.getBeforeTimeStr_donGetUp().split(":");
        }
        hour = Integer.parseInt(beforeTimeStrArr[0]);
        minutes = Integer.parseInt(beforeTimeStrArr[1]);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePickerBinding.timePicker.setHour(hour);
            timePickerBinding.timePicker.setMinute(minutes);
        }

        bottomSheetDialog.show();

        //获取起始点的时间
        timePickerBinding.timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            hour = hourOfDay;
            minutes = minute;
        });


        timePickerBinding.cancelTV.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            //供外部实现
            if (cancel != null) {
                cancel.doIt();
            }
        });

        timePickerBinding.confirmTV.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            //供外部实现
            if (confirm != null) {
                confirm.doIt();
            }
        });

    }

    /**
     * 获取用户选中的时间
     * @return 时间字符串
     */
    public String getTime() {
        String newMinutes;

        if (minutes < 10) {
            newMinutes = "0" + minutes;
        } else {
            newMinutes = "" + minutes;
        }

        return hour + ":" + newMinutes;
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
}
