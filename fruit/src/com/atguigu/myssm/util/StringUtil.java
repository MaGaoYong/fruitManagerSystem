package com.atguigu.myssm.util;

/**
 * @author adventure
 * @create 2022-05-18 14:24
 */
public class StringUtil {

    //判断某个字符串是否为空
    public static boolean isEmpty(String str){
        return str==null || "".equals(str);
    }

    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }
}
