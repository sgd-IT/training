package com.training.day13;

import java.util.List;

/**
 * Day 13 - Q1~Q3: SetLab —— Set 三家 + JDK 21 的 Sequenced Collections
 * <p>
 * ═══════════════════════════════════════════════════════════
 * Set 只有一个承诺：不重复。三家各有各的"顺序观"
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   HashSet        无顺序        add/get 都是 O(1)        ← 内部就是 HashMap 的 key
 *   LinkedHashSet  插入顺序      O(1)                     ← 链表串起插入次序
 *   TreeSet        排序顺序      O(log n)                 ← 红黑树
 * </pre>
 * 一句话：**要快用 HashSet，要稳定顺序用 LinkedHashSet，要排序用 TreeSet。**
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ★ 今天最大的坑：TreeSet 判重不看 equals，看 compareTo
 * ═══════════════════════════════════════════════════════════
 * HashSet 靠 `hashCode` + `equals` 判重（Day 7 的契约）。
 * <b>TreeSet 一个大法不靠它们</b> —— 它只看比较的结果：
 * <pre>
 *   compare(a, b) == 0   →   TreeSet 认为 a 和 b 是【同一个元素】，第二个 add 被丢弃
 * </pre>
 * 所以如果比较器只比较"长度"：
 * <pre>
 *   TreeSet&lt;String&gt; t = new TreeSet&lt;&gt;(Comparator.comparingInt(String::length));
 *   t.add("ab"); t.add("cd");      // → 只剩一个！因为长度都是 2，compare 返回 0
 * </pre>
 * 这是**不报错的丢数据**。测试里有一个专门展示它（天然绿，只读）。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ☕ JDK 21 Sequenced Collections（JEP 431）
 * ═══════════════════════════════════════════════════════════
 * JDK 21 之前，"拿到第一个/最后一个"每个集合写法都不一样：
 * <pre>
 *   List      →  list.get(0)  /  list.get(list.size() - 1)
 *   Deque     →  deque.getFirst()  /  deque.getLast()
 *   SortedSet →  set.first()  /  set.last()
 * </pre>
 * JDK 21 统一成一套接口：
 * <pre>
 *   SequencedCollection  ← List / Deque / LinkedHashSet / SortedSet 都实现了它
 *       getFirst()   getLast()   reversed()   addFirst()   addLast()
 *   SequencedMap        ← LinkedHashMap / TreeMap / ConcurrentSkipListMap
 *       firstEntry()  lastEntry()  pollFirstEntry()  reversed()
 * </pre>
 * 三个要点：
 * <pre>
 *   · reversed() 返回的是【视图】，不是拷贝 —— 改原集合，视图跟着变
 *   · 空集合上 getFirst() / getLast() 抛 NoSuchElementException
 *   · 这是接口层的统一，底层实现没变，所以**没有性能代价**
 * </pre>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q1: dedupePreservingOrder  去重 + **保持第一次出现的顺序**
 *   TODO Q2: dedupeSortedByLength   去重 + 按长度升序，同长度按字典序
 *   TODO Q3-1: first                列表第一个（用 JDK 21 的 SequencedCollection）
 *   TODO Q3-2: last                 列表最后一个
 *   TODO Q3-3: reversedList         反转后的【视图】
 * <p>
 * ⚠️ 先预测，再跑（写【输出值】）：
 * <pre>
 * // 预测：dedupePreservingOrder(["b","a","b","c","a"]) → ____
 * // 预测：dedupeSortedByLength(["bb","a","cc","b","aaa"]) → ____
 * // 预测：reversedList(List.of("a","b","c")).get(0) → ____
 * </pre>
 */
public class SetLab {

    /**
     * 去重，并**保持第一次出现的顺序**。
     * <p>
     * 题目只给一句话，选哪种 Set 是你的事 —— 但选错会丢顺序。
     * 返回 {@code List}，因为 {@code LinkedHashSet} 的遍历顺序已经是你要的。
     */
    public static List<String> dedupePreservingOrder(List<String> words) {
        throw new UnsupportedOperationException("TODO Q1");
    }

    /**
     * 去重，按**长度升序**排；长度相同则按**字典序**。
     * <p>
     * 输入 {@code ["bb","a","cc","b","aaa"]} → {@code ["a","b","bb","cc","aaa"]}
     * <p>
     * ⚠️ 比较器必须构成**全序**（长度相同时还要比内容）。
     * 只比长度的话，{@code "a"} 和 {@code "b"} 会被当成同一个元素 —— 见类注释里那个坑。
     */
    public static List<String> dedupeSortedByLength(List<String> words) {
        throw new UnsupportedOperationException("TODO Q2");
    }

    /**
     * 返回列表的第一个元素。用 JDK 21 的 {@code SequencedCollection} 方法，一行。
     */
    public static String first(List<String> words) {
        throw new UnsupportedOperationException("TODO Q3-1");
    }

    /** 返回列表的最后一个元素。一行。 */
    public static String last(List<String> words) {
        throw new UnsupportedOperationException("TODO Q3-2");
    }

    /**
     * 返回反转后的**视图**（不是拷贝）。
     * <p>
     * 测试会验证它是视图：改原列表，再取一次 {@code reversedList}，结果要跟着变。
     */
    public static List<String> reversedList(List<String> words) {
        throw new UnsupportedOperationException("TODO Q3-3");
    }
}