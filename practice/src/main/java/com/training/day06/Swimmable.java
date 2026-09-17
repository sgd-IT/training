package com.training.day06;

/**
 * Day 6 - 接口教科书（已实现）
 *
 * 接口 = 一份"纯契约"：只规定"必须有什么方法"，不管"怎么实现"。
 *
 * 接口的三条铁律：
 *   1. 用 interface 关键字（不是 class）
 *   2. 方法默认就是 public abstract——所以下面的 swim() 不用写这两个词
 *   3. 接口不能有实例字段，也不能有构造器（它描述的是"能力"，不是"东西"）
 *
 * ⚠️ 实现接口用 implements，不是 extends！这是新手最容易写错的地方。
 *
 * 和抽象类的分工：
 *   抽象类 = "你是什么"（Animal is-a 动物），可以有字段和构造器
 *   接口   = "你能干什么"（Swimmable 会游泳），只能是纯契约
 */
public interface Swimmable {

    /** 接口方法：默认 public abstract，写不写都一样 */
    String swim();
}
