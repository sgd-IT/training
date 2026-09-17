package com.training.day03;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ListExercisesTest {

    @Test
    void dedupeBasic() {
        assertEquals(Arrays.asList(1, 2, 3),
                ListExercises.dedupe(new int[]{1, 2, 2, 3, 1}));
    }

    @Test
    void dedupeAllSame() {
        assertEquals(Arrays.asList(7), ListExercises.dedupe(new int[]{7, 7, 7}));
    }

    @Test
    void dedupeEmpty() {
        assertEquals(Arrays.asList(), ListExercises.dedupe(new int[]{}));
    }

    @Test
    void reverseList() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        assertEquals(Arrays.asList(4, 3, 2, 1), ListExercises.reverse(list));
    }

    @Test
    void reverseReturnsSameList() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));
        List<Integer> result = ListExercises.reverse(list);
        // 原地反转：不能 new 新列表
        assertSame(list, result);
    }

    @Test
    void findDuplicatesHello() {
        assertEquals(Arrays.asList('l'), ListExercises.findDuplicates("hello"));
    }

    @Test
    void findDuplicatesTie() {
        assertEquals(Arrays.asList('a', 'b'), ListExercises.findDuplicates("aabbc"));
    }

    @Test
    void findDuplicatesNone() {
        assertEquals(Arrays.asList(), ListExercises.findDuplicates("abc"));
    }
}
