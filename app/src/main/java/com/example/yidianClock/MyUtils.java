package com.example.yidianClock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MyUtils {
    private final Context context;

    public MyUtils(Context context) {
        this.context = context;
    }

    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINESE);
        return format.format(new Date());
    }

    /**
     * 使用闹钟渠道来控制铃声
     * @param ringtone Ringtone对象
     */
    public void setAlarmControl(Activity activity, Ringtone ringtone) {
        AudioAttributes aa = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();
        ringtone.setAudioAttributes(aa);
        //这是activity的方法
        activity.setVolumeControlStream(AudioManager.STREAM_ALARM);

    }

    /**
     * 判断当前时间是否大于或等于目标时间，并且与目标时间临近（10分钟以内）
     * @param targetTP 目标时间，TimePoint类型
     * @return true 为临近
     */
    public boolean isMoreAndCloseTo(TimePoint targetTP) {
        //计算当前时间比目标闹钟的时间多几分钟
        int moreMinutes = new TimePoint(getCurrentTime()).moreMinutes(targetTP);
        return moreMinutes >= 0 && moreMinutes < 10;
    }

    /**
     * 隐藏软键盘
     * @param v View对象
     */
    public void hideSoftInput(View v) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

//    /**
//     * 取出小数后边多余的零，但有不影响非零小数
//     * @param f float类型的小数
//     * @return 返回去除小数部分的0后得到的字符串
//     * @deprecated 这个方法还是有问题，7.6会变成7.5，7.2会编程7.1，7.8又不变？
//     */
//    public static String getRoundDotStr(float f) {
//        return new BigDecimal(f).stripTrailingZeros().toPlainString();
//    }

    /**
     * 取出小数后边多余的零和小数点，但有不影响非零小数
     * @param f float类型的小数
     * @return 返回去除小数部分的0后得到的字符串
     */
    public static String getRoundDotStr(float f) {
        Number value;
        if (f > Math.floor(f)) {
            value = f;
        } else {
            value = (int) Math.floor(f);
        }
        return String.valueOf(value);
    }

    /**
     * 通过在媒体库中的id得到其真正的Uri（baseUri已知）
     * @param id 在媒体库中的id
     * @return 真正的Uri
     */
    public static Uri getRealUri(int id) {
        Uri baseUri = Uri.parse("content://media/external_primary/audio/media");
        return Uri.withAppendedPath(baseUri, "" + id);
    }

    /**
     * 给定一个File文件，先转换为绝对路径，靠着这个路径去获取本机媒体数据库中本图像对应的id，进而在基uri的基础上加上id，得到目标Uri
     * 如果该图像文件真实存在，但本机的媒体库中又没有，那就将这个文件插入保存其中，insert成功的话，会返回一个Uri
     * 反之，如果该文件根本不存在，那就返回null
     * @param imageFile 图像的File对象
     * @return 可能为null
     */
    public Uri getImageContentUri(Context context, java.io.File imageFile) {
        String filePath = imageFile.getAbsolutePath();

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }

        }
    }


//    private TimePoint[] lunchPOT = {new TimePoint("11:00"), new TimePoint("16:00")};
//    private TimePoint[] sleepPOT = {new TimePoint("21:30"), new TimePoint("02:30")};
//    /**
//     * 将时间字符串转换为毫秒，使之可比较
//     * @param timeStr 时间字符串
//     * @return long类型的毫秒数
//     * @deprecated 此方法得到的结果不正确，不过思路应该能行得通
//     */
//    public static long str2Millis(String timeStr) {
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINESE);
//        long timeMillis = 0;
//        try {
//            //使用getTime()方法可能产生空指针异常
//            timeMillis = Objects.requireNonNull(format.parse(timeStr)).getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return timeMillis;
//    }

}
