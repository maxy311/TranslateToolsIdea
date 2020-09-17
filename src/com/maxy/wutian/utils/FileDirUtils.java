package com.maxy.wutian.utils;



import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class FileDirUtils {
    public static File getDeskTopFile() {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        File home = fsv.getHomeDirectory();
        return new File(home, "Desktop");
    }
}
