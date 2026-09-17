package com.training.day02;

/**
 * Day 2 - String 练习
 * <p>
 * 规则同 ArrayExercises：先读测试，先写注释版伪代码，再实现。
 */
public class StringExercises {

    /**
     * Q3: 用分隔符拼接字符串 —— 昨天尾随空格 bug 的正式版
     * 示例：
     * join(["a", "b", "c"], ",")  → "a,b,c"
     * join(["hello"], "-")        → "hello"
     * join([], ",")               → ""
     * <p>
     * 昨天的 trim() 是治标：trim 只能去掉空格，去不掉逗号。
     * 根源是「每个词后面都加了分隔符，包括最后一个」。
     * 正确思路：让第一个元素特殊，后面每个元素都是「分隔符 + 内容」。
     */
    public static String join(String[] words, String separator) {
        // TODO: 先写伪代码注释，再写实现
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i == 0) {
                sb.append(words[i]);
            } else {
                sb.append(separator);
                sb.append(words[i]);
            }
        }
        return sb.toString();
    }

    /**
     * Q4: 判断回文（忽略大小写，跳过空格）
     * 示例：
     * isPalindrome("racecar")                     → true
     * isPalindrome("Racecar")                     → true（忽略大小写）
     * isPalindrome("A man a plan a canal Panama") → true（跳过空格）
     * isPalindrome("hello")                       → false
     * <p>
     * 思路提示：两个指针一左一右往中间走，遇到空格就跳过，逐对比较（忽略大小写）
     */
    public static boolean isPalindrome(String text) {
        // TODO: 先写伪代码注释，再写实现
        int left = 0;
        int right = text.length() - 1;
        while (left < right) {
            // 如果左边是空格，左指针右移
            if (text.charAt(left) == ' ') {
                left++;
                continue;
            }
            // 如果右边是空格，右指针左移
            if (text.charAt(right) == ' ') {
                right--;
                continue;
            }
            // 逐对比较（忽略大小写）
            if (Character.toLowerCase(text.charAt(left)) != Character.toLowerCase(text.charAt(right))) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}
