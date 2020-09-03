package com.maxy.wutian.get;

public class TestGet {
    public static void main(String[] args) {
        String projectPath = "/Users/maxy/Android/workspace/SHAREit";
        String outPutPath = "/Users/maxy/Desktop";
        String lastTag = "v5.6.58_ww";;
        GetTranslateHelper getSHAREitTranslate2 = new GetTranslateHelper("SHAREit", projectPath, outPutPath, lastTag);
        getSHAREitTranslate2.start();
    }
}
