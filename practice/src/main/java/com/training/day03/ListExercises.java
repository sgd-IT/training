package com.training.day03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Day 3 - ArrayList 练习
 * <p>
 * 规则：
 * 1. 先读对应的测试类，弄清每个方法的期望
 * 2. 动手前，先写「注释版伪代码」（中文，一行一个步骤）
 * 3. 不查 AI，只查 API 文档（查到的 API 必须自己敲一遍）
 */
public class ListExercises {

    /**
     * Q1: 数组去重，返回 ArrayList
     * 要求：保持第一次出现的顺序
     * 示例：[1, 2, 2, 3, 1] → [1, 2, 3]
     * <p>
     * 思路提示：
     * 遍历 nums，如果结果列表里「还没有」这个数，就 add 进去
     * （API 提示：List 怎么判断"包含"？自己查）
     * 注意：int 放进去会自动装箱成 Integer，contains 比较的是值（equals）不是地址
     */
    public static List<Integer> dedupe(int[] nums) {
        // TODO: 先写伪代码注释，再写实现
        List<Integer> result = new ArrayList<>();
        for (int num : nums) {
            if (!result.contains(num)) {
                result.add(num);
            }
        }
        return result;
    }

    /**
     * Q2: 原地反转 List —— 昨天的双指针搬到 List 上
     * 要求：不新建列表，只交换元素，返回同一个 list 对象
     * 示例：[1, 2, 3, 4] → [4, 3, 2, 1]
     * <p>
     * 思路提示：和数组版几乎一样——只是读写用 list.get(i) / list.set(i, value)
     */
    public static List<Integer> reverse(List<Integer> list) {
        // TODO: 先写伪代码注释，再写实现
        for (int i = 0; i < list.size()/2; i++) {
            int temp = list.get(i);
            list.set(i, list.get(list.size() - 1 - i));
            list.set(list.size() - 1 - i, temp);
        }
        return list;
    }

    /**
     * Q5: 找出字符串中出现超过一次的字符，按第一次出现的顺序返回
     * 示例：
     * findDuplicates("hello")  → ['l']
     * findDuplicates("aabbc")  → ['a', 'b']
     * findDuplicates("abc")    → []（空列表）
     * <p>
     * 思路提示：这是昨天 Q5 的升级版：
     * 第一步：Map 统计每个字符出现次数（昨天写过的套路，直接复用）
     * 第二步：再遍历一次字符串，把「次数 > 1 且还没收集过」的字符 add 进结果
     * （怎么避免重复收集？用 contains 判断）
     */
    public static List<Character> findDuplicates(String text) {
        // TODO: 先写伪代码注释，再写实现
        Map<Character, Integer> charCount = new HashMap<>();
        int count = 0;
        for (char c : text.toCharArray()) {
            if (charCount.containsKey(c)) {
                count = charCount.get(c);
                charCount.put(c, count + 1);
            } else {
                charCount.put(c, 1);
            }
        }
        List<Character> result = new ArrayList<>();
        for (char c : text.toCharArray()) {
            if (charCount.get(c) > 1 && !result.contains(c)) {
                result.add(c);
            }
        }
        return result;
    }
}
