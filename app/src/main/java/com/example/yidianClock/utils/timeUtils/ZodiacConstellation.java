package com.example.yidianClock.utils.timeUtils;

/**
 * 通过生日计算生肖和星座的工具类
 */
public class ZodiacConstellation {
    /**
     * 星座隔点日数组
     */
    private final static int[] dayArr = new int[] { 20, 19, 21, 20, 21, 22, 23,
            23, 23, 24, 23, 22 };
    /**
     * 星座名称数组
     */
    private final static String[] constellationArr = new String[] {
            "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座",
            "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };

    //——————————————————————————————————————————————————————————————————————————————————————————————

    /**
     * 通过生日来计算生肖和星座，得到由此二者组成的数组
     * @param dateOfBirth 标准化格式的出生日期，如：2001-11-09
     * @return 数组，0：生肖，1：星座
     */
    public static String[] getArr(String dateOfBirth) {
        String[] arr = new String[2];
        String[] dateArr = dateOfBirth.split("-");
        int year = Integer.parseInt(dateArr[0]);
        int month = Integer.parseInt(dateArr[1]);
        int day = Integer.parseInt(dateArr[2]);
        arr[0] = calculateTheZodiac(year);
        arr[1] = calculateTheConstellations(month, day);
        return arr;
    }

    /**
     * 通过月和日来计算星座
     */
    private static String calculateTheConstellations(int month, int day) {
        return day < dayArr[month - 1] ? constellationArr[month - 1]
                : constellationArr[month];
    }

    /**
     * 通过年份计算生肖
     */
    private static String calculateTheZodiac(int year) {
        if (year < 1900) {
            return "未知";
        }
        int start = 1900;
        String[] years = new String[] { "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊",
                "猴", "鸡", "狗", "猪" };
        return years[(year - start) % years.length];
    }

}
