package com.maxy.wutian.fix;

public class Test {
    public static void main(String[] args) {
        String path = "/Users/maxy/Android/workspace/SHAREit";
//        path = "/Users/maxy/Android/workspace/troy";
        FixAllTranslate fixTranslate = new FixAllTranslate();
        fixTranslate.start(path);
    }
}
