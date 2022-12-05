package com.example.yidianClock.matches;


import android.util.Log;

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
    public static final String TIME_REGEX = "((\\d{2,4}|[去今前明]|[零一二三四五六七八九]{2,4})[年\\./-])?" +
            "(((\\d{1,2}|十[一二]|闰?[一二三四五六七八九十正冬仲子腊])[月\\./-]" +
            "|大年)((\\d{1,2}|[一二三四五六七八九十]{1,3})[日号]?|[初廿三][一二三四五六七八九十])" +
            "|(立春|雨水|惊蛰|春分|谷雨|立夏|小满|芒种|夏至|小暑|大暑|立秋|处暑|白露|秋分|寒露|霜降|立冬|小雪|大雪|冬至|小寒|大寒" +
            "|春节|除夕|过年|中元节|七月半|鬼节|母亲节|父亲节|五一|劳动节|六一|儿童节|情人节|高考|(元旦|元宵|清明|端午|中秋|重阳|国庆|七夕|圣诞)节?))";
    /**
     * 匹配标签关键词
     */
    public static final String KEYWORD_REGEX = "的?生日|结的?婚|恋爱|相恋" +
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
        String keyWord = chooseKeyWordFromList(getMatchedList(KEYWORD_REGEX, source));
        Log.i("getSongsList", "keyWord = " + keyWord);
        if (!surplus.isEmpty()) {
            if (keyWord.contains("生日")) {
                title = surplus.replace(keyWord, "");
                label = "生日";
            } else if (isMatched("结的?婚|恋爱|相恋", keyWord)) {
                //计算几周年
                int num = Age.calculateRealYears(standardTime) + 1;
                if (keyWord.contains("婚")) {
                    title = "结婚" + " " + num + " 周年";
                } else {
                    title = "恋爱" + " " + num + " 周年";
                }
                label = "纪念日";
            } else {
                title = surplus;
                label = "倒计时";
            }
        } else {
            if (isMatched(MatchStandardization.FESTIVAL_REGEX, keyWord)) {
                title = source;
                if (keyWord.equals("高考")) {
                    label = "倒计时";
                } else {
                    label = "节日";
                }
                // if (isMatched(MatchStandardization.TERM_REGEX, keyWord))
                //除了节日，只可能是节气了
            } else {
                title = source;
                if (keyWord.equals("清明")) {
                    //大多数人不会关注清明这个节气，而只会关注这个节日
                    label = "节日";
                } else {
                    label = "节气";
                }
            }
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
     * 从指定列表中挑选优先关键词，没有就返回第一个元素，列表为空就返回空串
     */
    private static String chooseKeyWordFromList(List<String> list) {
        String keyWord = "";
        if (list.isEmpty()) {
            return keyWord;
        }
        keyWord = list.get(0);
        //优先级关键词列表
        String[] priorityKWArr = new String[] {
                "生日", "结的婚", "结婚", "恋爱", "相恋"
        };
        if (list.size() > 1) {
            //遍历优先级列表
            for (String priorityKW : priorityKWArr) {
                if (list.contains(priorityKW)) {
                    keyWord = priorityKW;
                    break;
                }
            }
        }
        return keyWord;
    }

    /**
     * 获取匹配字符串组成的List，该list有可能为空（一个都没匹配到）
     */
    public static List<String> getMatchedList(String regex, String source) {
        List<String> matchedStrList = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            matchedStrList.add(matcher.group());
        }
        return matchedStrList;

    }

    /**
     * 更新源文本后，获取新的匹配
     * @return 有则返回，无返回null
     */
    public static String getNewMatchedStr(String oldSource, String newSource) {
        //如果不存在KeyWord则返回空字符串
        String oldKeyWord = MatchStandardization.getDeepMatchedStr(TIME_REGEX, oldSource);
        String newKeyWord = MatchStandardization.getDeepMatchedStr(TIME_REGEX, newSource);
        if (!newKeyWord.equals(oldKeyWord)) {
            return newKeyWord;
        } else {
            return null;
        }
    }

}