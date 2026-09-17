package com.training.day02;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ArrayExercisesTest {

    @Test
    void reverseOddLength() {
        assertArrayEquals(new int[]{5, 4, 3, 2, 1},
                ArrayExercises.reverse(new int[]{1, 2, 3, 4, 5}));
    }

    @Test
    void reverseReturnsSameArray() {
        int[] nums = {1, 2, 3, 4};
        int[] result = ArrayExercises.reverse(nums);
        // 原地反转：不能 new 新数组，返回的必须是同一个数组对象
        assertArrayEquals(nums, result);
    }

    @Test
    void maxAndIndexBasic() {
        assertArrayEquals(new int[]{9, 2},
                ArrayExercises.maxAndIndex(new int[]{3, 5, 9, 1}));
    }

    @Test
    void maxAndIndexFirstWhenTied() {
        // 两个 9，取第一个出现的下标 1（不是 3）
        assertArrayEquals(new int[]{9, 1},
                ArrayExercises.maxAndIndex(new int[]{3, 9, 4, 9, 1}));
    }
}
