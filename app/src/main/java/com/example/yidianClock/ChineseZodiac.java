package com.example.yidianClock;

/**
 * 生肖和星座相关
 */
public class ChineseZodiac {
    private final static int[] dayArr = new int[] { 20, 19, 21, 20, 21, 22, 23,
            23, 23, 24, 23, 22 };
    private final static String[] constellationArr = new String[] { "摩羯座",
            "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座",
            "天蝎座", "射手座", "摩羯座" };

//    /**
//     * 根据出生日期计算属相和星座
//     */
//    public static void main(String[] args) {
//        int month = 11;
//        int day = 12;
//        System.out.println("星座为：" + getConstellation(month, day));
//        System.out.println("属相为:" + getYear(2001));
//
//    }

    /**
     * 通过生日计算星座
     */
    public static String getConstellation(int month, int day) {
        return day < dayArr[month - 1] ? constellationArr[month - 1]
                : constellationArr[month];
    }

    /**
     * 通过生日计算生肖
     */
    public static String getYear(int year) {
        if (year < 1900) {
            return "未知";
        }
        int start = 1900;
        String[] years = new String[] { "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊",
                "猴", "鸡", "狗", "猪" };
        return years[(year - start) % years.length];
    }
}
