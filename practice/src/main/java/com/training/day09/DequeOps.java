package com.training.day09;

import java.util.List;

/**
 * Day 9 - Q4: DequeOps —— LinkedList 到底该用来干什么
 *
 * ═══════════════════════════════════════════════════════════
 * 先破一个常见的误解
 * ═══════════════════════════════════════════════════════════
 *   很多人以为："LinkedList 插入删除快，所以频繁增删就用它。"
 *   **这基本上是错的。**
 *
 *   · LinkedList 的 get(i) 是 **O(n)** —— 它要从头一个一个数过去（没有数组下标）
 *   · ArrayList 的 get(i) 是 **O(1)** —— 直接算地址
 *   · 就算"中间插入"，LinkedList 也得先走到那个位置，**还是 O(n)**
 *   · 而且 ArrayList 的数组在内存里是**连续的**，CPU 缓存命中率高得多
 *
 *   → **实际项目里 99% 的场景用 ArrayList。**
 *
 * ═══════════════════════════════════════════════════════════
 * 那 LinkedList 什么时候该用？
 * ═══════════════════════════════════════════════════════════
 *   当它**不是当 List 用，而是当 Deque（双端队列）用**的时候。
 *   LinkedList 实现了 Deque 接口，头尾进出都是 O(1)：
 *
 *     Deque<String> d = new LinkedList<>();
 *     d.push("a");     // 加到头部（= addFirst）
 *     d.pop();         // 从头部取走（= removeFirst）
 *     d.offer("a");    // 加到尾部（= addLast）
 *     d.poll();        // 从头部取走（= removeFirst）
 *
 *   同一个 LinkedList：push/pop 是**栈**（后进先出），offer/poll 是**队列**（先进先出）。
 *
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q4-1: stackOrder(List<String> input)
 *              把 input 依次 push 进 LinkedList，再反复 pop，
 *              把**取出的顺序**收集成一个 List 返回
 *
 *   TODO Q4-2: queueOrder(List<String> input)
 *              同上，但用 offer / poll
 *
 * ⚠️ 先预测，再跑（写输出值）：
 *     // 预测：stackOrder(["a","b","c"]) → ____
 *     // 预测：queueOrder(["a","b","c"]) → ____
 */
public class DequeOps {

    /** 用 LinkedList 当栈（后进先出），返回出栈顺序 */
    public static List<String> stackOrder(List<String> input) {
        // TODO Q4-1
        throw new UnsupportedOperationException("TODO Q4-1");
    }

    /** 用 LinkedList 当队列（先进先出），返回出队顺序 */
    public static List<String> queueOrder(List<String> input) {
        // TODO Q4-2
        throw new UnsupportedOperationException("TODO Q4-2");
    }
}