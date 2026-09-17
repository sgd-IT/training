package com.training.day01;

/**
 * Day 1 摸底测验 Q6（选做，约 8 分钟）：一个简单的银行账户。
 *
 * 要求：
 * 1. 字段必须 private，只能通过方法访问（封装）。
 * 2. 构造方法 BankAccount(String owner, double initialBalance)：
 *    - owner 为 null 或空字符串时，抛 IllegalArgumentException
 *    - initialBalance 为负数时，抛 IllegalArgumentException
 * 3. getOwner() 返回户主名；getBalance() 返回余额。
 * 4. deposit(double amount)：amount 必须大于 0，否则抛 IllegalArgumentException；成功则余额增加。
 * 5. withdraw(double amount)：amount 必须大于 0，否则抛 IllegalArgumentException；
 *    余额不足（不允许透支）时抛 IllegalStateException；成功则余额减少。
 */
public class BankAccount {

    // TODO: 在这里声明字段（private String owner; private double balance;）
    private String owner;
    private double balance;

    public BankAccount(String owner, double initialBalance) {
        this.owner = owner;
        this.balance = initialBalance;
        // TODO: 在这里写你的实现（校验 + 赋值）
        if (owner == null || owner.isEmpty()){
            throw new IllegalArgumentException();
        }
        if (initialBalance < 0){
            throw new IllegalArgumentException();
        }
    }

    public String getOwner() {
        // TODO: 在这里写你的实现
        return this.owner;
    }

    public double getBalance() {
        // TODO: 在这里写你的实现
        return this.balance;
    }

    public void deposit(double amount) {
        // TODO: 在这里写你的实现
        if (amount > 0){
            this.balance += amount;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void withdraw(double amount) {
        // TODO: 在这里写你的实现
        if (amount > 0){
            if (this.balance >= amount){
                this.balance -= amount;
            } else {
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
}
