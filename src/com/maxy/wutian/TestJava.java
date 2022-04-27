package com.maxy.wutian;

import com.maxy.wutian.utils.ShellUtils;

import java.io.File;

public class TestJava {
    public static void main(String[] args) {
//        String str = "jfkadjfksfjawwwwwwwwwfdkjafkjask";
//        String special = "wwwwwwwww";
//        int indexOf = str.indexOf(special);
//        System.out.println(str.charAt(indexOf + special.length()));

//        String path = "/Users/maxy/Android/workspace/SHAREit";
//        String tag = "master";
//        boolean currentBranch = ShellUtils.checkoutToTag(path, tag);
//        System.out.println(currentBranch);

        delete(args);
    }

    public static void delete(String[] args) {
        File file = new File("/Users/maxy/Android/workspace/SHAREit/App-Lite/src/main/res");
        if (!file.exists())
            return;
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.isDirectory()) {
                File file2 = new File(file1, "theme_strings.xml");
                if (file2.exists()) {
                    file2.delete();
                    System.out.println(file2.getAbsolutePath());
                }
            }
        }
    }
}
