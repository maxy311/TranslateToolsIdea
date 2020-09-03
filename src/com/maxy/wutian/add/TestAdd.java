package com.maxy.wutian.add;

import java.io.File;
import java.nio.file.Path;

public class TestAdd {
    public static void main(String[] args) {
        String path = "/Users/maxy/Android/workspace/SHAREit/ShareAd/AdSdkCpi/src/main/res/values-zh-rCN/strings.xml";
        String parentPath = path.substring(0, path.lastIndexOf(File.separator));
        System.out.println(parentPath);
    }
}
