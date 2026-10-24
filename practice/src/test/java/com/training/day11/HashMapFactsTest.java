package com.training.day11;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 11 - Q3（只读，不写代码）：把 JDK 的 HashMap 拆开看
 * <p>
 * ⚠️ 这一组测试**一行 TODO 都没有**，而且**天然绿**。
 * 它的作用是当"标尺"：你手写的 MiniHashMap 和真实 HashMap 差在哪，用这些数字对照。
 * <p>
 * 用反射读私有字段，需要 surefire 加一个开关（pom.xml 里已经配好）：
 * <pre>
 *   --add-opens java.base/java.util=ALL-UNNAMED
 * </pre>
 * 没有它，JDK 17+ 的模块系统会直接抛 {@code InaccessibleObjectException}。
 * （这也顺带回答了一个常见面试题：反射不是万能的，模块化之后要显式开口子。）
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 读完要能回答的四个问题
 * ═══════════════════════════════════════════════════════════
 *   ① 为什么 new HashMap&lt;&gt;() 之后 table 是 null？（和 Day 9 的懒初始化同一件事）
 *   ② 为什么 new HashMap&lt;&gt;(100) 实际容量是 128，不是 100？
 *   ③ 链表什么时候变红黑树？为什么必须先让 table 长到 64？
 *   ④ 树退化成链表的阈值为什么是 6，而树化是 8？（为什么不是同一个数）
 */
class HashMapFactsTest {

    private static Field tableField() throws Exception {
        Field f = HashMap.class.getDeclaredField("table");
        f.setAccessible(true);
        return f;
    }

    private static int tableLength(Map<?, ?> map) throws Exception {
        Object[] table = (Object[]) tableField().get(map);
        return table == null ? -1 : table.length;
    }

    private static Object[] rawTable(Map<?, ?> map) throws Exception {
        return (Object[]) tableField().get(map);
    }

    private static int thresholdOf(Map<?, ?> map) throws Exception {
        Field f = HashMap.class.getDeclaredField("threshold");
        f.setAccessible(true);
        return f.getInt(map);
    }

    static final class SameHash {
        final String name;

        SameHash(String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return 7;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof SameHash s && Objects.equals(s.name, name);
        }
    }

    @Test
    void tableIsLazilyAllocated() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        assertEquals(-1, tableLength(map), "刚 new 出来时 table 是 null —— 懒初始化，和 Day 9 的 ArrayList 同一招");
        map.put("a", 1);
        assertEquals(16, tableLength(map), "第一次 put 才真的分配 16 个桶");
        assertEquals(12, thresholdOf(map), "阈值 = 16 × 0.75 = 12");
    }

    @Test
    void thirteenthPutDoublesCapacity() throws Exception {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            map.put(i, i);
        }
        assertEquals(16, tableLength(map), "12 个元素时还没超阈值");

        map.put(12, 12);
        assertEquals(32, tableLength(map), "第 13 个 → 扩容到 32");
        assertEquals(24, thresholdOf(map), "新阈值 24");
    }

    @Test
    void constructorCapacityIsRoundedUpToPowerOfTwo() throws Exception {
        Map<String, Integer> a = new HashMap<>(100);
        a.put("x", 1);
        assertEquals(128, tableLength(a), "100 向上取到 2 的幂 = 128（tableSizeFor）");

        Map<String, Integer> b = new HashMap<>(3);
        b.put("x", 1);
        assertEquals(4, tableLength(b), "3 向上取到 4");

        Map<String, Integer> c = new HashMap<>(16);
        c.put("x", 1);
        assertEquals(16, tableLength(c), "16 本身就是 2 的幂，保持不变");
    }

    @Test
    void loadFactorIs075() throws Exception {
        Field f = HashMap.class.getDeclaredField("loadFactor");
        f.setAccessible(true);
        assertEquals(0.75f, f.getFloat(new HashMap<>()), "默认负载因子 0.75");
    }

    @Test
    void nullKeyGoesToBucketZero() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put(null, 99);
        Object[] table = rawTable(map);
        assertNotNull(table[0], "null 键的 hash 被当成 0 → 落在 0 号桶");
        assertEquals(99, map.get(null));
    }

    @Test
    void bucketTurnsIntoTreeWhenItGetsTooLong() throws Exception {
        Map<SameHash, Integer> map = new HashMap<>(128);
        String treeNodeName = "java.util.HashMap$TreeNode";
        Class<?> treeNode = Class.forName(treeNodeName);

        for (int i = 0; i < 8; i++) {
            map.put(new SameHash("k" + i), i);
        }
        Object[] table = rawTable(map);
        int idx = table.length - 1 & 7;
        assertFalse(treeNode.isInstance(table[idx]),
                "8 个同 hash 元素时还是链表（树化阈值 8 是【大于】才转）");

        map.put(new SameHash("k8"), 8);
        table = rawTable(map);
        idx = table.length - 1 & 7;
        assertTrue(treeNode.isInstance(table[idx]),
                "第 9 个才转红黑树 —— 因为条件是 binCount >= TREEIFY_THRESHOLD(8)，从 0 数到 8");
    }

    @Test
    void treeifyNeedsTableOfAtLeast64() throws Exception {
        // 小容量时，同一个桶塞满也不会树化 —— JDK 会先选择"扩容"来稀释冲突。
        Map<SameHash, Integer> map = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            map.put(new SameHash("k" + i), i);
        }
        assertTrue(tableLength(map) >= 16, "小表先扩容，不在 16 个桶的规模上折腾红黑树");
        assertTrue(tableLength(map) <= 64, "此时还没到 MIN_TREEIFY_CAPACITY=64，所以仍是链表");

        String treeNodeName = "java.util.HashMap$TreeNode";
        Class<?> treeNode = Class.forName(treeNodeName);
        Object[] table = rawTable(map);
        for (Object head : table) {
            if (head != null) {
                assertFalse(treeNode.isInstance(head),
                        "容量没到 64 之前，再怎么冲突也不树化（先扩容稀释）");
            }
        }
    }
}