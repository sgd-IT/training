package com.training.day04;

/**
 * Day 4 - equals vs == 练习（面试高频题）
 *
 * ==    比较的是「引用」：两个变量是否指向同一个对象（地址）
 * equals 比较的是「内容」：两个对象的值是否一样
 * 基本类型（int 等）没有对象，== 直接比值
 */
public class EqualsExercises {

    /**
     * Q4: 判断两个字符串内容是否相同
     * 示例：
     *   isSameString("abc", "abc")              → true（字面量，同一个对象）
     *   isSameString(new String("abc"), "abc")  → true（内容一样，但不是同一个对象！）
     *   isSameString("abc", "abd")              → false
     *
     * 陷阱：这里不能用 ==，想想为什么（new 出来的和字面量不是同一个对象）
     */
    public static boolean isSameString(String a, String b) {
        //1.new出来的地址不一样，==比较的是地址
        return a.equals(b);// TODO: 伪代码先贴给教练，确认后再写
    }

    /**
     * Q5: 判断两个 int 是否相等
     * 示例：
     *   isSameValue(1, 1) → true
     *   isSameValue(1, 2) → false
     *
     * 对比 Q4：基本类型没有"对象"，== 直接比数值；String 是引用类型，== 比地址
     */
    public static boolean isSameValue(int a, int b) {
        return a == b;// TODO: 伪代码先贴给教练，确认后再写
    }
}
