package com.training.day04;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringApiExercisesTest {

    @Test
    void startsWithIgnoreCaseBasic() {
        assertTrue(StringApiExercises.startsWithIgnoreCase("HelloWorld", "hello"));
        assertTrue(StringApiExercises.startsWithIgnoreCase("HelloWorld", "HELLO"));
    }

    @Test
    void startsWithIgnoreCaseFalse() {
        assertFalse(StringApiExercises.startsWithIgnoreCase("HelloWorld", "world"));
        assertFalse(StringApiExercises.startsWithIgnoreCase("abc", "abcd"));
    }

    @Test
    void startsWithIgnoreCaseEmpty() {
        assertTrue(StringApiExercises.startsWithIgnoreCase("", ""));
    }

    @Test
    void initialsBasic() {
        assertEquals("ZS", StringApiExercises.initials("Zhang San"));
    }

    @Test
    void initialsSingleWord() {
        assertEquals("T", StringApiExercises.initials("tom"));
    }

    @Test
    void initialsMultiSpaces() {
        assertEquals("LS", StringApiExercises.initials("  Li  Si "));
    }

    @Test
    void initialsEmpty() {
        assertEquals("", StringApiExercises.initials(""));
        assertEquals("", StringApiExercises.initials("   "));
    }

    @Test
    void maskPhoneNormal() {
        assertEquals("*******5678", StringApiExercises.maskPhone("13812345678"));
    }

    @Test
    void maskPhoneTooShort() {
        assertEquals("12345", StringApiExercises.maskPhone("12345"));
    }

    @Test
    void maskPhoneEmpty() {
        assertEquals("", StringApiExercises.maskPhone(""));
    }
}
