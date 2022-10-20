package com.example.yidianClock;

public class TimePoint implements Comparable<TimePoint> {
    String timeStr;
//    boolean isNextDay;

//    public TimePoint(String timeStr, boolean isNextDay) {
//        this.timeStr = timeStr;
//        this.isNextDay = isNextDay;
//    }

    public TimePoint(String timeStr) {
//        this(timeStr, false);
        this.timeStr = timeStr;
    }



    @Override
    public int compareTo(TimePoint o) {
        String[] thisTimeArr = timeStr.split(":");
        int thisHour = Integer.parseInt(thisTimeArr[0]);
        int thisMinutes = Integer.parseInt(thisTimeArr[1]);
        String[] otherTimeArr = o.timeStr.split(":");
        int otherHour = Integer.parseInt(otherTimeArr[0]);
        int otherMinutes = Integer.parseInt(otherTimeArr[1]);

        //根据是否是第二天的时间来设置比较hour
//        if (!isNextDay) {
//            hour = thisHour;
//        } else {
//            hour = thisHour + 24;
//        }
        //若为次日凌晨，则更新this和other的hour值
        int hour1 = updateHour(thisHour);
        int hour2 = updateHour(otherHour);

        //开始比较
        if (hour1 > hour2) {
            return 1;
        } else if (hour1 == hour2) {
            return thisMinutes - otherMinutes;
        } else return -1;
    }

    /**
     * 判断是否是次日凌晨
     * @param hour 时点字符串的小时部分
     * @return true为次日凌晨
     */
    public boolean isNextDay(int hour) {
        // TODO: 2022/10/20 这个逻辑一般是没有什么问题，但仍然不够健壮
        return hour >= 0 && hour <= 5;
    }

    public int updateHour(int originalHour) {
        if (isNextDay(originalHour)) {
            return originalHour + 24;
        } else return originalHour;
    }
}
