package com.training.day20;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 20 测试 —— 契约都在断言消息里，先读再动手。
 * <p>
 * 8 个随 TODO 变绿；最后 4 个是**天然绿**（不用写实现，考概念）。
 * <p>
 * Q4 会起一个**本地** HTTP 服务（JDK 自带的 `com.sun.net.httpserver`），全程不联网。
 */
class JsonLabTest {

    static HttpServer server;
    static String base;

    @BeforeAll
    static void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/hello", exchange -> {
            byte[] body = "{\"msg\":\"你好\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        base = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // Q1 · 序列化
    // ═══════════════════════════════════════════════════════════

    @Test
    void serialisesOneRecord() throws JsonProcessingException {
        assertEquals("{\"x\":1,\"y\":2}", JsonLab.toJson(new JsonLab.Point(1, 2)),
                "record 的组件名就是 JSON 字段名，顺序按声明顺序");
    }

    @Test
    void serialisesACollection() throws JsonProcessingException {
        assertEquals("[{\"x\":1,\"y\":2},{\"x\":3,\"y\":4}]",
                JsonLab.toJson(List.of(new JsonLab.Point(1, 2), new JsonLab.Point(3, 4))),
                "集合会被一视同仁地展开成数组");
    }

    // ═══════════════════════════════════════════════════════════
    // Q2 · 反序列化单个对象
    // ═══════════════════════════════════════════════════════════

    @Test
    void parsesOneRecord() throws JsonProcessingException {
        JsonLab.Point p = JsonLab.parsePoint("{\"x\":1,\"y\":2}");
        assertEquals(1, p.x());
        assertEquals(2, p.y());
    }

    @Test
    void unknownFieldExplodes() {
        assertThrows(JsonProcessingException.class,
                () -> JsonLab.parsePoint("{\"x\":1,\"y\":2,\"z\":3}"),
                "默认 FAIL_ON_UNKNOWN_PROPERTIES=true —— 对方加一个字段，你的服务就 500，这是线上事故的形状");
    }

    // ═══════════════════════════════════════════════════════════
    // Q3 · 泛型集合（今天最难）
    // ═══════════════════════════════════════════════════════════

    @Test
    void parsesListOfPoints() throws JsonProcessingException {
        List<JsonLab.Point> points = JsonLab.parsePoints("[{\"x\":1,\"y\":2},{\"x\":3,\"y\":4}]");
        assertEquals(2, points.size());
        assertEquals(1, points.get(0).x());
        assertEquals(3, points.get(1).x(), "顺序必须保持");
    }

    @Test
    void elementIsReallyAPointNotAMap() throws JsonProcessingException {
        List<JsonLab.Point> points = JsonLab.parsePoints("[{\"x\":1,\"y\":2}]");
        assertEquals(JsonLab.Point.class, points.get(0).getClass(),
                "用 List.class 读，这里会是 LinkedHashMap —— 泛型擦除");
    }

    // ═══════════════════════════════════════════════════════════
    // Q4 · HTTP（本地服务，不联网）
    // ═══════════════════════════════════════════════════════════

    @Test
    void fetchesTheBody() throws Exception {
        assertEquals("{\"msg\":\"你好\"}", JsonLab.fetchBody(base + "/hello"));
    }

    @Test
    void chineseDoesNotTurnIntoGarbage() throws Exception {
        String body = JsonLab.fetchBody(base + "/hello");
        assertTrue(body.contains("你好"),
                "中文不能变乱码 —— body 一定要按 UTF-8 解码，实际拿到：" + body);
    }

    // ═══════════════════════════════════════════════════════════
    // 天然绿 · 不用写 TODO 就能过，考的是概念
    // ═══════════════════════════════════════════════════════════

    @Test
    void rawListGivesMapsBecauseOfTypeErasure() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Object raw = mapper.readValue("[{\"x\":1,\"y\":2}]", List.class);
        assertEquals(LinkedHashMap.class, ((List<?>) raw).get(0).getClass(),
                "List.class 里没有 Point 的信息，Jackson 只能退而给你 Map");

        List<JsonLab.Point> typed = mapper.readValue("[{\"x\":1,\"y\":2}]",
                new TypeReference<List<JsonLab.Point>>() {
                });
        assertEquals(JsonLab.Point.class, typed.get(0).getClass(),
                "TypeReference 是匿名子类：泛型信息写在子类签名里，运行期反射还能读到");
    }

    @Test
    void unknownFieldCanBeToleratedButThinkTwice() throws Exception {
        ObjectMapper lenient = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonLab.Point p = lenient.readValue("{\"x\":1,\"y\":2,\"z\":3}", JsonLab.Point.class);
        assertEquals(1, p.x(), "关掉开关能容忍未知字段，但接口悄悄加字段时，这会把 bug 藏起来");
    }

    @Test
    void jsonEscapesQuotesAndNewlines() throws Exception {
        String s = new ObjectMapper().writeValueAsString("a\"b\nc");
        assertEquals("\"a\\\"b\\nc\"", s,
                "引号和换行都要转义 —— 手拼 JSON 字符串 99% 会在这里出错，所以用库");
    }

    @Test
    void httpClientIsAutoCloseableSinceJdk21() {
        assertTrue(AutoCloseable.class.isAssignableFrom(HttpClient.class),
                "JDK 21 起 HttpClient 实现了 AutoCloseable，可以 try-with-resources");
    }
}
