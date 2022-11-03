package com.example.yidianClock.model;

import org.litepal.crud.LitePalSupport;

public class LunchAlarm extends LitePalSupport {
    private float restTime = 45;
    private String content = "别睡太长，晚上睡不着就完了";
    private boolean isSetShockTip = false;
    private boolean isSetTask = false;
    private boolean isRing = true;
    private int shockInterval = 30;

    private String timeStart = "10:30";
    private String timeEnd = "16:30";


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

    public void setLunchStart(String lunchStart) {
        this.timeStart = lunchStart;
    }

    public void setLunchEnd(String lunchEnd) {
        this.timeEnd = lunchEnd;
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
