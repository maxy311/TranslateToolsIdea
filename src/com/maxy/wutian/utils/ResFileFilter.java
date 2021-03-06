package com.maxy.wutian.utils;

import java.io.File;
import java.io.FileFilter;

public class ResFileFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        if (file.isHidden())
            return false;
        String name = file.getName();
        if (name.toLowerCase().contains("debug"))
            return false;
        if (name.equals("test"))
            return false;
        if (name.equals("tools"))
            return false;
        if (name.equals("release"))
            return false;
        if (name.equals("SDK"))
            return false;
        if (name.equals("Launcher"))
            return false;

        if (name.contains("build"))
            return false;
        //not translate pay module;
        if (name.equals("BizPay"))
            return false;
        return true;
    }
}
