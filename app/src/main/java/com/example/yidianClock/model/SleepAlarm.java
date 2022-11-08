package com.example.yidianClock.model;

import org.litepal.crud.LitePalSupport;

public class SleepAlarm extends LitePalSupport {
    private float restTime = 7;
    private String content = "又是元气满满的一天";
    private boolean isSetShockTip = false;
    private boolean isSetTask = false;
    private boolean isRing = true;
    private String ringtoneUriStr = "content://media/internal/audio/media/2549?title=ringtone_035&canonical=1";
    private String ringtoneTitle = "系统默认";
    private String timeStart = "21:30";
    private String timeEnd = "2:30";
    private int shockInterval = 50;
    private boolean isJustShockOn = true;
    private String beforeTimeStr = "7:00";

    public String getRingtoneTitle() {
        return ringtoneTitle;
    }

    public void setRingtoneTitle(String ringtoneTitle) {
        this.ringtoneTitle = ringtoneTitle;
    }

    public float getRestTime() {
        return restTime;
    }

    public void setRestTime(float restTime) {
        this.restTime = restTime;
    }

    public String getRingtoneUriStr() {
        return ringtoneUriStr;
    }

    public void setRingtoneUriStr(String ringtoneUriStr) {
        this.ringtoneUriStr = ringtoneUriStr;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getShockInterval() {
        return shockInterval;
    }

    public void setShockInterval(int shockInterval) {
        this.shockInterval = shockInterval;
    }

    public boolean isJustShockOn() {
        return isJustShockOn;
    }

    public void setJustShockOn(boolean justShockOn) {
        isJustShockOn = justShockOn;
    }

    public String getBeforeTimeStr() {
        return beforeTimeStr;
    }

    public void setBeforeTimeStr(String beforeTimeStr) {
        this.beforeTimeStr = beforeTimeStr;
    }

    public int getSleepShockInterval() {
        return shockInterval;
    }

    public void setSleepShockInterval(int sleepShockInterval) {
        this.shockInterval = sleepShockInterval;
    }

    public float getSleepTime() {
        return restTime;
    }

    public void setSleepTime(float sleepTime) {
        this.restTime = sleepTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSetShockTip() {
        return isSetShockTip;
    }

    public void setSetShockTip(boolean setShockTip) {
        isSetShockTip = setShockTip;
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

    public String getSleepStart() {
        return timeStart;
    }

    public void setSleepStart(String sleepStart) {
        this.timeStart = sleepStart;
    }

    public String getSleepEnd() {
        return timeEnd;
    }

    public void setSleepEnd(String sleepEnd) {
        this.timeEnd = sleepEnd;
    }
}
