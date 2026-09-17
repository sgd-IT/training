package com.training.day03;

/**
 * Day 3 - Q6: Employee 类 —— 构造方法重载 + this() 委托
 * <p>
 * 昨天的套路继续用：先校验后赋值、private 字段。
 * 今天新增两个知识点：
 * 1. 构造方法重载：两个构造方法，参数少的用 this(...) 委托参数多的
 * 2. 方法重载互相调用：annualSalary() 委托 annualSalary(12)，不复制代码
 * <p>
 * 要求：
 * - 两个 private 字段：String name、double salary
 * - Employee(String name, double salary)：
 * name null/空 → IllegalArgumentException("姓名不能为空")
 * salary < 0   → IllegalArgumentException("工资不能为负")
 * 校验通过才赋值
 * - Employee(String name)：this(name, 0) —— 默认工资 0（委托！）
 * - getName() / getSalary()
 * - void payRaise(double amount)：amount <= 0 → IllegalArgumentException("加薪必须为正数")；
 * 否则 salary += amount
 * - double annualSalary()：return annualSalary(12)（委托！）
 * - double annualSalary(double months)：months <= 0 → IllegalArgumentException("月数必须为正数")；
 * return salary * months
 */
public class Employee {

    // TODO: 两个 private 字段（String name、double salary）
    private String name;
    private double salary;

    // TODO: 构造方法 1：先校验后赋值
    public Employee(String name, double salary) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("姓名不能为空");
        if (salary < 0)
            throw new IllegalArgumentException("工资不能为负");
        this.name = name;
        this.salary = salary;
    }

    // TODO: 构造方法 2：this(name, 0) 委托
    public Employee(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        this.name = name;
        this.salary = 0;
    }

    public String getName() {
        return this.name; // TODO: 返回 name
    }

    public double getSalary() {
        return this.salary; // TODO: 返回 salary
    }

    public void payRaise(double amount) {
        // TODO: 伪代码：1. 校验 amount  2. 加薪
        if (amount <= 0)
            throw new IllegalArgumentException("加薪必须为正数");
        this.salary += amount;
    }

    public double annualSalary() {
        return annualSalary(12); // TODO: 委托 annualSalary(12)
    }

    public double annualSalary(double months) {
        if (months <= 0) {
            throw new IllegalArgumentException("月数必须为正数");
        }
        return this.salary * months; // TODO: 伪代码：1. 校验 months  2. 返回 salary * months
    }
}
