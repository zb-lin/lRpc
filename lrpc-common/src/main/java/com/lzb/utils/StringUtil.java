package com.lzb.utils;

/**
 * String 工具类
 */
public class StringUtil {

    public static boolean isBlank(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isNotBlank(String... strs) {
        for (String str : strs) {
            if (isBlank(str)) {
                return false;
            }
        }
        return true;
    }
}
