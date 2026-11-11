package com.training.day18;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Day 18 - Q1~Q4: CollectorsLab —— Stream II：**归集、分类、分区、以及什么时候别用流**
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ★ 必读：今天开工前先把这一整段读完（考点全在这里）
 * ═══════════════════════════════════════════════════════════
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 一、★ groupingBy = 分类 + 「下游收集器」
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   groupingBy(分类函数)                         → Map&lt;K, List&lt;T&gt;&gt;   默认每个桶是 List
 *   groupingBy(分类函数, counting())              → Map&lt;K, Long&gt;      每个桶只留个数（Long，不是 Integer）
 *   groupingBy(分类函数, mapping(f, toList()))    → 桶里放 f 的结果
 *   groupingBy(分类函数, TreeMap::new, toList())  → 换掉 Map 的实现（这样才能有序）
 * </pre>
 * 第二个参数叫**下游收集器**：它决定「一个桶里的东西怎么收」。不给 = 收成 `List`。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 二、★★ partitioningBy 与 groupingBy 不是一回事
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   partitioningBy(条件)  → Map&lt;Boolean, List&lt;T&gt;&gt;，**两个 key 永远都在**
 *   groupingBy(条件)      → Map&lt;Boolean, List&lt;T&gt;&gt;，只出现实际存在的 key
 * </pre>
 * 关键差别：输入是空列表时，`partitioningBy` 还是给你 `{false=[], true=[]}` ——
 * **两个 key 都在**；而 `groupingBy` 给你 `{}`。
 * 所以「用 true/false 分两堆」这件事，正确的是 `partitioningBy`。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 三、★★ toMap 的两个坑（真实项目里踩一次记一辈子）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   坑 1：key 重复 → IllegalStateException: Duplicate key
 *         （想合并就传第三个参数 (a, b) -&gt; b）
 *   坑 2：value 为 null → NullPointerException
 *         （toMap 内部对 value 做了 requireNonNull，装不进 null）
 * </pre>
 * `toMap` 不像 `groupingBy` 那样「多个值塞一个桶」，它是**一对一**的，
 * 撞了就当代码有 bug，直接抛。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 四、★ Map 的类型与顺序
 * ═══════════════════════════════════════════════════════════
 * `groupingBy` 默认给你 **`HashMap`** —— 遍历顺序不保证，别指望「按 key 升序」。
 * 要顺序，只有两条路：
 * <pre>
 *   groupingBy(f, TreeMap::new, toList())   // 归集时就指定 TreeMap
 *   new TreeMap&lt;&gt;(result)                   // 收完再转
 * </pre>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 五、★ 什么时候**不该**用流（面试常问，也是真实取舍）
 * ═══════════════════════════════════════════════════════════
 * <table border="1">
 *   <caption>流 vs 循环</caption>
 *   <tr><th></th><th>流</th><th>循环</th></tr>
 *   <tr><td>可读性</td><td>「做什么」一眼看得出</td><td>「怎么做」要读细节</td></tr>
 *   <tr><td>性能</td><td>有装箱 / 管道开销</td><td>基本类型循环最快</td></tr>
 *   <tr><td>提前退出 / 改外部状态</td><td>别扭</td><td>自然（`break` / 累加）</td></tr>
 * </table>
 * 一句话：**简单累加、需要 `break`、要改循环外的变量 —— 用循环；筛选、转换、分组 —— 用流。**
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q1: countByLength       按字符串长度分组，数每组的个数（Map&lt;Integer, Long&gt;）
 *   TODO Q2: splitEvenOdd        按偶/奇分两堆（Map&lt;Boolean, List&lt;Integer&gt;&gt;，空输入也要两个 key）
 *   TODO Q3: nameToLength        名字 → 长度（Map&lt;String, Integer&gt;，重复名字要抛）
 *   TODO Q4: groupByLengthSorted 按长度分组，并且 key 必须升序
 * <p>
 * ⚠️ 先预测，再跑（写【输出值】）：
 * <pre>
 * // 预测：splitEvenOdd(List.of()) 返回的 Map.size() = 2
 * // 预测：nameToLength(["aa","bb","aa"]) 会 → aa=2, bb=2
 * // 预测：countByLength(["a","bb","cc"]).get(1) 的【值和类型】= 2, Integer
 * // 预测：groupByLengthSorted(["ddd","a","cc"]) 的 keySet 顺序 = [1, 2, 3]
 * </pre>
 */
public class CollectorsLab {

    // ═══════════════════════════════════════════════════════════
    // TODO Q1
    // ═══════════════════════════════════════════════════════════
    /**
     * 按字符串**长度**分组，返回「长度 → 这种长度有几个」。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code ["a","bb","cc","ddd"]} → {@code {1=1, 2=2, 3=1}}</li>
     *   <li>空输入 → 空 Map</li>
     * </ul>
     * <p>
     * ⚠️ 返回值是 {@code Map<Integer, Long>} —— 注意是 **Long**。
     * 下游收集器用错了（比如又收成 List 再去 size），类型就对不上。
     */
    public static Map<Integer, Long> countByLength(List<String> words) {
        return words.stream()
                .collect(Collectors.groupingBy(String::length, Collectors.counting()));
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q2
    // ═══════════════════════════════════════════════════════════
    /**
     * 把整数分成两堆：偶数放 {@code true}，奇数放 {@code false}。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code [1,2,3,4]} → {@code {false=[1,3], true=[2,4]}}</li>
     *   <li>**空输入也要有两个 key**：{@code size() == 2}，两边都是空 List</li>
     * </ul>
     * <p>
     * ⚠️ 这条是今天的核心陷阱：用 {@code groupingBy} 写，第二条会红 ——
     * 空输入时它只返回一个空 Map，一个 key 都没有。
     */
    public static Map<Boolean, List<Integer>> splitEvenOdd(List<Integer> numbers) {
        return  numbers.stream()
                .collect(Collectors.partitioningBy( n -> n%2==0));
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q3
    // ═══════════════════════════════════════════════════════════
    /**
     * 名字 → 名字长度。**名字不能重复**。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code ["aa","bbb"]} → {@code {aa=2, bbb=3}}</li>
     *   <li>出现重复名字时抛 {@code IllegalStateException}，消息里含 {@code Duplicate key}</li>
     * </ul>
     * <p>
     * ⚠️ 不许自己写 `if (map.containsKey(...)) throw ...` 来凑消息 ——
     * 这个异常是 {@code Collectors.toMap} **自己**抛的，你要做的是**别拦它**。
     */
    public static Map<String, Integer> nameToLength(List<String> names) {
       return names.stream()
                .collect(Collectors.toMap( a -> a, String::length));
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q4
    // ═══════════════════════════════════════════════════════════
    /**
     * 按长度分组，但 {@code keySet()} 必须**按 key 升序**。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code ["ddd","a","cc"]} → {@code {1=[a], 2=[cc], 3=[ddd]}}</li>
     *   <li>把它们放进 {@code new ArrayList<>(result.keySet())}，必须等于 {@code [1,2,3]}</li>
     * </ul>
     * <p>
     * ⚠️ 默认的 `groupingBy` 给 HashMap，顺序不保证 —— 你信它就红。
     */
    public static Map<Integer, List<String>> groupByLengthSorted(List<String> words) {
        return  words.stream()
                .collect(Collectors.groupingBy(String::length, java.util.TreeMap::new, Collectors.toList()));
    }
}
