package com.training.day13;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 13 - 测试：SetLab + Set 三家的行为差异
 * <p>
 * 前面 9 个测 TODO；后面是**天然绿**的观察题（Set 的脾气、TreeSet 的判重坑、
 * SequencedMap 的新方法）。
 */
class SetLabTest {

    @Test
    void dedupePreservingOrderKeepsFirstSeenOrder() {
        assertEquals(List.of("b", "a", "c"),
                SetLab.dedupePreservingOrder(List.of("b", "a", "b", "c", "a")),
                "去重之后顺序必须是 b, a, c —— 按第一次出现的先后");
    }

    @Test
    void dedupePreservingOrderOnEmptyList() {
        assertEquals(List.of(), SetLab.dedupePreservingOrder(List.of()),
                "空输入 → 空列表。别为它写特判，循环本来就不进");
    }

    @Test
    void dedupeSortedByLengthSortsThenDedupes() {
        assertEquals(List.of("a", "b", "bb", "cc", "aaa"),
                SetLab.dedupeSortedByLength(List.of("bb", "a", "cc", "b", "aaa")),
                "长度升序；长度相同按字典序（a 在 b 前，bb 在 cc 前）");
    }

    @Test
    void dedupeSortedByLengthRemovesRealDuplicates() {
        assertEquals(List.of("x", "yy"),
                SetLab.dedupeSortedByLength(List.of("x", "yy", "x", "yy")),
                "真正的重复（内容也一样）必须被去掉");
    }

    @Test
    void firstAndLastUseSequencedApi() {
        List<String> words = List.of("a", "b", "c");
        assertEquals("a", SetLab.first(words));
        assertEquals("c", SetLab.last(words));
    }

    @Test
    void reversedListCarriesAllElementsReversed() {
        assertEquals(List.of("c", "b", "a"), SetLab.reversedList(List.of("a", "b", "c")));
        assertEquals(List.of(), SetLab.reversedList(List.of()), "空列表反转还是空列表");
    }

    @Test
    void reversedIsAViewNotACopy() {
        List<String> words = new ArrayList<>(List.of("a", "b", "c"));
        List<String> view = SetLab.reversedList(words);
        assertEquals(List.of("c", "b", "a"), view);

        words.add("d");
        assertEquals(List.of("d", "c", "b", "a"), view,
                "reversed() 是【视图】：改了原列表，视图跟着变。如果是拷贝，这里还是 c,b,a");
    }

    // ═══════════════════════════════════════════════════════════
    // 以下天然绿 —— 只读，不需要写 TODO
    // ═══════════════════════════════════════════════════════════

    @Test
    void hashSetHasNoOrderButLinkedHashSetDoes() {
        List<String> words = List.of("z", "a", "m", "a", "z");
        Set<String> hashed = new HashSet<>(words);
        Set<String> linked = new LinkedHashSet<>(words);

        assertEquals(3, hashed.size(), "HashSet 也去重了");
        assertEquals(List.of("z", "a", "m"), new ArrayList<>(linked),
                "LinkedHashSet 保留插入顺序");
        // ⚠️ 刻意不断言 HashSet 的顺序 —— HashMap 的遍历顺序不承诺稳定，
        //    断言它就是在写不稳定测试。这一点本身就是考点。
    }

    @Test
    void treeSetOnlyComparesByComparatorSoLengthOnlyCollapsesDifferentStrings() {
        // ★ 典型事故：以为 TreeSet 会像 HashSet 一样用 equals 判重。
        TreeSet<String> byLengthOnly = new TreeSet<>(Comparator.comparingInt(String::length));
        byLengthOnly.add("ab");
        byLengthOnly.add("cd");      // 长度相同 → compare 返回 0 → 被当成重复
        byLengthOnly.add("ef");

        assertEquals(1, byLengthOnly.size(),
                "三个内容完全不同的字符串，只剩一个 —— 因为比较器只比长度，TreeSet 认为它们相等");
        assertEquals("ab", byLengthOnly.first(), "留下的是第一个插进去的那个");

        // 正确写法：比较器构成全序（长度 + 内容）
        TreeSet<String> total = new TreeSet<>(
                Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()));
        total.addAll(List.of("ab", "cd", "ef"));
        assertEquals(3, total.size(), "补上 thenComparing 之后，三个都在");
    }

    @Test
    void treeSetIsSortedAndOrderedSetsHaveFirstAndLast() {
        TreeSet<Integer> t = new TreeSet<>(List.of(5, 1, 9, 3));
        assertEquals(List.of(1, 3, 5, 9), new ArrayList<>(t), "TreeSet 自动排序");
        assertEquals(1, t.getFirst(), "JDK 21：SortedSet 也能直接 getFirst()");
        assertEquals(9, t.getLast());
    }

    @Test
    void emptySequencedCollectionThrowsOnGetFirst() {
        assertThrows(NoSuchElementException.class, () -> List.of().getFirst(),
                "空集合没有第一个 —— JDK 21 的选择是抛异常，不是返回 null");
    }

    @Test
    void sequencedMapExposesFirstAndLastEntry() {
        TreeMap<String, Integer> map = new TreeMap<>();
        map.put("b", 2);
        map.put("a", 1);
        map.put("c", 3);

        assertEquals("a", map.firstEntry().getKey(), "TreeMap 按 key 排序");
        assertEquals("c", map.lastEntry().getKey());
        assertEquals(List.of("c", "b", "a"), new ArrayList<>(map.reversed().keySet()),
                "SequencedMap.reversed() 也是视图，key 顺序反过来");
    }
}