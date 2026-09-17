package com.training.day02;

import java.util.HashMap;
import java.util.Map;

/**
 * Day 2 - Map 综合练习（比昨天 Q4 多走一步）
 *
 * 规则：先读测试，先写注释版伪代码，再实现。
 * 需要 import 的话自己加（IDEA 里 Alt+Enter 可自动导入）。
 */
public class MapExercises {

    /**
     * Q5: 找出字符串中出现次数最多的字符
     * 示例：
     *   mostFrequentChar("hello") → 'l'
     *   mostFrequentChar("aabbc") → 'a'（a 和 b 都出现 2 次，取先达到 2 次的那个，即 a）
     *   mostFrequentChar("x")     → 'x'
     *
     * 思路提示：
     *   第一步：遍历字符串的每个字符，用 Map<Character, Integer> 统计出现次数
     *           （怎么「取旧值 + 1 再放回去」？取到 null 怎么办？回忆昨天 Q4 你写的 containsKey 分支）
     *   第二步：边统计边记录「当前次数最多的字符」，只有严格大于才替换（用 > 而不是 >=）
     */
    public static char mostFrequentChar(String text) {
        // TODO: 先写伪代码注释，再写实现
        int maxCount = 0;
        char maxChar = ' ';
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (map.containsKey(c)) {
                map.put(c, map.get(c) + 1);
            } else {
                map.put(c, 1);
            }
            if (map.get(c) > maxCount) {
                maxCount = map.get(c);
                maxChar = c;
            }
        }
        return maxChar;
    }
}
