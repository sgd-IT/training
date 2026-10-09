package com.training.day10;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 10 - Q3 测试：MapTraversal
 */
class MapTraversalTest {

    private static Map<String, Integer> sample() {
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        return map;
    }

    @Test
    void sumByKeySetAddsAllValues() {
        assertEquals(6, MapTraversal.sumByKeySet(sample()), "1 + 2 + 3");
    }

    @Test
    void sumByEntrySetAddsAllValues() {
        assertEquals(6, MapTraversal.sumByEntrySet(sample()), "1 + 2 + 3");
    }

    @Test
    void emptyMapSumsToZero() {
        assertEquals(0, MapTraversal.sumByKeySet(new HashMap<>()), "空 Map 求和是 0，不是异常");
        assertEquals(0, MapTraversal.sumByEntrySet(new HashMap<>()), "空 Map 求和是 0，不是异常");
    }

    @Test
    void removeBelowByIteratorDropsSmallValues() {
        Map<String, Integer> map = sample();
        MapTraversal.removeBelowByIterator(map, 3);
        assertEquals(Map.of("c", 3), map, "1 和 2 被删掉，3 留下");
    }

    @Test
    void removeBelowByRemoveIfDropsSmallValues() {
        Map<String, Integer> map = sample();
        MapTraversal.removeBelowByRemoveIf(map, 3);
        assertEquals(Map.of("c", 3), map, "1 和 2 被删掉，3 留下");
    }

    @Test
    void valueEqualToLimitSurvives() {
        Map<String, Integer> map = sample();
        MapTraversal.removeBelowByRemoveIf(map, 2);
        assertEquals(Map.of("b", 2, "c", 3), map, "条件是 < limit —— 等于 limit 的要留下");
    }

    @Test
    void removeLeavesMapEmptyWhenEverythingIsBelow() {
        Map<String, Integer> map = sample();
        MapTraversal.removeBelowByIterator(map, 100);
        assertTrue(map.isEmpty(), "全都不达标就应当删空，而且不抛异常");
    }

    // ─────────────────────────────────────────────────────────────
    // ⚠️ 这个测试【不验证你的代码】—— 它一行都没调用 MapTraversal。
    //    它记录的是 JDK 的行为：for-each 里 map.remove() 会抛 CME。
    //    用 LinkedHashMap 是为了让遍历顺序确定（a → b → c）：
    //    删掉 a、b 之后 hasNext() 仍为真，于是又调了一次 next()，
    //    modCount 对不上 → 抛。这和 Day 9 的 List 是同一个机制。
    // ─────────────────────────────────────────────────────────────

    @Test
    void jdkBehavior_forEachPlusMapRemoveThrowsCme() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        assertThrows(java.util.ConcurrentModificationException.class, () -> {
            for (String k : map.keySet()) {
                if (map.get(k) < 3) {
                    map.remove(k);
                }
            }
        }, "for-each 遍历中 remove —— 单线程也抛 CME");
    }
}