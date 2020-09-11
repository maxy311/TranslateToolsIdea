package com.maxy.wutian.test;

public class test {
    public static void main(String[] args) {
        String shareitPath = "/Users/maxy/Android/workspace/SHAREit";
        String inputPath = "/Users/maxy/Desktop/values.xml";
        GetSpecialString getSpecialString = new GetSpecialString("Troy",shareitPath, inputPath);
        getSpecialString.start();
    }
}
