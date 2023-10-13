package com.moyujian.texas.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

    private static final String MSG_DIGEST_ALGORITHM = "MD5";

    private static final char[] HEX_CHARS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static MessageDigest digest;

    public static String getMd5Hex(String msg) {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance(MSG_DIGEST_ALGORITHM);
            } catch (NoSuchAlgorithmException ignore) {}
        }

        byte[] bytes = digest.digest(msg.getBytes(StandardCharsets.UTF_8));
        return encodeHex(bytes);
    }

    private static String encodeHex(byte[] bytes) {
        char[] chars = new char[32];

        for(int i = 0; i < chars.length; i += 2) {
            byte b = bytes[i / 2];
            chars[i] = HEX_CHARS[b >>> 4 & 15];
            chars[i + 1] = HEX_CHARS[b & 15];
        }

        return new String(chars);
    }
}
