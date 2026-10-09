package com.training.day10;

import java.util.List;
import java.util.Map;

/**
 * Day 10 - Q2: Grouping —— 手写"分组"这件事，两遍
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 分组长什么形状
 * ═══════════════════════════════════════════════════════════
 * 分组的结果永远是 **Map&lt;K, List&lt;V&gt;&gt;** —— 一个键对着一"桶"元素。
 * 这个形状认不出来，分组代码就写不下去。今天把长度当键：
 * <pre>
 *   ["a", "bb", "c", "dd", "eee"]
 *        ↓ 按长度分组
 *   { 1: ["a", "c"],  2: ["bb", "dd"],  3: ["eee"] }
 * </pre>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 写法一：手写 for + get（4 行模板代码）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   List&lt;String&gt; bucket = map.get(len);
 *   if (bucket == null) {
 *       bucket = new ArrayList&lt;&gt;();
 *       map.put(len, bucket);
 *   }
 *   bucket.add(word);
 * </pre>
 * 这 4 行是**纯粹的模板**：每一次"往 Map&lt;K,List&gt; 里塞东西"都要重写一遍。
 * 你以后在项目里会见到它几百次。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 写法二：computeIfAbsent（1 行）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   map.computeIfAbsent(len, k -&gt; new ArrayList&lt;&gt;()).add(word);
 * </pre>
 * computeIfAbsent(key, fn) 的语义：
 * · 键不存在（或值为 null）→ 调用 fn 算出一个值放进 map，并**返回这个新值**
 * · 键已存在 → **直接返回现有值，fn 一次都不会被调用**
 * <p>
 * 上面那行之所以能直接 .add(word)，就是因为 computeIfAbsent **把桶返回出来了**。
 * 返回值是这行的关键，不是"顺带"的。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ⚠️ 一个必须知道的陷阱（Part C 会问）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   map.getOrDefault(len, new ArrayList&lt;&gt;()).add(word);   // ❌ 静默丢数据
 * </pre>
 * 键不存在时，getOrDefault 会 new 一个**临时** ArrayList 给你，
 * 你往里面 add 得很开心 —— 然后这个临时的桶**没有被放回 map**，
 * 函数一返回就被 GC 掉了。代码不报错，结果就是少数据。
 * <p>
 * 同理 map.getOrDefault(len, List.of()).add(word) 会抛
 * UnsupportedOperationException（List.of 不可变，Day 9 Q3 刚讲过）。
 * —— 一个报错，一个不报错，**不报错的那个更危险**。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q2-1: groupByLengthWithLoop             —— 手写 for + get 版
 *   TODO Q2-2: groupByLengthWithComputeIfAbsent  —— computeIfAbsent 版
 * <p>
 * 两个方法返回值必须一模一样。要求：
 * · 键 = 单词长度（Integer）
 * · 桶内**保持输入顺序**（["a","c"] 不是 ["c","a"]）
 * · 空输入 → **空 Map**，不是 null
 * <p>
 * ⚠️ 先预测，再跑（写【输出值】）：
 * <pre>
 * // 预测：groupByLengthWithLoop(["a","bb","c"]) → ____
 * // 预测：groupByLengthWithComputeIfAbsent([])  → ____
 * </pre>
 */
public class Grouping {

    /**
     * 写法一：手写 for + get / put。
     * <p>
     * 这一版**故意不给你用** computeIfAbsent —— 手写那 4 行模板。
     * 写完之后回头看一眼：这几行里，有哪一个字是真正跟"长度"有关的？
     */
    public static Map<Integer, List<String>> groupByLengthWithLoop(List<String> words) {
        throw new UnsupportedOperationException("TODO Q2-1");
    }

    /**
     * 写法二：computeIfAbsent 一行。
     * <p>
     * 想清楚：lambda 什么时候会被执行？执行几次？
     */
    public static Map<Integer, List<String>> groupByLengthWithComputeIfAbsent(List<String> words) {
        throw new UnsupportedOperationException("TODO Q2-2");
    }
}