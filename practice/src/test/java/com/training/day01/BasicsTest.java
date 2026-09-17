package com.training.day01;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasicsTest {

    @Test
    void q1SumEven() {
        assertEquals(6, Basics.sumEven(new int[]{1, 2, 3, 4, 5}));
        assertEquals(0, Basics.sumEven(new int[]{1, 3, 5}));
        assertEquals(12, Basics.sumEven(new int[]{2, 4, 6}));
        assertEquals(0, Basics.sumEven(new int[]{}));
    }

    @Test
    void q2CountWords() {
        assertEquals(2, Basics.countWords("hello world"));
        assertEquals(2, Basics.countWords("  a  b  "));
        assertEquals(0, Basics.countWords(""));
        assertEquals(1, Basics.countWords("single"));
    }

    @Test
    void q3ReverseWords() {
        assertEquals("coder a am I", Basics.reverseWords("I am a coder"));
        assertEquals("world hello", Basics.reverseWords("hello world"));
        assertEquals("solo", Basics.reverseWords("solo"));
        assertEquals("", Basics.reverseWords(""));
    }

    @Test
    void q4FrequencyMap() {
        List<String> words = Arrays.asList("a", "b", "a", "c", "b", "b");
        Map<String, Integer> map = Basics.frequencyMap(words);
        assertEquals(2, map.get("a"));
        assertEquals(3, map.get("b"));
        assertEquals(1, map.get("c"));
        assertEquals(0, Basics.frequencyMap(Arrays.asList()).size());
    }

    @Test
    void q5ParseIntSafe() {
        assertEquals(42, Basics.parseIntSafe("42", 0));
        assertEquals(-1, Basics.parseIntSafe("abc", -1));
        assertEquals(7, Basics.parseIntSafe(null, 7));
        assertEquals(123, Basics.parseIntSafe("123", 0));
    }
}
