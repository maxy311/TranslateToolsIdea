package com.maxy.wutian.ieport;

import com.maxy.wutian.ieport.GetSpecialString;

public class test {
    public static void main(String[] args) {
//        textExport();
        textImport();
    }

    public static void textExport() {
        String shareitPath = "/Users/maxy/Android/workspace/SHAREit";
        String inputPath = "/Users/maxy/Desktop/Log 2.txt";
        GetSpecialString getSpecialString = new GetSpecialString("Troy",shareitPath, inputPath);
        getSpecialString.start();
    }

    public static void textImport() {
        String projectPath = "/Users/maxy/Android/workspace/troy";
        String specialPath = "/Users/maxy/Desktop/Troy_Special_String";
        ImportSpecialString importSpecialString = new ImportSpecialString(projectPath, specialPath);
        importSpecialString.start();

    }
}
