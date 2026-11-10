package com.training.day18;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 18 测试 —— 契约都在断言消息里，先读再动手。
 * <p>
 * 8 个随 TODO 变绿；最后 5 个是**天然绿**（不用写实现，考概念）。
 */
class CollectorsLabTest {

    // ═══════════════════════════════════════════════════════════
    // Q1 · groupingBy + 下游收集器
    // ═══════════════════════════════════════════════════════════

    @Test
    void countsHowManyWordsPerLength() {
        Map<Integer, Long> m = CollectorsLab.countByLength(List.of("a", "bb", "cc", "ddd"));
        assertEquals(1L, m.get(1).longValue(), "长度为 1 的只有一个");
        assertEquals(2L, m.get(2).longValue(), "bb、cc 两个");
        assertEquals(1L, m.get(3).longValue());
    }

    @Test
    void countByLengthOnEmptyInputIsEmptyMap() {
        assertTrue(CollectorsLab.countByLength(List.of()).isEmpty());
    }

    // ═══════════════════════════════════════════════════════════
    // Q2 · partitioningBy
    // ═══════════════════════════════════════════════════════════

    @Test
    void splitsEvenAndOdd() {
        Map<Boolean, List<Integer>> m = CollectorsLab.splitEvenOdd(List.of(1, 2, 3, 4));
        assertEquals(List.of(2, 4), m.get(true), "偶数在 true 那一堆");
        assertEquals(List.of(1, 3), m.get(false), "奇数在 false 那一堆");
    }

    @Test
    void bothKeysExistEvenWhenInputIsEmpty() {
        Map<Boolean, List<Integer>> m = CollectorsLab.splitEvenOdd(List.of());
        assertEquals(2, m.size(),
                "partitioningBy 的两个 key 永远都在 —— 用 groupingBy 这里会是 0");
        assertTrue(m.get(true).isEmpty());
        assertTrue(m.get(false).isEmpty());
    }

    // ═══════════════════════════════════════════════════════════
    // Q3 · toMap 的坑
    // ═══════════════════════════════════════════════════════════

    @Test
    void mapsEachNameToItsLength() {
        Map<String, Integer> m = CollectorsLab.nameToLength(List.of("aa", "bbb"));
        assertEquals(2, m.get("aa"));
        assertEquals(3, m.get("bbb"));
        assertEquals(2, m.size());
    }

    @Test
    void duplicateNameExplodes() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> CollectorsLab.nameToLength(List.of("aa", "bb", "aa")),
                "toMap 是一对一的，撞 key 就是 bug，它自己会抛");
        assertTrue(ex.getMessage().contains("Duplicate key"),
                "要的是 toMap 自己抛的异常，实际消息：" + ex.getMessage());
    }

    // ═══════════════════════════════════════════════════════════
    // Q4 · Map 的顺序
    // ═══════════════════════════════════════════════════════════

    @Test
    void groupsWordsByLength() {
        Map<Integer, List<String>> m = CollectorsLab.groupByLengthSorted(List.of("ddd", "a", "cc", "bb"));
        assertEquals(List.of("a"), m.get(1));
        assertEquals(List.of("cc", "bb"), m.get(2));
        assertEquals(List.of("ddd"), m.get(3));
    }

    @Test
    void keyOrderIsAscending() {
        Map<Integer, List<String>> m = CollectorsLab.groupByLengthSorted(List.of("ddd", "a", "cc"));
        assertEquals(List.of(1, 2, 3), new ArrayList<>(m.keySet()),
                "keySet 必须按升序 —— 默认的 HashMap 不保证顺序，你信它就红");
    }

    // ═══════════════════════════════════════════════════════════
    // 天然绿 · 不用写 TODO 就能过，考的是概念
    // ═══════════════════════════════════════════════════════════

    @Test
    void groupingByGivesPlainHashMapByDefault() {
        Map<Integer, List<String>> m = Stream.of("a", "bb")
                .collect(Collectors.groupingBy(String::length));
        assertTrue(m instanceof HashMap,
                "默认实现是 HashMap —— 想要顺序必须自己指定 TreeMap::new，实际类型："
                        + m.getClass().getName());
    }

    @Test
    void toMapRefusesNullValues() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> Stream.of("a").collect(Collectors.toMap(s -> s, s -> (Integer) null)),
                "坑 2：toMap 内部对 value 做了 requireNonNull，null 直接 NPE");
        assertNotNull(ex);
    }

    @Test
    void joiningTakesDelimiterPrefixAndSuffix() {
        String joined = Stream.of("a", "b", "c").collect(Collectors.joining(", ", "[", "]"));
        assertEquals("[a, b, c]", joined, "三个参数的 joining：分隔符、前缀、后缀");
    }

    @Test
    void simpleSumLoopAndStreamAgree() {
        List<Integer> xs = List.of(1, 2, 3, 4);
        int byLoop = 0;
        for (int x : xs) {
            byLoop += x;
        }
        int byStream = xs.stream().mapToInt(Integer::intValue).sum();
        assertEquals(byLoop, byStream,
                "结果一样。但简单累加、要 break、要改外部变量 —— 循环更直白也更快；"
                        + "筛选 / 转换 / 分组才是流的战场");
    }

    @Test
    void distinctReliesOnEqualsAndHashCode() {
        record Point(int x) {
        }
        long n = Stream.of(new Point(1), new Point(1), new Point(2)).distinct().count();
        assertEquals(2, n,
                "distinct 靠 equals/hashCode 去重 —— record 自动生成这两个方法，"
                        + "换成手写 class 忘了重写，这里就会是 3");
    }
}
