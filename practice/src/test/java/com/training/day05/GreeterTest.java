package com.training.day05;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreeterTest {

    @Test
    void baseGreeterUnchanged() {
        assertEquals("Hello, tom", new Greeter().greet("tom"));
    }

    @Test
    void loudOverrideWorks() {
        // LoudGreeter 重写了 greet(String)
        assertEquals("HELLO, TOM!", new LoudGreeter().greet("tom"));
    }

    @Test
    void noArgDispatchToOverride() {
        // 无参 greet() 定义在父类里，内部调 greet("friend")
        // 但对象是 LoudGreeter → 实际执行 LoudGreeter 的重写版（多态！）
        assertEquals("HELLO, FRIEND!", new LoudGreeter().greet());
    }
}
