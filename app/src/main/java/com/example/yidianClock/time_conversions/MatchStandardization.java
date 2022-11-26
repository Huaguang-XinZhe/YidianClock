package com.example.yidianClock.time_conversions;

import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MatchStandardization {
    /**
     * 匹配年月日（号）类型
     */
    static final String YMD_REGEX = "((\\d{2,4}|[〇零一二三四五六七八九]{2,4})年)?" +
            "((\\d{1,2}|十[一二]|[一二三四五六七八九十])月|大年)(\\d{1,2}|[一二三四五六七八九十]{1,3})[日号]";
    /**
     * 匹配节日或节气正则表达式（包括年部分）
     */
    static final String YEAR_IN_FESTIVAL_REGEX = "((\\d{2,4}|[〇零一二三四五六七八九]{2,4})年)?" +
            "(立春|雨水|惊蛰|春分|清明|谷雨|立夏|小满|芒种|夏至|小暑|大暑|立秋|处暑|白露|秋分|寒露|霜降|立冬|小雪|大雪|冬至|小寒|大寒|" +
            "春节|除夕|过年|中元节|七月半|鬼节|母亲节|父亲节|五一|劳动节|六一|儿童节|情人节|高考|(元旦|元宵|清明|端午|中秋|重阳|国庆|七夕|圣诞)节?))";
    /**
     * 匹配农历日期的正则
     */
    static final String LUNAR_REGEX = "((\\d{2,4}|[〇零一二三四五六七八九]{2,4})年)?([一二三四五六七八九十正冬仲子腊]月|闰[一二三四五六七八九]|大年)[初十廿三][一二三四五六七八九十]$";
    /**
     * 单独匹配节日
     */
    static final String FESTIVAL_REGEX = "春节|除夕|过年|中元节|七月半|鬼节|母亲节|父亲节|五一|劳动节" +
            "|六一|儿童节|情人节|高考|(元旦|元宵|清明|端午|中秋|重阳|国庆|七夕|圣诞)节?))";
    /**
     * 单独匹配节气
     */
    static final String TERM_REGEX = "立春|雨水|惊蛰|春分|清明|谷雨|立夏|小满|芒种|夏至|小暑|大暑|立秋|处暑|白露|秋分|寒露|霜降|立冬|小雪|大雪|冬至|小寒|大寒";
    /**
     * 匹配年前边的部分
     */
    static final String YEAR_FORWARD_REGEX = "(\\d{2,4}|[〇零一二三四五六七八九]{2,4})(?=年)";
    /**
     * 匹配月前边的部分
     */
    static final String MONTH_FORWARD_REGEX = "(\\d{1,2}|十[一二]|[一二三四五六七八九十])(?=月)";
    /**
     * 匹配日前边的部分
     */
    static final String DAY_FORWARD_REGEX = "(\\d{1,2}|[一二三四五六七八九十]{1,3})(?=[日号])";
    /**
     * 中文数字与阿拉伯数字的映射
     */
    static final Map<Character, Integer> cn_numMap = new HashMap<>();
    /**
     * 今年，Int型
     */
    static final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    static {
        cn_numMap.put('〇', 0);
        cn_numMap.put('零', 0);
        cn_numMap.put('一', 1);
        cn_numMap.put('二', 2);
        cn_numMap.put('三', 3);
        cn_numMap.put('四', 4);
        cn_numMap.put('五', 5);
        cn_numMap.put('六', 6);
        cn_numMap.put('七', 7);
        cn_numMap.put('八', 8);
        cn_numMap.put('九', 9);
        cn_numMap.put('十', 0);
    }

    //含年月日（号）的类型，如：
    //2014年4月1日
    //2013年4月15日
    //03年5月7日
    //一九七七年四月九日
    //1977年4月9日
    //77年4月9号
    //01年11月9号
    //八三年十月三日
    //八三年十一月二十三日
    //八三年十二月十三日
    //01年11月九号
    /**
     * 转换含年月日类型的时间字符串为标准时间字符串
     * @param matchedStr 匹配到的时间字符串，默认不为空
     * @return 2001-11-09型字符串
     */
    public static String conversionsYMD(String matchedStr) {
        String year;
        String month;
        String day;
        String deepMatchedStr = getDeepMatchedStr(YMD_REGEX, matchedStr);
        //有可能为空串，空串就是未来的时间，倒计时
//        String yearMatched = getDeepMatchedStr(YEAR_FORWARD_REGEX, deepMatchedStr);
        String monthMatched = getDeepMatchedStr(MONTH_FORWARD_REGEX, deepMatchedStr);
        String dayMatched = getDeepMatchedStr(DAY_FORWARD_REGEX, deepMatchedStr);

        //处理年份，分两种情况：中文和数字。这两种情况下面又分两种情况：四位和两位___________________
        //中文转换为数字，不全面的转换为全面
        year = conversionsYear(deepMatchedStr);
        //处理月__________________________________________________________________________
        month = conversionsMonthOrDay(monthMatched);
        //处理日（号），这个和月的处理类似______________________________________________________
        day = conversionsMonthOrDay(dayMatched);

        //组合返回
        String date = year + "-" + month + "-" + day;
        Log.i("getSongsList", "date = " + date);
        return date;
    }

    //高考
    //母亲节
    //冬至
    //83年元旦
    //九四年重阳节
    //01年春分
    /**
     * 将含节日、节气的时间字符串转换为标准日期
     * @param matchedStr 匹配到的时间字符串（可能含年也可能不含）
     * @return 标准日期，如：2001-11-09
     */
    public static String conversions_festival(String matchedStr) {
        String date;
        //一定不为空串，结果如上示例
        String deepMatchedStr = getDeepMatchedStr(YEAR_IN_FESTIVAL_REGEX, matchedStr);
        //不为空串
        String year = conversionsYear(deepMatchedStr);
        //先匹配节日，如果匹配的节日为空，那就再匹配节气
        String festival = getDeepMatchedStr(FESTIVAL_REGEX, deepMatchedStr);
        if (!festival.isEmpty()) {
            date = Festival.festival2dateStr(year, festival);
        } else {
            //匹配到的字符串中一定含有节气，获取它
            String solarTerm = getDeepMatchedStr(TERM_REGEX, deepMatchedStr);
            date = Festival.solarTerms2dateStr(year, solarTerm);
        }
        return date;
    }

    //83年八月初二
    //八三年八月初二
    //83年八月廿二
    //九四年正月初九
    //70年正月初十
    //79年冬月三十
    //八零年腊月廿九
    //82年闰四初七
    //正月十五
    //五月廿二
    public static String conversionsLunar(String matchedStr) {
        String date;
        //一定不为空串，结果如上示例
        String deepMatchedStr = getDeepMatchedStr(LUNAR_REGEX, matchedStr);
        //不为空串
        String year = conversionsYear(deepMatchedStr);

    }


    /**
     * 单独转换年份，这个在其他类型的全日期转换中用得着
     * @param deepMatchedStr 含年的时间字符串
     * @return 返回标准的四位数字年份，不为空串（没有年份就是今年）
     */
    private static String conversionsYear(String deepMatchedStr) {
        String year;
        String yearMatched = getDeepMatchedStr(YEAR_FORWARD_REGEX, deepMatchedStr);
        if (!yearMatched.isEmpty()) {
            if (yearMatched.matches("\\d")) {
                //全是数字
                year = yearMatched;
            } else {
                //中文数字
                String yearNum = cn2num(yearMatched);
                if (yearNum.length() == 2) {
                    year = yearTwo2Four(yearNum);
                } else {
                    year = yearNum;
                }
            }
        } else {
            //获取不到年份就是今年
            year = currentYear + "";
        }
        return year;
    }

    /**
     * 转换月、日为两位数字形式
     * @param monthOrDayMatched 匹配到的月、日字符串
     * @return 两位数字字符串，如：11、09、28
     */
    private static String conversionsMonthOrDay(String monthOrDayMatched) {
        String monthOrDay;
        if (monthOrDayMatched.matches("\\d")) {
            //数字
            monthOrDay = monthOrDayMatched;
        } else {
            //中文
            monthOrDay = cn2arabic(monthOrDayMatched);
        }
        return monthOrDay;
    }

    /**
     * 三十一以内的中文数字转换为阿拉伯数字（有意义）
     * 让十变成0
     * 三》3  》》3
     * 十》10  》》0
     * 十一》11  》》01
     * 二十》20  》》20
     * 二十九》29  》》209
     * @param cn 三十一以内的中文
     * @return 标准化的月或日 01、10、12、21、31
     */
    private static String cn2arabic(String cn) {
        String arabic;
        String numStr = cn2num(cn);
        if (numStr.contains("0")) {
            switch (numStr.length()) {
                case 1:
                    arabic = "10";
                    break;
                case 2:
                    if (Integer.parseInt(numStr) < 10) {
                        //匹配01~09的情况
                        arabic = "1" + numStr.charAt(1);
                    } else {
                        //匹配20、30
                        arabic = numStr;
                    }
                    break;
                case 3:
                    //匹配21~29、31
                    arabic = numStr.replace("0", "");
                    break;
                default:
                    //一般不存在这种情况
                    arabic = "";
            }
        } else {
            //使用cn.charAt(0)将字符串转换为字符
            //匹配1~9，为了标准化，前面补上一个0
            arabic = "0" + cn_numMap.get(cn.charAt(0));
        }
        return arabic;
    }



    /**
     * 将以两位数表示的年份，转换为四位
     * @param yearLastTwo 年份的后两位（数字形式），String型
     * @return 以字符串表示的四位年份
     */
    private static String yearTwo2Four(String yearLastTwo) {
        String year;
        int currentYearLastTwo = Integer.parseInt(String.valueOf(currentYear).substring(2));
        Log.i("getSongsList", "currentYearLastTwo = " + currentYearLastTwo);
        if (Integer.parseInt(yearLastTwo) <= currentYearLastTwo) {
            //比当前小，21世纪，前面补20
            year = "20" + yearLastTwo;
        } else {
            //比当前大，20世纪，前面补19
            year = "19" + yearLastTwo;
        }
        Log.i("getSongsList", "四位数字年份：" + year);
        return year;
    }

    /**
     * 主要用于将中文年份转换为数字形式，如：一九七七 -> 1977 或 七七 -> 77
     * 此方法也能用于将中文数字转换为数字字符串（十用0代替，不一定有意义），如：二十九》》209
     * @param cn 中文年份，可能是四位，也可能是两位（也可以是任意像转换为数字形式的中文数字）
     */
    private static String cn2num(String cn) {
        //将中文年份字符串转换为字符数组
        char[] charArr = cn.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char yearChar : charArr) {
            builder.append(cn_numMap.get(yearChar));
        }
        return builder.toString();

    }

    /**
     * 按指定规则，深入匹配（只匹配一次）
     * @param regex 正则表达式
     * @param matchedStr 匹配到的时间字符串，不为空字符串
     * @return 匹配到了就返回，没匹配到就返回空串
     */
    public static String getDeepMatchedStr(String regex, String matchedStr) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(matchedStr);
        if (matcher.find()) {
            return matcher.group();
        } else return "";
    }


}
