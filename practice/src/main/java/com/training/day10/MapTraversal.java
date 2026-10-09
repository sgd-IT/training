package com.training.day10;

import java.util.Map;

/**
 * Day 10 - Q3: MapTraversal —— 遍历的三种方式，和遍历中删除
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 三种遍历
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   // ① 只要键
 *   for (String k : map.keySet()) { ... }
 *
 *   // ② 只要值
 *   for (Integer v : map.values()) { ... }
 *
 *   // ③ 键和值都要 —— 用 entrySet()
 *   for (Map.Entry&lt;String, Integer&gt; e : map.entrySet()) {
 *       String k = e.getKey();
 *       Integer v = e.getValue();
 *   }
 * </pre>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 为什么"键值都要"时必须用 entrySet
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   // ❌ 反面写法：拿到键，再回头查一遍值
 *   for (String k : map.keySet()) {
 *       Integer v = map.get(k);      // 每个键都多查一次哈希表
 *   }
 * </pre>
 * keySet() 遍历出来的每个 k，你都要再 get(k) 一次 ——
 * 而 entrySet() 里的 Map.Entry **本身就攥着值**，不用查。
 * <p>
 * 单条数据的差距很小，但这是**每次循环都要多付一遍**的开销，而且
 * 面试官非常爱问这一条。记住结论：**键值都要 → entrySet**。
 * <p>
 * ⚠️ 顺带一个边界：Map 里可以有 **null 值**（HashMap 允许）。
 * 所以 keySet() 版里 v 可能是 null —— 累加的时候会拆箱 NPE。
 * 测试数据里不会有 null，但你要知道有这个口子。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 遍历中删除：Day 9 的同一个坑，换了个容器
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   for (String k : map.keySet()) {
 *       if (...) map.remove(k);       // ❌ ConcurrentModificationException
 *   }
 * </pre>
 * 机制和 Day 9 的 List 一模一样：**迭代器内部的 modCount** 和
 * 容器的修改次数对不上，下一次 next() 就抛。
 * **单线程也会抛** —— 它说的"并发"是"两方同时在改"，不是"两个线程"。
 * <p>
 * 正确写法两种：
 * <pre>
 *   // ① 迭代器自己删（唯一能安全删的方式）
 *   Iterator&lt;Map.Entry&lt;String,Integer&gt;&gt; it = map.entrySet().iterator();
 *   while (it.hasNext()) {
 *       Map.Entry&lt;String,Integer&gt; e = it.next();
 *       if (e.getValue() &lt; limit) it.remove();
 *   }
 *
 *   // ② removeIf —— 一行，最省事
 *   map.entrySet().removeIf(e -&gt; e.getValue() &lt; limit);
 * </pre>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q3-1: sumByKeySet     遍历 keySet + get，求所有值的和
 *   TODO Q3-2: sumByEntrySet   遍历 entrySet，求所有值的和
 *   TODO Q3-3: removeBelowByIterator(map, limit)
 *              删掉所有 value &lt; limit 的条目，**用 Iterator.remove()**
 *   TODO Q3-4: removeBelowByRemoveIf(map, limit)
 *              同上，**用 removeIf**
 * <p>
 * ⚠️ Q3-3 / Q3-4 是**原地修改**（返回 void，直接改传进来的 map）。
 * ⚠️ 边界：value == limit 的条目要**留下**，不是删掉。
 * <p>
 * ⚠️ 先预测，再跑（写【输出值】）：
 * <pre>
 * // 预测：sumByEntrySet({a=1, b=2, c=3}) → ____
 * // 预测：sumByEntrySet({})               → ____
 * // 预测：removeBelowByIterator({a=1, b=5, c=5}, 5) 后 map = ____
 * </pre>
 */
public class MapTraversal {

    /**
     * 用 keySet() + get(k) 求和。
     * <p>
     * 这版是**故意的反面写法** —— 写它就是为了让你在 Q3-2 里对照出区别。
     * 累加时注意拆箱：值本身是 Integer。
     */
    public static int sumByKeySet(Map<String, Integer> map) {
        throw new UnsupportedOperationException("TODO Q3-1");
    }

    /** 用 entrySet() 求和。对照 Q3-1，哪一版更该出现在生产代码里？ */
    public static int sumByEntrySet(Map<String, Integer> map) {
        throw new UnsupportedOperationException("TODO Q3-2");
    }

    /**
     * 原地删除 value &lt; limit 的条目 —— **用迭代器**。
     * <p>
     * 形状就三步：拿迭代器 → while(it.hasNext()) →
     * 判断 e.getValue() 是否达标 → it.remove()。
     */
    public static void removeBelowByIterator(Map<String, Integer> map, int limit) {
        throw new UnsupportedOperationException("TODO Q3-3");
    }

    /** 原地删除 value &lt; limit 的条目 —— **用 removeIf**，一行。 */
    public static void removeBelowByRemoveIf(Map<String, Integer> map, int limit) {
        throw new UnsupportedOperationException("TODO Q3-4");
    }
}