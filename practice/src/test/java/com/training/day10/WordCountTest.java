package com.training.day10;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 10 - Q1 测试：WordCount
 *
 * 三种写法，同一个答案。Map.equals 与顺序无关，所以不用管 HashMap 的遍历顺序。
 */
class WordCountTest {

    private static final List<String> WORDS = List.of("a", "b", "a", "c", "a", "b");

    private static final Map<String, Integer> EXPECTED = Map.of("a", 3, "b", 2, "c", 1);

    @Test
    void containsKeyVariantCounts() {
        assertEquals(EXPECTED, WordCount.countWithContainsKey(WORDS), "写法一：containsKey + put");
    }

    @Test
    void getOrDefaultVariantCounts() {
        assertEquals(EXPECTED, WordCount.countWithGetOrDefault(WORDS), "写法二：getOrDefault + put");
    }

    @Test
    void mergeVariantCounts() {
        assertEquals(EXPECTED, WordCount.countWithMerge(WORDS), "写法三：merge");
    }

    @Test
    void allThreeVariantsAgree() {
        Map<String, Integer> a = WordCount.countWithContainsKey(WORDS);
        Map<String, Integer> b = WordCount.countWithGetOrDefault(WORDS);
        Map<String, Integer> c = WordCount.countWithMerge(WORDS);
        assertEquals(a, b, "三种写法结果必须一样（1 vs 2）");
        assertEquals(b, c, "三种写法结果必须一样（2 vs 3）");
    }

    @Test
    void emptyInputGivesEmptyMap() {
        assertEquals(Map.of(), WordCount.countWithContainsKey(List.of()));
        assertEquals(Map.of(), WordCount.countWithGetOrDefault(List.of()));
        assertEquals(Map.of(), WordCount.countWithMerge(List.of()));
    }

    @Test
    void singleWordCountsOne() {
        assertEquals(Map.of("x", 1), WordCount.countWithGetOrDefault(List.of("x")),
                "第一次见到这个词 —— 值必须是 1，不能是 null 或 0");
        assertEquals(Map.of("x", 1), WordCount.countWithMerge(List.of("x")),
                "merge 键不存在时放的是 value(1)，不是 remap 的结果");
    }
}