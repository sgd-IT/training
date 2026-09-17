package com.training.day02;

/**
 * Day 2 - Q6: Student 类 —— 构造方法校验 + 封装进阶
 * <p>
 * 昨天的教训（今天要按这个写）：
 * 1. 构造方法里两件事：先校验参数，再赋值字段（昨天你写反了，先赋值后校验）
 * 2. 字段必须 private，对外只暴露方法
 * <p>
 * 要求：
 * - 两个 private 字段：String name、int score
 * - 构造方法 Student(String name, int score)：
 * name 为 null 或空字符串 → IllegalArgumentException("姓名不能为空")
 * score 不在 [0, 100]    → IllegalArgumentException("分数必须在0-100之间")
 * - getName() / getScore()
 * - raiseScore(int points)：points <= 0 → IllegalArgumentException("加分必须为正数")
 * 加分后超过 100 → IllegalArgumentException（在加分前先判断！）
 * 否则 score += points
 * - isPassed()：score >= 60 返回 true
 */
public class Student {
    private String name;
    private int score;

    // TODO: 声明两个 private 字段（String name、int score），
    //       然后写构造方法：先校验后赋值（伪代码：1. 校验 name 2. 校验 score 3. 赋值）
    public Student(String name, int score) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("分数必须在0-100之间");
        }
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name; // TODO: 改成返回 name
    }

    public int getScore() {
        return score; // TODO: 改成返回 score
    }

    public void raiseScore(int points) {
        // TODO: 伪代码：1. 校验 points <= 0  2. 校验加分后是否超过 100  3. 加分
        if (points <= 0) {
            throw new IllegalArgumentException("加分必须为正数");
        }
        if (score + points > 100) {
            throw new IllegalArgumentException("加分后超过100");
        }
        score += points;
    }

    public boolean isPassed() {
        if (score >= 60) {
            return true;
        } else {
            return false;
        }// TODO: 改成 score >= 60
    }
}
