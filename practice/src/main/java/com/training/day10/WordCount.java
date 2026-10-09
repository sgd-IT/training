package com.training.day10;

import java.util.List;
import java.util.Map;

/**
 * Day 10 - Q1: WordCount —— 同一件事，三种写法
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 这是什么练习
 * ═══════════════════════════════════════════════════════════
 * 统计词频。Map 最经典的入门题，也是**最能看出手生不生**的一题：
 * 脑子里如果只有一种写法，这段代码就会写得很长。
 * <p>
 * 今天同一个功能写三遍，一遍换一个 API。目的不是"会用三个 API"，
 * 而是让你亲眼看到：**同一件事的代码量，从 5 行变成 1 行。**
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 写法一：containsKey + put（"先问再放"）
 * ═══════════════════════════════════════════════════════════
 * 最啰嗦，但最好懂 —— 面试写不出来时先写这个，能跑就对：
 * <pre>
 *   if (map.containsKey(word)) {
 *       map.put(word, map.get(word) + 1);   // get 被调了两次
 *   } else {
 *       map.put(word, 1);
 *   }
 * </pre>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 写法二：getOrDefault + put（把 if-else 压成一行）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   map.put(word, map.getOrDefault(word, 0) + 1);
 * </pre>
 * 逐个类型看清楚，这里**没有**空指针风险：
 * · key 不存在 → getOrDefault 返回 0（int 字面量，自动装箱成 Integer）
 * · 0 + 1 = 1 → 装箱回 Integer → put
 * <p>
 * 我们算的是 "0 + 1"，**从来不是**拿 null 去加 1。这一点想不通的人很多。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 写法三：merge（一句搞定"没有就放、有就合并"）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   map.merge(word, 1, Integer::sum);
 * </pre>
 * merge(key, value, remap) 的语义，逐字读：
 * · 键**不存在** → 直接 put(key, value)，**remap 根本不会被调用**
 * · 键**已存在** → put(key, remap(旧值, value))
 * <p>
 * 注意：键不存在时放进去的是 value（这里是 1），**不是** remap 的结果。
 * <p>
 * Integer::sum 是**方法引用**（Day 16 细讲），今天先把它当成"把两个数加起来"。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q1-1: countWithContainsKey   —— 写法一
 *   TODO Q1-2: countWithGetOrDefault  —— 写法二
 *   TODO Q1-3: countWithMerge         —— 写法三
 * <p>
 * 三个方法**返回值必须一模一样**。测试会拿同一个输入逐一对比。
 * 只写对其中一个，另外两个照样红。
 * <p>
 * ⚠️ 先预测，再跑（写【输出值】，不是写实现）：
 * <pre>
 * // 预测：countWithContainsKey(["a","b","a","c","a","b"]) → ____
 * // 预测：countWithMerge([])                              → ____
 * // 预测：countWithGetOrDefault(["x"])                    → ____
 * </pre>
 * ⚠️ 别去查 merge 的源码 —— 上面已经写清楚它的语义了。
 *    忘了签名可以查（铁律 2 允许）；没学过就搜出来抄，是另一回事。
 */
public class WordCount {

    /**
     * 写法一：先 containsKey 问一句，再决定 put 什么。
     * <p>
     * 提示：注意 map.get(word) 在 if 分支里被调用了 ——
     * 而 containsKey 刚刚已经查过一遍了。这就是多余的"模板代码"。
     */
    public static Map<String, Integer> countWithContainsKey(List<String> words) {
        throw new UnsupportedOperationException("TODO Q1-1");
    }

    /**
     * 写法二：一行 put + getOrDefault。
     * <p>
     * 想清楚默认值该填什么 —— 填 0 还是填 null，决定了这段代码会不会 NPE。
     */
    public static Map<String, Integer> countWithGetOrDefault(List<String> words) {
        throw new UnsupportedOperationException("TODO Q1-2");
    }

    /**
     * 写法三：一行 merge。
     * <p>
     * 第三个参数是 remap（重映射函数），想想该方法引用应该是什么。
     */
    public static Map<String, Integer> countWithMerge(List<String> words) {
        throw new UnsupportedOperationException("TODO Q1-3");
    }
}