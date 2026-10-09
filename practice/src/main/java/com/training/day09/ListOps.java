package com.training.day09;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Day 9 - Q2: ListOps —— 遍历中删除元素的四种写法（本日最实用的一题）
 * <p>
 * "边遍历边删除"是 Java 里最经典的坑之一。
 * 这一题的四个方法，**只有两个是对的**，另外两个各有各的死法。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 为什么 for-each 里 remove 会炸？
 * ═══════════════════════════════════════════════════════════
 * for (String s : list) { ... } 编译后其实是：
 * <p>
 * Iterator<String> it = list.iterator();
 * while (it.hasNext()) { String s = it.next(); ... }
 * <p>
 * 迭代器内部记着一个**修改次数计数器**（modCount）。
 * 你调 list.remove() 会改 modCount，但迭代器手里的那份没变。
 * 下一次 it.next() 发现"数字对不上" → 抛 ConcurrentModificationException。
 * <p>
 * ⚠️ 注意：这不是线程安全问题！**单线程也会抛**。
 * 它叫"并发"修改，指的是"两方同时在改"，不是"两个线程"。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q2-1: removeByForEach —— **故意写错的版本**，测试会断言它抛 CME
 *   TODO Q2-2: removeByIterator —— 用 Iterator 遍历，靠 it.remove() 删
 *   TODO Q2-3: removeByRemoveIf —— 一行搞定（提示：Collection 的 removeIf）
 *   TODO Q2-4: removeByIndex   —— 索引 for 循环。**它不抛异常，但会漏元素**
 *                                 这是四个里最危险的一个：不报错，结果却是错的
 * <p>
 * ⚠️ Q2-4 请**故意**写出那个"漏元素"的版本 ——
 * 测试会断言它删不干净，让这个坑在你眼前过一次。
 * （就像 Day 7 的 RawPoint 一样：把错误行为记录下来当标本）
 * <p>
 * ⚠️ 先预测，再跑（写输出值）：
 * // 预测：["a","a","a"] 走 Q2-4 之后剩下 → ____target =a  那输出为【‘’】
 * // 预测：["a","b","a","c"] 走 Q2-2 之后剩下 → ____["a","b","","c"]
 */
public class ListOps {

    /**
     * 实验 1：for-each 里直接 remove —— 故意写错
     */
    public static void removeByForEach(List<String> list, String target) {
        for (String s : list) {
            //cursor和size 1,2
            if (s.equals(target)) list.remove(s);
        }
    }

    /**
     * 实验 2：Iterator + it.remove() —— 正确
     */
    public static void removeByIterator(List<String> list, String target) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String s = it.next();
            if (s.equals(target)) {
                it.remove();
            }
        }
    }

    /**
     * 实验 3：removeIf —— 正确，且最简洁
     */
    public static void removeByRemoveIf(List<String> list, String target) {
        list.removeIf(s -> s.equals(target));
    }

    /**
     * 实验 4：索引 for 循环 —— 不抛异常，但会漏元素
     */
    public static void removeByIndex(List<String> list, String target) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(target)) {
                //remove有两个方法一个是object，int i
                list.remove(i);
            }
        }
    }
}