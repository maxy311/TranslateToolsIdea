package com.maxy.wutian.get;

public class TestGet {
    public static void main(String[] args) {
//        testTroy();
        testSHAREit();
    }

    private static void testSHAREit() {
        String projectPath = "/Users/maxiaoyu/Android/workspace/shareit_12/SHAREit";
        String outPutPath = "/Users/maxiaoyu/Desktop";
        String lastTag = null;
        GetTranslateHelper getSHAREitTranslate2 = new GetTranslateHelper("SHAREit", projectPath, outPutPath, lastTag, "values-aa");
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
