package com.training.day04;

/**
 * Day 4 - Q6: static 练习 —— 计数器
 *
 * static 的两种用法：
 *   1. static 字段：属于「类」的变量，所有对象共享一份（不存在每个对象一份）
 *   2. static 方法：属于类的方法，不依赖具体对象，用 类名.方法名() 调用（像 Math.max）
 *
 * 要求：
 *   - private static int count = 0;（类级计数器）
 *   - 每次 new Counter() 时 count++（计数器 +1）
 *   - public static int getCount()：返回当前 count
 *   - public static void reset()：count 清零
 *
 * 测试会创建多个 Counter 对象，然后验证 count 是「所有对象共享」的
 */
public class Counter {

    // TODO: private static int count = 0;
    private static int count = 0;

    public Counter() {
        // TODO: 伪代码先贴给教练，确认后再写（构造时 count++）
        count++;
    }

    public static int getCount() {
        return count; // TODO: 伪代码先贴给教练，确认后再写
    }

    public static void reset() {
        // TODO: 伪代码先贴给教练，确认后再写（count = 0）
        count = 0;
    }
}
