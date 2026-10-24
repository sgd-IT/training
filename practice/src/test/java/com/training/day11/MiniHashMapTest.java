package com.training.day11;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 11 - Q1 测试：MiniHashMap
 * <p>
 * 设计说明（教练的测试设计病，自记）：
 * 这里刻意不写成"只测 happy path"。三条容易漏的：
 * · **null 键**（真实 HashMap 支持，而且必须落在 0 号桶）
 * · **同 hash 不同 key**（不冲突的测试等于没测链表）
 * · **扩容后还能不能 get 到**（只测 capacity 翻倍 = 只测改得掉，不测装对）
 */
class MiniHashMapTest {

    /** 故意让所有实例 hashCode 相同，逼出链表冲突。 */
    static final class SameHashKey {
        final String name;

        SameHashKey(String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return 42;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof SameHashKey k && Objects.equals(k.name, name);
        }

        @Override
        public String toString() {
            return "SameHashKey(" + name + ")";
        }
    }

    @Test
    void freshMapHasCapacity16AndThreshold12() {
        MiniHashMap<String, Integer> map = new MiniHashMap<>();
        assertEquals(16, map.capacity(), "初始容量必须是 16 —— 不是 12，12 是阈值");
        assertEquals(12, map.threshold(), "threshold = 16 × 0.75 = 12");
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
    }

    @Test
    void putNewKeyReturnsNull() {
        MiniHashMap<String, Integer> map = new MiniHashMap<>();
        assertNull(map.put("a", 1), "新键 → put 返回 null");
        assertEquals(1, map.size());
        assertEquals(1, map.get("a"));
    }

    @Test
    void putExistingKeyReturnsOldValue() {
        MiniHashMap<String, Integer> map = new MiniHashMap<>();
        map.put("a", 1);
        assertEquals(1, map.put("a", 2), "覆盖已有键 → 返回【旧值】1");
        assertEquals(2, map.get("a"), "值被覆盖成 2");
        assertEquals(1, map.size(), "覆盖不算新增，size 不变");
    }

    @Test
    void getMissingKeyReturnsNull() {
        MiniHashMap<String, Integer> map = new MiniHashMap<>();
        assertNull(map.get("nope"));
        map.put("a", 1);
        assertNull(map.get("b"), "只放了一个键，别的键必须查不到");
    }

    @Test
    void isEmptyFlipsAfterFirstPut() {
        MiniHashMap<String, Integer> map = new MiniHashMap<>();
        assertTrue(map.isEmpty());
        map.put("a", 1);
        assertFalse(map.isEmpty());
    }

    @Test
    void nullKeyIsSupportedAndLandsInBucketZero() {
        MiniHashMap<String, Integer> map = new MiniHashMap<>();
        assertNull(map.put(null, 100));
        assertEquals(100, map.get(null), "null 键必须能取回自己的值");
        assertEquals(0, MiniHashMap.indexFor(0, 16), "null 的 hash 当成 0 → 落在 0 号桶");
    }

    @Test
    void nullKeyCanBeOverwritten() {
        MiniHashMap<String, Integer> map = new MiniHashMap<>();
        map.put(null, 1);
        assertEquals(1, map.put(null, 2), "null 键第二次 put 也是覆盖，返回旧值");
        assertEquals(2, map.get(null));
        assertEquals(1, map.size());
    }

    @Test
    void sameHashDifferentKeysBothRetrievable() {
        MiniHashMap<SameHashKey, String> map = new MiniHashMap<>();
        SameHashKey k1 = new SameHashKey("one");
        SameHashKey k2 = new SameHashKey("two");
        SameHashKey k3 = new SameHashKey("three");
        assertEquals(42, k1.hashCode());

        map.put(k1, "1");
        map.put(k2, "2");
        map.put(k3, "3");

        assertEquals(3, map.size(), "三个不同的 key 就是三个条目，哪怕 hash 一样");
        assertEquals("1", map.get(k1), "链表里必须能按 key 区分出节点 —— hashCode 相同，equals 不同");
        assertEquals("2", map.get(k2));
        assertEquals("3", map.get(k3));
    }

    @Test
    void resizeAtThirteenthPut() {
        MiniHashMap<Integer, Integer> map = new MiniHashMap<>();
        for (int i = 0; i < 12; i++) {
            map.put(i, i);
        }
        assertEquals(16, map.capacity(), "放满 12 个还没超阈值，不扩容");
        assertEquals(12, map.size());

        map.put(12, 12);
        assertEquals(32, map.capacity(), "第 13 个之后 size=13 > 12 → 翻倍");
        assertEquals(24, map.threshold(), "新阈值 = 32 × 0.75 = 24");
        assertEquals(13, map.size(), "扩容不改 size");
    }

    @Test
    void allEntriesSurviveResize() {
        MiniHashMap<Integer, String> map = new MiniHashMap<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, "v" + i);
        }
        assertEquals(100, map.size());
        for (int i = 0; i < 100; i++) {
            assertEquals("v" + i, map.get(i), "扩容搬运后第 " + i + " 个键丢了 —— resize 有问题");
        }
    }

    @Test
    void collisionsSurviveResize() {
        MiniHashMap<SameHashKey, Integer> map = new MiniHashMap<>();
        SameHashKey[] keys = new SameHashKey[20];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = new SameHashKey("k" + i);
            map.put(keys[i], i);
        }
        for (int i = 0; i < keys.length; i++) {
            assertEquals(i, map.get(keys[i]), "全同 hash 的 20 个键在扩容后必须全部还在");
        }
    }

    @Test
    void indexForUsesLowBitsOnly() {
        assertEquals(0, MiniHashMap.indexFor(0, 16));
        assertEquals(15, MiniHashMap.indexFor(15, 16));
        assertEquals(0, MiniHashMap.indexFor(16, 16), "第 5 位不影响 16 容量下的下标");
        assertEquals(6, MiniHashMap.indexFor(0b1111_0110, 16), "只取最低 4 位 = 0b0110");
    }

    @Test
    void indexForHandlesNegativeHash() {
        assertEquals(15, MiniHashMap.indexFor(-1, 16), "-1 的位模式是全 1，取低 4 位就是 15");
        assertTrue(MiniHashMap.indexFor(-12345, 16) >= 0, "下标永远不能是负数");
        assertTrue(MiniHashMap.indexFor(-12345, 16) < 16);
    }

    @Test
    void knownLimitation_ofContainsKey() {
        // 脚手架给的 containsKey 是 `get(key) != null`，所以值为 null 时会误报"不存在"。
        // 真实 HashMap.put(k, null) 之后 containsKey(k) 是 true —— 因为它查的是节点，不是值。
        // 这个测试不是让你改，是把这个差别记录成标本（和 Day 7 的 RawPoint 同一个做法）。
        MiniHashMap<String, Integer> map = new MiniHashMap<>();
        map.put("a", null);
        assertEquals(1, map.size(), "键确实进去了");
        assertFalse(map.containsKey("a"), "已知简化：拿 get()!=null 当 containsKey，值为 null 时会误判");
    }
}