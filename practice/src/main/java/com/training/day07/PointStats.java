package com.training.day07;

import java.util.*;

/**
 * Day 7 - Q3: PointStats —— 让 equals / hashCode 真正派上用场（你来补全）
 *
 * 这题回答一个问题：**我为什么要费劲重写 equals 和 hashCode？**
 * 让我们通过这题，真正体会到 equals / hashCode 的威力。
 *
 * ┌───────────────────────────────────────────────────────────┐
 * │ 方法 1：dedupe —— 去重                                    │
 * └───────────────────────────────────────────────────────────┘
 *   输入一个 List<Point>，返回**去重后**的 List，并且**保持第一次出现的顺序**。
 *
 *   例：  [(1,2), (3,4), (1,2), (5,6)]
 *      →  [(1,2), (3,4), (5,6)]
 *
 *   提示：HashSet 天生就去重（靠的就是 equals + hashCode）
 *        但 HashSet 不保证顺序 —— 想想用哪个集合类既能去重又能保序？
 *        （提示：类名里带 Linked）
 *
 * ┌───────────────────────────────────────────────────────────┐
 * │ 方法 2：countOccurrences —— 统计每个点出现几次            │
 * └───────────────────────────────────────────────────────────┘
 *   输入一个 List<Point>，返回 Map<Point, Integer>，
 *   key 是点，value 是它出现的次数。
 *
 *   例：  [(1,2), (3,4), (1,2)]
 *      →  { (1,2)=2, (3,4)=1 }
 *
 *   提示：用 HashMap。遍历 list，对每个 point：
 *         用 map.getOrDefault(point, 0) 取出当前次数，加 1，再 put 回去
 *         （getOrDefault 是 Map 的常用方法，比 if (containsKey) 干净得多）
 *
 * ┌───────────────────────────────────────────────────────────┐
 * │ ⚡ 这题的意义                                              │
 * └───────────────────────────────────────────────────────────┘
 *   HashMap / HashSet 是靠 **hashCode 找桶、再用 equals 确认** 来工作的。
 *   如果 Point 没重写这两个方法，上面两个功能**全都失效**：
 *     - dedupe 一个都去不掉
 *     - countOccurrences 会变成"每个对象各算一次"
 *
 *   跑 DedupeTest 你会看到 ——  对照一下 RawPointTest 里同样的操作用 RawPoint 是什么结果。
 *
 * ⚠️ 两轮推演（写在注释里再动手）：
 *     dedupe 推演： 输入 [(1,2),(3,4),(1,2)]，写出每一轮循环后 LinkedHashSet 里有什么
 *     count 推演：  同上，写出每一轮后 map 里有什么
 */
public class PointStats {

    /** 去重，并保持第一次出现的顺序 */
    public static List<Point> dedupe(List<Point> points) {
        //先用 LinkedHashSet 去重 他又可以去重又可以保持顺序
        LinkedHashSet<Point> set = new LinkedHashSet<>();
        //遍历取出元素
        for (Point point : points){
            set.add(point);
        }
        return set.stream().toList();
    }

    /** 统计每个 Point 出现的次数 */
    public static Map<Point, Integer> countOccurrences(List<Point> points) {
        //先用 HashMap 统计
        //创建一个 HashMap
        Map<Point, Integer> map = new HashMap<>();
        //遍历取出元素
        for (Point point : points){
            map.put(point, map.getOrDefault(point, 0) + 1);
        }
        return map;
    }
}