package com.training.day10;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 10 - Q4 测试：MapNulls
 *
 * 本日的分水岭在这里：merge 与 computeIfPresent
 * 在"键不存在"时的行为【完全相反】。
 */
class MapNullsTest {

    private static Map<String, Integer> mapOf(String k, int v) {
        Map<String, Integer> map = new HashMap<>();
        map.put(k, v);
        return map;
    }

    @Test
    void missingKeyGivesMinusOne() {
        assertEquals(-1, MapNulls.readOrMinusOne(new HashMap<>(), "nope"), "键不存在 → -1");
    }

    @Test
    void zeroValueIsNotMissing() {
        assertEquals(0, MapNulls.readOrMinusOne(mapOf("a", 0), "a"),
                "{a=0} 的 a 是【存在】的，值是 0 —— 不能返回 -1");
    }

    @Test
    void readOrDefaultUsesDefaultOnlyWhenMissing() {
        assertEquals(99, MapNulls.readOrDefault(new HashMap<>(), "nope", 99), "键不存在 → 默认值");
        assertEquals(7, MapNulls.readOrDefault(mapOf("a", 7), "a", 99), "键存在 → 真值，默认值用不上");
    }

    @Test
    void putIfAbsentReturnsNullOnInsert() {
        Map<String, Integer> map = new HashMap<>();
        assertNull(MapNulls.putIfAbsentReturnsOld(map, "a", 1), "成功放进去时返回 null");
        assertEquals(Map.of("a", 1), map);
    }

    @Test
    void putIfAbsentReturnsOldValueAndKeepsIt() {
        Map<String, Integer> map = mapOf("a", 1);
        assertEquals(1, MapNulls.putIfAbsentReturnsOld(map, "a", 9),
                "返回的是【被挡住的旧值】1，不是 9");
        assertEquals(Map.of("a", 1), map, "旧值不能被覆盖 —— 这就是 putIfAbsent 的字面意思");
    }

    @Test
    void mergeInsertsWhenKeyMissing() {
        Map<String, Integer> map = new HashMap<>();
        MapNulls.increaseByMerge(map, "a", 5);
        assertEquals(Map.of("a", 5), map, "merge：键不存在 → 直接放入 delta");
    }

    @Test
    void computeIfPresentDoesNothingWhenKeyMissing() {
        Map<String, Integer> map = new HashMap<>();
        MapNulls.increaseOnlyIfPresent(map, "a", 5);
        assertEquals(Map.of(), map, "computeIfPresent：键不存在 → 【什么都不做】，map 还是空的");
    }

    @Test
    void bothIncreasersAgreeOnExistingKey() {
        Map<String, Integer> m1 = mapOf("a", 1);
        Map<String, Integer> m2 = mapOf("a", 1);
        MapNulls.increaseByMerge(m1, "a", 5);
        MapNulls.increaseOnlyIfPresent(m2, "a", 5);
        assertEquals(Map.of("a", 6), m1, "键已存在时 merge 也是加上去");
        assertEquals(Map.of("a", 6), m2, "键已存在时两者行为一致 —— 差异只在键不存在时");
    }
}