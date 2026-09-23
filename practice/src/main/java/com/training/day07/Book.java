package com.training.day07;

import java.util.Objects;

/**
 * Day 7 - Q2: Book —— 自己独立再写一遍（你来补全，别看 Point）
 *
 * 和 Point 同一个套路，但这题有三个新东西：
 *
 * ┌───────────────────────────────────────────────────────────┐
 * │ 新东西 1：字段里有 String                                  │
 * └───────────────────────────────────────────────────────────┘
 *   比 String 千万不能用 == ！那比的是地址。
 *   但也不能直接写 title.equals(o.title) —— 万一 title 是 null 就 NPE 了。
 *
 *   正确写法：Objects.equals(title, o.title)
 *   这个工具方法会自己处理 null：两边都 null 也返回 true。
 *
 * ┌───────────────────────────────────────────────────────────┐
 * │ 新东西 2：hashCode 要组合三个字段                          │
 * └───────────────────────────────────────────────────────────┘
 *   直接 Objects.hash(title, author, year) 就行。
 *   （Objects.hash 的参数是可变参数 Object...，Day 4 学过 varargs）
 *
 * ┌───────────────────────────────────────────────────────────┐
 * │ 新东西 3：toString 格式自己定                              │
 * └───────────────────────────────────────────────────────────┘
 *   new Book("三体", "刘慈欣", 2008).toString()  →  "三体 / 刘慈欣 / 2008"
 *   格式就是： 书名 + " / " + 作者 + " / " + 年份
 *
 * ────────────────────────────────────────────────────────────
 * 不能偷懒的地方
 * ────────────────────────────────────────────────────────────
 *   ⚠️ equals 的参数必须是 Object，并且加 @Override（否则就是重载，不是重写）
 *   ⚠️ equals 的四步一步都不能少：自反 → null → 类型 → 强转后比字段
 *
 * ⚠️ 两轮推演（写在注释里再动手）：
 *     推演1: b.equals(b)                        →
 *     推演2: b.equals(new Book("三体","刘慈欣",2008)) →
 *     推演3: b.equals(null)                     →
 *     推演4: b.equals("字符串")                 →
 */
public class Book {

    private final String title;
    private final String author;
    private final int year;

    public Book(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    // TODO Q2-1: 重写 toString()
    @Override
    public String toString() {
        return title + " / " + author + " / " + year;
    }

    // TODO Q2-2: 重写 equals(Object o)
    @Override
    public boolean equals(Object o) {
        if (Objects.equals(this, o)){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        return true;
    }

    // TODO Q2-3: 重写 hashCode()
    @Override
    public int hashCode(){
        return Objects.hash(title, author, year);
    }

}