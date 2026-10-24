package com.training.day10;

import java.util.Map;

/**
 * Day 10 - Q4: MapNulls —— get 返回 null 到底是什么意思
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 这一题要解决的唯一问题
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   Integer v = map.get("a");
 *   // v == null 有两种可能：
 *   //   ① "a" 这个键根本不存在
 *   //   ② "a" 存在，但它的值就是 null
 * </pre>
 * get 一个返回值分不清这两件事。这在真实项目里是**真会出事**的：
 * 缓存里存了个 null 表示"查过了，没有"，和"没查过"完全是两个意思。
 * <p>
 * 想分清，只有一条路 —— **再用 containsKey 问一次**（这才是
 * containsKey 的唯一正当用途）。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 五个 API，今天的重点
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   map.containsKey(k)                  → boolean，键在不在
 *   map.getOrDefault(k, dflt)           → 键不存在时给个默认值（不做写入）
 *   map.putIfAbsent(k, v)               → 键不在才放；**返回旧的（被挡住的）值**，没有则 null
 *   map.merge(k, v, remap)              → 键不在 → put(k, v)
 *                                         键存在 → put(k, remap(旧, v))
 *   map.computeIfPresent(k, remap)      → 键**不在 → 什么都不做**
 *                                         键存在 → put(k, remap(旧, v))
 * </pre>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ★ 今天最值得记住的一条：merge vs computeIfPresent
 * ═══════════════════════════════════════════════════════════
 * 两个方法的 remap 参数长得一模一样，**区别全在"键不存在"时**：
 * <pre>
 *   map = {}
 *   map.merge("a", 5, Integer::sum);            // → {a=5}   ★ 插进去了
 *   map.computeIfPresent("a", (k,v) -&gt; v + 5);  // → {}      ★ 什么都没发生
 * </pre>
 * 一句话：**merge 会"无中生有"，computeIfPresent 不会。**
 * 用错了，一个是"新用户第一次计了个 1"，另一个是"新用户根本没被统计" ——
 * 都是不报错的 bug。
 * <p>
 * putIfAbsent 的返回值也常被忽略：它返回的是**被挡住的旧值**
 * （成功放进去时返回 null）。所以它既能"放"，又能"读旧值"，一箭双雕 ——
 * 这也是它比 containsKey + put 好的地方。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q4-1: readOrMinusOne(map, key)
 *              键**不存在** → 返回 -1
 *              键存在       → 返回它的值（值是 0 也要原样返回 0）
 *              → 必须用 containsKey 区分，不许用 get()==null 判断
 * <p>
 *   TODO Q4-2: readOrDefault(map, key, defaultValue)  → getOrDefault 一行
 * <p>
 *   TODO Q4-3: putIfAbsentReturnsOld(map, key, value) → 返回 putIfAbsent 的返回值
 * <p>
 *   TODO Q4-4: increaseByMerge(map, key, delta)        → merge
 * <p>
 *   TODO Q4-5: increaseOnlyIfPresent(map, key, delta)  → computeIfPresent
 * <p>
 * ⚠️ 先预测，再跑（写【输出值】—— 这两题是本日的分水岭）：
 * <pre>
 * // 预测：increaseByMerge(new HashMap&lt;&gt;(), "a", 5)        → map = ____
 * // 预测：increaseOnlyIfPresent(new HashMap&lt;&gt;(), "a", 5)  → map = ____
 * // 预测：{a=1} 上 increaseByMerge(map, "a", 5)            → map = ____
 * // 预测：{a=1} 上 increaseOnlyIfPresent(map, "a", 5)      → map = ____
 * </pre>
 * <p>
 * 注意 Q4-4 / Q4-5 都是返回 void、原地改 map（和 Q3-3 / Q3-4 一样）。
 */
public class MapNulls {

    /**
     * 区分"键不存在"和"值是 0"。
     * <p>
     * 关键在于：map.get(key) == null 判不出这两者，只有 containsKey 可以。
     * 测试里有个用例是 {a=0}，正确答案是 0 而不是 -1 ——
     * 用 get()==null 的写法会在这里露馅。
     */
    public static int readOrMinusOne(Map<String, Integer> map, String key) {
        if (!map.containsKey(key)) {
            return -1;
        }
        return map.get(key);
    }

    /** 键不存在时返回默认值。一行。 */
    public static int readOrDefault(Map<String, Integer> map, String key, int defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    /**
     * 键不存在才放入 value。
     *
     * @return putIfAbsent 的返回值 —— 被挡住的旧值；成功放入时是 null
     */
    public static Integer putIfAbsentReturnsOld(Map<String, Integer> map, String key, int value) {
        return map.putIfAbsent(key, value);
    }

    /**
     * 增加计数：键不存在就放入 delta，存在就加上去。
     * <p>
     * 这是"新用户也要被统计进来"的语义。
     */
    public static void increaseByMerge(Map<String, Integer> map, String key, int delta) {
        map.merge(key, delta, Integer::sum);
    }

    /**
     * 只在键**已经存在**时增加计数；键不存在时什么都不做。
     * <p>
     * 这是"只处理老用户"的语义 —— 和 Q4-4 只差一个词，结果完全不同。
     */
    public static void increaseOnlyIfPresent(Map<String, Integer> map, String key, int delta) {
        map.computeIfPresent(key, (k, v) -> v + delta);
    }
}