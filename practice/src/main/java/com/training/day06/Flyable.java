package com.training.day06;

/**
 * Day 6 - 接口教科书（已实现）
 *
 * 和 Swimmable 一模一样的写法。它存在的意义在于：
 * 一个类可以 implements 多个接口（Duck 就同时会游泳和飞）。
 *
 * 对照记忆：
 *   extends    只能有一个（一个类只能有一个亲爹）
 *   implements 可以有任意多个（能力可以有很多种）
 */
public interface Flyable {

    String fly();
}
