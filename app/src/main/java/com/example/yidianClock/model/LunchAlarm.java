package com.example.yidianClock.model;

import org.litepal.crud.LitePalSupport;

public class LunchAlarm extends LitePalSupport {
    private float restTime = 45;
    private String content = "别睡太长，晚上睡不着就完了";
    private boolean isSetShockTip = false;
    private boolean isSetTask = false;
    private boolean isRing = true;
    private int shockInterval = 30;
    private String ringtoneUriStr = "content://media/internal/audio/media/2549?title=ringtone_035&canonical=1";
    private String ringtoneTitle = "系统默认";
    private String timeStart = "10:30";
    private String timeEnd = "16:30";

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

    public int getShockInterval() {
        return shockInterval;
    }

    public void setShockInterval(int shockInterval) {
        this.shockInterval = shockInterval;
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

    public int getLunchShockInterval() {
        return shockInterval;
    }

    public void setLunchShockInterval(int lunchShockInterval) {
        this.shockInterval = lunchShockInterval;
    }

    public String getLunchStart() {
        return timeStart;
    }

    public String getLunchEnd() {
        return timeEnd;
    }

    public float getLunchTime() {
        return restTime;
    }

    public void setLunchTime(int lunchTime) {
        this.restTime = lunchTime;
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
}
