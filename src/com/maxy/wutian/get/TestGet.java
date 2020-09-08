package com.maxy.wutian.get;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class TestGet {
    public static void main(String[] args) {
//        String projectPath = "/Users/maxy/Android/workspace/SHAREit";
//        String outPutPath = "/Users/maxy/Desktop";
//        String lastTag = "v5.6.58_ww";
//        GetTranslateHelper getSHAREitTranslate2 = new GetTranslateHelper("SHAREit", projectPath, outPutPath, lastTag);
//        getSHAREitTranslate2.start();

        FileSystemView fsv = FileSystemView.getFileSystemView();
        File home = fsv.getHomeDirectory();
        String savePath = home.getPath();
        File deskDir = new File(home, "Desktop");

        System.out.println(savePath + "\n" + deskDir.exists() + "\n" + deskDir.getAbsolutePath());
    }
}
