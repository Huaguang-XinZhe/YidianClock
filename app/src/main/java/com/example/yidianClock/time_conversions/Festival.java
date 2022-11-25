package com.example.yidianClock.time_conversions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 节日和节气相关
 */
public class Festival {
    //春节|除夕|过年|中元节|七月半|母亲节|父亲节|五一|劳动节|六一|儿童节|情人节|高考|
    // (元旦|元宵|清明|端午|中秋|重阳|国庆|七夕|圣诞)节?
    static Map<String, String> festivalMap = new HashMap<>();
    /**
     * 节气D值
     */
    private static final double D = 0.2422;
    /**
     * 20世纪的节气C值
     */
    private static final double[] C_20 = { 6.11, 20.84, 4.6295, 19.4599, 6.3826, 21.4155, 5.59, 20.888, 6.318, 21.86,
            6.5, 22.2, 7.928, 23.65, 8.35, 23.95, 8.44, 23.822, 9.098, 24.218, 8.218, 23.08, 7.9, 22.6 };
    /**
     * 21世纪的节气C值
     */
    private static final double[] C_21 = { 5.4055, 20.12, 3.87, 18.73, 5.63, 20.646, 4.81, 20.1, 5.52, 21.04, 5.678,
            21.37, 7.108, 22.83, 7.5, 23.13, 7.646, 23.042, 8.318, 23.438, 7.438, 22.36, 7.18, 21.94 };
    /**
     * 24节气
     */
    private static final String[] TERM = { "小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨",
            "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至" };
    /**
     * 以上二十四节气所在的月份，一一对应
     */
    static final int[] TERM_MONTH = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12};
    /**
     * 寿星公式计算特例，匹配节气偏移年份及其增减情况，如2026+
     */
    private static final String OFFSET_REGEX = "\\d{4}[+-]";


    static {
        //这里加的都是公历日期
        festivalMap.put("元旦", "0101");
        festivalMap.put("元旦节", "0101");
        festivalMap.put("情人节", "02/14");
//        festivalMap.put("清明节")
    }

    // TODO: 2022/11/25 几月的第几个星期几
    // TODO: 2022/11/25 根据节气得出其具体日期
    

}
