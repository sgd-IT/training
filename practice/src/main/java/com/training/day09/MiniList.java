package com.training.day09;

import java.util.Arrays;

/**
 * Day 9 - Q1: MiniList —— ★ 手写迷你 ArrayList（Phase 2 的第一个"手写底层"练习）
 *
 * 读完这一题，你就永远不会再觉得 ArrayList 是个"魔法盒子"了。
 *
 * ═══════════════════════════════════════════════════════════
 * 核心事实：数组的长度是固定的
 * ═══════════════════════════════════════════════════════════
 *   new Object[4] 之后，它永远只能装 4 个。没有"变长数组"这回事。
 *
 *   ArrayList 的"可变长"是**假象** —— 它在背后做了两件事：
 *     · size     ：真实元素个数（对外暴露的 size()）
 *     · capacity ：底层数组的长度（对外看不见）
 *   装满了就 new 一个更大的数组，把旧的复制过去。这叫**扩容**。
 *
 *   ⚠️ size 和 capacity 是两个不同的东西 —— 这是本日最核心的区分。
 *
 * ═══════════════════════════════════════════════════════════
 * 设计：懒初始化（和 HashMap 的 table 一样）
 * ═══════════════════════════════════════════════════════════
 *   一开始**不分配数组**（data == null），第一次 add 时才 new Object[DEFAULT_CAPACITY]。
 *   这样 new ArrayList<>() 不浪费内存 —— 现实中大量 list 建出来就是空的。
 *
 *   构造器**已经写好了**（就是什么都不做），先读懂它为什么要这样。
 *
 * ═══════════════════════════════════════════════════════════
 * 为什么用 Object[] 而不是 E[]？
 * ═══════════════════════════════════════════════════════════
 *   Java **不允许** new E[]（泛型数组）。原因是类型擦除 —— Day 14 会正式讲。
 *   所以 JDK 自己也是这么干的：内部是 Object[]，取出来时强转成 E。
 *
 *   get() 里的 (E) data[index] 会有"unchecked"警告，加 @SuppressWarnings("unchecked") 压掉。
 *
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q1-1: capacity() —— 返回底层数组长度；还没分配时返回 0
 *   TODO Q1-2: add(E)     —— 追加；数组满了要**翻倍扩容**
 *   TODO Q1-3: get(int)   —— 越界抛 IndexOutOfBoundsException
 *   TODO Q1-4: toString() —— "[a, b, c]"；空列表 "[]
 *
 * ⚠️ Q1-3 的边界检查**必须放在使用数组之前** ——
 *    和 Day 8 `largest` 那个坑是同一个形状（先用了再检查 = 没检查）。
 *
 * ⚠️ Q1-4 的分隔符又是 Day 6 那个坑 —— 用你 Day 6 第 4 版的写法（判 sb.length() > 0）。
 *
 * ⚠️ 先预测，再跑（第 5 条硬规矩）—— 把**输出值**写进注释，不是写怎么算：
 *     // 预测：new MiniList<String>() 的 size / capacity → ____ / ____
 *     // 预测：连续 add 5 个之后 capacity → ____
 */
public class MiniList<E> {

    /** 第一次 add 时分配的容量 */
    private static final int DEFAULT_CAPACITY = 4;

    /** 底层数组。懒初始化 —— 第一次 add 之前一直是 null */
    private Object[] data;

    /** 真实元素个数（不是数组长度！） */
    private int size;

    public MiniList() {
        // 懒初始化：这里什么都不用做。
        // data 默认是 null，size 默认是 0 —— 正是我们要的初始状态。
    }

    /** 真实元素个数 */
    public int size() {
        return size;
    }

    // TODO Q1-1: 返回底层数组的长度；还没分配数组时返回 0
    public int capacity() {
        throw new UnsupportedOperationException("TODO Q1-1");
    }

    // TODO Q1-2: 追加到末尾；数组满了要翻倍扩容
    //   提示：扩容只能用 Arrays.copyOf(data, newCapacity) —— 数组本身改不了长度
    public void add(E value) {
        throw new UnsupportedOperationException("TODO Q1-2");
    }

    // TODO Q1-3: 按下标取元素；越界抛 IndexOutOfBoundsException
    @SuppressWarnings("unchecked")
    public E get(int index) {
        throw new UnsupportedOperationException("TODO Q1-3");
    }

    // TODO Q1-4: toString() → "[a, b, c]"；空列表 → "[]"
    @Override
    public String toString() {
        throw new UnsupportedOperationException("TODO Q1-4");
    }
}