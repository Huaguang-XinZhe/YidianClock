package com.example.yidianClock.utils.timeUtils;

public class TimePoint implements Comparable<TimePoint> {
    private int hour;
    private int minutes;

    public TimePoint(String timeStr) {
        String[] timeArr = timeStr.split(":");
        setHour(Integer.parseInt(timeArr[0]));
        setMinutes(Integer.parseInt(timeArr[1]));
    }

    @Override
    public int compareTo(TimePoint o) {
        //若为次日凌晨，则更新this和other的hour值
        int thisHour = updateHour(getHour());
        int otherHour = updateHour(o.getHour());
        //开始比较
        if (thisHour > otherHour) {
            return 1;
        } else if (thisHour == otherHour) {
            return getMinutes() - o.getMinutes();
        } else return -1;
    }

    /**
     * 比较两个TimePoint，看前边那个比后边那个多几分钟
     * @param o 传入的TimePoint对象
     * @return 返回为正，就是答案；返回为负，就说明前边那个TimePoint比较小
     */
    public int moreMinutes(TimePoint o) {
        if (this.compareTo(o) >= 0) {
            return this.getMinutes() - o.getMinutes();
        }
        return -1;
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
