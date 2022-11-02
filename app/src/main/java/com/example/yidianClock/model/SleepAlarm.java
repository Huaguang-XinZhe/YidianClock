package com.example.yidianClock.model;

import org.litepal.crud.LitePalSupport;

public class SleepAlarm extends LitePalSupport {
    private float sleepTime = 7;
    private String content = "又是元气满满的一天";
    private boolean isSetShockTip = true;
    private boolean isSetTask = true;
    private boolean isRing = true;

    private String sleepStart = "21:30";
    private String sleepEnd = "2:30";
    private int sleepShockInterval = 50;
    private boolean isJustShockOn = true;
    private String beforeTimeStr = "7:00";


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
        return sleepShockInterval;
    }

    public void setSleepShockInterval(int sleepShockInterval) {
        this.sleepShockInterval = sleepShockInterval;
    }

    public float getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(float sleepTime) {
        this.sleepTime = sleepTime;
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
        return sleepStart;
    }

    public void setSleepStart(String sleepStart) {
        this.sleepStart = sleepStart;
    }

    public String getSleepEnd() {
        return sleepEnd;
    }

    public void setSleepEnd(String sleepEnd) {
        this.sleepEnd = sleepEnd;
    }
}
