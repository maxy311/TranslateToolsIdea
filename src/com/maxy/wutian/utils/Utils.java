package com.maxy.wutian.utils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * miscellaneous utilities methods that not belong to all specific utilities classes such as FileUtils/NumberUtils, etc.
 */
//TODO liufs: need refine
public final class Utils {
    private static final int BUFFER_SIZE_16K = 1024 * 16;

    private static String[] CHARS = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};


    // read everything in an input stream and return as string (trim-ed, and may apply optional utf8 conversion)
    public static String inputStreamToString(final InputStream is, final boolean sourceIsUTF8) throws IOException {
        InputStreamReader isr = sourceIsUTF8 ? new InputStreamReader(is, Charset.forName("UTF-8")) : new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null)
            sb.append(line);
        br.close();
        return sb.toString().trim();
    }

    // url encode a string with UTF-8 encoding
    public static String urlEncode(String src) {
        try {
            return URLEncoder.encode(src, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static int readBuffer(InputStream input, byte[] buffer) throws IOException {
        return readBuffer(input, buffer, 0, buffer.length);
    }

    /**
     * inputstream 读取byte[] buffer不能保证�?��完整读取，使用本方法可以保证填满buffer
     *
     * @param input
     * @param buffer
     * @param offset
     * @param length
     * @return
     * @throws IOException
     */
    public static int readBuffer(InputStream input, byte[] buffer, int offset, int length) throws IOException {
        int sum = 0;
        int r;
        while (length > 0 && (r = input.read(buffer, offset, length)) != -1) {
            sum += r;
            offset += r;
            length -= r;
        }
        return sum;
    }

    public static long max(long value1, long value2) {
        return (value1 > value2 ? value1 : value2);
    }

    public static int toInt(byte b) {
        return b & 0xFF;
    }

    public static byte[] toBytes(long value) {
        byte[] result = new byte[Long.SIZE / Byte.SIZE];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) value;
            value >>= Byte.SIZE;
        }
        return result;
    }

    public static int toInt(String sInt) {
        int result = -1;
        try {
            result = Integer.valueOf(sInt);
        } catch (Exception e) {
        }
        return result;
    }

    public static int toInt(String sInt, int radix) {
        int result = -1;
        try {
            result = Integer.valueOf(sInt, radix);
        } catch (Exception e) {
        }
        return result;
    }

    public static long toLong(String sLong) {
        long result = -1;
        try {
            result = Long.valueOf(sLong);
        } catch (Exception e) {
        }
        return result;
    }

    public static byte[] toBytes(int value) {
        byte[] result = new byte[Integer.SIZE / Byte.SIZE];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) value;
            value >>= Byte.SIZE;
        }
        return result;
    }

    public static int toInt(byte[] buffer, int start) {
        int result = 0;
        int end = Math.min(buffer.length, start + (Integer.SIZE / Byte.SIZE));
        int moved = 0;
        for (int i = start; i < end; i++) {
            result |= (buffer[i] & 0xFF) << moved;
            moved += Byte.SIZE;
        }
        return result;
    }

    public static int[] toIntArray(String[] args) {
        if (args == null)
            return null;
        int[] result = new int[args.length];
        try {
            for (int i = 0; i < args.length; i++) {
                result[i] = Integer.valueOf(args[i]);
            }
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    public static boolean isInt(String value) {
        try {
            Integer.valueOf(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isEquals(Object obj1, Object obj2) {
        boolean isNull01 = obj1 == null;
        boolean isNull02 = obj2 == null;

        if (isNull01 ^ isNull02)
            return false;
        if (isNull01 && isNull02)
            return true;
        return obj1.equals(obj2);
    }

    /**
     * close object quietly, catch and ignore all exceptions.
     *
     * @param object the closeable object like inputstream, outputstream, reader, writer, randomaccessfile.
     */
    public static void close(Closeable object) {
        if (object != null) {
            try {
                object.close();
            } catch (Throwable e) {
            }
        }
    }


    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        out.flush();
    }

}
