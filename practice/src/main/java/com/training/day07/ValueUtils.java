package com.training.day07;

import java.util.Objects;

/**
 * Day 7 - Q4: ValueUtils —— java.util.Objects 工具箱（你来补全）
 *
 * Objects 是 JDK 7 加进来的工具类，专门解决"到处要判空"的痛点。
 * 以后你写 equals / hashCode 会天天用到它。
 *
 * ┌───────────────────────────────────────────────────────────┐
 * │ 方法 1：isSame(Object a, Object b) —— null 安全的相等比较 │
 * └───────────────────────────────────────────────────────────┘
 *   要求：a 和 b 都为 null  → true
 *        只有一个是 null    → false
 *        两个都不是 null    → 按内容比较
 *
 *   一行就能写完：return Objects.equals(a, b);
 *   （想想：如果自己写 a.equals(b)，a 为 null 时会怎样？）
 *
 * ┌───────────────────────────────────────────────────────────┐
 * │ 方法 2：describe(Object o) —— null 安全的字符串表示       │
 * └───────────────────────────────────────────────────────────┘
 *   要求：o 为 null → 返回字符串 "null"
 *        否则      → 返回 o.toString()
 *
 *   一行：return String.valueOf(o);
 *   （对比：o.toString() 在 o 为 null 时抛 NPE；String.valueOf 不会）
 *
 * ┌───────────────────────────────────────────────────────────┐
 * │ 方法 3：hashOf(Object... values) —— 组合多个值的哈希     │
 * └───────────────────────────────────────────────────────────┘
 *   要求：返回这些值组合起来的哈希值
 *   一行：return Objects.hash(values);
 *   （Objects.hash 自己就是可变参数，把 values 数组直接传进去即可 —— 复习 Day 4 varargs）
 *
 * ⚠️ 三题都很短，但请把"为什么需要它"想清楚 —— 检查时我会问：
 *    1. Objects.equals 和 a.equals(b) 有什么本质区别？
 *    2. String.valueOf(null) 返回什么？o.toString() 在 o 为 null 时呢？
 */
public class ValueUtils {

    /** null 安全的相等比较 */
    public static boolean isSame(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
    /** null 安全的字符串表示 */
    public static String describe(Object o) {
        if (o == null) return "null";
        return o.toString();
    }

    /** 组合多个值的哈希 */
    public static int hashOf(Object... values) {
        return Objects.hash(values);
    }
}