package com.example.yidianClock;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KeyWord {
//    //传入的源文本是时刻在变动的（传入的始终是最新值）
//    String sourceStr;
    //关键词列表相对稳定
    List<String> keyWords;
//
//
//    public KeyWord(String sourceStr, List<String> keyWords) {
//        this.sourceStr = sourceStr;
//        this.keyWords = keyWords;
//    }


    public KeyWord(List<String> keyWords) {
        this.keyWords = keyWords;
    }

    /**
     * 从源文本中获取关键词的末索引
     * @param source 源文本
     * @param keyWord 关键词
     */
    public static int getEnd(String source, String keyWord) {
        int end = 0;
        Map<Integer, List<Integer>> listMap = new HashMap<>();
        //单索引（源文本中只有一个索引）对应的Key，即KeyWord中的某个字符对应的索引，也是listMap中的某个Key
        List<Integer> singleIndexKeyList = new ArrayList<>();
        for (int i = 0; i < keyWord.length(); i++) {
            String charStr = keyWord.substring(i);
            List<Integer> indexList = getCharIndexList(charStr, source);
            assert indexList != null;
            listMap.put(i, indexList);
            if (indexList.size() == 1) {
                singleIndexKeyList.add(i);
            }
        }
        if (!singleIndexKeyList.isEmpty()) {
            //取第一个单索引（keyWord中某字符对应的索引）
            int singleIndexKey = singleIndexKeyList.get(0);
            //进而取其对应的value中的单索引（源文本中该字符对应的索引，这里的该字符指的是keyWord中某字符）
            int singleIndex = Objects.requireNonNull(listMap.get(singleIndexKey)).get(0);
            //该索引与KeyWord末索引的差值
            int indexDiff = keyWord.length() - 1 - singleIndexKey;
            //进而得到KeyWord末字符在源文本中对应的索引
            end = singleIndex + indexDiff;
        } else {
            //KeyWord中每一个字符在源文本中都有多个索引
            //以listMap中第一个元素为基准，遍历（从keyWord的第一个字符开始）
            List<Integer> firstIndexList = listMap.get(0);
            assert firstIndexList != null;
            if (keyWord.length() > 1) {
                for (int firstIndex : firstIndexList) {
                    List<Integer> secondIndexList = listMap.get(1);
                    //由于KeyWord在源文本中的索引是连续的，所以，如果KeyWord第一个字符在源文本中的索引是正确答案，
                    // 那么在下一个字符列表（KeyWord下一个字符在源文本中的索引列表）中也就一定能找到+1后的索引
                    assert secondIndexList != null;
                    boolean isFind = secondIndexList.contains(firstIndex + 1);
                    //只可能找得到一次，所以，下面的代码也只会执行一次
                    if (isFind) {
                        end = firstIndex + keyWord.length() - 1;
                    }
                }
            } else {
                //keyWord只有一个字符，那就将第一个索引（也是最后一个索引）作为最终索引传出
                end = firstIndexList.get(0);
            }
        }
        return end;
    }

    /**
     * 从源文本中获取指定单字符串的索引列表
     * @param charStr 指定单字符串
     * @param source 源文本
     * @return 有的话返回索引列表，无返回null
     */
    private static List<Integer> getCharIndexList(String charStr, String source) {
        if (source.contains(charStr)) {
            char[] sourceCharArr = source.toCharArray();
            char c = charStr.charAt(0);
            List<Integer> charIndexList = new ArrayList<>();
            //数组无法直接获取索引，只能使用这种循环方式
            for (int i = 0; i < sourceCharArr.length; i++) {
                if (sourceCharArr[i] == c) {
                    charIndexList.add(i);
                }
            }
            return charIndexList;
        } else {
            return null;
        }
    }

    /**
     * 源文本中是否含有关键词列表中的关键词，有就返回其中最长的那一个，没有就返回空字符串
     * @param sourceStr 源文本，时刻变动，适合放在参数位置传入
     */
    public String getKeyWord(String sourceStr) {
        //源文本包含关键词列表
        List<String> containsKeyWordList = new ArrayList<>();
        for (String keyWord : keyWords) {
            boolean isContains = sourceStr.contains(keyWord);
            if (isContains) {
                containsKeyWordList.add(keyWord);
            }
        }
        if (!containsKeyWordList.isEmpty()) {
            return getLongestKeyWord(containsKeyWordList);
        } else {
            return "";
        }
    }

    /**
     * 获取源文本包含关键词列表中最长的那一个
     * @param containsKeyWordList 源文本包含关键词列表
     */
    public String getLongestKeyWord(List<String> containsKeyWordList) {
        //刚开始的时候默认第一个就是最长的
        String longest = containsKeyWordList.get(0);;
        for (int i = 0; i < containsKeyWordList.size(); i++) {
            String next;
            //为防止数值超界
            if (i+1 < containsKeyWordList.size()) {
                next = containsKeyWordList.get(i+1);
            } else {
                next = containsKeyWordList.get(i);
            }
            longest = getLongest(longest, next);
        }
        return longest;
    }

    /**
     * 两个字符串，返回其中长度最长的
     */
    private String getLongest(String one, String two) {
        if (one.length() >= two.length()) {
            return one;
        } else {
            return two;
        }
    }

    /**
     * 更新源文本后，获取新关键词
     * @return 有则返回，无返回null
     */
    public String getNewKeyWord(String oldSource, String newSource) {
        //如果不存在KeyWord则返回空字符串
        String oldKeyWord = getKeyWord(oldSource);
        String newKeyWord = getKeyWord(newSource);
        if (!newKeyWord.equals(oldKeyWord)) {
            return newKeyWord;
        } else {
            return null;
        }
    }

}
