package com.example.yidianClock.time_conversions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
            "春节|除夕|过年|中元节|七月半|鬼节|母亲节|父亲节|五一|劳动节|六一|儿童节|情人节|高考|(元旦|元宵|清明|端午|中秋|重阳|国庆|七夕|圣诞)节?)";
    /**
     * 匹配农历日期的正则
     */
    static final String LUNAR_REGEX = "((\\d{2,4}|[〇零一二三四五六七八九]{2,4})年)?(闰?[一二三四五六七八九十正冬仲子腊]月|大年)[初十廿三][一二三四五六七八九十]$";
    /**
     * 匹配格式化的日期、混合型日期，如：01/11/9，九八年10-7
     * 注意，这里的年份有可能不存在
     */
    static final String FM_DATE_REGEX = "((\\d{2,4}|[零一二三四五六七八九]{2,4})[年./-])?\\d{1,2}[\\./-]\\d{1,2}";
    /**
     * 单独匹配节日
     */
    static final String FESTIVAL_REGEX = "春节|除夕|过年|中元节|七月半|鬼节|母亲节|父亲节|五一|劳动节" +
            "|六一|儿童节|情人节|高考|(元旦|元宵|清明|端午|中秋|重阳|国庆|七夕|圣诞)节?";
    /**
     * 单独匹配节气
     */
    static final String TERM_REGEX = "立春|雨水|惊蛰|春分|清明|谷雨|立夏|小满|芒种|夏至|小暑|大暑|立秋|处暑|白露|秋分|寒露|霜降|立冬|小雪|大雪|冬至|小寒|大寒";
    /**
     * 匹配年前边的部分（整合了格式化日期和混合日期型）
     */
    static final String YEAR_FORWARD_REGEX = "^(\\d{2,4}|[〇零一二三四五六七八九]{2,4})(?=[年\\./-])";
    /**
     * 匹配公历月前边的部分
     */
    static final String SOLAR_MONTH_FORWARD_REGEX = "(\\d{1,2}|十[一二]|[一二三四五六七八九十])(?=月)";
    /**
     * 匹配日（号）前边的部分
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
    /**
     * 农历的月份（中文）与公历月份映射的字符串（农公月映射字符串）数组
     */
    static final String[] _LUNAR_MONTH_ARR = new String[] {
            "正01", "一01", "二02", "三03", "四04", "五05", "六06", "七07", "八08", "九09", "十10",
            "仲11", "子11", "冬11", "十一11", "腊12", "十二12", "闰二02", "闰三03", "闰四04", "闰五05", "闰六06", "闰七07", "闰八08", "闰九09", "闰十10"
    };
    /**
     * 匹配农历月前边的部分（在农历时间字符串中匹配，如正月十五）
     */
    static final String LUNAR_MONTH_FORWARD_REGEX = "闰?[一二三四五六七八九十正冬仲子腊](?=月)";
    /**
     * 匹配农历日部分
     */
    static final String LUNAR_DAY_REGEX = "[初十廿三][一二三四五六七八九十]";
    /**
     * 农历日专用词和数字字符串的映射
     */
    static final Map<Character, String> lunarDayMap = new HashMap<>();
    /**
     * 匹配格式化的几月几号，如：11/1，11.1
     */
    static final String FORMAT_MD_REGEX = "\\d{1,2}[\\./-]\\d{1,2}$";
    /**
     * 匹配格式化日期、混合型日期的月
     */
    static final String FM_MONTH_REGEX = "\\d{1,2}(?=[\\\\./-])";
    /**
     * 匹配格式化日期、混合型日期的日
     */
    static final String FM_DAY_REGEX = "\\d{1,2}$";

    /**
     * 完整地匹配时间字符串的正则表达式数组
     */
    static final String[] COMPLETE_REGEX_ARR = new String[] {
            YMD_REGEX, FM_DATE_REGEX, YEAR_IN_FESTIVAL_REGEX, LUNAR_REGEX
    };

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

        lunarDayMap.put('初', "0");
        lunarDayMap.put('十', "1");
        lunarDayMap.put('廿', "2");
        lunarDayMap.put('三', "3");
    }

    /**
     * 对外接口，将一切形式的时间字符串转换为标准时间字符串，如：2001-11-09
     * @param matchedStr 目前只支持四种类型（互不重叠），几月几日（号）型、格式化或混合型、节日或节气型、农历型
     */
    public static String conversions(String matchedStr) {
        String date;
        String deepMatchedStr = "";
        int count = 0;
        int index = 0;
        for (String completeRegex : COMPLETE_REGEX_ARR) {
            //执行一次循环，记录一次
            count++;
            String _deepMatchedStr = getDeepMatchedStr(completeRegex, matchedStr);
            if (!_deepMatchedStr.isEmpty()) {
                //只可能有一个正则匹配出来不是空串，记录下来，退出循环
                deepMatchedStr = _deepMatchedStr;
                index = count - 1;
                break;
            }
        }
        String year = conversionsYear(deepMatchedStr);
        switch (index) {
            case 0:
                //年月日（号）类型
                date = conversionsYMD(year, deepMatchedStr);
                break;
            case 1:
                date = conversionsFMDate(year, deepMatchedStr);
                break;
            case 2:
                date = conversions_festival(year, deepMatchedStr);
                break;
            case 3:
                date = conversionsLunar(year, deepMatchedStr);
                break;
            default:
                date = "无法解析";
        }
        return date;

    }

//    public static void main(String[] args) {
//        String[] sourceArr = new String[] {
//                "2011.11.9", "一九七七年四月九日", "1977年4月9日", "2001-11-9", "77年4月9号", "01年11月9号",
//                "81-3-4", "01/4/19", "83年八月初二"
////                "八三年八月初二", "83年元旦", "九四年重阳节", "01年春分",
////                "八三年十二月二十三日", "九四年正月初九"
////                "70年正月初十", "79年冬月三十", "八零年腊月廿九", "82年闰四月初七", "九八年10-7", "立冬", "小雪"
//        };
//        for (String source : sourceArr) {
//            System.out.println(conversions(source));
//        }
//    }

    //2011.11.9
    //2001-11-9
    //81-3-4
    //01/4/19
    //九八年10/7
    /**
     * 将格式化的日期、混合型日期转换为标准日期格式，如：2001-11-09，九八年10/7
     * @param deepMatchedStr 对原始时间字符串进行深入匹配得到到该种类型的时间字符串，如上示例
     */
    private static String conversionsFMDate(String year, String deepMatchedStr) {
        String formatMD = getDeepMatchedStr(FORMAT_MD_REGEX, deepMatchedStr);
        //获取间隔符
//        String intervalSymbol = getDeepMatchedStr("[\\./-]", formatMD);
        //只可能有两个元素，一个月，一个日
//        String[] dateArr = formatMD.split(intervalSymbol);
        //注意，split中传入的是正则表达式，光一个点是不行的，必须进行转义
//        String[] dateArr = "11.9".split("\\.");
        String matchedMonth = getDeepMatchedStr(FM_MONTH_REGEX, formatMD);
        String matchedDay = getDeepMatchedStr(FM_DAY_REGEX, formatMD);
        String month = Festival.addZero(Integer.parseInt(matchedMonth));
        String day = Festival.addZero(Integer.parseInt(matchedDay));
        return year + "-" + month + "-" + day;
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
     * @param deepMatchedStr 对原始时间字符串进行深入匹配得到到该种类型的时间字符串，如上示例
     * @return 2001-11-09型字符串
     */
    private static String conversionsYMD(String year, String deepMatchedStr) {
        String month;
        String day;
        String monthMatched = getDeepMatchedStr(SOLAR_MONTH_FORWARD_REGEX, deepMatchedStr);
        String dayMatched = getDeepMatchedStr(DAY_FORWARD_REGEX, deepMatchedStr);

        //处理年份，分两种情况：中文和数字。这两种情况下面又分两种情况：四位和两位___________________
        //中文转换为数字，不全面的转换为全面
//        year = conversionsYear(deepMatchedStr);
        //处理月__________________________________________________________________________
        month = conversionsMonthOrDay(monthMatched);
        //处理日（号），这个和月的处理类似______________________________________________________
        day = conversionsMonthOrDay(dayMatched);

        //组合返回
        //Log.i("getSongsList", "date = " + date);
        return year + "-" + month + "-" + day;
    }

    //高考
    //母亲节
    //冬至
    //83年元旦
    //九四年重阳节
    //01年春分
    /**
     * 将含节日、节气的时间字符串转换为标准日期
     * @param deepMatchedStr 对原始时间字符串进行深入匹配得到到该种类型的时间字符串，如上示例
     * @return 标准日期，如：2001-11-09
     */
    private static String conversions_festival(String year, String deepMatchedStr) {
        String date;
        //不为空串
//        String year = conversionsYear(deepMatchedStr);
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
    //82年闰四月初七
    //正月十五
    //五月廿二

    /**
     * 转换农历时间字符串为标准日期格式，如：2001-11-09
     * deepMatchedStr 对原始时间字符串进行深入匹配得到到该种类型的时间字符串，如上示例
     */
    private static String conversionsLunar(String year, String deepMatchedStr) {
        //不为空串
//        String year = conversionsYear(deepMatchedStr);
        String dayStr;
        //处理农历月____________________________________________________________________________
        String lunarMonthStr = getDeepMatchedStr(LUNAR_MONTH_FORWARD_REGEX, deepMatchedStr);
        //匹配到的农历月字符串在映射数组中的索引
        int index = getLunarMonthList().indexOf(lunarMonthStr);
        //根据索引得到其对应的数字月
        String monthNumStr = getDeepMatchedStr("\\d{2}", _LUNAR_MONTH_ARR[index]);
        //处理农历日___________________________________________________________________________
        String lunarDayStr = getDeepMatchedStr(LUNAR_DAY_REGEX, deepMatchedStr);
        char lunarDayFirst = lunarDayStr.charAt(0);
        char lunarDayLast = lunarDayStr.charAt(1);
        if (lunarDayStr.equals("初十")) {
            //初十特例，按一般计算是00，必须判断
            dayStr = "10";
        } else {
            dayStr = lunarDayMap.get(lunarDayFirst) + cn_numMap.get(lunarDayLast);
        }
        //转为公历，返回_____________________________________________________________________
        String lunarDate = year + "-" + monthNumStr + "-" + dayStr;
        return Lunar.lunar2solar(lunarDate);
    }

    // TODO: 2022/11/26 该方法可通用
    /**
     * 从农公月映射字符串数组中获取农历月字符串的列表
     */
    private static List<String> getLunarMonthList() {
        List<String> lunarMonthList = new ArrayList<>();
        for (String _lunarMonth : _LUNAR_MONTH_ARR) {
            String lunarMonth = getDeepMatchedStr("[\\u4e00-\\u9fa5]+", _lunarMonth);
            lunarMonthList.add(lunarMonth);
        }
        return lunarMonthList;
    }


    /**
     * 单独转换年份，这个在其他类型的全日期转换中用得着
     * @param deepMatchedStr 含年的时间字符串
     * @return 返回标准的四位数字年份，不为空串（没有年份就是今年）
     */
    private static String conversionsYear(String deepMatchedStr) {
        String year;
        String yearMatched = getDeepMatchedStr(YEAR_FORWARD_REGEX, deepMatchedStr);
        //匹配数字年份
        String yearNumStr = getDeepMatchedStr("\\d{2,4}", yearMatched);
        if (!yearMatched.isEmpty()) {
            if (!yearNumStr.isEmpty()) {
                //全是数字
                if (yearNumStr.length() == 2) {
                    //两位数字，转换成四位
                    year = yearTwo2Four(yearNumStr);
                } else {
                    //四位数字
                    year = yearMatched;
                }
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
        //匹配月或日的数字表示（不要用matches，你不了解它，太坑了！）
        String mdNumStr = getDeepMatchedStr("\\d{1,2}", monthOrDayMatched);
        if (!mdNumStr.isEmpty()) {
            //数字
            monthOrDay = monthOrDayMatched;
        } else {
            //中文
            monthOrDay = cn2arabic(monthOrDayMatched);
        }
        return Festival.addZero(Integer.parseInt(monthOrDay));
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
//        Log.i("getSongsList", "currentYearLastTwo = " + currentYearLastTwo);
        if (Integer.parseInt(yearLastTwo) <= currentYearLastTwo) {
            //比当前小，21世纪，前面补20
            year = "20" + yearLastTwo;
        } else {
            //比当前大，20世纪，前面补19
            year = "19" + yearLastTwo;
        }
//        Log.i("getSongsList", "四位数字年份：" + year);
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
