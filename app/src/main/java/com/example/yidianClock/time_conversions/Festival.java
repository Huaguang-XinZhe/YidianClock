package com.example.yidianClock.time_conversions;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 节日和节气相关
 */
public class Festival {
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
     * 节气一般计算的特殊情况，偏移量为1
     * 该字符串数组的元素和二十四节气数组一一对应
     */
    static final String[] SPACIAL_CASE = new String[] {"小寒1982+2019-", "大寒2000+2082+", "", "雨水2026-",
            "", "春分2084+", "", "", "立夏1911+", "小满2008+", "芒种1902+", "夏至1928+", "小暑1925+2016+", "大暑1922+",
            "立秋2002+", "", "白露1927+", "秋分1942+", "", "霜降2089+", "", "小雪1978+", "大雪1954+", "冬至1918-2021-"};
    /**
     * 寿星公式计算特例，匹配节气偏移年份及其增减情况，如2026+
     */
    private static final String OFFSET_REGEX = "\\d{4}[+-]";
    /**
     * 节日名称及其相应日期（多种形式表征）字符串（完整节日字符串）数组
     * 春节|除夕|过年|中元节|七月半|母亲节|父亲节|五一|劳动节|六一|儿童节|情人节|高考|
     * (元旦|元宵|清明|端午|中秋|重阳|国庆|七夕|圣诞)节?
     */
    static final String[] _FESTIVAL_ARR = new String[] {
            //农历部分
            "除夕1230+", "过年1230+", "中元节0715+", "七月半0715+", "鬼节0715+", "元宵0115+", "元宵节0115+",
            "端午0505+", "端午节0505+", "中秋0815+", "中秋节0815+", "重阳0909+", "重阳节0909+", "七夕0707+", "七夕节0707+", "春节0101+",
            //公历部分
            "元旦0101", "元旦节0101", "情人节0214", "五一0501", "劳动节0501", "六一0601",
            "儿童节0601", "高考0607", "国庆1001", "国庆节1001", "圣诞1225", "圣诞节1225",
            //第几月的第几个星期几
            "母亲节050207", "父亲节060307",
            //清明节（由节气来计算）
            "清明节", "清明"
    };
    /**
     * 匹配节日名称的正则
     */
    static final String FESTIVAL_REGEX = "[\\u4e00-\\u9fa5]+";
    /**
     * 匹配节日后面的日期（含表征符号）的正则
     */
    static final String _DATE_REGEX = "\\d{4,6}\\+?";

    /**
     * 将节日转化为公历的具体日期（1900~2100）
     * 可能产生空串，但一般不会
     * @param yearStr 年份字符串
     * @param festival 节日名称
     * @return 标准日期字符串，如2001-11-09
     */
    public static String festival2dateStr(String yearStr, String festival) {
        String date;
        //传入节日在数组中的索引（关键)
        int index = getFestivalList().indexOf(festival);
        //通过索引获取完整节日字符串
        String _festivalStr = _FESTIVAL_ARR[index];
        String _dateStr = MatchStandardization.getDeepMatchedStr(_DATE_REGEX, _festivalStr);
        if (!_dateStr.isEmpty()) {
            //除清明外的节日————————————————————————————————————————————————————————————————
            String oldStr = _dateStr.substring(1, 2);
            //02-14或12-30+或05-0207
            //该方法有风险，替换可能有多个！！！一定要使用replaceFirst，而不是replace
            String month_day_ = _dateStr.replaceFirst(oldStr, oldStr + "-");
            switch (_dateStr.length()) {
                case 4:
                    //传入的是公历节日，匹配结果如：0214
                    date = yearStr + "-" + month_day_;
                    break;
                case 5:
                    //传入的是农历节日，匹配结果如：1230+
                    String lunarDate = yearStr + "-" + month_day_.replace("+", "");
                    date = Lunar.lunar2solar(lunarDate);
                    break;
                case 6:
                    //传入的是母亲节或父亲节，匹配结果如：050207
                    int month = Integer.parseInt(_dateStr.substring(0, 2));
                    int num = Integer.parseInt(_dateStr.substring(2, 4));
                    int wek = Integer.parseInt(_dateStr.substring(4, 6));
                    date = get5month5week5(Integer.parseInt(yearStr), month, num, wek);
                    break;
                default:
                    date = "";
            }
        } else {
            //清明节————————————————————————————————————————————————————————————————————————
            date = solarTerms2dateStr(yearStr, "清明");
        }
        return date;
    }

//    public static void main(String[] args) {
//        String sp = festival2dateStr("2022", "春节");
//        String sp2 = festival2dateStr("2022", "除夕");
//        System.out.println("春节 = " + sp + "，除夕" + sp2);
//    }

    /**
     * 获取完整节日字符串数组中的节日名称，列表返回
     */
    private static List<String> getFestivalList() {
        List<String> festivalList = new ArrayList<>();
        for (String _festival : _FESTIVAL_ARR) {
            //一定匹配得到（只匹配一次），不为空串
            String festival = MatchStandardization.getDeepMatchedStr(FESTIVAL_REGEX, _festival);
            festivalList.add(festival);
        }
        return festivalList;
    }


    /**
     * 将二十四节气转化为公历的具体日期（1900~2100）
     * 通式寿星公式：[Y×D+C]-L
     * [  ]里面取整数；Y=年数的后2位数；D=0.2422；L=Y/4，小寒、大寒、立春、雨水的L=(Y-1)/4
     * @param yearStr 年份字符串
     * @param term 节气的名称
     * @return 标准日期字符串，如2001-11-09
     */
    public static String solarTerms2dateStr(String yearStr, String term) {
        double valueC;
        int valueL;
        int day;
        int century = Integer.parseInt(yearStr.substring(0, 2));
        //年数的后两位，也就是Y值
        int yearLastTwo = Integer.parseInt(yearStr.substring(2));
        //节气在固定数组中的索引
        int termIndex = Arrays.asList(TERM).indexOf(term);
        //根据不同的情况获取C值
        if (century == 20) {
            if (yearLastTwo > 0) {
                //取21世纪的C值
                valueC = C_21[termIndex];
            } else {
                //只可能等于0，取20世纪的C值
                valueC = C_20[termIndex];
            }
        } else if (century == 19) {
            //取20世纪的C值
            valueC = C_20[termIndex];
        } else {
            valueC = 0;
            //一般不会在这个范围内
            Log.i("getSongsList", "输入的年份前两位不在范围内");
        }
        //根据不同的情况获取L值
        if (termIndex >= 0 && termIndex <= 3) {
            //前四个节气L值要先减一，再除4
            valueL = (yearLastTwo - 1)/4;
        } else {
            valueL = yearLastTwo/4;
        }
        //一般性计算，得到节气对应的公历日（初步）
        int preDay = (int) ((yearLastTwo * D + valueC) - valueL);
//        Log.i("getSongsList", "preDay = " + preDay);
        //该节气对应的特殊情况，如2026-
        String spacialCase = SPACIAL_CASE[termIndex];
        //获取真正的日
        if (spacialCase.isEmpty()) {
            //无特殊情况
            day = preDay;
        } else {
            //有特殊情况，获取偏差年份及方向
            List<String> matchedOffsetList = getMatchedStr2List(OFFSET_REGEX, spacialCase);
            //匹配列表中第一个元素获取的真正日（还不一定是真正的日）
            int _day = getRealDay(matchedOffsetList.get(0), yearStr, preDay);
            if (matchedOffsetList.size() > 1) {
                //有两个特殊情况
                if (_day == preDay) {
                    //如果第一次没撞见特殊情况，那就再撞一次
                    day = getRealDay(matchedOffsetList.get(1), yearStr, preDay);
                } else {
                    //如果撞对了，那就停止
                    day = _day;
                }
            } else {
                //只有一个特殊情况
                day = _day;
            }
        }
        //获取公历中的月
        int month = TERM_MONTH[termIndex];
        return yearStr + "-" + addZero(month) + "-" + addZero(day);
    }

    /**
     * 计算某年某月第几个星期几的日期
     * @param num 第几个
     * @param wek 星期几
     * @return 标准日期，如：2001-11-09
     */
    public static String get5month5week5(int year, int month, int num, int wek) {
        String date = "";
        int count = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        //Calendar对象的月份常量都往前推了1（1月为0），为了适配，我们传入的月就要前推一个单位
        calendar.set(Calendar.MONTH, month - 1);
        //本月份的天数
        int dayNum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        //遍历当月的每一天，找到第几个星期几对应的日
        for (int i = 1; i <= dayNum; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            int weekWhat = getRealWeek5(calendar);
            if (weekWhat == wek) count++;
            if (count == num) {
                //相等就是找到了，日记录下来就跳出循环
                date = year + "-" + addZero(month) + "-" + addZero(i);
                break;
            }
        }
        return date;
    }

    /**
     * 因为Calendar对象的星期常量都往后推了1（星期天是1），故获取真正的周几
     */
    private static int getRealWeek5(Calendar calendar) {
        int weekWhat;
        int weekConstants = calendar.get(Calendar.DAY_OF_WEEK);
        if (weekConstants - 1 == 0) {
            //星期天
            weekWhat = 7;
        } else {
            weekWhat = weekConstants - 1;
        }
        return weekWhat;
    }

//    public static void main(String[] args) {
//        String dateStr = solarTerms2dateStr("2002", "立秋");
//        String motherDay = get5month5week5(2021, 5, 2, 7);
//        System.out.println(motherDay);
//    }

    /**
     * 对月份或日补零，以获取标准日期
     * @param monthOrDay 月份或日
     * @return 标准的月或日
     */
    public static String addZero(int monthOrDay) {
        String standard;
        if (String.valueOf(monthOrDay).length() == 1) {
            standard = "0" + monthOrDay;
        } else {
            standard = monthOrDay + "";
        }
        return standard;
    }

    /**
     * 获取节气在公历中对应的真正的日（考虑特殊情况）
     * @param matchedOffset 匹配的偏差年份及方向，如2026-
     * @param yearStr 同节气一同输入的年份字符串
     * @param preDay 经过一般性计算得到的初步日
     * @return 真正日
     */
    private static int getRealDay(String matchedOffset, String yearStr, int preDay) {
        int day;
        String yearSpacial = matchedOffset.substring(0, 4);
        boolean isAdd = matchedOffset.charAt(4) == '+';
        if (yearStr.equals(yearSpacial)) {
            //输入年刚好赶上了特殊情况
            if (isAdd) {
                day = preDay + 1;
            } else {
                day = preDay - 1;
            }
        } else {
            //输入年正常
            day = preDay;
        }
        return day;
    }

    /**
     * 以指定的正则取源文本中匹配，得到的多个匹配结果储存在列表中
     * @param regex 正则表达式
     * @param source 源文本
     * @return 存储匹配结果的列表
     */
    public static List<String> getMatchedStr2List(String regex, String source) {
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

}
