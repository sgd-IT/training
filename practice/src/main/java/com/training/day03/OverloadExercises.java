package com.training.day03;

/**
 * Day 3 - 方法重载练习
 *
 * 重载 = 同一个类里，方法名相同、参数列表不同（类型或个数不同）。
 * 调用时 Java 根据实参自动选择匹配的方法。
 */
public class OverloadExercises {

    /**
     * Q3: 三个重载的 max
     *   max(2, 5)     → 5
     *   max(2, 5, 9)  → 9
     *   max(2.5, 1.8) → 2.5
     *
     * 要求：三个参数版不要重复写比较逻辑——
     *       想想怎么用已经写好的两个参数版拼出来
     */
    public static int max(int a, int b) {
        // TODO: 先写伪代码注释，再写实现
        return a > b ? a : b;
    }

    public static int max(int a, int b, int c) {
        // TODO: 先写伪代码注释，再写实现
        return Math.max(max(a, b), c);
    }

    public static double max(double a, double b) {
        // TODO: 先写伪代码注释，再写实现
        return Math.max(a, b);
    }

    /**
     * Q4: 重载的 area
     *   area(2.0)  → 圆的面积 πr² ≈ 12.566（一个参数 = 圆）
     *   area(3, 4) → 矩形的面积 12 = 长 × 宽（两个参数 = 矩形）
     *
     * 提示：π 用 Math.PI（Math 类，自己查一下怎么用）
     */
    public static double area(double r) {
        // TODO: 先写伪代码注释，再写实现
        return Math.PI * r * r;
    }

    public static double area(double w, double h) {
        // TODO: 先写伪代码注释，再写实现
        return w * h;
    }
}
