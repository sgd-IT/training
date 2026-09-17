package com.training.day01;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Day 1 摸底测验（限时 30 分钟，无 AI，自己写）
 * <p>
 * 规则：
 * 1. 只修改每个方法里标注 TODO 的地方，不要改方法签名、类名、包名。
 * 2. 先读测试类 BasicsTest，明白每个方法要干什么，再动手。
 * 3. 写完后运行测试，把失败输出原样贴回给教练。
 * <p>
 * 提示：可以用 java.util 的类（Map/List 等），也可以自己加 import。
 */
public class Basics {

    /**
     * Q1（热身，约 3 分钟）：返回数组中所有偶数的和。
     * 例：sumEven(new int[]{1, 2, 3, 4, 5}) == 6
     * sumEven(new int[]{2, 4, 6}) == 12
     * sumEven(new int[]{}) == 0
     */
    public static int sumEven(int[] nums) {
        // TODO: 在这里写你的实现
        int sum = 0;
        for (int num : nums) {
            if (num % 2 == 0) {
                sum += num;
            }
        }
        return sum;
    }

    /**
     * Q2（热身，约 3 分钟）：统计一段英文文本中的单词个数。
     * 单词之间用空格分隔；文本可能有多余空格，也可能为空字符串。
     * 例：countWords("hello world") == 2
     * countWords("  a  b  ") == 2
     * countWords("") == 0
     */
    public static int countWords(String text) {
        // TODO: 在这里写你的实现
        int count = 0;
        String tt = text.trim();
        String[] words = tt.split("\\s+");
        if (!tt.isEmpty()) {
            for (String word : words){
                count++;
            }
        } else {
            return 0;
        }
        return count;
    }

    /**
     * Q3（约 7 分钟）：反转句子中单词的顺序。
     * 例：reverseWords("I am a coder") == "coder a am I"
     * reverseWords("hello world") == "world hello"
     * 思路提示：先拆成单词数组，再倒着拼回去。
     */
    public static String reverseWords(String sentence) {
        // TODO: 在这里写你的实现
        String[] words = sentence.split(" ");
        StringBuilder sb = new StringBuilder();
        for(int i = words.length - 1; i >= 0; i--){
                sb.append(words[i]);
                sb.append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Q4（约 7 分钟）：统计 List 中每个字符串出现的次数。
     * 例：frequencyMap(["a", "b", "a", "c", "b", "b"]) 返回 {"a":2, "b":3, "c":1}
     * 提示：遍历列表，用 Map 记次数；用 Map.getOrDefault 或先 containsKey 判断。
     */
    public static Map<String, Integer> frequencyMap(List<String> words) {
        // TODO: 在这里写你的实现
        Map<String, Integer> uniqueWords = new HashMap<String, Integer>();
        for (String word : words) {
            if (uniqueWords.containsKey(word)) {
                uniqueWords.put(word, uniqueWords.get(word) + 1);
            }else {
                uniqueWords.put(word, 1);
            }
        }
        return uniqueWords;
    }

    /**
     * Q5（约 8 分钟）：把字符串解析成整数，解析失败时返回 defaultValue。
     * 例：parseIntSafe("42", 0) == 42
     * parseIntSafe("abc", -1) == -1
     * parseIntSafe(null, 7) == 7
     * 提示：Integer.parseInt 在解析失败或传入 null 时会抛异常，需要捕获。
     */
    public static int parseIntSafe(String s, int defaultValue) {
        // TODO: 在这里写你的实现
        try{
            if (s == null) {
                return defaultValue;
            }
            return Integer.parseInt(s);
        }catch (NumberFormatException e){
            return defaultValue;
        }
    }
}
