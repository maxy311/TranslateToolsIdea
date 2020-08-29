package com.maxy.wutian.utils;

import java.io.File;

public class ModulePareHelper {
    public static String parseFileToModuleName(File projectFile, File file) {
        try {
            String parent = file.getParent();
            String module = parent.replace(projectFile.getAbsolutePath(), "");
            if (module.contains("\\"))
                module = module.replaceAll("\\\\", "_");
            else if (module.contains("/"))
                module = module.replaceAll("/", "_");

            if (module.contains("src"))
                return module.substring(1, module.indexOf("src") - 1);
            else
                return module.substring(1, module.indexOf("res") - 1);
        } catch (Exception e) {
            System.out.println(file.getAbsolutePath() + "        " + e.toString());
        }
        return "SHAREit";
    }
}
