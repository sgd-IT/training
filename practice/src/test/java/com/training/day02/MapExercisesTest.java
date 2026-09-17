package com.training.day02;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapExercisesTest {

    @Test
    void mostFrequentBasic() {
        assertEquals('l', MapExercises.mostFrequentChar("hello"));
    }

    @Test
    void mostFrequentTieFirstReached() {
        // a 和 b 都出现 2 次，a 先达到 2 次，所以是 'a'
        assertEquals('a', MapExercises.mostFrequentChar("aabbc"));
    }

    @Test
    void mostFrequentSingleChar() {
        assertEquals('x', MapExercises.mostFrequentChar("x"));
    }
}
