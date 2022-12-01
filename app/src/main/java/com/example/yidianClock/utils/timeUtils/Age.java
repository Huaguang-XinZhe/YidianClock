package com.example.yidianClock.utils.timeUtils;

import android.text.format.DateFormat;
import android.util.Log;

import com.example.yidianClock.time_conversions.Festival;
import com.example.yidianClock.utils.MyUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * 年龄计算工具类（包括周岁、虚岁）
 */
public class Age {
    /**
     * 当前年份（今年）
     */
    private static final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    //—————————————————————————————————————————————————————————————————————————————————————————————

    /**
     * 计算周岁
     * @param dateOfBirth 标准化格式的出生日期，如：2001-11-09
     */
    public static int calculateRealYears(String dateOfBirth) {
        //初始 0 周岁
        int age = 0;
        if (isBirthOver(dateOfBirth)) {
            //生日过了，周岁 = 年份差
            age = getTheYearDiff(dateOfBirth);
        } else {
            //没过，周岁 = 年份差-1
            age = getTheYearDiff(dateOfBirth) - 1;
        }
        return age;
    }

    /**
     * 计算虚岁（比计算周岁要复杂一些）
     * 若混淆周虚岁，公历同年出生的人最多可相差 4 岁
     * @param dateOfBirth 标准化格式的出生日期，如：2001-11-09
     */
    public static int calculateVirtualYears(String dateOfBirth) {
        //初始 1 虚岁
        int age_birthYear = 1;
        int age_thisYear;
        //超过了就不加虚岁（过年当天出生也不再加），还没过就加一虚岁
        //出生年虚岁的计算
        boolean isOver_birthYear = isSpringFestivalOver(dateOfBirth);
        if (!isOver_birthYear) age_birthYear = 2;
        //出生年和今年中间间隔的年份数（不包括这两年）
        int intervalYears = getTheYearDiff(dateOfBirth) - 1;
        //今年虚岁的计算
        boolean isOver_thisYear = isSpringFestivalOver(MyUtils.getCurrentDate());
        if (isOver_thisYear) {
            //今天的日期已经超过了今年春节，今年的虚岁加一
            age_thisYear = 1;
        } else {
            //没过今年春节，不加岁
            age_thisYear = 0;
        }
        return age_birthYear + intervalYears + age_thisYear;
    }

    /**
     * 判断一个日期是否已经过了当年（给定日期的年份）春节（日期就在当年春节也算超过了）
     * @param date 要和春节比较的标准化日期，如：2001-11-09
     * @return true：超过了，false：还没到
     */
    private static boolean isSpringFestivalOver(String date) {
        //获取给定日期的年份
        String year = date.split("-")[0];
        //给定日期当年的春节
        String springFestival = Festival.festival2dateStr(year, "春节");
        return new MyDate(date).compareTo(new MyDate(springFestival)) >= 0;
    }

    /**
     * 判断当年的生日是否过了（生日当天也算过了）
     * @param dateOfBirth 标准化格式的出生日期，如：2001-11-09
     * @return true：过了，false：没过
     */
    public static boolean isBirthOver(String dateOfBirth) {
        MyDate currentDate = new MyDate(MyUtils.getCurrentDate());
        MyDate birthDate = new MyDate(getBirthdayThisYear(dateOfBirth));
        return currentDate.compareTo(birthDate) >= 0;
    }

    /**
     * 获取生日在今年的目标日，即今年的生日
     * @param dateOfBirth 标准化格式的出生日期，如：2001-11-09
     */
    public static String getBirthdayThisYear(String dateOfBirth) {
        String birthYear = dateOfBirth.split("-")[0];
        return dateOfBirth.replace(birthYear, currentYear + "");
    }

    /**
     * 获取生日年份和当前年份的年份差
     * @param dateOfBirth 标准化格式的出生日期，如：2001-11-09
     *                    注意！传入的出生年份一定不能比当前年份（今年）大
     * @return 年份差，int型
     */
    private static int getTheYearDiff(String dateOfBirth) {
        int year = Integer.parseInt(dateOfBirth.split("-")[0]);
        return currentYear - year;
    }

//    public static void main(String[] args) {
////        int age = Age.calculateVirtualYears("1998-10-07");
////        int realAge = Age.calculateRealYears("2001-11-09");
////        System.out.println(realAge);
//        System.out.println(getDaysDiff("2022-12-10"));
//
//    }
}
