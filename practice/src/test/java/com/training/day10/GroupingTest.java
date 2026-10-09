package com.training.day10;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 10 - Q2 测试：Grouping
 *
 * 注意 List 的 equals 是【顺序敏感】的 —— 桶里 ["a","c"] 和 ["c","a"] 不相等。
 * 所以这几个测试同时钉死了"桶内保持输入顺序"。
 */
class GroupingTest {

    private static final List<String> WORDS = List.of("a", "bb", "c", "dd", "eee");

    private static final Map<Integer, List<String>> EXPECTED = Map.of(
            1, List.of("a", "c"),
            2, List.of("bb", "dd"),
            3, List.of("eee"));

    @Test
    void loopVariantGroupsByLength() {
        assertEquals(EXPECTED, Grouping.groupByLengthWithLoop(WORDS), "手写 for + get 版");
    }

    @Test
    void computeIfAbsentVariantGroupsByLength() {
        assertEquals(EXPECTED, Grouping.groupByLengthWithComputeIfAbsent(WORDS), "computeIfAbsent 版");
    }

    @Test
    void bothVariantsAgree() {
        assertEquals(Grouping.groupByLengthWithLoop(WORDS),
                Grouping.groupByLengthWithComputeIfAbsent(WORDS),
                "两种写法必须给出同一个 Map");
    }

    @Test
    void emptyInputGivesEmptyMapNotNull() {
        assertEquals(Map.of(), Grouping.groupByLengthWithLoop(List.of()));
        assertEquals(Map.of(), Grouping.groupByLengthWithComputeIfAbsent(List.of()),
                "空输入要返回空 Map —— 不能是 null，不能抛异常");
    }

    @Test
    void sameLengthWordsLandInOneBucket() {
        assertEquals(Map.of(2, List.of("aa", "bb", "cc")),
                Grouping.groupByLengthWithComputeIfAbsent(List.of("aa", "bb", "cc")),
                "三个同长度的词必须挤在同一个桶里，而且要按输入顺序");
    }
}