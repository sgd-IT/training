package com.training.day02;

/**
 * Day 2 - 数组练习
 * <p>
 * 规则：
 * 1. 先读对应的测试类 ArrayExercisesTest，弄清每个方法的期望
 * 2. 动手前，先在你的实现位置写「注释版伪代码」（用中文描述步骤，一行一个步骤）
 * 3. 不查 AI，只查 API 文档（查到的 API 必须自己敲一遍）
 */
public class ArrayExercises {

    /**
     * Q1: 原地反转数组
     * 要求：不新建数组，只交换元素位置，最后返回同一个数组对象
     * 示例：[1, 2, 3, 4, 5] → [5, 4, 3, 2, 1]
     * <p>
     * 思路提示：两个指针，一个从 0 往右，一个从 length-1 往左，
     * 只要左指针还在右指针左边，就交换这两个位置的值
     */
    public static int[] reverse(int[] nums) {
        // TODO: 先写伪代码注释（例如：// 1. 左指针从 0 开始，右指针从 length-1 开始），再写实现
        int left = 0;
        int right = nums.length - 1;
        while (left < right) {
            int temp = nums[left];
            nums[left] = nums[right];
            nums[right] = temp;
            left++;
            right--;
        }
        return nums;
    }

    /**
     * Q2: 找出最大值和它的下标
     * 要求：返回一个新数组 int[]{最大值, 下标}
     * 示例：[3, 9, 4, 9, 1] → [9, 1]
     * （有多个最大值时，取第一个出现的，下标 1 而不是 3）
     * <p>
     * 思路提示：先假设第一个元素就是最大值，然后从第 2 个元素开始逐个比较
     */
    public static int[] maxAndIndex(int[] nums) {
        // TODO: 先写伪代码注释，再写实现
        int max = nums[0];
        int index = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > max) {
                max = nums[i];
                index = i;
            }
        }
        return new int[]{max, index};
    }
}
