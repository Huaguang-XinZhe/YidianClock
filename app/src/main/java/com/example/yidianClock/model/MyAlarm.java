package com.example.yidianClock.model;

import org.litepal.LitePal;

public class MyAlarm {
    float restTime;
    String alarmContent;
    String potStr;
    int shockInterval;
    boolean isShockTipSet;
    boolean isTaskSet;
    boolean isRing;
    String ringtoneUriStr;
    String ringtoneTitle;
    //__________________________________
    private final boolean isNight;
    private LunchAlarm lunch = LitePal.findFirst(LunchAlarm.class);
    private SleepAlarm sleep = LitePal.findFirst(SleepAlarm.class);


//    private static class One extends LunchAlarm { }
//
//    private static class Two extends SleepAlarm { }

    public MyAlarm(boolean isNight) {
        this.isNight = isNight;
        getDataFromDB();
    }

    /**
     * 从数据库取值，并设置数据到该对象（用于闹钟设置或setting界面）
     */
    public void getDataFromDB() {
        //共同需要
        restTime = getRestTime2();
        alarmContent = getContent2();
        isShockTipSet = isShockTipSet2();
        isTaskSet = isTaskSet2();
        shockInterval = getShockInterval2();
        isRing = isRing2();
        ringtoneUriStr = getRingtoneUri2();
        ringtoneTitle = getRingtoneTitle2();

        //setting页面需要
        potStr = getTimeStart2() + " ~ " + getTimeEnd2();
    }

    public String getRingtoneTitle2() {
        if (isNight) {
            return sleep.getRingtoneTitle();
        } else {
            return lunch.getRingtoneTitle();
        }
    }

    public String getRingtoneUri2() {
        if (isNight) {
            return sleep.getRingtoneUriStr();
        } else {
            return lunch.getRingtoneUriStr();
        }
    }

    public float getRestTime2() {
        if (isNight) {
            return sleep.getSleepTime();
        } else {
            return lunch.getLunchTime();
        }
    }

    public String getContent2() {
        if (isNight) {
            return sleep.getContent();
        } else {
            return lunch.getContent();
        }
    }

    public String getTimeStart2() {
        if (isNight) {
            return sleep.getSleepStart();
        } else {
            return lunch.getLunchStart();
        }
    }

    public String getTimeEnd2() {
        if (isNight) {
            return sleep.getSleepEnd();
        } else {
            return lunch.getLunchEnd();
        }
    }

    public int getShockInterval2() {
        if (isNight) {
            return sleep.getSleepShockInterval();
        } else {
            return lunch.getLunchShockInterval();
        }
    }

    public boolean isShockTipSet2() {
        if (isNight) {
            return sleep.isSetShockTip();
        } else {
            return lunch.isSetShockTip();
        }
    }

    public boolean isTaskSet2() {
        if (isNight) {
            return sleep.isSetTask();
        } else {
            return lunch.isSetTask();
        }
    }

    public boolean isRing2() {
        if (isNight) {
            return sleep.isRing();
        } else {
            return lunch.isRing();
        }
    }

    public String getRingtoneTitle() {
        return ringtoneTitle;
    }

    public void setRingtoneTitle(String ringtoneTitle) {
        this.ringtoneTitle = ringtoneTitle;
    }

    public String getRingtoneUriStr() {
        return ringtoneUriStr;
    }

    public void setRingtoneUriStr(String ringtoneUriStr) {
        this.ringtoneUriStr = ringtoneUriStr;
    }

    public float getRestTime() {
        return restTime;
    }

    public String getAlarmContent() {
        return alarmContent;
    }

    public String getPotStr() {
        return potStr;
    }

    public int getShockInterval() {
        return shockInterval;
    }

    public boolean isShockTipSet() {
        return isShockTipSet;
    }

    public boolean isTaskSet() {
        return isTaskSet;
    }

    public boolean isRing() {
        return isRing;
    }

    public void setRestTime(float restTime) {
        this.restTime = restTime;
    }

    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }

    public void setPotStr(String potStr) {
        this.potStr = potStr;
    }

    public void setShockInterval(int shockInterval) {
        this.shockInterval = shockInterval;
    }

    public void setShockTipSet(boolean shockTipSet) {
        isShockTipSet = shockTipSet;
    }

    public void setTaskSet(boolean taskSet) {
        isTaskSet = taskSet;
    }

    public void setRing(boolean ring) {
        isRing = ring;
    }
}
