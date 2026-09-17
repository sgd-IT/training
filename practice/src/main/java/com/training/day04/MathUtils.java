package com.training.day04;

/**
 * Day 4 - Q7: static 方法 + 可变参数（varargs）
 *
 * static 方法 = 不 new 对象，直接 MathUtils.sum(...) 调用（和 Math.max 一个用法）
 * 可变参数 int... numbers = 可以传任意个数（0 个也行），方法内部当数组用
 */
public class MathUtils {

    /**
     * 求和：
     *   sum(1, 2, 3) → 6
     *   sum(5)       → 5
     *   sum()        → 0
     *
     * 思路提示：for-each 累加（可变参数在方法里就是个 int[]）
     */
    public static int sum(int... numbers) {
        //先遍历数组
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return sum; // TODO: 伪代码先贴给教练，确认后再写
    }
}
