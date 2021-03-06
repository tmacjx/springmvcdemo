package com.bokecc.util;

import java.io.UnsupportedEncodingException;


public class Base64Util {

    private static final byte[] CHAR_SET;

    static {
        final String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        byte[] cs;

        try {
            cs = s.getBytes("ASCII");
        } catch (final UnsupportedEncodingException ex) {
            // should never happen
            cs = s.getBytes();
        }
        CHAR_SET = cs;
    }

    private static final byte[] BASE64INDEXES = {
            64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 62, 64, 64, 64, 63,
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 64, 64, 64, 64, 64,
            64, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 64, 64, 64, 64, 64,
            64, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64,
            64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64
    };

    /**
     * Encodes array of bytes using base64 encoding.
     *
     * @param buffer Array of bytes to be encoded.
     * @return Encoded result as an array of bytes.
     */
    public static byte[] encode(final byte[] buffer) {
        final int ccount = buffer.length / 3;
        final int rest = buffer.length % 3;
        final byte[] result = new byte[(ccount + (rest > 0 ? 1 : 0)) * 4];

        for (int i = 0; i < ccount; i++) {
            result[i * 4] = CHAR_SET[(buffer[i * 3] >> 2) & 0xff];
            result[i * 4 + 1] = CHAR_SET[(((buffer[i * 3] & 0x03) << 4) | (buffer[i * 3 + 1] >> 4)) & 0xff];
            result[i * 4 + 2] = CHAR_SET[(((buffer[i * 3 + 1] & 0x0f) << 2) | (buffer[i * 3 + 2] >> 6)) & 0xff];
            result[i * 4 + 3] = CHAR_SET[buffer[i * 3 + 2] & 0x3f];
        }

        int temp = 0;

        if (rest > 0) {
            if (rest == 2) {
                result[ccount * 4 + 2] = CHAR_SET[((buffer[ccount * 3 + 1] & 0x0f) << 2) & 0xff];
                temp = buffer[ccount * 3 + 1] >> 4;
            } else {
                result[ccount * 4 + 2] = CHAR_SET[CHAR_SET.length - 1];
            }
            result[ccount * 4 + 3] = CHAR_SET[CHAR_SET.length - 1];
            result[ccount * 4 + 1] = CHAR_SET[(((buffer[ccount * 3] & 0x03) << 4) | temp) & 0xff];
            result[ccount * 4] = CHAR_SET[(buffer[ccount * 3] >> 2) & 0xff];
        }

        return result;
    }

    /**
     * Decodes Base64 data into octects.
     *
     * @param buffer Byte array containing Base64 data
     * @return Array containing decoded data.
     */
    @SuppressWarnings("fallthrough")
    public static byte[] decode(final byte[] buffer) {
        if (buffer.length < 4 && (buffer.length % 4) != 0) {
            return new byte[0];
        }

        final int ccount = buffer.length / 4;
        final int paddingCount = (buffer[buffer.length - 1] == '=' ? 1 : 0) + (buffer[buffer.length - 2] == '=' ? 1 : 0);
        final byte[] result = new byte[3 * (ccount - 1) + (3 - paddingCount)];

        for (int i = 0; i < (ccount - 1); i++) {
            result[i * 3] = (byte) ((BASE64INDEXES[buffer[i * 4]] << 2) | (BASE64INDEXES[buffer[i * 4 + 1]] >> 4));
            result[i * 3 + 1] = (byte) ((BASE64INDEXES[buffer[i * 4 + 1]] << 4) | (BASE64INDEXES[buffer[i * 4 + 2]] >> 2));
            result[i * 3 + 2] = (byte) ((BASE64INDEXES[buffer[i * 4 + 2]] << 6) | BASE64INDEXES[buffer[i * 4 + 3]]);
        }

        final int i = ccount - 1;
        switch (paddingCount) {
            case 0:
                result[i * 3 + 2] = (byte) ((BASE64INDEXES[buffer[i * 4 + 2]] << 6) | BASE64INDEXES[buffer[i * 4 + 3]]);
                result[i * 3 + 1] = (byte) ((BASE64INDEXES[buffer[i * 4 + 1]] << 4) | (BASE64INDEXES[buffer[i * 4 + 2]] >> 2));
                result[i * 3] = (byte) ((BASE64INDEXES[buffer[i * 4]] << 2) | (BASE64INDEXES[buffer[i * 4 + 1]] >> 4));
                break;
            case 1:
                result[i * 3 + 1] = (byte) ((BASE64INDEXES[buffer[i * 4 + 1]] << 4) | (BASE64INDEXES[buffer[i * 4 + 2]] >> 2));
                result[i * 3] = (byte) ((BASE64INDEXES[buffer[i * 4]] << 2) | (BASE64INDEXES[buffer[i * 4 + 1]] >> 4));
                break;
            case 2:
                result[i * 3] = (byte) ((BASE64INDEXES[buffer[i * 4]] << 2) | (BASE64INDEXES[buffer[i * 4 + 1]] >> 4));
                break;
        }

        return result;
    }

    /**
     * Encodes array of bytes using base64 encoding and returns the result as a string.
     *
     * @param buffer Array of bytes to be encoded.
     * @return Resulting encoded string.
     */
    public static String encodeAsString(final byte[] buffer) {
        final byte[] result = encode(buffer);
        try {
            return new String(result, "ASCII");
        } catch (final UnsupportedEncodingException ex) {
            // should never happen
            return new String(result);
        }
    }

    /**
     * Encodes a string using base64 and returns the result as another string.
     *
     * @param text String to be encoded.
     * @return Resulting encoded string.
     */
    public static String encodeAsString(final String text) {
        return encodeAsString(text.getBytes());
    }

    /**
     * Decodes array of bytes using base64 decoding and returns the result as a string.
     *
     * @param buffer Array of bytes to be decoded.
     * @return Resulting decoded string.
     */
    public static String decodeAsString(final byte[] buffer) {
        final byte[] result = decode(buffer);
        try {
            return new String(result, "ASCII");
        } catch (final UnsupportedEncodingException ex) {
            // should never happen
            return new String(result);
        }
    }

    /**
     * Decodes a string using base64 and returns the result as another string.
     *
     * @param text String to be decoded.
     * @return Resulting decoded string.
     */
    public static String decodeAsString(final String text) {
        return decodeAsString(text.getBytes());
    }

}
