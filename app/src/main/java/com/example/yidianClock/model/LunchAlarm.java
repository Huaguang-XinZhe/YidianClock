package com.example.yidianClock.model;

import org.litepal.crud.LitePalSupport;

public class LunchAlarm extends LitePalSupport{
    private int lunchTime = 45;
    private String content = "别睡太长，晚上睡不着就芭比Q了";
    private boolean isSetShockTip = false;
    private boolean isSetTask = true;
    private boolean isRing = true;

    private String lunchStart = "10:30";
    private String lunchEnd = "16:30";


    public int getLunchShockInterval() {
        return lunchShockInterval;
    }

    public void setLunchShockInterval(int lunchShockInterval) {
        this.lunchShockInterval = lunchShockInterval;
    }

    private int lunchShockInterval = 30;


    public String getLunchStart() {
        return lunchStart;
    }

    public String getLunchEnd() {
        return lunchEnd;
    }

    public void setLunchStart(String lunchStart) {
        this.lunchStart = lunchStart;
    }

    public void setLunchEnd(String lunchEnd) {
        this.lunchEnd = lunchEnd;
    }

    public int getLunchTime() {
        return lunchTime;
    }

    public void setLunchTime(int lunchTime) {
        this.lunchTime = lunchTime;
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
