package com.maxy.wutian.filter;

import java.io.File;
import java.io.FileFilter;

public class FileFiltersEx {
    public static FileFilter sFileFilters = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if (file.isHidden())
                return false;
            String fileName = file.getName();
            if (fileName.endsWith(".sh"))
                return false;
            else if (fileName.endsWith(".properties"))
                return false;
            else if (fileName.endsWith(".gradle"))
                return false;
            else if (fileName.endsWith(".bat"))
                return false;
            return true;
        }
    };

    public static FileFilter sResDirFilters = new FileFilter() {
        @Override
        public boolean accept(File file) {
            if (file.isHidden())
                return false;
            String name = file.getName();
            if (name.equals("debug"))
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

            //not translate pay module;
            if (name.equals("BizPay"))
                return false;
            return true;
        }
    };

    public static boolean isStringsFile(File file) {
        String name = file.getName();
        if (file.isHidden())
            return false;
        if (!name.contains("string"))
            return false;
        if (name.contains("filter"))
            return false;
        if (name.contains("dimens"))
            return false;
        if (name.contains("account"))
            return false;
        if (name.contains("product_setting"))
            return false;
        if (name.contains("country_code_string"))
            return false;
        return true;
    }
}
