package com.example.yidianClock.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Process;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.yidianClock.utils.timeUtils.MyDate;
import com.example.yidianClock.utils.timeUtils.TimePoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyUtils {
    @SuppressLint("StaticFieldLeak")
    private static MyUtils myUtils;
    Context context;
    InputMethodManager manager;

    private MyUtils(Context context) {
        this.context = context;
        manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static MyUtils getInstance(Context context) {
        if (myUtils == null) {
            myUtils = new MyUtils(context);
        }
        return myUtils;
    }

    /**
     * 获取当前日期和目标日差距的天数
     * @param goalDay 目标日（一定还没到，不在今年就在明年）
     */
    public static int getDaysDiff(String goalDay) {
        int diff;
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        //计算现在日期在当年的第几天
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
        //计算目标日当天在当年的第几天
        int day = getDayOfYear(goalDay);
        //目标日的年
        int year= getDateArr(goalDay)[0];
        //这是今年还剩多少天（即今天之后还有多少天）
        int remainDays = calHowDaysInThisYear(currentYear) - currentDay;

        if (year == currentYear) {
            //目标日在今年
            diff = day - currentDay;
        } else {
            //目标日在明年
            diff = remainDays + day;
        }

        return diff;
    }

    /**
     * 获取目标日是在当年的第几天
     * 或目标日在当年已经过去了多少天
     * @param goalDay 目标日
     */
    public static int getDayOfYear(String goalDay) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        try {
            //解析可能出错，必须放在try-catch块中处理；解析出来的值可能为null，必须判空
            Date goalDate = sdFormat.parse(goalDay);
            if (goalDate != null) {
                //必须用Date对象来更新Calendar对象，否则获取的DAY_OF_YEAR会出异常
                calendar.setTime(goalDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 计算指定年有多少天（其实就是判断指定年是平年还是闰年，平年365天，闰年366天）
     * @param year 指定年
     */
    public static int calHowDaysInThisYear(int year) {
        //判断是否是闰年
        boolean isLeapYear = (year % 4 == 0 && !(year % 100 == 0)) || (year % 400 == 0)
                || (year % 3200 == 0 && year % 172800 == 0);
        if (isLeapYear) return 366;
        else return 365;
    }

//    public static void main(String[] args) {
//        System.out.println(getDaysDiff("2022-12-05"));
//    }

    /**
     * 判断指定日期是否过去（是否在当前日期的后边）
     * 注意，当天不算过了
     * @param date 指定日期
     * @return true：过了，false：没过
     */
    public static boolean isOver(String date) {
        MyDate currentDate = new MyDate(MyUtils.getCurrentDate());
        MyDate goalDate = new MyDate(date);
        return currentDate.compareTo(goalDate) > 0;
    }

    /**
     * 获取当前日期（今天）的标准化表示，如：2001-11-09
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        return sdFormat.format(new Date());
    }

    /**
     * 获取标准化日期的int数组
     * @param date 标准化日期，如：2001-11-09
     * @return int数组，0：年份，1：月份，2：日
     */
    public static int[] getDateArr(String date) {
        int[] arr = new int[3];
        String[] dateArr = date.split("-");
        arr[0]  = Integer.parseInt(dateArr[0]);
        arr[1] = Integer.parseInt(dateArr[1]);
        arr[2] = Integer.parseInt(dateArr[2]);
        return arr;
    }

    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINESE);
        return format.format(new Date());
    }

    /**
     * 自杀当前进程（即将自己的应用杀死，为了省电）
     */
    public static void suicide() {
        //注意，这里必须引用android.os包，不能引用java.lang包
        int pid = Process.myPid();
        Log.i("getSongsList", "当前进程的pid = " + pid);
        Process.killProcess(pid);
    }

    /**
     * 唤醒屏幕（interval后释放）
     */
    public void wakeUp(int interval){
//        //屏锁管理器
//        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
//        //解锁
//        kl.disableKeyguard();
        //获取电源管理器对象
        Log.i("getSongsList", "wakeUp唤醒执行！");
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        @SuppressLint("InvalidWakeLockTag")
//        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ON_AFTER_RELEASE,"bright");
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE,
                "TestWakeLock"
        );
        //点亮屏幕
        //1小时内系统将清除唤醒锁
//        if (!wakeLock.isHeld()) {
            wakeLock.acquire(60*60*1000L /*60 minutes*/);
//        }
        //释放（interval延后10秒）
        new Handler().postDelayed(wakeLock::release, interval * 60*1000L + 10000);
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
//        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInput(View v) {
        if (manager != null) {
            Log.i("getSongsList", "弹出软键盘执行");// TODO: 2022/11/19 没效果
            //无效方法，不弹软键盘
//            manager.showSoftInputFromInputMethod(v.getWindowToken(), InputMethodManager.SHOW_FORCED);
            //这里第二个参数最好传0，不要传forced，下不来！（底部的布局会粘在软键盘上方）
            manager.showSoftInput(v, 0);
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
