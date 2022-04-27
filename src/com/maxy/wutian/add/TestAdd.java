package com.maxy.wutian.add;

public class TestAdd {
    public static void main(String[] args) {
//        String path = "/Users/maxy/Android/workspace/SHAREit/ShareAd/AdSdkCpi/src/main/res/values-zh-rCN/strings.xml";
//        String parentPath = path.substring(0, path.lastIndexOf(File.separator));
//        LogManager.getInstance().log(parentPath);

        String projectPath = "/Users/maxy/Android/workspace/SHAREit";
        String translatePath = "/Users/maxy/Desktop/SHAREit_LiteClean-9L";
        AddTranslateHelper addTranslateHelper = new AddTranslateHelper("SHAREit", projectPath, translatePath);
        addTranslateHelper.start();
    }
}
