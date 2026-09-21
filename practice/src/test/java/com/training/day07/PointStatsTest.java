package com.training.day07;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 7 - Q3 测试：PointStats
 *
 * 这一组测试回答"我为什么要重写 equals/hashCode"。
 * 跑完之后，去 RawPointTest 看看同样的操作用 RawPoint 是什么结果。
 */
class PointStatsTest {

    @Test
    void dedupeRemovesDuplicates() {
        List<Point> input = List.of(new Point(1, 2), new Point(3, 4), new Point(1, 2));
        assertEquals(List.of(new Point(1, 2), new Point(3, 4)), PointStats.dedupe(input));
    }

    @Test
    void dedupeKeepsFirstOccurrenceOrder() {
        List<Point> input = List.of(
                new Point(5, 6), new Point(1, 2), new Point(5, 6), new Point(3, 4));
        assertEquals(List.of(new Point(5, 6), new Point(1, 2), new Point(3, 4)),
                PointStats.dedupe(input),
                "顺序必须保持第一次出现的顺序（想想该用哪个集合类）");
    }

    @Test
    void dedupeEmptyList() {
        assertEquals(List.of(), PointStats.dedupe(List.of()));
    }

    @Test
    void countOccurrencesWorks() {
        List<Point> input = List.of(new Point(1, 2), new Point(3, 4), new Point(1, 2));
        Map<Point, Integer> counts = PointStats.countOccurrences(input);

        Integer c12 = counts.get(new Point(1, 2));
        assertNotNull(c12, "取不到 (1,2) —— 检查 equals/hashCode 是否重写正确");
        assertEquals(2, c12.intValue());

        Integer c34 = counts.get(new Point(3, 4));
        assertNotNull(c34, "取不到 (3,4)");
        assertEquals(1, c34.intValue());

        assertEquals(2, counts.size(), "只应该有两个不同的点");
    }

    @Test
    void countOccurrencesEmptyList() {
        assertEquals(Map.of(), PointStats.countOccurrences(List.of()));
    }
}