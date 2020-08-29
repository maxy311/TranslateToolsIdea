package com.maxy.wutian;

import com.maxy.wutian.add.AddTranslateHelper;
import com.maxy.wutian.get.GetTranslateHelper;

import java.io.File;


public class TestJava {
    private static final String PROJECT_PATH = "/Users/maxy/Android/workspace/SHAREit";
    private static final String DESKTOP_PATH = "/Users/maxy/Desktop";
    public static void main(String[] args) {
        testAddMethod();
//        testGetMethod();
    }

    private static void testAddMethod() {
        File file = new File(PROJECT_PATH);
        File deskFile = new File(DESKTOP_PATH + "/SHAREit_Translate");
        AddTranslateHelper addTranslateHelper = new AddTranslateHelper(file, deskFile);
        addTranslateHelper.startAddTranslate();
    }

    private static void testGetMethod() {
        File file = new File(PROJECT_PATH);
        File deskFile = new File(DESKTOP_PATH);
        GetTranslateHelper getSHAREitTranslate = new GetTranslateHelper(file, deskFile, null, null);
        getSHAREitTranslate.startGetTranslate();
    }
}
