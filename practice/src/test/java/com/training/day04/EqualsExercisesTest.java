package com.training.day04;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EqualsExercisesTest {

    @Test
    void stringsSameContent() {
        assertTrue(EqualsExercises.isSameString("abc", "abc"));
    }

    @Test
    void stringsNewInstance() {
        // new 出来的和字面量不是同一个对象，== 会返回 false，equals 返回 true
        assertTrue(EqualsExercises.isSameString(new String("abc"), "abc"));
    }

    @Test
    void stringsDifferentContent() {
        assertFalse(EqualsExercises.isSameString("abc", "abd"));
    }

    @Test
    void intsSameValue() {
        assertTrue(EqualsExercises.isSameValue(1, 1));
        assertFalse(EqualsExercises.isSameValue(1, 2));
    }
}
