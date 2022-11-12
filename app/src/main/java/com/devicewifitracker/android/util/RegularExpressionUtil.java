package com.devicewifitracker.android.util;

import com.blankj.utilcode.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/*
正则表达式
 */
public class RegularExpressionUtil {
    /**
     * 去掉txt 文件 中每行行号
     * @param content
     * @return
     */
    public  static String reuglar(String content) {
      String[] strArr =   content.split("\n");
     Object[] objects =   Arrays.stream(strArr).map(line -> {
            return line.replaceAll("^(\\d)+()","").trim();
        }).toArray();
        Arrays.asList(objects);
        return listToString(Arrays.asList(objects)) ;
//        return (String) Arrays.asList(objects).stream().collect(Collectors.joining(","));
//        return String.join("~",Arrays.asList(objects)); │ map ==[00-18-F3	ASUSTek COMPUTER INC.]
    }

    /**
     * 集合转字符串
     * @param list
     * @return
     */
    public static String listToString(List list) {
        String result = null;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i));
        }
        if (stringBuilder != null && stringBuilder.length() > 0) {
            result =  list.isEmpty() ? "" : stringBuilder.toString().substring(0, stringBuilder.length() - 1);
        }

        return result;
    }
}
