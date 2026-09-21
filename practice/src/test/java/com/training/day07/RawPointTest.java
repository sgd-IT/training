package com.training.day07;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 7 - 教科书对照测试（别改它！）
 *
 * ⚡ 这些测试**全部通过** —— 但它们记录的正是"什么都不重写"的默认行为，
 *    也就是"坑"本身。跑一遍，看清默认有多糟。
 *
 *    然后去看 PointTest —— 同样的操作，重写之后是什么结果。
 */
class RawPointTest {

    @Test
    void defaultToStringIsUgly() {
        String s = new RawPoint(3, 4).toString();
        System.out.println("[RawPoint] 默认 toString = " + s);
        assertTrue(s.startsWith("com.training.day07.RawPoint@"),
                "默认 toString 应该是「类名@hashCode」的形式，实际是: " + s);
    }

    @Test
    void defaultEqualsComparesReferenceNotValue() {
        RawPoint a = new RawPoint(3, 4);
        RawPoint b = new RawPoint(3, 4);
        // 内容一模一样，但默认 equals 比的是地址
        assertNotEquals(a, b);
        // 只有自己和自己才相等
        assertEquals(a, a);
    }

    @Test
    void defaultHashCodeIsIdentityBased() {
        RawPoint a = new RawPoint(3, 4);
        RawPoint b = new RawPoint(3, 4);
        System.out.println("[RawPoint] a.hashCode() = " + a.hashCode());
        System.out.println("[RawPoint] b.hashCode() = " + b.hashCode());
        System.out.println("[RawPoint] 内容相同，哈希却不同 —— 这就是默认行为");
        // 同一个对象，哈希是稳定的
        assertEquals(a.hashCode(), a.hashCode());
    }

    @Test
    void cannotDedupeInHashSet() {
        Set<RawPoint> set = new HashSet<>();
        set.add(new RawPoint(3, 4));
        set.add(new RawPoint(3, 4));
        set.add(new RawPoint(3, 4));
        // 期望去重成 1 个，实际是 3 个
        assertEquals(3, set.size(), "没重写 equals/hashCode 时，HashSet 根本去不掉重");
    }

    @Test
    void cannotLookupInHashMap() {
        Map<RawPoint, String> map = new HashMap<>();
        map.put(new RawPoint(3, 4), "这个点");
        // 用一个"内容相同"的新对象去取
        String got = map.get(new RawPoint(3, 4));
        assertNull(got, "没重写 equals/hashCode 时，用内容相同的 key 取不出值");
    }
}