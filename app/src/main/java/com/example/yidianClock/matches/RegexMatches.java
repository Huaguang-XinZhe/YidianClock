package com.example.yidianClock.matches;


import com.example.yidianClock.time_conversions.MatchStandardization;
import com.example.yidianClock.utils.timeUtils.Age;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatches {
    /**
     * 匹配时间字符串
     */
    public static final String TIME_REGEX = "((\\d{2,4}|[零一二三四五六七八九]{2,4})[年\\./-])?" +
            "(((\\d{1,2}|十[一二]|闰?[一二三四五六七八九十正冬仲子腊])[月\\./-]" +
            "|大年)((\\d{1,2}|[一二三四五六七八九十]{1,3})[日号]?|[初廿三][一二三四五六七八九十])" +
            "|(立春|雨水|惊蛰|春分|谷雨|立夏|小满|芒种|夏至|小暑|大暑|立秋|处暑|白露|秋分|寒露|霜降|立冬|小雪|大雪|冬至|小寒|大寒" +
            "|春节|除夕|过年|中元节|七月半|鬼节|母亲节|父亲节|五一|劳动节|六一|儿童节|情人节|高考|(元旦|元宵|清明|端午|中秋|重阳|国庆|七夕|圣诞)节?))";
    /**
     * 匹配标签关键词
     */
    public static final String KEYWORD_REGEX = "的?生日|结婚|恋爱|相恋" +
            "|春节|除夕|过年|中元节|七月半|鬼节|母亲节|父亲节|五一|劳动节|六一|儿童节|情人节|高考|(元旦|元宵|清明|端午|中秋|重阳|国庆|七夕|圣诞)节?" +
            "|立春|雨水|惊蛰|春分|清明|谷雨|立夏|小满|芒种|夏至|小暑|大暑|立秋|处暑|白露|秋分|寒露|霜降|立冬|小雪|大雪|冬至|小寒|大寒";

    //老妈生日、结婚、""（高考、节气或节日）
    /**
     * 获取展示在列表item中的标题和头像上的标签
     * @param source 源文本
     * @param timeStr 时间源文本，不为空串
     * @param standardTime 标准化后的时间
     * @return 0：title，1：label
     */
    public static String[] getDisplay(String source, String timeStr, String standardTime) {
        String title;
        String label;
        //源文本去除时间字符串，可能为空串
        String surplus = source.replace(timeStr, "");
        String keyWord = MatchStandardization.getDeepMatchedStr(KEYWORD_REGEX, source);
        if (keyWord.contains("生日")) {
            title = surplus.replace(keyWord, "");
            label = "生日";
        } else if (isMatched("结婚|恋爱|相恋", keyWord)) {
            //计算几周年
            int num = Age.calculateRealYears(standardTime) + 1;
            title = keyWord + " " + num + " 周年";
            label = "纪念日";
        } else if (isMatched(MatchStandardization.FESTIVAL_REGEX, keyWord)) {
            title = source;
            if (keyWord.equals("高考")) {
                label = "倒计时";
            } else {
                label = "节日";
            }
        } else if (isMatched(MatchStandardization.TERM_REGEX, keyWord)) {
            title = source;
            if (keyWord.equals("清明")) {
                //大多数人不会关注清明这个节气，而只会关注这个节日
                label = "节日";
            } else {
                label = "节气";
            }
        }  else {
            title = surplus;
            label = "倒计时";
        }
        String[] displayArr = new String[2];
        displayArr[0] = title;
        displayArr[1] = label;
        return displayArr;
    }

    /**
     * 指定文本是否能用给定的正则匹配
     * @param regex 正则
     * @param text 要深入匹配的文本（只匹配一次）
     * @return true：能匹配，false：不能匹配
     */
    public static boolean isMatched(String regex, String text) {
        return !MatchStandardization.getDeepMatchedStr(regex, text).isEmpty();
    }

    /**
     * 获取第一个匹配到的字符串，匹配不到就返回空字符串
     */
    public static String getFirstMatchedStr(String regex, String source) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
//        //matcher.matches()是完全匹配，必须全部匹配才行。
//        //如匹配数字，源字符串全是数字matches才为true，有一个字符不为数字都不行。
//        if (matcher.matches()) {
//        } else return null;
        //总共有9个分组
//            Log.i("getSongsList", "groupCount = " + matcher.groupCount());
        List<String> matchedStrList = new ArrayList<>();
        while (matcher.find()) {
            matchedStrList.add(matcher.group());
        }
        if (!matchedStrList.isEmpty()) {
            return matchedStrList.get(0);
        } else return "";
        //返回的是二者的连接字符串，中间没有间隔（2014年4月1日2013年4月15日）
//        return builder.toString();

//        while (matcher.find()) {
        //我嘞个乖乖，matcher.find()调用一次就消耗一次，你都调用两次了还问自己为什么只提取到一个值？你也太逗了吧！
        //操！耗了老子几个小时在这种无关紧要的细节上！！！
//            System.out.println("find = " + matcher.find());
//            System.out.println(matcher.group());
//        }

    }

    /**
     * 更新源文本后，获取新的匹配
     * @return 有则返回，无返回null
     */
    public static String getNewMatchedStr(String oldSource, String newSource) {
        //如果不存在KeyWord则返回空字符串
        String oldKeyWord = getFirstMatchedStr(TIME_REGEX, oldSource);
        String newKeyWord = getFirstMatchedStr(TIME_REGEX, newSource);
        if (!newKeyWord.equals(oldKeyWord)) {
            return newKeyWord;
        } else {
            return null;
        }
    }

}