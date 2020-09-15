package com.maxy.wutian.add;

import com.maxy.wutian.log.LogManager;

import java.io.File;

public class TestAdd {
    public static void main(String[] args) {
//        String path = "/Users/maxy/Android/workspace/SHAREit/ShareAd/AdSdkCpi/src/main/res/values-zh-rCN/strings.xml";
//        String parentPath = path.substring(0, path.lastIndexOf(File.separator));
//        LogManager.getInstance().log(parentPath);

        String projectPath = "/Users/maxy/Android/workspace/troy";
        String translatePath = "/Users/maxy/Desktop/troy_Translate";
        AddTranslateHelper addTranslateHelper = new AddTranslateHelper("troy", projectPath, translatePath);
        addTranslateHelper.start();
    }
}
