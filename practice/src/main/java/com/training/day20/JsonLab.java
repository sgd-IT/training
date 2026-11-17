package com.training.day20;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Day 20 - Q1~Q4: JsonLab —— JSON + HTTP 客户端：**Agent 调模型就是这个动作**
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ★ 必读：今天开工前先把这一整段读完（考点全在这里）
 * ═══════════════════════════════════════════════════════════
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 一、JSON 就是「对象 ⇄ 字符串」的翻译
 * ═══════════════════════════════════════════════════════════
 * 调 OpenAI / 通义 / 任何模型，收发的全是 JSON：请求体是 JSON，响应体也是 JSON。
 * Java 侧的标准工具是 **Jackson**（Spring Boot 内置的就是它）。
 * 两个核心类：
 * <pre>
 *   ObjectMapper          「翻译机」，创建一次可以复用（线程安全）
 *   JsonProcessingException  受检异常 —— 解析失败必须处理（Day 15 的知识点在这复现）
 * </pre>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 二、★ record 天生就能被 Jackson 处理（2.12+）
 * ═══════════════════════════════════════════════════════════
 * record 的**组件名**就是 JSON 的字段名，不需要写 getter。下面这个：
 * <pre>
 *   record Point(int x, int y) {}
 *   序列化 → {"x":1,"y":2}
 * </pre>
 * 能这样，是因为 record 的访问器（`x()`、`y()`）和构造器都带名字信息 ——
 * Day 14 学的东西在这里直接用上。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 三、★★ 未知字段默认会**抛异常**（今天第一个坑）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   你定义为 record Point(int x, int y)
 *   对方返回 {"x":1,"y":2,"z":3}
 *   → UnrecognizedPropertyException（JsonProcessingException 的子类）
 * </pre>
 * 这由 `DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES` 控制，**默认是 true**。
 * 真实场景里对方接口加了个字段，你的服务直接 500 —— 这是线上真实事故的形状。
 * 想容忍就配 `configure(FAIL_ON_UNKNOWN_PROPERTIES, false)`，但要清楚你在放弃什么。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 四、★★ 泛型擦除在这里复发（今天第二个坑，也是最重要的一关）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   mapper.readValue(json, List.class)                    → List&lt;LinkedHashMap&gt;   ← 不是 List&lt;Point&gt;！
 *   mapper.readValue(json, new TypeReference&lt;List&lt;Point&gt;&gt;(){})  → List&lt;Point&gt;      ← 正确
 * </pre>
 * 因为 `List.class` 是个**擦除后的 Class 对象**，里面没有 `Point` 这个信息 ——
 * 和 Day 14 的 `checkcast` 是同一个机制。`TypeReference` 的原理是**匿名子类**：
 * 泛型信息写在了子类的签名里，运行期还能通过反射读到。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 五、★ HTTP：JDK 自带 HttpClient，不用加依赖
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   HttpClient client = HttpClient.newHttpClient();
 *   HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
 *   HttpResponse&lt;String&gt; resp = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
 * </pre>
 * 两个要点：① `send` 要处理 `IOException` 和 `InterruptedException`（都是受检的）；
 * ② 读 body **必须显式指定 UTF-8** —— `ofString()` 不带参数用的是默认编码。
 * 另外 JDK 21 起 `HttpClient` 实现了 `AutoCloseable`，可以 try-with-resources。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q1: toJson       对象 → JSON 字符串
 *   TODO Q2: parsePoint   JSON 字符串 → Point（未知字段要让它抛）
 *   TODO Q3: parsePoints  JSON 数组 → List&lt;Point&gt;（必须 TypeReference）
 *   TODO Q4: fetchBody    GET 一个 URL，按 UTF-8 把 body 读成 String
 * <p>
 * ⚠️ 先预测，再跑（写【输出值】）：
 * <pre>
 * // 预测：toJson(new Point(1, 2)) 的输出 = ?{"x":1,"y":2}
 * // 预测：parsePoint("{\"x\":1,\"y\":2,\"z\":3}") 会 →{\"x\":1,\"y\":2,\"z\":3}
 * // 预测：用 List.class 读数组后，第 0 个元素的 getClass().getName() = null
 * // 预测：fetchBody 拿到 {"msg":"你好"} 之后，String 里是"你好"还是乱码 → "你好"
 * </pre>
 */
public class JsonLab {

    /**
     * 今天的数据载体。record 的组件名 = JSON 字段名。
     */
    public record Point(int x, int y) {
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q1
    // ═══════════════════════════════════════════════════════════
    /**
     * 把任意对象序列化成 JSON 字符串。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code toJson(new Point(1, 2))} → {@code {"x":1,"y":2}}（顺序就是组件声明顺序）</li>
     *   <li>{@code toJson(List.of(new Point(1,2), new Point(3,4)))} → {@code [{"x":1,"y":2},{"x":3,"y":4}]}</li>
     * </ul>
     * <p>
     * ⚠️ 签名里带着 {@code throws JsonProcessingException} —— 这是受检异常，
     * 调用方必须处理。别用 `catch (Exception e) {}` 把它吞掉。
     */
    public static String toJson(Object value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(value);
        return json;
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q2
    // ═══════════════════════════════════════════════════════════
    /**
     * 把 JSON 字符串解析成 {@link Point}。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code {"x":1,"y":2}} → {@code Point(1, 2)}</li>
     *   <li>{@code {"x":1,"y":2,"z":3}} → 抛 {@code JsonProcessingException}（**别去关那个开关**）</li>
     * </ul>
     * <p>
     * ⚠️ 第二条是今天的坑：默认配置下未知字段就是异常。你要做的是**别拦它**。
     */
    public static Point parsePoint(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Point point = mapper.readValue(json, Point.class);
        return point;
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q3
    // ═══════════════════════════════════════════════════════════
    /**
     * 把 JSON 数组解析成 {@code List<Point>}。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code [{"x":1,"y":2},{"x":3,"y":4}]} → 两个 {@link Point}，顺序不变</li>
     *   <li>返回的 List 里，元素必须是 {@code Point}，**不能是 Map**</li>
     * </ul>
     * <p>
     * ⚠️ 这是今天最难的一题。用 {@code List.class} 一定失败（拿到的是 {@code LinkedHashMap}），
     * 因为 `List.class` 里没有 `Point` 的信息 —— Day 14 的类型擦除。
     * <p>
     * 提示：`new TypeReference<List<Point>>() {}` —— 注意那对**空的**大括号不是装饰。
     */
    public static List<Point> parsePoints(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return  mapper.readValue(json, new TypeReference<List<Point>>() {
        });
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q4
    // ═══════════════════════════════════════════════════════════
    /**
     * 对 {@code url} 发一个 GET 请求，把响应体按 **UTF-8** 读成 String 返回。
     * <p>
     * 契约：
     * <ul>
     *   <li>测试会起一个本地服务，返回 {@code {"msg":"你好"}}</li>
     *   <li>返回值必须与之**完全相等**，中文不许乱码</li>
     * </ul>
     * <p>
     * ⚠️ 两个受检异常（`IOException` / `InterruptedException`）都在签名里，
     * 直接往外抛；`InterruptedException` 的规矩是**别吞**。
     */
    public static String fetchBody(String url) throws java.io.IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }
}
