package com.training.day03;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OverloadExercisesTest {

    @Test
    void maxTwoInts() {
        assertEquals(5, OverloadExercises.max(2, 5));
    }

    @Test
    void maxThreeInts() {
        assertEquals(9, OverloadExercises.max(2, 5, 9));
        assertEquals(11, OverloadExercises.max(11, 2, 9));
    }

    @Test
    void maxDoubles() {
        // double 实参命中 max(double, double)
        assertEquals(2.5, OverloadExercises.max(2.5, 1.8), 0.0001);
    }

    @Test
    void areaCircle() {
        // 一个参数 = 圆：π × 2²
        assertEquals(12.566370614359172, OverloadExercises.area(2.0), 0.0001);
    }

    @Test
    void areaRectangle() {
        // int 实参自动提升为 double，命中 area(double, double)
        assertEquals(12.0, OverloadExercises.area(3, 4), 0.0001);
    }
}
