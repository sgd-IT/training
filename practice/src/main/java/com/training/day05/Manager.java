package com.training.day05;

import com.training.day03.Employee;

/**
 * Day 5 - Q4: Manager 继承 day03 的 Employee（跨包继承，你来补全）
 *
 * 背景：Employee（Day 3 写的）字段全是 private —— 子类 Manager 碰不到！
 * 所以 Manager 只能通过 super 和继承来的方法干活。这正好复习封装。
 *
 * 你要加三样东西：
 *   1. private final double bonus（奖金）字段 + getBonus()
 *   2. 构造器 Manager(String name, double salary, double bonus)：
 *      - super(name, salary) 第一行（复用 Employee 的校验！name 空、salary 负都会抛）
 *      - 校验 bonus < 0 → IllegalArgumentException("奖金不能为负")
 *      - 赋值 this.bonus
 *   3. 重写 annualSalary()（Employee 里是 salary * 12）：
 *      Manager 年薪 = 基本年薪 + 奖金 → super.annualSalary() + bonus
 *      （super.方法名() = 调用父类的该方法，别自己重算 salary*12！）
 *
 * 写完想想：new Manager("", 10000, 0) 会抛异常吗？谁抛的？
 *
 * 规则：先写注释版伪代码，再写实现。
 */
public class Manager extends Employee {
    private final double bonus;

    public Manager(String name, double salary, double bonus) {
        super(name, salary);
        // TODO: 校验 bonus >= 0
        if (bonus < 0) {
            throw new IllegalArgumentException("奖金不能为负");
        }
        // TODO: 赋值 this.bonus
        this.bonus = bonus;
    }

    // TODO: private final double bonus 字段声明

    public double getBonus() {
        return this.bonus; // TODO: 返回 bonus
    }

    @Override
    public double annualSalary() {
        return super.annualSalary() + getBonus(); // TODO: 改成 super.annualSalary() + bonus
    }
}
