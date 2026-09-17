package com.training.day02;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringExercisesTest {

    @Test
    void joinBasic() {
        assertEquals("a,b,c", StringExercises.join(new String[]{"a", "b", "c"}, ","));
    }

    @Test
    void joinSingleWord() {
        assertEquals("hello", StringExercises.join(new String[]{"hello"}, "-"));
    }

    @Test
    void joinEmptyArray() {
        assertEquals("", StringExercises.join(new String[]{}, ","));
    }

    @Test
    void palindromeBasic() {
        assertTrue(StringExercises.isPalindrome("racecar"));
        assertTrue(StringExercises.isPalindrome("Racecar"));              // 忽略大小写
        assertTrue(StringExercises.isPalindrome("A man a plan a canal Panama")); // 跳过空格
    }

    @Test
    void palindromeNot() {
        assertFalse(StringExercises.isPalindrome("hello"));
        assertFalse(StringExercises.isPalindrome("abcbaa"));
    }

    @Test
    void palindromeEmpty() {
        assertTrue(StringExercises.isPalindrome(""));
    }
}
