package com.maxy.wutian.get;

public class TestGet {
    public static void main(String[] args) {
//        testTroy();
        testSHAREit();
    }

    private static void testSHAREit() {
        String projectPath = "/Users/maxy/Android/workspace/SHAREit";
        String outPutPath = "/Users/maxy/Desktop";
        String lastTag = "alpha/v5.6.78";
        GetTranslateHelper getSHAREitTranslate2 = new GetTranslateHelper("SHAREit", projectPath, outPutPath, lastTag, "values-in");
        getSHAREitTranslate2.start();
    }

    private static void testTroy() {
        String projectPath = "/Users/maxy/Android/workspace/troy";
        String outPutPath = "/Users/maxy/Desktop";
        String lastTag = "alpha/v1.1.78";
        GetTranslateHelper getSHAREitTranslate2 = new GetTranslateHelper("troy", projectPath, outPutPath, lastTag, "values-in");
        getSHAREitTranslate2.start();
    }
}
