package com.example.yidianClock.matches;


import android.util.Log;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatches {
    public static final String TIME_REGEX = "((\\d{2,4}|[零一二三四五六七八九]{2,4})[年/-])?" +
            "(((\\d{1,2}|十[一二]|[零一二三四五六七八九十正冬仲子腊])[月/-]|闰[一二三四五六七八九])" +
            "((\\d{1,2}|[一二三四五六七八九十]{1,3})[日号]?|[初廿三][一二三四五六七八九十])" +
            "|(立春|雨水|惊蛰|春分|清明|谷雨|立夏|小满|芒种|夏至|小暑|大暑|立秋|处暑|白露|秋分|寒露|霜降|立冬|小雪|大雪|冬至|小寒|大寒" +
            "|春节|除夕|中元节|七月半|母亲节|父亲节|五一|劳动节|六一|儿童节|高考|(元旦|元宵|清明|端午|中秋|重阳|国庆|七夕|圣诞)节?))";
    public static final String KEYWORD_REGEX = "生日|结婚|恋爱";


    /**
     * 获取展示在列表item中的标题
     * @param source 源文本
     * @return 如果匹配到了就返回，一定有值，不会为null。
     * 匹配到时间和关键词的处理后返回，匹配到时间没匹配到关键词的返回说明，什么都没匹配到的返回空字符串
     */
    public static String getDisplayTitle(String source) {
        String displayStr;
        String matchedTimeStr = getFirstMatchedStr(TIME_REGEX, source);
        if (!matchedTimeStr.isEmpty()) {
            //如：老妈生日、结婚、""（高考、节气或节日）
            String surplus = source.replace(matchedTimeStr, "");
            String keyWord = getFirstMatchedStr(KEYWORD_REGEX, surplus);
            Log.i("getSongsList", "keyWord = " + keyWord);
            switch (keyWord) {
                case "生日":
                    displayStr = surplus.replace(keyWord, "");
                    // TODO: 2022/11/23 匹配图标、计算生肖、星座（交给外边来实现）
                    break;
                case "结婚":
                case "恋爱":
                    // TODO: 2022/11/23 有待计算结婚或恋爱几周年
                    displayStr = keyWord + " " + "几" + " 周年";
                    break;
                case "":
                    //高考、节气或节日
                    displayStr = matchedTimeStr;
                    break;
                default:
                    displayStr = "匹配到了时间，但是没匹配到关键词";

            }
        } else {
            //时间都没匹配到就展示空字符串
            displayStr = "";
        }
        Log.i("getSongsList", "displayStr = " + displayStr);
        return displayStr;
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