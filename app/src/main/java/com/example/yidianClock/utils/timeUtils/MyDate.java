package com.example.yidianClock.utils.timeUtils;

import com.example.yidianClock.utils.MyUtils;

/**
 * 标准化日期（如：2001-11-09）比较工具类
 */
public class MyDate implements Comparable<MyDate> {
    private String date;

    public MyDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(MyDate o) {
        int[] dateArr = MyUtils.getDateArr(date);
        int[] otherDateArr = MyUtils.getDateArr(o.date);
        //先比较年份
        if (dateArr[0] > otherDateArr[0]) {
            //当前年份大
            return 1;
        } else if (dateArr[0] == otherDateArr[0]) {
            //当前年份和比较年份相等，进一步比较月份
            if (dateArr[1] > otherDateArr[1]) {
                //当前月份大
                return 1;
            } else if (dateArr[1] == otherDateArr[1]) {
                //当前月份和比较月份相等，进一步比较日
                //Integer类的compare方法没有提供数值相等时的回调，故不能应用于比较的开端（只能用于末端）
                return Integer.compare(dateArr[2], otherDateArr[2]);
            } else {
                //当前月份小
                return -1;
            }

        } else {
            //当前年份小
            return -1;
        }

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

//    public static void main(String[] args) {
//        MyDate date = new MyDate("2022-11-28");
//        MyDate otherDate = new MyDate("2022-11-30");
//        if (date.compareTo(otherDate) < 0) System.out.println("true");
//    }
}
