package com.training.day09;

import java.util.List;

/**
 * Day 9 - Q3: ListSources —— 一个 List 是怎么造出来的，决定了它能不能改
 *
 * 你已经踩过一次：Day 8 的 `new ShapeBook(List.of(...))` 里，
 * `List.of()` 造出来的列表是**不能改**的。这一题把三种来源讲透。
 *
 * ═══════════════════════════════════════════════════════════
 * 三种来源，三种脾气
 * ═══════════════════════════════════════════════════════════
 *
 *  ① new ArrayList<>()             → 长度可变 ✅，内容可变 ✅
 *  ② Arrays.asList(...)            → 长度**固定** ❌（add/remove 抛异常），
 *                                    但 set(i, x) **可以** ✅
 *                                    ⚠️ 而且它是**视图**：改它 = 改原数组！
 *  ③ List.of(...)                  → **完全不可改** ❌（set 也不行）
 *                                    但它是 null 安全的、不可变的，适合当常量
 *
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q3-1: mutableOf(String... values)
 *              返回一个**能 add 的** ArrayList
 *              提示：new ArrayList<>(Arrays.asList(values))
 *
 *   TODO Q3-2: immutableOf(String... values)
 *              返回一个**不能改的**列表（用 List.of）
 *
 *   TODO Q3-3: setThroughView(String[] array, int index, String value)
 *              通过 Arrays.asList(array) 改写指定位置 —— 测试会检查**原数组**被改了
 *              （这就是"视图"的含义：它不复制，它只是套在数组外面的一层壳）
 *
 * ⚠️ 先预测，再跑（写输出值）：
 *     // 预测：List.of("a","b").add("c")  → ____（抛什么？还是成功？）
 *     // 预测：Arrays.asList("a","b").set(0,"x") → ____
 */
public class ListSources {

    /** 返回一个能改的列表 */
    public static List<String> mutableOf(String... values) {
        // TODO Q3-1
        throw new UnsupportedOperationException("TODO Q3-1");
    }

    /** 返回一个不能改的列表 */
    public static List<String> immutableOf(String... values) {
        // TODO Q3-2
        throw new UnsupportedOperationException("TODO Q3-2");
    }

    /** 通过 Arrays.asList 造出来的"视图"，改写原数组的指定位置 */
    public static void setThroughView(String[] array, int index, String value) {
        // TODO Q3-3
        throw new UnsupportedOperationException("TODO Q3-3");
    }
}