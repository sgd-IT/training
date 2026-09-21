package com.training.day06;

/**
 * Day 6 - Q3: Duck —— 同时继承抽象类 + 实现多个接口（你来补全）
 *
 * 这是今天的核心一题。Duck 一只鸭子：
 *   - 它"是"动物      → extends Animal
 *   - 它"会"游泳      → implements Swimmable
 *   - 它"会"飞        → implements Flyable
 *
 * 注意类声明那行：extends 在前，implements 在后，接口之间用逗号隔开。
 *
 * 你要做三件事：
 *   1. speak() 返回 "嘎嘎"
 *   2. swim()  返回 "划水"
 *   3. fly()   返回 "扑棱翅膀"
 *
 * 三个方法都要 @Override 注解（实现接口方法也用它）。
 *
 * 写完想一想：
 *   Duck 有几条"身份"？它能被赋给哪几种类型的变量？
 *   （看 InterfaceTest 里的 sameObjectIsBothAnimalAndSwimmable 用例）
 *
 * 规则：先在文件顶部写 3-5 行伪代码注释，再写实现。
 */
public class Duck extends Animal implements Swimmable, Flyable {

    public Duck(String name) {
        super(name);
    }

    @Override
    public String speak() {

        return "嘎嘎";
    }

    @Override
    public String swim() {

        return "划水";
    }

    @Override
    public String fly() {

        return "扑棱翅膀";
    }
}
