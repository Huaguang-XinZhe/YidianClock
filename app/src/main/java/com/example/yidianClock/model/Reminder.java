package com.example.yidianClock.model;

import org.litepal.crud.LitePalSupport;

import java.util.List;

public class Reminder extends LitePalSupport {
    /**
     * 姓名、纪念几周年、高考等
     */
    String title;
    /**
     * 经过处理的标准化的出生日期
     */
    String date;
    /**
     * 存储标签，如生日、纪念日、倒计时等
     */
    String label;

    public Reminder(String title, String label, String date) {
        this.title = title;
        this.date = date;
        this.label = label;
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
                ", date='" + date + '\'' +
                ", label='" + label + '\'' +
                '}';
    }

    public String getName() {
        return title;
    }

    public void setName(String title) {
        this.title = title;
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
