package com.maxy.wutian.filter;

import java.io.File;
import java.io.FileFilter;

public class StringFileFilter implements FileFilter {
    private String dirName;

    public StringFileFilter(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public boolean accept(File file) {
        if (file.isHidden())
            return false;

        boolean isDirectory = file.isDirectory();
        String fileName = file.getName();
        if (isDirectory && dirName.equals(fileName))
            return true;

        if (isDirectory && isNotCheckDir(fileName))
            return false;
        else if (isDirectory)
            return hasSubDir(file);
        return isStringsFile(fileName);
    }

    public static boolean hasSubDir(File file) {
        if (!file.isDirectory())
            return false;
        for (File listFile : file.listFiles()) {
            if (!listFile.isHidden() && listFile.isDirectory())
                return true;
        }
        return false;
    }

    public static boolean isNotCheckDir(String dirName) {
        if (dirName.equals("debug"))
            return true;
        else if (dirName.equals("test"))
            return true;
        else if (dirName.equals("tools"))
            return true;
        else if (dirName.equals("release"))
            return true;
        else if (dirName.equals("SDK"))
            return true;
        else if (dirName.equals("Launcher"))
            return true;
        else if (dirName.equals("build"))
            return true;
        else if (dirName.equals("assets"))
            return true;
        else if (dirName.equals("java"))
            return true;
        else if (dirName.equals("androidTest"))
            return true;
        return false;
    }

    public static boolean isStringsFile(File file) {
        if (file.isHidden())
            return false;
        return isStringsFile(file.getName());
    }

    private static boolean isStringsFile(String name) {
        if (!name.contains("string"))
            return false;
        else if (name.contains("filter"))
            return false;
        else if (name.contains("dimens"))
            return false;
        else if (name.contains("account"))
            return false;
        else if (name.contains("product_setting"))
            return false;
        else if (name.contains("country_code_string"))
            return false;
        return true;
    }
}
