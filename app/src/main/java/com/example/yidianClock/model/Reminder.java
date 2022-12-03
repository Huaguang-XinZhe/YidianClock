package com.example.yidianClock.model;

import org.litepal.crud.LitePalSupport;

import java.util.List;

public class Reminder extends LitePalSupport {
    /**
     * 姓名、纪念几周年、高考等
     */
    String title;
    /**
     * 原始标准化日期
     */
    String priDate;
    /**
     * 存储标签，如生日、纪念日、倒计时等
     */
    String label;
    /**
     * 类型，如直接转换、节日节气、农历
     */
    String type;
    /**
     * 匹配到的时间字符串
     */
    String timeStr;

    public Reminder(String title, String label, String priDate, String type, String timeStr) {
        this.title = title;
        this.priDate = priDate;
        this.label = label;
        this.type = type;
        this.timeStr = timeStr;
    }

    //——————————————————————————————————————————————————————————————————————————————————————————————

    /**
     * 判断Reminder List集合中是否存在指定title
     * @param reminderList Reminder List集合
     * @param title 指定title
     * @return true：存在，false：不存在
     */
    public static boolean containsTitle(List<Reminder> reminderList, String title) {
        boolean isContain = false;
        for (Reminder reminder : reminderList) {
            if (reminder.title.equals(title)) {
                isContain = true;
                break;
            }
        }
        return isContain;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "title='" + title + '\'' +
                ", priDate='" + priDate + '\'' +
                ", label='" + label + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getPriDate() {
        return priDate;
    }

    public void setPriDate(String priDate) {
        this.priDate = priDate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }
}
