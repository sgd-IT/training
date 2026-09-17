package com.training.day04;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MathUtilsTest {

    @Test
    void sumBasic() {
        assertEquals(6, MathUtils.sum(1, 2, 3));
    }

    @Test
    void sumSingle() {
        assertEquals(5, MathUtils.sum(5));
    }

    @Test
    void sumEmpty() {
        assertEquals(0, MathUtils.sum());
    }
}
