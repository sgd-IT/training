package com.training.day11;

/**
 * Day 11 - Q2: HashSpread —— 那个 {@code h ^ (h >>> 16)} 到底在救什么
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 先看一个会出事的数据
 * ═══════════════════════════════════════════════════════════
 * 假设有三个键，它们的 hashCode 是：
 * <pre>
 *   0x0000_0000
 *   0x0001_0000
 *   0x0002_0000
 * </pre>
 * 注意：**低 16 位全是 0**，差别全在高位。
 * <p>
 * 而 {@code indexFor} 算的是 {@code hash &amp; (capacity - 1)} ——
 * 容量 16 时，{@code capacity - 1 = 15 = 0b1111}，只取**最低 4 位**。
 * 三个数的低 4 位都是 0 → **全挤进 0 号桶**。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * JDK 8 的扰动函数（HashMap.hash）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   static final int hash(Object key) {
 *       int h;
 *       return (key == null) ? 0 : (h = key.hashCode()) ^ (h &gt;&gt;&gt; 16);
 *   }
 * </pre>
 * 一句话：**把高 16 位异或到低 16 位上**。
 * <p>
 * 因为下标只用到低位，低位的信息量不够 → 把高位"压"下来，让低位变得有区分度。
 * <pre>
 *   h = 0x0002_0000
 *   h &gt;&gt;&gt; 16      = 0x0000_0002
 *   h ^ (h &gt;&gt;&gt; 16) = 0x0002_0002      ← 低 4 位现在是 0b0010 = 2
 * </pre>
 * 三个键于是落到 0、1、2 号桶 —— 冲突被拆开了。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ⚠️ 别背结论，要能讲出这两句
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   · 为什么不直接用 hashCode() % capacity 就完事？
 *     → 因为 % 以前慢；且 JDK 已经用 &amp; 了，就必须靠扰动补低位信息
 *   · 为什么是 ^ 而不是 | 或 &amp;？
 *     → ^ 保留两边的差异信息；| 会污染（x|y 结果偏向 1），&amp; 会丢信息
 * </pre>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q2-1: <b>indexWithoutSpread</b>(hashCode, capacity)
 *              不扰动，直接取低位。一行
 *   TODO Q2-2: <b>disturb</b>(hashCode)
 *              JDK 8 的扰动：h ^ (h &gt;&gt;&gt; 16)。一行
 *   TODO Q2-3: <b>countUsedBuckets</b>(hashCodes, capacity, spread)
 *              返回"这些 hashCode 一共落到几个不同的桶里"。
 *              这是**度量冲突程度**的指标：桶越少 = 冲突越重
 * <p>
 * ⚠️ 先预测，再跑（写【输出值】，别写"应该会变好"）：
 * <pre>
 * // 预测：hashCodes = {0x0000_0000, 0x0001_0000, 0x0002_0000, 0x0003_0000}, capacity = 16
 * //       不扰动时用到了几个桶？ → ____
 * //       扰动后用到了几个桶？   → ____
 * </pre>
 * ⚠️ countUsedBuckets 别用 Set&lt;Integer&gt; 就交差 —— 用 Set 是可以，
 * 但你要能回答：**这里用 Set 统计，和 Map 内部的"桶"是同一件事吗？**
 * （提示：Set 内部也是一个桶数组，而且 HashSet 就是拿 HashMap 实现的 —— 明天 Day 13 讲）
 */
public class HashSpread {

    /**
     * 不扰动的下标：直接取低位。
     * <p>
     * 一行。动手前先想：capacity 是 2 的幂，取低位的位运算是什么？
     */
    static int indexWithoutSpread(int hashCode, int capacity) {
        throw new UnsupportedOperationException("TODO Q2-1");
    }

    /**
     * JDK 8 的扰动函数。一行。
     * <p>
     * <pre>
     *   h ^ (h &gt;&gt;&gt; 16)
     * </pre>
     * 注意 {@code >>>} 是**无符号**右移 —— 负数时它补 0，而 {@code >>} 补 1。
     * hashCode 完全可能是负数，这里用 {@code >>} 就错了。
     */
    static int disturb(int hashCode) {
        throw new UnsupportedOperationException("TODO Q2-2");
    }

    /**
     * 统计这一批 hashCode 落到了几个不同的桶里。
     *
     * @param hashCodes 一批 hashCode
     * @param capacity  桶数组容量（2 的幂）
     * @param spread    true 表示先扰动再算下标；false 表示不扰动
     * @return 用到的不同桶的数量。设 n = hashCodes.length，
     *         返回值最小 1（全挤在一个桶），最大 min(n, capacity)（全散开）
     */
    static int countUsedBuckets(int[] hashCodes, int capacity, boolean spread) {
        throw new UnsupportedOperationException("TODO Q2-3");
    }
}