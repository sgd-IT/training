package com.training.day05;

/**
 * Day 5 - 重载的活教材 Greeter（已实现——仔细读！）
 *
 * 注意两个方法同名 greet，参数不同——这就是「重载」（overload）：
 *   同一个类里，同名不同参，按实参自动选择。
 *
 * 关键点：无参版 greet() 委托给有参版 greet("friend")——
 *   调 greet() 时实际执行的是哪个 greet(String)？
 *   答案：跟着对象的真实类型走。如果对象是 Greeter 的子类 LoudGreeter，
 *   会执行 LoudGreeter 重写后的版本（先别管，写 LoudGreeter 时验证）
 */
public class Greeter {

    public String greet(String name) {
        return "Hello, " + name;
    }

    public String greet() {
        Greeter g = new LoudGreeter();
        System.out.println(g.greet("tom"));
        return greet("friend");
    }

    public static void main(String[] args) {
        Greeter greeter = new Greeter();
        greeter.greet();
    }

}
