package com.example.yidianClock.model;

import org.litepal.crud.LitePalSupport;

public class Reminder extends LitePalSupport {
    /**
     * 姓名、纪念几周年、高考等
     */
    String name;
    /**
     * 经过处理的标准化的出生日期
     */
    String date;
    /**
     * 存储标签，如生日、纪念日、倒计时等
     */
    String label;

    public Reminder(String name, String label, String date) {
        this.name = name;
        this.date = date;
        this.label = label;
    }

    //——————————————————————————————————————————————————————————————————————————————————————————————

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
