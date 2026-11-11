package com.training.day19;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 19 测试 —— 契约都在断言消息里，先读再动手。
 * <p>
 * 10 个随 TODO 变绿；最后 5 个是**天然绿**（不用写实现，考概念）。
 * <p>
 * ⚠️ 比 byte[] 一律用 {@code assertArrayEquals}，用 {@code assertEquals} 比的是**引用**。
 */
class ProtocolLabTest {

    // ═══════════════════════════════════════════════════════════
    // Q1 · 大端序写
    // ═══════════════════════════════════════════════════════════

    @Test
    void writesBigEndian() {
        assertArrayEquals(new byte[]{0x12, 0x34, 0x56, 0x78}, ProtocolLab.intToBytes(0x12345678),
                "大端序：高位字节先走");
        assertArrayEquals(new byte[]{0, 0, 0, 1}, ProtocolLab.intToBytes(1));
        assertArrayEquals(new byte[]{0, 0, 1, 0}, ProtocolLab.intToBytes(256));
    }

    @Test
    void writesNegativeWithoutLosingSignBits() {
        assertArrayEquals(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
                ProtocolLab.intToBytes(-1), "-1 的四个字节全是 0xFF");
        assertArrayEquals(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x80},
                ProtocolLab.intToBytes(Integer.MIN_VALUE), "0x80000000");
    }

    // ═══════════════════════════════════════════════════════════
    // Q2 · 大端序读
    // ═══════════════════════════════════════════════════════════

    @Test
    void readsBigEndian() {
        assertEquals(256, ProtocolLab.bytesToInt(new byte[]{0, 0, 1, 0}));
        assertEquals(0x12345678, ProtocolLab.bytesToInt(new byte[]{0x12, 0x34, 0x56, 0x78}));
    }

    @Test
    void readsNegativeOnlyIfYouMaskedEveryByte() {
        assertEquals(-1, ProtocolLab.bytesToInt(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}),
                "不 & 0xFF 的话这里会变成一个错的正数 —— 静悄悄地错，最难查");
        assertEquals(Integer.MIN_VALUE,
                ProtocolLab.bytesToInt(new byte[]{(byte) 0x80, 0, 0, 0}));
    }

    @Test
    void roundTripsEveryInt() {
        int[] samples = {0, 1, -1, 256, -256, 0x12345678, Integer.MAX_VALUE, Integer.MIN_VALUE};
        for (int v : samples) {
            assertEquals(v, ProtocolLab.bytesToInt(ProtocolLab.intToBytes(v)),
                    "往返失败：原值 " + v);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // Q3 · MQTT 变长整数
    // ═══════════════════════════════════════════════════════════

    @Test
    void encodesOneByteWhenItFitsInSevenBits() {
        assertArrayEquals(new byte[]{0x00}, ProtocolLab.mqttRemainingLength(0));
        assertArrayEquals(new byte[]{0x7F}, ProtocolLab.mqttRemainingLength(127),
                "127 正好塞进 7 位，一个字节搞定");
    }

    @Test
    void setsTheContinuationBitWhenMoreBytesFollow() {
        assertArrayEquals(new byte[]{(byte) 0x80, 0x01}, ProtocolLab.mqttRemainingLength(128),
                "128 的低 7 位是 0 → 第一字节 0x80（续位=1）；剩下 1 → 第二字节 0x01");
        assertArrayEquals(new byte[]{(byte) 0xFF, 0x7F}, ProtocolLab.mqttRemainingLength(16383),
                "16383 是两字节能表达的最大值");
    }

    @Test
    void growsToThreeAndFourBytes() {
        assertArrayEquals(new byte[]{(byte) 0x80, (byte) 0x80, 0x01},
                ProtocolLab.mqttRemainingLength(16384), "16384 需要三个字节");
        assertArrayEquals(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F},
                ProtocolLab.mqttRemainingLength(268435455), "协议规定的上限");
    }

    // ═══════════════════════════════════════════════════════════
    // Q4 · RESP 数组
    // ═══════════════════════════════════════════════════════════

    @Test
    void encodesRespBulkArray() {
        byte[] actual = ProtocolLab.respBulkArray(List.of("SET", "k", "v"));
        byte[] expected = "*3\r\n$3\r\nSET\r\n$1\r\nk\r\n$1\r\nv\r\n".getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(expected, actual);
    }

    @Test
    void countsUtf8BytesNotChars() {
        byte[] actual = ProtocolLab.respBulkArray(List.of("中"));
        byte[] expected = "*1\r\n$3\r\n中\r\n".getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(expected, actual,
                "\"中\".length() 是 1，但 UTF-8 占 3 个字节 —— 长度前缀写的是字节数");
    }

    // ═══════════════════════════════════════════════════════════
    // 天然绿 · 不用写 TODO 就能过，考的是概念
    // ═══════════════════════════════════════════════════════════

    @Test
    void byteIsSignedSoYouMustMask() {
        byte b = (byte) 0xFF;
        assertEquals(-1, b, "(byte) 0xFF 是 -1，不是 255");
        assertEquals(255, b & 0xFF, "& 0xFF 之后才是 255");
        assertEquals(-1, (int) b, "直接转 int 会保留符号");
    }

    @Test
    void utf8ByteLengthDiffersFromCharCount() {
        assertEquals(1, "中".length());
        assertEquals(3, "中".getBytes(StandardCharsets.UTF_8).length,
                "协议里的长度一律是字节数");
    }

    @Test
    void shiftRightVsUnsignedShiftRight() {
        assertEquals(-1, -1 >> 8, ">> 是算术右移：负数右移还在左边补 1");
        assertEquals(0x00FFFFFF, -1 >>> 8, ">>> 是无符号右移：左边补 0");
    }

    @Test
    void arraysMustBeComparedWithArrayEquals() {
        byte[] a = {1, 2};
        byte[] b = {1, 2};
        assertNotEquals(a, b, "assertEquals 比的是【引用】，两个不同的数组永远不相等");
        assertArrayEquals(a, b, "比内容必须用 assertArrayEquals");
    }

    @Test
    void respSimpleStringIsTheEasiestFrame() {
        byte[] ok = "+OK\r\n".getBytes(StandardCharsets.UTF_8);
        assertEquals(5, ok.length);
        assertEquals('+', (char) ok[0]);
        assertEquals('\r', (char) ok[3], "RESP 的所有行都以 CRLF 结尾");
    }
}
