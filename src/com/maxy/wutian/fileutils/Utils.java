package com.maxy.wutian.fileutils;


import java.io.Closeable;

public class Utils {
    public static void close(Closeable cursor) {
        try {
            if (cursor != null)
                cursor.close();
        } catch (Exception e){}
    }
}
