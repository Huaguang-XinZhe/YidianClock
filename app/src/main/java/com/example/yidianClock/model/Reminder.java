package com.example.yidianClock.model;

import org.litepal.crud.LitePalSupport;

import java.util.Date;
import java.util.List;

public class Reminder extends LitePalSupport {
    int id;
    /**
     * 姓名、纪念几周年、高考等
     */
    String title;
    /**
     * 第一次标准化之后通过计算过去的目标日（会变化），用来排序
     */
    Date goalDate;
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

    public Reminder(String title, String label, Date goalDate, String type, String timeStr) {
        this.title = title;
        this.goalDate = goalDate;
        this.label = label;
        this.type = type;
        this.timeStr = timeStr;
    }

    public Reminder() {
        this("", "", new Date(), "", "");
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
                ", goalDate='" + goalDate + '\'' +
                ", label='" + label + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getGoalDate() {
        return goalDate;
    }

    public void setGoalDate(Date goalDate) {
        this.goalDate = goalDate;
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
