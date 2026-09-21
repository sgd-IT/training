package com.training.day07;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 7 - Q1 测试：Point
 *
 * 对照 RawPointTest 看 —— 那里同样的操作全是"坏结果"，这里应该全是"好结果"。
 */
class PointTest {

    // ── toString ──────────────────────────────────────────────

    @Test
    void toStringHasReadableFormat() {
        assertEquals("(3, 4)", new Point(3, 4).toString());
    }

    @Test
    void toStringHandlesNegative() {
        assertEquals("(-1, 0)", new Point(-1, 0).toString());
    }

    @Test
    void printlnUsesToStringAutomatically() {
        // 字符串拼接会自动调 toString —— 这是重写 toString 最大的好处
        assertEquals("点=(3, 4)", "点=" + new Point(3, 4));
    }

    // ── equals ────────────────────────────────────────────────

    @Test
    void equalsComparesValues() {
        // ⚠️ 关键一行：assertEquals(Object, Object) 内部是以 Object 类型调 equals 的。
        //    如果你把方法写成了 equals(Point o)（重载而非重写），这条会失败。
        assertEquals(new Point(3, 4), new Point(3, 4));
    }

    @Test
    void notEqualsDifferentValues() {
        assertNotEquals(new Point(3, 4), new Point(3, 5));
        assertNotEquals(new Point(3, 4), new Point(4, 3));
    }

    @Test
    void equalsIsReflexive() {
        Point p = new Point(3, 4);
        assertTrue(p.equals(p));
        assertEquals(p, p);
    }

    @Test
    void equalsHandlesNull() {
        assertFalse(new Point(3, 4).equals(null), "和 null 比必须返回 false，不能抛异常");
    }

    @Test
    void equalsHandlesOtherType() {
        assertFalse(new Point(3, 4).equals("(3, 4)"), "和 String 比必须返回 false");
        assertFalse(new Point(3, 4).equals(new RawPoint(3, 4)), "和别的类比必须返回 false");
    }

    // ── hashCode ──────────────────────────────────────────────

    @Test
    void hashCodeConsistentWithEquals() {
        assertEquals(new Point(3, 4).hashCode(), new Point(3, 4).hashCode(),
                "equals 相等的两个对象，hashCode 必须相等");
    }

    // ── 最终目的：能当集合的元素 / key ───────────────────────

    @Test
    void usableAsHashSetElement() {
        Set<Point> set = new HashSet<>();
        set.add(new Point(3, 4));
        set.add(new Point(3, 4));
        assertEquals(1, set.size(), "重写之后 HashSet 才能去重");
    }

    @Test
    void usableAsHashMapKey() {
        Map<Point, String> map = new HashMap<>();
        map.put(new Point(3, 4), "这个点");
        assertEquals("这个点", map.get(new Point(3, 4)),
                "重写之后才能用内容相同的 key 取回值");
    }
}