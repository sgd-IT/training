package com.training.day05;

/**
 * Day 5 - Q3: LoudGreeter 继承 Greeter（你来补全）
 *
 * 你要做一件事：
 *   重写 greet(String name)：返回 "HELLO, " + name.toUpperCase() + "!"
 *   必须加 @Override 注解（告诉编译器"我是要重写父类方法"——
 *   如果父类没有同名同参方法，编译器会报错，帮你抓笔误）
 *
 * 写完后跑测试，重点看 loudNoArgDispatchesToOverride 这个用例：
 *   new LoudGreeter().greet() —— 走的是父类无参版，
 *   但父类无参版内部调 greet("friend") 会执行谁的版本？测试结果告诉你
 *
 * 规则：先写注释版伪代码，再写实现。
 */
public class LoudGreeter extends Greeter {

    // TODO: @Override public String greet(String name) { ... }
    @Override
    public String greet(String name) {
        return "HELLO, " + name.toUpperCase() + "!";
    }

    public static void main(String[] args) {
        LoudGreeter loudGreeter = new LoudGreeter();
        System.out.println(loudGreeter.greet("friend"));
    }
}
