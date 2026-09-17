package com.training.day04;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CounterTest {

    @BeforeEach
    void resetBeforeEachTest() {
        Counter.reset();
    }

    @Test
    void countIncrementsPerInstance() {
        new Counter();
        new Counter();
        new Counter();
        assertEquals(3, Counter.getCount());
    }

    @Test
    void countSharedAcrossInstances() {
        Counter c1 = new Counter();
        Counter c2 = new Counter();
        // static 字段是类级的：c1、c2 共享同一个 count
        assertEquals(2, Counter.getCount());
        assertEquals(2, c1.getCount()); // 用对象调 static 方法也能拿到（不推荐但能编译）
    }

    @Test
    void resetWorks() {
        new Counter();
        new Counter();
        Counter.reset();
        assertEquals(0, Counter.getCount());
    }
}
