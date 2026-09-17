package com.training.day04;

/**
 * Day 4 - String 常用 API 练习
 * <p>
 * 规则：
 * 1. 先读测试，理解期望
 * 2. 【新规矩】每题先把注释版伪代码贴给教练，确认后再写实现
 * 3. 不查 AI，只查 API 文档（查到的 API 必须自己敲一遍）
 */
public class StringApiExercises {

    /**
     * Q1: 忽略大小写判断前缀
     * 示例：
     * startsWithIgnoreCase("HelloWorld", "hello") → true
     * startsWithIgnoreCase("HelloWorld", "world") → false
     * startsWithIgnoreCase("", "")               → true
     * <p>
     * 思路提示：把 text 和 prefix 都转成小写，再调用 startsWith（两个 API 自己查）
     */
    public static boolean startsWithIgnoreCase(String text, String prefix) {
        //先把两个参数都变成小写
        //然后再进行开头的对比
        text = text.toLowerCase();
        prefix = prefix.toLowerCase();
        if (text.startsWith(prefix)) {
            return true;
        }
        return false; // TODO: 伪代码先贴给教练，确认后再写
    }

    /**
     * Q2: 取名字的首字母缩写（真实场景：头像、工牌）
     * 示例：
     * initials("Zhang San")  → "ZS"
     * initials("tom")        → "T"
     * initials("  Li  Si ")  → "LS"（名字里可能有多个空格）
     * initials("")           → ""
     * <p>
     * 思路提示：昨天 Day 1 用过 split("\\s+")；每个词取 charAt(0) 转大写，拼起来。
     * 注意空字符串边界：trim 之后是空 → 直接返回 ""
     */
    public static String initials(String name) {
        //1.校验接收参数是否为空
        //2.用空格分割每个单词
        //3.创建一个空字符串接收每一个单词的首字母
        if (name.isEmpty()) {
            return "";
        }
        String initName = "";
        String[] words = name.split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                initName += word.toUpperCase().charAt(0);
            }
        }
        return initName; // TODO: 伪代码先贴给教练，确认后再写
    }

    /**
     * Q3: 手机号脱敏（真实业务场景：列表页隐藏手机号）
     * 示例：
     * maskPhone("13812345678") → "*******5678"（前 7 位变 *，保留后 4 位）
     * maskPhone("12345")       → "12345"（长度不足 8 位不脱敏，原样返回）
     * maskPhone("")            → ""
     * <p>
     * 思路提示：substring + repeat 两个 API 自己查。
     * 后 4 位：下标从 length()-4 开始；前面补 *（repeat 几个？）
     */
    public static String maskPhone(String phone) {
        //1.校验各种情况
        //2.将前7位变为*
        if (phone.length() < 8) {
            return phone;
        }
        if (phone.isEmpty()) {
            return "";
        }
        String s = "*".repeat(phone.length() - 4) + phone.substring(phone.length()-4);
        return s; // TODO: 伪代码先贴给教练，确认后再写
    }
}
