package com.training.day09;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 9 - Q4 测试：DequeOps
 *
 * 同一个 LinkedList，push/pop 是栈，offer/poll 是队列。
 */
class DequeOpsTest {

    @Test
    void stackIsLifo() {
        assertEquals(List.of("c", "b", "a"), DequeOps.stackOrder(List.of("a", "b", "c")),
                "栈：后进先出");
    }

    @Test
    void queueIsFifo() {
        assertEquals(List.of("a", "b", "c"), DequeOps.queueOrder(List.of("a", "b", "c")),
                "队列：先进先出");
    }

    @Test
    void emptyInputGivesEmptyOutput() {
        assertEquals(List.of(), DequeOps.stackOrder(List.of()));
        assertEquals(List.of(), DequeOps.queueOrder(List.of()));
    }
}