package com.training.day02;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentTest {

    @Test
    void constructorAssignsFields() {
        Student s = new Student("张三", 85);
        assertEquals("张三", s.getName());
        assertEquals(85, s.getScore());
    }

    @Test
    void constructorRejectsEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new Student("", 60));
        assertThrows(IllegalArgumentException.class, () -> new Student(null, 60));
    }

    @Test
    void constructorRejectsScoreOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new Student("张三", -1));
        assertThrows(IllegalArgumentException.class, () -> new Student("张三", 101));
    }

    @Test
    void isPassedBoundary() {
        assertFalse(new Student("张三", 59).isPassed());
        assertTrue(new Student("张三", 60).isPassed());
        assertTrue(new Student("张三", 100).isPassed());
    }

    @Test
    void raiseScoreIncreases() {
        Student s = new Student("张三", 80);
        s.raiseScore(15);
        assertEquals(95, s.getScore());
    }

    @Test
    void raiseScoreRejectsNonPositive() {
        Student s = new Student("张三", 80);
        assertThrows(IllegalArgumentException.class, () -> s.raiseScore(0));
        assertThrows(IllegalArgumentException.class, () -> s.raiseScore(-5));
    }

    @Test
    void raiseScoreCannotExceed100() {
        Student s = new Student("张三", 90);
        assertThrows(IllegalArgumentException.class, () -> s.raiseScore(20));
        assertEquals(90, s.getScore()); // 加分失败后分数不能变
    }
}
