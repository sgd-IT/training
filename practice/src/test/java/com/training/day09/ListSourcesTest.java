package com.training.day09;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Day 9 - Q3 测试：ListSources（三种来源，三种脾气） */
class ListSourcesTest {

    @Test
    void mutableListAllowsAdd() {
        List<String> list = ListSources.mutableOf("a", "b");
        list.add("c");
        assertEquals(List.of("a", "b", "c"), list);
    }

    @Test
    void immutableListRejectsAdd() {
        List<String> list = ListSources.immutableOf("a", "b");
        assertThrows(UnsupportedOperationException.class, () -> list.add("c"),
                "List.of 造出来的不能改长度");
    }

    @Test
    void immutableListRejectsSetToo() {
        List<String> list = ListSources.immutableOf("a", "b");
        assertThrows(UnsupportedOperationException.class, () -> list.set(0, "x"),
                "List.of 连 set 都不行 —— 它是彻底不可变的");
    }

    @Test
    void arraysAsListIsFixedSizeButAllowsSet() {
        List<String> list = Arrays.asList("a", "b");
        list.set(0, "x");
        assertEquals("x", list.get(0), "set 可以");
        assertThrows(UnsupportedOperationException.class, () -> list.add("c"),
                "add 不行 —— 长度固定");
    }

    @Test
    void arraysAsListIsAViewOnTheArray() {
        String[] array = { "a", "b", "c" };
        ListSources.setThroughView(array, 1, "X");
        assertEquals("X", array[1], "改 list = 改原数组 —— 它没有复制任何东西");
        assertEquals("X", Arrays.asList(array).get(1));
    }

    @Test
    void theViewIsTwoWay() {
        String[] array = { "a", "b", "c" };
        Arrays.asList(array).set(2, "Z");
        assertEquals("Z", array[2], "反方向也通：改数组 list 也跟着变");
    }

    // ─────────────────────────────────────────────────────────────
    // 教练补的。上面两个 immutable 测试【只断言了"改不动"】，
    // 没断言"里面装的是不是对的东西" —— 一个只装了最后一个元素的
    // 不可变列表，照样能让 add/set 抛异常，于是 6/6 全绿。
    // 见 _probe/Day9Probe.java 段 C。
    // ─────────────────────────────────────────────────────────────

    @Test
    void immutableOfKeepsEveryElement() {
        List<String> list = ListSources.immutableOf("a", "b", "c");
        assertEquals(List.of("a", "b", "c"), list,
                "「不能改」和「装对了」是两件事，不可变列表也得断言内容");
        assertEquals(3, list.size());
    }
}