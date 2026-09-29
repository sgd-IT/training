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
}